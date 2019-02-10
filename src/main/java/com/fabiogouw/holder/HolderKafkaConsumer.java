package com.fabiogouw.holder;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class HolderKafkaConsumer<K, V> implements Consumer<K, V> {

    private Consumer<K, V> _baseConsumer;
    private static final Logger _log = LoggerFactory.getLogger(HolderKafkaConsumer.class);

    public HolderKafkaConsumer(Consumer<K, V> baseConsumer) {
        _baseConsumer = baseConsumer;
    }


    @Override
    public Set<TopicPartition> assignment() {
        return _baseConsumer.assignment();
    }

    @Override
    public Set<String> subscription() {
        return _baseConsumer.subscription();
    }

    @Override
    public void subscribe(Collection<String> collection) {
        _log.warn("Ignoring subscribe");
    }

    @Override
    public void subscribe(Collection<String> collection, ConsumerRebalanceListener consumerRebalanceListener) {
        _log.warn("Ignoring subscribe");
    }

    @Override
    public void assign(Collection<TopicPartition> collection) {
        _baseConsumer.assign(collection);
    }

    @Override
    public void subscribe(Pattern pattern, ConsumerRebalanceListener consumerRebalanceListener) {
        _log.warn("Ignoring subscribe");
    }

    @Override
    public void subscribe(Pattern pattern) {
        _log.warn("Ignoring subscribe");
    }

    @Override
    public void unsubscribe() {
        _baseConsumer.unsubscribe();
    }

    @Override
    public ConsumerRecords<K, V> poll(long l) {
        return _baseConsumer.poll(l);
    }

    @Override
    public ConsumerRecords<K, V> poll(Duration duration) {
        return _baseConsumer.poll(duration);
    }

    @Override
    public void commitSync() {
        _baseConsumer.commitSync();
    }

    @Override
    public void commitSync(Duration duration) {
        _baseConsumer.commitSync(duration);
    }

    @Override
    public void commitSync(Map<TopicPartition, OffsetAndMetadata> map) {
        _baseConsumer.commitSync(map);
    }

    @Override
    public void commitSync(Map<TopicPartition, OffsetAndMetadata> map, Duration duration) {
        _baseConsumer.commitSync(map, duration);
    }

    @Override
    public void commitAsync() {
        _baseConsumer.commitAsync();
    }

    @Override
    public void commitAsync(OffsetCommitCallback offsetCommitCallback) {
        _baseConsumer.commitAsync(offsetCommitCallback);
    }

    @Override
    public void commitAsync(Map<TopicPartition, OffsetAndMetadata> map, OffsetCommitCallback offsetCommitCallback) {
        _baseConsumer.commitAsync(map, offsetCommitCallback);
    }

    @Override
    public void seek(TopicPartition topicPartition, long l) {
        _baseConsumer.seek(topicPartition, l);
    }

    @Override
    public void seekToBeginning(Collection<TopicPartition> collection) {
        _baseConsumer.seekToBeginning(collection);
    }

    @Override
    public void seekToEnd(Collection<TopicPartition> collection) {
        _baseConsumer.seekToEnd(collection);
    }

    @Override
    public long position(TopicPartition topicPartition) {
        return _baseConsumer.position(topicPartition);
    }

    @Override
    public long position(TopicPartition topicPartition, Duration duration) {
        return _baseConsumer.position(topicPartition, duration);
    }

    @Override
    public OffsetAndMetadata committed(TopicPartition topicPartition) {
        return _baseConsumer.committed(topicPartition);
    }

    @Override
    public OffsetAndMetadata committed(TopicPartition topicPartition, Duration duration) {
        return _baseConsumer.committed(topicPartition, duration);
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return _baseConsumer.metrics();
    }

    @Override
    public List<PartitionInfo> partitionsFor(String s) {
        return _baseConsumer.partitionsFor(s);
    }

    @Override
    public List<PartitionInfo> partitionsFor(String s, Duration duration) {
        return _baseConsumer.partitionsFor(s, duration);
    }

    @Override
    public Map<String, List<PartitionInfo>> listTopics() {
        return _baseConsumer.listTopics();
    }

    @Override
    public Map<String, List<PartitionInfo>> listTopics(Duration duration) {
        return _baseConsumer.listTopics(duration);
    }

    @Override
    public Set<TopicPartition> paused() {
        return _baseConsumer.paused();
    }

    @Override
    public void pause(Collection<TopicPartition> collection) {
        _baseConsumer.pause(collection);
    }

    @Override
    public void resume(Collection<TopicPartition> collection) {
        _baseConsumer.resume(collection);
    }

    @Override
    public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> map) {
        return _baseConsumer.offsetsForTimes(map);
    }

    @Override
    public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> map, Duration duration) {
        return _baseConsumer.offsetsForTimes(map, duration);
    }

    @Override
    public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> collection) {
        return _baseConsumer.beginningOffsets(collection);
    }

    @Override
    public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> collection, Duration duration) {
        return _baseConsumer.beginningOffsets(collection, duration);
    }

    @Override
    public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> collection) {
        return _baseConsumer.endOffsets(collection);
    }

    @Override
    public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> collection, Duration duration) {
        return _baseConsumer.endOffsets(collection, duration);
    }

    @Override
    public void close() {
        _baseConsumer.close();
    }

    @Override
    public void close(long l, TimeUnit timeUnit) {
        _baseConsumer.close(l, timeUnit);
    }

    @Override
    public void close(Duration duration) {
        _baseConsumer.close(duration);
    }

    @Override
    public void wakeup() {
        _baseConsumer.wakeup();
    }
}
