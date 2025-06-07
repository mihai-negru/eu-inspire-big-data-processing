package ro.negru.mihai.configure.entity.pipeline.condition.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ro.negru.mihai.configure.entity.pipeline.Trigger;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.operator.TestOperator;
import ro.negru.mihai.configure.entity.status.Status;
import ro.negru.mihai.entity.validator.MappedTestAssertion;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class CountCondition implements TestCondition, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Status category;
    private TestOperator operator;
    private long value;

    @Override
    public Trigger evaluate(List<MappedTestAssertion> assertions) {
        final long count = assertions.parallelStream()
                .filter(m -> m.getStatus() == category)
                .count();

        return Trigger.fromBoolean(operator.evaluate(count, value));
    }
}
