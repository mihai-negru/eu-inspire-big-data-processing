package ro.negru.mihai.configure.entity.pipeline.condition.rule;

import lombok.AllArgsConstructor;
import ro.negru.mihai.configure.entity.pipeline.Trigger;
import ro.negru.mihai.entity.validator.MappedTestAssertion;

import java.util.List;

@AllArgsConstructor
public class AllwaysSucceedCondition implements TestCondition {

    @Override
    public Trigger evaluate(List<MappedTestAssertion> assertions) {
        return Trigger.PASS;
    }
}
