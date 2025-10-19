import api from './api.js'

/**
 * Service pour la gestion des membres de projets - Frontend Vue.js
 * Implémente la fonctionnalité F8 du cahier des charges
 * Version simplifiée sans méthodes inutilisées
 *
 * @author Équipe Développement
 * @version 1.0
 */
class MembreService {

  /**
   * Codes d'erreur standardisés pour l'internationalisation
   */
  static ERROR_CODES = {
    SEARCH_USERS_FAILED: 'erreurs.rechercheUtilisateurs',
    ADD_MEMBER_FAILED: 'erreurs.ajoutMembre',
    REMOVE_MEMBER_FAILED: 'erreurs.suppressionMembre',
    GET_MEMBERS_FAILED: 'erreurs.chargementMembres',
    VALIDATION_ERROR: 'erreurs.validation',
    CONFLICT: 'erreurs.conflit'
  }

  /**
   * F8 : Ajouter un membre à un projet
   * Contrainte : Chef de projet uniquement, utilisateur doit exister sur la plateforme
   *
   * @param {number} projetId - ID du projet
   * @param {number} utilisateurId - ID de l'utilisateur à ajouter
   * @param {Object} options - Options d'ajout
   * @param {string} options.role - Rôle du membre (défaut: 'MEMBRE')
   * @returns {Promise<Object>} Résultat de l'ajout
   */
  async ajouterMembreProjet(projetId, utilisateurId, options = {}) {
    try {
      const { role = 'MEMBRE' } = options

      console.log('[F8] Adding member', utilisateurId, 'to project', projetId)

      const response = await api.post(`/projets/${projetId}/membres/${utilisateurId}`, {
        role
      })

      console.log('[F8] Member added successfully')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('[F8] Add member error:', error.response?.data || error.message)

      if (error.response?.status === 409) {
        throw {
          success: false,
          errorCode: MembreService.ERROR_CODES.CONFLICT,
          message: error.response.data?.error
        }
      }

      throw {
        success: false,
        errorCode: MembreService.ERROR_CODES.ADD_MEMBER_FAILED,
        message: error.response?.data?.error || error.message
      }
    }
  }

  /**
   * F8 : Supprimer un membre d'un projet
   * Contrainte : Chef de projet uniquement
   *
   * @param {number} projetId - ID du projet
   * @param {number} utilisateurId - ID de l'utilisateur à retirer
   * @returns {Promise<Object>} Résultat de la suppression
   */
  async supprimerMembreProjet(projetId, utilisateurId) {
    try {
      console.log('[F8] Removing member', utilisateurId, 'from project', projetId)

      await api.delete(`/projets/${projetId}/membres/${utilisateurId}`)

      console.log('[F8] Member removed successfully')
      return {
        success: true,
        message: 'Membre retiré du projet'
      }
    } catch (error) {
      console.error('[F8] Remove member error:', error.response?.data || error.message)
      throw {
        success: false,
        errorCode: MembreService.ERROR_CODES.REMOVE_MEMBER_FAILED,
        message: error.response?.data?.error
      }
    }
  }

  /**
   * F8 : Obtenir la liste des membres d'un projet
   *
   * @param {number} projetId - ID du projet
   * @returns {Promise<Object>} Liste des membres du projet
   */
  async getMembresProjets(projetId) {
    try {
      console.log('[F8] Loading project members for project:', projetId)

      const response = await api.get(`/projets/${projetId}/membres`)

      console.log('[F8] Project members loaded successfully')
      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('[F8] Get members error:', error.response?.data || error.message)
      throw {
        success: false,
        errorCode: MembreService.ERROR_CODES.GET_MEMBERS_FAILED,
        message: error.response?.data?.error
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
    if (error.errorCode && t) {
      return t(error.errorCode)
    }
    return error.message || t('erreurs.general')
  }
}

// Export de l'instance unique
export default new MembreService()
