<template>
  <!-- 用户管理页 -->
  <div class="user-manage">
    <!-- 顶部操作栏 -->
    <div class="toolbar">
      <input v-model="keyword" placeholder="🔍 搜索用户名或手机号..." @input="onSearch" class="search-input" />
    </div>

    <!-- 提示消息 -->
    <div v-if="msg.text" :class="msg.type === 'ok' ? 'msg-ok' : 'msg-err'">{{ msg.text }}</div>

    <!-- 用户表格 -->
    <table class="user-table">
      <thead>
        <tr>
          <th>ID</th><th>用户名</th><th>手机号</th><th>角色</th><th>创建时间</th><th>最近修改</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="filteredList.length === 0">
          <td colspan="6" style="text-align:center;color:#999;padding:40px;">暂无用户</td>
        </tr>
        <tr v-for="u in filteredList" :key="u.userId">
          <td>{{ u.userId }}</td>
          <td>{{ u.username }}</td>
          <td>{{ u.phone }}</td>
          <td><span :class="u.role === 2 ? 'role-admin' : 'role-user'">{{ u.role === 2 ? '管理员' : '普通用户' }}</span></td>
          <td>{{ formatTime(u.createTime) }}</td>
          <td>{{ formatTime(u.updateTime) }}</td>
        </tr>
      </tbody>
    </table>

    <!-- 统计 -->
    <p class="summary">共 {{ filteredList.length }} 个用户，其中管理员 {{ adminCount }} 人</p>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import request from '@/utils/request'

const userList = ref([])
const keyword = ref('')
const msg = reactive({ text: '', type: 'ok' })

// 搜索过滤
const filteredList = computed(() => {
  if (!keyword.value.trim()) return userList.value
  const kw = keyword.value.toLowerCase()
  return userList.value.filter(u =>
    u.username.toLowerCase().includes(kw) || u.phone.includes(kw)
  )
})

// 管理员数量
const adminCount = computed(() => userList.value.filter(u => u.role === 2).length)

// 防抖搜索
let timer = null
function onSearch() {
  // 前端过滤，无需重新请求
  clearTimeout(timer)
  timer = setTimeout(() => {}, 100)
}

async function loadUsers() {
  const res = await request.get('/user/list')
  if (res.code === 200) userList.value = res.data
}

function formatTime(t) {
  return t ? t.replace('T', ' ') : '--'
}

onMounted(() => { loadUsers() })
</script>

<style scoped>
.user-manage { height: 100%; }
.toolbar { margin-bottom: 16px; }
.search-input { width: 240px; height: 36px; padding: 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; }

.msg-ok { padding: 10px 16px; background: #f0f9eb; color: #67c23a; border-radius: 4px; margin-bottom: 12px; font-size: 13px; }
.msg-err { padding: 10px 16px; background: #fef0f0; color: #f56c6c; border-radius: 4px; margin-bottom: 12px; font-size: 13px; }

.user-table { width: 100%; background: #fff; border-radius: 8px; border-collapse: collapse; }
.user-table th, .user-table td { padding: 12px 16px; border-bottom: 1px solid #ebeef5; text-align: left; font-size: 14px; }
.user-table th { background: #f5f7fa; color: #606266; font-weight: 600; }

.role-admin { color: #409eff; font-weight: 600; }
.role-user { color: #909399; }

.summary { margin-top: 16px; font-size: 13px; color: #909399; }
</style>
