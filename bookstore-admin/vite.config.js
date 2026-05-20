import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// Vite 配置
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    // 代理后端 API
    proxy: {
      '/api': {
        target: 'http://coderyu.top:8080/',
        changeOrigin: true
      }
    }
  }
})
