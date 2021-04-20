package com.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author yushilin
 * @date 2021/4/16 4:47 下午
 */
@Mapper
public interface GlobalTransactionMapper {

    List<Map<String, Integer>> queryAllTransactionStatus();

    void insertGlobalTransaction(Integer transId, Integer status);

    void updateGlobalTransactionStatus(Integer transId, Integer status);

}
