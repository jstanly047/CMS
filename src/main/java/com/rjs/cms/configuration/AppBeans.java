package com.rjs.cms.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AppBeans {

    @Bean
    ThreadPoolTaskExecutor getDBThreadPoolTaskExecutor(){
        ThreadPoolTaskExecutor dbThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        dbThreadPoolTaskExecutor.setCorePoolSize(5);
        dbThreadPoolTaskExecutor.setMaxPoolSize(10);
        return dbThreadPoolTaskExecutor;
    }

    /*@Bean
    public Scheduler getDBTaskScheduler(ThreadPoolTaskExecutor dbThreadPoolTaskExecutor) {
        return Schedulers.fromExecutor(dbThreadPoolTaskExecutor);
    }*/
}