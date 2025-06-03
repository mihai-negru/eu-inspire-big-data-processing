package ro.negru.mihai.kafkainspirevalidatorconnector.status;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TestAssertionResult {
    private String assertionEts;
    private String assertionStatus;

    public static TestAssertionResult fromJson(JsonNode node) {
        final TestAssertionResult assertionResult = new TestAssertionResult();

        assertionResult.assertionEts = node.get("resultedFrom").get("ref").asText();
        assertionResult.assertionStatus = node.get("status").asText();

        return assertionResult;
    }
}
