package com.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class MqProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void send(String key, String data) {
        kafkaTemplate.send("es-write-topic", key, data);
    }
}
