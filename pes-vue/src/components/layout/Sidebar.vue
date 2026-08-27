<!--
  侧边栏 — 哆啦A梦蓝色主题
  宽度完全由 .sidebar 控制，el-menu 不做任何宽度覆盖，自然填充
-->
<template>
  <aside class="sidebar" :class="{ collapsed }">
    <!-- Logo 区 -->
    <div class="sidebar-header">
      <div class="logo">
        <div class="logo-icon-wrap">
          <img :src="logoImg" alt="Logo" class="logo-img" />
        </div>
        <span v-show="!collapsed" class="logo-text">PES 系统</span>
      </div>
      <p v-show="!collapsed" class="logo-subtitle">四次元口袋管理平台</p>
    </div>

    <!-- 菜单区 -->
    <nav class="sidebar-menu">
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        mode="vertical"
        class="dora-menu"
        background-color="transparent"
        text-color="rgba(255,255,255,0.75)"
        active-text-color="#fff"
        @select="handleMenuSelect"
      >
        <template v-for="menu in menusWithIcons" :key="menu.id">
          <el-menu-item
            v-if="!menu.children || menu.children.length === 0"
            :index="menu.path"
          >
            <el-icon v-if="menu._icon" class="menu-icon"><component :is="menu._icon" /></el-icon>
            <span>{{ menu.name }}</span>
          </el-menu-item>
          <el-sub-menu
            v-else
            :index="String(menu.id)"
            popper-class="dora-popper"
          >
            <template #title>
              <el-icon v-if="menu._icon" class="menu-icon"><component :is="menu._icon" /></el-icon>
              <span>{{ menu.name }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.id"
              :index="child.path"
            >
              <el-icon v-if="child._icon" class="menu-icon"><component :is="child._icon" /></el-icon>
              <span>{{ child.name }}</span>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </nav>

    <!-- 折叠按钮 -->
    <div class="sidebar-footer">
      <button class="toggle-btn" @click="collapsed = !collapsed">
        <el-icon class="toggle-icon">
          <Expand v-if="collapsed" />
          <Fold v-else />
        </el-icon>
        <span v-show="!collapsed" class="toggle-text">收起菜单</span>
      </button>
    </div>

    <!-- 底部装饰 -->
    <div class="sidebar-dora" v-show="!collapsed">
      <img :src="decorImg" alt="" class="sidebar-dora-img" />
    </div>
  </aside>
</template>

<script setup>
import { ref, computed, inject, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import { Expand, Fold } from '@element-plus/icons-vue'
import { getIcon } from '@/utils/iconMap'
import logoImg from '@/img/smile.jpg'
import decorImg from '@/img/together.jpg'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

// 通过 provide/inject 与 Header 共享折叠状态
const collapsed = inject('sidebarCollapsed', ref(false))

// 同步到 store，保持 menu/breadcrumb 等组件兼容
watch(collapsed, (val) => {
  appStore.setSidebarCollapsed(val)
})

// 递归过滤菜单：只保留 type=1 且当前用户拥有其权限的菜单
const filterByPermission = (menus) => {
  const permissions = userStore.permissions || []
  return menus
    .filter(m => m && m.type === 1)
    .filter(m => !m.permission || permissions.includes(m.permission))
    .map(m => ({
      ...m,
      _icon: getIcon(m.icon),
      children: m.children ? filterByPermission(m.children) : []
    }))
    .filter(m => m.children.length > 0 || m.path)
}

// 顶层菜单，根据权限过滤，为每个节点预解析图标组件
const menusWithIcons = computed(() => {
  return filterByPermission(appStore.menuList || [])
})

const activeMenu = computed(() => route.path)

const handleMenuSelect = (index) => {
  if (index) router.push(index)
}
</script>

<style scoped>
/* ================================================================
   侧边栏主体
   宽度变化完全由此控制，overflow: hidden 裁剪超出的 el-menu 内容
   ================================================================ */
.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #1a6cb5 0%, #3b96d9 30%, #4BA3E3 60%, #3b96d9 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  transition: width 0.18s ease-out;
  box-shadow: 2px 0 20px rgba(30, 108, 184, 0.25);
}

.sidebar.collapsed {
  width: 64px;
}

/* 背景装饰 */
.sidebar::before {
  content: '';
  position: absolute; right: -40px; top: 120px;
  width: 120px; height: 120px; border-radius: 50%;
  background: rgba(255, 255, 255, 0.04);
  pointer-events: none;
}
.sidebar::after {
  content: '';
  position: absolute; left: -30px; bottom: 100px;
  width: 80px; height: 80px; border-radius: 50%;
  background: rgba(255, 255, 255, 0.03);
  pointer-events: none;
}

/* ================================================================
   Logo
   ================================================================ */
.sidebar-header {
  padding: 24px 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  flex-shrink: 0;
}
.logo { display: flex; align-items: center; gap: 10px; height: 40px; }
.logo-icon-wrap {
  flex-shrink: 0; width: 40px; height: 40px;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  backdrop-filter: blur(4px);
  border: 1.5px solid rgba(255, 255, 255, 0.25);
}
.logo-img { width: 28px; height: 28px; object-fit: contain; }
.logo-text {
  font-size: 18px; font-weight: 700;
  letter-spacing: 1px; white-space: nowrap;
}
.logo-subtitle {
  font-size: 11px; color: rgba(255, 255, 255, 0.6);
  margin: 6px 0 0 50px; white-space: nowrap;
}

/* ================================================================
   菜单区 — 不设 el-menu 宽度，让其自然适应
   ================================================================ */
.sidebar-menu {
  flex: 1; padding: 12px 0;
  overflow-y: auto; overflow-x: hidden;
}
.sidebar-menu::-webkit-scrollbar { width: 4px; }
.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15); border-radius: 2px;
}

