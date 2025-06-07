package ro.negru.mihai.configure.entity.pipeline;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import ro.negru.mihai.configure.entity.pipeline.condition.deserializer.TestConditionDeserializer;
import ro.negru.mihai.configure.entity.pipeline.condition.rule.TestCondition;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TestRule implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("rule")
    @JsonDeserialize(using = TestConditionDeserializer.class)
    private TestCondition rule;
}
