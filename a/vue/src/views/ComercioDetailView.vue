<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { fetchComercioById } from '../services/comercioService'
import panaderiaPan from '../assets/images/panaderia_pan.png'
import panaderiaCruasant from '../assets/images/panaderia_cruasant.png'
import saborImage from '../assets/images/sabor.png'
import torfelizImage from '../assets/images/torfeliz.png'
import fontaneroImage from '../assets/images/fontanero.png'
import buenaMesaImage from '../assets/images/buenaMesa.png'

const route = useRoute()
const comercio = ref(null)
const isLoading = ref(true)
const errorMessage = ref('')

const comercioImageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg,webp,svg,gif}', {
  eager: true,
  import: 'default',
})

const comercioImagesByName = Object.fromEntries(
  Object.entries(comercioImageModules).map(([path, url]) => [path.split('/').pop(), url]),
)

const DEFAULT_IMAGE = comercioImagesByName.logo_og || comercioImagesByName['logo_og.png'] || '/images/logo_og.png'

const PRODUCT_IMAGE_CATALOG = {
  'panaderia el trigal': {
    'pan de masa madre': panaderiaPan,
    'croissant de mantequilla': panaderiaCruasant,
    default: panaderiaPan,
  },
  'el sabor casero': {
    'menu del dia': buenaMesaImage,
    default: saborImage,
  },
  'el tornillo feliz': {
    'kit basico bricolaje': fontaneroImage,
    default: torfelizImage,
  },
}

const FALLBACK_PRODUCT_IMAGES = [
  panaderiaPan,
  panaderiaCruasant,
  buenaMesaImage,
  saborImage,
  fontaneroImage,
  torfelizImage,
]

