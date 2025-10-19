
<template>
  <div class="min-vh-100 bg-light">
    <!-- Header -->
    <header class="bg-white shadow-sm py-3">
      <div class="container">
        <div class="d-flex justify-content-between align-items-center">
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

    <!-- Cancel Section -->
    <main class="container py-5">
      <div class="row justify-content-center">
        <div class="col-lg-8 col-xl-6">

          <!-- Animation d'annulation -->
          <div class="text-center mb-5">
            <div class="cancel-animation mb-4">
              <div class="cancel-icon">
                <div class="cancel-x">
                  <span class="x-line x-line-1"></span>
                  <span class="x-line x-line-2"></span>
                  <div class="cancel-circle"></div>
                </div>
              </div>
            </div>

            <h1 class="h2 fw-bold text-warning mb-3">
              <i class="fas fa-exclamation-triangle me-2"></i>
              {{ $t('stripe.cancel.titre') }}
            </h1>

            <p class="lead text-muted mb-4">
              {{ $t('stripe.cancel.message', { prenom: utilisateur.prenom || '' }) }}
            </p>

            <div class="alert alert-warning border-0 shadow-sm">
              <div class="d-flex align-items-center">
                <i class="fas fa-info-circle fa-2x me-3"></i>
                <div class="text-start">
                  <h6 class="fw-bold mb-1">{{ $t('stripe.cancel.aucunDebit') }}</h6>
                  <small class="text-muted">
                    {{ $t('stripe.cancel.resteCompte') }}
                  </small>
                </div>
              </div>
            </div>
          </div>

          <!-- Raisons possibles -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h3 class="h5 fw-bold text-dark mb-3">
              <i class="fas fa-question-circle me-2"></i>
              {{ $t('stripe.cancel.pourquoiAnnule') }}
            </h3>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="reason-item d-flex align-items-start p-3 bg-light rounded">
                  <div class="reason-icon bg-info bg-opacity-10 rounded me-3 mt-1">
                    <i class="fas fa-user text-info"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1">{{ $t('stripe.cancel.decisionPersonnelle') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.decisionPersonnelleDesc') }}</small>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="reason-item d-flex align-items-start p-3 bg-light rounded">
                  <div class="reason-icon bg-warning bg-opacity-10 rounded me-3 mt-1">
                    <i class="fas fa-credit-card text-warning"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1">{{ $t('stripe.cancel.problemeCarte') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.problemeCarteDesc') }}</small>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="reason-item d-flex align-items-start p-3 bg-light rounded">
                  <div class="reason-icon bg-secondary bg-opacity-10 rounded me-3 mt-1">
                    <i class="fas fa-wifi text-secondary"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1">{{ $t('stripe.cancel.problemeTechnique') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.problemeTechniqueDesc') }}</small>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="reason-item d-flex align-items-start p-3 bg-light rounded">
                  <div class="reason-icon bg-primary bg-opacity-10 rounded me-3 mt-1">
                    <i class="fas fa-clock text-primary"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1">{{ $t('stripe.cancel.reflexionNecessaire') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.reflexionNecessaireDesc') }}</small>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Fonctionnalités actuelles (Membre gratuit) -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h3 class="h5 fw-bold text-dark mb-3">
              <i class="fas fa-user me-2"></i>
              {{ $t('stripe.cancel.fonctionnalitesActuelles') }}
            </h3>

            <div class="row g-3">
              <div class="col-sm-6">
                <div class="feature-current d-flex align-items-center p-2">
                  <div class="feature-icon bg-success bg-opacity-10 rounded me-3">
                    <i class="fas fa-check text-success"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1 small">{{ $t('stripe.cancel.participationProjets') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.rejoindreEquipes') }}</small>
                  </div>
                </div>
              </div>

              <div class="col-sm-6">
                <div class="feature-current d-flex align-items-center p-2">
                  <div class="feature-icon bg-success bg-opacity-10 rounded me-3">
                    <i class="fas fa-check text-success"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1 small">{{ $t('stripe.cancel.collaborationTempsReel') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.chatEquipe') }}</small>
                  </div>
                </div>
              </div>

              <div class="col-sm-6">
                <div class="feature-current d-flex align-items-center p-2">
                  <div class="feature-icon bg-success bg-opacity-10 rounded me-3">
                    <i class="fas fa-check text-success"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1 small">{{ $t('stripe.cancel.tachesAssignees') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.gererTaches') }}</small>
                  </div>
                </div>
              </div>

              <div class="col-sm-6">
                <div class="feature-current d-flex align-items-center p-2">
                  <div class="feature-icon bg-success bg-opacity-10 rounded me-3">
                    <i class="fas fa-check text-success"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1 small">{{ $t('stripe.cancel.projetsPublics') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.decouvrirProjets') }}</small>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Fonctionnalités Premium manquées -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h3 class="h5 fw-bold text-dark mb-3">
              <i class="fas fa-crown me-2"></i>
              {{ $t('stripe.cancel.fonctionnalitesPremiumManquees') }}
            </h3>

            <div class="row g-3">
              <div class="col-sm-6">
                <div class="feature-premium d-flex align-items-center p-2">
                  <div class="feature-icon bg-warning bg-opacity-10 rounded me-3">
                    <i class="fas fa-lock text-warning"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1 small">{{ $t('stripe.cancel.projetsIllimites') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.creerProjetsProres') }}</small>
                  </div>
                </div>
              </div>

              <div class="col-sm-6">
                <div class="feature-premium d-flex align-items-center p-2">
                  <div class="feature-icon bg-warning bg-opacity-10 rounded me-3">
                    <i class="fas fa-lock text-warning"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1 small">{{ $t('stripe.cancel.gestionEquipe') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.ajouterProjets') }}</small>
                  </div>
                </div>
              </div>

              <div class="col-sm-6">
                <div class="feature-premium d-flex align-items-center p-2">
                  <div class="feature-icon bg-warning bg-opacity-10 rounded me-3">
                    <i class="fas fa-lock text-warning"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1 small">{{ $t('stripe.cancel.assignationTaches') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.creerAssigner') }}</small>
                  </div>
                </div>
              </div>

              <div class="col-sm-6">
                <div class="feature-premium d-flex align-items-center p-2">
                  <div class="feature-icon bg-warning bg-opacity-10 rounded me-3">
                    <i class="fas fa-lock text-warning"></i>
                  </div>
                  <div>
                    <h6 class="fw-bold mb-1 small">{{ $t('stripe.cancel.facturesPDF') }}</h6>
                    <small class="text-muted">{{ $t('stripe.cancel.generationAutomatique') }}</small>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Offre spéciale de récupération -->
          <div class="bg-gradient-warning rounded-3 p-4 text-white mb-4">
            <div class="text-center">
              <h3 class="h5 fw-bold mb-3">
                <i class="fas fa-gift me-2"></i>
                {{ $t('stripe.cancel.offreRecuperation') }}
              </h3>

              <p class="mb-3">
                {{ $t('stripe.cancel.repenserDecision') }}
                <strong>{{ $t('stripe.cancel.essaiGratuit') }}</strong>
                {{ $t('stripe.cancel.avantFacturation') }}
              </p>

              <div class="pricing-box bg-white bg-opacity-20 rounded p-3 mb-3">
                <div class="h4 fw-bold mb-1">{{ $t('stripe.cancel.montantTotal') }}</div>
                <small>{{ $t('stripe.cancel.apresEssai') }}</small>
              </div>

              <div class="d-flex justify-content-center gap-2 mb-2">
                <span class="badge bg-white text-dark">✓ {{ $t('stripe.cancel.annulerToutMoment') }}</span>
                <span class="badge bg-white text-dark">✓ {{ $t('stripe.cancel.pasFraisCaches') }}</span>
              </div>
            </div>
          </div>

          <!-- Actions principales -->
          <div class="text-center">
            <div class="d-flex flex-column flex-md-row justify-content-center gap-3 mb-4">
              <router-link to="/abonnement" class="btn btn-collabpro btn-lg px-4">
                <i class="fas fa-crown me-2"></i>
                {{ $t('stripe.cancel.essayerPremium') }}
              </router-link>
              <router-link to="/tableau-bord" class="btn btn-outline-primary btn-lg px-4">
                <i class="fas fa-tachometer-alt me-2"></i>
                {{ $t('stripe.cancel.tableauBord') }}
              </router-link>
            </div>

            <div class="d-flex flex-column flex-sm-row justify-content-center gap-2 mb-4">
              <router-link to="/projets-publics" class="btn btn-outline-secondary">
                <i class="fas fa-eye me-1"></i>
                {{ $t('stripe.cancel.projetsPublics') }}
              </router-link>
              <router-link to="/mon-profil" class="btn btn-outline-secondary">
                <i class="fas fa-user me-1"></i>
                {{ $t('stripe.cancel.monProfil') }}
              </router-link>
              <router-link to="/" class="btn btn-outline-secondary">
                <i class="fas fa-home me-1"></i>
                {{ $t('stripe.cancel.accueil') }}
              </router-link>
            </div>

            <div class="pt-3 border-top">
              <p class="text-muted mb-2">
                <i class="fas fa-question-circle me-1"></i>
                {{ $t('stripe.cancel.questionsAbonnement') }}
              </p>
              <p class="text-muted small mb-0">
                {{ $t('stripe.cancel.contactezNous') }}
                <strong>{{ $t('stripe.cancel.supportEmail') }}</strong>
                <br>
                {{ $t('stripe.cancel.laPouraider') }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="bg-dark text-white py-3 mt-5">
      <div class="container">
        <div class="d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
          <router-link to="/" class="d-inline-flex align-items-center text-decoration-none text-white">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 32px; width: 32px;">
            <span class="fw-bold">CollabPro</span>
          </router-link>

          <div class="text-center">
            <router-link to="/conditions" class="text-white text-decoration-underline small me-3">
              {{ $t('conditions.titre') }}
            </router-link>
            <router-link to="/politique-confidentialite" class="text-white text-decoration-underline small me-3">
              {{ $t('auth.politiqueConfidentialite') }}
            </router-link>
            <span class="small text-white-50">© 2025 {{ $t('commun.droitsReserves') }}</span>
          </div>

          <div class="d-flex align-items-center">
            <div class="d-flex align-items-center lang-wrap">
              <i class="fas fa-globe me-2"></i>
              <select
                class="form-select form-select-sm lang-select"
                :value="$i18n.locale"
                @change="setLocale($event.target.value)"
                aria-label="Sélection de la langue"
              >
                <option value="fr">{{ $t('auth.francais') }}</option>
                <option value="en">{{ $t('auth.english') }}</option>
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
  name: 'StripeCancelView',
  data() {
    return {
      utilisateur: {}
    }
  },
  methods: {
    setLocale(locale) {
      this.$i18n.locale = locale
      try { localStorage.setItem('locale', locale) } catch (e) {}
    }
  },
  mounted() {
    window.scrollTo(0, 0)

    // Récupérer les informations utilisateur
    this.utilisateur = JSON.parse(localStorage.getItem('user')) || {}

    // Animation d'apparition des éléments
    setTimeout(() => {
      const elements = document.querySelectorAll('.reason-item, .feature-current, .feature-premium')
      elements.forEach((el, index) => {
        setTimeout(() => {
          el.classList.add('animate-in')
        }, index * 80)
      })
    }, 300)
  }
}
</script>

