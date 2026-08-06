package com.pes.config;

import com.pes.security.filter.JwtAuthenticationFilter;
import com.pes.security.handler.AccessDeniedHandlerImpl;
import com.pes.security.handler.AuthenticationEntryPointImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

/**
 * Spring Security 安全配置
 * 采用无状态 JWT 认证模式，禁用 Session 和 CSRF
 *
 * 配置内容:
 * - 禁用 CSRF（前后端分离，基于 Token 无需 CSRF 防护）
 * - 无状态会话管理（不创建 HttpSession）
 * - 放行公开接口：验证码、登录、注册、登出
 * - 其余接口需认证后访问
 * - 注册 JWT 认证过滤器，在 UsernamePasswordAuthenticationFilter 之前执行
 * - 自定义认证失败和权限不足的异常处理器
 * - 密码加密使用 BCrypt
 * - 启用方法级权限注解（@PreAuthorize 等）
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** JWT 认证过滤器，从请求头中解析 Token 并设置认证信息 */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** 认证失败处理器（未登录或 Token 无效时触发） */
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    /** 权限不足处理器（已登录但无权限时触发） */
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    /**
     * 构造器注入安全组件
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                         AuthenticationEntryPointImpl authenticationEntryPoint,
                         AccessDeniedHandlerImpl accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /**
     * 配置安全过滤链
     * 定义请求拦截规则、异常处理和过滤器顺序
     *
     * @param http HttpSecurity 配置对象
     * @return 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（前后端分离，基于 Token 认证）
            .csrf(AbstractHttpConfigurer::disable)
            // 无状态会话，不创建 Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 请求授权规则
            .authorizeHttpRequests(auth -> auth
                // 放行公开接口
                .requestMatchers("/captcha", "/login", "/register", "/logout").permitAll()
                // 其余接口需要认证
                .anyRequest().authenticated()
            )
            // 异常处理：认证失败、权限不足
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            // 在 UsernamePasswordAuthenticationFilter 之前添加 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 密码编码器
     * 使用 BCrypt 算法对密码进行加密和校验
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * 用于执行登录认证逻辑
     *
     * @param config Spring Security 认证配置
     * @return AuthenticationManager 实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 认证事件发布器
     * 将 InternalAuthenticationServiceException 映射为 BadCredentialsEvent，
     * 避免暴露内部异常细节（如用户不存在等）
     *
     * @param applicationEventPublisher Spring 事件发布器
     * @return DefaultAuthenticationEventPublisher 实例
     */
    @Bean
    public DefaultAuthenticationEventPublisher authenticationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        DefaultAuthenticationEventPublisher publisher =
                new DefaultAuthenticationEventPublisher(applicationEventPublisher);
        // 将用户不存在等内部异常统一映射为密码错误事件，避免泄露用户信息
        publisher.setAdditionalExceptionMappings(
                Map.of(InternalAuthenticationServiceException.class,
                        AuthenticationFailureBadCredentialsEvent.class));
        return publisher;
    }
}