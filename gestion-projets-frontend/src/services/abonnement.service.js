import api from './api'
import { endpoints } from '@/config/endpoints'

/**
 * Service Abonnement
 * F10 - Paiements et abonnements (membre, chef de projet, admin)
 */
const abonnementService = {
  getAll() {
    return api.get(endpoints.abonnements)
  },

  souscrire(data = {}) {
    return api.post(`${endpoints.abonnements}/souscrire`, data)
  },

  getByUserId(userId) {
    return api.get(`${endpoints.abonnements}/utilisateur/${userId}`)
  },

  verifierStatut(utilisateurId) {
    return api.get(`${endpoints.abonnements}/verification-statut/${utilisateurId}`)
  },

  renouveler(id) {
    return api.put(`${endpoints.abonnements}/${id}/renouveler`)
  },

  resilier(id) {
    return api.put(`${endpoints.abonnements}/${id}/resilier`)
  },

  supprimer(id) {
    return api.delete(`${endpoints.abonnements}/${id}`)
  },
}

export default abonnementService
