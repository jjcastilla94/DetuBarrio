<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import fondo from '../assets/images/fondo.png'
import { clearAuth, getAuth } from '../services/authService'
import { fetchComercioDashboard } from '../services/dashboardService'

const router = useRouter()
const auth = ref(getAuth())
const loading = ref(false)
const errorMessage = ref('')
const dashboard = ref(null)
const notificationDismissed = ref(false)

const displayName = computed(() => dashboard.value?.nombre || auth.value?.nombre || 'Comercio')
const email = computed(() => dashboard.value?.email || auth.value?.email || '')
const comercioName = computed(() => dashboard.value?.comercioNombre || 'Mi comercio')
const estadoComercio = computed(() => dashboard.value?.estadoComercio || 'PENDIENTE')
const gestionAutorizada = computed(() => Boolean(dashboard.value?.gestionAutorizada))
const solicitudColaboracionEstado = computed(() => dashboard.value?.solicitudColaboracionEstado || 'SIN_SOLICITUD')
const motivoBloqueoGestion = computed(() => dashboard.value?.motivoBloqueoGestion || '')
const motivoRechazo = computed(() => dashboard.value?.motivoRechazo || '')
const initial = computed(() => displayName.value.trim().charAt(0).toUpperCase() || 'C')
const notificationKey = computed(() => {
  const emailKey = (auth.value?.email || '').trim().toLowerCase() || 'sin-email'
  return `detubarrio_comercio_notice_${emailKey}`
})

const estadoBadgeClass = computed(() => {
  switch (estadoComercio.value) {
    case 'APROBADO':
      return 'bg-success'
    case 'RECHAZADO':
      return 'bg-danger'
    default:
      return 'bg-warning'
  }
})

const estadoText = computed(() => {
  switch (estadoComercio.value) {
    case 'APROBADO':
      return gestionAutorizada.value ? 'Aprobado y autorizado' : 'Cuenta aprobada'
    case 'RECHAZADO':
      return 'Rechazado'
    default:
      return 'Pendiente de aprobación'
  }
})

const shouldShowStatusNotice = computed(() => {
  if (estadoComercio.value === 'PENDIENTE') {
    return true
  }

  if (estadoComercio.value === 'APROBADO' && !gestionAutorizada.value) {
    return true
  }

  if (estadoComercio.value === 'RECHAZADO') {
    return true
  }

  return false
})

const canDismissNotification = computed(() => estadoComercio.value !== 'PENDIENTE')

function isNotificationDismissed() {
  return localStorage.getItem(notificationKey.value) === '1'
}

function setNotificationDismissed() {
  localStorage.setItem(notificationKey.value, '1')
}

function clearNotificationDismissed() {
  localStorage.removeItem(notificationKey.value)
}

function redirectByRole() {
  const currentAuth = getAuth()

  if (!currentAuth?.token) {
    router.replace({ name: 'login' })
    return false
  }

  if (currentAuth.rol !== 'COMERCIO') {
    router.replace(currentAuth.rol === 'USUARIO' ? { name: 'dashboard-usuario' } : currentAuth.rol === 'ADMIN' ? { name: 'admin' } : { name: 'home' })
    return false
  }

  auth.value = currentAuth
  return true
}

async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''

  try {
    dashboard.value = await fetchComercioDashboard()
    notificationDismissed.value = isNotificationDismissed()
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || error?.message || 'No se pudo cargar el panel de comercio.'
  } finally {
    loading.value = false
  }
}

async function handleDismissNotification() {
  if (estadoComercio.value === 'RECHAZADO') {
    setNotificationDismissed()
    clearAuth()
    router.replace({ name: 'login' })
    return
  }

  setNotificationDismissed()
  notificationDismissed.value = true
}

function handleLogout() {
  clearAuth()
  router.replace({ name: 'login' })
}

onMounted(async () => {
  if (!redirectByRole()) {
    return
  }

  await loadDashboard()
})
</script>

