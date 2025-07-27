import axios from 'axios'

export default {
  //  Récupérer tous les projets de l'utilisateur connecté
  getMesProjets(token) {
    return axios.get('/api/projet/mes', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  },

  //  Récupérer un projet spécifique par ID
  getProjetById(id, token) {
    return axios.get(`/api/projet/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  },

  //  Créer un nouveau projet
  creerProjet(projetData, token) {
    return axios.post('/api/projet', projetData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  },

  //  Supprimer un projet (si autorisé)
  supprimerProjet(id, token) {
    return axios.delete(`/api/projet/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  },

  //  Modifier un projet existant
  modifierProjet(id, projetData, token) {
    return axios.put(`/api/projet/${id}`, projetData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  }
}
