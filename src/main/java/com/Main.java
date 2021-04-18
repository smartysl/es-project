package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;

@MapperScan("com.dao.mapper")
@EnableAsync
@EnableCaching
@SpringBootApplication(exclude = {JtaAutoConfiguration.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
//    @Bean
//    public Object testBean(PlatformTransactionManager platformTransactionManager){
//        System.out.println(">>>>>>>>>>" + platformTransactionManager.getClass().getName());
//        return new Object();
//    }
}
