/**
 * 角色 API
 * 提供角色的 CRUD 操作及菜单分配接口
 */
import request from './request'

/**
 * 分页查询角色列表
 * @param {Object} [params] - 查询参数 { page, pageSize, name, code }
 * @returns {Promise}
 */
export const getRoleList = (params) => {
  return request({
    url: '/roles',
    method: 'get',
    ...(params && { params })
  })
}

/**
 * 角色下拉选项（供新增/编辑用户时选择角色，无需 role:list 权限）
 * @returns {Promise} [{ id, name }]
 */
export const getRoleOptions = () => {
  return request({
    url: '/roles/options',
    method: 'get'
  })
}

/**
 * 根据 ID 查询角色详情
 * @param {number} id - 角色 ID
 * @returns {Promise} 角色信息 + 已分配的菜单 ID 列表
 */
export const getRoleById = (id) => {
  return request({
    url: `/roles/${id}`,
    method: 'get'
  })
}

/**
 * 新增角色
 * @param {Object} data - 角色数据 { name, code, description }
 * @returns {Promise}
 */
export const createRole = (data) => {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

/**
 * 更新角色
 * @param {number} id   - 角色 ID
 * @param {Object} data - 角色数据
 * @returns {Promise}
 */
export const updateRole = (id, data) => {
  return request({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除角色
 * @param {number} id - 角色 ID
 * @returns {Promise}
 */
export const deleteRole = (id) => {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

/**
 * 为角色分配菜单权限
 * @param {number} roleId - 角色 ID
 * @param {Object} data   - { menuIds: number[] } 菜单 ID 数组
 * @returns {Promise}
 */
export const assignMenu = (roleId, data) => {
  return request({
    url: `/roles/${roleId}/assign`,
    method: 'post',
    data
  })
}
