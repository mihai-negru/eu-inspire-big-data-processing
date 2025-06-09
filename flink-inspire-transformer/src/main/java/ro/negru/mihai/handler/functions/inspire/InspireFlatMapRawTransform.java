package ro.negru.mihai.handler.functions.inspire;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.entity.kafka.PostTransformRequest;
import ro.negru.mihai.entity.kafka.TransformRequest;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

public class InspireFlatMapRawTransform extends RichFlatMapFunction<TransformRequest, PostTransformRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InspireFlatMapRawTransform.class);
    private final static XmlUtils.InspireXmlMapper xmlMapper = XmlUtils.getModuleWithDefaults().getXmlMapper();

    private final List<String> availableSchemas;

    public InspireFlatMapRawTransform(List<String> availableSchemas) {
        this.availableSchemas = availableSchemas;
    }

    @Override
    public void flatMap(TransformRequest record, Collector<PostTransformRequest> collector) {
        LOGGER.info("Received a transform request for the following schema: {}", record.getSchema());
        if (!availableSchemas.contains(record.getSchema())) {
            LOGGER.error("Transform schema {} is not available", record.getSchema());
            return;
        }

        LOGGER.info("Trying to transform the message for the following schema: {}", record.getSchema());
        FeatureCollection<?> transformed = null;
        try (InputStream input = new ByteArrayInputStream(record.getMessage().getBytes())) {
            transformed = xmlMapper.readFeature(input, record.getSchema());
        } catch (Exception e) {
            LOGGER.error("Exception happened when transforming the schema message", e);
        }

        if (transformed != null) {
            LOGGER.info("Successfully transformed the schema message: {}", record.getSchema());
            final StringWriter writer = new StringWriter();

            try {
                xmlMapper.writeValue(writer, transformed);
            } catch (Exception e) {
                LOGGER.error("Exception happened when serializing the message", e);
            }

            final String id = UUID.randomUUID().toString();
            final String groupId = record.getGroupId();
            final String xmlSchema = record.getSchema();
            final String xmlSchemaPath = record.getSchemaPath();
            final String xml = writer.toString();
            final String etsFamily = transformed.etsFamily();
            collector.collect(new PostTransformRequest(id, groupId, xmlSchema, xmlSchemaPath, xml, etsFamily));

        } else {
            LOGGER.error("The schema {} message {} could not be transformed, due to unknown reasons", record.getSchema(), record.getMessage());
        }
    }
}
