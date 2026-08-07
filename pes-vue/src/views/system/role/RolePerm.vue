<template>
  <el-dialog title="分配权限" v-model="visible" width="600px">
    <el-tree
      ref="treeRef"
      :data="menuTree"
      :props="treeProps"
      show-checkbox
      node-key="id"
      default-expand-all
    />
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getMenuTree } from '@/api/menu'
import { assignMenu } from '@/api/role'
import { ElMessage } from 'element-plus'

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

const treeProps = {
  label: 'name',
  children: 'children'
}

const loadMenuTree = async () => {
  try {
    const res = await getMenuTree()
    menuTree.value = res
  } catch (error) {
    ElMessage.error('获取菜单树失败')
  }
}

// 弹窗关闭（X / ESC / 遮罩 / 取消）时同步父级 showAssignModal，
// 避免父级状态一直为 true，导致再次点击分配权限不重新渲染弹窗
watch(visible, (val) => {
  if (!val) emit('close')
})

const handleCancel = () => {
  visible.value = false
}

const handleSubmit = async () => {
  try {
    const checkedKeys = treeRef.value.getCheckedKeys()
    const halfCheckedKeys = treeRef.value.getHalfCheckedKeys()
    const menuIds = [...checkedKeys, ...halfCheckedKeys]
    await assignMenu(props.roleId, { menuIds })
    ElMessage.success('分配成功')
    emit('success')
  } catch (error) {
    ElMessage.error('分配失败')
  }
}

onMounted(() => {
  loadMenuTree()
})
</script>
