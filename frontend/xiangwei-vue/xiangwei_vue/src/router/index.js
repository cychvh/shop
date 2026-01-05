import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

// 布局组件
import UserLayout from '@/layouts/UserLayout.vue'
import MerchantLayout from '@/layouts/MerchantLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'


const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue')
  },
  // --- 用户路由 ---
  {
    path: '/user',
    component: UserLayout,
    meta: { role: 'USER' },
    children: [
      { path: 'home', component: () => import('@/views/user/Home.vue') },
      { path: 'product/:id', component: () => import('@/views/user/ProductDetail.vue') },
      { path: 'cart', component: () => import('@/views/user/Cart.vue') },
      { path: 'order', component: () => import('@/views/user/Order.vue') },
      { path: 'profile', component: () => import('@/views/user/Profile.vue') },
      { path: 'notice', component: () => import('@/views/user/Notice.vue') },
      { path: 'all-products', component: () => import('@/views/user/ProductList.vue') }
    ]
  },
  // --- 商家路由 ---
  {
    path: '/merchant',
    component: MerchantLayout,
    meta: { role: 'MERCHANT' },
    children: [
      { path: 'home', component: () => import('@/views/merchant/Home.vue') },
      { path: 'stat', component: () => import('@/views/merchant/Stat.vue') },
      { path: 'product', component: () => import('@/views/merchant/Product.vue') },
      { path: 'order', component: () => import('@/views/merchant/Order.vue') },
      { path: 'profile', component: () => import('@/views/merchant/Profile.vue') },
      { path: 'delivery', component: () => import('@/views/merchant/Delivery.vue') },
      { path: 'notice', component: () => import('@/views/merchant/Notice.vue') }
    ]
  },
  // --- 管理员路由 ---
  {
    path: '/admin',
    component: AdminLayout,
    meta: { role: 'ADMIN' },
    children: [
      { path: 'home', component: () => import('@/views/admin/Home.vue') },
      { path: 'user', component: () => import('@/views/admin/User.vue') },
      { path: 'notice', component: () => import('@/views/admin/Notice.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 1. 未登录，只能去登录页
  if (!token) {
    if (to.path === '/login') {
      return next()
    } else {
      return next('/login')
    }
  }

  // 2. 已登录，禁止再访问登录页
  if (to.path === '/login') {
    if (role === 'ADMIN') return next('/admin/home')
    if (role === 'MERCHANT') return next('/merchant/home')
    if (role === 'USER') return next('/user/home')
    return next()
  }

  // 3. 有角色要求，且不匹配
  if (to.meta.role && to.meta.role !== role) {
    if (role === 'ADMIN') return next('/admin/home')
    if (role === 'MERCHANT') return next('/merchant/home')
    if (role === 'USER') return next('/user/home')
    return next('/login')
  }

  // 4. 放行
  next()
})



export default router