package com.mq.example.publisher.service;

import com.mq.example.publisher.model.QueueMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class PublisherService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    public void publishTextMessage(String message, String queueName) {

        System.out.println(message);

        rabbitTemplate.convertAndSend(queueName, message);

    }

    public void publishJsonMessage(String message, String queueName) {

        QueueMessage msgObject = (QueueMessage) JsonToObject(message, QueueMessage.class);

        rabbitTemplate.convertAndSend(queueName, msgObject);

    }

    private JsonNode objectToNode(Object jsonObject) { return objectMapper.valueToTree(jsonObject);}

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
