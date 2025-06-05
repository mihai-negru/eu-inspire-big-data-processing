package ro.negru.mihai;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ro.negru.mihai.xml.xmladapter.XmlUtils;

public class DataStreamJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamJob.class);

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.enableCheckpointing(10000);
		env.getCheckpointConfig().setCheckpointingConsistencyMode(CheckpointingMode.EXACTLY_ONCE);

		LOGGER.info("Creating Kafka Input Sources");
		final DataStream<TransformRequest> rawDataStream = DataStreamHandler.<TransformRequest>createDataStream(env, KafkaHandler.<TransformRequest>createKafkaSource("raw", "flink-raw-data", new TransformRequestSchema()), "flink-kafka-raw-data");
		final DataStream<ValidatorTestResponse> kafkaValidatedStream = DataStreamHandler.<ValidatorTestResponse>createDataStream(env, KafkaHandler.<ValidatorTestResponse>createKafkaSource("validator.output", "flink-validator-output", new ValidatorTestResponseSchema()), "flink-kafka-validator-output");
		final DataStream<CommandRequest> commandSignalStream = DataStreamHandler.<CommandRequest>createDataStream(env, KafkaHandler.<CommandRequest>createKafkaSource("command", "flink-command", new CommandRequestSchema()), "flink-kafka-command");
		LOGGER.info("Successfully created Kafka Input Sources");

		LOGGER.info("Creating the transformer routine and a sinker back to kafka for validation");
		final DataStream<ValidatorTestRequest> transformedDataStream = rawDataStream.flatMap(new DataStreamHandler.InspireFlatMapTransform(XmlUtils.getAvailableSchemas())).name("toINSPIRECompliant").returns(TypeInformation.of(ValidatorTestRequest.class));
		KafkaHandler.<ValidatorTestRequest>sinker("validator.input", transformedDataStream);
		LOGGER.info("Successfully created transformer routines");

		LOGGER.info("Creating the Casandra sinker to insert newly non-validated responses");
		final DataStream<TransformResult> cassandraPendingStream = transformedDataStream.map(new CassandraHandler.PendingCassandraMapFunction()).name("ToUnvalidatedCassandraDB").returns(TypeInformation.of(TransformResult.class));
		CassandraHandler.sinker(cassandraPendingStream, true);
		LOGGER.info("Successfully created Cassandra insert sinker");

		LOGGER.info("Creating the Casandra sinker to update newly validated responses");
		final DataStream<TransformResult> cassandraUpdateStatusStream = kafkaValidatedStream.flatMap(new DataStreamHandler.InspireFlatMapComputeStatus()).name("ToValidatedCassandraDB").returns(TypeInformation.of(TransformResult.class));
		CassandraHandler.sinker(cassandraUpdateStatusStream, false);
		LOGGER.info("Successfully created Cassandra update sinker");

		env.execute("INSPIRE EU Directive Transform Pipeline Application Job");
	}
}
