package com.sky.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.constant.MessageConstant;
import com.sky.dto.LikeDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.time.Duration;
import java.util.*;


@Configuration
@EnableKafkaStreams
@Slf4j
public class KafkaStreamConfig {

    public class JsonListSerializer implements Serializer<List<String>> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public byte[] serialize(String topic, List<String> data) {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException("Error serializing JSON message", e);
            }
        }
    }

    public class JsonListDeserializer implements Deserializer<List<String>> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public List<String> deserialize(String topic, byte[] data) {
            try {
                return objectMapper.readValue(data, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Error deserializing JSON message", e);
            }
        }
    }

    @Bean
    public KStream<String, String> kafkaStreams(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream(MessageConstant.KAFKA_TOPIC_LIKE_SAVE);
        log.info("启动");
        stream.map((key, value) -> {
                    LikeDTO likeDTO = JSON.parseObject(value, LikeDTO.class);
                    log.info("Stream流接收到的消息: {}", likeDTO);
                    return new KeyValue<>(likeDTO.getUserId().toString(), likeDTO.getVideoId().toString());
                })
                .groupBy((key, value) -> key)
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                .aggregate(
                        ArrayList::new, // 初始化一个空的ArrayList作为每个视频ID的用户ID列表
                        (key, value, aggregate) -> {
                            // 聚合逻辑，如果列表中尚未包含当前用户ID，则将其添加到列表中
                            aggregate.add(value);
                            return aggregate; // 返回更新后的用户ID列表
                        },
                        Materialized.<String, List<String>, WindowStore<Bytes, byte[]>>as("likes-list")
                                .withKeySerde(Serdes.String()) // 使用String Serde序列化key
                                .withValueSerde(Serdes.serdeFrom(new JsonListSerializer(), new JsonListDeserializer())) // 使用自定义的JSON Serde序列化HashSet // 使用自定义的JSON Serde序列化value
                )
                .toStream()
                .map((key, value) -> {
                    String originalKey = key.key();
                    String firstPartOfKey = originalKey.split("@")[0];
                    log.info("key: {}, value: {}", firstPartOfKey, value);// 假设key用"@"分隔，取前半部分
                    return new KeyValue<>(firstPartOfKey, value);
                });
//                .aggregate(
//                        () -> 0L, // 初始化为0
//                        (key, value, aggregate) -> aggregate + 1, // 每次接收到新值，累加1
//                        Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("123")
//                                .withKeySerde(Serdes.String()) // 使用String Serde序列化key
//                                .withValueSerde(Serdes.Long()) // 使用Long Serde序列化value
//                )
//                .toStream()
//                .foreach((key, value) -> {
//                    log.info("key: {}, value: {}", key, value); // 使用foreach直接打印每个键值对
//                });
        return stream;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration defaultKafkaStreamsBuilder() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-eexample");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        return new KafkaStreamsConfiguration(props);
    }
}
