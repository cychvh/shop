<template>
  <div class="product-detail" v-loading="loading">
    <!-- 面包屑 -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item to="/user/home">首页</el-breadcrumb-item>
      <el-breadcrumb-item>商品详情</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="40" style="margin-top: 30px" v-if="product.id">
      <!-- 左侧图片 -->
      <el-col :span="10">
        <el-image
          :src="getImageUrl(product.imageurl)"
          fit="cover"
          style="width: 100%; height: 320px; border-radius: 8px"
        >
          <template #error>
            <div class="img-error">暂无图片</div>
          </template>
        </el-image>
      </el-col>

      <!-- 右侧信息 -->
      <el-col :span="14">
        <h1>{{ product.name }}</h1>
        <h2 style="color: red">¥ {{ product.price }}</h2>

        <el-descriptions :column="1" border style="margin-top: 20px">
          <el-descriptions-item label="分类">
            {{ product.categoryname }}
          </el-descriptions-item>
          <el-descriptions-item label="产地">
            {{ product.originplace }}
          </el-descriptions-item>
          <el-descriptions-item label="库存">
            {{ product.stock }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 数量 -->
        <div style="margin: 20px 0">
          <span style="margin-right: 10px">购买数量：</span>

          <!-- 关键：防止 min > max 报错 -->
          <el-input-number
            v-if="product.stock > 0"
            v-model="quantity"
            :min="1"
            :max="product.stock"
          />
          <span v-else style="color: #999">暂无库存</span>
        </div>

        <!-- 操作 -->
        <el-button
          type="primary"
          size="large"
          :disabled="product.stock <= 0"
          @click="addToCart"
        >
          加入购物车
        </el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const productId = route.params.id

const loading = ref(false)
const quantity = ref(1)

const product = ref({
  id: null,
  name: '',
  price: 0,
  stock: 0,
  categoryname: '',
  originplace: '',
  imageurl: ''
})

// 图片地址处理
const BASE_IMAGE_URL = 'http://localhost:9999'
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return BASE_IMAGE_URL + url
}

// 加载商品详情
const loadProduct = async () => {
  loading.value = true
  try {
    const res = await request.get(`/api/product/user/productOne/${productId}`)
    if (res.code === 200 || res.code === '200') {
      product.value = res.data
      // 防止 InputNumber 报错
      quantity.value = product.value.stock > 0 ? 1 : 0
    } else {
      ElMessage.error(res.msg || '商品不存在')
    }
  } finally {
    loading.value = false
  }
}

// 加入购物车
const addToCart = async () => {
  if (quantity.value <= 0) {
    ElMessage.warning('请选择正确的数量')
    return
  }

  await request.post('/api/cart/addcart', {
    productid: product.value.id,
    quantity: quantity.value
  })

  ElMessage.success({
    message: `商品已加入购物车`,
    duration: 2000,
    showClose: true
  })
}

onMounted(loadProduct)
</script>

<style scoped>
.product-detail {
  padding: 30px;
}

.img-error {
  width: 100%;
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #999;
}
</style>
