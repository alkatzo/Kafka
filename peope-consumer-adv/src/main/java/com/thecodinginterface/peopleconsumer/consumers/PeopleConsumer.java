package com.thecodinginterface.peopleconsumer.consumers;

import com.thecodinginterface.peopleconsumer.entities.Person;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PeopleConsumer {
    static final Logger logger = LoggerFactory.getLogger(PeopleConsumer.class);

//    @KafkaListener(topics = "${topics.people-adv.name}", containerFactory = "personListenerFactory")
//    public void handlePersonEvent(Person person) {
//        logger.info("Processing " + person);
//    }

    @KafkaListener(topics = "${topics.people-adv.name}", containerFactory = "personListenerFactory")
    public void handlePersonEvent(ConsumerRecord<String, Person> record) {
        logger.info("Processing message from partition {} with offset {}",
                record.partition(),
                record.offset());
        Person person = record.value();
        // process person...
    }
}
