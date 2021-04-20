package com.manager;

import java.util.Map;

public class EsOperator {

    public enum OperatorType {
        INDEX("index"),
        CREATE("create"),
        UPDATE("update"),
        DELETE("delete");

        private final String formatted;

        OperatorType(String formatted) {
            this.formatted = formatted;
        }

        @Override
        public String toString() {
            return this.formatted;
        }
    }

    private OperatorType operatorType;
    private String indexName;
    private String documentId;
    private Map<String, Object> data;

    public EsOperator(OperatorType operatorType, String indexName, String documentId, Map<String, Object> data) {
        this.operatorType = operatorType;
        this.indexName = indexName;
        this.documentId = documentId;
        this.data = data;
    }

    public EsOperator() {}

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
