// src/services/utilisateur.service.js
import api, { endpoints } from './api.js'

/**
 * Service pour la gestion des utilisateurs - Frontend Vue.js
 * Mappe les fonctionnalités F1, F4, F5, F8 du cahier des charges
 * Complètement internationalisé avec codes d'erreur standardisés
 *
 * @author Équipe Développement
 * @version 1.0
 */
class UtilisateurService {

  static ERROR_CODES = {
    INSCRIPTION_FAILED: 'erreurs.inscription',
    PROFILE_LOAD_FAILED: 'erreurs.chargementProfil',
    PROFILE_UPDATE_FAILED: 'erreurs.miseAJourProfil',
    USER_SEARCH_FAILED: 'erreurs.rechercheUtilisateur',
    USERS_SEARCH_FAILED: 'erreurs.rechercheUtilisateurs',
    MY_PROFILE_LOAD_FAILED: 'erreurs.chargementMonProfil',
    PASSWORD_CHANGE_FAILED: 'erreurs.changementMotDePasse',
    STATISTICS_LOAD_FAILED: 'erreurs.chargementStatistiques',
    EMAIL_VERIFICATION_FAILED: 'erreurs.verificationEmail'
  }

  /**
   * F1 : S'inscrire - Inscription d'un nouvel utilisateur
   * Conforme au cahier des charges avec validation CGU obligatoire
   *
   * @param {Object} payload - Données d'inscription
   * @param {string} payload.nom - Nom de famille
   * @param {string} payload.prenom - Prénom
   * @param {string} payload.email - Adresse email
   * @param {string} payload.motDePasse - Mot de passe
   * @param {string} payload.langue - Langue préférée (fr/en)
   * @param {boolean} payload.cguAccepte - Acceptation CGU obligatoire
   * @returns {Promise<Object>} Résultat de l'inscription
   */
  async inscrire(payload) {
    try {
      // Mapping frontend → backend pour respecter le DTO backend
      const payloadBackend = {
        nom: payload.nom,
        prenom: payload.prenom,
        email: payload.email,
        motDePasse: payload.motDePasse,
        langue: payload.langue || 'fr',
        // Mapping spécifique pour les CGU (conformité RGPD cahier des charges)
        cguAccepte: payload.acceptCGU || payload.cguAccepte || false
      }

      console.log('[F1] User registration for email:', payloadBackend.email)

      const response = await api.post(`${endpoints.users}/inscription`, payloadBackend)

      console.log('[F1] Registration successful')
      return {
        success: true,
        data: response.data,
        user: response.data
      }
    } catch (error) {
      console.error('[F1] Registration error:', error.response?.data || error.message)
      throw {
        success: false,
        errorCode: UtilisateurService.ERROR_CODES.INSCRIPTION_FAILED,
        message: error.response?.data?.error,
        status: error.response?.status,
        errors: error.response?.data?.errors
      }
    }
  }

  /**
   * F4 : Consulter son profil - Obtenir les informations utilisateur
   * Affiche informations personnelles et projets actifs
   *
   * @param {number} utilisateurId - ID de l'utilisateur
   * @returns {Promise<Object>} Données du profil utilisateur
   */
  async obtenirProfil(utilisateurId) {
    try {
      console.log('[F4] Loading user profile ID:', utilisateurId)

      const response = await api.get(`${endpoints.users}/${utilisateurId}`)

      console.log('[F4] Profile loaded successfully')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('[F4] Profile loading error:', error.response?.data || error.message)
      throw {
        success: false,
        errorCode: UtilisateurService.ERROR_CODES.PROFILE_LOAD_FAILED,
        message: error.response?.data?.error
      }
    }
  }

  /**
   * F5 : Mettre à jour son profil - Modification des informations
   * Permet modification informations et préférences
   *
   * @param {number} utilisateurId - ID de l'utilisateur
   * @param {Object} payload - Nouvelles données du profil
   * @returns {Promise<Object>} Profil mis à jour
   */
  async mettreAJourProfil(utilisateurId, payload) {
    try {
      console.log('[F5] Updating profile for user ID:', utilisateurId)

      const response = await api.put(`${endpoints.users}/${utilisateurId}`, payload)

      console.log('[F5] Profile updated successfully')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('[F5] Profile update error:', error.response?.data || error.message)
      throw {
        success: false,
        errorCode: UtilisateurService.ERROR_CODES.PROFILE_UPDATE_FAILED,
        message: error.response?.data?.error
      }
    }
  }

  /**
   * F8 : Recherche d'utilisateurs par email (pour chefs de projet)
   * Permet aux chefs de projet de découvrir des utilisateurs à ajouter
   *
   * @param {string} email - Email à rechercher
   * @returns {Promise<Object>} Utilisateur trouvé ou null
   */
  async rechercherParEmail(email) {
    try {
      console.log('[F8] Searching user by email:', email)

      const response = await api.get(`${endpoints.users}/recherche/email`, {
        params: { email }
      })

      console.log('[F8] Search completed')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('[F8] User search error:', error.response?.data || error.message)
      throw {
        success: false,
        errorCode: UtilisateurService.ERROR_CODES.USER_SEARCH_FAILED,
        message: error.response?.data?.error
      }
    }
  }

