package ro.negru.mihai.kafkainspirevalidatorconnector.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String requestId;

    @Column(nullable = false)
    private String requestGroupId;

    @Column
    private String testObjectId;

    @Column
    private String testRunId;

    @Column
    Instant created;

    @Column
    Instant updated;
}
