package com.fabiogouw.holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BusinessProcessorKafkaConsumer {

    private static final Logger _log = LoggerFactory.getLogger(BusinessProcessorKafkaConsumer.class);
    public static final String BUSINESS_TOPIC = "holder.business";

    private final KafkaTemplate<String, OperationResponse> _businessKafkaProducer;

    public BusinessProcessorKafkaConsumer(KafkaTemplate<String, OperationResponse> businessKafkaProducer) {
        _businessKafkaProducer = businessKafkaProducer;
    }

    @KafkaListener(topics = BUSINESS_TOPIC, groupId = "foo", containerFactory = "businessConsumerFactory")
    public void listenToParition(
            @Payload OperationRequest payload,
            @Header("hold-id") String holdId,
            @Header("reply-to-partition") int replyToPartition) {
        _log.info("simulating some processing for message='{}'...", payload);
        OperationResponse response = new OperationResponse(payload.getValue(), UUID.randomUUID().toString());
        Message<OperationResponse> message = MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, RequestReleaseKafkaConsumer.HOLDER_TOPIC)
                .setHeader(KafkaHeaders.PARTITION_ID, replyToPartition)
                .setHeader("hold-id", holdId)
                .build();
        _log.info("sending message='{}' to topic='{}'", response, RequestReleaseKafkaConsumer.HOLDER_TOPIC);
        _businessKafkaProducer.send(message);
    }

}
