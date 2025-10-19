// src/services/commentaire.service.js
import api from './api.js'

/**
 * Service de gestion des commentaires - Frontend Vue.js
 * Implémente la fonctionnalité F12 du cahier des charges
 * Internationalisation complète FR/EN
 * Aucune donnée en dur - Configuration dynamique
 *
 * @author Équipe Développement
 * @version 1.0
 */
class CommentaireService {
  /**
   * Codes d'erreur standardisés pour l'internationalisation
   */
  static ERROR_CODES = {
    CREATE_COMMENT_FAILED: 'comments.errors.createFailed',
    GET_COMMENTS_FAILED: 'comments.errors.loadFailed',
    UPDATE_COMMENT_FAILED: 'comments.errors.updateFailed',
    DELETE_COMMENT_FAILED: 'comments.errors.deleteFailed',
    CONTENT_REQUIRED: 'comments.errors.contentRequired',
    CONTENT_TOO_LONG: 'comments.errors.contentTooLong',
    TARGET_TYPE_INVALID: 'comments.errors.targetTypeInvalid',
    TARGET_ID_INVALID: 'comments.errors.targetIdInvalid',
    COMMENT_ID_INVALID: 'comments.errors.commentIdInvalid',
    UNAUTHORIZED: 'comments.errors.unauthorized',
    FORBIDDEN: 'comments.errors.forbidden',
    NOT_FOUND: 'comments.errors.notFound',
    NETWORK_ERROR: 'comments.errors.networkError',
    SERVER_ERROR: 'comments.errors.serverError',
    VALIDATION_ERROR: 'comments.errors.validationError'
  }

  /**
   * Messages de succès standardisés
   */
  static SUCCESS_MESSAGES = {
    COMMENT_CREATED: 'comments.success.created',
    COMMENT_UPDATED: 'comments.success.updated',
    COMMENT_DELETED: 'comments.success.deleted',
    COMMENTS_LOADED: 'comments.success.loaded'
  }

  /**
   * Types de cibles pour les commentaires
   */
  static TARGET_TYPES = {
    PROJECT: 'PROJET',
    TASK: 'TACHE'
  }

  /**
   * Endpoints API
   */
  static ENDPOINTS = {
    COMMENTS: '/commentaires',
    PROJECT_COMMENTS: '/commentaires/projet',
    TASK_COMMENTS: '/commentaires/tache'
  }

  /**
   * Configuration de validation
   */
  static VALIDATION_RULES = {
    CONTENT_MIN_LENGTH: 1,
    CONTENT_MAX_LENGTH: 1000,
    MIN_TARGET_ID: 1
  }

  /**
   * F12: Ajouter un commentaire sur une tâche ou un projet
   * Validation complète et gestion d'erreurs robuste
   *
   * @param {Object} commentaireData - Données du commentaire
   * @param {string} commentaireData.contenu - Contenu du commentaire
   * @param {string} commentaireData.cibleType - Type de cible (PROJET ou TACHE)
   * @param {number} commentaireData.cibleId - ID de la cible
   * @returns {Promise<Object>} Commentaire créé
   */
  async ajouterCommentaire(commentaireData) {
    try {
      console.log('[F12] Ajout commentaire')

      // Validation des données
      const validationResult = this.validateCommentData(commentaireData)
      if (!validationResult.isValid) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.VALIDATION_ERROR,
          errors: validationResult.errors,
          message: validationResult.errors[0]
        }
      }

      const payload = {
        contenu: commentaireData.contenu.trim(),
        cibleType: commentaireData.cibleType,
        cibleId: parseInt(commentaireData.cibleId, 10)
      }

