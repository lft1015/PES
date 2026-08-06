
package com.pes.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Bean 拷贝工具类
 * 基于 Spring BeanUtils 提供对象属性拷贝的便捷静态方法
 */
@Component
public class BeanCopyUtils {

    /**
     * 将源对象的属性拷贝到目标类的新实例中
     *
     * @param source      源对象
     * @param targetClass 目标类
     * @param <T>         目标类型
     * @return 目标对象的新实例，source 为 null 时返回 null
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean copy failed", e);
        }
    }

    /**
     * 将源对象的属性拷贝到目标对象中
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }
}