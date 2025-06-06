package ro.negru.mihai.configure.entity.pipeline.condition.parser.operator;

import lombok.Getter;

@Getter
public enum TestOperator {
    LT("<"),
    LTE("<="),
    EQ("="),
    GTE(">="),
    GT(">");

    private final String symbol;

    TestOperator(String symbol) {
        this.symbol = symbol;
    }

    public static TestOperator fromValue(String text) {
        for (TestOperator op : TestOperator.values()) {
            if (op.getSymbol().equals(text)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unsupported operator: " + text);
    }
}
