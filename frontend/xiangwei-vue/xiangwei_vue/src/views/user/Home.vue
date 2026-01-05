<template>
  <div class="shop-home">
    <!-- 搜索栏 -->
    <div class="search-header">
      <div class="search-wrapper">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索您感兴趣的农产品..."
          class="main-search"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 推荐轮播 -->
    <div class="section-container">
      <div class="recommend-title">
        <span class="title-line"></span>
        <span class="title-text">推荐特惠</span>
      </div>

      <el-carousel height="300px" style="border-radius: 16px; overflow: hidden;">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="banner-card" :style="{ background: item.bg }">
            <div class="banner-content">
              <span class="banner-tag">精选</span>
              <h2>{{ item.title }}</h2>
              <p>{{ item.sub }}</p>
              <el-button round class="banner-btn" @click="router.push('/user/all-products')">立即抢购</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 全部产品 -->
    <div class="section-container" style="margin-top: 50px;">
      <div class="section-title">
        <div class="left">
          <span class="main-text">全部产品</span>
          <span class="dot"></span>
          <span class="sub-text">大自然的馈赠 · 每一份都新鲜</span>
        </div>
      </div>

      <el-row :gutter="20" v-loading="loading">
        <el-col
          :xs="12" :sm="8" :md="6" :lg="4.8"
          v-for="item in productList"
          :key="item.id"
        >
          <div class="product-item" @click="goDetail(item.id)">
            <div class="product-img-box">
              <el-image
                :src="getFullImgUrl(item.imageurl || item.img)"
                fit="cover"
                class="img"
                :preview-src-list="[getFullImgUrl(item.imageurl || item.img)]"
              >
                <template #error>
                  <div class="img-placeholder">
                    <el-icon :size="30"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div v-if="item.salesVolume" class="sales-tag">销量 {{ item.salesVolume }}</div>
            </div>

            <div class="product-info">
              <h4 class="product-name">{{ item.productName || item.name }}</h4>
              <div class="product-footer">
                <div class="price-box">
                  <span class="symbol">¥</span>
                  <span class="price">{{ item.price }}</span>
                </div>
                <el-icon class="add-cart-icon"><CirclePlusFilled /></el-icon>
              </div>
            </div>
          </div>
        </el-col>

        <el-empty
          v-if="productList.length === 0 && !loading"
          description="没有找到相关产品"
          style="width: 100%"
        />
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Picture, CirclePlusFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const productList = ref([])
const searchKeyword = ref('')

// 后端图片基础地址
const IMG_BASE_URL = 'http://localhost:9999'

const banners = [
  { title: '新鲜农场直供', sub: '源头直采 · 绿色健康', bg: 'linear-gradient(135deg, #2ecc71, #16a085)' },
  { title: '特惠水果季', sub: '当季时令水果，满50立减10元', bg: 'linear-gradient(135deg, #f1c40f, #e67e22)' },
  { title: '有机蔬菜礼盒', sub: '送礼自用两相宜，品质保障', bg: 'linear-gradient(135deg, #3498db, #2980b9)' }
]

// 构造完整图片URL
const getFullImgUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${IMG_BASE_URL}${url.startsWith('/') ? '' : '/'}${url}`
}

// 获取产品列表
const getAllProducts = async (name = '') => {
  loading.value = true
  try {
    const res = await request.get('/api/product/user/list', {
      params: { productName: name }
    })
    if (res.code === 200 || res.code === '200') {
      // 支持数组或分页格式
      productList.value = res.data.records ? res.data.records : res.data
    } else {
      ElMessage.error(res.msg || '获取产品失败')
    }
  } catch (e) {
    console.error('获取产品异常:', e)
    ElMessage.error('无法加载产品列表')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  getAllProducts(searchKeyword.value)
}

const goDetail = (id) => {
  router.push(`/user/product/${id}`)
}

onMounted(() => {
  getAllProducts()
})
</script>

<style scoped>
.shop-home {
  max-width: 1260px;
  margin: 0 auto;
  padding: 0 20px 30px;
  background-color: #fff;
}

.search-header {
  padding: 40px 0;
  display: flex;
  justify-content: center;
}
.search-wrapper { width: 100%; max-width: 700px; }

:deep(.main-search .el-input__wrapper) {
  border-radius: 30px 0 0 30px;
  padding-left: 20px;
  height: 50px;
  font-size: 16px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
}
:deep(.main-search .el-input-group__append) {
  border-radius: 0 30px 30px 0;
  background-color: #27ae60;
  color: white;
  border: none;
  width: 100px;
}
:deep(.main-search .el-input-group__append button) {
  color: white;
  font-weight: bold;
}

.section-container { margin-bottom: 20px; }
.recommend-title, .section-title { display: flex; align-items: center; margin-bottom: 20px; }
.title-line { width: 5px; height: 22px; background-color: #27ae60; border-radius: 10px; margin-right: 12px; }
.main-text { font-size: 24px; font-weight: bold; color: #333; }
.dot { width: 8px; height: 8px; background: #27ae60; border-radius: 50%; margin: 0 12px; }
.sub-text { color: #7f8c8d; font-size: 15px; }

.banner-card { height: 100%; display: flex; align-items: center; padding: 0 80px; color: white; }
.banner-content h2 { font-size: 40px; margin: 15px 0; }
.banner-btn { border: none; color: #27ae60; font-weight: bold; }

.product-item {
  cursor: pointer; margin-bottom: 30px; transition: 0.3s;
}
.product-item:hover { transform: translateY(-4px); box-shadow: 0 8px 20px rgba(0,0,0,0.08); }

.product-img-box {
  width: 100%;
  aspect-ratio: 1/1;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  background: #f9f9f9;
}
.img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.product-item:hover .img { transform: scale(1.05); }

.sales-tag {
  position: absolute;
  bottom: 10px; right: 10px;
  background: rgba(0,0,0,0.5); color: white; padding: 2px 10px;
  border-radius: 10px; font-size: 12px;
}

.product-info { padding: 15px 5px; }
.product-name { font-size: 16px; font-weight: 600; margin-bottom: 8px; }
.price-box { font-size: 18px; font-weight: bold; color: #e74c3c; display: flex; align-items: center; gap: 2px; }
.add-cart-icon { font-size: 24px; color: #27ae60; opacity: 0.3; transition: 0.3s; }
.product-item:hover .add-cart-icon { opacity: 1; transform: scale(1.2); }

.img-placeholder { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; background: #f5f7fa; color: #dadce0; }
</style>
