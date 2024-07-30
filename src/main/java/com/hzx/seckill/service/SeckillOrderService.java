package com.hzx.seckill.service;

import com.hzx.seckill.pojo.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzx.seckill.pojo.User;

/**
 * @author 10355
 * @description 针对表【t_seckill_order】的数据库操作Service
 * @createDate 2024-07-26 13:02:30
 */
public interface SeckillOrderService extends IService<SeckillOrder> {

    //为每一个用户生成一个唯一的秒杀路径
    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
