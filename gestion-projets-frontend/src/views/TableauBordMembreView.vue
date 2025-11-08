<template>
  <div class="container-fluid py-3">
    <!-- HEADER -->
    <div class="member-header mb-4">
      <div class="d-flex justify-content-between align-items-center flex-wrap">
        <div>
          <h1 class="mb-1 d-flex align-items-center gap-2">
            <i class="fas fa-user-circle text-light"></i>
            {{ $t('tableauBord.membre.titre') }}
          </h1>
          <p class="text-white-75 mb-0">
            {{ $t('commun.bienvenue') }} {{ utilisateur?.prenom }} {{ utilisateur?.nom }}
          </p>
        </div>

        <div class="mt-3 mt-md-0">
          <button class="btn btn-outline-light" @click="seDeconnecter">
            <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
          </button>
        </div>
      </div>
    </div>

    <!-- PROMO PREMIUM -->
    <div v-if="!chargementGlobal && !abonnementActif" class="alert alert-warning border-0 shadow-sm mb-4 d-flex justify-content-between align-items-center">
      <div>
        <h6 class="mb-1"><i class="fas fa-star text-warning me-2"></i>{{ $t('membre.promoPremium.titre') }}</h6>
        <small class="text-muted">{{ $t('membre.promoPremium.description') }}</small>
      </div>
      <router-link to="/abonnement-premium" class="btn btn-warning">
        <i class="fas fa-arrow-right me-1"></i>{{ $t('membre.promoPremium.bouton') }}
      </router-link>
    </div>

    <!-- ERREUR / CHARGEMENT -->
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
      <!-- KPIs -->
      <div class="row g-3 mb-4">
        <div class="col-md-3">
          <div
            class="card kpi-card border-0 shadow-sm h-100"
            @click="onglet = 'projets'"
          >
            <div class="card-body text-center">
              <i class="fas fa-project-diagram fa-2x mb-2 text-primary"></i>
              <h3 class="fw-bold mb-0">{{ mesProjets.length }}</h3>
              <p class="text-muted mb-0 small">{{ $t('membre.kpi.projetsRejoints') }}</p>
              <small class="text-primary">{{ mesProjets.filter(p=>p.statut==='ACTIF').length }} {{ $t('commun.actifs') }}</small>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div
            class="card kpi-card border-0 shadow-sm h-100"
            @click="onglet = 'taches'"
          >
            <div class="card-body text-center">
              <i class="fas fa-tasks fa-2x mb-2 text-warning"></i>
              <h3 class="fw-bold mb-0">{{ mesTaches.length }}</h3>
              <p class="text-muted mb-0 small">{{ $t('membre.kpi.tachesAttribuees') }}</p>
              <small class="text-warning">{{ mesTaches.filter(t=>t.statut==='EN_COURS').length }} {{ $t('taches.enCours') }}</small>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div
            class="card kpi-card border-0 shadow-sm h-100"
            @click="onglet = 'taches'"
          >
            <div class="card-body text-center">
              <i class="fas fa-check-circle fa-2x mb-2 text-success"></i>
              <h3 class="fw-bold mb-0">{{ mesTaches.filter(t=>t.statut==='TERMINE').length }}</h3>
              <p class="text-muted mb-0 small">{{ $t('membre.kpi.tachesTerminees') }}</p>
              <small class="text-success">{{ tauxReussite }}% {{ $t('membre.kpi.reussite') }}</small>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div
            class="card kpi-card border-0 shadow-sm h-100"
            @click="onglet = 'notifications'"
          >
            <div class="card-body text-center">
              <i class="fas fa-bell fa-2x mb-2 text-info"></i>
              <h3 class="fw-bold mb-0">{{ notifications.filter(n=>!n.lu).length }}</h3>
              <p class="text-muted mb-0 small">{{ $t('membre.kpi.notifications') }}</p>
              <small class="text-info">{{ notifications.length }} {{ $t('commun.total') }}</small>
            </div>
          </div>
        </div>
      </div>

      <!-- NAV ONGLET -->
      <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap">
        <ul class="nav nav-pills bg-light rounded p-2">
          <li class="nav-item">
            <a class="nav-link" :class="{active:onglet==='projets'}" @click="onglet='projets'">
              <i class="fas fa-folder-open me-2"></i>{{ $t('nav.mesProjets') }}
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
              <span v-if="notifications.filter(n=>!n.lu).length>0" class="badge bg-danger ms-1">
                {{ notifications.filter(n=>!n.lu).length }}
              </span>
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{active:onglet==='collaboration'}" @click="onglet='collaboration'">
              <i class="fas fa-comments me-2"></i>{{ $t('nav.collaboration') }}
            </a>
          </li>
        </ul>
        <router-link to="/profil" class="btn btn-link text-decoration-none">
          <i class="fas fa-user me-1"></i>{{ $t('membre.profil') || 'Mon Profil' }}
        </router-link>
      </div>

      <!-- ONGLET PROJETS -->
      <div v-if="onglet==='projets'">
        <div class="card shadow-sm border-0">
          <div class="card-header bg-white">
            <h5 class="mb-0">{{ $t('membre.sections.projets') || 'Projets où je participe' }}</h5>
            <small class="text-muted">{{ $t('membre.projets.description') || 'Collaboration et suivi de progression' }}</small>
          </div>
          <div class="card-body p-0">
            <div v-if="mesProjets.length===0" class="text-center text-muted py-4">
              <i class="fas fa-folder-open fa-3x mb-2"></i>
              <p>{{ $t('membre.projets.aucunProjet') }}</p>
            </div>
            <table v-else class="table table-hover align-middle mb-0">
              <thead class="table-light">
              <tr>
                <th>{{ $t('projets.projet') }}</th>
                <th>{{ $t('projets.chefProjet') }}</th>
                <th>{{ $t('projets.statut') }}</th>
                <th>{{ $t('projets.progression') }}</th>
                <th>{{ $t('membre.mesTaches') }}</th>
                <th>{{ $t('projets.derniereActivite') }}</th>
                <th class="text-end">{{ $t('commun.actions') }}</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="p in mesProjets" :key="p.id">
                <td>
                  <strong>{{ translateProjectTitle(p.titre) }}</strong><br>
                  <small class="text-muted">{{ translateProjectDescription(p.description).substring(0, 50) }}...</small>
                </td>
                <td>{{ p.createur?.prenom }} {{ p.createur?.nom }}</td>
                <td><span class="badge bg-success">{{ p.statut || 'ACTIF' }}</span></td>
                <td>
                  <div class="progress" style="height:6px;">
                    <div class="progress-bar bg-success" role="progressbar" :style="{width:(p.progression||0)+'%'}"></div>
                  </div>
                  <small>{{ p.progression || 0 }}%</small>
                </td>
                <td><span class="badge bg-warning text-dark">{{ getTachesProjet(p.id) }}</span></td>
                <td><small class="text-muted">{{ formatDate(p.dateModification) }}</small></td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary me-1" @click="consulterProjet(p)">
                    <i class="fas fa-eye"></i>
                  </button>
                  <button class="btn btn-sm btn-outline-success" @click="ouvrirChatProjet(p)">
                    <i class="fas fa-comments"></i>
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
              <li v-for="t in mesTaches" :key="t.id" class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                  <strong>{{ t.titre }}</strong><br>
                  <small class="text-muted">{{ t.description }}</small>
                </div>
                <span class="badge" :class="getStatutTacheClass(t.statut)">{{ t.statut }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- ONGLET NOTIFICATIONS -->
      <div v-else-if="onglet==='notifications'">
        <div class="card shadow-sm border-0">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ $t('membre.sections.notifications') }}</h5>
            <button
              class="btn btn-outline-secondary btn-sm"
              @click="marquerToutesLues"
              :disabled="notifications.filter(n=>!n.lu).length===0"
            >
              <i class="fas fa-check-double me-1"></i>{{ $t('notifications.marquerToutesLues') }}
            </button>
          </div>
          <div class="card-body">
            <div v-if="notifications.length===0" class="text-center text-muted py-4">
              <i class="fas fa-bell fa-3x mb-2"></i>
              <p>{{ $t('membre.notifications.vide') || 'Aucune notification pour le moment' }}</p>
            </div>
            <div v-else class="list-group">
              <div
                v-for="n in notifications"
                :key="n.id"
                class="list-group-item d-flex justify-content-between align-items-start"
              >
                <div class="flex-grow-1">
                  <strong>{{ n.titre || n.title }}</strong><br>
                  <small class="text-muted">{{ n.message || n.contenu }}</small><br>
                  <small class="text-muted">{{ formatDateRelative(n.date || n.createdAt) }}</small>
                </div>
                <div class="btn-group">
                  <button v-if="!n.lu" class="btn btn-sm btn-outline-success" @click="marquerNotificationLue(n)">
                    <i class="fas fa-check"></i>
                  </button>
                  <button class="btn btn-sm btn-outline-danger" @click="supprimerNotification(n)">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ONGLET COLLABORATION -->
      <div v-else-if="onglet==='collaboration'">
        <div class="row g-3">
          <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
              <div class="card-header bg-white">
                <h6 class="mb-0">{{ $t('nav.mesProjets') }}</h6>
              </div>
              <div class="list-group list-group-flush">
                <button
                  v-for="p in mesProjets"
                  :key="p.id"
                  class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                  :class="{active: projetChatActuel && projetChatActuel.id===p.id}"
                  @click="ouvrirChatProjet(p)"
                >
                  <span class="text-truncate">{{ p.titre }}</span>
                  <span class="badge bg-secondary">{{ getMessagesNonLusProjet(p.id) }}</span>
                </button>
                <div v-if="mesProjets.length===0" class="text-center text-muted py-4">—</div>
              </div>
            </div>
          </div>

          <div class="col-md-8">
            <div class="card border-0 shadow-sm h-100">
              <div class="card-header bg-white d-flex align-items-center gap-2">
                <h6 class="mb-0">
                  <i class="fas fa-comments me-2"></i>
                  {{ projetChatActuel ? projetChatActuel.titre : $t('nav.collaboration') }}
                </h6>
              </div>

              <div class="card-body d-flex flex-column" style="height:420px">
                <div class="flex-grow-1 overflow-auto messages-container">
                  <div v-if="!projetChatActuel" class="text-center text-muted py-5">
                    {{ $t('projets.choisirProjet') }}
                  </div>

                  <div v-else>
                    <div v-if="messagesChat.length===0" class="text-center text-muted py-5">—</div>

                    <div
                      v-for="m in messagesChat"
                      :key="m.id"
                      class="p-2 rounded mb-2 chat-bubble"
                      :class="getMessageClass(m)"
                      style="max-width:80%"
                    >
                      <div class="small opacity-75 mb-1">
                        {{ m.auteur?.prenom || m.auteur?.firstName || m.utilisateurNom || '—' }}
                        · {{ formatTime(m.dateEnvoi || m.date || m.createdAt) }}
                      </div>
                      <div>{{ m.contenu }}</div>
                    </div>
                  </div>
                </div>

                <div class="pt-2 border-top" v-if="projetChatActuel">
                  <div class="input-group">
                    <input
                      class="form-control"
                      v-model.trim="nouveauMessage"
                      :placeholder="$t('collaboration.ecrireMessage')"
                      @keyup.enter="envoyerMessage"
                    >
                    <button class="btn btn-success" :disabled="envoyantMessage || !nouveauMessage" @click="envoyerMessage">
                      <span v-if="envoyantMessage" class="spinner-border spinner-border-sm"></span>
                      <i v-else class="fas fa-paper-plane me-1"></i>{{ $t('commun.envoyer') }}
                    </button>
                  </div>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { projectAPI, taskAPI, notificationAPI, messagesAPI } from '@/services/api'
