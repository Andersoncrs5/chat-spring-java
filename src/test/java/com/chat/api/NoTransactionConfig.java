package com.chat.api;

import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoTransactionManager;

public class NoTransactionConfig {
    @Bean
    public MongoTransactionManager mongoTransactionManager() {
        return null;
    }
}
