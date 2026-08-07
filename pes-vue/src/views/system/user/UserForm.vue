
<template>
  <el-dialog
    :title="editData ? '编辑用户' : '新增用户'"
    v-model="visible"
    width="500px"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="!editData">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="form.status" placeholder="请选择状态">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="角色" prop="roleId">
        <el-select
          v-model="form.roleId"
          placeholder="请选择角色"
          style="width: 100%"
          :loading="roleLoading"
        >
          <el-option
            v-for="role in roleOptions"
            :key="role.id"
            :label="role.name"
            :value="role.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { createUser, updateUser } from '@/api/user'
import { getRoleList } from '@/api/role'
import { ElMessage } from 'element-plus'

const props = defineProps({
  editData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

const visible = ref(true)
const formRef = ref()
/** 角色下拉选项 */
const roleOptions = ref([])
const roleLoading = ref(false)
const submitLoading = ref(false)

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1,
  roleId: null
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

watch(() => props.editData, (val) => {
  if (val) {
    // 编辑：回填基本信息与角色（列表数据已包含 roleId，一个用户一个角色）
    form.username = val.username
    form.nickname = val.nickname
    form.email = val.email
    form.phone = val.phone
    form.status = val.status
    form.roleId = val.roleId != null ? val.roleId : null
  } else {
    // 新增：重置表单
    form.username = ''
    form.password = ''
    form.nickname = ''
    form.email = ''
    form.phone = ''
    form.status = 1
    form.roleId = null
  }
}, { immediate: true })

/** 加载全部角色供下拉选择 */
const loadRoles = async () => {
  roleLoading.value = true
  try {
    const res = await getRoleList()
    roleOptions.value = Array.isArray(res) ? res : []
  } catch {
    roleOptions.value = []
  } finally {
    roleLoading.value = false
  }
}

// 弹窗关闭（X / ESC / 遮罩 / 取消）时同步父级 showFormModal，
// 避免父级状态一直为 true，导致再次点击新增/编辑不重新渲染弹窗
watch(visible, (val) => {
  if (!val) emit('close')
})

const handleCancel = () => {
  visible.value = false
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return // 校验失败，不提交
  }

  submitLoading.value = true
  try {
    const payload = { ...form }
    if (props.editData) {
      // 编辑模式：密码留空则不改密码
      if (!payload.password) delete payload.password
    }
    if (props.editData) {
      await updateUser(props.editData.id, payload)
      ElMessage.success('更新成功')
    } else {
      await createUser(payload)
      ElMessage.success('创建成功')
    }
    emit('success')
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 挂载时加载角色下拉选项
onMounted(() => { loadRoles() })
</script>