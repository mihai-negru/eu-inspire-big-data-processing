package ro.negru.mihai.webinspireconverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    private final String TOPIC = "test-topic";

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            byte[] fileBytes = file.getBytes();
            // Send the file bytes to the Kafka topic
            kafkaTemplate.send(TOPIC, fileBytes);
            model.addAttribute("success", true);
        } catch (Exception e) {
            // Log error and set error attribute if needed
            model.addAttribute("success", false);
        }
        return "upload";
    }
}
