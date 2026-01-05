<template>
  <div class="stats-container">
    <el-row :gutter="20">
      <el-col :span="11">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span><el-icon><List /></el-icon> 近期销售明细</span>
            </div>
          </template>
          <el-table :data="dailyData" stripe border height="450" v-loading="loading">
            <el-table-column prop="date" label="销售日期" align="center" width="120" />
            <el-table-column prop="orderCount" label="订单数" align="center" />
            <el-table-column prop="totalAmount" label="销售额(¥)" align="center">
              <template #default="scope">
                <span style="color: #67c23a; font-weight: bold">
                  {{ scope.row.totalAmount.toFixed(2) }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="13">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span><el-icon><PieChart /></el-icon> 商品销量占比</span>
            </div>
          </template>
          <div ref="pieRef" class="chart-box"></div>
          
          <el-empty v-if="!hasData" description="暂无销量数据" :image-size="100" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { List, PieChart } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '@/utils/request' // 确保你的请求工具路径正确
import { ElMessage } from 'element-plus'

// 数据定义
const dailyData = ref([])
const loading = ref(false)
const hasData = ref(true)
const pieRef = ref(null)
let myChart = null

// 初始化饼图函数
const initChart = (data) => {
  if (!pieRef.value) return
  
  // 销毁旧实例，防止重复初始化
  if (myChart) {
    myChart.dispose()
  }
  
  myChart = echarts.init(pieRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b} : {c} 件 ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 'bottom'
    },
    series: [
      {
        name: '商品销量',
        type: 'pie',
        radius: ['40%', '70%'], // 环形图更美观
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}'
        },
        data: data // 对应后端返回的 pie 数组
      }
    ]
  }
  myChart.setOption(option)
  
  // 自适应窗口大小
  window.addEventListener('resize', () => myChart && myChart.resize())
}

// 获取后端数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/statistics/all')
    console.log('原始数据:', res) // 调试用

    // 处理数据剥离：针对你的后端 JSON 结构
    // 如果 request.js 已经剥离了第一层 data，则直接用 res.daily
    const finalData = res.data ? res.data : res
    
    if (finalData.daily) {
      dailyData.value = finalData.daily
    }

    if (finalData.pie && finalData.pie.length > 0) {
      hasData.value = true
      await nextTick() // 确保 DOM 已经渲染
      initChart(finalData.pie)
    } else {
      hasData.value = false
    }
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

// 生命周期挂载
onMounted(() => {
  loadData()
})

// 销毁监听，防止内存泄漏
onUnmounted(() => {
  window.removeEventListener('resize', () => myChart && myChart.resize())
  if (myChart) {
    myChart.dispose()
  }
})
</script>

<style scoped>
.stats-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 100px);
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}

.card-header .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.chart-box {
  width: 100%;
  height: 450px; /* 固定高度确保 ECharts 渲染 */
}

:deep(.el-card__header) {
  padding: 15px 20px;
  background-color: #fafafa;
}
</style>