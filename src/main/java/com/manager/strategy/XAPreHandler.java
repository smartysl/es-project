package com.manager.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.manager.DBOperator;
import com.manager.EsOperator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author yushilin
 * @date 2021/4/16 3:15 下午
 */
@Service
public class XAPreHandler extends BaseHandler{

    @Override
    public void preHandle(List<DBOperator> dbOperatorList, List<EsOperator> esOperatorList) {
        doPreHandle(dbOperatorList, esOperatorList);
    }

    @Transactional
    private void doPreHandle(List<DBOperator> dbOperatorList, List<EsOperator> esOperatorList) {
        IntStream.range(0,dbOperatorList.size()).forEach(i -> {
            DBOperator dbOperator = dbOperatorList.get(i);
            EsOperator esOperator = esOperatorList.get(i);
            doOperate(dbOperator);
            try {
                sendMessage(esOperator);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
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
