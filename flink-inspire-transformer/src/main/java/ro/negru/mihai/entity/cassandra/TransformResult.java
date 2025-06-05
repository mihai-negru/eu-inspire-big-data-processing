package ro.negru.mihai.entity.cassandra;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.Map;

@Table(keyspace = "inspire", name = "transformed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransformResult {
    @Column(name = "id")
    private String id;

    @Column(name = "xml_path")
    private String xmlPath;

    @Column(name = "xml")
    private ByteBuffer xml;

    @Column(name = "status")
    private String status;

    @Column(name = "failure_details")
    private Map<String, String> failureDetails;
}
