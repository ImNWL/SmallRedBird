//package com.sky.config;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sky.constant.MessageConstant;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.common.serialization.Deserializer;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.common.serialization.Serializer;
//import org.apache.kafka.common.utils.Bytes;
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.KStream;
//import org.apache.kafka.streams.kstream.Materialized;
//import org.apache.kafka.streams.state.KeyValueStore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//
//@Configuration
//@Slf4j
//public class KafkaStreamListener {
//
//
//
////    @Bean
////    public StreamsBuilder streamsBuilder() {
////        StreamsBuilder builder = new StreamsBuilder();
////
////        KStream<String, String> stream = builder.stream(MessageConstant.KAFKA_TOPIC_LIKE_SAVE);
////
////        stream.map((key, value) -> {
////                    return new KeyValue<>(key, value); // 转换key和value的位置
////                })
////                .groupBy((key, value) -> key)
////                .aggregate(
////                        ArrayList::new, // 初始化一个空的ArrayList作为每个视频ID的用户ID列表
////                        (key, value, aggregate) -> {
////                            // 聚合逻辑，如果列表中尚未包含当前用户ID，则将其添加到列表中
////                            aggregate.add(value);
////                            return aggregate; // 返回更新后的用户ID列表
////                        },
////                        Materialized.<String, List<String>, KeyValueStore<Bytes, byte[]>>as("likes-list-store")
////                                .withKeySerde(Serdes.String()) // 使用String Serde序列化key
////                                .withValueSerde(Serdes.serdeFrom(new JsonListSerializer(), new JsonListDeserializer())) // 使用自定义的JSON Serde序列化HashSet // 使用自定义的JSON Serde序列化value
////                )
////                .toStream()
////                .map((key, value) -> {
////                    log.info("key: {}, value: {}", key, value);
////                    return null;
////                });
////
////        return builder;
////    }
////
////    @Bean
////    public Properties streamsConfig() {
////        Properties props = new Properties();
////        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-example");
////        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
////        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
////        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
////
////        return props;
////    }
//}
