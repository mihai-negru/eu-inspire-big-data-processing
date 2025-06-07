package ro.negru.mihai.configure.entity.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class StatusConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("mapping")
    private Map<Status, Status> mapping;

    @JsonProperty("weight")
    private Map<Status, Integer> weight;
}
