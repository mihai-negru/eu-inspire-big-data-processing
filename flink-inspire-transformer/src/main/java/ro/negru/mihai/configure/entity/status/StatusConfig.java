package ro.negru.mihai.configure.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class StatusConfig {
    @JsonProperty("mapping")
    private Map<Status, Status> mapping;

    @JsonProperty("weight")
    private Map<String, Integer> weight;
}
