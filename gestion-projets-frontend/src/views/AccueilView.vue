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
              {{ $t('nav.connexion') }}
            </router-link>
            <router-link to="/inscription" class="btn btn-collabpro">
              {{ $t('nav.inscription') }}
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Sélecteur de langue -->
    <div class="lang-wrap mt-3 text-center">
      <select class="lang-select" @change="setLocale($event.target.value)">
        <option value="en">English</option>
        <option value="fr">Français</option>
      </select>
    </div>

    <!-- Hero -->
    <section class="hero-section py-5">
      <div class="container text-center py-5">
        <div class="mb-5">
          <img src="/logo-collabpro.png" alt="CollabPro" class="hero-logo mb-4">
          <h2 class="display-4 fw-bold text-collabpro mb-3">CollabPro</h2>
          <!-- Texte i18n EXACTEMENT comme l'image -->
          <p class="lead text-muted mb-4">
            {{ $t('home.hero.tagline') }}
          </p>
          <p class="lead text-muted mb-5 mx-auto" style="max-width: 600px;">
            {{ $t('home.hero.description') }}
          </p>
        </div>
        <div class="d-flex justify-content-center gap-3 flex-wrap">
          <router-link to="/inscription" class="btn btn-collabpro btn-lg px-4 py-3">
            <i class="fas fa-user-plus me-2"></i>
            {{ $t('auth.creerMonCompte') }}
          </router-link>
          <router-link to="/projets-publics" class="btn btn-outline-primary btn-lg px-4 py-3">
            <i class="fas fa-eye me-2"></i>
            {{ $t('nav.projetsPublics') }}
          </router-link>
        </div>
      </div>
    </section>

    <!-- Fonctionnalités -->
    <section class="py-5 bg-white">
      <div class="container">
        <div class="text-center mb-5">
          <h3 class="h2 fw-bold text-dark mb-3">{{ $t('commun.fonctionnalites') }}</h3>
        </div>
        <div class="row g-4">
          <!-- F1 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-user-plus fa-3x text-primary"></i></div>
              <h4 class="h5 fw-bold mb-3">{{ $t('inscription.titre') }}</h4>
              <p class="text-muted mb-0">{{ $t('inscription.description') }}</p>
            </div>
          </div>

          <!-- F2 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-sign-in-alt fa-3x text-success"></i></div>
              <h4 class="h5 fw-bold mb-3">{{ $t('connexion.titre') }}</h4>
              <p class="text-muted mb-0">{{ $t('connexion.sousTitre') }}</p>
            </div>
          </div>

          <!-- F3 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-globe fa-3x text-info"></i></div>
              <h4 class="h5 fw-bold mb-3">{{ $t('projetsPublics.titre') }}</h4>
              <p class="text-muted mb-0">{{ $t('projetsPublics.description') }}</p>
            </div>
          </div>

          <!-- F6 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-project-diagram fa-3x text-warning"></i></div>
              <h4 class="h5 fw-bold mb-3">{{ $t('projets.gestion') }}</h4>
              <p class="text-muted mb-0">{{ $t('projets.descriptionGestion') }}</p>
            </div>
          </div>

          <!-- F7 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-tasks fa-3x text-secondary"></i></div>
              <h4 class="h5 fw-bold mb-3">{{ $t('taches.gestion') }}</h4>
              <p class="text-muted mb-0">{{ $t('taches.descriptionGestion') }}</p>
            </div>
          </div>

          <!-- F8 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-users fa-3x text-primary"></i></div>
              <h4 class="h5 fw-bold mb-3">{{ $t('membres.ajout') }}</h4>
              <p class="text-muted mb-0">{{ $t('membres.descriptionAjout') }}</p>
            </div>
          </div>

          <!-- F9 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-comments fa-3x text-success"></i></div>
              <h4 class="h5 fw-bold mb-3">{{ $t('collaboration.tempsReel') }}</h4>
              <p class="text-muted mb-0">{{ $t('collaboration.descriptionTempsReel') }}</p>
            </div>
          </div>

          <!-- F10 -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-credit-card fa-3x text-warning"></i></div>
              <h4 class="h5 fw-bold mb-3">{{ $t('abonnement.paiements') }}</h4>
              <p class="text-muted mb-0">{{ $t('abonnement.descriptionPaiements') }}</p>
            </div>
          </div>

          <!-- F11 - CORRECTION AJUSTÉE -->
          <div class="col-lg-4 col-md-6">
            <div class="card h-100 border-0 shadow-sm text-center p-4">
              <div class="mb-4"><i class="fas fa-file-invoice fa-3x text-danger"></i></div>
              <h4 class="h5 fw-bold mb-3">{{ $t('factures.titre') }}</h4>
              <p class="text-muted mb-0">{{ $t('factures.descriptionPdf') }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA / Call to action -->
    <section class="py-5 bg-primary text-white">
      <div class="container text-center">
        <h2 class="h1 fw-bold mb-3">{{ $t('commun.pretCommencer') }}</h2>
        <p class="lead mb-4">
          {{ $t('commun.rejoindreCollabPro') }}
        </p>
        <!-- Message d’accroche + un seul bouton -->
        <p class="fw-semibold mb-4">
          {{ $t('commun.invitationUnique') }}
        </p>
        <div class="d-flex justify-content-center">
          <router-link to="/inscription" class="btn btn-light btn-lg text-primary px-4 py-3">
            <i class="fas fa-user-plus me-2"></i>
            {{ $t('auth.creerMonCompte') }}
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'AccueilViews',
  methods: {
    setLocale(locale) {
      this.$i18n.locale = locale;
      try {
        localStorage.setItem('locale', locale);
      } catch (error) {
        console.log('Erreur lors du stockage de la locale:', error);
      }
    }
  }
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
  color: #fff;
  font-weight: 600;
  transition: all .3s;
  box-shadow: 0 2px 8px rgba(0,123,255,.2);
}

.btn-collabpro:hover {
  background: linear-gradient(135deg, #0056b3, #004085);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,123,255,.3);
  color:#fff;
}

.text-collabpro {
  color:#007bff;
}

.card {
  transition: all .3s ease;
  border-radius: 12px;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0,0,0,.1) !important;
}

/* Sélecteur de langue */
.lang-wrap {
  color:#fff;
}

.lang-select{
  background: transparent;
  color:#fff;
  border:1px solid rgba(255,255,255,.6);
  padding:.2rem .5rem;
}

.lang-select option {
  color:#000;
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
    padding: .75rem 1.5rem;
    font-size: 1rem;
  }
}
</style>
