<template>
  <div class="order-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>
            <el-icon><List /></el-icon>
            我的订单
          </span>

          <el-input
            v-model="searchName"
            placeholder="按商品名称搜索"
            clearable
            style="width: 260px"
            @clear="fetchOrderList"
          >
            <template #append>
              <el-button :icon="Search" @click="fetchOrderList" />
            </template>
          </el-input>
        </div>
      </template>

      <!-- 订单表格 -->
      <el-table :data="orderList" v-loading="loading" stripe>
        <el-table-column prop="id" label="订单编号" width="140" />

        <el-table-column label="实付金额" width="120">
          <template #default="scope">
            <span class="money">￥{{ scope.row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="下单时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="showDetail(scope.row)">
              详情
            </el-button>

            <!-- 已发货：查询物流 -->
            <el-button
              v-if="scope.row.status === 2"
              link
              type="info"
              @click="handleQueryDelivery(scope.row)"
            >
              查询快递
            </el-button>

            <!-- 已发货：确认收货 -->
            <el-button
              v-if="scope.row.status === 2"
              link
              type="success"
              @click="handleConfirm(scope.row)"
            >
              确认收货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          layout="total, prev, pager, next"
          :total="total"
          @current-change="fetchOrderList"
        />
      </div>
    </el-card>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-table
        :data="orderItems"
        border
        v-loading="detailLoading"
      >
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="price" label="单价" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="小计" width="120">
          <template #default="scope">
            ￥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 物流信息弹窗 -->
    <el-dialog v-model="deliveryVisible" title="物流信息" width="420px">
      <div v-if="delivery">
        <p>快递公司：{{ delivery.expressCompany }}</p>
        <p>快递单号：{{ delivery.expressNo }}</p>
        <p>发货时间：{{ formatTime(delivery.shipTime) }}</p>
        <p>
          物流状态：
          <el-tag :type="deliveryStatusTag(delivery.status)">
            {{ deliveryStatusText(delivery.status) }}
          </el-tag>
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, List } from '@element-plus/icons-vue'
import request from '@/utils/request'

/* ===== 列表 ===== */
const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const searchName = ref('')
const query = reactive({ pageNum: 1, pageSize: 10 })

/* ===== 订单详情 ===== */
const detailVisible = ref(false)
const detailLoading = ref(false)
const orderItems = ref([])

/* ===== 物流 ===== */
const deliveryVisible = ref(false)
const delivery = ref(null)

/* 获取订单列表 */
const fetchOrderList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/order/userList', {
      params: {
        pageNum: query.pageNum,
        pageSize: query.pageSize,
        productName: searchName.value
      }
    })
    if (res.code === 200 || res.code === '200') {
      orderList.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

/* 查看订单详情 */
const showDetail = async (row) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    const res = await request.get('/api/order/getOrderItem', {
      params: { orderId: row.id }
    })
    if (res.code === 200 || res.code === '200') {
      orderItems.value = res.data
    }
  } finally {
    detailLoading.value = false
  }
}

/* 查询物流 */
const handleQueryDelivery = async (row) => {
  const res = await request.get('/api/delivery/getOne', {
    params: { orderId: row.id }
  })
  if (res.code === 200 || res.code === '200') {
    delivery.value = res.data
    deliveryVisible.value = true
  }
}

/* 确认收货 */
const handleConfirm = async (row) => {
  await ElMessageBox.confirm(
    '确认已收到商品？',
    '确认收货',
    { type: 'success' }
  )
  await request.put('/api/order/confirmReceipt', null, {
    params: { orderId: row.id }
  })
  ElMessage.success('确认收货成功')
  fetchOrderList()
}

/* ===== 状态映射 ===== */
const getStatusText = (s) =>
  ({ 1: '已支付', 2: '已发货', 3: '已完成', 0: '已取消' }[s])

const getStatusTag = (s) =>
  ({ 1: 'warning', 2: 'primary', 3: 'success', 0: 'info' }[s])

const deliveryStatusText = (s) =>
  ({ 1: '已发货', 2: '已签收', 3: '作废' }[s])

const deliveryStatusTag = (s) =>
  ({ 1: 'primary', 2: 'success', 3: 'info' }[s])

const formatTime = (t) => (t ? t.replace('T', ' ') : '')

onMounted(fetchOrderList)
</script>

<style scoped>
.order-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
.box-card {
  max-width: 1100px;
  margin: auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.money {
  color: #ff4d4f;
  font-weight: bold;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
