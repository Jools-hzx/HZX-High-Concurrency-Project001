package com.hzx.seckill.utils;

import org.junit.jupiter.api.Test;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/24 21:47
 * @description: 测试 UUID 工具类
 */
public class UUIDUtilsTest {

    @Test
    public void testUUIDGenerated() {
        for (int i = 0; i < 5; i++) {
            System.out.println(UUIDUtils.getUUIDTicket());
        }
    }
}
