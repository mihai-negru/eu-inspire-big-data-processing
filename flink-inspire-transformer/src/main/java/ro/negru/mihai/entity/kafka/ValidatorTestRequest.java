package ro.negru.mihai.entity.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ValidatorTestRequest {
    private String id;
    private String groupId;
    private String etsFamily;
    private String xml;
}
