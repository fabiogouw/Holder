package com.fabiogouw.holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RequestReleaseKafkaConsumer {

    private final RequestHolder _requestHolder;
    public static final String HOLDER_TOPIC = "holder.release";

    private static final Logger _log = LoggerFactory.getLogger(RequestReleaseKafkaConsumer.class);

    public RequestReleaseKafkaConsumer(RequestHolder requestHolder) {
        _requestHolder = requestHolder;
    }

    @KafkaListener(topics = "xxx", containerFactory = "holderConsumerFactory")
    public void listenToParition(
            @Payload OperationResponse payload,
            @Header("hold-id") String holdId,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            Acknowledgment acknowledgment) {
        _log.info("Receiving a release from partition '{}'", partition);
        _requestHolder.release(UUID.fromString(holdId), payload);
        acknowledgment.acknowledge();
    }
}
