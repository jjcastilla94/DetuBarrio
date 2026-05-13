<script setup>
import { computed } from 'vue'

const props = defineProps({
    state: {
        type: [Boolean, String, Number],
        default: false,
    },
})

const isOpened = computed(() => {
    return props.state === true || props.state === 'true' || props.state === 1 || props.state === '1'
})

</script>

<template>
    <span
        v-if="isOpened"
        class="badge badge-status bg-success text-white rounded-pill position-absolute top-0 end-0 m-3 px-3 py-2"
    >
        Abierto
    </span>
    <span
        v-else
        class="badge badge-status bg-danger text-white rounded-pill position-absolute top-0 end-0 m-3 px-3 py-2"
    >
        Cerrado
    </span>
</template>