package com.hzx.seckill.controller;

import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.UserService;
import com.hzx.seckill.vo.RespBean;
import com.hzx.seckill.vo.RespBeanEnum;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/26 18:38
 * @description: TODO
 */

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user, String address) {
        System.out.println("携带的 address 信息为:" + address);
        return RespBean.success(user);
    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public RespBean updatePwd(String userTicket, String newPwd,
                              HttpServletRequest request, HttpServletResponse response) {
        try {
            return userService.changeUserPwd(newPwd, userTicket, request, response);
        } catch (Exception e) {
            return RespBean.error(RespBeanEnum.UPDATE_PWD_FAIL_ERROR);
        }
    }
}
