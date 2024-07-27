package com.hzx.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzx.seckill.pojo.Order;
import com.hzx.seckill.pojo.SeckillGoods;
import com.hzx.seckill.pojo.SeckillOrder;
import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.GoodsService;
import com.hzx.seckill.service.OrderService;
import com.hzx.seckill.service.SeckillGoodsService;
import com.hzx.seckill.service.SeckillOrderService;
import com.hzx.seckill.vo.GoodsVo;
import com.hzx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/26 14:45
 * @description: 秒杀控制器
 */

@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillController {

    @Resource
    private GoodsService goodsService;

    @Resource
    private SeckillOrderService seckillOrderService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate redisTemplate;

    @RequestMapping("/doSeckill")
    public String doSecKill(User user, Model model, Integer goodsId) {

        //检查输入
        if (null == goodsId || goodsId < 1 || null == model) {
            return "goodsList";
        }

        //1.检查登录
        if (null == user) {
            return "login";
        }

        //检查库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        Integer remainStock = goodsVo.getStockCount();
        if (remainStock < 1) {
            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);
            return "secKillFail";
        }

        //检查是否发生重复购买 - 使用 Redis 进行优化
        /*
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                                                        .eq("user_id", user.getId())
                                                        .eq("goods_id", goodsId));
        if (null != seckillOrder) {
            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_DUPLICATE_BUY);
            return "secKillFail";
        }
         */
        Order order = null;

        order = (Order) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        // 如果查询到该用户已经有该商品的秒杀订单，返回重复购买错误消息
        if (null != order) {
            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_DUPLICATE_BUY);
            return "secKillFail";
        }

        //否则建立秒杀订单
        order = orderService.saveSecKillOrder(user, goodsVo);
        if (null == order) {
            model.addAttribute("errmsg", RespBeanEnum.ERROR);
            return "secKillFail";
        }

        //携带订单数据
        model.addAttribute("order", order);
        //携带商品数据
        model.addAttribute("goods", goodsVo);

        return "orderDetail";
    }

}
