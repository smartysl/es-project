package com.service;

import com.manager.Operator;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class IndexService extends DataService{

    public DataServiceResult indexDocument(String transId, String indexName, String documentId, Map<String, Object> data) {
        DataServiceResult result = new DataServiceResult(0, "");
        transactionManager.addTransactionOperator(transId, Operator.OperatorType.INDEX, indexName, documentId, data);
        return result;
    }
}
