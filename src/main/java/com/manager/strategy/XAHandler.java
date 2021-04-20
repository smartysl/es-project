package com.manager.strategy;

import com.manager.DBOperator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yushilin
 * @date 2021/4/16 3:15 下午
 */
@Service
public class XAHandler extends BaseHandler{

    @Override
    @Transactional(value = "xaManager")
    public void preHandle(List<DBOperator> dbOperatorList,Integer transId) {
        dbOperatorList.forEach(this::doOperate);
    }

    private void doOperate(DBOperator dbOperator) {
        String indexName = dbOperator.getIndexName();
        String documentId = dbOperator.getDocumentId();
        if(dbOperator.getOperatorType().equals(DBOperator.OperatorType.CREATE)) {
            doCreate(indexName, documentId);
        } else {
            doUpdate(indexName, documentId);
        }
    }

    private void doCreate(String indexName, String documentId) {
        esMappingMapper.insertEsMapping(indexName, documentId);
    }

    private void doUpdate(String indexName, String documentId) {
        esMappingMapper.updateEsMappingOnlyLock(indexName, documentId);
    }
}
