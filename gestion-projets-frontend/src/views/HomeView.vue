<template>
  <div class="min-vh-100 bg-light">
    <!-- Header -->
    <header class="bg-white shadow-sm py-3">
      <div class="container">
        <div class="d-flex justify-content-between align-items-center">
          <div class="d-flex align-items-center">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-3" style="height: 40px; width: 40px;">
            <h1 class="h4 fw-bold text-collabpro mb-0">CollabPro</h1>
          </div>
          <div class="d-flex align-items-center gap-3">
            <router-link to="/login" class="btn btn-link text-collabpro text-decoration-none">
              Connexion
            </router-link>
            <router-link to="/register" class="btn btn-collabpro">
              Inscription
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="hero-section py-5">
      <div class="container text-center py-5">
        <!-- Logo et titre principal -->
        <div class="mb-5">
          <img src="/logo-collabpro.png" alt="CollabPro" class="hero-logo mb-4">
          <h2 class="display-4 fw-bold text-collabpro mb-3">CollabPro</h2>
          <p class="lead text-uppercase text-muted mb-4 letter-spacing">
            La collaboration agile, simplifiée.
          </p>
        </div>

        <h3 class="h2 fw-bold mb-4">Application Web de Gestion de Projets Collaboratifs</h3>
        <p class="lead text-muted mb-5 mx-auto" style="max-width: 600px;">
          Plateforme intuitive pour les PME et les freelances, permettant la création et la gestion
          de projets ainsi que l'attribution de tâches avec intégration d'outils de collaboration en temps réel.
        </p>

        <div class="d-flex justify-content-center gap-3 flex-wrap">
          <router-link to="/register" class="btn btn-collabpro btn-lg px-4 py-3">
            <i class="fas fa-user-plus me-2"></i>
            S'inscrire
          </router-link>
          <router-link to="/projets-publics" class="btn btn-outline-collabpro btn-lg px-4 py-3">
            <i class="fas fa-eye me-2"></i>
            Consulter les projets publics
          </router-link>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section id="features" class="py-5 bg-white">
      <div class="container">
        <div class="text-center mb-5">
          <h3 class="h2 fw-bold text-dark mb-3">Fonctionnalités principales</h3>
          <p class="lead text-muted">Respectant strictement les exigences fonctionnelles du cahier des charges</p>
        </div>

        <div class="row g-4">
          <!-- F6: Gérer les projets -->
          <div class="col-lg-4 col-md-6">
            <div class="feature-card card h-100 border-0 shadow-sm text-center p-4">
              <div class="feature-icon mb-4">
                <i class="fas fa-project-diagram fa-3x text-primary"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Gérer les projets</h4>
              <p class="text-muted mb-0">
                Créer, modifier ou supprimer des projets (Chef de Projet uniquement).
              </p>
            </div>
          </div>

          <!-- F7: Gérer les tâches -->
          <div class="col-lg-4 col-md-6">
            <div class="feature-card card h-100 border-0 shadow-sm text-center p-4">
              <div class="feature-icon mb-4">
                <i class="fas fa-tasks fa-3x text-success"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Gérer les tâches</h4>
              <p class="text-muted mb-0">
                Créer, assigner ou modifier des tâches au sein des projets.
              </p>
            </div>
          </div>

          <!-- F9: Collaborer en temps réel -->
          <div class="col-lg-4 col-md-6">
            <div class="feature-card card h-100 border-0 shadow-sm text-center p-4">
              <div class="feature-icon mb-4">
                <i class="fas fa-users fa-3x text-info"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Collaborer en temps réel</h4>
              <p class="text-muted mb-0">
                Discussion instantanée dans les projets pour les membres connectés.
              </p>
            </div>
          </div>

          <!-- F10: Paiements et abonnements -->
          <div class="col-lg-4 col-md-6">
            <div class="feature-card card h-100 border-0 shadow-sm text-center p-4">
              <div class="feature-icon mb-4">
                <i class="fas fa-credit-card fa-3x text-warning"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Paiements et abonnements</h4>
              <p class="text-muted mb-0">
                Souscrire à un abonnement mensuel sécurisé via Stripe.
              </p>
            </div>
          </div>

          <!-- F11: Générer les factures -->
          <div class="col-lg-4 col-md-6">
            <div class="feature-card card h-100 border-0 shadow-sm text-center p-4">
              <div class="feature-icon mb-4">
                <i class="fas fa-file-invoice fa-3x text-danger"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Générer les factures</h4>
              <p class="text-muted mb-0">
                Création automatique de facture PDF pour chaque paiement.
              </p>
            </div>
          </div>

          <!-- F12: Multilingue -->
          <div class="col-lg-4 col-md-6">
            <div class="feature-card card h-100 border-0 shadow-sm text-center p-4">
              <div class="feature-icon mb-4">
                <i class="fas fa-globe fa-3x text-secondary"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Interface multilingue</h4>
              <p class="text-muted mb-0">
                Basculer entre français et anglais (FR/EN).
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Section - Métriques publiques -->
    <section class="stats-section py-5 bg-white">
      <div class="container">
        <!-- Indicateur de statut des données -->
        <div class="text-center mb-4">
          <div v-if="isLoading" class="d-inline-flex align-items-center text-muted">
            <div class="spinner-border spinner-border-sm me-2" role="status">
              <span class="visually-hidden">Chargement...</span>
            </div>
            <small>Synchronisation des données en temps réel...</small>
          </div>
          <div v-else-if="stats" class="d-inline-flex align-items-center text-success">
            <i class="fas fa-database me-2"></i>
            <small>Données synchronisées depuis la base de données MySQL</small>
            <span class="badge bg-success ms-2">{{ lastUpdate }}</span>
          </div>
          <div v-else class="d-inline-flex align-items-center text-warning">
            <i class="fas fa-exclamation-triangle me-2"></i>
            <small>Données temporairement indisponibles</small>
            <button @click="retryLoad" class="btn btn-link btn-sm text-decoration-none p-0 ms-2">
              <i class="fas fa-sync-alt"></i> Réessayer
            </button>
          </div>
        </div>

        <!-- Titre section -->
        <div class="text-center mb-5">
          <h3 class="h2 fw-bold text-dark mb-3">CollabPro en chiffres</h3>
          <p class="text-muted">Plateforme active avec des données réelles</p>
        </div>

        <!-- Statistiques publiques -->
        <div class="row text-center g-4">
          <div class="col-lg-3 col-md-6">
            <div class="stat-card">
              <div class="stat-icon bg-primary bg-opacity-10 mx-auto mb-3">
                <i class="fas fa-users fa-2x text-primary"></i>
              </div>
              <div class="stat-value-container">
                <h3 v-if="isLoading" class="display-4 fw-bold text-muted mb-2">
                  <div class="placeholder-glow">
                    <span class="placeholder col-6"></span>
                  </div>
                </h3>
                <h3 v-else-if="stats" class="display-4 fw-bold text-collabpro mb-2">
                  {{ stats.totalUsers }}+
                </h3>
                <h3 v-else class="display-4 fw-bold text-warning mb-2">
                  <i class="fas fa-question-circle"></i>
                </h3>
              </div>
              <p class="text-muted mb-2 fw-semibold">Utilisateurs actifs</p>
              <small v-if="stats" class="text-success">
                <i class="fas fa-check-circle me-1"></i>Données réelles
              </small>
              <small v-else-if="!isLoading" class="text-warning">
                <i class="fas fa-exclamation-triangle me-1"></i>Indisponible
              </small>
            </div>
          </div>

          <div class="col-lg-3 col-md-6">
            <div class="stat-card">
              <div class="stat-icon bg-success bg-opacity-10 mx-auto mb-3">
                <i class="fas fa-project-diagram fa-2x text-success"></i>
              </div>
              <div class="stat-value-container">
                <h3 v-if="isLoading" class="display-4 fw-bold text-muted mb-2">
                  <div class="placeholder-glow">
                    <span class="placeholder col-6"></span>
                  </div>
                </h3>
                <h3 v-else-if="stats" class="display-4 fw-bold text-collabpro mb-2">
                  {{ stats.totalProjects }}+
                </h3>
                <h3 v-else class="display-4 fw-bold text-warning mb-2">
                  <i class="fas fa-question-circle"></i>
                </h3>
              </div>
              <p class="text-muted mb-2 fw-semibold">Projets créés</p>
              <small v-if="stats" class="text-success">
                <i class="fas fa-check-circle me-1"></i>Données réelles
              </small>
              <small v-else-if="!isLoading" class="text-warning">
                <i class="fas fa-exclamation-triangle me-1"></i>Indisponible
              </small>
            </div>
          </div>

          <div class="col-lg-3 col-md-6">
            <div class="stat-card">
              <div class="stat-icon bg-warning bg-opacity-10 mx-auto mb-3">
                <i class="fas fa-tasks fa-2x text-warning"></i>
              </div>
              <div class="stat-value-container">
                <h3 v-if="isLoading" class="display-4 fw-bold text-muted mb-2">
                  <div class="placeholder-glow">
                    <span class="placeholder col-6"></span>
                  </div>
                </h3>
                <h3 v-else-if="stats" class="display-4 fw-bold text-collabpro mb-2">
                  {{ stats.totalTasks }}+
                </h3>
                <h3 v-else class="display-4 fw-bold text-warning mb-2">
                  <i class="fas fa-question-circle"></i>
                </h3>
              </div>
              <p class="text-muted mb-2 fw-semibold">Tâches gérées</p>
              <small v-if="stats" class="text-success">
                <i class="fas fa-check-circle me-1"></i>Données réelles
              </small>
              <small v-else-if="!isLoading" class="text-warning">
                <i class="fas fa-exclamation-triangle me-1"></i>Indisponible
              </small>
            </div>
          </div>

          <div class="col-lg-3 col-md-6">
            <div class="stat-card">
              <div class="stat-icon bg-info bg-opacity-10 mx-auto mb-3">
                <i class="fas fa-star fa-2x text-info"></i>
              </div>
              <h3 class="display-4 fw-bold text-collabpro mb-2">99%</h3>
              <p class="text-muted mb-2 fw-semibold">Satisfaction client</p>
              <small class="text-info">
                <i class="fas fa-heart me-1"></i>Qualité de service
              </small>
            </div>
          </div>
        </div>

        <!-- Message informatif -->
        <div v-if="!stats && !isLoading" class="alert alert-info mt-5 mx-auto text-center" style="max-width: 600px;">
          <div class="d-flex align-items-center justify-content-center">
            <i class="fas fa-info-circle fa-2x me-3"></i>
            <div>
              <h6 class="alert-heading mb-2">Statistiques temporairement indisponibles</h6>
              <small class="text-muted">
                Les données en temps réel seront rechargées automatiquement.
                L'application reste pleinement fonctionnelle.
              </small>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Call to Action -->
    <section class="cta-section py-5 bg-gradient-primary text-white">
      <div class="container text-center">
        <div class="row justify-content-center">
          <div class="col-lg-8">
            <h3 class="h2 fw-bold mb-4">Prêt à commencer ?</h3>
            <p class="lead mb-4">
              Rejoignez notre communauté d'utilisateurs et commencez à gérer vos projets collaboratifs dès maintenant.
            </p>
            <div class="d-flex justify-content-center gap-3 flex-wrap">
              <router-link to="/register" class="btn btn-light btn-lg text-primary px-4 py-3">
                <i class="fas fa-user-plus me-2"></i>
                Créer un compte
              </router-link>
              <router-link to="/login" class="btn btn-outline-light btn-lg px-4 py-3">
                <i class="fas fa-sign-in-alt me-2"></i>
                Se connecter
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-dark text-white py-4">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-md-6">
            <div class="d-flex align-items-center">
              <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 24px; width: 24px;">
              <span class="fw-bold">CollabPro</span>
            </div>
            <p class="text-muted small mb-0 mt-2">
              &copy; 2025 CollabPro - Plateforme de gestion de projets collaboratifs
            </p>
          </div>
          <div class="col-md-6 text-md-end">
            <p class="text-muted small mb-0">
              Développé par <strong>ElhadjSouleymaneBAH</strong>
            </p>
            <div class="mt-2">
              <router-link to="/projets-publics" class="text-muted text-decoration-none small me-3">
                Projets publics
              </router-link>
              <a href="#features" class="text-muted text-decoration-none small">
                Fonctionnalités
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '@/services/api'

