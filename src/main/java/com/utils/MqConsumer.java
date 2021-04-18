//package com.utils;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//
//@Configuration
//public class MqConsumer {
//
//    @Autowired
//    private KafkaTemplate kafkaTemplate;
//
//    @KafkaListener(topics = "es-write-topic", groupId = "group.test")
//    public void listener(@Payload String msg, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
//        System.out.println(key);
//        System.out.println(msg);
//    }
//}
