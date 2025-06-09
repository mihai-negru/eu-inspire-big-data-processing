package ro.negru.mihai.kafkainspirevalidatorconnector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.negru.mihai.kafkainspirevalidatorconnector.status.ValidatorTestResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestResponse {
    private String id;
    private String groupId;
    private ValidatorTestResponse status;
}
