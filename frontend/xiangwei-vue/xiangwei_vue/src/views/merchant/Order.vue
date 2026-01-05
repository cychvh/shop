<template>
  <div class="order-container">
    <el-card class="box-card">
      <!-- 头部 -->
      <template #header>
        <div class="card-header">
          <span><el-icon><List /></el-icon> 商家订单管理</span>
        </div>
      </template>

      <!-- 订单表格 -->
      <el-table 
        :data="orderList" 
        style="width: 100%" 
        v-loading="loading"
        stripe
      >
        <el-table-column prop="id" label="订单编号" min-width="120" />
        <el-table-column prop="username" label="下单用户" min-width="100" />
        <el-table-column prop="phone" label="联系方式" min-width="120" />
        <el-table-column prop="address" label="收货地址" min-width="180" />
        <el-table-column prop="totalAmount" label="实付金额" width="120">
          <template #default="scope">
            <span class="total-amount">￥{{ scope.row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="showDetail(scope.row)">
              查看详细
            </el-button>
            <el-button 
              type="success" 
              link 
              v-if="scope.row.status === 1" 
              @click="openShipDialog(scope.row)"
            >
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          layout="total, prev, pager, next"
          :total="total"
          @current-change="fetchOrderList"
        />
      </div>
    </el-card>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详细信息"
      width="600px"
      destroy-on-close
    >
      <div v-if="currentOrder" class="order-base-info">
        <p><strong>订单单号：</strong>{{ currentOrder.id }}</p>
        <p><strong>下单用户：</strong>{{ currentOrder.username }}</p>
        <p><strong>联系方式：</strong>{{ currentOrder.phone }}</p>
        <p><strong>收货地址：</strong>{{ currentOrder.address }}</p>
        <p><strong>实付总额：</strong><span class="red">￥{{ currentOrder.totalAmount }}</span></p>
        <p><strong>订单状态：</strong>{{ getStatusText(currentOrder.status) }}</p>
      </div>
      
      <el-table :data="orderItems" border style="margin-top: 15px" v-loading="detailLoading">
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">￥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="小计" width="100">
          <template #default="scope">
            <span class="bold">￥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog
      v-model="shipDialogVisible"
      title="填写发货信息"
      width="400px"
      :before-close="() => { shipDialogVisible = false }"
    >
      <el-form :model="shipForm" ref="shipFormRef" label-width="100px">
        <el-form-item label="快递公司" prop="expressCompany" :rules="[{ required: true, message: '请输入快递公司', trigger: 'blur' }]">
          <el-input v-model="shipForm.expressCompany" placeholder="例如：顺丰" />
        </el-form-item>
        <el-form-item label="快递单号" prop="expressNo" :rules="[{ required: true, message: '请输入快递单号', trigger: 'blur' }]">
          <el-input v-model="shipForm.expressNo" placeholder="例如：SF123456789" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { List } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, ElForm } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const orderList = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

// 弹窗逻辑
const detailVisible = ref(false)
const detailLoading = ref(false)
const orderItems = ref([])
const currentOrder = ref(null)

const shipDialogVisible = ref(false)
const shipForm = reactive({
  orderId: null,
  expressCompany: '',
  expressNo: ''
})
const shipFormRef = ref(null)

// 获取商家订单列表
const fetchOrderList = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/order/MerchantList', {
      params: {
        pageNum: queryParams.pageNum,
        pageSize: queryParams.pageSize
      }
    })
    if (res.code === 200 || res.code === '200') {
      orderList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('商家订单列表加载失败')
  } finally {
    loading.value = false
  }
}

// 显示订单详情
const showDetail = async (row) => {
  currentOrder.value = row
  detailVisible.value = true
  detailLoading.value = true
  orderItems.value = []

  try {
    const res = await request.get('/api/order/merchantOrderItems', {
      params: { orderId: row.id }
    })
    if (res.code === 200 || res.code === '200') {
      orderItems.value = res.data
    } else {
      ElMessage.error(res.msg || '获取订单详情失败')
    }
  } catch (error) {
    ElMessage.error('服务异常')
  } finally {
    detailLoading.value = false
  }
}

// 打开发货弹窗
const openShipDialog = (row) => {
  shipForm.orderId = row.id
  shipForm.expressCompany = ''
  shipForm.expressNo = ''
  shipDialogVisible.value = true
}

// 提交发货
const submitShip = async () => {
  const form = shipFormRef.value
  await form.validate(async (valid) => {
    if (!valid) return
    try {
      const res = await request.post('/api/delivery/ship', shipForm)
      if (res.code === 200 || res.code === '200') {
        ElMessage.success('发货成功')
        shipDialogVisible.value = false
        fetchOrderList()
      } else {
        ElMessage.error(res.msg || '发货失败')
      }
    } catch (error) {
      ElMessage.error('服务异常')
    }
  })
}

// 状态文字
const getStatusText = (s) => {
  return { 1: '已支付', 2: '已发货', 3: '已完成', 0: '已取消' }[s] || '未知状态'
}

// 状态颜色
const getStatusTag = (s) => {
  return { 1: 'warning', 2: 'primary', 3: 'success', 0: 'info' }[s] || ''
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  if (Array.isArray(time)) {
    return `${time[0]}-${String(time[1]).padStart(2,'0')}-${String(time[2]).padStart(2,'0')} ${String(time[3]).padStart(2,'0')}:${String(time[4]).padStart(2,'0')}`
  }
  return time.replace('T', ' ')
}

onMounted(fetchOrderList)
</script>

<style scoped>
.order-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}
.box-card {
  max-width: 1000px;
  margin: 0 auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.total-amount {
  color: #ff4d4f;
  font-weight: bold;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.order-base-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #606266;
}
.red { color: #ff4d4f; font-weight: bold; }
.bold { font-weight: bold; }
</style>
