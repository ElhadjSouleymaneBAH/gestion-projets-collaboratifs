<template>
  <div class="notifications-component">
    <h5 class="text-collabpro mb-3">
      <i class="fas fa-bell me-2"></i>{{ t('notifications.recents') }}
    </h5>

    <ul class="list-group small">
      <li
        v-for="notif in notifications"
        :key="notif.id"
        class="list-group-item d-flex justify-content-between align-items-start"
      >
        <div>
          <strong>{{ notif.titre }}</strong><br />
          <small class="text-muted">{{ notif.message }}</small>
        </div>
        <small class="text-muted">{{ formatDate(notif.dateCreation) }}</small>
      </li>

      <li v-if="notifications.length === 0" class="list-group-item text-center text-muted">
        {{ t('notifications.aucune') }}
      </li>
    </ul>

    <div class="text-end mt-2">
      <router-link to="/notifications" class="btn btn-outline-primary btn-sm">
        <i class="fas fa-eye me-1"></i>{{ t('notifications.toutes') }}
      </router-link>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import notificationService from '@/services/notification.service.js'

export default {
  name: 'NotificationsList',
  setup() {
    const { t } = useI18n()
    const notifications = ref([])

    const formatDate = (iso) => {
      if (!iso) return ''
      return new Date(iso).toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    onMounted(async () => {
      try {
        // utiliser getNotifications avec pagination
        const res = await notificationService.getNotifications({
          page: 0,
          size: 5 // Limiter à 5 notifications récentes
        })

        if (res.success && res.data) {
          // Si data est paginé
          notifications.value = res.data.content || res.data || []
        }
      } catch (error) {
        console.error('Erreur chargement notifications récentes:', error)
        notifications.value = []
      }
    })

    return {
      t,
      notifications,
      formatDate,
    }
  },
}
</script>

<style scoped>
.notifications-component ul {
  max-height: 250px;
  overflow-y: auto;
}
</style>
