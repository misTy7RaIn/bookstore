<template>
  <!-- 登录页 -->
  <div class="login-page">
    <div class="login-card">
      <h2>在线图书商城</h2>
      <p class="subtitle">欢迎回来</p>
      <form @submit.prevent="handleLogin">
        <div class="form-item">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名" required />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" required />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" :disabled="loading" class="login-btn">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>
      <p class="link-text">
        <router-link to="/register">没有账号？去注册</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const form = reactive({ username: '', password: '' })
const loading = ref(false)
const errorMsg = ref('')

/**
 * 处理登录
 */
async function handleLogin() {
  errorMsg.value = ''
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
  } catch (e) {
    errorMsg.value = e.message || '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex; align-items: center; justify-content: center;
  min-height: 100vh; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}
.login-card {
  width: 400px; padding: 44px 40px; background: rgba(255,255,255,0.95);
  border-radius: 16px; text-align: center; box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  backdrop-filter: blur(10px);
}
.login-card h2 { font-size: 26px; color: #2c3e50; font-weight: 800; letter-spacing: 2px; }
.subtitle { margin: 10px 0 32px; color: #909399; font-size: 14px; }
.form-item { text-align: left; margin-bottom: 22px; }
.form-item label { display: block; margin-bottom: 8px; color: #2c3e50; font-size: 13px; font-weight: 600; }
.form-item input {
  width: 100%; height: 44px; padding: 0 14px; border: 2px solid #e8ecf1;
  border-radius: 10px; font-size: 14px; outline: none; transition: all 0.25s; background: #f8f9fb;
}
.form-item input:focus { border-color: #3498db; background: #fff; box-shadow: 0 0 0 3px rgba(52,152,219,0.1); }
.error-msg { color: #e74c3c; font-size: 13px; margin-bottom: 10px; }
.login-btn {
  width: 100%; height: 46px; background: linear-gradient(135deg, #3498db, #2980b9);
  color: #fff; border: none; border-radius: 10px; font-size: 16px; cursor: pointer;
  transition: all 0.25s; font-weight: 600; box-shadow: 0 4px 12px rgba(52,152,219,0.3);
  letter-spacing: 2px;
}
.login-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(52,152,219,0.4); }
.login-btn:disabled { background: #a0cfff; box-shadow: none; cursor: not-allowed; transform: none; }
.link-text { margin-top: 18px; font-size: 13px; }
.link-text a { color: #3498db; font-weight: 500; text-decoration: none; }
.link-text a:hover { text-decoration: underline; }
</style>
