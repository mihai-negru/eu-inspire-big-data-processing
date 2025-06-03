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
import ro.negru.mihai.kafkainspirevalidatorconnector.status.ValidatorTestResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        LOGGER.info("Creating a TestObject to pass on for the testing which represents the file for the current payload");

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, Map.class).getBody();
        if (response == null || !response.containsKey("testObject")) {
            LOGGER.error("Could not upload the TestObject because it did not contain a valid testObject");
            throw new RuntimeException("Failed to upload TestObject; no 'testObject' in response");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> testObject = (Map<String, Object>) response.get("testObject");
        if (testObject == null || !testObject.containsKey("id")) {
            LOGGER.error("Failed to upload the TestObject because it did not contain an id inside the testObject");
            throw new RuntimeException("Failed to upload TestObject; no 'id' in response");
        }

        final String id = testObject.get("id").toString();
        LOGGER.info("Successfully uploaded on validator TestObject with id: \"{}\"", id);

        return id;
    }

    public String createTestRun(String etsFamily, String testObjectId) {
        final Map<String, Object> payload = new HashMap<>();
        payload.put("label", String.valueOf(new Random().nextInt()));

        final List<String> suiteIds = validatorEtsProperties.getIds().get(etsFamily);
        if (suiteIds == null) {
            LOGGER.error("The etsFamily \"{}\" is not one of \"{}\"", etsFamily, validatorEtsProperties.getIds().keySet().toString());
            throw new IllegalArgumentException("The etsFamily " + etsFamily + "is invalid");
        }

        if (!suiteIds.isEmpty()) {
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

        LOGGER.info("Creating a TestRun with the TestObject \"{}\"", testObjectId);

        @SuppressWarnings("unchecked")
        final Map<String, Object> response = restTemplate.exchange(runsUrl, HttpMethod.POST, requestEntity, Map.class).getBody();
        final Map<String, Object> testRun = unwrapCreateTestRunResponseBody(response);

        final String testRunId = testRun.get("id").toString();
        LOGGER.info("Successfully uploaded on validator a TestRun with id: \"{}\"", testRunId);

        return testRunId;
    }

    @SuppressWarnings("unchecked")
    public ValidatorTestResponse getTestRunStatus(String testRunId) {
        final String statusUrl = UriComponentsBuilder
                .fromUriString(validatorBaseUrl)
                .pathSegment("v2", "TestRuns", testRunId + ".json")
                .build()
                .toUriString();

        LOGGER.info("Retrieving TestRun Status for TestRun with id: \"{}\"", testRunId);

        final Map<String, Object> response = restTemplate.getForObject(statusUrl, Map.class);
        final Map<String, Object> testRun = unwrapStatusTestRunResponseBody(response);

        final Map<String, Object> testTaskResults = (Map<String, Object>) testRun.get("testTaskResults");
        if (testTaskResults == null) {
            LOGGER.warn("TestRun with id \"{}\" returned an empty status, no following action needed", testRunId);
            return null;
        }

        LOGGER.info("TestRun with id \"{}\" has finished, computing the status response", testRunId);
        return ValidatorTestResponse.fromBody(testTaskResults);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> unwrapCreateTestRunResponseBody(final Map<String, Object> response) {
        if (response == null || !response.containsKey("EtfItemCollection")) {
            throw new RuntimeException("Failed to unwrap TestRun; no \"EtfItemCollection\" in response");
        }

        Map<String, Object> newBody = (Map<String, Object>) response.get("EtfItemCollection");
        if (newBody == null || !newBody.containsKey("testRuns")) {
            throw new RuntimeException("Failed to unwrap TestRun; no \"testRuns\" in response");
        }

        newBody = (Map<String,Object>) newBody.get("testRuns");
        if (newBody == null || !newBody.containsKey("TestRun")) {
            throw new RuntimeException("Failed to unwrap TestRun; no \"TestRun\" in response");
        }

        newBody = (Map<String, Object>) newBody.get("TestRun");
        if (newBody == null || !newBody.containsKey("id")) {
            throw new RuntimeException("Failed to unwrap TestRun; no \"id\" in response");
        }

        return newBody;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> unwrapStatusTestRunResponseBody(final Map<String, Object> response) {
        if (response == null || !response.containsKey("EtfItemCollection")) {
            throw new RuntimeException("Failed to unwrap TestRun; no \"EtfItemCollection\" in response");
        }

        Map<String, Object> newBody = (Map<String, Object>) response.get("EtfItemCollection");
        if (newBody == null || !newBody.containsKey("referencedItems")) {
            throw new RuntimeException("Failed to unwrap TestRun; no \"referencedItems\" in response");
        }

        newBody = (Map<String,Object>) newBody.get("referencedItems");
        return newBody;
    }
}
