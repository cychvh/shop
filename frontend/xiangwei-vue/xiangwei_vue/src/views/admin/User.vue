<template>
  <div>
    <!-- 搜索 -->
    <el-input
      v-model="search"
      placeholder="请输入用户名关键字"
      style="width: 250px; margin-bottom: 10px"
      clearable
    />
    <el-button type="primary" @click="getUsers">搜索用户/商家</el-button>

    <el-tabs type="card" style="margin-top: 15px">
      <!-- 用户列表 -->
      <el-tab-pane label="用户列表">
        <el-table :data="users" border style="width: 100%">
          <el-table-column prop="username" label="用户名" />
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="deleteUser(row.id)">删除</el-button>
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
      </el-tab-pane>

      <!-- 商家列表 -->
      <el-tab-pane label="商家列表">
        <el-table :data="merchants" border style="width: 100%">
          <el-table-column prop="shopName" label="店铺名" />
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="deleteUser(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="编辑用户/商家" width="400px">
      <el-form :model="form" label-width="80px">
        <!-- 用户名 -->
        <el-form-item label="用户名" v-if="form.type === 1">
          <el-input v-model="form.username" />
        </el-form-item>
        <!-- 店铺名 -->
        <el-form-item label="店铺名" v-if="form.type === 2">
          <el-input v-model="form.shopName" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const merchants = ref([])
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const search = ref('')

const dialogVisible = ref(false)
const form = ref({})

// 获取用户和商家列表
const getUsers = async () => {
  try {
    const res = await axios.get('/api/user/getuser', {
      params: { pageNum: pageNum.value, size: pageSize.value, search: search.value }
    })
    if (res.code === '200') {
      // type === 1 为用户, type === 2 为商家
      users.value = res.data.records.filter(u => u.type === 1)
      merchants.value = res.data.records
        .filter(u => u.type === 2)
        .map(u => ({
          ...u,
          shopName: u.shopName || u.username // 如果后端没有 shopName，用 username 映射
        }))
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg || '获取列表失败')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('获取列表失败')
  }
}

// 分页
const handlePageChange = (page) => {
  pageNum.value = page
  getUsers()
}

// 删除
const deleteUser = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除吗？', '提示', { type: 'warning' })
    const res = await axios.delete(`/api/user/${id}`)
    if (res.code === '200') {
      ElMessage.success('删除成功')
      getUsers()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch {}
}

// 打开编辑弹窗
const openEditDialog = (row) => {
  form.value = { ...row } // 克隆数据
  dialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    const res = await axios.put('/api/user/updateUser', form.value)
    if (res.code === '200') {
      ElMessage.success('修改成功')
      dialogVisible.value = false
      getUsers()
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('修改失败')
  }
}

onMounted(() => getUsers())
</script>
