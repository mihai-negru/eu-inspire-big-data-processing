package ro.negru.mihai.handler.functions.cassandra;

import org.apache.flink.api.common.functions.RichMapFunction;
import ro.negru.mihai.configure.entity.status.Status;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.kafka.PostTransformRequest;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PendingCassandraMapFunction extends RichMapFunction<PostTransformRequest, TransformResult> {

    @Override
    public TransformResult map(PostTransformRequest req) {
        return new TransformResult(
                req.getId(),
                req.getGroupId(),
                req.getSchema(),
                req.getSchemaPath(),
                ByteBuffer.wrap(req.getXml().getBytes(StandardCharsets.UTF_8)),
                Status.PENDING.str(),
                null
        );
    }
}