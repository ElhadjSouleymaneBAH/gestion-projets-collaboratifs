<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h1 class="text-danger mb-2">Tableau de bord Administrateur</h1>
            <p class="text-muted mb-0">Bienvenue {{ user.prenom }} {{ user.nom }}</p>
          </div>
          <button class="btn btn-outline-danger" @click="seDeconnecter">
            <i class="fas fa-sign-out-alt me-2"></i>Se déconnecter
          </button>
        </div>

        <!-- Statistiques -->
        <div class="row mb-4">
          <div class="col-md-3">
            <div class="card text-center h-100 cursor-pointer" @click="changerOnglet('utilisateurs')">
              <div class="card-body">
                <h2 class="text-danger">{{ stats.totalUsers }}</h2>
                <p class="text-muted mb-0">Utilisateurs</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card text-center h-100 cursor-pointer" @click="changerOnglet('projets')">
              <div class="card-body">
                <h2 class="text-primary">{{ stats.totalProjects }}</h2>
                <p class="text-muted mb-0">Projets</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card text-center h-100">
              <div class="card-body">
                <h2 class="text-warning">{{ stats.totalTasks }}</h2>
                <p class="text-muted mb-0">Tâches</p>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="card text-center h-100">
              <div class="card-body">
                <h2 class="text-success">{{ stats.totalMembers }}</h2>
                <p class="text-muted mb-0">Membres actifs</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Navigation -->
        <ul class="nav nav-tabs mb-4">
          <li class="nav-item">
            <a class="nav-link" :class="{active: ongletActif === 'utilisateurs'}" @click="changerOnglet('utilisateurs')" href="#">Utilisateurs</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{active: ongletActif === 'projets'}" @click="changerOnglet('projets')" href="#">Projets</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{active: ongletActif === 'systeme'}" @click="changerOnglet('systeme')" href="#">Système</a>
          </li>
        </ul>

        <!-- Contenu -->
        <div class="tab-content">
          <!-- Gestion des Utilisateurs -->
          <div v-if="ongletActif === 'utilisateurs'">
            <h4>Gestion des utilisateurs</h4>
            <div class="table-responsive">
              <table class="table table-striped">
                <thead>
                <tr>
                  <th>ID</th>
                  <th>Nom</th>
                  <th>Prénom</th>
                  <th>Email</th>
                  <th>Rôle</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="utilisateur in utilisateurs" :key="utilisateur.id">
                  <td>{{ utilisateur.id }}</td>
                  <td>{{ utilisateur.nom }}</td>
                  <td>{{ utilisateur.prenom }}</td>
                  <td>{{ utilisateur.email }}</td>
                  <td>
                    <span class="badge" :class="getRoleBadgeClass(utilisateur.role)">
                      {{ utilisateur.role }}
                    </span>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-outline-primary me-1" @click="modifierUtilisateur(utilisateur.id)">
                      <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" @click="supprimerUtilisateur(utilisateur.id)">
                      <i class="fas fa-trash"></i>
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Gestion des Projets -->
          <div v-if="ongletActif === 'projets'">
            <h4>Gestion des projets</h4>
            <div class="table-responsive">
              <table class="table table-striped">
                <thead>
                <tr>
                  <th>ID</th>
                  <th>Titre</th>
                  <th>Description</th>
                  <th>Statut</th>
                  <th>Propriétaire</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="projet in projets" :key="projet.id">
                  <td>{{ projet.id }}</td>
                  <td>{{ projet.titre }}</td>
                  <td>{{ projet.description }}</td>
                  <td>
                    <span class="badge" :class="getStatutBadgeClass(projet.statut)">
                      {{ projet.statut }}
                    </span>
                  </td>
                  <td>{{ projet.proprietaire.prenom }} {{ projet.proprietaire.nom }}</td>
                  <td>
                    <button class="btn btn-sm btn-outline-info me-1" @click="voirProjet(projet.id)">
                      <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-primary me-1" @click="modifierProjet(projet.id)">
                      <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" @click="supprimerProjet(projet.id)">
                      <i class="fas fa-trash"></i>
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Informations Système -->
          <div v-if="ongletActif === 'systeme'">
            <h4>Informations système</h4>
            <div class="row">
              <div class="col-md-6">
                <div class="card">
                  <div class="card-header">
                    <h6><i class="fas fa-database me-2"></i>Base de données</h6>
                  </div>
                  <div class="card-body">
                    <p><strong>Nom :</strong> gestionprojets</p>
                    <p><strong>Type :</strong> MySQL</p>
                    <p><strong>Enregistrements :</strong> {{ stats.totalUsers + stats.totalProjects }} (utilisateurs + projets)</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card">
                  <div class="card-header">
                    <h6><i class="fas fa-chart-bar me-2"></i>Statistiques générales</h6>
                  </div>
                  <div class="card-body">
                    <p><strong>Utilisateurs :</strong> {{ stats.totalUsers }}</p>
                    <p><strong>Projets :</strong> {{ stats.totalProjects }}</p>
                    <p><strong>Membres actifs :</strong> {{ stats.totalMembers }}</p>
                    <p><strong>Dernière connexion :</strong> {{ new Date().toLocaleString() }}</p>
                  </div>
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
  name: 'TableauDeBordAdminView',
  data() {
    return {
      ongletActif: 'utilisateurs',

      // Données utilisateur Admin
      user: {
        id: 51,
        nom: 'Durand',
        prenom: 'Émilie',
        email: 'emilie.durand50@icc.be',
        role: 'ADMINISTRATEUR'
      },

      // Utilisateurs basés dur la base de données SQL
      utilisateurs: [
        {
          id: 1,
          nom: 'Durand',
          prenom: 'Émilie',
          email: 'emilie.durand0@icc.be',
          role: 'CHEF_PROJET'
        },
        {
          id: 7,
          nom: 'Fournier',
          prenom: 'Sarah',
          email: 'sarah.fournier6@icc.be',
          role: 'MEMBRE'
        },
        {
          id: 8,
          nom: 'Giraud',
          prenom: 'Hugo',
          email: 'hugo.giraud7@icc.be',
          role: 'MEMBRE'
        },
        {
          id: 24,
          nom: 'Garcia',
          prenom: 'Nathan',
          email: 'nathan.garcia23@icc.be',
          role: 'VISITEUR'
        },
        {
          id: 51,
          nom: 'Durand',
          prenom: 'Émilie',
          email: 'emilie.durand50@icc.be',
          role: 'ADMINISTRATEUR'
        }
      ],

      // Projets basés sur les données SQL
      projets: [
        {
          id: 1,
          titre: 'Application Mobile E-commerce',
          description: 'Développement mobile',
          statut: 'ACTIF',
          proprietaire: { prenom: 'Émilie', nom: 'Durand' }
        },
        {
          id: 2,
          titre: 'Site Web Corporate',
          description: 'Site vitrine',
          statut: 'ACTIF',
          proprietaire: { prenom: 'Émilie', nom: 'Durand' }
        },
        {
          id: 3,
          titre: 'Système de Gestion RH',
          description: 'Logiciel RH',
          statut: 'ACTIF',
          proprietaire: { prenom: 'Émilie', nom: 'Durand' }
        }
      ]
    }
  },

  computed: {
    // Statistiques dynamiques basées sur les données SQL
    stats() {
      return {
        totalUsers: this.utilisateurs.length, // 5 utilisateurs selon SQL
        totalProjects: this.projets.length, // 3 projets selon SQL
        totalTasks: 0, // Pas de tâches dans votre SQL pour le moment
        totalMembers: this.utilisateurs.filter(u => u.role === 'MEMBRE').length // 2 membres selon SQL
      }
    }
  },

  methods: {
    changerOnglet(onglet) {
      this.ongletActif = onglet;
    },

    getRoleBadgeClass(role) {
      switch(role) {
        case 'ADMINISTRATEUR': return 'bg-danger';
        case 'CHEF_PROJET': return 'bg-success';
        case 'MEMBRE': return 'bg-primary';
        case 'VISITEUR': return 'bg-warning';
        default: return 'bg-secondary';
      }
    },

    getStatutBadgeClass(statut) {
      switch(statut) {
        case 'ACTIF': return 'bg-success';
        case 'TERMINE': return 'bg-secondary';
        case 'SUSPENDU': return 'bg-warning';
        default: return 'bg-secondary';
      }
    },

    // Actions utilisateurs
    modifierUtilisateur(id) {
      alert(`Modification utilisateur ID: ${id}`);
    },

    supprimerUtilisateur(id) {
      if (confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
        this.utilisateurs = this.utilisateurs.filter(u => u.id !== id);
        alert('Utilisateur supprimé');
      }
    },

    // Actions projets
    voirProjet(id) {
      alert(`Affichage détails projet ID: ${id}`);
    },

    modifierProjet(id) {
      alert(`Modification projet ID: ${id}`);
    },

    supprimerProjet(id) {
      if (confirm('Êtes-vous sûr de vouloir supprimer ce projet ?')) {
        this.projets = this.projets.filter(p => p.id !== id);
        alert('Projet supprimé');
      }
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
