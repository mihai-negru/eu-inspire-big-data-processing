package ro.negru.mihai.configure.entity.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ro.negru.mihai.configure.entity.pipeline.TestRule;
import ro.negru.mihai.configure.entity.status.StatusConfig;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SchemaConfig {
    @JsonProperty("name")
    private String name;

    @JsonProperty("hardTests")
    private List<String> hardTests;

    @JsonProperty("whitelist")
    private List<String> whitelist;

    @JsonProperty("status")
    private StatusConfig status;

    @JsonProperty("testPipeline")
    private List<TestRule> testPipeline;
}
