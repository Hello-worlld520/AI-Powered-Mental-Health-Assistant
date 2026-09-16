<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const user = computed(() => auth.user || {})

const rows = computed(() => [
  { label: '用户名', value: user.value.username },
  { label: '邮箱', value: user.value.email },
  { label: '昵称', value: user.value.nickname },
  { label: '手机号', value: user.value.phone },
  { label: '用户状态', value: user.value.statusDisplayName },
  { label: '注册时间', value: user.value.createdAt }
])

function back() {
  router.push('/')
}
</script>

<template>
  <div class="auth-view">
    <div class="profile-card">
      <div class="profile-head">
        <div class="profile-avatar">🌷</div>
        <h1 class="profile-name">{{ auth.displayName }}</h1>
        <button class="btn btn--ghost" @click="back">返回咨询</button>
      </div>

      <dl class="profile-list">
        <div v-for="row in rows" :key="row.label" class="profile-row">
          <dt class="profile-label">{{ row.label }}</dt>
          <dd class="profile-value">{{ row.value || '—' }}</dd>
        </div>
      </dl>
    </div>
  </div>
</template>
