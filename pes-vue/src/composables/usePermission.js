/**
 * 权限检查 Composable
 * 封装权限和角色判断逻辑，用于模板中的 v-if/v-show 指令式权限控制
 */
import { useUserStore } from '@/store/modules/user'

/**
 * 权限检查组合式函数
 * @returns {{ checkPermission: Function, hasRole: Function }}
 */
export const usePermission = () => {
  const userStore = useUserStore()

  /**
   * 检查用户是否拥有指定权限标识
   * permission 为空时返回 true（无权限要求的操作默认允许）
   * @param {string} permission - 权限标识，如 'user:create'、'role:delete'
   * @returns {boolean} 是否拥有该权限
   */
  const checkPermission = (permission) => {
    if (!permission) return true
    if (!userStore.permissions) return false
    return userStore.permissions.includes(permission)
  }

  /**
   * 检查用户是否拥有指定角色
   * role 为空时返回 true
   * @param {string} role - 角色编码，如 'admin'、'user'
   * @returns {boolean} 是否拥有该角色
   */
  const hasRole = (role) => {
    if (!role) return true
    if (!userStore.roles) return false
    return userStore.roles.includes(role)
  }

  return {
    checkPermission,
    hasRole
  }
}
