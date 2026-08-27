<!--
  菜单管理 — 蓝色主题
  默认树形表折叠，支持展开/折叠全部切换
-->
<template>
  <div class="menu-manage">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Menu /></el-icon>
        菜单管理
      </h2>
      <p class="page-desc">管理系统菜单结构和权限标识</p>
    </div>

    <!-- 搜索工具栏 -->
    <div class="toolbar-card">
      <div class="toolbar-left" v-if="checkPermission('menu:view')">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入菜单名称搜索"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon> 重置
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-button @click="toggleExpandAll">
          <el-icon><Fold v-if="allExpanded" /><Expand v-else /></el-icon>
          {{ allExpanded ? '折叠全部' : '展开全部' }}
        </el-button>
        <el-button type="success" v-if="checkPermission('menu:add')" @click="openCreateModal">
          <el-icon><Plus /></el-icon> 新增菜单
        </el-button>
      </div>
    </div>

    <!-- 树形表格 -->
    <div class="table-card">
      <div class="table-wrap">
        <el-table
        ref="tableRef"
        :data="filteredMenuTree"
        :key="tableKey"
        :default-expand-all="allExpanded"
        border stripe
        row-key="id"
        height="100%"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        v-loading="loading"
      >
        <el-table-column prop="name" label="菜单名称" min-width="160" align="center">
          <template #default="{ row }">
            <span :class="{ 'menu-name-root': row.parentId === 0 }">
              {{ row.name || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" min-width="80" align="center">
          <template #default="{ row }">
            <span class="icon-cell">
              <el-icon v-if="row._icon" :size="18"><component :is="row._icon" /></el-icon>
              <span v-else class="icon-name">{{ row.icon || '-' }}</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="140" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.path" type="info" size="small" effect="plain">{{ row.path }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="permission" label="权限标识" min-width="140" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.permission" type="warning" size="small" effect="plain">{{ row.permission }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" min-width="60" align="center">
          <template #default="{ row }">{{ row.sort != null ? row.sort : '-' }}</template>
        </el-table-column>
        <el-table-column prop="type" label="类型" min-width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 0 ? 'warning' : (row.type === 1 ? '' : 'info')" size="small">
              {{ row.type === 0 ? '目录' : (row.type === 1 ? '菜单' : '按钮') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" align="center">
          <template #default="{ row }">
            {{ row.createTime ? row.createTime.replace('T', ' ') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" v-if="checkPermission('menu:edit')" @click="openEditModal(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button size="small" type="success" v-if="checkPermission('menu:add')" @click="openAddChildModal(row)">
              <el-icon><CirclePlus /></el-icon> 添加子菜单
            </el-button>
            <el-button size="small" type="danger" v-if="checkPermission('menu:delete')" @click="handleDelete(row.id)">
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :title="dialogTitle"
      v-model="showFormModal"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentMenuTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="顶级菜单"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="0">目录</el-radio>
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.type === 0 || form.type === 1" label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名，如 Setting、User" />
        </el-form-item>
        <el-form-item v-if="form.type === 1" label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由路径，如 /system/user" />
        </el-form-item>
        <el-form-item v-if="form.type === 1" label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路径，如 system/user/index" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permission">
          <el-input v-model="form.permission" placeholder="如 user:list、role:add" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFormModal = false">取消</el-button>
        <el-button type="primary" @click="handleFormSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, nextTick } from 'vue'
import { getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/menu'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Menu, Search, Refresh, Fold, Expand, Plus, Edit, CirclePlus, Delete } from '@element-plus/icons-vue'
import { getIcon } from '@/utils/iconMap'
import { usePermission } from '@/composables/usePermission'
const { checkPermission } = usePermission()

// ==================== 数据 ====================
const menuTree = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const showFormModal = ref(false)
const editData = ref(null)
const formRef = ref()
const tableRef = ref()
const searchKeyword = ref('')
const allExpanded = ref(false)
const tableKey = ref(0)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = reactive({
  parentId: 0,
  name: '',
  type: 1,
  path: '',
  component: '',
  icon: '',
  permission: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

// ==================== 计算属性 ====================
const dialogTitle = computed(() => {
  if (!editData.value) return '新增菜单'
  return editData.value._isChild ? '添加子菜单' : '编辑菜单'
})

const filteredMenuTree = computed(() => {
  const list = menuTree.value
  total.value = list.length
  const start = (page.value - 1) * pageSize.value
  return list.slice(start, start + pageSize.value)
})

const parentMenuTree = computed(() => {
  const filterDir = (nodes) => {
    const result = []
    for (const node of nodes) {
      if (node.type === 0 || node.type === 1) { // 目录和菜单都可作为上级
        result.push({
          id: node.id,
          name: node.name,
          children: node.children ? filterDir(node.children) : []
        })
      }
    }
    return result
  }
  const list = menuTree.value || []
  return filterDir(list)
})

// ==================== 数据加载 ====================
/** 递归给每个节点挂 _icon */
const attachIcons = (nodes) => {
  if (!nodes) return []
  return nodes.map(n => ({ ...n, _icon: getIcon(n.icon), children: attachIcons(n.children) }))
}

const loadData = async () => {
  loading.value = true
  try {
    const params = searchKeyword.value ? { keyword: searchKeyword.value } : {}
    const res = await getMenuTree(params)
    menuTree.value = attachIcons(res || [])
  } catch {
    ElMessage.error('获取菜单树失败')
  } finally {
    loading.value = false
  }
}

// ==================== 展开/折叠全部 ====================
/**
 * 切换全部展开/折叠
 * 通过 default-expand-all + 强制重建表格来实现，避免 toggleRowExpansion
 * 在深层嵌套时因父行未展开导致子行无法匹配的问题
 */
const toggleExpandAll = async () => {
  const target = !allExpanded.value
  allExpanded.value = target
  // 等待 DOM 更新后再切换 key 强制重建
  await nextTick()
  tableKey.value++
}

// ==================== 搜索 ====================
const handleSearch = () => { page.value = 1; loadData() }
const handleReset = () => { searchKeyword.value = ''; page.value = 1; loadData() }

let searchTimer = null
watch(searchKeyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 1; loadData() }, 300)
})

// ==================== 增删改 ====================
const openCreateModal = () => {
  editData.value = null
  form.parentId = 0
  form.name = ''
  form.type = 1
  form.path = ''
  form.component = ''
  form.icon = ''
  form.permission = ''
  form.sort = 0
  form.status = 1
  showFormModal.value = true
}

const openEditModal = (row) => {
  editData.value = row
  form.parentId = row.parentId || 0
  form.name = row.name
  form.type = row.type || 1
  form.path = row.path || ''
  form.component = row.component || ''
  form.icon = row.icon || ''
  form.permission = row.permission || ''
  form.sort = row.sort || 0
  form.status = row.status != null ? row.status : 1
  showFormModal.value = true
}

const openAddChildModal = (row) => {
  editData.value = { ...row, _isChild: true }
  form.parentId = row.id
  form.name = ''
  form.type = 1
  form.path = ''
  form.component = ''
  form.icon = ''
  form.permission = ''
  form.sort = 0
  form.status = 1
  showFormModal.value = true
}

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitLoading.value = true
  try {
    if (editData.value && !editData.value._isChild) {
      await updateMenu(editData.value.id, form)
      ElMessage.success('更新成功')
    } else {
      await createMenu(form)
      ElMessage.success('创建成功')
    }
    showFormModal.value = false
    await loadData()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗？子菜单将一并删除！', '警告', { type: 'warning' })
    await deleteMenu(id)
    ElMessage.success('删除成功')
    await loadData()
  } catch { /* 取消 */ }
}

onMounted(() => loadData())
</script>

<style scoped>
.menu-manage { display: flex; flex-direction: column; padding: 4px 0; }

/* 页面标题 */
.page-header { margin-bottom: 10px; flex-shrink: 0; }
.page-title {
  font-size: 20px; font-weight: 700; color: #1a1a2e;
  display: flex; align-items: center; gap: 8px; margin: 0 0 4px;
}
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
.search-input { width: 220px; }

/* 表格 */
.table-card {
  flex: 1; min-height: 0;
  display: flex; flex-direction: column;
  background: #fff; border-radius: 14px; padding: 12px;
  box-shadow: 0 1px 8px rgba(0,0,0,0.04);
}
.table-wrap { flex: 1; min-height: 0; }
.pagination-wrap {
  display: flex; justify-content: flex-end;
  margin-top: 8px; padding-top: 8px;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0;
}
.menu-name-root { font-weight: 700; color: #1a1a2e; }
.icon-cell { display: flex; align-items: center; justify-content: center; gap: 4px; color: #4BA3E3; }
.icon-name { font-size: 12px; color: #666; font-family: monospace; }

/* 弹窗内 el-tree-select 高度适配 */
:deep(.el-tree-select) { width: 100%; }
</style>
