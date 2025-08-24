<template>
  <div class="min-vh-100 bg-light">
    <!-- Header -->
    <header class="bg-white shadow-sm py-3">
      <div class="container">
        <div class="d-flex justify-content-between align-items-center">
          <router-link to="/" class="d-flex align-items-center text-decoration-none text-dark">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-3" style="height: 40px; width: 40px;">
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
                <i class="fas fa-shield-alt me-3"></i>
                Politique de Confidentialité
              </h1>
              <p class="lead text-muted">
                CollabPro s'engage à protéger et respecter votre vie privée
              </p>
              <p class="small text-muted">
                <i class="fas fa-calendar-alt me-2"></i>
                Dernière mise à jour : 23 août 2025
              </p>
            </div>

            <div class="alert alert-info border-0">
              <i class="fas fa-info-circle me-2"></i>
              <strong>Information importante :</strong> Cette politique de confidentialité explique comment nous collectons, utilisons et protégeons vos données personnelles conformément au RGPD (Règlement Général sur la Protection des Données).
            </div>
          </div>

          <!-- Section 1 : Données collectées -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-database text-primary me-2"></i>
              1. Données Collectées
            </h2>
            <p class="text-muted mb-3">
              Nous collectons différents types de données pour vous fournir nos services :
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-light h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-user me-1"></i>
                      Données d'identification
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>Nom et prénom</li>
                      <li>Adresse e-mail</li>
                      <li>Mot de passe (crypté)</li>
                      <li>Type de compte (rôle)</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-credit-card me-1"></i>
                      Données de paiement
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>Informations de facturation</li>
                      <li>Historique des transactions</li>
                      <li>Données Stripe (sécurisées)</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-warning mb-2">
                      <i class="fas fa-chart-line me-1"></i>
                      Données d'utilisation
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>Projets créés/participés</li>
                      <li>Tâches et commentaires</li>
                      <li>Messages de chat</li>
                      <li>Logs de connexion</li>
                    </ul>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 bg-light h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-info mb-2">
                      <i class="fas fa-laptop me-1"></i>
                      Données techniques
                    </h5>
                    <ul class="small text-muted mb-0">
                      <li>Adresse IP</li>
                      <li>Navigateur et appareil</li>
                      <li>Pages visitées</li>
                      <li>Préférences linguistiques</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 2 : Utilisation des données -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-cogs text-success me-2"></i>
              2. Utilisation des Données
            </h2>
            <p class="text-muted mb-3">
              Vos données sont utilisées pour les finalités suivantes :
            </p>

            <div class="row g-3">
              <div class="col-12">
                <div class="d-flex align-items-start mb-3">
                  <div class="badge bg-primary rounded-pill me-3 mt-1">1</div>
                  <div>
                    <h5 class="h6 fw-bold mb-1">Fourniture des services</h5>
                    <p class="small text-muted mb-0">
                      Création et gestion de votre compte, accès aux fonctionnalités de collaboration, gestion des projets et tâches.
                    </p>
                  </div>
                </div>

                <div class="d-flex align-items-start mb-3">
                  <div class="badge bg-success rounded-pill me-3 mt-1">2</div>
                  <div>
                    <h5 class="h6 fw-bold mb-1">Traitement des paiements</h5>
                    <p class="small text-muted mb-0">
                      Gestion des abonnements, facturation, remboursements via notre partenaire sécurisé Stripe.
                    </p>
                  </div>
                </div>

                <div class="d-flex align-items-start mb-3">
                  <div class="badge bg-warning rounded-pill me-3 mt-1">3</div>
                  <div>
                    <h5 class="h6 fw-bold mb-1">Communication</h5>
                    <p class="small text-muted mb-0">
                      Notifications de projets, messages système, support client et informations importantes sur le service.
                    </p>
                  </div>
                </div>

                <div class="d-flex align-items-start mb-3">
                  <div class="badge bg-info rounded-pill me-3 mt-1">4</div>
                  <div>
                    <h5 class="h6 fw-bold mb-1">Amélioration du service</h5>
                    <p class="small text-muted mb-0">
                      Analyse d'usage pour optimiser les performances, développer de nouvelles fonctionnalités et résoudre les problèmes techniques.
                    </p>
                  </div>
                </div>

                <div class="d-flex align-items-start">
                  <div class="badge bg-danger rounded-pill me-3 mt-1">5</div>
                  <div>
                    <h5 class="h6 fw-bold mb-1">Sécurité et conformité</h5>
                    <p class="small text-muted mb-0">
                      Prévention des fraudes, respect des obligations légales et protection de la plateforme.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 3 : Partage des données -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-share-alt text-warning me-2"></i>
              3. Partage des Données
            </h2>

            <div class="alert alert-success border-0 mb-3">
              <i class="fas fa-shield-alt me-2"></i>
              <strong>Principe fondamental :</strong> Nous ne vendons jamais vos données personnelles à des tiers.
            </div>

            <p class="text-muted mb-3">
              Vos données ne sont partagées que dans les cas suivants :
            </p>

            <div class="card border-0 bg-light mb-3">
              <div class="card-body">
                <h5 class="h6 fw-bold text-primary mb-2">
                  <i class="fab fa-stripe me-1"></i>
                  Partenaires de paiement
                </h5>
                <p class="small text-muted mb-0">
                  Stripe (notre processeur de paiement) accède uniquement aux données nécessaires pour traiter vos transactions de manière sécurisée.
                </p>
              </div>
            </div>

            <div class="card border-0 bg-light mb-3">
              <div class="card-body">
                <h5 class="h6 fw-bold text-danger mb-2">
                  <i class="fas fa-gavel me-1"></i>
                  Obligations légales
                </h5>
                <p class="small text-muted mb-0">
                  En cas de demande légale des autorités compétentes ou pour protéger nos droits légitimes.
                </p>
              </div>
            </div>

            <div class="card border-0 bg-light">
              <div class="card-body">
                <h5 class="h6 fw-bold text-info mb-2">
                  <i class="fas fa-users me-1"></i>
                  Membres de projet
                </h5>
                <p class="small text-muted mb-0">
                  Vos informations de profil (nom, prénom) sont visibles par les autres membres des projets auxquels vous participez.
                </p>
              </div>
            </div>
          </div>

          <!-- Section 4 : Conservation -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-archive text-info me-2"></i>
              4. Conservation des Données
            </h2>

            <div class="table-responsive">
              <table class="table table-sm">
                <thead class="table-light">
                <tr>
                  <th>Type de données</th>
                  <th>Durée de conservation</th>
                  <th>Justification</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td><strong>Données de compte</strong></td>
                  <td>Tant que le compte est actif + 3 ans</td>
                  <td>Fourniture du service et obligations comptables</td>
                </tr>
                <tr>
                  <td><strong>Données de paiement</strong></td>
                  <td>10 ans après la transaction</td>
                  <td>Obligations légales et fiscales</td>
                </tr>
                <tr>
                  <td><strong>Logs de connexion</strong></td>
                  <td>1 an maximum</td>
                  <td>Sécurité et résolution de problèmes</td>
                </tr>
                <tr>
                  <td><strong>Données de projets</strong></td>
                  <td>Selon les paramètres du projet</td>
                  <td>Collaboration et historique</td>
                </tr>
                </tbody>
              </table>
            </div>

            <div class="alert alert-info border-0 mt-3">
              <i class="fas fa-info-circle me-2"></i>
              À l'expiration de ces délais, vos données sont automatiquement supprimées de nos systèmes.
            </div>
          </div>

          <!-- Section 5 : Vos droits -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-user-shield text-primary me-2"></i>
              5. Vos Droits (RGPD)
            </h2>

            <p class="text-muted mb-4">
              Conformément au RGPD, vous disposez des droits suivants sur vos données personnelles :
            </p>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="card border-0 bg-primary bg-opacity-10 h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-2">
                      <i class="fas fa-eye me-1"></i>
                      Droit d'accès
                    </h5>
                    <p class="small text-muted mb-0">
                      Obtenir une copie de toutes les données vous concernant.
                    </p>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="card border-0 bg-success bg-opacity-10 h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-2">
                      <i class="fas fa-edit me-1"></i>
                      Droit de rectification
                    </h5>
                    <p class="small text-muted mb-0">
                      Corriger ou modifier vos données inexactes ou incomplètes.
                    </p>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="card border-0 bg-danger bg-opacity-10 h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-danger mb-2">
                      <i class="fas fa-trash me-1"></i>
                      Droit à l'effacement
                    </h5>
                    <p class="small text-muted mb-0">
                      Demander la suppression de vos données dans certaines conditions.
                    </p>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="card border-0 bg-warning bg-opacity-10 h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-warning mb-2">
                      <i class="fas fa-pause me-1"></i>
                      Droit à la limitation
                    </h5>
                    <p class="small text-muted mb-0">
                      Restreindre le traitement de vos données en cas de contestation.
                    </p>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="card border-0 bg-info bg-opacity-10 h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-info mb-2">
                      <i class="fas fa-download me-1"></i>
                      Droit à la portabilité
                    </h5>
                    <p class="small text-muted mb-0">
                      Récupérer vos données dans un format structuré et lisible.
                    </p>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="card border-0 bg-secondary bg-opacity-10 h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-secondary mb-2">
                      <i class="fas fa-ban me-1"></i>
                      Droit d'opposition
                    </h5>
                    <p class="small text-muted mb-0">
                      Vous opposer au traitement de vos données à des fins légitimes.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div class="alert alert-primary border-0 mt-4">
              <h5 class="h6 fw-bold mb-2">
                <i class="fas fa-question-circle me-2"></i>
                Comment exercer ces droits ?
              </h5>
              <p class="small mb-0">
                Pour exercer l'un de ces droits, contactez-nous à
                <strong>privacy@collabpro.com</strong> ou via la section contact ci-dessous.
                Nous vous répondrons dans les 30 jours.
              </p>
            </div>
          </div>

          <!-- Section 6 : Sécurité -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-lock text-success me-2"></i>
              6. Sécurité des Données
            </h2>

            <p class="text-muted mb-3">
              Nous mettons en œuvre des mesures techniques et organisationnelles appropriées pour protéger vos données :
            </p>

            <div class="row g-3">
              <div class="col-md-4">
                <div class="text-center p-3">
                  <i class="fas fa-shield-alt fa-2x text-primary mb-2"></i>
                  <h5 class="h6 fw-bold">Chiffrement</h5>
                  <p class="small text-muted">Données chiffrées en transit et au repos</p>
                </div>
              </div>
              <div class="col-md-4">
                <div class="text-center p-3">
                  <i class="fas fa-key fa-2x text-success mb-2"></i>
                  <h5 class="h6 fw-bold">Authentification</h5>
                  <p class="small text-muted">JWT et mots de passe hachés BCrypt</p>
                </div>
              </div>
              <div class="col-md-4">
                <div class="text-center p-3">
                  <i class="fas fa-server fa-2x text-info mb-2"></i>
                  <h5 class="h6 fw-bold">Infrastructure</h5>
                  <p class="small text-muted">Hébergement sécurisé et surveillance</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 7 : Contact -->
          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h2 class="h4 fw-bold text-dark mb-3">
              <i class="fas fa-envelope text-primary me-2"></i>
              7. Contact et Questions
            </h2>

            <div class="row g-4">
              <div class="col-md-6">
                <div class="card border-0 bg-light h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-primary mb-3">
                      <i class="fas fa-shield-alt me-1"></i>
                      Délégué à la protection des données
                    </h5>
                    <p class="small text-muted mb-2">
                      <i class="fas fa-envelope me-2"></i>
                      <strong>privacy@collabpro.com</strong>
                    </p>
                    <p class="small text-muted mb-0">
                      Pour toute question relative à la protection de vos données personnelles.
                    </p>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="card border-0 bg-light h-100">
                  <div class="card-body">
                    <h5 class="h6 fw-bold text-success mb-3">
                      <i class="fas fa-headset me-1"></i>
                      Support technique
                    </h5>
                    <p class="small text-muted mb-2">
                      <i class="fas fa-envelope me-2"></i>
                      <strong>support@collabpro.com</strong>
                    </p>
                    <p class="small text-muted mb-0">
                      Pour l'assistance technique et les questions générales.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div class="alert alert-info border-0 mt-4">
              <i class="fas fa-info-circle me-2"></i>
              <strong>Autorité de contrôle :</strong> Si vous estimez que vos droits ne sont pas respectés, vous pouvez déposer une plainte auprès de la Commission Nationale de l'Informatique et des Libertés (CNIL) ou de votre autorité de protection des données locale.
            </div>
          </div>

          <!-- Navigation et actions -->
          <div class="bg-white rounded-3 shadow-sm p-4 text-center">
            <div class="d-flex flex-column flex-md-row justify-content-center gap-3">
              <router-link to="/conditions" class="btn btn-outline-primary">
                <i class="fas fa-file-contract me-2"></i>
                Voir les CGU
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
              <i class="fas fa-calendar-alt me-2"></i>
              Cette politique de confidentialité peut être mise à jour.
              La date de dernière modification est indiquée en haut de cette page.
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
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 24px; width: 24px;">
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

          <div class="d-flex align-items-center gap-2">
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
  name: 'PolitiqueConfidentialiteView',
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
