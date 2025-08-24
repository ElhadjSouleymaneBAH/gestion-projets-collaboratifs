<template>
  <div class="projets-publics-view">
    <!-- Header simple -->
    <header class="bg-white shadow-sm">
      <div class="container py-3">
        <div class="d-flex justify-content-between align-items-center">
          <router-link to="/" class="d-flex align-items-center text-decoration-none text-dark">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 40px;">
            <h1 class="h5 fw-bold mb-0">CollabPro</h1>
          </router-link>

          <div class="text-muted small">
            Mode consultation publique
          </div>
        </div>
      </div>
    </header>

    <div class="container py-4">
      <!-- Titre simple -->
      <div class="text-center mb-4">
        <h2 class="h2 fw-bold text-primary mb-2">
          <i class="fas fa-globe me-2"></i>Projets Publics
        </h2>
        <p class="text-muted">
          Consultation des projets partagés par la communauté
        </p>
      </div>

      <!-- Filtres -->
      <div class="row mb-4">
        <div class="col-md-8">
          <input
            v-model="rechercheTexte"
            type="text"
            class="form-control"
            placeholder="Rechercher un projet..."
          >
        </div>
        <div class="col-md-4">
          <select v-model="filtreStatut" class="form-select">
            <option value="">Tous les statuts</option>
            <option value="ACTIF">Actifs</option>
            <option value="TERMINE">Terminés</option>
          </select>
        </div>
      </div>

      <!-- Compteur -->
      <div class="mb-4">
        <span class="badge bg-primary">{{ projetsFiltres.length }} projet(s) trouvé(s)</span>
      </div>

      <!-- Loading -->
      <div v-if="chargement" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
        <p class="mt-2 text-muted">Chargement...</p>
      </div>

      <!-- Erreur -->
      <div v-else-if="erreur" class="alert alert-danger">
        <i class="fas fa-exclamation-triangle me-2"></i>{{ erreur }}
      </div>

      <!-- Liste des projets -->
      <div v-else-if="projetsFiltres.length > 0" class="row g-3">
        <div v-for="projet in projetsFiltres" :key="projet.id" class="col-md-6 col-lg-4">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <h6 class="card-title fw-bold mb-1">{{ projet.titre }}</h6>
                <span :class="getStatutBadgeClass(projet.statut)" class="badge">
                  {{ projet.statut }}
                </span>
              </div>

              <p class="card-text text-muted small mb-3" style="min-height: 40px;">
                {{ projet.description ?
                projet.description.substring(0, 100) + (projet.description.length > 100 ? '...' : '')
                : 'Aucune description' }}
              </p>

              <div class="border-top pt-2">
                <div class="d-flex justify-content-between align-items-center">
                  <small class="text-muted">
                    <i class="fas fa-user me-1"></i>{{ projet.createurNom || 'Anonyme' }}
                  </small>
                  <small class="text-muted">
                    <i class="fas fa-calendar me-1"></i>{{ formatDate(projet.dateCreation) }}
                  </small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- État vide -->
      <div v-else class="text-center py-5">
        <i class="fas fa-folder-open fa-3x text-muted mb-3 opacity-50"></i>
        <h5 class="text-muted">Aucun projet public trouvé</h5>
        <p class="text-muted mb-4">
          {{ rechercheTexte || filtreStatut ?
          'Aucun projet ne correspond à vos critères.' :
          'Aucun projet public disponible pour le moment.' }}
        </p>
        <button v-if="rechercheTexte || filtreStatut"
                @click="reinitialiserFiltres"
                class="btn btn-outline-primary">
          Réinitialiser
        </button>
      </div>

      <!-- Message info avec lien fonctionnel -->
      <div v-if="!chargement" class="alert alert-info mt-4">
        <div class="text-center">
          <i class="fas fa-info-circle me-2"></i>
          Mode consultation : Vous pouvez voir les projets publics mais pas interagir.
          <router-link to="/connexion" class="text-decoration-none fw-bold ms-1">
            Connectez-vous
          </router-link>
          pour participer !
        </div>
      </div>

      <!-- Retour accueil -->
      <div class="text-center mt-4">
        <router-link to="/" class="btn btn-outline-secondary">
          <i class="fas fa-arrow-left me-2"></i>Retour à l'accueil
        </router-link>
      </div>
    </div>

    <!-- Footer comme image 1 -->
    <footer class="bg-dark text-white py-3 mt-5">
      <div class="container">
        <div class="d-flex flex-column flex-md-row align-items-center justify-content-between">
          <!-- Logo -->
          <div class="d-flex align-items-center mb-2 mb-md-0">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 24px;">
            <span class="fw-bold">CollabPro</span>
          </div>

          <!-- Liens centraux -->
          <div class="d-flex align-items-center gap-3 mb-2 mb-md-0">
            <router-link to="/conditions" class="text-white text-decoration-underline small">
              Conditions d'utilisation
            </router-link>
            <router-link to="/politique-confidentialite" class="text-white text-decoration-underline small">
              Politique de confidentialité
            </router-link>
            <span class="small text-white-50">© 2025 Tous droits réservés</span>
          </div>

          <!-- Langue -->
          <div class="d-flex align-items-center">
            <i class="fas fa-globe me-2"></i>
            <select class="form-select form-select-sm bg-dark text-white border-secondary" style="width: auto;">
              <option value="fr" selected>Français</option>
              <option value="en">English</option>
            </select>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { projectAPI } from '@/services/api.js'

