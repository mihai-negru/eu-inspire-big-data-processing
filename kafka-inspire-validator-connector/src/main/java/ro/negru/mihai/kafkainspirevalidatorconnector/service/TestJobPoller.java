package ro.negru.mihai.kafkainspirevalidatorconnector.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ro.negru.mihai.kafkainspirevalidatorconnector.dto.TestResponse;

import java.util.List;

@Component
public class TestJobPoller {
    private final TestJobService testJobService;
    private final KafkaTemplate<String, TestResponse> kafkaTemplate;
    private final String kafkaOutputTopic;

    public TestJobPoller(TestJobService testJobService,
                         KafkaTemplate<String, TestResponse> kafkaTemplate,
                         @Value("${topics.output}") String kafkaOutputTopic) {
        this.testJobService = testJobService;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaOutputTopic = kafkaOutputTopic;
    }

    @Scheduled(fixedDelayString = "${validator.poll.delay}")
    public void poll() {
        List<TestResponse> responses = testJobService.getCompletedTestJobsAsResponses();

        for (TestResponse response : responses) {
            kafkaTemplate.send(kafkaOutputTopic, response.getId(), response);
        }
    }
}
