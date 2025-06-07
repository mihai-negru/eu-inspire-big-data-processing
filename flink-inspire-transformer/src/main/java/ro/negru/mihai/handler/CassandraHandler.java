package ro.negru.mihai.handler;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.mapping.Mapper;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.apache.flink.streaming.connectors.cassandra.ClusterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.kafka.PostTransformRequest;
import ro.negru.mihai.oslevel.OSEnvHandler;
import ro.negru.mihai.configure.entity.status.Status;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CassandraHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHandler.class);

    public static class PendingCassandraMapFunction extends RichMapFunction<PostTransformRequest, TransformResult> {

        @Override
        public TransformResult map(PostTransformRequest req) {
            return new TransformResult(
                    req.getId(),
                    req.getSchema(),
                    req.getSchemaPath(),
                    ByteBuffer.wrap(req.getXml().getBytes(StandardCharsets.UTF_8)),
                    Status.PENDING.str(),
                    null
            );
        }
    }

    public static void sinker(final DataStream<TransformResult> stream, final OSEnvHandler osEnvHandler, final boolean override) {
        try {
            LOGGER.info("Trying to add a Cassandra sink from the flink application");
            CassandraSink.addSink(stream)
                    .setClusterBuilder(new ClusterBuilder() {
                        @Override
                        protected Cluster buildCluster(Cluster.Builder builder) {
                            return builder
                                    .addContactPoint(osEnvHandler.getEnv("cassandra"))
                                    .withPort(9042)
                                    .withCredentials(
                                            osEnvHandler.getEnv("cassandra_user"),
                                            osEnvHandler.getEnv("cassandra_pass")
                                    ).build();
                        }
                    })
                    .setMapperOptions(() -> new Mapper.Option[] { Mapper.Option.saveNullFields(override) })
                    .build();
            LOGGER.info("Successfully added a Cassandra sink");
        } catch (Exception e) {
            LOGGER.error("Could not add sink to Cassandra", e);
        }
    }

    public static CqlSessionBuilder getSessionBuilder(final OSEnvHandler osEnvHandler) {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(osEnvHandler.getEnv("cassandra"), 9042))
                .withAuthCredentials(
                        osEnvHandler.getEnv("cassandra_user"),
                        osEnvHandler.getEnv("cassandra_pass")
                )
                .withKeyspace("inspire")
                .withLocalDatacenter(osEnvHandler.getEnv("cassandra_dc"));
    }

    public static PreparedStatement lookUpStatement(final CqlSession session) {
        return session.prepare("SELECT * FROM transformed WHERE id=?");
    }

    public static TransformResult fromRow(final Row row) {
        final String id = row.getString("id");
        final String xmlSchema = row.getString("xml_schema");
        final String xmlPath = row.getString("xml_path");
        final ByteBuffer xmlBytes = row.getByteBuffer("xml");
        final String status = row.getString("status");
        final Map<String, String> failureDetails = row.getMap("failure_details", String.class, String.class);

        return new TransformResult(id, xmlSchema, xmlPath, xmlBytes, status, failureDetails);
    }
}
