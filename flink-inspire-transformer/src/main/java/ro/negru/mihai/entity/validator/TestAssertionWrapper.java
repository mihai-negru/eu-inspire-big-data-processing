package ro.negru.mihai.entity.validator;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TestAssertionWrapper {
    List<TestAssertion> etsAssertions;
}
