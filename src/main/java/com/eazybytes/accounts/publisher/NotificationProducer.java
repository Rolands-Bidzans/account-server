package com.eazybytes.accounts.publisher;

import com.eazybytes.accounts.dto.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {
    private Logger LOGGER = LoggerFactory.getLogger(NotificationProducer.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.binding.routing.notification.key}")
    private String notificationRoutingKey;

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(NotificationEvent notificationEvent) {
        LOGGER.info(String.format("Order event sent -> %s", notificationEvent.toString()));
        // Send to notification queue
        rabbitTemplate.convertAndSend(exchange, notificationRoutingKey, notificationEvent);
    }
}
