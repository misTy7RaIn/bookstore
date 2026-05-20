<template>
  <!-- 侧边栏菜单 -->
  <nav class="sidebar">
    <div class="logo">📚 BookStore</div>
    <router-link v-for="item in menus" :key="item.path" :to="item.path" class="menu-item">
      {{ item.name }}
    </router-link>
  </nav>
</template>

<script setup>
const menus = [
  { path: '/', name: '首页' },
  { path: '/books', name: '图书管理' },
  { path: '/orders', name: '订单管理' },
  { path: '/users', name: '用户管理' },
  { path: '/admins', name: '管理员管理' }
]
</script>

<style scoped>
.sidebar {
  width: 220px; background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  color: #fff; flex-shrink: 0; box-shadow: 2px 0 12px rgba(0,0,0,0.1);
}
.logo {
  height: 60px; display: flex; align-items: center; justify-content: center;
  font-size: 20px; font-weight: 800; letter-spacing: 2px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
  background: rgba(255,255,255,0.03);
}
.menu-item {
  display: flex; align-items: center; gap: 10px; padding: 14px 24px;
  color: rgba(255,255,255,0.65); text-decoration: none; font-size: 14px;
  font-weight: 500; transition: all 0.25s; border-left: 3px solid transparent;
}
.menu-item:hover { color: #fff; background: rgba(255,255,255,0.06); border-left-color: #3498db; }
.router-link-exact-active { color: #fff; background: rgba(52,152,219,0.2); border-left-color: #3498db; font-weight: 600; }
</style>
