
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  build: {
    rollupOptions: {
      // 保留图标库全部导出，不被 tree-shaking 移除
      treeshake: {
        moduleSideEffects: (id) => {
          if (id.includes('@element-plus/icons-vue')) return 'no-treeshake'
        }
      }
    }
  }
})