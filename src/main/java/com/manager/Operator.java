package com.manager;

import java.util.Map;

public class Operator {

    public enum OperatorType {
        INDEX, CREATE, UPDATE, DELETE;
    }

    private OperatorType operatorType;
    private String indexName;
    private String documentId;
    private Map<String, Object> data;

    public Operator(OperatorType operatorType, String indexName, String documentId, Map<String, Object> data) {
        this.operatorType = operatorType;
        this.indexName = indexName;
        this.documentId = documentId;
        this.data = data;
    }

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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
