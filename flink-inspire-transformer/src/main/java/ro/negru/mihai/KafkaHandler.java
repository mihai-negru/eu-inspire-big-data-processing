package ro.negru.mihai;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.schema.TopicAwareRecord;
import ro.negru.mihai.schema.TopicAwareRecordSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class KafkaHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaHandler.class);

    public static void createTopicIfNotExist(final String bootstrapServer, final List<String> topicNames) {
        final Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServer);

        try (AdminClient adminClient = AdminClient.create(props)) {
            List<NewTopic> topics = new ArrayList<>();
            for (String topicName : topicNames) {
                topics.add(new NewTopic(topicName, 1, (short) 1));
                LOGGER.info("Creating topic {}", topicName);
            }

            adminClient.createTopics(topics).all().get(10, TimeUnit.SECONDS);

        } catch (TopicExistsException | ExecutionException | InterruptedException | TimeoutException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static List<String> createRawTopics(final List<String> topicNames) {
        return topicNames.stream().map(topic -> "raw/" + topic).collect(Collectors.toList());
    }

    public static KafkaSource<TopicAwareRecord> createKafkaSource(final String bootstrapServer, final String topicPrefix, final String groupId) {
        if (bootstrapServer == null || bootstrapServer.isEmpty()) {
            LOGGER.error("Bootstrap server is null or empty");
            throw new IllegalArgumentException("Bootstrap server is null or empty");
        }

        if (topicPrefix == null || topicPrefix.isEmpty()) {
            LOGGER.error("TopicPrefix is null or empty");
            throw new IllegalArgumentException("TopicPrefix is null or empty");
        }

        if (groupId == null || groupId.isEmpty()) {
            LOGGER.error("GroupId is null or empty");
            throw new IllegalArgumentException("GroupId is null or empty");
        }

        return KafkaSource.<TopicAwareRecord>builder()
                .setBootstrapServers(bootstrapServer)
                .setTopicPattern(Pattern.compile(topicPrefix + "/.*"))
                .setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(new TopicAwareRecordSchema(topicPrefix))
                .setProperty("partition.discovery.interval.ms", "10000")
                .setProperty("allow.auto.create.topics", "true")
                .build();
    }
}
