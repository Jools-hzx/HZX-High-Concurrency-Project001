package com.hzx.seckill.controller;

import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.GoodsService;
import com.hzx.seckill.service.UserService;
import com.hzx.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/24 22:08
 * @description: TODO
 */
@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private UserService userService;    //封装到 HandlerMethodArgumentResolver 类管理
    @Resource
    private GoodsService goodsService;

    @RequestMapping("/toDetail/{goodsId}")      //uri 依据前端界面的配置
    public String toDetail(@PathVariable(value = "goodsId") Integer goodsId,
                           User user,
                           Model model) {
        //如果用户未登录返回登录界面
        if (null == user) {
            return "login";
        }
        log.info("用户:{} - 访问商品 {} 详情页面", user.getNickname(), goodsId);

        //基于Id查询商品详情
        GoodsVo goodsVo = null;
        try {
            goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        } catch (Exception e) {
            log.error("Error occurred during getGoodsVoByGoodsId() process");
            return "goodsList";
        }


        int secKillStatus = 0;  //记录秒杀是否开始，默认为 0 表示未开始
        long remainSeconds = 0;  //记录秒杀开始的剩余时间
        Date endDate = goodsVo.getEndDate();
        Date startDate = goodsVo.getStartDate();
        Date now = new Date();

        if (startDate.after(now)) {  //如果未开始，状态设置为 0
            //计算距离秒杀开始时间的期限; 将 ms 转换成 s
            remainSeconds = (startDate.getTime() - now.getTime()) / 1000;
        } else if (startDate.before(now) && endDate.after(now)) {
            secKillStatus = 1;  //如果秒杀正在开始，则设置为 1
        } else if (endDate.before(now)) {
            secKillStatus = 2;  //如果秒杀已经结束，设置为 2
            remainSeconds = -1;
        }

        log.info("秒杀状态：{}, 剩余时间: {}", secKillStatus, remainSeconds);

        //将查询到的商品信息通过 model 携带到商品详情界面
        //attributeName 依据前端界面设置编写
        model.addAttribute("goods", goodsVo);
        model.addAttribute("user", user);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "goodsDetail";
    }

    //跳转到商品列表界面
    @RequestMapping("/list")
    public String toGoodsList(User user, Model model/*, @CookieValue(value = "userTicket") String userTicket*/) {
//        //1.判断是否携带 Cookie
//        if (!StringUtils.hasText(userTicket)) {  //如果用户未登录，返回登录
//            return "login";
//        }
//        //2.判断是否能够获取到登录的 User
//
//        /*User user = (User) request.getSession().getAttribute(ticket);*/
//        //从 Redis 存储的数据中获取
//        log.info("请求查询携带的 userTicket:{}", userTicket);
//        User user = userService.getUserByTicket(userTicket, request, response);

        //使用 WebMvcConfigurer 简化上述逻辑

        if (null == user) {     //如果未获取到登录用户，返回登录
            return "login";
        }
        log.info("刷新登录用户携带userTicket的Cookie时间, user:{}", user.getNickname());

        //将 User 信息携带到商品列表页面
        model.addAttribute("user", user);

        //将商品信息携带到商品列表页面
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if (goodsVos.isEmpty()) {
            log.warn("请求到的商品数据为空！！");
        }

        model.addAttribute("goodsList", goodsVos);

        //3.跳转到商品页面
        return "goodsList";
    }
}
