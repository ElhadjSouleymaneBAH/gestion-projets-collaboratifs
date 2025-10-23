<template>
  <div class="container-fluid py-3">
    <!-- Header -->
    <div class="member-header mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h1 class="mb-1 d-flex align-items-center gap-2">
            <i class="fas fa-user-circle text-primary"></i>
            {{ $t('tableauBord.membre.titre') }}
          </h1>
          <p class="text-muted mb-0">
            {{ $t('commun.bienvenue') }}
            {{ utilisateur?.prenom }} {{ utilisateur?.nom }}
          </p>
        </div>

        <button class="btn btn-outline-danger" @click="seDeconnecter">
          <i class="fas fa-sign-out-alt me-2"></i>
          {{ $t('nav.deconnexion') }}
        </button>
      </div>
    </div>

    <!-- Promo Premium -->
    <div class="alert alert-warning border-0 shadow-sm mb-4" v-if="!abonnementActif">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h6 class="mb-1">
            <i class="fas fa-star text-warning me-2"></i>
            {{ $t('membre.promoPremium.titre') }}
          </h6>
          <small class="text-muted">
            {{ $t('membre.promoPremium.description') }}
          </small>
        </div>
        <router-link to="/abonnement-premium" class="btn btn-warning">
          <i class="fas fa-arrow-right me-1"></i>
          {{ $t('membre.promoPremium.bouton') }}
        </router-link>
      </div>
    </div>

    <!-- Erreur backend / Loading -->
    <div v-if="erreurBackend" class="alert alert-danger d-flex justify-content-between align-items-center">
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">{{ $t('commun.actualiser') }}</button>
    </div>
    <div v-else-if="chargementGlobal" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="text-muted mt-2">{{ $t('commun.chargement') }}</p>
    </div>

    <!-- Contenu -->
    <div v-else>
      <div class="row g-3 mb-4">
        <!-- Projets -->
        <div class="col-md-4">
          <div class="card metric-card shadow-sm border-0 cursor-pointer" @click="onglet='projets'">
            <div class="card-body d-flex align-items-center gap-3">
              <i class="fas fa-folder-open fa-2x text-primary"></i>
              <div>
                <h3 class="mb-0 fw-bold">{{ mesProjets.length }}</h3>
                <p class="text-muted mb-0 small">{{ $t('membre.kpi.projetsRejoints') }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Tâches -->
        <div class="col-md-4">
          <div class="card metric-card shadow-sm border-0 cursor-pointer" @click="onglet='taches'">
            <div class="card-body d-flex align-items-center gap-3">
              <i class="fas fa-tasks fa-2x text-success"></i>
              <div>
                <h3 class="mb-0 fw-bold">{{ mesTaches.length }}</h3>
                <p class="text-muted mb-0 small">{{ $t('membre.kpi.tachesAttribuees') }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Notifications -->
        <div class="col-md-4">
          <div class="card metric-card shadow-sm border-0 cursor-pointer" @click="onglet='notifications'">
            <div class="card-body d-flex align-items-center gap-3">
              <i class="fas fa-bell fa-2x text-warning"></i>
              <div>
                <h3 class="mb-0 fw-bold">{{ notificationsNonLues }}</h3>
                <p class="text-muted mb-0 small">{{ $t('membre.kpi.notificationsNonLues') }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Nav -->
      <ul class="nav nav-pills bg-light rounded p-2 mb-4">
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='projets'}" @click="onglet='projets'">
            <i class="fas fa-project-diagram me-2"></i>{{ $t('nav.mesProjets') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='taches'}" @click="onglet='taches'">
            <i class="fas fa-tasks me-2"></i>{{ $t('nav.mesTaches') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='notifications'}" @click="onglet='notifications'">
            <i class="fas fa-bell me-2"></i>{{ $t('nav.notifications') }}
            <span v-if="notificationsNonLues > 0" class="badge bg-danger ms-1">{{ notificationsNonLues }}</span>
          </a>
        </li>
        <li class="nav-item ms-auto">
          <a class="nav-link" :class="{active:onglet==='profil'}" @click="onglet='profil'">
            <i class="fas fa-user-cog me-2"></i>{{ $t('nav.monProfil') }}
          </a>
        </li>
      </ul>

      <!-- PROJETS -->
      <div v-if="onglet === 'projets'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white">
            <h5 class="mb-0">{{ $t('membre.sections.projets') }}</h5>
            <small class="text-muted">{{ $t('membre.projets.description') }}</small>
          </div>
          <div class="card-body">
            <div v-if="mesProjets.length === 0" class="text-center text-muted py-4">
              <i class="fas fa-folder-open fa-3x mb-2"></i>
              <p class="mb-0">{{ $t('membre.projets.aucunProjet') }}</p>
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('projets.projet') }}</th>
                  <th>{{ $t('projets.chefProjet') }}</th>
                  <th class="text-end">{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="p in mesProjets" :key="p.id">
                  <td>
                    <div>
                      <h6 class="mb-0">{{ p.titre || p.nom }}</h6>
                      <small class="text-muted">{{ (p.description || '').substring(0, 80) }}</small>
                    </div>
                  </td>
                  <td>
                    {{ (p.createurPrenom || p.createur?.prenom || '') + ' ' + (p.createurNom || p.createur?.nom || '') }}
                  </td>
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
      </div>

      <!-- TÂCHES -->
      <div v-else-if="onglet === 'taches'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white">
            <h5 class="mb-0">{{ $t('membre.sections.taches') }}</h5>
            <small class="text-muted">{{ $t('membre.taches.description') }}</small>
          </div>
          <div class="card-body">
            <div v-if="mesTaches.length === 0" class="text-center text-muted py-4">
              <i class="fas fa-tasks fa-3x mb-2"></i>
              <p class="mb-0">{{ $t('membre.taches.aucuneTache') }}</p>
            </div>
            <ul v-else class="list-group">
              <li v-for="t in mesTaches" :key="t.id" class="list-group-item d-flex justify-content-between">
                <div>
                  <strong>{{ t.titre || t.nom }}</strong>
                  <small class="text-muted d-block">{{ t.description }}</small>
                </div>
                <span class="badge bg-light text-dark">{{ t.statut }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- NOTIFICATIONS -->
      <div v-else-if="onglet === 'notifications'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('membre.sections.notifications') }}</h5>
              <small class="text-muted">{{ $t('notifications.description') }}</small>
            </div>
            <button
              v-if="notificationsNonLues > 0"
              class="btn btn-sm btn-outline-primary"
              @click="marquerToutesLues"
            >
              <i class="fas fa-check-double me-1"></i>
              {{ $t('notifications.marquerToutesLues') }}
            </button>
          </div>
          <div class="card-body">
            <div v-if="notifications.length === 0" class="text-center text-muted py-4">
              <i class="fas fa-bell fa-3x mb-2"></i>
              <p class="mb-0">{{ $t('notifications.aucuneNotification') }}</p>
            </div>
            <ul v-else class="list-group">
              <li
                v-for="n in notifications"
                :key="n.id"
                class="list-group-item d-flex justify-content-between align-items-start"
                :class="{ 'bg-light': !n.lu }"
              >
                <div class="flex-grow-1">
                  <div class="d-flex justify-content-between align-items-start mb-1">
                    <strong class="me-2">{{ n.titre || $t('notifications.notification') }}</strong>
                    <small class="text-muted">{{ formatDateRelative(n.createdAt) }}</small>
                  </div>
                  <span class="text-muted">{{ n.message || n.contenu }}</span>
                </div>
                <div class="d-flex gap-2 ms-2">
                  <span v-if="!n.lu" class="badge bg-danger">{{ $t('notifications.nonLue') }}</span>
                  <button
                    v-if="!n.lu"
                    class="btn btn-sm btn-outline-success"
                    @click="marquerCommeLue(n.id)"
                    :title="$t('notifications.marquerLue')"
                  >
                    <i class="fas fa-check"></i>
                  </button>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- PROFIL (F5) -->
      <div v-else-if="onglet === 'profil'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white">
            <h5 class="mb-0">
              <i class="fas fa-user-cog me-2"></i>{{ $t('profil.titre') }}
            </h5>
            <small class="text-muted">{{ $t('profil.modifier') }}</small>
          </div>
          <div class="card-body">
            <form @submit.prevent="sauvegarderProfil">
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="fas fa-user me-1"></i>{{ $t('profil.prenom') }}
                  </label>
                  <input
                    v-model="utilisateur.prenom"
                    type="text"
                    class="form-control"
                    maxlength="100"
                    required
                  >
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="fas fa-user me-1"></i>{{ $t('profil.nom') }}
                  </label>
                  <input
                    v-model="utilisateur.nom"
                    type="text"
                    class="form-control"
                    maxlength="100"
                    required
                  >
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="fas fa-envelope me-1"></i>{{ $t('profil.email') }}
                  </label>
                  <input
                    v-model="utilisateur.email"
                    type="email"
                    class="form-control"
                    disabled
                  >
                  <small class="text-muted">{{ $t('profil.emailNonModifiable') }}</small>
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-semibold">
                    <i class="fas fa-language me-1"></i>{{ $t('profil.langue') }}
                  </label>
                  <select v-model="utilisateur.langue" class="form-select">
                    <option value="fr">Français</option>
                    <option value="en">English</option>
                  </select>
                </div>
                <div class="col-md-12">
                  <label class="form-label fw-semibold">
                    <i class="fas fa-shield-alt me-1"></i>{{ $t('profil.role') }}
                  </label>
                  <input
                    :value="$t(`roles.${(utilisateur.role || 'membre').toLowerCase()}`)"
                    type="text"
                    class="form-control"
                    disabled
                  >
                </div>
              </div>

              <div class="mt-4 pt-3 border-top">
                <button
                  type="submit"
                  class="btn btn-primary"
                  :disabled="sauvegardeProfil"
                >
                  <span v-if="sauvegardeProfil" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="fas fa-save me-2"></i>
                  {{ $t('commun.enregistrer') }}
                </button>
                <button
                  type="button"
                  class="btn btn-outline-secondary ms-2"
                  @click="annulerModificationsProfil"
                >
                  <i class="fas fa-times me-2"></i>{{ $t('commun.annuler') }}
                </button>
              </div>
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
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { projectAPI, taskAPI, notificationAPI, userAPI } from '@/services/api'
import WebSocketService from '@/services/websocket.service.js'

const { t } = useI18n()
const router = useRouter()
const store = useAuthStore()

const utilisateur = ref(store.user)
const mesProjets = ref([])
const mesTaches = ref([])
const notifications = ref([])
const notificationsNonLues = ref(0)
const abonnementActif = ref(true)
const erreurBackend = ref('')
const chargementGlobal = ref(true)
const onglet = ref('projets')
const subscribedTopics = new Set()
const sauvegardeProfil = ref(false)
const utilisateurOriginal = ref(null)

const normalizeId = (v) => (v == null ? v : String(v).split(':')[0])

const chargerToutesDonnees = async () => {
  erreurBackend.value = ''
  try {
    const [p, taches, notifs] = await Promise.all([
      projectAPI.byUser(utilisateur.value.id),
      taskAPI.byUser(utilisateur.value.id),
      notificationAPI.list(utilisateur.value.id)
    ])
    mesProjets.value = Array.isArray(p.data) ? p.data : (Array.isArray(p.data?.content) ? p.data.content : [])
    mesTaches.value = Array.isArray(taches.data) ? taches.data : (Array.isArray(taches.data?.content) ? taches.data.content : [])
    notifications.value = Array.isArray(notifs.data) ? notifs.data : []
    notificationsNonLues.value = notifications.value.filter(n => !n.lu).length
  } catch (err) {
    console.error('Erreur chargement :', err)
    erreurBackend.value = t('erreurs.chargementDonnees')
  }
}

// ✅ CORRECTION F13: Topic WebSocket personnalisé par utilisateur
const initWebsocket = () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return

    WebSocketService.connect(token)

    // ✅ Topic personnalisé pour les notifications de l'utilisateur
    const topicUser = `/user/${utilisateur.value.id}/topic/notifications`

    if (!subscribedTopics.has(topicUser)) {
      WebSocketService.subscribe(topicUser, (msg) => {
        console.log('[F13] Notification WebSocket reçue:', msg)

        if (msg?.type === 'NOTIFICATION') {
          const nouvelleNotif = {
            id: msg.id || Date.now(),
            titre: msg.titre || msg.title || 'Notification',
            message: msg.message || msg.contenu,
            type: msg.sousType || msg.type || 'SYSTEME',
            lu: false,
            createdAt: msg.createdAt || new Date().toISOString()
          }

          notifications.value.unshift(nouvelleNotif)
          notificationsNonLues.value = notifications.value.filter(n => !n.lu).length

          // Notification visuelle (optionnel)
          if ('Notification' in window && Notification.permission === 'granted') {
            new Notification(nouvelleNotif.titre, {
              body: nouvelleNotif.message,
              icon: '/favicon.ico'
            })
          }
        }
      })
      subscribedTopics.add(topicUser)
      console.log(`[F13] ✅ Abonné au topic: ${topicUser}`)
    }
  } catch (error) {
    console.error('[F13] Erreur WebSocket:', error)
  }
}

// ✅ Marquer notification comme lue
const marquerCommeLue = async (notifId) => {
  try {
    await notificationAPI.markAsRead(notifId, utilisateur.value.id)
    const notif = notifications.value.find(n => n.id === notifId)
    if (notif) {
      notif.lu = true
      notificationsNonLues.value = notifications.value.filter(n => !n.lu).length
    }
  } catch (error) {
    console.error('Erreur marquer comme lue:', error)
  }
}

// ✅ Marquer toutes comme lues
const marquerToutesLues = async () => {
  try {
    await notificationAPI.markAllAsRead(utilisateur.value.id)
    notifications.value.forEach(n => n.lu = true)
    notificationsNonLues.value = 0
  } catch (error) {
    console.error('Erreur marquer toutes lues:', error)
  }
}

// ✅ Format date relative
const formatDateRelative = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = Math.floor((now - date) / 1000) // en secondes

  if (diff < 60) return t('temps.aLInstant')
  if (diff < 3600) return t('temps.ilYaMinutes', { n: Math.floor(diff / 60) })
  if (diff < 86400) return t('temps.ilYaHeures', { n: Math.floor(diff / 3600) })
  return date.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short' })
}

onMounted(async () => {
  // Récup session
  let user = store.user || JSON.parse(localStorage.getItem('user') || 'null')
  if (!user) {
    return router.push({ path: '/connexion', query: { redirect: router.currentRoute.value.fullPath } })
  }

  // Redirect role
  const role = String(user.role || '').toUpperCase().trim()
  if (role === 'CHEF_PROJET') return router.push('/tableau-bord-chef-projet')
  if (role === 'ADMINISTRATEUR') return router.push('/admin/tableau-de-bord')
  if (role !== 'MEMBRE') {
    return router.push({ path: '/connexion', query: { redirect: router.currentRoute.value.fullPath } })
  }

  store.user = user
  utilisateur.value = user
  utilisateurOriginal.value = JSON.parse(JSON.stringify(user)) // Clone pour annulation

  // Demander permission notifications
  if ('Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission()
  }

  try {
    await chargerToutesDonnees()
    initWebsocket()
  } catch (err) {
    console.error(err)
    erreurBackend.value = t('erreurs.chargementDonnees')
  } finally {
    chargementGlobal.value = false
  }
})

onBeforeUnmount(() => {
  WebSocketService.disconnect()
})

// ✅ F5: Gestion du profil
const sauvegarderProfil = async () => {
  if (sauvegardeProfil.value) return

  try {
    sauvegardeProfil.value = true

    // Validation basique
    if (!utilisateur.value.prenom?.trim() || !utilisateur.value.nom?.trim()) {
      alert(t('profil.champObligatoires'))
      return
    }

    await userAPI.updateProfile(utilisateur.value.id, {
      nom: utilisateur.value.nom,
      prenom: utilisateur.value.prenom,
      langue: utilisateur.value.langue || 'fr'
    })

    // Mise à jour du store et localStorage
    const userUpdated = { ...store.user, ...utilisateur.value }
    store.setUser(userUpdated)
    localStorage.setItem('user', JSON.stringify(userUpdated))
    utilisateurOriginal.value = JSON.parse(JSON.stringify(userUpdated))

    alert(t('profil.misAJour'))
  } catch (error) {
    console.error('Erreur sauvegarde profil:', error)
    alert(t('erreurs.sauvegardeProfile'))
  } finally {
    sauvegardeProfil.value = false
  }
}

const annulerModificationsProfil = () => {
  if (utilisateurOriginal.value) {
    utilisateur.value = JSON.parse(JSON.stringify(utilisateurOriginal.value))
  }
  onglet.value = 'projets'
}

// Actions
const consulterProjet = (p) => {
  router.push(`/projet/${normalizeId(p.id)}`)
}

const seDeconnecter = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  store.user = null
  WebSocketService.disconnect()
  router.push('/connexion')
}
</script>

<style scoped>
.member-header{background:linear-gradient(135deg,#119c72,#96ddc8);border-radius:12px;padding:20px;color:#fff}
.metric-card{transition:.2s}.metric-card:hover{transform:translateY(-2px);box-shadow:0 4px 12px rgba(0,0,0,.08)}
.nav-pills .nav-link{border-radius:8px;margin:0 2px;transition:.2s;cursor:pointer}
.nav-pills .nav-link.active{background:linear-gradient(135deg,#007bff,#0056b3);transform:translateY(-1px)}
.table-hover tbody tr:hover{background-color:rgba(0,123,255,.05)}
.cursor-pointer{cursor:pointer}
.list-group-item.bg-light {
  background-color: #f8f9fa !important;
  border-left: 3px solid #ffc107;
}
</style>
