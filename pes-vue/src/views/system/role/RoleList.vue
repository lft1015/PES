<template>
  <div class="role-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title"><el-icon><UserFilled /></el-icon>角色管理</h2>
      <p class="page-desc">管理系统角色和权限分配</p>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar-card">
      <div class="toolbar-left">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入角色名称搜索"
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
        <el-button type="success" @click="openCreateModal"><el-icon><Plus /></el-icon> 新增角色</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <div class="table-wrap">
        <el-table :data="tableData" border stripe height="100%" v-loading="loading">
          <el-table-column prop="id" label="ID" min-width="60" align="center" />
          <el-table-column prop="name" label="角色名称" min-width="100" align="center">
            <template #default="{ row }">{{ row.name || '-' }}</template>
          </el-table-column>
          <el-table-column prop="code" label="角色编码" min-width="100" align="center">
            <template #default="{ row }">{{ row.code || '-' }}</template>
          </el-table-column>
          <el-table-column prop="description" label="描述" min-width="160" align="center">
            <template #default="{ row }">{{ row.description || '-' }}</template>
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
          <el-table-column label="操作" min-width="230" align="center">
            <template #default="scope">
              <el-button size="small" type="primary" @click="openEditModal(scope.row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button size="small" type="warning" @click="openAssignModal(scope.row)">
                <el-icon><Key /></el-icon> 分配权限
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">
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

    <el-dialog :title="editData ? '编辑角色' : '新增角色'" v-model="showFormModal" width="450px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="如 admin、editor" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFormModal = false">取消</el-button>
        <el-button type="primary" @click="handleFormSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <RolePerm v-if="showAssignModal" :role-id="assignRoleId" @close="showAssignModal = false" @success="loadData" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { getRoleList, createRole, updateRole, deleteRole } from '@/api/role'
import { ElMessage } from 'element-plus'
import { UserFilled, Search, Refresh, Plus, Edit, Key, Delete } from '@element-plus/icons-vue'
import RolePerm from './RolePerm.vue'

const allData = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const showFormModal = ref(false)
const showAssignModal = ref(false)
const editData = ref(null)
const assignRoleId = ref(null)
const formRef = ref()
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchKeyword = ref('')

const form = reactive({ name: '', code: '', description: '', status: 1 })
const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const tableData = computed(() => {
  total.value = allData.value.length
  const start = (page.value - 1) * pageSize.value
  return allData.value.slice(start, start + pageSize.value)
})

const loadData = async () => {
  loading.value = true
  try {
    const params = searchKeyword.value ? { keyword: searchKeyword.value } : {}
    const res = await getRoleList(params)
    allData.value = Array.isArray(res) ? res : []
    page.value = 1
  } catch { ElMessage.error('获取角色列表失败') }
  finally { loading.value = false }
}

const handleSearch = () => { page.value = 1; loadData() }
const handleReset = () => { searchKeyword.value = ''; page.value = 1; loadData() }

let searchTimer = null
watch(searchKeyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 1; loadData() }, 300)
})

const openCreateModal = () => {
  editData.value = null
  form.name = ''; form.code = ''; form.description = ''; form.status = 1
  showFormModal.value = true
}
const openEditModal = (row) => {
  editData.value = row
  form.name = row.name; form.code = row.code; form.description = row.description; form.status = row.status
  showFormModal.value = true
}
const openAssignModal = (row) => { assignRoleId.value = row.id; showAssignModal.value = true }

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (editData.value) {
      await updateRole(editData.value.id, form)
      ElMessage.success('更新成功')
    } else {
      await createRole(form)
      ElMessage.success('创建成功')
    }
    showFormModal.value = false
    loadData()
  } catch { /* validation or API error */ }
  finally { submitLoading.value = false }
}

const handleDelete = async (id) => {
  try {
    await deleteRole(id)
    ElMessage.success('删除成功')
    loadData()
  } catch { ElMessage.error('删除失败') }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.role-list { display: flex; flex-direction: column; padding: 4px 0; }

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
