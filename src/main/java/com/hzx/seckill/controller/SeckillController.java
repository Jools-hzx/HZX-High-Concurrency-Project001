package com.hzx.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzx.seckill.config.AccessLimit;
import com.hzx.seckill.pojo.*;
import com.hzx.seckill.rabbitmq.SeckillMQSender;
import com.hzx.seckill.service.GoodsService;
import com.hzx.seckill.service.OrderService;
import com.hzx.seckill.service.SeckillGoodsService;
import com.hzx.seckill.service.SeckillOrderService;
import com.hzx.seckill.vo.GoodsVo;
import com.hzx.seckill.vo.RespBean;
import com.hzx.seckill.vo.RespBeanEnum;
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    //优化六: 该接口用于请求该用户用于秒杀的唯一路径
    //优化七: 携带用户输入的校验码，校验码校验通过之后才能完成秒杀操作
    //优化八: 使用拦截器完成限流规则机制
    @GetMapping("/path")
    @ResponseBody
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null) return RespBean.error(RespBeanEnum.SESSION_INVALID_ERROR);

        //优化七：引入验证码校验机制，防止脚本
        boolean captchaValid = seckillOrderService.checkCaptcha(user, goodsId.longValue(), captcha);
        log.info("接收到校验码:{}", captcha);
        if (!captchaValid) {
            return RespBean.error(RespBeanEnum.CAPTCHA_NO_VALID);
        }

        //优化八: 添加限流秒杀机制,统计单位时间内 5s 内访问该接口的频率
        /*
            String uri = request.getRequestURI();   //获取到请求的 uri
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get("uri:" + user.getId());
            if (null == count) {
                //设置第一次访问，并且 5s 之后该统计会消失
                valueOperations.set("uri:" + user.getId(), 1, 5, TimeUnit.SECONDS);
            } else {
                valueOperations.increment("uri:" + user.getId());
            }
            //如果短时间内超过 5 次，返回限流提示
            if (count > 5) return RespBean.error(RespBeanEnum.RATE_LIMIT_EXCEPTION);
         */

        //创建真正的地址
        String url = seckillOrderService.createPath(user, goodsId);
        return RespBean.success(url);
    }

    //优化六: 引入秒杀路径校验机制
    @RequestMapping("/{path}/doSeckill")
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path,
                              User user, Model model,
                              Integer goodsId) {

        //检查输入
        if (null == goodsId || goodsId < 1 || null == model) {
            return RespBean.error(RespBeanEnum.BING_ERROR);
//            return "goodsList";
        }

        //1.检查登录
        if (null == user) {
//            return "login";
            return RespBean.error(RespBeanEnum.SESSION_INVALID_ERROR);
        }

        //优化六: 引入秒杀路径校验机制
        boolean valid = seckillOrderService.checkPath(user, goodsId.longValue(), path);
        if (!valid) {
            //如果校验不成功，则返回非法请求信息
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //获取要进行秒杀抢购的商品信息
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        Integer remainStock = goodsVo.getStockCount();
        if (remainStock < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);
            return RespBean.error(RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);
//            return "secKillFail";
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
//            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_DUPLICATE_BUY);
//            return "secKillFail";
            return RespBean.error(RespBeanEnum.SECKILL_FAIL_DUPLICATE_BUY);
        }

        //优化三: 使用内存标记
        //如果该秒杀商品在内存内已经被标记为不可以秒杀
        if (!stockCountFlag.get(goodsVo.getId())) {
            log.info("内存标记检验 - goodsId:{}", goodsVo.getId());
            //直接返回
//            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);
//            return "secKillFail";
            return RespBean.error(RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);
        }

        //优化二：使用Redis完成预减
        //decrement 具有原子性
        //如果查询到存在该key，返回 value -1 后的值
        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsVo.getId());
        if (decrement < 0) {
            //将负数情况 + 1 置为 0 供下一次 decr 检查
            //返回库存不足秒杀失败消息
            redisTemplate.opsForValue().increment("seckillGoods:" + goodsVo.getId());
//            model.addAttribute("errmsg", RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);

            //如果内存已经探测到库存减为 0，更新内存标记
            stockCountFlag.put(Long.valueOf(goodsId), false);

            //返回秒杀失败界面
//            return "secKillFail";
            return RespBean.error(RespBeanEnum.SECKILL_FAIL_NOT_ENOUGH_STOCK);
        }

        //否则建立秒杀订单  --> 优化五: 引入消息队列，实现异步完成秒杀操作
        /*
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

        //优化五: 引入消息队列，实现异步完成秒杀操作
        //如果以上秒杀条件符合，构建秒杀请求消息，调用消息队列 Producer (Sender) 发送消息
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(goodsVo.getId());

        //调用 Hutool 工具类方法将 SeckillMessage 消息对象转换为字符串
        seckillMQSender.sendSecKillMsg(JSONUtil.toJsonStr(seckillMessage));

        //直接返回 "秒杀中...." 消息给前端 - 秒杀安全
        //model.addAttribute("errmsg", "秒杀排队中...");

        return RespBean.error(RespBeanEnum.SEC_KILL_WAIT);
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

    //为每个用户分配校验码
    @RequestMapping("/captcha")
    public void happyCahptcha(User user, Long goodsId,
                              HttpServletRequest request, HttpServletResponse response) {
        System.out.println("请求 goodsId:" + goodsId);
        if (null == user || goodsId < 1) return;
        HappyCaptcha.require(request, response)
                .style(CaptchaStyle.ANIM) //设置展现样式为动画
                .type(CaptchaType.NUMBER) //设置验证码内容为数字
                .length(6)          //设置字符长度为 6
                .width(220)         //设置动画宽度为 220
                .height(80)         //设置动画高度为 80
                .font(Fonts.getInstance().zhFont()) //设置汉字的字体
                .build().finish();  //生成并输出验证码

        //将生成的校验码存放到 Redis
        redisTemplate.opsForValue().set(
                "captcha:" + user.getId() + ":" + goodsId,
                (String) request.getSession().getAttribute("happy-captcha"),
                100,
                TimeUnit.SECONDS);  //设置验证码有效期为 100
    }


}
