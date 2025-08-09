package com.example.module.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 交换机名称
    public static final String CATEGORY_DELETE_EXCHANGE = "category.delete.exchange";
    
    // 队列名称
    public static final String CATEGORY_DELETE_QUEUE = "category.delete.queue";
    
    // 路由键
    public static final String CATEGORY_DELETE_ROUTING_KEY = "category.delete";

    @Bean
    public DirectExchange categoryDeleteExchange() {
        return new DirectExchange(CATEGORY_DELETE_EXCHANGE);
    }

    @Bean
    public Queue categoryDeleteQueue() {
        return QueueBuilder.durable(CATEGORY_DELETE_QUEUE)
                .withArgument("x-dead-letter-exchange", CATEGORY_DELETE_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", CATEGORY_DELETE_ROUTING_KEY + ".failed")
                .build();
    }

    @Bean
    public Binding categoryDeleteBinding() {
        return BindingBuilder
                .bind(categoryDeleteQueue())
                .to(categoryDeleteExchange())
                .with(CATEGORY_DELETE_ROUTING_KEY);
    }

    // 死信队列配置
    @Bean
    public DirectExchange categoryDeleteDlxExchange() {
        return new DirectExchange(CATEGORY_DELETE_EXCHANGE + ".dlx");
    }

    @Bean
    public Queue categoryDeleteDlxQueue() {
        return QueueBuilder.durable(CATEGORY_DELETE_QUEUE + ".dlx").build();
    }

    @Bean
    public Binding categoryDeleteDlxBinding() {
        return BindingBuilder
                .bind(categoryDeleteDlxQueue())
                .to(categoryDeleteDlxExchange())
                .with(CATEGORY_DELETE_ROUTING_KEY + ".failed");
    }

    // 使用JSON消息转换器替代Java序列化
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}