package ro.negru.mihai.configure.entity.pipeline.condition.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ro.negru.mihai.configure.entity.pipeline.Trigger;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.operator.TestOperator;
import ro.negru.mihai.configure.entity.status.Status;
import ro.negru.mihai.entity.validator.MappedTestAssertion;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class PercentCondition implements TestCondition {
    private Status category;
    private TestOperator operator;
    private double value;

    @Override
    public Trigger evaluate(List<MappedTestAssertion> assertions) {
        final long total = assertions.size();
        final long count = assertions.parallelStream()
                .filter(m -> m.getStatus() == category)
                .count();

        final double percent = (double) count / (double) total;
        return Trigger.fromBoolean(operator.evaluate(percent, value));
    }
}
