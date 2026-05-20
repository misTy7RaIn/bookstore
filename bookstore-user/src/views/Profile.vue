<template>
  <!-- 个人中心 -->
  <div class="profile-page">
    <h2>个人中心</h2>

    <!-- 用户信息 -->
    <div class="info-card">
      <h3>基本信息</h3>
      <div class="info-row"><span>用户名：</span>{{ userStore.userInfo?.username }}</div>
      <div class="info-row"><span>手机号：</span>{{ userStore.userInfo?.phone }}</div>
      <div class="info-row"><span>角色：</span>{{ userStore.userInfo?.role >= 2 ? '管理员' : '普通用户' }}</div>
    </div>

    <!-- 收货地址 -->
    <div class="address-section">
      <div class="section-header">
        <h3>收货地址</h3>
        <button class="add-addr-btn" @click="openAddAddr">+ 添加地址</button>
      </div>
      <div v-if="addrList.length === 0" class="empty">暂无收货地址</div>
      <div class="addr-list">
        <div v-for="addr in addrList" :key="addr.addressId" :class="['addr-card', { 'addr-default': addr.isDefault === 1 }]">
          <div class="addr-info">
            <p class="addr-name-phone">{{ addr.receiverName }} {{ addr.receiverPhone }}</p>
            <p class="addr-detail">{{ addr.addressDetail }}</p>
            <span v-if="addr.isDefault === 1" class="default-tag">默认</span>
          </div>
          <div class="addr-actions">
            <button class="edit-btn" @click="openEditAddr(addr)">编辑</button>
            <button v-if="addr.isDefault !== 1" class="del-btn" @click="handleDelAddr(addr.addressId)">删除</button>
            <button v-if="addr.isDefault !== 1" class="default-btn" @click="setDefaultAddr(addr.addressId)">设为默认</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 收藏列表 -->
    <div class="fav-section">
      <h3>我的收藏</h3>
      <div v-if="favList.length === 0" class="empty">暂无收藏</div>
      <div class="fav-grid">
        <div v-for="fav in favList" :key="fav.favoriteId" class="fav-card">
          <img :src="getBook(fav.bookId).coverImg" @click="$router.push(`/book/${fav.bookId}`)" @error="e => e.target.src=''" />
          <p class="fav-name">{{ getBook(fav.bookId).bookName }}</p>
          <p class="fav-price">¥{{ getBook(fav.bookId).price }}</p>
          <button @click="removeFav(fav.favoriteId)">取消收藏</button>
        </div>
      </div>
    </div>

    <!-- 地址编辑弹窗 -->
    <div v-if="addrDialogVisible" class="dialog-overlay" @click.self="addrDialogVisible = false">
      <div class="dialog">
        <h3>{{ editingAddr ? '编辑地址' : '添加地址' }}</h3>
        <form @submit.prevent="handleSaveAddr">
          <div class="form-row"><label>收货人</label><input v-model="addrForm.receiverName" required /></div>
          <div class="form-row"><label>联系电话</label><input v-model="addrForm.receiverPhone" required /></div>
          <div class="form-row"><label>地址</label><input v-model="addrForm.addressDetail" required /></div>
          <div class="form-row"><label><input type="checkbox" v-model="addrForm.isDefault" true-value="1" false-value="0" /> 设为默认地址</label></div>
          <div class="dialog-btns">
            <button type="button" class="cancel-btn" @click="addrDialogVisible = false">取消</button>
            <button type="submit" :disabled="addrSaving" class="confirm-btn">{{ addrSaving ? '保存中...' : '保存' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const favList = ref([])
const bookMap = ref({})
const addrList = ref([])
const addrDialogVisible = ref(false)
const editingAddr = ref(null)
const addrSaving = ref(false)
const addrForm = reactive({ receiverName: '', receiverPhone: '', addressDetail: '', isDefault: '0' })

async function loadData() {
  if (!userStore.userInfo) return
  const userId = userStore.userInfo.userId

  const [favRes, bookRes, addrRes] = await Promise.all([
    request.get('/favorite/list', { params: { userId } }),
    request.get('/book/list'),
    request.get('/address/list')
  ])
  if (favRes.code === 200) favList.value = favRes.data
  if (bookRes.code === 200) {
    bookRes.data.forEach(b => { bookMap.value[b.bookId] = b })
  }
  if (addrRes.code === 200) addrList.value = addrRes.data
}

function getBook(bookId) { return bookMap.value[bookId] || {} }
async function removeFav(favId) {
  const res = await request.delete(`/favorite/remove/${favId}`)
  if (res.code === 200) favList.value = favList.value.filter(f => f.favoriteId !== favId)
}

/* 地址管理 */
function openAddAddr() {
  editingAddr.value = null
  Object.assign(addrForm, { receiverName: '', receiverPhone: '', addressDetail: '', isDefault: '0' })
  addrDialogVisible.value = true
}
function openEditAddr(addr) {
  editingAddr.value = addr
  Object.assign(addrForm, {
    receiverName: addr.receiverName, receiverPhone: addr.receiverPhone,
    addressDetail: addr.addressDetail, isDefault: String(addr.isDefault)
  })
  addrDialogVisible.value = true
}
async function handleSaveAddr() {
  if (addrSaving.value) return
  addrSaving.value = true
  try {
    const data = { ...addrForm, isDefault: Number(addrForm.isDefault) }
    let res
    if (editingAddr.value) {
      res = await request.put(`/address/update/${editingAddr.value.addressId}`, data)
    } else {
      res = await request.post('/address/add', data)
    }
    if (res.code === 200) { addrDialogVisible.value = false; loadData() }
  } finally { addrSaving.value = false }
}
async function handleDelAddr(id) {
  if (!confirm('确认删除该地址？')) return
  const res = await request.delete(`/address/delete/${id}`)
  if (res.code === 200) loadData()
}
async function setDefaultAddr(id) {
  const res = await request.put(`/address/default/${id}`)
  if (res.code === 200) loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped>
.profile-page { max-width: 960px; margin: 0 auto; }
.info-card { background: #fff; border-radius: 8px; padding: 24px; margin-bottom: 24px; }
.info-card h3 { margin-bottom: 16px; }
.info-row { margin-bottom: 10px; font-size: 14px; color: #303133; }
.info-row span { color: #909399; }

/* 地址 */
.address-section { background: #fff; border-radius: 8px; padding: 24px; margin-bottom: 24px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.section-header h3 { margin: 0; }
.add-addr-btn { padding: 6px 16px; background: #409eff; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-size: 13px; }
.addr-list { display: flex; flex-direction: column; gap: 12px; }
.addr-card { display: flex; justify-content: space-between; align-items: center; border: 1px solid #ebeef5; border-radius: 6px; padding: 14px 16px; }
.addr-default { border-color: #409eff; background: #ecf5ff; }
.addr-info { flex: 1; }
.addr-name-phone { font-weight: 600; margin-bottom: 4px; }
.addr-detail { color: #606266; font-size: 13px; }
.default-tag { display: inline-block; background: #409eff; color: #fff; font-size: 11px; padding: 1px 6px; border-radius: 3px; margin-top: 4px; }
.addr-actions { display: flex; gap: 8px; flex-shrink: 0; }
.edit-btn { padding: 4px 10px; background: #e6a23c; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }
.del-btn { padding: 4px 10px; background: #f56c6c; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }
.default-btn { padding: 4px 10px; background: #409eff; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }

.fav-section h3 { margin-bottom: 16px; }
.empty { color: #999; padding: 40px; text-align: center; }
.fav-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; }
.fav-card { background: #fff; border-radius: 8px; padding: 12px; text-align: center; }
.fav-card img { width: 100%; height: 160px; object-fit: cover; border-radius: 4px; cursor: pointer; }
.fav-name { font-size: 14px; font-weight: 600; margin: 8px 0 4px; }
.fav-price { font-size: 15px; color: #f56c6c; font-weight: bold; margin-bottom: 8px; }
.fav-card button { padding: 4px 12px; background: #fef0f0; color: #f56c6c; border: 1px solid #f56c6c; border-radius: 4px; cursor: pointer; font-size: 12px; }

/* 弹窗 */
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 100; }
.dialog { width: 420px; background: #fff; border-radius: 8px; padding: 24px; }
.dialog h3 { margin-bottom: 20px; }
.form-row { margin-bottom: 14px; }
.form-row label { display: block; margin-bottom: 4px; color: #606266; font-size: 13px; }
.form-row input[type="text"] { width: 100%; height: 36px; padding: 0 10px; border: 1px solid #dcdfe6; border-radius: 4px; }
.dialog-btns { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
.cancel-btn { padding: 8px 20px; background: #f5f7fa; border: 1px solid #dcdfe6; border-radius: 4px; cursor: pointer; }
.confirm-btn { padding: 8px 20px; background: #409eff; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
.confirm-btn:disabled { background: #a0cfff; cursor: not-allowed; }
</style>
