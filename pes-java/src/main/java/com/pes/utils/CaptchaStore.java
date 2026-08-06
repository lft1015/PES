package com.pes.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 验证码存储器 — 基于 Redis
 *
 * <p>将验证码存入 Redis，支持分布式部署和自动过期。
 * 验证码为一次性消费：获取后立即从 Redis 中删除，防止重复使用。
 */
@Component
public class CaptchaStore {

    /** Redis 工具类 */
    private final RedisUtils redisUtils;

    /**
     * 构造器注入 RedisUtils
     */
    public CaptchaStore(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    /**
     * 保存验证码到 Redis
     *
     * @param key   Redis key，格式为 captcha:{uuid}
     * @param value 验证码文本（已转为小写，便于忽略大小写比对）
     * @param ttl   过期时间数值
     * @param unit  时间单位
     */
    public void put(String key, String value, long ttl, TimeUnit unit) {
        redisUtils.set(key, value, ttl, unit);
    }

    /**
     * 获取并删除验证码（一次性消费）
     *
     * <p>从 Redis 中取出验证码后立即删除，确保每个验证码只能使用一次。
     * 处理 Jackson 序列化时可能引入的引号包裹问题。
     *
     * @param key Redis key
     * @return 验证码文本，不存在返回 null
     */
    public String getAndRemove(String key) {
        Object value = redisUtils.get(key);
        if (value != null) {
            redisUtils.delete(key);
            return value.toString().replace("\"", "");  // 处理 Jackson 序列化的引号
        }
        return null;
    }
}