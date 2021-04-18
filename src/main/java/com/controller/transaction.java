package com.controller;

import com.responseTemplate.TransactionCreateResponse;
import com.responseTemplate.TransactionResponseTemplate;
import com.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transaction")
public class transaction {

    @Autowired
    TransactionService transactionService;

    @PostMapping()
    public TransactionCreateResponse startTransaction() {
        String transactionId = transactionService.startTransaction();
        return new TransactionCreateResponse(0, "", transactionId);
    }

    @PostMapping("/{transactionId}/_commit")
    public TransactionResponseTemplate commitTransaction(@PathVariable("transactionId") String transId) {
        TransactionService.Result result = transactionService.commitTransaction(transId);
        return new TransactionResponseTemplate(result.getErrorNo(), result.getErrorMsg());
    }

    @PostMapping("/{transactionId}/_rollback")
    public TransactionResponseTemplate rollbackTransaction(@PathVariable("transactionId") String transId) {
        TransactionService.Result result = transactionService.commitTransaction(transId);
        return new TransactionResponseTemplate(result.getErrorNo(), result.getErrorMsg());
    }

}
