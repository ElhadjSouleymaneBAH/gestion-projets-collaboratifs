<template>
  <div class="container-fluid py-3">
    <!-- HEADER -->
    <div class="member-header mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h1 class="mb-1 d-flex align-items-center gap-2">
            <i class="fas fa-user-circle text-primary"></i>
            {{ $t('tableauBord.membre.titre') }}
          </h1>
          <p class="text-white-75 mb-0">
            {{ $t('commun.bienvenue') }}
            {{ utilisateur?.prenom }} {{ utilisateur?.nom }}
          </p>
        </div>

        <button class="btn btn-outline-light" @click="seDeconnecter">
          <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
        </button>
      </div>
    </div>

    <!-- PROMO PREMIUM -->
    <div v-if="!chargementGlobal && !abonnementActif" class="alert alert-warning border-0 shadow-sm mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h6 class="mb-1">
            <i class="fas fa-star text-warning me-2"></i>{{ $t('membre.promoPremium.titre') }}
          </h6>
          <small class="text-muted">{{ $t('membre.promoPremium.description') }}</small>
        </div>
        <router-link to="/abonnement-premium" class="btn btn-warning">
          <i class="fas fa-arrow-right me-1"></i>{{ $t('membre.promoPremium.bouton') }}
        </router-link>
      </div>
    </div>

    <!-- ERREUR OU CHARGEMENT -->
    <div v-if="erreurBackend" class="alert alert-danger d-flex justify-content-between align-items-center">
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">{{ $t('commun.actualiser') }}</button>
    </div>
    <div v-else-if="chargementGlobal" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="text-muted mt-2">{{ $t('commun.chargement') }}</p>
    </div>

    <!-- CONTENU PRINCIPAL -->
    <div v-else>
      <div class="row g-3 mb-4">
        <div class="col-md-4" v-for="(item, i) in stats" :key="i">
          <div class="card metric-card shadow-sm border-0 cursor-pointer" @click="onglet=item.onglet">
            <div class="card-body d-flex align-items-center gap-3">
              <i :class="item.icon + ' fa-2x ' + item.color"></i>
              <div>
                <h3 class="mb-0 fw-bold">{{ item.value }}</h3>
                <p class="text-muted mb-0 small">{{ item.label }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- NAV -->
      <ul class="nav nav-pills bg-light rounded p-2 mb-4">
        <li v-for="tab in tabs" :key="tab.key" class="nav-item">
          <a class="nav-link" :class="{active:onglet===tab.key}" @click="onglet=tab.key">
            <i :class="tab.icon + ' me-2'"></i>{{ tab.label }}
          </a>
        </li>
      </ul>

      <!-- ONGLET PROJETS -->
      <div v-if="onglet==='projets'">
        <div class="card shadow-sm border-0">
          <div class="card-header bg-white">
            <h5 class="mb-0">{{ $t('membre.sections.projets') }}</h5>
          </div>
          <div class="card-body">
            <div v-if="mesProjets.length===0" class="text-center text-muted py-4">
              <i class="fas fa-folder-open fa-3x mb-2"></i><p>{{ $t('membre.projets.aucunProjet') }}</p>
            </div>
            <table v-else class="table table-hover align-middle">
              <thead><tr><th>Projet</th><th>Chef</th><th class="text-end">Actions</th></tr></thead>
              <tbody>
              <tr v-for="p in mesProjets" :key="p.id">
                <td><strong>{{ p.titre }}</strong><br><small>{{ p.description }}</small></td>
                <td>{{ p.createur?.prenom }} {{ p.createur?.nom }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary" @click="consulterProjet(p)">
                    <i class="fas fa-eye"></i>
                  </button>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ONGLET TÂCHES -->
      <div v-else-if="onglet==='taches'">
        <div class="card shadow-sm border-0">
          <div class="card-header bg-white">
            <h5 class="mb-0">{{ $t('membre.sections.taches') }}</h5>
          </div>
          <div class="card-body">
            <div v-if="mesTaches.length===0" class="text-center text-muted py-4">
              <i class="fas fa-tasks fa-3x mb-2"></i>
              <p>{{ $t('membre.taches.aucuneTache') }}</p>
            </div>
            <ul v-else class="list-group">
              <li v-for="t in mesTaches" :key="t.id" class="list-group-item d-flex justify-content-between">
                <div><strong>{{ t.titre }}</strong><br><small class="text-muted">{{ t.description }}</small></div>
                <span class="badge bg-light text-dark">{{ t.statut }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- ONGLET CHAT / COLLABORATION -->
      <div v-else-if="onglet==='collaboration'">
        <div class="card shadow-sm border-0">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0"><i class="fas fa-comments me-2"></i>{{ $t('membre.sections.collaboration') }}</h5>
            <small class="text-muted">{{ $t('membre.chat.description') }}</small>
          </div>
          <div class="card-body">
            <div class="chat-box">
              <div v-for="m in messagesChat" :key="m.id" class="chat-message" :class="{'sent':m.senderId===utilisateur.id}">
                <strong>{{ m.senderName }} :</strong> <span>{{ m.contenu }}</span>
              </div>
            </div>
            <form class="d-flex mt-3" @submit.prevent="envoyerMessage">
              <input v-model="nouveauMessage" type="text" class="form-control me-2" :placeholder="$t('membre.chat.placeholder')">
              <button class="btn btn-primary" :disabled="!nouveauMessage.trim()"><i class="fas fa-paper-plane"></i></button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { projectAPI, taskAPI, notificationAPI, messagesAPI } from '@/services/api'
import WebSocketService from '@/services/websocket.service.js'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const router = useRouter()
const store = useAuthStore()

const utilisateur = ref(store.user)
const mesProjets = ref([])
const mesTaches = ref([])
const notifications = ref([])
const messagesChat = ref([])
const nouveauMessage = ref('')
const abonnementActif = ref(true)
const erreurBackend = ref('')
const chargementGlobal = ref(true)
const onglet = ref('projets')

const tabs = [
  { key: 'projets', label: t('nav.mesProjets'), icon: 'fas fa-folder-open' },
  { key: 'taches', label: t('nav.mesTaches'), icon: 'fas fa-tasks' },
  { key: 'collaboration', label: t('nav.collaboration'), icon: 'fas fa-comments' }
]

const stats = [
  { icon: 'fas fa-folder-open', color: 'text-primary', label: t('membre.kpi.projetsRejoints'), value: mesProjets.value.length, onglet: 'projets' },
  { icon: 'fas fa-tasks', color: 'text-success', label: t('membre.kpi.tachesAttribuees'), value: mesTaches.value.length, onglet: 'taches' },
  { icon: 'fas fa-comments', color: 'text-warning', label: t('membre.kpi.messages'), value: messagesChat.value.length, onglet: 'collaboration' }
]

const chargerToutesDonnees = async () => {
  try {
    const [p, t, n] = await Promise.all([
      projectAPI.byUser(utilisateur.value.id),
      taskAPI.byUser(utilisateur.value.id),
      notificationAPI.list(utilisateur.value.id)
    ])
    mesProjets.value = p.data || []
    mesTaches.value = t.data || []
    notifications.value = n.data || []
  } catch (e) {
    erreurBackend.value = t('erreurs.chargementDonnees')
  } finally {
    chargementGlobal.value = false
  }
}

// 🔁 Chat temps réel
const initWebsocket = () => {
  const token = localStorage.getItem('token')
  if (!token) return
  WebSocketService.connect(token)

  const topicChat = `/user/${utilisateur.value.id}/topic/chat`
  WebSocketService.subscribe(topicChat, (msg) => {
    if (msg?.contenu) messagesChat.value.push(msg)
  })
}

const envoyerMessage = async () => {
  if (!nouveauMessage.value.trim()) return
  const msg = { senderId: utilisateur.value.id, contenu: nouveauMessage.value, senderName: utilisateur.value.prenom }
  messagesChat.value.push(msg)
  await messagesAPI.send(msg)
  nouveauMessage.value = ''
}

const consulterProjet = (p) => router.push(`/projet/${p.id}`)
const seDeconnecter = () => { localStorage.clear(); router.push('/connexion') }

onMounted(async () => { await chargerToutesDonnees(); initWebsocket() })
onBeforeUnmount(() => WebSocketService.disconnect())
</script>

<style scoped>
.member-header {
  background: linear-gradient(135deg, #119c72, #96ddc8);
  border-radius: 12px;
  padding: 20px;
  color: white;
}
.chat-box { max-height: 350px; overflow-y: auto; padding: 10px; border: 1px solid #ddd; border-radius: 8px; }
.chat-message { margin-bottom: 8px; }
.chat-message.sent { text-align: right; color: #007bff; }
</style>
