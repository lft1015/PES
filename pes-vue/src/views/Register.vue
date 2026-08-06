<template>
  <div class="register-page">
    <!-- 背景装饰 -->
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <div class="register-container">
      <!-- 左侧品牌区 -->
      <div class="register-brand">
        <div class="brand-content">
          <div class="brand-logo">
            <span class="logo-icon">P</span>
          </div>
          <h1 class="brand-title">加入 PES</h1>
          <p class="brand-desc">创建您的账号，开启企业级权限管理体验</p>
          <div class="brand-features">
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>简单快速注册</span>
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>数据安全加密</span>
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>免费开始使用</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="register-form-wrapper">
        <div class="form-header">
          <h2>创建账号</h2>
          <p>填写以下信息完成注册</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="register-form"
          @keyup.enter="handleRegister"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码（至少6位）"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请确认密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="nickname">
            <el-input
              v-model="form.nickname"
              placeholder="昵称（选填，默认使用用户名）"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><UserFilled /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              placeholder="邮箱（选填）"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="phone">
            <el-input
              v-model="form.phone"
              placeholder="手机号（选填）"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="captcha">
            <div class="captcha-row">
              <el-input
                v-model="form.captcha"
                placeholder="验证码"
                size="large"
                class="captcha-input"
                maxlength="4"
              >
                <template #prefix>
                  <el-icon><Key /></el-icon>
                </template>
              </el-input>
              <Captcha ref="captchaRef" @key-change="onCaptchaKeyChange" />
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="register-btn"
              :loading="loading"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '注 册' }}
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span class="login-link">
              已有账号？<el-link type="primary" :underline="false" @click="goLogin">立即登录</el-link>
            </span>
          </div>
        </el-form>
      </div>
    </div>

    <div class="register-copyright">
      Copyright &copy; {{ new Date().getFullYear() }} PES. All Rights Reserved.
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { User, Lock, Key, UserFilled, Message, Phone } from '@element-plus/icons-vue'
import Captcha from '@/components/common/Captcha.vue'

const router = useRouter()
const formRef = ref()
const captchaRef = ref()
const captchaKey = ref('')
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: '',
  phone: '',
  captcha: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

const onCaptchaKeyChange = (key) => {
  captchaKey.value = key
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    await register({
      username: form.username,
      password: form.password,
      nickname: form.nickname || undefined,
      email: form.email || undefined,
      phone: form.phone || undefined,
      captcha: form.captcha,
      captchaKey: captchaKey.value
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    const msg = error?.response?.data?.message || error?.message || '注册失败，请稍后重试'
    ElMessage.error(msg)
    // 刷新验证码
    captchaRef.value?.refreshCaptcha()
    form.captcha = ''
  } finally {
    loading.value = false
  }
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 40%, #0f3460 100%);
  overflow: hidden;
}

/* 背景装饰形状 */
.bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
}

.shape-1 {
  width: 600px;
  height: 600px;
  background: #67c23a;
  top: -300px;
  right: -200px;
  animation: float 20s ease-in-out infinite;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: #409eff;
  bottom: -200px;
  left: -100px;
  animation: float 25s ease-in-out infinite reverse;
}

.shape-3 {
  width: 200px;
  height: 200px;
  background: #e6a23c;
  top: 50%;
  left: 10%;
  animation: float 15s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

/* 注册卡片 */
.register-container {
  position: relative;
  z-index: 1;
  display: flex;
  width: 920px;
  min-height: 640px;
  background: rgba(255, 255, 255, 0.97);
  border-radius: 16px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  backdrop-filter: blur(10px);
}

/* 左侧品牌区 */
.register-brand {
  flex: 0 0 380px;
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 40px;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.register-brand::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
  top: -100px;
  right: -100px;
}

.register-brand::after {
  content: '';
  position: absolute;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 50%;
  bottom: -50px;
  left: -50px;
}

.brand-content {
  position: relative;
  z-index: 1;
  text-align: center;
}

.brand-logo {
  margin-bottom: 24px;
}

.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.2);
  border: 3px solid rgba(255, 255, 255, 0.4);
  border-radius: 18px;
  font-size: 34px;
  font-weight: 700;
  backdrop-filter: blur(10px);
}

.brand-title {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 8px;
  letter-spacing: 2px;
}

.brand-desc {
  font-size: 13px;
  opacity: 0.8;
  margin-bottom: 40px;
  line-height: 1.6;
  padding: 0 10px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
  text-align: left;
  max-width: 200px;
  margin: 0 auto;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.feature-dot {
  width: 6px;
  height: 6px;
  background: #fff;
  border-radius: 50%;
  flex-shrink: 0;
}

/* 右侧表单区 */
.register-form-wrapper {
  flex: 1;
  padding: 40px 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow-y: auto;
}

.form-header {
  margin-bottom: 28px;
}

.form-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 6px;
}

.form-header p {
  font-size: 14px;
  color: #999;
}

.register-form {
  width: 100%;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.captcha-row {
  display: flex;
  align-items: flex-start;
  gap: 0;
}

.captcha-input {
  flex: 1;
}

.register-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 8px;
  margin-top: 4px;
}

.form-footer {
  text-align: center;
  margin-top: -4px;
}

.login-link {
  font-size: 14px;
  color: #999;
}

/* 底部版权 */
.register-copyright {
  position: absolute;
  bottom: 20px;
  z-index: 1;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  text-align: center;
}

/* 响应式 */
@media (max-width: 960px) {
  .register-container {
    width: 94vw;
    flex-direction: column;
    min-height: auto;
  }

  .register-brand {
    flex: 0 0 auto;
    padding: 30px 20px;
  }

  .brand-features {
    display: none;
  }

  .brand-desc {
    margin-bottom: 0;
  }

  .register-form-wrapper {
    padding: 30px 28px;
  }
}
</style>
