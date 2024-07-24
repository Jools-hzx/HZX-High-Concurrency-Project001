package com.hzx.seckill.vo;

import com.hzx.seckill.validate.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

    @NotNull
    @IsMobile
    private String mobile;      //手机号

    @NotNull
    @Length(min = 32)   //校验密码最小长度为 32
    private String password;    //密码
}
