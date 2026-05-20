<template>
  <!-- 购物车页 -->
  <div class="cart-page">
    <h2>我的购物车</h2>

    <div v-if="cartList.length === 0" class="empty-cart">
      <p>购物车是空的</p>
      <router-link to="/books" class="go-shopping">去逛逛</router-link>
    </div>

    <template v-else>
      <!-- 全选栏 -->
      <div class="select-all-bar">
        <label><input type="checkbox" v-model="allChecked" @change="toggleAll" /> 全选</label>
        <button class="clear-btn" @click="handleClear">清空购物车</button>
      </div>

      <!-- 商品列表 -->
      <div class="cart-items">
        <div v-for="item in cartList" :key="item.cartId" class="cart-item">
          <input type="checkbox" v-model="item.checked" />
          <img :src="getBookInfo(item.bookId).coverImg" class="item-cover" @error="e => e.target.src=''" />
          <div class="item-info">
            <p class="item-name">{{ getBookInfo(item.bookId).bookName }}</p>
            <p class="item-author">{{ getBookInfo(item.bookId).author }}</p>
            <p class="item-price">¥{{ getBookInfo(item.bookId).price }}</p>
          </div>
          <div class="qty-ctrl">
            <button @click="changeQty(item, -1)" :disabled="item.quantity <= 1">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="changeQty(item, 1)">+</button>
          </div>
          <p class="item-subtotal">¥{{ (getBookInfo(item.bookId).price * item.quantity).toFixed(2) }}</p>
          <button class="item-del" @click="handleRemove(item.cartId)">删除</button>
        </div>
      </div>

      <!-- 底部结算栏 -->
      <div class="bottom-bar">
        <span>已选 {{ checkedCount }} 件，合计：</span>
        <span class="total-price">¥{{ totalPrice }}</span>
        <button class="checkout-btn" :disabled="checkedCount === 0" @click="openCheckout">去结算</button>
      </div>
    </template>

    <!-- 结算弹窗 -->
    <div v-if="checkoutVisible" class="dialog-overlay" @click.self="checkoutVisible = false">
      <div class="checkout-dialog">
        <h3>确认订单信息</h3>
        <!-- 已保存的地址列表 -->
        <div v-if="addrList.length > 0" class="addr-select">
          <p class="addr-title">选择收货地址</p>
          <div v-for="addr in addrList" :key="addr.addressId"
               :class="['addr-option', { 'addr-checked': selectedAddrId === addr.addressId }]"
               @click="selectAddr(addr)">
            <div class="addr-radio"></div>
            <div class="addr-text">
              <span class="addr-tag-name">{{ addr.receiverName }}</span>
              <span class="addr-tag-phone">{{ addr.receiverPhone }}</span>
              <span v-if="addr.isDefault === 1" class="default-tag">默认</span>
              <p class="addr-tag-detail">{{ addr.addressDetail }}</p>
            </div>
          </div>
        </div>
        <form @submit.prevent="handleCheckout">
          <p class="checkout-summary">共 {{ checkedCount }} 件商品，合计 ¥{{ totalPrice }}</p>
          <p v-if="orderMsg.text" :class="orderMsg.type === 'ok' ? 'msg-ok' : 'msg-err'">{{ orderMsg.text }}</p>
          <div class="dialog-btns">
            <button type="button" class="cancel-btn" @click="checkoutVisible = false">取消</button>
            <button type="submit" :disabled="submitting" class="confirm-btn">
              {{ submitting ? '提交中...' : '确认下单' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()
const cartList = ref([])
const bookMap = ref({})
const allChecked = ref(false)
const userId = computed(() => userStore.userInfo?.userId)

// 结算弹窗
const checkoutVisible = ref(false)
const submitting = ref(false)
const orderMsg = reactive({ text: '', type: 'ok' })
const form = reactive({ receiverName: '', receiverPhone: '', receiverAddress: '' })
const addrList = ref([])
const selectedAddrId = ref(null)

async function loadCart() {
  if (!userId.value) return
  const [cartRes, bookRes] = await Promise.all([
    request.get('/cart/list', { params: { userId: userId.value } }),
    request.get('/book/list')
  ])
  if (bookRes.code === 200) {
    bookRes.data.forEach(b => { bookMap.value[b.bookId] = b })
  }
  if (cartRes.code === 200) {
    cartList.value = cartRes.data.map(item => ({ ...item, checked: false }))
  }
}

function getBookInfo(bookId) {
  return bookMap.value[bookId] || {}
}

// 全选联动
const checkedCount = computed(() => cartList.value.filter(i => i.checked).length)
const totalPrice = computed(() => {
  return cartList.value
    .filter(i => i.checked)
    .reduce((sum, i) => sum + (getBookInfo(i.bookId).price || 0) * i.quantity, 0)
    .toFixed(2)
})

watch(() => cartList.value.map(i => i.checked), () => {
  allChecked.value = cartList.value.length > 0 && cartList.value.every(i => i.checked)
}, { deep: true })

function toggleAll() {
  cartList.value.forEach(i => { i.checked = allChecked.value })
}

// 修改数量
async function changeQty(item, delta) {
  const newQty = item.quantity + delta
  if (newQty < 1) return
  const res = await request.put('/cart/update', null, {
    params: { cartId: item.cartId, quantity: newQty }
  })
  if (res.code === 200) item.quantity = newQty
}

// 删除单项
async function handleRemove(cartId) {
  if (!confirm('确认删除？')) return
  const res = await request.delete(`/cart/remove/${cartId}`)
  if (res.code === 200) cartList.value = cartList.value.filter(i => i.cartId !== cartId)
}

// 清空购物车
async function handleClear() {
  if (!confirm('确认清空购物车？')) return
  const res = await request.delete('/cart/clear', { params: { userId: userId.value } })
  if (res.code === 200) cartList.value = []
}

// 打开结算弹窗，加载地址列表并自动选默认地址
async function openCheckout() {
  orderMsg.text = ''
  checkoutVisible.value = true
  selectedAddrId.value = null
  // 加载地址列表
  try {
    const res = await request.get('/address/list')
    if (res.code === 200) {
      addrList.value = res.data
      // 自动选默认地址
      const def = res.data.find(a => a.isDefault === 1)
      if (def) selectAddr(def)
      return
    }
  } catch (e) { /* 忽略 */ }
  // 无地址时用用户信息兜底
  if (!form.receiverName) form.receiverName = userStore.userInfo?.username || ''
  if (!form.receiverPhone) form.receiverPhone = userStore.userInfo?.phone || ''
}

/** 选择地址填入表单 */
function selectAddr(addr) {
  selectedAddrId.value = addr.addressId
  form.receiverName = addr.receiverName
  form.receiverPhone = addr.receiverPhone
  form.receiverAddress = addr.addressDetail
}

// 提交订单
async function handleCheckout() {
  if (submitting.value) return
  submitting.value = true
  orderMsg.text = ''
  if (!selectedAddrId.value) {
    orderMsg.text = '请选择收货地址'
    orderMsg.type = 'err'
    return
  }
  try {
    const cartIds = cartList.value.filter(i => i.checked).map(i => i.cartId)
    const res = await request.post('/order/checkout', {
      userId: userId.value,
      receiverName: form.receiverName,
      receiverPhone: form.receiverPhone,
      receiverAddress: form.receiverAddress,
      cartIds
    })
    if (res.code === 200) {
      checkoutVisible.value = false
      orderMsg.text = '下单成功！'
      orderMsg.type = 'ok'
      cartList.value = cartList.value.filter(i => !i.checked)
    } else {
      orderMsg.text = res.message || '下单失败'
      orderMsg.type = 'err'
    }
  } catch (e) {
    orderMsg.text = '请求异常，请重试'
    orderMsg.type = 'err'
  } finally {
    submitting.value = false
  }
}

onMounted(() => { loadCart() })
</script>

<style scoped>
.empty-cart { text-align: center; padding: 80px 0; }
.empty-cart p { font-size: 18px; color: #909399; margin-bottom: 16px; }
.go-shopping { color: #3498db; font-size: 15px; font-weight: 500; }

.select-all-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; font-size: 14px; padding: 0 4px; }
.clear-btn { padding: 6px 16px; background: #e74c3c; color: #fff; border: none; border-radius: 8px; cursor: pointer; font-size: 13px; transition: all 0.2s; }
.clear-btn:hover { background: #c0392b; }

.cart-items { background: #fff; border-radius: 14px; overflow: hidden; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.cart-item {
  display: flex; align-items: center; gap: 18px; padding: 18px 24px;
  border-bottom: 1px solid #f0f2f5; transition: background 0.2s;
}
.cart-item:hover { background: #fafbfc; }
.item-cover { width: 72px; height: 96px; object-fit: cover; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
.item-info { flex: 1; min-width: 0; }
.item-name { font-size: 15px; font-weight: 600; color: #2c3e50; }
.item-author { font-size: 12px; color: #909399; margin: 4px 0; }
.item-price { font-size: 16px; color: #e74c3c; font-weight: 700; }

.qty-ctrl { display: flex; align-items: center; gap: 0; border-radius: 8px; overflow: hidden; border: 2px solid #e8ecf1; }
.qty-ctrl button {
  width: 30px; height: 30px; border: none; background: #f8f9fb; cursor: pointer; font-size: 16px; color: #606266;
}
.qty-ctrl button:hover { background: #e8ecf1; }
.qty-ctrl button:disabled { opacity: 0.4; cursor: not-allowed; }
.qty-ctrl span { min-width: 28px; text-align: center; font-size: 14px; font-weight: 600; }

.item-subtotal { font-size: 15px; font-weight: 700; color: #e74c3c; width: 100px; text-align: right; }
.item-del { padding: 6px 12px; background: #f8f9fb; color: #e74c3c; border: 1px solid #f0f2f5; border-radius: 6px; cursor: pointer; font-size: 12px; transition: all 0.2s; }
.item-del:hover { background: #fef0f0; border-color: #e74c3c; }

.bottom-bar {
  display: flex; align-items: center; justify-content: flex-end; gap: 20px;
  margin-top: 20px; padding: 18px 24px; background: #fff; border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.total-price { font-size: 24px; font-weight: 800; color: #e74c3c; }
.checkout-btn {
  padding: 12px 36px; background: linear-gradient(135deg, #e74c3c, #c0392b);
  color: #fff; border: none; border-radius: 10px; font-size: 16px; cursor: pointer;
  font-weight: 600; box-shadow: 0 4px 12px rgba(231,76,60,0.3); transition: all 0.25s;
}
.checkout-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(231,76,60,0.4); }
.checkout-btn:disabled { background: #ccc; box-shadow: none; cursor: not-allowed; transform: none; }

.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; backdrop-filter: blur(4px); }
.checkout-dialog { width: 520px; max-height: 80vh; overflow: auto; background: #fff; border-radius: 16px; padding: 28px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); }
.checkout-dialog h3 { margin-bottom: 20px; font-size: 18px; color: #2c3e50; }

.addr-select { margin-bottom: 18px; }
.addr-title { font-size: 13px; color: #606266; margin-bottom: 10px; font-weight: 600; }
.addr-option { display: flex; align-items: flex-start; gap: 12px; padding: 12px 14px; border: 2px solid #e8ecf1; border-radius: 10px; margin-bottom: 8px; cursor: pointer; transition: all 0.2s; }
.addr-option:hover { border-color: #3498db; }
.addr-checked { border-color: #3498db; background: #ecf5ff; }
.addr-radio { width: 18px; height: 18px; border-radius: 50%; border: 2px solid #dcdfe6; flex-shrink: 0; margin-top: 2px; transition: all 0.2s; }
.addr-checked .addr-radio { border-color: #3498db; background: #3498db; box-shadow: inset 0 0 0 4px #fff; }
.addr-text { flex: 1; min-width: 0; }
.addr-tag-name { font-weight: 600; margin-right: 8px; }
.addr-tag-phone { color: #909399; font-size: 13px; }
.default-tag { display: inline-block; background: #3498db; color: #fff; font-size: 10px; padding: 2px 6px; border-radius: 4px; margin-left: 6px; vertical-align: middle; }
.addr-tag-detail { color: #606266; font-size: 12px; margin-top: 4px; }

.checkout-summary { font-size: 16px; font-weight: 700; color: #e74c3c; margin: 16px 0; text-align: center; }
.msg-ok { color: #27ae60; font-size: 13px; margin-bottom: 8px; text-align: center; font-weight: 500; }
.msg-err { color: #e74c3c; font-size: 13px; margin-bottom: 8px; text-align: center; font-weight: 500; }
.dialog-btns { display: flex; justify-content: flex-end; gap: 12px; }
.cancel-btn { padding: 10px 24px; background: #f8f9fb; border: 2px solid #e8ecf1; border-radius: 10px; cursor: pointer; font-size: 14px; font-weight: 500; }
.confirm-btn { padding: 10px 28px; background: linear-gradient(135deg, #e74c3c, #c0392b); color: #fff; border: none; border-radius: 10px; cursor: pointer; font-size: 14px; font-weight: 600; }
.confirm-btn:disabled { background: #ccc; cursor: not-allowed; }
</style>
