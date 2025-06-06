package ro.negru.mihai.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.configure.entity.schema.RootConfig;
import ro.negru.mihai.configure.entity.schema.SchemaConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public enum TestStrategy {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStrategy.class);
    private static final String TEST_STRATEGY_FILE_PATH = "/opt/flink/conf/flink-test-strategy.yaml";

    private Map<String, SchemaConfig> config;

    static {
        TestStrategy.INSTANCE.init();
    }

    private void init() {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        RootConfig rootConfig;
        try {
            rootConfig = mapper.readValue(new File(TEST_STRATEGY_FILE_PATH), RootConfig.class);
        } catch (Exception e) {
            LOGGER.error("Could not read configuration file", e);
            return;
        }

        if (rootConfig != null) {
            LOGGER.info("Parsed the following root config: {}", rootConfig);

            config = new HashMap<>();
            for (SchemaConfig schemaConfig : rootConfig.getSchemas()) {
                config.put(schemaConfig.getName(), schemaConfig);
            }
        } else {
            LOGGER.error("Could not parse the configuration file");
        }
    }

    public SchemaConfig getSchemaConfig(final String schema) {
        return config.get(schema);
    }
}
