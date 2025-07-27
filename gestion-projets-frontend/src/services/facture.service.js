class FactureService {
  constructor() {
    this.baseURL = 'http://localhost:8080/api'

    // AUTO-DÉTECTION MODE (comme membre.service.js)
    this.modeLocal = this.detecterModeLocal()

    console.log(`FactureService initialisé en mode: ${this.modeLocal ? 'LOCAL (démo)' : 'API (production)'}`)
  }

  // DÉTECTION AUTOMATIQUE DU MODE
  detecterModeLocal() {
    if (process.env.NODE_ENV === 'production') return false
    if (window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') return false
    if (window.location.port !== '5173' && window.location.port !== '3000') return false
    if (localStorage.getItem('FORCE_API_MODE') === 'true') return false
    return true
  }

  // F11 - OBTENIR FACTURES D'UN UTILISATEUR
  async obtenirFactures(utilisateurId) {
    if (this.modeLocal) {
      return this.obtenirFacturesLocal(utilisateurId)
    } else {
      return this.obtenirFacturesAPI(utilisateurId)
    }
  }

  // VERSION LOCALE - Simulation basée sur les données SQL
  obtenirFacturesLocal(utilisateurId) {
    return new Promise((resolve) => {
      setTimeout(() => {
        // Données basées sur votre SQL
        const facturesSimulees = [
          {
            id: 1,
            numeroFacture: 'FACT-2024-001',
            montantHT: 100.00,
            tva: 21.00,
            montantTTC: 121.00,
            dateEmission: '2024-12-15',
            dateEcheance: '2025-01-14',
            statut: 'GENEREE',
            idTransaction: 1,
            // Données enrichies pour l'affichage
            periode: 'Décembre 2024',
            description: 'Abonnement Premium Mensuel',
            tauxTva: 21,
            numeroTransaction: 'TXN-001-2024',
            dernierChiffres: '4242',
            methodePaiement: 'CARTE_BANCAIRE',
            client: {
              nom: 'Émilie Durand',
              email: 'emilie.durand0@icc.be',
              adresse: 'Adresse client'
            },
            entreprise: {
              nom: 'CollabPro',
              logo: '/logo-collabpro.png',
              adresse: '123 Rue de la Collaboration',
              ville: '1000 Bruxelles, Belgique',
              email: 'contact@collabpro.be',
              tva: 'BE0123456789'
            }
          },
          {
            id: 2,
            numeroFacture: 'FACT-2024-002',
            montantHT: 10.00,
            tva: 2.10,
            montantTTC: 12.10,
            dateEmission: '2024-12-14',
            dateEcheance: '2025-01-13',
            statut: 'GENEREE',
            idTransaction: 2,
            periode: 'Décembre 2024',
            description: 'Abonnement Premium Mensuel',
            tauxTva: 21,
            numeroTransaction: 'TXN-002-2024',
            dernierChiffres: '1234',
            methodePaiement: 'CARTE_BANCAIRE',
            client: {
              nom: 'Lucas Lefevre',
              email: 'lucas.lefevre1@icc.be',
              adresse: 'Adresse client'
            },
            entreprise: {
              nom: 'CollabPro',
              logo: '/logo-collabpro.png',
              adresse: '123 Rue de la Collaboration',
              ville: '1000 Bruxelles, Belgique',
              email: 'contact@collabpro.be',
              tva: 'BE0123456789'
            }
          }
        ]

        // Filtrer par utilisateur (simulation)
        const facturesUtilisateur = facturesSimulees.filter(f => {
          // En réalité, vous feriez le lien via les transactions
          return true // Pour la démo, on retourne toutes les factures
        })

        resolve({
          success: true,
          data: facturesUtilisateur,
          total: facturesUtilisateur.length
        })
      }, 400)
    })
  }

  // VERSION API - Production
  async obtenirFacturesAPI(utilisateurId) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.get(`${this.baseURL}/utilisateurs/${utilisateurId}/factures`, {
        headers: this.getAuthHeaders()
      })

      return {
        success: true,
        data: response.data,
        total: response.data.length
      }
    } catch (error) {
      console.error('Erreur API obtenir factures:', error)
      this.gererErreur(error)
      throw error
    }
  }

  // OBTENIR UNE FACTURE SPÉCIFIQUE
  async obtenirFacture(factureId) {
    if (this.modeLocal) {
      return this.obtenirFactureLocal(factureId)
    } else {
      return this.obtenirFactureAPI(factureId)
    }
  }

  obtenirFactureLocal(factureId) {
    return new Promise((resolve, reject) => {
      setTimeout(async () => {
        try {
          const facturesResponse = await this.obtenirFacturesLocal()
          const facture = facturesResponse.data.find(f => f.id === parseInt(factureId))

          if (!facture) {
            reject(new Error('Facture non trouvée'))
            return
          }

          resolve({
            success: true,
            data: facture
          })
        } catch (error) {
          reject(error)
        }
      }, 300)
    })
  }

  async obtenirFactureAPI(factureId) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.get(`${this.baseURL}/factures/${factureId}`, {
        headers: this.getAuthHeaders()
      })

      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('Erreur API obtenir facture:', error)
      this.gererErreur(error)
      throw error
    }
  }

  // TÉLÉCHARGER PDF
  async telechargerPDF(factureId) {
    if (this.modeLocal) {
      return this.telechargerPDFLocal(factureId)
    } else {
      return this.telechargerPDFAPI(factureId)
    }
  }

  telechargerPDFLocal(factureId) {
    return new Promise((resolve) => {
      setTimeout(() => {
        // Simulation téléchargement
        console.log(`Téléchargement PDF facture ${factureId} (mode local)`)

        // Créer un lien de téléchargement simulé
        const link = document.createElement('a')
        link.href = '#'
        link.download = `facture-${factureId}.pdf`
        link.click()

        resolve({
          success: true,
          message: 'Téléchargement simulé en mode local'
        })
      }, 1000)
    })
  }

  async telechargerPDFAPI(factureId) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.get(`${this.baseURL}/factures/${factureId}/pdf`, {
        headers: this.getAuthHeaders(),
        responseType: 'blob'
      })

      // Créer le lien de téléchargement
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.download = `facture-${factureId}.pdf`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)

      return {
        success: true,
        message: 'Facture téléchargée avec succès'
      }
    } catch (error) {
      console.error('Erreur téléchargement PDF:', error)
      this.gererErreur(error)
      throw error
    }
  }

  // ENVOYER PAR EMAIL
  async envoyerParEmail(factureId) {
    if (this.modeLocal) {
      return this.envoyerParEmailLocal(factureId)
    } else {
      return this.envoyerParEmailAPI(factureId)
    }
  }

  envoyerParEmailLocal(factureId) {
    return new Promise((resolve) => {
      setTimeout(async () => {
        try {
          // Obtenir les infos de la facture
          const factureResponse = await this.obtenirFactureLocal(factureId)
          const facture = factureResponse.data

          console.log(`Email envoyé à ${facture.client.email} (mode local)`)

          resolve({
            success: true,
            message: `Facture envoyée par email à ${facture.client.email}`,
            email: facture.client.email
          })
        } catch (error) {
          resolve({
            success: false,
            message: 'Erreur lors de l\'envoi'
          })
        }
      }, 800)
    })
  }

  async envoyerParEmailAPI(factureId) {
    try {
      const axios = (await import('axios')).default
      const response = await axios.post(`${this.baseURL}/factures/${factureId}/envoyer-email`, {}, {
        headers: this.getAuthHeaders()
      })

      return {
        success: true,
        message: response.data.message || 'Facture envoyée par email',
        email: response.data.email
      }
    } catch (error) {
      console.error('Erreur envoi email:', error)
      this.gererErreur(error)
      throw error
    }
  }

  // OBTENIR FACTURES PAR STATUT
  async obtenirFacturesParStatut(utilisateurId, statut) {
    try {
      const response = await this.obtenirFactures(utilisateurId)

      if (response.success) {
        const facturesFiltrees = response.data.filter(f => f.statut === statut)
        return {
          success: true,
          data: facturesFiltrees,
          total: facturesFiltrees.length
        }
      }

      return response
    } catch (error) {
      console.error('Erreur filtre par statut:', error)
      throw error
    }
  }

  // STATISTIQUES FACTURES
  async obtenirStatistiquesFactures(utilisateurId) {
    try {
      const response = await this.obtenirFactures(utilisateurId)

      if (response.success) {
        const factures = response.data
        const stats = {
          total: factures.length,
          generees: factures.filter(f => f.statut === 'GENEREE').length,
          echecs: factures.filter(f => f.statut === 'ECHEC').length,
          montantTotal: factures.reduce((sum, f) => sum + f.montantTTC, 0),
          derniereMise: factures.length > 0 ? factures[0].dateEmission : null
        }

        return {
          success: true,
          data: stats
        }
      }

      return response
    } catch (error) {
      console.error('Erreur statistiques factures:', error)
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
          console.error('Facture non trouvée')
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

  // VALIDATION
  validerFactureId(id) {
    return id && (typeof id === 'number' || typeof id === 'string') && Number(id) > 0
  }

  validerUtilisateurId(id) {
    return id && (typeof id === 'number' || typeof id === 'string') && Number(id) > 0
  }
}

export default new FactureService()
