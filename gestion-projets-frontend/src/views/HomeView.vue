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
            <router-link to="/connexion" class="btn btn-link text-collabpro text-decoration-none">
              Connexion
            </router-link>
            <router-link to="/inscription" class="btn btn-collabpro">
              Inscription
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="hero-section py-5">
      <div class="container text-center py-5">
        <div class="mb-5">
          <img src="/logo-collabpro.png" alt="CollabPro" class="hero-logo mb-4">
          <h2 class="display-4 fw-bold text-collabpro mb-3">CollabPro</h2>
          <p class="lead text-muted mb-4">
            Application Web de Gestion de Projets Collaboratifs
          </p>
        </div>

        <p class="lead text-muted mb-5 mx-auto" style="max-width: 600px;">
          Plateforme intuitive pour les PME et les freelances, permettant la création et la gestion
          de projets ainsi que l'attribution de tâches avec intégration d'outils de collaboration en temps réel.
        </p>

        <div class="d-flex justify-content-center gap-3 flex-wrap">
          <router-link to="/inscription" class="btn btn-collabpro btn-lg px-4 py-3">
            <i class="fas fa-user-plus me-2"></i>
            S'inscrire
          </router-link>
          <router-link to="/projets-publics" class="btn btn-outline-primary btn-lg px-4 py-3">
            <i class="fas fa-eye me-2"></i>
            Consulter les projets publics
          </router-link>
        </div>
      </div>
    </section>

    <!-- Fonctionnalités Cahier des Charges -->
    <section class="py-5 bg-white">
      <div class="container">
        <div class="text-center mb-5">
          <h3 class="h2 fw-bold text-dark mb-3">Fonctionnalités</h3>
        </div>

        <div class="row g-4">
          <!-- F1: S'inscrire -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4">
                <i class="fas fa-user-plus fa-3x text-primary"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">S'inscrire</h4>
              <p class="text-muted mb-0">
                Créer un compte utilisateur avec nom, prénom, email et mot de passe.
              </p>
            </div>
          </div>

          <!-- F2: Se connecter -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4">
                <i class="fas fa-sign-in-alt fa-3x text-success"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Se connecter</h4>
              <p class="text-muted mb-0">
                Authentification sécurisée avec email et mot de passe.
              </p>
            </div>
          </div>

          <!-- F3: Consulter projets publics -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4">
                <i class="fas fa-globe fa-3x text-info"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Projets publics</h4>
              <p class="text-muted mb-0">
                Consulter la liste des projets publics même sans compte.
              </p>
            </div>
          </div>

          <!-- F6: Gérer les projets -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4">
                <i class="fas fa-project-diagram fa-3x text-warning"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Gérer les projets</h4>
              <p class="text-muted mb-0">
                Créer, modifier ou supprimer des projets (Chef de Projet uniquement).
              </p>
            </div>
          </div>

          <!-- F7: Gérer les tâches -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4">
                <i class="fas fa-tasks fa-3x text-secondary"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Gérer les tâches</h4>
              <p class="text-muted mb-0">
                Créer, assigner ou modifier des tâches au sein des projets.
              </p>
            </div>
          </div>

          <!-- F8: Ajouter des membres -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4">
                <i class="fas fa-users fa-3x text-primary"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Ajouter des membres</h4>
              <p class="text-muted mb-0">
                Inviter et gérer les membres des projets (Chef de Projet).
              </p>
            </div>
          </div>

          <!-- F9: Collaborer en temps réel -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4">
                <i class="fas fa-comments fa-3x text-success"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Chat temps réel</h4>
              <p class="text-muted mb-0">
                Discussion instantanée dans les projets pour les membres connectés.
              </p>
            </div>
          </div>

          <!-- F10: Paiements et abonnements -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4">
                <i class="fas fa-credit-card fa-3x text-warning"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Paiements Stripe</h4>
              <p class="text-muted mb-0">
                Souscrire à un abonnement mensuel sécurisé via Stripe.
              </p>
            </div>
          </div>

          <!-- F11: Générer les factures -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4">
                <i class="fas fa-file-invoice fa-3x text-danger"></i>
              </div>
              <h4 class="h5 fw-bold mb-3">Factures PDF</h4>
              <p class="text-muted mb-0">
                Génération automatique de facture PDF pour chaque paiement.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Call to Action -->
    <section class="py-5 bg-primary text-white">
      <div class="container text-center">
        <div class="row justify-content-center">
          <div class="col-lg-8">
            <h3 class="h2 fw-bold mb-4">Prêt à commencer ?</h3>
            <p class="lead mb-4">
              Rejoignez CollabPro et commencez à gérer vos projets collaboratifs dès maintenant.
            </p>
            <div class="d-flex justify-content-center gap-3 flex-wrap">
              <router-link to="/inscription" class="btn btn-light btn-lg text-primary px-4 py-3">
                <i class="fas fa-user-plus me-2"></i>
                Créer un compte
              </router-link>
              <router-link to="/connexion" class="btn btn-outline-light btn-lg px-4 py-3">
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
              &copy; 2025 CollabPro - Gestion de projets collaboratifs
            </p>
          </div>
          <div class="col-md-6 text-md-end">
            <p class="text-muted small mb-0">
              TFE par <strong>Elhadj Souleymane BAH</strong>
            </p>
            <div class="mt-2">
              <router-link to="/projets-publics" class="text-muted text-decoration-none small me-3">
                Projets publics
              </router-link>
              <router-link to="/inscription" class="text-muted text-decoration-none small">
                S'inscrire
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'HomeView'
}
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

.btn-collabpro {
  background: linear-gradient(135deg, #007bff, #0056b3);
  border: none;
  color: white;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.2);
}

.btn-collabpro:hover {
  background: linear-gradient(135deg, #0056b3, #004085);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
  color: white;
}

.text-collabpro {
  color: #007bff;
}

.card {
  transition: all 0.3s ease;
  border-radius: 12px;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0,0,0,0.1) !important;
}

@media (max-width: 768px) {
  .hero-logo {
    height: 80px;
    width: 80px;
  }

  .display-4 {
    font-size: 2rem;
  }

  .btn-lg {
    padding: 0.75rem 1.5rem;
    font-size: 1rem;
  }
}
</style>
