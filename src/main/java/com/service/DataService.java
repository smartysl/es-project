package com.service;

import com.manager.ResourceManager;
import com.manager.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

public class DataService {

    @Autowired
    protected TransactionManager transactionManager;
    @Autowired
    protected ResourceManager resourceManager;

}
