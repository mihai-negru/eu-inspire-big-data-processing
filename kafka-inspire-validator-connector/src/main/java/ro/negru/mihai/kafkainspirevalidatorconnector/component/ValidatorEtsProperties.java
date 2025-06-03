package ro.negru.mihai.kafkainspirevalidatorconnector.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("validator.ets")
@Getter
@Setter
public class ValidatorEtsProperties {
    Map<String, List<String>> ids;
}
