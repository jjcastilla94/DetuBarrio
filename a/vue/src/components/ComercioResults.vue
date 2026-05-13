<script setup>
import ComercioCard from './ComercioCard.vue'

defineProps({
  comercios: {
    type: Array,
    default: () => [],
  },
  searchQuery: {
    type: String,
    default: '',
  },
  horaActual: {
    type: Object,
    default: null,
  },
  formatRating: {
    type: Function,
    required: true,
  },
  isComercioOpen: {
    type: Function,
    required: true,
  },
  totalComercios: {
    type: Number,
    default: 0,
  },
  totalResultados: {
    type: Number,
    default: 0,
  },
})

const emit = defineEmits(['update:searchQuery'])

function updateSearchQuery(event) {
  emit('update:searchQuery', event.target.value)
}
</script>

<template>
  <div class="col-12 col-lg-9">
    <div class="card border-0 shadow-sm rounded-4 p-3 p-lg-4 mb-4 commerce-search-shell">
      <div class="input-group search-bar">
        <span class="input-group-text bg-white border-1 rounded-start-3 border-muted">
          <i class="bi bi-search"></i>
        </span>
        <input
          :value="searchQuery"
          type="search"
          id="searchInput"
          class="form-control border-1 rounded-end-3 border-muted"
          placeholder="Busca un comercio por nombre o palabra clave..."
          aria-label="Buscar comercio"
          @input="updateSearchQuery"
        />
      </div>

      <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mt-3">
        <p class="text-muted mb-0" id="resultsCount">
          Mostrando {{ totalResultados }} de {{ totalComercios }} comercios
        </p>
        <span class="badge rounded-pill text-bg-light border">
          {{ totalResultados }} resultados
        </span>
      </div>
    </div>

    <div v-if="comercios.length" class="row g-4" id="comercioGrid">
      <ComercioCard
        v-for="comercio in comercios"
        :key="comercio.id || comercio.nombreComercio"
        :image-url="comercio.imageUrl"
        :image-alt="comercio.nombreComercio"
        :category="comercio.categoria"
        :is-opened="isComercioOpen(comercio, horaActual)"
        :name="comercio.nombreComercio"
        :star="formatRating(comercio.puntuacionMedia)"
        :opinions="comercio.totalResenas || 0"
        :to="{ name: 'comercio-detalle', params: { id: comercio.id } }"
      />
    </div>

    <div v-else class="card border-0 shadow-sm rounded-4 p-4 p-lg-5 text-center bg-white">
      <div class="mx-auto" style="max-width: 520px;">
        <div class="empty-state-icon mb-3">
          <i class="bi bi-shop fs-2"></i>
        </div>
        <h3 class="h4 fw-bold mb-2">No hay comercios para estos filtros</h3>
        <p class="text-muted mb-4">
          Prueba a cambiar la búsqueda, ajustar la categoría o limpiar los filtros para ver más resultados.
        </p>
        <button class="btn btn-primary rounded-pill px-4" type="button" @click="$emit('update:searchQuery', '')">
          Limpiar búsqueda
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.search-bar .form-control {
  border-color: #e0e0e0;
  box-shadow: none;
}

.search-bar .form-control:focus {
  border-color: var(--db-secondary);
  box-shadow: 0 0 0 0.2rem rgba(58, 134, 255, 0.12);
}

.border-muted {
  border-color: #e0e0e0 !important;
}

.commerce-search-shell {
  background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}

.empty-state-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--db-secondary);
  background: rgba(58, 134, 255, 0.1);
}
</style>