      const response = await api.post(CommentaireService.ENDPOINTS.COMMENTS, payload)

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F12] Commentaire ajouté avec succès')

      return {
        success: true,
        data: response.data,
        messageKey: CommentaireService.SUCCESS_MESSAGES.COMMENT_CREATED
      }
    } catch (error) {
      console.error('[F12] Erreur ajout commentaire:', error)
      return this.handleCommentError(error, CommentaireService.ERROR_CODES.CREATE_COMMENT_FAILED)
    }
  }

  /**
   * F12: Obtenir les commentaires d'un projet
   * Avec validation et gestion des permissions
   *
   * @param {number} projetId - ID du projet
   * @returns {Promise<Object>} Liste des commentaires du projet
   */
  async getCommentairesProjet(projetId) {
    try {
      console.log('[F12] Chargement commentaires projet')

      // Validation de l'ID projet
      if (!this.isValidId(projetId)) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.TARGET_ID_INVALID,
          message: 'ID de projet invalide'
        }
      }

      const url = `${CommentaireService.ENDPOINTS.PROJECT_COMMENTS}/${projetId}`
      const response = await api.get(url)

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F12] Commentaires projet chargés avec succès')

      return {
        success: true,
        data: Array.isArray(response.data) ? response.data : [],
        messageKey: CommentaireService.SUCCESS_MESSAGES.COMMENTS_LOADED
      }
    } catch (error) {
      console.error('[F12] Erreur chargement commentaires projet:', error)
      return this.handleCommentError(error, CommentaireService.ERROR_CODES.GET_COMMENTS_FAILED)
    }
  }

  /**
   * F12: Obtenir les commentaires d'une tâche
   * Avec validation et gestion des permissions
   *
   * @param {number} tacheId - ID de la tâche
   * @returns {Promise<Object>} Liste des commentaires de la tâche
   */
  async getCommentairesTache(tacheId) {
    try {
      console.log('[F12] Chargement commentaires tâche')

      // Validation de l'ID tâche
      if (!this.isValidId(tacheId)) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.TARGET_ID_INVALID,
          message: 'ID de tâche invalide'
        }
      }

      const url = `${CommentaireService.ENDPOINTS.TASK_COMMENTS}/${tacheId}`
      const response = await api.get(url)

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F12] Commentaires tâche chargés avec succès')

      return {
        success: true,
        data: Array.isArray(response.data) ? response.data : [],
        messageKey: CommentaireService.SUCCESS_MESSAGES.COMMENTS_LOADED
      }
    } catch (error) {
      console.error('[F12] Erreur chargement commentaires tâche:', error)
      return this.handleCommentError(error, CommentaireService.ERROR_CODES.GET_COMMENTS_FAILED)
    }
  }

  /**
   * Mettre à jour un commentaire
   * Avec validation des permissions utilisateur
   *
   * @param {number} commentId - ID du commentaire
   * @param {Object} updateData - Données de mise à jour
   * @param {string} updateData.contenu - Nouveau contenu du commentaire
   * @returns {Promise<Object>} Commentaire mis à jour
   */
  async mettreAJourCommentaire(commentId, updateData) {
    try {
      console.log('[F12] Mise à jour commentaire')

      // Validation de l'ID commentaire
      if (!this.isValidId(commentId)) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.COMMENT_ID_INVALID,
          message: 'ID de commentaire invalide'
        }
      }

      // Validation du contenu
      if (!updateData || !updateData.contenu?.trim()) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.CONTENT_REQUIRED,
          message: 'Contenu du commentaire requis'
        }
      }

      const contenu = updateData.contenu.trim()
      if (contenu.length > CommentaireService.VALIDATION_RULES.CONTENT_MAX_LENGTH) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.CONTENT_TOO_LONG,
          message: `Contenu trop long (max ${CommentaireService.VALIDATION_RULES.CONTENT_MAX_LENGTH} caractères)`
        }
      }

      const payload = {
        ...updateData,
        contenu
      }

      const url = `${CommentaireService.ENDPOINTS.COMMENTS}/${commentId}`
      const response = await api.put(url, payload)

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F12] Commentaire mis à jour avec succès')

      return {
        success: true,
        data: response.data,
        messageKey: CommentaireService.SUCCESS_MESSAGES.COMMENT_UPDATED
      }
    } catch (error) {
      console.error('[F12] Erreur mise à jour commentaire:', error)
      return this.handleCommentError(error, CommentaireService.ERROR_CODES.UPDATE_COMMENT_FAILED)
    }
  }

  /**
   * Supprimer un commentaire
   * Avec vérification des permissions
   *
   * @param {number} commentId - ID du commentaire
   * @returns {Promise<Object>} Confirmation de suppression
   */
  async supprimerCommentaire(commentId) {
    try {
      console.log('[F12] Suppression commentaire')

      // Validation de l'ID commentaire
      if (!this.isValidId(commentId)) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.COMMENT_ID_INVALID,
          message: 'ID de commentaire invalide'
        }
      }

      const url = `${CommentaireService.ENDPOINTS.COMMENTS}/${commentId}`
      await api.delete(url)

      console.log('[F12] Commentaire supprimé avec succès')

      return {
        success: true,
        messageKey: CommentaireService.SUCCESS_MESSAGES.COMMENT_DELETED
      }
    } catch (error) {
      console.error('[F12] Erreur suppression commentaire:', error)
      return this.handleCommentError(error, CommentaireService.ERROR_CODES.DELETE_COMMENT_FAILED)
    }
  }

  /**
   * Obtenir les commentaires par type et ID de cible (méthode générique)
   *
   * @param {string} targetType - Type de cible (PROJET ou TACHE)
   * @param {number} targetId - ID de la cible
   * @returns {Promise<Object>} Liste des commentaires
   */
  async getCommentaires(targetType, targetId) {
    try {
      console.log('[F12] Chargement commentaires génériques')

      // Validation du type de cible
      if (!Object.values(CommentaireService.TARGET_TYPES).includes(targetType)) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.TARGET_TYPE_INVALID,
          message: 'Type de cible invalide'
        }
      }

      // Déléguer à la méthode spécifique
      switch (targetType) {
        case CommentaireService.TARGET_TYPES.PROJECT:
          return await this.getCommentairesProjet(targetId)
        case CommentaireService.TARGET_TYPES.TASK:
          return await this.getCommentairesTache(targetId)
        default:
          throw {
            success: false,
            errorCode: CommentaireService.ERROR_CODES.TARGET_TYPE_INVALID,
            message: 'Type de cible non supporté'
          }
      }
    } catch (error) {
      console.error('[F12] Erreur chargement commentaires génériques:', error)
      return this.handleCommentError(error, CommentaireService.ERROR_CODES.GET_COMMENTS_FAILED)
    }
  }

  /**
   * Obtenir le nombre de commentaires pour une cible
   *
   * @param {string} targetType - Type de cible (PROJET ou TACHE)
   * @param {number} targetId - ID de la cible
   * @returns {Promise<Object>} Nombre de commentaires
   */
  async getCommentCount(targetType, targetId) {
    try {
      console.log('[F12] Récupération nombre commentaires')

      // Validation
      if (!Object.values(CommentaireService.TARGET_TYPES).includes(targetType)) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.TARGET_TYPE_INVALID,
          message: 'Type de cible invalide'
        }
      }

      if (!this.isValidId(targetId)) {
        throw {
          success: false,
          errorCode: CommentaireService.ERROR_CODES.TARGET_ID_INVALID,
          message: 'ID de cible invalide'
        }
      }

      const endpoint = targetType === CommentaireService.TARGET_TYPES.PROJECT
        ? CommentaireService.ENDPOINTS.PROJECT_COMMENTS
        : CommentaireService.ENDPOINTS.TASK_COMMENTS

      const url = `${endpoint}/${targetId}/count`
      const response = await api.get(url)

      if (!response.data && response.data !== 0) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F12] Nombre commentaires récupéré avec succès')

      return {
        success: true,
        data: { count: response.data || 0 }
      }
    } catch (error) {
      console.error('[F12] Erreur récupération nombre commentaires:', error)
      return this.handleCommentError(error, CommentaireService.ERROR_CODES.GET_COMMENTS_FAILED)
    }
  }

  /**
   * Valider les données d'un commentaire
   * @private
   */
  validateCommentData(commentData) {
    const errors = []

    if (!commentData) {
      errors.push('Données de commentaire requises')
      return { isValid: false, errors }
    }

    // Validation du contenu
    if (!commentData.contenu || typeof commentData.contenu !== 'string') {
      errors.push('Contenu du commentaire requis')
    } else {
      const contenu = commentData.contenu.trim()

      if (contenu.length < CommentaireService.VALIDATION_RULES.CONTENT_MIN_LENGTH) {
        errors.push('Le contenu du commentaire ne peut pas être vide')
      }

      if (contenu.length > CommentaireService.VALIDATION_RULES.CONTENT_MAX_LENGTH) {
        errors.push(`Le contenu du commentaire doit faire moins de ${CommentaireService.VALIDATION_RULES.CONTENT_MAX_LENGTH} caractères`)
      }
    }

    // Validation du type de cible
    if (!commentData.cibleType || !Object.values(CommentaireService.TARGET_TYPES).includes(commentData.cibleType)) {
      errors.push('Type de cible valide requis (PROJET ou TACHE)')
    }

    // Validation de l'ID de cible
    if (!this.isValidId(commentData.cibleId)) {
      errors.push('ID de cible valide requis')
    }

    return {
      isValid: errors.length === 0,
      errors
    }
  }

  /**
   * Valider un ID
   * @private
   */
  isValidId(id) {
    const numId = parseInt(id, 10)
    return !isNaN(numId) && numId >= CommentaireService.VALIDATION_RULES.MIN_TARGET_ID
  }

  /**
   * Gérer les erreurs de commentaires
   * @private
   */
  handleCommentError(error, defaultErrorCode) {
    if (error.errorCode) {
      throw error
    }

    let errorCode = defaultErrorCode
    let message = error.message

    if (error.response) {
      const status = error.response.status
      const responseData = error.response.data

      switch (status) {
        case 400:
          errorCode = CommentaireService.ERROR_CODES.VALIDATION_ERROR
          break
        case 401:
          errorCode = CommentaireService.ERROR_CODES.UNAUTHORIZED
          break
        case 403:
          errorCode = CommentaireService.ERROR_CODES.FORBIDDEN
          break
        case 404:
          errorCode = CommentaireService.ERROR_CODES.NOT_FOUND
          break
        case 422:
          errorCode = CommentaireService.ERROR_CODES.VALIDATION_ERROR
          break
        case 500:
        case 502:
        case 503:
          errorCode = CommentaireService.ERROR_CODES.SERVER_ERROR
          break
        default:
          errorCode = CommentaireService.ERROR_CODES.NETWORK_ERROR
      }

      if (responseData?.error) {
        message = responseData.error
      } else if (responseData?.message) {
        message = responseData.message
      }
    }

    throw {
      success: false,
      errorCode,
      message: message || 'Erreur inconnue'
    }
  }

  /**
   * Obtenir le message d'erreur internationalisé
   *
   * @param {Object} error - Objet d'erreur du service
   * @param {Function} t - Fonction de traduction Vue i18n
   * @returns {string} Message d'erreur traduit
   */
  getLocalizedErrorMessage(error, t) {
    if (!t || typeof t !== 'function') {
      return error.message || 'Erreur inconnue'
    }

    if (error.errorCode && t(error.errorCode) !== error.errorCode) {
      return t(error.errorCode)
    }

    return error.message || t('comments.errors.general')
  }

  /**
   * Obtenir le message de succès internationalisé
   *
   * @param {string} messageKey - Clé du message
   * @param {Function} t - Fonction de traduction Vue i18n
   * @returns {string} Message de succès traduit
   */
  getLocalizedSuccessMessage(messageKey, t) {
    if (!t || typeof t !== 'function') {
      return 'Succès'
    }

    if (messageKey && t(messageKey) !== messageKey) {
      return t(messageKey)
    }

    return t('comments.success.general')
  }

  /**
   * Obtenir les types de cibles disponibles
   *
   * @returns {Object} Types de cibles avec leurs labels de traduction
   */
  getTargetTypes() {
    return {
      [CommentaireService.TARGET_TYPES.PROJECT]: 'comments.targetTypes.project',
      [CommentaireService.TARGET_TYPES.TASK]: 'comments.targetTypes.task'
    }
  }
}

// Export instance unique
export default new CommentaireService()
