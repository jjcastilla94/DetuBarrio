<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import callegestionCliente from '../assets/images/callegestionCliente.png'
import { clearAuth, getAuth } from '../services/authService'
import { fetchUsuarioDashboard } from '../services/dashboardService'

const router = useRouter()
const auth = ref(getAuth())
const loading = ref(false)
const errorMessage = ref('')
const dashboard = ref(null)

const displayName = computed(() => dashboard.value?.nombre || auth.value?.nombre || 'Usuario')
const email = computed(() => dashboard.value?.email || auth.value?.email || '')
const initial = computed(() => displayName.value.trim().charAt(0).toUpperCase() || 'U')

function redirectByRole() {
  const currentAuth = getAuth()

  if (!currentAuth?.token) {
    router.replace({ name: 'login' })
    return false
  }

  if (currentAuth.rol !== 'USUARIO') {
    router.replace(currentAuth.rol === 'COMERCIO' ? { name: 'dashboard-comercio' } : currentAuth.rol === 'ADMIN' ? { name: 'admin' } : { name: 'home' })
    return false
  }

  auth.value = currentAuth
  return true
}

async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''

  try {
    dashboard.value = await fetchUsuarioDashboard()
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || error?.message || 'No se pudo cargar el panel de usuario.'
  } finally {
    loading.value = false
  }
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
          <h6 class="mb-1 fw-bold">{{ displayName }}</h6>
          <small class="text-muted">{{ email }}</small>
        </div>
      </div>

      <nav class="sidebar-nav">
        <a class="active" href="#">
          <i class="bi bi-grid me-2"></i> Panel general
        </a>
        <a href="#">
          <i class="bi bi-calendar-check me-2"></i> Mis reservas
        </a>
        <a href="#">
          <i class="bi bi-bag me-2"></i> Mis pedidos
        </a>
        <a href="#">
          <i class="bi bi-person me-2"></i> Mi perfil
        </a>
        <a href="#">
          <i class="bi bi-gear me-2"></i> Ajustes
        </a>
      </nav>

      <div class="sidebar-actions">
        <RouterLink class="btn btn-light w-100 fw-semibold" to="/comercios">
          <i class="bi bi-shop me-1"></i> Explorar comercios
        </RouterLink>
        <button class="btn btn-outline-danger w-100 fw-semibold" type="button" @click="handleLogout">
          Cerrar sesión
        </button>
      </div>
    </aside>

    <section class="dashboard-content">
      <header class="dashboard-header">
        <div>
          <p class="eyebrow mb-1">Área personal</p>
          <h1 class="h2 fw-bold mb-1">Hola, {{ displayName }}</h1>
          <p class="text-muted mb-0">Bienvenida a tu panel de control personal.</p>
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

        <div class="hero-panel shadow-sm mb-4">
          <div class="hero-panel__text">
            <h2 class="fw-bold mb-2">¿Lista para tu próxima experiencia local?</h2>
            <p class="text-muted mb-4">
              Encuentra y reserva servicios en los comercios de tu barrio de forma rápida y sencilla.
            </p>
            <RouterLink class="btn btn-primary px-4 py-2 fw-semibold" to="/comercios">
              <i class="bi bi-plus-lg me-1"></i> Hacer una nueva reserva
            </RouterLink>
          </div>
          <div class="hero-panel__image">
            <img :src="callegestionCliente" alt="Ilustración de barrio" />
          </div>
        </div>

        <div class="row g-4 mb-4">
          <div class="col-md-4">
            <div class="metric-card shadow-sm">
              <p class="text-muted mb-2">Reservas activas</p>
              <div class="metric-value">3</div>
              <small class="text-success">+1 esta semana</small>
            </div>
          </div>
          <div class="col-md-4">
            <div class="metric-card shadow-sm">
              <p class="text-muted mb-2">Comercios favoritos</p>
              <div class="metric-value">5</div>
              <small class="text-primary">Tus lugares guardados</small>
            </div>
          </div>
          <div class="col-md-4">
            <div class="metric-card shadow-sm">
              <p class="text-muted mb-2">Estado de cuenta</p>
              <div class="metric-value">Activo</div>
              <small class="text-muted">Sesión iniciada correctamente</small>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <div class="col-lg-8">
            <div class="panel-card shadow-sm h-100">
              <div class="panel-card__head">
                <h5 class="fw-bold mb-0">Últimas reservas</h5>
                <span class="text-muted small">Actividad reciente</span>
              </div>
              <div class="table-responsive">
                <table class="table align-middle mb-0">
                  <thead>
                    <tr>
                      <th>Comercio</th>
                      <th>Servicio</th>
                      <th>Fecha</th>
                      <th class="text-end">Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td class="fw-semibold">Peluquería RD</td>
                      <td class="text-muted">Corte de pelo</td>
                      <td>25 Dic, 2025</td>
                      <td class="text-end"><span class="status-badge status-confirmada">Confirmada</span></td>
                    </tr>
                    <tr>
                      <td class="fw-semibold">Fisio Luz</td>
                      <td class="text-muted">Masaje descontracturante</td>
                      <td>27 Oct, 2025</td>
                      <td class="text-end"><span class="status-badge status-cancelada">Cancelada</span></td>
                    </tr>
                    <tr>
                      <td class="fw-semibold">Clínica Dental Sonrisas</td>
                      <td class="text-muted">Limpieza bucal</td>
                      <td>15 Feb, 2025</td>
                      <td class="text-end"><span class="status-badge status-pendiente">Pendiente</span></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div class="col-lg-4">
            <div class="panel-card shadow-sm h-100">
              <div class="panel-card__head">
                <h5 class="fw-bold mb-0">Notificaciones</h5>
                <span class="text-muted small">Recientes</span>
              </div>

              <div class="notifications">
                <div class="notification-item">
                  <div class="notification-icon bg-success-subtle text-success">
                    <i class="bi bi-check-lg"></i>
                  </div>
                  <div>
                    <p class="mb-1 fw-medium">Tu reserva en 'Peluquería RD' ha sido confirmada.</p>
                    <small class="text-muted">Hace 5 minutos</small>
                  </div>
                </div>

                <div class="notification-item">
                  <div class="notification-icon bg-warning-subtle text-warning">
                    <i class="bi bi-tag-fill"></i>
                  </div>
                  <div>
                    <p class="mb-1 fw-medium">¡Nueva oferta! 15% de dto. en 'Frutería Pepe'.</p>
                    <small class="text-muted">Hace 2 horas</small>
                  </div>
                </div>

                <div class="notification-item">
                  <div class="notification-icon bg-light text-primary">
                    <i class="bi bi-info-circle"></i>
                  </div>
                  <div>
                    <p class="mb-1 fw-medium">Completa tu perfil para obtener recomendaciones personalizadas.</p>
                    <small class="text-muted">Hace 1 día</small>
                  </div>
                </div>
              </div>
            </div>
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
  display: flex;
  align-items: center;
  justify-content: center;
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

.panel-card table {
  margin-bottom: 0;
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
.status-cancelada { background: #fee2e2; color: #991b1b; }
.status-pendiente { background: #fef9c3; color: #854d0e; }

.notifications {
  display: grid;
  gap: 1rem;
  padding: 0 1.25rem 1.25rem;
}

.notification-item {
  display: flex;
  gap: 0.9rem;
}

.notification-icon {
  width: 42px;
  height: 42px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

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
