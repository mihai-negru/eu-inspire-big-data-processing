package ro.negru.mihai.handler.functions.command;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.entity.cassandra.CommandResult;
import ro.negru.mihai.entity.kafka.CommandRequest;

import java.util.UUID;

public class CommandGenerateGroupIdExecutor extends RichFlatMapFunction<CommandRequest, CommandResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandGenerateGroupIdExecutor.class);

    @Override
    public void flatMap(CommandRequest request, Collector<CommandResult> collector) {
        String groupId = request.getGroupId();

        if (groupId == null || groupId.isBlank()) {
            LOGGER.info("Group id is null or empty a random group id will be generated");
            groupId = UUID.randomUUID().toString();
        }

        collector.collect(new CommandResult(groupId, request.getCommand().getValue(), null));
    }
}
