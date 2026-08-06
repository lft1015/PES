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

  // ---- 已登录用户，首次进入时加载菜单 ----
  if (!appStore.menuList.length) {
    try {
      const res = await getMenuTree()
      appStore.setMenuList(res)
    } catch (error) {
      console.error('获取菜单失败:', error)
    }
  }

  return true
})

// 设置页面标题
router.afterEach((to) => {
  if (to.meta?.title) {
    document.title = `${to.meta.title} - PES系统`
  }
})
