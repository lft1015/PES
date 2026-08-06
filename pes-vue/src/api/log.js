/**
 * 日志 API
 * 提供操作日志和登录日志的查询、删除、批量删除、清空接口
 */
import request from './request'

// ==================== 操作日志 ====================

/**
 * 分页查询操作日志列表
 * @param {Object} params - 查询参数 { page, pageSize, operName, title, status, beginTime, endTime }
 * @returns {Promise}
 */
export function getOperLogList(params) {
  return request.get('/logs/operation', { params })
}

/**
 * 删除单条操作日志
 * @param {number} id - 日志 ID
 * @returns {Promise}
 */
export function deleteOperLog(id) {
  return request.delete(`/logs/operation/${id}`)
}

/**
 * 批量删除操作日志
 * @param {number[]} ids - 日志 ID 数组
 * @returns {Promise}
 */
export function batchDeleteOperLog(ids) {
  return request.delete('/logs/operation/batch', { data: ids })
}

/**
 * 清空所有操作日志
 * @returns {Promise}
 */
export function clearOperLog() {
  return request.delete('/logs/operation/clear')
}

// ==================== 登录日志 ====================

/**
 * 分页查询登录日志列表
 * @param {Object} params - 查询参数 { page, pageSize, username, status, beginTime, endTime }
 * @returns {Promise}
 */
export function getLoginLogList(params) {
  return request.get('/logs/login', { params })
}

/**
 * 删除单条登录日志
 * @param {number} id - 日志 ID
 * @returns {Promise}
 */
export function deleteLoginLog(id) {
  return request.delete(`/logs/login/${id}`)
}

/**
 * 批量删除登录日志
 * @param {number[]} ids - 日志 ID 数组
 * @returns {Promise}
 */
export function batchDeleteLoginLog(ids) {
  return request.delete('/logs/login/batch', { data: ids })
}

/**
 * 清空所有登录日志
 * @returns {Promise}
 */
export function clearLoginLog() {
  return request.delete('/logs/login/clear')
}
