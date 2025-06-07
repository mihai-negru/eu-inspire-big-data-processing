package ro.negru.mihai.entity.cassandra;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.Map;

import com.datastax.oss.driver.api.core.cql.Row;

@Table(keyspace = "inspire", name = "transformed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransformResult {
    @Column(name = "id")
    private String id;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "xml_schema")
    private String xmlSchema;

    @Column(name = "xml_path")
    private String xmlPath;

    @Column(name = "xml")
    private ByteBuffer xml;

    @Column(name = "status")
    private String status;

    @Column(name = "failure_details")
    private Map<String, String> failureDetails;

    public static TransformResult fromRow(Row row) {
        final String id = row.getString("id");
        final String groupId = row.getString("group_id");
        final String xmlSchema = row.getString("xml_schema");
        final String xmlPath = row.getString("xml_path");
        final ByteBuffer xmlBytes = row.getByteBuffer("xml");
        final String status = row.getString("status");
        final Map<String, String> failureDetails = row.getMap("failure_details", String.class, String.class);

        return new TransformResult(id, groupId, xmlSchema, xmlPath, xmlBytes, status, failureDetails);
    }

    public static String lookUpStatement() {
        return "SELECT * FROM transformed WHERE id=?";
    }

    public static String lookUpGroupStatement() {
        return "SELECT * FROM transformed WHERE group_id=?";
    }
}
