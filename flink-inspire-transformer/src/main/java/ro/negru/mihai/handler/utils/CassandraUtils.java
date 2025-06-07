package ro.negru.mihai.handler.utils;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.mapping.Mapper;
import com.datastax.oss.driver.api.core.CqlSession;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.apache.flink.streaming.connectors.cassandra.ClusterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.configure.OSEnvHandler;

import java.net.InetSocketAddress;

public class CassandraUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraUtils.class);

    public static <OUT> void sinker(final DataStream<OUT> stream, final OSEnvHandler osEnvHandler, final boolean override) {
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

    public static CqlSession getSession(final OSEnvHandler osEnvHandler) {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(osEnvHandler.getEnv("cassandra"), 9042))
                .withAuthCredentials(
                        osEnvHandler.getEnv("cassandra_user"),
                        osEnvHandler.getEnv("cassandra_pass")
                )
                .withKeyspace("inspire")
                .withLocalDatacenter(osEnvHandler.getEnv("cassandra_dc"))
                .build();
    }
}
