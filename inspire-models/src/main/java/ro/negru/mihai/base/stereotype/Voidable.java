package ro.negru.mihai.base.stereotype;

import jakarta.validation.Valid;
import lombok.Getter;
import ro.negru.mihai.base.types.codelist.VoidReasonValue;

@Getter
public class Voidable<T> {

    @Valid
    private T value;
    private VoidReasonValue reason;

    private Voidable(VoidReasonValue reason) {
        this.reason = reason;
    }

    private Voidable(T value) {
        this.value = value;
    }

    public boolean isVoid() {
        return value == null && reason != null;
    }

    public static<M> Voidable<M> ofVoid(String reason) {
        return new Voidable<>(VoidReasonValue.fromValue(reason));
    }

    public static<M> Voidable<M> ofValue(M value) {
        return new Voidable<>(value);
    }
}
