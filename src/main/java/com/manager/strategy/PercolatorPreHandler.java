package com.manager.strategy;

import com.manager.DBOperator;
import com.manager.EsOperator;
import com.manager.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author yushilin
 * @date 2021/4/16 4:31 下午
 */
public class PercolatorPreHandler extends BaseHandler {

    static class TransactionView {

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
    private TransactionManager transactionManager;

    @Override
    public void preHandle(List<DBOperator> dbOperatorList, List<EsOperator> esOperatorList) {

    }

    private void doPreHandle(List<DBOperator> dbOperatorList, List<EsOperator> esOperatorList) {

    }


}
