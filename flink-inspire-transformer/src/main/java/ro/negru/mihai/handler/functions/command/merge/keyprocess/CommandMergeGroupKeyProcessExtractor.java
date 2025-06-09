package ro.negru.mihai.handler.functions.command.merge.keyprocess;

import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import ro.negru.mihai.entity.command.MergeCommandData;
import ro.negru.mihai.entity.command.MergeCommandGroupedData;

import java.util.ArrayList;
import java.util.List;

public class CommandMergeGroupKeyProcessExtractor extends KeyedProcessFunction<String, MergeCommandData, MergeCommandGroupedData> {

    private transient ListState<MergeCommandData> buffer;
    private transient ValueState<Long> batchSize;

    @Override
    public void processElement(MergeCommandData mergeCommandData, KeyedProcessFunction<String, MergeCommandData, MergeCommandGroupedData>.Context context, Collector<MergeCommandGroupedData> collector) throws Exception {
        buffer.add(mergeCommandData);

        final Long batchSizeLong = batchSize.value();
        long currentBatchSize = ( batchSizeLong == null ? 0 : batchSizeLong ) + 1;
        batchSize.update(currentBatchSize);

        if (currentBatchSize >= mergeCommandData.getBatchSize()) {
            final List<MergeCommandData> merged = new ArrayList<>();
            for (MergeCommandData mergeCommand : buffer.get()) {
                merged.add(mergeCommand);
            }

            collector.collect(new MergeCommandGroupedData(context.getCurrentKey(), merged));

            buffer.clear();
            batchSize.clear();
        }
    }

    @Override
    public void open(OpenContext openContext) throws Exception {
        super.open(openContext);

        buffer = getRuntimeContext().getListState(new ListStateDescriptor<>("process-batch-buffer", MergeCommandData.class));
        batchSize = getRuntimeContext().getState(new ValueStateDescriptor<>("process-batch-size", Long.class));
    }
}
