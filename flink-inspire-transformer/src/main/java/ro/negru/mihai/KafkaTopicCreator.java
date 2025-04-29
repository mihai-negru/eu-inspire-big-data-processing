package ro.negru.mihai;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KafkaTopicCreator {
    public static void createTopicIfNotExist(final String bootstrapServer, final String topicName) {
        final Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServer);

        try (AdminClient adminClient = AdminClient.create(props)) {
            adminClient.createTopics(Collections.singleton(new NewTopic(topicName, 1, (short)1)))
                    .all().get(10, TimeUnit.SECONDS);
        } catch (TopicExistsException | ExecutionException | InterruptedException | TimeoutException e) {
            // Logger
            e.printStackTrace();
        }
    }
}
