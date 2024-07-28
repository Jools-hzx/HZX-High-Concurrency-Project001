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
 * @date 2024/7/28 22:33
 * @description: RabbitMQ 消息队列关于秒杀操作的配置
 */

@Configuration
public class RabbitMQSeckillConfig {

    private static final String QUEUE1 = "queue_seckill";
    private static final String EXCHANGE_SECKILL = "topicSeckillExchange";

    private static final String SECKILL_ROUTE_KEY = "seckill.*";

    //配置队列
    @Bean
    public Queue queue_seckill01() {
        return new Queue(QUEUE1);
    }

    //配置秒杀操作消息队列的交换机 - 使用 Topic 模式
    @Bean
    public TopicExchange topicSeckillExchange() {
        return new TopicExchange(EXCHANGE_SECKILL);
    }

    @Bean
    public Binding binding_seckill_topic() {
        return BindingBuilder
                .bind(queue_seckill01())
                .to(topicSeckillExchange())
                .with(SECKILL_ROUTE_KEY);
    }
}
