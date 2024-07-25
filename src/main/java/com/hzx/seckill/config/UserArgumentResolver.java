package com.hzx.seckill.config;

import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.UserService;
import com.hzx.seckill.utils.CookieUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/25 16:06
 * @description: 针对用户类的参数解析器
 */

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Resource
    private UserService userService;

    //如果这个方法返回 true 才会执行下面的 resolveArgument 方法
    //返回 false 不执行下面的方
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //判断获取的参数类型
        Class<?> aClass = parameter.getParameterType();
        return aClass == User.class;
    }

    /**
     * 类似拦截器，将传入的参数;
     * 取出 cookie 值，然后获取对应的 User 对象
     * 并把这个 User 对象作为参数继续传递
     *
     * @param parameter     the method parameter to resolve. This parameter must
     *                      have previously been passed to {@link #supportsParameter} which must
     *                      have returned {@code true}.
     * @param mavContainer  the ModelAndViewContainer for the current request
     * @param webRequest    the current request
     * @param binderFactory a factory for creating {@link WebDataBinder} instances
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        //从当前 request 内取出 userTicketxq
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");

        //判断是否携带 ticket
        if (!StringUtils.hasText(userTicket)) return null;

        //基于该 userTicket 获取到 User 信息
        return userService.getUserByTicket(userTicket, request, response);
    }
}
