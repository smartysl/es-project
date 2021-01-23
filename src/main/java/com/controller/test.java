package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class test {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @GetMapping("/test")
    @Transactional
    public void kafkaTest(String fail) throws InterruptedException {
        kafkaTemplate.send("es-write-topic", "prepare fail");
        if(Objects.equals(fail, "fail")) {
            throw new RuntimeException();
        }
        kafkaTemplate.send("es-write-topic", "fail");
    }

    @KafkaListener(topics = "es-write-topic", groupId = "group.test")
    public void listener(String msg) {
        System.out.println(msg);
    }
}
