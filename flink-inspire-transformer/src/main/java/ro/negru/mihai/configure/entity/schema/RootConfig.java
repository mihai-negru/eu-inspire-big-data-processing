package ro.negru.mihai.configure.entity.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RootConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("schemas")
    private List<SchemaConfig> schemas;
}
