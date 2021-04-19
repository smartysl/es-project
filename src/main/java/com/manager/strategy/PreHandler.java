package com.manager.strategy;

import com.manager.DBOperator;
import com.manager.EsOperator;

import java.util.List;

/**
 * @author yushilin
 * @date 2021/4/16 3:13 下午
 */
public interface PreHandler {
    void preHandle(List<DBOperator> dbOperatorList, List<EsOperator> esOperatorList);
}
