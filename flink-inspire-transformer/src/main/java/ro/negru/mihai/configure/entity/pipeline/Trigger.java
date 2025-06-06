package ro.negru.mihai.configure.entity.pipeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

@ToString
public enum Trigger {
    PASS,
    FAIL;

    @JsonCreator
    public static Trigger fromValue(String v) {
        if (v == null) {
            throw new IllegalArgumentException("Trigger cannot be null");
        }
        return switch (v.trim().toLowerCase()) {
            case "pass" -> PASS;
            case "fail" -> FAIL;
            default -> throw new IllegalArgumentException("Unknown trigger value: " + v);
        };
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
