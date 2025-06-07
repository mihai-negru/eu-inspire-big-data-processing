package ro.negru.mihai.entity.schema.deserializer;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public abstract class AbstractKafkaJsonDeserializerSchema<T> implements KafkaRecordDeserializationSchema<T> {
    private final Class<T> clazz;
    private final ObjectMapper jsonMapper;

    public AbstractKafkaJsonDeserializerSchema(Class<T> clazz) {
        this.clazz = clazz;
        this.jsonMapper = new ObjectMapper();
    }

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<T> out) throws IOException {
        final byte[] json = record.value();
        if (json != null && json.length > 0) {
            final T obj = jsonMapper.readValue(json, clazz);

            if (obj != null) {
                out.collect(obj);
            }
        }
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(clazz);
    }
}
