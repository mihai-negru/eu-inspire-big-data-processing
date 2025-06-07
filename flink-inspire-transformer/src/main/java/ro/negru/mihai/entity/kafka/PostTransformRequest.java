package ro.negru.mihai.entity.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostTransformRequest {
    private String id;
    private String groupId;
    private String schema;
    private String schemaPath;
    private String xml;
    private String etsFamily;
}
