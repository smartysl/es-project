package com.utils;

import com.service.Exceptions.KafkaSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

@Configuration
public class MqProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${app.test}")
    private boolean test;

    public static final Logger log = LoggerFactory.getLogger(MqProducer.class);

    public void send(String data) {
        log.info("[MQ消息发送] 消息={}", data);
//        if(test) {
//            return;
//        }
        try {
            ListenableFuture<SendResult<String, Object>> sendResult = kafkaTemplate.send("es-write-topic", data);
            sendResult.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("[MQ消息发送失败] e={}", ExceptionUtil.getStackTrace(e));
            throw new KafkaSendException();
        }
    }

    public void sendBack(String data) {
        log.info("[MQ消息回放] 消息={}", data);
        kafkaTemplate.executeInTransaction((KafkaOperations.OperationsCallback) kafkaOperations -> {
            try {
                ListenableFuture<SendResult<String, Object>> sendResult = kafkaOperations.send("es-write-topic", data);
                sendResult.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("[MQ消息回放发送失败] e={}", ExceptionUtil.getStackTrace(e));
                throw new KafkaSendException();
            }
            return true;
        });
    }
}
