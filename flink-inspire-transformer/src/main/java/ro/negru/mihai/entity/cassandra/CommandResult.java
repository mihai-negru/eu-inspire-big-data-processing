package ro.negru.mihai.entity.cassandra;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.oss.driver.api.core.cql.Row;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.ByteBuffer;

@Table(keyspace = "inspire", name = "executed")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommandResult {
    @Column(name = "id")
    private String id;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "command")
    private String command;

    @Column(name = "obj")
    private ByteBuffer obj;

    public static CommandResult fromRow(Row row) {
        final String id = row.getString("id");
        final String groupId = row.getString("group_id");
        final String command = row.getString("command");
        final ByteBuffer obj = row.getByteBuffer("obj");

        return new CommandResult(id, groupId, command, obj);
    }
}
