package ro.negru.mihai.handler.functions.command.merge.flatmap;

import jodd.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.base.featuretype.Feature;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.entity.cassandra.CommandResult;
import ro.negru.mihai.entity.command.Command;
import ro.negru.mihai.entity.command.MergeCommandData;
import ro.negru.mihai.entity.command.MergeCommandGroupedData;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CommandMergeExecutor extends RichFlatMapFunction<MergeCommandGroupedData, CommandResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandMergeExecutor.class);
    private final static XmlUtils.InspireXmlMapper xmlMapper = XmlUtils.getModuleWithDefaults().getXmlMapper();
    private static final BeanUtil JODD = BeanUtil.declaredForced;

    @Override
    public void flatMap(MergeCommandGroupedData groupedValues, Collector<CommandResult> collector) {
        final List<MergeCommandData> items = groupedValues.getData();
        if (items == null || items.isEmpty()) {
            LOGGER.warn("No items in group found for merge command");
            return;
        }

        final List<SortedMergeCommandData> sortedItems = items.parallelStream()
                .map((data) -> {
                    final XmlUtils.XmlPath xmlPath = XmlUtils.computeFeaturePath(data.getXmlPath());
                    return new SortedMergeCommandData(xmlPath.getPath(), data.getFeature(), xmlPath.getSize());
                })
                .sorted(Comparator.comparingLong(SortedMergeCommandData::getPathSize))
                .toList();

        final Iterator<SortedMergeCommandData> sortedIterator = sortedItems.iterator();
        final FeatureCollection<?> rootFeatureCollection = sortedIterator.next().getFeature();
        final Feature rootFeature = XmlUtils.getFeatureFromCollection(rootFeatureCollection);

        while (sortedIterator.hasNext()) {
            final SortedMergeCommandData commandData = sortedIterator.next();

            final Feature subFeature = XmlUtils.getFeatureFromCollection(commandData.getFeature());
            final String subForcedPath = commandData.getXmlPath();
            if (subFeature == null || subForcedPath == null || subForcedPath.isEmpty()) {
                continue;
            }

            try {
                JODD.setProperty(rootFeature, subForcedPath, subFeature);
            } catch (Exception e) {
                LOGGER.error("Error setting property for merge command", e);
            }
        }

        try {
            ByteBuffer xml = ByteBuffer.wrap(xmlMapper.writeValueAsBytes(rootFeatureCollection));
            collector.collect(new CommandResult(UUID.randomUUID().toString(), items.get(0).getGroupId(), Command.MERGE.getValue(), xml));
        } catch (Exception e) {
            LOGGER.error("Error serializing root feature collection", e);
            return;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class SortedMergeCommandData {
        private String xmlPath;
        private FeatureCollection<?> feature;
        private long pathSize;
    }
}
