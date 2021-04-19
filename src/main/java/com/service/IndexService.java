package com.service;

import com.manager.EsOperator;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class IndexService extends DataService{

    public DataServiceResult indexDocument(String transId, String indexName, String documentId, Map<String, Object> data) {
        DataServiceResult result = checkTransactionState(transId);
        if(result.getErrorNo() != 0) {
            return result;
        }
        transactionManager.addTransactionOperator(transId, EsOperator.OperatorType.INDEX, indexName, documentId, data);
        return result;
    }
}
