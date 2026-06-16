package com.mq.example.subscriber.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.host}")
    private String hostName;

    @Value("${rabbitmq.username}")
    private String userName;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.port}")
    private int port;

    @Value("${rabbitmq.queuename}")
    private String queueName;

    @Bean
    public CachingConnectionFactory connectionFactory() throws Exception {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(hostName);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setPort(port);
        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory listenerContainerFactory(
            ConnectionFactory connectionFactory, StatelessRetryOperationsInterceptor retryOperationsInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(retryOperationsInterceptor);
        return factory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() throws Exception {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    Queue createQueue() throws Exception {
        Queue q = QueueBuilder.durable(queueName).build();
        amqpAdmin().declareQueue(q);
        return q;
    }

    @Bean
    Queue boqQueue() throws Exception {
        Queue q = QueueBuilder.durable("boq." + queueName).build();
        amqpAdmin().declareQueue(q);
        return q;
    }

    @Bean
    public RepublishMessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        RepublishMessageRecoverer recoverer = new RepublishMessageRecoverer(rabbitTemplate);
        recoverer.setErrorRoutingKeyPrefix("boq.");
        return recoverer;
    }

    @Bean
    public StatelessRetryOperationsInterceptor retryOperationsInterceptor(
            RepublishMessageRecoverer recoverer) {
        StatelessRetryOperationsInterceptor interceptor = RetryInterceptorBuilder
                .stateless()
                .maxRetries(2)
                .backOffOptions(2000, 1, 100000)
                .recoverer(recoverer)
                .build();
        return interceptor;
    }
}
