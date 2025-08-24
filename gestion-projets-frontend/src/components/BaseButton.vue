<template>
  <button
    :class="buttonClasses"
    :disabled="disabled || loading"
    @click="handleClick"
    :aria-label="ariaLabel"
  >
    <!-- Icône de chargement -->
    <span v-if="loading" class="loading-spinner"></span>

    <!-- Icône -->
    <i v-if="icon && !loading" :class="`fas fa-${icon} ${hasSlot ? 'me-2' : ''}`"></i>

    <!-- Contenu du slot ou texte par défaut -->
    <slot>
      <span v-if="defaultText">{{ $t(defaultText) }}</span>
    </slot>
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
      validator: (value) => ['primary', 'secondary', 'outline', 'danger', 'success', 'warning', 'info'].includes(value)
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
    },
    icon: {
      type: String,
      default: ''
    },
    defaultText: {
      type: String,
      default: '' // Clé de traduction pour texte par défaut
    },
    ariaLabel: {
      type: String,
      default: ''
    }
  },
  emits: ['click'],
  setup(props, { emit, slots }) {
    const hasSlot = computed(() => {
      return !!slots.default
    })

    const buttonClasses = computed(() => {
      const baseClasses = 'inline-flex items-center justify-center font-medium rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2'

      // Variants avec couleurs F12
      const variantClasses = {
        primary: 'bg-collabpro text-white hover:bg-blue-700 focus:ring-collabpro-blue disabled:bg-gray-400',
        secondary: 'bg-white text-collabpro border border-collabpro hover:bg-collabpro-light focus:ring-collabpro-blue',
        outline: 'bg-transparent text-collabpro border border-collabpro hover:bg-collabpro hover:text-white focus:ring-collabpro-blue',
        danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500 disabled:bg-gray-400',
        success: 'bg-green-600 text-white hover:bg-green-700 focus:ring-green-500 disabled:bg-gray-400',
        warning: 'bg-yellow-500 text-white hover:bg-yellow-600 focus:ring-yellow-400 disabled:bg-gray-400',
        info: 'bg-blue-500 text-white hover:bg-blue-600 focus:ring-blue-400 disabled:bg-gray-400'
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
        : 'cursor-pointer hover:shadow-lg'

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
      handleClick,
      hasSlot
    }
  }
}
</script>

<style scoped>
.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Variables CSS pour cohérence F12 */
:root {
  --collabpro-blue: #007bff;
  --collabpro-light: rgba(0, 123, 255, 0.1);
}

.bg-collabpro {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
}

.text-collabpro {
  color: var(--collabpro-blue);
}

.border-collabpro {
  border-color: var(--collabpro-blue);
}

.bg-collabpro-light {
  background-color: var(--collabpro-light);
}

.focus\:ring-collabpro-blue:focus {
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.3);
}

/* Animations améliorées */
.inline-flex {
  transform-origin: center;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.inline-flex:hover:not(:disabled) {
  transform: translateY(-1px);
}

.inline-flex:active:not(:disabled) {
  transform: translateY(0);
}

/* Responsive */
@media (max-width: 640px) {
  .inline-flex {
    width: 100%;
    justify-content: center;
  }
}
</style>
