<!--
  App.vue — 根组件

  关键设计：使用 v-if="appReady" 确保路由守卫（如登录检查）完全执行完毕后
  才渲染 <router-view />。这彻底消除了"首页闪过后跳转登录页"的问题。

  原理：
    1. 页面加载 → appReady = false → 渲染空白 loading 区
    2. router.isReady() 等待初始导航 + beforeEach 守卫完成
    3. 守卫判断无 token → redirect /login → 守卫再次执行 → 放行 /login
    4. isReady() resolve → appReady = true → 渲染 Login 页面
-->
<template>
  <router-view v-if="appReady" />
  <div v-else class="app-init">
    <!-- 路由初始化中，不渲染任何页面，避免闪屏 -->
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const appReady = ref(false)

// 等待路由初始导航（含所有 beforeEach 守卫和 redirect）完全结束后
// 才允许渲染 router-view
router.isReady().then(() => {
  appReady.value = true
})
</script>

<style>
/* 全局初始化占位 — 仅路由解析期间短暂存在 */
.app-init {
  min-height: 100vh;
  background: #f0f2f5;
}
</style>