.dora-menu {
  border-right: none !important;
  background: transparent !important;
}

/* 菜单图标 */
.menu-icon { margin-right: 8px; font-size: 16px; flex-shrink: 0; }

/* 菜单项 */
.dora-menu :deep(.el-menu-item),
.dora-menu :deep(.el-sub-menu__title) {
  height: 44px; line-height: 44px;
  margin: 2px 10px; border-radius: 10px;
  padding-left: 16px !important;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75) !important;
}
.dora-menu :deep(.el-menu-item:hover),
.dora-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.12) !important;
  color: #fff !important;
}
.dora-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.2) !important;
  color: #fff !important; font-weight: 600;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.dora-menu :deep(.el-menu-item.is-active)::before {
  content: ''; position: absolute; left: 0; top: 50%;
  transform: translateY(-50%);
  width: 3px; height: 20px;
  background: #F9A825; border-radius: 0 3px 3px 0;
}

/* 折叠态：隐藏文字、居中图标 */
.sidebar.collapsed .menu-icon { margin-right: 0; }
.sidebar.collapsed .dora-menu :deep(.el-menu-item) span,
.sidebar.collapsed .dora-menu :deep(.el-sub-menu__title) span {
  display: none;
}
.sidebar.collapsed .dora-menu :deep(.el-menu-item),
.sidebar.collapsed .dora-menu :deep(.el-sub-menu__title) {
  padding: 0 !important; justify-content: center;
}

/* 子菜单 */
.dora-menu :deep(.el-sub-menu .el-menu) {
  background: rgba(0, 0, 0, 0.1) !important;
  border-radius: 0 0 10px 10px;
  margin: 0 10px;
}
.dora-menu :deep(.el-sub-menu__icon-arrow) {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* ================================================================
   底部折叠按钮
   ================================================================ */
.sidebar-footer {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.12);
  flex-shrink: 0;
}
.toggle-btn {
  width: 100%; min-width: 0;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1.5px solid rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer; font-size: 13px;
  display: flex; align-items: center; justify-content: center; gap: 8px;
  transition: background 0.3s, border-color 0.3s, color 0.3s;
}
.toggle-btn:hover {
  background: rgba(255, 255, 255, 0.18);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
}
.toggle-icon { font-size: 14px; flex-shrink: 0; display: flex; align-items: center; }
.toggle-text { white-space: nowrap; }

/* 底部装饰 */
.sidebar-dora {
  position: absolute; bottom: 70px; left: 0; right: 0;
  display: flex; justify-content: center;
  opacity: 0.18; pointer-events: none;
}
.sidebar-dora-img {
  width: 100%;
  max-width: 200px;
  object-fit: contain;
}
</style>

<!-- 非 scoped：覆盖 el-menu 内部动画 + 子菜单弹出层 -->
<style>
/* 禁用 el-menu 折叠/展开的内部 JS 动画，由 sidebar CSS transition 统一控制 */
.el-menu--collapse {
  transition: none !important;
}

.dora-popper {
  background: linear-gradient(180deg, #2b8dd6 0%, #4BA3E3 100%) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  border-radius: 10px !important;
  box-shadow: 0 8px 30px rgba(30, 108, 184, 0.35) !important;
  padding: 4px 0 !important;
}
.dora-popper .el-menu-item {
  background: transparent !important;
  color: rgba(255, 255, 255, 0.8) !important;
  height: 38px !important; line-height: 38px !important;
  margin: 0 6px !important; padding-left: 16px !important;
}
.dora-popper .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.15) !important;
  color: #fff !important; border-radius: 8px !important;
}
.dora-popper .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.22) !important;
  color: #fff !important;
}
</style>
