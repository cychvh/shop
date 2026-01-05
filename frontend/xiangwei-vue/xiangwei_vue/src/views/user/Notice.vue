<template>
  <div class="merchant-notice">
    <el-card>
      <h2 style="margin-bottom: 15px">用户公告</h2>

      <el-table
        :data="noticeList"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column
          prop="id"
          label="ID"
          width="80"
          align="center"
        />

        <el-table-column
          prop="content"
          label="公告内容"
        />

        <el-table-column
          label="发布时间"
          width="200"
        >
          <template #default="scope">
            {{ formatDate(scope.row.date) }}
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 20px; text-align: right"
        background
        layout="prev, pager, next"
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script>
// ⚠️ 注意：这里用的是 request.js，不是 axios
import request from '@/utils/request'

export default {
  name: 'MerchantNotice',
  data() {
    return {
      noticeList: [],
      pageNum: 1,
      pageSize: 5,
      total: 0,
      loading: false,
      userInfo: null
    }
  },
  created() {
    const user = localStorage.getItem('userInfo')
    if (!user) {
      this.$message.error('未登录，请重新登录')
      return
    }
    this.userInfo = JSON.parse(user)
    this.loadNotice()
  },
  methods: {
    loadNotice() {
      this.loading = true
      request.get('/api/notice/getNotice', {
        params: {
          type: this.userInfo.type,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
        // ⚠️ 这里的 res 已经是 response.data（被拦截器处理过）
        this.noticeList = res.data.records || []
        this.total = res.data.total || 0
      }).catch(() => {
        // 错误提示已经在拦截器里统一处理了
      }).finally(() => {
        this.loading = false
      })
    },

    handlePageChange(page) {
      this.pageNum = page
      this.loadNotice()
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      return dateStr.replace('T', ' ')
    }
  }
}
</script>

<style scoped>
.merchant-notice {
  padding: 20px;
}
</style>
