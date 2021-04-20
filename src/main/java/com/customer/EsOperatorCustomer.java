package com.customer;

import com.manager.EsOperator;
import com.manager.ResourceManager;
import com.utils.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.concurrent.ExecutorService;

@Configuration
public class EsOperatorCustomer {

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private ResourceManager resourceManager;

    @Value("${app.isolation}")
    private String isolation;

    private static final Logger log = LoggerFactory.getLogger(EsOperatorCustomer.class);

    @KafkaListener(topics = "es-write-topic", groupId = "group.test")
    public void listener(ConsumerRecord<?, ?> consumerRecord, Acknowledgment acknowledgment) {
        String msg = consumerRecord.value().toString();

        ExecutorService threadPool = ThreadPoolUtil.getThreadPool(isolation);
        EsOperator esOperator = JsonUtil.fromJson(msg, EsOperator.class);
        threadPool.submit(() -> {
            try {
                resourceManager.doWriteEsDocument(
                        esOperator.getOperatorType(),
                        esOperator.getIndexName(),
                        esOperator.getDocumentId(),
                        esOperator.getData()
                );
            } catch (Exception e) {
                log.warn("[RM模块写入Es失败] 回放消息以重试 e={}", ExceptionUtil.getStackTrace(e));
                mqProducer.sendBack(msg);
            } finally {
                acknowledgment.acknowledge();
            }
        });
    }
}