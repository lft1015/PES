/**
 * 权限检查 Composable
 * 封装权限和角色判断逻辑，用于模板中的 v-if/v-show 指令式权限控制
 *
 * 关键设计：必须使用 storeToRefs 获取 permissions/roles 的 Ref 对象，
 * 而不是直接访问 userStore.permissions（Pinia 会返回 unwrap 后的值，
 * 导致 Vue 的 render effect 无法追踪 .value 变化）。
 *
 * 经验证：侧边栏菜单使用 storeToRefs 工作正常，此方案与侧边栏保持一致。
 */
import { useUserStore } from '@/store/modules/user'
import { storeToRefs } from 'pinia'

export const usePermission = () => {
  const userStore = useUserStore()
  const { permissions, roles } = storeToRefs(userStore)

  const checkPermission = (permission) => {
    if (!permission) return true
    const perms = permissions.value
    if (!perms || !perms.length) return false
    return perms.includes(permission)
  }

  const hasRole = (role) => {
    if (!role) return true
    const userRoles = roles.value
    if (!userRoles || !userRoles.length) return false
    return userRoles.includes(role)
  }

  return {
    checkPermission,
    hasRole
  }
}
