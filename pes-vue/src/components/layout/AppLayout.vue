<template>
  <div class="app-layout">
    <Sidebar />
    <div class="main-content">
      <Header />
      <div class="content-wrapper">
        <Breadcrumb />
        <div class="page-container">
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'
import Breadcrumb from './Breadcrumb.vue'

// 共享侧边栏折叠状态（Header 和 Sidebar 都通过 inject 访问）
const sidebarCollapsed = ref(false)
provide('sidebarCollapsed', sidebarCollapsed)
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
}

.main-content {
  flex: 1;
  min-width: 0;
  overflow: hidden;         /* 根层级禁止溢出滚动 */
  display: flex;
  flex-direction: column;
  background-color: #e8f2fb;
}

.content-wrapper {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  padding: 12px;
  display: flex;
  flex-direction: column;
}

.page-container {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
</style>

<style>
/* 让 router-view 渲染的每个页面自动填满 page-container */
.page-container > div {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
</style>