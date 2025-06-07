package ro.negru.mihai.handler.utils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DataStreamUtils {
    public static <IN> DataStream<IN> createDataStream(StreamExecutionEnvironment env, KafkaSource<IN> ingest, String sourceName) {
        return env.fromSource(ingest, WatermarkStrategy.noWatermarks(), sourceName);
    }
}
