import api from './api.js'


/**
 * Service de gestion des tâches - Frontend Vue.js
 * Workflow : BROUILLON → EN_ATTENTE_VALIDATION → TERMINE ou ANNULE
 * Internationalisation complète FR/EN

 * @author Elhadj Souleymane BAH
 */
class TacheService {
  /**
   * États des tâches selon les spécifications F7
   */
  static STATES = {
    BROUILLON: 'BROUILLON',
    EN_ATTENTE_VALIDATION: 'EN_ATTENTE_VALIDATION',
    TERMINE: 'TERMINE',
    ANNULE: 'ANNULE'
  }

  /**
   * Priorités des tâches (cohérent avec PrioriteTache.java)
   */
  static PRIORITIES = {
    FAIBLE: 'FAIBLE',
    NORMALE: 'NORMALE',
    HAUTE: 'HAUTE'
  }

  /**
   * Codes d'erreur standardisés pour l'internationalisation
   */
  static ERROR_CODES = {
    CREATE_TASK_FAILED: 'tasks.errors.createFailed',
    UPDATE_TASK_FAILED: 'tasks.errors.updateFailed',
    DELETE_TASK_FAILED: 'tasks.errors.deleteFailed',
    GET_TASKS_FAILED: 'tasks.errors.loadFailed',
    CHANGE_STATUS_FAILED: 'tasks.errors.statusChangeFailed',
    TITLE_REQUIRED: 'tasks.errors.titleRequired',
    TITLE_TOO_LONG: 'tasks.errors.titleTooLong',
    DESCRIPTION_TOO_LONG: 'tasks.errors.descriptionTooLong',
    PROJECT_ID_INVALID: 'tasks.errors.projectIdInvalid',
    TASK_ID_INVALID: 'tasks.errors.taskIdInvalid',
    USER_ID_INVALID: 'tasks.errors.userIdInvalid',
    PRIORITY_INVALID: 'tasks.errors.priorityInvalid',
    STATUS_INVALID: 'tasks.errors.statusInvalid',
    TRANSITION_INVALID: 'tasks.errors.transitionInvalid',
    DATE_INVALID: 'tasks.errors.dateInvalid',
    UNAUTHORIZED: 'tasks.errors.unauthorized',
    FORBIDDEN: 'tasks.errors.forbidden',
    NOT_FOUND: 'tasks.errors.notFound',
    NETWORK_ERROR: 'tasks.errors.networkError',
    SERVER_ERROR: 'tasks.errors.serverError',
    VALIDATION_ERROR: 'tasks.errors.validationError',
    WORKFLOW_ERROR: 'tasks.errors.workflowError'
  }

  /**
   * Messages de succès standardisés
   */
  static SUCCESS_MESSAGES = {
    TASK_CREATED: 'tasks.success.created',
    TASK_UPDATED: 'tasks.success.updated',
    TASK_DELETED: 'tasks.success.deleted',
    STATUS_CHANGED: 'tasks.success.statusChanged',
    TASK_SUBMITTED: 'tasks.success.submitted',
    TASK_VALIDATED: 'tasks.success.validated',
    TASK_RETURNED: 'tasks.success.returned',
    TASK_CANCELLED: 'tasks.success.cancelled',
    TASKS_LOADED: 'tasks.success.loaded'
  }

  /**
   * Endpoints API
   */
  static ENDPOINTS = {
    TASKS: '/api/taches',
    PROJECT_TASKS: '/api/taches/projet',
    USER_TASKS: '/api/taches/utilisateur'
  }


  /**
   * Configuration de validation
   */
  static VALIDATION_RULES = {
    TITLE_MIN_LENGTH: 1,
    TITLE_MAX_LENGTH: 200,
    DESCRIPTION_MAX_LENGTH: 2000,
    MIN_ID: 1
  }

  /**
   * Transitions d'état autorisées selon le workflow F7
   */
  static ALLOWED_TRANSITIONS = {
    [this.STATES?.BROUILLON]: ['EN_ATTENTE_VALIDATION', 'ANNULE'],
    [this.STATES?.EN_ATTENTE_VALIDATION]: ['TERMINE', 'BROUILLON', 'ANNULE'],
    [this.STATES?.TERMINE]: ['ANNULE'],
    [this.STATES?.ANNULE]: [] // État final
  }

