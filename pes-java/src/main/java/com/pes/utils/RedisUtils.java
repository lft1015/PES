package com.pes.utils;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * 基于 Spring Boot 自动配置的 RedisTemplate 提供常用操作封装
 */
@Component
public class RedisUtils {

    private static final Logger log = LoggerFactory.getLogger(RedisUtils.class);

    /** RedisTemplate 由 Spring Boot 自动配置注入，Redis 不可用时启动将直接报错 */
    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 启动时检查 Redis 连接是否可用
     * 通过 PING 命令验证连接，失败则阻止应用启动，避免运行时才发现 Redis 不可用
     */
    @PostConstruct
    public void init() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            log.info("Redis 连接成功");
        } catch (Exception e) {
            log.error("Redis 连接失败，请检查 Redis 是否已启动：{}", e.getMessage());
            throw new IllegalStateException("Redis 连接失败，应用无法启动", e);
        }
    }

    /**
     * 设置键值（无过期时间）
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置键值并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间数值
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取键对应的值
     *
     * @param key 键
     * @return 值，不存在返回 null
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return true 存在，false 不存在
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除键
     *
     * @param key 键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置键的过期时间
     *
     * @param key     键
     * @param timeout 过期时间数值
     * @param unit    时间单位
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
}