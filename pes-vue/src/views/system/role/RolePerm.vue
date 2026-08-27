<template>
  <el-dialog
    v-model="visible"
    :title="''"
    width="580px"
    class="role-perm-dialog"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <template #header>
      <div class="dialog-header">
        <div class="header-title">
          <el-icon class="title-icon"><Key /></el-icon>
          <span>分配权限</span>
        </div>
        <div class="header-role" v-if="roleInfo">
          <el-tag type="primary" effect="dark" size="large">
            {{ roleInfo.name }}
          </el-tag>
        </div>
      </div>
    </template>

    <div class="perm-tips" v-if="!loading">
      <el-alert type="info" :closable="false" show-icon>
        <template #title>
          <span>目录和菜单控制页面/导航的可见性，按钮控制页面内的操作权限。已选中 <strong>{{ checkedCount }}</strong> 项</span>
        </template>
      </el-alert>
    </div>

    <div class="perm-toolbar" v-if="!loading">
      <el-input
        v-model="filterText"
        placeholder="搜索目录/菜单/按钮"
        size="default"
        clearable
        class="filter-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button-group>
        <el-button size="default" @click="checkAll">
          <el-icon><Select /></el-icon>
          全选
        </el-button>
        <el-button size="default" @click="uncheckAll">
          <el-icon><CloseBold /></el-icon>
          取消
        </el-button>
        <el-button size="default" @click="toggleExpand">
          <el-icon><Fold /></el-icon>
          {{ isExpanded ? '收起' : '展开' }}
        </el-button>
      </el-button-group>
    </div>

    <div class="perm-tree-wrapper" v-loading="loading">
      <div v-if="!loading && menuTree.length === 0" class="empty-state">
        <el-empty description="暂无菜单数据" :image-size="80" />
      </div>
      <el-tree
        v-else
        ref="treeRef"
        :data="menuTree"
        :props="treeProps"
        :filter-node-method="filterNode"
        show-checkbox
        check-strictly
        node-key="id"
        default-expand-all
        highlight-current
        class="perm-tree"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <el-icon class="node-icon" :class="data.type === 0 ? 'is-directory' : (data.type === 2 ? 'is-button' : 'is-menu')">
              <component :is="getNodeIcon(data)" />
            </el-icon>
            <span class="node-label">{{ node.label }}</span>
            <el-tag v-if="data.type === 0" size="small" type="warning" class="node-tag">目录</el-tag>
            <el-tag v-else-if="data.type === 2" size="small" type="info" class="node-tag">按钮</el-tag>
            <el-tag v-else size="small" type="success" class="node-tag">菜单</el-tag>
          </span>
        </template>
      </el-tree>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <span class="footer-count">
          已选择 <strong>{{ checkedCount }}</strong> 项权限
        </span>
        <div class="footer-actions">
          <el-button @click="handleCancel" size="default">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting" size="default">
            <el-icon v-if="!submitting"><Check /></el-icon>
            保存
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onMounted, computed, nextTick } from 'vue'
import { getMenuTree } from '@/api/menu'
import { getRoleById, assignMenu } from '@/api/role'
import { ElMessage } from 'element-plus'
import {
  Key, Search, Select, CloseBold, Fold, Check,
  Menu, Link, Operation, Grid, Setting, Document, Folder
} from '@element-plus/icons-vue'

