package com.controller;

import com.dao.mapper.EsMappingMapper;
import com.utils.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {

    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private EsMappingMapper esMappingMapper;

//    @GetMapping("/mq_test")
//    @Transactional
//    public void kafkaTest(String fail) throws InterruptedException {
//        mqProducer.send("1", "prepare");
//        if(Objects.equals(fail, "fail")) {
//            throw new RuntimeException();
//        }
//        mqProducer.send("2", "fail");
//    }

    @GetMapping("/db_test")
    @Transactional(value = "xaManager")
    public void dbTest(String docId) {
        esMappingMapper.insertEsMapping(docId, "demo", "1");
    }
}
