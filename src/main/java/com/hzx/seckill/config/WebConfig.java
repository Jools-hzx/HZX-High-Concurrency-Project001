package com.hzx.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/25 16:14
 * @description: 1. @EnableWebMvc 是一个注解，用于启用 Spring MVC 的配置，
 * 相当于在 XML 配置中使用 <mvc:annotation-driven />。
 * 它会导入 DelegatingWebMvcConfiguration，从而启用 Spring MVC 的默认配置。
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private UserArgumentResolver userArgumentResolver;

    @Resource
    private AccessLimitInterceptor accessLimitInterceptor;

    //注册拦截器，实现限流规则和登录校验
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);
    }

    //静态资源加载
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/**")
                .addResourceLocations(("classpath:/static/"));
    }

    //将我们自定义的 UserArgumentResolver 解析器加载到 HandlerMethodArgumentResolver 列表内
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }
}
