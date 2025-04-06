package com.thecodinginterface.avropeopleconsumer.configs;

import com.thecodinginterface.avrodomainevents.Person;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;

@EnableKafka
@Configuration
public class PeopleConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${spring.kafka.consumer.properties.schema.registry.url}")
    String schemaRegUrl;

    @Value("${spring.kafka.consumer.properties.group.id}")
    String consumerGroup;

    // configs map
    public Map<String, Object> consumerConfigs() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, consumerGroup,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroDeserializer.class,
                "schema.registry.url", schemaRegUrl,
                "specific.avro.reader", "true"
        );
    }

    // ConsumerFactory bean
    @Bean
    public ConsumerFactory<String, Person> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    // ConcurrentKafkaListenerContainerFactory bean
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Person> personListenerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Person>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
