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
              Connexion
            </router-link>
            <router-link to="/inscription" class="btn btn-collabpro">
              Inscription
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
                Conditions Générales d'Utilisation
              </h1>
              <p class="lead text-muted">
                Conditions d'usage de la plateforme CollabPro
              </p>
              <p class="small text-muted">
                <i class="fas fa-calendar-alt me-2"></i>
                Dernière mise à jour : 24 août 2025
              </p>
            </div>
          </div>

          <!-- Section 1 : Introduction -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-handshake text-primary me-2"></i>
              1. Introduction
            </h2>
            <p class="text-muted">
              Bienvenue sur notre plateforme de gestion de projets collaboratifs destinée aux PME et aux freelances.
              En utilisant notre application CollabPro, vous acceptez les présentes Conditions Générales d'Utilisation.
            </p>
            <div class="alert alert-info border-0 mt-3">
              <i class="fas fa-info-circle me-2"></i>
              <strong>Important :</strong> L'utilisation de la plateforme vaut acceptation pleine et entière des présentes conditions.
            </div>
          </div>

          <!-- Section 2 : Objet -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-bullseye text-success me-2"></i>
              2. Objet de l'Application
            </h2>
            <p class="text-muted mb-3">
              Notre application permet la création, la gestion de projets, l'attribution de tâches et l'utilisation d'outils collaboratifs en temps réel.
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-project-diagram me-1"></i>
                      Gestion de projets
                    </h5>
                    <p class="small text-muted mb-0">Création, modification et suivi des projets collaboratifs</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-users me-1"></i>
                      Collaboration temps réel
                    </h5>
                    <p class="small text-muted mb-0">Chat, notifications et partage instantané</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 3 : Inscription -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-user-plus text-warning me-2"></i>
              3. Inscription et Comptes Utilisateurs
            </h2>
            <p class="text-muted mb-3">
              Vous devez fournir des informations exactes lors de l'inscription. Vous acceptez de garder vos identifiants confidentiels.
            </p>

            <div class="card border-0 bg-warning bg-opacity-10">
              <div class="card-body">
                <h5 class="h6 fw-bold text-warning mb-2">
                  <i class="fas fa-shield-alt me-1"></i>
                  Responsabilité du compte
                </h5>
                <ul class="small text-muted mb-0">
                  <li>Informations d'inscription exactes et à jour</li>
                  <li>Confidentialité des identifiants de connexion</li>
                  <li>Notification immédiate en cas d'usage non autorisé</li>
                </ul>
              </div>
            </div>
          </div>

          <!-- Section 4 : Utilisation -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-gavel text-info me-2"></i>
              4. Utilisation de l'Application
            </h2>
            <p class="text-muted mb-3">
              Vous vous engagez à utiliser l'application de manière légale et conforme aux présentes CGU.
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-success bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-check me-1"></i>
                      Usages autorisés
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>Gestion de projets professionnels</li>
                      <li>Collaboration en équipe</li>
                      <li>Communication via les outils intégrés</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-danger bg-opacity-10">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-danger mb-2">
                      <i class="fas fa-times me-1"></i>
                      Usages interdits
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>Contenu illégal ou offensant</li>
                      <li>Tentatives de piratage</li>
                      <li>Spam ou sollicitations</li>
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
              5. Paiements et Transactions
            </h2>
            <p class="text-muted mb-3">
              Les paiements sont sécurisés via Stripe. Toutes les transactions sont finales et non remboursables sauf mention contraire.
            </p>

            <div class="alert alert-primary border-0">
              <h5 class="h6 fw-bold mb-2">
                <i class="fab fa-stripe me-2"></i>
                Sécurité Stripe
              </h5>
              <p class="small mb-0">
                Tous les paiements sont traités de manière sécurisée par Stripe,
                leader mondial des solutions de paiement en ligne. CollabPro ne stocke jamais vos informations bancaires.
              </p>
            </div>

            <div class="table-responsive mt-3">
              <table class="table table-sm">
                <thead class="table-light">
                <tr>
                  <th>Type d'abonnement</th>
                  <th>Prix</th>
                  <th>Fonctionnalités</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td><strong>Chef de Projet</strong></td>
                  <td>10€/mois</td>
                  <td>Création/gestion projets, équipes, factures</td>
                </tr>
                <tr>
                  <td><strong>Membre</strong></td>
                  <td>Gratuit</td>
                  <td>Participation aux projets</td>
                </tr>
                <tr>
                  <td><strong>Visiteur</strong></td>
                  <td>Gratuit</td>
                  <td>Consultation projets publics</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Section 6 : Données -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-database text-success me-2"></i>
              6. Protection des Données Personnelles
            </h2>
            <p class="text-muted mb-3">
              Nous collectons vos données pour fournir nos services. Vous avez le droit d'accéder, rectifier ou supprimer vos données.
            </p>

            <div class="alert alert-info border-0">
              <h5 class="h6 fw-bold mb-2">
                <i class="fas fa-shield-alt me-2"></i>
                Référence RGPD
              </h5>
              <p class="small mb-0">
                Pour des informations détaillées sur le traitement de vos données personnelles,
                consultez notre <router-link to="/politique-confidentialite" class="text-decoration-none fw-bold">Politique de Confidentialité</router-link>.
              </p>
            </div>
          </div>

          <!-- Section 7 : Responsabilités -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-balance-scale text-warning me-2"></i>
              7. Responsabilités
            </h2>
            <p class="text-muted mb-3">
              Nous nous engageons à assurer la disponibilité de l'application mais ne pouvons garantir une utilisation ininterrompue.
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-handshake me-1"></i>
                      Nos engagements
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>Disponibilité optimale de la plateforme</li>
                      <li>Sécurité des données</li>
                      <li>Support technique</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-secondary mb-2">
                      <i class="fas fa-exclamation-triangle me-1"></i>
                      Limites de responsabilité
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>Interruptions techniques</li>
                      <li>Maintenance programmée</li>
                      <li>Cas de force majeure</li>
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
              8. Modification des CGU
            </h2>
            <p class="text-muted mb-3">
              Nous nous réservons le droit de modifier les CGU. Les modifications seront notifiées aux utilisateurs.
            </p>

            <div class="alert alert-warning border-0">
              <i class="fas fa-bell me-2"></i>
              <strong>Notification des changements :</strong> Toute modification sera communiquée par email
              et/ou notification dans l'application avec un préavis de 30 jours minimum.
            </div>
          </div>

          <!-- Section 9 : Loi applicable -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-globe-europe text-primary me-2"></i>
              9. Loi Applicable
            </h2>
            <p class="text-muted mb-3">
              Les présentes CGU sont soumises aux lois en vigueur en Europe.
            </p>

            <div class="card border-0 bg-primary bg-opacity-10">
              <div class="card-body">
                <h5 class="h6 fw-bold text-primary mb-2">
                  <i class="fas fa-gavel me-1"></i>
                  Juridiction compétente
                </h5>
                <p class="small text-muted mb-0">
                  Tout litige relatif à l'interprétation ou à l'exécution des présentes CGU relève
                  de la compétence exclusive des tribunaux européens, conformément au droit applicable dans l'Union Européenne.
                </p>
              </div>
            </div>
          </div>

          <!-- Contact et actions -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-envelope text-success me-2"></i>
              10. Contact
            </h2>
            <p class="text-muted mb-3">
              Pour toute question relative aux CGU, contactez-nous à
              <strong>support@collabpro.com</strong>.
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body text-center">
                    <i class="fas fa-headset fa-2x text-primary mb-2"></i>
                    <h5 class="h6 fw-bold">Support technique</h5>
                    <p class="small text-muted">support@collabpro.com</p>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light">
                  <div class="card-body text-center">
                    <i class="fas fa-shield-alt fa-2x text-success mb-2"></i>
                    <h5 class="h6 fw-bold">Protection des données</h5>
                    <p class="small text-muted">privacy@collabpro.com</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Navigation et footer -->
          <div class="bg-white rounded-3 shadow-sm p-4 text-center">
            <div class="d-flex flex-column flex-md-row justify-content-center gap-3 mb-4">
              <router-link to="/politique-confidentialite" class="btn btn-outline-primary">
                <i class="fas fa-shield-alt me-2"></i>
                Politique de Confidentialité
              </router-link>
              <router-link to="/inscription" class="btn btn-collabpro">
                <i class="fas fa-user-plus me-2"></i>
                Créer un compte
              </router-link>
              <router-link to="/" class="btn btn-outline-secondary">
                <i class="fas fa-home me-2"></i>
                Retour à l'accueil
              </router-link>
            </div>

            <hr class="my-4">

            <p class="small text-muted mb-0">
              <i class="fas fa-balance-scale me-2"></i>
              Ces Conditions Générales d'Utilisation sont adaptées à un projet de gestion de projets collaboratifs
              avec intégration de paiements via Stripe et conformité RGPD.
            </p>

            <p class="small text-muted mt-2 mb-0">
              <i class="fas fa-calendar-alt me-2"></i>
              Document mis à jour le 24 août 2025
            </p>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="bg-dark text-white py-3">
      <div class="container">
        <div class="d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
          <router-link to="/" class="d-inline-flex align-items-center text-decoration-none text-white">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 32px; width: 32px;">
            <span class="fw-bold">CollabPro</span>
          </router-link>

          <div class="text-center">
            <router-link to="/conditions" class="text-white text-decoration-underline small me-3">
              CGU
            </router-link>
            <router-link to="/politique-confidentialite" class="text-white text-decoration-underline small me-3">
              Politique de confidentialité
            </router-link>
            <span class="small text-white-50">© 2025 Tous droits réservés</span>
          </div>

          <div class="d-flex align-items-center">
            <div class="d-flex align-items-center lang-wrap">
              <i class="fas fa-globe me-2"></i>
              <select class="form-select form-select-sm lang-select" aria-label="Sélection de la langue">
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
  name: 'ConditionsView',
  mounted() {
    // Scroll vers le haut au chargement
    window.scrollTo(0, 0)
  }
}
</script>

<style scoped>
.text-collabpro {
  color: #007bff;
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
  color: #fff;
}

.card {
  transition: all .3s ease;
  border-radius: 8px;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,.1) !important;
}

.lang-wrap {
  color: #fff;
}

.lang-select {
  background: transparent;
  color: #fff;
  border: 1px solid rgba(255,255,255,.6);
  padding: .2rem .5rem;
  font-size: 0.875rem;
}

.lang-select option {
  color: #000;
}

/* Animation pour les sections */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.bg-white {
  animation: fadeInUp 0.6s ease-out;
}

/* Responsive */
@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }

  .col-lg-8 {
    padding-left: 0.5rem;
    padding-right: 0.5rem;
  }

  .table-responsive {
    font-size: 0.875rem;
  }
}
</style>
