package ro.negru.mihai.kafkainspirevalidatorconnector.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestRequest {
    @NotBlank
    private String id;

    @NotBlank
    private String etsFamily;

    @NotBlank
    private String xml;
}
