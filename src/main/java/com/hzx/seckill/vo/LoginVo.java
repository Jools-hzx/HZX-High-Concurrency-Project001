package com.hzx.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 20:42
 * @description: 封装登录信息类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {

    private String mobile;      //手机号
    private String password;    //密码
}
