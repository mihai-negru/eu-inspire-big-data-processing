package ro.negru.mihai.configure.entity.pipeline.condition.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.AllConditionParser;
import ro.negru.mihai.configure.entity.pipeline.condition.parser.rule.ConditionParser;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;

import java.io.IOException;
import java.text.ParseException;

public class TestConditionDeserializer extends JsonDeserializer<TestCondition> {
    private final ConditionParser conditionParser;

    public TestConditionDeserializer() {
        conditionParser = new AllConditionParser();
    }

    @Override
    public TestCondition deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final String conditionStr = p.getText();
        try {
            return conditionParser.parse(conditionStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
