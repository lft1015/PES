<!--
  顶部导航栏 — 哆啦A梦蓝色主题点缀
-->
<template>
  <header class="header">
    <div class="header-left">
      <!-- 折叠按钮 — 蓝色圆角 -->
      <button @click="toggleSidebar" class="sidebar-toggle">
        <span>☰</span>
      </button>
      <!-- 页面标题 -->
      <h1 class="page-title">
        <span class="title-dot">●</span>
        {{ pageTitle }}
      </h1>
    </div>

    <div class="header-right">
      <!-- 用户下拉菜单 -->
      <div class="user-menu">
        <el-dropdown trigger="click">
          <span class="user-info">
            <div class="user-avatar">
              {{ (userStore.nickname || '管')[0] }}
            </div>
            <span class="user-name">{{ userStore.nickname || '管理员' }}</span>
            <el-icon class="arrow-icon"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goProfile">
                <el-icon><User /></el-icon>
                <span>个人中心</span>
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { useAppStore } from '@/store/modules/app'
import { ElMessage } from 'element-plus'
import { ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const pageTitle = computed(() => {
  const matched = route.matched.slice(-1)[0]
  return matched?.meta?.title || '系统管理'
})

// 通过 inject 共享侧边栏折叠状态（与 Sidebar 同一个 ref）
const sidebarCollapsed = inject('sidebarCollapsed', null)

const toggleSidebar = () => {
  if (sidebarCollapsed) {
    sidebarCollapsed.value = !sidebarCollapsed.value
    // 同步到 store，保持兼容
    appStore.setSidebarCollapsed(sidebarCollapsed.value)
  }
}

const goProfile = () => {
  router.push('/profile')
}

const handleLogout = () => {
  // JWT 无状态，登出 = 客户端清除 token，无需服务端配合
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}
</script>

<style scoped>
/* ================================================================
   顶栏主体
   ================================================================ */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 8px rgba(75, 163, 227, 0.12);
  position: relative;
  z-index: 10;
}

/* 底部蓝色渐变装饰线 */
.header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #4BA3E3, #89CFF0, #4BA3E3);
}

/* ================================================================
   左侧
   ================================================================ */
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 折叠按钮 */
.sidebar-toggle {
  width: 34px;
  height: 34px;
  border: 1.5px solid #e8edf2;
  border-radius: 10px;
  background: #f8fafc;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.25s ease;
}

.sidebar-toggle:hover {
  background: #E3F2FD;
  border-color: #4BA3E3;
  color: #4BA3E3;
}

/* 页面标题 */
.page-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a1a2e;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.title-dot {
  color: #4BA3E3;
  font-size: 10px;
}

/* ================================================================
   右侧
   ================================================================ */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 14px 6px 6px;
  border-radius: 24px;
  transition: all 0.25s ease;
  border: 1.5px solid transparent;
}

.user-info:hover {
  background: #f0f7ff;
  border-color: #e0edf8;
}

/* 用户头像 — 蓝色圆形 */
.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4BA3E3, #1E6CB8);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.arrow-icon {
  font-size: 12px;
  color: #999;
  transition: transform 0.3s;
}
</style>
