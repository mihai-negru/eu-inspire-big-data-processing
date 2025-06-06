package ro.negru.mihai.configure.entity.pipeline;

import lombok.ToString;

@ToString
public enum Trigger {
    PASS,
    FAIL;

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

    public static Trigger fromBoolean(boolean v) {
        return v ? PASS : FAIL;
    }

    public String toValue() {
        return name().toLowerCase();
    }
}
