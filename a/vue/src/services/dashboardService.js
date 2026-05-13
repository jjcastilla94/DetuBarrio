import { createAuthApi } from './authService'

const api = createAuthApi()

export async function fetchUsuarioDashboard() {
  const response = await api.get('/api/dashboard/usuario')
  return response.data
}

export async function fetchComercioDashboard() {
  const response = await api.get('/api/dashboard/comercio')
  return response.data
}

export async function deleteRejectedCommerceAccount() {
  const response = await api.delete('/api/dashboard/comercio')
  return response.data
}
