<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import logoOg from '../assets/images/logo_og.png'
import { getAuth, login } from '../services/authService'

const router = useRouter()
const route = useRoute()

const loginForm = ref({ email: '', password: '' })
const registerForm = ref({
  nombre: '',
  email: '',
  password: '',
  rol: 'USUARIO',
  comercioId: '',
  nombreComercio: '',
  descripcionComercio: '',
  categoriaId: '',
})
const categorias = ref([])
const activeTab = ref('login')
const crearNuevoComercio = ref(false)
const loading = ref(false)
const registerLoading = ref(false)
const errorMessage = ref('')
const registerErrorMessage = ref('')
const successMessage = ref('')

const demoCredentials = computed(() => 'admin@detubarrio.local / admin123')

function syncTabFromRoute() {
  activeTab.value = route.query.tab === 'register' ? 'register' : 'login'
}

function redirectByRole(auth) {
  router.replace(auth.rol === 'ADMIN' ? { name: 'admin' } : { name: 'home' })
}

async function handleLogin() {
  loading.value = true
  errorMessage.value = ''

  try {
    const auth = await login(loginForm.value.email.trim(), loginForm.value.password)
    redirectByRole(auth)
  } catch (error) {
    errorMessage.value = error?.response?.data?.details?.[0] || error?.message || 'No se pudo iniciar sesión'
  } finally {
    loading.value = false
  }
}

async function fetchCategorias() {
  try {
    const response = await fetch('/api/categorias')
    if (response.ok) {
      categorias.value = await response.json()
    }
  } catch (error) {
    console.error('Error fetching categories:', error)
  }
}

async function handleRegister() {
  registerLoading.value = true
  registerErrorMessage.value = ''
  successMessage.value = ''

  try {
    const payload = {
      nombre: registerForm.value.nombre.trim(),
      email: registerForm.value.email.trim(),
      password: registerForm.value.password,
      rol: registerForm.value.rol,
      comercioId: registerForm.value.comercioId ? Number(registerForm.value.comercioId) : null,
    }

    // Si es COMERCIO y quiere crear uno nuevo
    if (registerForm.value.rol === 'COMERCIO' && crearNuevoComercio.value) {
      if (!registerForm.value.nombreComercio.trim()) {
        throw new Error('El nombre del comercio es requerido')
      }
      if (!registerForm.value.descripcionComercio.trim()) {
        throw new Error('La descripción del comercio es requerida')
      }
      if (!registerForm.value.categoriaId) {
        throw new Error('La categoría es requerida')
      }
      
      payload.nombreComercio = registerForm.value.nombreComercio.trim()
      payload.descripcionComercio = registerForm.value.descripcionComercio.trim()
      payload.categoriaId = Number(registerForm.value.categoriaId)
    }

    const response = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })

    if (!response.ok) {
      const body = await response.json().catch(() => null)
      throw new Error(body?.details?.[0] || body?.error || 'No se pudo completar el registro')
    }

    const auth = await response.json()
    successMessage.value = crearNuevoComercio.value 
      ? 'Registro completado. Tu comercio está pendiente de aprobación. Redirigiendo...'
      : 'Registro completado. Redirigiendo...'
    setTimeout(() => redirectByRole(auth), 400)
  } catch (error) {
    registerErrorMessage.value = error?.message || 'No se pudo completar el registro'
  } finally {
    registerLoading.value = false
  }
}

onMounted(() => {
  const currentAuth = getAuth()
  if (currentAuth?.token) {
    redirectByRole(currentAuth)
    return
  }

  syncTabFromRoute()
  fetchCategorias()
})
</script>

