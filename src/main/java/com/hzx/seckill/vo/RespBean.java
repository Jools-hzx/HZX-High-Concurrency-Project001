package com.hzx.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/23 20:29
 * @description: 消息返回 Entity
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespBean {

    private Integer code;
    private String msg;
    private Object data;

    //构建成功消息
    public static RespBean success() {
        return new RespBean(
                RespBeanEnum.SUCCESS.getCode(),
                RespBeanEnum.SUCCESS.getMessage(),
                null
        );
    }

    //构建成功消息并且携带数据
    public static RespBean success(Object data) {
        return new RespBean(
                RespBeanEnum.SUCCESS.getCode(),
                RespBeanEnum.SUCCESS.getMessage(),
                data
        );
    }

    //构建失败消息
    public static RespBean error(RespBeanEnum respBeanEnum) {
        return new RespBean(
                respBeanEnum.getCode(),
                respBeanEnum.getMessage(),
                null
        );
    }

    //构建失败消息并且携带数据
    public static RespBean error(RespBeanEnum respBeanEnum, Object data) {
        return new RespBean(
                respBeanEnum.getCode(),
                respBeanEnum.getMessage(),
                data
        );
    }
}
