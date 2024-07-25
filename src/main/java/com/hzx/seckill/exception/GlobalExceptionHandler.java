package com.hzx.seckill.exception;

import com.hzx.seckill.vo.RespBean;
import com.hzx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/24 16:57
 * @description: TODO
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //@ExceptionHandler 注解可以配置处理多个异常或者拦截全部异常
    //比如: @ExceptionHandler({ArithmeticException.class,NullPointerException.class})
    //返回类型可以是一个类也可以是一个视图地址
    @ExceptionHandler({Exception.class})
    public RespBean handleGlobalExceptions(Exception ex) {
        log.error("异常：{}", ex.getMessage());

        //1. 如果是全局异常
        if (ex instanceof GlobalException) {
            log.error("异常类型为: {}", (ex.getClass()));
            GlobalException globalException = (GlobalException) ex; //转换为运行类型
            return RespBean.error(globalException.getRespBeanEnum());   //基于GlobalException自带的RespBeanEnum 字段返回错误消息
        } else if (ex instanceof BindException) {
            //2. 如果是一个绑定异常(字段上添加的校验注解)
            log.error("异常类型为: {}", (ex.getClass()));
            BindException bindException = (BindException) ex;
            RespBean respBean = RespBean.error(RespBeanEnum.BING_ERROR);
            respBean.setMsg("参数校验异常 ~~ 异常参数异常为:" + bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        //默认返回服务器错误
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
