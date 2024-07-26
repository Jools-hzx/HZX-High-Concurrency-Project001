package com.hzx.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzx.seckill.exception.GlobalException;
import com.hzx.seckill.mapper.SeckillGoodsMapper;
import com.hzx.seckill.mapper.SeckillOrderMapper;
import com.hzx.seckill.pojo.Order;
import com.hzx.seckill.pojo.SeckillGoods;
import com.hzx.seckill.pojo.SeckillOrder;
import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.GoodsService;
import com.hzx.seckill.service.OrderService;
import com.hzx.seckill.mapper.OrderMapper;
import com.hzx.seckill.service.SeckillOrderService;
import com.hzx.seckill.vo.GoodsVo;
import com.hzx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 10355
 * @description 针对表【t_order】的数据库操作Service实现
 * @createDate 2024-07-26 13:02:04
 */

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {
    @Resource
    private OrderService orderService;
    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;
    @Resource
    private SeckillOrderService seckillOrderService;

    @Override
    public Order saveSecKillOrder(User user, GoodsVo goodsVo) {
        /*
        ● 查询秒杀商品的库存，检验库存大于0
        ● 基于 goods_id 查询该秒杀商品
        ● 完成基本秒杀操作，更新库存 - 1
        ● 生成一个普通订单
        ● 保存普通订单
        ● 生成一个秒杀订单
        ● 保存秒杀订单
         */
        Order order = null;
        try {
            //通过GoodsVo获取秒杀商品的信息
            Long goodsId = goodsVo.getId();

            //只要进入到该方法默认库存 > 0

            //1.基于当前 GoodsVo Id 查询该参与秒杀商品的信息
            SeckillGoods skGoods = seckillGoodsMapper.selectOne(
                    new QueryWrapper<SeckillGoods>()
                            .eq("goods_id", goodsId)
            );
            Integer goodsStockCount = skGoods.getStockCount();

            //基于秒杀商品的信息封装成一个商品订单信息
            order = new Order();
            order.setCreateDate(new Date());
            order.setOrderChannel(0);                   //默认为 PC
            order.setStatus(0);                         //默认起始状态为新建未支付
            order.setGoodsPrice(skGoods.getSeckillPrice());
            order.setUserId(user.getId());
            order.setDeliveryAddrId(0L);                //默认0
            order.setGoodsName(goodsVo.getGoodsName()); //填充商品名称
            order.setGoodsId(skGoods.getGoodsId());       //填充商品信息，已秒杀商品记录的 GoodsId 字段填充
            order.setGoodsCount(1);                     //默认仅能秒杀一件

            //保存订单
            orderService.save(order);

            //更新商品库存 - 1
            skGoods.setStockCount(goodsStockCount - 1);
            seckillGoodsMapper.update(skGoods,
                    new QueryWrapper<SeckillGoods>()
                            .eq("goods_id", skGoods.getGoodsId()));

            //生成一个秒杀订单
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setOrderId(order.getId()); //数据库自动生成一个id
            seckillOrder.setUserId(user.getId());
            seckillOrder.setGoodsId(skGoods.getGoodsId());

            //保存秒杀订单
            seckillOrderService.save(seckillOrder);

        } catch (Exception e) {
            log.info("生成秒杀订单是发生错误: user:{} goodsId:{}", user.getId(), goodsVo.getId());
            throw new GlobalException(RespBeanEnum.ERROR);
        }
        return order;
    }
}




