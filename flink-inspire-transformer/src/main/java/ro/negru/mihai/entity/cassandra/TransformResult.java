package ro.negru.mihai.entity.cassandra;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Table(keyspace = "inspire", name = "transformed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransformResult {
    @Column(name = "id")
    private UUID id;

    @Column(name = "xml")
    private byte[] xml;

    @Column(name = "status")
    private String status;

    @Column(name = "failure_details")
    private Map<String, String> failureDetails;
}
