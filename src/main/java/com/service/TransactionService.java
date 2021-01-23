package com.service;

import com.manager.ResourceManager;
import com.manager.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionService {

    public enum Result {
        SUCCESS(0, ""),
        STATE_CONFLICT_ERROR(1, "transaction is already been finished"),
        NO_SUCH_TRANSACTION_ERROR(2, "no such transaction");
        private Integer errorNo;
        private String errorMsg;
        Result(Integer errorNo, String errorMsg) {
            this.errorNo = errorNo;
            this.errorMsg = errorMsg;
        }

        public Integer getErrorNo() {
            return errorNo;
        }

        public void setErrorNo(Integer errorNo) {
            this.errorNo = errorNo;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    @Autowired
    TransactionManager transactionManager;
    @Autowired
    ResourceManager resourceManager;

    public String startTransaction() {
        return transactionManager.addTransaction();
    }

    public Result commitTransaction(String transId) {
        TransactionManager.Transaction transaction = transactionManager.getTransaction(transId);
        if(transaction == null) {
            return Result.NO_SUCH_TRANSACTION_ERROR;
        }
        if(transaction.getTransactionState() != TransactionManager.TransactionState.UNCOMMITTED) {
            return Result.STATE_CONFLICT_ERROR;
        }
        try {
            resourceManager.writeEsDocuments(transactionManager.getTransactionOperators(transId));
            transaction.setTransactionState(TransactionManager.TransactionState.COMMITTED);
        } catch (Exception e) {

        }
        return Result.SUCCESS;
    }

    public Result rollbackTransaction(String transId) {
        TransactionManager.Transaction transaction = transactionManager.getTransaction(transId);
        if(transaction == null) {
            return Result.NO_SUCH_TRANSACTION_ERROR;
        }
        if(transaction.getTransactionState() != TransactionManager.TransactionState.UNCOMMITTED) {
            return Result.STATE_CONFLICT_ERROR;
        }
        try {
            System.out.println("rollback transaction");
            transaction.setTransactionState(TransactionManager.TransactionState.ROLLBACK);
        } catch (Exception e) {

        }
        return Result.SUCCESS;
    }

    public TransactionManager.TransactionState getTransactionState(String transId) {
        return transactionManager.getTransactionState(transId);
    }
}
