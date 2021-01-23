package com.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EsMappingMapper {

    public Integer selectVersion(String indexName, String docId);

    public Long selectTimeStamp(String indexName, String id);
}
