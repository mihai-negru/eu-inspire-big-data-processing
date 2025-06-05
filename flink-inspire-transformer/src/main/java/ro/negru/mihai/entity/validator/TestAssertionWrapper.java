package ro.negru.mihai.entity.validator;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TestAssertionWrapper {
    private List<TestAssertion> etsAssertions;
}
