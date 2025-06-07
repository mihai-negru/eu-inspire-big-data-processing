package ro.negru.mihai.configure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.configure.entity.schema.RootConfig;
import ro.negru.mihai.configure.entity.schema.SchemaConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestStrategy implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStrategy.class);
    private static final Path TEST_STRATEGY_FILE_PATH = Paths.get(/*File.separator, "opt", "flink", "usrlib", */"flink-test-strategy.yaml");

    private final Map<String, SchemaConfig> config;

    private TestStrategy() {
        config = new HashMap<>();
    }

    public static TestStrategy readTestStrategy() {
        final TestStrategy testStrategy = new TestStrategy();
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        RootConfig rootConfig;
        try (final InputStream in = Files.newInputStream(TEST_STRATEGY_FILE_PATH)) {
            rootConfig = mapper.readValue(in, RootConfig.class);
        } catch (NoSuchFileException e) {
            LOGGER.error("Could not find test strategy file {}", TEST_STRATEGY_FILE_PATH);
            return testStrategy;
        } catch (IOException e) {
            LOGGER.error("Could not read test strategy file", e);
            return testStrategy;
        } catch (Exception e) {
            LOGGER.error("Could not map the test strategy file", e);
            return testStrategy;
        }

        if (rootConfig != null) {
            LOGGER.info("Parsed the following root config: {}", rootConfig);

            for (SchemaConfig schemaConfig : rootConfig.getSchemas()) {
                testStrategy.config.put(schemaConfig.getName(), schemaConfig);
            }
        } else {
            LOGGER.error("The test strategy file could not be parsed");
        }

        return testStrategy;
    }

    public SchemaConfig getSchemaConfig(final String schema) {
        return config.get(schema);
    }
}
