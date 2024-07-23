package com.hzx.seckill.controller;

import com.hzx.seckill.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 21:24
 * @description:
 */

@Controller
@RequestMapping("/login")
public class LoginController {

    @Resource
    private UserService userService;

    @RequestMapping("/toLogin")
    public String login() {
        return "login";
    }
}
