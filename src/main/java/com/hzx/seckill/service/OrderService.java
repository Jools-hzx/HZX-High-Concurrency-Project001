package com.hzx.seckill.service;

import com.hzx.seckill.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzx.seckill.pojo.User;
import com.hzx.seckill.vo.GoodsVo;

/**
* @author 10355
* @description 针对表【t_order】的数据库操作Service
* @createDate 2024-07-26 13:02:04
*/
public interface OrderService extends IService<Order> {

    Order saveSecKillOrder(User user, GoodsVo goodsVo);
}
