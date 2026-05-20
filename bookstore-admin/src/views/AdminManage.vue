<template>
  <!-- 管理员管理页 -->
  <div class="admin-manage">
    <div class="toolbar">
      <h3>管理员账号管理</h3>
      <button v-if="isSuper" class="add-btn" @click="openCreate">+ 新建管理员</button>
    </div>

    <div v-if="msg.text" :class="msg.type === 'ok' ? 'msg-ok' : 'msg-err'">{{ msg.text }}</div>

    <table class="admin-table">
      <thead>
        <tr>
          <th>ID</th><th>用户名</th><th v-if="isSuper">密码</th><th>手机号</th><th>邮箱</th><th>角色</th><th>创建时间</th><th v-if="isSuper">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="adminList.length === 0">
          <td :colspan="isSuper ? 8 : 6" style="text-align:center;color:#999;padding:40px;">暂无数据</td>
        </tr>
        <tr v-for="u in adminList" :key="u.userId">
          <td>{{ u.userId }}</td>
          <td>{{ u.username }}</td>
          <td v-if="isSuper" class="pwd-cell">{{ u.rawPassword || '--' }}</td>
          <td>{{ u.phone || '--' }}</td>
          <td>{{ u.email || '--' }}</td>
          <td><span :class="u.role === 3 ? 'role-super' : 'role-admin'">{{ u.role === 3 ? '超级管理员' : '管理员' }}</span></td>
          <td>{{ formatTime(u.createTime) }}</td>
          <td v-if="isSuper">
            <button v-if="isSelf(u.userId)" class="del-btn disabled" disabled>当前用户</button>
            <template v-else>
              <button class="edit-btn" @click="openEdit(u)">编辑</button>
              <button class="del-btn" @click="handleDelete(u.userId, u.username)">删除</button>
            </template>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 新建/编辑弹窗 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <h3>{{ isEdit ? '编辑管理员' : '新建管理员' }}</h3>
        <form @submit.prevent="handleSave">
          <div class="form-row"><label>用户名</label><input v-model="form.username" required /></div>
          <div class="form-row"><label>密码{{ isEdit ? '（留空则不修改）' : '' }}</label><input v-model="form.password" type="password" :required="!isEdit" /></div>
          <div class="form-row"><label>手机号</label><input v-model="form.phone" /></div>
          <div class="form-row"><label>邮箱</label><input v-model="form.email" type="email" /></div>
          <div class="dialog-btns">
            <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
            <button type="submit" :disabled="saving" class="confirm-btn">{{ saving ? '保存中...' : (isEdit ? '保存修改' : '确认创建') }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const adminList = ref([])
const msg = reactive({ text: '', type: 'ok' })
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const editUserId = ref(null)
const form = reactive({ username: '', password: '', phone: '', email: '' })

/** 当前用户是否为超级管理员 */
const isSuper = computed(() => userStore.userInfo && userStore.userInfo.role >= 3)

async function loadAdmins() {
  const res = await request.get('/admin/list')
  if (res.code === 200) adminList.value = res.data
}

function isSelf(userId) {
  return userStore.userInfo && userStore.userInfo.userId === userId
}

function openCreate() {
  isEdit.value = false
  editUserId.value = null
  Object.assign(form, { username: '', password: '', phone: '', email: '' })
  dialogVisible.value = true
}

function openEdit(u) {
  isEdit.value = true
  editUserId.value = u.userId
  Object.assign(form, { username: u.username, password: '', phone: u.phone || '', email: u.email || '' })
  dialogVisible.value = true
}

function closeDialog() { dialogVisible.value = false }

async function handleSave() {
  if (saving.value) return
  saving.value = true
  try {
    let res
    if (isEdit.value) {
      res = await request.put(`/admin/update/${editUserId.value}`, {
        username: form.username, password: form.password,
        phone: form.phone, email: form.email
      })
    } else {
      res = await request.post('/admin/create', {
        username: form.username, password: form.password,
        phone: form.phone, email: form.email
      })
    }
    if (res.code === 200) {
      showMsg(isEdit.value ? '修改成功' : '创建成功')
      closeDialog()
      loadAdmins()
    } else {
      showMsg(res.message || '操作失败', 'err')
    }
  } catch (e) {
    showMsg('请求异常', 'err')
  } finally { saving.value = false }
}

async function handleDelete(userId, username) {
  if (!confirm(`确认删除管理员「${username}」？`)) return
  const res = await request.delete(`/admin/delete/${userId}`)
  if (res.code === 200) { showMsg('删除成功'); loadAdmins() }
  else showMsg(res.message || '删除失败', 'err')
}

function showMsg(text, type = 'ok') {
  msg.text = text; msg.type = type
  setTimeout(() => { msg.text = '' }, 3000)
}

function formatTime(t) { return t ? t.replace('T', ' ') : '--' }

onMounted(() => { loadAdmins() })
</script>

<style scoped>
.admin-manage { height: 100%; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.toolbar h3 { margin: 0; font-size: 16px; }
.add-btn { height: 36px; padding: 0 20px; background: #409eff; color: #fff; border: none; border-radius: 4px; cursor: pointer; }

.msg-ok { padding: 10px 16px; background: #f0f9eb; color: #67c23a; border-radius: 4px; margin-bottom: 12px; font-size: 13px; }
.msg-err { padding: 10px 16px; background: #fef0f0; color: #f56c6c; border-radius: 4px; margin-bottom: 12px; font-size: 13px; }

.admin-table { width: 100%; background: #fff; border-radius: 8px; border-collapse: collapse; }
.admin-table th, .admin-table td { padding: 12px 14px; border-bottom: 1px solid #ebeef5; text-align: left; font-size: 14px; }
.admin-table th { background: #f5f7fa; color: #606266; font-weight: 600; }

.role-super { color: #f56c6c; font-weight: 600; }
.role-admin { color: #409eff; }

.edit-btn { padding: 4px 12px; background: #e6a23c; color: #fff; border: none; border-radius: 4px; cursor: pointer; margin-right: 6px; }
.del-btn { padding: 4px 12px; background: #f56c6c; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
.del-btn.disabled { background: #fab6b6; cursor: not-allowed; }

.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 100; }
.dialog { width: 420px; background: #fff; border-radius: 8px; padding: 24px; }
.dialog h3 { margin-bottom: 20px; }
.form-row { margin-bottom: 14px; }
.form-row label { display: block; margin-bottom: 4px; color: #606266; font-size: 13px; }
.form-row input { width: 100%; height: 36px; padding: 0 10px; border: 1px solid #dcdfe6; border-radius: 4px; }
.dialog-btns { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
.cancel-btn { padding: 8px 20px; background: #f5f7fa; border: 1px solid #dcdfe6; border-radius: 4px; cursor: pointer; }
.confirm-btn { padding: 8px 20px; background: #409eff; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
.confirm-btn:disabled { background: #a0cfff; cursor: not-allowed; }
</style>
