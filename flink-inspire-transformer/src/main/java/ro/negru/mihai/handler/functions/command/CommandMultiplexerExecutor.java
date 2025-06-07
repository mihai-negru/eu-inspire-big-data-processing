package ro.negru.mihai.handler.functions.command;

import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.entity.cassandra.CommandResult;
import ro.negru.mihai.entity.kafka.Command;
import ro.negru.mihai.entity.kafka.CommandRequest;

import java.util.HashMap;
import java.util.Map;

public class CommandMultiplexerExecutor extends RichFlatMapFunction<CommandRequest, CommandResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandMultiplexerExecutor.class);

    private transient Map<Command, RichFlatMapFunction<CommandRequest, CommandResult>> delegates;

    @Override
    public void flatMap(CommandRequest request, Collector<CommandResult> out) throws Exception {
        final Command cmd = request.getCommand();
        final RichFlatMapFunction<CommandRequest, CommandResult> function = delegates.get(cmd);

        if (function == null) {
            LOGGER.error("No delegate function found for command {}", cmd);
        } else {
            function.flatMap(request, out);
        }
    }

    @Override
    public void open(OpenContext openContext) throws Exception {
        super.open(openContext);

        delegates = new HashMap<>();
        delegates.put(Command.MERGE, new CommandMergeExecutor());
        delegates.put(Command.GENERATE_GROUP, new CommandGenerateGroupIdExecutor());

        for (RichFlatMapFunction<CommandRequest, CommandResult> executor : delegates.values())
            executor.open(openContext);
    }

    @Override
    public void close() throws Exception {
        for (RichFlatMapFunction<CommandRequest, CommandResult> executor : delegates.values())
            executor.close();

        super.close();
    }
}
