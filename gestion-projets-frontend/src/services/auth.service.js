import api from './api.js'

/**
 * Service d'authentification - Frontend Vue.js
 * Implémente la fonctionnalité F2 du cahier des charges
 * Internationalisation complète FR/EN
 * Aucune donnée en dur - Configuration dynamique
 *
 * @author Équipe Développement
 * @version 1.0
 */
class AuthService {
  /**
   * Codes d'erreur standardisés pour l'internationalisation
   */
  static ERROR_CODES = {
    LOGIN_FAILED: 'auth.errors.loginFailed',
    LOGOUT_FAILED: 'auth.errors.logoutFailed',
    CREDENTIALS_INVALID: 'auth.errors.credentialsInvalid',
    EMAIL_REQUIRED: 'auth.errors.emailRequired',
    PASSWORD_REQUIRED: 'auth.errors.passwordRequired',
    EMAIL_INVALID: 'auth.errors.emailInvalid',
    TOKEN_EXPIRED: 'auth.errors.tokenExpired',
    SESSION_EXPIRED: 'auth.errors.sessionExpired',
    UNAUTHORIZED: 'auth.errors.unauthorized',
    NETWORK_ERROR: 'auth.errors.networkError',
    SERVER_ERROR: 'auth.errors.serverError',
    VALIDATION_ERROR: 'auth.errors.validationError'
  }

  /**
   * Messages de succès standardisés
   */
  static SUCCESS_MESSAGES = {
    LOGIN_SUCCESS: 'auth.success.loginSuccess',
    LOGOUT_SUCCESS: 'auth.success.logoutSuccess',
    PASSWORD_RESET_SENT: 'auth.success.passwordResetSent',
    PASSWORD_RESET_SUCCESS: 'auth.success.passwordResetSuccess'
  }

  /**
   * Clés de stockage de session
   */
  static STORAGE_KEYS = {
    TOKEN: 'auth_token',
    USER: 'auth_user',
    REFRESH_TOKEN: 'auth_refresh_token',
    EXPIRES_AT: 'auth_expires_at',
    LANGUAGE: 'user_language'
  }

  /**
   * Endpoints API
   */
  static ENDPOINTS = {
    LOGIN: '/auth/connexion',
    LOGOUT: '/auth/deconnexion',
    REFRESH: '/auth/refresh',
    FORGOT_PASSWORD: '/auth/mot-de-passe-oublie',
    RESET_PASSWORD: '/auth/reinitialiser-mot-de-passe'
  }

  constructor() {
    this.sessionCheckInterval = null
    this.initializeSessionMonitoring()
  }

  /**
   * F2: Se connecter à la plateforme
   * Validation complète des identifiants
   * Gestion des erreurs internationalisée
   *
   * @param {Object} credentials - Identifiants de connexion
   * @param {string} credentials.email - Adresse email
   * @param {string} credentials.motDePasse - Mot de passe
   * @returns {Promise<Object>} Résultat de la connexion
   */
  async seConnecter(credentials) {
    try {
      console.log('[F2] Tentative de connexion')

      // Validation des entrées
      const validationResult = this.validateLoginCredentials(credentials)
      if (!validationResult.isValid) {
        throw {
          success: false,
          errorCode: AuthService.ERROR_CODES.VALIDATION_ERROR,
          errors: validationResult.errors,
          message: validationResult.errors[0]
        }
      }

      const payload = {
        email: credentials.email.toLowerCase().trim(),
        motDePasse: credentials.motDePasse
      }

      const response = await api.post(AuthService.ENDPOINTS.LOGIN, payload)

      if (!response.data || !response.data.token || !response.data.user) {
        throw new Error('Réponse serveur invalide')
      }

      const { token, user, refreshToken, expiresIn } = response.data

      // Stocker les informations de session
      this.storeSessionData(token, user, refreshToken, expiresIn)

      console.log('[F2] Connexion réussie')

      return {
        success: true,
        data: { token, user },
        messageKey: AuthService.SUCCESS_MESSAGES.LOGIN_SUCCESS
      }
    } catch (error) {
      console.error('[F2] Erreur de connexion:', error)
      return this.handleAuthError(error, AuthService.ERROR_CODES.LOGIN_FAILED)
    }
  }

