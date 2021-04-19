package com.manager.strategy;

import com.dao.mapper.EsMappingMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.DBOperator;
import com.manager.EsOperator;
import com.utils.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author yushilin
 * @date 2021/4/16 3:29 下午
 */
public abstract class BaseHandler implements PreHandler{

    @Autowired
    public EsMappingMapper esMappingMapper;
    @Autowired
    private MqProducer mqProducer;

    @Override
    public void preHandle(List<DBOperator> dbOperatorList, List<EsOperator> esOperatorList) {

    }

    protected void sendMessage(EsOperator esOperator) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        mqProducer.send("default-key", objectMapper.writeValueAsString(esOperator));
    }

}
