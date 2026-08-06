package com.pes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 * 允许前端应用通过 AJAX 跨域访问后端接口，解决浏览器的同源策略限制
 *
 * 配置说明:
 * - 允许所有来源的跨域请求（addAllowedOriginPattern("*")）
 * - 允许所有请求头
 * - 允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）
 * - 允许携带 Cookie 等认证信息
 * - 预检请求缓存时间为 3600 秒（1 小时）
 */
@Configuration
public class CorsConfig {

    /**
     * 注册跨域过滤器
     * 配置全局 CORS 规则，对所有路径生效
     *
     * @return CorsFilter 跨域过滤器实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源的跨域请求
        config.addAllowedOriginPattern("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有 HTTP 方法
        config.addAllowedMethod("*");
        // 允许携带 Cookie
        config.setAllowCredentials(true);
        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}