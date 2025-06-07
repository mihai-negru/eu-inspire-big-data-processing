package ro.negru.mihai.oslevel;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum OSEnvHandler {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(OSEnvHandler.class);
    private final Map<String, String> envs = new HashMap<>();

    @Getter
    private boolean transformerLoggerEnabled = false;

    public void init() {
        try {
            Map<String, String> osEnvs = System.getenv();

            envs.put("kafka", osEnvs.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
            envs.put("kafka_tmt", osEnvs.getOrDefault("KAFKA_TRANSACTION_MAX_TIMEOUT", "900000"));
            envs.put("cassandra", osEnvs.getOrDefault("CASSANDRA_SERVER", "localhost:9042"));
            envs.put("cassandra_user", osEnvs.getOrDefault("CASSANDRA_USER", "cassandra"));
            envs.put("cassandra_pass", osEnvs.getOrDefault("CASSANDRA_PASSWORD", "cassandra"));
            envs.put("cassandra_dc", osEnvs.getOrDefault("CASSANDRA_DATABASE_CENTER", "eu-east-1"));

            envs.put("fromTransformStream", osEnvs.getOrDefault("KAFKA_FROM_TRANSFORM_TOPIC", "raw"));
            envs.put("fromExecCommandStream", osEnvs.getOrDefault("KAFKA_FROM_EXEC_COMMAND_TOPIC", "command.input"));
            envs.put("fromValidateStream", osEnvs.getOrDefault("KAFKA_FROM_VALIDATE_TOPIC", "validator.output"));
            envs.put("toValidateStream", osEnvs.getOrDefault("KAFKA_TO_VALIDATE_TOPIC", "validator.input"));
            envs.put("toExecCommandStream", osEnvs.getOrDefault("KAFKA_TO_EXEC_COMMAND_TOPIC", "command.output"));
            envs.put("toTransformerLogger", osEnvs.getOrDefault("KAFKA_TO_TRANSFORMER_LOGGER_TOPIC", "transformer.log"));

            final String isLoggerEnabledStr = osEnvs.getOrDefault("KAFKA_IS_TRANSFORMER_LOGGER_ENABLED", "false");
            try {
                transformerLoggerEnabled = Boolean.parseBoolean(isLoggerEnabledStr);
            } catch (Exception e) {
                transformerLoggerEnabled = false;
            }

            LOGGER.info("Kafka server: '{}'", envs.get("kafka"));
            LOGGER.info("Kafka trans_max_timeout: '{}'", envs.get("kafka_tmt"));
            LOGGER.info("Cassandra server: '{}'", envs.get("cassandra"));
            LOGGER.info("Cassandra user: '{}'", envs.get("cassandra_user"));
            LOGGER.info("Cassandra password: '{}'", envs.get("cassandra_pass"));
            LOGGER.info("Cassandra database center: '{}'", envs.get("cassandra_dc"));

            LOGGER.info("Kafka fromTransformStream: '{}'", envs.get("fromTransformStream"));
            LOGGER.info("Kafka fromExecCommandStream: '{}'", envs.get("fromExecCommandStream"));
            LOGGER.info("Kafka fromValidateStream: '{}'", envs.get("fromValidateStream"));
            LOGGER.info("Kafka toValidateStream: '{}'", envs.get("toValidateStream"));
            LOGGER.info("Kafka toExecCommandStream: '{}'", envs.get("toExecCommandStream"));
            LOGGER.info("Kafka toTransformerLogger: '{}'", envs.get("toTransformerLogger"));

            LOGGER.info("Kafka isLoggerEnabled: '{}'", transformerLoggerEnabled);
        } catch (Exception e) {
            LOGGER.error("Failed to load environment variables", e);
        }
    }

    public String getEnv(String key) {
        return envs.get(key);
    }
}
