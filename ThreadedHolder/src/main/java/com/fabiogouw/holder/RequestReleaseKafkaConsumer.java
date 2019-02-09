package com.fabiogouw.holder;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RequestReleaseKafkaConsumer {

    private final RequestHolder _requestHolder;
    public static final String HOLDER_TOPIC = "holder.release";

    public RequestReleaseKafkaConsumer(RequestHolder requestHolder) {
        _requestHolder = requestHolder;
    }

    @KafkaListener(topics = HOLDER_TOPIC, groupId = "foo", containerFactory = "holderConsumerFactory")
    public void listenToParition(
            @Payload OperationResponse payload,
            @Header("hold-id") String holdId,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        _requestHolder.release(UUID.fromString(holdId), payload);
    }
}
