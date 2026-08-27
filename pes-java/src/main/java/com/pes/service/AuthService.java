package com.pes.service;

import com.pes.dto.request.LoginReq;
import com.pes.dto.request.RegisterReq;
import com.pes.dto.response.CaptchaResp;
import com.pes.dto.response.LoginResp;

/**
 * 认证服务接口
 * 提供登录、登出、验证码生成、注册功能
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param req 登录请求（用户名、密码、验证码）
     * @return 登录响应（Token、用户信息）
     */
    LoginResp login(LoginReq req);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 生成图形验证码
     *
     * @return 验证码响应（Base64 图片、验证码 Key）
     */
    CaptchaResp generateCaptcha();

    /**
     * 用户注册
     *
     * @param req 注册请求
     */
    void register(RegisterReq req);

    /**
     * 获取当前登录用户的权限信息
     *
     * @return 登录响应（包含最新权限列表）
     */
    LoginResp getCurrentUserInfo();
}