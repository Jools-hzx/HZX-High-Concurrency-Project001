package com.hzx.seckill.config;

import com.hzx.seckill.pojo.User;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/8/1 15:23
 * @description: TODO
 */
public class UserContext {

    private static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    //添加信息
    public static void set(User user) {
        userHolder.set(user);
    }

    //获取存放的 User
    public static User getUser() {
        return userHolder.get();
    }
}
