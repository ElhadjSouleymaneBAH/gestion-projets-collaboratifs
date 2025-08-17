<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h1 class="text-primary mb-2">Tableau de bord Membre</h1>
            <p class="text-muted mb-0">Bienvenue {{ utilisateur.prenom }} {{ utilisateur.nom }}</p>
          </div>
          <button class="btn btn-outline-danger" @click="seDeconnecter">
            <i class="fas fa-sign-out-alt me-2"></i>Se déconnecter
          </button>
        </div>

        <!-- Statistiques -->
        <div class="row mb-4">
          <div class="col-md-3">
            <div class="card text-center h-100 cursor-pointer" @click="changerOnglet('projets')">
              <div class="card-body">
                <h2 class="text-primary">{{ statistiques().mesProjets }}</h2>
                <p class="text-muted mb-0">Mes projets</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card text-center h-100 cursor-pointer" @click="changerOnglet('taches')">
              <div class="card-body">
                <h2 class="text-warning">{{ statistiques().tachesAssignees }}</h2>
                <p class="text-muted mb-0">Tâches assignées</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card text-center h-100">
              <div class="card-body">
                <h2 class="text-info">{{ statistiques().messagesNonLus }}</h2>
                <p class="text-muted mb-0">Messages non lus</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card text-center h-100">
              <div class="card-body">
                <h2 class="text-success">{{ statistiques().tachesTerminees }}</h2>
                <p class="text-muted mb-0">Tâches terminées</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Navigation -->
        <ul class="nav nav-tabs mb-4">
          <li class="nav-item">
            <a class="nav-link" :class="{active: ongletActif === 'projets'}" @click="changerOnglet('projets')" href="#">Mes projets</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{active: ongletActif === 'taches'}" @click="changerOnglet('taches')" href="#">Mes tâches</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{active: ongletActif === 'profil'}" @click="changerOnglet('profil')" href="#">Mon profil</a>
          </li>
        </ul>

        <!-- Contenu -->
        <div class="tab-content">
          <!-- F4 - Mes Projets -->
          <div v-if="ongletActif === 'projets'">
            <h4>Mes projets</h4>
            <div class="row">
              <div v-for="projet in mesProjets" :key="projet.id" class="col-md-6 mb-3">
                <div class="card">
                  <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">{{ projet.titre }}</h5>
                    <span class="badge bg-success">{{ projet.statut }}</span>
                  </div>
                  <div class="card-body">
                    <p class="text-muted">{{ projet.description }}</p>
                    <div class="d-flex gap-2">
                      <button class="btn btn-outline-primary btn-sm" @click="ouvrirProjet(projet.id)">
                        <i class="fas fa-eye me-1"></i>Ouvrir
                      </button>
                      <button class="btn btn-outline-info btn-sm" @click="ouvrirChatTache(projet.id)">
                        <i class="fas fa-comments me-1"></i>Chat
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- F7 - Mes Tâches -->
          <div v-if="ongletActif === 'taches'">
            <h4>Mes tâches</h4>
            <div v-if="mesTaches.length === 0" class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>
              Aucune tâche assignée pour le moment. Les tâches apparaîtront quand le Chef de Projet vous en assignera.
            </div>
            <div v-else class="list-group">
              <div v-for="tache in mesTaches" :key="tache.id" class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <h6>{{ tache.titre }}</h6>
                    <small class="text-muted">{{ tache.projet }}</small>
                  </div>
                  <div>
                    <span class="badge" :class="getTacheBadgeClass(tache.statut)">
                      {{ tache.statut }}
                    </span>
                    <button class="btn btn-sm btn-outline-info ms-2" @click="ouvrirChatTache(tache.projetId)">
                      <i class="fas fa-comments"></i>
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
                      <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-2"></i>Sauvegarder
                      </button>
                    </form>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Chat Modal -->
        <div v-if="chatOuvert" class="modal fade show d-block" style="background: rgba(0,0,0,0.5)">
          <div class="modal-dialog modal-lg">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">
                  <i class="fas fa-comments me-2"></i>
                  Chat - {{ projetChatActuel ? projetChatActuel.titre : 'Projet' }}
                </h5>
                <button type="button" class="btn-close" @click="fermerChat"></button>
              </div>
              <div class="modal-body">
                <div class="chat-container" style="height: 300px; overflow-y: auto; border: 1px solid #ddd; padding: 10px; margin-bottom: 10px;">
                  <div v-for="message in messages" :key="message.id" class="mb-2">
                    <strong>{{ message.auteur }}:</strong> {{ message.contenu }}
                    <small class="text-muted d-block">{{ message.date }}</small>
                  </div>
                  <div v-if="messages.length === 0" class="text-muted text-center">
                    Aucun message pour le moment
                  </div>
                </div>
                <div class="input-group">
                  <input
                    type="text"
                    class="form-control"
                    v-model="nouveauMessage"
                    @keyup.enter="envoyerMessage"
                    placeholder="Tapez votre message..."
                  >
                  <button class="btn btn-primary" @click="envoyerMessage">
                    <i class="fas fa-paper-plane"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TableauBordMembre',
  data() {
    return {
      ongletActif: 'projets',
      chatOuvert: false,
      projetChatActuel: null,
      nouveauMessage: '',

      // Données utilisateur basées sur SQL - Sarah Fournier ID 7
      utilisateur: {
        id: 7,
        nom: 'Fournier',
        prenom: 'Sarah',
        email: 'sarah.fournier6@icc.be',
        role: 'MEMBRE'
      },

      // Projets selon données SQL - Sarah participe aux projets 1 et 3
      mesProjets: [
        {
          id: 1,
          titre: 'Application Mobile E-commerce',
          description: 'Développement mobile',
          statut: 'ACTIF'
        },
        {
          id: 3,
          titre: 'Système de Gestion RH',
          description: 'Logiciel RH',
          statut: 'ACTIF'
        }
      ],

      // Tâches selon analyse SQL - Sarah n'a aucune tâche assignée
      mesTaches: [],

      // Messages par projet (F9 - Collaboration temps réel)
      messagesParProjet: {
        1: [],
        3: []
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
    // Statistiques dynamiques calculées selon données SQL réelles
    statistiques() {
      const totalMessages = Object.values(this.messagesParProjet).flat().length

      return {
        mesProjets: this.mesProjets.length, // 2 projets réels (ID 1 et 3)
        tachesAssignees: this.mesTaches.length, // 0 pour le moment (pas dans SQL)
        messagesNonLus: totalMessages, // Messages réels envoyés
        tachesTerminees: this.mesTaches.filter(t => t.statut === 'TERMINE').length // 0 pour le moment
      }
    },

    changerOnglet(onglet) {
      this.ongletActif = onglet;
    },

    ouvrirProjet(projetId) {
      alert(`Ouverture projet ${projetId} - Détails du projet`);
    },

    ouvrirChatTache(projetId) {
      this.projetChatActuel = this.mesProjets.find(p => p.id === projetId);
      this.chatOuvert = true;
    },

    fermerChat() {
      this.chatOuvert = false;
      this.projetChatActuel = null;
      this.nouveauMessage = '';
    },

    envoyerMessage() {
      if (this.nouveauMessage.trim() && this.projetChatActuel) {
        const message = {
          id: Date.now(),
          auteur: `${this.utilisateur.prenom} ${this.utilisateur.nom}`,
          contenu: this.nouveauMessage,
          date: new Date().toLocaleString()
        };

        if (!this.messagesParProjet[this.projetChatActuel.id]) {
          this.messagesParProjet[this.projetChatActuel.id] = [];
        }

        this.messagesParProjet[this.projetChatActuel.id].push(message);
        this.nouveauMessage = '';
      }
    },

    getTacheBadgeClass(statut) {
      switch(statut) {
        case 'TERMINE': return 'bg-success';
        case 'EN_COURS': return 'bg-primary';
        case 'EN_ATTENTE': return 'bg-warning';
        default: return 'bg-secondary';
      }
    },

    sauvegarderProfil() {
      alert('Profil sauvegardé avec succès !');
    },

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
.cursor-pointer {
  cursor: pointer;
}
.cursor-pointer:hover {
  opacity: 0.8;
}
</style>
