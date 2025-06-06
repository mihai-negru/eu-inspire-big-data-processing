package ro.negru.mihai.configure.entity.pipeline.condition.parser.rule;

import ro.negru.mihai.configure.entity.pipeline.condition.rule.HasHardTestFailsCondition;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HasHardTestFailsConditionParser implements ConditionParser {
    private static final Pattern HAS_HARD_TEST_FAILS_PATTERN = Pattern.compile(
            "\\s*hasHardTestFails\\s*=\\s*(true|false)\\s*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public TestCondition parse(String input) {
        final Matcher m = HAS_HARD_TEST_FAILS_PATTERN.matcher(input);
        return m.matches() ?
                new HasHardTestFailsCondition(
                        Boolean.parseBoolean(m.group(1).toLowerCase())
                )
                :
                null;
    }
}
