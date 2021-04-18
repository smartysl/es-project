package com.dao.mapper;

public interface EsMappingMapper{

    public Integer selectVersion(String indexName, String docId);

    public Long selectTimeStamp(String indexName, String docId);

    public void insertEsMapping(String docId, String indexName, String timestamp);
}
