package com.hzx.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzx.seckill.pojo.SeckillOrder;
import com.hzx.seckill.pojo.User;
import com.hzx.seckill.service.SeckillOrderService;
import com.hzx.seckill.mapper.SeckillOrderMapper;
import com.hzx.seckill.utils.MD5Utils;
import com.hzx.seckill.utils.UUIDUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 10355
 * @description 针对表【t_seckill_order】的数据库操作Service实现
 * @createDate 2024-07-26 13:02:30
 */
@Service
public class SeckillOrderServiceImpl
        extends ServiceImpl<SeckillOrderMapper, SeckillOrder>
        implements SeckillOrderService {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Utils.md5(UUIDUtils.getUUIDTicket());   //随机生成路径，也可以加盐
        //将随机生成的一个秒杀会话路径存放到内存 Redis 内
        //设置 redis 内存记录，并且设置过期时间为 60s
        redisTemplate.opsForValue().
                set("seckillPath:" + user.getId() + ":" + goodsId,
                        str,
                        60,
                        TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {

        //检查输入
        if (null == user || goodsId < 0 || !StringUtils.hasText(path)) return false;

        //获取到该用户对应的唯一合法秒杀路径
        String str = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);

        return path.equals(str);
    }

    //校验用户输入的二维码信息是否正确
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {

        //检查输入
        if (user == null || goodsId < 0 || !StringUtils.hasText(captcha)) return false;

        //从 Redis 内取出验证码并且进行校验
        String saveCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return saveCaptcha.equals(captcha);
    }
}




