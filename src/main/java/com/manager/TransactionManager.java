package com.manager;

import com.dao.mapper.EsMappingMapper;
import com.dao.mapper.GlobalTransactionMapper;
import com.manager.strategy.PercolatorPreHandler;
import com.manager.strategy.PreHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Configuration
public class TransactionManager {

    public enum TransactionState {
        UNCOMMITTED(0), COMMITTED(1), ROLLBACK(2);
        private Integer state;

        TransactionState(Integer state) {
            this.state = state;
        }

        public Integer getState() {
            return state;
        }
    }

    public static class Transaction {
        private TransactionState transactionState = TransactionState.UNCOMMITTED;
        private List<EsOperator> esOperatorList;
        private List<DBOperator> dbOperatorList;

        public List<DBOperator> getDbOperatorList() {
            return dbOperatorList;
        }

        public void setDbOperatorList(List<DBOperator> dbOperatorList) {
            this.dbOperatorList = dbOperatorList;
        }

        Transaction(Integer id) {
        }

        public TransactionState getTransactionState() {
            return transactionState;
        }

        public void setTransactionState(TransactionState transactionState) {
            this.transactionState = transactionState;
        }

        public List<EsOperator> getEsOperatorList() {
            return esOperatorList;
        }

        public void setEsOperatorList(List<EsOperator> esOperatorList) {
            this.esOperatorList = esOperatorList;
        }
    }

    public static class TransactionView {

        private Integer lastTransaction;

        private Integer currentTransaction;

        public Integer getLastTransaction() {
            return lastTransaction;
        }

        public void setLastTransaction(Integer lastTransaction) {
            this.lastTransaction = lastTransaction;
        }

        public Integer getCurrentTransaction() {
            return currentTransaction;
        }

        public void setCurrentTransaction(Integer currentTransaction) {
            this.currentTransaction = currentTransaction;
        }
    }

    @Autowired
    private GlobalTransactionMapper globalTransactionMapper;
    @Autowired
    private EsMappingMapper esMappingMapper;

    private AtomicInteger counter = new AtomicInteger();

    public Transaction buildTransaction(List<EsOperator> esOperatorList) {
        Integer transId = counter.addAndGet(1);
        Transaction transaction = new Transaction(transId);
        transaction.setEsOperatorList(esOperatorList);
        transaction.setDbOperatorList(esOperatorList.stream().map(this::convertToDBOperator).collect(Collectors.toList()));
        return transaction;
    }

    @Transactional
    public TransactionView updateGlobalTransaction(String indexName, String documentId, Map<Integer, Integer> transactionStatusMap) {
        List<Integer> globalTransactions = Arrays.stream(esMappingMapper.selectGlobalTransaction(indexName, documentId).split(","))
                .map(Integer::valueOf).sorted().collect(Collectors.toList());
        Collections.reverse(globalTransactions);


        TransactionView transactionView = new TransactionView();
        if (globalTransactions.size() == 0) {
            transactionView.setCurrentTransaction(0);
            transactionView.setLastTransaction(0);
            return transactionView;
        }

        List<Integer> newGlobalTransactions = new ArrayList<>();
        boolean isFirstCommit = false;

        for(Integer transaction: globalTransactions) {
            if(Objects.equals(transactionStatusMap.get(transaction), TransactionState.COMMITTED.getState()) && !isFirstCommit) {
                isFirstCommit = true;
                transactionView.setCurrentTransaction(transaction);
                newGlobalTransactions.add(transaction);
            } else if(Objects.equals(transactionStatusMap.get(transaction), TransactionState.UNCOMMITTED.getState())) {
                transactionView.setLastTransaction(Math.max(transactionView.getLastTransaction(), newGlobalTransactions.get(transaction)));
                newGlobalTransactions.add(globalTransactions.get(transaction));
            }
        }

        //TODO

        return null;
    }

    public Map<Integer, Integer> queryAllTransactionStatus() {
        return globalTransactionMapper.queryAllTransactionStatus().stream()
                .collect(Collectors.toMap(
                        (Map<String, Integer> item) -> item.get("id"),
                        (Map<String, Integer> item) -> item.get("status")
                ));
    }

    private DBOperator convertToDBOperator(EsOperator esOperator) {
        switch (esOperator.getOperatorType()) {
            case DELETE:
                return new DBOperator(DBOperator.OperatorType.DELETE, esOperator.getIndexName(), esOperator.getDocumentId());
            case CREATE:
            case INDEX:
                return new DBOperator(DBOperator.OperatorType.CREATE, esOperator.getIndexName(), esOperator.getDocumentId());
            case UPDATE:
                return new DBOperator(DBOperator.OperatorType.UPDATE, esOperator.getIndexName(), esOperator.getDocumentId());
            default:
                return null;
        }
    }
//
//    public TransactionManager.TransactionState getTransactionState(String transId) {
//        TransactionManager.Transaction transaction = getTransaction(transId);
//        if(transaction != null) {
//            return transaction.getTransactionState();
//        }
//        return null;
//    }
}
