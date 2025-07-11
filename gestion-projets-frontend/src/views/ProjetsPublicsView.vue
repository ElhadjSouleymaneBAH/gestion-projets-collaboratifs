<template>
  <div class="min-vh-100 bg-light">

    <!-- Header -->
    <header class="bg-white shadow-sm border-bottom">
      <div class="container-fluid">
        <div class="d-flex justify-content-between align-items-center py-3">
          <div class="d-flex align-items-center">
            <button @click="goBack" class="btn btn-link text-muted me-3 p-0">
              <i class="fas fa-arrow-left"></i>
            </button>
            <h1 class="h4 fw-bold text-dark mb-0">Projets Publics</h1>
            <span class="badge bg-secondary ms-2">Lecture seule</span>
          </div>
          <div class="d-flex align-items-center">
            <select v-model="currentLanguage" @change="changeLanguage" class="form-select form-select-sm">
              <option value="fr">FR</option>
              <option value="en">EN</option>
            </select>
          </div>
        </div>
      </div>
    </header>

    <!-- Contenu principal - F3: Consulter les projets publics -->
    <main class="container-fluid py-4">
      <!-- Information pour les visiteurs -->
      <div class="alert alert-info d-flex align-items-center mb-4">
        <i class="fas fa-info-circle me-2"></i>
        <div>
          <strong>Mode consultation :</strong> Vous pouvez voir les projets publics mais pas interagir.
          <router-link to="/login" class="alert-link">Connectez-vous</router-link> pour participer !
        </div>
      </div>

      <!-- Filtres et recherche -->
      <div class="card mb-4">
        <div class="card-body">
          <div class="row g-3 align-items-center">
            <div class="col-md-6">
              <input v-model="searchQuery" type="text" placeholder="Rechercher un projet..."
                     class="form-control">
            </div>
            <div class="col-md-4">
              <select v-model="selectedStatus" class="form-select">
                <option value="">Tous les statuts</option>
                <option value="ACTIF">Actif</option>
                <option value="TERMINE">Terminé</option>
                <option value="EN_ATTENTE">En attente</option>
              </select>
            </div>
            <div class="col-md-2">
              <span class="text-muted">{{ filteredProjects.length }} projet(s)</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Chargement...</span>
        </div>
        <p class="mt-2 text-muted">Chargement des projets...</p>
      </div>

      <!-- Aucun projet -->
      <div v-else-if="filteredProjects.length === 0" class="text-center py-5">
        <i class="fas fa-folder-open fa-3x text-muted mb-3"></i>
        <h5 class="text-muted">Aucun projet trouvé</h5>
        <p class="text-muted">Aucun projet public ne correspond à vos critères.</p>
      </div>

      <!-- Liste des projets publics -->
      <div v-else class="row g-4">
        <div v-for="projet in filteredProjects" :key="projet.id" class="col-lg-4 col-md-6">
          <div class="card h-100 shadow-sm hover-shadow">
            <div class="card-body">
              <!-- Header du projet -->
              <div class="d-flex justify-content-between align-items-start mb-3">
                <div class="flex-grow-1">
                  <h5 class="card-title">{{ projet.titre }}</h5>
                  <span :class="getStatusClass(projet.statut)" class="badge mb-2">
                    {{ getStatusText(projet.statut) }}
                  </span>
                </div>
                <i class="fas fa-lock text-success"></i>
              </div>

              <!-- Description -->
              <p class="card-text text-muted small">{{ truncateText(projet.description, 100) }}</p>

              <!-- Métadonnées -->
              <div class="small text-muted mb-3">
                <div class="d-flex align-items-center mb-1">
                  <i class="fas fa-user me-2"></i>
                  Créé par : {{ getCreatorName(projet.idCreateur) }}
                </div>
                <div class="d-flex align-items-center mb-1">
                  <i class="fas fa-users me-2"></i>
                  {{ getProjectTasks(projet.id).length }} tâche(s)
                </div>
                <div class="d-flex align-items-center">
                  <i class="fas fa-calendar me-2"></i>
                  {{ formatDate(projet.dateCreation) }}
                </div>
              </div>

              <!-- Tâches visibles -->
              <div v-if="getProjectTasks(projet.id).length > 0" class="mb-3">
                <h6 class="small fw-bold text-dark">Tâches récentes :</h6>
                <div class="list-group list-group-flush">
                  <div v-for="tache in getProjectTasks(projet.id).slice(0, 2)" :key="tache.id"
                       class="list-group-item border-0 px-0 py-1 small">
                    <i class="fas fa-circle text-primary me-2" style="font-size: 0.5rem;"></i>
                    {{ tache.titre }}
                  </div>
                  <p v-if="getProjectTasks(projet.id).length > 2" class="small text-muted mb-0">
                    +{{ getProjectTasks(projet.id).length - 2 }} autre(s) tâche(s)
                  </p>
                </div>
              </div>
            </div>

            <!-- Footer -->
            <div class="card-footer bg-light d-flex justify-content-between align-items-center">
              <small class="text-muted">
                <i class="fas fa-eye me-1"></i> Consultation publique
              </small>
              <button @click="showProjectDetails(projet)" class="btn btn-sm btn-outline-primary">
                Voir détails
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Call to action -->
      <div class="card mt-5 bg-gradient-primary text-white">
        <div class="card-body text-center py-5">
          <h3 class="fw-bold mb-3">Rejoignez CollabPro !</h3>
          <p class="mb-4">Créez un compte pour participer aux projets, collaborer en temps réel et bien plus !</p>
          <div class="d-flex justify-content-center gap-3">
            <button @click="goToRegister" class="btn btn-light btn-lg">
              Créer un compte
            </button>
            <button @click="goToLogin" class="btn btn-outline-light btn-lg">
              Se connecter
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- Modal détails projet -->
    <div v-if="selectedProject" class="modal show d-block" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ selectedProject.titre }}</h5>
            <button type="button" class="btn-close" @click="selectedProject = null"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <h6 class="fw-bold">Description :</h6>
              <p class="text-muted">{{ selectedProject.description }}</p>
            </div>

            <div class="mb-3">
              <h6 class="fw-bold">Informations :</h6>
              <ul class="list-unstyled small text-muted">
                <li><strong>Statut :</strong> {{ getStatusText(selectedProject.statut) }}</li>
                <li><strong>Créateur :</strong> {{ getCreatorName(selectedProject.idCreateur) }}</li>
                <li><strong>Tâches :</strong> {{ getProjectTasks(selectedProject.id).length }}</li>
                <li><strong>Date de création :</strong> {{ formatDate(selectedProject.dateCreation) }}</li>
              </ul>
            </div>

            <div class="alert alert-warning">
              <i class="fas fa-lock me-2"></i>
              <strong>Accès limité :</strong> Connectez-vous pour voir plus de détails et participer à ce projet.
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()
const loading = ref(true)
const searchQuery = ref('')
const selectedStatus = ref('')
const currentLanguage = ref('fr')
const selectedProject = ref(null)
const projetsPublics = ref([])
const utilisateurs = ref([])
const taches = ref([])