import WebSocketService from '@/services/websocket.service.js'
import { useI18n } from 'vue-i18n'
import { useDataTranslation } from '@/composables/useDataTranslation'

const { t } = useI18n()
const { translateProjectTitle, translateProjectDescription } = useDataTranslation()
const router = useRouter()
const store = useAuthStore()

// Fonction helper pour récupérer l'utilisateur de manière sûre
const getUserSafe = () => {
  if (store.user) return store.user
  try {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  } catch {
    return null
  }
}

// Utiliser la fonction helper au lieu de store.user directement
const utilisateur = ref(getUserSafe())
const mesProjets = ref([])
const mesTaches = ref([])
const notifications = ref([])
const messagesChat = ref([])
const messagesParProjet = ref({})
const nouveauMessage = ref('')
const abonnementActif = ref(true)
const erreurBackend = ref('')
const chargementGlobal = ref(true)
const onglet = ref('projets')
const projetChatActuel = ref(null)
const envoyantMessage = ref(false)

const tauxReussite = computed(() => {
  if (mesTaches.value.length === 0) return 0
  const terminees = mesTaches.value.filter(t => t.statut === 'TERMINE').length
  return Math.round((terminees / mesTaches.value.length) * 100)
})

const normalizeId = (v) => v == null ? v : String(v).split(':')[0]

