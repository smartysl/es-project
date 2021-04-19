package com.service;

import com.ResponseTemplate.TransactionResponseTemplate;
import com.manager.EsOperator;
import com.manager.TransactionManager;
import com.manager.strategy.PreHandler;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yushilin
 * @date 2021/4/16 2:41 下午
 */
@Service
public class BulkService {

    @Autowired
    private TransactionManager transactionManager;
    @Autowired
    private PreHandler preHandler;

    public TransactionResponseTemplate handleBulk(List<EsOperator> esOperators) {
        TransactionManager.Transaction transaction = transactionManager.buildTransaction(esOperators);
        try {
            preHandler.preHandle(transaction.getDbOperatorList(), transaction.getEsOperatorList());
        } catch (Exception e) {
            return new TransactionResponseTemplate(1, MessageFormatter.format("handler bulk fail, message={}", e.getMessage()).getMessage());
        }
        return new TransactionResponseTemplate(0, "");
    }

}
