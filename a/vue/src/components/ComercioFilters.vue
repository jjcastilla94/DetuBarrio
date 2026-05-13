<script setup>
defineProps({
  categorias: {
    type: Array,
    default: () => [],
  },
  categoriaSeleccionada: {
    type: [Number, String, null],
    default: null,
  },
  valoracionSeleccionada: {
    type: String,
    default: '',
  },
  horarioSeleccionado: {
    type: String,
    default: '',
  },
  valoracionOpciones: {
    type: Array,
    default: () => [],
  },
  horarioOpciones: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits([
  'update:categoriaSeleccionada',
  'update:valoracionSeleccionada',
  'update:horarioSeleccionado',
  'clear',
])

function setCategoria(value) {
  emit('update:categoriaSeleccionada', value)
}

function setValoracion(value) {
  emit('update:valoracionSeleccionada', value)
}

function setHorario(value) {
  emit('update:horarioSeleccionado', value)
}

function limpiarFiltros() {
  emit('clear')
}
</script>

<template>
  <div class="card commerce-filters-panel p-3 p-lg-4 shadow-sm border-0 sticky-top rounded-4" style="top: 20px; z-index: 100;">
    <div class="d-flex align-items-center justify-content-between mb-3">
      <h5 class="fw-bold mb-0">Filtros</h5>
      <span class="badge rounded-pill text-bg-light border">Refina la búsqueda</span>
    </div>

    <button
      class="btn btn-sm btn-outline-secondary d-lg-none mb-3"
      type="button"
      data-bs-toggle="collapse"
      data-bs-target="#filterCollapse"
      aria-expanded="false"
      aria-controls="filterCollapse"
    >
      Mostrar/Ocultar Filtros
    </button>

    <div class="collapse show d-lg-block" id="filterCollapse">
      <div class="accordion accordion-flush" id="accordionCategory">
        <div class="accordion-item">
          <h2 class="accordion-header" id="headingCategory">
            <button
              class="accordion-button fw-medium"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#collapseCategory"
              aria-expanded="true"
              aria-controls="collapseCategory"
            >
              Categoría
            </button>
          </h2>
          <div
            id="collapseCategory"
            class="accordion-collapse collapse show"
            aria-labelledby="headingCategory"
            data-bs-parent="#accordionCategory"
          >
            <div class="accordion-body">
              <div class="form-check mb-2">
                <input
                  class="form-check-input"
                  type="radio"
                  name="categoria"
                  id="categoriaTodas"
                  :checked="categoriaSeleccionada === null"
                  @change="setCategoria(null)"
                />
                <label class="form-check-label" for="categoriaTodas">
                  Todas las categorías
                </label>
              </div>
              <div
                v-for="categoria in categorias"
                :key="categoria.id"
                class="form-check mb-2"
              >
                <input
                  class="form-check-input"
                  type="radio"
                  name="categoria"
                  :id="`categoria-${categoria.id}`"
                  :checked="categoriaSeleccionada === categoria.id"
                  @change="setCategoria(categoria.id)"
                />
                <label class="form-check-label" :for="`categoria-${categoria.id}`">
                  {{ categoria.nombreCategoria }}
                </label>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="accordion accordion-flush mt-3" id="accordionOthers">
        <div class="accordion-item">
          <h2 class="accordion-header">
            <button
              class="accordion-button collapsed fw-medium"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#collapseDistancia"
              aria-expanded="false"
            >
              Distancia
            </button>
          </h2>
          <div
            id="collapseDistancia"
            class="accordion-collapse collapse"
          >
            <div class="accordion-body text-muted small">
              Rango de 1 km a 5 km.
            </div>
          </div>
        </div>
        <div class="accordion-item">
          <h2 class="accordion-header">
            <button
              class="accordion-button collapsed fw-medium"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#collapseValoracion"
              aria-expanded="false"
            >
              Valoración
            </button>
          </h2>
          <div
            id="collapseValoracion"
            class="accordion-collapse collapse"
          >
            <div class="accordion-body">
              <div
                v-for="opcion in valoracionOpciones"
                :key="opcion.value || 'valoracion-todas'"
                class="form-check mb-2"
              >
                <input
                  class="form-check-input"
                  type="radio"
                  name="valoracion"
                  :id="`valoracion-${opcion.value || 'todas'}`"
                  :value="opcion.value"
                  :checked="valoracionSeleccionada === opcion.value"
                  @change="setValoracion(opcion.value)"
                />
                <label
                  class="form-check-label"
                  :for="`valoracion-${opcion.value || 'todas'}`"
                >
                  {{ opcion.label }}
                </label>
              </div>
            </div>
          </div>
        </div>
        <div class="accordion-item">
          <h2 class="accordion-header">
            <button
              class="accordion-button collapsed fw-medium"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#collapseHorario"
              aria-expanded="false"
            >
              Horario
            </button>
          </h2>
          <div id="collapseHorario" class="accordion-collapse collapse">
            <div class="accordion-body">
              <div
                v-for="opcion in horarioOpciones"
                :key="opcion.value || 'horario-todos'"
                class="form-check mb-2"
              >
                <input
                  class="form-check-input"
                  type="radio"
                  name="horario"
                  :id="`horario-${opcion.value || 'todos'}`"
                  :value="opcion.value"
                  :checked="horarioSeleccionado === opcion.value"
                  @change="setHorario(opcion.value)"
                />
                <label
                  class="form-check-label"
                  :for="`horario-${opcion.value || 'todos'}`"
                >
                  {{ opcion.label }}
                </label>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="d-grid gap-2 mt-4">
        <button
          class="btn btn-outline-secondary rounded-3"
          type="button"
          @click="limpiarFiltros"
        >
          Limpiar Filtros
        </button>
      </div>
    </div>
  </div>
</template>