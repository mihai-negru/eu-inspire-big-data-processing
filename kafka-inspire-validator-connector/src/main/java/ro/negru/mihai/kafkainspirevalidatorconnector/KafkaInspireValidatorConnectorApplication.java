package ro.negru.mihai.kafkainspirevalidatorconnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaInspireValidatorConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaInspireValidatorConnectorApplication.class, args);
    }

}
