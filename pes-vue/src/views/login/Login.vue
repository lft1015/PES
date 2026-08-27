<!--
  登录页面 Login.vue
  ==================
  功能：
    1. 用户名 + 密码 + 验证码表单（含前端校验）
    2. 验证码由 Captcha 子组件管理，key 通过 @key-change 事件回传
    3. 登录成功后存储 token + 用户信息到 Pinia store，跳转首页
    4. 登录失败自动刷新验证码，清空验证码输入
    5. 支持 Enter 快捷键提交，按钮 loading 防重复
    6. "立即注册" 链接跳转注册页

  页面布局：左右双栏
    左侧 — 品牌展示区（蓝色渐变 + Logo + 卖点）
    右侧 — 登录表单区
-->
<template>
  <div class="login-page">
    <!-- ==================== 背景浮动装饰 ==================== -->
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <!-- ==================== 登录卡片 ==================== -->
    <div class="login-container">
      <!-- 左侧：品牌展示区 -->
      <div class="login-brand">
        <div class="brand-content">
          <div class="brand-logo">
            <span class="logo-icon">P</span>
          </div>
          <h1 class="brand-title">PES 系统</h1>
          <p class="brand-desc">Permission &amp; Enterprise System</p>
          <div class="brand-features">
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>统一权限管理</span>
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>安全可靠认证</span>
            </div>
            <div class="feature-item">
              <span class="feature-dot"></span>
              <span>高效易用体验</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：登录表单区 -->
      <div class="login-form-wrapper">
        <div class="form-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>

        <!--
          el-form 表单
          ref="formRef" — 用于调用 validate() 校验
          @keyup.enter — 回车键触发登录
        -->
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <!-- 用户名 -->
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

          <!-- 密码（支持切换明文/密文） -->
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- 验证码：输入框 + Captcha 组件 -->
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
              <!--
                @key-change: Captcha 组件加载成功后回传 captchaKey
                ref="captchaRef": 用于登录失败后调用 refreshCaptcha()
              -->
              <Captcha ref="captchaRef" @key-change="onCaptchaKeyChange" />
            </div>
          </el-form-item>

          <!-- 登录按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>

          <!-- 注册链接 -->
          <div class="form-footer">
            <span class="register-link">
              还没有账号？<el-link type="primary" :underline="false" @click="goRegister">立即注册</el-link>
            </span>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 底部版权 -->
    <div class="login-copyright">
      Copyright &copy; {{ new Date().getFullYear() }} PES. All Rights Reserved.
    </div>
  </div>
</template>

<script setup>
/**
 * 登录页面逻辑
 *
 * 核心流程：
 *   1. 用户填写表单 → 前端校验
 *   2. 调用 login({ username, password, captcha, captchaKey })
 *   3. 后端校验验证码（Redis 比对，忽略大小写）→ 认证 → 返回 token
 *   4. 前端存储 token + 用户信息 → 跳转首页
 *   5. 失败时自动刷新验证码，清空输入
 */
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'
// Element Plus 图标
import { User, Lock, Key } from '@element-plus/icons-vue'
// 验证码子组件
import Captcha from '@/components/common/Captcha.vue'

// ==================== 依赖注入 ====================
const router = useRouter()
const userStore = useUserStore()

// ==================== 模板引用 ====================
const formRef = ref()       // el-form 引用，用于 validate()
const captchaRef = ref()    // Captcha 组件引用，用于 refreshCaptcha()

// ==================== 响应式数据 ====================
const captchaKey = ref('')  // 验证码唯一标识（由 Captcha 组件回传）
const loading = ref(false)  // 登录按钮 loading 状态

/** 登录表单数据 */
const form = reactive({
  username: '',
  password: '',
  captcha: ''    // 验证码文本（用户输入）
})

/** 表单校验规则 */
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// ==================== 事件处理 ====================

/** Captcha 组件加载成功后回调，保存验证码 key */
const onCaptchaKeyChange = (key) => {
  captchaKey.value = key
}

/** 跳转到注册页 */
const goRegister = () => {
  router.push('/register')
}

/**
 * 处理登录提交
 *
 * 步骤：
 *  1. 表单前端校验（el-form validate）
 *  2. 调用 login API，传入 captchaKey
 *  3. 成功后存储 token 和用户信息
 *  4. 失败时自动刷新验证码
 */
const handleLogin = async () => {
  // ---- 第1步：前端表单校验 ----
  try {
    await formRef.value.validate()
  } catch {
    // 校验不通过，el-form 会自动提示
    return
  }

  // ---- 第2步：调用登录 API ----
  loading.value = true
  try {
    // 将 captchaKey 合并到请求体中
    const res = await login({
      ...form,
      captchaKey: captchaKey.value
    })

    // ---- 第3步：登录成功 ----
    if (res && res.token) {
      // 保存 token
      userStore.setToken(res.token)
      // 保存用户信息
      userStore.setUserInfo({
        username: res.username,
        nickname: res.nickname,
        roles: res.roles || [],
        permissions: res.permissions || []
      })
      ElMessage.success('登录成功，欢迎回来！')
      // 跳转到首页
      router.push('/')
    } else {
      ElMessage.error('登录失败：未获取到认证信息')
    }
  } catch (error) {
    // ---- 第4步：登录失败 ----
    // request.js 拦截器已经展示了 ElMessage.error，这里只需处理页面状态
    // 自动刷新验证码，清空输入（因为验证码已失效）
    captchaRef.value?.refreshCaptcha()
    form.captcha = ''
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ==================== 页面背景 ==================== */
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 40%, #0f3460 100%);
  overflow: hidden;
}

/* ==================== 背景浮动装饰形状 ==================== */
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
  background: #409eff;
  top: -300px;
  right: -200px;
  animation: float 20s ease-in-out infinite;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: #67c23a;
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

/* ==================== 登录卡片 ==================== */
.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  width: 880px;
  min-height: 520px;
  background: rgba(255, 255, 255, 0.97);
  border-radius: 16px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  backdrop-filter: blur(10px);
}

/* ==================== 左侧品牌区 ==================== */
.login-brand {
  flex: 0 0 400px;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 40px;
  color: #fff;
  position: relative;
  overflow: hidden;
}

/* 装饰圆形 */
.login-brand::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
  top: -100px;
  right: -100px;
}

.login-brand::after {
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
  letter-spacing: 1px;
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

/* ==================== 右侧表单区 ==================== */
.login-form-wrapper {
  flex: 1;
  padding: 50px 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 32px;
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

.login-form {
  width: 100%;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.captcha-row {
  display: flex;
  align-items: flex-start;
  gap: 0;
}

.captcha-input {
  flex: 1;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 8px;
  margin-top: 4px;
}

.form-footer {
  text-align: center;
  margin-top: -6px;
}

.register-link {
  font-size: 14px;
  color: #999;
}

/* ==================== 底部版权 ==================== */
.login-copyright {
  position: absolute;
  bottom: 20px;
  z-index: 1;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  text-align: center;
}

/* ==================== 响应式适配 ==================== */
@media (max-width: 920px) {
  .login-container {
    width: 94vw;
    flex-direction: column;
    min-height: auto;
  }

  .login-brand {
    flex: 0 0 auto;
    padding: 30px 20px;
  }

  .brand-features {
    display: none;
  }

  .brand-desc {
    margin-bottom: 0;
  }

  .login-form-wrapper {
    padding: 30px 28px;
  }
}
</style>

