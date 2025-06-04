package ro.negru.mihai.kafkainspirevalidatorconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ro.negru.mihai.kafkainspirevalidatorconnector.dto.TestRequest;
import ro.negru.mihai.kafkainspirevalidatorconnector.dto.TestResponse;
import ro.negru.mihai.kafkainspirevalidatorconnector.entity.TestJob;
import ro.negru.mihai.kafkainspirevalidatorconnector.repository.TestJobRepository;
import ro.negru.mihai.kafkainspirevalidatorconnector.status.ValidatorTestResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestJobService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestJobService.class);
    private final TestJobRepository testJobRepository;
    private final CallInspireValidatorService callInspireValidatorService;

    public TestJobService(TestJobRepository testJobRepository, CallInspireValidatorService callInspireValidatorService) {
        this.testJobRepository = testJobRepository;
        this.callInspireValidatorService = callInspireValidatorService;
    }

    public void submitNewTestJob(final TestRequest req) {
        TestJob job = TestJob.builder()
                .requestId(req.getId())
                .created(Instant.now())
                .updated(Instant.now())
                .build();

        testJobRepository.save(job);

        doUploadAndRunAsync(job.getId(), req);
    }

    @Async("jobTaskExecutor")
    public void doUploadAndRunAsync(final Long id, final TestRequest req) {
        testJobRepository.findById(id).ifPresent(job -> {
            try {
                final String testObjectId = callInspireValidatorService.uploadTestObject(req.getXml());
                final String testRunId = callInspireValidatorService.createTestRun(req.getEtsFamily(), testObjectId);

                job.setTestObjectId(testObjectId);
                job.setTestRunId(testRunId);
                job.setUpdated(Instant.now());
                testJobRepository.save(job);
            } catch (Exception e) {
                testJobRepository.deleteById(id);
                LOGGER.error("Due to the following error the TestJob with id \"{}\" was deleted from the repository {}", id, e);
            }
        });
    }

    public List<TestResponse> getCompletedTestJobsAsResponses() {

        final List<TestJob> jobs = testJobRepository.findAll();
        if (jobs.isEmpty()) {
            return List.of();
        }

        List<TestResponse> responses = new ArrayList<>();

        int counter = 0;
        for (TestJob job : jobs) {
            final ValidatorTestResponse status = callInspireValidatorService.getTestRunStatus(job.getTestRunId());

            if (status == null)
                continue;

            counter++;
            responses.add(new TestResponse(job.getRequestId(), status));
            testJobRepository.delete(job);
        }

        LOGGER.info("Completed jobs [{}/{}]", counter, jobs.size());
        return responses;
    }
}
