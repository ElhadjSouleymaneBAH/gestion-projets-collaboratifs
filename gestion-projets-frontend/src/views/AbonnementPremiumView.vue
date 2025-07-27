<template>
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow">
          <div class="card-header bg-warning text-dark">
            <h4><i class="fas fa-crown me-2"></i>Abonnement Premium Requis</h4>
          </div>
          <div class="card-body">
            <!-- Message d'information -->
            <div class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>
              <strong>Bienvenue {{ utilisateur.prenom }} !</strong><br>
              Pour accéder aux fonctionnalités Chef de Projet, un abonnement Premium est requis.
            </div>

            <!-- Plan Premium -->
            <div class="row">
              <div class="col-md-6">
                <div class="card border-primary h-100">
                  <div class="card-header bg-primary text-white text-center">
                    <h5><i class="fas fa-crown me-2"></i>Plan Premium</h5>
                  </div>
                  <div class="card-body">
                    <div class="text-center mb-4">
                      <h2 class="text-primary">10€<small class="text-muted">/mois</small></h2>
                    </div>

                    <ul class="list-unstyled">
                      <li><i class="fas fa-check text-success me-2"></i>Créer des projets illimités</li>
                      <li><i class="fas fa-check text-success me-2"></i>Gérer une équipe</li>
                      <li><i class="fas fa-check text-success me-2"></i>Assigner des tâches</li>
                      <li><i class="fas fa-check text-success me-2"></i>Chat temps réel</li>
                      <li><i class="fas fa-check text-success me-2"></i>Statistiques avancées</li>
                      <li><i class="fas fa-check text-success me-2"></i>Support prioritaire</li>
                    </ul>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <!-- Formulaire de paiement -->
                <div class="card">
                  <div class="card-header">
                    <h6>Informations de paiement</h6>
                  </div>
                  <div class="card-body">
                    <form @submit.prevent="processerPaiement">
                      <div class="mb-3">
                        <label class="form-label">Nom sur la carte</label>
                        <input
                          type="text"
                          class="form-control"
                          v-model="paiement.nomCarte"
                          required
                        >
                      </div>

                      <div class="mb-3">
                        <label class="form-label">Numéro de carte</label>
                        <input
                          type="text"
                          class="form-control"
                          v-model="paiement.numeroCarte"
                          placeholder="1234 5678 9012 3456"
                          maxlength="19"
                          @input="formatCartNumber"
                          required
                        >
                      </div>

                      <div class="row">
                        <div class="col-md-6 mb-3">
                          <label class="form-label">Expiration</label>
                          <input
                            type="text"
                            class="form-control"
                            v-model="paiement.expiration"
                            placeholder="MM/AA"
                            maxlength="5"
                            @input="formatExpiration"
                            required
                          >
                        </div>
                        <div class="col-md-6 mb-3">
                          <label class="form-label">CVV</label>
                          <input
                            type="text"
                            class="form-control"
                            v-model="paiement.cvv"
                            placeholder="123"
                            maxlength="3"
                            required
                          >
                        </div>
                      </div>

                      <div class="mb-3">
                        <label class="form-label">Adresse de facturation</label>
                        <input
                          type="text"
                          class="form-control"
                          v-model="paiement.adresse"
                          placeholder="123 Rue de la Paix, 1000 Bruxelles"
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
                          J'accepte les conditions d'abonnement et autorise le prélèvement mensuel de 10€
                        </label>
                      </div>

                      <!-- Résumé commande -->
                      <div class="card bg-light mb-3">
                        <div class="card-body">
                          <h6>Résumé de la commande</h6>
                          <div class="d-flex justify-content-between">
                            <span>Abonnement Premium (mensuel)</span>
                            <strong>10,00 €</strong>
                          </div>
                          <div class="d-flex justify-content-between">
                            <span>TVA (21%)</span>
                            <span>2,10 €</span>
                          </div>
                          <hr>
                          <div class="d-flex justify-content-between">
                            <strong>Total</strong>
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
                        <i class="fas fa-credit-card me-2"></i>
                        {{ loading ? 'Traitement...' : 'Souscrire à Premium (12,10€)' }}
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
export default {
  name: 'AbonnementPremium',
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
    // Récupérer l'utilisateur connecté
    this.utilisateur = JSON.parse(localStorage.getItem('user')) || {}

    // Pré-remplir le nom
    if (this.utilisateur.prenom && this.utilisateur.nom) {
      this.paiement.nomCarte = `${this.utilisateur.prenom} ${this.utilisateur.nom}`
    }

    // Vérifier si l'utilisateur a déjà un abonnement actif
    if (this.utilisateur.abonnement?.statut === 'ACTIF') {
      this.$router.push('/tableau-bord-chef-projet')
    }
  },

  methods: {
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
        // Validation basique
        if (!this.validerDonnesPaiement()) {
          return
        }

        // Simulation du processus de paiement (3 secondes)
        await new Promise(resolve => setTimeout(resolve, 3000))

        // Créer la facture
        const facture = this.creerFacture()

        // Mettre à jour le statut utilisateur
        this.activerAbonnementPremium()

        // Sauvegarder la facture
        this.sauvegarderFacture(facture)

        this.message = 'Paiement réussi ! Votre abonnement Premium est maintenant actif.'
        this.messageType = 'alert-success'

        // Redirection vers tableau Chef après 2 secondes
        setTimeout(() => {
          this.$router.push('/tableau-bord-chef-projet')
        }, 2000)

      } catch (error) {
        console.error('Erreur paiement:', error)
        this.message = 'Erreur lors du traitement du paiement. Veuillez réessayer.'
        this.messageType = 'alert-danger'
      } finally {
        this.loading = false
      }
    },

    validerDonnesPaiement() {
      if (this.paiement.numeroCarte.replace(/\s/g, '').length !== 16) {
        this.message = 'Numéro de carte invalide'
        this.messageType = 'alert-danger'
        return false
      }

      if (this.paiement.cvv.length !== 3) {
        this.message = 'CVV invalide'
        this.messageType = 'alert-danger'
        return false
      }

      if (!this.paiement.accepteConditions) {
        this.message = 'Veuillez accepter les conditions d\'abonnement'
        this.messageType = 'alert-danger'
        return false
      }

      return true
    },

    creerFacture() {
      return {
        id: Date.now(),
        numero: `FAC-${new Date().getFullYear()}-${String(Date.now()).slice(-6)}`,
        utilisateurId: this.utilisateur.id,

        // Informations entreprise (VOTRE LOGO/INFO)
        entreprise: {
          nom: 'CollabPro Solutions',
          logo: '/logo-collabpro.png', // Votre logo
          adresse: 'Avenue de la Innovation 123',
          ville: '1000 Bruxelles, Belgique',
          email: 'contact@collabpro.be',
          tva: 'BE0123.456.789'
        },

        // Informations client
        client: {
          nom: `${this.utilisateur.prenom} ${this.utilisateur.nom}`,
          email: this.utilisateur.email,
          adresse: this.paiement.adresse
        },

        // Détails facture
        montantHT: 10.00,
        tva: 2.10,
        tauxTva: 21,
        montantTTC: 12.10,
        description: 'Abonnement Premium CollabPro - Mensuel',
        periode: 'Janvier 2025',

        // Dates
        dateEmission: new Date().toISOString(),
        datePaiement: new Date().toISOString(),
        dateEcheance: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),

        // Statut
        statut: 'PAYEE',
        methodePaiement: 'CARTE_BANCAIRE',
        dernierChiffres: this.paiement.numeroCarte.slice(-4),

        // Références
        numeroTransaction: 'TXN-' + Date.now(),
        referenceCommande: 'CMD-' + Date.now()
      }
    },

    activerAbonnementPremium() {
      // Mettre à jour l'utilisateur avec abonnement Premium
      this.utilisateur.abonnement = {
        type: 'PREMIUM',
        statut: 'ACTIF',
        dateDebut: new Date().toISOString(),
        prochainPaiement: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(), // +30 jours
        montant: 10.00
      }

      localStorage.setItem('user', JSON.stringify(this.utilisateur))

      // Mettre à jour aussi dans la liste des utilisateurs
      const nouveauxUtilisateurs = JSON.parse(localStorage.getItem('nouveauxUtilisateurs')) || []
      const index = nouveauxUtilisateurs.findIndex(u => u.id === this.utilisateur.id)
      if (index !== -1) {
        nouveauxUtilisateurs[index] = this.utilisateur
        localStorage.setItem('nouveauxUtilisateurs', JSON.stringify(nouveauxUtilisateurs))
      }
    },

    sauvegarderFacture(facture) {
      const factures = JSON.parse(localStorage.getItem('factures')) || []
      factures.push(facture)
      localStorage.setItem('factures', JSON.stringify(factures))

      // Sauvegarder aussi par utilisateur pour affichage dans tableau
      const facturesUtilisateur = JSON.parse(localStorage.getItem(`factures_${this.utilisateur.id}`)) || []
      facturesUtilisateur.push(facture)
      localStorage.setItem(`factures_${this.utilisateur.id}`, JSON.stringify(facturesUtilisateur))
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

.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}
</style>
