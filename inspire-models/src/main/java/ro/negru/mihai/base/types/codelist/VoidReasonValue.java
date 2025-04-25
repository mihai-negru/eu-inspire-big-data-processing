package ro.negru.mihai.base.types.codelist;

public enum VoidReasonValue {

    UNPOPULATED("unpopulated"),
    UNKNOWN("unknown"),
    WITHHELD("withheld");

    private final String value;

    VoidReasonValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VoidReasonValue fromValue(String v) {
        for (VoidReasonValue c : VoidReasonValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
