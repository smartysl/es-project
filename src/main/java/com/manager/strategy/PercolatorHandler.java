package com.manager.strategy;

import com.manager.DBOperator;
import com.manager.TransactionManager;
import com.service.Exceptions.ConflictException;
import com.utils.ExceptionUtil;
import com.utils.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yushilin
 * @date 2021/4/16 4:31 下午
 */
@Service
public class PercolatorHandler extends BaseHandler {

    @Autowired
    private TransactionManager transactionManager;

    private static final Logger log = LoggerFactory.getLogger(PercolatorHandler.class);

    @Override
    public void preHandle(List<DBOperator> dbOperatorList, Integer transId) {
        Map<Integer, Integer> transactionStatusMap = transactionManager.queryAllTransactionStatus();

        List<Integer> snapshot = doSnapshot(dbOperatorList, transId, transactionStatusMap);

        log.info("[获取版本快照] 当前事务={}, 快照={}", transId, snapshot);

        transactionManager.beginTransaction(transId);
        log.info("[开启事务] 当前事务={}", transId);

        doActualOperate(dbOperatorList, transId, transactionStatusMap, snapshot);
    }

    @Override
    public void rollback(Integer transId) {
        transactionManager.rollbackTransaction(transId);
    }

    @Override
    public void commit(Integer transId) {
        transactionManager.commitTransaction(transId);
    }

    private void doActualOperate(List<DBOperator> dbOperatorList, Integer transId, Map<Integer, Integer> transactionStatusMap, List<Integer> snapshot) {
        IntStream.range(0, snapshot.size()).forEach(i -> {
            TransactionManager.TransactionView transactionView = transactionManager.updateGlobalTransaction(
                    dbOperatorList.get(i).getIndexName(),
                    dbOperatorList.get(i).getDocumentId(),
                    transId,
                    transactionStatusMap
            );
            if(!transactionView.getLastTransaction().equals(snapshot.get(i))) {
                log.warn("[版本冲突] 当前行已被其他事务更新,准备回滚 当前事务={}, 最新版本={}, 快照版本={}, indexName={}, docId={}",
                        transId,
                        transactionView.getLastTransaction(),
                        snapshot.get(i),
                        dbOperatorList.get(i).getIndexName(),
                        dbOperatorList.get(i).getDocumentId());
                throw new ConflictException();
            }
        });
    }

    private List<Integer> doSnapshot(List<DBOperator> dbOperatorList, Integer transId, Map<Integer, Integer> transactionStatusMap) {
        List<Callable<Integer>> tasks = dbOperatorList.stream().map(dbOperator -> (Callable<Integer>) () -> {
            TransactionManager.TransactionView transactionView = transactionManager.updateGlobalTransaction(
                    dbOperator.getIndexName(),
                    dbOperator.getDocumentId(),
                    null,
                    transactionStatusMap
            );
            if(!transactionView.getLastTransaction().equals(transactionView.getCurrentTransaction())) {
                log.warn("[快照时版本不一致] 快照时有正在执行的事务,准备回滚 当前事务={}, 最新版本={}, 当前版本={}, indexName={}, docId={}",
                        transId,
                        transactionView.getLastTransaction(),
                        transactionView.getCurrentTransaction(),
                        dbOperator.getIndexName(),
                        dbOperator.getDocumentId());
                throw new RuntimeException();
            }
            return transactionView.getCurrentTransaction();
        }).collect(Collectors.toList());

        List<Integer> snapshot = new ArrayList<>();
        List<Future<Integer>> futures;
        try {
            futures = ThreadPoolUtil.getSnapshotPool().invokeAll(tasks);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        for(Future<Integer> future : futures) {
            try {
                snapshot.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.warn("[快照线程执行失败] e={}", ExceptionUtil.getStackTrace(e));
                throw new ConflictException();
            }
        }
        return snapshot;
    }

}