<style scoped>
.text-collabpro { color: #007bff; }

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

.bg-gradient-warning {
  background: linear-gradient(135deg, #ffc107, #ff9800);
}

/* Cancel Animation */
.cancel-animation {
  display: inline-block;
}

.cancel-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #dc3545;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  position: relative;
  animation: shake 0.6s ease-in-out;
}

.cancel-x {
  position: relative;
  width: 40px;
  height: 40px;
}

.x-line {
  position: absolute;
  width: 30px;
  height: 3px;
  background: white;
  border-radius: 2px;
  top: 50%;
  left: 50%;
}

.x-line-1 {
  transform: translate(-50%, -50%) rotate(45deg);
  animation: x-line-1 0.5s ease-out 0.2s both;
}

.x-line-2 {
  transform: translate(-50%, -50%) rotate(-45deg);
  animation: x-line-2 0.5s ease-out 0.4s both;
}

.cancel-circle {
  position: absolute;
  top: -4px;
  left: -4px;
  width: 80px;
  height: 80px;
  border: 4px solid rgba(220, 53, 69, 0.3);
  border-radius: 50%;
  animation: cancel-circle 0.6s ease-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

@keyframes x-line-1 {
  0% { width: 0; opacity: 0; }
  100% { width: 30px; opacity: 1; }
}

@keyframes x-line-2 {
  0% { width: 0; opacity: 0; }
  100% { width: 30px; opacity: 1; }
}

@keyframes cancel-circle {
  0% { transform: scale(0.8); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

/* Feature items */
.feature-icon, .reason-icon {
  width: 40px; height: 40px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 8px;
}

.reason-item, .feature-current, .feature-premium {
  transition: all 0.3s ease;
  border-radius: 8px;
  opacity: 0;
  transform: translateY(10px);
}

.reason-item.animate-in, .feature-current.animate-in, .feature-premium.animate-in {
  opacity: 1;
  transform: translateY(0);
}

.reason-item:hover, .feature-current:hover {
  background: rgba(0,123,255,0.05) !important;
  transform: translateX(3px);
}

.feature-premium:hover {
  background: rgba(255,193,7,0.1) !important;
  transform: translateX(3px);
}

.pricing-box {
  backdrop-filter: blur(10px);
}

/* Lang switch */
.lang-wrap { color:#fff; }
.lang-select{
  background: transparent;
  color:#fff;
  border:1px solid rgba(255,255,255,.6);
  padding:.2rem .5rem;
  font-size: 0.875rem;
}
.lang-select option { color:#000; }

/* Responsive */
@media (max-width: 768px) {
  .cancel-icon { width: 60px; height: 60px; }
  .x-line { width: 20px; }
  .cancel-circle { width: 60px; height: 60px; top: -4px; left: -4px; }
  .container { padding-left: 1rem; padding-right: 1rem; }
  .feature-current, .feature-premium, .reason-item { margin-bottom: 0.5rem; }
}
</style>