  /**
   * F2: Se déconnecter de la plateforme
   * Nettoyage complet de la session
   *
   * @returns {Promise<Object>} Confirmation de déconnexion
   */
  async seDeconnecter() {
    try {
      console.log('[F2] Déconnexion utilisateur')

      // Notification serveur (optionnelle - ne pas échouer si erreur)
      try {
        const token = this.obtenirToken()
        if (token) {
          await api.post(AuthService.ENDPOINTS.LOGOUT)
        }
      } catch (serverError) {
        console.warn('[F2] Notification serveur déconnexion échouée:', serverError.message)
      }

      // Nettoyage local obligatoire
      this.clearSessionData()
      this.stopSessionMonitoring()

      console.log('[F2] Déconnexion terminée')

      return {
        success: true,
        messageKey: AuthService.SUCCESS_MESSAGES.LOGOUT_SUCCESS
      }
    } catch (error) {
      console.error('[F2] Erreur déconnexion:', error)

      // Forcer le nettoyage même en cas d'erreur
      this.clearSessionData()
      this.stopSessionMonitoring()

      throw {
        success: false,
        errorCode: AuthService.ERROR_CODES.LOGOUT_FAILED,
        message: error.message
      }
    }
  }

  /**
   * Rafraîchir le token d'authentification
   * Gestion automatique de l'expiration
   *
   * @returns {Promise<Object>} Nouvelles données de token
   */
  async rafraichirToken() {
    try {
      console.log('[F2] Rafraîchissement token')

      const refreshToken = localStorage.getItem(AuthService.STORAGE_KEYS.REFRESH_TOKEN)

      if (!refreshToken) {
        throw new Error('Token de rafraîchissement manquant')
      }

      const response = await api.post(AuthService.ENDPOINTS.REFRESH, { refreshToken })

      if (!response.data) {
        throw new Error('Réponse serveur invalide')
      }

      const { token, user, refreshToken: newRefreshToken, expiresIn } = response.data

      // Mettre à jour les données de session
      this.storeSessionData(token, user, newRefreshToken, expiresIn)

      console.log('[F2] Token rafraîchi avec succès')

      return {
        success: true,
        data: { token, user }
      }
    } catch (error) {
      console.error('[F2] Erreur rafraîchissement token:', error)

      // Session invalide - nettoyer
      this.clearSessionData()

      throw {
        success: false,
        errorCode: AuthService.ERROR_CODES.TOKEN_EXPIRED,
        message: error.message
      }
    }
  }

  /**
   * Demande de mot de passe oublié
   *
   * @param {string} email - Email utilisateur
   * @returns {Promise<Object>} Résultat de la demande
   */
  async motDePasseOublie(email) {
    try {
      console.log('[F2] Demande réinitialisation mot de passe')

      if (!email || !this.isValidEmail(email)) {
        throw {
          success: false,
          errorCode: AuthService.ERROR_CODES.EMAIL_INVALID,
          message: 'Email invalide'
        }
      }

      const payload = { email: email.toLowerCase().trim() }

      await api.post(AuthService.ENDPOINTS.FORGOT_PASSWORD, payload)

      console.log('[F2] Email de réinitialisation envoyé')

      return {
        success: true,
        messageKey: AuthService.SUCCESS_MESSAGES.PASSWORD_RESET_SENT
      }
    } catch (error) {
      console.error('[F2] Erreur réinitialisation:', error)
      return this.handleAuthError(error, AuthService.ERROR_CODES.SERVER_ERROR)
    }
  }

  /**
   * Vérifier si l'utilisateur est connecté
   * Validation de l'expiration du token
   *
   * @returns {boolean} true si connecté et token valide
   */
  estConnecte() {
    const token = localStorage.getItem(AuthService.STORAGE_KEYS.TOKEN)
    const user = localStorage.getItem(AuthService.STORAGE_KEYS.USER)
    const expiresAt = localStorage.getItem(AuthService.STORAGE_KEYS.EXPIRES_AT)

    if (!token || !user) {
      return false
    }

    // Vérifier l'expiration
    if (expiresAt && new Date().getTime() > parseInt(expiresAt)) {
      console.log('[F2] Token expiré')
      this.clearSessionData()
      return false
    }

    return true
  }

  /**
   * Obtenir l'utilisateur connecté
   * Validation et parsing sécurisé
   *
   * @returns {Object|null} Données utilisateur ou null
   */
  obtenirUtilisateurConnecte() {
    try {
      const userJson = localStorage.getItem(AuthService.STORAGE_KEYS.USER)
      if (!userJson) return null

      const user = JSON.parse(userJson)

      // Validation de la structure utilisateur
      if (!user || !user.id || !user.email || !user.role) {
        console.warn('[F2] Données utilisateur invalides')
        this.clearSessionData()
        return null
      }

      return user
    } catch (error) {
      console.error('[F2] Erreur parsing utilisateur:', error)
      this.clearSessionData()
      return null
    }
  }

  /**
   * Obtenir le token d'authentification
   *
   * @returns {string|null} Token ou null
   */
  obtenirToken() {
    return localStorage.getItem(AuthService.STORAGE_KEYS.TOKEN)
  }

