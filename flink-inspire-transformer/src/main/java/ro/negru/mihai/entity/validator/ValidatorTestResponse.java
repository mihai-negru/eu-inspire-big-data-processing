package ro.negru.mihai.entity.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorTestResponse {
    private UUID id;
    private TestAssertionWrapper status;
}
