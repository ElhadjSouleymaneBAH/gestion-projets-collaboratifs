import axios from 'axios'

/**
 * Service de gestion des projets - Frontend Vue.js
 *
 * @author Elhadj Souleymane BAH
 * @version 1.0
 */

/**
 * Codes d'erreur standardisés pour l'internationalisation
 */
const ERROR_CODES = {
  GET_MY_PROJECTS_FAILED: 'projects.errors.getMyProjectsFailed',
  GET_PROJECT_BY_ID_FAILED: 'projects.errors.getProjectByIdFailed',
  CREATE_PROJECT_FAILED: 'projects.errors.createProjectFailed',
  DELETE_PROJECT_FAILED: 'projects.errors.deleteProjectFailed',
  UPDATE_PROJECT_FAILED: 'projects.errors.updateProjectFailed',
  NETWORK_ERROR: 'projects.errors.networkError',
  SERVER_ERROR: 'projects.errors.serverError',
  UNAUTHORIZED: 'projects.errors.unauthorized',
  FORBIDDEN: 'projects.errors.forbidden',
  NOT_FOUND: 'projects.errors.notFound',
  VALIDATION_ERROR: 'projects.errors.validationError'
}

/**
 * Messages de succès standardisés
 */
const SUCCESS_MESSAGES = {
  PROJECTS_LOADED: 'projects.success.loaded',
  PROJECT_LOADED: 'projects.success.projectLoaded',
  PROJECT_CREATED: 'projects.success.created',
  PROJECT_DELETED: 'projects.success.deleted',
  PROJECT_UPDATED: 'projects.success.updated'
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

  return t('projects.errors.general')
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

  return t('projects.success.general')
}

/**
 * Wrapper pour gérer les réponses avec internationalisation
 * @param {Promise} axiosPromise - Promise axios
 * @param {string} successMessageKey - Clé du message de succès
 * @param {string} errorCode - Code d'erreur par défaut
 * @returns {Promise<Object>} Réponse formatée
 */
async function handleResponse(axiosPromise, successMessageKey, errorCode) {
  try {
    const response = await axiosPromise
    return {
      success: true,
      data: response.data,
      messageKey: successMessageKey,
      response
    }
  } catch (error) {
    return {
      success: false,
      errorCode,
      message: error.response?.data?.message || error.message,
      error
    }
  }
}

export default {
  //  Récupérer tous les projets d'un utilisateur (conforme au backend)
  async getMesProjets(utilisateurId, token) {
    const axiosPromise = axios.get(`/api/projets/utilisateur/${Number(utilisateurId)}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    return handleResponse(
      axiosPromise,
      SUCCESS_MESSAGES.PROJECTS_LOADED,
      ERROR_CODES.GET_MY_PROJECTS_FAILED
    )
  },

  //  Récupérer un projet spécifique par ID
  async getProjetById(id, token) {
    const axiosPromise = axios.get(`/api/projets/${Number(id)}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    return handleResponse(
      axiosPromise,
      SUCCESS_MESSAGES.PROJECT_LOADED,
      ERROR_CODES.GET_PROJECT_BY_ID_FAILED
    )
  },

  //  Créer un nouveau projet
  async creerProjet(projetData, token) {
    const axiosPromise = axios.post('/api/projets', projetData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    return handleResponse(
      axiosPromise,
      SUCCESS_MESSAGES.PROJECT_CREATED,
      ERROR_CODES.CREATE_PROJECT_FAILED
    )
  },

  //  Supprimer un projet (si autorisé)
  async supprimerProjet(id, token) {
    const axiosPromise = axios.delete(`/api/projets/${Number(id)}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    return handleResponse(
      axiosPromise,
      SUCCESS_MESSAGES.PROJECT_DELETED,
      ERROR_CODES.DELETE_PROJECT_FAILED
    )
  },

  //  Modifier un projet existant
  async modifierProjet(id, projetData, token) {
    const axiosPromise = axios.put(`/api/projets/${Number(id)}`, projetData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    return handleResponse(
      axiosPromise,
      SUCCESS_MESSAGES.PROJECT_UPDATED,
      ERROR_CODES.UPDATE_PROJECT_FAILED
    )
  },

  /**
   * Méthodes originales synchrones pour compatibilité
   * (conservées pour ne pas casser le code existant)
   */

  //  Récupérer tous les projets d'un utilisateur (version originale)
  getMesProjetsSync(utilisateurId, token) {
    return axios.get(`/api/projets/utilisateur/${Number(utilisateurId)}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  },

  //  Récupérer un projet spécifique par ID
  getProjetByIdSync(id, token) {
    return axios.get(`/api/projets/${Number(id)}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  },

  //  Créer un nouveau projet
  creerProjetSync(projetData, token) {
    return axios.post('/api/projets', projetData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  },

  //  Supprimer un projet
  supprimerProjetSync(id, token) {
    return axios.delete(`/api/projets/${Number(id)}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  },

  //  Modifier un projet existant
  modifierProjetSync(id, projetData, token) {
    return axios.put(`/api/projets/${Number(id)}`, projetData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  },

  /**
   * Obtenir le message d'erreur internationalisé
   * @param {Object} error - Objet d'erreur du service
   * @param {Function} t - Fonction de traduction Vue i18n
   * @returns {string} Message d'erreur traduit
   */
  getLocalizedErrorMessage(error, t) {
    return getLocalizedErrorMessage(error.errorCode, t) || error.message
  },

  /**
   * Obtenir le message de succès internationalisé
   * @param {string} messageKey - Clé du message
   * @param {Function} t - Fonction de traduction Vue i18n
   * @returns {string} Message de succès traduit
   */
  getLocalizedSuccessMessage(messageKey, t) {
    return getLocalizedSuccessMessage(messageKey, t)
  },

  /**
   * Codes d'erreur disponibles
   */
  ERROR_CODES,

  /**
   * Messages de succès disponibles
   */
  SUCCESS_MESSAGES
}
