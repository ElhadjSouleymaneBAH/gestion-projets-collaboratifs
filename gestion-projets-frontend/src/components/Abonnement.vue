<template>
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow">
          <div class="card-header bg-warning text-dark">
            <h4><i class="fas fa-crown me-2"></i>{{ $t('abonnement.abonnementPremiumRequis') }}</h4>
          </div>
          <div class="card-body">
            <!-- Message d'information -->
            <div class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>
              <strong>{{ $t('commun.bienvenue') }} {{ utilisateur.prenom }} !</strong><br>
              {{ $t('abonnement.pourAccederFonctionnalites') }}
            </div>

            <!-- Plan Premium -->
            <div class="row">
              <div class="col-md-6">
                <div class="card border-primary h-100">
                  <div class="card-header bg-primary text-white text-center">
                    <h5><i class="fas fa-crown me-2"></i>{{ $t('abonnement.planPremium') }}</h5>
                  </div>
                  <div class="card-body">
                    <div class="text-center mb-4">

                      <h2 class="text-primary">
                        {{ $t('abonnement.prixMensuel', { prix: formatPrice(10) }) }}
                      </h2>
                    </div>

                    <ul class="list-unstyled">
                      <li><i class="fas fa-check text-success me-2"></i>{{ $t('abonnement.features.creationProjets') }}</li>
                      <li><i class="fas fa-check text-success me-2"></i>{{ $t('abonnement.features.gestionEquipes') }}</li>
                      <li><i class="fas fa-check text-success me-2"></i>{{ $t('abonnement.features.outils') }}</li>
                      <li><i class="fas fa-check text-success me-2"></i>{{ $t('abonnement.features.chat') }}</li>
                      <li><i class="fas fa-check text-success me-2"></i>{{ $t('abonnement.features.statistiques') }}</li>
                      <li><i class="fas fa-check text-success me-2"></i>{{ $t('abonnement.features.support') }}</li>
                    </ul>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <!-- Formulaire de paiement -->
                <div class="card">
                  <div class="card-header">
                    <h6>{{ $t('paiement.titre') }}</h6>
                  </div>
                  <div class="card-body">
                    <form @submit.prevent="processerPaiement">
                      <div class="mb-3">
                        <label class="form-label">{{ $t('paiement.nomCarte') }}</label>
                        <input
                          type="text"
                          class="form-control"
                          v-model="paiement.nomCarte"
                          required
                        >
                      </div>

                      <div class="mb-3">
                        <label class="form-label">{{ $t('paiement.numeroCarte') }}</label>
                        <input
                          type="text"
                          class="form-control"
                          v-model="paiement.numeroCarte"
                          :placeholder="$t('paiement.numeroCartePlaceholder')"
                          maxlength="19"
                          @input="formatCartNumber"
                          required
                        >
                      </div>

                      <div class="row">
                        <div class="col-md-6 mb-3">
                          <label class="form-label">{{ $t('paiement.expiration') }}</label>
                          <input
                            type="text"
                            class="form-control"
                            v-model="paiement.expiration"
                            :placeholder="$t('paiement.expirationPlaceholder')"
                            maxlength="5"
                            @input="formatExpiration"
                            required
                          >
                        </div>
                        <div class="col-md-6 mb-3">
                          <label class="form-label">{{ $t('paiement.cvv') }}</label>
                          <input
                            type="text"
                            class="form-control"
                            v-model="paiement.cvv"
                            :placeholder="$t('paiement.cvvPlaceholder')"
                            maxlength="3"
                            required
                          >
                        </div>
                      </div>

                      <div class="mb-3">
                        <label class="form-label">{{ $t('paiement.adresseFacturation') }}</label>
                        <input
                          type="text"
                          class="form-control"
                          v-model="paiement.adresse"
                          :placeholder="$t('paiement.adressePlaceholder')"
                          required
                        >
                      </div>

                      <div class="form-check mb-3">
                        <input
                          class="form-check-input"
                          type="checkbox"
                          v-model="paiement.accepteConditions"
                          required
                        >
                        <label class="form-check-label">
                          {{ $t('abonnement.accepterConditions') }}
                        </label>
                      </div>

                      <!-- Résumé commande -->
                      <div class="card bg-light mb-3">
                        <div class="card-body">
                          <h6>{{ $t('paiement.resumerCommande') }}</h6>
                          <div class="d-flex justify-content-between">
                            <span>{{ $t('paiement.abonnementMensuel') }}</span>
                            <strong>10,00 €</strong>
                          </div>
                          <div class="d-flex justify-content-between">
                            <span>{{ $t('paiement.tva') }}</span>
                            <span>2,10 €</span>
                          </div>
                          <hr>
                          <div class="d-flex justify-content-between">
                            <strong>{{ $t('paiement.total') }}</strong>
                            <strong>12,10 €</strong>
                          </div>
                        </div>
                      </div>

                      <button
                        type="submit"
                        class="btn btn-success w-100"
                        :disabled="loading"
                      >
                        <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                        <i v-else class="fas fa-credit-card me-2"></i>
                        {{ loading ? $t('paiement.traitement') : `${$t('paiement.souscrire')} (12,10€)` }}
                      </button>
                    </form>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Messages -->
        <div v-if="message" class="alert mt-3" :class="messageType">
          <i :class="messageType.includes('success') ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle'"></i>
          {{ message }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { abonnementAPI, paiementAPI } from '@/services/api'

export default {
  name: 'AbonnementView',

  setup() {
    const router = useRouter()
    return { router }
  },

  data() {
    return {
      loading: false,
      message: '',
      messageType: 'alert-success',
      utilisateur: {},
      paiement: {
        nomCarte: '',
        numeroCarte: '',
        expiration: '',
        cvv: '',
        adresse: '',
        accepteConditions: false
      }
    }
  },

  mounted() {
    const authStore = useAuthStore()
    this.utilisateur = authStore.user || {}

    if (this.utilisateur.prenom && this.utilisateur.nom) {
      this.paiement.nomCarte = `${this.utilisateur.prenom} ${this.utilisateur.nom}`
    }

    this.verifierAbonnementExistant().catch(error => {
      console.log('Erreur vérification abonnement:', error)
    })
  },

  methods: {

    formatPrice(v) {
      return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(v)
    },

    async verifierAbonnementExistant() {
      try {
        const response = await abonnementAPI.getByUserId(this.utilisateur.id)
        if (response.data && response.data.statut === 'ACTIF') {
          await this.router.push('/tableau-bord-chef-projet')
        }
      } catch (error) {
        console.log('Aucun abonnement actif trouvé:', error.message || error)
      }
    },

    formatCartNumber(event) {
      let value = event.target.value.replace(/\s/g, '').replace(/[^0-9]/gi, '')
      let formatedValue = value.match(/.{1,4}/g)?.join(' ') || value
      if (formatedValue.length > 19) formatedValue = formatedValue.substring(0, 19)
      this.paiement.numeroCarte = formatedValue
    },

    formatExpiration(event) {
      let value = event.target.value.replace(/\D/g, '')
      if (value.length >= 2) {
        value = value.substring(0, 2) + '/' + value.substring(2, 4)
      }
      this.paiement.expiration = value
    },

    async processerPaiement() {
      this.loading = true
      this.message = ''

      try {
        if (!this.validerDonnesPaiement()) {
          this.loading = false
          return
        }

        const paymentIntentResponse = await paiementAPI.createPaymentIntent()
        await paiementAPI.confirmPayment(paymentIntentResponse.data.paymentIntentId)
        await abonnementAPI.souscrire({
          nom: 'Plan Premium Mensuel',
          prix: 10.00,
          duree: 30,
          type: 'MENSUEL',
          statut: 'ACTIF'
        })

        this.message = this.$t('paiement.paiementReussi')
        this.messageType = 'alert-success'

        setTimeout(async () => {
          try {
            await this.router.push('/tableau-bord-chef-projet')
          } catch (navigationError) {
            console.error('Erreur navigation:', navigationError)
          }
        }, 2000)

      } catch (error) {
        console.error('Erreur paiement:', error)
        this.message = this.$t('paiement.erreurPaiement')
        this.messageType = 'alert-danger'
      } finally {
        this.loading = false
      }
    },

    validerDonnesPaiement() {
      const numeroCarteSansEspaces = this.paiement.numeroCarte.replace(/\s/g, '')
      if (numeroCarteSansEspaces.length !== 16) {
        this.message = this.$t('paiement.numeroCarteInvalide')
        this.messageType = 'alert-danger'
        return false
      }

      if (this.paiement.cvv.length !== 3) {
        this.message = this.$t('paiement.cvvInvalide')
        this.messageType = 'alert-danger'
        return false
      }

      const expirationRegex = /^(0[1-9]|1[0-2])\/\d{2}$/
      if (!expirationRegex.test(this.paiement.expiration)) {
        this.message = this.$t('paiement.expirationInvalide')
        this.messageType = 'alert-danger'
        return false
      }

      if (!this.paiement.accepteConditions) {
        this.message = this.$t('paiement.accepterConditionsRequis')
        this.messageType = 'alert-danger'
        return false
      }

      return true
    }
  }
}
</script>

<style scoped>
.card { border-radius: 15px; }
.card-header { border-radius: 15px 15px 0 0; }
.form-control:focus { border-color: #007bff; box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25); }
</style>
