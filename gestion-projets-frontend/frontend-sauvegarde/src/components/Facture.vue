<template>
  <div class="facture-container">
    <!-- Bouton pour ouvrir la facture -->
    <button v-if="!afficherFacture" class="btn btn-primary" @click="afficherFacture = true">
      <i class="fas fa-file-invoice me-2"></i>{{ $t('invoice_view') }}
    </button>

    <!-- Loading -->
    <div v-if="chargement" class="loading-container">
      <div class="spinner"></div>
      <p>{{ $t('loading') }}</p>
    </div>

    <!-- Message d'erreur -->
    <div v-if="erreur" class="alert alert-danger">
      <i class="fas fa-exclamation-triangle me-2"></i>{{ erreur }}
    </div>

    <!-- Facture complète -->
    <div v-if="afficherFacture && !chargement" class="facture-pdf">
      <!-- Header avec logo -->
      <div class="facture-header">
        <div class="row">
          <div class="col-md-6">
            <img :src="facture.entreprise.logo" alt="CollabPro" class="logo-facture">
            <h3 class="text-primary mt-2">{{ facture.entreprise.nom }}</h3>
            <div class="entreprise-info">
              <p class="mb-1">{{ facture.entreprise.adresse }}</p>
              <p class="mb-1">{{ facture.entreprise.ville }}</p>
              <p class="mb-1">{{ $t('email') }}: {{ facture.entreprise.email }}</p>
              <p class="mb-0">{{ $t('vat') }}: {{ facture.entreprise.tva }}</p>
            </div>
          </div>
          <div class="col-md-6 text-end">
            <h2 class="text-danger">{{ $t('invoice').toUpperCase() }}</h2>
            <h4>{{ facture.numeroFacture }}</h4>
            <div class="mt-3">
              <p><strong>{{ $t('date') }}:</strong> {{ formatDate(facture.dateEmission) }}</p>
              <p><strong>{{ $t('due_date') }}:</strong> {{ formatDate(facture.dateEcheance) }}</p>
              <p><strong>{{ $t('period') }}:</strong> {{ facture.periode }}</p>
            </div>
          </div>
        </div>
      </div>

      <hr class="my-4">

      <!-- Informations client -->
      <div class="row mb-4">
        <div class="col-md-6">
          <h5>{{ $t('bill_to') }}:</h5>
          <div class="client-info p-3 bg-light rounded">
            <p class="mb-1"><strong>{{ facture.client.nom }}</strong></p>
            <p class="mb-1">{{ facture.client.email }}</p>
            <p class="mb-0">{{ facture.client.adresse }}</p>
          </div>
        </div>
        <div class="col-md-6">
          <h5>{{ $t('payment_details') }}:</h5>
          <div class="paiement-info p-3 bg-light rounded">
            <p class="mb-1"><strong>{{ $t('payment_method') }}:</strong> {{ getMethodePaiement() }}</p>
            <p class="mb-1"><strong>{{ $t('card') }}:</strong> **** **** **** {{ facture.dernierChiffres }}</p>
            <p class="mb-1"><strong>{{ $t('transaction_id') }}:</strong> {{ facture.numeroTransaction }}</p>
            <p class="mb-0"><strong>{{ $t('status') }}:</strong>
              <span :class="getStatutClass()">{{ $t(`invoice_status_${facture.statut}`) }}</span>
            </p>
          </div>
        </div>
      </div>

      <!-- Détails des services -->
      <div class="table-responsive mb-4">
        <table class="table table-bordered">
          <thead class="table-primary">
          <tr>
            <th>{{ $t('description') }}</th>
            <th>{{ $t('period') }}</th>
            <th class="text-end">{{ $t('amount_ht') }}</th>
            <th class="text-end">{{ $t('vat') }} ({{ facture.tauxTva }}%)</th>
            <th class="text-end">{{ $t('total_ttc') }}</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>
              <strong>{{ facture.description }}</strong>
              <br>
              <small class="text-muted">{{ $t('premium_features_access') }}</small>
            </td>
            <td>{{ facture.periode }}</td>
            <td class="text-end">{{ formatMontant(facture.montantHT) }}</td>
            <td class="text-end">{{ formatMontant(facture.tva) }}</td>
            <td class="text-end"><strong>{{ formatMontant(facture.montantTTC) }}</strong></td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Total -->
      <div class="row">
        <div class="col-md-6">
          <div class="legal-info">
            <h6>{{ $t('payment_terms') }}:</h6>
            <ul class="list-unstyled small">
              <li>• {{ $t('automatic_debit') }}</li>
              <li>• {{ $t('auto_renewal') }}</li>
              <li>• {{ $t('cancellation_possible') }}</li>
            </ul>
          </div>
        </div>
        <div class="col-md-6">
          <div class="total-section p-3 bg-primary text-white rounded">
            <div class="d-flex justify-content-between mb-2">
              <span>{{ $t('subtotal') }}:</span>
              <span>{{ formatMontant(facture.montantHT) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-2">
              <span>{{ $t('vat') }} ({{ facture.tauxTva }}%):</span>
              <span>{{ formatMontant(facture.tva) }}</span>
            </div>
            <hr class="border-white">
            <div class="d-flex justify-content-between">
              <strong>{{ $t('total_ttc').toUpperCase() }}:</strong>
              <strong class="fs-4">{{ formatMontant(facture.montantTTC) }}</strong>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="facture-footer mt-4 pt-3 border-top text-center">
        <p class="text-muted small mb-1">
          {{ $t('thank_you_trust') }} {{ facture.entreprise.email }}
        </p>
        <p class="text-muted small mb-0">
          CollabPro - {{ $t('collaborative_solution') }}
        </p>
      </div>

      <!-- Actions -->
      <div class="facture-actions mt-4 text-center">
        <button
          class="btn btn-success me-2"
          @click="telechargerPDF"
          :disabled="actionEnCours"
        >
          <span v-if="actionEnCours === 'pdf'" class="spinner-border spinner-border-sm me-2"></span>
          <i v-else class="fas fa-download me-2"></i>
          {{ $t('download_pdf') }}
        </button>

        <button
          class="btn btn-info me-2"
          @click="envoyerParEmail"
          :disabled="actionEnCours"
        >
          <span v-if="actionEnCours === 'email'" class="spinner-border spinner-border-sm me-2"></span>
          <i v-else class="fas fa-envelope me-2"></i>
          {{ $t('send_by_email') }}
        </button>

        <button class="btn btn-outline-secondary" @click="fermerFacture">
          <i class="fas fa-times me-2"></i>{{ $t('close') }}
        </button>
      </div>

      <!-- Messages de succès/erreur -->
      <div v-if="message.texte" :class="['alert', 'mt-3', message.type === 'success' ? 'alert-success' : 'alert-danger']">
        <i :class="message.type === 'success' ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle'" class="me-2"></i>
        {{ message.texte }}
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'

export default {
  name: 'Facture',
  props: {
    facture: {
      type: Object,
      required: true
    },
    mode: {
      type: String,
      default: 'button'
    }
  },
  emits: ['facture-fermee', 'action-terminee'],
  setup(props, { emit }) {
    // États réactifs
    const afficherFacture = ref(props.mode === 'direct')
    const chargement = ref(false)
    const actionEnCours = ref(null)
    const erreur = ref('')

    const message = reactive({
      texte: '',
      type: ''
    })

    // Méthodes
    const afficherMessage = (texte, type = 'success') => {
      message.texte = texte
      message.type = type
      setTimeout(() => {
        message.texte = ''
        message.type = ''
      }, 5000)
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      })
    }

    const formatMontant = (montant) => {
      if (typeof montant !== 'number') return '0,00 €'
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR'
      }).format(montant)
    }

    const getMethodePaiement = () => {
      const methodes = {
        'CARTE_BANCAIRE': 'Carte bancaire',
        'VIREMENT': 'Virement',
        'PAYPAL': 'PayPal'
      }
      return methodes[props.facture.methodePaiement] || props.facture.methodePaiement
    }

    const getStatutClass = () => {
      const classes = {
        'GENEREE': 'badge bg-success',
        'ECHEC': 'badge bg-danger',
        'EN_ATTENTE': 'badge bg-warning'
      }
      return classes[props.facture.statut] || 'badge bg-secondary'
    }

    const telechargerPDF = async () => {
      try {
        actionEnCours.value = 'pdf'
        // Simulation téléchargement
        await new Promise(resolve => setTimeout(resolve, 2000))
        afficherMessage('Téléchargement réussi', 'success')
        emit('action-terminee', { type: 'telecharger', success: true })
      } catch (error) {
        console.error('Erreur téléchargement PDF:', error)
        afficherMessage('Erreur téléchargement', 'error')
      } finally {
        actionEnCours.value = null
      }
    }

    const envoyerParEmail = async () => {
      try {
        actionEnCours.value = 'email'
        // Simulation envoi email
        await new Promise(resolve => setTimeout(resolve, 2000))
        afficherMessage('Email envoyé avec succès', 'success')
        emit('action-terminee', { type: 'email', success: true })
      } catch (error) {
        console.error('Erreur envoi email:', error)
        afficherMessage('Erreur envoi email', 'error')
      } finally {
        actionEnCours.value = null
      }
    }

    const fermerFacture = () => {
      afficherFacture.value = false
      emit('facture-fermee')
    }

    return {
      // États
      afficherFacture,
      chargement,
      actionEnCours,
      erreur,
      message,

      // Méthodes
      formatDate,
      formatMontant,
      getMethodePaiement,
      getStatutClass,
      telechargerPDF,
      envoyerParEmail,
      fermerFacture
    }
  }
}
</script>

<style scoped>
/* Styles condensés - version simplifiée */
.facture-container {
  max-width: 800px;
  margin: 0 auto;
}

.facture-pdf {
  background: white;
  padding: 30px;
  border: 1px solid #ddd;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-top: 20px;
}

.logo-facture {
  height: 60px;
  width: auto;
  object-fit: contain;
}

.total-section {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%) !important;
}

.btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: none;
  cursor: pointer;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e2e8f0;
  border-top: 3px solid #4299e1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 768px) {
  .facture-pdf {
    padding: 20px;
    margin: 10px;
  }

  .facture-actions .btn {
    display: block;
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>
