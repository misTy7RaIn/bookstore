<template>
  <!-- 注册页（含邮箱验证码） -->
  <div class="reg-page">
    <div class="reg-card">
      <h2>用户注册</h2>
      <p class="subtitle">创建您的账号</p>
      <form @submit.prevent="handleRegister">
        <div class="form-item">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名" required />
        </div>
        <div class="form-item">
          <label>邮箱</label>
          <div class="email-row">
            <input v-model="form.email" type="email" placeholder="请输入邮箱地址" required />
            <button type="button" class="send-code-btn" :disabled="countdown > 0 || sending" @click="sendCode">
              {{ countdown > 0 ? countdown + 's 后重发' : sending ? '发送中...' : '发送验证码' }}
            </button>
          </div>
        </div>
        <div class="form-item">
          <label>验证码</label>
          <input v-model="form.code" type="text" placeholder="请输入邮箱验证码" required maxlength="6" />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" required />
        </div>
        <div class="form-item">
          <label>手机号</label>
          <input v-model="form.phone" type="text" placeholder="请输入手机号" required />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" :disabled="loading" class="btn">
          {{ loading ? '注册中...' : '注 册' }}
        </button>
      </form>
      <p class="link-text">
        <router-link to="/login">已有账号？去登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const form = reactive({ username: '', email: '', code: '', password: '', phone: '' })
const loading = ref(false)
const sending = ref(false)
const countdown = ref(0)
const errorMsg = ref('')

let timer = null

// 发送验证码
async function sendCode() {
  if (!form.email) {
    errorMsg.value = '请先填写邮箱地址'
    return
  }
  errorMsg.value = ''
  sending.value = true
  try {
    const res = await request.post('/user/send-verify-code', { email: form.email })
    if (res.code !== 200) {
      errorMsg.value = res.message || '发送失败'
      return
    }
    // 启动 60 秒倒计时
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
        timer = null
      }
    }, 1000)
  } catch (e) {
    errorMsg.value = '发送失败，请稍后重试'
  } finally {
    sending.value = false
  }
}

// 注册
async function handleRegister() {
  errorMsg.value = ''
  loading.value = true
  try {
    await userStore.register(form.username, form.password, form.phone, form.email, form.code)
  } catch (e) {
    errorMsg.value = e.message || '注册失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.reg-page {
  display: flex; align-items: center; justify-content: center;
  min-height: 100vh; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 20px;
}
.reg-card {
  width: 440px; padding: 40px; background: rgba(255,255,255,0.95);
  border-radius: 16px; text-align: center; box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}
.reg-card h2 { font-size: 26px; color: #2c3e50; font-weight: 800; letter-spacing: 2px; }
.subtitle { margin: 10px 0 28px; color: #909399; font-size: 14px; }
.form-item { text-align: left; margin-bottom: 18px; }
.form-item label { display: block; margin-bottom: 6px; color: #2c3e50; font-size: 13px; font-weight: 600; }
.form-item input {
  width: 100%; height: 42px; padding: 0 14px; border: 2px solid #e8ecf1;
  border-radius: 10px; font-size: 14px; outline: none; transition: all 0.25s; background: #f8f9fb;
}
.form-item input:focus { border-color: #3498db; background: #fff; box-shadow: 0 0 0 3px rgba(52,152,219,0.1); }
.email-row { display: flex; gap: 8px; }
.email-row input { flex: 1; }
.send-code-btn {
  white-space: nowrap; height: 42px; padding: 0 14px;
  background: #3498db; color: #fff; border: none; border-radius: 10px;
  font-size: 13px; cursor: pointer; transition: all 0.2s; font-weight: 500;
}
.send-code-btn:hover { background: #2980b9; }
.send-code-btn:disabled { background: #a0cfff; cursor: not-allowed; }
.error-msg { color: #e74c3c; font-size: 13px; margin-bottom: 10px; }
.btn {
  width: 100%; height: 46px; background: linear-gradient(135deg, #27ae60, #1e8449);
  color: #fff; border: none; border-radius: 10px; font-size: 16px; cursor: pointer;
  transition: all 0.25s; font-weight: 600; box-shadow: 0 4px 12px rgba(39,174,96,0.3);
  letter-spacing: 2px;
}
.btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(39,174,96,0.4); }
.btn:disabled { background: #a0cfff; box-shadow: none; cursor: not-allowed; transform: none; }
.link-text { margin-top: 18px; font-size: 13px; }
.link-text a { color: #3498db; font-weight: 500; text-decoration: none; }
.link-text a:hover { text-decoration: underline; }
</style>
