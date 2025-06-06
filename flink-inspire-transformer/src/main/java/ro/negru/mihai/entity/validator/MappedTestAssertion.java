package ro.negru.mihai.entity.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.negru.mihai.configure.entity.status.Status;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MappedTestAssertion {
    private String ets;
    private Status status;
    private int weight;
}
