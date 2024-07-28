package com.hzx.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/28 16:08
 * @description: RabbitMQ 基于 Headers 工作模式的配置
 */
//@Configuration
public class RabbitMQHeadersConfig {

    private static final String QUEUE01 = "queue_header01";
    private static final String QUEUE02 = "queue_header02";
    private static final String EXCHANGE = "headersExchange";

    //注册基于Headers模式的队列一
    @Bean
    public Queue queue_header01() {
        return new Queue(QUEUE01);
    }

    //注册基于Headers模式的队列一
    @Bean
    public Queue queue_header02() {
        return new Queue(QUEUE02);
    }

    //注册基于Headers模式的交换机
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(EXCHANGE);
    }

    //用于绑定指定的队列
    @Bean
    public Binding binding_header01() {
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "low");
        System.out.println("yy=" + headersExchange().hashCode());
        //设定部分匹配
        return BindingBuilder.bind(queue_header01())
                .to(headersExchange())
                .whereAny(map)
                .match();
    }

    //用于绑定指定队列
    @Bean
    public Binding binding_header02() {
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "fast");
        System.out.println("yy = " + headersExchange().hashCode());
        //设置全部匹配
        return BindingBuilder.bind(queue_header02())
                .to(headersExchange())
                .whereAll(map)
                .match();
    }
}
