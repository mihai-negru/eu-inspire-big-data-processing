package ro.negru.mihai.handler.functions.inspire;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.configure.OSEnvHandler;
import ro.negru.mihai.configure.TestStrategy;
import ro.negru.mihai.configure.entity.pipeline.TestRule;
import ro.negru.mihai.configure.entity.pipeline.Trigger;
import ro.negru.mihai.configure.entity.schema.SchemaConfig;
import ro.negru.mihai.configure.entity.status.Status;
import ro.negru.mihai.configure.entity.status.StatusConfig;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.validator.MappedTestAssertion;
import ro.negru.mihai.entity.validator.TestAssertion;
import ro.negru.mihai.entity.validator.ValidatorTestResponse;
import ro.negru.mihai.handler.utils.CassandraUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplyTestStrategyMapFunction extends RichFlatMapFunction<ValidatorTestResponse, TransformResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyTestStrategyMapFunction.class);

    private transient CqlSession session;
    private transient PreparedStatement lookupSchemaStatement;
    private transient SchemaConfig testSchemaConfig;

    private final OSEnvHandler osEnvHandler;
    private final TestStrategy testStrategy;

    public ApplyTestStrategyMapFunction(final OSEnvHandler osEnvHandler, final TestStrategy testStrategy) {
        this.osEnvHandler = osEnvHandler;
        this.testStrategy = testStrategy;
    }

    @Override
    public void open(OpenContext openContext) throws Exception {
        super.open(openContext);

        session = CassandraUtils.getSession(osEnvHandler);
        lookupSchemaStatement = session.prepare(TransformResult.lookUpIdStatement());
    }

    @Override
    public void flatMap(ValidatorTestResponse validatorTestResponse, Collector<TransformResult> collector) {
        LOGGER.info("Calculating the status for validator output");
        final List<TestAssertion> originalAssertions = validatorTestResponse.getStatus().getEtsAssertions();

        final String id = validatorTestResponse.getId();
        final String groupId = validatorTestResponse.getGroupId();
        if (id == null || groupId == null) {
            LOGGER.warn("The id or groupId of the validator output is null");
            return;
        }

        final Row lookupRow = session.execute(lookupSchemaStatement.bind(groupId, id)).one();
        if (lookupRow == null) {
            LOGGER.warn("The id '{}' or groupId '{}' may be wrong or misconfigured", id, groupId);
            return;
        }

        final TransformResult lookupElement = TransformResult.fromRow(lookupRow);
        LOGGER.info("Calculating status for Transform '{}' with schema '{}'", lookupElement.getId(), lookupElement.getXmlSchema());

        testSchemaConfig = testStrategy.getSchemaConfig(lookupElement.getXmlSchema());
        if (testSchemaConfig == null) {
            LOGGER.warn("The schema id {} does not exist assume test is passed by default", id);
            collector.collect(new TransformResult(id, groupId, null, null, null, Status.PASSED.str(), generateFailureDetails(originalAssertions)));
            return;
        }

        final List<String> whitelist = testSchemaConfig.getWhitelist();
        final List<TestAssertion> assertions = whitelist == null || whitelist.isEmpty() ? originalAssertions : originalAssertions.parallelStream().filter(assertion -> !whitelist.contains(assertion.getAssertionEts())).toList();
        final List<MappedTestAssertion> mappedAssertions = mappedAssertionsStream(assertions);

        if (hasAnyHardTestsFail(mappedAssertions)) {
            LOGGER.warn("The validation test '{}' has hard test failures, the test is automatically failed", id);

            collector.collect(new TransformResult(id, groupId, null, null, null, Status.FAILED.str(), generateFailureDetails(originalAssertions)));
            return;
        }

        final Trigger trigger = evaluatePipeline(mappedAssertions);
        collector.collect(new TransformResult(id, groupId, null, null, null, trigger == Trigger.PASS ? Status.PASSED.str() : Status.FAILED.str(), generateFailureDetails(originalAssertions)));
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