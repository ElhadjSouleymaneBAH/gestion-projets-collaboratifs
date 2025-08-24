// services/stripeService.js
import { loadStripe } from '@stripe/stripe-js'
import api from '@/services/api.js'

// Initialisation Stripe avec clé publique
const stripePromise = loadStripe('pk_test_51QUuS1G4o2phWc0JMMPWvxd1tzOX5bMqPJoANzrbsHJjm6fXVCBI57Ikdfj1n6fvhVw5zX5YqhzLmRHINYFy2Ly600q3HhwWLe')

// Plans d'abonnement disponibles
export const SUBSCRIPTION_PLANS = {
  PREMIUM: {
    id: 'premium',
    name: 'Plan Premium Mensuel',
    price: 10,
    currency: 'EUR',
    features: [
      'Création illimitée de projets',
      'Gestion complète des équipes',
      'Collaboration temps réel',
      'Facturation automatique',
      'Support prioritaire'
    ]
  }
}

export const stripeService = {
  // Obtenir l'instance Stripe
  async getStripe() {
    return await stripePromise
  },

  // Créer une session de paiement Stripe
  async createCheckoutSession(planId) {
    try {
      const response = await api.post('/stripe/create-checkout-session', {
        planId,
        successUrl: `${window.location.origin}/abonnement/success`,
        cancelUrl: `${window.location.origin}/abonnement/cancel`
      })
      return response.data
    } catch (error) {
      console.error('Erreur création session Stripe:', error)
      throw error
    }
  },

  // Rediriger vers Stripe Checkout
  async redirectToCheckout(sessionId) {
    try {
      const stripe = await this.getStripe()
      const { error } = await stripe.redirectToCheckout({ sessionId })

      if (error) {
        console.error('Erreur redirection Stripe:', error)
        throw error
      }
    } catch (error) {
      console.error('Erreur redirection checkout:', error)
      throw error
    }
  },

  // Processus complet de paiement
  async processPayment(planId) {
    try {
      // 1. Créer la session Stripe
      const session = await this.createCheckoutSession(planId)

      // 2. Rediriger vers Stripe
      await this.redirectToCheckout(session.sessionId)

      return { success: true }
    } catch (error) {
      console.error('Erreur processus paiement:', error)
      return {
        success: false,
        error: error.response?.data?.message || 'Erreur lors du paiement'
      }
    }
  },

  // Vérifier le statut d'un paiement
  async verifyPayment(sessionId) {
    try {
      const response = await api.get(`/stripe/verify-payment/${sessionId}`)
      return response.data
    } catch (error) {
      console.error('Erreur vérification paiement:', error)
      throw error
    }
  },

  // Récupérer l'abonnement actuel de l'utilisateur
  async getCurrentSubscription() {
    try {
      const response = await api.get('/stripe/subscription/current')
      return response.data
    } catch (error) {
      console.error('Erreur récupération abonnement:', error)
      return null
    }
  },

  // Annuler un abonnement
  async cancelSubscription() {
    try {
      const response = await api.post('/stripe/subscription/cancel')
      return response.data
    } catch (error) {
      console.error('Erreur annulation abonnement:', error)
      throw error
    }
  },

  // Obtenir les factures de l'utilisateur
  async getInvoices() {
    try {
      const response = await api.get('/stripe/invoices')
      return response.data
    } catch (error) {
      console.error('Erreur récupération factures:', error)
      return []
    }
  }
}
