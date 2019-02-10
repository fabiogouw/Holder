package com.fabiogouw.holder;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HolderKafkaConsumerFactory<K, V> extends DefaultKafkaConsumerFactory<K, V> {

    private static final Logger _log = LoggerFactory.getLogger(HolderKafkaConsumerFactory.class);
    private final int _startPartition;
    private final int _endPartition;

    public HolderKafkaConsumerFactory(Map<String, Object> configs, int startPartition, int endPartition) {
        super(configs);
        _startPartition = startPartition;
        _endPartition = endPartition;
    }

    @Override
    public Consumer<K,V> createConsumer(@Nullable String groupId,
                                           @Nullable String clientIdPrefix,
                                           @Nullable String clientIdSuffix) {
        Consumer<K,V> consumer = super.createConsumer(groupId, clientIdPrefix, clientIdSuffix);
        _log.info("Creating custom consumer for holder '{}'...", consumer);
        List<TopicPartition> topics = new ArrayList<>();
        for(int i = _startPartition; i <= _endPartition; i++) {
            topics.add(new TopicPartition(RequestReleaseKafkaConsumer.HOLDER_TOPIC, i));
            _log.info("Adding reply-to partition '{}'...", i);
        }
        consumer.assign(topics);
        consumer.seekToEnd(topics);
        return new HolderKafkaConsumer<>(consumer);
    }
}
