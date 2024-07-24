package com.hzx.seckill.validate;

import com.hzx.seckill.utils.ValidatorUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Jools He
 * @version 1.0
 * @date 2024/7/24 16:48
 * @description: 自定义手机号码格式注解的校验器
 */
public class isMobileValidator implements ConstraintValidator<IsMobile, String> {


    private boolean isRequire;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        isRequire = constraintAnnotation.require(); //基于注解的设定判断该字段是否必须填入
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isRequire) {    //是否必须
            return ValidatorUtils.isMobile(value);
        } else {    //如果不是必填项，查看是否不为空字符串
            if (!StringUtils.hasText(value)) {
                return true;
            } else {
                return ValidatorUtils.isMobile(value);
            }
        }
    }
}
