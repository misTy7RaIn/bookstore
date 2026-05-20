<template>
  <!-- 图书管理页 -->
  <div class="book-manage">
    <!-- 顶部操作栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <input v-model="keyword" placeholder="🔍 搜索书名..." @input="onSearch" class="search-input" />
        <select v-model="categoryId" @change="loadBooks" class="filter-select">
          <option :value="0">全部分类</option>
          <option v-for="c in categories" :key="c.categoryId" :value="c.categoryId">{{ c.categoryName }}</option>
        </select>
      </div>
      <button class="add-btn" @click="openAdd">+ 新增图书</button>
    </div>

    <!-- 提示消息 -->
    <div v-if="msg.text" :class="msg.type === 'ok' ? 'msg-ok' : 'msg-err'">{{ msg.text }}</div>

    <!-- 图书表格 -->
    <table class="book-table">
      <thead>
        <tr>
          <th>序号</th><th>封面</th><th>书名</th><th>作者</th><th>分类</th><th>价格</th><th>库存</th><th>状态</th><th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="bookList.length === 0">
          <td colspan="9" style="text-align:center;color:#999;padding:40px;">暂无数据</td>
        </tr>
        <tr v-for="(book, index) in bookList" :key="book.bookId">
          <td>{{ index + 1 }}</td>
          <td><img :src="book.coverImg" class="cover-thumb" @error="e => e.target.src=''" /></td>
          <td>{{ book.bookName }}</td>
          <td>{{ book.author }}</td>
          <td>{{ getCategoryName(book.categoryId) }}</td>
          <td>¥{{ book.price }}</td>
          <td>{{ book.stock }}</td>
          <td><span :class="book.status === 1 ? 'tag-on' : 'tag-off'">{{ book.status === 1 ? '上架' : '下架' }}</span></td>
          <td>
            <button class="edit-btn" @click="openEdit(book)">编辑</button>
            <button class="del-btn" @click="confirmDelete(book.bookId, book.bookName)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 新增/编辑弹窗 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="closeDialog">
      <div class="dialog">
        <h3>{{ isEdit ? '编辑图书' : '新增图书' }}</h3>
        <form @submit.prevent="handleSave">
          <div class="form-row">
            <label>书名</label><input v-model="form.bookName" required maxlength="100" />
          </div>
          <div class="form-row">
            <label>作者</label><input v-model="form.author" required maxlength="50" />
          </div>
          <div class="form-row">
            <label>分类</label>
            <select v-model="form.categoryId" required>
              <option v-for="c in categories" :key="c.categoryId" :value="c.categoryId">{{ c.categoryName }}</option>
            </select>
          </div>
          <div class="form-row half">
            <div><label>价格 (¥)</label><input v-model="form.price" type="number" step="0.01" min="0" required /></div>
            <div><label>库存</label><input v-model="form.stock" type="number" min="0" required /></div>
          </div>
          <div class="form-row">
            <label>简介</label><textarea v-model="form.description" rows="3" maxlength="500"></textarea>
          </div>
          <div class="form-row">
            <label>封面图片</label>
            <!-- 编辑时显示当前封面 -->
            <div v-if="isEdit && form.coverImg" class="current-cover">
              <img :src="form.coverImg" @error="e => e.target.style.display='none'" />
              <span>当前封面</span>
            </div>
            <input type="file" ref="fileInput" @change="onFileChange" accept="image/*" />
            <span class="file-hint">{{ coverFile ? coverFile.name : (isEdit ? '留空则不更换封面' : '请选择封面图片') }}</span>
          </div>
          <div class="form-row">
            <label>状态</label>
            <select v-model="form.status">
              <option :value="1">上架</option><option :value="0">下架</option>
            </select>
          </div>
          <div class="dialog-btns">
            <button type="button" class="cancel-btn" @click="closeDialog">取消</button>
            <button type="submit" :disabled="saving" class="confirm-btn">{{ saving ? '保存中...' : (isEdit ? '保存修改' : '新增图书') }}</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="delVisible" class="dialog-overlay" @click.self="delVisible = false">
      <div class="dialog confirm-dialog">
        <h3>确认删除</h3>
        <p>确定要删除图书「{{ delBookName }}」吗？此操作不可撤销。</p>
        <div class="dialog-btns">
          <button class="cancel-btn" @click="delVisible = false">取消</button>
          <button class="del-btn" :disabled="deleting" @click="doDelete">{{ deleting ? '删除中...' : '确认删除' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'

// 图书列表
const bookList = ref([])
const categories = ref([])
const keyword = ref('')
const categoryId = ref(0)

// 提示消息
const msg = reactive({ text: '', type: 'ok' })

// 编辑弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const coverFile = ref(null)
const fileInput = ref(null)
const saving = ref(false)
const form = reactive({ bookId: null, bookName: '', author: '', categoryId: null, price: '', stock: 0, description: '', status: 1, coverImg: '' })

// 删除确认
const delVisible = ref(false)
const delBookId = ref(null)
const delBookName = ref('')
const deleting = ref(false)

/**
 * 显示通知
 */
function showMsg(text, type = 'ok') {
  msg.text = text; msg.type = type
  setTimeout(() => { msg.text = '' }, 3000)
}

/**
 * 加载图书列表
 */
async function loadBooks() {
  const res = await request.get('/book/list', { params: { categoryId: categoryId.value || undefined } })
  if (res.code === 200) bookList.value = res.data
}

/**
 * 加载分类列表
 */
async function loadCategories() {
  const res = await request.get('/book/categories')
  if (res.code === 200) categories.value = res.data
}

function getCategoryName(id) {
  const c = categories.value.find(c => c.categoryId === id)
  return c ? c.categoryName : ''
}

/**
 * 搜索（防抖 300ms）
 */
let timer = null
function onSearch() {
  clearTimeout(timer)
  timer = setTimeout(async () => {
    if (!keyword.value.trim()) { loadBooks(); return }
    const res = await request.get('/book/search', { params: { keyword: keyword.value } })
    if (res.code === 200) bookList.value = res.data
  }, 300)
}

/**
 * 打开新增弹窗
 */
function openAdd() {
  isEdit.value = false
  Object.assign(form, { bookId: null, bookName: '', author: '', categoryId: null, price: '', stock: 0, description: '', status: 1, coverImg: '' })
  coverFile.value = null
  if (fileInput.value) fileInput.value.value = ''
  dialogVisible.value = true
}

/**
 * 打开编辑弹窗
 */
function openEdit(book) {
  isEdit.value = true
  Object.assign(form, {
    bookId: book.bookId, bookName: book.bookName, author: book.author,
    categoryId: book.categoryId, price: book.price, stock: book.stock,
    description: book.description || '', status: book.status, coverImg: book.coverImg || ''
  })
  coverFile.value = null
  if (fileInput.value) fileInput.value.value = ''
  dialogVisible.value = true
}

/**
 * 关闭弹窗
 */
function closeDialog() {
  dialogVisible.value = false
  coverFile.value = null
}

/**
 * 文件选择
 */
function onFileChange(e) {
  coverFile.value = e.target.files[0] || null
}

/**
 * 保存（新增 / 编辑）
 */
async function handleSave() {
  if (saving.value) return
  if (!form.bookName.trim()) { showMsg('请输入书名', 'err'); return }
  if (!form.author.trim()) { showMsg('请输入作者', 'err'); return }
  if (!form.categoryId) { showMsg('请选择分类', 'err'); return }
  if (!form.price) { showMsg('请输入价格', 'err'); return }
  saving.value = true
  try {
    const fd = new FormData()
    fd.append('bookName', form.bookName)
    fd.append('author', form.author)
    fd.append('categoryId', form.categoryId)
    fd.append('price', form.price)
    fd.append('stock', form.stock)
    fd.append('description', form.description || '')
    fd.append('status', form.status)
    if (form.bookId) fd.append('bookId', form.bookId)
    if (coverFile.value) fd.append('coverFile', coverFile.value)

    // 调试：打印 FormData 内容
    console.log('=== FormData 内容 ===')
    for (const [k, v] of fd.entries()) { console.log(k + ':', v) }

    const res = await request.post('/book/save', fd)
    if (res.code === 200) {
      showMsg(isEdit.value ? '修改成功' : '新增成功')
      closeDialog()
      loadBooks()
    } else {
      showMsg(res.message || '操作失败', 'err')
    }
  } catch (e) {
    showMsg('请求异常，请重试', 'err')
  } finally {
    saving.value = false
  }
}

/**
 * 弹出删除确认
 */
function confirmDelete(id, name) {
  delBookId.value = id
  delBookName.value = name
  delVisible.value = true
}

/**
 * 执行删除
 */
async function doDelete() {
  if (deleting.value) return
  deleting.value = true
  try {
    const res = await request.delete(`/book/${delBookId.value}`)
    if (res.code === 200) {
      showMsg('删除成功')
      delVisible.value = false
      loadBooks()
    } else {
      showMsg(res.message || '删除失败', 'err')
    }
  } catch (e) {
    showMsg('请求异常，请重试', 'err')
  } finally {
    deleting.value = false
  }
}

onMounted(() => { loadBooks(); loadCategories() })
</script>

<style scoped>
.book-manage { height: 100%; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.toolbar-left { display: flex; gap: 12px; }
.search-input { width: 220px; height: 38px; padding: 0 14px; border: 2px solid #e8ecf1; border-radius: 10px; font-size: 13px; outline: none; transition: border-color 0.2s; }
.search-input:focus { border-color: #3498db; }
.filter-select { height: 38px; padding: 0 10px; border: 2px solid #e8ecf1; border-radius: 10px; font-size: 13px; outline: none; }
.add-btn { height: 38px; padding: 0 22px; background: linear-gradient(135deg, #3498db, #2980b9); color: #fff; border: none; border-radius: 10px; cursor: pointer; font-weight: 600; box-shadow: 0 2px 8px rgba(52,152,219,0.25); transition: all 0.2s; }
.add-btn:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(52,152,219,0.35); }

.msg-ok { padding: 10px 16px; background: #f0f9eb; color: #27ae60; border-radius: 8px; margin-bottom: 12px; font-size: 13px; }
.msg-err { padding: 10px 16px; background: #fef0f0; color: #e74c3c; border-radius: 8px; margin-bottom: 12px; font-size: 13px; }

.book-table { width: 100%; background: #fff; border-radius: 14px; border-collapse: collapse; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.04); }
.book-table th, .book-table td { padding: 12px 16px; border-bottom: 1px solid #f0f2f5; text-align: left; font-size: 14px; }
.book-table th { background: #f8f9fb; color: #2c3e50; font-weight: 600; }
.book-table tbody tr:hover { background: #fafbfc; }
.cover-thumb { width: 48px; height: 64px; object-fit: cover; border-radius: 6px; }
.tag-on { color: #27ae60; font-weight: 600; }
.tag-off { color: #e74c3c; font-weight: 600; }
.edit-btn { padding: 5px 14px; background: #f39c12; color: #fff; border: none; border-radius: 6px; cursor: pointer; margin-right: 6px; font-size: 13px; transition: all 0.2s; }
.edit-btn:hover { background: #e67e22; }
.del-btn { padding: 5px 14px; background: #e74c3c; color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 13px; transition: all 0.2s; }
.del-btn:hover { background: #c0392b; }

.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; backdrop-filter: blur(4px); }
.dialog { width: 540px; max-height: 90vh; overflow: auto; background: #fff; border-radius: 16px; padding: 28px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); }
.confirm-dialog { width: 400px; text-align: center; }
.dialog h3 { margin-bottom: 20px; font-size: 18px; color: #2c3e50; }
.confirm-dialog p { margin-bottom: 20px; color: #606266; }
.form-row { margin-bottom: 14px; }
.form-row label { display: block; margin-bottom: 6px; color: #2c3e50; font-size: 13px; font-weight: 500; }
.form-row input, .form-row select, .form-row textarea { width: 100%; height: 38px; padding: 0 12px; border: 2px solid #e8ecf1; border-radius: 8px; font-size: 14px; outline: none; transition: border-color 0.2s; }
.form-row input:focus, .form-row select:focus { border-color: #3498db; }
.form-row textarea { height: auto; padding: 10px 12px; resize: vertical; }
.half { display: flex; gap: 12px; }
.half div { flex: 1; }
.current-cover { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.current-cover img { width: 60px; height: 80px; object-fit: cover; border-radius: 6px; }
.current-cover span { font-size: 12px; color: #909399; }
.file-hint { font-size: 12px; color: #909399; margin-left: 8px; }
.dialog-btns { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
.cancel-btn { padding: 8px 22px; background: #f8f9fb; border: 2px solid #e8ecf1; border-radius: 8px; cursor: pointer; font-weight: 500; }
.confirm-btn { padding: 8px 22px; background: linear-gradient(135deg, #3498db, #2980b9); color: #fff; border: none; border-radius: 8px; cursor: pointer; font-weight: 600; }
.confirm-btn:disabled { background: #ccc; cursor: not-allowed; }
</style>
