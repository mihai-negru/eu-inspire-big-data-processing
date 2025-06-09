package ro.negru.mihai.handler.functions.command.merge.flatmap;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.command.MergeCommandData;
import ro.negru.mihai.entity.command.MergeCommandPreTransform;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CommandMergeConvertToFeatureExecutor extends RichFlatMapFunction<MergeCommandPreTransform, MergeCommandData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandMergeConvertToFeatureExecutor.class);
    private final static XmlUtils.InspireXmlMapper xmlMapper = XmlUtils.getModuleWithDefaults().getXmlMapper();

    @Override
    public void flatMap(MergeCommandPreTransform preTransformed, Collector<MergeCommandData> collector) {
        final TransformResult transformed = preTransformed.getTransformResult();
        final long batchSize = preTransformed.getBatchSize();

        LOGGER.info("Trying to transform the message (merge command) for the following schema: {}", transformed.getXmlSchema());
        FeatureCollection<?> transformedFeature = null;
        try (InputStream input = new ByteArrayInputStream(transformed.getXml().array())) {
            transformedFeature = xmlMapper.readFeature(input, transformed.getXmlSchema());
        } catch (Exception e) {
            LOGGER.error("Exception happened when transforming the schema message (merge command)", e);
        }

        if (transformedFeature != null) {
            LOGGER.info("Successfully transformed(merge command) the schema message: {}", transformed.getXmlSchema());
            collector.collect(new MergeCommandData(transformed.getGroupId(), transformed.getXmlPath(), transformedFeature, batchSize));
        } else {
            LOGGER.error("The schema {} message {} could not be transformed (merge command), due to unknown reasons", transformed.getXmlSchema(), transformed.getXml());
        }
    }
}
