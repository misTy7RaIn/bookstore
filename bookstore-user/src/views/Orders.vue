<template>
  <!-- 我的订单 -->
  <div class="orders-page">
    <h2>我的订单</h2>

    <!-- 状态筛选 -->
    <div class="status-tabs">
      <button v-for="tab in statusTabs" :key="tab.value"
              :class="filterStatus === tab.value ? 'tab-active' : ''"
              @click="filterStatus = tab.value">{{ tab.label }}</button>
    </div>

    <!-- 订单列表 -->
    <div v-if="filteredList.length === 0" class="empty">暂无订单</div>
    <div v-for="o in filteredList" :key="o.orderId" class="order-card">
      <div class="order-header">
        <span>{{ o.bookNames }}</span>
        <span :class="statusClass(o.orderStatus)">{{ statusLabel(o.orderStatus) }}</span>
      </div>
      <div class="order-body">
        <p>收货人：{{ o.receiverName }} | {{ o.receiverPhone }}</p>
        <p>地址：{{ o.receiverAddress }}</p>
        <p>金额：<span class="price">¥{{ o.totalPrice }}</span></p>
        <p>下单时间：{{ formatTime(o.createTime) }}</p>
        <p v-if="o.payTime">支付时间：{{ formatTime(o.payTime) }}</p>
      </div>
      <div class="order-actions">
        <button class="detail-btn" @click="openDetail(o.orderId)">查看详情</button>
        <button v-if="o.orderStatus === 1" class="cancel-btn" @click="handleCancel(o.orderId)">取消订单</button>
        <button v-if="o.orderStatus === 2" class="receive-btn" @click="handleReceive(o.orderId)">确认收货</button>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="detailVisible" class="dialog-overlay" @click.self="detailVisible = false">
      <div class="detail-dialog">
        <h3>订单详情</h3>
        <div v-if="detail.order" class="detail-body">
          <div class="info-row"><span>订单号：</span>{{ detail.order.orderId }}</div>
          <div class="info-row"><span>状态：</span><span :class="statusClass(detail.order.orderStatus)">{{ statusLabel(detail.order.orderStatus) }}</span></div>
          <div class="info-row"><span>收货人：</span>{{ detail.order.receiverName }}</div>
          <div class="info-row"><span>电话：</span>{{ detail.order.receiverPhone }}</div>
          <div class="info-row"><span>地址：</span>{{ detail.order.receiverAddress }}</div>
          <div class="info-row"><span>总金额：</span>¥{{ detail.order.totalPrice }}</div>
          <div class="info-row"><span>创建时间：</span>{{ formatTime(detail.order.createTime) }}</div>
          <div class="info-row"><span>支付时间：</span>{{ formatTime(detail.order.payTime) }}</div>

          <h4 style="margin:16px 0 8px;">商品明细</h4>
          <table class="item-table">
            <thead><tr><th>图书名称</th><th>单价</th><th>数量</th><th>小计</th></tr></thead>
            <tbody>
              <tr v-for="item in detail.items" :key="item.orderItemId">
                <td>{{ item.bookName }}</td>
                <td>¥{{ item.price }}</td>
                <td>{{ item.quantity }}</td>
                <td>¥{{ (item.price * item.quantity).toFixed(2) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="dialog-btns">
          <button class="close-btn" @click="detailVisible = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const orderList = ref([])
const filterStatus = ref(-1)

// 状态配置
const statusTabs = [
  { label: '全部', value: -1 },
  { label: '待发货', value: 1 }, { label: '待收货', value: 2 },
  { label: '已收货', value: 3 }, { label: '已取消', value: 4 }
]

const filteredList = computed(() => {
  if (filterStatus.value === -1) return orderList.value
  return orderList.value.filter(o => o.orderStatus === filterStatus.value)
})

// 详情弹窗
const detailVisible = ref(false)
const detail = reactive({ order: null, items: [] })

async function loadOrders() {
  if (!userStore.userInfo) return
  const res = await request.get('/order/list', { params: { userId: userStore.userInfo.userId } })
  if (res.code === 200) orderList.value = res.data
}

async function openDetail(orderId) {
  const res = await request.get(`/order/detail/${orderId}`)
  if (res.code === 200) {
    detail.order = res.data.order
    detail.items = res.data.items
    detailVisible.value = true
  }
}

async function handlePay(orderId) {
  if (!confirm('确认支付？')) return
  const res = await request.put(`/order/pay/${orderId}`)
  if (res.code === 200) loadOrders()
}

async function handleCancel(orderId) {
  if (!confirm('确认取消该订单？')) return
  const res = await request.put(`/order/cancel/${orderId}`)
  if (res.code === 200) loadOrders()
}

async function handleReceive(orderId) {
  if (!confirm('确认已收到商品？')) return
  const res = await request.put(`/order/receive/${orderId}`)
  if (res.code === 200) loadOrders()
}

function statusLabel(s) {
  return ['待支付', '待发货', '待收货', '已收货', '已取消'][s] || '未知'
}

function statusClass(s) {
  return ['tag-warn', 'tag-info', 'tag-primary', 'tag-ok', 'tag-cancel'][s] || ''
}

function formatTime(t) {
  return t ? t.replace('T', ' ') : '--'
}

onMounted(() => { loadOrders() })
</script>

<style scoped>
.status-tabs { display: flex; gap: 8px; margin-bottom: 20px; flex-wrap: wrap; }
.status-tabs button {
  padding: 8px 20px; background: #fff; border: 2px solid #e8ecf1;
  border-radius: 22px; cursor: pointer; font-size: 13px; font-weight: 500;
  transition: all 0.25s; color: #606266;
}
.status-tabs button:hover { color: #3498db; border-color: #3498db; }
.tab-active {
  background: linear-gradient(135deg, #3498db, #2980b9) !important;
  color: #fff !important; border-color: transparent !important;
  box-shadow: 0 2px 8px rgba(52,152,219,0.3);
}

.empty { text-align: center; color: #999; padding: 60px; }

.order-card {
  background: #fff; border-radius: 14px; padding: 20px 24px; margin-bottom: 16px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04); transition: box-shadow 0.2s;
}
.order-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
.order-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 14px; border-bottom: 1px solid #f0f2f5; font-size: 15px; font-weight: 600; color: #2c3e50; }
.order-body { padding: 14px 0; font-size: 13px; color: #606266; line-height: 2; }
.price { color: #e74c3c; font-weight: 700; font-size: 16px; }

.order-actions { display: flex; gap: 10px; justify-content: flex-end; padding-top: 10px; }
.detail-btn { padding: 6px 16px; background: #f8f9fb; border: 1px solid #e8ecf1; border-radius: 8px; cursor: pointer; font-size: 13px; transition: all 0.2s; }
.detail-btn:hover { border-color: #3498db; color: #3498db; }
.receive-btn { padding: 6px 16px; background: #27ae60; color: #fff; border: none; border-radius: 8px; cursor: pointer; font-size: 13px; font-weight: 500; }
.cancel-btn { padding: 6px 16px; background: #f8f9fb; color: #e74c3c; border: 1px solid #e74c3c; border-radius: 8px; cursor: pointer; font-size: 13px; transition: all 0.2s; }
.cancel-btn:hover { background: #fef0f0; }

.tag-warn { color: #e67e22; font-weight: 600; }
.tag-info { color: #3498db; font-weight: 600; }
.tag-primary { color: #3498db; font-weight: 600; }
.tag-ok { color: #27ae60; font-weight: 600; }
.tag-cancel { color: #e74c3c; font-weight: 600; }

.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; backdrop-filter: blur(4px); }
.detail-dialog { width: 620px; max-height: 85vh; overflow: auto; background: #fff; border-radius: 16px; padding: 28px; box-shadow: 0 20px 60px rgba(0,0,0,0.2); }
.detail-dialog h3 { margin-bottom: 16px; font-size: 18px; color: #2c3e50; }
.info-row { margin-bottom: 10px; font-size: 14px; color: #303133; }
.info-row span { color: #909399; display: inline-block; width: 80px; font-weight: 500; }
.item-table { width: 100%; border-collapse: collapse; margin-top: 10px; border-radius: 8px; overflow: hidden; }
.item-table th, .item-table td { padding: 10px 12px; border: 1px solid #f0f2f5; text-align: left; font-size: 13px; }
.item-table th { background: #f8f9fb; color: #2c3e50; font-weight: 600; }
.dialog-btns { display: flex; justify-content: flex-end; margin-top: 20px; }
.close-btn { padding: 8px 24px; background: #f8f9fb; border: 1px solid #e8ecf1; border-radius: 8px; cursor: pointer; font-size: 14px; }
</style>
