package ro.negru.mihai.entity.schema.deserializer;

import ro.negru.mihai.entity.command.CommandRequest;

public class CommandRequestSchema extends AbstractKafkaJsonDeserializerSchema<CommandRequest> {
    public CommandRequestSchema() {
        super(CommandRequest.class);
    }
}