// Vérification robuste de l'utilisateur
const chargerToutesDonnees = async () => {
  chargementGlobal.value = true
  erreurBackend.value = ''

  try {
    // Vérifier que l'utilisateur existe
    if (!utilisateur.value || !utilisateur.value.id) {
      console.warn(' Utilisateur non défini, tentative de récupération...')
      utilisateur.value = getUserSafe()

      if (!utilisateur.value || !utilisateur.value.id) {
        console.error('Impossible de récupérer l utilisateur')
        erreurBackend.value = 'Session expiree, veuillez vous reconnecter'
        chargementGlobal.value = false
        setTimeout(() => router.push('/connexion'), 2000)
        return
      }
    }

    const userId = normalizeId(utilisateur.value.id)
    console.log(' Chargement des données pour userId:', userId)

    //  Promise.allSettled pour gérer les erreurs partielles
    const [pRes, tRes, nRes] = await Promise.allSettled([
      projectAPI.byUser(userId),
      taskAPI.byUser(userId),
      notificationAPI.list(userId)
    ])

    //  Gérer les réponses même si certaines échouent
    mesProjets.value = pRes.status === 'fulfilled' && Array.isArray(pRes.value?.data)
      ? pRes.value.data
      : []

    mesTaches.value = tRes.status === 'fulfilled' && Array.isArray(tRes.value?.data)
      ? tRes.value.data
      : []

    notifications.value = nRes.status === 'fulfilled' && Array.isArray(nRes.value?.data)
      ? nRes.value.data
      : []

    console.log(' Données chargées:', {
      projets: mesProjets.value.length,
      taches: mesTaches.value.length,
      notifications: notifications.value.length
    })

    const erreurs = [pRes, tRes, nRes].filter(r => r.status === 'rejected')
    if (erreurs.length === 3) {
      erreurBackend.value = t('erreurs.chargementDonnees')
      console.error(' Toutes les API ont échoué')
    } else if (erreurs.length > 0) {
      console.warn(' Erreurs partielles:', erreurs.map(e => e.reason?.message))
    }

  } catch (e) {
    console.error(' Erreur critique:', e)
    erreurBackend.value = t('erreurs.chargementDonnees')
  } finally {
    chargementGlobal.value = false
  }
}

