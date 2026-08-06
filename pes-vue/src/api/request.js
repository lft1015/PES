/**
 * Axios 请求封装
 *
 * 功能：
 *   1. 统一 baseURL（开发环境通过 Vite 代理转发 /api → 后端）
 *   2. 请求拦截：自动附加 JWT token
 *   3. 响应拦截：解包 { code, msg, data } → 直接返回 data，错误时统一提示
 *   4. 401 处理：自动清除登录态并跳转登录页
 */

import axios from 'axios'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'

// ==================== 创建 axios 实例 ====================
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,  // 开发环境: /api，生产环境: 空或实际地址
  timeout: 10000,                               // 10 秒超时
  withCredentials: true                         // 跨域请求携带 cookie（兼容旧版验证码方案）
})

// ==================== 请求拦截器 ====================
request.interceptors.request.use(
  (config) => {
    // 如果已登录，自动附加 Bearer token
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// ==================== 响应拦截器 ====================
request.interceptors.response.use(
  (response) => {
    const { code, msg, message } = response.data

    // ---- 新统一格式：{ code, msg, data } ----
    if (code !== undefined) {
      if (code !== 200) {
        // 业务错误：弹出提示并 reject
        const errMsg = msg || message || '请求失败'
        ElMessage.error(errMsg)
        return Promise.reject(new Error(errMsg))
      }
      // 成功：解包 data 字段（没有 data 时返回整个响应体）
      return response.data.data !== undefined ? response.data.data : response.data
    }

    // ---- 兼容旧格式 ----
    if (code !== 200 && code !== undefined) {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }
    return response.data
  },
  // ---- 网络 / HTTP 错误处理 ----
  (error) => {
    if (error.response && error.response.status === 401) {
      // token 过期或未授权：清除登录态，跳转登录页
      const userStore = useUserStore()
      userStore.logout()
      ElMessage.error('登录已过期，请重新登录')
      window.location.href = '/login'
      return Promise.reject(error)
    }
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default request
