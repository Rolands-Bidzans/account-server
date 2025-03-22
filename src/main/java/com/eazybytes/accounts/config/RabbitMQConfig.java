package com.eazybytes.accounts.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;


@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.notification.name}")
    private String notificationQueue;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.binding.routing.notification.key}")
    private String notificationRoutingKey;

    // spring bean for queue - notification queue
    @Bean
    public Queue queue() {
        return new Queue(notificationQueue);
    }

    // spring bean for exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    //spring bean for binding between exchange and queue using routing key

    @Bean
    public Binding orderBinding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(notificationRoutingKey);
    }

    //message converter
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    // configure RabbitTemplate
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
