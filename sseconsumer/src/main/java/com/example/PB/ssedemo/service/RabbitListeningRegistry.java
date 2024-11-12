package com.example.PB.ssedemo.service;

import com.example.PB.ssedemo.domain.CustomMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RabbitListeningRegistry implements ChannelAwareMessageListener {
    private static final Map<Integer, SseEmitter> listeners = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void register(int id, SseEmitter sseEmitter) {
        listeners.put(id, sseEmitter);
    }

    public void unregister(int id) {
        listeners.remove(id);
        String message = "De Registering client " + id+ " " + LocalDateTime.now();
        System.out.println(message);
    }

    @RabbitListener(queues="PIB.q1",containerFactory = "myFactory")
    public void onMessage(Message rabbitMessage, Channel channel) {
        String jsonMessage = new String(rabbitMessage.getBody());
        CustomMessage message = null;
        SseEmitter sseEmitter;
        try {
            message = objectMapper.readValue(jsonMessage, CustomMessage.class);
            sseEmitter = listeners.getOrDefault(message.getMessageId(), null);
            if (Objects.nonNull(sseEmitter)) {
                sseEmitter.send(message);
                channel.basicAck(rabbitMessage.getMessageProperties().getDeliveryTag(), false);
            } else {
                channel.basicNack(rabbitMessage.getMessageProperties().getDeliveryTag(), false, true);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
