<template>
  <!-- 前台布局框架 -->
  <div class="layout">
    <TopNav />
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import TopNav from '@/components/TopNav.vue'
</script>

<style scoped>
.layout { min-height: 100vh; background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%); }
.content { max-width: 1200px; margin: 0 auto; padding: 24px 20px; }
</style>
