<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h2>用户注册</h2>
          <p class="header-subtitle">创建新账户</p>
        </div>
      </template>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
        @keyup.enter="handleRegister"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            clearable
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
          <div class="password-tips">
            密码长度6-20位，必须包含字母和数字
          </div>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            clearable
          >
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleRegister"
            style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>

        <!-- 底部链接区域 -->
        <div class="form-footer">
          <el-link type="info" @click="goToLogin" :underline="false">
            <el-icon><ArrowLeft /></el-icon>
            返回登录
          </el-link>
          <div class="login-link">
            已有账号？
            <el-link type="primary" @click="goToLogin" :underline="false">
              立即登录
            </el-link>
          </div>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Lock,
  Message,
  ArrowLeft
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 表单引用
const registerFormRef = ref()

// 加载状态
const loading = ref(false)

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

// 密码强度验证函数
const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码长度必须在6到20个字符之间'))
  } else if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密码必须包含字母和数字'))
  } else {
    // 确认密码验证
    if (registerForm.confirmPassword && registerFormRef.value) {
      registerFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

// 确认密码验证函数
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 邮箱验证函数
const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入有效的邮箱地址'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { 
      pattern: /^[a-zA-Z0-9_]+$/,
      message: '用户名只能包含字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, validator: validateEmail, trigger: 'blur' }
  ]
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}

// 实际的注册逻辑
const performRegister = async () => {
  try {
    loading.value = true
    const response = await axios.post('/api/user/register', {
      username: registerForm.username,
      password: registerForm.password,
      email: registerForm.email,
    })

    if (response.data.code === '200') {
      ElMessage.success('注册成功！请登录')
      
      // 清空表单
      if (registerFormRef.value) {
        registerFormRef.value.resetFields()
      }
      
      // 跳转到登录页面
      router.push('/login')
    } else {
      ElMessage.error(response.data.msg || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    
    if (error.response) {
      if (error.response.data && error.response.data.msg) {
        ElMessage.error(error.response.data.msg)
      } else {
        ElMessage.error(`注册失败: ${error.response.status}`)
      }
    } else if (error.request) {
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 显示注册协议对话框
const showAgreement = () => {
  ElMessageBox.alert(
    `
    <div style="max-height: 400px; overflow-y: auto; padding-right: 10px;">
      <h3 style="margin-bottom: 15px; color: #333;">用户注册协议</h3>
      <p style="margin-bottom: 10px; color: #666;">
        欢迎您注册成为我们的用户！在注册前，请仔细阅读以下协议：
      </p>
      <ol style="color: #666; line-height: 1.6;">
        <li>您需要提供真实、准确、完整的个人信息。</li>
        <li>您需要妥善保管账户信息，不得将账户转让或出借给他人使用。</li>
        <li>您在使用服务时需遵守相关法律法规，不得从事任何违法活动。</li>
        <li>我们承诺保护您的个人信息安全，详情请参阅我们的隐私政策。</li>
        <li>本协议可能会不定期更新，请定期查看。</li>
      </ol>
      <p style="margin-top: 15px; color: #999; font-size: 12px;">
        点击"同意并注册"即表示您已阅读并同意以上协议。
      </p>
    </div>
    `,
    '用户协议',
    {
      dangerouslyUseHTMLString: true,
      showCancelButton: true,
      confirmButtonText: '同意并注册',
      cancelButtonText: '取消',
      beforeClose: async (action, instance, done) => {
        if (action === 'confirm') {
          instance.confirmButtonLoading = true
          instance.confirmButtonText = '注册中...'
          
          try {
            // 直接执行注册逻辑
            await performRegister()
            done()
          } catch (error) {
            console.error('注册失败:', error)
            // 注册失败时不关闭对话框
            instance.confirmButtonLoading = false
            instance.confirmButtonText = '同意并注册'
            return // 不调用 done()，保持对话框打开
          }
        } else {
          done()
        }
      }
    }
  ).then(() => {
    // 注册成功后的处理
    console.log('用户同意协议并注册')
  }).catch(() => {
    // 用户取消注册
    ElMessage.info('已取消注册')
  })
}

// 注册方法
const handleRegister = async () => {
  // 验证表单
  if (!registerFormRef.value) return
  
  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    // 显示注册协议对话框
    showAgreement()
  } catch (error) {
    console.error('表单验证错误:', error)
  }
}

// 在组件挂载时添加一些初始化逻辑
onMounted(() => {
  console.log('注册页面已加载')
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  padding: 20px;
}

.register-card {
  width: 500px;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.register-header {
  text-align: center;
  color: #333;
}

.register-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.header-subtitle {
  margin: 8px 0 0;
  font-size: 14px;
  color: #666;
}

/* 密码提示 */
.password-tips {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  line-height: 1.4;
}

/* 底部链接区域 */
.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.form-footer .el-link {
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.login-link {
  font-size: 14px;
  color: #666;
}

.login-link .el-link {
  margin-left: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-card {
    width: 90%;
    max-width: 400px;
  }
  
  .register-container {
    padding: 10px;
  }
}

@media (max-width: 480px) {
  .form-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  :deep(.el-form-item__label) {
    font-size: 14px;
  }
}
</style>