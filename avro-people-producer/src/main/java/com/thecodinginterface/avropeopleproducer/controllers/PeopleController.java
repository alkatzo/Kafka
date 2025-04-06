package com.thecodinginterface.avropeopleproducer.controllers;

import com.github.javafaker.Faker;
import com.thecodinginterface.avrodomainevents.Person;
import com.thecodinginterface.avropeopleproducer.commands.CreatePeopleCommand;
import com.thecodinginterface.avropeopleproducer.models.PersonDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PeopleController {
    static final Logger logger = LoggerFactory.getLogger(PeopleController.class);

    @Value("${topics.people-avro.name}")
    private String personAvroTopic;

    private final KafkaTemplate<String, Person> kafkaTemplate;

    public PeopleController(KafkaTemplate<String, Person> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/people")
    @ResponseStatus(HttpStatus.CREATED)
    public List<PersonDto> create(@RequestBody CreatePeopleCommand cmd) {
        logger.info("Creating people cmd " + cmd);
        List<PersonDto> people = new ArrayList<>();
        var faker = new Faker();

        for (var i = 0; i < cmd.getCount(); i++) {
            var person = new Person();
            person.setName(faker.name().fullName());
            person.setTitle(faker.job().title());
            people.add(new PersonDto(person.getName(), person.getTitle()));

            var future = kafkaTemplate.send(
                    personAvroTopic,
                    person.getName().toLowerCase().replaceAll("\\s+", "-"),
                    person
            );

            future.addCallback(
                    result -> {
                        logger.info("Published person=" + person
                                + ",\n  partition=" + result.getRecordMetadata().partition()
                                + ",\n offset=" + result.getRecordMetadata().offset());
                    },
                    ex -> {
                        logger.error("Failed to publish " + person, ex);
                    }
            );
        }
        kafkaTemplate.flush();

        return people;
    }

}
