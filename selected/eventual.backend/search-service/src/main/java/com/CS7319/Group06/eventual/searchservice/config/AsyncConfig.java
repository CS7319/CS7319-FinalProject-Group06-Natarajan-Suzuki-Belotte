package com.CS7319.Group06.eventual.searchservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for notifications and indexing data to elastic search
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "ingestionExecutor")
    public Executor ingestionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("ingestion-");
        executor.initialize();
        return executor;
    }

    /**
     * Single-threaded executor for reindex jobs — ensures only one reindex runs at a time
     * and progress is tracked accurately.
     */
    @Bean(name = "reindexExecutor")
    public Executor reindexExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("reindex-");
        executor.initialize();
        return executor;
    }
}