const chargerMessagesProjet = async (projetId) => {
  try {
    const r = await messagesAPI.byProjet(projetId)
    messagesChat.value = Array.isArray(r.data) ? r.data : []
    messagesParProjet.value[projetId] = messagesChat.value
  } catch (e) {
    console.error(e)
    messagesChat.value = []
  }
}

// Vérification de l'utilisateur dans initWebsocket
const initWebsocket = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    console.warn('[WS] Pas de token, WebSocket non initialisé')
    return
  }

  // Vérifier que l'utilisateur existe
  if (!utilisateur.value || !utilisateur.value.id) {
    console.error('[WS] Utilisateur non défini, WebSocket non initialisé')
    return
  }

  WebSocketService.connect(token)

  const userId = normalizeId(utilisateur.value.id)
  const topicNotifications = `/user/${userId}/topic/notifications`

  console.log('[WS] Souscription WebSocket:', topicNotifications)

  WebSocketService.subscribe(topicNotifications, (msg) => {
    console.log('[WS] Notification reçue:', msg)

    if (msg?.type === 'NOTIFICATION') {
      notifications.value.unshift({
        id: msg.id || Date.now(),
        titre: msg.titre || 'Notification',
        message: msg.message || msg.contenu,
        date: msg.createdAt || new Date().toISOString(),
        lu: false,
        type: msg.sousType || 'SYSTEME'
      })

      if ('Notification' in window && Notification.permission === 'granted') {
        new Notification(msg.titre || 'Notification', {
          body: msg.message || msg.contenu,
          icon: '/favicon.ico'
        })
      }
    }
  })

  if (projetChatActuel.value) {
    const topicProjet = `/topic/projet/${projetChatActuel.value.id}`
    console.log(' Souscription WebSocket projet:', topicProjet)

    WebSocketService.subscribe(topicProjet, (msg) => {
      console.log('[WS] Message chat reçu:', msg)
      messagesChat.value.push(msg)
      if (messagesParProjet.value[projetChatActuel.value.id]) {
        messagesParProjet.value[projetChatActuel.value.id].push(msg)
      }
    })
  }
}

const envoyerMessage = async () => {
  if (!nouveauMessage.value.trim() || !projetChatActuel.value) return

  envoyantMessage.value = true
  try {
    const r = await messagesAPI.send({
      projetId: projetChatActuel.value.id,
      contenu: nouveauMessage.value,
      type: 'TEXT'
    })

    messagesChat.value.push(r.data)
    nouveauMessage.value = ''
  } catch (e) {
    console.error(e)
    alert(t('erreurs.envoyerMessage'))
  } finally {
    envoyantMessage.value = false
  }
}

const ouvrirChatProjet = async (projet) => {
  //  Vérification d'accès avant ouverture du chat
  if (projet.prive && !projet.estMembre) {
    console.warn(`[WS] Accès refusé au projet privé : ${projet.titre}`)
    alert('Accès refusé à ce projet privé.')
    return
  }

  projetChatActuel.value = projet
  onglet.value = 'collaboration'
  await chargerMessagesProjet(projet.id)
}

