package com.hzx.seckill.utils;

import java.util.UUID;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/24 21:46
 * @description: TODO
 */
public class UUIDUtils {

    public static String getUUIDTicket() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