function normalizeImageUrl(imageUrl) {
  if (!imageUrl) {
    return DEFAULT_IMAGE
  }

  const fileName = imageUrl.split('/').pop()
  if (comercioImagesByName[fileName]) {
    return comercioImagesByName[fileName]
  }

  if (/^https?:\/\//i.test(imageUrl) || imageUrl.startsWith('data:') || imageUrl.startsWith('/')) {
    return imageUrl
  }

  const cleanedImage = imageUrl.replace(/^\.\//, '').replace(/^images\//, '')
  const cleanedName = cleanedImage.split('/').pop()

  if (comercioImagesByName[cleanedName]) {
    return comercioImagesByName[cleanedName]
  }

  return `/images/${cleanedName}`
}

function getProductoImage(producto, index = 0) {
  const comercioKey = (comercio.value?.nombreComercio || '').trim().toLowerCase()
  const productoKey = (producto?.nombreProducto || '').trim().toLowerCase()
  const comercioCatalog = PRODUCT_IMAGE_CATALOG[comercioKey]

  if (comercioCatalog) {
    if (comercioCatalog[productoKey]) {
      return comercioCatalog[productoKey]
    }

    return comercioCatalog.default || FALLBACK_PRODUCT_IMAGES[index % FALLBACK_PRODUCT_IMAGES.length]
  }

  if (/^https?:\/\//i.test(producto?.imagen || '')) {
    return producto.imagen
  }

  return FALLBACK_PRODUCT_IMAGES[index % FALLBACK_PRODUCT_IMAGES.length]
}

const heroImage = computed(() => normalizeImageUrl(comercio.value?.banner || comercio.value?.logo))
const logoImage = computed(() => normalizeImageUrl(comercio.value?.logo || comercio.value?.banner))
const productos = computed(() => comercio.value?.productos || [])
const resenas = computed(() => comercio.value?.resenas || [])
const ratingValue = computed(() => Number(comercio.value?.puntuacionMedia || 0).toFixed(1))
const totalResenas = computed(() => Number(comercio.value?.totalResenas || 0))

const ratingDistribution = computed(() => {
  const counts = [1, 2, 3, 4, 5].map((valoracion) => ({ valoracion, count: 0 }))

  for (const resena of resenas.value) {
    const entry = counts.find((item) => item.valoracion === Number(resena.valoracion))
    if (entry) {
      entry.count += 1
    }
  }

  return counts.reverse().map((item) => ({
    ...item,
    percentage: totalResenas.value ? Math.round((item.count / totalResenas.value) * 100) : 0,
  }))
})

function formatCurrency(value) {
  const amount = Number(value || 0)
  return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(amount)
}

function formatDate(value) {
  if (!value) {
    return ''
  }

  return new Intl.DateTimeFormat('es-ES', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

function buildStarIcons(value) {
  const rating = Number(value || 0)
  const fullStars = Math.floor(rating)
  const hasHalfStar = rating - fullStars >= 0.5
  const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0)

  return {
    fullStars,
    hasHalfStar,
    emptyStars,
  }
}

async function loadComercio() {
  const comercioId = Number(route.params.id)

  if (!Number.isFinite(comercioId) || comercioId <= 0) {
    errorMessage.value = 'El identificador del comercio no es válido.'
    isLoading.value = false
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    comercio.value = await fetchComercioById(comercioId)
  } catch (error) {
    console.error(error)
    errorMessage.value = 'No se pudo cargar el detalle del comercio. Revisa que la API esté arrancada.'
    comercio.value = null
  } finally {
    isLoading.value = false
  }
}

watch(() => route.params.id, loadComercio, { immediate: true })
</script>

<template>
  <div class="detail-page py-4 py-lg-5">
    <div class="container container-xl">
      <div v-if="isLoading" class="d-flex justify-content-center py-5">
        <div class="text-center">
          <div class="spinner-border text-primary" role="status" aria-hidden="true"></div>
          <p class="mt-3 mb-0 text-muted">Cargando comercio...</p>
        </div>
      </div>

      <div v-else-if="errorMessage" class="alert alert-danger border-0 shadow-sm" role="alert">
        {{ errorMessage }}
        <div class="mt-3">
          <RouterLink class="btn btn-light fw-semibold" to="/comercios">Volver al listado</RouterLink>
        </div>
      </div>

      <template v-else-if="comercio">
        <div class="mb-4">
          <RouterLink class="btn btn-outline-secondary rounded-pill mb-3" to="/comercios">
            <i class="bi bi-arrow-left me-2"></i> Volver al listado
          </RouterLink>

          <div class="detail-hero card border-0 shadow-sm overflow-hidden rounded-5">
            <div class="position-relative">
              <img
                :src="heroImage"
                class="detail-hero-image w-100"
                :alt="comercio.nombreComercio"
              />
              <div class="detail-hero-overlay position-absolute bottom-0 start-0 w-100 p-4 p-lg-5 text-white">
                <div class="d-flex flex-wrap align-items-end justify-content-between gap-3">
                  <div>
                    <p class="text-uppercase small fw-semibold mb-2 opacity-75">{{ comercio.categoria }}</p>
                    <h1 class="display-6 fw-bold mb-1">{{ comercio.nombreComercio }}</h1>
                    <p class="mb-0 opacity-90">{{ comercio.descripcion }}</p>
                  </div>
                  <span class="badge rounded-pill bg-success px-3 py-2">{{ comercio.puntuacionMedia >= 4 ? 'Muy valorado' : 'Comercio local' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="row g-4 align-items-start">
          <div class="col-12 col-lg-8">
            <section class="card border-0 shadow-sm rounded-4 p-4 mb-4">
              <h2 class="h4 fw-bold mb-3">Sobre nosotros</h2>
              <p class="text-muted mb-0">{{ comercio.descripcion || 'Este comercio todavía no ha añadido una descripción.' }}</p>
            </section>

            <section class="mb-4">
              <div class="d-flex align-items-center justify-content-between gap-3 mb-3">
                <h2 class="h4 fw-bold mb-0">Nuestros productos estrella</h2>
                <span class="text-muted small">{{ productos.length }} productos</span>
              </div>

              <div class="row g-3">
                <div v-for="(producto, index) in productos" :key="producto.id || producto.nombreProducto" class="col-12 col-md-6">
                  <div class="card border-0 shadow-sm rounded-4 h-100 overflow-hidden product-card">
                    <img
                      :src="getProductoImage(producto, index)"
                      class="product-card-image"
                      :alt="producto.nombreProducto"
                    />
                    <div class="card-body">
                      <h3 class="h6 fw-bold mb-1">{{ producto.nombreProducto }}</h3>
                      <p class="text-muted small mb-2">{{ producto.descripcion || 'Sin descripción' }}</p>
                      <p class="fw-semibold mb-0">{{ formatCurrency(producto.precio) }}</p>
                    </div>
                  </div>
                </div>

                <div v-if="!productos.length" class="col-12">
                  <div class="alert alert-light border mb-0">Este comercio aún no tiene productos publicados.</div>
                </div>
              </div>
            </section>

            <section class="card border-0 shadow-sm rounded-4 p-4">
              <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
                <div>
                  <h2 class="h4 fw-bold mb-1">Opiniones de clientes</h2>
                  <p class="text-muted mb-0">{{ totalResenas }} reseñas publicadas</p>
                </div>
                <div class="d-flex align-items-center gap-3">
                  <h3 class="display-6 fw-bold mb-0">{{ ratingValue }}</h3>
                  <div>
                    <div class="text-warning lh-1">
                      <template v-for="index in buildStarIcons(comercio.puntuacionMedia).fullStars" :key="`full-${index}`">
                        <i class="bi bi-star-fill"></i>
                      </template>
                      <i v-if="buildStarIcons(comercio.puntuacionMedia).hasHalfStar" class="bi bi-star-half"></i>
                      <template v-for="index in buildStarIcons(comercio.puntuacionMedia).emptyStars" :key="`empty-${index}`">
                        <i class="bi bi-star"></i>
                      </template>
                    </div>
                    <p class="text-muted small mb-0">Valoración media</p>
                  </div>
                </div>
              </div>

              <div class="mb-4">
                <div v-for="item in ratingDistribution" :key="item.valoracion" class="d-flex align-items-center gap-2 mb-2">
                  <span class="small text-muted rating-label">{{ item.valoracion }}</span>
                  <div class="rating-bar flex-grow-1">
                    <div class="rating-bar-fill" :style="{ width: `${item.percentage}%` }"></div>
                  </div>
                  <span class="small text-muted rating-percentage">{{ item.percentage }}%</span>
                </div>
              </div>

              <div class="d-grid gap-3" id="resenasList">
                <div v-for="resena in resenas" :key="resena.id" class="border rounded-4 p-3 bg-white">
                  <div class="d-flex justify-content-between align-items-start gap-3 mb-2">
                    <div>
                      <h3 class="h6 fw-bold mb-1">{{ resena.titulo }}</h3>
                      <p class="text-muted small mb-0">{{ resena.autorNombre }}<span v-if="resena.autorEmail"> · {{ resena.autorEmail }}</span></p>
                    </div>
                    <span class="badge text-bg-primary">{{ resena.valoracion }}/5</span>
                  </div>
                  <p class="mb-2 text-body-secondary">{{ resena.comentario || 'Sin comentario.' }}</p>
                  <small class="text-muted">{{ formatDate(resena.fecha) }}</small>
                </div>

                <div v-if="!resenas.length" class="text-muted">Aún no hay reseñas para este comercio.</div>
              </div>
            </section>
          </div>

          <div class="col-12 col-lg-4">
            <div class="sticky-top detail-sidebar" style="top: 20px;">
              <div class="card border-0 shadow-sm rounded-4 p-4 mb-3">
                <div class="d-flex align-items-center gap-3 mb-3">
                  <img
                    :src="logoImage"
                    class="rounded-4 detail-logo"
                    :alt="comercio.nombreComercio"
                  />
                  <div>
                    <p class="text-muted small mb-1">{{ comercio.categoria }}</p>
                    <h2 class="h5 fw-bold mb-0">{{ comercio.nombreComercio }}</h2>
                  </div>
                </div>

                <button class="btn btn-primary w-100 rounded-3 mb-3">Contactar ahora</button>

                <div class="mb-3">
                  <p class="small text-uppercase text-muted fw-semibold mb-2">Horario</p>
                  <p class="mb-1 small">{{ comercio.horario || 'Horario no disponible' }}</p>
                  <p class="mb-0 small">{{ comercio.diasApertura || 'Días de apertura no disponibles' }}</p>
                </div>

                <div>
                  <p class="small text-uppercase text-muted fw-semibold mb-2">Estado</p>
                  <div class="d-flex align-items-center gap-2">
                    <span class="badge rounded-pill bg-success">Abierto</span>
                    <span class="small text-muted">Información orientativa desde la ficha del comercio</span>
                  </div>
                </div>
              </div>

              <div class="card border-0 shadow-sm rounded-4 p-4">
                <h3 class="h6 fw-bold mb-3">Ubicación</h3>
                <p class="text-muted small mb-1">📍 Calle del Pan, 123, 28080</p>
                <p class="text-muted small mb-3">Madrid, España</p>
                <img :src="logoImage" class="w-100 rounded-4" :alt="comercio.nombreComercio" />
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.detail-page {
  background:
    radial-gradient(circle at top left, rgba(58, 134, 255, 0.08), transparent 30%),
    linear-gradient(180deg, #fbfdff 0%, #ffffff 55%);
}

.detail-hero-image {
  height: 360px;
  object-fit: cover;
}

.detail-hero-overlay {
  background: linear-gradient(180deg, transparent 0%, rgba(5, 25, 40, 0.82) 100%);
}

.product-card-image {
  height: 170px;
  width: 100%;
  object-fit: cover;
}

.detail-logo {
  width: 72px;
  height: 72px;
  object-fit: cover;
}

.rating-bar {
  height: 10px;
  border-radius: 999px;
  background: #e9ecef;
  overflow: hidden;
}

.rating-bar-fill {
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #3a86ff 0%, #67a4ff 100%);
}

.rating-label,
.rating-percentage {
  min-width: 24px;
}

.detail-sidebar {
  z-index: 10;
}

/* Forzar color blanco en el texto del overlay hero del comercio */
.detail-hero-overlay,
.detail-hero-overlay h1,
.detail-hero-overlay p,
.detail-hero-overlay .text-uppercase {
  color: #ffffff !important;
}
</style>