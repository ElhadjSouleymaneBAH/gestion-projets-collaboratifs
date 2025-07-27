
class ProfilService {
  constructor() {
    this.baseURL = '/api/profil'
  }

  // F4 - Consulter son profil
  async obtenirProfil(utilisateurId) {
    try {
      // Vérification de l'utilisateur connecté
      const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || 'null')
      if (!utilisateurConnecte || utilisateurConnecte.id !== utilisateurId) {
        throw new Error('Accès non autorisé')
      }

      // Simuler appel API - remplacer par votre vraie API
      const response = await this.simulerObtenirProfil(utilisateurId)

      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      console.error('Erreur obtention profil:', error)
      throw new Error(error.message || 'Erreur lors de la récupération du profil')
    }
  }

  // F5 - Mettre à jour son profil
  async mettreAJourProfil(utilisateurId, donneesModifiees) {
    try {
      // Validation des données selon votre table utilisateurs
      this.validerDonneesProfil(donneesModifiees)

      // Vérification de l'utilisateur connecté
      const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || 'null')
      if (!utilisateurConnecte || utilisateurConnecte.id !== utilisateurId) {
        throw new Error('Accès non autorisé')
      }

      // Appel API de mise à jour
      const response = await this.simulerMettreAJourProfil(utilisateurId, donneesModifiees)

      // Mettre à jour le localStorage et le store Pinia
      const utilisateurMisAJour = {
        ...utilisateurConnecte,
        ...donneesModifiees,
        dateModification: new Date().toISOString()
      }

      localStorage.setItem('user', JSON.stringify(utilisateurMisAJour))

      return {
        success: true,
        data: utilisateurMisAJour,
        message: 'Profil mis à jour avec succès'
      }
    } catch (error) {
      console.error('Erreur mise à jour profil:', error)
      throw new Error(error.message || 'Erreur lors de la mise à jour du profil')
    }
  }

  // Changer mot de passe (sécurité)
  async changerMotDePasse(utilisateurId, donneesMotDePasse) {
    try {
      const { ancienMotDePasse, nouveauMotDePasse, confirmationMotDePasse } = donneesMotDePasse

      // Validation
      this.validerChangementMotDePasse(donneesMotDePasse)

      // Vérification utilisateur
      const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || 'null')
      if (!utilisateurConnecte || utilisateurConnecte.id !== utilisateurId) {
        throw new Error('Accès non autorisé')
      }

      // Appel API changement mot de passe
      const response = await this.simulerChangerMotDePasse(utilisateurId, donneesMotDePasse)

      return {
        success: true,
        message: 'Mot de passe modifié avec succès'
      }
    } catch (error) {
      console.error('Erreur changement mot de passe:', error)
      throw new Error(error.message || 'Erreur lors du changement de mot de passe')
    }
  }

  // Obtenir statistiques utilisateur pour affichage profil
  async obtenirStatistiquesProfil(utilisateurId) {
    try {
      // Compter projets de l'utilisateur selon son rôle
      const utilisateur = JSON.parse(localStorage.getItem('user') || 'null')
      const projets = JSON.parse(localStorage.getItem('projets') || '[]')
      const taches = JSON.parse(localStorage.getItem('taches') || '[]')
      const notifications = JSON.parse(localStorage.getItem('notifications') || '[]')

      let statistiques = {
        projets: 0,
        taches: 0,
        notificationsNonLues: 0
      }

      if (utilisateur.role === 'CHEF_PROJET') {
        // Projets créés par le chef de projet
        statistiques.projets = projets.filter(p => p.id_createur === utilisateurId).length
      } else {
        // Projets où l'utilisateur est membre
        statistiques.projets = projets.filter(p =>
          p.membres && p.membres.includes(utilisateurId)
        ).length
      }

      // Tâches assignées à l'utilisateur
      statistiques.taches = taches.filter(t => t.id_assigne === utilisateurId).length

      // Notifications non lues
      statistiques.notificationsNonLues = notifications.filter(n =>
        n.id_utilisateur === utilisateurId && !n.lu
      ).length

      return {
        success: true,
        data: statistiques
      }
    } catch (error) {
      console.error('Erreur statistiques profil:', error)
      return {
        success: false,
        data: { projets: 0, taches: 0, notificationsNonLues: 0 }
      }
    }
  }

  // Validation des données profil selon table utilisateurs
  validerDonneesProfil(donnees) {
    const { nom, prenom, email, langue } = donnees

    if (nom && (!nom.trim() || nom.trim().length < 2)) {
      throw new Error('Le nom doit contenir au moins 2 caractères')
    }

    if (prenom && (!prenom.trim() || prenom.trim().length < 2)) {
      throw new Error('Le prénom doit contenir au moins 2 caractères')
    }

    if (email && !this.validerEmail(email)) {
      throw new Error('Format d\'email invalide')
    }

    if (langue && !['fr', 'en'].includes(langue)) {
      throw new Error('Langue non supportée (fr/en uniquement)')
    }
  }

  // Validation changement mot de passe
  validerChangementMotDePasse(donnees) {
    const { ancienMotDePasse, nouveauMotDePasse, confirmationMotDePasse } = donnees

    if (!ancienMotDePasse) {
      throw new Error('Ancien mot de passe requis')
    }

    if (!nouveauMotDePasse || nouveauMotDePasse.length < 8) {
      throw new Error('Le nouveau mot de passe doit contenir au moins 8 caractères')
    }

    if (nouveauMotDePasse !== confirmationMotDePasse) {
      throw new Error('Les mots de passe ne correspondent pas')
    }

    if (ancienMotDePasse === nouveauMotDePasse) {
      throw new Error('Le nouveau mot de passe doit être différent de l\'ancien')
    }
  }

  // Validation email
  validerEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return regex.test(email)
  }

  // MÉTHODES DE SIMULATION - Remplacer par vraies API
  async simulerObtenirProfil(utilisateurId) {
    // Simuler latence API
    await new Promise(resolve => setTimeout(resolve, 500))

    // Récupérer depuis localStorage (simulation base de données)
    const utilisateurs = JSON.parse(localStorage.getItem('utilisateurs') || '[]')
    const utilisateur = utilisateurs.find(u => u.id === utilisateurId)

    if (!utilisateur) {
      throw new Error('Utilisateur non trouvé')
    }

    // Retourner les données sans le mot de passe
    const { motDePasse, ...utilisateurSansMotDePasse } = utilisateur

    return {
      success: true,
      data: utilisateurSansMotDePasse
    }
  }

  async simulerMettreAJourProfil(utilisateurId, donneesModifiees) {
    // Simuler latence API
    await new Promise(resolve => setTimeout(resolve, 800))

    // Mettre à jour dans localStorage (simulation base de données)
    const utilisateurs = JSON.parse(localStorage.getItem('utilisateurs') || '[]')
    const index = utilisateurs.findIndex(u => u.id === utilisateurId)

    if (index === -1) {
      throw new Error('Utilisateur non trouvé')
    }

    // Vérifier unicité email si modifié
    if (donneesModifiees.email) {
      const emailExiste = utilisateurs.some(u =>
        u.id !== utilisateurId && u.email === donneesModifiees.email
      )
      if (emailExiste) {
        throw new Error('Cet email est déjà utilisé')
      }
    }

    // Mettre à jour l'utilisateur
    utilisateurs[index] = {
      ...utilisateurs[index],
      ...donneesModifiees,
      dateModification: new Date().toISOString()
    }

    localStorage.setItem('utilisateurs', JSON.stringify(utilisateurs))

    const { motDePasse, ...utilisateurMisAJour } = utilisateurs[index]

    return {
      success: true,
      data: utilisateurMisAJour
    }
  }

  async simulerChangerMotDePasse(utilisateurId, donneesMotDePasse) {
    // Simuler latence API
    await new Promise(resolve => setTimeout(resolve, 1000))

    const { ancienMotDePasse, nouveauMotDePasse } = donneesMotDePasse

    // Récupérer utilisateur
    const utilisateurs = JSON.parse(localStorage.getItem('utilisateurs') || '[]')
    const index = utilisateurs.findIndex(u => u.id === utilisateurId)

    if (index === -1) {
      throw new Error('Utilisateur non trouvé')
    }

    const utilisateur = utilisateurs[index]

    // Vérifier ancien mot de passe (simulation - en réalité hash bcrypt)
    if (this.simulerVerificationMotDePasse(ancienMotDePasse, utilisateur.motDePasse)) {
      throw new Error('Ancien mot de passe incorrect')
    }

    // Mettre à jour le mot de passe (simulation hash)
    utilisateurs[index].motDePasse = this.simulerHashMotDePasse(nouveauMotDePasse)
    utilisateurs[index].dateModification = new Date().toISOString()

    localStorage.setItem('utilisateurs', JSON.stringify(utilisateurs))

    return {
      success: true
    }
  }

  // Simulation hash mot de passe (remplacer par bcrypt)
  simulerHashMotDePasse(motDePasse) {
    return btoa(motDePasse + 'salt_secret') // Simulation basique
  }

  // Simulation vérification mot de passe
  simulerVerificationMotDePasse(motDePasse, hash) {
    return this.simulerHashMotDePasse(motDePasse) !== hash
  }
}

// Instance singleton
const profilService = new ProfilService()

export default profilService
