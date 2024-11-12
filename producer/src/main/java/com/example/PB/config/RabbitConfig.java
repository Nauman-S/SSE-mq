package com.example.PB.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {



    @Value("${spring.rabbitmq.exchangeName}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routingKey}")
    private String routingKey;

    @Value("${spring.rabbitmq.queueName}")
    private String queueName;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @PostConstruct
    public void initializeRabbit() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        TopicExchange topicExchange = new TopicExchange(exchangeName);
        rabbitAdmin.declareExchange(topicExchange);
        Queue queue = new Queue(queueName);
        rabbitAdmin.declareQueue(queue);
        Binding binding = BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);
        rabbitTemplate.setExchange(exchangeName);
        rabbitTemplate.setRoutingKey(routingKey);
    }
}
