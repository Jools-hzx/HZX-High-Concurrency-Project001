package com.hzx.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.UserService;
import com.hzx.seckill.mapper.UserMapper;
import com.hzx.seckill.utils.MD5Utils;
import com.hzx.seckill.utils.ValidatorUtils;
import com.hzx.seckill.vo.LoginVo;
import com.hzx.seckill.vo.RespBean;
import com.hzx.seckill.vo.RespBeanEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Jools He
 * @description 针对表【seckill_user】的数据库操作Service实现
 * @createDate 2024-07-23 20:07:43
 */
@Service
public class UserServiceImpl
        extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;


    @Override
    public RespBean validLoginVo(LoginVo loginVo, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        //1.校验信息不能为空
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //校验登录信息不能为空
        if (!StringUtils.hasText(mobile)
                || !StringUtils.hasText(password)) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        //校验手机
        if (!ValidatorUtils.isMobile(mobile)) {
            return RespBean.error(RespBeanEnum.PHONE_NUMBER_ERROR);
        }

        //根据手机号码从数据库内查询该用户
        User user = userMapper.selectById(mobile);
        if (user == null) return RespBean.error(RespBeanEnum.LOGIN_ERROR);

        //基于LoginVo 传入的 password 加盐 MD5 加密之后和数据库内的密码进行匹配
        if (!MD5Utils.midToDbPassword(password, user.getSlat()).equals(user.getPassword())) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        return RespBean.success();
    }
}




