package ro.negru.mihai.entity.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.negru.mihai.entity.cassandra.TransformResult;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MergeCommandPreTransform {
    private long batchSize;
    private TransformResult transformResult;
}
