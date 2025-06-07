package ro.negru.mihai.entity.schema.deserializer;

import ro.negru.mihai.entity.kafka.TransformRequest;

public class TransformRequestSchema extends AbstractKafkaJsonDeserializerSchema<TransformRequest> {
    public TransformRequestSchema() {
        super(TransformRequest.class);
    }
}