// Projets filtrés
const filteredProjects = computed(() => {
  let filtered = projetsPublics.value

  if (searchQuery.value) {
    filtered = filtered.filter(projet =>
      projet.titre.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      projet.description.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  if (selectedStatus.value) {
    filtered = filtered.filter(projet => projet.statut === selectedStatus.value)
  }

  return filtered
})

const loadProjetsPublics = async () => {
  loading.value = true
  try {

    const [projetsResponse, utilisateursResponse, tachesResponse] = await Promise.all([
      api.get('/projets'),
      api.get('/utilisateurs'),
      api.get('/taches')
    ])

    projetsPublics.value = projetsResponse.data
    utilisateurs.value = utilisateursResponse.data
    taches.value = tachesResponse.data

  } catch (error) {
    console.error('Erreur chargement projets publics:', error)
    projetsPublics.value = []
  } finally {
    loading.value = false
  }
}

const getStatusClass = (statut) => {
  const classes = {
    'ACTIF': 'bg-success',
    'TERMINE': 'bg-primary',
    'EN_ATTENTE': 'bg-warning',
    'ANNULE': 'bg-danger'
  }
  return classes[statut] || 'bg-secondary'
}

const getStatusText = (statut) => {
  const texts = {
    'ACTIF': 'En cours',
    'TERMINE': 'Terminé',
    'EN_ATTENTE': 'En attente',
    'ANNULE': 'Annulé'
  }
  return texts[statut] || statut
}

const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const options = { year: 'numeric', month: 'long', day: 'numeric' }
  return new Date(dateString).toLocaleDateString('fr-FR', options)
}

const showProjectDetails = (projet) => {
  selectedProject.value = projet
}

const changeLanguage = () => {
  localStorage.setItem('language', currentLanguage.value)
}

const goBack = () => {
  router.go(-1)
}

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}

onMounted(() => {
  currentLanguage.value = localStorage.getItem('language') || 'fr'
  loadProjetsPublics()
})
</script>

<style scoped>
.hover-shadow:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15) !important;
  transition: all 0.3s ease;
}

.bg-gradient-primary {
  background: linear-gradient(135deg, #007bff, #6610f2);
}
</style>
