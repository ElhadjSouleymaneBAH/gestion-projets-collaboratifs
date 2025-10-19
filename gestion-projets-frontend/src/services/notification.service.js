import apiClient from '@/services/api.js'

/**
 * Service de gestion des notifications - Frontend Vue.js
 * @author Équipe Développement
 * @version 1.0
 */

/**
 * Codes d'erreur standardisés pour l'internationalisation
 */
const ERROR_CODES = {
  LOAD_NOTIFICATIONS_FAILED: 'notifications.errors.loadFailed',
  MARK_READ_FAILED: 'notifications.errors.markReadFailed',
  MARK_ALL_READ_FAILED: 'notifications.errors.markAllReadFailed',
  DELETE_NOTIFICATION_FAILED: 'notifications.errors.deleteFailed',
  DELETE_ALL_READ_FAILED: 'notifications.errors.deleteAllReadFailed',
  NETWORK_ERROR: 'notifications.errors.networkError',
  SERVER_ERROR: 'notifications.errors.serverError',
  VALIDATION_ERROR: 'notifications.errors.validationError'
}

/**
 * Messages de succès standardisés
 */
const SUCCESS_MESSAGES = {
  NOTIFICATIONS_LOADED: 'notifications.success.loaded',
  NOTIFICATION_MARKED_READ: 'notifications.success.markedRead',
  ALL_NOTIFICATIONS_MARKED_READ: 'notifications.success.allMarkedRead',
  NOTIFICATION_DELETED: 'notifications.success.deleted',
  ALL_READ_NOTIFICATIONS_DELETED: 'notifications.success.allReadDeleted'
}

/**
 * Obtenir le message d'erreur internationalisé
 * @param {string} errorCode - Code d'erreur
 * @param {Function} t - Fonction de traduction Vue i18n
 * @returns {string} Message d'erreur traduit
 */
function getLocalizedErrorMessage(errorCode, t) {
  if (!t || typeof t !== 'function') {
    return 'Erreur inconnue'
  }

  if (errorCode && t(errorCode) !== errorCode) {
    return t(errorCode)
  }

  return t('notifications.errors.general')
}

/**
 * Obtenir le message de succès internationalisé
 * @param {string} messageKey - Clé du message
 * @param {Function} t - Fonction de traduction Vue i18n
 * @returns {string} Message de succès traduit
 */
function getLocalizedSuccessMessage(messageKey, t) {
  if (!t || typeof t !== 'function') {
    return 'Succès'
  }

  if (messageKey && t(messageKey) !== messageKey) {
    return t(messageKey)
  }

  return t('notifications.success.general')
}

class NotificationService {

  async getNotifications(params = {}) {
    try {
      const queryParams = new URLSearchParams()

      if (params.page !== undefined) queryParams.append('page', params.page)
      if (params.size !== undefined) queryParams.append('size', params.size)
      if (params.type) queryParams.append('type', params.type)
      if (params.lu !== undefined) queryParams.append('lu', params.lu)
      if (params.periode) queryParams.append('periode', params.periode)
      if (params.recherche) queryParams.append('recherche', params.recherche)

      const response = await apiClient.get(`/api/notifications?${queryParams.toString()}`)

      return {
        success: true,
        data: response.data,
        messageKey: SUCCESS_MESSAGES.NOTIFICATIONS_LOADED
      }
    } catch (error) {
      return {
        success: false,
        errorCode: ERROR_CODES.LOAD_NOTIFICATIONS_FAILED,
        message: error.response?.data?.message || 'Erreur lors du chargement des notifications'
      }
    }
  }

  async marquerCommeLue(id) {
    try {
      const response = await apiClient.put(`/api/notifications/${id}/read`)
      return {
        success: true,
        data: response.data,
        messageKey: SUCCESS_MESSAGES.NOTIFICATION_MARKED_READ
      }
    } catch (error) {
      return {
        success: false,
        errorCode: ERROR_CODES.MARK_READ_FAILED,
        message: error.response?.data?.message || 'Erreur'
      }
    }
  }

  async marquerToutesLues() {
    try {
      const response = await apiClient.put('/api/notifications/read-all')
      return {
        success: true,
        data: response.data,
        messageKey: SUCCESS_MESSAGES.ALL_NOTIFICATIONS_MARKED_READ
      }
    } catch (error) {
      return {
        success: false,
        errorCode: ERROR_CODES.MARK_ALL_READ_FAILED,
        message: error.response?.data?.message || 'Erreur'
      }
    }
  }

  async supprimerNotification(id) {
    try {
      await apiClient.delete(`/api/notifications/${id}`)
      return {
        success: true,
        messageKey: SUCCESS_MESSAGES.NOTIFICATION_DELETED
      }
    } catch (error) {
      return {
        success: false,
        errorCode: ERROR_CODES.DELETE_NOTIFICATION_FAILED,
        message: error.response?.data?.message || 'Erreur'
      }
    }
  }

  async supprimerToutesLues() {
    try {
      await apiClient.delete('/api/notifications/read')
      return {
        success: true,
        messageKey: SUCCESS_MESSAGES.ALL_READ_NOTIFICATIONS_DELETED
      }
    } catch (error) {
      return {
        success: false,
        errorCode: ERROR_CODES.DELETE_ALL_READ_FAILED,
        message: error.response?.data?.message || 'Erreur'
      }
    }
  }

  /**
   * Obtenir le message d'erreur internationalisé
   * @param {Object} error - Objet d'erreur du service
   * @param {Function} t - Fonction de traduction Vue i18n
   * @returns {string} Message d'erreur traduit
   */
  getLocalizedErrorMessage(error, t) {
    return getLocalizedErrorMessage(error.errorCode, t) || error.message
  }

  /**
   * Obtenir le message de succès internationalisé
   * @param {string} messageKey - Clé du message
   * @param {Function} t - Fonction de traduction Vue i18n
   * @returns {string} Message de succès traduit
   */
  getLocalizedSuccessMessage(messageKey, t) {
    return getLocalizedSuccessMessage(messageKey, t)
  }

  /**
   * Codes d'erreur disponibles
   */
  get ERROR_CODES() {
    return ERROR_CODES
  }

  /**
   * Messages de succès disponibles
   */
  get SUCCESS_MESSAGES() {
    return SUCCESS_MESSAGES
  }
}

export default new NotificationService()
