package ro.negru.mihai.kafkainspirevalidatorconnector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TestRequest {
    @NotBlank
    private final String id;

    @NotNull
    @NotEmpty
    private final String xml;
}
