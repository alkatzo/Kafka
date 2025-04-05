package com.thecodinginterface.peopleserviceadv.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${topics.people-adv.name}")
    String topicName;

    @Value("${topics.people-adv.replicas}")
    int topicReplicas;

    @Value("${topics.people-adv.partitions}")
    int topicPartitions;

    @Bean
    public NewTopic peopleAdvTopic() {
        return TopicBuilder.name(topicName)
                .partitions(topicPartitions)
                .replicas(topicReplicas)
                .build();
    }
}
