import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  // 这是 Vue 项目，请使用 Vue 插件
  plugins: [vue()],
  // 打包后资源使用相对路径，方便 Electron 本地加载。
  base: './',
  // @ 就代表 src。
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'vue-vendor': ['vue', 'vue-router', 'pinia']
        }
      }
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
// 浏览器
//    │
//    │ /api/music
//    ↓
// Vite Dev Server :5173
//    │
//    │ proxy
//    ↓
// Spring Boot :8080