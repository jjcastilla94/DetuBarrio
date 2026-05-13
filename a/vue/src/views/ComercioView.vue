<script setup>
import ComercioFilters from '../components/ComercioFilters.vue'
import ComercioResults from '../components/ComercioResults.vue'
import { useComercioList } from '../composables/useComercioList'

const {
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
} = useComercioList()
</script>

<template>
  <div class="comercios-page py-4 py-lg-5">
    <div class="container container-xl">
      <div class="page-intro mb-4 mb-lg-5">
        <p class="text-uppercase text-secondary fw-semibold mb-2 small letter-spacing">
          Directorio local
        </p>
        <h1 class="display-6 fw-bold mb-2">
          Descubre los Comercios de tu Barrio
        </h1>
        <p class="lead text-muted mb-0">
          Encuentra todo lo que necesitas a la vuelta de la esquina.
        </p>
      </div>

      <div v-if="errorMessage" class="alert alert-warning border-0 shadow-sm mb-4" role="alert">
        {{ errorMessage }}
      </div>

      <div v-if="isLoading" class="d-flex justify-content-center py-5">
        <div class="text-center">
          <div class="spinner-border text-primary" role="status" aria-hidden="true"></div>
          <p class="mt-3 mb-0 text-muted">Cargando comercios...</p>
        </div>
      </div>

      <div v-else class="row g-4 align-items-start">
        <div class="col-12 col-lg-3">
          <ComercioFilters
            :categorias="categorias"
            :categoria-seleccionada="categoriaSeleccionada"
            :valoracion-seleccionada="valoracionSeleccionada"
            :horario-seleccionado="horarioSeleccionado"
            :valoracion-opciones="valoracionOpciones"
            :horario-opciones="horarioOpciones"
            @update:categoria-seleccionada="categoriaSeleccionada = $event"
            @update:valoracion-seleccionada="valoracionSeleccionada = $event"
            @update:horario-seleccionado="horarioSeleccionado = $event"
            @clear="limpiarFiltros"
          />
        </div>

        <ComercioResults
          :comercios="comerciosFiltrados"
          :search-query="searchQuery"
          :hora-actual="horaActual"
          :format-rating="formatRating"
          :is-comercio-open="isComercioOpen"
          :total-comercios="totalComercios"
          :total-resultados="totalResultados"
          @update:search-query="searchQuery = $event"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.comercios-page {
  background:
    radial-gradient(circle at top left, rgba(58, 134, 255, 0.08), transparent 30%),
    linear-gradient(180deg, #fbfdff 0%, #ffffff 55%);
}

.page-intro {
  max-width: 760px;
}

.letter-spacing {
  letter-spacing: 0.12em;
}
</style>