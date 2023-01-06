package com.malpro.importer.configuration;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by fahian on 07.10.22.
 */
@Configuration
@EnableRabbit
public class RabbitProducerConfiguration {

    @Value("${rabbitmq.store-product.exchange}")
    private String storeProductExchange;

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    DirectExchange messagesExchange() {
        return new DirectExchange(storeProductExchange);
    }

}
