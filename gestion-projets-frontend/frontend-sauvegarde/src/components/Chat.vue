<template>
  <div class="chat-container bg-collabpro-light">
    <div class="container py-3 fade-in">
      <h2 class="text-center text-collabpro mb-3">
        {{ $t('project_discussion') }} {{ projectId }}
      </h2>

      <div class="card-collabpro mb-3" ref="messageBox" style="max-height: 60vh; overflow-y: auto;">
        <div v-if="messages.length === 0" class="text-center py-4">
          <i class="fas fa-comments fa-2x text-muted mb-2"></i>
          <p class="text-muted">{{ $t('no_messages') }}</p>
          <small class="text-muted">{{ $t('start_conversation') }}</small>
        </div>

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
          :placeholder="$t('type_message')"
          @keyup.enter="envoyerMessage"
        />
      </div>

      <div class="d-flex gap-2 flex-wrap justify-content-center">
        <button @click="envoyerMessage" class="btn-collabpro">
          <i class="fas fa-paper-plane me-2"></i>{{ $t('send_message') }}
        </button>
        <button @click="envoyerNotification" class="btn-collabpro">
          <i class="fas fa-bell me-2"></i>{{ $t('send_notification') }}
        </button>
        <router-link to="/mes-projets">
          <button class="btn btn-secondary">
            <i class="fas fa-arrow-left me-2"></i>{{ $t('back_to_projects') }}
          </button>
        </router-link>
      </div>

      <!-- Membres en ligne (optionnel) -->
      <div v-if="membresEnLigne.length > 0" class="mt-3 text-center">
        <small class="text-muted">
          {{ $t('online_members') }}:
          <span v-for="(membre, index) in membresEnLigne" :key="membre.id">
            {{ membre.nom }}{{ index < membresEnLigne.length - 1 ? ', ' : '' }}
          </span>
        </small>
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
const membresEnLigne = ref([])
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

.btn-collabpro {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.3);
}

.btn-collabpro:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.4);
}

.card-collabpro {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: none;
}

.bg-collabpro-light {
  background: linear-gradient(135deg, #f8faff 0%, #e3f2fd 100%);
  min-height: 100vh;
}

.fade-in {
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
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
    margin-bottom: 0.5rem;
  }

  .d-flex {
    flex-direction: column;
  }
}
</style>
