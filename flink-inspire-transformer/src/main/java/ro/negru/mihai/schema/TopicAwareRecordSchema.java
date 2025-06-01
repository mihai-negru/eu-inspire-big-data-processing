package ro.negru.mihai.schema;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class TopicAwareRecordSchema implements KafkaRecordDeserializationSchema<TopicAwareRecord> {
    private final DeserializationSchema<String> textDeserializer;
    private final String topicPrefix;

    public TopicAwareRecordSchema(final String removeTopicPrefix) {
        topicPrefix = removeTopicPrefix + '/';
        textDeserializer = new SimpleStringSchema();
    }

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> consumerRecord, Collector<TopicAwareRecord> collector) throws IOException {
        final String topic = consumerRecord.topic();

        if (topic.startsWith(topicPrefix)) {
            final String text = textDeserializer.deserialize(consumerRecord.value());

            collector.collect(new TopicAwareRecord(topic.replaceFirst(topicPrefix, ""), text));
        }
    }

    @Override
    public TypeInformation<TopicAwareRecord> getProducedType() {
        return TypeInformation.of(TopicAwareRecord.class);
    }
}