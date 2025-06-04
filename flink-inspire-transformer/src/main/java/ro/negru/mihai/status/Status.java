package ro.negru.mihai.status;

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

    public static Status fromValue(String v) {
        for (Status c : Status.values())
            if (c.status.equals(v))
                return c;

        return Status.UNDEFINED;
    }

    public String str() {
        return status;
    }
}