<template>
  <main class="dashboard-shell">
    <aside class="dashboard-sidebar">
      <div class="profile-card">
        <div class="profile-avatar">{{ initial }}</div>
        <div>
          <h6 class="mb-1 fw-bold">{{ comercioName }}</h6>
          <small class="text-muted">{{ email }}</small>
        </div>
      </div>

      <nav class="sidebar-nav">
        <a class="active" href="#">
          <i class="bi bi-grid me-2"></i> Panel general
        </a>
        <a href="#">
          <i class="bi bi-box me-2"></i> Productos
        </a>
        <a href="#">
          <i class="bi bi-calendar-event me-2"></i> Reservas
        </a>
        <a href="#">
          <i class="bi bi-people me-2"></i> Clientes
        </a>
        <a href="#">
          <i class="bi bi-bar-chart me-2"></i> Estadísticas
        </a>
        <a href="#">
          <i class="bi bi-gear me-2"></i> Configuración
        </a>
      </nav>

      <div class="sidebar-actions">
        <RouterLink class="btn btn-light w-100 fw-semibold" to="/comercios">
          <i class="bi bi-shop me-1"></i> Ver escaparate
        </RouterLink>
        <button class="btn btn-outline-danger w-100 fw-semibold" type="button" @click="handleLogout">
          Cerrar sesión
        </button>
      </div>
    </aside>

    <section class="dashboard-content">
      <header class="dashboard-header">
        <div>
          <p class="eyebrow mb-1">Área de comercio</p>
          <h1 class="h2 fw-bold mb-1">Panel general - {{ comercioName }}</h1>
          <p class="text-muted mb-0">Bienvenido de nuevo, aquí tienes un resumen de tu actividad.</p>
        </div>

        <div class="header-actions">
          <button class="icon-round" type="button" aria-label="Notificaciones">
            <i class="bi bi-bell"></i>
          </button>
          <div class="avatar-badge">{{ initial }}</div>
        </div>
      </header>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="mt-3 text-muted mb-0">Cargando tu panel...</p>
      </div>

      <template v-else>
        <div v-if="errorMessage" class="alert alert-danger border-0 shadow-sm mb-4">{{ errorMessage }}</div>

        <!-- Estado del comercio -->
        <div v-if="shouldShowStatusNotice && !notificationDismissed" class="alert mb-4" :class="{
          'alert-info': estadoComercio === 'PENDIENTE',
          'alert-success': estadoComercio === 'APROBADO',
          'alert-danger': estadoComercio === 'RECHAZADO'
        }">
          <div class="d-flex align-items-start justify-content-between gap-3">
            <div>
              <h5 class="mb-1 fw-bold">Estado del comercio</h5>
              <p class="mb-0">Tu comercio está: <strong>{{ estadoText }}</strong></p>
              <small v-if="estadoComercio === 'PENDIENTE'" class="d-block mt-2">
                Tu cuenta está en revisión. Cuando sea aprobada, todavía tendrás que solicitar la colaboración del administrador para desbloquear la gestión.
              </small>
              <small v-else-if="estadoComercio === 'APROBADO' && !gestionAutorizada" class="d-block mt-2">
                Tu cuenta ya está aprobada, pero tu comercio sigue bloqueado hasta que el administrador autorice la colaboración.
              </small>
              <small v-else-if="estadoComercio === 'RECHAZADO'" class="d-block mt-2">
                Tu solicitud fue rechazada. Si cierras este aviso, el bloqueo seguirá visible.
              </small>
              <small v-else-if="estadoComercio === 'APROBADO' && gestionAutorizada" class="d-block mt-2">
                Tu comercio ya puede gestionarse y aparecerá en el listado público.
              </small>
            </div>
            <div class="d-flex flex-column align-items-end gap-2">
              <span class="badge" :class="estadoBadgeClass">{{ estadoText }}</span>
              <button class="btn btn-sm btn-light" type="button" @click="handleDismissNotification" v-if="canDismissNotification && estadoComercio !== 'PENDIENTE'">
                Cerrar
              </button>
              <RouterLink v-if="estadoComercio === 'APROBADO' && !gestionAutorizada" class="btn btn-sm btn-outline-primary" to="/contacto">
                Ir a contacto
              </RouterLink>
            </div>
          </div>
          <p v-if="estadoComercio === 'RECHAZADO' && motivoRechazo" class="mb-0 mt-3 small">
            Motivo: {{ motivoRechazo }}
          </p>
          <p v-else-if="estadoComercio === 'APROBADO' && !gestionAutorizada && motivoBloqueoGestion" class="mb-0 mt-3 small">
            {{ motivoBloqueoGestion }}
          </p>
        </div>

        <!-- Panel bloqueado si no está aprobado -->
        <div v-if="estadoComercio !== 'APROBADO' || !gestionAutorizada" class="alert alert-warning border-0 shadow-sm mb-4">
          <div class="d-flex align-items-center gap-3">
            <div style="font-size: 2rem;">
              <i class="bi bi-lock-fill"></i>
            </div>
            <div>
              <h5 class="mb-2 fw-bold">Tu comercio está bloqueado</h5>
              <p class="mb-0 text-muted">
                <span v-if="estadoComercio === 'PENDIENTE'">
                  Espera la aprobación del administrador para desbloquear la cuenta.
                </span>
                <span v-else-if="estadoComercio === 'APROBADO' && !gestionAutorizada">
                  Tu cuenta está aprobada, pero necesitas solicitar colaboración al administrador para poder gestionar el comercio y aparecer en el listado.
                </span>
                <span v-else-if="estadoComercio === 'RECHAZADO'">
                  Tu solicitud fue rechazada. El bloqueo seguirá visible hasta que se autorice la colaboración.
                </span>
              </p>
              <RouterLink v-if="estadoComercio === 'APROBADO' && !gestionAutorizada" class="btn btn-outline-primary btn-sm mt-3" to="/contacto">
                Solicitar colaboración
              </RouterLink>
            </div>
          </div>
        </div>

        <div class="hero-panel shadow-sm mb-4">
          <div class="hero-panel__text">
            <p class="eyebrow mb-2">Resumen del negocio</p>
            <h2 class="fw-bold mb-2">Gestiona tu comercio desde una sola vista</h2>
            <p class="text-muted mb-4">
              Consulta productos, reservas y estadísticas sin salir del panel.
            </p>
            <button class="btn btn-primary px-4 py-2 fw-semibold" type="button">
              <i class="bi bi-plus-circle me-1"></i> Añadir producto
            </button>
          </div>
          <div class="hero-panel__image">
            <img :src="fondo" alt="Imagen de negocio" />
          </div>
        </div>

        <div class="row g-4 mb-4">
          <div class="col-md-4">
            <div class="metric-card shadow-sm">
              <p class="text-muted mb-2">Visitas al perfil (30 días)</p>
              <div class="metric-value">1,230</div>
              <small class="text-success">+5.2%</small>
            </div>
          </div>
          <div class="col-md-4">
            <div class="metric-card shadow-sm">
              <p class="text-muted mb-2">Nuevas reservas</p>
              <div class="metric-value">8</div>
              <small class="text-success">Hoy</small>
            </div>
          </div>
          <div class="col-md-4">
            <div class="metric-card shadow-sm">
              <p class="text-muted mb-2">Opiniones recientes</p>
              <div class="metric-value">4</div>
              <small class="text-danger">-0.5%</small>
            </div>
          </div>
        </div>

        <div class="panel-card shadow-sm">
          <div class="panel-card__head">
            <h5 class="fw-bold mb-0">Gestión de productos</h5>
            <span class="text-muted small">Catálogo activo</span>
          </div>

          <div class="table-responsive">
            <table class="table align-middle mb-0">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Categoría</th>
                  <th>Precio</th>
                  <th>Estado</th>
                  <th class="text-end">Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>Zapatillas Deportivas</td>
                  <td>Calzado</td>
                  <td>79,99 €</td>
                  <td><span class="status-badge status-confirmada">Activo</span></td>
                  <td class="text-end"><a class="text-primary text-decoration-none" href="#">Editar</a></td>
                </tr>
                <tr>
                  <td>Reloj Clásico</td>
                  <td>Accesorios</td>
                  <td>125,00 €</td>
                  <td><span class="status-badge status-confirmada">Activo</span></td>
                  <td class="text-end"><a class="text-primary text-decoration-none" href="#">Editar</a></td>
                </tr>
                <tr>
                  <td>Mochila de Cuero</td>
                  <td>Bolsos</td>
                  <td>95,50 €</td>
                  <td><span class="status-badge status-pendiente">Inactivo</span></td>
                  <td class="text-end"><a class="text-primary text-decoration-none" href="#">Editar</a></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </template>
    </section>
  </main>
