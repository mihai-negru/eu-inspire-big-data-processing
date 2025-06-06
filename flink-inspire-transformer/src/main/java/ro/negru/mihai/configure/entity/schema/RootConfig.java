package ro.negru.mihai.configure.entity.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RootConfig {
    @JsonProperty("schemas")
    private List<SchemaConfig> schemas;
}
