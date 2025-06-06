package ro.negru.mihai.handler;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.mapping.Mapper;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.apache.flink.streaming.connectors.cassandra.ClusterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.kafka.ValidatorTestRequest;
import ro.negru.mihai.oslevel.OSEnvHandler;
import ro.negru.mihai.configure.entity.status.Status;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class CassandraHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHandler.class);

    public static class PendingCassandraMapFunction extends RichMapFunction<ValidatorTestRequest, TransformResult> {

        @Override
        public TransformResult map(ValidatorTestRequest req) {
            return new TransformResult(
                    req.getId(),
                    req.getSchemaPath(),
                    ByteBuffer.wrap(req.getXml().getBytes(StandardCharsets.UTF_8)),
                    Status.PENDING.str(),
                    null
            );
        }
    }

    public static void sinker(final DataStream<TransformResult> stream, final boolean override) {
        try {
            LOGGER.info("Trying to add a Cassandra sink from the flink application");
            CassandraSink.addSink(stream)
                    .setClusterBuilder(new ClusterBuilder() {
                        @Override
                        protected Cluster buildCluster(Cluster.Builder builder) {
                            return builder
                                    .addContactPoint(OSEnvHandler.INSTANCE.getEnv("cassandra"))
                                    .withPort(9042)
                                    .withCredentials(
                                            OSEnvHandler.INSTANCE.getEnv("cassandra_user"),
                                            OSEnvHandler.INSTANCE.getEnv("cassandra_pass")
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
}
