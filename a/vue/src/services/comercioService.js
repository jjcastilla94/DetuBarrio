import axios from 'axios'

export async function fetchCategorias() {
  const respuesta = await axios.get('/api/categorias')
  return Array.isArray(respuesta.data) ? respuesta.data : []
}

export async function fetchComercios() {
  const respuesta = await axios.get('/api/comercios')
  return Array.isArray(respuesta.data) ? respuesta.data : []
}

export async function fetchComercioById(comercioId) {
  const respuesta = await axios.get(`/api/comercios/${comercioId}`)
  return respuesta.data || null
}