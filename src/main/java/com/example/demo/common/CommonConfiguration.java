
package com.example.demo.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class CommonConfiguration {
    @Bean
    public ThreadPoolTaskExecutor customerThreadPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池大小
        threadPoolTaskExecutor.setCorePoolSize(10);
        //线程池最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(10);
        //最大等待任务数
        threadPoolTaskExecutor.setQueueCapacity(500);
        //线程池的初始化
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }
}
