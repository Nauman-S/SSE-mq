package com.example.PB.ssedemo.controller;

import com.example.PB.ssedemo.domain.RequestCount;
import com.example.PB.ssedemo.domain.RequestData;
import com.example.PB.ssedemo.service.BatchService;
import com.example.PB.ssedemo.service.RabbitListeningRegistry;
import com.example.PB.ssedemo.service.RabbitListeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Queue;

@RestController
@RequestMapping("")
public class SseController {
    @Autowired
    BatchService batchService;

    @Autowired
    RabbitListeningService rabbitListeningService;

    @Autowired
    RabbitListeningRegistry rabbitListeningRegistry;

    @PostMapping("/poll/batch")
    public SseEmitter poll(@RequestBody RequestData requestData) {
        SseEmitter sseEmitter = new SseEmitter(0L);

        batchService.register(sseEmitter, requestData.getId());


        return sseEmitter;
    }

    @PostMapping("/poll/mq/any")
    public SseEmitter pollMq(@RequestBody RequestCount requestCount) throws Exception {
        SseEmitter sseEmitter = new SseEmitter(0L);

        rabbitListeningService.recieveMessage(sseEmitter);

        return sseEmitter;
    }

    @PostMapping("/poll/mq/targeted")
    public SseEmitter pollMqTargeted(@RequestBody RequestData requestData) throws Exception {
        SseEmitter sseEmitter = new SseEmitter(0L);
        rabbitListeningRegistry.register(requestData.getId(), sseEmitter);
        sseEmitter.onTimeout(() -> rabbitListeningRegistry.unregister(requestData.getId()));
        sseEmitter.onCompletion(()-> rabbitListeningRegistry.unregister(requestData.getId()));

        return sseEmitter;
    }




}
