package ro.negru.mihai.kafkainspirevalidatorconnector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.negru.mihai.kafkainspirevalidatorconnector.dto.TestRequest;

@Component
@RequiredArgsConstructor
public class TestJobListenerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestJobListenerService.class);

    private final TestJobService testJobService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${topics.input}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTestRequest(final String json) {
        try {
            final TestRequest testRequest = objectMapper.readValue(json, TestRequest.class);
            testJobService.submitNewTestJob(testRequest);
        } catch (Exception e) {
            LOGGER.error("Could not parse the json object", e);
        }
    }
}
