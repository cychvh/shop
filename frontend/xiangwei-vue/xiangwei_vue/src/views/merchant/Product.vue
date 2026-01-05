<template>
  <div style="padding: 20px">
    <!-- 查询与操作 -->
    <div style="margin-bottom: 20px; display: flex; gap: 10px;">
      <el-input
        v-model="search"
        placeholder="请输入商品名称搜索"
        style="width: 200px"
        clearable
        @clear="load"
        @keyup.enter="load"
      />
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="handleAdd">新增商品</el-button>
    </div>

    <!-- 商品表格 -->
    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column label="商品图片" width="100" align="center">
        <template #default="{ row }">
          <el-image
            style="width: 50px; height: 50px"
            :src="getImageUrl(row.imageurl)"
            :preview-src-list="[getImageUrl(row.imageurl)]"
            fit="cover"
          >
            <template #error>
              <div style="font-size: 12px; color: #999;">无图</div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="categoryname" label="分类" />
      <el-table-column prop="price" label="价格">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" />
      <el-table-column prop="originplace" label="产地" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button
            size="small"
            :type="row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-popconfirm title="确定删除吗？" @confirm="handleDelete(row)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 20px; text-align: right;">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        @current-change="load"
        @size-change="load"
      />
    </div>

    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.categoryname" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="产地"><el-input v-model="form.originplace" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            :auto-upload="false"
            :limit="1"
            :file-list="fileList"
            :on-change="handleFileChange"
          >
            <el-button type="primary">选择图片</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

const tableData = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const search = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const fileList = ref([])
const currentFile = ref(null)
const BASE_IMAGE_URL = 'http://localhost:9999'


const form = reactive({
  id: null,
  name: '',
  categoryname: '',
  price: 0,
  stock: 0,
  originplace: '',
  status: 1,
  imageurl: '',
  merchantId: null
})

// 图片路径处理
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return BASE_IMAGE_URL + url
}


// 新增
const handleAdd = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  isEdit.value = true
  dialogVisible.value = true
}

// 文件选择
const handleFileChange = (file) => {
  currentFile.value = file.raw
  fileList.value = [file]
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    categoryname: '',
    price: 0,
    stock: 0,
    originplace: '',
    status: 1,
    imageurl: '',
    merchantId: null
  })
  fileList.value = []
  currentFile.value = null
}

// 获取列表
const load = () => {
  loading.value = true
  service.get('/api/product/list', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      search: search.value
    }
  }).then(res => {
    tableData.value = res.data.records
    total.value = res.data.total
  }).finally(() => loading.value = false)
}

// 保存新增/编辑
const save = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.id) {
    ElMessage.error('未获取到用户信息，请重新登录')
    return
  }

  form.merchantId = userInfo.id

  const formData = new FormData()
  formData.append('product', new Blob([JSON.stringify(form)], { type: 'application/json' }))
  if (currentFile.value) formData.append('file', currentFile.value)

  const url = isEdit.value ? '/api/product' : '/api/product/add'
  const method = isEdit.value ? 'put' : 'post'

  service({ method, url, data: formData }).then(() => {
    ElMessage.success('操作成功')
    dialogVisible.value = false
    load()
  })
}

// 删除
const handleDelete = (row) => {
  service.delete('/api/product/deleProduct', { data: { id: row.id } }).then(() => {
    ElMessage.success('删除成功')
    load()
  })
}

// 上下架
const toggleStatus = (row) => {
  const temp = { ...row, status: row.status === 1 ? 0 : 1 }
  const formData = new FormData()
  formData.append('product', new Blob([JSON.stringify(temp)], { type: 'application/json' }))
  service.put('/api/product', formData).then(load)
}

onMounted(load)
</script>
