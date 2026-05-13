<script setup>
import { RouterLink } from 'vue-router'
import OpenedBadge from './OpenedBadge.vue'

defineProps({
  imageUrl: {
    type: String,
    default: '/images/logo_og.png',
  },
  imageAlt: {
    type: String,
    default: '',
  },
  category: {
    type: String,
    default: '',
  },
  isOpened: {
    type: [Boolean, String, Number],
    default: false,
  },
  name: {
    type: String,
    default: '',
  },
  star: {
    type: [String, Number],
    default: 0,
  },
  opinions: {
    type: [String, Number],
    default: 0,
  },
  to: {
    type: [String, Object],
    default: '#',
  },
})
</script>

<template>
  <div class="col-12 col-sm-6 col-md-4 col-xl-3">
    <div class="card commerce-card border-0 shadow-sm h-100 overflow-hidden rounded-4">
      <div class="position-relative">
        <img
          :src="imageUrl || '/images/logo_og.png'"
          class="card-img-top commerce-card-image"
          :alt="imageAlt"
        />
        <OpenedBadge :state="isOpened" />
      </div>

      <div class="card-body d-flex flex-column flex-grow-1">
        <p class="card-text text-muted mb-1 commerce-card-category">
          {{ category }}
        </p>
        <h5 class="card-title fw-bold mb-2 commerce-card-title">{{ name }}</h5>
        <div class="d-flex align-items-center mb-2">
          <i class="bi bi-star-fill text-warning me-1"></i>
          <span class="fw-medium commerce-card-rating">{{ star }}</span>
          <span class="text-muted ms-1 commerce-card-opinions">({{ opinions }})</span>
        </div>
        <div class="d-grid mt-auto">
          <RouterLink
            :to="to"
            class="btn btn-sm btn-primary rounded-3 comercio-card-btn"
          >
            Ver Detalles
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.commerce-card-image {
  height: 172px;
  object-fit: cover;
}

.commerce-card-category {
  font-size: 0.85rem;
}

.commerce-card-title {
  min-height: 2.5rem;
}

.commerce-card-rating {
  font-size: 0.95rem;
}

.commerce-card-opinions {
  font-size: 0.8rem;
}

.comercio-card-btn {
  background-color: var(--db-secondary);
  border-color: var(--db-secondary);
  color: #fff;
  text-decoration: none;
}

.comercio-card-btn:hover,
.comercio-card-btn:focus {
  background-color: #2f73e0;
  border-color: #2f73e0;
  color: #fff;
}
</style>