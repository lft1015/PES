package com.pes.annotation;

import java.lang.annotation.*;

/**
 * 日志操作注解
 * 用于标记需要进行日志记录的方法，支持在方法执行时自动记录操作日志
 *
 * 注解说明:
 * - 该注解只能用于方法上
 * - 在运行时通过反射获取注解信息
 * - 会生成到API文档中
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {

    /**
     * 操作描述信息
     * 用于记录当前被标注方法的操作名称或描述
     *
     * @return 操作描述字符串，默认为空字符串
     */
    String value() default "";
}