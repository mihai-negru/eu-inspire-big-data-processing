package ro.negru.mihai.entity.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ValidatorTestRequest {
    private UUID id;

    private String etsFamily;

    private String xml;
}
