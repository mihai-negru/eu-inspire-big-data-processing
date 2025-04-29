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

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.regex.Pattern;

public class DataStreamJob {

	public static void main(String[] args) throws Exception {

		KafkaTopicCreator.createTopicIfNotExist("broker:9092", "test-topic");


		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		final KafkaSource<String> source = KafkaSource.<String>builder()
				.setBootstrapServers("broker:9092")
				.setTopicPattern(Pattern.compile("test-topic.*"))
				.setGroupId("group-id")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new SimpleStringSchema())
				.setProperty("partition.discovery.interval.ms", "10000")
				.setProperty("allow.auto.create.topics", "true")
				.build();

		DataStream<String> data = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

		DataStream<String> sinker = data.flatMap((String str, Collector<Integer> out) -> {
			// Split by one or more whitespace characters
			for (String token : str.split("\\s+")) {
				try {
					out.collect(Integer.parseInt(token));
				} catch (NumberFormatException e) {
					// Optionally log or handle tokens that cannot be parsed
				}
			}
		}).returns(Types.INT).keyBy(num -> 0).reduce(Integer::sum).map(Object::toString);

		KafkaSink<String> sink = KafkaSink.<String>builder()
				.setBootstrapServers("broker:9092")
				.setRecordSerializer(KafkaRecordSerializationSchema.builder()
						.setTopic("test-response")
						.setValueSerializationSchema(new SimpleStringSchema())
						.build()
				)
				.build();

		sinker.sinkTo(sink);

		env.execute("Simple Kafka Source");
	}
}
