<template>
  <div id="app">
    <!-- Notifications -->
    <div class="global-notifications">
      <div v-for="notification in globalNotifications" :key="notification.id"
           :class="['alert', `alert-${notification.type}`, 'notification-toast']">
        <button @click="removeNotification(notification.id)" class="btn-close"></button>
        {{ notification.message }}
      </div>
    </div>

    <!-- Router view -->
    <router-view />
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'App',
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
.global-notifications{position:fixed;top:20px;right:20px;z-index:1060;max-width:400px}
.notification-toast{margin-bottom:10px;border-radius:8px;box-shadow:0 4px 12px rgba(0,0,0,.15);animation:slideInRight .3s ease-out;position:relative}
@keyframes slideInRight{from{transform:translateX(100%);opacity:0}to{transform:translateX(0);opacity:1}}
</style>
