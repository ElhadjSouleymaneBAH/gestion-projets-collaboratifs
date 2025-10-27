<template>
  <div id="app" class="app-container">
    <!-- Header global -->
    <AppHeader />

    <!-- Notifications -->
    <div class="global-notifications">
      <div
        v-for="notification in globalNotifications"
        :key="notification.id"
        :class="['alert', `alert-${notification.type}`, 'notification-toast']"
      >
        <button @click="removeNotification(notification.id)" class="btn-close"></button>
        {{ notification.message }}
      </div>
    </div>

    <!-- Contenu principal -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- Footer global -->
    <AppFooter class="app-footer" />
  </div>
</template>

<script>
import { ref } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'

export default {
  name: 'App',
  components: { AppHeader, AppFooter },
  setup() {
    const globalNotifications = ref([])

    const addNotification = (message, type = 'info', duration = 5000) => {
      const n = { id: Date.now(), message, type }
      globalNotifications.value.push(n)
      setTimeout(() => removeNotification(n.id), duration)
    }

    const removeNotification = (id) => {
      globalNotifications.value = globalNotifications.value.filter(n => n.id !== id)
    }

    return { globalNotifications, addNotification, removeNotification }
  }
}
</script>

<style>
/* Structure principale */
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-top: 56px; /* espace pour le header fixé */
}

.app-footer {
  margin-top: auto;
}

/* Notifications */
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
  box-shadow: 0 4px 12px rgba(0,0,0,.15);
  animation: slideInRight .3s ease-out;
}

@keyframes slideInRight {
  from { transform: translateX(100%); opacity: 0; }
  to   { transform: translateX(0); opacity: 1; }
}

/* Reset */
* {
  box-sizing: border-box;
}

html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  background-color: #f8f9fa;
}

#app {
  height: 100%;
}
</style>
