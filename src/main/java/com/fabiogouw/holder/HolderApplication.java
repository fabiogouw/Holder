package com.fabiogouw.holder;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@SpringBootApplication
public class HolderApplication {

	@Value(value = "${kafka.bootstrapAddress}")
	private String _bootstrapAddress;

	@Value(value = "${holder.kafka.start-partition}")
	private int _startPartition;

	@Value(value = "${holder.kafka.end-partition}")
	private int _endPartition;

	private static final Logger _log = LoggerFactory.getLogger(HolderApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HolderApplication.class, args);
	}

	@Bean
	public RequestHolder getRequestHolder() {
		return new RequestHolder();
	}

	private Map<String, Object> producerProps() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, _bootstrapAddress);
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return props;
	}

	@Bean
	public KafkaTemplate<String, OperationRequest> businessKafkaTemplate() {
		Map<String, Object> senderProps = producerProps();
		ProducerFactory<String, OperationRequest> pf = new DefaultKafkaProducerFactory<>(senderProps);
		KafkaTemplate<String, OperationRequest> template = new KafkaTemplate<>(pf);
		return template;
	}

	@Bean
	public KafkaTemplate<String, OperationResponse> holderKafkaTemplate() {
		Map<String, Object> senderProps = producerProps();
		ProducerFactory<String, OperationResponse> pf = new DefaultKafkaProducerFactory<>(senderProps);
		KafkaTemplate<String, OperationResponse> template = new KafkaTemplate<>(pf);
		return template;

	}

	private Map<String, Object> consumerProps() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, _bootstrapAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "example");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		//props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		//props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return props;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, OperationRequest> businessConsumerFactory() {
		Map<String, Object> props = consumerProps();
		ConcurrentKafkaListenerContainerFactory<String, OperationRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		return factory;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, OperationResponse> holderConsumerFactory() {
		Map<String, Object> props = consumerProps();
		ConcurrentKafkaListenerContainerFactory<String, OperationResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(new HolderKafkaConsumerFactory<>(props, _startPartition, _endPartition));
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		return factory;
	}
}
