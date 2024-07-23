package com.hzx.seckill.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 20:43
 * @description: 登录信息校验工具类
 */
public class ValidatorUtils {

    //针对手机号的校验规则
    private static final String MOBILE_PATTERN = "^1[3-9]\\d{9}$";

    //验证手机号
    public static boolean isMobile(String mobile) {
        //Pattern 传入正则表达式字符串
        Pattern pattern = Pattern.compile(MOBILE_PATTERN);
        //Matcher 传入手机号返回 Matcher 对象
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }
}
