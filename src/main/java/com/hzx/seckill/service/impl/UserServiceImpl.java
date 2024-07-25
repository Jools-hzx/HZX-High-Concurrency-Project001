package com.hzx.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzx.seckill.exception.GlobalException;
import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.UserService;
import com.hzx.seckill.mapper.UserMapper;
import com.hzx.seckill.utils.CookieUtil;
import com.hzx.seckill.utils.MD5Utils;
import com.hzx.seckill.utils.UUIDUtils;
import com.hzx.seckill.utils.ValidatorUtils;
import com.hzx.seckill.vo.LoginVo;
import com.hzx.seckill.vo.RespBean;
import com.hzx.seckill.vo.RespBeanEnum;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Jools He
 * @description 针对表【seckill_user】的数据库操作Service实现
 * @createDate 2024-07-23 20:07:43
 */
@Service
@Slf4j
public class UserServiceImpl
        extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public RespBean validLoginVo(LoginVo loginVo, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        //1.校验信息不能为空
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //校验登录信息不能为空
//        if (!StringUtils.hasText(mobile)
//                || !StringUtils.hasText(password)) {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//
//        //校验手机
//        if (!ValidatorUtils.isMobile(mobile)) {
//            return RespBean.error(RespBeanEnum.PHONE_NUMBER_ERROR);
//        }

        //根据手机号码从数据库内查询该用户
        User user = userMapper.selectById(mobile);
        if (null == user) {
            log.error("User not found, throwing GlobalException");
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST_ERROR);
//            return RespBean.error(RespBeanEnum.MOBILE_NOT_EXIST_ERROR);   //抛出异常让全局处理
        }

        //基于LoginVo 传入的 password 加盐 MD5 加密之后和数据库内的密码进行匹配
        if (!MD5Utils.midToDbPassword(password, user.getSlat()).equals(user.getPassword())) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        //随机生成ticket
        String ticket = UUIDUtils.getUUIDTicket();

        /*
            HttpSession session = httpServletRequest.getSession();
            基于 ticket 可以获取到当前登录的 user 信息
            session.setAttribute(ticket, user);
         */

        //使用 user:ticket字符串的格式存储到 Redis
        redisTemplate.opsForValue().set("user:" + ticket, user);

        //登陆成功，返回携带着 ticket 的 cookie
        CookieUtil.setCookie(
                httpServletRequest, httpServletResponse,
                "userTicket", ticket);

        return RespBean.success(RespBeanEnum.SUCCESS);
    }

    @Override
    public User getUserByTicket(String userTicket, HttpServletRequest request, HttpServletResponse response) {

        if (!StringUtils.hasText(userTicket) || null == userTicket) return null;
        User user = null;
        try {
            user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
            //更新 Cookie 内 userTicket 的持续时间
            CookieUtil.setCookie(request, response,"userTicket", userTicket);
        } catch (Exception e) {
            return null;
        }
        return user;
    }
}




