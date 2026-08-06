/**
 * 用户状态 Store
 * 管理登录 Token、用户信息、角色、权限等认证相关状态
 *
 * 数据持久化：通过 storage 工具类将关键状态同步到 localStorage，
 * 确保页面刷新后登录状态不丢失
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { storage } from '@/utils/storage'

export const useUserStore = defineStore('user', () => {
  /** JWT Token（持久化到 localStorage） */
  const token = ref(storage.get('token') || '')
  /** 用户名 */
  const username = ref(storage.get('username') || '')
  /** 昵称 */
  const nickname = ref(storage.get('nickname') || '')
  /** 角色编码列表，如 ['admin', 'user'] */
  const roles = ref(storage.get('roles') || [])
  /** 权限标识列表，如 ['user:create', 'role:delete'] */
  const permissions = ref(storage.get('permissions') || [])

  /**
   * 设置 Token 并持久化
   * @param {string} newToken - JWT Token
   */
  const setToken = (newToken) => {
    token.value = newToken
    storage.set('token', newToken)
  }

  /**
   * 设置用户信息并持久化
   * @param {Object} info - { username, nickname, roles, permissions }
   */
  const setUserInfo = (info) => {
    username.value = info.username || ''
    nickname.value = info.nickname || ''
    roles.value = info.roles || []
    permissions.value = info.permissions || []
    storage.set('username', username.value)
    storage.set('nickname', nickname.value)
    storage.set('roles', roles.value)
    storage.set('permissions', permissions.value)
  }

  /**
   * 退出登录：清空所有状态和 localStorage
   */
  const logout = () => {
    token.value = ''
    username.value = ''
    nickname.value = ''
    roles.value = []
    permissions.value = []
    storage.remove('token')
    storage.remove('username')
    storage.remove('nickname')
    storage.remove('roles')
    storage.remove('permissions')
  }

  return {
    token,
    username,
    nickname,
    roles,
    permissions,
    setToken,
    setUserInfo,
    logout
  }
})
