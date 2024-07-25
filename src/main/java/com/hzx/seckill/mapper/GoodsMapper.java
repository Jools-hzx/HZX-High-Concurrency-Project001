package com.hzx.seckill.mapper;

import com.hzx.seckill.pojo.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzx.seckill.vo.GoodsVo;

import java.util.List;

/**
* @author 10355
* @description 针对表【t_goods】的数据库操作Mapper
* @createDate 2024-07-25 20:08:50
* @Entity com.hzx.seckill.pojo.Goods
*/
public interface GoodsMapper extends BaseMapper<Goods> {

    //获取商品列表
    List<GoodsVo> listGoodsVo();

    //根据商品 Id 查询商品详情
    GoodsVo getGoodsVoByGoodsId(Integer goodsId);
}




