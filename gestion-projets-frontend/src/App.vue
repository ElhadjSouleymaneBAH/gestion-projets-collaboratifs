<template>
  <div id="app">
    <!-- Global loading overlay -->
    <div v-if="globalLoading" class="global-loading">
      <div class="loading-spinner">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Chargement...</span>
        </div>
        <p class="mt-2">Chargement en cours...</p>
      </div>
    </div>

    <!-- Error boundary -->
    <div v-if="globalError" class="global-error">
      <div class="container">
        <div class="alert alert-danger">
          <h4><i class="fas fa-exclamation-triangle me-2"></i>Erreur Système</h4>
          <p>{{ globalError }}</p>
          <button @click="reloadApp" class="btn btn-danger">
            <i class="fas fa-redo me-2"></i>Recharger l'application
          </button>
        </div>
      </div>
    </div>

    <!-- Main application -->
    <div v-else class="app-container">
      <!-- Global notifications -->
      <div class="global-notifications">
        <div v-for="notification in globalNotifications" :key="notification.id"
             :class="['alert', `alert-${notification.type}`, 'notification-toast']">
          <button @click="removeNotification(notification.id)" class="btn-close"></button>
          {{ notification.message }}
        </div>
      </div>

      <!-- Router view with transitions -->
      <router-view v-slot="{ Component, route }">
        <transition :name="getTransitionName(route)" mode="out-in">
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onErrorCaptured } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const route = useRoute()
    const router = useRouter()

    // Global state
    const globalLoading = ref(false)
    const globalError = ref(null)
    const globalNotifications = ref([])

    // Development mode
    const isDevelopment = computed(() => {
      return import.meta.env.DEV
    })

    // Lifecycle
    onMounted(() => {
      initializeApp()
      setupGlobalErrorHandling()
      setupPerformanceMonitoring()
    })

    // Error handling
    onErrorCaptured((error, instance, info) => {
      console.error('Vue Error Captured:', error, info)
      globalError.value = `Erreur Vue: ${error.message}`
      return false
    })

    // Methods
    const initializeApp = async () => {
      try {
        globalLoading.value = true

        // Simple check for existing auth
        const token = localStorage.getItem('token')
        const user = localStorage.getItem('user')

        if (token && user) {
          console.log('Utilisateur déjà connecté:', JSON.parse(user).email)
        }

        // Initialize analytics (production only)
        if (!isDevelopment.value) {
          initializeAnalytics()
        }

        // Set app metadata
        updateDocumentTitle()
        updateDocumentMeta()

      } catch (error) {
        console.error('App initialization error:', error)
        globalError.value = 'Erreur lors de l\'initialisation de l\'application'
      } finally {
        globalLoading.value = false
      }
    }

    const setupGlobalErrorHandling = () => {
      // Catch unhandled promise rejections
      window.addEventListener('unhandledrejection', (event) => {
        console.error('Unhandled promise rejection:', event.reason)
        addNotification('Erreur système détectée', 'danger')
        event.preventDefault()
      })

      // Catch JavaScript errors
      window.addEventListener('error', (event) => {
        console.error('JavaScript error:', event.error)
        addNotification('Erreur JavaScript détectée', 'warning')
      })
    }

    const setupPerformanceMonitoring = () => {
      // Monitor performance (production only)
      if (!isDevelopment.value && 'performance' in window) {
        window.addEventListener('load', () => {
          setTimeout(() => {
            const perfData = performance.getEntriesByType('navigation')[0]
            console.log('App Load Performance:', {
              loadTime: perfData.loadEventEnd - perfData.loadEventStart,
              domContentLoaded: perfData.domContentLoadedEventEnd - perfData.domContentLoadedEventStart,
              totalTime: perfData.loadEventEnd - perfData.fetchStart
            })
          }, 0)
        })
      }
    }

    const initializeAnalytics = () => {
      console.log('Analytics initialized for production')
    }

    const updateDocumentTitle = () => {
      const baseTitle = 'CollabPro - Gestion de Projets Collaboratifs'
      document.title = baseTitle

      router.afterEach((to) => {
        const routeTitle = to.meta?.title || to.name
        document.title = routeTitle ? `${routeTitle} | CollabPro` : baseTitle
      })
    }

    const updateDocumentMeta = () => {
      const metaDescription = document.querySelector('meta[name="description"]')
      if (metaDescription) {
        metaDescription.setAttribute('content', 'Plateforme intuitive de gestion de projets collaboratifs pour PME et freelances avec collaboration temps réel')
      }

      const metaViewport = document.querySelector('meta[name="viewport"]')
      if (!metaViewport) {
        const viewport = document.createElement('meta')
        viewport.name = 'viewport'
        viewport.content = 'width=device-width, initial-scale=1.0'
        document.getElementsByTagName('head')[0].appendChild(viewport)
      }
    }

    const addNotification = (message, type = 'info', duration = 5000) => {
      const notification = {
        id: Date.now(),
        message,
        type
      }

      globalNotifications.value.push(notification)

      setTimeout(() => {
        removeNotification(notification.id)
      }, duration)
    }

    const removeNotification = (id) => {
      const index = globalNotifications.value.findIndex(n => n.id === id)
      if (index > -1) {
        globalNotifications.value.splice(index, 1)
      }
    }

    const getTransitionName = (route) => {
      if (route.meta?.transition) {
        return route.meta.transition
      }

      if (route.name?.includes('admin')) {
        return 'slide-left'
      }

      if (route.name?.includes('tableau')) {
        return 'slide-up'
      }

      return 'fade'
    }

    const reloadApp = () => {
      globalError.value = null
      window.location.reload()
    }

    // Expose global notification method
    window.addGlobalNotification = addNotification

    return {
      globalLoading,
      globalError,
      globalNotifications,
      addNotification,
      removeNotification,
      getTransitionName,
      reloadApp
    }
  }
}
</script>

