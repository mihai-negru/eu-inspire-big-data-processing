package ro.negru.mihai.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.entity.kafka.ValidatorTestRequest;
import ro.negru.mihai.oslevel.OSEnvHandler;
import ro.negru.mihai.schema.deserializer.AbstractKafkaJsonDeserializerSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KafkaHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <OUT> void sinker(final String validatorInputTopic, final DataStream<OUT> stream) {
        final DataStream<String> stringStream = stream.map(MAPPER::writeValueAsString).returns(Types.STRING);

        final KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
                .setBootstrapServers(OSEnvHandler.INSTANCE.getEnv("kafka"))
                .setRecordSerializer(
                        KafkaRecordSerializationSchema.builder()
                                .setTopic(validatorInputTopic)
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build()
                )
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .build();

        stringStream.sinkTo(kafkaSink);
    }

    public static void createTopicIfNotExist(final String bootstrapServer, final List<String> topicNames) {
        final Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServer);

        try (AdminClient adminClient = AdminClient.create(props)) {
            List<NewTopic> topics = new ArrayList<>();
            for (String topicName : topicNames) {
                topics.add(new NewTopic(topicName, 1, (short) 1));
                LOGGER.info("Creating topic {}", topicName);
            }

            adminClient.createTopics(topics).all().get(10, TimeUnit.MINUTES);
            LOGGER.info("Successfully created topics");
        } catch (Exception e) {
            LOGGER.error("Failed to create topics");
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static List<String> createRawTopics(final List<String> topicNames) {
        return topicNames.stream().map(topic -> "raw." + topic).collect(Collectors.toList());
    }

    public static <IN> KafkaSource<IN> createKafkaSource(final String topic, final String groupId, AbstractKafkaJsonDeserializerSchema<IN> deserializer) {
        return KafkaSource.<IN>builder()
                .setBootstrapServers(OSEnvHandler.INSTANCE.getEnv("kafka"))
                .setTopics(topic)
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(deserializer)
                .setProperty("partition.discovery.interval.ms", "10000")
                .setProperty("allow.auto.create.topics", "true")
                .build();
    }
}
