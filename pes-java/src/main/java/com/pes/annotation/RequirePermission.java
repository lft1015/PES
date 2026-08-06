package com.pes.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 用于标记需要进行权限验证的方法，在方法执行前检查当前用户是否具备指定权限
 *
 * 注解说明:
 * - 该注解只能用于方法上
 * - 在运行时通过AOP或拦截器进行权限校验
 * - 支持单个权限或多个权限配置（满足其一即可）
 * - 会生成到API文档中
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 所需权限标识
     * 用于定义访问该方法所需的权限，支持配置单个或多个权限
     * 当配置多个权限时，用户只需具备其中任意一个即可通过校验（OR关系）
     *
     * @return 权限标识字符串数组，默认为空字符串
     */
    String value() default "";
}