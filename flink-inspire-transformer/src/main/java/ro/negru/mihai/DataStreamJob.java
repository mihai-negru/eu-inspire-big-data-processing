/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.negru.mihai;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.negru.mihai.schema.TopicAwareRecord;
import ro.negru.mihai.xml.xmladapter.XmlUtils;

import java.util.Map;

public class DataStreamJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamJob.class);
	private static final String bootstrapServers;

	static {
		String envBootstrapServers = "localhost:9092";
		try {
			Map<String, String> envs = System.getenv();
			if (envs != null && envs.containsKey("KAFKA_BOOTSTRAP_SERVERS"))
				envBootstrapServers = envs.get("KAFKA_BOOTSTRAP_SERVERS");
		} catch (Exception e) {
			LOGGER.error("Failed to load environment variables to extract the bootstrap server", e);
		}

		bootstrapServers = envBootstrapServers;
	}

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.enableCheckpointing(10000);
		env.getCheckpointConfig().setCheckpointingConsistencyMode(CheckpointingMode.EXACTLY_ONCE);

//		KafkaHandler.createTopicIfNotExist(bootstrapServers, XmlUtils.getAvailableFeatures());

		final DataStream<TopicAwareRecord> rawDataStream = DataStreamHandler.createDataStream(
				env,
				KafkaHandler.createKafkaSource(bootstrapServers, "raw", "raw-data"),
				"kafka-raw-data");

		final DataStream<String> transformedDataStream = rawDataStream.flatMap(new DataStreamHandler.InspireFlatMapTransform()).returns(Types.STRING);
//
//		DataStream<String> sinker = data.flatMap((String str, Collector<Integer> out) -> {
//			// Split by one or more whitespace characters
//			for (String token : str.split("\\s+")) {
//				try {
//					out.collect(Integer.parseInt(token));
//				} catch (NumberFormatException e) {
//					// Optionally log or handle tokens that cannot be parsed
//				}
//			}
//		}).returns(Types.INT).keyBy(num -> 0).reduce(Integer::sum).map(Object::toString);
//
//		KafkaSink<String> sink = KafkaSink.<String>builder()
//				.setBootstrapServers("broker:9092")
//				.setRecordSerializer(KafkaRecordSerializationSchema.builder()
//						.setTopic("test-response")
//						.setValueSerializationSchema(new SimpleStringSchema())
//						.build()
//				)
//				.build();
//
//		sinker.sinkTo(sink);


		env.execute("Simple Kafka Source");
	}
}
