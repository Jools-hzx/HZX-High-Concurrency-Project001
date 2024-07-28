package com.hzx.seckill.controller;

import com.hzx.seckill.rabbitmq.MQSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/27 22:27
 * @description: 配置 RabbitMQ 的控制器
 */

@Controller
public class RabbitMQHandler {


    @Resource
    private MQSender mqSender;

    @GetMapping("/mq/header01")
    @ResponseBody
    public void header01() {
        mqSender.sendHeaders01("Hello ABC");
    }

    @GetMapping("/mq/header02")
    @ResponseBody
    public void header02() {
        mqSender.sendHeaders02("Hello Jools He!!!");
    }

    //测试Topic工作模式，发送请求
    @GetMapping("/mq/topic01")
    @ResponseBody
    public void mqTopic01() {
        mqSender.sendTopic01("Hello Red!!!!");
    }

    @GetMapping("/mq/topic02")
    @ResponseBody
    public void mqTopic02() {
        mqSender.sendTopic02("Hello Green!!!!");
    }

    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSender.send("hello");
    }

    @GetMapping("/mq/fanout")
    @ResponseBody
    public void fanout() {
        mqSender.sendFanout("hello~");
    }

    @GetMapping("/mq/direct01")
    @ResponseBody
    public void direct01() {
        mqSender.sendDirect01("hello:red");
    }

    @GetMapping("/mq/direct02")
    @ResponseBody
    public void direct02() {
        mqSender.sendDirect02("hello:green");
    }
}
