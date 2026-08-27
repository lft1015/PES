package com.pes.controller;

import com.pes.common.Result;
import com.pes.dto.request.LoginReq;
import com.pes.dto.request.RegisterReq;
import com.pes.dto.response.CaptchaResp;
import com.pes.dto.response.LoginResp;
import com.pes.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供验证码获取、登录、注册、登出等公开接口，无需认证即可访问
 */
@RestController
public class AuthController {

    /** 认证服务 */
    private final AuthService authService;

    /**
     * 构造器注入认证服务
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 获取验证码
     * GET /captcha
     */
    @GetMapping("/captcha")
    public Result<CaptchaResp> captcha() {
        CaptchaResp resp = authService.generateCaptcha();
        return Result.ok(resp);
    }

    /**
     * 用户登录
     * POST /login
     */
    @PostMapping("/login")
    public Result<LoginResp> login(@Valid @RequestBody LoginReq req) {
        LoginResp resp = authService.login(req);
        return Result.ok(resp);
    }

    /**
     * 用户注册
     * POST /register → { code: 200, msg: "success", data: null }
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterReq req) {
        authService.register(req);
        return Result.ok();
    }

    /**
     * 用户登出
     * POST /logout → { code: 200, msg: "success", data: null }
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    /**
     * 获取当前登录用户的最新权限信息
     * GET /auth/me → { code: 200, data: { username, nickname, roles, permissions } }
     */
    @GetMapping("/auth/me")
    public Result<LoginResp> getCurrentUserInfo() {
        LoginResp resp = authService.getCurrentUserInfo();
        return Result.ok(resp);
    }
}