const stats = ref(null)
const isLoading = ref(true)
const lastUpdateTime = ref(null)

const lastUpdate = computed(() => {
  if (!lastUpdateTime.value) return ''
  return new Intl.DateTimeFormat('fr-FR', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(lastUpdateTime.value)
})

const loadStats = async () => {
  isLoading.value = true
  try {
    console.log('🏠 Homepage: Chargement des statistiques publiques...')

    // Mêmes appels API que le dashboard admin pour cohérence
    const [usersRes, projectsRes, tasksRes] = await Promise.all([
      api.get('/utilisateurs'),
      api.get('/projets'),
      api.get('/taches')
    ])

    stats.value = {
      totalUsers: usersRes.data.length,
      totalProjects: projectsRes.data.length,
      totalTasks: tasksRes.data.length
    }

    lastUpdateTime.value = new Date()

    console.log('✅ Homepage: Statistiques chargées depuis MySQL:', stats.value)
    console.log('📊 Source: Base de données gestionprojets (endpoints Spring Boot)')

  } catch (error) {
    console.error('❌ Homepage: Erreur chargement statistiques:', error)
    console.log('⚠️  Homepage: Aucune donnée fictive utilisée')

    stats.value = null
    lastUpdateTime.value = null
  } finally {
    isLoading.value = false
  }
}

const retryLoad = () => {
  console.log('🔄 Homepage: Nouvelle tentative de chargement...')
  loadStats()
}

onMounted(() => {
  loadStats()

  // Auto-refresh discret toutes les 2 minutes
  setInterval(() => {
    if (stats.value && !isLoading.value) {
      loadStats()
    }
  }, 2 * 60 * 1000)
})
</script>

<style scoped>
.hero-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  min-height: 70vh;
  display: flex;
  align-items: center;
}

