<!-- BaseButton.vue -->
<template>
  <button
    :class="buttonClasses"
    :disabled="disabled"
    @click="handleClick"
  >
    <slot />
  </button>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'BaseButton',
  props: {
    variant: {
      type: String,
      default: 'primary',
      validator: (value) => ['primary', 'secondary', 'outline', 'danger', 'success'].includes(value)
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    disabled: {
      type: Boolean,
      default: false
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['click'],
  setup(props, { emit }) {
    const buttonClasses = computed(() => {
      const baseClasses = 'inline-flex items-center justify-center font-medium rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2'

      // Variants
      const variantClasses = {
        primary: 'bg-collabpro text-white hover:bg-blue-700 focus:ring-collabpro-blue disabled:bg-gray-400',
        secondary: 'bg-white text-collabpro border border-collabpro hover:bg-collabpro-light focus:ring-collabpro-blue',
        outline: 'bg-transparent text-collabpro border border-collabpro hover:bg-collabpro hover:text-white focus:ring-collabpro-blue',
        danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500 disabled:bg-gray-400',
        success: 'bg-green-600 text-white hover:bg-green-700 focus:ring-green-500 disabled:bg-gray-400'
      }

      // Sizes
      const sizeClasses = {
        small: 'px-3 py-1.5 text-sm',
        medium: 'px-4 py-2 text-base',
        large: 'px-6 py-3 text-lg'
      }

      // States
      const stateClasses = props.disabled || props.loading
        ? 'cursor-not-allowed opacity-50'
        : 'cursor-pointer'

      return [
        baseClasses,
        variantClasses[props.variant],
        sizeClasses[props.size],
        stateClasses
      ].join(' ')
    })

    const handleClick = (event) => {
      if (!props.disabled && !props.loading) {
        emit('click', event)
      }
    }

    return {
      buttonClasses,
      handleClick
    }
  }
}
</script>
