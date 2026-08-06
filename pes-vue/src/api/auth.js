/**
 * 认证相关 API
 *
 * 所有接口通过 Vite 代理转发：
 *   前端请求 /api/xxx → Vite 去除 /api → 后端接收 /xxx
 *
 * 统一响应格式（由 request.js 拦截器解包）：
 *   { code: 200, msg: "success", data: { ... } }
 */

import request from './request'

/**
 * 获取验证码
 * GET /captcha → { key: "uuid", image: "data:image/png;base64,..." }
 *
 * @returns {Promise<{key: string, image: string}>} key 登录时回传，image 是 base64 可直接用于 <img src>
 */
export const getCaptcha = () => {
  return request({
    url: '/captcha',
    method: 'get'
  })
}

/**
 * 用户登录
 * POST /login → { token, username, nickname, roles, permissions, ... }
 *
 * @param {Object} data - { username, password, captcha, captchaKey }
 * @returns {Promise<Object>} 登录成功返回用户信息和 token
 */
export const login = (data) => {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * POST /register → null
 *
 * @param {Object} data - { username, password, nickname?, email?, phone?, captcha, captchaKey }
 * @returns {Promise<void>}
 */
export const register = (data) => {
  return request({
    url: '/register',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 * POST /logout → null
 */
export const logout = () => {
  return request({
    url: '/logout',
    method: 'post'
  })
}
