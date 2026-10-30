package com.PriceIQ.PriceIQ.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String saslJaasConfig;

    @Value("${KAFKA_CA_CERT_PATH:src/main/resources/aiven-ca.pem}")
    private String caCertificatePath;

    // Common configuration for producer and consumer
    private Map<String, Object> commonKafkaConfig() throws Exception {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);

        // SASL authentication
        config.put("security.protocol", securityProtocol);
        config.put("sasl.mechanism", saslMechanism);
        config.put("sasl.jaas.config", saslJaasConfig);

        // PEM CA certificate verification
        config.put("ssl.truststore.type", "PEM");
config.put("ssl.truststore.certificates", getCaCertificate());

        return config;
    }

    // Topic configuration
    @Bean
    public NewTopic userEventsTopic() {

        return TopicBuilder.name("user-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Producer configuration
    @Bean
    public ProducerFactory<String, String> producerFactory() throws Exception{

        Map<String, Object> config = commonKafkaConfig();

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);

        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() throws Exception {

        return new KafkaTemplate<>(producerFactory());
    }

    // Consumer configuration
    @Bean
    public ConsumerFactory<String, String> consumerFactory() throws Exception {

        Map<String, Object> config = commonKafkaConfig();

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() throws Exception{

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    // Shared ObjectMapper
    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        mapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }

        private String getCaCertificate() throws Exception {
        try (var input = new org.springframework.core.io.ClassPathResource("aiven-ca.pem")
                .getInputStream()) {
            return new String(input.readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}