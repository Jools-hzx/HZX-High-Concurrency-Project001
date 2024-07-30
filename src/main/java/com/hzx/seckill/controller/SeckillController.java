package com.hzx.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzx.seckill.pojo.*;
import com.hzx.seckill.rabbitmq.SeckillMQSender;
import com.hzx.seckill.service.GoodsService;
import com.hzx.seckill.service.OrderService;
import com.hzx.seckill.service.SeckillGoodsService;
import com.hzx.seckill.service.SeckillOrderService;
import com.hzx.seckill.vo.GoodsVo;
import com.hzx.seckill.vo.RespBeanEnum;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/26 14:45
 * @description: 秒杀控制器
 */

@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Resource
    private GoodsService goodsService;

    @Resource
    private SeckillOrderService seckillOrderService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SeckillMQSender seckillMQSender;

    private Map<Long, Boolean> stockCountFlag = new HashMap<>();

    @Resource
    private SeckillGoodsService seckillGoodsService;

//    @RequestMapping("/doSeckill")
//    public String doSecKill(User user, Model model, Integer goodsId) {
//
//        //检查输入
//        if (null == goodsId || goodsId < 1 || null == model) {
//            return "goodsList";
//        }
//
//        //1.检查登录
//        if (null == user) {
//            return "login";
//        }
//
//        //检查库存
//        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
//
//        Integer remainStock = goodsVo.getStockCount();
//        if (remainStock < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);
//            return "secKillFail";
//        }
//
//        //检查是否发生重复购买 - 使用 Redis 进行优化
//        /*
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
//                                                        .eq("user_id", user.getId())
//                                                        .eq("goods_id", goodsId));
//        if (null != seckillOrder) {
//            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_DUPLICATE_BUY);
//            return "secKillFail";
//        }
//         */
//        Order order = null;
//
//        order = (Order) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
//        // 如果查询到该用户已经有该商品的秒杀订单，返回重复购买错误消息
//        if (null != order) {
//            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_DUPLICATE_BUY);
//            return "secKillFail";
//        }
//
//        //否则建立秒杀订单
//        order = orderService.saveSecKillOrder(user, goodsVo);
//        if (null == order) {
//            model.addAttribute("errmsg", RespBeanEnum.ERROR);
//            return "secKillFail";
//        }
//
//        //携带订单数据
//        model.addAttribute("order", order);
//        //携带商品数据
//        model.addAttribute("goods", goodsVo);
//
//        return "orderDetail";
//    }

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

        //获取要进行秒杀抢购的商品信息
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        Integer remainStock = goodsVo.getStockCount();
        if (remainStock < 1) {
            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);
            return "secKillFail";
        }

        //优化一: 检查是否发生重复购买;使用 Redis 进行优化
        /*
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                                                        .eq("user_id", user.getId())
                                                        .eq("goods_id", goodsId));
        if (null != seckillOrder) {
            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_DUPLICATE_BUY);
            return "secKillFail";
        }
         */
        SeckillOrder order = null;

        order = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        // 如果查询到该用户已经有该商品的秒杀订单，返回重复购买错误消息
        if (null != order) {
            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_DUPLICATE_BUY);
            return "secKillFail";
        }

        //优化三: 使用内存标记
        //如果该秒杀商品在内存内已经被标记为不可以秒杀
        if (!stockCountFlag.get(goodsVo.getId())) {
            log.info("内存标记检验 - goodsId:{}", goodsVo.getId());
            //直接返回
            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);
            return "secKillFail";
        }

        //优化二：使用Redis完成预减
        //decrement 具有原子性
        //如果查询到存在该key，返回 value -1 后的值
        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsVo.getId());
        if (decrement < 0) {
            //将负数情况 + 1 置为 0 供下一次 decr 检查
            //返回库存不足秒杀失败消息
            redisTemplate.opsForValue().increment("seckillGoods:" + goodsVo.getId());
            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);

            //如果内存已经探测到库存减为 0，更新内存标记
            stockCountFlag.put(Long.valueOf(goodsId), false);

            //返回秒杀失败界面
            return "secKillFail";
        }

        //否则建立秒杀订单
        /*
         优化四: 引入消息队列，实现异步完成秒杀操作
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
         */

        //优化四: 引入消息队列，实现异步完成秒杀操作
        //如果以上秒杀条件符合，构建秒杀请求消息，调用消息队列 Producer (Sender) 发送消息
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(goodsVo.getId());

        //调用 Hutool 工具类方法将 SeckillMessage 消息对象转换为字符串
        seckillMQSender.sendSecKillMsg(JSONUtil.toJsonStr(seckillMessage));

        //直接返回 "秒杀中...." 消息给前端
        model.addAttribute("errmsg", "秒杀排队中...");

        return "secKillFail";
    }

    @RequestMapping("/checkSeckill/{goodsId}")
    public String checkSeckillResult(User user, Model model,
                                     @PathVariable(value = "goodsId") Long goodsId) {
        //检查输入
        if (null == user || goodsId < 1) {
            model.addAttribute("errmsg", RespBeanEnum.BING_ERROR);
        }

        Long userId = user.getId();
        SeckillGoods seckillGoods = seckillGoodsService.getBaseMapper().selectOne(
                new QueryWrapper<SeckillGoods>()
                        .eq("goods_id", goodsId)
        );
        Long seckillGoodsId = seckillGoods.getGoodsId();

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //拼接待查询的 Redis key:
        String key = new StringBuilder()
                .append("seckillFail:")
                .append(userId + ":")
                .append(seckillGoodsId)
                .toString();
        Object o = valueOperations.get(key);
        if (null != o) {
            //如果存在秒杀订单失败的消息，直接判断秒杀失败:
            model.addAttribute("errmsg", "秒杀结果: 失败！由于未知原因，请再次尝试!");
            return "secKillFail";
        }

        String seckillOrderKey = "order:" + user.getId() + ":" + goodsId;
        SeckillOrder order = (SeckillOrder) valueOperations.get(seckillOrderKey);
        if (null != order) {
            //如果存在秒杀订单成功的消息，返回成功
            //简化 ---> 统一返回到 secKillFail 页面处理
            model.addAttribute("errmsg", "秒杀结果: 秒杀成功！订单结果已经建立");
            return "secKillFail";
        }

        //否则说明还在尝试完成秒杀流程
        model.addAttribute("errmsg", "秒杀排队中....");
        return "secKillFail";
    }

    //优化三: 将当前秒杀商品的库存保存到 Redis 内,使用 Redis 完成预减库存操作
    @Override
    public void afterPropertiesSet() throws Exception {

        //在 Bean 初始化完成之后查询数据库，获取所有的秒杀商品库存
        //将库存保存在内存内
        List<SeckillGoods> seckillGoodsList = seckillGoodsService.list();

        if (CollectionUtils.isEmpty(seckillGoodsList)) {
            //如果不存在秒杀商品，直接返回
            return;
        }

        //优化二: Redis预减库存
        //加载初始库存进内存内
        //使用按照 key: seckillGoods:{商品id}  value: 剩余库存存放
        seckillGoodsList.forEach(
                (goods) -> {
                    redisTemplate.opsForValue()
                            .set("seckillGoods:" + goods.getGoodsId(),
                                    goods.getStockCount());

                    //优化四: 使用内存标记
                    //在内存读入所有商品库存之后标记为可以秒杀
                    //内存标记所有可秒杀的商品 id 为 true
                    stockCountFlag.put(goods.getGoodsId(), true);
                }
        );
    }
}
