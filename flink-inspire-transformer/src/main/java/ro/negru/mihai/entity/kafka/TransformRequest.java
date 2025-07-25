package ro.negru.mihai.entity.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransformRequest {
    private String groupId;
    private String schema;
    private String schemaPath;
    private String message;
}
