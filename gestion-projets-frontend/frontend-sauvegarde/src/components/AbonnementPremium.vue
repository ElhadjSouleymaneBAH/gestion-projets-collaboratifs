<template>
  <div class="abonnement-premium">
    <!-- F12 : Interface multilingue FR/EN -->
    <div class="header-section">
      <h1 class="title">{{ $t('abonnement.titre') }}</h1>
      <p class="subtitle">{{ $t('abonnement.description') }}</p>

      <!-- Indicateur de statut utilisateur -->
      <div class="user-status" :class="statusClass">
        <i :class="statusIcon"></i>
        <span>{{ statusText }}</span>
      </div>
    </div>

    <!-- F10 : Plan Premium Mensuel (Chef de Projet obligatoire) -->
    <div class="premium-card">
      <div class="card-header">
        <h2 class="plan-name">{{ $t('abonnement.planPremium') }}</h2>
        <div class="price-section">
          <span class="price">10€</span>
          <span class="period">/ {{ $t('abonnement.mois') }}</span>
        </div>
        <p class="obligation-text">{{ $t('abonnement.obligatoire') }}</p>
      </div>

      <div class="card-body">
        <!-- F6-F11 : Fonctionnalités Chef de Projet -->
        <ul class="features-list">
          <li class="feature-item">
            <i class="icon-check"></i>
            <span>{{ $t('abonnement.fonctionnalites.gererProjets') }}</span> <!-- F6 -->
          </li>
          <li class="feature-item">
            <i class="icon-check"></i>
            <span>{{ $t('abonnement.fonctionnalites.gererTaches') }}</span> <!-- F7 -->
          </li>
          <li class="feature-item">
            <i class="icon-check"></i>
            <span>{{ $t('abonnement.fonctionnalites.ajouterMembres') }}</span> <!-- F8 -->
          </li>
          <li class="feature-item">
            <i class="icon-check"></i>
            <span>{{ $t('abonnement.fonctionnalites.collaborationTempsReel') }}</span> <!-- F9 -->
          </li>
          <li class="feature-item">
            <i class="icon-check"></i>
            <span>{{ $t('abonnement.fonctionnalites.paiementsStripe') }}</span> <!-- F10 -->
          </li>
          <li class="feature-item">
            <i class="icon-check"></i>
            <span>{{ $t('abonnement.fonctionnalites.facturesPDF') }}</span> <!-- F11 -->
          </li>
          <li class="feature-item">
            <i class="icon-check"></i>
            <span>{{ $t('abonnement.fonctionnalites.notifications') }}</span>
          </li>
          <li class="feature-item">
            <i class="icon-check"></i>
            <span>{{ $t('abonnement.fonctionnalites.multilingue') }}</span> <!-- F12 -->
          </li>
        </ul>

        <!-- Statut abonnement actuel -->
        <div v-if="currentSubscription" class="subscription-status">
          <div class="status-active">
            <i class="icon-success"></i>
            <span>{{ $t('abonnement.statut.actif') }}</span>
          </div>
          <div class="subscription-details">
            <p class="next-billing">
              <strong>{{ $t('abonnement.prochainPaiement') }}:</strong>
              {{ formatDate(currentSubscription.prochainPaiement) }}
            </p>
            <p class="subscription-info">
              <strong>{{ $t('abonnement.abonnementActif') }}:</strong>
              {{ currentSubscription.nom }}
            </p>
          </div>
        </div>

        <!-- Action selon le rôle utilisateur -->
        <div class="action-buttons">
          <!-- Si pas encore Chef de Projet : abonnement obligatoire -->
          <button
            v-if="!isChefProjet && !currentSubscription"
            @click="initializePayment"
            :disabled="loading"
            class="btn-subscribe"
          >
            <span v-if="loading" class="spinner"></span>
            <i v-else class="icon-crown"></i>
            {{ loading ? $t('commun.chargement') : $t('abonnement.souscrire') }}
          </button>

          <!-- Si déjà Chef de Projet : gestion abonnement -->
          <div v-else-if="isChefProjet && currentSubscription" class="subscription-actions">
            <button @click="voirFactures" class="btn-manage">
              <i class="icon-invoice"></i>
              {{ $t('abonnement.voirFactures') }} <!-- F11 -->
            </button>
            <button @click="gererAbonnement" class="btn-portal">
              <i class="icon-settings"></i>
              {{ $t('abonnement.gerer') }}
            </button>
            <button @click="annulerAbonnement" class="btn-cancel">
              <i class="icon-cancel"></i>
              {{ $t('abonnement.annuler') }}
            </button>
          </div>

          <!-- Message pour Visiteur/Membre -->
          <div v-else-if="!isChefProjet" class="role-message">
            <i class="icon-info"></i>
            <p>{{ $t('abonnement.messageRole') }}</p>
            <small>{{ $t('abonnement.avantages') }}</small>
          </div>
        </div>
      </div>
    </div>

    <!-- F10 : Informations paiement sécurisé Stripe -->
    <div v-if="!currentSubscription" class="payment-info">
      <h3>{{ $t('abonnement.paiementSecurise') }}</h3>
      <div class="security-features">
        <div class="security-item">
          <i class="icon-shield"></i>
          <span>{{ $t('abonnement.chiffrementSSL') }}</span>
        </div>
        <div class="security-item">
          <i class="icon-stripe"></i>
          <span>{{ $t('abonnement.stripeDescription') }}</span>
        </div>
        <div class="security-item">
          <i class="icon-secure"></i>
          <span>{{ $t('abonnement.aucuneStockage') }}</span>
        </div>
      </div>
    </div>

    <!-- F11 : Aperçu des factures récentes -->
    <div v-if="isChefProjet && recentInvoices.length > 0" class="recent-invoices">
      <h3>{{ $t('abonnement.facturesRecentes') }}</h3>
      <div class="invoices-list">
        <div
          v-for="invoice in recentInvoices"
          :key="invoice.id"
          class="invoice-item"
          @click="downloadInvoice(invoice.id)"
        >
          <div class="invoice-info">
            <span class="invoice-number">{{ invoice.numeroFacture }}</span>
            <span class="invoice-amount">{{ formatCurrency(invoice.montantTTC) }}</span>
          </div>
          <div class="invoice-date">{{ formatDate(invoice.dateEmission) }}</div>
          <i class="icon-download"></i>
        </div>
      </div>
    </div>

    <!-- F10 : Modal paiement Stripe -->
    <div v-if="showPaymentForm" class="payment-modal" @click.self="closePaymentForm">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ $t('abonnement.paiement.titre') }}</h3>
          <button @click="closePaymentForm" class="btn-close">&times;</button>
        </div>
        <div class="modal-body">
          <div class="payment-summary">
            <h4>{{ $t('abonnement.resumeCommande') }}</h4>
            <div class="summary-line">
              <span>{{ $t('abonnement.planPremium') }}</span>
              <span><strong>10,00€</strong></span>
            </div>
            <div class="summary-line">
              <span>{{ $t('abonnement.tva') }} (21%)</span>
              <span>2,10€</span>
            </div>
            <div class="summary-total">
              <span>{{ $t('abonnement.total') }}</span>
              <span><strong>12,10€</strong></span>
            </div>
          </div>

          <div class="payment-form">
            <label>{{ $t('abonnement.paiement.infosCarte') }}</label>
            <div id="stripe-card-element" class="stripe-element"></div>
            <div id="card-errors" class="error-message"></div>
          </div>

          <div class="payment-actions">
            <button @click="processPayment" :disabled="processingPayment" class="btn-pay">
              <span v-if="processingPayment" class="spinner"></span>
              <i v-else class="icon-lock"></i>
              {{ processingPayment ? $t('commun.traitement') : $t('abonnement.paiement.confirmer') }}
            </button>
            <button @click="closePaymentForm" class="btn-cancel-payment">
              {{ $t('commun.annuler') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Notifications et messages -->
    <div v-if="notifications.length > 0" class="notifications-section">
      <h3>{{ $t('abonnement.notifications') }}</h3>
      <div class="notifications-list">
        <div
          v-for="notification in notifications"
          :key="notification.id"
          :class="['notification-item', { 'unread': !notification.lu }]"
          @click="markAsRead(notification.id)"
        >
          <div class="notification-content">
            <p>{{ notification.message }}</p>
            <small>{{ formatDateTime(notification.date) }}</small>
          </div>
          <i v-if="!notification.lu" class="icon-unread"></i>
        </div>
      </div>
    </div>

    <!-- Messages de retour -->
    <transition name="message-fade">
      <div v-if="message" :class="['message', messageType]">
        <i :class="messageIcon"></i>
        <span>{{ message }}</span>
        <button @click="clearMessage" class="message-close">&times;</button>
      </div>
    </transition>

    <!-- FAQ Section -->
    <div class="faq-section">
      <h3>{{ $t('abonnement.faq.titre') }}</h3>
      <div class="faq-list">
        <div
          v-for="(faq, index) in faqs"
          :key="index"
          class="faq-item"
          :class="{ 'active': faq.open }"
        >
          <div class="faq-question" @click="toggleFaq(index)">
            <span>{{ faq.question }}</span>
            <i :class="['icon-chevron', { 'open': faq.open }]"></i>
          </div>
          <transition name="faq-content">
            <div v-if="faq.open" class="faq-answer">
              <p>{{ faq.answer }}</p>
            </div>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useStore } from 'vuex'
import { loadStripe } from '@stripe/stripe-js'
import axiosInstance from '@/services/api'

export default {
  name: 'AbonnementPremium',
  setup() {
    const { t } = useI18n()
    const store = useStore()

    // États réactifs
    const loading = ref(false)
    const processingPayment = ref(false)
    const showPaymentForm = ref(false)
    const currentSubscription = ref(null)
    const recentInvoices = ref([])
    const notifications = ref([])
    const message = ref('')
    const messageType = ref('success')
    const stripe = ref(null)
    const cardElement = ref(null)

    // FAQ data
    const faqs = ref([
      {
        question: t('abonnement.faq.q1'),
        answer: t('abonnement.faq.a1'),
        open: false
      },
      {
        question: t('abonnement.faq.q2'),
        answer: t('abonnement.faq.a2'),
        open: false
      },
      {
        question: t('abonnement.faq.q3'),
        answer: t('abonnement.faq.a3'),
        open: false
      },
      {
        question: t('abonnement.faq.q4'),
        answer: t('abonnement.faq.a4'),
        open: false
      }
    ])

    // Propriétés calculées
    const user = computed(() => store.getters.currentUser)
    const isChefProjet = computed(() => user.value?.role === 'CHEF_PROJET')

    const statusClass = computed(() => {
      if (isChefProjet.value) return 'status-premium'
      return 'status-standard'
    })

    const statusIcon = computed(() => {
      if (isChefProjet.value) return 'icon-crown'
      return 'icon-user'
    })

    const statusText = computed(() => {
      if (isChefProjet.value) return t('abonnement.statutPremium')
      return t('abonnement.statutStandard')
    })

    const messageIcon = computed(() => {
      switch (messageType.value) {
        case 'success': return 'icon-check-circle'
        case 'error': return 'icon-x-circle'
        case 'warning': return 'icon-alert-triangle'
        default: return 'icon-info'
      }
    })

    // F10 : Charger abonnement actuel
    const loadCurrentSubscription = async () => {
      if (!isChefProjet.value) return

      try {
        const response = await axiosInstance.get('/api/abonnements/current')
        currentSubscription.value = response.data
      } catch (error) {
        console.error('Erreur chargement abonnement:', error)
      }
    }

    // F11 : Charger factures récentes
    const loadRecentInvoices = async () => {
      if (!isChefProjet.value) return

      try {
        const response = await axiosInstance.get('/api/factures/recentes?limit=3')
        recentInvoices.value = response.data
      } catch (error) {
        console.error('Erreur chargement factures:', error)
      }
    }

    // Charger notifications
    const loadNotifications = async () => {
      if (!isChefProjet.value) return

      try {
        const response = await axiosInstance.get('/api/notifications/recentes')
        notifications.value = response.data.slice(0, 5) // Limiter à 5
      } catch (error) {
        console.error('Erreur chargement notifications:', error)
      }
    }

    // F10 : Initialiser paiement Stripe
    const initializePayment = async () => {
      loading.value = true
      try {
        const stripePublicKey = process.env.VUE_APP_STRIPE_PUBLIC_KEY
        if (!stripePublicKey) {
          throw new Error('Clé Stripe manquante')
        }

        stripe.value = await loadStripe(stripePublicKey)
        showPaymentForm.value = true

        // Création Stripe Elements avec style personnalisé
        setTimeout(() => {
          const elements = stripe.value.elements({
            locale: t('commun.locale')
          })

          cardElement.value = elements.create('card', {
            style: {
              base: {
                fontSize: '16px',
                color: '#424770',
                '::placeholder': {
                  color: '#aab7c4',
                },
                iconColor: '#666EE8',
                fontWeight: '500',
                fontFamily: '"Segoe UI", Roboto, sans-serif',
                fontSmoothing: 'antialiased',
              },
              invalid: {
                color: '#fa755a',
                iconColor: '#fa755a'
              }
            },
            hidePostalCode: true
          })

          cardElement.value.mount('#stripe-card-element')

          // Gestion des erreurs en temps réel
          cardElement.value.on('change', (event) => {
            const displayError = document.getElementById('card-errors')
            if (event.error) {
              displayError.textContent = event.error.message
            } else {
              displayError.textContent = ''
            }
          })
        }, 100)
      } catch (error) {
        showMessage(t('abonnement.erreur.initialisation'), 'error')
        console.error('Erreur initialisation Stripe:', error)
      } finally {
        loading.value = false
      }
    }

    // F10 : Traiter paiement Stripe (10€/mois)
    const processPayment = async () => {
      processingPayment.value = true
      try {
        // Créer intention de paiement
        const { data } = await axiosInstance.post('/api/paiements/create-payment-intent', {
          amount: 1210, // 12.10€ en centimes (10€ + 21% TVA)
          currency: 'eur',
          subscriptionType: 'premium',
          description: 'Abonnement Chef de Projet - Plan Premium Mensuel'
        })

        // Confirmer paiement avec Stripe
        const { error, paymentIntent } = await stripe.value.confirmCardPayment(
          data.clientSecret,
          {
            payment_method: {
              card: cardElement.value,
              billing_details: {
                name: `${user.value.prenom} ${user.value.nom}`,
                email: user.value.email,
              },
            },
          }
        )

        if (error) {
          showMessage(error.message, 'error')
        } else if (paymentIntent.status === 'succeeded') {
          // F10 : Créer abonnement (✅ SANS stocker la réponse)
          await axiosInstance.post('/api/abonnements', {
            nom: 'Plan Premium Mensuel',
            prix: 10,
            duree: 1,
            paymentIntentId: paymentIntent.id
          })

          showMessage(t('abonnement.paiement.succes'), 'success')
          closePaymentForm()

          // Mettre à jour le rôle utilisateur en Chef de Projet
          await store.dispatch('updateUserRole', 'CHEF_PROJET')

          // Recharger les données
          await Promise.all([
            loadCurrentSubscription(),
            loadRecentInvoices(),
            loadNotifications()
          ])

          // Notification de bienvenue
          setTimeout(() => {
            showMessage(t('abonnement.bienvenue'), 'success')
          }, 2000)
        }
      } catch (error) {
        console.error('Erreur paiement:', error)
        showMessage(t('abonnement.paiement.erreur'), 'error')
      } finally {
        processingPayment.value = false
      }
    }

    // F11 : Voir factures PDF
    const voirFactures = () => {
      // Navigation vers la vue des factures
      store.dispatch('navigation/goToInvoices')
    }

    // F11 : Télécharger facture
    const downloadInvoice = async (invoiceId) => {
      try {
        const response = await axiosInstance.get(`/api/factures/${invoiceId}/download`, {
          responseType: 'blob'
        })

        // Créer un lien de téléchargement
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `facture-${invoiceId}.pdf`)
        document.body.appendChild(link)
        link.click()
        link.remove()
        window.URL.revokeObjectURL(url)

        showMessage(t('abonnement.factureTelechargee'), 'success')
      } catch (error) {
        showMessage(t('abonnement.erreur.telechargement'), 'error')
      }
    }

    // Gérer abonnement (portail Stripe)
    const gererAbonnement = async () => {
      try {
        const { data } = await axiosInstance.post('/api/abonnements/create-portal-session')
        window.open(data.url, '_blank')
      } catch (error) {
        showMessage(t('abonnement.erreur.portail'), 'error')
      }
    }

    // F10 : Annuler abonnement
    const annulerAbonnement = async () => {
      if (confirm(t('abonnement.confirmerAnnulation'))) {
        try {
          await axiosInstance.delete(`/api/abonnements/${currentSubscription.value.id}`)
          showMessage(t('abonnement.annulation.succes'), 'success')
          currentSubscription.value = null

          // Remettre en rôle Membre
          await store.dispatch('updateUserRole', 'MEMBRE')
        } catch (error) {
          showMessage(t('abonnement.annulation.erreur'), 'error')
        }
      }
    }

    // Marquer notification comme lue
    const markAsRead = async (notificationId) => {
      try {
        await axiosInstance.put(`/api/notifications/${notificationId}/marquer-lue`)

        // Mettre à jour localement
        const notification = notifications.value.find(n => n.id === notificationId)
        if (notification) {
          notification.lu = true
        }

        // Mettre à jour le store
        store.dispatch('notifications/markAsRead', notificationId)
      } catch (error) {
        console.error('Erreur marquage notification:', error)
      }
    }

    // Utilitaires
    const closePaymentForm = () => {
      showPaymentForm.value = false
      if (cardElement.value) {
        cardElement.value.destroy()
        cardElement.value = null
      }
    }

    const toggleFaq = (index) => {
      faqs.value[index].open = !faqs.value[index].open
    }

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleDateString(t('commun.locale'))
    }

    const formatDateTime = (dateString) => {
      return new Date(dateString).toLocaleString(t('commun.locale'))
    }

    const formatCurrency = (amount) => {
      return new Intl.NumberFormat(t('commun.locale'), {
        style: 'currency',
        currency: 'EUR'
      }).format(amount)
    }

    const showMessage = (text, type = 'success') => {
      message.value = text
      messageType.value = type
      setTimeout(() => {
        clearMessage()
      }, 5000)
    }

    const clearMessage = () => {
      message.value = ''
    }

    // Watchers
    watch(() => user.value?.role, (newRole) => {
      if (newRole === 'CHEF_PROJET') {
        loadCurrentSubscription()
        loadRecentInvoices()
        loadNotifications()
      }
    })

    // Lifecycle
    onMounted(async () => {
      await Promise.all([
        loadCurrentSubscription(),
        loadRecentInvoices(),
        loadNotifications()
      ])
    })

    return {
      // States
      loading,
      processingPayment,
      showPaymentForm,
      currentSubscription,
      recentInvoices,
      notifications,
      message,
      messageType,
      faqs,
      // Computed
      user,
      isChefProjet,
      statusClass,
      statusIcon,
      statusText,
      messageIcon,
      // Methods
      initializePayment,
      processPayment,
      voirFactures,
      downloadInvoice,
      gererAbonnement,
      annulerAbonnement,
      markAsRead,
      closePaymentForm,
      toggleFaq,
      formatDate,
      formatDateTime,
      formatCurrency,
      clearMessage
    }
  }
}
</script>

