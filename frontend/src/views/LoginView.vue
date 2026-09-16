<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await auth.login(username.value.trim(), password.value)
    router.push(route.query.redirect || '/')
  } catch (e) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-view">
    <div class="auth-card">
      <div class="auth-brand">
        <div class="auth-mark">🌷</div>
        <h1 class="auth-title">暖心陪伴</h1>
        <p class="auth-subtitle">你的 AI 心理健康助手</p>
      </div>

      <form class="auth-form" @submit.prevent="submit">
        <label class="field">
          <span class="field__label">用户名或邮箱</span>
          <input
            v-model="username"
            class="field__input"
            type="text"
            placeholder="请输入用户名或邮箱"
            autocomplete="username"
            required
          />
        </label>

        <label class="field">
          <span class="field__label">密码</span>
          <input
            v-model="password"
            class="field__input"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
            required
          />
        </label>

        <p v-if="error" class="form-error" role="alert">{{ error }}</p>

        <button class="btn btn--primary btn--block" type="submit" :disabled="loading">
          {{ loading ? '登录中…' : '进入咨询' }}
        </button>
      </form>

      <p class="auth-hint">AI 回复仅供参考，不能替代专业医疗建议。</p>
    </div>
  </div>
</template>
