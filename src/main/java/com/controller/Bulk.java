package com.controller;

import com.responseTemplate.TransactionResponseTemplate;
import com.manager.EsOperator;
import com.service.BulkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author yushilin
 * @date 2021/4/16 2:34 下午
 */
@RestController
@RequestMapping(value = "/api")
public class Bulk {

    @Autowired
    private BulkService bulkService;

    @PostMapping("/bulk")
    public TransactionResponseTemplate bulk() {
        return bulkService.handleBulk(convertToOperator());
    }

    private List<EsOperator> convertToOperator() {
        List<EsOperator> esOperators = new ArrayList<>();
        esOperators.add(new EsOperator(EsOperator.OperatorType.UPDATE, "demo", "3", new HashMap<>() {{
            put("name", "ysl");
        }}));
        esOperators.add(new EsOperator(EsOperator.OperatorType.UPDATE, "demo", "4", new HashMap<>() {{
            put("name", "ysl");
        }}));
        return esOperators;
    }
}
