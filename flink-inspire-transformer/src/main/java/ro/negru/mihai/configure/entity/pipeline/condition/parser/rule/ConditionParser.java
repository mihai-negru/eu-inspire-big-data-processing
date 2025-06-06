package ro.negru.mihai.configure.entity.pipeline.condition.parser.rule;

import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;

import java.text.ParseException;

public interface ConditionParser {
    TestCondition parse(String input) throws ParseException;
}
