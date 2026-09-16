<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useChatStore } from '../stores/chat'

const router = useRouter()
const auth = useAuthStore()
const chat = useChatStore()

const input = ref('')
const listEl = ref(null)
const creating = ref(false)

const activeTitle = computed(() => chat.activeSession?.sessionTitle || '新会话')

async function scrollToBottom() {
  await nextTick()
  if (listEl.value) {
    listEl.value.scrollTop = listEl.value.scrollHeight
  }
}

async function startNewSession() {
  creating.value = true
  try {
    await chat.createSession('')
    input.value = ''
    await scrollToBottom()
  } finally {
    creating.value = false
  }
}

async function send() {
  const text = input.value.trim()
  if (!text) return
  if (!chat.activeSessionId) {
    await chat.createSession(text.slice(0, 20))
  }
  input.value = ''
  await chat.sendMessage(text)
  await scrollToBottom()
}

async function selectSession(id) {
  await chat.selectSession(id)
  await scrollToBottom()
}

function goProfile() {
  router.push('/profile')
}

function logout() {
  auth.logout()
  router.push('/login')
}

onMounted(async () => {
  try {
    await auth.fetchUser()
  } catch {
    await router.replace({ name: 'login', query: { redirect: '/' } })
    return
  }
  await chat.loadSessions()
  await scrollToBottom()
})
</script>

<template>
  <div class="app-layout">
    <aside class="sidebar">
      <div class="sidebar__head">
        <div class="brand-row">
          <div class="auth-mark auth-mark--sm">🌷</div>
          <div>
            <h2 class="brand-title">暖心陪伴</h2>
            <p class="brand-subtitle">心理健康助手</p>
          </div>
        </div>
      </div>

      <button class="btn btn--primary btn--block" :disabled="creating" @click="startNewSession">
        {{ creating ? '创建中…' : '＋ 开启新会话' }}
      </button>

      <div class="sidebar__section">我的会话</div>
      <ul v-if="chat.sessions.length" class="session-list">
        <li
          v-for="session in chat.sessions"
          :key="session.sessionId"
          class="session-item"
          :class="{ 'session-item--active': session.sessionId === chat.activeSessionId }"
          @click="selectSession(session.sessionId)"
        >
          {{ session.sessionTitle }}
        </li>
      </ul>
      <p v-else class="session-empty">还没有会话</p>

      <div class="sidebar__footer">
        <div class="user-card" @click="goProfile">
          <div class="user-avatar">🌷</div>
          <div class="user-meta">
            <div class="user-name">{{ auth.displayName }}</div>
            <div class="user-status">{{ auth.user?.statusDisplayName || '正常' }}</div>
          </div>
        </div>
        <button class="btn btn--ghost btn--block" @click="logout">退出登录</button>
      </div>
    </aside>

    <main class="chat">
      <header class="chat__header">
        <div>
          <h3 class="chat__title">{{ activeTitle }}</h3>
          <span class="chat__subtitle">在这里，你可以放心地聊聊。</span>
        </div>
        <span class="badge">AI 咨询中</span>
      </header>

      <div ref="listEl" class="message-list">
        <p v-if="chat.historyError" class="form-error" role="alert">{{ chat.historyError }}</p>
        <div v-if="chat.historyLoading" class="empty-state">
          <p>正在加载历史消息…</p>
        </div>
        <div v-else-if="!chat.messages.length" class="empty-state">
          <div class="empty-emoji">🌼</div>
          <p>开始一次温暖的对话吧</p>
        </div>
        <div
          v-for="(message, index) in chat.messages"
          :key="index"
          class="message"
          :class="message.role === 'user' ? 'message--user' : 'message--ai'"
        >
          <div class="message__bubble">{{ message.content }}</div>
        </div>
        <div v-if="chat.streaming" class="message message--ai">
          <div class="message__bubble message__bubble--typing">正在思考…</div>
        </div>
      </div>

      <footer class="composer">
        <div class="composer__box">
          <textarea
            v-model="input"
            class="composer__input"
            rows="1"
            placeholder="说说你此刻的感受…"
            @keydown.enter.exact.prevent="send"
          ></textarea>
          <button class="btn btn--primary" :disabled="chat.streaming" @click="send">发送</button>
        </div>
        <p class="composer__hint">AI 回复仅供参考，不能替代专业医疗建议。</p>
      </footer>
    </main>
  </div>
</template>
