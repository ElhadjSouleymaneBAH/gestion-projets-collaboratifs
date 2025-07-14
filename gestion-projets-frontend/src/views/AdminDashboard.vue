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
          <!-- Indicateur de synchronisation -->
          <div v-if="loading" class="me-3">
            <div class="spinner-border spinner-border-sm text-light" role="status">
              <span class="visually-hidden">Synchronisation...</span>
            </div>
          </div>
          <div v-else-if="lastUpdate" class="me-3">
            <small class="text-light">
              <i class="fas fa-database me-1"></i>
              MySQL: {{ lastUpdate }}
            </small>
          </div>

          <div class="dropdown">
            <button class="btn btn-outline-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
              <i class="fas fa-user me-2"></i>
              {{ user?.prenom }} {{ user?.nom }}
              <span class="badge bg-danger ms-2">ADMIN</span>
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
              <i class="fas fa-file-invoice me-2"></i>
              Factures
              <span class="badge bg-secondary rounded-pill float-end">{{ stats.totalInvoices }}</span>
            </a>
            <a class="list-group-item list-group-item-action">
              <i class="fas fa-chart-bar me-2"></i>
              Rapports
            </a>
          </div>
        </div>

        <!-- Main Content -->
        <div class="col-md-9 col-lg-10 p-4">
          <!-- Welcome Message avec statut -->
          <div class="row mb-4">
            <div class="col-12">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <h2 class="fw-bold text-collabpro">
                    Bienvenue, {{ user?.prenom }} !
                  </h2>
                  <p class="text-muted">
                    Dashboard administrateur - Base de données MySQL "gestionprojets"
                  </p>
                </div>
                <div class="text-end">
                  <div v-if="!loading && stats.totalUsers > 0" class="badge bg-success fs-6 p-2">
                    <i class="fas fa-check-circle me-1"></i>
                    Système opérationnel
                  </div>
                  <div v-else-if="loading" class="badge bg-warning fs-6 p-2">
                    <i class="fas fa-spinner fa-spin me-1"></i>
                    Chargement...
                  </div>
                  <div v-else class="badge bg-danger fs-6 p-2">
                    <i class="fas fa-exclamation-triangle me-1"></i>
                    Connexion API
                  </div>
                  <br>
                  <small class="text-muted">
                    <i class="fas fa-server me-1"></i>
                    Spring Boot + MySQL
                  </small>
                </div>
              </div>
            </div>
          </div>

          <!-- Statistics Cards principales -->
          <div class="row mb-4">
            <!-- Utilisateurs -->
            <div class="col-lg-3 col-md-6 mb-3">
              <div class="card card-collabpro h-100">
                <div class="card-body text-center">
                  <div class="rounded-circle bg-primary bg-opacity-10 d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                    <i class="fas fa-users fa-lg text-primary"></i>
                  </div>
                  <h3 class="fw-bold text-primary">{{ stats.totalUsers }}</h3>
                  <p class="text-muted mb-1">Utilisateurs total</p>
                  <small class="text-success">
                    <i class="fas fa-crown me-1"></i>
                    {{ stats.chefsProjets }} Chefs de Projet
                  </small>
                </div>
              </div>
            </div>

            <!-- Projets -->
            <div class="col-lg-3 col-md-6 mb-3">
              <div class="card card-collabpro h-100">
                <div class="card-body text-center">
                  <div class="rounded-circle bg-success bg-opacity-10 d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                    <i class="fas fa-project-diagram fa-lg text-success"></i>
                  </div>
                  <h3 class="fw-bold text-success">{{ stats.totalProjects }}</h3>
                  <p class="text-muted mb-1">Projets créés</p>
                  <small class="text-info">
                    <i class="fas fa-play me-1"></i>
                    {{ stats.projetsActifs }} Actifs
                  </small>
                </div>
              </div>
            </div>

            <!-- Tâches -->
            <div class="col-lg-3 col-md-6 mb-3">
              <div class="card card-collabpro h-100">
                <div class="card-body text-center">
                  <div class="rounded-circle bg-warning bg-opacity-10 d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                    <i class="fas fa-tasks fa-lg text-warning"></i>
                  </div>
                  <h3 class="fw-bold text-warning">{{ stats.totalTasks }}</h3>
                  <p class="text-muted mb-1">Tâches gérées</p>
                  <small class="text-success">
                    <i class="fas fa-check me-1"></i>
                    {{ stats.tachesTerminees }} Terminées
                  </small>
                </div>
              </div>
            </div>

            <!-- Revenus -->
            <div class="col-lg-3 col-md-6 mb-3">
              <div class="card card-collabpro h-100">
                <div class="card-body text-center">
                  <div class="rounded-circle bg-info bg-opacity-10 d-inline-flex align-items-center justify-content-center mb-3" style="width: 60px; height: 60px;">
                    <i class="fas fa-euro-sign fa-lg text-info"></i>
                  </div>
                  <h3 class="fw-bold text-info">{{ stats.revenusMensuel }}€</h3>
                  <p class="text-muted mb-1">Revenus/mois</p>
                  <small class="text-success">
                    <i class="fas fa-credit-card me-1"></i>
                    {{ stats.activeSubscriptions }} Abonnés Premium
                  </small>
                </div>
              </div>
            </div>
          </div>

          <!-- Analytics détaillées -->
          <div class="row mb-4">
            <div class="col-lg-8">
              <div class="card card-collabpro">
                <div class="card-header">
                  <h5 class="card-title mb-0">
                    <i class="fas fa-chart-line me-2"></i>
                    Analytics détaillées (données réelles base MySQL)
                  </h5>
                </div>
                <div class="card-body">
                  <div v-if="loading" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Analyse des données...</span>
                    </div>
                    <p class="text-muted mt-2">Chargement depuis la base gestionprojets...</p>
                  </div>

                  <div v-else class="row">
                    <!-- Répartition des rôles -->
                    <div class="col-md-6 mb-4">
                      <h6 class="fw-bold mb-3">
                        <i class="fas fa-users me-2"></i>
                        Répartition des rôles ({{ stats.totalUsers }} utilisateurs)
                      </h6>
                      <div class="mb-2">
                        <div class="d-flex justify-content-between">
                          <span>Administrateurs</span>
                          <span class="fw-bold">{{ stats.roleDistribution.ADMINISTRATEUR }}</span>
                        </div>
                        <div class="progress mb-2" style="height: 8px;">
                          <div class="progress-bar bg-danger" :style="`width: ${getRolePercentage('ADMINISTRATEUR')}%`"></div>
                        </div>
                      </div>
                      <div class="mb-2">
                        <div class="d-flex justify-content-between">
                          <span>Chefs de Projet</span>
                          <span class="fw-bold">{{ stats.roleDistribution.CHEF_PROJET }}</span>
                        </div>
                        <div class="progress mb-2" style="height: 8px;">
                          <div class="progress-bar bg-primary" :style="`width: ${getRolePercentage('CHEF_PROJET')}%`"></div>
                        </div>
                      </div>
                      <div class="mb-2">
                        <div class="d-flex justify-content-between">
                          <span>Membres</span>
                          <span class="fw-bold">{{ stats.roleDistribution.MEMBRE }}</span>
                        </div>
                        <div class="progress mb-2" style="height: 8px;">
                          <div class="progress-bar bg-success" :style="`width: ${getRolePercentage('MEMBRE')}%`"></div>
                        </div>
                      </div>
                      <div class="mb-2">
                        <div class="d-flex justify-content-between">
                          <span>Visiteurs</span>
                          <span class="fw-bold">{{ stats.roleDistribution.VISITEUR }}</span>
                        </div>
                        <div class="progress mb-2" style="height: 8px;">
                          <div class="progress-bar bg-warning" :style="`width: ${getRolePercentage('VISITEUR')}%`"></div>
                        </div>
                      </div>
                    </div>

                    <!-- Statuts projets et tâches -->
                    <div class="col-md-6 mb-4">
                      <h6 class="fw-bold mb-3">
                        <i class="fas fa-project-diagram me-2"></i>
                        Statuts des projets
                      </h6>
                      <div class="mb-3">
                        <div class="d-flex justify-content-between">
                          <span>Projets Actifs</span>
                          <span class="fw-bold text-success">{{ stats.projectStatus.ACTIF }}</span>
                        </div>
                        <div class="progress mb-2" style="height: 8px;">
                          <div class="progress-bar bg-success" :style="`width: ${getProjectPercentage('ACTIF')}%`"></div>
                        </div>
                      </div>
                      <div class="mb-3">
                        <div class="d-flex justify-content-between">
                          <span>Projets Terminés</span>
                          <span class="fw-bold text-primary">{{ stats.projectStatus.TERMINE }}</span>
                        </div>
                        <div class="progress mb-2" style="height: 8px;">
                          <div class="progress-bar bg-primary" :style="`width: ${getProjectPercentage('TERMINE')}%`"></div>
                        </div>
                      </div>

                      <h6 class="fw-bold mb-2 mt-4">
                        <i class="fas fa-tasks me-2"></i>
                        Tâches par statut
                      </h6>
                      <small class="text-muted">
                        {{ stats.taskStatus.BROUILLON }} Brouillon •
                        {{ stats.taskStatus.EN_ATTENTE_VALIDATION }} En attente •
                        {{ stats.taskStatus.TERMINE }} Terminées
                      </small>
                    </div>

                    <!-- Métriques financières -->
                    <div class="col-12">
                      <hr class="my-3">
                      <h6 class="fw-bold mb-3">
                        <i class="fas fa-chart-bar me-2"></i>
                        Métriques financières et système
                      </h6>
                      <div class="row">
                        <div class="col-md-3">
                          <div class="text-center p-3 bg-light rounded">
                            <h5 class="fw-bold text-success">{{ stats.transactionSuccessRate }}%</h5>
                            <small class="text-muted">Taux succès Stripe</small>
                          </div>
                        </div>
                        <div class="col-md-3">
                          <div class="text-center p-3 bg-light rounded">
                            <h5 class="fw-bold text-info">{{ stats.totalTransactions }}</h5>
                            <small class="text-muted">Transactions</small>
                          </div>
                        </div>
                        <div class="col-md-3">
                          <div class="text-center p-3 bg-light rounded">
                            <h5 class="fw-bold text-warning">{{ stats.totalInvoices }}</h5>
                            <small class="text-muted">Factures PDF</small>
                          </div>
                        </div>
                        <div class="col-md-3">
                          <div class="text-center p-3 bg-light rounded">
                            <h5 class="fw-bold text-primary">{{ stats.invoiceSuccessRate }}%</h5>
                            <small class="text-muted">Factures réussies</small>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Infos système et actions -->
            <div class="col-lg-4">
              <div class="card card-collabpro mb-3">
                <div class="card-header">
                  <h5 class="card-title mb-0">
                    <i class="fas fa-server me-2"></i>
                    État du système
                  </h5>
                </div>
                <div class="card-body">
                  <div class="d-flex align-items-center mb-3">
                    <div class="status-indicator bg-success me-3"></div>
                    <div>
                      <strong>MySQL gestionprojets</strong>
                      <br>
                      <small class="text-muted">{{ stats.totalUsers + stats.totalProjects + stats.totalTasks }} enregistrements</small>
                    </div>
                  </div>
                  <div class="d-flex align-items-center mb-3">
                    <div class="status-indicator bg-success me-3"></div>
                    <div>
                      <strong>API Spring Boot</strong>
                      <br>
                      <small class="text-muted">Endpoints opérationnels</small>
                    </div>
                  </div>
                  <div class="d-flex align-items-center mb-3">
                    <div class="status-indicator bg-success me-3"></div>
                    <div>
                      <strong>Paiements Stripe</strong>
                      <br>
                      <small class="text-muted">{{ stats.transactionSuccessRate }}% de succès</small>
                    </div>
                  </div>
                  <div class="d-flex align-items-center">
                    <div class="status-indicator bg-success me-3"></div>
                    <div>
                      <strong>Interface Vue.js</strong>
                      <br>
                      <small class="text-muted">Réactive et responsive</small>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Informations TFE -->
              <div class="card card-collabpro">
                <div class="card-header">
                  <h5 class="card-title mb-0">
                    <i class="fas fa-graduation-cap me-2"></i>
                    Projet TFE 2025
                  </h5>
                </div>
                <div class="card-body">
                  <div class="alert alert-info mb-3" role="alert">
                    <h6 class="alert-heading">CollabPro</h6>
                    <p class="mb-1">Application Web de Gestion de Projets Collaboratifs</p>
                    <small>Développé par <strong>ElhadjSouleymaneBAH</strong></small>
                  </div>

                  <div class="alert alert-success" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                    <strong>Toutes les données sont réelles</strong>
                    <br>
                    <small>Source authentique : Base MySQL + API Spring Boot</small>
                  </div>

                  <button class="btn btn-outline-primary w-100 mb-2" @click="refreshStats">
                    <i class="fas fa-sync-alt me-2"></i>
                    Actualiser données
                  </button>
                  <button class="btn btn-outline-info w-100" @click="exportData">
                    <i class="fas fa-download me-2"></i>
                    Exporter rapport
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Activité récente avec vraies données -->
          <div class="row">
            <div class="col-lg-8">
              <div class="card card-collabpro">
                <div class="card-header">
                  <h5 class="card-title mb-0">
                    <i class="fas fa-clock me-2"></i>
                    Activité système récente
                  </h5>
                </div>
                <div class="card-body">
                  <div v-if="loading" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Chargement...</span>
                    </div>
                  </div>

                  <div v-else-if="recentActivities.length === 0" class="text-center py-4 text-muted">
                    <i class="fas fa-database fa-2x mb-3"></i>
                    <p>Système opérationnel</p>
                    <small>Dashboard initialisé avec les données MySQL</small>
                  </div>

                  <div v-else class="list-group list-group-flush">
                    <div v-for="activity in recentActivities" :key="activity.id" class="list-group-item border-0 px-0">
                      <div class="d-flex align-items-center">
                        <div class="me-3">
                          <i :class="activity.icon" :class="activity.iconColor"></i>
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
                    <i class="fas fa-tasks me-2"></i>
                    Actions rapides
                  </h5>
                </div>
                <div class="card-body">
                  <div class="d-grid gap-2">
                    <button class="btn btn-outline-primary">
                      <i class="fas fa-users me-2"></i>
                      Gérer utilisateurs ({{ stats.totalUsers }})
                    </button>
                    <button class="btn btn-outline-success">
                      <i class="fas fa-project-diagram me-2"></i>
                      Gérer projets ({{ stats.totalProjects }})
                    </button>
                    <button class="btn btn-outline-warning">
                      <i class="fas fa-credit-card me-2"></i>
                      Abonnements ({{ stats.activeSubscriptions }})
                    </button>
                    <button class="btn btn-outline-info">
                      <i class="fas fa-file-invoice me-2"></i>
                      Factures ({{ stats.totalInvoices }})
                    </button>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '@/services/api'
