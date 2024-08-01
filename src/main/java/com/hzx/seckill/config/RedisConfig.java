package com.hzx.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/25 14:36
 * @description: 把 Session 信息提取出来存放到 redis
 * 主要实现序列化，这里是以常规操作
 */

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //设置相应 key 的序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //value 序列化
        //redis 默认是 jdk 的序列化是二进制,这里使用的是通用的 json 数据,不用传具体的序列化的对象
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //设置相应的 hash 序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        //注入到连接工厂
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean
    public DefaultRedisScript<Long> script() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        //设置脚本的位置，放在 resources 目录下即可
        redisScript.setLocation(new ClassPathResource("lock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