<template>
  <main class="login-shell">
    <section class="login-hero d-none d-lg-flex">
      <div class="login-hero__content">
        <img :src="logoOg" alt="Logo DetuBarrio" class="login-logo mb-4" />
        <span class="eyebrow">DetuBarrio</span>
        <h1>Accede a tu espacio con una experiencia clara y profesional.</h1>
        <p>
          Inicia sesión para entrar al panel de tu perfil o continuar para gestionar tu propio comercio.
        </p>
      </div>
    </section>

    <section class="login-form-panel">
      <div class="login-card shadow-lg">
        <div class="d-flex align-items-center justify-content-between mb-4">
          <div>
            <p class="text-uppercase text-muted small mb-1">Acceso seguro</p>
            <h2 class="h3 fw-bold mb-0">Bienvenido de nuevo</h2>
          </div>
          <RouterLink class="btn btn-outline-secondary btn-sm" to="/">Volver</RouterLink>
        </div>

        <ul class="nav nav-tabs mb-4">
          <li class="nav-item">
            <button class="nav-link" :class="{ active: activeTab === 'login' }" type="button" @click="activeTab = 'login'">
              Iniciar sesión
            </button>
          </li>
          <li class="nav-item">
            <button class="nav-link" :class="{ active: activeTab === 'register' }" type="button" @click="activeTab = 'register'">
              Registrarse
            </button>
          </li>
        </ul>

        <div v-if="activeTab === 'login'">
          <p class="text-muted mb-4">Introduce tus credenciales para acceder.</p>

          <div v-if="errorMessage" class="alert alert-danger border-0">{{ errorMessage }}</div>

          <form @submit.prevent="handleLogin">
            <div class="mb-3">
              <label class="form-label">Email</label>
              <input v-model="loginForm.email" type="email" class="form-control form-control-lg" placeholder="tu.email@ejemplo.com" required>
            </div>

            <div class="mb-3">
              <label class="form-label">Contraseña</label>
              <input v-model="loginForm.password" type="password" class="form-control form-control-lg" placeholder="Introduce tu contraseña" required>
            </div>

            <button class="btn btn-primary w-100 btn-lg fw-bold" type="submit" :disabled="loading">
              {{ loading ? 'Entrando...' : 'Iniciar sesión' }}
            </button>
          </form>

          <div class="login-note mt-4">
            <strong>Usuario demo admin:</strong> {{ demoCredentials }}
          </div>
        </div>

        <div v-else>
          <p class="text-muted mb-4">Crea tu cuenta y empieza a usar la plataforma.</p>

          <div v-if="registerErrorMessage" class="alert alert-danger border-0">{{ registerErrorMessage }}</div>
          <div v-if="successMessage" class="alert alert-success border-0">{{ successMessage }}</div>

          <form @submit.prevent="handleRegister">
            <div class="mb-3">
              <label class="form-label">Nombre</label>
              <input v-model="registerForm.nombre" type="text" class="form-control form-control-lg" placeholder="Tu nombre completo" required>
            </div>

            <div class="mb-3">
              <label class="form-label">Email</label>
              <input v-model="registerForm.email" type="email" class="form-control form-control-lg" placeholder="tu.email@ejemplo.com" required>
            </div>

            <div class="mb-3">
              <label class="form-label">Contraseña</label>
              <input v-model="registerForm.password" type="password" class="form-control form-control-lg" placeholder="Crea una contraseña" required>
            </div>

            <div class="mb-3">
              <label class="form-label">Tipo de cuenta</label>
              <select v-model="registerForm.rol" class="form-select form-select-lg" required>
                <option value="USUARIO">Usuario</option>
                <option value="COMERCIO">Comercio</option>
              </select>
            </div>

            <div v-if="registerForm.rol === 'COMERCIO'" class="card bg-light mb-3">
              <div class="card-body">
                <div class="mb-3">
                  <div class="form-check">
                    <input v-model="crearNuevoComercio" type="checkbox" class="form-check-input" id="crearComercio">
                    <label class="form-check-label" for="crearComercio">
                      Crear nuevo comercio (será validado por un administrador)
                    </label>
                  </div>
                </div>

                <div v-if="!crearNuevoComercio" class="mb-3">
                  <label class="form-label">ID de comercio existente (opcional)</label>
                  <input v-model="registerForm.comercioId" type="number" min="1" class="form-control form-control-lg" placeholder="Ej: 2">
                </div>

                <div v-else>
                  <div class="alert alert-info border-0 mb-3">
                    <p class="mb-0 small">
                      <strong>Información importante:</strong> Tu comercio será revisado por un administrador. Solo necesitamos los datos básicos de tu negocio.
                    </p>
                  </div>

                  <div class="mb-3">
                    <label class="form-label">Nombre del comercio *</label>
                    <input v-model="registerForm.nombreComercio" type="text" class="form-control form-control-lg" placeholder="Ej: Mi Tienda" required>
                  </div>

                  <div class="mb-3">
                    <label class="form-label">Descripción *</label>
                    <textarea v-model="registerForm.descripcionComercio" class="form-control form-control-lg" rows="2" placeholder="Describe brevemente tu comercio" required></textarea>
                  </div>

                  <div class="mb-3">
                    <label class="form-label">Categoría *</label>
                    <select v-model="registerForm.categoriaId" class="form-select form-select-lg" required>
                      <option value="">Selecciona una categoría</option>
                      <option v-for="cat in categorias" :key="cat.id" :value="cat.id">
                        {{ cat.nombreCategoria }}
                      </option>
                    </select>
                  </div>
                </div>
              </div>
            </div>

            <button class="btn btn-outline-primary w-100 btn-lg fw-bold" type="submit" :disabled="registerLoading">
              {{ registerLoading ? 'Registrando...' : 'Crear cuenta' }}
            </button>
          </form>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.login-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.15fr 1fr;
  background:
    radial-gradient(circle at top left, rgba(47, 115, 224, 0.22), transparent 30%),
    linear-gradient(180deg, #f6f9fe 0%, #eef3fb 100%);
}

.login-hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  color: white;
  background:
    linear-gradient(110deg, rgba(6, 23, 50, 0.97) 0%, rgba(12, 39, 78, 0.92) 42%, rgba(27, 79, 153, 0.84) 100%),
    url('../assets/images/fondo_login.png');
  background-size: cover;
  background-position: center right;
  overflow: hidden;
}

.login-hero::before {
  content: '';
  position: absolute;
  inset: 1.75rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 2rem;
  pointer-events: none;
}

.login-hero__content {
  position: relative;
  max-width: 30rem;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.login-logo {
  width: 92px;
  height: 92px;
  object-fit: cover;
  border-radius: 1.25rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.24);
}

.eyebrow {
  display: inline-block;
  margin-bottom: 1rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.72);
}

.login-hero h1 {
  font-size: clamp(2.4rem, 4vw, 4.4rem);
  line-height: 0.98;
  margin-bottom: 1rem;
  font-weight: 800;
}

.login-hero p {
  max-width: 28rem;
  font-size: 1.05rem;
  color: rgba(255, 255, 255, 0.82);
}

.login-form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.login-card {
  width: 100%;
  max-width: 520px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 1.75rem;
  padding: 2rem;
}

.login-note {
  border-radius: 1rem;
  padding: 0.9rem 1rem;
  background: #f8fbff;
  border: 1px solid #dbe6f7;
  color: #16355f;
}

/* Forzar color blanco en el texto del hero de login */
.login-hero__content,
.login-hero__content h1,
.login-hero__content p,
.login-hero__content .eyebrow {
  color: #ffffff !important;
}

@media (max-width: 991.98px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-form-panel {
    min-height: 100vh;
  }
}
</style>