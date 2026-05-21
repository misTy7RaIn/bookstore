<template>
  <!-- 订单管理页 -->
  <div class="order-manage">
    <!-- 状态筛选标签 -->
    <div class="status-tabs">
      <button v-for="tab in statusTabs" :key="tab.value"
              :class="tab.value === filterStatus ? 'tab-active' : ''"
              @click="filterStatus = tab.value">{{ tab.label }}</button>
    </div>

    <!-- 提示消息 -->
    <div v-if="msg.text" :class="msg.type === 'ok' ? 'msg-ok' : 'msg-err'">{{ msg.text }}</div>

    <!-- 订单表格 -->
    <table class="order-table">
      <thead>
        <tr>
          <th>书籍</th><th>金额</th><th>收货人</th><th>电话</th><th>状态</th><th>创建时间</th><th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="filteredList.length === 0">
          <td colspan="7" style="text-align:center;color:#999;padding:40px;">暂无订单</td>
        </tr>
        <tr v-for="o in filteredList" :key="o.orderId">
          <td>{{ o.bookNames }}</td>
          <td>¥{{ o.totalPrice }}</td>
          <td>{{ o.receiverName }}</td>
          <td>{{ o.receiverPhone }}</td>
          <td><span :class="statusClass(o.orderStatus)">{{ statusLabel(o.orderStatus) }}</span></td>
          <td>{{ formatTime(o.createTime) }}</td>
          <td>
            <button class="detail-btn" @click="openDetail(o.orderId)">详情</button>
            <button v-if="o.orderStatus === 1" class="deliver-action-btn" @click="handleDeliver(o.orderId)">发货</button>
            <button v-if="o.orderStatus === 1" class="cancel-action-btn" @click="handleCancel(o.orderId)">取消</button>
            <button v-if="o.orderStatus === 3 || o.orderStatus === 4" class="delete-action-btn" @click="handleDelete(o.orderId)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 详情弹窗 -->
    <div v-if="detailVisible" class="dialog-overlay" @click.self="detailVisible = false">
      <div class="detail-dialog">
        <h3>订单详情</h3>
        <div v-if="detailData.order" class="detail-body">
          <div class="info-row"><span>订单号：</span>{{ detailData.order.orderId }}</div>
          <div class="info-row"><span>收货人：</span>{{ detailData.order.receiverName }}</div>
          <div class="info-row"><span>电话：</span>{{ detailData.order.receiverPhone }}</div>
          <div class="info-row"><span>地址：</span>{{ detailData.order.receiverAddress }}</div>
          <div class="info-row"><span>状态：</span><span :class="statusClass(detailData.order.orderStatus)">{{ statusLabel(detailData.order.orderStatus) }}</span></div>
          <div class="info-row"><span>总金额：</span>¥{{ detailData.order.totalPrice }}</div>
          <div class="info-row"><span>创建时间：</span>{{ formatTime(detailData.order.createTime) }}</div>
          <div class="info-row"><span>支付时间：</span>{{ formatTime(detailData.order.payTime) }}</div>
          <div class="info-row"><span>发货时间：</span>{{ formatTime(detailData.order.deliverTime) }}</div>

          <!-- 明细列表 -->
          <h4 style="margin: 16px 0 8px;">商品明细</h4>
          <table class="item-table">
            <thead><tr><th>图书名称</th><th>单价</th><th>数量</th><th>小计</th></tr></thead>
            <tbody>
              <tr v-for="item in detailData.items" :key="item.orderItemId">
                <td>{{ item.bookName }}</td>
                <td>¥{{ item.price }}</td>
                <td>{{ item.quantity }}</td>
                <td>¥{{ (item.price * item.quantity).toFixed(2) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="dialog-btns">
          <button class="cancel-btn" @click="detailVisible = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import request from '@/utils/request'

// 状态配置
const statusTabs = [
  { label: '全部', value: -1 },
  { label: '待发货', value: 1 }, { label: '待收货', value: 2 },
  { label: '已收货', value: 3 }, { label: '已取消', value: 4 }
]

// 订单列表
const orderList = ref([])
const filterStatus = ref(-1)
const msg = reactive({ text: '', type: 'ok' })

// 详情弹窗
const detailVisible = ref(false)
const detailData = reactive({ order: null, items: [] })

// 筛选后的列表
const filteredList = computed(() => {
  if (filterStatus.value === -1) return orderList.value
  return orderList.value.filter(o => o.orderStatus === filterStatus.value)
})

/**
 * 加载订单列表
 */
async function loadOrders() {
  const res = await request.get('/order/list')
  if (res.code === 200) orderList.value = res.data
}

/**
 * 打开详情
 */
async function openDetail(orderId) {
  const res = await request.get(`/order/detail/${orderId}`)
  if (res.code === 200) {
    detailData.order = res.data.order
    detailData.items = res.data.items
    detailVisible.value = true
  }
}

/**
 * 发货
 */
async function handleDeliver(orderId) {
  if (!confirm('确认发货？')) return
  const res = await request.put(`/order/deliver/${orderId}`)
  if (res.code === 200) { showMsg('发货成功'); loadOrders() }
  else showMsg(res.message || '操作失败', 'err')
}

/**
 * 取消订单
 */
async function handleCancel(orderId) {
  if (!confirm('确认取消该订单？')) return
  const res = await request.put(`/order/cancel/${orderId}`)
  if (res.code === 200) { showMsg('已取消'); loadOrders() }
  else showMsg(res.message || '操作失败', 'err')
}

/**
 * 删除订单
 */
async function handleDelete(orderId) {
  if (!confirm('确认删除该订单？删除后不可恢复。')) return
  const res = await request.delete(`/order/${orderId}`)
  if (res.code === 200) { showMsg('已删除'); loadOrders() }
  else showMsg(res.message || '操作失败', 'err')
}

function showMsg(text, type = 'ok') {
  msg.text = text; msg.type = type
  setTimeout(() => { msg.text = '' }, 3000)
}

function statusLabel(s) {
  return ['', '待发货', '待收货', '已收货', '已取消'][s] || '未知'
}

function statusClass(s) {
  return ['', 'tag-info', 'tag-primary', 'tag-ok', 'tag-cancel'][s] || ''
}

function formatTime(t) {
  return t ? t.replace('T', ' ') : '--'
}

onMounted(() => { loadOrders() })
</script>

<style scoped>
.order-manage { height: 100%; }
.status-tabs { display: flex; gap: 8px; margin-bottom: 16px; }
.status-tabs button { padding: 6px 20px; background: #f5f7fa; border: 1px solid #dcdfe6; border-radius: 4px; cursor: pointer; font-size: 13px; }
.status-tabs button:hover { color: #409eff; border-color: #409eff; }
.tab-active { background: #409eff !important; color: #fff !important; border-color: #409eff !important; }

.msg-ok { padding: 10px 16px; background: #f0f9eb; color: #67c23a; border-radius: 4px; margin-bottom: 12px; font-size: 13px; }
.msg-err { padding: 10px 16px; background: #fef0f0; color: #f56c6c; border-radius: 4px; margin-bottom: 12px; font-size: 13px; }

.order-table { width: 100%; background: #fff; border-radius: 8px; border-collapse: collapse; }
.order-table th, .order-table td { padding: 12px 14px; border-bottom: 1px solid #ebeef5; text-align: left; font-size: 14px; }
.order-table th { background: #f5f7fa; color: #606266; font-weight: 600; }
.order-id { font-family: monospace; font-size: 13px; }

.tag-warn { color: #e6a23c; }
.tag-info { color: #909399; }
.tag-primary { color: #409eff; }
.tag-ok { color: #67c23a; }
.tag-cancel { color: #f56c6c; }

.detail-btn { padding: 4px 12px; background: #409eff; color: #fff; border: none; border-radius: 4px; cursor: pointer; margin-right: 6px; }
.deliver-action-btn { padding: 4px 12px; background: #67c23a; color: #fff; border: none; border-radius: 4px; cursor: pointer; margin-right: 6px; }
.cancel-action-btn { padding: 4px 12px; background: #e6a23c; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
.delete-action-btn { padding: 4px 12px; background: #f56c6c; color: #fff; border: none; border-radius: 4px; cursor: pointer; margin-left: 6px; }

.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: 100; }
.detail-dialog { width: 660px; max-height: 85vh; overflow: auto; background: #fff; border-radius: 8px; padding: 24px; }
.detail-dialog h3 { margin-bottom: 16px; }
.detail-body { font-size: 14px; }
.info-row { margin-bottom: 8px; color: #303133; }
.info-row span { color: #909399; display: inline-block; width: 80px; }
.item-table { width: 100%; border-collapse: collapse; margin-top: 8px; }
.item-table th, .item-table td { padding: 8px 10px; border: 1px solid #ebeef5; text-align: left; font-size: 13px; }
.item-table th { background: #f5f7fa; }
.dialog-btns { display: flex; justify-content: flex-end; margin-top: 20px; }
.cancel-btn { padding: 8px 20px; background: #f5f7fa; border: 1px solid #dcdfe6; border-radius: 4px; cursor: pointer; }
</style>
