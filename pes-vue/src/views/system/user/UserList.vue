<template>
  <div class="user-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><el-icon><User /></el-icon>用户管理</h2>
      <p class="page-desc">管理系统用户账号和基本信息</p>
    </div>

    <!-- 搜索工具栏 -->
    <div class="toolbar-card">
      <div class="toolbar-left" v-if="checkPermission('user:view')">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入用户名搜索"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
        <el-button @click="handleReset"><el-icon><Refresh /></el-icon> 重置</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="success" v-if="checkPermission('user:add')" @click="openCreateModal"><el-icon><Plus /></el-icon> 新增用户</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <div class="table-wrap">
        <el-table :data="tableData" border stripe height="100%" v-loading="loading">
          <el-table-column prop="id" label="ID" min-width="60" align="center" />
          <el-table-column prop="username" label="用户名" min-width="100" align="center">
            <template #default="{ row }">{{ row.username || '-' }}</template>
          </el-table-column>
          <el-table-column prop="nickname" label="昵称" min-width="100" align="center">
            <template #default="{ row }">{{ row.nickname || '-' }}</template>
          </el-table-column>
          <el-table-column prop="email" label="邮箱" min-width="140" align="center">
            <template #default="{ row }">{{ row.email || '-' }}</template>
          </el-table-column>
          <el-table-column prop="phone" label="手机号" min-width="120" align="center">
            <template #default="{ row }">{{ row.phone || '-' }}</template>
          </el-table-column>
          <el-table-column prop="roleName" label="角色" min-width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.roleName" size="small" effect="plain">{{ row.roleName }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" min-width="80" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="160" align="center">
            <template #default="{ row }">
              {{ row.createTime ? row.createTime.replace('T', ' ') : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="180" align="center">
            <template #default="scope">
              <el-button size="small" type="primary" v-if="checkPermission('user:edit')" @click="openEditModal(scope.row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button size="small" type="danger" v-if="checkPermission('user:delete')" @click="handleDelete(scope.row.id)">
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          background
        />
      </div>
    </div>

    <UserForm
      v-if="showFormModal"
      :edit-data="editData"
      @close="closeFormModal"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { getUserList, deleteUser } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import UserForm from './UserForm.vue'
import { usePermission } from '@/composables/usePermission'
const { checkPermission } = usePermission()

const allData = ref([])
const loading = ref(false)
const showFormModal = ref(false)
const editData = ref(null)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({ keyword: '' })

const tableData = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return allData.value.slice(start, start + pageSize.value)
})

const loadData = async () => {
  loading.value = true
  try {
    const params = searchForm.keyword ? { keyword: searchForm.keyword } : {}
    const res = await getUserList(params)
    allData.value = Array.isArray(res) ? res : []
    total.value = allData.value.length
    page.value = 1
  } catch { ElMessage.error('获取用户列表失败') }
  finally { loading.value = false }
}

const handleSearch = () => { loadData() }
const handleReset = () => { searchForm.keyword = ''; loadData() }
const openCreateModal = () => { editData.value = null; showFormModal.value = true }
const openEditModal = (row) => { editData.value = { ...row }; showFormModal.value = true }
const closeFormModal = () => { showFormModal.value = false; editData.value = null }
const handleFormSuccess = () => { closeFormModal(); loadData() }

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？删除后不可恢复！', '警告', { type: 'warning' })
  } catch {
    return // 用户取消
  }
  try {
    await deleteUser(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    ElMessage.error('删除失败')
  }
}

let searchTimer = null
watch(() => searchForm.keyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 1; loadData() }, 300)
})

onMounted(() => { loadData() })
</script>

<style scoped>
.user-list { display: flex; flex-direction: column; padding: 4px 0; }

/* 页面标题 */
.page-header { margin-bottom: 10px; flex-shrink: 0; }
.page-title { font-size: 20px; font-weight: 700; color: #1a1a2e; display: flex; align-items: center; gap: 8px; margin: 0 0 4px; }
.page-title .el-icon { color: #4BA3E3; font-size: 22px; }
.page-desc { font-size: 13px; color: #999; margin: 0; }

/* 工具栏卡片 */
.toolbar-card {
  background: #fff; border-radius: 14px; padding: 12px 20px; margin-bottom: 10px;
  display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;
  box-shadow: 0 1px 8px rgba(0,0,0,0.04); flex-shrink: 0;
}
.toolbar-left, .toolbar-right { display: flex; gap: 10px; align-items: center; }
.search-input { width: 220px; }

/* 表格卡片 */
.table-card {
  flex: 1; min-height: 0; display: flex; flex-direction: column;
  background: #fff; border-radius: 14px; padding: 12px;
  box-shadow: 0 1px 8px rgba(0,0,0,0.04);
}
.table-wrap { flex: 1; min-height: 0; }
.pagination-wrap {
  display: flex; justify-content: flex-end; margin-top: 8px; padding-top: 8px;
  border-top: 1px solid #f0f0f0; flex-shrink: 0;
}
</style>
