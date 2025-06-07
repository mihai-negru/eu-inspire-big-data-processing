package ro.negru.mihai.oslevel;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class OSEnvHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(OSEnvHandler.class);
    private final Map<String, String> envs;

    @Getter
    private boolean transformerLoggerEnabled;

    private OSEnvHandler() {
        envs = new HashMap<>();
        transformerLoggerEnabled = false;
    }

    public static OSEnvHandler readSystemEnvs() {
        final OSEnvHandler osEnvHandler = new OSEnvHandler();

        try {
            Map<String, String> osEnvs = System.getenv();

            osEnvHandler.envs.put("kafka", osEnvs.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
            osEnvHandler.envs.put("kafka_tmt", osEnvs.getOrDefault("KAFKA_TRANSACTION_MAX_TIMEOUT", "900000"));
            osEnvHandler.envs.put("cassandra", osEnvs.getOrDefault("CASSANDRA_SERVER", "localhost:9042"));
            osEnvHandler.envs.put("cassandra_user", osEnvs.getOrDefault("CASSANDRA_USER", "cassandra"));
            osEnvHandler.envs.put("cassandra_pass", osEnvs.getOrDefault("CASSANDRA_PASSWORD", "cassandra"));
            osEnvHandler.envs.put("cassandra_dc", osEnvs.getOrDefault("CASSANDRA_DATABASE_CENTER", "eu-east-1"));

            osEnvHandler.envs.put("fromTransformStream", osEnvs.getOrDefault("KAFKA_FROM_TRANSFORM_TOPIC", "raw"));
            osEnvHandler.envs.put("fromExecCommandStream", osEnvs.getOrDefault("KAFKA_FROM_EXEC_COMMAND_TOPIC", "command.input"));
            osEnvHandler.envs.put("fromValidateStream", osEnvs.getOrDefault("KAFKA_FROM_VALIDATE_TOPIC", "validator.output"));
            osEnvHandler.envs.put("toValidateStream", osEnvs.getOrDefault("KAFKA_TO_VALIDATE_TOPIC", "validator.input"));
            osEnvHandler.envs.put("toExecCommandStream", osEnvs.getOrDefault("KAFKA_TO_EXEC_COMMAND_TOPIC", "command.output"));
            osEnvHandler.envs.put("toTransformerLogger", osEnvs.getOrDefault("KAFKA_TO_TRANSFORMER_LOGGER_TOPIC", "transformer.log"));

            final String isLoggerEnabledStr = osEnvs.getOrDefault("KAFKA_IS_TRANSFORMER_LOGGER_ENABLED", "false");
            try {
                osEnvHandler.transformerLoggerEnabled = Boolean.parseBoolean(isLoggerEnabledStr);
            } catch (Exception e) {
                osEnvHandler.transformerLoggerEnabled = false;
            }

            LOGGER.info("Kafka server: '{}'", osEnvHandler.envs.get("kafka"));
            LOGGER.info("Kafka trans_max_timeout: '{}'", osEnvHandler.envs.get("kafka_tmt"));
            LOGGER.info("Cassandra server: '{}'", osEnvHandler.envs.get("cassandra"));
            LOGGER.info("Cassandra user: '{}'", osEnvHandler.envs.get("cassandra_user"));
            LOGGER.info("Cassandra password: '{}'", osEnvHandler.envs.get("cassandra_pass"));
            LOGGER.info("Cassandra database center: '{}'", osEnvHandler.envs.get("cassandra_dc"));

            LOGGER.info("Kafka fromTransformStream: '{}'", osEnvHandler.envs.get("fromTransformStream"));
            LOGGER.info("Kafka fromExecCommandStream: '{}'", osEnvHandler.envs.get("fromExecCommandStream"));
            LOGGER.info("Kafka fromValidateStream: '{}'", osEnvHandler.envs.get("fromValidateStream"));
            LOGGER.info("Kafka toValidateStream: '{}'", osEnvHandler.envs.get("toValidateStream"));
            LOGGER.info("Kafka toExecCommandStream: '{}'", osEnvHandler.envs.get("toExecCommandStream"));
            LOGGER.info("Kafka toTransformerLogger: '{}'", osEnvHandler.envs.get("toTransformerLogger"));

            LOGGER.info("Kafka isLoggerEnabled: '{}'", osEnvHandler.transformerLoggerEnabled);
        } catch (Exception e) {
            LOGGER.error("Failed to load environment variables", e);
        }

        return osEnvHandler;
    }

    public String getEnv(String key) {
        return envs.get(key);
    }
}
