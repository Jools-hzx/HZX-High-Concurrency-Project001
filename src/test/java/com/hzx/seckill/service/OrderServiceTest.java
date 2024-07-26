package com.hzx.seckill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.BeanProperty;
import com.hzx.seckill.pojo.Goods;
import com.hzx.seckill.pojo.Order;
import com.hzx.seckill.pojo.SeckillOrder;
import com.hzx.seckill.pojo.User;
import com.hzx.seckill.vo.GoodsVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigInteger;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/26 13:35
 * @description: TODO
 */

@SpringBootTest
public class OrderServiceTest {

    private static Long goodsId;
    private static Long userId;

    @Resource
    private OrderService orderService;
    @Resource
    private UserService userService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private SeckillOrderService seckillOrderService;

    @BeforeEach
    public void init() {
        userId = 13000000000L;
        goodsId = 1L;

        Assertions.assertNotNull(goodsService.getGoodsVoByGoodsId(goodsId.intValue()));
        Assertions.assertNotNull(userService.getById(userId));
    }

    @Test
    public void saveSecKillOrderTest() {

        //Prepare User and GoodsVo for seconds kill
        User user = userService.getById(userId);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId.intValue());

        //Save seconds kill order and order
        Order actual = orderService.saveSecKillOrder(user, goodsVo);
        Assertions.assertNotNull(actual);

        Long orderId = actual.getId();

        //Check if order is saved successfully!
        Order orderById = orderService.getById(orderId);
        SeckillOrder seckillOrder = seckillOrderService.getOne(
                new QueryWrapper<SeckillOrder>().eq("order_id", orderId)
        );

        Assertions.assertNotNull(orderById);
        Assertions.assertNotNull(seckillOrder);
    }
}
