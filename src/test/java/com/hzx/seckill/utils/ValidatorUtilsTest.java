package com.hzx.seckill.utils;

import org.junit.jupiter.api.Test;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 20:46
 * @description: 验证数据工具类校验测试类
 */
public class ValidatorUtilsTest {

    @Test
    public void testMobile() {
        String[] phoneNumbers = {
                "13812345678", // 有效手机号
                "12345678901", // 无效手机号
                "19987654321", // 有效手机号
                "1801234567a"  // 无效手机号
        };

        for (String phoneNumber : phoneNumbers) {
            System.out.println(phoneNumber + " is valid: " + ValidatorUtils.isMobile(phoneNumber));
        }
    }
}
