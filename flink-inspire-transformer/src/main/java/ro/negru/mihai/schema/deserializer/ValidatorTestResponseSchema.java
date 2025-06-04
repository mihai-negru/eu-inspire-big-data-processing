package ro.negru.mihai.schema.deserializer;

import ro.negru.mihai.entity.validator.ValidatorTestResponse;

public class ValidatorTestResponseSchema extends AbstractKafkaJsonDeserializerSchema<ValidatorTestResponse> {
    public ValidatorTestResponseSchema() {
        super(ValidatorTestResponse.class);
    }
}