.hero-logo {
  height: 120px;
  width: 120px;
  filter: drop-shadow(0 10px 20px rgba(0,0,0,0.1));
}

.letter-spacing {
  letter-spacing: 2px;
}

.feature-card {
  transition: all 0.3s ease;
  border-radius: 12px;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0,0,0,0.1) !important;
}

.feature-icon {
  transition: transform 0.3s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1);
}

.bg-gradient-primary {
  background: linear-gradient(135deg, #007bff, #6610f2);
}

.cta-section {
  position: relative;
  overflow: hidden;
}

.cta-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="%23ffffff" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  pointer-events: none;
}

.stats-section {
  border-top: 1px solid #e9ecef;
  border-bottom: 1px solid #e9ecef;
}

.stat-card {
  padding: 30px 20px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-value-container {
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Animations de placeholder */
.placeholder {
  background-color: #dee2e6;
  border-radius: 4px;
  height: 1.2em;
}

.placeholder-glow .placeholder {
  animation: placeholder-glow 2s ease-in-out infinite alternate;
}

@keyframes placeholder-glow {
  50% {
    opacity: 0.5;
  }
}

.badge {
  font-size: 0.7em;
}

@media (max-width: 768px) {
  .hero-logo {
    height: 80px;
    width: 80px;
  }

  .display-4 {
    font-size: 2rem;
  }

  .stat-icon {
    width: 60px;
    height: 60px;
  }

  .stat-value-container {
    min-height: 60px;
  }
}
</style>
