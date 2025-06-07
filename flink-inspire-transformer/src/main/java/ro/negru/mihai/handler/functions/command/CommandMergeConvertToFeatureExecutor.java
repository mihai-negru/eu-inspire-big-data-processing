package ro.negru.mihai.handler.functions.command;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CommandMergeConvertToFeatureExecutor extends RichFlatMapFunction<TransformResult, TransformResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandMergeConvertToFeatureExecutor.class);
    private final static XmlUtils.InspireXmlMapper xmlMapper = XmlUtils.getModuleWithDefaults().getXmlMapper();

    @Override
    public void flatMap(TransformResult transformedByGroup, Collector<TransformResult> out) throws Exception {
        LOGGER.info("Trying to transform the message (merge command) for the following schema: {}", transformedByGroup.getXmlSchema());
        FeatureCollection<?> transformed = null;
        try (InputStream input = new ByteArrayInputStream(transformedByGroup.getXml().array())) {
            transformed = xmlMapper.readFeature(input, transformedByGroup.getXmlSchema());
        } catch (Exception e) {
            LOGGER.error("Exception happened when transforming the schema message (merge command)", e);
        }

        if (transformed != null) {
            // FIXME: Here we do not need to return the TransformResult but another class, Perhaps a FeatureCollection?
        }
    }
}
