package com.hzx.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/28 14:52
 * @description: 针对 Topic 使用模式的配置类
 */
@Configuration
public class RabbitMQTopicConfig {


    private static final String QUEUE01 = "queue_topic01";
    private static final String QUEUE02 = "queue_topic02";
    private static final String EXCHANGE = "topicExchange";
    private static final String ROUTING_KEY_01 = "*.queue.#";
    private static final String ROUTING_KEY_02 = "#.queue.#";

    @Bean
    public Queue queue_topic01() {
        return new Queue(QUEUE01);
    }

    @Bean
    public Queue queue_topic02() {
        return new Queue(QUEUE02);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding_topic01() {
        return BindingBuilder
                .bind(queue_topic01())
                .to(topicExchange())
                .with(ROUTING_KEY_01);
    }

    @Bean
    public Binding binding_topic02() {
        return BindingBuilder
                .bind(queue_topic01())
                .to(topicExchange())
                .with(ROUTING_KEY_02);
    }
}
