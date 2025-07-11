<!-- AdminDashboard.vue -->
<template>
  <div class="min-vh-100 bg-light">
    <!-- Navigation Header -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-collabpro shadow-sm">
      <div class="container-fluid">
        <div class="navbar-brand d-flex align-items-center">
          <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 32px; width: 32px;">
          <div>
            <h5 class="mb-0 text-white">CollabPro Admin</h5>
            <small class="text-light">Tableau de bord administrateur</small>
          </div>
        </div>

        <div class="d-flex align-items-center">
          <div class="dropdown">
            <button class="btn btn-outline-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
              <i class="fas fa-user me-2"></i>
              {{ user?.prenom }} {{ user?.nom }}
            </button>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#"><i class="fas fa-user me-2"></i>Mon profil</a></li>
              <li><a class="dropdown-item" href="#"><i class="fas fa-cog me-2"></i>Paramètres</a></li>
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item" href="#" @click="logout"><i class="fas fa-sign-out-alt me-2"></i>Déconnexion</a></li>
            </ul>
          </div>
        </div>
      </div>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 bg-white shadow-sm p-0">
          <div class="list-group list-group-flush">
            <a class="list-group-item list-group-item-action active">
              <i class="fas fa-tachometer-alt me-2"></i>
              Tableau de bord
            </a>
            <a class="list-group-item list-group-item-action">
              <i class="fas fa-users me-2"></i>
              Utilisateurs
              <span class="badge bg-primary rounded-pill float-end">{{ stats.totalUsers }}</span>
            </a>
            <a class="list-group-item list-group-item-action">
              <i class="fas fa-project-diagram me-2"></i>
              Projets
              <span class="badge bg-success rounded-pill float-end">{{ stats.totalProjects }}</span>
            </a>
            <a class="list-group-item list-group-item-action">
              <i class="fas fa-tasks me-2"></i>
              Tâches
              <span class="badge bg-warning rounded-pill float-end">{{ stats.totalTasks }}</span>
            </a>
            <a class="list-group-item list-group-item-action">
              <i class="fas fa-credit-card me-2"></i>
              Abonnements
              <span class="badge bg-info rounded-pill float-end">{{ stats.activeSubscriptions }}</span>
            </a>
            <a class="list-group-item list-group-item-action">
              <i class="fas fa-chart-bar me-2"></i>
              Rapports
            </a>
          </div>
        </div>

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 p-4">
          <!-- Welcome Message -->
          <div class="row mb-4">
            <div class="col-12">
              <h2 class="fw-bold text-collabpro">
                Bienvenue, {{ user?.prenom }} ! 👋
              </h2>
              <p class="text-muted">Voici un aperçu de votre plateforme CollabPro</p>
            </div>
          </div>

          <!-- Statistics Cards -->
          <div class="row mb-4">
            <div class="col-lg-3 col-md-6 mb-3">
              <div class="card card-collabpro h-100">
                <div class="card-body text-center">
                  <div class="rounded-circle bg-primary bg-opacity-10 d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                    <i class="fas fa-users fa-lg text-primary"></i>
                  </div>
                  <h3 class="fw-bold text-primary">{{ stats.totalUsers }}</h3>
                  <p class="text-muted mb-0">Utilisateurs</p>
                </div>
              </div>
            </div>

            <div class="col-lg-3 col-md-6 mb-3">
              <div class="card card-collabpro h-100">
                <div class="card-body text-center">
                  <div class="rounded-circle bg-success bg-opacity-10 d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                    <i class="fas fa-project-diagram fa-lg text-success"></i>
                  </div>
                  <h3 class="fw-bold text-success">{{ stats.totalProjects }}</h3>
                  <p class="text-muted mb-0">Projets</p>
                </div>
              </div>
            </div>

            <div class="col-lg-3 col-md-6 mb-3">
              <div class="card card-collabpro h-100">
                <div class="card-body text-center">
                  <div class="rounded-circle bg-warning bg-opacity-10 d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                    <i class="fas fa-tasks fa-lg text-warning"></i>
                  </div>
                  <h3 class="fw-bold text-warning">{{ stats.totalTasks }}</h3>
                  <p class="text-muted mb-0">Tâches</p>
                </div>
              </div>
            </div>

            <div class="col-lg-3 col-md-6 mb-3">
              <div class="card card-collabpro h-100">
                <div class="card-body text-center">
                  <div class="rounded-circle bg-info bg-opacity-10 d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                    <i class="fas fa-credit-card fa-lg text-info"></i>
                  </div>
                  <h3 class="fw-bold text-info">{{ stats.activeSubscriptions }}</h3>
                  <p class="text-muted mb-0">Abonnements</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Recent Activity -->
          <div class="row">
            <div class="col-lg-8">
              <div class="card card-collabpro">
                <div class="card-header">
                  <h5 class="card-title mb-0">
                    <i class="fas fa-clock me-2"></i>
                    Activité récente
                  </h5>
                </div>
                <div class="card-body">
                  <div v-if="loading" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Chargement...</span>
                    </div>
                  </div>

                  <div v-else class="list-group list-group-flush">
                    <div v-for="activity in recentActivities" :key="activity.id" class="list-group-item border-0 px-0">
                      <div class="d-flex align-items-center">
                        <div class="me-3">
                          <i :class="activity.icon" class="text-muted"></i>
                        </div>
                        <div class="flex-grow-1">
                          <p class="mb-1">{{ activity.description }}</p>
                          <small class="text-muted">{{ formatDate(activity.date) }}</small>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-lg-4">
              <div class="card card-collabpro">
                <div class="card-header">
                  <h5 class="card-title mb-0">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Actions requises
                  </h5>
                </div>
                <div class="card-body">
                  <div class="alert alert-warning" role="alert">
                    <i class="fas fa-user-clock me-2"></i>
                    <strong>3 nouveaux utilisateurs</strong> en attente de validation
                  </div>

                  <div class="alert alert-info" role="alert">
                    <i class="fas fa-credit-card me-2"></i>
                    <strong>2 abonnements</strong> expirent bientôt
                  </div>

                  <div class="alert alert-success" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                    Système à jour - Aucune action requise
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

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '@/services/api'

