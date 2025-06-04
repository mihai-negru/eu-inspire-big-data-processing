package ro.negru.mihai.oslevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum OSEnvHandler {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(OSEnvHandler.class);
    private final Map<String, String> envs = new HashMap<>();

    static {
        OSEnvHandler.INSTANCE.init();
    }

    public void init() {
        try {
            Map<String, String> osEnvs = System.getenv();

            envs.put("kafka", osEnvs.getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
            envs.put("cassandra", osEnvs.getOrDefault("CASSANDRA_SERVER", "localhost:9042"));

            LOGGER.info("Kafka server: {}", envs.get("kafka"));
            LOGGER.info("Cassandra server: {}", envs.get("cassandra"));
        } catch (Exception e) {
            LOGGER.error("Failed to load environment variables", e);
        }
    }

    public String getEnv(String key) {
        return envs.get(key);
    }
}
