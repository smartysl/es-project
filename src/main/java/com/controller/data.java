package com.controller;

import com.ResponseTemplate.DataResponseTemplate;
import com.ResponseTemplate.GetResponse;
import com.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/data")
public class data {

    @Autowired
    GetService getService;
    @Autowired
    IndexService indexService;
    @Autowired
    TransactionService transactionService;

    @GetMapping("/{indexName}/{id}/_get")
    public GetResponse get(@PathVariable("indexName") String indexName, @PathVariable("id") String documentId, String transactionId) {
        GetServiceResult result = getService.getDocument(transactionId, indexName, documentId);
        return new GetResponse(result.getErrorNo(),
                result.getErrorMsg(),
                transactionService.getTransactionState(transactionId).getState(),
                result.getData());
    }

    @PostMapping({"/{indexName}", "/{indexName}/{id}/_create"})
    public DataResponseTemplate index(@PathVariable("indexName") String indexName, @PathVariable(value = "id", required = false) String documentId,
                                      String transactionId, @RequestBody() Map<String, Object> data) {
        DataServiceResult result = indexService.indexDocument(transactionId, indexName, documentId, data);
        return new DataResponseTemplate(result.getErrorNo(),
                result.getErrorMsg(),
                transactionService.getTransactionState(transactionId).getState()
        );
    }
}
