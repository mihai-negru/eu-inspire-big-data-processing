package ro.negru.mihai.entity.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.negru.mihai.base.featuretype.FeatureCollection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MergeCommandData {
    private String groupId;
    private String xmlSchema;
    private String xmlPath;
    private FeatureCollection<?> feature;
    private long batchSize;
}
