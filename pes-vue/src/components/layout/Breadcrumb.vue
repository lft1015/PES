<template>
  <div class="breadcrumb">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item
        v-for="(item, index) in breadcrumbList"
        :key="index"
        :to="item.path ? { path: item.path } : ''"
      >
        {{ item.title }}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const breadcrumbList = computed(() => {
  return route.matched.slice(1).map(item => ({
    title: item.meta?.title || '',
    path: item.path
  }))
})
</script>

<style scoped>
.breadcrumb {
  flex-shrink: 0;
  margin-bottom: 8px;
  padding: 10px 0;
  border-bottom: 1px solid #dce8f2;
}

/* 面包屑蓝色主题 */
.breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #4BA3E3;
  font-weight: 500;
}

.breadcrumb :deep(.el-breadcrumb__inner:hover) {
  color: #1E6CB8;
}
</style>