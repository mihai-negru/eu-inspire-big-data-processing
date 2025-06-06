package ro.negru.mihai.configure.entity.pipeline.condition.parser;

import ro.negru.mihai.configure.entity.pipeline.condition.parser.rule.*;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.AllwaysSucceedCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AllConditionParser implements ConditionParser {
    private final List<ConditionParser> conditionParsers;

    public AllConditionParser() {
        conditionParsers = new ArrayList<>(4);
        conditionParsers.add(new CountConditionParser());
        conditionParsers.add(new PercentConditionParser());
        conditionParsers.add(new AllTestsWeightConditionParser());
        conditionParsers.add(new RatioConditionParser());
    }

    @Override
    public TestCondition parse(String input) throws ParseException {
        final String trimmed;
        if (input == null || (trimmed = input.trim()).isEmpty())
            return new AllwaysSucceedCondition();

        for (ConditionParser conditionParser : conditionParsers) {
            final TestCondition testCondition = conditionParser.parse(trimmed);
            if (testCondition != null) {
                return testCondition;
            }
        }

        return new AllwaysSucceedCondition();
    }
}
