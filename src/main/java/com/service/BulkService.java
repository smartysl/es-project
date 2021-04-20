package com.service;

import com.responseTemplate.TransactionResponseTemplate;
import com.manager.EsOperator;
import com.manager.TransactionManager;
import com.manager.strategy.Handler;
import com.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yushilin
 * @date 2021/4/16 2:41 下午
 */
@Service
public class BulkService {

    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    private Map<String, Handler> handlerMap;

    private static final Logger log = LoggerFactory.getLogger(BulkService.class);

    @Value("${app.strategy}")
    private String strategy;

    public TransactionResponseTemplate handleBulk(List<EsOperator> esOperators) {
        Handler handler = handlerMap.get(strategy);

        TransactionManager.Transaction transaction = transactionManager.buildTransaction(esOperators);
        try {
            handler.checkHealthy();
            handler.preHandle(transaction.getDbOperatorList(), transaction.getId());
            handler.checkHealthy();
            log.info("[Bulk并发控制&健康检查通过] 当前事务={}", transaction.getId());
            handler.doSendMessage(transaction.getEsOperatorList());
            log.info("[Bulk数据发送MQ消息成功] 当前事务{}", transaction.getId());
            handler.commit(transaction.getId());
        } catch (Exception e) {
            handler.rollback(transaction.getId());
            log.warn("[Bulk未成功执行] 已回滚事务, 当前事务={}, e={}", transaction.getId(), ExceptionUtil.getStackTrace(e));
            return new TransactionResponseTemplate(1, MessageFormatter.format("[Bulk未成功执行] message={}", e.getMessage()).getMessage());
        }
        return new TransactionResponseTemplate(0, "");
    }

}
