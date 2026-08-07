/**
 * 用户 API
 * 提供用户的 CRUD、个人信息管理、密码修改、下拉选项等接口
 */
import request from './request'

/**
 * 分页查询用户列表
 * @param {Object} [params] - 查询参数 { page, pageSize, username, nickname, status }
 * @returns {Promise}
 */
export const getUserList = (params) => {
  return request({
    url: '/users',
    method: 'get',
    ...(params && { params })
  })
}

/**
 * 用户下拉选项（无需 user:list 权限）
 * 用于角色分配等场景选择用户
 * @returns {Promise} [{ username, nickname }]
 */
export const getUserOptions = () => {
  return request({
    url: '/users/options',
    method: 'get'
  })
}

/**
 * 根据 ID 查询用户详情
 * @param {number} id - 用户 ID
 * @returns {Promise} 用户信息 + 已分配的角色 ID 列表
 */
export const getUserById = (id) => {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

/**
 * 新增用户
 * @param {Object} data - 用户数据 { username, password, nickname, email, phone, roleId }
 * @returns {Promise}
 */
export const createUser = (data) => {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 * @param {number} id   - 用户 ID
 * @param {Object} data - 用户数据
 * @returns {Promise}
 */
export const updateUser = (id, data) => {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {number} id - 用户 ID
 * @returns {Promise}
 */
export const deleteUser = (id) => {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

/**
 * 获取当前登录用户个人信息
 * @returns {Promise} { username, nickname, email, phone, roles, permissions }
 */
export const getUserProfile = () => {
  return request({
    url: '/users/profile',
    method: 'get'
  })
}

/**
 * 更新当前登录用户个人信息
 * @param {Object} data - { nickname, email, phone }
 * @returns {Promise}
 */
export const updateUserProfile = (data) => {
  return request({
    url: '/users/profile',
    method: 'put',
    data
  })
}

/**
 * 修改当前登录用户密码
 * @param {Object} data - { oldPassword, newPassword }
 * @returns {Promise}
 */
export const changePassword = (data) => {
  return request({
    url: '/users/password',
    method: 'put',
    data
  })
}
