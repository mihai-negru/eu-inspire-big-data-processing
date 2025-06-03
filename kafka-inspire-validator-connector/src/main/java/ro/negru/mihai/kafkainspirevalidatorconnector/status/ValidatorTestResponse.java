package ro.negru.mihai.kafkainspirevalidatorconnector.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ValidatorTestResponse {
    List<TestAssertionResult> etsAssertions;

    public static ValidatorTestResponse fromBody(final Map<String, Object> json) {
        final ValidatorTestResponse validatorTestResponse = new ValidatorTestResponse();

        validatorTestResponse.etsAssertions = findAssertions(json);
        return validatorTestResponse;
    }

    private static List<TestAssertionResult> findAssertions(final Map<String, Object> json) {
        final List<TestAssertionResult> assertions = new ArrayList<>();
        final JsonNode root = new ObjectMapper().valueToTree(json);

        findAssertionsDfs(root, assertions);
        return assertions;
    }

    private static void findAssertionsDfs(final JsonNode node, final List<TestAssertionResult> assertions) {
        if (node.isObject()) {
            final Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                final String fieldName = fieldNames.next();
                final JsonNode childNode = node.get(fieldName);

                if ("TestAssertionResult".equals(fieldName)) {
                    if (childNode.isArray())
                        childNode.forEach(arrayNode -> { assertions.add(TestAssertionResult.fromJson(arrayNode)); });
                    else if (childNode.isObject())
                        assertions.add(TestAssertionResult.fromJson(childNode));
                } else if (childNode.isContainerNode()) {
                    findAssertionsDfs(childNode, assertions);
                }
            }
        } else if (node.isArray()) {
            for (final JsonNode childNode : node) {
                findAssertionsDfs(childNode, assertions);
            }
        }
    }
}
