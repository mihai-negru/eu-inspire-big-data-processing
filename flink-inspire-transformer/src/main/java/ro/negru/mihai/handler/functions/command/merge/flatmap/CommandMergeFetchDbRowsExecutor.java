package ro.negru.mihai.handler.functions.command.merge.flatmap;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.configure.OSEnvHandler;
import ro.negru.mihai.configure.entity.status.Status;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.command.CommandRequest;
import ro.negru.mihai.entity.command.MergeCommandPreTransform;
import ro.negru.mihai.handler.utils.CassandraUtils;

import java.util.List;

public class CommandMergeFetchDbRowsExecutor extends RichFlatMapFunction<CommandRequest, MergeCommandPreTransform> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandMergeFetchDbRowsExecutor.class);

    private transient CqlSession session;
    private transient PreparedStatement lookupSchemaStatement;

    private final OSEnvHandler osEnvHandler;

    public CommandMergeFetchDbRowsExecutor(OSEnvHandler osEnvHandler) {
        this.osEnvHandler = osEnvHandler;
    }

    @Override
    public void flatMap(CommandRequest request, Collector<MergeCommandPreTransform> collector) {
        final String groupId = request.getGroupId();
        if (groupId == null || groupId.isBlank()) {
            LOGGER.error("GroupId is null or empty and is not allowed for merge command");
            return;
        }

        LOGGER.info("Extracting the rows for the following groupId: {}", request.getGroupId());
        final List<TransformResult> transformed = session.execute(lookupSchemaStatement.bind(groupId)).map(TransformResult::fromRow).all();
        if (transformed.isEmpty()) {
            LOGGER.warn("No transformed rows found for groupId: {}", groupId);
            return;
        }

        final long batchSize = transformed.size();
        for (TransformResult transformResult : transformed) {
            if (Status.fromValue(transformResult.getStatus()) == Status.PASSED)
                collector.collect(new MergeCommandPreTransform(batchSize, transformResult));
        }
    }

    @Override
    public void open(OpenContext openContext) throws Exception {
        super.open(openContext);

        session = CassandraUtils.getSession(osEnvHandler);
        lookupSchemaStatement = session.prepare(TransformResult.lookUpGroupStatement());
    }
}
