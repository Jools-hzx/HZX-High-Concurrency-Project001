package com.hzx.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/27 22:26
 * @description: TODO
 */
@Service
@Slf4j
public class MQReceiver {

    @RabbitListener(queues = "queue_header01")
    public void receive07(Message message) {
        log.info("QUEUE01 接收消息 message 对象" + message);
        log.info("QUEUE01 接收消息" + new String(message.getBody()));   //将字节流转换成字符串文本
    }

    @RabbitListener(queues = "queue_header02")
    public void receive08(Message message) {
        log.info("QUEUE02 接收消息 message 对象" + message);
        log.info("QUEUE02 接收消息" + new String(message.getBody()));   //将字节流转换成字符串文本
    }

    //接收Topic 工作机制的消息
    @RabbitListener(queues = "queue_topic01")
    public void receive05(Object msg) {
        log.info("从 QUEUE1 接收消息:" + msg);
    }

    @RabbitListener(queues = "queue_topic02")
    public void receive06(Object msg) {
        log.info("从 QUEUE2 接收消息:" + msg);
    }

    @RabbitListener(queues = "queue")
    public void receive(Object msg) {
        log.info("接收到消息 --" + msg);
    }

    @RabbitListener(queues = "queue_fanout01")
    public void receive1(Object msg) {
        log.info("从 queue_fanout01 接收消息 - " + msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receive2(Object msg) {
        log.info("从 queue_fanout02 接收消息 - " + msg);
    }


    //Direct模式下的接收者队列
    @RabbitListener(queues = "queue_direct01")
    public void queue_direct01(Object msg) {
        log.info("从 queue_direct01 中接收消息 - " + msg);
    }

    @RabbitListener(queues = "queue_direct02")
    public void queue_direct02(Object msg) {
        log.info("从 queue_direct02 中接收消息 -" + msg);
    }
}
