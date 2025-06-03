package ro.negru.mihai.kafkainspirevalidatorconnector.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("job.task.executor")
@Getter
@Setter
public class JobTaskExecutorProperties {
    int core;
    int max;
    int queue;
    String prefix;
}
