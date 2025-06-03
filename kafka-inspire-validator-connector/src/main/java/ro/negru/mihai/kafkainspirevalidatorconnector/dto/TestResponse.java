package ro.negru.mihai.kafkainspirevalidatorconnector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class TestResponse {
    private final String id;
    private final Map<String, String> result;
}
