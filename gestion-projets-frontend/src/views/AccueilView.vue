<template>
  <div class="min-vh-100 bg-light">
    <!-- Header -->
    <header class="bg-white shadow-sm py-3">
      <div class="container">
        <div class="d-flex justify-content-between align-items-center">
          <!-- Logo : reste sur la Home -->
          <router-link to="/" class="d-flex align-items-center text-decoration-none text-dark">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-3" style="height: 48px; width: 48px;">
            <h1 class="h4 fw-bold text-collabpro mb-0">CollabPro</h1>
          </router-link>

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

    <!-- Hero -->
    <section class="hero-section py-5">
      <div class="container text-center py-5">
        <div class="mb-5">
          <img src="/logo-collabpro.png" alt="CollabPro" class="hero-logo mb-4">
          <h2 class="display-4 fw-bold text-collabpro mb-3">CollabPro</h2>
          <p class="lead text-muted mb-4">Application Web de Gestion de Projets Collaboratifs</p>
        </div>

        <p class="lead text-muted mb-5 mx-auto" style="max-width: 600px;">
          Plateforme intuitive pour les PME et les freelances, permettant la création et la gestion
          de projets ainsi que l'attribution de tâches avec intégration d'outils de collaboration en temps réel.
        </p>

        <div class="d-flex justify-content-center gap-3 flex-wrap">
          <router-link to="/inscription" class="btn btn-collabpro btn-lg px-4 py-3">
            <i class="fas fa-user-plus me-2"></i> S'inscrire
          </router-link>
          <router-link to="/projets-publics" class="btn btn-outline-primary btn-lg px-4 py-3">
            <i class="fas fa-eye me-2"></i> Consulter les projets publics
          </router-link>
        </div>
      </div>
    </section>

    <!-- Fonctionnalités -->
    <section class="py-5 bg-white">
      <div class="container">
        <div class="text-center mb-5">
          <h3 class="h2 fw-bold text-dark mb-3">Fonctionnalités</h3>
        </div>

        <div class="row g-4">
          <!-- F1 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-user-plus fa-3x text-primary"></i></div>
              <h4 class="h5 fw-bold mb-3">S'inscrire</h4>
              <p class="text-muted mb-0">Créer un compte utilisateur avec nom, prénom, email et mot de passe.</p>
            </div>
          </div>
          <!-- F2 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-sign-in-alt fa-3x text-success"></i></div>
              <h4 class="h5 fw-bold mb-3">Se connecter</h4>
              <p class="text-muted mb-0">Authentification sécurisée avec email et mot de passe.</p>
            </div>
          </div>
          <!-- F3 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-globe fa-3x text-info"></i></div>
              <h4 class="h5 fw-bold mb-3">Projets publics</h4>
              <p class="text-muted mb-0">Consulter la liste des projets publics même sans compte.</p>
            </div>
          </div>
          <!-- F6 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-project-diagram fa-3x text-warning"></i></div>
              <h4 class="h5 fw-bold mb-3">Gérer les projets</h4>
              <p class="text-muted mb-0">Créer, modifier ou supprimer des projets (Chef de Projet uniquement).</p>
            </div>
          </div>
          <!-- F7 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-tasks fa-3x text-secondary"></i></div>
              <h4 class="h5 fw-bold mb-3">Gérer les tâches</h4>
              <p class="text-muted mb-0">Créer, assigner ou modifier des tâches au sein des projets.</p>
            </div>
          </div>
          <!-- F8 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-users fa-3x text-primary"></i></div>
              <h4 class="h5 fw-bold mb-3">Ajouter des membres</h4>
              <p class="text-muted mb-0">Inviter et gérer les membres des projets (Chef de Projet).</p>
            </div>
          </div>
          <!-- F9 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-comments fa-3x text-success"></i></div>
              <h4 class="h5 fw-bold mb-3">Chat temps réel</h4>
              <p class="text-muted mb-0">Discussion instantanée dans les projets pour les membres connectés.</p>
            </div>
          </div>
          <!-- F10 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-credit-card fa-3x text-warning"></i></div>
              <h4 class="h5 fw-bold mb-3">Paiements Stripe</h4>
              <p class="text-muted mb-0">Souscrire à un abonnement mensuel sécurisé via Stripe.</p>
            </div>
          </div>
          <!-- F11 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-file-invoice fa-3x text-danger"></i></div>
              <h4 class="h5 fw-bold mb-3">Factures PDF</h4>
              <p class="text-muted mb-0">Génération automatique de facture PDF pour chaque paiement.</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA / Call to action -->
    <section class="py-5 bg-primary text-white">
      <div class="container text-center">
        <h2 class="h1 fw-bold mb-3">Prêt à commencer ?</h2>
        <p class="lead mb-4">
          Rejoignez CollabPro et commencez à gérer vos projets collaboratifs dès maintenant.
        </p>
        <div class="d-flex justify-content-center gap-3 flex-wrap">
          <router-link to="/inscription" class="btn btn-light btn-lg text-primary px-4 py-3">
            <i class="fas fa-user-plus me-2"></i> Créer un compte
          </router-link>
          <router-link to="/connexion" class="btn btn-outline-light btn-lg px-4 py-3">
            <i class="fas fa-sign-in-alt me-2"></i> Se connecter
          </router-link>
        </div>
      </div>
    </section>

    <!-- Footer mis à jour -->
    <footer class="bg-dark text-white py-3">
      <div class="container">
        <div class="d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
          <!-- Marque -->
          <router-link to="/" class="d-inline-flex align-items-center text-decoration-none text-white">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 32px; width: 32px;">
            <span class="fw-bold">CollabPro</span>
          </router-link>

          <!-- Conditions + Politique + Copyright -->
          <div class="text-center">
            <router-link to="/conditions" class="text-white text-decoration-underline small me-3">
              Conditions d'utilisation
            </router-link>
            <router-link to="/politique-confidentialite" class="text-white text-decoration-underline small me-3">
              Politique de confidentialité
            </router-link>
            <span class="small text-white-50">© 2025 Tous droits réservés</span>
          </div>

          <!-- Langue uniquement (réseaux sociaux supprimés) -->
          <div class="d-flex align-items-center">
            <div class="d-flex align-items-center lang-wrap">
              <i class="fas fa-globe me-2"></i>
              <select
                class="form-select form-select-sm lang-select"
                :value="$i18n.locale"
                @change="setLocale($event.target.value)"
                aria-label="Sélection de la langue"
              >
                <option value="fr">Français</option>
                <option value="en">English</option>
              </select>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
  methods: {
    setLocale(locale) {
      // Bascule la langue (tu as déjà l'i18n en place)
      this.$i18n.locale = locale;
      try { localStorage.setItem('locale', locale); } catch (e) {}
    }
  }
}
</script>

