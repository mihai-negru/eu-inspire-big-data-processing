package ro.negru.mihai.configure.entity.pipeline.condition.parser.rule;

import ro.negru.mihai.configure.entity.pipeline.condition.rule.RatioCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.operator.TestOperator;
import ro.negru.mihai.configure.entity.status.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RatioConditionParser implements ConditionParser {
    private static final Pattern RATIO_PATTERN = Pattern.compile(
            "\\s*ratio\\(\\s*([a-zA-Z]+)\\s*,\\s*([a-zA-Z]+)\\s*\\)\\s*(<=|>=|<|>|=)\\s*([+-]?\\d+(?:\\.\\d*)?)\\s*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public TestCondition parse(String input) {
        final Matcher m = RATIO_PATTERN.matcher(input);
        return m.matches() ?
                new RatioCondition(
                        Status.fromValue(m.group(1).toLowerCase()),
                        Status.fromValue(m.group(2).toLowerCase()),
                        TestOperator.fromValue(m.group(3)),
                        Double.parseDouble(m.group(4))
                )
                :
                null;
    }
}
