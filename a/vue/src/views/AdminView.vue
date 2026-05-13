<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { clearAuth, getAuth } from '../services/authService'
import { fetchAdminColaboraciones, fetchAdminMensajes, fetchAdminSolicitudesComercios, aprobarComercio, rechazarComercio, aprobarColaboracion, rechazarColaboracion } from '../services/adminService'

const auth = ref(getAuth())
const loading = ref(false)
const errorMessage = ref('')
const mensajes = ref([])
const colaboraciones = ref([])
const solicitudesComercios = ref([])
const activeTab = ref('mensajes')
const processingId = ref(null)
const processingColaboracionId = ref(null)

const adminName = computed(() => auth.value?.nombre || 'Administrador')
const isAdmin = computed(() => auth.value?.rol === 'ADMIN')

function syncAuth() {
  auth.value = getAuth()
}

function getEstadoColaboracion(colaboracion) {
  return colaboracion?.estado || 'PENDIENTE'
}

function isColaboracionPendiente(colaboracion) {
  return getEstadoColaboracion(colaboracion) === 'PENDIENTE'
}

async function loadData() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [mensajesResponse, colaboracionesResponse, solicitudesResponse] = await Promise.all([
      fetchAdminMensajes(),
      fetchAdminColaboraciones(),
      fetchAdminSolicitudesComercios(),
    ])

    mensajes.value = mensajesResponse
    colaboraciones.value = colaboracionesResponse
    solicitudesComercios.value = solicitudesResponse
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || error?.message || 'No se pudo cargar la información de administración.'
  } finally {
    loading.value = false
  }
}

function handleLogout() {
  clearAuth()
  auth.value = null
  mensajes.value = []
  colaboraciones.value = []
  solicitudesComercios.value = []
}

async function handleAprobarComercio(comercioId) {
  processingId.value = comercioId
  try {
    await aprobarComercio(comercioId)
    await loadData()
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || 'No se pudo aprobar el comercio.'
  } finally {
    processingId.value = null
  }
}

async function handleRechazarComercio(comercioId) {
  const motivo = prompt('¿Por qué rechazas esta solicitud?')
  if (!motivo) return

  processingId.value = comercioId
  try {
    await rechazarComercio(comercioId, motivo)
    await loadData()
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || 'No se pudo rechazar el comercio.'
  } finally {
    processingId.value = null
  }
}

async function handleAprobarColaboracion(solicitudId) {
  processingColaboracionId.value = solicitudId
  try {
    await aprobarColaboracion(solicitudId)
    await loadData()
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || 'No se pudo aprobar la colaboración.'
  } finally {
    processingColaboracionId.value = null
  }
}

async function handleRechazarColaboracion(solicitudId) {
  const motivo = prompt('¿Por qué rechazas esta colaboración?')
  if (!motivo) return

  processingColaboracionId.value = solicitudId
  try {
    await rechazarColaboracion(solicitudId, motivo)
    await loadData()
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || 'No se pudo rechazar la colaboración.'
  } finally {
    processingColaboracionId.value = null
  }
}

onMounted(async () => {
  window.addEventListener('detubarrio-auth-changed', syncAuth)

  if (auth.value?.token) {
    await loadData()
  }
})

onUnmounted(() => {
  window.removeEventListener('detubarrio-auth-changed', syncAuth)
})
</script>

