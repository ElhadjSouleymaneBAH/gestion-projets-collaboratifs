import api from './api'
import { endpoints } from '@/config/endpoints'

/**
 * Service Abonnement
 * F10 - Paiements et abonnements (membre, chef de projet, admin)
 * ✅ Correction : ajout du token JWT dans chaque requête sécurisée
 */
const abonnementService = {
  getAll(token) {
    return api.get(endpoints.abonnements, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    })
  },

  souscrire(data = {}, token) {
    return api.post(`${endpoints.abonnements}/souscrire`, data, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    })
  },

  getByUserId(userId, token) {
    return api.get(`${endpoints.abonnements}/utilisateur/${userId}`, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    })
  },

  verifierStatut(utilisateurId, token) {
    return api.get(`${endpoints.abonnements}/verification-statut/${utilisateurId}`, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    })
  },

  renouveler(id, token) {
    return api.put(`${endpoints.abonnements}/${id}/renouveler`, {}, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    })
  },

  resilier(id, token) {
    return api.put(`${endpoints.abonnements}/${id}/resilier`, {}, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    })
  },

  supprimer(id, token) {
    return api.delete(`${endpoints.abonnements}/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    })
  },
}

export default abonnementService