const props = defineProps({
  roleId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['close', 'success'])

const visible = ref(true)
const treeRef = ref()
const menuTree = ref([])
const filterText = ref('')
const loading = ref(true)
const submitting = ref(false)
const roleInfo = ref(null)
const isExpanded = ref(true)

const treeProps = {
  label: 'name',
  children: 'children'
}

const checkedCount = computed(() => {
  if (!treeRef.value) return 0
  return treeRef.value.getCheckedKeys().length
})

const getNodeIcon = (data) => {
  if (data.type === 0) return 'Folder'
  if (data.type === 2) return 'Operation'
  const iconMap = {
    'Setting': 'Setting',
    'Menu': 'Menu',
    'Document': 'Document',
    'DataBoard': 'Grid',
    'User': 'Menu',
    'UserFilled': 'Menu',
    'Tickets': 'Document',
    'Stamp': 'Document'
  }
  return iconMap[data.icon] || 'Menu'
}

const filterNode = (value, data) => {
  if (!value) return true
  return data.name.toLowerCase().includes(value.toLowerCase())
}

const getAllKeys = (nodes) => {
  const keys = []
  const walk = (list) => {
    list.forEach(item => {
      keys.push(item.id)
      if (item.children && item.children.length) walk(item.children)
    })
  }
  walk(nodes)
  return keys
}

watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

watch(visible, (val) => {
  if (!val) emit('close')
})

const loadData = async () => {
  loading.value = true
  try {
    const [treeRes, roleRes] = await Promise.all([getMenuTree(), getRoleById(props.roleId)])
    menuTree.value = treeRes || []
    roleInfo.value = roleRes

    if (roleRes && roleRes.menuIds && roleRes.menuIds.length > 0) {
      await nextTick()
      const validIds = roleRes.menuIds.filter(id => {
        const node = treeRef.value?.getNode(id)
        return node && node.data
      })
      if (validIds.length > 0) {
        treeRef.value?.setCheckedKeys(validIds)
      }
    }
  } catch (error) {
    ElMessage.error('加载菜单数据失败')
  } finally {
    loading.value = false
  }
}

const checkAll = () => {
  const allKeys = getAllKeys(menuTree.value)
  treeRef.value?.setCheckedKeys(allKeys)
}

const uncheckAll = () => {
  treeRef.value?.setCheckedKeys([])
}

const toggleExpand = () => {
  const tree = treeRef.value
  if (!tree) return

  const walkAndToggle = (nodes) => {
    nodes.forEach(data => {
      const node = tree.getNode(data.id)
      if (node && node.childNodes && node.childNodes.length > 0) {
        if (isExpanded.value) {
          node.collapse()
        } else {
          node.expand()
        }
      }
      if (data.children && data.children.length) {
        walkAndToggle(data.children)
      }
    })
  }
  walkAndToggle(menuTree.value)
  isExpanded.value = !isExpanded.value
}

const handleCancel = () => {
  visible.value = false
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    const menuIds = treeRef.value.getCheckedKeys()
    await assignMenu(props.roleId, { menuIds })
    ElMessage.success('权限分配成功')
    emit('success')
    visible.value = false
  } catch (error) {
    ElMessage.error('权限分配失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.role-perm-dialog :deep(.el-dialog__header) {
  padding: 0;
  margin: 0;
  border-bottom: 1px solid #f0f0f0;
}

.role-perm-dialog :deep(.el-dialog__body) {
  padding: 16px 20px;
}

.role-perm-dialog :deep(.el-dialog__footer) {
  padding: 0;
}

.dialog-header {
  padding: 18px 20px 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 17px;
  font-weight: 600;
  color: #303133;
}

.title-icon {
  font-size: 20px;
  color: #4BA3E3;
}

.header-role {
  display: flex;
  align-items: center;
  gap: 8px;
}

.perm-tips {
  margin-bottom: 14px;
}

.perm-tips :deep(.el-alert__title) {
  font-size: 13px;
}

.perm-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.filter-input {
  width: 200px;
  flex-shrink: 0;
}

.perm-tree-wrapper {
  height: 360px;
  overflow-y: auto;
  border: 1px solid #e8eaed;
  border-radius: 14px;
  padding: 10px 6px;
  background: #f5f7fa;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 260px;
}

.perm-tree {
  background: transparent;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  padding-right: 8px;
}

.node-icon {
  font-size: 15px;
  flex-shrink: 0;
}

.node-icon.is-directory {
  color: #E6A23C;
}

.node-icon.is-menu {
  color: #4BA3E3;
}

.node-icon.is-button {
  color: #909399;
}

.node-label {
  font-size: 14px;
  color: #303133;
}

.node-tag {
  margin-left: auto;
  flex-shrink: 0;
}

.perm-tree :deep(.el-tree-node__content) {
  border-radius: 4px;
  padding: 2px 4px;
  transition: background 0.2s;
}

.perm-tree :deep(.el-tree-node__content:hover) {
  background: #ecf5ff;
}

.perm-tree :deep(.el-tree-node.is-checked > .el-tree-node__content) {
  background: #ecf5ff;
}

.perm-tree :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #4BA3E3;
  border-color: #4BA3E3;
}

.perm-tree :deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner) {
  background-color: #4BA3E3;
  border-color: #4BA3E3;
}

.perm-tree :deep(.el-tree-node__expand-icon) {
  color: #909399;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  background: #f5f7fa;
  border-radius: 0 0 14px 14px;
}
</style>
