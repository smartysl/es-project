package com.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EsMappingMapper {

    Integer selectVersion(String indexName, String docId);

    Long selectTimeStamp(String indexName, String id);

    void insertEsMapping(String indexName, String docId);

    void updateEsMappingOnlyLock(String indexName, String docId);

    String selectGlobalTransaction(String indexName, String docId);

    void updateGlobalTransaction(String indexName, String docId, String globalTransactions);
}
