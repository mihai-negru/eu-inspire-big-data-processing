package ro.negru.mihai.handler.functions.command;

import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.kafka.Command;
import ro.negru.mihai.entity.kafka.CommandRequest;

import java.util.HashMap;
import java.util.Map;

public class CommandMultiplexerExecutor extends RichFlatMapFunction<CommandRequest, TransformResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandMultiplexerExecutor.class);

    private transient Map<Command, RichFlatMapFunction<CommandRequest, TransformResult>> delegates;

    @Override
    public void flatMap(CommandRequest request, Collector<TransformResult> out) throws Exception {
        final Command cmd = request.getCommand();
        final RichFlatMapFunction<CommandRequest, TransformResult> function = delegates.get(cmd);

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

        for (RichFlatMapFunction<CommandRequest, TransformResult> executor : delegates.values())
            executor.open(openContext);
    }

    @Override
    public void close() throws Exception {
        for (RichFlatMapFunction<CommandRequest, TransformResult> executor : delegates.values())
            executor.close();

        super.close();
    }
}
