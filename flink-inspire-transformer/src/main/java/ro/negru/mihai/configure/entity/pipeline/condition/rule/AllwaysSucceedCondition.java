package ro.negru.mihai.configure.entity.pipeline.condition.rule;

import lombok.AllArgsConstructor;
import ro.negru.mihai.configure.entity.pipeline.Trigger;
import ro.negru.mihai.entity.validator.MappedTestAssertion;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
public class AllwaysSucceedCondition implements TestCondition, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public Trigger evaluate(List<MappedTestAssertion> assertions) {
        return Trigger.PASS;
    }
}
