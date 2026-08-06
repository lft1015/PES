
package com.pes.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 验证码生成工具
 * 从数字+大小写字母中随机选取指定长度的字符组成验证码
 */
@Component
public class CaptchaUtils {

    /** 字符池：数字 + 大写字母 + 小写字母（排除易混淆字符 O/0/l/1/I 等） */
    private static final String CHAR_POOL = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * 生成指定长度的随机验证码
     *
     * @param length 验证码长度
     * @return 随机验证码字符串
     */
    public String generateCaptcha(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }
}