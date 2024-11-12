package com.example.PB.service;

import com.example.PB.domain.CustomMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RabbitMqPublisher {

    private AtomicInteger atomicInteger;

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper mapper;

    @Value("${spring.rabbitmq.routingKey}")
    private String routingKey;

    public RabbitMqPublisher( RabbitTemplate rabbitTemplate) {
        this.atomicInteger = new AtomicInteger(0);
        this.rabbitTemplate = rabbitTemplate;
        this.mapper = new ObjectMapper();
    }
    public void publish() throws JsonProcessingException {
        int messageId = atomicInteger.incrementAndGet();
        String message = String.format("message %d - %s", messageId, LocalDateTime.now());

        String jsonMessage = mapper.writeValueAsString(new CustomMessage(messageId, message));

        rabbitTemplate.convertAndSend(routingKey, jsonMessage);
        System.out.println(messageId);
    }
}
