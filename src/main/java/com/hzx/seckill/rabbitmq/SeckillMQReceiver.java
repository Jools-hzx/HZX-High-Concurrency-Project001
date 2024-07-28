package com.hzx.seckill.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.hzx.seckill.pojo.SeckillMessage;
import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.GoodsService;
import com.hzx.seckill.service.OrderService;
import com.hzx.seckill.vo.GoodsVo;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/28 22:46
 * @description: TODO
 */
@Service
@Slf4j
public class SeckillMQReceiver {

    @Resource
    private GoodsService goodsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private OrderService orderService;

    @RabbitListener(queues = "queue_seckill")
    public void queue(String message) {
        log.info("接收到的消息:{}", message);

        SeckillMessage seckillMessage = JSONUtil.toBean(message, SeckillMessage.class);
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();

        //获取当前秒杀商品的信息
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId.intValue());

        //执行下单操作
        orderService.saveSecKillOrder(user, goodsVo);
    }
}
