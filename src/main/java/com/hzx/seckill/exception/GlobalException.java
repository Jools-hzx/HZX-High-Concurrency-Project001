package com.hzx.seckill.exception;

import com.hzx.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/24 16:59
 * @description: 自定义全局异常类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends RuntimeException {

    private RespBeanEnum respBeanEnum;
}
