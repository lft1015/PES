<!--
  验证码组件 Captcha.vue
  ======================
  职责：
    1. 调用 GET /captcha 获取验证码图片和唯一标识 key
    2. 将 base64 图片渲染为 <img>，支持点击刷新
    3. 通过 emit('keyChange', key) 将 key 传给父组件（登录/注册表单需要回传）
    4. 包含三种状态：加载中 → 正常显示 → 加载失败(可重试)

  父组件用法：
    <Captcha ref="captchaRef" @key-change="captchaKey = $event" />
    // 手动刷新：captchaRef.value.refreshCaptcha()
-->
<template>
  <div class="captcha-wrapper">
    <!-- 状态1: 加载中，显示旋转动画 -->
    <div v-if="loading" class="captcha-placeholder">
      <el-icon class="loading-icon"><Loading /></el-icon>
    </div>

    <!-- 状态2: 加载失败，点击重试 -->
    <div v-else-if="error" class="captcha-placeholder captcha-error" @click="refreshCaptcha">
      <span class="error-text">点击重试</span>
    </div>

    <!-- 状态3: 正常显示验证码图片，点击刷新 -->
    <img
      v-else
      :src="captchaImage"
      alt="验证码"
      class="captcha-img"
      @click="refreshCaptcha"
    />

    <!-- "换一张" 文字链接 -->
    <el-link type="primary" :underline="false" @click="refreshCaptcha" class="refresh-link">
      换一张
    </el-link>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getCaptcha } from '@/api/auth'
import { Loading } from '@element-plus/icons-vue'

// ==================== 事件定义 ====================
// keyChange: 验证码加载成功后，将 key 传递给父组件
const emit = defineEmits(['keyChange'])

// ==================== 响应式状态 ====================
const captchaImage = ref('')  // base64 图片字符串（含 data:image/png;base64, 前缀）
const loading = ref(true)     // 是否正在加载
const error = ref(false)      // 是否加载失败

// ==================== 核心方法 ====================

/**
 * 刷新验证码
 * 调用后端接口获取新的验证码图片和 key
 */
const refreshCaptcha = async () => {
  loading.value = true
  error.value = false

  try {
    // getCaptcha() 返回 { key, image }（已由 request.js 拦截器解包）
    const data = await getCaptcha()

    if (data && data.image) {
      // 设置 base64 图片数据，直接作为 img src
      captchaImage.value = data.image
      // 通知父组件：验证码 key 已更新
      emit('keyChange', data.key)
    } else {
      throw new Error('验证码响应格式异常')
    }
  } catch (err) {
    console.error('验证码加载失败:', err.message || err)
    error.value = true
    captchaImage.value = ''
  } finally {
    loading.value = false
  }
}

// ==================== 暴露给父组件 ====================
defineExpose({
  /**
   * 手动触发验证码刷新（父组件登录/注册失败后调用）
   */
  refreshCaptcha
})

// ==================== 初始化 ====================
// 组件挂载时立即加载验证码
refreshCaptcha()
</script>

<style scoped>
.captcha-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

/* 验证码图片 */
.captcha-img {
  width: 130px;
  height: 48px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  cursor: pointer;
  display: block;
}

.captcha-img:hover {
  border-color: #409eff;
}

/* 占位状态（加载中 / 失败） */
.captcha-placeholder {
  width: 130px;
  height: 48px;
  border-radius: 4px;
  border: 1px dashed #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

/* 失败状态：红色边框 + 背景 */
.captcha-placeholder.captcha-error {
  cursor: pointer;
  border-color: #f56c6c;
  background: #fef0f0;
}

/* 加载中的旋转图标 */
.captcha-placeholder .loading-icon {
  font-size: 18px;
  color: #c0c4cc;
  animation: spin 1s linear infinite;
}

/* 失败提示文字 */
.captcha-placeholder .error-text {
  font-size: 12px;
  color: #f56c6c;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 刷新链接 */
.refresh-link {
  font-size: 13px;
  white-space: nowrap;
  flex-shrink: 0;
}
</style>
