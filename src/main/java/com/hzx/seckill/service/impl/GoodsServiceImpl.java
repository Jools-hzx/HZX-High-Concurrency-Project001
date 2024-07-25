package com.hzx.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzx.seckill.pojo.Goods;
import com.hzx.seckill.service.GoodsService;
import com.hzx.seckill.mapper.GoodsMapper;
import com.hzx.seckill.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jools He
 * @description 针对表【t_goods】的数据库操作Service实现
 * @createDate 2024-07-25 20:08:50
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
        implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(Integer goodsId) {
        if (null == goodsId || goodsId < 0) return null;
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }
}




