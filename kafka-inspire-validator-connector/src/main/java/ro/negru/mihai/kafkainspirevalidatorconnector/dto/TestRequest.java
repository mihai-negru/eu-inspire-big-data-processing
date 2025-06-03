package ro.negru.mihai.kafkainspirevalidatorconnector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @NotEmpty
    private String xml;
}
