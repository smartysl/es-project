package com.manager;

import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class TransactionManager {
    public enum TransactionState {
        UNCOMMITTED("uncommitted"), COMMITTED("committed"), ROLLBACK("rollback");
        private String state;

        TransactionState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    public static class Transaction {
        private TransactionState transactionState = TransactionState.UNCOMMITTED;
        private final List<Operator> operatorList = new ArrayList<>();
        private final Map<AbstractMap.Entry<String, String>, Integer> versionMap = new HashMap<>();

        Transaction(String id) {
        }

        public TransactionState getTransactionState() {
            return transactionState;
        }

        public void setTransactionState(TransactionState transactionState) {
            this.transactionState = transactionState;
        }

        public List<Operator> getOperatorList() {
            return operatorList;
        }

        public Map<AbstractMap.Entry<String, String>, Integer> getVersionMap() {
            return versionMap;
        }
    }

    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger();

    public String addTransaction() {
        String transId = String.valueOf(counter.addAndGet(1));
        transactions.put(transId, new Transaction(transId));
        return transId;
    }

    public Transaction getTransaction(String transId) {
        return transactions.get(transId);
    }

    public TransactionManager.TransactionState getTransactionState(String transId) {
        TransactionManager.Transaction transaction = getTransaction(transId);
        if(transaction != null) {
            return transaction.getTransactionState();
        }
        return null;
    }

    public void addTransactionOperator(String transId, Operator.OperatorType operatorType, String indexName, String documentId, Map<String, Object> data) {
        if(transactions.containsKey(transId)) {
            transactions.get(transId).getOperatorList().add(new Operator(operatorType, indexName, documentId, data));
        }
    }

    public List<Operator> getTransactionOperators(String transId) {
        return transactions.get(transId).getOperatorList();
    }

    public void setTransactionDataVersion(String transId, String indexName, String documentId, Integer version) {
        if(transactions.containsKey(transId)) {
            transactions.get(transId).getVersionMap().put(new AbstractMap.SimpleEntry<>(indexName, documentId), version);;
        }
    }

    public Integer getTransactionDataVersion(String transId, String indexName, String documentId) {
        if(transactions.containsKey(transId)) {
            return transactions.get(transId)
                    .getVersionMap()
                    .getOrDefault(new AbstractMap.SimpleEntry<>(transId, indexName), 0);

        }
        return 0;
    }
}
