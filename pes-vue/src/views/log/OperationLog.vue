<!--
  操作日志 — 蓝色主题
-->
<template>
  <div class="log-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Document /></el-icon>
        操作日志
      </h2>
      <p class="page-desc">记录系统中所有用户的操作行为</p>
    </div>

    <!-- 搜索工具栏 -->
    <div class="toolbar-card">
      <div class="toolbar-left" v-if="checkPermission('operlog:view')">
        <el-select
          v-model="searchForm.username"
          placeholder="操作人"
          clearable
          class="search-select"
        >
          <el-option
            v-for="u in userList"
            :key="u.username"
            :label="u.nickname !== u.username ? `${u.nickname} (${u.username})` : u.username"
            :value="u.username"
          />
        </el-select>
        <el-input
          v-model="searchForm.operation"
          placeholder="操作内容"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix><el-icon><Operation /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon> 重置
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="danger" plain v-if="checkPermission('operlog:batch')" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
          <el-icon><Delete /></el-icon> 批量删除 ({{ selectedIds.length }})
        </el-button>
        <el-button type="danger" v-if="checkPermission('operlog:clear')" @click="handleClear">
          <el-icon><DeleteFilled /></el-icon> 清空日志
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <div class="table-wrap">
        <el-table
          :data="tableData"
          border stripe
          height="100%"
          v-loading="loading"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="id" label="ID" min-width="60" align="center" />
          <el-table-column prop="username" label="操作人" min-width="100" align="center">
            <template #default="{ row }">
              <el-tag type="info" effect="plain" size="small">{{ row.username || '-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operation" label="操作内容" min-width="150" show-overflow-tooltip align="center">
            <template #default="{ row }">{{ row.operation || '-' }}</template>
          </el-table-column>
          <el-table-column prop="className" label="类名" min-width="160" show-overflow-tooltip align="center">
            <template #default="{ row }">
              <span class="mono-text">{{ row.className || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="methodName" label="方法" min-width="120" align="center">
            <template #default="{ row }">
              <el-tag size="small" effect="plain">{{ row.methodName || '-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="耗时" min-min-width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.time > 200 ? 'warning' : row.time > 500 ? 'danger' : 'success'" size="small">
                {{ row.time }}ms
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" min-min-width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" effect="dark">
                {{ row.status === 1 ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="操作时间" min-width="160" align="center">
            <template #default="{ row }">
              {{ row.createTime ? row.createTime.replace('T', ' ') : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="80" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="danger" v-if="checkPermission('operlog:delete')" @click="handleDelete(row.id)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { getOperLogList, deleteOperLog, batchDeleteOperLog, clearOperLog } from '@/api/log'
import { usePermission } from '@/composables/usePermission'
const { checkPermission } = usePermission()
import { getUserOptions } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, User, Operation, Search, Refresh, Delete, DeleteFilled } from '@element-plus/icons-vue'

const tableData = ref([])
const loading = ref(false)
const selectedIds = ref([])
const userList = ref([])

const searchForm = reactive({ username: '', operation: '' })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOperLogList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...(searchForm.username && { username: searchForm.username }),
      ...(searchForm.operation && { operation: searchForm.operation })
    })
    // 统一格式返回 { records, total, ... }
    tableData.value = res.records || []
    pagination.total = res.total || 0
  } catch {
    ElMessage.error('获取操作日志失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleReset = () => { searchForm.username = ''; searchForm.operation = ''; pagination.page = 1; loadData() }

let searchTimer = null
watch([() => searchForm.username, () => searchForm.operation], () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { pagination.page = 1; loadData() }, 300)
})

const handleSelectionChange = (sel) => { selectedIds.value = sel.map(i => i.id) }

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该条日志？', '提示', { type: 'warning' })
    await deleteOperLog(id)
    ElMessage.success('已删除')
    loadData()
  } catch { /* cancel */ }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 条？`, '提示', { type: 'warning' })
    await batchDeleteOperLog(selectedIds.value)
    ElMessage.success(`已删除 ${selectedIds.value.length} 条`)
    selectedIds.value = []
    loadData()
  } catch { /* cancel */ }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定清空全部操作日志？此操作不可恢复！', '危险操作', { type: 'error' })
    await clearOperLog()
    ElMessage.success('日志已清空')
    loadData()
  } catch { /* cancel */ }
}

onMounted(() => {
  loadData()
  getUserOptions().then(res => { userList.value = Array.isArray(res) ? res : [] }).catch(() => {})
})
</script>

<style scoped>
.log-page { display: flex; flex-direction: column; padding: 4px 0; }

/* 页面标题 */
.page-header { margin-bottom: 10px; flex-shrink: 0; }
.page-title { font-size: 20px; font-weight: 700; color: #1a1a2e; display: flex; align-items: center; gap: 8px; margin: 0 0 4px; }
.page-title .el-icon { color: #4BA3E3; font-size: 22px; }
.page-desc { font-size: 13px; color: #999; margin: 0; }

/* 工具栏 */
.toolbar-card {
  background: #fff; border-radius: 14px; padding: 12px 20px; margin-bottom: 10px;
  display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;
  box-shadow: 0 1px 8px rgba(0,0,0,0.04);
  flex-shrink: 0;
}
.toolbar-left, .toolbar-right { display: flex; gap: 10px; align-items: center; }
.search-input { width: 180px; }
.search-select { width: 150px; }

/* 表格 */
.table-card {
  flex: 1; min-height: 0; display: flex; flex-direction: column;
  background: #fff; border-radius: 14px; padding: 12px;
  box-shadow: 0 1px 8px rgba(0,0,0,0.04);
}
.table-wrap { flex: 1; min-height: 0; }
.mono-text { font-family: 'Courier New', monospace; font-size: 12px; color: #666; }

/* 分页 */
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 8px; padding-top: 8px; border-top: 1px solid #f0f0f0; }

/* Element Plus 分页蓝色主题 */
.pagination-wrap :deep(.el-pagination.is-background .el-pager li.is-active) { background: #4BA3E3; }
</style>
