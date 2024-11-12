package com.example.PB.batch;

import com.example.PB.service.RabbitMqPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class PushBatch {

    @Autowired
    RabbitMqPublisher rabbitMqPublisher;

    @Scheduled(fixedRate = 10000)
    public void populateData() {
        try {
            rabbitMqPublisher.publish();
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
    }
}