export default {
  name: 'ProjetsPublicsView',
  setup() {
    const projets = ref([])
    const chargement = ref(false)
    const erreur = ref(null)
    const rechercheTexte = ref('')
    const filtreStatut = ref('')

    const projetsFiltres = computed(() => {
      let filtres = projets.value

      if (rechercheTexte.value) {
        const recherche = rechercheTexte.value.toLowerCase()
        filtres = filtres.filter(projet =>
          projet.titre?.toLowerCase().includes(recherche) ||
          projet.description?.toLowerCase().includes(recherche)
        )
      }

      if (filtreStatut.value) {
        filtres = filtres.filter(projet => projet.statut === filtreStatut.value)
      }

      return filtres
    })

    const chargerProjets = async () => {
      try {
        chargement.value = true
        erreur.value = null

        const response = await projectAPI.getPublicProjects()

        projets.value = (response.data || [])
          .map(projet => ({
            ...projet,
            createurNom: projet.createur?.prenom + ' ' + projet.createur?.nom || 'Utilisateur'
          }))
          .sort((a, b) => new Date(b.dateCreation) - new Date(a.dateCreation))

      } catch (error) {
        console.error('Erreur chargement projets publics:', error)
        erreur.value = 'Erreur lors du chargement des projets publics.'
      } finally {
        chargement.value = false
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleDateString('fr-FR')
    }

    const getStatutBadgeClass = (statut) => {
      const classes = {
        'ACTIF': 'bg-success',
        'TERMINE': 'bg-primary',
        'PAUSE': 'bg-warning text-dark',
        'ANNULE': 'bg-danger'
      }
      return classes[statut] || 'bg-secondary'
    }

    const reinitialiserFiltres = () => {
      rechercheTexte.value = ''
      filtreStatut.value = ''
    }

    onMounted(() => {
      chargerProjets()
    })

    return {
      projets,
      chargement,
      erreur,
      rechercheTexte,
      filtreStatut,
      projetsFiltres,
      formatDate,
      getStatutBadgeClass,
      reinitialiserFiltres
    }
  }
}
</script>

<style scoped>
.projets-publics-view {
  min-height: 100vh;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
}

.card {
  transition: transform 0.2s ease;
  border-radius: 8px;
}

.card:hover {
  transform: translateY(-2px);
}

.badge {
  font-size: 0.7rem;
  padding: 0.3rem 0.6rem;
}

footer {
  margin-top: auto;
}

.form-select.bg-dark {
  background-color: #212529 !important;
  border-color: #6c757d;
}

.form-select.bg-dark:focus {
  background-color: #212529 !important;
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}
</style>
