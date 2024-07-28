package com.hzx.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/28 22:42
 * @description: TODO
 */
@Service
@Slf4j
public class SeckillMQSender {

    @Resource
    private RabbitTemplate rabbitTemplate;

    //基于 Topic 工作机制的发送者
    //目标队列的 routingKey: seckill.*
    public void sendSecKillMsg(String msg) {
        log.info("SeckillSender - 发送一个秒杀消息:{}", msg);
        rabbitTemplate.convertAndSend("topicSeckillExchange", "seckill.message", msg);
    }

}
