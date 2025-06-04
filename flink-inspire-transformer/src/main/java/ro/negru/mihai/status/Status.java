package ro.negru.mihai.status;

import ro.negru.mihai.application.schema.administrativeunits.codelist.LegalStatusValue;

public enum Status {
    PENDING("PENDING"),
    PASSED("PASSED"),
    FAILED("FAILED"),
    NOT_APPLICABLE("NOT_APPLICABLE");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public static Status fromValue(String v) {
        for (Status c : Status.values())
            if (c.status.equals(v))
                return c;
        throw new IllegalArgumentException(v);
    }

    public String str() {
        return status;
    }
}
