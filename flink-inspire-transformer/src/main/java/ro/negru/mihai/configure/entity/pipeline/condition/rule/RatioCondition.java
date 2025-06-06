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
public class RatioCondition implements TestCondition {
    private Status leftCategory;
    private Status rightCategory;
    private TestOperator operator;
    private double value;

    @Override
    public Trigger evaluate(List<MappedTestAssertion> assertions) {
        final long leftCount = assertions.parallelStream()
                .filter(m -> m.getStatus() == leftCategory)
                .count();
        final long rightCount = assertions.parallelStream()
                .filter(m -> m.getStatus() == rightCategory)
                .count();

        final double ratio = rightCount != 0 ? (double) leftCount / (double) rightCount : 0.0;
        return Trigger.fromBoolean(operator.evaluate(ratio, value));
    }
}
