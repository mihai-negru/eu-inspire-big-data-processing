package ro.negru.mihai.configure.entity.pipeline.condition.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ro.negru.mihai.configure.entity.pipeline.Trigger;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.operator.TestOperator;
import ro.negru.mihai.entity.validator.MappedTestAssertion;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class AllTestsWeightCondition implements TestCondition {
    private TestOperator operator;
    private long value;

    @Override
    public Trigger evaluate(List<MappedTestAssertion> assertions) {
        final long totalWeight = assertions.parallelStream()
                .mapToLong(MappedTestAssertion::getWeight)
                .sum();

        return Trigger.fromBoolean(operator.evaluate(totalWeight, value));
    }
}
