<template>
  <div>
    <!-- 搜索 & 新增 -->
    <el-card style="margin-bottom: 15px;">
      <el-input
        v-model="search"
        placeholder="请输入公告内容关键字"
        style="width: 250px; margin-right: 10px"
        clearable
      />
      <el-button type="primary" @click="getNotices">搜索</el-button>
      <el-button type="success" style="margin-left: 10px" @click="openAddDialog">
        新增公告
      </el-button>
    </el-card>

    <!-- 表格 -->
    <el-table :data="notices" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="date" label="发布时间" width="180" />
      <el-table-column prop="content" label="公告内容" />
      <el-table-column label="发送对象" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.type === 'User'" type="success">用户</el-tag>
          <el-tag v-else-if="row.type === 'Merchant'" type="warning">商家</el-tag>
          <el-tag v-else type="info">全部</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteNotice(row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      background
      layout="prev, pager, next"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
      style="margin-top: 15px; text-align: right"
    />

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="公告内容">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>

        <el-form-item label="发送对象">
          <el-select v-model="form.type">
            <el-option label="全部" value="All" />
            <el-option label="用户" value="User" />
            <el-option label="商家" value="Merchant" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNotice">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

/* 表格 & 分页 */
const notices = ref([])
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const search = ref('')

/* 弹窗 */
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({
  id: null,
  content: '',
  type: 'User'
})

/* 获取公告 */
const getNotices = async () => {
  const res = await axios.get('/api/notice/list', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      search: search.value
    }
  })

  if (res.code === '200') {
    notices.value = res.data.records
    total.value = res.data.total
  }
}

/* 新增 */
const openAddDialog = () => {
  dialogTitle.value = '新增公告'
  form.value = {
    id: null,
    content: '',
    type: 'User'
  }
  dialogVisible.value = true
}

/* 编辑 */
const openEditDialog = (row) => {
  dialogTitle.value = '编辑公告'
  form.value = { ...row }
  dialogVisible.value = true
}

/* 提交 */
const submitNotice = async () => {
  if (!form.value.content) {
    ElMessage.warning('公告内容不能为空')
    return
  }

  const res = await axios({
    url: form.value.id ? '/api/notice/updateNotice' : '/api/notice/addNotice',
    method: form.value.id ? 'put' : 'post',
    data: form.value
  })

  if (res.code === '200') {
    ElMessage.success('操作成功')
    dialogVisible.value = false
    getNotices()
  }
}

/* 删除 */
const deleteNotice = (id) => {
  ElMessageBox.confirm('确认删除该公告吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await axios.delete('/api/notice/deleteNotice', {
      params: { id }
    })

    if (res.code === '200') {
      ElMessage.success('删除成功')
      getNotices()
    }
  })
}

/* 分页 */
const handlePageChange = (page) => {
  pageNum.value = page
  getNotices()
}

onMounted(() => {
  getNotices()
})
</script>
