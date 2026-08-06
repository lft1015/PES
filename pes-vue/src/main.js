/**
 * PES 系统入口
 *
 * 导入顺序很重要：
 *   1. '@/router/guard' 必须在 router 之后导入 → 注册 beforeEach 守卫
 *   2. App.vue 的 v-if="appReady" + router.isReady() 防止守卫执行前的闪屏
 */
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// ⚠️ 副作用导入：注册路由守卫（必须放在 router 导入之后）
import '@/router/guard'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/styles/index.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
