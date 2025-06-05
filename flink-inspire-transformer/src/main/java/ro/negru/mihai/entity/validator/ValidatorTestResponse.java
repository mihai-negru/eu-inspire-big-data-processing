package ro.negru.mihai.entity.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorTestResponse {
    private String id;
    private TestAssertionWrapper status;
}
