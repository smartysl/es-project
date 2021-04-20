package com.manager;

import com.dao.mapper.EsMappingMapper;
import com.dao.mapper.GlobalTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        private Integer id;

        public List<DBOperator> getDbOperatorList() {
            return dbOperatorList;
        }

        public void setDbOperatorList(List<DBOperator> dbOperatorList) {
            this.dbOperatorList = dbOperatorList;
        }

        Transaction(Integer id) {
            this.id = id;
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

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public static class TransactionView {

        private Integer lastTransaction = 0;

        private Integer currentTransaction = 0;

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

    @Transactional(value = "xaManager")
    public TransactionView updateGlobalTransaction(String indexName, String documentId, Integer currentTransId, Map<Integer, Integer> transactionStatusMap) {
        List<Integer> globalTransactions = Arrays.stream(esMappingMapper.selectGlobalTransaction(indexName, documentId).split(","))
                .filter(s -> s.length() > 0)
                .map(Integer::valueOf).sorted().collect(Collectors.toList());
        Collections.reverse(globalTransactions);

        List<String> newGlobalTransactions = new ArrayList<>();
        if(!Objects.isNull(currentTransId)) {
            newGlobalTransactions.add(String.valueOf(currentTransId));
        }

        TransactionView transactionView = new TransactionView();
        boolean isFirstCommit = false;
        for(Integer transaction: globalTransactions) {
            if(Objects.equals(transactionStatusMap.get(transaction), TransactionState.COMMITTED.getState()) && !isFirstCommit) {
                isFirstCommit = true;
                transactionView.setCurrentTransaction(transaction);
                transactionView.setLastTransaction(Math.max(transactionView.getLastTransaction(), transaction));
                newGlobalTransactions.add(String.valueOf(transaction));
            } else if(Objects.equals(transactionStatusMap.get(transaction), TransactionState.UNCOMMITTED.getState())) {
                transactionView.setLastTransaction(Math.max(transactionView.getLastTransaction(), transaction));
                newGlobalTransactions.add(String.valueOf(transaction));
            }
        }

        esMappingMapper.updateGlobalTransaction(indexName, documentId, String.join(",", newGlobalTransactions));

        return transactionView;
    }

    public Map<Integer, Integer> queryAllTransactionStatus() {
        return globalTransactionMapper.queryAllTransactionStatus().stream()
                .collect(Collectors.toMap(
                        (Map<String, Integer> item) -> item.get("id"),
                        (Map<String, Integer> item) -> item.get("status")
                ));
    }

    public void beginTransaction(Integer transId) {
        globalTransactionMapper.insertGlobalTransaction(transId, TransactionState.UNCOMMITTED.getState());
    }

    public void rollbackTransaction(Integer transId) {
        globalTransactionMapper.updateGlobalTransactionStatus(transId, TransactionState.ROLLBACK.getState());
    }

    public void commitTransaction(Integer transId) {
        globalTransactionMapper.updateGlobalTransactionStatus(transId, TransactionState.COMMITTED.getState());
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
}
