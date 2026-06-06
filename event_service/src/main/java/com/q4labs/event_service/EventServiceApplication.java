package com.q4labs.event_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class EventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.q4labs.event_service.EventServiceApplication.class, args);
        System.out.println("******* "+System.getenv("DB_PASSWORD")+"**************");
    }

}
