package com.dao.mapper;

public interface EsMappingMapper{

    void insertEsMapping(String indexName, String docId);

    void updateEsMappingOnlyLock(String indexName, String docId);

    String selectGlobalTransaction(String indexName, String docId);

    void updateGlobalTransaction(String indexName, String docId, String globalTransactions);
}
