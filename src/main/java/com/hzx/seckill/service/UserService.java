package com.hzx.seckill.service;

import com.hzx.seckill.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzx.seckill.vo.LoginVo;
import com.hzx.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jools He
 * @description 针对表【seckill_user】的数据库操作Service
 * @createDate 2024-07-23 20:07:43
 */
public interface UserService extends IService<User> {

    /*
     验证登录信息是否合法
     */
    RespBean validLoginVo(LoginVo loginVo,
                          HttpServletRequest httpServletRequest,
                          HttpServletResponse httpServletResponse);


}
