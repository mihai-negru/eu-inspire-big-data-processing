package ro.negru.mihai.handler.functions.command;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.kafka.CommandRequest;

public class CommandMergeExecutor extends RichFlatMapFunction<CommandRequest, TransformResult> {
    @Override
    public void flatMap(CommandRequest value, Collector<TransformResult> out) throws Exception {

    }
}
