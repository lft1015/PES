package com.pes.security.filter;

import com.pes.security.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 继承 OncePerRequestFilter，确保每个请求只执行一次过滤
 * 从请求头 Authorization 中提取 Bearer Token，校验有效性后设置 Spring Security 认证上下文
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** JWT 工具类，用于解析和校验 Token */
    private final JwtUtils jwtUtils;
    /** 用户详情服务，用于加载用户信息 */
    private final UserDetailsService userDetailsService;

    /**
     * 构造器注入 JWT 工具类和用户详情服务
     *
     * @param jwtUtils          JWT 工具类
     * @param userDetailsService 用户详情服务
     */
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 过滤逻辑：从请求头提取 Token → 校验未过期 → 加载用户信息 → 设置认证上下文
     *
     * @param request      HTTP 请求
     * @param response     HTTP 响应
     * @param filterChain  过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);
        if (token != null && !jwtUtils.isTokenExpired(token)) {
            String username = jwtUtils.getUsernameFromToken(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头 Authorization 中提取 Bearer Token
     *
     * @param request HTTP 请求
     * @return Token 字符串，不存在或格式不正确则返回 null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}