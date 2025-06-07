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

    public <T extends Comparable<T>> boolean evaluate(T o1, T o2) {
        int cmp = o1.compareTo(o2);

        return switch (this) {
            case LT -> (cmp < 0);
            case LTE -> (cmp <= 0);
            case EQ -> (cmp == 0);
            case GTE -> (cmp >= 0);
            case GT -> (cmp > 0);
        };
    }
}
