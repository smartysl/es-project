package com.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class OperatorThreadPool {
    @Bean
    public ExecutorService operatorPool() {
        return Executors.newSingleThreadExecutor();
    }
}
