<template>
  <div class="profile-page">
    <!-- 用户概览卡片 -->
    <div class="hero-card">
      <div class="hero-avatar">
        <span class="avatar-text">{{ (userInfo.nickname || userInfo.username || '管')[0] }}</span>
      </div>
      <div class="hero-info">
        <h1 class="hero-name">{{ userInfo.nickname || userInfo.username || '管理员' }}</h1>
        <p class="hero-detail">
          <span>@{{ userInfo.username }}</span>
          <span class="hero-dot">·</span>
          <span>{{ userStore.roles?.length ? userStore.roles.join(' / ') : '普通用户' }}</span>
        </p>
      </div>
      <div class="hero-stats">
        <div class="stat-item">
          <span class="stat-num">{{ userStore.permissions?.length || 0 }}</span>
          <span class="stat-label">权限数</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">{{ userStore.roles?.length || 0 }}</span>
          <span class="stat-label">角色数</span>
        </div>
      </div>
    </div>

    <!-- 主体双栏 -->
    <div class="profile-body">
      <!-- 左侧：个人信息 -->
      <div class="profile-card">
        <div class="card-title">
          <el-icon><UserFilled /></el-icon> 个人信息
          <div class="card-actions">
            <template v-if="!editing">
              <el-button type="primary" size="small" @click="startEdit"><el-icon><Edit /></el-icon> 编辑</el-button>
            </template>
            <template v-else>
              <el-button size="small" @click="cancelEdit">取消</el-button>
              <el-button type="primary" size="small" @click="saveProfile" :loading="saving">保存</el-button>
            </template>
          </div>
        </div>
        <el-form :model="editForm" label-width="80px" class="profile-form">
          <el-form-item label="用户名">
            <el-input v-model="editForm.username" disabled>
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickname" placeholder="请输入昵称" :disabled="!editing">
              <template #prefix><el-icon><UserFilled /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" placeholder="请输入邮箱" :disabled="!editing">
              <template #prefix><el-icon><Message /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" placeholder="请输入手机号" :disabled="!editing">
              <template #prefix><el-icon><Phone /></el-icon></template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>

      <!-- 右侧：修改密码 -->
      <div class="profile-card">
        <div class="card-title">
          <el-icon><Lock /></el-icon> 修改密码
        </div>
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px" class="profile-form">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password>
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" placeholder="至少6位" show-password>
              <template #prefix><el-icon><Key /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请确认新密码" show-password>
              <template #prefix><el-icon><CircleCheck /></el-icon></template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="pwd-btn" @click="handleChangePwd">确认修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { getUserProfile, updateUserProfile, changePassword } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User, UserFilled, Message, Phone, Lock, Key, CircleCheck, Edit } from '@element-plus/icons-vue'

const userStore = useUserStore()
const pwdFormRef = ref()
const editing = ref(false)
const saving = ref(false)

const userInfo = reactive({ username: '', nickname: '', email: '', phone: '' })
const editForm = reactive({ username: '', nickname: '', email: '', phone: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (_, v, cb) => v !== pwdForm.newPassword ? cb(new Error('两次输入的密码不一致')) : cb(), trigger: 'blur' }
  ]
}

const loadProfile = async () => {
  try {
    const res = await getUserProfile()
    userInfo.username = res.username || userStore.username
    userInfo.nickname = res.nickname || userStore.nickname
    userInfo.email = res.email || ''
    userInfo.phone = res.phone || ''
    Object.assign(editForm, { ...userInfo })
  } catch {
    userInfo.username = userStore.username
    userInfo.nickname = userStore.nickname
  }
}

const startEdit = () => {
  Object.assign(editForm, { ...userInfo })
  editing.value = true
}
const cancelEdit = () => { editing.value = false }

const saveProfile = async () => {
  saving.value = true
  try {
    await updateUserProfile({ nickname: editForm.nickname || undefined, email: editForm.email || undefined, phone: editForm.phone || undefined })
    Object.assign(userInfo, { nickname: editForm.nickname, email: editForm.email, phone: editForm.phone })
    userStore.setUserInfo({ username: userInfo.username, nickname: userInfo.nickname })
    editing.value = false
    ElMessage.success('个人资料已更新')
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

const handleChangePwd = async () => {
  try { await pwdFormRef.value.validate() } catch { return }
  try {
    await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''; pwdForm.newPassword = ''; pwdForm.confirmPassword = ''
  } catch { ElMessage.error('密码修改失败') }
}

onMounted(() => loadProfile())
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  padding: 4px 0;
  height: 100%;
}

/* 用户概览卡片 */
.hero-card {
  display: flex;
  align-items: center;
  gap: 24px;
  background: linear-gradient(135deg, #1E6CB8 0%, #4BA3E3 60%, #89CFF0 100%);
  border-radius: 14px;
  padding: 24px 32px;
  margin-bottom: 10px;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(75, 163, 227, 0.2);
}
.hero-avatar {
  width: 64px; height: 64px;
  border-radius: 50%;
  background: rgba(255,255,255,0.22);
  border: 2.5px solid rgba(255,255,255,0.4);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.avatar-text { font-size: 26px; font-weight: 700; color: #fff; }
.hero-info { flex: 1; min-width: 0; }
.hero-name { font-size: 20px; font-weight: 700; color: #fff; margin: 0 0 4px; }
.hero-detail { font-size: 13px; color: rgba(255,255,255,0.8); margin: 0; display: flex; align-items: center; gap: 6px; }
.hero-dot { color: rgba(255,255,255,0.4); }
.hero-stats { display: flex; gap: 24px; flex-shrink: 0; }
.stat-item { text-align: center; }
.stat-num { font-size: 22px; font-weight: 700; color: #F9A825; display: block; }
.stat-label { font-size: 12px; color: rgba(255,255,255,0.7); }

/* 主体双栏 */
.profile-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  flex: 1;
  min-height: 0;
}
.profile-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px;
  box-shadow: 0 1px 8px rgba(0,0,0,0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px; font-weight: 700; color: #1a1a2e;
  margin-bottom: 16px; padding-bottom: 12px;
  border-bottom: 1.5px solid #edf2f7;
  flex-shrink: 0;
}
.card-title .el-icon { color: #4BA3E3; }
.card-actions { margin-left: auto; display: flex; gap: 8px; }

/* 表单 */
.profile-form {
  flex: 1;
  overflow-y: auto;
  padding: 0 4px;
}
.profile-form :deep(.el-form-item) { margin-bottom: 16px; }
.profile-form :deep(.el-input .el-input__wrapper) { border-radius: 10px; }
.profile-form :deep(.el-input.is-disabled .el-input__wrapper) { background: #f8fafc; }

.pwd-btn {
  width: 100%;
  height: 40px;
  border-radius: 10px;
  font-size: 14px;
  background: linear-gradient(135deg, #4BA3E3, #1E6CB8);
  border: none;
  letter-spacing: 2px;
}
.pwd-btn:hover { background: linear-gradient(135deg, #5bb5f0, #2b7cc8); }

@media (max-width: 768px) {
  .hero-card { flex-direction: column; text-align: center; padding: 20px; }
  .hero-stats { justify-content: center; }
  .profile-body { grid-template-columns: 1fr; }
}
</style>
