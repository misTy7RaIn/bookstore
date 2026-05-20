<template>
  <!-- 首页 -->
  <div class="home">
    <!-- Banner -->
    <div class="banner">
      <h2>欢迎来到在线图书商城</h2>
      <p>海量好书，尽在掌握</p>
    </div>

    <!-- 分类快捷入口 -->
    <div class="category-bar">
      <button v-for="c in categories" :key="c.categoryId"
              :class="activeCat === c.categoryId ? 'cat-active' : ''"
              @click="activeCat = c.categoryId; loadBooks()">{{ c.categoryName }}</button>
    </div>

    <!-- 图书网格 -->
    <div class="book-grid">
      <div v-if="bookList.length === 0" class="empty">暂无图书</div>
      <div v-for="book in bookList" :key="book.bookId" class="book-card" @click="goDetail(book.bookId)">
        <img :src="book.coverImg" class="book-cover" @error="e => e.target.src=''" />
        <div class="book-info">
          <p class="book-name">{{ book.bookName }}</p>
          <p class="book-author">{{ book.author }}</p>
          <p class="book-price">¥{{ book.price }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const bookList = ref([])
const activeCat = ref(0)
const categories = ref([{ categoryId: 0, categoryName: '全部' }])

/**
 * 加载分类（仅子分类：parentId > 0）
 */
async function loadCategories() {
  const res = await request.get('/book/categories')
  if (res.code === 200) {
    categories.value = [
      { categoryId: 0, categoryName: '全部' },
      ...res.data.filter(c => c.parentId > 0)
    ]
  }
}

/**
 * 加载图书
 */
async function loadBooks() {
  const params = activeCat.value > 0 ? { categoryId: activeCat.value } : {}
  const res = await request.get('/book/list', { params })
  if (res.code === 200) bookList.value = res.data
}

function goDetail(id) {
  router.push(`/book/${id}`)
}

onMounted(() => { loadCategories(); loadBooks() })
</script>

<style scoped>
.banner {
  height: 220px; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  border-radius: 16px; display: flex; flex-direction: column; align-items: center;
  justify-content: center; color: #fff; margin-bottom: 28px;
  position: relative; overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}
.banner::before {
  content: ''; position: absolute; inset: 0;
  background: radial-gradient(circle at 30% 50%, rgba(255,255,255,0.08) 0%, transparent 60%),
              radial-gradient(circle at 70% 50%, rgba(255,255,255,0.05) 0%, transparent 50%);
}
.banner h2 { font-size: 32px; font-weight: 800; position: relative; z-index: 1; letter-spacing: 2px; }
.banner p { margin-top: 10px; font-size: 15px; opacity: 0.7; position: relative; z-index: 1; }

.category-bar { display: flex; gap: 10px; margin-bottom: 24px; flex-wrap: wrap; }
.category-bar button {
  padding: 8px 22px; background: #fff; border: 2px solid #e8ecf1;
  border-radius: 24px; cursor: pointer; font-size: 13px; font-weight: 500;
  transition: all 0.25s; color: #606266;
}
.category-bar button:hover { color: #3498db; border-color: #3498db; transform: translateY(-1px); }
.cat-active {
  background: linear-gradient(135deg, #3498db, #2980b9) !important;
  color: #fff !important; border-color: transparent !important;
  box-shadow: 0 2px 8px rgba(52,152,219,0.3);
}

.book-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 20px; }
.empty { grid-column: 1 / -1; text-align: center; color: #999; padding: 60px; }
.book-card {
  background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06); transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.book-card:hover { transform: translateY(-6px); box-shadow: 0 12px 28px rgba(0,0,0,0.15); }
.book-cover { width: 100%; height: 220px; object-fit: cover; background: linear-gradient(135deg, #f5f7fa, #e8ecf1); }
.book-info { padding: 14px; }
.book-name { font-size: 14px; font-weight: 600; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: #2c3e50; }
.book-author { font-size: 12px; color: #909399; margin: 4px 0; }
.book-price { font-size: 18px; color: #e74c3c; font-weight: 700; }
</style>
