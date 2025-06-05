package ro.negru.mihai.handler;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.kafka.TransformRequest;
import ro.negru.mihai.entity.kafka.ValidatorTestRequest;
import ro.negru.mihai.entity.validator.TestAssertion;
import ro.negru.mihai.entity.validator.ValidatorTestResponse;
import ro.negru.mihai.status.Status;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataStreamHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamHandler.class);

    private final static XmlUtils.InspireXmlMapper xmlMapper;

    static {
        xmlMapper = XmlUtils.getModuleWithDefaults().getXmlMapper();
    }

    public static <IN> DataStream<IN> createDataStream(StreamExecutionEnvironment env, KafkaSource<IN> ingest, String sourceName) {
        return env.fromSource(ingest, WatermarkStrategy.noWatermarks(), sourceName);
    }

    public static class InspireFlatMapTransform implements FlatMapFunction<TransformRequest, ValidatorTestRequest> {
        private final List<String> availableSchemas;

        public InspireFlatMapTransform(List<String> availableSchemas) {
            this.availableSchemas = availableSchemas;
        }

        @Override
        public void flatMap(TransformRequest record, Collector<ValidatorTestRequest> collector) throws Exception {
            if (!availableSchemas.contains(record.getSchema())) {
                LOGGER.error("Schema {} is not available", record.getSchema());
                return;
            }

            FeatureCollection<?> transformed = null;
            try (InputStream input = new ByteArrayInputStream(record.getMessage().getBytes())) {
                transformed = xmlMapper.readFeature(input, record.getSchema());
            } catch (Exception e) {
                LOGGER.error("InspireFlatMapTransform error", e);
            }

            if (transformed != null) {
                final StringWriter writer = new StringWriter();
                xmlMapper.writeValue(writer, transformed);

                final String id = UUID.randomUUID().toString();
                final String etsFamily = transformed.etsFamily();
                final String xml = writer.toString();
                collector.collect(new ValidatorTestRequest(id, etsFamily, xml));

            } else {
                LOGGER.error("No transformed inspire record, because something went wrong");
            }
        }
    }

    public static class InspireFlatMapComputeStatus implements FlatMapFunction<ValidatorTestResponse, TransformResult> {
        @Override
        public void flatMap(ValidatorTestResponse validatorTestResponse, Collector<TransformResult> collector){
            // FIXME: Impement this

            Map<String, String> details = null;
            final List<TestAssertion> assertions = validatorTestResponse.getStatus().getEtsAssertions();
            int counter = 0;
            for (TestAssertion assertion : assertions) {
                final Status assertionStatus = Status.fromValue(assertion.getAssertionStatus());
                final String assertionEtsFamily = assertion.getAssertionEts();

                if (assertionStatus == Status.PASSED) {
                    counter++;
                } else {
                    if (details == null)
                        details = new HashMap<>();

                    details.put(assertionEtsFamily, assertion.getAssertionEts());
                }
            }

            collector.collect(new TransformResult(validatorTestResponse.getId(), null, (counter == assertions.size() ? Status.PASSED : Status.FAILED).str(), details));
        }
    }
}
