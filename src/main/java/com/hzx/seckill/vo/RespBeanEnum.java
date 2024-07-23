package com.hzx.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 20:23
 * @description: 响应信息枚举类
 */

@AllArgsConstructor
@Getter
@ToString
public enum RespBeanEnum {

    //通用美剧
    SUCCESS(200, "success"),
    ERROR(500, "server error"),
    //登录模块校验信息
    LOGIN_ERROR(50210, "用户名或者密码错误"),
    PHONE_NUMBER_ERROR(50211, "手机号码格式不正确"),
    BING_ERROR(50212, "参数绑定异常"),
    MOBILE_NOT_EXIST_ERROR(50213, "手机号码不存在"),
    UPDATE_PWD_FAIL_ERROR(50214, "更新密码失败");

    private final Integer code;
    private final String message;

}
