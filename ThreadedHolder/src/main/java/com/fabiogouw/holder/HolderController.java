package com.fabiogouw.holder;

import java.util.UUID;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

@Controller
@RequestMapping("/proxy")
public class HolderController {

    private final long _timeout = 5000;

    private static final Logger _log = LoggerFactory.getLogger(HolderController.class);

    private final RequestHolder _requestHolder;
    private final KafkaTemplate<String, OperationRequest> _businessKafkaProducer;

    public HolderController(RequestHolder requestHolder, KafkaTemplate<String, OperationRequest> businessKafkaProducer) {
        _requestHolder = requestHolder;
        _businessKafkaProducer = businessKafkaProducer;
    }

    @RequestMapping(value="call", method = RequestMethod.POST)
    public @ResponseBody DeferredResult<OperationResponse> call(@RequestBody OperationRequest operation) {
        DeferredResult<OperationResponse> result = new DeferredResult<>(_timeout);
        UUID holdId = _requestHolder.add(result);
        callDoSomething(operation, holdId);
        return result;
    }

    private void callDoSomething(OperationRequest operation, UUID holdId) {
        Message<OperationRequest> message = MessageBuilder
                .withPayload(operation)
                .setHeader(KafkaHeaders.TOPIC, BusinessProcessorKafkaConsumer.BUSINESS_TOPIC)
                .setHeader(KafkaHeaders.MESSAGE_KEY, operation.getValue())
                .setHeader("hold-id", holdId.toString())
                .setHeader("reply-to-partition", _requestHolder.getReplyToPartition())
                .build();
        _log.info("sending message='{}' to topic='{}'", operation, BusinessProcessorKafkaConsumer.BUSINESS_TOPIC);
        _businessKafkaProducer.send(message);
    }
}