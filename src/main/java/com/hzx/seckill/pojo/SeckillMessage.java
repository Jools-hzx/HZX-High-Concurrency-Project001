package com.hzx.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/28 22:32
 * @description: 秒杀操作消息，持有秒杀商品Id和进行秒杀的用户
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class SeckillMessage {

    private Long goodsId;
    private User user;
}
