package ro.negru.mihai.handler.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.configure.OSEnvHandler;
import ro.negru.mihai.entity.schema.deserializer.AbstractKafkaJsonDeserializerSchema;

import java.util.UUID;

public class KafkaUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <OUT> void sinker(final String validatorInputTopic, final OSEnvHandler osEnvHandler, final DataStream<OUT> stream) {
        LOGGER.info("Creating a kafka source sinker for the following topic: {}", validatorInputTopic);

        final DataStream<String> stringStream = stream.map(MAPPER::writeValueAsString).returns(Types.STRING);

        final KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
                .setBootstrapServers(osEnvHandler.getEnv("kafka"))
                .setRecordSerializer(
                        KafkaRecordSerializationSchema.builder()
                                .setTopic(validatorInputTopic)
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build()
                )
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, osEnvHandler.getEnv("kafka_tmt"))
                .setTransactionalIdPrefix(UUID.randomUUID().toString())
                .build();

        stringStream.sinkTo(kafkaSink);

        LOGGER.info("Kafka sinker created for the following topic: {}", validatorInputTopic);
    }

    public static <IN> KafkaSource<IN> createKafkaSource(final String topic, final String groupId, final OSEnvHandler osEnvHandler, AbstractKafkaJsonDeserializerSchema<IN> deserializer) {
        LOGGER.info("Creating a kafka source for the following topic: {} and group id {}", topic, groupId);
        return KafkaSource.<IN>builder()
                .setBootstrapServers(osEnvHandler.getEnv("kafka"))
                .setTopics(topic)
                .setGroupId(groupId)
                .setClientIdPrefix(UUID.randomUUID().toString())
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(deserializer)
                .setProperty("partition.discovery.interval.ms", "10000")
                .setProperty("allow.auto.create.topics", "true")
                .build();
    }
}
