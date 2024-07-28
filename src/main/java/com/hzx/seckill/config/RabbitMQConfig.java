package com.hzx.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/27 22:19
 * @description: TODO
 */
//@Configuration
public class RabbitMQConfig {

    private static final String QUEUE = "queue";

    //测试 fanout 模式
    private static final String QUEUE1 = "queue_fanout01";
    private static final String QUEUE2 = "queue_fanout02";
    private static final String exchange = "fanoutExchange";

    //测试 Direct 模式
    private static final String QUEUE_Direct1 = "queue_direct01";
    private static final String QUEUE_Direct2 = "queue_direct02";
    private static final String exchange_Direct = "directExchange";
    private static final String routingKey01 = "queue.red";
    private static final String routingKey02 = "queue.green";

    /*
     创建基于 Direct 规则的交换机 01
     */
    @Bean
    public Queue queue_direct1() {
        return new Queue(QUEUE_Direct1); //
    }

    /*
     闯进基于 Direct 规则的交换机 02
     */
    @Bean
    public Queue queue_direct2() {
        return new Queue(QUEUE_Direct2);
    }

    @Bean
    public DirectExchange exchange_direct() {
        return new DirectExchange(exchange_Direct);
    }

    /*
     将队列绑定到 指定交换机 并且指定路由为 queue.red
     */
    @Bean
    public Binding binding_direct01() {
        return BindingBuilder
                .bind(queue_direct1())
                .to(exchange_direct())
                .with(routingKey01);
    }

    /*
     将队列绑定到 指定交换机 并且指定路由为 queue.green
     */
    @Bean
    public Binding binding_direct02() {
        return BindingBuilder
                .bind(queue_direct2())
                .to(exchange_direct())
                .with(routingKey02);
    }

    /*
    1. 该 Bean 用于配置队列
    2. 队列名称为: queue
    3. true 表示: 将队列进行持久化操作
       durable: 队列是否持久化。队列默认是存放到内存中的，rabbitmq 重启则丢失;
                若想重启之后还存在则队列要持久化。
                报端到 Erlang 自带的 Mnesia 数据库内，当 rabbitmq 重启之后会读取该数据库
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Queue queue1() {
        return new Queue(QUEUE1);
    }

    @Bean
    public Queue queue2() {
        return new Queue(QUEUE2);
    }

    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(exchange);
    }

    /*
     将 queue_fanout01 队列绑定到交换机 fanoutExchange
     */
    @Bean
    public Binding binding01() {
        return BindingBuilder.bind(queue1()).to(exchange());
    }

    /*
     将 queue_fanout02 队列绑定到交换机 fanoutExchange
     */
    @Bean
    public Binding binding02() {
        return BindingBuilder.bind(queue2()).to(exchange());
    }
}
