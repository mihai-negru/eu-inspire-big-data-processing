package ro.negru.mihai.base.stereotype;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ro.negru.mihai.base.types.codelist.VoidReasonValue;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Voidable<T> {

    @Valid
    private T voidValue;
    private VoidReasonValue voidReason;

    private Voidable(VoidReasonValue reason) {
        this.voidReason = reason;
    }

    private Voidable(T value) {
        this.voidValue = value;
    }

    public boolean isVoid() {
        return voidValue == null && voidReason != null;
    }

    public static<M> Voidable<M> ofVoid(String reason) {
        return new Voidable<>(VoidReasonValue.fromValue(reason));
    }

    public static<M> Voidable<M> ofValue(M value) {
        return new Voidable<>(value);
    }
}
