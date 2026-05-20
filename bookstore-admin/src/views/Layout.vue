<template>
  <!-- 管理端布局框架 -->
  <div class="layout">
    <Sidebar />
    <div class="layout-right">
      <header class="topbar">
        <span class="user-info">👤 {{ userStore.userInfo?.username || '管理员' }}</span>
        <button class="logout-btn" @click="userStore.logout()">退出登录</button>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import Sidebar from '@/components/Sidebar.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.layout { display: flex; height: 100vh; }
.layout-right { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.topbar {
  height: 60px; background: #fff; color: #2c3e50; display: flex; align-items: center;
  justify-content: space-between; padding: 0 28px; font-size: 16px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.06); z-index: 10;
}
.user-info { font-size: 14px; font-weight: 500; }
.logout-btn {
  padding: 7px 18px; background: transparent; color: #e74c3c;
  border: 2px solid #e74c3c; border-radius: 8px; cursor: pointer; font-size: 13px;
  transition: all 0.2s; font-weight: 500;
}
.logout-btn:hover { background: #e74c3c; color: #fff; }
.content { flex: 1; padding: 24px; background: #f5f7fa; overflow: auto; }
</style>