import api from '@/services/api'

const router = useRouter()
const loading = ref(true)
const lastUpdateTime = ref(null)

// Données utilisateur
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

// Statistiques
const stats = reactive({
  totalUsers: 0,
  totalProjects: 0,
  totalTasks: 0,
  activeSubscriptions: 0,
  totalTransactions: 0,
  totalInvoices: 0,
  revenusMensuel: 0,
  chefsProjets: 0,
  projetsActifs: 0,
  tachesTerminees: 0,
  transactionSuccessRate: 0,
  invoiceSuccessRate: 0,
  roleDistribution: {
    ADMINISTRATEUR: 0,
    CHEF_PROJET: 0,
    MEMBRE: 0,
    VISITEUR: 0
  },
  projectStatus: {
    ACTIF: 0,
    TERMINE: 0
  },
  taskStatus: {
    BROUILLON: 0,
    EN_ATTENTE_VALIDATION: 0,
    TERMINE: 0
  }
})

// Activités récentes
const recentActivities = ref([])

const lastUpdate = computed(() => {
  if (!lastUpdateTime.value) return ''
  return new Intl.DateTimeFormat('fr-FR', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(lastUpdateTime.value)
})

const getRolePercentage = (role) => {
  if (stats.totalUsers === 0) return 0
  return Math.round((stats.roleDistribution[role] / stats.totalUsers) * 100)
}

const getProjectPercentage = (status) => {
  if (stats.totalProjects === 0) return 0
  return Math.round((stats.projectStatus[status] / stats.totalProjects) * 100)
}

// Chargement des statistiques depuis les APIs
const loadStats = async () => {
  loading.value = true
  try {
    console.log('Admin Dashboard: Chargement des données depuis MySQL...')

    // Appels à vos vraies APIs Spring Boot
    const [usersResponse, projectsResponse, tasksResponse, transactionsResponse, facturesResponse, abonnementsResponse] = await Promise.all([
      api.get('/utilisateurs'),           // Table utilisateurs
      api.get('/projets'),               // Table projets
      api.get('/taches'),                // Table taches
      api.get('/transactions'),          // Table transactions
      api.get('/factures'),              // Table factures
      api.get('/abonnements')            // Table abonnements
    ])

    const users = usersResponse.data
    const projects = projectsResponse.data
    const tasks = tasksResponse.data
    const transactions = transactionsResponse.data
    const factures = facturesResponse.data
    const abonnements = abonnementsResponse.data

    // Calculs des statistiques principales
    stats.totalUsers = users.length
    stats.totalProjects = projects.length
    stats.totalTasks = tasks.length
    stats.totalTransactions = transactions.length
    stats.totalInvoices = factures.length
    stats.activeSubscriptions = abonnements.length

    // Répartition des rôles
    stats.roleDistribution.ADMINISTRATEUR = users.filter(u => u.role === 'ADMINISTRATEUR').length
    stats.roleDistribution.CHEF_PROJET = users.filter(u => u.role === 'CHEF_PROJET').length
    stats.roleDistribution.MEMBRE = users.filter(u => u.role === 'MEMBRE').length
    stats.roleDistribution.VISITEUR = users.filter(u => u.role === 'VISITEUR').length

    stats.chefsProjets = stats.roleDistribution.CHEF_PROJET

    // Statuts des projets
    stats.projectStatus.ACTIF = projects.filter(p => p.statut === 'ACTIF').length
    stats.projectStatus.TERMINE = projects.filter(p => p.statut === 'TERMINE').length
    stats.projetsActifs = stats.projectStatus.ACTIF

    // Statuts des tâches
    stats.taskStatus.BROUILLON = tasks.filter(t => t.statut === 'BROUILLON').length
    stats.taskStatus.EN_ATTENTE_VALIDATION = tasks.filter(t => t.statut === 'EN_ATTENTE_VALIDATION').length
    stats.taskStatus.TERMINE = tasks.filter(t => t.statut === 'TERMINE').length
    stats.tachesTerminees = stats.taskStatus.TERMINE

    // Métriques financières
    const transactionsComplete = transactions.filter(t => t.statut === 'COMPLETE').length
    stats.transactionSuccessRate = transactions.length > 0 ? Math.round((transactionsComplete / transactions.length) * 100) : 0

    const facturesGenerees = factures.filter(f => f.statut === 'GENEREE').length
    stats.invoiceSuccessRate = factures.length > 0 ? Math.round((facturesGenerees / factures.length) * 100) : 0

    // Calcul revenus (10€ par abonnement Premium selon le business plan)
    stats.revenusMensuel = abonnements.length * 10

    lastUpdateTime.value = new Date()

    // Log de l'activité pour le dashboard
    addActivity('Dashboard initialisé avec données MySQL', 'fas fa-database', 'text-success')

    console.log('Admin Dashboard: Statistiques complètes chargées:', {
      users: stats.totalUsers,
      projects: stats.totalProjects,
      tasks: stats.totalTasks,
      revenue: stats.revenusMensuel + '€/mois',
      chefsProjets: stats.chefsProjets,
      transactionSuccessRate: stats.transactionSuccessRate + '%'
    })

  } catch (error) {
    console.error('Admin Dashboard: Erreur chargement données:', error)
    addActivity('Erreur de connexion à la base MySQL', 'fas fa-exclamation-triangle', 'text-danger')

    // Réinitialisation des stats en cas d'erreur
    Object.keys(stats).forEach(key => {
      if (typeof stats[key] === 'number') {
        stats[key] = 0
      } else if (typeof stats[key] === 'object') {
        Object.keys(stats[key]).forEach(subKey => {
          stats[key][subKey] = 0
        })
      }
    })
  } finally {
    loading.value = false
  }
}

const addActivity = (message, icon, iconColor) => {
  const activity = {
    id: Date.now(),
    description: message,
    icon: icon,
    iconColor: iconColor,
    date: new Date()
  }

  recentActivities.value.unshift(activity)

  if (recentActivities.value.length > 5) {
    recentActivities.value = recentActivities.value.slice(0, 5)
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

const refreshStats = () => {
  console.log('Admin Dashboard: Actualisation manuelle...')
  addActivity('Actualisation manuelle des données', 'fas fa-sync-alt', 'text-primary')
  loadStats()
}

const exportData = () => {
  console.log('Admin Dashboard: Export des données...')
  addActivity('Export des données administrateur', 'fas fa-download', 'text-info')

  const exportData = {
    timestamp: new Date().toISOString(),
    statistics: {
      users: {
        total: stats.totalUsers,
        distribution: stats.roleDistribution
      },
      projects: {
        total: stats.totalProjects,
        status: stats.projectStatus
      },
      tasks: {
        total: stats.totalTasks,
        status: stats.taskStatus
      },
      financial: {
        monthlyRevenue: stats.revenusMensuel,
        subscriptions: stats.activeSubscriptions,
        transactionSuccessRate: stats.transactionSuccessRate,
        invoiceSuccessRate: stats.invoiceSuccessRate
      }
    },
    source: 'MySQL gestionprojets via Spring Boot API',
    generated_by: 'CollabPro Admin Dashboard'
  }

  // Création et téléchargement du fichier JSON
  const dataStr = JSON.stringify(exportData, null, 2)
  const dataBlob = new Blob([dataStr], { type: 'application/json' })
  const url = URL.createObjectURL(dataBlob)
  const link = document.createElement('a')
  link.href = url
  link.download = `collabpro-admin-report-${new Date().toISOString().split('T')[0]}.json`
  link.click()
  URL.revokeObjectURL(url)
}

const logout = () => {
  addActivity('Déconnexion administrateur', 'fas fa-sign-out-alt', 'text-warning')
  authAPI.logout()
  router.push('/login')
}

// Lifecycle
onMounted(() => {
  console.log('Admin Dashboard: Initialisation...')
  addActivity('Connexion dashboard administrateur', 'fas fa-user-shield', 'text-success')
  loadStats()

  // Auto-refresh toutes les 3 minutes
  setInterval(() => {
    if (!loading.value) {
      loadStats()
    }
  }, 3 * 60 * 1000)
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

.card-header {
  background-color: rgba(37, 99, 235, 0.05);
  border-bottom: 1px solid rgba(37, 99, 235, 0.1);
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
}

.card-collabpro {
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

.card-collabpro:hover {
  box-shadow: 0 4px 15px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.progress {
  border-radius: 10px;
  overflow: hidden;
}

.progress-bar {
  border-radius: 10px;
  transition: width 0.6s ease;
}

.badge {
  font-size: 0.75em;
  font-weight: 600;
}

@media (max-width: 768px) {
  .col-md-3 {
    margin-bottom: 1rem;
  }

  .card-body {
    padding: 1rem;
  }

  .display-4 {
    font-size: 1.8rem;
  }
}

@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-collabpro h3 {
  animation: countUp 0.8s ease-out;
}

.alert {
  border-radius: 8px;
  border: none;
}

.alert-info {
  background-color: rgba(13, 202, 240, 0.1);
  color: #0c5460;
}

.alert-success {
  background-color: rgba(25, 135, 84, 0.1);
  color: #0f5132;
}

.alert-warning {
  background-color: rgba(255, 193, 7, 0.1);
  color: #664d03;
}

.alert-danger {
  background-color: rgba(220, 53, 69, 0.1);
  color: #721c24;
}
</style>
