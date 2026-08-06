/**
 * 应用全局状态 Store
 * 管理侧边栏折叠状态、动态菜单列表等全局 UI 状态
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  /** 侧边栏是否折叠 */
  const sidebarCollapsed = ref(false)
  /** 动态菜单列表（从后端获取） */
  const menuList = ref([])

  /** 设置侧边栏折叠状态 */
  const setSidebarCollapsed = (collapsed) => {
    sidebarCollapsed.value = collapsed
  }

  /** 设置菜单列表 */
  const setMenuList = (menus) => {
    menuList.value = menus
  }

  return {
    sidebarCollapsed,
    menuList,
    setSidebarCollapsed,
    setMenuList
  }
})