<style scoped>
.abonnement-premium {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
}

.header-section {
  text-align: center;
  margin-bottom: 3rem;
}

.title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.subtitle {
  font-size: 1.2rem;
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.user-status {
  display: inline-flex;
  align-items: center;
  padding: 0.75rem 1.5rem;
  border-radius: 50px;
  font-weight: 600;
  margin-bottom: 1rem;
}

.user-status i {
  margin-right: 0.5rem;
  font-size: 1.2rem;
}

.status-premium {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.status-standard {
  background: #f8f9fa;
  color: #6c757d;
  border: 2px solid #e9ecef;
}

.premium-card {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  border-radius: 20px;
  color: white;
  margin-bottom: 3rem;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  position: relative;
}

.premium-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #f39c12, #e74c3c, #9b59b6, #3498db);
}

.card-header {
  padding: 2.5rem 2rem;
  text-align: center;
  background: rgba(255,255,255,0.1);
  backdrop-filter: blur(10px);
}

.plan-name {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.price-section {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.period {
  font-size: 1.2rem;
  opacity: 0.8;
}

.obligation-text {
  font-size: 0.95rem;
  opacity: 0.9;
  font-style: italic;
  margin: 0;
}

.card-body {
  padding: 2.5rem 2rem;
}

.features-list {
  list-style: none;
  padding: 0;
  margin-bottom: 2.5rem;
  display: grid;
  gap: 1rem;
}

.feature-item {
  display: flex;
  align-items: center;
  font-size: 1.1rem;
  padding: 0.5rem 0;
}

.icon-check::before {
  content: '✓';
  display: inline-block;
  width: 24px;
  height: 24px;
  background: #27ae60;
  color: white;
  border-radius: 50%;
  text-align: center;
  line-height: 24px;
  margin-right: 1rem;
  font-size: 0.9rem;
  font-weight: bold;
}

.subscription-status {
  background: rgba(255,255,255,0.1);
  padding: 1.5rem;
  border-radius: 15px;
  margin-bottom: 2rem;
  backdrop-filter: blur(10px);
}

.status-active {
  display: flex;
  align-items: center;
  font-weight: 600;
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.icon-success::before {
  content: '✓';
  background: #27ae60;
  border-radius: 50%;
  color: white;
  width: 24px;
  height: 24px;
  display: inline-block;
  text-align: center;
  line-height: 24px;
  margin-right: 0.75rem;
  font-weight: bold;
}

.subscription-details p {
  margin: 0.5rem 0;
  opacity: 0.95;
}

.action-buttons {
  text-align: center;
}

.btn-subscribe {
  background: linear-gradient(135deg, #e67e22 0%, #d35400 100%);
  color: white;
  border: none;
  padding: 1.25rem 2.5rem;
  border-radius: 50px;
  font-size: 1.2rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  min-width: 200px;
}

.btn-subscribe:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.2);
}

.btn-subscribe:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.subscription-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-manage, .btn-portal, .btn-cancel {
  padding: 1rem 1.5rem;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-manage {
  background: #3498db;
  color: white;
}

.btn-portal {
  background: #9b59b6;
  color: white;
}

.btn-cancel {
  background: #e74c3c;
  color: white;
}

.btn-manage:hover, .btn-portal:hover, .btn-cancel:hover {
  transform: translateY(-1px);
  opacity: 0.9;
}

.role-message {
  background: rgba(255,255,255,0.1);
  padding: 2rem;
  border-radius: 15px;
  text-align: center;
}

.role-message i {
  font-size: 2rem;
  margin-bottom: 1rem;
  opacity: 0.7;
}

.role-message p {
  margin: 1rem 0;
  font-size: 1.1rem;
}

.role-message small {
  opacity: 0.8;
  display: block;
  margin-top: 0.5rem;
}

.payment-info {
  background: #f8f9fa;
  padding: 2.5rem;
  border-radius: 20px;
  margin-bottom: 3rem;
  text-align: center;
}

.payment-info h3 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

.security-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.security-item {
  display: flex;
  align-items: center;
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.security-item:hover {
  transform: translateY(-2px);
}

.security-item i {
  font-size: 1.5rem;
  margin-right: 1rem;
  color: #27ae60;
}

.recent-invoices {
  background: #f8f9fa;
  padding: 2rem;
  border-radius: 20px;
  margin-bottom: 3rem;
}

.recent-invoices h3 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.invoices-list {
  display: grid;
  gap: 1rem;
}

.invoice-item {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.invoice-item:hover {
  border-color: #3498db;
  transform: translateY(-1px);
}

.invoice-info {
  display: flex;
  flex-direction: column;
}

.invoice-number {
  font-weight: 600;
  color: #2c3e50;
}

.invoice-amount {
  color: #27ae60;
  font-weight: 600;
  font-size: 1.1rem;
}

.invoice-date {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.notifications-section {
  margin-bottom: 3rem;
}

.notifications-section h3 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.notifications-list {
  display: grid;
  gap: 1rem;
}

.notification-item {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.notification-item.unread {
  border-left-color: #3498db;
  background: #f0f8ff;
}

.notification-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.notification-content p {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
}

.notification-content small {
  color: #7f8c8d;
}

.payment-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: white;
  border-radius: 20px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 50px rgba(0,0,0,0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2rem;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.btn-close {
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #7f8c8d;
  transition: color 0.3s ease;
}

.btn-close:hover {
  color: #e74c3c;
}

.modal-body {
  padding: 2rem;
}

.payment-summary {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 2rem;
}

.payment-summary h4 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
}

.summary-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  color: #7f8c8d;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  padding-top: 1rem;
  border-top: 2px solid #e9ecef;
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
}

.payment-form {
  margin-bottom: 2rem;
}

.payment-form label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #2c3e50;
}

.stripe-element {
  padding: 1rem;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  transition: border-color 0.3s ease;
}

.stripe-element:focus-within {
  border-color: #3498db;
}

.error-message {
  color: #e74c3c;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.payment-actions {
  display: flex;
  gap: 1rem;
}

.btn-pay, .btn-cancel-payment {
  flex: 1;
  padding: 1.25rem;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.btn-pay {
  background: #27ae60;
  color: white;
}

.btn-pay:hover:not(:disabled) {
  background: #219a52;
}

.btn-pay:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-cancel-payment {
  background: #95a5a6;
  color: white;
}

.btn-cancel-payment:hover {
  background: #7f8c8d;
}

.faq-section {
  margin-top: 3rem;
}

.faq-section h3 {
  text-align: center;
  margin-bottom: 2rem;
  color: #2c3e50;
}

.faq-list {
  display: grid;
  gap: 1rem;
}

.faq-item {
  border: 1px solid #e9ecef;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.faq-item.active {
  border-color: #3498db;
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.1);
}

.faq-question {
  padding: 1.5rem;
  background: #f8f9fa;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: background 0.3s ease;
  font-weight: 600;
  color: #2c3e50;
}

.faq-question:hover {
  background: #e9ecef;
}

.icon-chevron::before {
  content: '▼';
  transition: transform 0.3s ease;
  font-size: 0.8rem;
}

.icon-chevron.open::before {
  transform: rotate(180deg);
}

.faq-answer {
  padding: 1.5rem;
  background: white;
  border-top: 1px solid #e9ecef;
  color: #555;
  line-height: 1.6;
}

.faq-answer p {
  margin: 0;
}

.message {
  padding: 1.25rem 1.5rem;
  border-radius: 12px;
  margin: 1.5rem 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  position: relative;
}

.message.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.message.warning {
  background: #fff3cd;
  color: #856404;
  border: 1px solid #ffeeba;
}

.message-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  position: absolute;
  right: 1rem;
  color: inherit;
  opacity: 0.7;
}

.message-close:hover {
  opacity: 1;
}

.spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid #ffffff;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Transitions */
.message-fade-enter-active, .message-fade-leave-active {
  transition: all 0.3s ease;
}

.message-fade-enter-from, .message-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.faq-content-enter-active, .faq-content-leave-active {
  transition: all 0.3s ease;
}

.faq-content-enter-from, .faq-content-leave-to {
  opacity: 0;
  max-height: 0;
}

.faq-content-enter-to, .faq-content-leave-from {
  opacity: 1;
  max-height: 200px;
}

/* Responsive */
@media (max-width: 768px) {
  .abonnement-premium {
    padding: 1rem;
  }

  .title {
    font-size: 2rem;
  }

  .premium-card {
    margin-bottom: 2rem;
  }

  .subscription-actions {
    flex-direction: column;
  }

  .payment-actions {
    flex-direction: column;
  }

  .security-features {
    grid-template-columns: 1fr;
  }

  .invoice-item, .notification-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .modal-content {
    width: 95%;
    margin: 1rem;
  }

  .modal-body {
    padding: 1.5rem;
  }
}

@media (max-width: 480px) {
  .card-header {
    padding: 2rem 1.5rem;
  }

  .card-body {
    padding: 2rem 1.5rem;
  }

  .price-section {
    font-size: 2.5rem;
  }
}
</style>
