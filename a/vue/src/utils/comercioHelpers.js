export const valoracionOpciones = [
  { value: '', label: 'Todas las valoraciones' },
  { value: '0', label: '0.0 ', min: 0.0, max: 0.9 },
  { value: '1', label: '1.0 ', min: 1.0, max: 1.9 },
  { value: '2', label: '2.0 ', min: 2.0, max: 2.9 },
  { value: '3', label: '3.0 ', min: 3.0, max: 3.9 },
  { value: '4', label: '4.0 ', min: 4.0, max: 4.9 },
  { value: '5', label: '5.0 ', min: 5.0, max: 5.0 },
]

export const horarioOpciones = [
  { value: '', label: 'Todos los comercios' },
  { value: 'abierto', label: 'Abierto ahora' },
  { value: 'cerrado', label: 'Cerrado ahora' },
]

export function normalizeImageUrl(imageUrl, imageLookup, defaultImage) {
  if (!imageUrl) {
    return defaultImage
  }

  const fileName = imageUrl.split('/').pop()
  if (imageLookup[fileName]) {
    return imageLookup[fileName]
  }

  if (/^https?:\/\//i.test(imageUrl) || imageUrl.startsWith('data:') || imageUrl.startsWith('/')) {
    return imageUrl
  }

  const cleanedImage = imageUrl.replace(/^\.\//, '').replace(/^images\//, '')
  const cleanedName = cleanedImage.split('/').pop()

  if (imageLookup[cleanedName]) {
    return imageLookup[cleanedName]
  }

  return `/images/${cleanedName}`
}

export function formatRating(value) {
  return Number(value || 0).toFixed(1)
}

export function mapComercio(comercio, imageLookup, defaultImage) {
  return {
    ...comercio,
    imageUrl: normalizeImageUrl(comercio.logo || comercio.banner || comercio.imagen, imageLookup, defaultImage),
    isOpened: comercio.abierto ?? comercio.isOpen ?? comercio.disponible ?? comercio.activo ?? null,
  }
}

export function parseMinutes(timeValue) {
  const [hours, minutes] = timeValue.split(':').map(Number)
  if (Number.isNaN(hours) || Number.isNaN(minutes)) {
    return null
  }

  return (hours * 60) + minutes
}

export function parseDaysText(textoDias) {
  const texto = (textoDias || '').toLowerCase().trim()

  if (!texto) {
    return null
  }

  if (texto.includes('lunes a domingo') || texto.includes('todos los dias') || texto.includes('todos los días')) {
    return [0, 1, 2, 3, 4, 5, 6]
  }

  if (texto.includes('lunes a viernes')) {
    return [1, 2, 3, 4, 5]
  }

  if (texto.includes('lunes a sabado') || texto.includes('lunes a sábado')) {
    return [1, 2, 3, 4, 5, 6]
  }

  const matchAbreviado = texto.match(/([dlmxjvs])\s*[-–]\s*([dlmxjvs])/i)
  if (matchAbreviado) {
    const mapaDias = { d: 0, l: 1, m: 2, x: 3, j: 4, v: 5, s: 6 }
    const diaInicio = mapaDias[matchAbreviado[1].toLowerCase()]
    const diaFin = mapaDias[matchAbreviado[2].toLowerCase()]

    if (diaInicio === undefined || diaFin === undefined) {
      return null
    }

    if (diaInicio <= diaFin) {
      return Array.from({ length: diaFin - diaInicio + 1 }, (_, index) => diaInicio + index)
    }

    return [...Array.from({ length: 7 - diaInicio }, (_, index) => diaInicio + index), ...Array.from({ length: diaFin + 1 }, (_, index) => index)]
  }

  return null
}

export function isComercioOpen(comercio, currentDate) {
  if (typeof comercio.isOpened === 'boolean') {
    return comercio.isOpened
  }

  const diaActual = currentDate.getDay()
  const minutosActuales = (currentDate.getHours() * 60) + currentDate.getMinutes()
  const diasPermitidos = parseDaysText(comercio.diasApertura || comercio.horario)

  if (diasPermitidos && !diasPermitidos.includes(diaActual)) {
    return false
  }

  const textoHorario = `${comercio.horario || ''}`
  const rangoHoras = textoHorario.match(/(\d{1,2}:\d{2})\s*[-–]\s*(\d{1,2}:\d{2})/)
  if (!rangoHoras) {
    return true
  }

  const apertura = parseMinutes(rangoHoras[1])
  const cierre = parseMinutes(rangoHoras[2])

  if (apertura === null || cierre === null) {
    return true
  }

  if (apertura <= cierre) {
    return minutosActuales >= apertura && minutosActuales <= cierre
  }

  return minutosActuales >= apertura || minutosActuales <= cierre
}