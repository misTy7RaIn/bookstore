<template>
  <!-- 图书详情页 -->
  <div class="detail-page">
    <button class="back-btn" @click="$router.back()">← 返回</button>
    <div v-if="book" class="detail-card">
      <!-- 封面 -->
      <div class="cover-section">
        <img :src="book.coverImg" class="cover-img" @error="e => e.target.src=''" />
      </div>

      <!-- 信息 -->
      <div class="info-section">
        <h2>{{ book.bookName }}</h2>
        <p class="author">作者：{{ book.author }}</p>
        <p class="category">分类：{{ categoryName }}</p>
        <p class="price">¥{{ book.price }}</p>
        <p class="stock">库存：{{ book.stock > 0 ? book.stock + ' 本' : '已售罄' }}</p>

        <div class="desc-box">
          <h4>图书简介</h4>
          <p>{{ book.description || '暂无简介' }}</p>
        </div>

        <!-- 加入购物车 -->
        <div class="cart-action">
          <div class="qty-ctrl">
            <button @click="qty = Math.max(1, qty - 1)">-</button>
            <span>{{ qty }}</span>
            <button @click="qty++">+</button>
          </div>
          <button class="fav-btn" :disabled="faving" @click="toggleFavorite">
            {{ isFaved ? '❤️ 已收藏' : '🤍 收藏' }}
          </button>
          <button class="add-cart-btn" :disabled="book.stock <= 0 || adding" @click="addToCart">
            {{ adding ? '添加中...' : (book.stock > 0 ? '加入购物车' : '已售罄') }}
          </button>
        </div>

        <p v-if="msg.text" :class="msg.type === 'ok' ? 'msg-ok' : 'msg-err'">{{ msg.text }}</p>
      </div>
    </div>

    <div v-else class="loading">加载中...</div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const book = ref(null)
const qty = ref(1)
const adding = ref(false)
const faving = ref(false)
const isFaved = ref(false)
const favId = ref(null)
const msg = reactive({ text: '', type: 'ok' })

// 分类映射
const categoryMap = { 5: '中国当代小说', 6: '外国文学', 7: '历史', 8: '哲学', 9: '计算机', 10: '经济学' }
const categoryName = computed(() => categoryMap[book.value?.categoryId] || '未知')

async function loadBook() {
  const id = route.params.id
  const res = await request.get(`/book/detail/${id}`)
  if (res.code === 200) book.value = res.data
}

/**
 * 加入购物车
 */
async function addToCart() {
  if (!userStore.userInfo) {
    router.push('/login')
    return
  }
  adding.value = true
  msg.text = ''
  try {
    const res = await request.post('/cart/add', null, {
      params: { userId: userStore.userInfo.userId, bookId: book.value.bookId, quantity: qty.value }
    })
    if (res.code === 200) {
      msg.text = '已添加到购物车'
      msg.type = 'ok'
      setTimeout(() => { msg.text = '' }, 2000)
    } else {
      msg.text = res.message || '添加失败'
      msg.type = 'err'
    }
  } catch (e) {
    msg.text = '请求异常'
    msg.type = 'err'
  } finally {
    adding.value = false
  }
}

/**
 * 收藏/取消收藏
 */
async function toggleFavorite() {
  if (!userStore.userInfo) { router.push('/login'); return }
  faving.value = true
  try {
    if (isFaved.value) {
      const res = await request.delete(`/favorite/remove/${favId.value}`)
      if (res.code === 200) { isFaved.value = false; favId.value = null }
    } else {
      const res = await request.post('/favorite/add', null, {
        params: { userId: userStore.userInfo.userId, bookId: book.value.bookId }
      })
      if (res.code === 200) { isFaved.value = true }
    }
  } finally { faving.value = false }
}

/**
 * 检查是否已收藏
 */
async function checkFavorite() {
  if (!userStore.userInfo) return
  const res = await request.get('/favorite/list', { params: { userId: userStore.userInfo.userId } })
  if (res.code === 200) {
    const found = res.data.find(f => f.bookId === book.value.bookId)
    if (found) { isFaved.value = true; favId.value = found.favoriteId }
  }
}

onMounted(() => { loadBook().then(() => checkFavorite()) })
</script>

<style scoped>
.detail-page { max-width: 900px; margin: 0 auto; }
.back-btn {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 8px 18px; background: #fff; border: 2px solid #e8ecf1;
  border-radius: 24px; cursor: pointer; font-size: 14px; color: #606266;
  margin-bottom: 20px; transition: all 0.25s; font-weight: 500;
}
.back-btn:hover { color: #3498db; border-color: #3498db; transform: translateX(-2px); }
.detail-card {
  display: flex; gap: 36px; background: #fff; border-radius: 16px;
  padding: 36px; box-shadow: 0 2px 16px rgba(0,0,0,0.06);
}
.cover-section { flex-shrink: 0; }
.cover-img {
  width: 280px; height: 380px; object-fit: cover; border-radius: 12px;
  background: linear-gradient(135deg, #f5f7fa, #e8ecf1);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}
.info-section { flex: 1; }
.info-section h2 { font-size: 24px; font-weight: 700; margin-bottom: 16px; color: #2c3e50; }
.author, .category { font-size: 14px; color: #606266; margin-bottom: 8px; }
.price { font-size: 32px; color: #e74c3c; font-weight: 800; margin: 18px 0 10px; }
.stock { font-size: 13px; color: #909399; margin-bottom: 24px; }
.desc-box { background: #f8f9fb; border-radius: 10px; padding: 18px; margin-bottom: 24px; border-left: 3px solid #3498db; }
.desc-box h4 { font-size: 14px; margin-bottom: 8px; color: #2c3e50; font-weight: 600; }
.desc-box p { font-size: 13px; color: #606266; line-height: 1.8; }

.cart-action { display: flex; align-items: center; gap: 14px; }
.qty-ctrl { display: flex; align-items: center; gap: 0; border-radius: 8px; overflow: hidden; border: 2px solid #e8ecf1; }
.qty-ctrl button {
  width: 36px; height: 36px; border: none; background: #f8f9fb; cursor: pointer; font-size: 18px; color: #606266;
  transition: background 0.2s;
}
.qty-ctrl button:hover { background: #e8ecf1; }
.qty-ctrl button:disabled { opacity: 0.4; cursor: not-allowed; }
.qty-ctrl span { min-width: 36px; text-align: center; font-size: 16px; font-weight: 600; }

.fav-btn {
  padding: 10px 22px; background: #fff; color: #e74c3c; border: 2px solid #e74c3c;
  border-radius: 8px; font-size: 14px; cursor: pointer; transition: all 0.2s; font-weight: 500;
}
.fav-btn:hover { background: #fef0f0; }
.fav-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.add-cart-btn {
  padding: 10px 30px; background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: #fff; border: none; border-radius: 8px; font-size: 15px; cursor: pointer;
  transition: all 0.2s; font-weight: 600; box-shadow: 0 4px 12px rgba(231,76,60,0.3);
}
.add-cart-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(231,76,60,0.4); }
.add-cart-btn:disabled { background: #ccc; box-shadow: none; cursor: not-allowed; }

.msg-ok { margin-top: 12px; color: #27ae60; font-size: 13px; font-weight: 500; }
.msg-err { margin-top: 12px; color: #e74c3c; font-size: 13px; font-weight: 500; }
.loading { text-align: center; padding: 80px; color: #909399; }
</style>
