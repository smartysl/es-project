package com.service;

import com.manager.ResourceManager;
import com.manager.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

public class DataService {

    @Autowired
    protected TransactionManager transactionManager;
    @Autowired
    protected ResourceManager resourceManager;

    protected DataServiceResult checkTransactionState(String transId) {
        TransactionManager.TransactionState state = transactionManager.getTransactionState(transId);
        if(state == null) {
            return new DataServiceResult(1, "no such transaction");
        }
        if(state != TransactionManager.TransactionState.UNCOMMITTED) {
            return new DataServiceResult(2, "transaction is already been finished");
        }
        return new DataServiceResult(0, "");
    }
}
