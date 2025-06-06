package ro.negru.mihai.configure.entity.pipeline.condition.parser;

import ro.negru.mihai.configure.entity.pipeline.condition.parser.rule.*;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.AllwaysSucceedCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;

import java.text.ParseException;

public class AllConditionParser implements ConditionParser {
    private final ConditionParser countConditionParser;
    private final ConditionParser percentConditionParser;
    private final ConditionParser allTestsWeightConditionParser;
    private final ConditionParser hasHardTestFailsConditionParser;
    private final ConditionParser ratioConditionParser;

    public AllConditionParser() {
        countConditionParser = new CountConditionParser();
        percentConditionParser = new PercentConditionParser();
        allTestsWeightConditionParser = new AllTestsWeightConditionParser();
        hasHardTestFailsConditionParser = new HasHardTestFailsConditionParser();
        ratioConditionParser = new RatioConditionParser();
    }

    @Override
    public TestCondition parse(String input) throws ParseException {
        final String trimmed;
        if (input == null || (trimmed = input.trim()).isEmpty())
            return new AllwaysSucceedCondition();

        TestCondition condition;
        if ((condition = countConditionParser.parse(trimmed)) != null) return condition;
        if ((condition = percentConditionParser.parse(trimmed)) != null) return condition;
        if ((condition = allTestsWeightConditionParser.parse(trimmed)) != null) return condition;
        if ((condition = hasHardTestFailsConditionParser.parse(trimmed)) != null) return condition;
        if ((condition = ratioConditionParser.parse(trimmed)) != null) return condition;

        return new AllwaysSucceedCondition();
    }
}