const router = useRouter()
const loading = ref(true)

// Données utilisateur
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

// Statistiques
const stats = reactive({
  totalUsers: 0,
  totalProjects: 0,
  totalTasks: 0,
  activeSubscriptions: 0
})

// Activités récentes
const recentActivities = ref([
  {
    id: 1,
    icon: 'fas fa-user-plus',
    description: 'Nouvel utilisateur inscrit : Lucas Lefevre',
    date: new Date()
  },
  {
    id: 2,
    icon: 'fas fa-project-diagram',
    description: 'Nouveau projet créé : Site Web E-commerce',
    date: new Date(Date.now() - 1000 * 60 * 30)
  },
  {
    id: 3,
    icon: 'fas fa-check-circle',
    description: 'Tâche terminée : Configuration serveur',
    date: new Date(Date.now() - 1000 * 60 * 60)
  }
])

// Méthodes
const loadStats = async () => {
  try {
    // Simulation des statistiques - remplacez par vos vraies APIs
    stats.totalUsers = 6
    stats.totalProjects = 4
    stats.totalTasks = 12
    stats.activeSubscriptions = 3
  } catch (error) {
    console.error('Erreur chargement stats:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date) => {
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / (1000 * 60))

  if (minutes < 1) return 'À l\'instant'
  if (minutes < 60) return `Il y a ${minutes} min`

  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `Il y a ${hours}h`

  return date.toLocaleDateString('fr-FR')
}

const logout = () => {
  authAPI.logout()
  router.push('/login')
}

// Lifecycle
onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.list-group-item-action.active {
  background-color: var(--collabpro-blue);
  border-color: var(--collabpro-blue);
}

.list-group-item-action:hover {
  background-color: var(--collabpro-light);
}
</style>
