package com.hzx.seckill.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import static org.apache.commons.codec.digest.DigestUtils.md5;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 19:37
 * @description: MD5 密码加密加盐工具类
 */
public class MD5Utils {

    public static final String MD5_SALT = "P6q7R8a";
    public static final String FONT_END_SALT = "4tIY5VcX";

    /*
     * Calculates the MD2 digest and returns the value as a 32 character hex string.
        public static String md2Hex(final String data) {
            return Hex.encodeHexString(md2(data));
        }
     */
    public static String md5(String src) {
        return DigestUtils.md2Hex(src);
    }

    //将初始密码加盐使用MD5加密转换成中间码
    public static String inputPassToMid(String input) {
        //检查输入
        if (input.isEmpty()) throw new RuntimeException("Input password can not be empty");
        System.out.println("加盐 SALT-1:" + FONT_END_SALT.charAt(0));
        System.out.println("加盐 SALT-7:" + FONT_END_SALT.charAt(6));
        String mid = FONT_END_SALT.charAt(0) + input + FONT_END_SALT.charAt(6);
        return md5(mid);
    }

    //将中间码加盐使用MD5加密生成数据库入库密码
    public static String midToDbPassword(String mid, String salt) {
        if (mid.isEmpty()) throw new RuntimeException("Input mid can not be empty");
        System.out.println("加盐 SALT-2:" + MD5_SALT.charAt(0));
        System.out.println("加盐 SALT-5:" + MD5_SALT.charAt(4));
        String dbPwd = MD5_SALT.charAt(1) + mid + MD5_SALT.charAt(4);
        return md5(dbPwd);
    }

    //将初始字符串直接转换成数据库入库密码
    public static String inputStrToDbPassword(String src) {
        String mid = inputPassToMid(src);
        System.out.println("mid:" + mid);
        return midToDbPassword(mid, MD5Utils.MD5_SALT);
    }
}
