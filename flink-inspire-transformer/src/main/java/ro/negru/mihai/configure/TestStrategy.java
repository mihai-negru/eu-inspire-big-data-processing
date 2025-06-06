package ro.negru.mihai.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.configure.entity.schema.RootConfig;

import java.io.File;

public enum TestStrategy {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStrategy.class);
    private static final String TEST_STRATEGY_FILE_PATH = "/opt/flink/conf/flink-test-strategy.yaml";

    private RootConfig config;

    static {
        TestStrategy.INSTANCE.init();
    }

    private void init() {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            config = mapper.readValue(new File(TEST_STRATEGY_FILE_PATH), RootConfig.class);
        } catch (Exception e) {
            LOGGER.error("Could not read configuration file", e);
        }
    }

    public RootConfig getConfig() {
        return config;
    }
}
