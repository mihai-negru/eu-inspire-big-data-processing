package ro.negru.mihai.handler.functions.command.filter;

import org.apache.flink.api.common.functions.RichFilterFunction;
import ro.negru.mihai.entity.kafka.Command;
import ro.negru.mihai.entity.kafka.CommandRequest;

public class FilterGenerateGroupIdCommand extends RichFilterFunction<CommandRequest> {
    @Override
    public boolean filter(CommandRequest request) throws Exception {
        return request.getCommand() == Command.GENERATE_GROUP;
    }
}
