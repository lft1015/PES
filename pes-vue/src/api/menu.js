/**
 * 菜单 API
 * 提供菜单的树形查询、列表查询、CRUD 操作接口
 */
import request from './request'

/**
 * 获取菜单树（用于左侧导航和角色分配）
 * @param {Object} [params] - 可选查询参数（如 type 过滤菜单类型）
 * @returns {Promise} 菜单树形结构数组
 */
export const getMenuTree = (params) => {
  return request({
    url: '/menus/tree',
    method: 'get',
    ...(params && { params })
  })
}

/**
 * 分页查询菜单列表
 * @param {Object} [params] - 查询参数 { page, pageSize, name, type }
 * @returns {Promise}
 */
export const getMenuList = (params) => {
  return request({
    url: '/menus',
    method: 'get',
    ...(params && { params })
  })
}

/**
 * 根据 ID 查询菜单详情
 * @param {number} id - 菜单 ID
 * @returns {Promise}
 */
export const getMenuById = (id) => {
  return request({
    url: `/menus/${id}`,
    method: 'get'
  })
}

/**
 * 新增菜单
 * @param {Object} data - 菜单数据 { name, type, path, component, permission, icon, parentId, sort }
 * @returns {Promise}
 */
export const createMenu = (data) => {
  return request({
    url: '/menus',
    method: 'post',
    data
  })
}

/**
 * 更新菜单
 * @param {number} id   - 菜单 ID
 * @param {Object} data - 菜单数据
 * @returns {Promise}
 */
export const updateMenu = (id, data) => {
  return request({
    url: `/menus/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除菜单
 * @param {number} id - 菜单 ID
 * @returns {Promise}
 */
export const deleteMenu = (id) => {
  return request({
    url: `/menus/${id}`,
    method: 'delete'
  })
}
