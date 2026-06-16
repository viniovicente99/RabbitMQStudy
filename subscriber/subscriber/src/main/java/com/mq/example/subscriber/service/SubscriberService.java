package com.mq.example.subscriber.service;

import com.mq.example.subscriber.model.QueueMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Service
public class SubscriberService {

    private ObjectMapper objectMapper;

    @RabbitListener( containerFactory = "listenerContainerFactory", queues = "${rabbitmq.queuename}")
    public void receiveMessage(Message message) {

        System.out.println("New RMQ message received");

        try {

            String jsonMessage = rmqMessageToString(message);

            QueueMessage msgObject = (QueueMessage) JsonToObject(jsonMessage, QueueMessage.class);

            System.out.println(msgObject.toString());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    private String rmqMessageToString(Message message) {
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }

    private Object JsonToObject(String jsonString, Class<?> clazz) {
        try{
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init(){
        objectMapper = new ObjectMapper();
    }

}
