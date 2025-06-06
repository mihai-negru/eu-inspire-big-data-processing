package ro.negru.mihai.configure.entity.pipeline.condition.parser.rule;

import ro.negru.mihai.configure.entity.pipeline.condition.rule.CountCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.operator.TestOperator;
import ro.negru.mihai.configure.entity.status.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountConditionParser implements ConditionParser {
    private static final Pattern COUNT_PATTERN = Pattern.compile(
            "\\s*count\\(\\s*([a-zA-Z]+)\\s*\\)\\s*(<=|>=|<|>|=)\\s*(\\d+)\\s*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public TestCondition parse(String input) {
        final Matcher m = COUNT_PATTERN.matcher(input);
        return m.matches() ?
                new CountCondition(
                        Status.fromValue(m.group(1).toLowerCase()),
                        TestOperator.fromValue(m.group(2)),
                        Integer.parseInt(m.group(3))
                )
                :
                null;
    }
}
