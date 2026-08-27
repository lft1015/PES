/**
 * 路由守卫 — 权限控制
 *
 * Vue Router 4 风格的 return 语法：
 *   return true / undefined  → 放行
 *   return '/path'            → 重定向到 /path
 *   return false              → 取消导航
 *
 * 必须在 main.js 中以副作用导入：import '@/router/guard'
 */

import router from './index'
import { useUserStore } from '@/store/modules/user'
import { useAppStore } from '@/store/modules/app'
import { getMenuTree } from '@/api/menu'
import { getCurrentUserInfo } from '@/api/auth'

/**
 * 递归构建 路由路径 → 所需权限 的映射
 * 仅收集有 path 且有 permission 的菜单节点（type=1）
 */
const buildPathPermMap = (menus, map = {}) => {
  for (const m of menus || []) {
    if (m && m.path && m.permission) {
      map[m.path] = m.permission
    }
    if (m && m.children && m.children.length) {
      buildPathPermMap(m.children, map)
    }
  }
  return map
}

router.beforeEach(async (to) => {
  const userStore = useUserStore()
  const appStore = useAppStore()

  // ---- 访问登录/注册页 ----
  if (to.path === '/login' || to.path === '/register') {
    // 已登录用户访问登录页 → 重定向到首页
    if (userStore.token) {
      return '/dashboard'
    }
    return true
  }

  // ---- 访问任何其他页面 ----
  // 未登录 → 重定向到登录页
  if (!userStore.token) {
    return '/login'
  }

  // ---- 已登录用户，首次进入时加载菜单并刷新最新权限 ----
  // 侧边栏菜单与按钮权限都依赖 permissions：
  // 菜单从后端全量 tree 加载后按权限过滤，权限变更（如角色被重新分配）
  // 后，刷新页面即可通过 /auth/me 拿到最新权限，无需重新登录。
  if (!appStore.menuList.length) {
    try {
      const res = await getMenuTree()
      appStore.setMenuList(res)
    } catch (error) {
      console.error('获取菜单失败:', error)
    }
    // 静默刷新最新用户信息（roles / permissions），失败不阻塞导航
    try {
      const info = await getCurrentUserInfo()
      userStore.setUserInfo(info)
    } catch (error) {
      // 保持登录时缓存的权限，静默忽略
    }
  }

  // ---- 路由级权限拦截 ----
  // 菜单隐藏只是前端入口过滤，这里确保无权限用户直接输入 URL
  // 访问管理页面时被重定向到仪表盘（仪表盘对所有角色可见）。
  // 与后端 @RequirePermission 接口校验形成完整闭环。
  const pathPermMap = buildPathPermMap(appStore.menuList)
  const required = pathPermMap[to.path]
  if (required && !(userStore.permissions || []).includes(required)) {
    return '/dashboard'
  }

  return true
})

// 设置页面标题
router.afterEach((to) => {
  if (to.meta?.title) {
    document.title = `${to.meta.title} - PES系统`
  }
})
