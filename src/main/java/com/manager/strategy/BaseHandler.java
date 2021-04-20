package com.manager.strategy;

import com.dao.mapper.EsMappingMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.DBOperator;
import com.manager.EsOperator;
import com.manager.ResourceManager;
import com.service.Exceptions.ClusterNotHealthException;
import com.utils.JsonUtil;
import com.utils.MqProducer;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yushilin
 * @date 2021/4/16 3:29 下午
 */
public abstract class BaseHandler implements Handler {

    @Autowired
    public EsMappingMapper esMappingMapper;
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private ResourceManager resourceManager;
    private static final Logger log = LoggerFactory.getLogger(BaseHandler.class);

    @Override
    public void preHandle(List<DBOperator> dbOperatorList, Integer transId) {

    }

    @Override
    public void rollback(Integer transId) {

    }

    @Override
    public void commit(Integer transId) {

    }

    @Override
    @Transactional(value = "kafkaTransactionManager")
    public void doSendMessage(List<EsOperator> esOperatorList) {
        esOperatorList.forEach(esOperator -> mqProducer.send(JsonUtil.toJson(esOperator)));
    }

    @Override
    public void checkHealthy() {
        ClusterHealthStatus healthStatus = resourceManager.getClusterHealthyStatus();
        log.info("[健康状态检查] 检查结果={}", healthStatus.value());

        if (!(ClusterHealthStatus.GREEN.equals(healthStatus) || ClusterHealthStatus.YELLOW.equals(healthStatus))) {
            throw new ClusterNotHealthException();
        }
    }
}
