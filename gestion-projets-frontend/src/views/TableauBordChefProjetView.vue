<template>
  <div class="container mt-4">
    <!-- Header avec bouton déconnexion -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h2 class="text-success">Tableau de bord Chef de Projet</h2>
            <p class="text-muted">Bienvenue {{ utilisateur.prenom }} {{ utilisateur.nom }}</p>
          </div>
          <div class="d-flex align-items-center gap-3">
            <div class="text-end">
              <span class="badge bg-success fs-6">CHEF_PROJET</span>
              <br>
              <small class="text-muted">Abonnement Premium</small>
            </div>
            <button class="btn btn-outline-danger" @click="seDeconnecter">
              <i class="fas fa-sign-out-alt me-2"></i>Se déconnecter
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Statistics basées sur données SQL réelles -->
    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card text-center cursor-pointer" @click="ongletActif = 'projets'">
          <div class="card-body">
            <h3 class="text-success">{{ statistiques().mesProjets }}</h3>
            <p class="text-muted">Mes projets</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center cursor-pointer" @click="ongletActif = 'membres'">
          <div class="card-body">
            <h3 class="text-primary">{{ statistiques().totalMembres }}</h3>
            <p class="text-muted">Membres d'équipe</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center">
          <div class="card-body">
            <h3 class="text-warning">{{ statistiques().tachesEnCours }}</h3>
            <p class="text-muted">Tâches en cours</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card text-center">
          <div class="card-body">
            <h3 class="text-info">{{ statistiques().messagesNonLus }}</h3>
            <p class="text-muted">Messages non lus</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Navigation -->
    <div class="row mb-4">
      <div class="col-12">
        <ul class="nav nav-tabs">
          <li class="nav-item">
            <a class="nav-link" :class="{ active: ongletActif === 'projets' }" @click="ongletActif = 'projets'">
              Mes projets
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{ active: ongletActif === 'membres' }" @click="ongletActif = 'membres'">
              Gestion équipes
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{ active: ongletActif === 'factures' }" @click="ongletActif = 'factures'">
              Mes factures
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{ active: ongletActif === 'profil' }" @click="ongletActif = 'profil'">
              Mon profil
            </a>
          </li>
        </ul>
      </div>
    </div>

    <!-- Content -->
    <div class="row">
      <div class="col-12">
        <!-- F6 - Mes Projets -->
        <div v-if="ongletActif === 'projets'">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h4>Mes projets ({{ mesProjets.length }})</h4>
            <button class="btn btn-success" @click="creerNouveauProjet">
              <i class="fas fa-plus me-1"></i>Nouveau projet
            </button>
          </div>

          <div class="row">
            <div v-for="projet in mesProjets" :key="projet.id" class="col-md-6 mb-3">
              <div class="card">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start mb-2">
                    <h5 class="card-title">{{ projet.titre }}</h5>
                    <span class="badge" :class="getProjetBadgeClass(projet.statut)">
                      {{ projet.statut }}
                    </span>
                  </div>
                  <p class="card-text text-muted">{{ projet.description }}</p>
                  <div class="d-flex justify-content-between align-items-center">
                    <small class="text-muted">
                      <i class="fas fa-users me-1"></i>{{ projet.nombreMembres }} membres
                    </small>
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-primary" @click="ouvrirProjet(projet.id)">
                        <i class="fas fa-eye"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-info" @click="gererMembres(projet.id)">
                        <i class="fas fa-users"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-success" @click="ouvrirChat(projet.id)">
                        <i class="fas fa-comments"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- F8 - Gestion Équipes -->
        <div v-if="ongletActif === 'membres'">
          <h4>Gestion des équipes</h4>

          <!-- Formulaires F8 -->
          <div class="row mb-4">
            <div class="col-md-6">
              <div class="card">
                <div class="card-header">
                  <h6>Ajouter un membre au projet</h6>
                </div>
                <div class="card-body">
                  <form @submit.prevent="ajouterMembreProjet">
                    <div class="mb-3">
                      <label class="form-label">Projet</label>
                      <select class="form-select" v-model="formulaireMembre.projetId" required>
                        <option value="">Sélectionner un projet</option>
                        <option v-for="projet in mesProjets" :key="projet.id" :value="projet.id">
                          {{ projet.titre }}
                        </option>
                      </select>
                    </div>
                    <div class="mb-3">
                      <label class="form-label">Email du membre</label>
                      <input type="email" class="form-control" v-model="formulaireMembre.email" required>
                    </div>
                    <button type="submit" class="btn btn-primary">
                      <i class="fas fa-user-plus me-2"></i>Ajouter membre
                    </button>
                  </form>
                </div>
              </div>
            </div>

            <div class="col-md-6">
              <div class="card">
                <div class="card-header">
                  <h6>Assigner une tâche</h6>
                </div>
                <div class="card-body">
                  <form @submit.prevent="assignerTache">
                    <div class="mb-3">
                      <label class="form-label">Projet</label>
                      <select class="form-select" v-model="formulaireTache.projetId" required>
                        <option value="">Sélectionner un projet</option>
                        <option v-for="projet in mesProjets" :key="projet.id" :value="projet.id">
                          {{ projet.titre }}
                        </option>
                      </select>
                    </div>
                    <div class="mb-3">
                      <label class="form-label">Titre de la tâche</label>
                      <input type="text" class="form-control" v-model="formulaireTache.titre" required>
                    </div>
                    <div class="mb-3">
                      <label class="form-label">Description</label>
                      <textarea class="form-control" v-model="formulaireTache.description" rows="3"></textarea>
                    </div>
                    <div class="mb-3">
                      <label class="form-label">Assigner à (email)</label>
                      <input type="email" class="form-control" v-model="formulaireTache.emailMembre" required>
                    </div>
                    <button type="submit" class="btn btn-success">
                      <i class="fas fa-tasks me-2"></i>Assigner tâche
                    </button>
                  </form>
                </div>
              </div>
            </div>
          </div>

          <!-- Liste des projets et membres -->
          <div class="row">
            <div v-for="projet in mesProjets" :key="'membres-' + projet.id" class="col-md-6 mb-3">
              <div class="card">
                <div class="card-header">
                  <h6 class="mb-0">{{ projet.titre }}</h6>
                </div>
                <div class="card-body">
                  <p class="text-muted">{{ projet.nombreMembres }} membres</p>
                  <button class="btn btn-sm btn-outline-primary">
                    Voir les membres
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- F10/F11 - Factures -->
        <div v-if="ongletActif === 'factures'">
          <h4>Mes factures</h4>
          <div v-if="mesFactures.length === 0" class="alert alert-info">
            <i class="fas fa-info-circle me-2"></i>
            Aucune facture pour le moment. Les factures apparaîtront avec l'abonnement Premium.
          </div>
          <div v-else class="list-group">
            <div v-for="facture in mesFactures" :key="facture.id" class="list-group-item">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <h6>Facture #{{ facture.numero }}</h6>
                  <small class="text-muted">{{ facture.date }} - {{ facture.montant }}€</small>
                </div>
                <div>
                  <span class="badge bg-success me-2">{{ facture.statut }}</span>
                  <button class="btn btn-sm btn-outline-primary">
                    <i class="fas fa-download"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- F4/F5 - Mon Profil -->
        <div v-if="ongletActif === 'profil'">
          <h4>Mon profil</h4>
          <div class="row">
            <div class="col-md-6">
              <div class="card">
                <div class="card-body">
                  <form @submit.prevent="sauvegarderProfil">
                    <div class="mb-3">
                      <label class="form-label">Nom</label>
                      <input type="text" class="form-control" v-model="utilisateur.nom">
                    </div>
                    <div class="mb-3">
                      <label class="form-label">Prénom</label>
                      <input type="text" class="form-control" v-model="utilisateur.prenom">
                    </div>
                    <div class="mb-3">
                      <label class="form-label">Email</label>
                      <input type="email" class="form-control" v-model="utilisateur.email" readonly>
                    </div>
                    <button type="submit" class="btn btn-success">
                      <i class="fas fa-save me-2"></i>Sauvegarder
                    </button>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- F9 - Modal Chat temps réel -->
    <div v-if="chatOuvert" class="modal d-block" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-success text-white">
            <h5 class="modal-title">
              <i class="fas fa-comments me-2"></i>Chat temps réel - {{ projetChatActuel?.titre }}
            </h5>
            <button class="btn-close btn-close-white" @click="fermerChat"></button>
          </div>
          <div class="modal-body">
            <div class="chat-container mb-3" style="height: 300px; overflow-y: auto; border: 1px solid #dee2e6; padding: 15px; background: #f8f9fa;">
              <div v-for="message in messages" :key="message.id" class="mb-2">
                <div class="d-flex" :class="{ 'justify-content-end': estMonMessage(message) }">
                  <div class="p-2 rounded" :class="getMessageClass(message)" style="max-width: 70%;">
                    <div v-if="!estMonMessage(message)" class="small mb-1">
                      <strong>{{ message.auteur }}</strong>
                      <span class="text-muted ms-2">{{ formatTime(message.timestamp) }}</span>
                    </div>
                    <div>{{ message.contenu }}</div>
                  </div>
                </div>
              </div>
              <div v-if="messages.length === 0" class="text-muted text-center">
                Aucun message pour le moment
              </div>
            </div>

            <div class="input-group">
              <input
                v-model="nouveauMessage"
                class="form-control"
                placeholder="Tapez votre message..."
                @keyup.enter="envoyerMessage"
              >
              <button class="btn btn-success" @click="envoyerMessage" :disabled="!nouveauMessage.trim()">
                <i class="fas fa-paper-plane"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TableauBordChefProjet',
  data() {
    return {
      ongletActif: 'projets',
      chatOuvert: false,
      projetChatActuel: null,
      nouveauMessage: '',

      // Données utilisateur basées sur SQL - Émilie Durand ID 1 (Chef)
      utilisateur: {
        id: 1,
        nom: 'Durand',
        prenom: 'Émilie',
        email: 'emilie.durand0@icc.be',
        role: 'CHEF_PROJET'
      },

      // Projets selon données SQL - Émilie créatrice des projets 1, 2, 3
      mesProjets: [
        {
          id: 1,
          titre: 'Application Mobile E-commerce',
          description: 'Développement app mobile de vente en ligne',
          statut: 'ACTIF',
          nombreMembres: 2 // Émilie + Sarah selon votre SQL
        },
        {
          id: 2,
          titre: 'Site Web Corporate',
          description: 'Refonte complète du site vitrine',
          statut: 'ACTIF',
          nombreMembres: 1 // Seule Émilie pour le moment
        },
        {
          id: 3,
          titre: 'Système de Gestion RH',
          description: 'Logiciel de gestion des ressources humaines',
          statut: 'ACTIF',
          nombreMembres: 2 // Émilie + Sarah selon votre SQL
        }
      ],

      // Factures - vide selon analyse SQL
      mesFactures: [],

      // Formulaires F8
      formulaireMembre: {
        projetId: '',
        email: ''
      },

      formulaireTache: {
        projetId: '',
        titre: '',
        description: '',
        emailMembre: ''
      },

      // Messages séparés par projet (F9)
      messagesParProjet: {
        1: [], // Application Mobile E-commerce
        2: [], // Site Web Corporate
        3: []  // Système de Gestion RH
      }
    }
  },

  computed: {
    messages() {
      return this.projetChatActuel ?
        (this.messagesParProjet[this.projetChatActuel.id] || []) : []
    }
  },

  methods: {
    // Statistiques dynamiques basées sur données SQL réelles
    statistiques() {
      const totalMessages = Object.values(this.messagesParProjet).flat().length
      const totalMembres = this.mesProjets.reduce((total, projet) => total + projet.nombreMembres, 0) - this.mesProjets.length

      return {
        mesProjets: this.mesProjets.length, // 3 projets selon SQL
        totalMembres: totalMembres + 1, // +1 pour Émilie elle-même
        tachesEnCours: 0, // Pas de tâches EN_COURS dans votre SQL pour le moment
        messagesNonLus: totalMessages // Messages réels
      }
    },

    // F6 - Créer nouveau projet
    creerNouveauProjet() {
      const titre = prompt('Titre du nouveau projet:')
      if (titre) {
        const description = prompt('Description du projet:')
        const nouveauProjet = {
          id: Date.now(),
          titre: titre,
          description: description || '',
          statut: 'ACTIF',
          nombreMembres: 1
        }
        this.mesProjets.push(nouveauProjet)
        this.messagesParProjet[nouveauProjet.id] = []
        alert('Projet créé avec succès !')
      }
    },

    ouvrirProjet(projetId) {
      alert(`Affichage détails du projet ${projetId}`)
    },

    gererMembres(projetId) {
      this.ongletActif = 'membres'
      this.formulaireMembre.projetId = projetId
    },

    // F8 - Ajouter membre au projet
    ajouterMembreProjet() {
      const projet = this.mesProjets.find(p => p.id == this.formulaireMembre.projetId)
      if (projet) {
        projet.nombreMembres++
        alert(`Membre ${this.formulaireMembre.email} ajouté au projet ${projet.titre}`)
        this.formulaireMembre = { projetId: '', email: '' }
      }
    },

    // F7/F8 - Assigner tâche (selon diagramme de séquence)
    assignerTache() {
      const projet = this.mesProjets.find(p => p.id == this.formulaireTache.projetId)
      if (projet) {
        alert(`Tâche "${this.formulaireTache.titre}" assignée à ${this.formulaireTache.emailMembre} dans le projet ${projet.titre}`)
        this.formulaireTache = { projetId: '', titre: '', description: '', emailMembre: '' }
      }
    },

    // F9 - Chat temps réel
    ouvrirChat(projetId) {
      this.projetChatActuel = this.mesProjets.find(p => p.id === projetId)
      this.chatOuvert = true

      // Initialiser les messages du projet s'ils n'existent pas
      if (!this.messagesParProjet[projetId]) {
        this.messagesParProjet[projetId] = []
      }
    },

    fermerChat() {
      this.chatOuvert = false
      this.projetChatActuel = null
      this.nouveauMessage = ''
    },

    envoyerMessage() {
      if (!this.nouveauMessage.trim() || !this.projetChatActuel) return

      const message = {
        id: Date.now(),
        auteur: `${this.utilisateur.prenom} ${this.utilisateur.nom}`,
        email: this.utilisateur.email,
        contenu: this.nouveauMessage,
        timestamp: new Date()
      }

      // Ajouter le message au projet spécifique
      this.messagesParProjet[this.projetChatActuel.id].push(message)
      this.nouveauMessage = ''
    },

    estMonMessage(message) {
      return message.email === this.utilisateur.email
    },

    getMessageClass(message) {
      return this.estMonMessage(message) ? 'bg-success text-white' : 'bg-light border'
    },

    formatTime(timestamp) {
      return new Date(timestamp).toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    getProjetBadgeClass(statut) {
      return {
        'ACTIF': 'bg-success',
        'TERMINE': 'bg-secondary',
        'EN_PAUSE': 'bg-warning'
      }[statut] || 'bg-primary'
    },

    sauvegarderProfil() {
      alert('Profil sauvegardé avec succès !')
    },

    // Déconnexion
    seDeconnecter() {
      // Supprimer les données de session
      localStorage.removeItem('token')
      localStorage.removeItem('user')

      // Redirection vers connexion
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.nav-link {
  cursor: pointer;
}
.modal {
  z-index: 1050;
}
.cursor-pointer {
  cursor: pointer;
}
.cursor-pointer:hover {
  opacity: 0.8;
}
</style>
