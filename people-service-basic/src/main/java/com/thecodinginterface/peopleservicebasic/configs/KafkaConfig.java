package com.thecodinginterface.peopleservicebasic.configs;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaConfig {

    static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${topics.people-basic.name}")
    String topicName;

    @Value("${topics.people-basic.replicas}")
    int topicReplicas;

    @Value("${topics.people-basic.partitions}")
    int topicPartitions;

    @Bean
    public NewTopic peopleBasicTopic() {
        return TopicBuilder.name(topicName)
                .partitions(topicPartitions)
                .replicas(topicReplicas)
                .build();
    }

    @Bean
    public NewTopic peopleBasicShortTopic() {
        return TopicBuilder.name(topicName + ".short")
                .partitions(topicPartitions)
                .replicas(topicReplicas)
                .config(TopicConfig.RETENTION_MS_CONFIG, "360000")
                .build();
    }

    @PostConstruct // happens after all the beans have been created by the framework
    public void changePeopleBasicTopicRetention() throws ExecutionException, InterruptedException {
        // create a connection with configs to bootstrap server
        Map<String, Object> connectionConfig =
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try(var admin  = AdminClient.create(connectionConfig)) {
            // create a config resource to fetch topic configs
            var configResource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);

            // filter down to specific topic (retention.ms)
            ConfigEntry topicConfigEntry = admin.describeConfigs(Collections.singleton(configResource))
                    .all().get().entrySet().stream()
                    .findFirst().get().getValue().entries().stream()
                    .filter(configEntry -> configEntry.name().equals(TopicConfig.RETENTION_MS_CONFIG))
                    .findFirst().get();

            // check if the config is 360,000 if not update to 1 hour
            if(!topicConfigEntry.value().equals("7200000")) {
                // create a config entry and filter config opt to specify what config to change (retention.ms)
                var alterConfigEntry = new ConfigEntry(TopicConfig.RETENTION_MS_CONFIG, "720000");
                var alterOp = new AlterConfigOp(alterConfigEntry, AlterConfigOp.OpType.SET);


                // use admin to execute the alter operation
                Map<ConfigResource, Collection<AlterConfigOp>> alterConfigs = Map.of(
                        configResource, Collections.singletonList(alterOp));
                admin.incrementalAlterConfigs(alterConfigs).all().get();
                logger.info("Updated topic "  + topicName + " with " + TopicConfig.RETENTION_MS_CONFIG + " : 720000");
            }

        }
    }
}
