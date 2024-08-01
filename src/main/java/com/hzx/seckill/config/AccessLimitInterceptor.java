package com.hzx.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.UserService;
import com.hzx.seckill.utils.CookieUtil;
import com.hzx.seckill.vo.RespBean;
import com.hzx.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.yaml.snakeyaml.events.Event;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/8/1 15:36
 * @description: TODO
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取目标方法的注解 AccessLimit 进行解析
        if (handler instanceof HandlerMethod) {
            User user = getUserByTicket(request, response);
            //将该已经登录的用户信息存放在 threadLocal 内
            UserContext.set(user);
            //解析当前方法是否携带限流注解
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            //如果不是限流的接口，直接放行
            if (null == accessLimit) return true;

            //否则进行限流
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean neededLogin = accessLimit.needLogin();

            //如果请求接口需要登录之后才能访问并且没有获取到已经登录的用户信息
            if (neededLogin) {
                if (null == user) {
                    //返回未登录提示信息
                    render(response, RespBeanEnum.SESSION_INVALID_ERROR);
                    return false;   //拦截成功返回
                }
            }

            //否则基于限流注解执行限流机制
            String uri = request.getRequestURI();
            //获取到请求的 uri
            ValueOperations valueOperations = redisTemplate.opsForValue();
            //获取到当前秒杀接口的请求次数统计
            String seckillKey = "uri:" + user.getId();
            Integer count = (Integer) valueOperations.get(seckillKey);
            if (null == count) {
                //如果是第一次访问，初始化访问统计并且设置过期时间为注解内配置的 second
                valueOperations.set(seckillKey, 1, second, TimeUnit.SECONDS);
            } else if (count < maxCount) {
                valueOperations.increment(seckillKey);
            } else {
                //如果短时间内超过 5 次，返回限流提示
                //返回错误的信息
                render(response, RespBeanEnum.RATE_LIMIT_EXCEPTION);
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    //构建返回对象
    private void render(HttpServletResponse response, RespBeanEnum sessionError) {
        System.out.println("render - " + sessionError.getMessage());
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            PrintWriter out = response.getWriter();
            RespBean error = RespBean.error(sessionError);
            out.write(new ObjectMapper().writeValueAsString(error));
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //基于当前会话获取登录用户的信息
    private User getUserByTicket(HttpServletRequest request, HttpServletResponse response) {
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        return userService.getUserByTicket(userTicket, request, response);
    }
}
