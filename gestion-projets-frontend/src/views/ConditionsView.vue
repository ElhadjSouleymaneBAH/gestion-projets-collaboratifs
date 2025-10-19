<!-- src/views/ConditionsView.vue -->
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

    <!-- Contenu principal -->
    <main class="container py-5">
      <div class="row justify-content-center">
        <div class="col-lg-8">
          <!-- Titre et introduction -->
          <div class="bg-white rounded-3 shadow-sm p-5 mb-4">
            <div class="text-center mb-4">
              <h1 class="h2 fw-bold text-collabpro mb-3">
                <i class="fas fa-file-contract me-3"></i>
                {{ $t('conditions.titre') }}
              </h1>
              <p class="lead text-muted">
                {{ $t('conditions.conditionsUsage') }}
              </p>
              <p class="small text-muted">
                <i class="fas fa-calendar-alt me-2"></i>
                {{ $t('conditions.documentMisAJour') }}
              </p>
            </div>
          </div>

          <!-- Section 1 : Introduction -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-handshake text-primary me-2"></i>
              1. {{ $t('conditions.introduction') }}
            </h2>
            <p class="text-muted">
              {{ $t('conditions.introductionText') }}
            </p>
            <div class="alert alert-info border-0 mt-3">
              <i class="fas fa-info-circle me-2"></i>
              <strong>{{ $t('conditions.important') }} :</strong>
              {{ $t('conditions.importantText') }}
            </div>
          </div>

          <!-- Section 2 : Objet -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-bullseye text-success me-2"></i>
              2. {{ $t('conditions.objetApplication') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('conditions.objetText') }}
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-project-diagram me-1"></i>
                      {{ $t('conditions.gestionProjets') }}
                    </h5>
                    <p class="small text-muted mb-0">{{ $t('conditions.gestionProjetsDesc') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-users me-1"></i>
                      {{ $t('conditions.collaborationTempsReel') }}
                    </h5>
                    <p class="small text-muted mb-0">{{ $t('conditions.collaborationDesc') }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 3 : Inscription -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-user-plus text-warning me-2"></i>
              3. {{ $t('conditions.inscriptionComptes') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('conditions.inscriptionText') }}
            </p>

            <div class="card border-0 bg-warning bg-opacity-10">
              <div class="card-body">
                <h5 class="h6 fw-bold text-warning mb-2">
                  <i class="fas fa-shield-alt me-1"></i>
                  {{ $t('conditions.responsabiliteCompte') }}
                </h5>
                <ul class="small text-muted mb-0">
                  <li>{{ $t('conditions.informationsExactes') }}</li>
                  <li>{{ $t('conditions.confidentialiteIdentifiants') }}</li>
                  <li>{{ $t('conditions.notificationUsage') }}</li>
                </ul>
              </div>
            </div>
          </div>

          <!-- Section 4 : Utilisation -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-gavel text-info me-2"></i>
              4. {{ $t('conditions.utilisationApplication') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('conditions.utilisationText') }}
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-success bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-check me-1"></i>
                      {{ $t('conditions.usagesAutorises') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('conditions.gestionProjetsPro') }}</li>
                      <li>{{ $t('conditions.collaborationEquipe') }}</li>
                      <li>{{ $t('conditions.communicationOutils') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-danger bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-danger mb-2">
                      <i class="fas fa-times me-1"></i>
                      {{ $t('conditions.usagesInterdits') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('conditions.contenuIllegal') }}</li>
                      <li>{{ $t('conditions.tentativesPiratage') }}</li>
                      <li>{{ $t('conditions.spamSollicitations') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 5 : Paiements -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-credit-card text-primary me-2"></i>
              5. {{ $t('conditions.paiementsTransactions') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('conditions.paiementsText') }}
            </p>

            <div class="alert alert-primary border-0">
              <h5 class="h6 fw-bold mb-2">
                <i class="fab fa-stripe me-2"></i>
                {{ $t('conditions.securiteStripe') }}
              </h5>
              <p class="small mb-0">
                {{ $t('conditions.securiteStripeDesc') }}
              </p>
            </div>

            <div class="table-responsive mt-3">
              <table class="table table-sm">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('conditions.typeAbonnement') }}</th>
                  <th>{{ $t('admin.prix') }}</th>
                  <th>{{ $t('abonnement.fonctionnalites') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td><strong>{{ $t('roles.chefProjet') }}</strong></td>
                  <td>{{ $t('abonnement.prix') }}</td>
                  <td>{{ $t('conditions.creationGestion') }}</td>
                </tr>
                <tr>
                  <td><strong>{{ $t('roles.membre') }}</strong></td>
                  <td>{{ $t('commun.gratuit') || 'Gratuit' }}</td>
                  <td>{{ $t('conditions.participationProjets') }}</td>
                </tr>
                <tr>
                  <td><strong>{{ $t('roles.visiteur') }}</strong></td>
                  <td>{{ $t('commun.gratuit') || 'Gratuit' }}</td>
                  <td>{{ $t('conditions.consultationPublics') }}</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Section 6 : Données -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-database text-success me-2"></i>
              6. {{ $t('conditions.protectionDonnees') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('conditions.protectionText') }}
            </p>

            <div class="alert alert-info border-0">
              <h5 class="h6 fw-bold mb-2">
                <i class="fas fa-shield-alt me-2"></i>
                {{ $t('conditions.referenceRGPD') }}
              </h5>
              <p class="small mb-0">
                {{ $t('conditions.referenceRGPDText') }}
                <router-link to="/politique-confidentialite" class="text-decoration-none fw-bold">
                  {{ $t('auth.politiqueConfidentialite') }}
                </router-link>.
              </p>
            </div>
          </div>

          <!-- Section 7 : Responsabilités -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-balance-scale text-warning me-2"></i>
              7. {{ $t('conditions.responsabilites') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('conditions.responsabilitesText') }}
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-handshake me-1"></i>
                      {{ $t('conditions.nosEngagements') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('conditions.disponibiliteOptimale') }}</li>
                      <li>{{ $t('conditions.securiteDonnees') }}</li>
                      <li>{{ $t('conditions.supportTechnique') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-secondary mb-2">
                      <i class="fas fa-exclamation-triangle me-1"></i>
                      {{ $t('conditions.limitesResponsabilite') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('conditions.interruptionsTechniques') }}</li>
                      <li>{{ $t('conditions.maintenanceProgrammee') }}</li>
                      <li>{{ $t('conditions.forceMajeure') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 8 : Modifications -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-edit text-info me-2"></i>
              8. {{ $t('conditions.modificationsCGU') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('conditions.modificationsText') }}
            </p>

            <div class="alert alert-warning border-0">
              <i class="fas fa-bell me-2"></i>
              <strong>{{ $t('conditions.notificationChangements') }} :</strong>
              {{ $t('conditions.notificationDesc') }}
            </div>
          </div>

          <!-- Section 9 : Loi applicable -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-globe-europe text-primary me-2"></i>
              9. {{ $t('conditions.loiApplicable') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('conditions.loiText') }}
            </p>

            <div class="card border-0 bg-primary bg-opacity-10">
              <div class="card-body">
                <h5 class="h6 fw-bold text-primary mb-2">
                  <i class="fas fa-gavel me-1"></i>
                  {{ $t('conditions.juridictionCompetente') }}
                </h5>
                <p class="small text-muted mb-0">
                  {{ $t('conditions.juridictionDesc') }}
                </p>
              </div>
            </div>
          </div>

          <!-- Contact et actions -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-envelope text-success me-2"></i>
              10. {{ $t('conditions.contact') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('conditions.contactText') }}
              <strong>{{ $t('conditions.supportEmail') }}</strong>.
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body text-center">
                    <i class="fas fa-headset fa-2x text-primary mb-2"></i>
                    <h5 class="h6 fw-bold">{{ $t('conditions.supportTechnique') }}</h5>
                    <p class="small text-muted">{{ $t('conditions.supportEmail') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body text-center">
                    <i class="fas fa-shield-alt fa-2x text-success mb-2"></i>
                    <h5 class="h6 fw-bold">{{ $t('conditions.protectionDonnees') }}</h5>
                    <p class="small text-muted">{{ $t('conditions.protectionEmail') }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Navigation et footer local -->
          <div class="bg-white rounded-3 shadow-sm p-4 text-center">
            <div class="d-flex flex-column flex-md-row justify-content-center gap-3 mb-4">
              <router-link to="/politique-confidentialite" class="btn btn-outline-primary">
                <i class="fas fa-shield-alt me-2"></i>
                {{ $t('auth.politiqueConfidentialite') }}
              </router-link>
              <router-link to="/inscription" class="btn btn-collabpro">
                <i class="fas fa-user-plus me-2"></i>
                {{ $t('conditions.creerCompteBtn') }}
              </router-link>
              <router-link to="/" class="btn btn-outline-secondary">
                <i class="fas fa-home me-2"></i>
                {{ $t('conditions.retourAccueilBtn') }}
              </router-link>
            </div>

            <hr class="my-4">

            <p class="small text-muted mb-0">
              <i class="fas fa-balance-scale me-2"></i>
              {{ $t('conditions.adapteProjets') }}
            </p>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'ConditionsView',
  methods: {
    setLocale(locale) {
      this.$i18n.locale = locale
      try { localStorage.setItem('locale', locale) } catch (e) {}
    }
  },
  mounted() {
    window.scrollTo(0, 0)
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

.card { transition: all .3s ease; border-radius: 8px; }
.card:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(0,0,0,.1) !important; }

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

/* Animation */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}
.bg-white { animation: fadeInUp 0.6s ease-out; }

/* Responsive */
@media (max-width: 768px) {
  .container { padding-left: 1rem; padding-right: 1rem; }
  .col-lg-8 { padding-left: .5rem; padding-right: .5rem; }
  .table-responsive { font-size: .875rem; }
}
</style>
