package com.hzx.seckill.controller;

import com.hzx.seckill.service.UserService;
import com.hzx.seckill.vo.LoginVo;
import com.hzx.seckill.vo.RespBean;
import com.hzx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 21:24
 * @description:
 */

@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    @Resource
    private UserService userService;

    @RequestMapping("/toLogin")
    public String login() {
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        log.info("loginVo:{}", loginVo);

        try {
            return userService.validLoginVo(loginVo, request, response);
        } catch (Exception e) {
            return RespBean.error(RespBeanEnum.ERROR);
        }
    }
}
