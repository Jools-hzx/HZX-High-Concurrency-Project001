package com.hzx.seckill.validate;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
1. 参考 @NotNull 注解来开发
2. @Constraint(validatedBy = {}) 内绑定真正的校验器，需要实现ConstraintValidator接口来开发
3. message 定义如果校验错误返回的信息
 */
@Documented
@Constraint(validatedBy = {isMobileValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface IsMobile {

    String message() default "手机号码格式错误";    //也可以配置从 .properties 文件内读取

    boolean require() default true;     //是否必须传入值

    Class<?>[] groups() default {};   //默认你参数

    Class<? extends Payload>[] payload() default {};//默认参
}
