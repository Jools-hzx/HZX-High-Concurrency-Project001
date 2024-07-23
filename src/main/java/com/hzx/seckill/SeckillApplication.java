package com.hzx.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 18:54
 * @description: TODO
 */
@SpringBootApplication
@MapperScan("com.hzx.seckill.mapper")
public class SeckillApplication {

    public static void main(String[] args) {

        SpringApplication.run(SeckillApplication.class, args);
    }
}
