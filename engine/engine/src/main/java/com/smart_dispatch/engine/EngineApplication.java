package com.smart_dispatch.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
public class EngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(EngineApplication.class, args);
        System.out.println("\n✅ SMART CITY DISPATCH API IS LIVE AND WAITING FOR CALLS...\n");
    }

    @Bean("VaadinTaskExecutor")
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("engine-");
    }
}