const BASE_URL = ''

function getToken() {
  return localStorage.getItem('token') || ''
}

async function request(path, options = {}) {
  const headers = { ...(options.headers || {}) }
  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json'
  }
  const token = getToken()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers
  })

  const contentType = response.headers.get('content-type') || ''
  if (!contentType.includes('application/json')) {
    if (!response.ok) {
      throw new Error(`请求失败（${response.status}）`)
    }
    return null
  }

  const payload = await response.json()
  if (payload.code !== '200') {
    const error = new Error(payload.msg || '请求失败')
    error.code = payload.code
    throw error
  }
  return payload.data
}

export async function login(username, password) {
  return request('/api/user/login', {
    method: 'POST',
    body: JSON.stringify({ username, password })
  })
}

export async function getCurrentUser() {
  return request('/api/user/current')
}

export async function createSession(title) {
  return request('/api/psychologicalchat/session', {
    method: 'POST',
    body: JSON.stringify({ sessionTitle: title || '' })
  })
}

export async function getSessions() {
  return request('/api/psychologicalchat/session')
}

export async function getSessionMessages(sessionId) {
  return request(`/api/psychologicalchat/session/${sessionId}/messages`)
}

export function streamChat(sessionId, message, handlers) {
  const controller = new AbortController()
  fetch(`/api/psychologicalchat/session/${sessionId}/messages`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${getToken()}`,
      Accept: 'text/event-stream'
    },
    body: JSON.stringify({ message }),
    signal: controller.signal
  }).then(async (response) => {
    if (!response.ok) {
      throw new Error(`请求失败（${response.status}）`)
    }
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    const dispatch = (raw) => {
      for (const line of raw.split('\n')) {
        const trimmed = line.trim()
        if (!trimmed) continue
        if (trimmed.startsWith('event:')) {
          const type = trimmed.slice(6).trim()
          if (handlers.onEvent) handlers.onEvent(type)
          continue
        }
        if (trimmed.startsWith('data:')) {
          const data = trimmed.slice(5).trim()
          if (data === '[DONE]') {
            if (handlers.onDone) handlers.onDone()
            continue
          }
          if (handlers.onMessage) handlers.onMessage(data)
        }
      }
    }

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const parts = buffer.split('\n\n')
      buffer = parts.pop()
      for (const chunk of parts) dispatch(chunk)
    }
    if (buffer) dispatch(buffer)
  }).catch((err) => {
    if (err.name !== 'AbortError' && handlers.onError) {
      handlers.onError(err)
    }
  })

  return controller
}
