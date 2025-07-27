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
                Mes Notifications
              </h1>
              <p class="text-muted mb-0">Gérez vos notifications en temps réel</p>
            </div>
            <div class="col-md-4 text-end">
              <div class="d-flex gap-2 flex-wrap">
                <button
                  @click="marquerToutesLues"
                  class="btn btn-collabpro btn-sm"
                  :disabled="!hasNotificationsNonLues || chargement"
                >
                  <i class="fas fa-check-double me-2"></i>
                  Marquer tout lu
                </button>
                <button
                  @click="supprimerToutesLues"
                  class="btn btn-outline-danger btn-sm"
                  :disabled="!hasNotificationsLues || chargement"
                >
                  <i class="fas fa-trash me-2"></i>
                  Supprimer lues
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
                <p class="text-muted mb-0">Total</p>
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
                <p class="text-muted mb-0">Non lues</p>
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
                <p class="text-muted mb-0">Lues</p>
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
                <p class="text-muted mb-0">Aujourd'hui</p>
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
              <label class="form-label fw-semibold">Filtrer par type</label>
              <select v-model="filtreType" @change="appliquerFiltres" class="form-select">
                <option value="">Tous les types</option>
                <option value="TACHE">Tâche</option>
                <option value="PROJET">Projet</option>
                <option value="PAIEMENT">Paiement</option>
                <option value="INVITATION">Invitation</option>
                <option value="SYSTEME">Système</option>
              </select>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">Filtrer par statut</label>
              <select v-model="filtreStatut" @change="appliquerFiltres" class="form-select">
                <option value="">Tous les statuts</option>
                <option value="false">Non lues</option>
                <option value="true">Lues</option>
              </select>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">Période</label>
              <select v-model="filtrePeriode" @change="appliquerFiltres" class="form-select">
                <option value="">Toutes les périodes</option>
                <option value="today">Aujourd'hui</option>
                <option value="week">Cette semaine</option>
                <option value="month">Ce mois</option>
              </select>
            </div>
            <div class="col-md-3 mb-3">
              <label class="form-label fw-semibold">Rechercher</label>
              <input
                v-model="rechercheTexte"
                @input="rechercherNotifications"
                type="text"
                class="form-control"
                placeholder="Rechercher une notification..."
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
          <span class="visually-hidden">Chargement...</span>
        </div>
        <p class="mt-2">Chargement des notifications...</p>
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
                Marquer lu
              </button>

              <button
                v-if="notification.actionUrl"
                @click="naviguerVersAction(notification)"
                class="btn btn-outline-primary btn-sm"
              >
                <i class="fas fa-external-link-alt me-1"></i>
                Voir
              </button>

              <button
                @click="supprimerNotification(notification.id)"
                class="btn btn-outline-danger btn-sm"
                :disabled="actionsEnCours[notification.id]"
              >
                <i class="fas fa-trash me-1"></i>
                Supprimer
              </button>
            </div>

            <div v-if="isNouvelle(notification)" class="position-absolute top-0 end-0 p-2">
              <span class="badge bg-warning">Nouveau</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Aucune notification -->
      <div v-else-if="!chargement" class="card-collabpro">
        <div class="card-body text-center py-5">
          <i class="fas fa-bell-slash fa-3x text-muted mb-3"></i>
          <h4>Aucune notification</h4>
          <p class="text-muted">Vous n'avez pas de notifications pour le moment.</p>
          <button class="btn btn-collabpro" @click="reinitialiserFiltres">
            <i class="fas fa-refresh me-2"></i>Réinitialiser les filtres
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import notificationService from '@/services/notification.service.js'
import websocketService from '@/services/websocket.service.js'

