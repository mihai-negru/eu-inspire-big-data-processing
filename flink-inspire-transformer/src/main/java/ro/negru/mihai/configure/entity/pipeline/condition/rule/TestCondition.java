package ro.negru.mihai.configure.entity.pipeline.condition.rule;

import ro.negru.mihai.configure.entity.pipeline.Trigger;
import ro.negru.mihai.entity.validator.MappedTestAssertion;

import java.util.List;

public interface TestCondition {
    Trigger evaluate(List<MappedTestAssertion> assertions);
}
