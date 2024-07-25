package com.hzx.seckill.controller;

import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    private UserService userService;

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

        //3.跳转到商品页面
        return "goodsList";
    }
}