</template>

<style scoped>
.dashboard-shell {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  min-height: calc(100vh - 72px);
  background: #f6f9fe;
}

.dashboard-sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  padding: 1.5rem;
  background: white;
  border-right: 1px solid rgba(15, 23, 42, 0.08);
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 0.9rem;
  padding: 1rem;
  border-radius: 1rem;
  background: #f8fbff;
}

.profile-avatar,
.avatar-badge {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--db-primary);
  color: white;
  font-weight: 700;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.sidebar-nav a {
  text-decoration: none;
  color: #495057;
  padding: 0.8rem 1rem;
  border-radius: 0.9rem;
}

.sidebar-nav a.active,
.sidebar-nav a:hover {
  background: #eaf2ff;
  color: var(--db-primary);
  font-weight: 600;
}

.sidebar-actions {
  margin-top: auto;
  display: grid;
  gap: 0.75rem;
}

.dashboard-content {
  padding: 1.5rem;
}

.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.18em;
  font-size: 0.72rem;
  color: var(--db-secondary);
  font-weight: 700;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.icon-round {
  width: 42px;
  height: 42px;
  border-radius: 999px;
  border: 1px solid rgba(15, 23, 42, 0.1);
  background: white;
}

.hero-panel,
.metric-card,
.panel-card {
  background: white;
  border-radius: 1.25rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
}

