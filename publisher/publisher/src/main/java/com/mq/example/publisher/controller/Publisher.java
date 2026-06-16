package com.mq.example.publisher.controller;

import com.mq.example.publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Publisher {

    @Value("${rabbitmq.queuename}")
    private String queueName;

    @Autowired
    private PublisherService publisherService;

    @PostMapping("/publish/text")
    public void publishText(@RequestBody String text) {

        System.out.println("Sending message " + text);

        publisherService.publishTextMessage(text, queueName);

    }

    @PostMapping("/publish/json")
    public void publishJson(@RequestBody String text) {

        System.out.println("Sending message " + text);

        publisherService.publishJsonMessage(text, queueName);

    }
}
