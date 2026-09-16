import { defineStore } from 'pinia'
import { createSession, getSessionMessages, getSessions, streamChat } from '../api'

export const useChatStore = defineStore('chat', {
  state: () => ({
    sessions: [],
    activeSessionId: null,
    messages: [],
    loading: false,
    historyLoading: false,
    historyError: '',
    streaming: false
  }),
  getters: {
    activeSession: (state) =>
      state.sessions.find((s) => s.sessionId === state.activeSessionId) || null
  },
  actions: {
    async createSession(title) {
      const session = await createSession(title)
      this.sessions.unshift(session)
      this.activeSessionId = session.sessionId
      this.messages = []
      this.historyLoading = false
      this.historyError = ''
      return session
    },
    async loadSessions() {
      this.loading = true
      this.historyError = ''
      try {
        this.sessions = await getSessions()
        if (!this.sessions.some((session) => session.sessionId === this.activeSessionId)) {
          await this.selectSession(this.sessions[0]?.sessionId ?? null)
        }
      } catch (error) {
        this.historyError = error.message || '会话历史加载失败'
      } finally {
        this.loading = false
      }
    },
    async selectSession(id) {
      this.activeSessionId = id
      this.messages = []
      this.historyError = ''
      if (!id) {
        this.historyLoading = false
        return
      }

      const requestedSessionId = id
      this.historyLoading = true
      try {
        const messages = await getSessionMessages(requestedSessionId)
        if (this.activeSessionId === requestedSessionId) {
          this.messages = messages
        }
      } catch (error) {
        if (this.activeSessionId === requestedSessionId) {
          this.historyError = error.message || '消息历史加载失败'
        }
      } finally {
        if (this.activeSessionId === requestedSessionId) {
          this.historyLoading = false
        }
      }
    },
    async sendMessage(text) {
      if (!text || !this.activeSessionId || this.streaming) return
      this.messages.push({ role: 'user', content: text })
      const assistant = { role: 'assistant', content: '' }
      this.messages.push(assistant)
      this.streaming = true

      const controller = streamChat(this.activeSessionId, text, {
        onMessage: (chunk) => {
          assistant.content += chunk
        },
        onError: () => {
          if (!assistant.content) assistant.content = 'AI 服务暂时不可用。'
          this.streaming = false
        },
        onDone: () => {
          this.streaming = false
        }
      })

      // 保留 controller 引用，未来可用于中断。
      this._controller = controller
      return controller
    },
    abort() {
      if (this._controller) {
        this._controller.abort()
        this._controller = null
      }
      this.streaming = false
    }
  }
})