<style>
/* Global Styles */
#app {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  position: relative;
}

body {
  margin: 0;
  padding: 0;
  background: #f8f9fa;
}

/* Global Loading */
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-spinner {
  text-align: center;
}

/* Global Error */
.global-error {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}

/* Global Notifications */
.global-notifications {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1060;
  max-width: 400px;
}

.notification-toast {
  margin-bottom: 10px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  animation: slideInRight 0.3s ease-out;
  position: relative;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* Page Transitions */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.slide-left-enter-active, .slide-left-leave-active {
  transition: transform 0.3s ease;
}

.slide-left-enter-from {
  transform: translateX(100%);
}

.slide-left-leave-to {
  transform: translateX(-100%);
}

.slide-up-enter-active, .slide-up-leave-active {
  transition: transform 0.3s ease;
}

.slide-up-enter-from {
  transform: translateY(100%);
}

.slide-up-leave-to {
  transform: translateY(-100%);
}

/* Utility Classes */
.cursor-pointer {
  cursor: pointer;
}

.cursor-pointer:hover {
  opacity: 0.8;
}

/* Component Enhancements */
.card {
  border-radius: 10px;
  border: none;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.btn {
  border-radius: 6px;
  transition: all 0.2s ease;
  font-weight: 500;
}

.btn:hover {
  transform: translateY(-1px);
}

.form-control {
  border-radius: 6px;
  transition: all 0.2s ease;
}

.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
  transform: scale(1.02);
}

/* Responsive Design */
@media (max-width: 768px) {
  .global-notifications {
    top: 10px;
    right: 10px;
    left: 10px;
    max-width: none;
  }

  .notification-toast {
    font-size: 0.9em;
  }
}

/* Print Styles */
@media print {
  .global-notifications {
    display: none !important;
  }
}

/* Accessibility */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* Dark Mode Support */
@media (prefers-color-scheme: dark) {
  body {
    background: #1a1a1a;
    color: #ffffff;
  }

  .global-loading {
    background: rgba(26, 26, 26, 0.9);
  }
}
</style>
