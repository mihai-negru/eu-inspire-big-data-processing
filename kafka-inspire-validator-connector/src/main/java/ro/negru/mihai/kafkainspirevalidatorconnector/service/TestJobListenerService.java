package ro.negru.mihai.kafkainspirevalidatorconnector.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.negru.mihai.kafkainspirevalidatorconnector.dto.TestRequest;

@Component
@RequiredArgsConstructor
public class TestJobListenerService {
    private final static Logger LOGGER = LoggerFactory.getLogger(TestJobListenerService.class);
    private final TestJobService testJobService;

    @KafkaListener(
            topics = "${topics.input}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTestRequest(TestRequest req) {
        testJobService.submitNewTestJob(req);
    }
}