  constructor() {
    // Initialiser les transitions après que les STATES soient définis
    this.initializeTransitions()
  }

  /**
   * Initialiser les transitions d'état
   * @private
   */
  initializeTransitions() {
    this.constructor.ALLOWED_TRANSITIONS = {
      [TacheService.STATES.BROUILLON]: [
        TacheService.STATES.EN_ATTENTE_VALIDATION,
        TacheService.STATES.ANNULE
      ],
      [TacheService.STATES.EN_ATTENTE_VALIDATION]: [
        TacheService.STATES.TERMINE,
        TacheService.STATES.BROUILLON,
        TacheService.STATES.ANNULE
      ],
      [TacheService.STATES.TERMINE]: [
        TacheService.STATES.ANNULE
      ],
      [TacheService.STATES.ANNULE]: []
    }
  }

  /**
   * F7: Créer une nouvelle tâche
   * Contrainte: Abonnement actif requis pour la création
   * État initial: BROUILLON
   *
   * @param {Object} tacheData - Données de la tâche
   * @param {string} tacheData.titre - Titre de la tâche
   * @param {string} tacheData.description - Description de la tâche
   * @param {number} tacheData.idProjet - ID du projet
   * @param {number} tacheData.idAssigne - ID de l'utilisateur assigné
   * @param {string} tacheData.priorite - Priorité (FAIBLE, NORMALE, HAUTE)
   * @param {string} tacheData.dateEcheance - Date d'échéance (ISO string)
   * @returns {Promise<Object>} Tâche créée
   */
  async creerTache(tacheData) {
    try {
      console.log('[F7] Création nouvelle tâche')

      // Validation des données
      const validationResult = this.validateTaskData(tacheData)
      if (!validationResult.isValid) {
        throw {
          success: false,
          errorCode: TacheService.ERROR_CODES.VALIDATION_ERROR,
          errors: validationResult.errors,
          message: validationResult.errors[0]
        }
      }

      const payload = {
        titre: tacheData.titre.trim(),
        description: tacheData.description?.trim() || '',
        idProjet: parseInt(tacheData.idProjet, 10),
        idAssigne: tacheData.idAssigne ? parseInt(tacheData.idAssigne, 10) : null,
        priorite: tacheData.priorite || TacheService.PRIORITIES.NORMALE,
        dateEcheance: tacheData.dateEcheance || null,
        statut: TacheService.STATES.BROUILLON
      }

      const response = await api.post(TacheService.ENDPOINTS.TASKS, payload)

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F7] Tâche créée avec succès avec statut BROUILLON')

      return {
        success: true,
        data: response.data,
        messageKey: TacheService.SUCCESS_MESSAGES.TASK_CREATED
      }
    } catch (error) {
      console.error('[F7] Erreur création tâche:', error)
      return this.handleTaskError(error, TacheService.ERROR_CODES.CREATE_TASK_FAILED)
    }
  }

  /**
   * F7: Obtenir les tâches d'un projet
   * Avec filtres et pagination
   *
   * @param {number} projetId - ID du projet (correspond à idProjet backend)
   * @param {Object} filters - Filtres de recherche
   * @param {string} filters.statut - Filtrer par statut
   * @param {number} filters.idAssigne - Filtrer par utilisateur assigné
   * @param {string} filters.priorite - Filtrer par priorité
   * @param {number} filters.page - Page (pour pagination)
   * @param {number} filters.limit - Limite par page
   * @returns {Promise<Object>} Liste des tâches
   */
  async getTachesProjet(projetId, filters = {}) {
    try {
      console.log('[F7] Chargement tâches projet')

      // Validation de l'ID projet
      if (!this.isValidId(projetId)) {
        throw {
          success: false,
          errorCode: TacheService.ERROR_CODES.PROJECT_ID_INVALID,
          message: 'ID de projet invalide'
        }
      }

      // Nettoyer et valider les filtres
      const cleanFilters = this.cleanFilters(filters)

      const url = `${TacheService.ENDPOINTS.PROJECT_TASKS}/${projetId}`
      const response = await api.get(url, { params: cleanFilters })

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F7] Tâches projet chargées avec succès')

      return {
        success: true,
        data: Array.isArray(response.data) ? response.data : [],
        messageKey: TacheService.SUCCESS_MESSAGES.TASKS_LOADED
      }
    } catch (error) {
      console.error('[F7] Erreur chargement tâches projet:', error)
      return this.handleTaskError(error, TacheService.ERROR_CODES.GET_TASKS_FAILED)
    }
  }

  /**
   * F7: Obtenir les tâches assignées à un utilisateur
   *
   * @param {number} utilisateurId - ID de l'utilisateur
   * @param {Object} filters - Filtres optionnels
   * @returns {Promise<Object>} Liste des tâches assignées
   */
  async getTachesUtilisateur(utilisateurId, filters = {}) {
    try {
      console.log('[F7] Chargement tâches utilisateur')

      // Validation de l'ID utilisateur
      if (!this.isValidId(utilisateurId)) {
        throw {
          success: false,
          errorCode: TacheService.ERROR_CODES.USER_ID_INVALID,
          message: 'ID utilisateur invalide'
        }
      }

      const cleanFilters = this.cleanFilters(filters)

      const url = `${TacheService.ENDPOINTS.USER_TASKS}/${utilisateurId}`
      const response = await api.get(url, { params: cleanFilters })

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F7] Tâches utilisateur chargées avec succès')

      return {
        success: true,
        data: Array.isArray(response.data) ? response.data : [],
        messageKey: TacheService.SUCCESS_MESSAGES.TASKS_LOADED
      }
    } catch (error) {
      console.error('[F7] Erreur chargement tâches utilisateur:', error)
      return this.handleTaskError(error, TacheService.ERROR_CODES.GET_TASKS_FAILED)
    }
  }

  /**
   * F7: Mettre à jour une tâche
   * Permissions selon les spécifications:
   * - Chef de projet: toutes les tâches du projet
   * - Membre: seulement ses tâches assignées
   *
   * @param {number} tacheId - ID de la tâche
   * @param {Object} updateData - Données à mettre à jour
   * @returns {Promise<Object>} Tâche mise à jour
   */
  async mettreAJourTache(tacheId, updateData) {
    try {
      console.log('[F7] Mise à jour tâche')

      // Validation de l'ID tâche
      if (!this.isValidId(tacheId)) {
        throw {
          success: false,
          errorCode: TacheService.ERROR_CODES.TASK_ID_INVALID,
          message: 'ID de tâche invalide'
        }
      }

      // Validation des données de mise à jour
      const validationResult = this.validateUpdateData(updateData)
      if (!validationResult.isValid) {
        throw {
          success: false,
          errorCode: TacheService.ERROR_CODES.VALIDATION_ERROR,
          errors: validationResult.errors,
          message: validationResult.errors[0]
        }
      }

      // Nettoyer les données
      const cleanData = this.cleanUpdateData(updateData)

      const url = `${TacheService.ENDPOINTS.TASKS}/${tacheId}`
      const response = await api.put(url, cleanData)

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F7] Tâche mise à jour avec succès')

      return {
        success: true,
        data: response.data,
        messageKey: TacheService.SUCCESS_MESSAGES.TASK_UPDATED
      }
    } catch (error) {
      console.error('[F7] Erreur mise à jour tâche:', error)
      return this.handleTaskError(error, TacheService.ERROR_CODES.UPDATE_TASK_FAILED)
    }
  }

  /**
   * F7: Changer le statut d'une tâche (workflow)
   * Actions selon l'état et le rôle:
   *
   * BROUILLON → EN_ATTENTE_VALIDATION (Soumettre)
   * EN_ATTENTE_VALIDATION → TERMINE (Valider - Chef projet uniquement)
   * EN_ATTENTE_VALIDATION → BROUILLON (Renvoyer en brouillon - Chef projet)
   * TERMINE → EN_ATTENTE_VALIDATION (Modifier - Chef projet)
   * * → ANNULE (Annuler - Chef projet ou Admin)
   *
   * @param {number} tacheId - ID de la tâche
   * @param {string} nouveauStatut - Nouveau statut
   * @param {string} commentaire - Commentaire optionnel
   * @returns {Promise<Object>} Tâche avec nouveau statut
   */
  async changerStatutTache(tacheId, nouveauStatut, commentaire = '') {
    try {
      console.log('[F7] Changement statut tâche')

      // Validation de l'ID tâche
      if (!this.isValidId(tacheId)) {
        throw {
          success: false,
          errorCode: TacheService.ERROR_CODES.TASK_ID_INVALID,
          message: 'ID de tâche invalide'
        }
      }

      // Validation du nouveau statut
      if (!Object.values(TacheService.STATES).includes(nouveauStatut)) {
        throw {
          success: false,
          errorCode: TacheService.ERROR_CODES.STATUS_INVALID,
          message: 'Statut de tâche invalide'
        }
      }

      const payload = {
        statut: nouveauStatut,
        commentaire: commentaire?.trim() || ''
      }

      const url = `${TacheService.ENDPOINTS.TASKS}/${tacheId}/statut`
      const response = await api.patch(url, payload)

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      console.log('[F7] Statut tâche changé avec succès')

      return {
        success: true,
        data: response.data,
        messageKey: TacheService.SUCCESS_MESSAGES.STATUS_CHANGED
      }
    } catch (error) {
      console.error('[F7] Erreur changement statut:', error)
      return this.handleTaskError(error, TacheService.ERROR_CODES.CHANGE_STATUS_FAILED)
    }
  }

  /**
   * F7: Soumettre une tâche pour validation
   * BROUILLON → EN_ATTENTE_VALIDATION
   *
   * @param {number} tacheId - ID de la tâche
   * @param {string} commentaire - Commentaire optionnel
   * @returns {Promise<Object>} Tâche soumise
   */
  async soumettreTaskeValidation(tacheId, commentaire = '') {
    try {
      const result = await this.changerStatutTache(
        tacheId,
        TacheService.STATES.EN_ATTENTE_VALIDATION,
        commentaire || 'Tâche soumise pour validation'
      )

      return {
        ...result,
        messageKey: TacheService.SUCCESS_MESSAGES.TASK_SUBMITTED
      }
    } catch (error) {
      console.error('[F7] Erreur soumission tâche:', error)
      throw error
    }
  }

  /**
   * F7: Valider une tâche (Chef de projet uniquement)
   * EN_ATTENTE_VALIDATION → TERMINE
   *
   * @param {number} tacheId - ID de la tâche
   * @param {string} commentaire - Commentaire de validation
   * @returns {Promise<Object>} Tâche validée
   */
  async validerTache(tacheId, commentaire = '') {
    try {
      const result = await this.changerStatutTache(
        tacheId,
        TacheService.STATES.TERMINE,
        commentaire || 'Tâche validée'
      )

      return {
        ...result,
        messageKey: TacheService.SUCCESS_MESSAGES.TASK_VALIDATED
      }
    } catch (error) {
      console.error('[F7] Erreur validation tâche:', error)
      throw error
    }
  }

  /**
   * F7: Renvoyer une tâche en brouillon (Chef de projet uniquement)
   * EN_ATTENTE_VALIDATION → BROUILLON
   *
   * @param {number} tacheId - ID de la tâche
   * @param {string} commentaire - Raison du renvoi
   * @returns {Promise<Object>} Tâche renvoyée en brouillon
   */
  async renvoyerEnBrouillon(tacheId, commentaire = '') {
    try {
      const result = await this.changerStatutTache(
        tacheId,
        TacheService.STATES.BROUILLON,
        commentaire || 'Tâche renvoyée en brouillon'
      )

      return {
        ...result,
        messageKey: TacheService.SUCCESS_MESSAGES.TASK_RETURNED
      }
    } catch (error) {
      console.error('[F7] Erreur renvoi brouillon:', error)
      throw error
    }
  }

  /**
   * F7: Annuler une tâche (Chef de projet ou Admin)
   * * → ANNULE
   *
   * @param {number} tacheId - ID de la tâche
   * @param {string} raisonAnnulation - Raison de l'annulation
   * @returns {Promise<Object>} Tâche annulée
   */
  async annulerTache(tacheId, raisonAnnulation = '') {
    try {
      const result = await this.changerStatutTache(
        tacheId,
        TacheService.STATES.ANNULE,
        raisonAnnulation || 'Tâche annulée'
      )

      return {
        ...result,
        messageKey: TacheService.SUCCESS_MESSAGES.TASK_CANCELLED
      }
    } catch (error) {
      console.error('[F7] Erreur annulation tâche:', error)
      throw error
    }
  }

  /**
   * F7: Supprimer une tâche (Chef de projet uniquement)
   *
   * @param {number} tacheId - ID de la tâche
   * @returns {Promise<Object>} Confirmation de suppression
   */
  async supprimerTache(tacheId) {
    try {
      console.log('[F7] Suppression tâche')

      // Validation de l'ID tâche
      if (!this.isValidId(tacheId)) {
        throw {
          success: false,
          errorCode: TacheService.ERROR_CODES.TASK_ID_INVALID,
          message: 'ID de tâche invalide'
        }
      }

      const url = `${TacheService.ENDPOINTS.TASKS}/${tacheId}`
      await api.delete(url)

      console.log('[F7] Tâche supprimée avec succès')

      return {
        success: true,
        messageKey: TacheService.SUCCESS_MESSAGES.TASK_DELETED
      }
    } catch (error) {
      console.error('[F7] Erreur suppression tâche:', error)
      return this.handleTaskError(error, TacheService.ERROR_CODES.DELETE_TASK_FAILED)
    }
  }

  /**
   * Vérifier si une transition de statut est valide
   *
   * @param {string} statutActuel - Statut actuel de la tâche
   * @param {string} nouveauStatut - Nouveau statut souhaité
   * @returns {boolean} true si la transition est valide
   */
  estTransitionValide(statutActuel, nouveauStatut) {
    const transitions = this.constructor.ALLOWED_TRANSITIONS[statutActuel] || []
    return transitions.includes(nouveauStatut)
  }

  /**
   * Obtenir les transitions disponibles pour un statut donné
   *
   * @param {string} statutActuel - Statut actuel de la tâche
   * @returns {string[]} Liste des statuts possibles
   */
  getTransitionsDisponibles(statutActuel) {
    return this.constructor.ALLOWED_TRANSITIONS[statutActuel] || []
  }

  /**
   * Obtenir le libellé du statut pour l'internationalisation
   *
   * @param {string} status - Statut de la tâche
   * @returns {string} Clé de traduction pour le statut
   */
  getStatusLabel(status) {
    const labels = {
      [TacheService.STATES.BROUILLON]: 'tasks.status.draft',
      [TacheService.STATES.EN_ATTENTE_VALIDATION]: 'tasks.status.awaitingValidation',
      [TacheService.STATES.TERMINE]: 'tasks.status.completed',
      [TacheService.STATES.ANNULE]: 'tasks.status.cancelled'
    }

    return labels[status] || 'tasks.status.unknown'
  }

  /**
   * Obtenir le libellé de la priorité pour l'internationalisation
   *
   * @param {string} priority - Priorité de la tâche
   * @returns {string} Clé de traduction pour la priorité
   */
  getPriorityLabel(priority) {
    const labels = {
      [TacheService.PRIORITIES.FAIBLE]: 'tasks.priority.low',
      [TacheService.PRIORITIES.NORMALE]: 'tasks.priority.normal',
      [TacheService.PRIORITIES.HAUTE]: 'tasks.priority.high'
    }

    return labels[priority] || 'tasks.priority.normal'
  }

  /**
   * Obtenir tous les statuts disponibles avec leurs labels
   *
   * @returns {Object} Statuts avec leurs labels de traduction
   */
  getAllStatuses() {
    return Object.keys(TacheService.STATES).reduce((acc, key) => {
      const status = TacheService.STATES[key]
      acc[status] = this.getStatusLabel(status)
      return acc
    }, {})
  }

  /**
   * Obtenir toutes les priorités disponibles avec leurs labels
   *
   * @returns {Object} Priorités avec leurs labels de traduction
   */
  getAllPriorities() {
    return Object.keys(TacheService.PRIORITIES).reduce((acc, key) => {
      const priority = TacheService.PRIORITIES[key]
      acc[priority] = this.getPriorityLabel(priority)
      return acc
    }, {})
  }

  /**
   * Valider les données d'une tâche pour la création
   * @private
   */
  validateTaskData(tacheData) {
    const errors = []

    if (!tacheData) {
      errors.push('Données de tâche requises')
      return { isValid: false, errors }
    }

    // Validation du titre
    if (!tacheData.titre || typeof tacheData.titre !== 'string') {
      errors.push('Titre de la tâche requis')
    } else {
      const titre = tacheData.titre.trim()
      if (titre.length < TacheService.VALIDATION_RULES.TITLE_MIN_LENGTH) {
        errors.push('Le titre de la tâche ne peut pas être vide')
      }
      if (titre.length > TacheService.VALIDATION_RULES.TITLE_MAX_LENGTH) {
        errors.push(`Le titre doit faire moins de ${TacheService.VALIDATION_RULES.TITLE_MAX_LENGTH} caractères`)
      }
    }

    // Validation de la description (optionnelle)
    if (tacheData.description && tacheData.description.length > TacheService.VALIDATION_RULES.DESCRIPTION_MAX_LENGTH) {
      errors.push(`La description doit faire moins de ${TacheService.VALIDATION_RULES.DESCRIPTION_MAX_LENGTH} caractères`)
    }

    // Validation de l'ID projet (corrigé: idProjet au lieu de projetId)
    if (!this.isValidId(tacheData.idProjet)) {
      errors.push('ID de projet valide requis')
    }

    // Validation de la priorité (optionnelle)
    if (tacheData.priorite && !Object.values(TacheService.PRIORITIES).includes(tacheData.priorite)) {
      errors.push('Priorité invalide')
    }

    // Validation de la date d'échéance (optionnelle)
    if (tacheData.dateEcheance && !this.isValidDate(tacheData.dateEcheance)) {
      errors.push('Date d\'échéance invalide')
    }

    // Validation de l'utilisateur assigné (optionnel, corrigé: idAssigne)
    if (tacheData.idAssigne && !this.isValidId(tacheData.idAssigne)) {
      errors.push('ID utilisateur assigné invalide')
    }

    return {
      isValid: errors.length === 0,
      errors
    }
  }

  /**
   * Valider les données de mise à jour
   * @private
   */
  validateUpdateData(updateData) {
    const errors = []

    if (!updateData || typeof updateData !== 'object') {
      errors.push('Données de mise à jour requises')
      return { isValid: false, errors }
    }

    // Validation du titre (si fourni)
    if (updateData.titre !== undefined) {
      if (!updateData.titre || typeof updateData.titre !== 'string') {
        errors.push('Titre invalide')
      } else {
        const titre = updateData.titre.trim()
        if (titre.length < TacheService.VALIDATION_RULES.TITLE_MIN_LENGTH) {
          errors.push('Le titre ne peut pas être vide')
        }
        if (titre.length > TacheService.VALIDATION_RULES.TITLE_MAX_LENGTH) {
          errors.push(`Le titre doit faire moins de ${TacheService.VALIDATION_RULES.TITLE_MAX_LENGTH} caractères`)
        }
      }
    }

    // Validation de la description (si fournie)
    if (updateData.description !== undefined && updateData.description !== null) {
      if (typeof updateData.description === 'string' &&
        updateData.description.length > TacheService.VALIDATION_RULES.DESCRIPTION_MAX_LENGTH) {
        errors.push(`La description doit faire moins de ${TacheService.VALIDATION_RULES.DESCRIPTION_MAX_LENGTH} caractères`)
      }
    }

    // Validation de la priorité (si fournie)
    if (updateData.priorite && !Object.values(TacheService.PRIORITIES).includes(updateData.priorite)) {
      errors.push('Priorité invalide')
    }

    // Validation de la date d'échéance (si fournie)
    if (updateData.dateEcheance && !this.isValidDate(updateData.dateEcheance)) {
      errors.push('Date d\'échéance invalide')
    }

    // Validation de l'utilisateur assigné (si fourni, corrigé: idAssigne)
    if (updateData.idAssigne !== undefined && updateData.idAssigne !== null && !this.isValidId(updateData.idAssigne)) {
      errors.push('ID utilisateur assigné invalide')
    }

    return {
      isValid: errors.length === 0,
      errors
    }
  }

  /**
   * Nettoyer les filtres
   * @private
   */
  cleanFilters(filters) {
    const cleaned = {}

    if (filters.statut && Object.values(TacheService.STATES).includes(filters.statut)) {
      cleaned.statut = filters.statut
    }

    if (filters.priorite && Object.values(TacheService.PRIORITIES).includes(filters.priorite)) {
      cleaned.priorite = filters.priorite
    }

    // Corrigé: idAssigne au lieu de assigneAId
    if (this.isValidId(filters.idAssigne)) {
      cleaned.idAssigne = parseInt(filters.idAssigne, 10)
    }

    if (filters.page && !isNaN(filters.page) && filters.page > 0) {
      cleaned.page = parseInt(filters.page, 10)
    }

    if (filters.limit && !isNaN(filters.limit) && filters.limit > 0 && filters.limit <= 100) {
      cleaned.limit = parseInt(filters.limit, 10)
    }

    return cleaned
  }

  /**
   * Nettoyer les données de mise à jour
   * @private
   */
  cleanUpdateData(updateData) {
    const cleaned = {}

    if (updateData.titre) {
      cleaned.titre = updateData.titre.trim()
    }

    if (updateData.description !== undefined) {
      cleaned.description = updateData.description ? updateData.description.trim() : ''
    }

    if (updateData.priorite) {
      cleaned.priorite = updateData.priorite
    }

    if (updateData.dateEcheance) {
      cleaned.dateEcheance = updateData.dateEcheance
    }

    // Corrigé: idAssigne au lieu de assigneAId
    if (updateData.idAssigne !== undefined) {
      cleaned.idAssigne = updateData.idAssigne ? parseInt(updateData.idAssigne, 10) : null
    }

    return cleaned
  }

  /**
   * Valider un ID
   * @private
   */
  isValidId(id) {
    const numId = parseInt(id, 10)
    return !isNaN(numId) && numId >= TacheService.VALIDATION_RULES.MIN_ID
  }

  /**
   * Valider une date
   * @private
   */
  isValidDate(dateString) {
    if (!dateString) return false
    const date = new Date(dateString)
    return date instanceof Date && !isNaN(date.getTime())
  }

  /**
   * Gérer les erreurs de tâches
   * @private
   */
  handleTaskError(error, defaultErrorCode) {
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
          errorCode = TacheService.ERROR_CODES.VALIDATION_ERROR
          break
        case 401:
          errorCode = TacheService.ERROR_CODES.UNAUTHORIZED
          break
        case 403:
          errorCode = TacheService.ERROR_CODES.FORBIDDEN
          break
        case 404:
          errorCode = TacheService.ERROR_CODES.NOT_FOUND
          break
        case 422:
          errorCode = TacheService.ERROR_CODES.WORKFLOW_ERROR
          break
        case 500:
        case 502:
        case 503:
          errorCode = TacheService.ERROR_CODES.SERVER_ERROR
          break
        default:
          errorCode = TacheService.ERROR_CODES.NETWORK_ERROR
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

    return error.message || t('tasks.errors.general')
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

    return t('tasks.success.general')
  }

  /**
   * F7 - Obtenir les tâches par chef de projet
   * Récupère toutes les tâches liées aux projets dont l'utilisateur est chef
   *
   * @param {number} idChef - ID du chef de projet
   * @returns {Promise<Object>} Liste des tâches (DTO)
   */
  async byChefProjet(idChef) {
    try {
      console.log('[F7] Chargement des tâches du chef de projet', idChef);

      // Vérification de l'ID
      if (!this.isValidId(idChef)) {
        throw {
          success: false,
          errorCode: TacheService.ERROR_CODES.USER_ID_INVALID,
          message: 'ID chef de projet invalide'
        };
      }

      // Appel à l’API backend
      const response = await api.get(`/api/taches/chef/${idChef}`);

      if (!response.data) {
        throw new Error('Réponse serveur invalide');
      }

      console.log('[F7] Tâches du chef de projet chargées avec succès');

      return {
        success: true,
        data: Array.isArray(response.data) ? response.data : [],
        messageKey: TacheService.SUCCESS_MESSAGES.TASKS_LOADED
      };
    } catch (error) {
      console.error('[F7] Erreur chargement tâches chef de projet:', error);
      return this.handleTaskError(error, TacheService.ERROR_CODES.GET_TASKS_FAILED);
    }
  }

}


export default new TacheService()
