package ro.negru.mihai.handler.functions.command.merge.flatmap;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import ro.negru.mihai.entity.cassandra.CommandResult;
import ro.negru.mihai.entity.command.MergeCommandGroupedData;

public class CommandMergeExecutor extends RichFlatMapFunction<MergeCommandGroupedData, CommandResult> {
    @Override
    public void flatMap(MergeCommandGroupedData value, Collector<CommandResult> out) throws Exception {

    }
}
