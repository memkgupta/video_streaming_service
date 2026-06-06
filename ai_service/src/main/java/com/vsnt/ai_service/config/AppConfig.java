package com.vsnt.ai_service.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    private final String JDBC_URL = System.getenv("DB_URL");
    private final String DB_USERNAME =  System.getenv("DB_USERNAME");
    private final String DB_PASSWORD =  System.getenv("DB_PASSWORD");
    @Bean
    public HikariConfig hikariConfig() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(JDBC_URL);
    config.setUsername(DB_USERNAME);
    config.setPassword(DB_PASSWORD);
    config.setMaximumPoolSize(10);
    return config;
}
@Bean
    public HikariDataSource hikariDataSource() {
        return new  HikariDataSource(hikariConfig());
}

}
