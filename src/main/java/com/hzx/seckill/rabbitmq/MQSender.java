package com.hzx.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/27 22:22
 * @description: TODO
 */
@Service    //作为一个 Service 组件注入
@Slf4j
public class MQSender {


    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendHeaders01(String msg) {
        log.info("发送消息 QUEUE1 和 QUEUE2 接收:" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "fast");
        Message message = new Message(msg.getBytes(), properties);
        //需要指定交换机 + 路由key + 发送的消息
        rabbitTemplate.convertAndSend("headersExchange", "", message);
    }

    public void sendHeaders02(String msg) {
        log.info("发送消息 QUEUE1 接收:" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "normal");
        Message message = new Message(msg.getBytes(), properties);
        //需要指定交换机 + 路由key + 发送的消息
        rabbitTemplate.convertAndSend("headersExchange", "", message);
    }

    //测试 Topic 工作机制 Sender - 01
    public void sendTopic01(Object msg) {
        log.info("发送消息 QUEUE01 接收:" + msg);
        //发送消息到 topicExchange 队列，同时携带 routingKey queue.red.message
        rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);
    }

    //测试 Topic 工作机制 Sender - 02
    public void sendTopic02(Object msg) {
        log.info("发送消息 QUEUE01 接收:" + msg);
        //发送消息到 topicExchange 队列，同时携带 routingKey queue.red.message
        rabbitTemplate.convertAndSend("topicExchange", "green.queue.green.message", msg);
    }

    /*
     添加方法发送到 Direct 规则指定的队列1 with routingKey:queue.green
     */
    public void sendDirect01(Object msg) {
        log.info("发送消息" + msg);
        //将消息发送到 directExchange 交换机内，同时指定路由路径为 queue.red
        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
    }

    /*
     添加方法发送到 Direct 规则指定的队列2 with routingKey:queue.red
     */
    public void sendDirect02(Object msg) {
        log.info("发送消息" + msg);
        //将消息发送到 directExchange 交换机内，同时指定路由路径为 queue.green
        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
    }

    //定义发送方法
    public void send(Object msg) {
        log.info("发送消息 - " + msg);
        //向 rabbit 队列内发送消息; 注入 routingKey 配置要在 RabbitMQConfig 内映射
        rabbitTemplate.convertAndSend("queue", msg);
    }

    //使用 fanout 模式发送消息
    public void sendFanout(Object msg) {
        log.info("发送消息:" + msg);
        /*
         注意选择的重载方法为:
         public void convertAndSend(String exchange, String routingKey, final Object object)
         1. 参数 1 用于指定交换机的名称
         2. 参数 2 用于指定 routingKey
         3. 参数 3 用于指定发送的 msg
         */
        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
    }
}
