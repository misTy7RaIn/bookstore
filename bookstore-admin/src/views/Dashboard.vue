<template>
  <!-- 仪表盘：数据统计页 -->
  <div class="dashboard">
    <h2>数据概览</h2>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card" v-for="card in statCards" :key="card.title">
        <div class="card-icon" :style="{ background: card.color }">{{ card.icon }}</div>
        <div class="card-info">
          <p class="card-title">{{ card.title }}</p>
          <p class="card-value">{{ card.value }}</p>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts">
      <div class="chart-box">
        <h3>分类图书数量</h3>
        <div ref="pieChart" class="chart"></div>
      </div>
      <div class="chart-box">
        <h3>近7天订单趋势</h3>
        <div ref="lineChart" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

// 统计卡片数据
const statCards = reactive([
  { title: '图书总数', value: 0, icon: '📚', color: '#409eff' },
  { title: '订单总数', value: 0, icon: '📦', color: '#67c23a' },
  { title: '用户总数', value: 0, icon: '👤', color: '#e6a23c' },
  { title: '总销售额（元）', value: '0.00', icon: '💰', color: '#f56c6c' }
])

const pieChart = ref(null)
const lineChart = ref(null)

/**
 * 加载仪表盘数据
 */
async function loadStats() {
  const res = await request.get('/dashboard/stats')
  if (res.code === 200) {
    const d = res.data
    statCards[0].value = d.bookCount || 0
    statCards[1].value = d.orderCount || 0
    statCards[2].value = d.userCount || 0
    statCards[3].value = (d.totalSales || 0).toFixed(2)

    await nextTick()
    renderPieChart(d.categoryBookCount || [])
    renderLineChart(d.dailyOrderCount || [])
  }
}

/**
 * 分类饼图
 */
function renderPieChart(data) {
  if (!pieChart.value) return
  const chart = echarts.init(pieChart.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      label: { show: true, formatter: '{b}: {c}' },
      emphasis: { label: { fontSize: 16, fontWeight: 'bold' } },
      data: data.map(i => ({ name: i.name, value: i.value }))
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}

/**
 * 订单趋势折线图
 */
function renderLineChart(data) {
  if (!lineChart.value) return
  const chart = echarts.init(lineChart.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', boundaryGap: false, data: data.map(i => i.date) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'line',
      smooth: true,
      areaStyle: { color: 'rgba(64, 158, 255, 0.2)' },
      lineStyle: { color: '#409eff' },
      data: data.map(i => i.count)
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}

onMounted(() => { loadStats() })
</script>

<style scoped>
.dashboard { height: 100%; }
.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-top: 20px; }
.stat-card {
  display: flex; align-items: center; gap: 18px; padding: 28px 24px; background: #fff;
  border-radius: 14px; box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  transition: all 0.25s; cursor: default;
}
.stat-card:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0,0,0,0.1); }
.card-icon {
  width: 60px; height: 60px; border-radius: 16px; display: flex;
  align-items: center; justify-content: center; font-size: 30px;
}
.card-title { font-size: 13px; color: #909399; font-weight: 500; }
.card-value { font-size: 28px; font-weight: 800; color: #2c3e50; margin-top: 6px; }
.charts { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 24px; }
.chart-box { background: #fff; border-radius: 14px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.chart-box h3 { font-size: 15px; color: #2c3e50; margin-bottom: 16px; font-weight: 600; }
.chart { width: 100%; height: 340px; }
</style>
