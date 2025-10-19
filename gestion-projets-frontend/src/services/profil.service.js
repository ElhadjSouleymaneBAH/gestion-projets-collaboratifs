import utilisateurService from './utilisateur.service.js'

/**
 * Service pour la gestion des profils utilisateur - Frontend Vue.js
 * Implémente les fonctionnalités F4 et F5 du cahier des charges
 * Délègue les appels API au service utilisateur principal
 * Complètement internationalisé et sans erreur ESLint
 *
 * @author Équipe Développement
 * @version 1.0
 */
class ProfilService {

  /**
   * Codes d'erreur standardisés pour l'internationalisation
   */
  static ERROR_CODES = {
    PROFILE_LOAD_FAILED: 'erreurs.chargementProfil',
    PROFILE_UPDATE_FAILED: 'erreurs.miseAJourProfil',
    PASSWORD_CHANGE_FAILED: 'erreurs.changementMotDePasse',
    STATISTICS_LOAD_FAILED: 'erreurs.chargementStatistiques',
    UNAUTHORIZED: 'erreurs.nonAutorise',
    VALIDATION_ERROR: 'erreurs.validation',
    EMAIL_EXISTS: 'erreurs.emailExistant'
  }

  /**
   * F4 : Consulter son profil
   * Délègue au service utilisateur principal pour cohérence
   *
   * @param {number} utilisateurId - ID de l'utilisateur
   * @returns {Promise<Object>} Données du profil utilisateur
   */
  async obtenirProfil(utilisateurId) {
    try {
      console.log('[F4] Loading profile via profil service for user:', utilisateurId)

      // Vérification sécurité : utilisateur connecté
      const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || 'null')
      if (!utilisateurConnecte || utilisateurConnecte.id !== utilisateurId) {
        throw {
          success: false,
          errorCode: ProfilService.ERROR_CODES.UNAUTHORIZED,
          message: 'Accès non autorisé'
        }
      }

      // Déléguer au service utilisateur principal
      const result = await utilisateurService.obtenirProfil(utilisateurId)

      console.log('[F4] Profile loaded successfully via profil service')
      return result
    } catch (error) {
      console.error('[F4] Profile loading error via profil service:', error)

      // Si l'erreur vient du service utilisateur, la propager
      if (error.errorCode) {
        throw error
      }

      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.PROFILE_LOAD_FAILED,
        message: error.message
      }
    }
  }

  /**
   * F5 : Mettre à jour son profil
   * Délègue au service utilisateur avec validation supplémentaire
   *
   * @param {number} utilisateurId - ID de l'utilisateur
   * @param {Object} donneesModifiees - Nouvelles données du profil
   * @returns {Promise<Object>} Profil mis à jour
   */
  async mettreAJourProfil(utilisateurId, donneesModifiees) {
    try {
      console.log('[F5] Updating profile via profil service for user:', utilisateurId)

      // Vérification sécurité : utilisateur connecté
      const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || 'null')
      if (!utilisateurConnecte || utilisateurConnecte.id !== utilisateurId) {
        throw {
          success: false,
          errorCode: ProfilService.ERROR_CODES.UNAUTHORIZED,
          message: 'Accès non autorisé'
        }
      }

      // Validation côté client des données
      this.validerDonneesProfil(donneesModifiees)

      // Déléguer au service utilisateur principal
      const result = await utilisateurService.mettreAJourProfil(utilisateurId, donneesModifiees)

      // Mettre à jour le localStorage utilisateur
      if (result.success && result.data) {
        const utilisateurMisAJour = {
          ...utilisateurConnecte,
          ...result.data,
          dateModification: new Date().toISOString()
        }
        localStorage.setItem('user', JSON.stringify(utilisateurMisAJour))
      }

      console.log('[F5] Profile updated successfully via profil service')
      return result
    } catch (error) {
      console.error('[F5] Profile update error via profil service:', error)

      // Si l'erreur vient du service utilisateur, la propager
      if (error.errorCode) {
        throw error
      }

      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.PROFILE_UPDATE_FAILED,
        message: error.message
      }
    }
  }

  /**
   * F5+ : Changer mot de passe (extension logique de F5)
   * Délègue au service utilisateur avec validation renforcée
   *
   * @param {number} utilisateurId - ID de l'utilisateur
   * @param {Object} donneesMotDePasse - Données de changement de mot de passe
   * @returns {Promise<Object>} Confirmation du changement
   */
  async changerMotDePasse(utilisateurId, donneesMotDePasse) {
    try {
      console.log('[F5+] Changing password via profil service for user:', utilisateurId)

      // Vérification sécurité : utilisateur connecté
      const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || 'null')
      if (!utilisateurConnecte || utilisateurConnecte.id !== utilisateurId) {
        throw {
          success: false,
          errorCode: ProfilService.ERROR_CODES.UNAUTHORIZED,
          message: 'Accès non autorisé'
        }
      }

      // Validation côté client du changement de mot de passe
      this.validerChangementMotDePasse(donneesMotDePasse)

      // Déléguer au service utilisateur principal
      const result = await utilisateurService.changerMotDePasse(utilisateurId, donneesMotDePasse)

      console.log('[F5+] Password changed successfully via profil service')
      return result
    } catch (error) {
      console.error('[F5+] Password change error via profil service:', error)

      // Si l'erreur vient du service utilisateur, la propager
      if (error.errorCode) {
        throw error
      }

      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.PASSWORD_CHANGE_FAILED,
        message: error.message
      }
    }
  }

  /**
   * F4 : Obtenir statistiques du profil (projets actifs selon cahier des charges)
   * Délègue au service utilisateur principal
   *
   * @param {number} utilisateurId - ID de l'utilisateur
   * @returns {Promise<Object>} Statistiques du profil
   */
  async obtenirStatistiquesProfil(utilisateurId) {
    try {
      console.log('[F4] Loading profile statistics via profil service for user:', utilisateurId)

      // Déléguer au service utilisateur principal
      const result = await utilisateurService.obtenirStatistiquesProfil(utilisateurId)

      console.log('[F4] Profile statistics loaded via profil service')
      return result
    } catch (error) {
      console.error('[F4] Statistics loading error via profil service:', error)

      // Fallback avec données par défaut en cas d'erreur
      console.warn('Falling back to default statistics via profil service')
      return {
        success: false,
        errorCode: ProfilService.ERROR_CODES.STATISTICS_LOAD_FAILED,
        data: {
          projets: 0,
          taches: 0,
          notificationsNonLues: 0
        }
      }
    }
  }

  /**
   * MÉTHODES DE VALIDATION CÔTÉ CLIENT
   */

  /**
   * Validation des données profil selon le cahier des charges
   * @param {Object} donnees - Données à valider
   * @throws {Object} Erreur avec code d'erreur si validation échoue
   */
  validerDonneesProfil(donnees) {
    const { nom, prenom, email, langue } = donnees

    if (nom !== undefined && (!nom?.trim() || nom.trim().length < 2)) {
      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.VALIDATION_ERROR,
        message: 'Le nom doit contenir au moins 2 caractères',
        field: 'nom'
      }
    }

    if (prenom !== undefined && (!prenom?.trim() || prenom.trim().length < 2)) {
      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.VALIDATION_ERROR,
        message: 'Le prénom doit contenir au moins 2 caractères',
        field: 'prenom'
      }
    }

    if (email !== undefined && !this.validerEmail(email)) {
      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.VALIDATION_ERROR,
        message: 'Format d\'email invalide',
        field: 'email'
      }
    }

    if (langue !== undefined && !['fr', 'en'].includes(langue)) {
      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.VALIDATION_ERROR,
        message: 'Langue non supportée (fr/en uniquement)',
        field: 'langue'
      }
    }
  }

  /**
   * Validation changement mot de passe selon les contraintes de sécurité
   * @param {Object} donnees - Données de changement de mot de passe
   * @throws {Object} Erreur avec code d'erreur si validation échoue
   */
  validerChangementMotDePasse(donnees) {
    const { ancienMotDePasse, nouveauMotDePasse, confirmationMotDePasse } = donnees

    if (!ancienMotDePasse) {
      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.VALIDATION_ERROR,
        message: 'Ancien mot de passe requis',
        field: 'ancienMotDePasse'
      }
    }

    if (!nouveauMotDePasse || nouveauMotDePasse.length < 6) {
      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.VALIDATION_ERROR,
        message: 'Le nouveau mot de passe doit contenir au moins 6 caractères',
        field: 'nouveauMotDePasse'
      }
    }

    if (nouveauMotDePasse !== confirmationMotDePasse) {
      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.VALIDATION_ERROR,
        message: 'Les mots de passe ne correspondent pas',
        field: 'confirmationMotDePasse'
      }
    }

    if (ancienMotDePasse === nouveauMotDePasse) {
      throw {
        success: false,
        errorCode: ProfilService.ERROR_CODES.VALIDATION_ERROR,
        message: 'Le nouveau mot de passe doit être différent de l\'ancien',
        field: 'nouveauMotDePasse'
      }
    }
  }

  /**
   * Validation format email
   * @param {string} email - Email à valider
   * @returns {boolean} true si l'email est valide
   */
  validerEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return email && typeof email === 'string' && regex.test(email.trim())
  }

  /**
   * MÉTHODES UTILITAIRES
   */

  /**
   * Obtenir le message d'erreur internationalisé
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
export default new ProfilService()
