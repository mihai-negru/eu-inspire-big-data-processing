package ro.negru.mihai;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.configure.TestStrategy;
import ro.negru.mihai.entity.cassandra.CommandResult;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.command.CommandRequest;
import ro.negru.mihai.entity.command.MergeCommandData;
import ro.negru.mihai.entity.command.MergeCommandGroupedData;
import ro.negru.mihai.entity.command.MergeCommandPreTransform;
import ro.negru.mihai.entity.kafka.*;
import ro.negru.mihai.entity.validator.ValidatorTestResponse;
import ro.negru.mihai.handler.functions.command.generategroupid.flatmap.CommandGenerateGroupIdExecutor;
import ro.negru.mihai.handler.functions.command.merge.flatmap.CommandMergeConvertToFeatureExecutor;
import ro.negru.mihai.handler.functions.command.merge.flatmap.CommandMergeExecutor;
import ro.negru.mihai.handler.functions.command.merge.flatmap.CommandMergeFetchDbRowsExecutor;
import ro.negru.mihai.handler.functions.command.generategroupid.filter.FilterGenerateGroupIdCommand;
import ro.negru.mihai.handler.functions.command.merge.filter.FilterMergeCommand;
import ro.negru.mihai.handler.functions.command.merge.keyprocess.CommandMergeGroupKeyProcessExtractor;
import ro.negru.mihai.handler.functions.inspire.ApplyTestStrategyMapFunction;
import ro.negru.mihai.handler.functions.inspire.InspireFlatMapRawTransform;
import ro.negru.mihai.handler.functions.cassandra.PendingCassandraMapFunction;
import ro.negru.mihai.handler.functions.kafka.ToReferenceValidatorMapFunction;
import ro.negru.mihai.handler.utils.CassandraUtils;
import ro.negru.mihai.handler.utils.DataStreamUtils;
import ro.negru.mihai.handler.utils.KafkaUtils;
import ro.negru.mihai.configure.OSEnvHandler;
import ro.negru.mihai.entity.schema.deserializer.CommandRequestSchema;
import ro.negru.mihai.entity.schema.deserializer.TransformRequestSchema;
import ro.negru.mihai.entity.schema.deserializer.ValidatorTestResponseSchema;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

public class DataStreamJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamJob.class);

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.enableCheckpointing(10000);
		env.getCheckpointConfig().setCheckpointingConsistencyMode(CheckpointingMode.EXACTLY_ONCE);

		final OSEnvHandler osEnvHandler = OSEnvHandler.readSystemEnvs();
		final TestStrategy testStrategy = TestStrategy.readTestStrategy();

		LOGGER.info("Creating Kafka Input Sources");
		final DataStream<TransformRequest> rawDataStream = DataStreamUtils.createDataStream(env, KafkaUtils.createKafkaSource(osEnvHandler.getEnv("fromTransformStream"), "flink-raw-data", osEnvHandler, new TransformRequestSchema()), "flink-kafka-raw-data");
		final DataStream<ValidatorTestResponse> kafkaValidatedStream = DataStreamUtils.createDataStream(env, KafkaUtils.createKafkaSource(osEnvHandler.getEnv("fromValidateStream"), "flink-validator-output", osEnvHandler, new ValidatorTestResponseSchema()), "flink-kafka-validator-output");
		final DataStream<CommandRequest> commandSignalStream = DataStreamUtils.createDataStream(env, KafkaUtils.createKafkaSource(osEnvHandler.getEnv("fromExecCommandStream"), "flink-command-input", osEnvHandler, new CommandRequestSchema()), "flink-kafka-command-input");
		LOGGER.info("Successfully created Kafka Input Sources");

		LOGGER.info("Creating the transformer routine and a sinker back to kafka for validation");
		final DataStream<PostTransformRequest> transformedDataStream = rawDataStream
				.flatMap(new InspireFlatMapRawTransform(XmlUtils.getAvailableSchemas())).name("ToINSPIRECompliant").returns(TypeInformation.of(PostTransformRequest.class));

		final DataStream<ValidatorTestRequest> toValidateTransformedDataStream = transformedDataStream
				.map(new ToReferenceValidatorMapFunction()).name("ToINSPIREReferenceValidator").returns(TypeInformation.of(ValidatorTestRequest.class));
		KafkaUtils.sinker(osEnvHandler.getEnv("toValidateStream"), osEnvHandler, toValidateTransformedDataStream);
		LOGGER.info("Successfully created transformer routines");

		LOGGER.info("Creating the Casandra sinker to insert newly non-validated responses");
		final DataStream<TransformResult> cassandraPendingStream = transformedDataStream
				.map(new PendingCassandraMapFunction()).name("ToUnvalidatedCassandraDB").returns(TypeInformation.of(TransformResult.class));
		CassandraUtils.sinker(osEnvHandler, true, cassandraPendingStream);
		LOGGER.info("Successfully created Cassandra insert sinker");

		LOGGER.info("Creating the Casandra sinker to update newly validated responses");
		final DataStream<TransformResult> cassandraUpdateStatusStream = kafkaValidatedStream
				.map(new ApplyTestStrategyMapFunction(osEnvHandler, testStrategy)).name("ToValidatedCassandraDB").returns(TypeInformation.of(TransformResult.class));
		CassandraUtils.sinker(osEnvHandler, false, cassandraUpdateStatusStream);
		LOGGER.info("Successfully created Cassandra update sinker");

		final DataStream<CommandRequest> commandGenerateGroupIdStream = commandSignalStream
				.filter(new FilterGenerateGroupIdCommand()).name("FilterGenerateGroupIdCommand").returns(TypeInformation.of(CommandRequest.class));
		final DataStream<CommandRequest> commandMergeStream = commandSignalStream
				.filter(new FilterMergeCommand()).name("FilterMergeCommand").returns(TypeInformation.of(CommandRequest.class));

		final DataStream<CommandResult> commandGenerateGroupIdStreamResult = commandGenerateGroupIdStream
				.flatMap(new CommandGenerateGroupIdExecutor()).name("ExecuteGenerateCommand").returns(TypeInformation.of(CommandResult.class));

		final DataStream<CommandResult> commandMergeStreamResult = commandMergeStream
				.flatMap(new CommandMergeFetchDbRowsExecutor(osEnvHandler)).name("FromRowsToTransformed").returns(TypeInformation.of(MergeCommandPreTransform.class))
				.flatMap(new CommandMergeConvertToFeatureExecutor()).name("FromTransformedXmlToInspireCompliant").returns(TypeInformation.of(MergeCommandData.class))
				.keyBy(MergeCommandData::getGroupId)
				.process(new CommandMergeGroupKeyProcessExtractor()).name("ToGroupedInspireCompliant").returns(TypeInformation.of(MergeCommandGroupedData.class))
				.flatMap(new CommandMergeExecutor()).name("ExecuteMergeCommand").returns(TypeInformation.of(CommandResult.class));

		CassandraUtils.sinker(osEnvHandler, true, commandGenerateGroupIdStreamResult, commandMergeStreamResult);
		KafkaUtils.sinker(osEnvHandler.getEnv("toExecCommandStream"), osEnvHandler, commandGenerateGroupIdStreamResult, commandMergeStreamResult);

		if (osEnvHandler.isTransformerLoggerEnabled()) {
			KafkaUtils.sinker(osEnvHandler.getEnv("toTransformerLogger"), osEnvHandler, cassandraUpdateStatusStream, commandGenerateGroupIdStreamResult, commandMergeStreamResult);
		}

		env.execute("INSPIRE EU Directive Transform Pipeline Application Job");
	}
}