<style scoped>
.hero-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  min-height: 70vh; display: flex; align-items: center;
}
.hero-logo { height: 120px; width: 120px; filter: drop-shadow(0 10px 20px rgba(0,0,0,0.1)); }

.btn-collabpro {
  background: linear-gradient(135deg, #007bff, #0056b3);
  border: none; color: #fff; font-weight: 600; transition: all .3s;
  box-shadow: 0 2px 8px rgba(0,123,255,.2);
}
.btn-collabpro:hover {
  background: linear-gradient(135deg, #0056b3, #004085);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,123,255,.3);
  color:#fff;
}
.text-collabpro { color:#007bff; }

.card { transition: all .3s ease; border-radius: 12px; }
.card:hover { transform: translateY(-5px); box-shadow: 0 15px 35px rgba(0,0,0,.1) !important; }

/* Sélecteur de langue pour s'intégrer visuellement au footer sombre */
.lang-wrap { color:#fff; }
.lang-select{
  background: transparent;
  color:#fff;
  border:1px solid rgba(255,255,255,.6);
  padding:.2rem .5rem;
}
.lang-select option { color:#000; } /* options en fond clair par défaut */

@media (max-width: 768px) {
  .hero-logo { height: 80px; width: 80px; }
  .display-4 { font-size: 2rem; }
  .btn-lg { padding: .75rem 1.5rem; font-size: 1rem; }
}
</style>
