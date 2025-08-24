<template>
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card shadow text-center">
          <div class="card-body p-5">
            <div class="cancel-icon mb-4">
              <i class="fas fa-times-circle text-warning" style="font-size: 4rem;"></i>
            </div>

            <h1 class="text-warning mb-3">Paiement annulé</h1>
            <p class="lead mb-4">
              Aucun souci {{ utilisateur.prenom }} ! <br>
              Vous pouvez réessayer quand vous voulez.
            </p>

            <div class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>
              Votre compte reste actif. L'abonnement Premium vous permettra d'accéder à toutes les fonctionnalités.
            </div>

            <div class="d-grid gap-2">
              <router-link to="/abonnement-premium" class="btn btn-primary btn-lg">
                <i class="fas fa-credit-card me-2"></i>
                Réessayer l'abonnement
              </router-link>

              <router-link to="/tableau-de-bord" class="btn btn-outline-secondary">
                <i class="fas fa-home me-2"></i>
                Retour au tableau de bord
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
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
  mounted() {
    this.utilisateur = JSON.parse(localStorage.getItem('user')) || {}
  }
}
</script>
