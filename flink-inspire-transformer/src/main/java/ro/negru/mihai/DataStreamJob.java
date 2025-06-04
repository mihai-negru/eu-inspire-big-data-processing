package ro.negru.mihai;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.kafka.CommandRequest;
import ro.negru.mihai.entity.kafka.TransformRequest;
import ro.negru.mihai.entity.kafka.ValidatorTestRequest;
import ro.negru.mihai.entity.validator.ValidatorTestResponse;
import ro.negru.mihai.handler.CassandraHandler;
import ro.negru.mihai.handler.DataStreamHandler;
import ro.negru.mihai.handler.KafkaHandler;
import ro.negru.mihai.schema.deserializer.CommandRequestSchema;
import ro.negru.mihai.schema.deserializer.TransformRequestSchema;
import ro.negru.mihai.schema.deserializer.ValidatorTestResponseSchema;

public class DataStreamJob {
	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.enableCheckpointing(10000);
		env.getCheckpointConfig().setCheckpointingConsistencyMode(CheckpointingMode.EXACTLY_ONCE);

//		KafkaHandler.createTopicIfNotExist(bootstrapServers, XmlUtils.getAvailableFeatures());

		final DataStream<TransformRequest> rawDataStream = DataStreamHandler.<TransformRequest>createDataStream(env, KafkaHandler.<TransformRequest>createKafkaSource("raw", "flink-raw-data", new TransformRequestSchema()), "flink-kafka-raw-data");
		final DataStream<ValidatorTestResponse> kafkaValidatedStream = DataStreamHandler.<ValidatorTestResponse>createDataStream(env, KafkaHandler.<ValidatorTestResponse>createKafkaSource("validator.output", "flink-validator-output", new ValidatorTestResponseSchema()), "flink-kafka-validator-output");
		final DataStream<CommandRequest> commandSignalStream = DataStreamHandler.<CommandRequest>createDataStream(env, KafkaHandler.<CommandRequest>createKafkaSource("command", "flink-command", new CommandRequestSchema()), "flink-kafka-command");

		final DataStream<ValidatorTestRequest> transformedDataStream = rawDataStream.flatMap(new DataStreamHandler.InspireFlatMapTransform()).returns(ValidatorTestRequest.class);
		KafkaHandler.<ValidatorTestRequest>sinker("validator.input", transformedDataStream);

		final DataStream<TransformResult> cassandraPendingStream = transformedDataStream.map(new CassandraHandler.PendingCassandraMapFunction()).returns(TransformResult.class);
		CassandraHandler.sinkerInsert(cassandraPendingStream);

		final DataStream<TransformResult> cassandraUpdateStatusStream = kafkaValidatedStream.flatMap(new DataStreamHandler.InspireFlatMapComputeStatus()).returns(TransformResult.class);
		CassandraHandler.sinkerUpdate(cassandraUpdateStatusStream);

		env.execute("INSPIRE EU Directive Transform Pipeline Application Job");
	}
}
