package ro.negru.mihai.handler;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.base.featuretype.FeatureCollection;
import ro.negru.mihai.configure.TestStrategy;
import ro.negru.mihai.configure.entity.pipeline.TestRule;
import ro.negru.mihai.configure.entity.pipeline.Trigger;
import ro.negru.mihai.configure.entity.schema.SchemaConfig;
import ro.negru.mihai.configure.entity.status.Status;
import ro.negru.mihai.configure.entity.status.StatusConfig;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.kafka.PostTransformRequest;
import ro.negru.mihai.entity.kafka.TransformRequest;
import ro.negru.mihai.entity.kafka.ValidatorTestRequest;
import ro.negru.mihai.entity.validator.MappedTestAssertion;
import ro.negru.mihai.entity.validator.TestAssertion;
import ro.negru.mihai.entity.validator.ValidatorTestResponse;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

public class DataStreamHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamHandler.class);

    private final static XmlUtils.InspireXmlMapper xmlMapper;

    static {
        xmlMapper = XmlUtils.getModuleWithDefaults().getXmlMapper();
    }

    public static <IN> DataStream<IN> createDataStream(StreamExecutionEnvironment env, KafkaSource<IN> ingest, String sourceName) {
        return env.fromSource(ingest, WatermarkStrategy.noWatermarks(), sourceName);
    }

    public static class InspireFlatMapTransform extends RichFlatMapFunction<TransformRequest, PostTransformRequest> {
        private final List<String> availableSchemas;

        public InspireFlatMapTransform(List<String> availableSchemas) {
            this.availableSchemas = availableSchemas;
        }

        @Override
        public void flatMap(TransformRequest record, Collector<PostTransformRequest> collector) throws Exception {
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
                xmlMapper.writeValue(writer, transformed);

                final String id = UUID.randomUUID().toString();
                final String xmlSchema = record.getSchema();
                final String xmlSchemaPath = record.getSchemaPath();
                final String xml = writer.toString();
                final String etsFamily = transformed.etsFamily();
                collector.collect(new PostTransformRequest(id, xmlSchema, xmlSchemaPath, xml, etsFamily));

            } else {
                LOGGER.error("The schema {} message {} could not be transformed, due to unknown reasons", record.getSchema(), record.getMessage());
            }
        }
    }

    public static class ReferenceValidatorMapFunction extends RichMapFunction<PostTransformRequest, ValidatorTestRequest> {
        @Override
        public ValidatorTestRequest map(PostTransformRequest post){
            return new ValidatorTestRequest(post.getId(), post.getEtsFamily(), post.getXml());
        }
    }

    public static class InspireFlatMapComputeStatus extends RichFlatMapFunction<ValidatorTestResponse, TransformResult> {
        private transient CqlSession session;
        private transient PreparedStatement lookupSchemaStatement;
        private transient SchemaConfig testSchemaConfig;

        @Override
        public void open(OpenContext openContext) throws Exception {
            super.open(openContext);

            session = CassandraHandler.getSessionBuilder().build();
            lookupSchemaStatement = CassandraHandler.lookUpStatement(session);
        }

        @Override
        public void flatMap(ValidatorTestResponse validatorTestResponse, Collector<TransformResult> collector) throws Exception {
            LOGGER.info("Calculating the status for validator output");

            final String id = validatorTestResponse.getId();
            if (id == null) {
                LOGGER.warn("The id of the validator output is null");
                return;
            }

            final Row lookupRow = session.execute(lookupSchemaStatement.bind(id)).one();
            if (lookupRow == null) {
                LOGGER.warn("The schema id {} does not exist", id);
                return;
            }

            final TransformResult lookupElement = CassandraHandler.fromRow(lookupRow);
            LOGGER.info("Calculating status for Transform '{}' with schema '{}'", lookupElement.getId(), lookupElement.getXmlSchema());

            testSchemaConfig = TestStrategy.INSTANCE.getSchemaConfig(lookupElement.getXmlSchema());
            if (testSchemaConfig == null) {
                LOGGER.warn("The schema id {} does not exist", id);
                return;
            }

            final List<TestAssertion> originalAssertions = validatorTestResponse.getStatus().getEtsAssertions();

            final List<String> whitelist = testSchemaConfig.getWhitelist();
            final List<TestAssertion> assertions = whitelist == null || whitelist.isEmpty() ? originalAssertions : originalAssertions.parallelStream().filter(assertion -> whitelist.contains(assertion.getAssertionEts())).toList();
            final List<MappedTestAssertion> mappedAssertions = mappedAssertionsStream(assertions);

            if (hasAnyHardTestsFail(mappedAssertions)) {
                LOGGER.warn("The validation test '{}' has hard test failures, the test is automatically failed", id);

                collector.collect(new TransformResult(validatorTestResponse.getId(), null, null, null, Status.FAILED.str(), generateFailureDetails(originalAssertions)));
                return;
            }

            final Trigger trigger = evaluatePipeline(mappedAssertions);
            collector.collect(new TransformResult(validatorTestResponse.getId(), null, null, null, trigger == Trigger.PASS ? Status.PASSED.str() : Status.FAILED.str(), generateFailureDetails(originalAssertions)));
        }

        private List<MappedTestAssertion> mappedAssertionsStream(final List<TestAssertion> assertions) {
            final StatusConfig statusConfig = testSchemaConfig.getStatus();
            final Map<Status, Status> statusMapping = statusConfig == null || statusConfig.getMapping() == null ? Collections.emptyMap() : statusConfig.getMapping();
            final Map<Status, Integer> statusWeight = statusConfig == null || statusConfig.getWeight() == null ? Collections.emptyMap() : statusConfig.getWeight();

            return assertions.parallelStream().map(assertion -> {
                final Status originalStatus = Status.fromValue(assertion.getAssertionStatus());
                final Status mappedStatus = statusMapping.getOrDefault(originalStatus, originalStatus);
                final Integer mappedWeight = statusWeight.getOrDefault(originalStatus, 1);
                return new MappedTestAssertion(assertion.getAssertionEts(), mappedStatus, mappedWeight);
            }).toList();
        }

        private Map<String, String> generateFailureDetails(List<TestAssertion> assertions) {
            return assertions.parallelStream().collect(Collectors.toMap(TestAssertion::getAssertionEts, TestAssertion::getAssertionStatus, (a, b) -> a));
        }

        private boolean hasAnyHardTestsFail(final List<MappedTestAssertion> assertions) {
            final List<String> hardTests = testSchemaConfig.getHardTests();
            if (hardTests == null || hardTests.isEmpty()) {
                return false;
            }

            return assertions.parallelStream().anyMatch(assertion -> hardTests.contains(assertion.getEts()) && Status.isFailure(assertion.getStatus()));
        }

        private Trigger evaluatePipeline(final List<MappedTestAssertion> assertions) {
            final List<TestRule> testRules = testSchemaConfig.getTestPipeline();
            if (testRules == null || testRules.isEmpty()) {
                return Trigger.PASS;
            }

            for (final TestRule testRule : testRules) {
                LOGGER.info("Evaluating the following rule: {}", testRule);

                if (testRule.getRule() != null && testRule.getRule().evaluate(assertions) == Trigger.FAIL) {
                    return Trigger.FAIL;
                }
            }

            return Trigger.PASS;
        }
    }
}
