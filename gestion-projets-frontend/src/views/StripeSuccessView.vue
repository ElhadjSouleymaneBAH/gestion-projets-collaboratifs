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
            <router-link to="/tableau-bord" class="btn btn-link text-collabpro text-decoration-none">
              {{ $t('nav.tableauBord') }}
            </router-link>
            <router-link to="/mon-profil" class="btn btn-outline-primary">
              {{ $t('nav.monProfil') }}
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Section principale -->
    <main class="container py-5">
      <div class="row justify-content-center">
        <div class="col-lg-8 col-xl-6 text-center">

          <!-- Animation -->
          <div class="success-animation mb-4">
            <div class="success-checkmark">
              <div class="check-icon">
                <span class="icon-line line-tip"></span>
                <span class="icon-line line-long"></span>
                <div class="icon-circle"></div>
                <div class="icon-fix"></div>
              </div>
            </div>
          </div>

          <h1 class="h2 fw-bold text-success mb-3">
            <i class="fas fa-crown me-2"></i>
            {{ $t('stripe.success.titre') }}
          </h1>

          <p class="lead text-muted mb-4">
            {{ $t('stripe.success.felicitations', { prenom: utilisateur.prenom || '' }) }}
          </p>

          <div class="alert alert-success border-0 shadow-sm mb-5">
            <i class="fas fa-check-circle fa-lg me-2"></i>
            {{ $t('stripe.success.abonnementActif') }}
          </div>

          <div class="bg-white rounded-3 shadow-sm p-4 mb-4">
            <h3 class="h5 fw-bold text-dark mb-3">
              <i class="fas fa-receipt me-2"></i>
              {{ $t('stripe.success.detailsAbonnement') }}
            </h3>

            <div class="row g-3">
              <div class="col-md-6 d-flex justify-content-between">
                <span class="text-muted">{{ $t('stripe.success.plan') }} :</span>
                <strong class="text-primary">{{ $t('stripe.success.planPremiumMensuel') }}</strong>
              </div>
              <div class="col-md-6 d-flex justify-content-between">
                <span class="text-muted">{{ $t('stripe.success.statut') }} :</span>
                <span class="badge bg-success fs-6">{{ $t('stripe.success.actif') }}</span>
              </div>
              <div class="col-md-6 d-flex justify-content-between">
                <span class="text-muted">{{ $t('stripe.success.prochainPaiement') }} :</span>
                <strong class="text-dark">{{ nextPaymentDate }}</strong>
              </div>
              <div class="col-md-6 d-flex justify-content-between">
                <span class="text-muted">{{ $t('stripe.success.montant') }} :</span>
                <strong class="text-warning">{{ $t('stripe.success.montantTotal') }}</strong>
              </div>
            </div>
          </div>

          <div class="alert alert-info border-0 bg-primary bg-opacity-10">
            <i class="fas fa-file-invoice fa-lg text-primary me-2"></i>
            {{ $t('stripe.success.factureGeneree') }}
          </div>

          <div class="text-center mt-4">
            <router-link to="/tableau-bord" class="btn btn-collabpro btn-lg px-4 me-2">
              <i class="fas fa-tachometer-alt me-2"></i>
              {{ $t('stripe.success.accederTableauBord') }}
            </router-link>
            <router-link to="/factures" class="btn btn-outline-primary btn-lg px-4">
              <i class="fas fa-file-invoice me-2"></i>
              {{ $t('stripe.success.voirFactures') }}
            </router-link>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="bg-dark text-white py-3 mt-5">
      <div class="container d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
        <router-link to="/" class="text-white text-decoration-none">
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
      </div>
    </footer>
  </div>
</template>

<script>
import { stripeAPI } from '@/services/api'

export default {
  name: 'StripeSuccessView',
  data() {
    return {
      utilisateur: {},
      sessionId: null,
      nextPaymentDate: this.calculateNextPayment()
    }
  },
  methods: {
    setLocale(locale) {
      this.$i18n.locale = locale
      localStorage.setItem('locale', locale)
      this.nextPaymentDate = this.calculateNextPayment()
    },
    calculateNextPayment() {
      const nextMonth = new Date()
      nextMonth.setMonth(nextMonth.getMonth() + 1)
      return nextMonth.toLocaleDateString(this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },
    async processStripeSession() {
      this.sessionId = this.$route.query.session_id
      if (!this.sessionId) return

      try {
        // Vérifie la session Stripe côté backend
        const { data } = await stripeAPI.confirmPayment(this.sessionId)
        console.log('✅ Paiement confirmé côté serveur:', data)

        // Met à jour le rôle utilisateur local
        this.utilisateur = JSON.parse(localStorage.getItem('user')) || {}
        if (this.utilisateur.role !== 'CHEF_PROJET') {
          this.utilisateur.role = 'CHEF_PROJET'
          localStorage.setItem('user', JSON.stringify(this.utilisateur))
        }

        // Redirige vers le tableau de bord Chef de Projet après 3s
        setTimeout(() => {
          this.$router.replace('/tableau-bord-chef-projet')
        }, 3000)
      } catch (error) {
        console.error('❌ Erreur lors de la confirmation du paiement:', error)
        alert(this.$t('stripe.erreurConfirmation') || 'Erreur de confirmation du paiement.')
      }
    }
  },
  mounted() {
    window.scrollTo(0, 0)
    this.utilisateur = JSON.parse(localStorage.getItem('user')) || {}
    this.processStripeSession()
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

/* Animation de succès */
.success-animation { display: inline-block; }
.success-checkmark {
  width: 80px; height: 80px; border-radius: 50%;
  stroke: #28a745; margin: 10% auto; position: relative;
  box-shadow: inset 0px 0px 0px #28a745;
  animation: fill 0.4s ease-in-out 0.4s forwards, scale 0.3s ease-in-out 0.9s both;
}
.check-icon { position: relative; width: 100%; height: 100%; background: #28a745; border-radius: 50%; }
.icon-line { height: 3px; background-color: #fff; position: absolute; border-radius: 2px; }
.line-tip { top: 46px; left: 14px; width: 25px; transform: rotate(45deg); animation: icon-line-tip 0.75s; }
.line-long { top: 38px; right: 8px; width: 47px; transform: rotate(-45deg); animation: icon-line-long 0.75s; }
.icon-circle { position: absolute; width: 80px; height: 80px; border-radius: 50%; border: 4px solid rgba(40,167,69,0.2); }
.icon-fix { position: absolute; width: 5px; height: 85px; top: 8px; left: 26px; transform: rotate(-45deg); background-color: #fff; }

@keyframes icon-line-tip {
  0%,54% { width: 0; left: 1px; top: 19px; }
  70% { width: 50px; left: -8px; top: 37px; }
  100% { width: 25px; left: 14px; top: 45px; }
}
@keyframes icon-line-long {
  0%,65% { width: 0; right: 46px; top: 54px; }
  84% { width: 55px; right: 0; top: 35px; }
  100% { width: 47px; right: 8px; top: 38px; }
}
@keyframes fill { 100% { box-shadow: inset 0px 0px 0px 60px #28a745; } }
@keyframes scale { 0%,100% { transform: none; } 50% { transform: scale3d(1.1,1.1,1); } }
</style>
