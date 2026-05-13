<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import fondo from '../assets/images/fondo.png';
import buenaMesa from '../assets/images/buenaMesa.png';
import estiloUrbano from '../assets/images/estiloUrbano.png';
import patasFelices from '../assets/images/patasFelices.png';
import fontanero from '../assets/images/fontanero.png';

const router = useRouter()
const searchTerm = ref('')

function buscarComercios() {
  const query = searchTerm.value.trim()

  router.push({
    path: '/comercios',
    query: query ? { q: query } : {},
  })
}

</script>

<template>
    <section class="py-3">
      <div class="container">
        <div
          class="position-relative rounded-5 overflow-hidden text-center text-white"
          style="min-height: 500px"
        >
          <img
            :src="fondo"
            alt="Barrio Fondo"
            class="w-100 h-100 position-absolute top-0 start-0 object-fit-cover"
            style="filter: brightness(0.6); z-index: 1"
          />

          <div
            class="position-relative d-flex flex-column justify-content-center align-items-center h-100 p-4 p-lg-5"
            style="z-index: 2; height: 500px"
          >
            <h1 class="display-4 fw-bolder mb-3 text-white">
              Conecta con tu barrio.<br />
              Encuentra todo lo que <br />
              necesitas, al lado de casa.
            </h1>

            <p class="lead mb-4 fw-normal text-white">
              Apoya el comercio local y descubre los mejores productos y
              servicios cerca de ti.
            </p>

            <div
              class="bg-white p-2 rounded-4 shadow-lg w-100"
              style="max-width: 700px"
            >
              <form class="d-flex w-100" @submit.prevent="buscarComercios">
                <span class="input-group-text bg-white border-0 ps-3">
                  <i class="bi bi-search text-secondary"></i>
                </span>
                <input
                  class="form-control border-0 shadow-none ps-2"
                  type="search"
                  placeholder="Busca comercios o servicios en tu barrio"
                  aria-label="Search"
                  v-model="searchTerm"
                />
                <button
                  class="btn btn-primary px-4 rounded-3 fw-bold"
                  type="submit"
                >
                  Buscar
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="py-5 bg-white">
      <div class="container text-center py-4">
        <h2 class="fw-bold mb-2">¿Qué es DetuBarrio?</h2>
        <p class="text-muted mb-5">
          Somos la plataforma que digitaliza y une a los negocios de tu barrio
          con sus vecinos.
        </p>

        <div class="row g-4">
          <div class="col-md-4">
            <div class="card h-100 border-0 shadow-sm py-4 rounded-4">
              <div class="card-body">
                <div
                  class="bg-light d-inline-block p-3 rounded-circle mb-3 text-primary"
                >
                  <i class="bi bi-heart-fill fs-3"></i>
                </div>
                <h5 class="fw-bold">Apoyo Local</h5>
                <p class="text-muted small px-3">
                  Cada compra que haces ayuda a un emprendedor de tu comunidad.
                </p>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card h-100 border-0 shadow-sm py-4 rounded-4">
              <div class="card-body">
                <div
                  class="bg-light d-inline-block p-3 rounded-circle mb-3 text-primary"
                >
                  <i class="bi bi-map-fill fs-3"></i>
                </div>
                <h5 class="fw-bold">Comodidad</h5>
                <p class="text-muted small px-3">
                  Encuentra lo que necesitas a solo unos pasos de tu casa.
                </p>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card h-100 border-0 shadow-sm py-4 rounded-4">
              <div class="card-body">
                <div
                  class="bg-light d-inline-block p-3 rounded-circle mb-3 text-primary"
                >
                  <i class="bi bi-bag-fill fs-3"></i>
                </div>
                <h5 class="fw-bold">Variedad</h5>
                <p class="text-muted small px-3">
                  Descubre una gran oferta de productos y servicios únicos.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="py-5">
      <div class="container text-center">
        <h3 class="fw-bold mb-5">Explora por Categoría</h3>
        <div class="d-flex flex-wrap justify-content-center gap-5">
          <RouterLink
            to="/comercios"
            class="btn btn-white shadow-sm rounded-4 px-5 py-4 d-flex align-items-center gap-3 fw-bold text-dark border fs-5"
          >
            <i class="bi bi-cup-hot-fill text-primary"></i> Restaurantes
          </RouterLink>
          <RouterLink
            to="/comercios"
            class="btn btn-white shadow-sm rounded-4 px-5 py-4 d-flex align-items-center gap-3 fw-bold text-dark border fs-5"
          >
            <i class="bi bi-shop text-primary"></i> Tiendas
          </RouterLink>
          <RouterLink
            to="/comercios"
            class="btn btn-white shadow-sm rounded-4 px-5 py-4 d-flex align-items-center gap-3 fw-bold text-dark border fs-5"
          >
            <i class="bi bi-flower1 text-primary"></i> Salud y Belleza
          </RouterLink>
          <RouterLink
            to="/comercios"
            class="btn btn-white shadow-sm rounded-4 px-5 py-4 d-flex align-items-center gap-3 fw-bold text-dark border fs-5"
          >
            <i class="bi bi-house-door-fill text-primary"></i> Hogar
          </RouterLink>
        </div>
      </div>
    </section>

    <section class="py-5 bg-light">
      <div class="container">
        <h3 class="fw-bold text-center mb-5">Nuestros Comercios Destacados</h3>
        <div class="row g-4">
          <div class="col-md-6 col-lg-3">
            <div
              class="card h-100 border-0 shadow-sm rounded-4 overflow-hidden"
            >
              <img
                :src="buenaMesa"
                class="card-img-top"
                alt="Restaurante"
                style="height: 150px; object-fit: cover"
              />
              <div class="card-body">
                <h6 class="fw-bold mb-0">La Buena Mesa</h6>
                <small class="text-muted">Restaurante</small>
                <div class="text-warning small mt-2">
                  <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i
                  ><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i
                  ><i class="bi bi-star-half"></i>
                  <span class="text-muted">(124)</span>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-6 col-lg-3">
            <div
              class="card h-100 border-0 shadow-sm rounded-4 overflow-hidden"
            >
              <img
                :src="estiloUrbano"
                class="card-img-top"
                alt="Peluqueria"
                style="height: 150px; object-fit: cover"
              />
              <div class="card-body">
                <h6 class="fw-bold mb-0">Estilo Urbano</h6>
                <small class="text-muted">Peluquería</small>
                <div class="text-warning small mt-2">
                  <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i
                  ><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i
                  ><i class="bi bi-star"></i>
                  <span class="text-muted">(89)</span>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-6 col-lg-3">
            <div
              class="card h-100 border-0 shadow-sm rounded-4 overflow-hidden"
            >
              <img
                :src="fontanero"
                class="card-img-top"
                alt="Fontaneria"
                style="height: 150px; object-fit: cover"
              />
              <div class="card-body">
                <h6 class="fw-bold mb-0">ReparaTodo</h6>
                <small class="text-muted">Fontanería</small>
                <div class="text-warning small mt-2">
                  <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i
                  ><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i
                  ><i class="bi bi-star-fill"></i>
                  <span class="text-muted">(150)</span>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-6 col-lg-3">
            <div
              class="card h-100 border-0 shadow-sm rounded-4 overflow-hidden"
            >
              <img
                :src="patasFelices"
                class="card-img-top"
                alt="Mascotas"
                style="height: 150px; object-fit: cover"
              />
              <div class="card-body">
                <h6 class="fw-bold mb-0">Patitas Felices</h6>
                <small class="text-muted">Mascotas</small>
                <div class="text-warning small mt-2">
                  <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i
                  ><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i
                  ><i class="bi bi-star-half"></i>
                  <span class="text-muted">(210)</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="py-5">
      <div class="container">
        <div class="row g-4">
          <div class="col-md-6">
            <div
              class="p-5 bg-info-subtle rounded-4 h-100 d-flex flex-column justify-content-center align-items-start"
            >
              <h3 class="fw-bold">Descubre lo mejor de tu barrio</h3>
              <p class="mb-4">
                Encuentra comercios únicos, servicios de confianza y ofertas
                especiales cerca de ti.
              </p>
              <button class="btn btn-primary">Explorar Comercios</button>
            </div>
          </div>
          <div class="col-md-6">
            <div
              class="p-5 bg-dark text-white rounded-4 h-100 d-flex flex-column justify-content-center align-items-start"
            >
              <h3 class="fw-bold text-white">¿Tienes un negocio?</h3>
              <p class="mb-4 text-white">
                Digitaliza tu comercio, llega a más vecinos y haz crecer tus
                ventas.
              </p>
              <button class="btn btn-light fw-bold text-dark">
                Regístrate Ahora
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
</template>

<style scoped>
  :root {
        --db-primary: #003366; /* Azul oscuro */
        --db-secondary: #3a86ff; /* Azul brillante para botones */
        --db-success: #28a745; /* Verde para 'Abierto' */
        --db-danger: #dc3545; /* Rojo para 'Cerrado' */
      }
</style>
