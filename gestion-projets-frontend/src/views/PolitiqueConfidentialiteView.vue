<template>
  <div class="min-vh-100 bg-light">
    <!-- Header -->
    <header class="bg-white shadow-sm py-3">
      <div class="container">
        <div class="d-flex justify-content-between align-items-center">
          <router-link to="/" class="d-flex align-items-center text-decoration-none text-dark">
            <img src="/logo-collabpro.png" :alt="$t('commun.nomApplication')" class="me-3" style="height: 48px; width: 48px;">
            <h1 class="h4 fw-bold text-collabpro mb-0">{{ $t('commun.nomApplication') }}</h1>
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
              <!-- Titre principal amélioré -->
              <div class="title-hero mb-4">
                <div class="d-inline-flex align-items-center bg-success bg-opacity-10 rounded-pill px-4 py-2 mb-3">
                  <i class="fas fa-shield-alt text-success me-2"></i>
                  <span class="badge bg-success rounded-pill">{{ $t('politique.rgpd') }}</span>
                </div>
                <h1 class="display-5 fw-bold text-dark mb-2">
                  {{ $t('politique.titre') }}
                </h1>
                <div class="title-underline mx-auto"></div>
              </div>

              <p class="lead text-muted">
                {{ $t('politique.sousTitre') }}
              </p>
              <p class="small text-muted">
                <i class="fas fa-calendar-alt me-2"></i>
                {{ $t('politique.derniereMiseAJour') }}
              </p>
            </div>

            <div class="alert alert-success border-0">
              <h5 class="h6 fw-bold mb-2">
                <i class="fas fa-info-circle me-2"></i>
                {{ $t('politique.conformiteRGPD') }}
              </h5>
              <p class="small mb-0">
                {{ $t('politique.conformiteRGPDDesc') }}
              </p>
            </div>
          </div>

          <!-- 1. Collecte des données -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-database text-primary me-2"></i>
              1. {{ $t('politique.donneesCollectees') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('politique.donneesCollecteesDesc') }}
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-user me-1"></i>
                      {{ $t('politique.donneesIdentification') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('inscription.prenom') }} {{ $t('inscription.nom') }}</li>
                      <li>{{ $t('inscription.email') }}</li>
                      <li>{{ $t('inscription.motDePasse') }} ({{ $t('politique.hache') }})</li>
                      <li>{{ $t('profil.langue') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-project-diagram me-1"></i>
                      {{ $t('politique.donneesUtilisation') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('projets.titre') }} {{ $t('politique.et') }} {{ $t('politique.contenus') }}</li>
                      <li>{{ $t('collaboration.chat') }} {{ $t('politique.et') }} {{ $t('politique.messages') }}</li>
                      <li>{{ $t('taches.titre') }} {{ $t('politique.et') }} {{ $t('commentaires.titre') }}</li>
                      <li>{{ $t('politique.logsConnexion') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-warning mb-2">
                      <i class="fas fa-credit-card me-1"></i>
                      {{ $t('politique.donneesPaiement') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('politique.informationsFacturation') }}</li>
                      <li>{{ $t('politique.historiqueTransactions') }}</li>
                      <li>{{ $t('politique.donneesStripe') }}</li>
                      <li>{{ $t('factures.titre') }} {{ $t('politique.generees') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-info mb-2">
                      <i class="fas fa-laptop me-1"></i>
                      {{ $t('politique.donneesTechniques') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('politique.adresseIP') }}</li>
                      <li>{{ $t('politique.navigateurAppareil') }}</li>
                      <li>{{ $t('politique.pagesVisitees') }}</li>
                      <li>{{ $t('politique.preferencesLangue') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 2. Finalités du traitement -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-bullseye text-warning me-2"></i>
              2. {{ $t('politique.finalitesTraitement') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('politique.finalitesTraitementDesc') }}
            </p>

            <div class="row g-3">
              <div class="col-12">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-cog me-2"></i>
                      {{ $t('politique.fournitureServices') }}
                    </h5>
                    <p class="text-muted mb-2">
                      {{ $t('politique.fournitureServicesDesc') }}
                    </p>
                    <p class="mb-0">
                      <strong>{{ $t('politique.baseLegale') }} :</strong>
                      <span class="text-muted">{{ $t('politique.executionContrat') }}</span>
                    </p>
                  </div>
                </div>
              </div>

              <div class="col-12">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-credit-card me-2"></i>
                      {{ $t('politique.gestionPaiements') }}
                    </h5>
                    <p class="text-muted mb-2">
                      {{ $t('politique.gestionPaiementsDesc') }}
                    </p>
                    <p class="mb-0">
                      <strong>{{ $t('politique.baseLegale') }} :</strong>
                      <span class="text-muted">{{ $t('politique.executionContratObligations') }}</span>
                    </p>
                  </div>
                </div>
              </div>

              <div class="col-12">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-info mb-2">
                      <i class="fas fa-bell me-2"></i>
                      {{ $t('politique.communicationNotifications') }}
                    </h5>
                    <p class="text-muted mb-2">
                      {{ $t('politique.communicationNotificationsDesc') }}
                    </p>
                    <p class="mb-0">
                      <strong>{{ $t('politique.baseLegale') }} :</strong>
                      <span class="text-muted">{{ $t('politique.interetLegitime') }}</span>
                    </p>
                  </div>
                </div>
              </div>

              <div class="col-12">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-warning mb-2">
                      <i class="fas fa-chart-line me-2"></i>
                      {{ $t('politique.ameliorationService') }}
                    </h5>
                    <p class="text-muted mb-2">
                      {{ $t('politique.ameliorationServiceDesc') }}
                    </p>
                    <p class="mb-0">
                      <strong>{{ $t('politique.baseLegale') }} :</strong>
                      <span class="text-muted">{{ $t('politique.interetLegitime') }}</span>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 3. Durée de conservation -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-clock text-info me-2"></i>
              3. {{ $t('politique.dureeConservation') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('politique.dureeConservationDesc') }}
            </p>

            <div class="table-responsive">
              <table class="table table-sm">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('politique.typeDonnees') }}</th>
                  <th>{{ $t('politique.dureeConservation') }}</th>
                  <th>{{ $t('politique.justification') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td><strong>{{ $t('politique.compteUtilisateur') }}</strong></td>
                  <td>{{ $t('politique.dureeCompte') }}</td>
                  <td>{{ $t('politique.gestionService') }}</td>
                </tr>
                <tr>
                  <td><strong>{{ $t('factures.titre') }}</strong></td>
                  <td>{{ $t('politique.dureeFactures') }}</td>
                  <td>{{ $t('politique.obligationsComptables') }}</td>
                </tr>
                <tr>
                  <td><strong>{{ $t('politique.donneesChat') }}</strong></td>
                  <td>{{ $t('politique.dureeChat') }}</td>
                  <td>{{ $t('politique.fonctionnaliteCollaborative') }}</td>
                </tr>
                <tr>
                  <td><strong>{{ $t('politique.donneesConnexion') }}</strong></td>
                  <td>{{ $t('politique.dureeConnexion') }}</td>
                  <td>{{ $t('politique.securiteAudit') }}</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- 4. Vos droits RGPD -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-user-shield text-success me-2"></i>
              4. {{ $t('politique.vosDroitsRGPD') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('politique.vosDroitsRGPDDesc') }}
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-success bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-eye me-1"></i>
                      {{ $t('politique.droitAcces') }}
                    </h5>
                    <p class="small text-muted mb-0">{{ $t('politique.droitAccesDesc') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-primary bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-edit me-1"></i>
                      {{ $t('politique.droitRectification') }}
                    </h5>
                    <p class="small text-muted mb-0">{{ $t('politique.droitRectificationDesc') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-danger bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-danger mb-2">
                      <i class="fas fa-trash me-1"></i>
                      {{ $t('politique.droitEffacement') }}
                    </h5>
                    <p class="small text-muted mb-0">{{ $t('politique.droitEffacementDesc') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-warning bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-warning mb-2">
                      <i class="fas fa-download me-1"></i>
                      {{ $t('politique.droitPortabilite') }}
                    </h5>
                    <p class="small text-muted mb-0">{{ $t('politique.droitPortabiliteDesc') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-info bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-info mb-2">
                      <i class="fas fa-pause me-1"></i>
                      {{ $t('politique.droitLimitation') }}
                    </h5>
                    <p class="small text-muted mb-0">{{ $t('politique.droitLimitationDesc') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-secondary bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-secondary mb-2">
                      <i class="fas fa-hand-paper me-1"></i>
                      {{ $t('politique.droitOpposition') }}
                    </h5>
                    <p class="small text-muted mb-0">{{ $t('politique.droitOppositionDesc') }}</p>
                  </div>
                </div>
              </div>
            </div>

            <div class="alert alert-info border-0 mt-3">
              <strong>{{ $t('politique.commentExercerDroits') }} :</strong>
              <p class="mb-0">
                {{ $t('politique.commentExercerDroitsDesc') }}
                <strong>{{ $t('conditions.protectionEmail') }}</strong>
                {{ $t('politique.avecPieceIdentite') }}
                {{ $t('politique.delaiReponse') }}
              </p>
            </div>
          </div>

          <!-- 5. Sécurité et transferts -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-lock text-warning me-2"></i>
              5. {{ $t('politique.securiteTransferts') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('politique.securiteTransfertsDesc') }}
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-shield-alt me-1"></i>
                      {{ $t('politique.mesuresTechniques') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('politique.chiffrementDonnees') }}</li>
                      <li>{{ $t('politique.hachageMotsPasse') }}</li>
                      <li>{{ $t('politique.authentificationJWT') }}</li>
                      <li>{{ $t('politique.sauvegardeReguliere') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-users-cog me-1"></i>
                      {{ $t('politique.mesuresOrganisationnelles') }}
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>{{ $t('politique.accesLimiteDonnees') }}</li>
                      <li>{{ $t('politique.formationPersonnel') }}</li>
                      <li>{{ $t('politique.proceduresIncidents') }}</li>
                      <li>{{ $t('politique.auditsSecurite') }}</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>

            <div class="alert alert-primary border-0 mt-3">
              <h5 class="h6 fw-bold mb-2">
                <i class="fab fa-stripe me-2"></i>
                {{ $t('conditions.securiteStripe') }}
              </h5>
              <p class="small mb-0">
                {{ $t('conditions.securiteStripeDesc') }}
              </p>
            </div>
          </div>

          <!-- 6. Destinataires des données -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-share-alt text-info me-2"></i>
              6. {{ $t('politique.destinatairesDonnees') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('politique.destinatairesDonneesDesc') }}
            </p>

            <div class="table-responsive">
              <table class="table table-sm">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('politique.destinataire') }}</th>
                  <th>{{ $t('politique.finalite') }}</th>
                  <th>{{ $t('politique.donneesConcernees') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td><strong>{{ $t('membres.titre') }}</strong></td>
                  <td>{{ $t('collaboration.tempsReel') }}</td>
                  <td>{{ $t('politique.nomPrenomMessages') }}</td>
                </tr>
                <tr>
                  <td><strong>{{ $t('politique.stripeInc') }}</strong></td>
                  <td>{{ $t('abonnement.paiements') }}</td>
                  <td>{{ $t('politique.donneesPaiementUniquement') }}</td>
                </tr>
                <tr>
                  <td><strong>{{ $t('politique.hebergeurAWS') }}</strong></td>
                  <td>{{ $t('politique.hebergementTechnique') }}</td>
                  <td>{{ $t('politique.toutesdonneesSousTraitant') }}</td>
                </tr>
                <tr>
                  <td><strong>{{ $t('politique.serviceEmail') }}</strong></td>
                  <td>{{ $t('notifications.titre') }}</td>
                  <td>{{ $t('politique.emailNomPrenom') }}</td>
                </tr>
                </tbody>
              </table>
            </div>

            <div class="alert alert-warning border-0">
              <strong>{{ $t('politique.transfertsHorsUE') }} :</strong>
              {{ $t('politique.transfertsHorsUEDesc') }}
            </div>
          </div>

          <!-- 7. Contact et réclamations -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-envelope text-success me-2"></i>
              7. {{ $t('politique.contactReclamations') }}
            </h2>
            <p class="text-muted mb-3">
              {{ $t('politique.contactReclamationsDesc') }}
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body text-center">
                    <i class="fas fa-user-shield fa-2x text-success mb-2"></i>
                    <h5 class="h6 fw-bold">{{ $t('politique.delegueDPO') }}</h5>
                    <p class="small text-muted">{{ $t('conditions.protectionEmail') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body text-center">
                    <i class="fas fa-gavel fa-2x text-warning mb-2"></i>
                    <h5 class="h6 fw-bold">{{ $t('politique.autoriteControle') }}</h5>
                    <p class="small text-muted">{{ $t('politique.commissionEuropeenne') }}</p>
                  </div>
                </div>
              </div>
            </div>

            <div class="alert alert-info border-0 mt-3">
              <i class="fas fa-info-circle me-2"></i>
              <strong>{{ $t('politique.droitReclamation') }} :</strong>
              {{ $t('politique.droitReclamationDesc') }}
            </div>
          </div>

          <!-- Navigation et footer local -->
          <div class="bg-white rounded-3 shadow-sm p-4 text-center">
            <div class="d-flex flex-column flex-md-row justify-content-center gap-3 mb-4">
              <router-link to="/conditions" class="btn btn-outline-success">
                <i class="fas fa-file-contract me-2"></i>
                {{ $t('conditions.titre') }}
              </router-link>
              <router-link to="/inscription" class="btn btn-collabpro">
                <i class="fas fa-user-plus me-2"></i>
                {{ $t('inscription.creerCompte') }}
              </router-link>
              <router-link to="/" class="btn btn-outline-secondary">
                <i class="fas fa-home me-2"></i>
                {{ $t('conditions.retourAccueilBtn') }}
              </router-link>
            </div>

            <hr class="my-4">

            <p class="small text-muted mb-0">
              <i class="fas fa-shield-alt me-2"></i>
              {{ $t('politique.conformiteRGPDFooter') }}
            </p>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'PolitiqueConfidentialiteView',
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

/* Styles pour le titre amélioré */
.title-hero {
  position: relative;
}

.title-underline {
  width: 80px;
  height: 4px;
  background: linear-gradient(135deg, #28a745, #20c997);
  border-radius: 2px;
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
  .display-5 { font-size: 2rem !important; }
}
</style>
