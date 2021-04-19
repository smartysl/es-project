package com.manager;

import java.util.Map;

/**
 * @author yushilin
 * @date 2021/4/16 2:58 下午
 */
public class DBOperator {
    public enum OperatorType {
        CREATE, UPDATE, DELETE;
    }

    private DBOperator.OperatorType operatorType;
    private String indexName;
    private String documentId;

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public DBOperator(DBOperator.OperatorType operatorType, String indexName, String documentId) {
        this.operatorType = operatorType;
        this.indexName = indexName;
        this.documentId = documentId;
    }
}
