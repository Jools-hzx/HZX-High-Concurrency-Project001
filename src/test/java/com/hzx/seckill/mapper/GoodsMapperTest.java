package com.hzx.seckill.mapper;

import com.hzx.seckill.vo.GoodsVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/25 21:49
 * @description: TODO
 */
@SpringBootTest
public class GoodsMapperTest {

    @Resource
    private GoodsMapper goodsMapper;

    @Test
    public void testGetGoodsVoById() {
        GoodsVo goodsVo = goodsMapper.getGoodsVoByGoodsId(1);
        System.out.println(goodsVo);
    }
}
