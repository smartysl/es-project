package com.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ThreadPoolUtil.applicationContext  = applicationContext;
    }

    @Bean
    public ExecutorService singleThreadPool() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService concurrentThreadPool() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public ExecutorService snapshotThreadPool() { return Executors.newFixedThreadPool(10); }

    public static ExecutorService getThreadPool(String isolation) {
        if(isolation.equals("serialize")) {
            return (ExecutorService) applicationContext.getBean("singleThreadPool");
        } else{
            return (ExecutorService) applicationContext.getBean("concurrentThreadPool");
        }
    }

    public static ExecutorService getSnapshotPool() {
        return (ExecutorService) applicationContext.getBean("snapshotThreadPool");
    }

}
