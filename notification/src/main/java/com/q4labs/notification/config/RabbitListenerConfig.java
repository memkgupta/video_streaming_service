package com.q4labs.notification.config;


import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitListenerConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory
    ) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        // ⚡ concurrency (you can tune later)
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);


        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(3) // retry 3 times
                        .backOffOptions(
                                2000,   // initial interval (2s)
                                2.0,    // multiplier
                                10000   // max interval (10s)
                        )
                        .recoverer((message, cause) -> {


                        })
                        .build()
        );

        return factory;
    }
}
