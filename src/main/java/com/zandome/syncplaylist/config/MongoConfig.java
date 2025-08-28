package com.zandome.syncplaylist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.zandome.syncplaylist.*.infra.persistence")
public class MongoConfig {
}