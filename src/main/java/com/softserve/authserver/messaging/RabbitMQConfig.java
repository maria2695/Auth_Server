package com.softserve.authserver.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.queue}")
    private String queue;
    @Value("${spring.rabbitmq.authqueue}")
    private String authqueue;
    @Value("${spring.rabbitmq.tokenqueue}")
    private String tokenqueue;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.routingkey}")
    private String routingKey;
    @Value("${spring.rabbitmq.authroutingkey}")
    private String authroutingKey;
    @Value("${spring.rabbitmq.tokenroutingkey}")
    private String tokenroutingKey;

    @Value("${spring.rabbitmq.host}")
    String host;

    @Value("${spring.rabbitmq.username}")
    String username;

    @Value("${spring.rabbitmq.password}")
    String password;

    @Bean
    Queue queue() {
        return new Queue(queue, true);
    }
    @Bean
    Queue authqueue() {
        return new Queue(authqueue, true);
    }
    @Bean
    Queue tokenqueue() {
        return new Queue(tokenqueue, true);
    }

    @Bean
    Exchange myExchange() {
        return ExchangeBuilder.directExchange(exchange).durable(true).build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(myExchange())
                .with(routingKey)
                .noargs();
    }
    @Bean
    Binding authbinding() {
        return BindingBuilder
                .bind(authqueue())
                .to(myExchange())
                .with(authroutingKey)
                .noargs();
    }
    @Bean
    Binding tokenbinding() {
        return BindingBuilder
                .bind(tokenqueue())
                .to(myExchange())
                .with(tokenroutingKey)
                .noargs();
    }

    @Bean
    CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        return cachingConnectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}