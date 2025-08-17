class MembreService {
  constructor() {
    this.baseURL = 'http://localhost:8080/api'

    //  AUTO-DÉTECTION MODE
    this.modeLocal = this.detecterModeLocal()

    console.log(`MembreService initialisé en mode: ${this.modeLocal ? 'LOCAL (démo)' : 'API (production)'}`)
  }

  //  DÉTECTION AUTOMATIQUE DU MODE
  detecterModeLocal() {
    // Méthode 1: Variable d'environnement
    if (process.env.NODE_ENV === 'production') {
      return false // Production = API
    }

    // Méthode 2: Hostname
    if (window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
      return false // Domaine réel = API
    }

    // Méthode 3: Port de développement
    if (window.location.port !== '5173' && window.location.port !== '3000') {
      return false // Port production = API
    }

    // Méthode 4: Présence de flag debug
    if (localStorage.getItem('FORCE_API_MODE') === 'true') {
      return false // Force API mode
    }

    return true // Par défaut: mode local pour démo
  }

  //  RECHERCHER UTILISATEURS (F8)
  async rechercherUtilisateurs(query) {
    if (this.modeLocal) {
      return this.rechercherUtilisateursLocal(query)
    } else {
      return this.rechercherUtilisateursAPI(query)
    }
  }

  //  VERSION LOCALE - Pour démo
  rechercherUtilisateursLocal(query) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const tousLesUtilisateurs = this.obtenirTousLesUtilisateurs()
        const terme = query.toLowerCase()

        const resultats = tousLesUtilisateurs.filter(user => {
          const nomComplet = `${user.prenom} ${user.nom}`.toLowerCase()
          const emailMatch = user.email.toLowerCase().includes(terme)
          const nomMatch = nomComplet.includes(terme)

          // F8 : STRICTEMENT MEMBRE et CHEF_PROJET selon cahier des charges
          const roleValide = user.role === 'MEMBRE' || user.role === 'CHEF_PROJET'

          return (emailMatch || nomMatch) && roleValide
        })

        resolve(resultats.slice(0, 10))
      }, 300)
    })
  }

  //  VERSION API - Pour production
  async rechercherUtilisateursAPI(query) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.get(`${this.baseURL}/utilisateurs/recherche`, {
        params: { q: query, page: 0, size: 10 },
        headers: this.getAuthHeaders()
      })

      // F8 : Filtrer STRICTEMENT selon cahier des charges
      return response.data.filter(user =>
        user.role === 'MEMBRE' || user.role === 'CHEF_PROJET'
      )
    } catch (error) {
      console.error('Erreur API recherche utilisateurs:', error)
      this.gererErreur(error)
      throw error
    }
  }

  //  OBTENIR TOUS LES UTILISATEURS (système hybride)
  obtenirTousLesUtilisateurs() {
    // Utilisateurs figés selon VOS DONNÉES SQL EXACTES
    const utilisateursFiges = [
      { id: 1, email: 'emilie.durand0@icc.be', prenom: 'Émilie', nom: 'Durand', role: 'CHEF_PROJET' },
      { id: 7, email: 'sarah.fournier6@icc.be', prenom: 'Sarah', nom: 'Fournier', role: 'MEMBRE' },
      { id: 8, email: 'hugo.giraud7@icc.be', prenom: 'Hugo', nom: 'Giraud', role: 'MEMBRE' },
      { id: 24, email: 'nathan.garcia23@icc.be', prenom: 'Nathan', nom: 'Garcia', role: 'VISITEUR' },
      { id: 51, email: 'emilie.durand50@icc.be', prenom: 'Émilie', nom: 'Durand', role: 'ADMINISTRATEUR' }
    ]

    // Nouveaux utilisateurs dynamiques
    const nouveauxUtilisateurs = JSON.parse(localStorage.getItem('nouveauxUtilisateurs')) || []

    return [...utilisateursFiges, ...nouveauxUtilisateurs]
  }

  //  RÉCUPÉRER MEMBRES D'UN PROJET
  async getMembresProjets(projetId) {
    if (this.modeLocal) {
      return this.getMembresProjetsLocal(projetId)
    } else {
      return this.getMembresProjetsAPI(projetId)
    }
  }

  getMembresProjetsLocal(projetId) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const membresProjet = JSON.parse(localStorage.getItem(`membres_projet_${projetId}`)) || []
        resolve(membresProjet)
      }, 200)
    })
  }

  async getMembresProjetsAPI(projetId) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.get(`${this.baseURL}/projets/${projetId}/membres`, {
        headers: this.getAuthHeaders()
      })
      return response.data
    } catch (error) {
      this.gererErreur(error)
      throw error
    }
  }

  // AJOUTER MEMBRE AU PROJET (F8)
  async ajouterMembreProjet(projetId, utilisateurId) {
    if (this.modeLocal) {
      return this.ajouterMembreProjetLocal(projetId, utilisateurId)
    } else {
      return this.ajouterMembreProjetAPI(projetId, utilisateurId)
    }
  }

  ajouterMembreProjetLocal(projetId, utilisateurId) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        try {
          const membresProjet = JSON.parse(localStorage.getItem(`membres_projet_${projetId}`)) || []

          // Vérifier si déjà membre
          const dejaMembre = membresProjet.some(m => m.userId === utilisateurId)
          if (dejaMembre) {
            reject(new Error('Utilisateur déjà membre de ce projet'))
            return
          }

          // Trouver l'utilisateur
          const utilisateur = this.obtenirTousLesUtilisateurs().find(u => u.id === utilisateurId)
          if (!utilisateur) {
            reject(new Error('Utilisateur non trouvé'))
            return
          }

          // Ajouter le membre
          const nouveauMembre = {
            userId: utilisateur.id,
            nom: utilisateur.nom,
            prenom: utilisateur.prenom,
            email: utilisateur.email,
            role: utilisateur.role,
            dateAjout: new Date().toISOString()
          }

          membresProjet.push(nouveauMembre)
          localStorage.setItem(`membres_projet_${projetId}`, JSON.stringify(membresProjet))

          console.log(`Membre ${utilisateurId} ajouté au projet ${projetId}`)
          resolve({ success: true, membre: nouveauMembre })
        } catch (error) {
          reject(error)
        }
      }, 500)
    })
  }

  async ajouterMembreProjetAPI(projetId, utilisateurId) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.post(`${this.baseURL}/projets/${projetId}/membres`, {
        utilisateurId: utilisateurId
      }, {
        headers: this.getAuthHeaders()
      })

      console.log(`Membre ${utilisateurId} ajouté au projet ${projetId}`)
      return response.data
    } catch (error) {
      this.gererErreur(error)
      throw error
    }
  }

  // INVITER PAR EMAIL (F8 - InvitationMembres)
  async inviterParEmail(projetId, email, message = '') {
    if (this.modeLocal) {
      return this.inviterParEmailLocal(projetId, email, message)
    } else {
      return this.inviterParEmailAPI(projetId, email, message)
    }
  }

  inviterParEmailLocal(projetId, email, message) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        try {
          // Vérifier si l'utilisateur existe
          const utilisateur = this.obtenirTousLesUtilisateurs().find(u => u.email === email)
          if (!utilisateur) {
            reject(new Error('Utilisateur non trouvé'))
            return
          }

          // Ajouter automatiquement au projet (simulation acceptation)
          this.ajouterMembreProjetLocal(projetId, utilisateur.id)
            .then(() => {
              // Enregistrer l'invitation
              const invitations = JSON.parse(localStorage.getItem('invitations')) || []
              const nouvelleInvitation = {
                id: Date.now(),
                projetId,
                email,
                message,
                statut: 'ACCEPTEE',
                dateEnvoi: new Date().toISOString(),
                utilisateurId: utilisateur.id
              }

              invitations.push(nouvelleInvitation)
              localStorage.setItem('invitations', JSON.stringify(invitations))

              resolve({ success: true, utilisateur, invitation: nouvelleInvitation })
            })
            .catch(reject)
        } catch (error) {
          reject(error)
        }
      }, 400)
    })
  }

  async inviterParEmailAPI(projetId, email, message) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.post(`${this.baseURL}/projets/${projetId}/invitations`, {
        email: email,
        message: message
      }, {
        headers: this.getAuthHeaders()
      })

      console.log(`Invitation envoyée à ${email} pour le projet ${projetId}`)
      return response.data
    } catch (error) {
      this.gererErreur(error)
      throw error
    }
  }

  // SUPPRIMER MEMBRE PROJET
  async supprimerMembreProjet(projetId, utilisateurId) {
    if (this.modeLocal) {
      return this.supprimerMembreProjetLocal(projetId, utilisateurId)
    } else {
      return this.supprimerMembreProjetAPI(projetId, utilisateurId)
    }
  }

  supprimerMembreProjetLocal(projetId, utilisateurId) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const membresProjet = JSON.parse(localStorage.getItem(`membres_projet_${projetId}`)) || []
        const nouveauxMembres = membresProjet.filter(m => m.userId !== utilisateurId)
        localStorage.setItem(`membres_projet_${projetId}`, JSON.stringify(nouveauxMembres))

        console.log(`Membre ${utilisateurId} retiré du projet ${projetId}`)
        resolve({ success: true })
      }, 300)
    })
  }

  async supprimerMembreProjetAPI(projetId, utilisateurId) {
    try {
      const axios = (await import('axios')).default
      await axios.delete(`${this.baseURL}/projets/${projetId}/membres/${utilisateurId}`, {
        headers: this.getAuthHeaders()
      })

      console.log(`Membre ${utilisateurId} retiré du projet ${projetId}`)
    } catch (error) {
      this.gererErreur(error)
      throw error
    }
  }

  // VÉRIFIER SI MEMBRE D'UN PROJET
  async estMembreProjet(projetId, utilisateurId) {
    if (this.modeLocal) {
      return this.estMembreProjetLocal(projetId, utilisateurId)
    } else {
      return this.estMembreProjetAPI(projetId, utilisateurId)
    }
  }

  estMembreProjetLocal(projetId, utilisateurId) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const membresProjet = JSON.parse(localStorage.getItem(`membres_projet_${projetId}`)) || []
        const estMembre = membresProjet.some(m => m.userId === utilisateurId)
        resolve(estMembre)
      }, 100)
    })
  }

  async estMembreProjetAPI(projetId, utilisateurId) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.get(`${this.baseURL}/projets/${projetId}/membres/${utilisateurId}`, {
        headers: this.getAuthHeaders()
      })
      return response.status === 200
    } catch (error) {
      if (error.response && error.response.status === 404) {
        return false
      }
      throw error
    }
  }

  // OBTENIR PROJETS D'UN MEMBRE
  async getProjetsMembre(utilisateurId) {
    if (this.modeLocal) {
      return this.getProjetsMembreLocal(utilisateurId)
    } else {
      return this.getProjetsMembreAPI(utilisateurId)
    }
  }

  getProjetsMembreLocal(utilisateurId) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const projets = []

        // Projets figés avec DONNÉES SQL EXACTES
        if (utilisateurId === 7) {
          projets.push(
            { id: 1, titre: 'Application Mobile E-commerce', description: 'Développement mobile', statut: 'ACTIF' },
            { id: 3, titre: 'Système de Gestion RH', description: 'Logiciel RH', statut: 'ACTIF' }
          )
        }

        resolve(projets)
      }, 200)
    })
  }

  async getProjetsMembreAPI(utilisateurId) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.get(`${this.baseURL}/utilisateurs/${utilisateurId}/projets`, {
        headers: this.getAuthHeaders()
      })
      return response.data
    } catch (error) {
      this.gererErreur(error)
      throw error
    }
  }

  // UTILITAIRES
  getAuthHeaders() {
    const token = localStorage.getItem('token')
    return token ? {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    } : {
      'Content-Type': 'application/json'
    }
  }

  gererErreur(error) {
    if (error.response) {
      const status = error.response.status
      const message = error.response.data?.message || 'Erreur serveur'

      switch (status) {
        case 401:
          console.error('Non autorisé - Token invalide')
          this.redirectionConnexion()
          break
        case 403:
          console.error('Accès refusé - Droits insuffisants')
          break
        case 404:
          console.error('Ressource non trouvée')
          break
        case 409:
          console.error('Conflit - Membre déjà présent')
          break
        default:
          console.error(`Erreur ${status}: ${message}`)
      }
    } else {
      console.error('Erreur:', error.message)
    }
  }

  redirectionConnexion() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')

    if (window.location.pathname !== '/connexion') {
      window.location.href = '/connexion'
    }
  }

  // MÉTHODES DE CONTRÔLE MANUEL
  forceApiMode() {
    localStorage.setItem('FORCE_API_MODE', 'true')
    this.modeLocal = false
    console.log('Mode API forcé')
  }

  forceLocalMode() {
    localStorage.removeItem('FORCE_API_MODE')
    this.modeLocal = true
    console.log('Mode local forcé')
  }

  getCurrentMode() {
    return this.modeLocal ? 'LOCAL' : 'API'
  }

  // VALIDATION DES DONNÉES
  validerEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return regex.test(email)
  }

  validerUtilisateurId(id) {
    return id && (typeof id === 'number' || typeof id === 'string') && Number(id) > 0
  }

  validerProjetId(id) {
    return id && (typeof id === 'number' || typeof id === 'string') && Number(id) > 0
  }
}

export default new MembreService()
