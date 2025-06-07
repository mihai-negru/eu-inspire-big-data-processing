package ro.negru.mihai.configure.entity.pipeline.condition.parser.rule;

import ro.negru.mihai.configure.entity.pipeline.condition.rule.PercentCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.operator.TestOperator;
import ro.negru.mihai.configure.entity.status.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PercentConditionParser implements ConditionParser {
    private static final Pattern PERCENT_PATTERN = Pattern.compile(
            "\\s*percent\\(\\s*([a-zA-Z]+)\\s*\\)\\s*(<=|>=|<|>|=)\\s*([+-]?\\d+(?:\\.\\d*)?)\\s*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public TestCondition parse(String input) {
        final Matcher m = PERCENT_PATTERN.matcher(input);
        return m.matches() ?
                new PercentCondition(
                        Status.fromValue(m.group(1)),
                        TestOperator.fromValue(m.group(2)),
                        Double.parseDouble(m.group(3))
                )
                :
                null;
    }
}
