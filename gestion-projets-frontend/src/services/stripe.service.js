// src/services/stripe.service.js
import api from '@/services/api'
import { endpoints } from '@/config/endpoints'

/** Codes d’erreur (i18n) */
const ERROR_CODES = {
  CREATE_PAYMENT_INTENT_FAILED: 'stripe.errors.createPaymentIntentFailed',
  CONFIRM_PAYMENT_FAILED: 'stripe.errors.confirmPaymentFailed',
  GET_INVOICES_FAILED: 'stripe.errors.getInvoicesFailed',
  GET_SUBSCRIPTION_FAILED: 'stripe.errors.getSubscriptionFailed',
  NETWORK_ERROR: 'stripe.errors.networkError',
  SERVER_ERROR: 'stripe.errors.serverError',
}

/** Messages de succès (i18n) */
const SUCCESS_MESSAGES = {
  PAYMENT_INTENT_CREATED: 'stripe.success.paymentIntentCreated',
  PAYMENT_CONFIRMED: 'stripe.success.paymentConfirmed',
  INVOICES_RETRIEVED: 'stripe.success.invoicesRetrieved',
  SUBSCRIPTION_RETRIEVED: 'stripe.success.subscriptionRetrieved',
}

/* Helpers i18n */
function getLocalizedErrorMessage(code, t) {
  if (!t || typeof t !== 'function') return 'Erreur inconnue'
  return t(code) !== code ? t(code) : t('stripe.errors.general')
}
function getLocalizedSuccessMessage(key, t) {
  if (!t || typeof t !== 'function') return 'Succès'
  return t(key) !== key ? t(key) : t('stripe.success.general')
}

export default {
  /** F10 — Créer un PaymentIntent (10€ / mois) */
  async createPaymentIntent() {
    try {
      const { data } = await api.post(`${endpoints.stripe}/create-payment-intent`)
      // data = { payment_intent_id, client_secret }
      return {
        success: true,
        data,
        messageKey: SUCCESS_MESSAGES.PAYMENT_INTENT_CREATED,
      }
    } catch (error) {
      console.error('createPaymentIntent error:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.CREATE_PAYMENT_INTENT_FAILED,
        message: error.response?.data?.error || 'Erreur création PaymentIntent',
        error,
      }
    }
  },

  /** F10+F11 — Confirmer le paiement (serveur: crée abonnement + génère facture) */
  async confirmPayment(payment_intent_id) {
    try {
      const payload = { payment_intent_id }
      const { data } = await api.post(`${endpoints.stripe}/confirm-payment`, payload)
      // data = {message: "Paiement confirmé et facture générée"}
      return {
        success: true,
        data,
        messageKey: SUCCESS_MESSAGES.PAYMENT_CONFIRMED,
      }
    } catch (error) {
      console.error('confirmPayment error:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.CONFIRM_PAYMENT_FAILED,
        message: error.response?.data?.error || 'Erreur confirmation paiement',
        error,
      }
    }
  },

  /** Factures de l’utilisateur (backend: GET /api/factures/mes-factures) */
  async getInvoices() {
    try {
      const { data } = await api.get(`${endpoints.factures}/mes-factures`)
      return {
        success: true,
        data,
        messageKey: SUCCESS_MESSAGES.INVOICES_RETRIEVED,
      }
    } catch (error) {
      console.error('getInvoices error:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.GET_INVOICES_FAILED,
        message: error.response?.data?.error || 'Erreur récupération factures',
        data: [],
      }
    }
  },

  /** Abonnement courant par id utilisateur (GET /api/abonnements/utilisateur/{id}) */
  async getCurrentSubscription(userId) {
    try {
      const { data } = await api.get(`${endpoints.abonnements}/utilisateur/${userId}`)
      return {
        success: true,
        data,
        messageKey: SUCCESS_MESSAGES.SUBSCRIPTION_RETRIEVED,
      }
    } catch (error) {
      console.error('getCurrentSubscription error:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.GET_SUBSCRIPTION_FAILED,
        message: error.response?.data?.error || 'Erreur récupération abonnement',
        data: null,
      }
    }
  },

  /* Helpers i18n exposés */
  getLocalizedErrorMessage(err, t) {
    return getLocalizedErrorMessage(err?.errorCode, t) || err?.message
  },
  getLocalizedSuccessMessage(key, t) {
    return getLocalizedSuccessMessage(key, t)
  },

  ERROR_CODES,
  SUCCESS_MESSAGES,
}
