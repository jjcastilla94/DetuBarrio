import axios from 'axios'

const AUTH_KEY = 'detubarrio_auth'

function emitAuthChanged() {
  window.dispatchEvent(new Event('detubarrio-auth-changed'))
}

export function saveAuth(authResponse) {
  localStorage.setItem(AUTH_KEY, JSON.stringify(authResponse))
  emitAuthChanged()
}

export function getAuth() {
  const raw = localStorage.getItem(AUTH_KEY)
  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw)
  } catch (_error) {
    localStorage.removeItem(AUTH_KEY)
    return null
  }
}

export function clearAuth() {
  localStorage.removeItem(AUTH_KEY)
  emitAuthChanged()
}

export function getToken() {
  return getAuth()?.token || null
}

export function createAuthApi() {
  const instance = axios.create()
  instance.interceptors.request.use((config) => {
    const token = getToken()
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })
  return instance
}

export async function login(email, password) {
  const response = await axios.post('/api/auth/login', { email, password })
  saveAuth(response.data)
  return response.data
}