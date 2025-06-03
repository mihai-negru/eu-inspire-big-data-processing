package ro.negru.mihai.kafkainspirevalidatorconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ro.negru.mihai.kafkainspirevalidatorconnector.dto.TestRequest;
import ro.negru.mihai.kafkainspirevalidatorconnector.dto.TestResponse;
import ro.negru.mihai.kafkainspirevalidatorconnector.entity.TestJob;
import ro.negru.mihai.kafkainspirevalidatorconnector.repository.TestJobRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        LOGGER.info("A request to submit a new test job {} with payload {}" , req.getId(), req.getXml());
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
        TestJob job = testJobRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Job with id {} not found", id);
                    return new IllegalStateException("No TestJob with ID " + id);
                });

        try {
            LOGGER.info("Submitting a new test object {}", job);
            final String testObjectId = callInspireValidatorService.uploadTestObject(req.getXml());
            LOGGER.info("Successfully uploaded test object {}", testObjectId);

            LOGGER.info("Submitted a new test run {}", job);
            final String testRunId = callInspireValidatorService.createTestRun(testObjectId);
            LOGGER.info("Successfully uploaded test run {}", testRunId);

            job.setTestObjectId(testObjectId);
            job.setTestRunId(testRunId);
            job.setUpdated(Instant.now());
            testJobRepository.save(job);
        } catch (Exception e) {
            testJobRepository.deleteById(id);
            LOGGER.error("Error uploading test job", e);
        }
    }

    public List<TestResponse> getCompletedTestJobsAsResponses() {

        LOGGER.info("Retrieving completed test jobs by polling");

        final List<TestJob> jobs = testJobRepository.findAll();
        if (jobs.isEmpty()) {
            return List.of();
        }

        List<TestResponse> responses = new ArrayList<>();
        for (TestJob job : jobs) {
            final Map<String, String> statuses = callInspireValidatorService.getTestRunStatus(job.getTestRunId());

            if (statuses == null)
                continue;

            responses.add(new TestResponse(job.getRequestId(), statuses));
            testJobRepository.delete(job);
        }

        LOGGER.info("Retrieved completed test jobs by polling");
        return responses;
    }
}
