import request from '@/utils/request'

// --- 公共接口 ---
export const login = (data) => {
  return request({ url: '/user/login', method: 'post', data })
}

// --- 用户端接口 (User) ---
export const getUserProducts = () => request({ url: '/user/product/list', method: 'get' })
export const getProductDetail = (id) => request({ url: `/user/product/${id}`, method: 'get' })
export const getUserOrders = () => request({ url: '/user/order/list', method: 'get' })
export const getUserProfile = () => request({ url: '/user/profile', method: 'get' })
export const getUserNotices = () => request({ url: '/user/notice/list', method: 'get' })

// --- 商家端接口 (Merchant) ---
export const getMerchantStats = () => request({ url: '/merchant/stat', method: 'get' })
export const getMerchantProducts = () => request({ url: '/merchant/product/list', method: 'get' })
export const getMerchantProfile = () => request({ url: '/merchant/profile', method: 'get' })
export const getMerchantNotices = () => request({ url: '/merchant/notice/list', method: 'get' })

// --- 管理员接口 (Admin) ---
export const getAdminUsers = () => request({ url: '/admin/user/list', method: 'get' })
export const getAdminNotices = () => request({ url: '/admin/notice/list', method: 'get' })

src/store/user.js
Pinia 状态管理：登录、登出、角色存储。
import { defineStore } from 'pinia'
import { login } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    role: localStorage.getItem('role') || ''
  }),
  actions: {
    async loginAction(loginForm) {
      try {
        const res = await login(loginForm)
        const { token, role } = res.data
        this.token = token
        this.role = role
        localStorage.setItem('token', token)
        localStorage.setItem('role', role)
        return role // 返回角色以便组件跳转
      } catch (error) {
        throw error
      }
    },
    logout() {
      this.token = ''
      this.role = ''
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      location.reload() // 简单粗暴刷新页面清除状态
    }
  }
})