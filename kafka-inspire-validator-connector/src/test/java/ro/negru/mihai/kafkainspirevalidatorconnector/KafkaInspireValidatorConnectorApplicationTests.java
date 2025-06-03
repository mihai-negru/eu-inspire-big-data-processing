package ro.negru.mihai.kafkainspirevalidatorconnector;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ro.negru.mihai.kafkainspirevalidatorconnector.dto.TestRequest;
import ro.negru.mihai.kafkainspirevalidatorconnector.dto.TestResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(
        properties = {
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.group-id=test-group",
                "server.port=8081",
        }
)
class KafkaInspireValidatorConnectorApplicationTests {
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
            .withEmbeddedZookeeper();

    @Container
    static GenericContainer<?> validator = new GenericContainer<>(DockerImageName.parse("ghcr.io/inspire-mif/helpdesk-validator/inspire-validator:2024.3"))
            .withExposedPorts(8080)
            .waitingFor(
                    org.testcontainers.containers.wait.strategy.Wait
                            .forHttp("/validator/v2/status")
                            .forStatusCode(200)
                            .withStartupTimeout(Duration.ofMinutes(10))
            );

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        final String valAddr = "http://" + validator.getHost() + ":" + validator.getMappedPort(8080) + "/validator";
        registry.add("validator.base.url", () -> valAddr);
    }

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${validator.base.url}")
    private String validatorBaseUrl;

    @Autowired
    private KafkaTemplate<String, TestRequest> kafkaTemplate;

    private MessageListenerContainer responseListenerContainer;
    private BlockingQueue<TestResponse> responses;

    @BeforeEach
    void setUpConsumer() {
        ContainerProperties containerProps = new ContainerProperties("validator.output");
        containerProps.setGroupId("validator-bridge-group");

        Map<String,Object> consumerProps = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, Objects.requireNonNull(containerProps.getGroupId()),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );

        JsonDeserializer<TestResponse> deserializer = new JsonDeserializer<>(TestResponse.class);
        deserializer.addTrustedPackages("*");

        DefaultKafkaConsumerFactory<String,TestResponse> cf =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps,
                        new StringDeserializer(),
                        deserializer
                );

        responseListenerContainer = new KafkaMessageListenerContainer<>(cf, containerProps);
        responses = new LinkedBlockingQueue<>();

        responseListenerContainer.setupMessageListener(
                (MessageListener<String,TestResponse>) (ConsumerRecord<String, TestResponse> record) -> {
                    responses.add(record.value());
                }
        );

        responseListenerContainer.start();
    }

    @AfterEach
    void tearDownConsumer() {
        if (responseListenerContainer != null) {
            responseListenerContainer.stop();
        }
    }

    @Test
    void testRoundtripThroughKafkaAndValidator() throws IOException, InterruptedException {
        String xml = Files.readString(Path.of("src/test/resources/sample.xml"));

        String requestId = UUID.randomUUID().toString();
        TestRequest req = new TestRequest(requestId, xml);

        System.err.println("Sending the message");
        kafkaTemplate.send("validator.input", requestId, req);

        TestResponse resp = responses.poll(10, java.util.concurrent.TimeUnit.MINUTES);
        assertThat(resp).as("should have received exactly one TestResponse").isNotNull();
        assertThat(resp.getId()).isEqualTo(requestId);

        System.out.println(resp.getStatus());
    }
}
