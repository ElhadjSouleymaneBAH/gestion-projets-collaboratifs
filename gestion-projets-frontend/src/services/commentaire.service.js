// src/services/commentaire.service.js
import api from './api.js'

/**
 * Service de gestion des commentaires - Conforme au backend
 * Fonctionnalité F12: Commentaires sur TÂCHES uniquement
 *
 * @author Équipe Développement
 * @version 2.0 - Conforme backend
 */
class CommentaireService {

  /**
   * Obtenir tous les commentaires d'une tâche
   * GET /api/commentaires/tache/{tacheId}
   *
   * @param {number} tacheId - ID de la tâche
   * @returns {Promise<Object>} { success: boolean, data: CommentaireDTO[] }
   */
  async getCommentairesTache(tacheId) {
    try {
      console.log(`[F12] GET /api/commentaires/tache/${tacheId}`)

      if (!tacheId || tacheId < 1) {
        throw new Error('ID de tâche invalide')
      }

      const response = await api.get(`/api/commentaires/tache/${tacheId}`)

      return {
        success: true,
        data: Array.isArray(response.data) ? response.data : []
      }
    } catch (error) {
      console.error('[F12] Erreur chargement commentaires:', error)
      return {
        success: false,
        data: [],
        error: error.response?.data?.message || error.message
      }
    }
  }

  /**
   * Créer un nouveau commentaire sur une tâche
   * POST /api/commentaires
   *
   * @param {Object} commentaireDTO - { contenu: string, tacheId: number }
   * @returns {Promise<Object>} { success: boolean, data: CommentaireDTO }
   */
  async creerCommentaire(commentaireDTO) {
    try {
      console.log('[F12] POST /api/commentaires', commentaireDTO)

      // Validation
      if (!commentaireDTO.contenu || !commentaireDTO.contenu.trim()) {
        throw new Error('Le contenu du commentaire est obligatoire')
      }

      if (!commentaireDTO.tacheId || commentaireDTO.tacheId < 1) {
        throw new Error('ID de tâche invalide')
      }

      const payload = {
        contenu: commentaireDTO.contenu.trim(),
        tacheId: parseInt(commentaireDTO.tacheId, 10)
      }

      const response = await api.post('/api/commentaires', payload)

      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('[F12] Erreur création commentaire:', error)
      return {
        success: false,
        error: error.response?.data?.message || error.message
      }
    }
  }

  /**
   * Supprimer un commentaire
   * DELETE /api/commentaires/{id}
   * Permissions: CHEF_PROJET ou ADMINISTRATEUR
   *
   * @param {number} commentaireId - ID du commentaire
   * @returns {Promise<Object>} { success: boolean }
   */
  async supprimerCommentaire(commentaireId) {
    try {
      console.log(`[F12] DELETE /api/commentaires/${commentaireId}`)

      if (!commentaireId || commentaireId < 1) {
        throw new Error('ID de commentaire invalide')
      }

      await api.delete(`/api/commentaires/${commentaireId}`)

      return {
        success: true
      }
    } catch (error) {
      console.error('[F12] Erreur suppression commentaire:', error)
      return {
        success: false,
        error: error.response?.data?.message || error.message
      }
    }
  }

  /**
   * Obtenir tous les commentaires (Admin uniquement)
   * GET /api/commentaires
   *
   * @returns {Promise<Object>} { success: boolean, data: CommentaireDTO[] }
   */
  async obtenirTous() {
    try {
      console.log('[F12] GET /api/commentaires (Admin)')

      const response = await api.get('/api/commentaires')

      return {
        success: true,
        data: Array.isArray(response.data) ? response.data : []
      }
    } catch (error) {
      console.error('[F12] Erreur récupération tous commentaires:', error)
      return {
        success: false,
        data: [],
        error: error.response?.data?.message || error.message
      }
    }
  }
}

// Export instance unique
export default new CommentaireService()
