package ro.negru.mihai.configure.entity.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    PENDING("PENDING"),

    PASSED("PASSED"),
    FAILED("FAILED"),
    SKIPPED("SKIPPED"),
    NOT_APPLICABLE("NOT_APPLICABLE"),
    INFO("INFO"),
    WARNING("WARNING"),
    UNDEFINED("UNDEFINED"),
    PASSED_MANUAL("PASSED_MANUAL");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    @JsonCreator
    public static Status fromValue(String v) {
        for (Status c : Status.values())
            if (c.status.equals(v))
                return c;

        return Status.UNDEFINED;
    }

    @JsonValue
    public String str() {
        return status;
    }
}
