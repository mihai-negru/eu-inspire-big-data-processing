package ro.negru.mihai.kafkainspirevalidatorconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ro.negru.mihai.kafkainspirevalidatorconnector.component.ValidatorEtsProperties;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

@Service
public class CallInspireValidatorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallInspireValidatorService.class);

    private final RestTemplate restTemplate;
    private final String validatorBaseUrl;
    private final ValidatorEtsProperties validatorEtsProperties;

    public CallInspireValidatorService(RestTemplate restTemplate,
                                       ValidatorEtsProperties validatorEtsProperties,
                                       @Value("${validator.base.url}") String validatorBaseUrl
                                       ) {
        this.restTemplate = restTemplate;
        this.validatorBaseUrl = validatorBaseUrl;
        this.validatorEtsProperties = validatorEtsProperties;
    }

    public String uploadTestObject(final String xmlPayload) {
        final ByteArrayResource xmlResource = new ByteArrayResource(xmlPayload.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "to_test_inspire.xml";
            }
        };

        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("fileupload", xmlResource)
                .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"fileupload\"; filename=\"" + xmlResource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_XML);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, HttpEntity<?>>> requestEntity = new HttpEntity<>(builder.build(), headers);

        final String uploadUrl = UriComponentsBuilder
                .fromUriString(validatorBaseUrl)
                .pathSegment("v2", "TestObjects")
                .queryParam("action", "upload")
                .build()
                .toUriString();

        LOGGER.info("Upload a TestObject at the following url: {}", uploadUrl);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, Map.class).getBody();
        if (response == null || !response.containsKey("testObject")) {
            LOGGER.error("Failed to upload test object: {}", response != null ? response.toString() : null);
            throw new RuntimeException("Failed to upload TestObject; no 'testObject' in response");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> testObject = (Map<String, Object>) response.get("testObject");
        if (testObject == null || !testObject.containsKey("id")) {
            LOGGER.error("Failed to upload test object: {}", testObject != null ? testObject.toString() : null);
            throw new RuntimeException("Failed to upload TestObject; no 'id' in response");
        }

        LOGGER.info("Successfully uploaded test object, and retrieved the id: {}", testObject.get("id").toString());
        return testObject.get("id").toString();
    }

    public String createTestRun(String testObjectId) {
        final Map<String, Object> payload = new HashMap<>();
        payload.put("label", String.valueOf(new Random().nextInt()));

        final List<String> suiteIds = validatorEtsProperties.getIds();
        if (suiteIds != null && !suiteIds.isEmpty()) {
            payload.put("executableTestSuiteIds", suiteIds);
        }

        final Map<String, String> arguments = new HashMap<>();
        arguments.put("files_to_test", ".*");
        arguments.put("tests_to_execute", ".*");
        payload.put("arguments", arguments);

        final Map<String, String> testObjectRef = new HashMap<>();
        testObjectRef.put("id", testObjectId);
        payload.put("testObject", testObjectRef);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        final String runsUrl = UriComponentsBuilder
                .fromUriString(validatorBaseUrl)
                .pathSegment("v2", "TestRuns")
                .build()
                .toUriString();

        LOGGER.info("Create a TestRun at the following url: {}", runsUrl);

        @SuppressWarnings("unchecked")
        final Map<String, Object> response = restTemplate.exchange(runsUrl, HttpMethod.POST, requestEntity, Map.class).getBody();
        final Map<String, Object> testRun = getTestRunInfo(response);

        LOGGER.info("Successfully created a TestRun with id: {}", testRun.get("id").toString());
        return testRun.get("id").toString();
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getTestRunStatus(String testRunId) {
        final String statusUrl = UriComponentsBuilder
                .fromUriString(validatorBaseUrl)
                .pathSegment("v2", "TestRuns", testRunId)
                .build()
                .toUriString();

        LOGGER.info("Retrieve a TestRun status at the following url: {}", statusUrl);

        final Map<String, Object> response = restTemplate.getForObject(statusUrl, Map.class);
        final Map<String, Object> testRun = getTestRunInfo(response);

        final Map<String, Object> testTaskResults = (Map<String, Object>) testRun.get("testTaskResults");
        if (testTaskResults == null) {
            LOGGER.info("Task with id {} has not finished yet", testRunId);
            return null;
        }

        LOGGER.info("Task with id {} has finished", testRunId);
        final Map<String, String> result = new HashMap<>();

        Object maybeTasks = testTaskResults.get("TestTaskResult");
        if (maybeTasks instanceof List) {
            for (Object o : (List<Object>) maybeTasks) {
                extractSingleTestTaskResult((Map<String,Object>) o, result);
            }
        } else if (maybeTasks instanceof Map) {
            extractSingleTestTaskResult((Map<String,Object>) maybeTasks, result);
        }

        return result;
    }

    private Map<String, Object> getTestRunInfo(final Map<String, Object> response) {
        LOGGER.info("Trying to retrive the testRunInfo");

        if (response == null || !response.containsKey("EtfItemCollection")) {
            LOGGER.error("Failed to upload TestRun; no 'EtfItemCollection' in response");
            throw new RuntimeException("Failed to upload TestRun; no 'EtfItemCollection' in response");
        }

        @SuppressWarnings("unchecked")
        final Map<String, Object> etfItemCollection = (Map<String, Object>) response.get("EtfItemCollection");
        if (etfItemCollection == null || !etfItemCollection.containsKey("testRuns")) {
            LOGGER.error("Failed to upload TestRun; no 'testRuns' in response");
            throw new RuntimeException("Failed to upload TestRun; no 'testRuns' in response");
        }

        @SuppressWarnings("unchecked")
        final Map<String,Object> testRuns = (Map<String,Object>) etfItemCollection.get("testRuns");
        if (testRuns == null || !testRuns.containsKey("TestRun")) {
            LOGGER.error("Failed to upload TestRun; no 'TestRun' in response");
            throw new RuntimeException("Failed to upload TestRun; no 'TestRun' in response");
        }

        @SuppressWarnings("unchecked")
        final Map<String, Object> testRun = (Map<String, Object>) testRuns.get("TestRun");
        if (testRun == null || !testRun.containsKey("id")) {
            LOGGER.error("Failed to upload TestRun; no 'id' in response");
            throw new RuntimeException("Failed to upload TestRun; no 'id' in response");
        }

        LOGGER.info("Successfully retrived the testRunInfo");
        return testRun;
    }

    @SuppressWarnings("unchecked")
    private void extractAndDfsResult(Map<String, Object> node, Map<String, String> result, String dfsKey, String dfsWrapper, BiConsumer<Map<String, Object>, Map<String, String>> dfsCall) {
        if (node.containsKey("id") && node.containsKey("status")) {
            String id = node.get("id").toString();
            String status = node.get("status").toString();

            LOGGER.info("Retrieving testRun with id {} and status {}", id, status);
            result.put(id, status);
        }

        if (dfsKey != null && dfsWrapper != null) {
            if (node.containsKey(dfsKey)) {
                Map<String, Object> container = (Map<String, Object>) node.get(dfsKey);

                Object wrapperContainer = container.get(dfsWrapper);
                if (wrapperContainer instanceof List) {
                    for (Object o : (List<Object>) wrapperContainer) {
                        dfsCall.accept((Map<String, Object>) o, result);
                    }
                } else if (wrapperContainer instanceof Map) {
                    dfsCall.accept((Map<String, Object>) wrapperContainer, result);
                }
            }
        }
    }

    private void extractSingleTestTaskResult(Map<String,Object> node, Map<String,String> result) {
        extractAndDfsResult(node, result, "testModuleResults", "TestModuleResult", this::extractSingleModuleResult);
    }

    private void extractSingleModuleResult(Map<String,Object> node, Map<String,String> result) {
        extractAndDfsResult(node, result, "testCaseResults", "TestCaseResult", this::extractSingleCaseResult);
    }

    private void extractSingleCaseResult(Map<String,Object> node, Map<String,String> result) {
        extractAndDfsResult(node, result, "testStepResults", "testStepResults", this::extractSingleStepResult);
    }

    private void extractSingleStepResult(Map<String,Object> node, Map<String,String> result) {
        extractAndDfsResult(node, result, "testAssertionResults", "TestAssertionResult", this::extractSingleAssertionResult);
    }

    private void extractSingleAssertionResult(Map<String,Object> node, Map<String,String> result) {
        extractAndDfsResult(node, result, null, null, null);
    }
}
