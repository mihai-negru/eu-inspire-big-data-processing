package ro.negru.mihai;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.schema.TopicAwareRecord;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

public class DataStreamHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamHandler.class);

    private final static XmlUtils.InspireXmlMapper xmlMapper;

    static {
        xmlMapper = XmlUtils.getModuleWithDefaults().getXmlMapper();
    }

    public static DataStream<TopicAwareRecord> createDataStream(StreamExecutionEnvironment env, KafkaSource<TopicAwareRecord> ingest, String sourceName) {
        return env.fromSource(ingest, WatermarkStrategy.noWatermarks(), sourceName);
    }

    public static class InspireFlatMapTransform implements FlatMapFunction<TopicAwareRecord, String> {
        @Override
        public void flatMap(TopicAwareRecord record, Collector<String> collector) throws Exception {
            Object transformed = null;
            try (InputStream input = new ByteArrayInputStream(record.text().getBytes())) {
                LOGGER.info("InspireFlatMapTransform record to transform: {}", record.topic());
                transformed = xmlMapper.readFeature(input, record.topic());
            } catch (Exception e) {
                LOGGER.error("InspireFlatMapTransform error", e);
            }

            if (transformed != null) {
                LOGGER.info("InspireFlatMapTransform transformed successfully, trying to map to output");
                final StringWriter writer = new StringWriter();
                xmlMapper.writeValue(writer, transformed);

                final String inspireCompliant = writer.toString();
                collector.collect(inspireCompliant);

                LOGGER.info("Inspire compliant generated");
            } else {
                LOGGER.error("No transformed inspire record, because something went wrong");
            }
        }
    }
}
