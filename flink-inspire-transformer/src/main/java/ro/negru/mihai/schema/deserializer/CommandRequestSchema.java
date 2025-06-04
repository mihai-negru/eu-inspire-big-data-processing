package ro.negru.mihai.schema.deserializer;

import ro.negru.mihai.entity.kafka.CommandRequest;

public class CommandRequestSchema extends AbstractKafkaJsonDeserializerSchema<CommandRequest> {
    public CommandRequestSchema() {
        super(CommandRequest.class);
    }
}
