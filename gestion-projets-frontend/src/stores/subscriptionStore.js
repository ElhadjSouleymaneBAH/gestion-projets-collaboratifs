// stores/subscriptionStore.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { stripeService, SUBSCRIPTION_PLANS } from '@/services/stripeService.js'
import { useAuthStore } from '@/stores/authStore.js'

export const useSubscriptionStore = defineStore('subscription', () => {
  // State
  const currentSubscription = ref(null)
  const invoices = ref([])
  const loading = ref(false)
  const error = ref(null)
  const paymentProcessing = ref(false)

  // Getters
  const hasActiveSubscription = computed(() =>
    currentSubscription.value?.statut === 'ACTIF'
  )

  const subscriptionPlan = computed(() =>
    currentSubscription.value?.type || null
  )

  const canCreateProjects = computed(() => {
    const authStore = useAuthStore()
    return authStore.isChefProjet && hasActiveSubscription.value
  })

  const subscriptionStatus = computed(() => {
    if (!currentSubscription.value) return 'AUCUN'
    return currentSubscription.value.statut
  })

  const nextBillingDate = computed(() => {
    return currentSubscription.value?.date_fin || null
  })

  // Actions
  const fetchCurrentSubscription = async () => {
    loading.value = true
    error.value = null

    try {
      const subscription = await stripeService.getCurrentSubscription()
      currentSubscription.value = subscription
      return subscription
    } catch (err) {
      error.value = 'Erreur lors de la récupération de l\'abonnement'
      console.error('Erreur fetch subscription:', err)
    } finally {
      loading.value = false
    }
  }

  const startPaymentProcess = async (planId) => {
    paymentProcessing.value = true
    error.value = null

    try {
      const result = await stripeService.processPayment(planId)

      if (result.success) {
        // Le paiement va rediriger vers Stripe
        return { success: true }
      } else {
        error.value = result.error
        return { success: false, error: result.error }
      }
    } catch (err) {
      error.value = 'Erreur lors de l\'initialisation du paiement'
      console.error('Erreur payment process:', err)
      return { success: false, error: error.value }
    } finally {
      paymentProcessing.value = false
    }
  }

  const verifyPaymentSuccess = async (sessionId) => {
    loading.value = true
    error.value = null

    try {
      const result = await stripeService.verifyPayment(sessionId)

      if (result.success) {
        // Recharger l'abonnement après paiement réussi
        await fetchCurrentSubscription()
        return { success: true, subscription: result.subscription }
      } else {
        error.value = 'Échec de la vérification du paiement'
        return { success: false, error: error.value }
      }
    } catch (err) {
      error.value = 'Erreur lors de la vérification du paiement'
      console.error('Erreur verify payment:', err)
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  const cancelSubscription = async () => {
    loading.value = true
    error.value = null

    try {
      const result = await stripeService.cancelSubscription()

      if (result.success) {
        // Recharger l'abonnement après annulation
        await fetchCurrentSubscription()
        return { success: true }
      } else {
        error.value = 'Erreur lors de l\'annulation'
        return { success: false, error: error.value }
      }
    } catch (err) {
      error.value = 'Erreur lors de l\'annulation de l\'abonnement'
      console.error('Erreur cancel subscription:', err)
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  const fetchInvoices = async () => {
    loading.value = true
    error.value = null

    try {
      const fetchedInvoices = await stripeService.getInvoices()
      invoices.value = fetchedInvoices
      return fetchedInvoices
    } catch (err) {
      error.value = 'Erreur lors de la récupération des factures'
      console.error('Erreur fetch invoices:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  const clearError = () => {
    error.value = null
  }

  const resetStore = () => {
    currentSubscription.value = null
    invoices.value = []
    error.value = null
    loading.value = false
    paymentProcessing.value = false
  }

  // Initialisation automatique si l'utilisateur est Chef de Projet
  const initializeSubscription = async () => {
    const authStore = useAuthStore()

    if (authStore.isChefProjet) {
      await fetchCurrentSubscription()
    }
  }

  return {
    // State
    currentSubscription,
    invoices,
    loading,
    error,
    paymentProcessing,

    // Getters
    hasActiveSubscription,
    subscriptionPlan,
    canCreateProjects,
    subscriptionStatus,
    nextBillingDate,

    // Actions
    fetchCurrentSubscription,
    startPaymentProcess,
    verifyPaymentSuccess,
    cancelSubscription,
    fetchInvoices,
    clearError,
    resetStore,
    initializeSubscription,

    // Constants
    SUBSCRIPTION_PLANS
  }
})
