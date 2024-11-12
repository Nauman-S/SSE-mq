package com.example.PB.ssedemo.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BatchService {

    private static final ConcurrentHashMap<Integer, SseEmitter> emitterMap = new ConcurrentHashMap<>();


    public void register(SseEmitter sseEmitter, int id) {
        sseEmitter.onCompletion(() -> {
            System.out.println("Removing Emitter On Completion -" + id);
            emitterMap.remove(id);
        });
        sseEmitter.onTimeout(()-> {
            System.out.println("Removing Emitter On Timeout -" + id);
            emitterMap.remove(id);
        });
        sseEmitter.onError(throwable -> {
            System.out.println("Removing Emitter On Error -" + id);
            emitterMap.remove(id);
        });
        emitterMap.put(id, sseEmitter);
    }

    @Scheduled(fixedRate = 15000)
    public void publish() {
        System.out.println(emitterMap.keySet());
        List<Integer> removables = new ArrayList<>();
        for (Map.Entry<Integer, SseEmitter> e: emitterMap.entrySet()) {
            try {
                e.getValue().send(String.format("Message - %d", e.getKey()));
            } catch (IOException ex) {
                removables.add(e.getKey());
            }
        }

        for (int i: removables) {
           emitterMap.remove(i);
        }
    }
}
