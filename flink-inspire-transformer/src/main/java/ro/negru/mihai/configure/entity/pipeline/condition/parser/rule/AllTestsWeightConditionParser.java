package ro.negru.mihai.configure.entity.pipeline.condition.parser.rule;

import ro.negru.mihai.configure.entity.pipeline.condition.rule.AllTestsWeightCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.operator.TestOperator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllTestsWeightConditionParser implements ConditionParser {
    private static final Pattern ALL_TESTS_WEIGHT_PATTERN = Pattern.compile(
            "\\s*allTestsWeight\\s*(<=|>=|<|>|=)\\s*([+-]?\\d+)\\s*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public TestCondition parse(String input) {
        final Matcher m = ALL_TESTS_WEIGHT_PATTERN.matcher(input);
        return m.matches() ?
                new AllTestsWeightCondition(
                        TestOperator.fromValue(m.group(1)),
                        Long.parseLong(m.group(2))
                )
                :
                null;
    }
}