export default {
  name: 'NotificationsView',
  setup() {
    const router = useRouter()

    // États réactifs
    const notifications = ref([])
    const chargement = ref(false)
    const filtreType = ref('')
    const filtreStatut = ref('')
    const filtrePeriode = ref('')
    const rechercheTexte = ref('')
    const actionsEnCours = ref({})
    const websocketConnecte = ref(false)

    const statistiques = reactive({
      total: 0,
      nonLues: 0,
      lues: 0,
      aujourdhui: 0
    })

    const message = reactive({
      texte: '',
      type: ''
    })

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
          const dateNotif = new Date(n.dateCreation)
          switch (filtrePeriode.value) {
            case 'today':
              return dateNotif.toDateString() === maintenant.toDateString()
            case 'week':
              const debutSemaine = new Date(maintenant)
              debutSemaine.setDate(maintenant.getDate() - 7)
              return dateNotif >= debutSemaine
            case 'month':
              const debutMois = new Date(maintenant)
              debutMois.setDate(maintenant.getDate() - 30)
              return dateNotif >= debutMois
            default:
              return true
          }
        })
      }

      if (rechercheTexte.value) {
        const recherche = rechercheTexte.value.toLowerCase()
        filtrees = filtrees.filter(n =>
          n.titre?.toLowerCase().includes(recherche) ||
          n.message?.toLowerCase().includes(recherche)
        )
      }

      return filtrees.sort((a, b) => new Date(b.dateCreation) - new Date(a.dateCreation))
    })

    const hasNotificationsNonLues = computed(() => {
      return notifications.value.some(n => !n.lu)
    })

    const hasNotificationsLues = computed(() => {
      return notifications.value.some(n => n.lu)
    })

    // Méthodes utilitaires
    const afficherMessage = (texte, type = 'success') => {
      message.texte = texte
      message.type = type
      setTimeout(() => {
        message.texte = ''
        message.type = ''
      }, 5000)
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      const maintenant = new Date()
      const diffMs = maintenant - date
      const diffMins = Math.floor(diffMs / 60000)
      const diffHeures = Math.floor(diffMs / 3600000)
      const diffJours = Math.floor(diffMs / 86400000)

      if (diffMins < 1) return 'Maintenant'
      if (diffMins < 60) return `Il y a ${diffMins} min`
      if (diffHeures < 24) return `Il y a ${diffHeures}h`
      if (diffJours < 7) return `Il y a ${diffJours} jour${diffJours > 1 ? 's' : ''}`

      return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const formatMontant = (montant) => {
      if (typeof montant !== 'number') return '0,00 €'
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR'
      }).format(montant)
    }

    const getTypeIcon = (type) => {
      const icons = {
        'TACHE': 'fas fa-tasks',
        'PROJET': 'fas fa-project-diagram',
        'PAIEMENT': 'fas fa-credit-card',
        'INVITATION': 'fas fa-user-plus',
        'SYSTEME': 'fas fa-cog'
      }
      return icons[type] || 'fas fa-bell'
    }

    const getTypeColor = (type) => {
      const colors = {
        'TACHE': '#007bff',
        'PROJET': '#28a745',
        'PAIEMENT': '#ffc107',
        'INVITATION': '#17a2b8',
        'SYSTEME': '#6c757d'
      }
      return colors[type] || '#007bff'
    }

    const getTypeLabel = (type) => {
      const labels = {
        'TACHE': 'Tâche',
        'PROJET': 'Projet',
        'PAIEMENT': 'Paiement',
        'INVITATION': 'Invitation',
        'SYSTEME': 'Système'
      }
      return labels[type] || type
    }

    const isNouvelle = (notification) => {
      const maintenant = new Date()
      const dateNotif = new Date(notification.dateCreation)
      const diffMs = maintenant - dateNotif
      return diffMs < 300000 && !notification.lu // Nouvelle si < 5 minutes et non lue
    }

    // Méthodes principales
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
          throw new Error(result.message || 'Erreur lors du chargement des notifications')
        }
      } catch (error) {
        console.error('Erreur chargement notifications:', error)
        afficherMessage('Erreur lors du chargement des notifications', 'error')
      } finally {
        chargement.value = false
      }
    }

    const calculerStatistiques = () => {
      statistiques.total = notifications.value.length
      statistiques.nonLues = notifications.value.filter(n => !n.lu).length
      statistiques.lues = notifications.value.filter(n => n.lu).length

      const aujourdhui = new Date().toDateString()
      statistiques.aujourdhui = notifications.value.filter(n =>
        new Date(n.dateCreation).toDateString() === aujourdhui
      ).length
    }

    const marquerCommeLue = async (notificationId) => {
      try {
        actionsEnCours.value[notificationId] = true

        const result = await notificationService.marquerCommeLue(notificationId)

        if (result.success) {
          const notification = notifications.value.find(n => n.id === notificationId)
          if (notification) {
            notification.lu = true
            calculerStatistiques()
          }
          afficherMessage('Notification marquée comme lue', 'success')
        } else {
          throw new Error(result.message || 'Erreur marquer comme lue')
        }
      } catch (error) {
        console.error('Erreur marquer comme lue:', error)
        afficherMessage('Erreur lors du marquage', 'error')
      } finally {
        actionsEnCours.value[notificationId] = false
      }
    }

    const marquerToutesLues = async () => {
      try {
        chargement.value = true
        const result = await notificationService.marquerToutesLues()

        if (result.success) {
          notifications.value.forEach(n => n.lu = true)
          calculerStatistiques()
          afficherMessage('Toutes les notifications ont été marquées comme lues', 'success')
        } else {
          throw new Error(result.message || 'Erreur marquer toutes comme lues')
        }
      } catch (error) {
        console.error('Erreur marquer toutes comme lues:', error)
        afficherMessage('Erreur lors du marquage', 'error')
      } finally {
        chargement.value = false
      }
    }

    const supprimerNotification = async (notificationId) => {
      if (!confirm('Êtes-vous sûr de vouloir supprimer cette notification ?')) return

      try {
        actionsEnCours.value[notificationId] = true

        const result = await notificationService.supprimerNotification(notificationId)

        if (result.success) {
          const index = notifications.value.findIndex(n => n.id === notificationId)
          if (index !== -1) {
            notifications.value.splice(index, 1)
            calculerStatistiques()
          }
          afficherMessage('Notification supprimée', 'success')
        } else {
          throw new Error(result.message || 'Erreur suppression')
        }
      } catch (error) {
        console.error('Erreur suppression notification:', error)
        afficherMessage('Erreur lors de la suppression', 'error')
      } finally {
        actionsEnCours.value[notificationId] = false
      }
    }

    const supprimerToutesLues = async () => {
      if (!confirm('Êtes-vous sûr de vouloir supprimer toutes les notifications lues ?')) return

      try {
        chargement.value = true
        const result = await notificationService.supprimerToutesLues()

        if (result.success) {
          notifications.value = notifications.value.filter(n => !n.lu)
          calculerStatistiques()
          afficherMessage('Notifications lues supprimées', 'success')
        } else {
          throw new Error(result.message || 'Erreur suppression notifications lues')
        }
      } catch (error) {
        console.error('Erreur suppression toutes lues:', error)
        afficherMessage('Erreur lors de la suppression', 'error')
      } finally {
        chargement.value = false
      }
    }

    const naviguerVersAction = (notification) => {
      if (notification.actionUrl) {
        router.push(notification.actionUrl)
        if (!notification.lu) {
          marquerCommeLue(notification.id)
        }
      }
    }

    const appliquerFiltres = () => {
      chargerNotifications()
    }

    const rechercherNotifications = () => {
      clearTimeout(window.rechercheTimeout)
      window.rechercheTimeout = setTimeout(() => {
        chargerNotifications()
      }, 500)
    }

    const reinitialiserFiltres = () => {
      filtreType.value = ''
      filtreStatut.value = ''
      filtrePeriode.value = ''
      rechercheTexte.value = ''
      chargerNotifications()
    }

    // Initialisation WebSocket pour notifications temps réel (F9 + F12)
    const initWebSocket = () => {
      const token = localStorage.getItem('token')
      if (token) {
        websocketService.connect(token)
        websocketConnecte.value = websocketService.connected

        // Écouter les notifications en temps réel
        websocketService.subscribeToProject('notifications', (messageData) => {
          if (messageData.type === 'NOTIFICATION') {
            // Ajouter la nouvelle notification en temps réel
            const nouvelleNotification = {
              id: Date.now(), // ID temporaire
              titre: 'Nouvelle notification',
              message: messageData.contenu,
              type: 'SYSTEME',
              lu: false,
              dateCreation: new Date().toISOString(),
              donnees: messageData.donnees || {}
            }

            notifications.value.unshift(nouvelleNotification)
            calculerStatistiques()
            afficherMessage('Nouvelle notification reçue', 'success')
          }
        })
      }
    }

    // Cycle de vie
    onMounted(() => {
      chargerNotifications()
      initWebSocket()
    })

    onUnmounted(() => {
      websocketService.disconnect()
    })

    return {
      // États
      notifications,
      chargement,
      filtreType,
      filtreStatut,
      filtrePeriode,
      rechercheTexte,
      actionsEnCours,
      websocketConnecte,
      statistiques,
      message,

      // Computed
      notificationsFiltrees,
      hasNotificationsNonLues,
      hasNotificationsLues,

      // Méthodes
      formatDate,
      formatMontant,
      getTypeIcon,
      getTypeColor,
      getTypeLabel,
      isNouvelle,
      chargerNotifications,
      marquerCommeLue,
      marquerToutesLues,
      supprimerNotification,
      supprimerToutesLues,
      naviguerVersAction,
      appliquerFiltres,
      rechercherNotifications,
      reinitialiserFiltres
    }
  }
}
</script>

<style scoped>
.notifications-view {
  min-height: 100vh;
  padding: 20px 0;
}

.notification-item {
  position: relative;
  transition: all 0.3s ease;
}

.notification-item:hover {
  background-color: rgba(43, 69, 248, 0.05) !important;
}

@media (max-width: 768px) {
  .notifications-view {
    padding: 10px;
  }

  .d-flex.gap-2 {
    flex-direction: column;
  }

  .d-flex.gap-2 .btn {
    width: 100%;
    margin-bottom: 0.5rem;
  }
}
</style>