.hero-panel {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  align-items: center;
  overflow: hidden;
}

.hero-panel__text {
  padding: 1.75rem;
}

.hero-panel__image {
  background: linear-gradient(135deg, #eef4ff, #dce9ff);
  height: 100%;
}

.hero-panel__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  min-height: 220px;
}

.metric-card {
  padding: 1.25rem;
}

.metric-value {
  font-size: 2rem;
  font-weight: 800;
  line-height: 1;
  margin-bottom: 0.35rem;
}

.panel-card {
  padding: 0;
  overflow: hidden;
}

.panel-card__head {
  display: flex;
  align-items: end;
  justify-content: space-between;
  padding: 1.25rem 1.25rem 0.75rem;
}

.panel-card thead th {
  font-size: 0.72rem;
  text-transform: uppercase;
  color: #6c757d;
  border-top: 0;
}

.panel-card tbody td {
  padding-top: 1rem;
  padding-bottom: 1rem;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.35rem 0.8rem;
  border-radius: 999px;
  font-size: 0.82rem;
  font-weight: 700;
}

.status-confirmada { background: #d1fae5; color: #065f46; }
.status-pendiente { background: #fee2e2; color: #991b1b; }

@media (max-width: 991.98px) {
  .dashboard-shell {
    grid-template-columns: 1fr;
  }

  .dashboard-sidebar {
    border-right: 0;
    border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  }

  .hero-panel {
    grid-template-columns: 1fr;
  }
}
</style>
