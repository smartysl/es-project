package com.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;

@Configuration
public class MqConsumer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = "es-write-topic", groupId = "group.test")
    public void listener(String msg, MessageHeaders headers) {
        System.out.println(headers.get("kafka_receivedMessageKey"));
        System.out.println(msg);
    }
}
