package com.hzx.seckill.utils;

import org.junit.jupiter.api.Test;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 19:47
 * @description: TODO
 */

public class MD5UtilsTest {

    @Test
    public void testMD5MidPwd() {
        String mid = "cb063decabc24b95d7023e7914138d81";
        String dbPassword = MD5Utils.midToDbPassword(mid, MD5Utils.MD5_SALT);
        System.out.println(dbPassword);
    }

    @Test
    public void testInitToPwd() {
        String init = "123";
        String dbPwd = MD5Utils.inputStrToDbPassword(init);
        System.out.println(dbPwd);
    }

    @Test
    public void testValidPwd() {
        String dbPassword = MD5Utils.midToDbPassword("75d7eacdc4a31d0c8c939112ae5880cf", MD5Utils.MD5_SALT);
        System.out.println(dbPassword);
    }

    @Test
    public void t1() {

        //1. 12345 就是用户输入的密码, inputPassToMidPass() 返回的是中间密码
        //  中间密码是为了增强安全性，防止传输的密码被网络拦截设计的
        String midStr = MD5Utils.inputPassToMid("123456");
        System.out.println(midStr);

        //2. 中间密码也是前端经过 md5() 设计之后得到的，并通过网络发送给服务器的
        //3. 也就是说，我们发送给服务端其后端的密码是先经过加密的
        String dbPassword = MD5Utils.midToDbPassword(midStr, MD5Utils.MD5_SALT);
        System.out.println(dbPassword);


        String testDbPassword = MD5Utils.inputStrToDbPassword("123456");
        System.out.println(testDbPassword);
        System.out.println(testDbPassword.equals(dbPassword));
    }
}
