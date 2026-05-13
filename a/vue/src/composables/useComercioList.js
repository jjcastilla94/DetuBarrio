import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchCategorias, fetchComercios } from '../services/comercioService'
import {
  valoracionOpciones,
  horarioOpciones,
  formatRating,
  mapComercio,
  isComercioOpen,
} from '../utils/comercioHelpers'

export function useComercioList() {
  const comercios = ref([])
  const categorias = ref([])
  const categoriaSeleccionada = ref(null)
  const valoracionSeleccionada = ref('')
  const horarioSeleccionado = ref('')
  const searchQuery = ref('')
  const horaActual = ref(new Date())
  const isLoading = ref(true)
  const errorMessage = ref('')
  let intervaloHoraActual = null
  const route = useRoute()

  const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
    eager: true,
    import: 'default',
  })

  const comercioImagesByName = Object.fromEntries(
    Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
  )

  const DEFAULT_COMERCIO_IMAGE = comercioImagesByName.logo_og || comercioImagesByName['logo_og.png'] || '/images/logo_og.png'

  function getCategoriaNombreById(categoriaId) {
    return categorias.value.find((categoria) => categoria.id === categoriaId)?.nombreCategoria || ''
  }

  function cumpleRangoValoracion(comercio) {
    if (!valoracionSeleccionada.value) {
      return true
    }

    const opcion = valoracionOpciones.find((item) => item.value === valoracionSeleccionada.value)
    if (!opcion) {
      return true
    }

    const valor = Number(comercio.puntuacionMedia || 0)
    return valor >= opcion.min && valor <= opcion.max
  }

  function cumpleHorario(comercio) {
    if (!horarioSeleccionado.value) {
      return true
    }

    const abiertoAhora = isComercioOpen(comercio, horaActual.value)
    return horarioSeleccionado.value === 'abierto' ? abiertoAhora : !abiertoAhora
  }

  function cumpleBusqueda(comercio) {
    const query = searchQuery.value.trim().toLowerCase()
    if (!query) {
      return true
    }

    const textoBuscable = [
      comercio.nombreComercio,
      comercio.descripcion,
      comercio.categoria,
      comercio.horario,
      comercio.diasApertura,
    ]
      .filter(Boolean)
      .join(' ')
      .toLowerCase()

    return textoBuscable.includes(query)
  }

  const comerciosFiltrados = computed(() => {
    const categoriaNombreSeleccionada = getCategoriaNombreById(categoriaSeleccionada.value)

    return comercios.value.filter((comercio) => {
      const cumpleCategoria = !categoriaNombreSeleccionada
        || comercio.categoria?.toLowerCase() === categoriaNombreSeleccionada.toLowerCase()

      return cumpleCategoria
        && cumpleRangoValoracion(comercio)
        && cumpleHorario(comercio)
        && cumpleBusqueda(comercio)
    })
  })

  const totalComercios = computed(() => comercios.value.length)
  const totalResultados = computed(() => comerciosFiltrados.value.length)

  function limpiarFiltros() {
    categoriaSeleccionada.value = null
    valoracionSeleccionada.value = ''
    horarioSeleccionado.value = ''
    searchQuery.value = ''
  }

  function actualizarHoraActual() {
    horaActual.value = new Date()
  }

  watch(
    () => route.query.q,
    (value) => {
      searchQuery.value = typeof value === 'string' ? value : ''
    },
    { immediate: true },
  )

  onMounted(async () => {
    try {
      const [respuestaCateg, respuestaComercios] = await Promise.all([
        fetchCategorias(),
        fetchComercios(),
      ])

      categorias.value = respuestaCateg
      comercios.value = respuestaComercios.map((item) => mapComercio(item, comercioImagesByName, DEFAULT_COMERCIO_IMAGE))
    } catch (error) {
      console.error(error)
      errorMessage.value = 'No se pudieron cargar los comercios. Revisa que la API esté arrancada.'
    } finally {
      isLoading.value = false
    }

    actualizarHoraActual()
    intervaloHoraActual = window.setInterval(actualizarHoraActual, 60000)
  })

  onUnmounted(() => {
    if (intervaloHoraActual) {
      window.clearInterval(intervaloHoraActual)
    }
  })

  return {
    comercios,
    categorias,
    categoriaSeleccionada,
    valoracionSeleccionada,
    horarioSeleccionado,
    searchQuery,
    horaActual,
    isLoading,
    errorMessage,
    valoracionOpciones,
    horarioOpciones,
    comerciosFiltrados,
    totalComercios,
    totalResultados,
    limpiarFiltros,
    formatRating,
    isComercioOpen,
  }
}