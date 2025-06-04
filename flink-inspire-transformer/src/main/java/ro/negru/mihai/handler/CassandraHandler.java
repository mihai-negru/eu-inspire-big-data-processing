package ro.negru.mihai.handler;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.entity.cassandra.TransformResult;
import ro.negru.mihai.entity.kafka.ValidatorTestRequest;
import ro.negru.mihai.oslevel.OSEnvHandler;
import ro.negru.mihai.status.Status;

import java.nio.charset.StandardCharsets;

public class CassandraHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHandler.class);

    public static class PendingCassandraMapFunction implements MapFunction<ValidatorTestRequest, TransformResult> {

        @Override
        public TransformResult map(ValidatorTestRequest req) {
            return new TransformResult(req.getId(), req.getXml().getBytes(StandardCharsets.UTF_8), Status.PENDING.str(), null);
        }
    }

    public static void sinkerInsert(DataStream<TransformResult> stream) {
        try {
            CassandraSink.addSink(stream)
                    .setHost(OSEnvHandler.INSTANCE.getEnv("cassandra"))
                    .setQuery("INSERT INTO inspire.transformed (id, xml, status) VALUES (?, ?, ?);")
                    .build();
        } catch (Exception e) {
            LOGGER.error("Could not add insert sink to Cassandra", e);
        }
    }

    public static void sinkerUpdate(DataStream<TransformResult> stream) {
        try {
            CassandraSink.addSink(stream)
                    .setHost(OSEnvHandler.INSTANCE.getEnv("cassandra"))
                    .setQuery("UPDATE inspire.transformed SET status = ?, failure_details = ? WHERE id = ?;")
                    .build();
        } catch (Exception e) {
            LOGGER.error("Could not add update sink to Cassandra", e);
        }
    }
}
