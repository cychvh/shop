<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>乡味农产品电商系统</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-button type="primary" class="w-100" @click="handleLogin" :loading="loading">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const form = ref({ username: '', password: '' })
const router = useRouter()
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await request.post('/api/user/login', form.value)

    // 后端固定返回格式
    const { code, msg, data } = res
    if (code !== '200') {
      ElMessage.error(msg || '登录失败')
      return
    }

    const { token, user } = data
    if (!token || !user) {
      ElMessage.error('返回数据不完整')
      return
    }

    // 1. 存 token & 用户信息
    localStorage.setItem('token', token)
    localStorage.setItem('userInfo', JSON.stringify(user))
    localStorage.setItem('isLogin', 'true')

    // 2. 根据 type 映射角色
    let role = ''
    let redirectPath = ''

    switch (user.type) {
      case 0:
        role = 'ADMIN'
        redirectPath = '/admin/home'
        break
      case 1:
        role = 'MERCHANT'
        redirectPath = '/merchant/home'
        break
      case 2:
        role = 'USER'
        redirectPath = '/user/home'
        break
      default:
        ElMessage.error('未知用户类型')
        return
    }

    // 3. 存 role
    localStorage.setItem('role', role)

    ElMessage.success('登录成功')
    router.push(redirectPath)
  } catch (e) {
    console.error('登录错误', e)
    ElMessage.error(e?.response?.data?.msg || e?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
.login-container { display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #2d3a4b; }
.login-card { width: 400px; padding: 20px; }
.w-100 { width: 100%; }
h2 { text-align: center; margin-bottom: 30px; }
</style>