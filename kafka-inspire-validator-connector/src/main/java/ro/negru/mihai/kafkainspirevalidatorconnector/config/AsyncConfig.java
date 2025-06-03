package ro.negru.mihai.kafkainspirevalidatorconnector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ro.negru.mihai.kafkainspirevalidatorconnector.component.JobTaskExecutorProperties;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private final JobTaskExecutorProperties jobTaskExecutorProperties;

    public AsyncConfig(JobTaskExecutorProperties jobTaskExecutorProperties) {
        this.jobTaskExecutorProperties = jobTaskExecutorProperties;
    }

    @Bean(name = "jobTaskExecutor")
    public Executor jobTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(jobTaskExecutorProperties.getCore());
        executor.setMaxPoolSize(jobTaskExecutorProperties.getMax());
        executor.setQueueCapacity(jobTaskExecutorProperties.getQueue());
        executor.setThreadNamePrefix(jobTaskExecutorProperties.getPrefix());
        executor.initialize();
        return executor;
    }
}
