package ro.negru.mihai.configure.entity.pipeline;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import ro.negru.mihai.configure.entity.pipeline.condition.deserializer.TestConditionDeserializer;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TestRule {
    @JsonProperty("rule")
    @JsonDeserialize(using = TestConditionDeserializer.class)
    private TestCondition rule;

    @JsonProperty("trigger")
    private Trigger trigger;
}
