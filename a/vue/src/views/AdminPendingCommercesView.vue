<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getAuth } from '../services/authService'
import { fetchAdminSolicitudesComercios, aprobarComercio, rechazarComercio } from '../services/adminService'

const router = useRouter()
const auth = computed(() => getAuth())
const loading = ref(true)
const comerciosPendientes = ref([])
const errorMessage = ref('')
const selectedComercio = ref(null)
const motivoRechazo = ref('')
const isProcessing = ref(false)
const successMessage = ref('')

function checkAuth() {
  const currentAuth = getAuth()
  if (!currentAuth?.token || currentAuth.rol !== 'ADMIN') {
    router.replace({ name: 'login' })
    return false
  }
  return true
}

async function loadComerciosPendientes() {
  loading.value = true
  errorMessage.value = ''

  try {
    comerciosPendientes.value = await fetchAdminSolicitudesComercios()
  } catch (error) {
    errorMessage.value = error?.message || 'No se pudo cargar las solicitudes de comercios'
  } finally {
    loading.value = false
  }
}

async function handleAprobar(comercio) {
  if (!confirm(`¿Aprobar el comercio "${comercio.nombreComercio}"?`)) {
    return
  }

  isProcessing.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    await aprobarComercio(comercio.id)
    successMessage.value = `Comercio "${comercio.nombreComercio}" aprobado exitosamente`
    await loadComerciosPendientes()
  } catch (error) {
    errorMessage.value = error?.message || 'No se pudo aprobar el comercio'
  } finally {
    isProcessing.value = false
  }
}

async function handleRechazar(comercio) {
  selectedComercio.value = comercio
  motivoRechazo.value = ''
}

async function confirmarRechazo() {
  if (!motivoRechazo.value.trim()) {
    errorMessage.value = 'Debes ingresar un motivo de rechazo'
    return
  }

  isProcessing.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    await rechazarComercio(selectedComercio.value.id, motivoRechazo.value.trim())
    successMessage.value = `Comercio "${selectedComercio.value.nombreComercio}" rechazado`
    selectedComercio.value = null
    motivoRechazo.value = ''
    await loadComerciosPendientes()
  } catch (error) {
    errorMessage.value = error?.message || 'No se pudo rechazar el comercio'
  } finally {
    isProcessing.value = false
  }
}

onMounted(() => {
  if (!checkAuth()) {
    return
  }
  loadComerciosPendientes()
})
</script>

<template>
  <main class="container-fluid py-5">
    <div class="row mb-4">
      <div class="col">
        <h1 class="fw-bold">Solicitudes de Comercios Pendientes</h1>
        <p class="text-muted">Revisa y aprueba o rechaza las solicitudes de nuevos comercios</p>
      </div>
    </div>

    <div v-if="errorMessage" class="alert alert-danger border-0 mb-4">{{ errorMessage }}</div>
    <div v-if="successMessage" class="alert alert-success border-0 mb-4">{{ successMessage }}</div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-3 text-muted">Cargando solicitudes...</p>
    </div>

    <div v-else-if="comerciosPendientes.length === 0" class="alert alert-info border-0">
      <i class="bi bi-info-circle me-2"></i> No hay comercios pendientes de aprobación.
    </div>

    <div v-else class="row g-4">
      <div v-for="comercio in comerciosPendientes" :key="comercio.id" class="col-md-6 col-lg-4">
        <div class="card h-100 shadow-sm border-0">
          <div class="card-header bg-light border-bottom">
            <h5 class="card-title mb-0">{{ comercio.nombreComercio }}</h5>
          </div>
          <div class="card-body">
            <p class="card-text text-muted small mb-2">{{ comercio.descripcion }}</p>
            
            <div class="info-group mb-3">
              <strong>Solicitante:</strong> {{ comercio.nombreUsuario }}
            </div>
            <div class="info-group mb-3">
              <strong>Email:</strong> <a :href="`mailto:${comercio.emailUsuario}`">{{ comercio.emailUsuario }}</a>
            </div>
            <div class="info-group mb-3">
              <strong>Categoría:</strong> {{ comercio.categoria }}
            </div>
            <div class="info-group">
              <strong>Fecha de solicitud:</strong> 
              <br>
              <small>{{ new Date(comercio.fechaSolicitud).toLocaleDateString('es-ES') }}</small>
            </div>
          </div>
          <div class="card-footer bg-light border-top d-flex gap-2">
            <button 
              class="btn btn-success btn-sm flex-grow-1" 
              @click="handleAprobar(comercio)"
              :disabled="isProcessing"
            >
              <i class="bi bi-check-circle me-1"></i> Aprobar
            </button>
            <button 
              class="btn btn-danger btn-sm flex-grow-1" 
              @click="handleRechazar(comercio)"
              :disabled="isProcessing"
            >
              <i class="bi bi-x-circle me-1"></i> Rechazar
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de rechazo -->
    <div v-if="selectedComercio" class="modal d-block bg-dark bg-opacity-50" style="display: block !important;">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header border-bottom">
            <h5 class="modal-title fw-bold">Rechazar comercio</h5>
            <button type="button" class="btn-close" @click="selectedComercio = null"></button>
          </div>
          <div class="modal-body">
            <p class="mb-3">¿Está seguro de que desea rechazar el comercio <strong>"{{ selectedComercio.nombreComercio }}"</strong>?</p>
            <label class="form-label fw-bold">Motivo del rechazo:</label>
            <textarea 
              v-model="motivoRechazo" 
              class="form-control" 
              rows="3" 
              placeholder="Explica el motivo del rechazo..."
            ></textarea>
          </div>
          <div class="modal-footer border-top">
            <button type="button" class="btn btn-secondary" @click="selectedComercio = null">Cancelar</button>
            <button 
              type="button" 
              class="btn btn-danger" 
              @click="confirmarRechazo"
              :disabled="isProcessing"
            >
              {{ isProcessing ? 'Rechazando...' : 'Confirmar rechazo' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.info-group {
  padding: 0.5rem 0;
  border-bottom: 1px solid #eee;
}

.info-group:last-child {
  border-bottom: none;
}
</style>