const consulterProjet = (p) => {
  const id = normalizeId(p.id)
  router.push(`/projet/${id}`)
}

const seDeconnecter = () => {
  try { useAuthStore().logout?.() } catch {}
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.replace({ path: '/connexion', query: { switch: '1' } })
}

const getMessagesNonLusProjet = (projetId) => {
  const messages = messagesParProjet.value[projetId] || []
  return messages.filter(m => m.statut !== 'LU').length
}

const getTachesProjet = (projetId) => {
  return mesTaches.value.filter(t =>
    normalizeId(t.projetId || t.id_projet) == normalizeId(projetId)
  ).length
}

const getStatutTacheClass = (statut) => {
  const classes = {
    'BROUILLON': 'bg-secondary',
    'EN_COURS': 'bg-warning text-dark',
    'EN_ATTENTE_VALIDATION': 'bg-info',
    'TERMINE': 'bg-success',
    'ANNULE': 'bg-danger'
  }
  return classes[statut] || 'bg-secondary'
}

const getMessageClass = (m) => {
  const monId = normalizeId(utilisateur.value?.id)
  const auteurId = normalizeId(m.utilisateurId || m.authorId)
  return auteurId == monId ? 'bg-primary text-white ms-auto' : 'bg-white border shadow-sm'
}

const marquerNotificationLue = async (n) => {
  try {
    const userId = normalizeId(utilisateur.value.id)
    await notificationAPI.markAsRead(n.id, userId)
    n.lu = true
  } catch (e) {
    console.error(e)
  }
}

const marquerToutesLues = async () => {
  try {
    const userId = normalizeId(utilisateur.value.id)
    await notificationAPI.markAllAsRead(userId)
    notifications.value.forEach(n => n.lu = true)
    alert(t('notifications.toutesMarquees'))
  } catch (e) {
    console.error(e)
  }
}

const supprimerNotification = async (n) => {
  if (!confirm(t('notifications.confirmerSuppression'))) return
  try {
    const userId = normalizeId(utilisateur.value.id)
    await notificationAPI.delete(n.id, userId)
    notifications.value = notifications.value.filter(x => x.id !== n.id)
  } catch (e) {
    console.error(e)
  }
}

const formatDate = (date) => {
  if (!date) return '—'
  const d = new Date(date)
  const jour = String(d.getDate()).padStart(2, '0')
  const mois = String(d.getMonth() + 1).padStart(2, '0')
  const annee = d.getFullYear()
  return `${jour}/${mois}/${annee}`
}

const formatDateRelative = (date) => {
  if (!date) return '—'
  const now = new Date()
  const diff = now - new Date(date)
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (minutes < 1) return t('temps.maintenant')
  if (minutes < 60) return `${t('temps.ilYa')} ${minutes}${t('temps.min')}`
  if (hours < 24) return `${t('temps.ilYa')} ${hours}${t('temps.h')}`
  if (days < 7) return `${t('temps.ilYa')} ${days}${t('temps.j')}`
  return formatDate(date)
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleTimeString('fr-FR', {
    hour: '2-digit', minute: '2-digit'
  })
}

onMounted(async () => {
  await chargerToutesDonnees()
  initWebsocket()
})

onBeforeUnmount(() => {
  WebSocketService.disconnect()
})
</script>

<style scoped>
.member-header {
  background: linear-gradient(135deg, #119c72, #96ddc8);
  border-radius: 12px;
  padding: 20px;
  color: white;
}

.text-white-75 {
  color: rgba(255, 255, 255, 0.75);
}

.kpi-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.kpi-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.card {
  border-radius: 12px;
  overflow: hidden;
}

.nav-pills .nav-link {
  border-radius: 8px;
  margin: 0 2px;
  transition: all 0.2s ease;
}

.nav-pills .nav-link.active {
  background: linear-gradient(135deg, #119c72, #96ddc8);
  color: white;
  font-weight: 600;
}

.chat-bubble {
  animation: slideIn 0.3s ease;
}

.messages-container {
  scroll-behavior: smooth;
}

.messages-container::-webkit-scrollbar {
  width: 6px;
}

.messages-container::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.messages-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.table th {
  border-top: none;
  font-weight: 600;
  font-size: 0.875rem;
  color: #495057;
}

.table-hover tbody tr:hover {
  background-color: rgba(17, 156, 114, 0.08);
}

.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}

.progress {
  background-color: #e9ecef;
  border-radius: 4px;
}
</style>
