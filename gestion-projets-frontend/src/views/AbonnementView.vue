<template>
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow">
          <!-- En-tête -->
          <div class="card-header bg-warning text-dark rounded-top-3">
            <h4 class="mb-0 d-flex align-items-center">
              <i class="fas fa-crown me-2"></i>
              {{ $t('abonnement.abonnementPremiumRequis') }}
            </h4>
          </div>

          <div class="card-body">
            <!-- Info utilisateur -->
            <div class="alert alert-info d-flex align-items-start">
              <i class="fas fa-info-circle me-2 mt-1"></i>
              <div>
                <strong>{{ $t('commun.bienvenue') }} {{ utilisateur?.prenom }} !</strong><br>
                {{ $t('abonnement.pourAccederFonctionnalites') }}
              </div>
            </div>

            <!-- Statut existant -->
            <div
              v-if="abonnement"
              class="alert"
              :class="abonnementActif ? 'alert-success' : 'alert-danger'"
            >
              <i
                class="fas"
                :class="abonnementActif ? 'fa-check-circle' : 'fa-exclamation-triangle'"
              ></i>
              <span class="ms-2">
                <strong>{{
                    abonnementActif
                      ? $t('abonnement.actif')
                      : $t('abonnement.expire')
                  }}</strong>
                <span v-if="abonnement.date_fin" class="ms-2">—
                  {{ $t('abonnement.expireLe') }}
                  {{ formatDate(abonnement.date_fin) }}</span
                >
              </span>
            </div>

            <div class="row g-3">
              <!-- Colonne Plan -->
              <div class="col-md-6">
                <div class="card border-primary h-100">
                  <div class="card-header bg-primary text-white text-center">
                    <h5 class="mb-0">
                      <i class="fas fa-crown me-2"></i
                      >{{ $t('abonnement.planPremium') }}
                    </h5>
                  </div>
                  <div class="card-body">
                    <div class="text-center mb-4">
                      <h2 class="text-primary">
                        {{ $t('abonnement.prix') }}
                        <small class="text-muted">{{
                            $t('abonnement.parMois')
                          }}</small>
                      </h2>
                    </div>
                    <ul class="list-unstyled mb-0 features-list">
                      <li class="mb-2">
                        <i class="fas fa-check text-success me-2"></i
                        >{{ $t('abonnement.features.creationProjets') }}
                      </li>
                      <li class="mb-2">
                        <i class="fas fa-check text-success me-2"></i
                        >{{ $t('abonnement.features.gestionEquipes') }}
                      </li>
                      <li class="mb-2">
                        <i class="fas fa-check text-success me-2"></i
                        >{{ $t('abonnement.features.outils') }}
                      </li>
                      <li class="mb-2">
                        <i class="fas fa-check text-success me-2"></i
                        >{{ $t('abonnement.features.chat') }}
                      </li>
                      <li class="mb-2">
                        <i class="fas fa-check text-success me-2"></i
                        >{{ $t('abonnement.features.statistiques') }}
                      </li>
                      <li class="mb-2">
                        <i class="fas fa-check text-success me-2"></i
                        >{{ $t('abonnement.features.support') }}
                      </li>
                    </ul>
                  </div>
                </div>
              </div>

              <!-- Colonne Paiement -->
              <div class="col-md-6">
                <div class="card h-100">
                  <div class="card-header bg-white">
                    <h6 class="mb-0">{{ $t('paiement.titre') }}</h6>
                  </div>
                  <div class="card-body">
                    <form @submit.prevent="souscrire">
                      <!-- Nom sur la carte -->
                      <div class="mb-3">
                        <label class="form-label">{{
                            $t('paiement.nomCarte')
                          }}</label>
                        <input
                          class="form-control"
                          v-model.trim="paiement.nomCarte"
                          required
                        />
                      </div>

                      <!-- Numéro de carte -->
                      <div class="mb-3">
                        <label class="form-label">{{
                            $t('paiement.numeroCarte')
                          }}</label>
                        <input
                          class="form-control"
                          :placeholder="$t('paiement.numeroCartePlaceholder')"
                          v-model="paiement.numeroCarte"
                          maxlength="19"
                          @input="formatCardNumber"
                          required
                        />
                      </div>

                      <!-- Expiration + CVV -->
                      <div class="row">
                        <div class="col-md-6 mb-3">
                          <label class="form-label">{{
                              $t('paiement.expiration')
                            }}</label>
                          <input
                            class="form-control"
                            :placeholder="$t('paiement.expirationPlaceholder')"
                            v-model="paiement.expiration"
                            maxlength="5"
                            @input="formatExpiration"
                            required
                          />
                        </div>
                        <div class="col-md-6 mb-3">
                          <label class="form-label">{{
                              $t('paiement.cvv')
                            }}</label>
                          <!-- 🔒 CVV masqué uniquement (modif demandée) -->
                          <input
                            class="form-control"
                            type="password"
                            autocomplete="cc-csc"
                            inputmode="numeric"
                            :placeholder="$t('paiement.cvvPlaceholder')"
                            v-model="paiement.cvv"
                            maxlength="3"
                            required
                          />
                        </div>
                      </div>

                      <!-- Adresse de facturation -->
                      <div class="mb-3">
                        <label class="form-label">{{
                            $t('paiement.adresseFacturation')
                          }}</label>
                        <input
                          class="form-control"
                          :placeholder="$t('paiement.adressePlaceholder')"
                          v-model.trim="paiement.adresse"
                          required
                        />
                      </div>

                      <!-- Conditions -->
                      <div class="form-check mb-3">
                        <input
                          class="form-check-input"
                          type="checkbox"
                          id="conditions"
                          v-model="paiement.accepteConditions"
                          required
                        />
                        <label class="form-check-label" for="conditions">
                          {{ $t('abonnement.accepterConditions') }}
                        </label>
                      </div>

                      <!-- Résumé -->
                      <div class="card bg-light mb-3 shadow-sm">
                        <div class="card-body">
                          <h6 class="mb-3">
                            {{ $t('paiement.resumerCommande') }}
                          </h6>
                          <div class="d-flex justify-content-between">
                            <span>{{ $t('paiement.abonnementMensuel') }}</span>
                            <strong>{{ montantHTFormatte }}</strong>
                          </div>
                          <div class="d-flex justify-content-between">
                            <span>{{ $t('paiement.tva') }}</span>
                            <span>{{ tvaFormattee }}</span>
                          </div>
                          <hr />
                          <div class="d-flex justify-content-between">
                            <strong>{{ $t('paiement.total') }}</strong>
                            <strong>{{ totalTTCFormatte }}</strong>
                          </div>
                        </div>
                      </div>

                      <!-- Bouton -->
                      <button
                        type="submit"
                        class="btn btn-success w-100"
                        :disabled="loadingAction"
                      >
                        <span
                          v-if="loadingAction"
                          class="spinner-border spinner-border-sm me-2"
                        ></span>
                        <i v-else class="fas fa-credit-card me-2"></i>
                        {{
                          abonnementActif
                            ? $t('abonnement.renouveler')
                            : $t('abonnement.souscrirePremium', {
                              prix: totalTTCFormatte,
                            })
                        }}
                      </button>
                    </form>

                    <!-- Messages -->
                    <div
                      v-if="message"
                      class="alert mt-3"
                      :class="messageType"
                    >
                      <i
                        :class="
                          messageType.includes('success')
                            ? 'fas fa-check-circle'
                            : 'fas fa-exclamation-triangle'
                        "
                      ></i>
                      <span class="ms-2">{{ message }}</span>
                    </div>
                  </div>
                </div>

                <p class="text-muted small mt-2">
                  <i class="fas fa-lock me-1"></i
                  >{{ $t('abonnement.descriptionPaiements') }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Lien tableau de bord si actif -->
        <div class="text-center mt-3" v-if="abonnementActif">
          <router-link
            to="/tableau-bord-chef-projet"
            class="btn btn-outline-success"
          >
            <i class="fas fa-arrow-right me-2"></i
            >{{ $t('nav.tableauBord') }}
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth'
import { abonnementAPI, stripeAPI, userAPI } from '@/services/api'

export default {
  name: 'AbonnementView',
  data() {
    return {
      abonnement: null,
      loadingAction: false,
      message: '',
      messageType: 'alert-success',
      prixMensuelHT: 10,
      tauxTVA: 0.21,
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
  computed: {
    utilisateur() {
      return useAuthStore().user
    },
    abonnementActif() {
      const a = this.abonnement
      if (!a) return false
      if (typeof a.est_actif === 'boolean') return a.est_actif
      const fin = a.date_fin ? new Date(a.date_fin) : null
      return (a.statut || 'ACTIF') === 'ACTIF' && fin && fin > new Date()
    },
    montantHT() {
      return this.prixMensuelHT
    },
    tva() {
      return +(this.prixMensuelHT * this.tauxTVA).toFixed(2)
    },
    totalTTC() {
      return +(this.prixMensuelHT + this.tva).toFixed(2)
    },
    montantHTFormatte() {
      return this.formatPrix(this.montantHT)
    },
    tvaFormattee() {
      return this.formatPrix(this.tva)
    },
    totalTTCFormatte() {
      return this.formatPrix(this.totalTTC)
    }
  },
  mounted() {
    const u = useAuthStore().user
    if (!u || !u.id) {
      this.$router.push('/connexion')
      return
    }
    if (u?.prenom && u?.nom) {
      this.paiement.nomCarte = `${u.prenom} ${u.nom}`
    }
    if (u?.adresse) {
      this.paiement.adresse = u.adresse
    }
    this.chargerAbonnement()
  },
  methods: {
    async chargerAbonnement() {
      try {
        const u = this.utilisateur
        if (!u || !u.id) return
        const userId = String(u.id).split(':')[0]
        const { data } = await abonnementAPI.getByUserId(userId)
        this.abonnement = data || null
      } catch (e) {
        if (e?.response?.status === 404) {
          this.abonnement = null
        } else {
          console.error('Erreur chargement abonnement:', e)
        }
      }
    },
    formatCardNumber(e) {
      const raw = e.target.value.replace(/\s+/g, '').replace(/\D/g, '')
      const grouped = raw.match(/.{1,4}/g)?.join(' ') || raw
      this.paiement.numeroCarte = grouped.slice(0, 19)
    },
    formatExpiration(e) {
      const digits = e.target.value.replace(/\D/g, '').slice(0, 4)
      this.paiement.expiration =
        digits.length > 2 ? `${digits.slice(0, 2)}/${digits.slice(2)}` : digits
    },
    formatPrix(val) {
      return new Intl.NumberFormat(
        this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US',
        { style: 'currency', currency: 'EUR' }
      ).format(val)
    },
    formatDate(date) {
      if (!date) return '—'
      return new Date(date).toLocaleDateString(
        this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US'
      )
    },
    validerForm() {
      const num = this.paiement.numeroCarte.replace(/\s/g, '')
      if (num.length !== 16) {
        this.message = this.$t('paiement.numeroCarteInvalide')
        this.messageType = 'alert-danger'
        return false
      }
      if (!/^\d{2}\/\d{2}$/.test(this.paiement.expiration)) {
        this.message = this.$t('paiement.expirationInvalide')
        this.messageType = 'alert-danger'
        return false
      }
      if (this.paiement.cvv.length !== 3) {
        this.message = this.$t('paiement.cvvInvalide')
        this.messageType = 'alert-danger'
        return false
      }
      if (!this.paiement.adresse?.trim()) {
        this.message = this.$t('paiement.adresseInvalide')
        this.messageType = 'alert-danger'
        return false
      }
      if (!this.paiement.accepteConditions) {
        this.message = this.$t('paiement.accepterConditionsRequis')
        this.messageType = 'alert-danger'
        return false
      }
      return true
    },

    /**
     * Souscription/Renouvellement avec Stripe RÉEL
     * Flux dynamique aligné au backend
     */
    async souscrire() {
      if (!this.validerForm()) return

      const store = useAuthStore()
      const u = store.user
      if (!u || !u.id) {
        this.message = 'Erreur : utilisateur non connecté'
        this.messageType = 'alert-danger'
        return
      }

      this.loadingAction = true
      this.message = ''

      try {
        // Étape 1 : Créer PaymentIntent Stripe
        const createRes = await stripeAPI.createPaymentIntent()
        const paymentIntentId =
          createRes?.data?.payment_intent_id ||
          createRes?.data?.paymentIntentId ||
          createRes?.data?.id

        if (!paymentIntentId) {
          throw new Error('PaymentIntent ID manquant')
        }

        // Étape 2 : Confirmer le paiement Stripe
        const confirmRes = await stripeAPI.confirmPayment(paymentIntentId)
        if (confirmRes?.data?.success !== true) {
          throw new Error(confirmRes?.data?.message || 'Paiement échoué')
        }

        // Étape 3 : Souscription backend (gère tout automatiquement)
        const { data } = await abonnementAPI.souscrire({
          nom: 'Plan Premium Mensuel',
          prix: this.prixMensuelHT,
          duree: 1,
          type: 'premium'
        })

        this.abonnement = data

        // Étape 4 : Rafraîchir utilisateur (rôle mis à jour)
        const userId = String(u.id).split(':')[0]
        const me = await userAPI.getById(userId)
        store.user = me.data
        localStorage.setItem('user', JSON.stringify(me.data))

        // Message succès
        this.message =
          this.$t('paiement.paiementReussi') ||
          'Abonnement souscrit avec succès !'
        this.messageType = 'alert-success'

        // Redirection
        setTimeout(() => {
          this.$router.push('/tableau-bord-chef-projet')
        }, 1500)

      } catch (e) {
        console.error('Erreur souscription:', e)
        const errorMsg =
          e?.response?.data?.error ||
          e?.response?.data?.message ||
          e?.message ||
          this.$t('paiement.erreurPaiement')

        this.message = typeof errorMsg === 'string' ? errorMsg : 'Erreur paiement'
        this.messageType = 'alert-danger'
      } finally {
        this.loadingAction = false
      }
    }
  }
}
</script>

<style scoped>
.card {
  border-radius: 15px;
}
.card-header {
  border-radius: 15px 15px 0 0;
}
.shadow {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}
.form-control:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}
.features-list li {
  display: flex;
  align-items: center;
  font-size: 0.95rem;
}
.btn-success {
  border-radius: 10px;
  padding: 0.65rem 1rem;
  font-weight: 600;
}
.alert-info {
  background: #e7f3ff;
  border-color: #b6dcff;
}
.rounded-top-3 {
  border-top-left-radius: 1rem !important;
  border-top-right-radius: 1rem !important;
}
</style>
