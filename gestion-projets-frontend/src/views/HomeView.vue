
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

    <!-- Call to Action -->
    <section class="cta-section py-5 bg-gradient-primary text-white">
      <div class="container text-center">
        <div class="row justify-content-center">
          <div class="col-lg-8">
            <h3 class="h2 fw-bold mb-4">Prêt à commencer ?</h3>
            <p class="lead mb-4">
              Inscrivez-vous dès maintenant pour commencer à gérer vos projets collaboratifs.
            </p>
            <div class="d-flex justify-content-center gap-3 flex-wrap">
              <router-link to="/register" class="btn btn-light btn-lg text-primary px-4 py-3">
                <i class="fas fa-user-plus me-2"></i>
                S'inscrire
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

    <!-- Stats Section -->
    <section class="stats-section py-5 bg-white">
      <div class="container">
        <div class="row text-center g-4">
          <div class="col-lg-3 col-md-6">
            <div class="stat-item">
              <h3 class="display-4 fw-bold text-collabpro mb-2">{{ stats.totalUsers }}+</h3>
              <p class="text-muted mb-0">Utilisateurs actifs</p>
            </div>
          </div>
          <div class="col-lg-3 col-md-6">
            <div class="stat-item">
              <h3 class="display-4 fw-bold text-collabpro mb-2">{{ stats.totalProjects }}+</h3>
              <p class="text-muted mb-0">Projets créés</p>
            </div>
          </div>
          <div class="col-lg-3 col-md-6">
            <div class="stat-item">
              <h3 class="display-4 fw-bold text-collabpro mb-2">{{ stats.totalTasks }}+</h3>
              <p class="text-muted mb-0">Tâches terminées</p>
            </div>
          </div>
          <div class="col-lg-3 col-md-6">
            <div class="stat-item">
              <h3 class="display-4 fw-bold text-collabpro mb-2">99%</h3>
              <p class="text-muted mb-0">Satisfaction client</p>
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
              &copy; 2025 CollabPro
            </p>
          </div>
          <div class="col-md-6 text-md-end">
            <p class="text-muted small mb-0">
              Développé par <strong>ElhadjSouleymaneBAH</strong>
            </p>
            <div class="mt-2">
              <router-link to="/projets-publics" class="text-muted text-decoration-none small">
                Consulter les projets publics
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

const stats = ref({
  totalUsers: 0,
  totalProjects: 0,
  totalTasks: 0
})

const scrollToFeatures = () => {
  const element = document.getElementById('features')
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

const loadStats = async () => {
  try {
    // Charger les vraies statistiques de votre base
    const [usersResponse, projectsResponse, tasksResponse] = await Promise.all([
      api.get('/utilisateurs'),
      api.get('/projets'),
      api.get('/taches')
    ])

    stats.value.totalUsers = usersResponse.data.length
    stats.value.totalProjects = projectsResponse.data.length
    stats.value.totalTasks = tasksResponse.data.length

  } catch (error) {
    console.error('Erreur chargement statistiques:', error)
    // Valeurs par défaut
    stats.value = { totalUsers: 100, totalProjects: 101, totalTasks: 100 }
  }
}

onMounted(() => {
  loadStats()
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
}

.stat-item {
  padding: 20px;
}

@media (max-width: 768px) {
  .hero-logo {
    height: 80px;
    width: 80px;
  }

  .display-4 {
    font-size: 2rem;
  }
}
</style>