  /**
   * Obtenir le rôle de l'utilisateur connecté
   *
   * @returns {string|null} Rôle ou null
   */
  obtenirRoleUtilisateur() {
    const user = this.obtenirUtilisateurConnecte()
    return user?.role || null
  }

  /**
   * Vérifier si l'utilisateur a un rôle spécifique
   *
   * @param {string} role - Rôle à vérifier
   * @returns {boolean} true si l'utilisateur a le rôle
   */
  utilisateurARole(role) {
    if (!role) return false
    return this.obtenirRoleUtilisateur() === role
  }

  /**
   * Vérifier si l'utilisateur a un des rôles spécifiés
   *
   * @param {string[]} roles - Liste des rôles autorisés
   * @returns {boolean} true si l'utilisateur a un des rôles
   */
  utilisateurAUnDesRoles(roles) {
    if (!Array.isArray(roles) || roles.length === 0) return false
    const userRole = this.obtenirRoleUtilisateur()
    return roles.includes(userRole)
  }

  /**
   * Valider les identifiants de connexion
   * @private
   */
  validateLoginCredentials(credentials) {
    const errors = []

    if (!credentials) {
      errors.push('Identifiants requis')
      return { isValid: false, errors }
    }

    if (!credentials.email || !credentials.email.trim()) {
      errors.push('Email requis')
    } else if (!this.isValidEmail(credentials.email)) {
      errors.push('Format email invalide')
    }

    if (!credentials.motDePasse || !credentials.motDePasse.trim()) {
      errors.push('Mot de passe requis')
    }

    return {
      isValid: errors.length === 0,
      errors
    }
  }

  /**
   * Valider le format email
   * @private
   */
  isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(email)
  }

  /**
   * Stocker les données de session
   * @private
   */
  storeSessionData(token, user, refreshToken, expiresIn) {
    if (!token || !user) {
      throw new Error('Données de session invalides')
    }

    localStorage.setItem(AuthService.STORAGE_KEYS.TOKEN, token)
    localStorage.setItem(AuthService.STORAGE_KEYS.USER, JSON.stringify(user))

    if (refreshToken) {
      localStorage.setItem(AuthService.STORAGE_KEYS.REFRESH_TOKEN, refreshToken)
    }

    if (expiresIn && typeof expiresIn === 'number') {
      const expiresAt = new Date().getTime() + (expiresIn * 1000)
      localStorage.setItem(AuthService.STORAGE_KEYS.EXPIRES_AT, expiresAt.toString())
    }

    // Redémarrer la surveillance de session
    this.initializeSessionMonitoring()
  }

  /**
   * Vider toutes les données de session
   * @private
   */
  clearSessionData() {
    Object.values(AuthService.STORAGE_KEYS).forEach(key => {
      localStorage.removeItem(key)
    })
  }

  /**
   * Initialiser la surveillance de session
   * @private
   */
  initializeSessionMonitoring() {
    this.stopSessionMonitoring()

    // Vérifier la session toutes les minutes
    this.sessionCheckInterval = setInterval(() => {
      if (!this.estConnecte()) {
        this.stopSessionMonitoring()
        // Émettre un événement pour notifier l'application
        window.dispatchEvent(new CustomEvent('auth:sessionExpired'))
      }
    }, 60000) // 1 minute
  }

  /**
   * Arrêter la surveillance de session
   * @private
   */
  stopSessionMonitoring() {
    if (this.sessionCheckInterval) {
      clearInterval(this.sessionCheckInterval)
      this.sessionCheckInterval = null
    }
  }

  /**
   * Gérer les erreurs d'authentification
   * @private
   */
  handleAuthError(error, defaultErrorCode) {
    if (error.errorCode) {
      throw error
    }

    let errorCode = defaultErrorCode
    let message = error.message

    if (error.response) {
      const status = error.response.status
      const responseData = error.response.data

      switch (status) {
        case 401:
          errorCode = AuthService.ERROR_CODES.CREDENTIALS_INVALID
          break
        case 403:
          errorCode = AuthService.ERROR_CODES.UNAUTHORIZED
          break
        case 429:
          errorCode = AuthService.ERROR_CODES.SERVER_ERROR
          message = 'Trop de tentatives. Réessayez plus tard.'
          break
        case 500:
        case 502:
        case 503:
          errorCode = AuthService.ERROR_CODES.SERVER_ERROR
          break
        default:
          errorCode = AuthService.ERROR_CODES.NETWORK_ERROR
      }

      if (responseData?.error) {
        message = responseData.error
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

    return error.message || t('auth.errors.general')
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

    return t('auth.success.general')
  }

  /**
   * Nettoyer les ressources lors de la destruction
   */
  destroy() {
    this.stopSessionMonitoring()
  }
}

// Export instance unique
export default new AuthService()
