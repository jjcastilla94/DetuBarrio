import axios from 'axios'

export async function enviarMensajeContacto(payload) {
  const response = await axios.post('/api/contacto/mensaje', payload)
  return response.data
}

export async function solicitarColaboracion(payload) {
  const response = await axios.post('/api/contacto/colaboracion', payload)
  return response.data
}