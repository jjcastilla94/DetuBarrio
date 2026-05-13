<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { RouterView, RouterLink } from 'vue-router'
import HomeView from './views/HomeView.vue'
import logoOg from './assets/images/logo_og.png'
import { clearAuth, getAuth } from './services/authService'

const auth = ref(getAuth())

function syncAuth() {
  auth.value = getAuth()
}

const isAdmin = computed(() => auth.value?.rol === 'ADMIN')
const isLoggedIn = computed(() => Boolean(auth.value?.token))
const userInitial = computed(() => (auth.value?.nombre?.trim()?.charAt(0) || 'U').toUpperCase())
const accountRoute = computed(() => '/mi-cuenta')

onMounted(() => {
  window.addEventListener('detubarrio-auth-changed', syncAuth)
})

onUnmounted(() => {
  window.removeEventListener('detubarrio-auth-changed', syncAuth)
})

function handleLogout() {
  clearAuth()
}

</script>

<template>
  <nav class="navbar navbar-expand-lg navbar-custom sticky-top shadow-sm">
      <div class="container-fluid container-xl">
        <!-- Logo DetuBarrio -->
        <RouterLink to="/" class="navbar-brand d-flex align-items-center fw-bold">
          <img
            :src="logoOg"
            alt="Logo"
            class="me-2 rounded-2"
            width="32"
            height="32"
            style="object-fit: cover"
          />
          <span style="color: var(--db-primary)">DetuBarrio</span>
        </RouterLink>

        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <!-- Elementos del menú -->
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-lg-center">
            <li class="nav-item">
              <RouterLink class="nav-link text-primary fw-medium" to="/comercios">Comercios</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link text-secondary fw-medium" to="/about">¿Qué es?</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link text-secondary fw-medium" to="/contacto">Contacto</RouterLink>
            </li>
            <li v-if="isAdmin" class="nav-item">
              <RouterLink class="nav-link text-secondary fw-medium" to="/admin">Admin</RouterLink>
            </li>
            <li v-if="isLoggedIn" class="nav-item ms-lg-2">
              <button class="nav-link icon-link" type="button" aria-label="Favoritos">
                <i class="bi bi-heart"></i>
              </button>
            </li>
            <li v-if="isLoggedIn" class="nav-item dropdown ms-lg-2">
              <button
                class="btn btn-outline-primary user-pill dropdown-toggle d-flex align-items-center gap-2"
                type="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                <span class="user-avatar">{{ userInitial }}</span>
                <span class="d-none d-md-inline">{{ auth?.nombre || 'Usuario' }}</span>
              </button>
              <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0 p-2 user-dropdown">
                <li class="px-3 py-2">
                  <div class="small text-muted text-uppercase">Sesión activa</div>
                  <div class="fw-semibold">{{ auth?.email }}</div>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li><RouterLink class="dropdown-item rounded-3" :to="accountRoute">Mi cuenta</RouterLink></li>
                <li v-if="isAdmin"><RouterLink class="dropdown-item rounded-3" to="/admin">Panel admin</RouterLink></li>
                <li><button class="dropdown-item rounded-3 text-danger" type="button" @click="handleLogout">Cerrar sesión</button></li>
              </ul>
            </li>
          </ul>
          <div v-if="!isLoggedIn" class="d-flex ms-lg-4 gap-2">
            <RouterLink class="btn btn-light px-4 fw-bold" to="/login">Iniciar Sesión</RouterLink>
            <RouterLink class="btn btn-primary px-4 fw-bold" to="/login?tab=register">Registrar</RouterLink>
          </div>
        </div>
      </div>
    </nav>

  <RouterView v-slot="{ Component }">
    <component :is="Component || HomeView" />
  </RouterView>

  <footer class="footer-custom">
      <div class="container">
        <div class="row g-4 justify-content-between">
          <div class="col-lg-4 col-md-12">
            <h5 class="mb-3">DetuBarrio</h5>
            <p class="small text-white-50" style="max-width: 300px">
              Conectando comunidades, un negocio a la vez.
            </p>
          </div>

          <div class="col-lg-2 col-6">
            <h6 class="mb-3">Navegación</h6>
            <ul class="list-unstyled small d-flex flex-column gap-2">
              <li><RouterLink to="/">Inicio</RouterLink></li>
              <li><RouterLink to="/comercios">Comercios</RouterLink></li>
              <li><RouterLink to="/about">¿Qué es?</RouterLink></li>
              <li><RouterLink to="/contacto">Contacto</RouterLink></li>
              <li v-if="isAdmin"><RouterLink to="/admin">Admin</RouterLink></li>
            </ul>
          </div>

          <div class="col-lg-2 col-6">
            <h6 class="mb-3">Legal</h6>
            <ul class="list-unstyled small d-flex flex-column gap-2">
              <li><a href="#">Términos y Condiciones</a></li>
              <li><a href="#">Política de Privacidad</a></li>
              <li><a href="#">FAQ</a></li>
            </ul>
          </div>

          <div class="col-lg-2 col-12">
            <h6 class="mb-3">Síguenos</h6>
            <ul class="list-unstyled small d-flex flex-column gap-2">
              <li><a href="#" target="_blank">FB</a></li>
              <li><a href="https://www.instagram.com/detubarrio_es" target="_blank">Instagram</a></li>
              <li><a href="https://www.tiktok.com/@detubarrio" target="_blank">TikTok</a></li>
            </ul>
          </div>
        </div>

        <div class="footer-divider text-center small text-white-50">
          © 2024 DetuBarrio. Todos los derechos reservados.
        </div>
      </div>
    </footer>
</template>

<style scoped>
.icon-link {
  width: 2.6rem;
  height: 2.6rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(47, 115, 224, 0.24);
  border-radius: 999px;
  color: var(--db-primary);
  background: white;
}

.user-pill {
  border-radius: 999px;
  padding: 0.45rem 0.8rem 0.45rem 0.45rem;
}

.user-avatar {
  width: 1.9rem;
  height: 1.9rem;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--db-primary);
  color: white;
  font-weight: 700;
  font-size: 0.9rem;
}

.user-dropdown {
  min-width: 220px;
}

.dropdown-item:active {
  background-color: rgba(47, 115, 224, 0.12);
  color: var(--db-primary);
}

</style>
