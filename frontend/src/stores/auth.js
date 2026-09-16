import { defineStore } from 'pinia'
import { login as apiLogin, getCurrentUser } from '../api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),
  getters: {
    displayName: (state) => state.user?.displayName || state.user?.nickname || state.user?.username || '用户',
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async login(username, password) {
      const data = await apiLogin(username, password)
      this.token = data.token
      this.user = data.userInfo || null
      localStorage.setItem('token', this.token)
      localStorage.setItem('user', JSON.stringify(this.user))
    },
    async fetchUser() {
      if (!this.token) return
      this.user = await getCurrentUser()
      localStorage.setItem('user', JSON.stringify(this.user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