  /**
   * Recherche générale d'utilisateurs
   * Support pour F8 - recherche élargie
   *
   * @param {string} terme - Terme de recherche
   * @returns {Promise<Object>} Liste d'utilisateurs
   */
  async rechercherUtilisateurs(terme = '') {
    try {
      console.log('Searching users with term:', terme)

      const response = await api.get(`${endpoints.users}/recherche`, {
        params: { q: terme }
      })

      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('Users search error:', error.response?.data || error.message)
      throw {
        success: false,
        errorCode: UtilisateurService.ERROR_CODES.USERS_SEARCH_FAILED,
        message: error.response?.data?.error
      }
    }
  }

  /**
   * Vérification disponibilité email (support F1)
   * Validation en temps réel pour l'inscription
   *
   * @param {string} email - Email à vérifier
   * @returns {Promise<Object>} Disponibilité de l'email
   */
  async verifierEmailDisponible(email) {
    try {
      const response = await api.get(`${endpoints.users}/email-disponible`, {
        params: { email }
      })

      return {
        success: true,
        disponible: response.data?.disponible || response.data
      }
    } catch (error) {
      console.error('Email verification error:', error.response?.data || error.message)
      return {
        success: false,
        errorCode: UtilisateurService.ERROR_CODES.EMAIL_VERIFICATION_FAILED,
        disponible: false
      }
    }
  }

  /**
   * Obtenir le profil de l'utilisateur connecté
   * Raccourci pour F4 - consultation de son propre profil
   *
   * @returns {Promise<Object>} Profil de l'utilisateur connecté
   */
  async monProfil() {
    try {
      console.log('Loading my profile')

      const response = await api.get(`${endpoints.users}/me`)

      console.log('My profile loaded')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('My profile loading error:', error.response?.data || error.message)
      throw {
        success: false,
        errorCode: UtilisateurService.ERROR_CODES.MY_PROFILE_LOAD_FAILED,
        message: error.response?.data?.error
      }
    }
  }

  /**
   * F4 : Statistiques du profil utilisateur
   * Complément de "Consulter son profil" avec affichage des projets actifs
   *
   * @param {number} utilisateurId - ID de l'utilisateur
   * @returns {Promise<Object>} Statistiques du profil
   */
  async obtenirStatistiquesProfil(utilisateurId) {
    try {
      console.log('Loading profile statistics ID:', utilisateurId)

      // Appel vers l'endpoint backend pour les vraies statistiques
      const response = await api.get(`${endpoints.users}/${utilisateurId}/statistiques`)

      console.log('Profile statistics loaded successfully')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('Profile statistics error:', error.response?.data || error.message)

      // Fallback avec données par défaut en cas d'erreur
      console.warn('Falling back to default statistics')
      return {
        success: false,
        errorCode: UtilisateurService.ERROR_CODES.STATISTICS_LOAD_FAILED,
        data: {
          projets: 0,
          taches: 0,
          notificationsNonLues: 0
        }
      }
    }
  }

  /**
   * F5+ : Changement de mot de passe (extension logique de F5)
   * Complément de "Mettre à jour son profil" pour sécurité
   *
   * @param {number} utilisateurId - ID de l'utilisateur
   * @param {Object} payload - Données de changement de mot de passe
   * @param {string} payload.ancienMotDePasse - Ancien mot de passe
   * @param {string} payload.nouveauMotDePasse - Nouveau mot de passe
   * @returns {Promise<Object>} Confirmation du changement
   */
  async changerMotDePasse(utilisateurId, payload) {
    try {
      console.log('Changing password for user ID:', utilisateurId)

      const response = await api.put(`${endpoints.users}/${utilisateurId}/mot-de-passe`, {
        ancienMotDePasse: payload.ancienMotDePasse,
        nouveauMotDePasse: payload.nouveauMotDePasse
      })

      console.log('Password changed successfully')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('Password change error:', error.response?.data || error.message)
      throw {
        success: false,
        errorCode: UtilisateurService.ERROR_CODES.PASSWORD_CHANGE_FAILED,
        message: error.response?.data?.error
      }
    }
  }

  /**
   * Utilitaire : Obtenir le message d'erreur internationalisé
   * Permet aux composants Vue de traduire les erreurs
   *
   * @param {Object} error - Objet d'erreur du service
   * @param {Function} t - Fonction de traduction Vue i18n
   * @returns {string} Message d'erreur traduit
   */
  getLocalizedErrorMessage(error, t) {
    if (error.errorCode && t) {
      return t(error.errorCode)
    }
    return error.message || t('erreurs.general')
  }
}

// Export de l'instance unique
export default new UtilisateurService()
