package com.hzx.seckill.service;

import com.hzx.seckill.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzx.seckill.vo.GoodsVo;

import java.util.List;

/**
* @author Jools He
* @description 针对表【t_goods】的数据库操作Service
* @createDate 2024-07-25 20:08:50
*/
public interface GoodsService extends IService<Goods> {

    List<GoodsVo> listGoodsVo();

    GoodsVo getGoodsVoByGoodsId(Integer goodsId);
}
