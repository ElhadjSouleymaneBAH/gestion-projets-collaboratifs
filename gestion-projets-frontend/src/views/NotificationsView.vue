<template>
  <div class="notifications-view">
    <div class="container-fluid">
      <!-- En-tête -->
      <div class="card-collabpro mb-4">
        <div class="card-body p-4">
          <div class="row align-items-center">
            <div class="col-md-8">
              <h1 class="text-collabpro mb-2">
                <i class="fas fa-bell me-3"></i>
                {{ t('notifications.titre') }}
              </h1>
              <p class="text-muted mb-0">{{ t('notifications.sousTitre') }}</p>
            </div>
            <div class="col-md-4 text-end">
              <div class="d-flex gap-2 flex-wrap">
                <button
                  @click="marquerToutesLues"
                  class="btn btn-collabpro btn-sm"
                  :disabled="!hasNotificationsNonLues || chargement"
                >
                  <i class="fas fa-check-double me-2"></i>
                  {{ t('notifications.marquerToutesLues') }}
                </button>
                <button
                  @click="supprimerToutesLues"
                  class="btn btn-outline-danger btn-sm"
                  :disabled="!hasNotificationsLues || chargement"
                >
                  <i class="fas fa-trash me-2"></i>
                  {{ t('notifications.supprimerLues') }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Statistiques -->
      <div class="row mb-4">
        <div class="col-md-3 mb-3">
          <div class="card-collabpro">
            <div class="card-body d-flex align-items-center">
              <div class="icon-stat me-3">
                <i class="fas fa-bell"></i>
              </div>
              <div>
                <h3 class="text-collabpro mb-1">{{ statistiques.total }}</h3>
                <p class="text-muted mb-0">{{ t('notifications.total') }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="card-collabpro">
            <div class="card-body d-flex align-items-center">
              <div class="icon-stat me-3 text-warning">
                <i class="fas fa-bell-slash"></i>
              </div>
              <div>
                <h3 class="text-warning mb-1">{{ statistiques.nonLues }}</h3>
                <p class="text-muted mb-0">{{ t('notifications.nonLues') }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="card-collabpro">
            <div class="card-body d-flex align-items-center">
              <div class="icon-stat me-3 text-success">
                <i class="fas fa-check"></i>
              </div>
              <div>
                <h3 class="text-success mb-1">{{ statistiques.lues }}</h3>
                <p class="text-muted mb-0">{{ t('notifications.lues') }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="card-collabpro">
            <div class="card-body d-flex align-items-center">
              <div class="icon-stat me-3 text-info">
                <i class="fas fa-clock"></i>
              </div>
              <div>
                <h3 class="text-info mb-1">{{ statistiques.aujourdhui }}</h3>
                <p class="text-muted mb-0">{{ t('notifications.aujourdhui') }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Filtres -->
      <div class="card-collabpro mb-4">
        <div class="card-body">
          <div class="row">
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">{{ t('notifications.filtres.type') }}</label>
              <select v-model="filtreType" @change="appliquerFiltres" class="form-select">
                <option value="">{{ t('notifications.filtres.tousTypes') }}</option>
                <option value="TACHE">{{ t('notifications.types.tache') }}</option>
                <option value="PROJET">{{ t('notifications.types.projet') }}</option>
                <option value="PAIEMENT">{{ t('notifications.types.paiement') }}</option>
                <option value="INVITATION">{{ t('notifications.types.invitation') }}</option>
                <option value="SYSTEME">{{ t('notifications.types.systeme') }}</option>
              </select>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">{{ t('notifications.filtres.statut') }}</label>
              <select v-model="filtreStatut" @change="appliquerFiltres" class="form-select">
                <option value="">{{ t('notifications.filtres.tousStatuts') }}</option>
                <option value="false">{{ t('notifications.nonLues') }}</option>
                <option value="true">{{ t('notifications.lues') }}</option>
              </select>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">{{ t('notifications.filtres.periode') }}</label>
              <select v-model="filtrePeriode" @change="appliquerFiltres" class="form-select">
                <option value="">{{ t('notifications.filtres.toutesPeriodes') }}</option>
                <option value="today">{{ t('notifications.periodes.ajd') }}</option>
                <option value="week">{{ t('notifications.periodes.semaine') }}</option>
                <option value="month">{{ t('notifications.periodes.mois') }}</option>
              </select>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">{{ t('notifications.filtres.rechercher') }}</label>
              <input
                v-model="rechercheTexte"
                @input="rechercherNotifications"
                type="text"
                class="form-control"
                :placeholder="t('notifications.filtres.placeholder')"
              >
            </div>
          </div>
        </div>
      </div>

      <!-- Messages -->
      <div v-if="message.texte" :class="['alert', message.type === 'success' ? 'alert-success' : 'alert-danger']">
        <i :class="message.type === 'success' ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle'" class="me-2"></i>
        {{ message.texte }}
      </div>

      <!-- Loading -->
      <div v-if="chargement" class="text-center py-5">
        <div class="spinner-border text-collabpro" role="status">
          <span class="visually-hidden">{{ t('commun.chargement') }}</span>
        </div>
        <p class="mt-2">{{ t('notifications.chargement') }}</p>
      </div>

      <!-- Liste des notifications -->
      <div v-else-if="notificationsFiltrees.length > 0" class="card-collabpro">
        <div class="card-body p-0">
          <div
            v-for="notification in notificationsFiltrees"
            :key="notification.id"
            class="notification-item border-bottom p-3"
            :class="{
              'bg-collabpro-light': !notification.lu,
              'border-start border-warning border-3': isNouvelle(notification)
            }"
          >
            <div class="d-flex justify-content-between align-items-start mb-2">
              <div class="d-flex align-items-center">
                <i :class="getTypeIcon(notification.type)" :style="{ color: getTypeColor(notification.type) }" class="me-2"></i>
                <span class="badge bg-secondary text-uppercase small">{{ getTypeLabel(notification.type) }}</span>
              </div>
              <small class="text-muted">{{ formatDate(notification.dateCreation) }}</small>
            </div>

            <div class="mb-2">
              <h6 class="text-collabpro mb-1">{{ notification.titre }}</h6>
              <p class="mb-2">{{ notification.message }}</p>

              <!-- Données spécifiques -->
              <div v-if="notification.donnees" class="d-flex gap-2 flex-wrap mb-2">
                <span v-if="notification.type === 'TACHE'" class="badge bg-primary">{{ notification.donnees.projetNom }}</span>
                <span v-if="notification.type === 'PAIEMENT'" class="badge bg-success">{{ formatMontant(notification.donnees.montant) }}</span>
                <span v-if="notification.type === 'INVITATION'" class="badge bg-info">{{ notification.donnees.projetNom }}</span>
              </div>
            </div>

            <div class="d-flex gap-2 flex-wrap">
              <button
                v-if="!notification.lu"
                @click="marquerCommeLue(notification.id)"
                class="btn btn-collabpro btn-sm"
                :disabled="actionsEnCours[notification.id]"
              >
                <span v-if="actionsEnCours[notification.id]" class="spinner-border spinner-border-sm me-1"></span>
                <i v-else class="fas fa-check me-1"></i>
                {{ t('notifications.marquerLue') }}
              </button>

              <button
                v-if="notification.actionUrl"
                @click="naviguerVersAction(notification)"
                class="btn btn-outline-primary btn-sm"
              >
                <i class="fas fa-external-link-alt me-1"></i>
                {{ t('commun.voir') || 'Voir' }}
              </button>

              <button
                @click="supprimerNotification(notification.id)"
                class="btn btn-outline-danger btn-sm"
                :disabled="actionsEnCours[notification.id]"
              >
                <i class="fas fa-trash me-1"></i>
                {{ t('commun.supprimer') }}
              </button>
            </div>

            <div v-if="isNouvelle(notification)" class="position-absolute top-0 end-0 p-2">
              <span class="badge bg-warning">{{ t('notifications.nouveau') }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Aucune notification -->
      <div v-else-if="!chargement" class="card-collabpro">
        <div class="card-body text-center py-5">
          <i class="fas fa-bell-slash fa-3x text-muted mb-3"></i>
          <h4>{{ t('notifications.aucuneNotification') }}</h4>
          <p class="text-muted">{{ t('notifications.aucuneDesc') }}</p>
          <button class="btn btn-collabpro" @click="reinitialiserFiltres">
            <i class="fas fa-refresh me-2"></i>{{ t('notifications.reinitialiserFiltres') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import notificationService from '@/services/notification.service.js'
import websocketService from '@/services/websocket.service.js'

const router = useRouter()
const { t, locale } = useI18n()

// États
const notifications = ref([])
const chargement = ref(false)
const filtreType = ref('')
const filtreStatut = ref('')
const filtrePeriode = ref('')
const rechercheTexte = ref('')
const actionsEnCours = ref({})
const websocketConnecte = ref(false)

const statistiques = reactive({
  total: 0, nonLues: 0, lues: 0, aujourdhui: 0
})

const message = reactive({ texte: '', type: '' })

// Helpers i18n
const rtf = computed(() => new Intl.RelativeTimeFormat(locale.value || 'fr-FR', { numeric: 'auto' }))

// Computed
const notificationsFiltrees = computed(() => {
  let filtrees = notifications.value

  if (filtreType.value) {
    filtrees = filtrees.filter(n => n.type === filtreType.value)
  }

  if (filtreStatut.value !== '') {
    const estLue = filtreStatut.value === 'true'
    filtrees = filtrees.filter(n => n.lu === estLue)
  }

  if (filtrePeriode.value) {
    const maintenant = new Date()
    filtrees = filtrees.filter(n => {
      const d = new Date(n.dateCreation)
      switch (filtrePeriode.value) {
        case 'today': return d.toDateString() === maintenant.toDateString()
        case 'week':  return d >= new Date(maintenant.getTime() - 7 * 86400000)
        case 'month': return d >= new Date(maintenant.getTime() - 30 * 86400000)
        default: return true
      }
    })
  }

  if (rechercheTexte.value) {
    const r = rechercheTexte.value.toLowerCase()
    filtrees = filtrees.filter(n =>
      n.titre?.toLowerCase().includes(r) ||
      n.message?.toLowerCase().includes(r)
    )
  }

  return filtrees.sort((a, b) => new Date(b.dateCreation) - new Date(a.dateCreation))
})

const hasNotificationsNonLues = computed(() => notifications.value.some(n => !n.lu))
const hasNotificationsLues = computed(() => notifications.value.some(n => n.lu))

// Méthodes utilitaires
const afficherMessage = (texte, type = 'success') => {
  message.texte = texte
  message.type = type
  setTimeout(() => { message.texte = ''; message.type = '' }, 5000)
}

const formatDate = (iso) => {
  if (!iso) return ''
  const date = new Date(iso)
  const diffMs = Date.now() - date.getTime()

  const minutes = Math.round(diffMs / 60000)
  const hours = Math.round(diffMs / 3600000)
  const days = Math.round(diffMs / 86400000)

  if (Math.abs(minutes) < 60) return rtf.value.format(-minutes, 'minute')
  if (Math.abs(hours) < 24)   return rtf.value.format(-hours, 'hour')
  if (Math.abs(days) < 7)     return rtf.value.format(-days, 'day')

  return date.toLocaleString(locale.value || 'fr-FR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}

const formatMontant = (montant) => {
  const n = Number.isFinite(montant) ? montant : 0
  return new Intl.NumberFormat(
    locale.value?.startsWith('fr') ? 'fr-FR' : 'en-US',
    { style: 'currency', currency: 'EUR' }
  ).format(n)
}

const getTypeIcon = (type) => ({
  TACHE: 'fas fa-tasks',
  PROJET: 'fas fa-project-diagram',
  PAIEMENT: 'fas fa-credit-card',
  INVITATION: 'fas fa-user-plus',
  SYSTEME: 'fas fa-cog'
}[type] || 'fas fa-bell')

const getTypeColor = (type) => ({
  TACHE: '#007bff',
  PROJET: '#28a745',
  PAIEMENT: '#ffc107',
  INVITATION: '#17a2b8',
  SYSTEME: '#6c757d'
}[type] || '#007bff')

const getTypeLabel = (type) => ({
  TACHE: t('notifications.types.tache'),
  PROJET: t('notifications.types.projet'),
  PAIEMENT: t('notifications.types.paiement'),
  INVITATION: t('notifications.types.invitation'),
  SYSTEME: t('notifications.types.systeme')
}[type] || type)

const isNouvelle = (n) => {
  const diffMs = Date.now() - new Date(n.dateCreation).getTime()
  return diffMs < 5 * 60 * 1000 && !n.lu
}

// API
const chargerNotifications = async () => {
  try {
    chargement.value = true
    message.texte = ''

    const params = {
      type: filtreType.value || undefined,
      lu: filtreStatut.value !== '' ? filtreStatut.value === 'true' : undefined,
      periode: filtrePeriode.value || undefined,
      recherche: rechercheTexte.value || undefined
    }

    const result = await notificationService.getNotifications(params)

    if (result.success) {
      notifications.value = result.data || []
      calculerStatistiques()
    } else {
      throw new Error(result.message || 'load_error')
    }
  } catch (error) {
    console.error('Erreur chargement notifications:', error)
    afficherMessage(t('notifications.erreurChargement'), 'error')
  } finally {
    chargement.value = false
  }
}

const calculerStatistiques = () => {
  statistiques.total = notifications.value.length
  statistiques.nonLues = notifications.value.filter(n => !n.lu).length
  statistiques.lues = notifications.value.filter(n => n.lu).length
  const ajd = new Date().toDateString()
  statistiques.aujourdhui = notifications.value.filter(n =>
    new Date(n.dateCreation).toDateString() === ajd
  ).length
}

const marquerCommeLue = async (id) => {
  try {
    actionsEnCours.value[id] = true
    const result = await notificationService.marquerCommeLue(id)
    if (result.success) {
      const n = notifications.value.find(x => x.id === id)
      if (n) n.lu = true
      calculerStatistiques()
      afficherMessage(t('notifications.msgLue'), 'success')
    } else {
      throw new Error(result.message || 'mark_error')
    }
  } catch (error) {
    console.error('Erreur marquer comme lue:', error)
    afficherMessage(t('notifications.msgErreurMarquage'), 'error')
  } finally {
    actionsEnCours.value[id] = false
  }
}

const marquerToutesLues = async () => {
  try {
    chargement.value = true
    const result = await notificationService.marquerToutesLues()
    if (result.success) {
      notifications.value.forEach(n => { n.lu = true })
      calculerStatistiques()
      afficherMessage(t('notifications.msgToutesLues'), 'success')
    } else {
      throw new Error(result.message || 'mark_all_error')
    }
  } catch (error) {
    console.error('Erreur marquer toutes comme lues:', error)
    afficherMessage(t('notifications.msgErreurMarquage'), 'error')
  } finally {
    chargement.value = false
  }
}

const supprimerNotification = async (id) => {
  if (!confirm(t('notifications.confirmerSuppression'))) return
  try {
    actionsEnCours.value[id] = true
    const result = await notificationService.supprimerNotification(id)
    if (result.success) {
      notifications.value = notifications.value.filter(n => n.id !== id)
      calculerStatistiques()
      afficherMessage(t('notifications.msgSupprimee'), 'success')
    } else {
      throw new Error(result.message || 'delete_error')
    }
  } catch (error) {
    console.error('Erreur suppression notification:', error)
    afficherMessage(t('notifications.msgErreurSuppression'), 'error')
  } finally {
    actionsEnCours.value[id] = false
  }
}

const supprimerToutesLues = async () => {
  if (!confirm(t('notifications.confirmerSuppressionLues'))) return
  try {
    chargement.value = true
    const result = await notificationService.supprimerToutesLues()
    if (result.success) {
      notifications.value = notifications.value.filter(n => !n.lu)
      calculerStatistiques()
      afficherMessage(t('notifications.msgLuesSupprimees'), 'success')
    } else {
      throw new Error(result.message || 'delete_read_error')
    }
  } catch (error) {
    console.error('Erreur suppression toutes lues:', error)
    afficherMessage(t('notifications.msgErreurSuppression'), 'error')
  } finally {
    chargement.value = false
  }
}

const naviguerVersAction = (n) => {
  if (n.actionUrl) {
    router.push(n.actionUrl)
    if (!n.lu) marquerCommeLue(n.id)
  }
}

const appliquerFiltres = () => { chargerNotifications() }
const rechercherNotifications = () => {
  clearTimeout(window.__notifSearchTimer)
  window.__notifSearchTimer = setTimeout(() => chargerNotifications(), 500)
}
const reinitialiserFiltres = () => {
  filtreType.value = ''
  filtreStatut.value = ''
  filtrePeriode.value = ''
  rechercheTexte.value = ''
  chargerNotifications()
}

// WebSocket temps réel
const initWebSocket = () => {
  const token = localStorage.getItem('token')
  if (!token) return
  websocketService.connect(token)
  websocketConnecte.value = websocketService.connected

  websocketService.subscribeToProject('notifications', (msg) => {
    if (msg.type === 'NOTIFICATION') {
      notifications.value.unshift({
        id: Date.now(),
        titre: t('notifications.nouvelle'),
        message: msg.contenu,
        type: 'SYSTEME',
        lu: false,
        dateCreation: new Date().toISOString(),
        donnees: msg.donnees || {}
      })
      calculerStatistiques()
      afficherMessage(t('notifications.nouvelleRecue'), 'success')
    }
  })
}

// Lifecycle
onMounted(() => { chargerNotifications(); initWebSocket() })
onUnmounted(() => { websocketService.disconnect() })
</script>

<style scoped>
.notifications-view { min-height: 100vh; padding: 20px 0; }
.notification-item { position: relative; transition: all .3s ease; }
.notification-item:hover { background-color: rgba(43, 69, 248, 0.05) !important; }
@media (max-width: 768px) {
  .notifications-view { padding: 10px; }
  .d-flex.gap-2 { flex-direction: column; }
  .d-flex.gap-2 .btn { width: 100%; margin-bottom: .5rem; }
}
</style>
