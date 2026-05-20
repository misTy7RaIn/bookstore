<template>
  <!-- 顶部导航栏 -->
  <header class="topnav">
    <div class="nav-inner">
      <router-link to="/" class="logo">📚 在线图书商城</router-link>

      <!-- 搜索框 -->
      <div class="search-box">
        <input v-model="kw" placeholder="搜索图书..." @keyup.enter="goSearch" />
        <button @click="goSearch">🔍</button>
      </div>

      <div class="nav-links">
        <router-link to="/books">图书</router-link>
        <router-link to="/cart">购物车</router-link>
        <router-link to="/orders">订单</router-link>
        <template v-if="userStore.token">
          <router-link to="/profile">个人中心</router-link>
          <span class="nav-user">👤 {{ userStore.userInfo?.username }}</span>
          <a href="#" @click.prevent="userStore.logout()">退出</a>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const kw = ref(route.query.keyword || '')

// 跳转搜索页
function goSearch() {
  const k = kw.value.trim()
  if (k) {
    router.push({ path: '/books', query: { keyword: k } })
  } else {
    router.push('/books')
  }
}
</script>

<style scoped>
.topnav {
  height: 60px; background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
  color: #fff; position: sticky; top: 0; z-index: 100;
  box-shadow: 0 2px 12px rgba(0,0,0,0.15);
  backdrop-filter: blur(10px);
}
.nav-inner { max-width: 1200px; margin: 0 auto; height: 100%; display: flex; align-items: center; padding: 0 24px; }
.logo { font-size: 20px; font-weight: 800; flex-shrink: 0; letter-spacing: 1px; text-shadow: 0 2px 4px rgba(0,0,0,0.2); }

.search-box { display: flex; flex: 1; max-width: 420px; margin: 0 48px; }
.search-box input {
  flex: 1; height: 38px; padding: 0 16px; border: none; border-radius: 20px 0 0 20px; font-size: 14px; outline: none;
  box-shadow: inset 0 1px 3px rgba(0,0,0,0.1);
}
.search-box button {
  width: 44px; height: 38px; background: #fff; color: #3498db; border: none; border-radius: 0 20px 20px 0; cursor: pointer; font-size: 16px;
  transition: background 0.2s;
}
.search-box button:hover { background: #ecf5ff; }

.nav-links { display: flex; align-items: center; gap: 20px; font-size: 14px; flex-shrink: 0; }
.nav-links a { color: rgba(255,255,255,0.9); padding: 5px 10px; border-radius: 6px; transition: all 0.2s; white-space: nowrap; text-decoration: none; font-weight: 500; }
.nav-links a:hover { background: rgba(255,255,255,0.2); color: #fff; }
.nav-links a.router-link-active { background: rgba(255,255,255,0.25); color: #fff; font-weight: 600; }
.nav-user { font-size: 13px; white-space: nowrap; opacity: 0.9; }
</style>