<template>
  <section class="admin-hero py-5 text-white">
    <div class="container py-4 py-lg-5">
      <div class="row align-items-end g-4">
        <div class="col-lg-8">
          <span class="badge rounded-pill text-bg-light text-primary px-3 py-2 mb-3">Panel de administración</span>
          <h1 class="display-5 fw-bold mb-3 text-white">Mensajes y colaboraciones del barrio</h1>
          <p class="lead mb-0 text-white-75">
            Revisa aquí los mensajes de usuarios y las solicitudes de comercios sin abrir la base de datos.
          </p>
        </div>
        <div class="col-lg-4">
          <div class="glass-panel rounded-4 p-4 shadow-lg">
            <template v-if="isAdmin">
              <p class="mb-2 text-white-75">Sesión activa</p>
              <p class="h5 fw-bold mb-2 text-white">{{ adminName }}</p>
              <p class="mb-3 text-white-75">{{ auth.email }}</p>
              <button class="btn btn-light w-100 fw-bold" type="button" @click="handleLogout">Cerrar sesión</button>
            </template>
            <template v-else-if="auth?.token">
              <p class="mb-2 text-white-75">Acceso denegado</p>
              <p class="h5 fw-bold mb-3 text-white">Tu usuario no tiene permisos de administrador</p>
              <button class="btn btn-light w-100 fw-bold" type="button" @click="handleLogout">Cerrar sesión</button>
            </template>
            <template v-else>
              <p class="mb-2 text-white-75">Acceso restringido</p>
              <p class="h5 fw-bold mb-3 text-white">Accede desde la página de inicio de sesión</p>
              <RouterLink class="btn btn-light w-100 fw-bold" to="/login">Ir al login</RouterLink>
              <p class="small text-white-75 mt-3 mb-0">Solo el rol ADMIN puede entrar a este panel.</p>
            </template>
          </div>
        </div>
      </div>
    </div>
  </section>

  <section class="py-5">
    <div class="container">
      <div v-if="auth?.token && !isAdmin" class="alert alert-danger border-0 shadow-sm" role="alert">
        Este panel es exclusivo para usuarios con rol ADMIN.
      </div>

      <div v-else-if="!auth?.token" class="alert alert-warning border-0 shadow-sm" role="alert">
        Debes iniciar sesión como administrador para ver los datos.
      </div>

      <div v-else-if="isAdmin">
        <div class="row g-4 mb-4">
          <div class="col-md-3">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-body p-4">
                <p class="text-muted mb-1">Mensajes de usuarios</p>
                <h2 class="display-6 fw-bold mb-0">{{ mensajes.length }}</h2>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-body p-4">
                <p class="text-muted mb-1">Solicitudes de comercio</p>
                <h2 class="display-6 fw-bold mb-0">{{ solicitudesComercios.length }}</h2>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-body p-4">
                <p class="text-muted mb-1">Colaboraciones</p>
                <h2 class="display-6 fw-bold mb-0">{{ colaboraciones.length }}</h2>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-body p-4">
                <p class="text-muted mb-1">Estado</p>
                <h2 class="h3 fw-bold mb-0">Operativo</h2>
              </div>
            </div>
          </div>
        </div>

        <div v-if="errorMessage" class="alert alert-danger border-0 shadow-sm" role="alert">
          {{ errorMessage }}
        </div>

        <div class="card border-0 shadow-sm rounded-4">
          <div class="card-body p-0">
            <ul class="nav nav-tabs px-4 pt-4">
              <li class="nav-item">
                <button class="nav-link" :class="{ active: activeTab === 'mensajes' }" @click="activeTab = 'mensajes'">
                  Mensajes
                </button>
              </li>
              <li class="nav-item">
                <button class="nav-link" :class="{ active: activeTab === 'solicitudes' }" @click="activeTab = 'solicitudes'">
                  Solicitudes de Comercios
                </button>
              </li>
              <li class="nav-item">
                <button class="nav-link" :class="{ active: activeTab === 'colaboraciones' }" @click="activeTab = 'colaboraciones'">
                  Colaboraciones
                </button>
              </li>
            </ul>

            <div class="p-4">
              <div v-if="loading" class="text-center py-5">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="mt-3 text-muted mb-0">Cargando datos...</p>
              </div>

              <div v-else-if="activeTab === 'mensajes'">
                <div v-if="mensajes.length === 0" class="alert alert-light border mb-0">
                  No hay mensajes todavía.
                </div>
                <div v-else class="table-responsive">
                  <table class="table align-middle mb-0">
                    <thead>
                      <tr>
                        <th>Fecha</th>
                        <th>Nombre</th>
                        <th>Email</th>
                        <th>Asunto</th>
                        <th>Tipo</th>
                        <th>Mensaje</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="mensaje in mensajes" :key="mensaje.id">
                        <td class="text-nowrap">{{ new Date(mensaje.fechaCreacion).toLocaleString('es-ES') }}</td>
                        <td>{{ mensaje.nombre }}</td>
                        <td>{{ mensaje.email }}</td>
                        <td>{{ mensaje.asunto }}</td>
                        <td><span class="badge text-bg-secondary">{{ mensaje.tipo }}</span></td>
                        <td style="min-width: 280px">{{ mensaje.mensaje }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <div v-else-if="activeTab === 'solicitudes'">
                <div v-if="solicitudesComercios.length === 0" class="alert alert-light border mb-0">
                  No hay solicitudes de comercios pendientes.
                </div>
                <div v-else class="table-responsive">
                  <table class="table align-middle mb-0">
                    <thead>
                      <tr>
                        <th>Fecha</th>
                        <th>Nombre del Comercio</th>
                        <th>Propietario</th>
                        <th>Email</th>
                        <th>Categoría</th>
                        <th>Descripción</th>
                        <th>Acciones</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="solicitud in solicitudesComercios" :key="solicitud.id">
                        <td class="text-nowrap">{{ new Date(solicitud.fechaSolicitud).toLocaleString('es-ES') }}</td>
                        <td class="fw-bold">{{ solicitud.nombreComercio }}</td>
                        <td>{{ solicitud.nombreUsuario }}</td>
                        <td>{{ solicitud.emailUsuario }}</td>
                        <td><span class="badge text-bg-primary">{{ solicitud.categoria }}</span></td>
                        <td style="min-width: 280px">{{ solicitud.descripcion || 'Sin descripción' }}</td>
                        <td class="text-nowrap">
                          <button
                            class="btn btn-sm btn-success me-2"
                            @click="handleAprobarComercio(solicitud.id)"
                            :disabled="processingId === solicitud.id"
                          >
                            {{ processingId === solicitud.id ? 'Procesando...' : 'Aprobar' }}
                          </button>
                          <button
                            class="btn btn-sm btn-danger"
                            @click="handleRechazarComercio(solicitud.id)"
                            :disabled="processingId === solicitud.id"
                          >
                            {{ processingId === solicitud.id ? 'Procesando...' : 'Rechazar' }}
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>

              <div v-else>
                <div v-if="colaboraciones.length === 0" class="alert alert-light border mb-0">
                  No hay solicitudes de colaboración todavía.
                </div>
                <div v-else class="table-responsive">
                  <table class="table align-middle mb-0">
                    <thead>
                      <tr>
                        <th>Fecha</th>
                        <th>Comercio</th>
                        <th>Titular</th>
                        <th>Email</th>
                        <th>Teléfono</th>
                        <th>Categoría</th>
                        <th>Descripción</th>
                        <th>Acciones</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="colaboracion in colaboraciones" :key="colaboracion.id">
                        <td class="text-nowrap">{{ new Date(colaboracion.fechaCreacion).toLocaleString('es-ES') }}</td>
                        <td>{{ colaboracion.nombreComercio }}</td>
                        <td>{{ colaboracion.nombreTitular }}</td>
                        <td>{{ colaboracion.emailComercio }}</td>
                        <td>{{ colaboracion.telefonoComercio }}</td>
                        <td><span class="badge text-bg-primary">{{ colaboracion.categoria }}</span></td>
                        <td style="min-width: 280px">
                          <div>{{ colaboracion.descripcion || 'Sin descripción' }}</div>
                          <div v-if="getEstadoColaboracion(colaboracion) === 'RECHAZADA' && colaboracion.motivoRechazo" class="alert alert-danger py-2 px-3 mt-2 mb-0 small">
                            Motivo del rechazo: {{ colaboracion.motivoRechazo }}
                          </div>
                        </td>
                        <td class="text-nowrap">
                          <button
                            class="btn btn-sm btn-success me-2"
                            :disabled="processingColaboracionId === colaboracion.id || !isColaboracionPendiente(colaboracion)"
                            @click="handleAprobarColaboracion(colaboracion.id)"
                          >
                            {{ processingColaboracionId === colaboracion.id ? 'Procesando...' : 'Aprobar' }}
                          </button>
                          <button
                            class="btn btn-sm btn-danger"
                            :disabled="processingColaboracionId === colaboracion.id || !isColaboracionPendiente(colaboracion)"
                            @click="handleRechazarColaboracion(colaboracion.id)"
                          >
                            Rechazar
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.admin-hero {
  background: linear-gradient(135deg, #0b2447 0%, #163e6f 55%, #2f73e0 100%);
}

.glass-panel {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.text-white-75 {
  color: rgba(255, 255, 255, 0.78);
}
</style>