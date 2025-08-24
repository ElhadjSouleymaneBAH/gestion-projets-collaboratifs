<template>
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card shadow text-center">
          <div class="card-body p-5">
            <div class="success-icon mb-4">
              <i class="fas fa-check-circle text-success" style="font-size: 4rem;"></i>
            </div>

            <h1 class="text-success mb-3">Paiement réussi !</h1>
            <p class="lead mb-4">
              Félicitations {{ utilisateur.prenom }} ! <br>
              Votre abonnement Premium est maintenant actif.
            </p>

            <div class="alert alert-success">
              <i class="fas fa-crown me-2"></i>
              Vous avez maintenant accès à toutes les fonctionnalités Chef de Projet !
            </div>

            <div class="d-grid gap-2">
              <router-link to="/tableau-bord-chef-projet" class="btn btn-primary btn-lg">
                <i class="fas fa-tachometer-alt me-2"></i>
                Accéder au tableau de bord
              </router-link>

              <router-link to="/factures" class="btn btn-outline-secondary">
                <i class="fas fa-file-invoice me-2"></i>
                Voir ma facture
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
  name: 'StripeSuccessView',
  data() {
    return {
      utilisateur: {}
    }
  },
  mounted() {
    this.utilisateur = JSON.parse(localStorage.getItem('user')) || {}

    // Traiter le session_id de Stripe si présent
    const sessionId = this.$route.query.session_id
    if (sessionId) {
      console.log('Session Stripe ID:', sessionId)
      // Ici tu peux faire un appel API pour confirmer le paiement
    }
  }
}
</script>
