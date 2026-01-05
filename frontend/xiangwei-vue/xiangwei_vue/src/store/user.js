import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  // 只负责保存数据，不负责发送请求
  const setUser = (newToken, newUserInfo) => {
    token.value = newToken
    userInfo.value = newUserInfo
    localStorage.setItem('token', newToken)
    localStorage.setItem('userInfo', JSON.stringify(newUserInfo))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.clear()
    window.location.href = '/login'
  }

  return { token, userInfo, setUser, logout }
})