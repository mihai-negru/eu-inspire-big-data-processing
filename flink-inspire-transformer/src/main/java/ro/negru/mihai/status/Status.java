package ro.negru.mihai.status;

public enum Status {
    PENDING("PENDING"),
    PASSED("PASSED"),
    FAILED("FAILED"),
    NOT_APPLICABLE("NOT_APPLICABLE"),
    UNKNOWN("UNKNOWN");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public static Status fromValue(String v) {
        for (Status c : Status.values())
            if (c.status.equals(v))
                return c;

        return Status.UNKNOWN;
    }

    public String str() {
        return status;
    }
}
