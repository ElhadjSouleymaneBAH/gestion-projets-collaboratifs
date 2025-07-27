<template>
  <div class="chat-container bg-collabpro-light">
    <div class="container py-3 fade-in">
      <h2 class="text-center text-collabpro mb-3">Discussion du projet {{ projectId }}</h2>

      <div class="card-collabpro mb-3" ref="messageBox" style="max-height: 60vh; overflow-y: auto;">
        <div
          v-for="message in messages"
          :key="message.id"
          class="p-3 border-bottom"
          :class="message.type === 'TEXT' ? 'text-message' :
                   message.type === 'SYSTEM' ? 'system-message' :
                   message.type === 'NOTIFICATION' ? 'notification-message' : ''"
        >
          <template v-if="message.type === 'TEXT'">
            <strong>{{ message.utilisateurNom }}</strong> :
            <span>{{ message.contenu }}</span>
          </template>

          <template v-else-if="message.type === 'SYSTEM'">
            <em>{{ message.contenu }}</em>
          </template>

          <template v-else-if="message.type === 'NOTIFICATION'">
            <span class="notification">{{ message.contenu }}</span>
          </template>

          <div class="text-end text-muted small mt-1">{{ formatDate(message.dateEnvoi) }}</div>
        </div>
      </div>

      <div class="input-group mb-3">
        <input
          v-model="newMessage"
          class="form-control"
          type="text"
          placeholder="Écris ton message..."
          @keyup.enter="envoyerMessage"
        />
      </div>

      <div class="d-flex gap-2 flex-wrap justify-content-center">
        <button @click="envoyerMessage" class="btn-collabpro">Envoyer</button>
        <button @click="envoyerNotification" class="btn-collabpro">Notifier</button>
        <router-link to="/mes-projets">
          <button class="btn btn-secondary">Retour aux projets</button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import WebSocketService from '@/services/websocket.service'
import { useAuthStore } from '@/stores/auth'

const messages = ref([])
const newMessage = ref('')
const route = useRoute()
const projectId = ref(parseInt(route.params.id))
const messageBox = ref(null)

const authStore = useAuthStore()
const token = authStore.token
const userNom = authStore.user.nom

const envoyerMessage = () => {
  if (newMessage.value.trim()) {
    WebSocketService.sendMessage(projectId.value, newMessage.value)
    newMessage.value = ''
  }
}

const envoyerNotification = () => {
  WebSocketService.sendNotification(
    projectId.value,
    'Une nouvelle tâche a été assignée au projet'
  )
}

const formatDate = iso => {
  return new Date(iso).toLocaleTimeString('fr-BE', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageBox.value) {
      messageBox.value.scrollTop = messageBox.value.scrollHeight
    }
  })
}

onMounted(() => {
  WebSocketService.connect(token)
  WebSocketService.subscribeToProject(projectId.value, msg => {
    messages.value.push(msg)
    scrollToBottom()
  })
  WebSocketService.sendJoinMessage(projectId.value, userNom)
})

onBeforeUnmount(() => {
  WebSocketService.disconnect()
})
</script>

<style scoped>
.text-message {
  color: #222;
}

.system-message {
  color: #555;
  font-style: italic;
}

.notification-message {
  background-color: #fff8e1;
  border-left: 4px solid var(--collabpro-blue);
  padding-left: 10px;
}

.notification {
  font-weight: 600;
  color: var(--collabpro-blue);
  display: block;
}

@media (max-width: 600px) {
  .chat-container .container {
    padding: 1rem 0.5rem;
  }

  .input-group {
    flex-direction: column;
  }

  .btn-collabpro,
  .btn-secondary {
    width: 100%;
    font-size: 0.95rem;
  }
}
</style>
