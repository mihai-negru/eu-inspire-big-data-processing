package ro.negru.mihai.entity.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Command {
    MERGE("merge"),
    GENERATE_GROUP("generate-group"),
    UNDEFINED("undefined");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Command fromValue(String v) {
        for (Command c : Command.values())
            if (c.value.equalsIgnoreCase(v))
                return c;

        return Command.UNDEFINED;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
