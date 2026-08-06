/**
 * Pinia 状态管理入口
 * 创建并导出全局 Pinia 实例，在 main.js 中注册到 Vue 应用
 */
import { createPinia } from 'pinia'

const pinia = createPinia()

export default pinia
