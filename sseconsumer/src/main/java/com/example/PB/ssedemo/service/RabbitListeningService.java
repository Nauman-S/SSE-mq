package com.example.PB.ssedemo.service;

import com.example.PB.ssedemo.domain.CustomMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class RabbitListeningService {

    @Value("${spring.rabbitmq.queueName}")
    private String queueName;

    private static final long ONE_SECOND_TIMEOUT= 1000L;

    @Autowired
    RabbitTemplate rabbitTemplate;


    public void recieveMessage(SseEmitter emitter) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = null;
        try {
            jsonMessage = (String) rabbitTemplate.receiveAndConvert(queueName, ONE_SECOND_TIMEOUT);
        } catch (Exception e) {
            System.out.println(e);
        }

        if (jsonMessage!= null) {
            CustomMessage message = objectMapper.readValue(jsonMessage, CustomMessage.class);
            Thread.sleep(2000);
            emitter.send(jsonMessage);

        } else {
            emitter.send("Message could not be retrieved from Queue");
        }

        emitter.complete();
    }


}
