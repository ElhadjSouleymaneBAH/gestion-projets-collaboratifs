<template>
  <div class="facture-container">
    <!-- Bouton pour ouvrir la facture -->
    <button v-if="!afficherFacture" class="btn btn-primary" @click="afficherFacture = true">
      <i class="fas fa-file-invoice me-2"></i>{{ $t('facture.voirFacture') }}
    </button>

    <!-- Loading -->
    <div v-if="chargement" class="loading-container">
      <div class="spinner"></div>
      <p>{{ $t('commun.chargement') }}</p>
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
              <p class="mb-1">Email: {{ facture.entreprise.email }}</p>
              <p class="mb-0">TVA: {{ facture.entreprise.tva }}</p>
            </div>
          </div>
          <div class="col-md-6 text-end">
            <h2 class="text-danger">{{ $t('facture.facture').toUpperCase() }}</h2>
            <h4>{{ facture.numeroFacture }}</h4>
            <div class="mt-3">
              <p><strong>{{ $t('facture.date') }}:</strong> {{ formatDate(facture.dateEmission) }}</p>
              <p><strong>{{ $t('facture.echeance') }}:</strong> {{ formatDate(facture.dateEcheance) }}</p>
              <p><strong>{{ $t('facture.periode') }}:</strong> {{ facture.periode }}</p>
            </div>
          </div>
        </div>
      </div>

      <hr class="my-4">

      <!-- Informations client -->
      <div class="row mb-4">
        <div class="col-md-6">
          <h5>{{ $t('facture.factureA') }}:</h5>
          <div class="client-info p-3 bg-light rounded">
            <p class="mb-1"><strong>{{ facture.client.nom }}</strong></p>
            <p class="mb-1">{{ facture.client.email }}</p>
            <p class="mb-0">{{ facture.client.adresse }}</p>
          </div>
        </div>
        <div class="col-md-6">
          <h5>{{ $t('facture.detailsPaiement') }}:</h5>
          <div class="paiement-info p-3 bg-light rounded">
            <p class="mb-1"><strong>{{ $t('facture.methode') }}:</strong> {{ getMethodePaiement() }}</p>
            <p class="mb-1"><strong>{{ $t('facture.carte') }}:</strong> **** **** **** {{ facture.dernierChiffres }}</p>
            <p class="mb-1"><strong>{{ $t('facture.transaction') }}:</strong> {{ facture.numeroTransaction }}</p>
            <p class="mb-0"><strong>{{ $t('facture.statut') }}:</strong>
              <span :class="getStatutClass()">{{ $t(`facture.statuts.${facture.statut}`) }}</span>
            </p>
          </div>
        </div>
      </div>

      <!-- Détails des services -->
      <div class="table-responsive mb-4">
        <table class="table table-bordered">
          <thead class="table-primary">
          <tr>
            <th>{{ $t('facture.description') }}</th>
            <th>{{ $t('facture.periode') }}</th>
            <th class="text-end">{{ $t('facture.prixHT') }}</th>
            <th class="text-end">{{ $t('facture.tva') }} ({{ facture.tauxTva }}%)</th>
            <th class="text-end">{{ $t('facture.totalTTC') }}</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>
              <strong>{{ facture.description }}</strong>
              <br>
              <small class="text-muted">
                {{ $t('facture.accesFonctionnalites') }}
              </small>
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
          <!-- Informations légales -->
          <div class="legal-info">
            <h6>{{ $t('facture.conditionsPaiement') }}:</h6>
            <ul class="list-unstyled small">
              <li>• {{ $t('facture.prelevementAutomatique') }}</li>
              <li>• {{ $t('facture.renouvellementAutomatique') }}</li>
              <li>• {{ $t('facture.resiliationPossible') }}</li>
            </ul>
          </div>
        </div>
        <div class="col-md-6">
          <div class="total-section p-3 bg-primary text-white rounded">
            <div class="d-flex justify-content-between mb-2">
              <span>{{ $t('facture.sousTotal') }}:</span>
              <span>{{ formatMontant(facture.montantHT) }}</span>
            </div>
            <div class="d-flex justify-content-between mb-2">
              <span>{{ $t('facture.tva') }} ({{ facture.tauxTva }}%):</span>
              <span>{{ formatMontant(facture.tva) }}</span>
            </div>
            <hr class="border-white">
            <div class="d-flex justify-content-between">
              <strong>{{ $t('facture.totalTTC').toUpperCase() }}:</strong>
              <strong class="fs-4">{{ formatMontant(facture.montantTTC) }}</strong>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="facture-footer mt-4 pt-3 border-top text-center">
        <p class="text-muted small mb-1">
          {{ $t('facture.merciConfiance') }} {{ facture.entreprise.email }}
        </p>
        <p class="text-muted small mb-0">
          CollabPro - {{ $t('facture.solutionCollaborative') }}
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
          {{ $t('facture.telechargerPDF') }}
        </button>

        <button
          class="btn btn-info me-2"
          @click="envoyerParEmail"
          :disabled="actionEnCours"
        >
          <span v-if="actionEnCours === 'email'" class="spinner-border spinner-border-sm me-2"></span>
          <i v-else class="fas fa-envelope me-2"></i>
          {{ $t('facture.envoyerEmail') }}
        </button>

        <button class="btn btn-outline-secondary" @click="fermerFacture">
          <i class="fas fa-times me-2"></i>{{ $t('commun.fermer') }}
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
import { useI18n } from 'vue-i18n'
import factureService from '@/services/facture.service.js'

export default {
  name: 'Facture',
  props: {
    facture: {
      type: Object,
      required: true
    },
    // Mode d'affichage : 'button' (avec bouton) ou 'direct' (affichage direct)
    mode: {
      type: String,
      default: 'button'
    }
  },
  emits: ['facture-fermee', 'action-terminee'],
  setup(props, { emit }) {
    const { t, locale } = useI18n()

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
      return date.toLocaleDateString(locale.value === 'fr' ? 'fr-FR' : 'en-US', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      })
    }

    const formatMontant = (montant) => {
      if (typeof montant !== 'number') return '0,00 €'
      return new Intl.NumberFormat(locale.value === 'fr' ? 'fr-FR' : 'en-US', {
        style: 'currency',
        currency: 'EUR'
      }).format(montant)
    }

    const getMethodePaiement = () => {
      const methodes = {
        'CARTE_BANCAIRE': t('facture.methodes.carteBancaire'),
        'VIREMENT': t('facture.methodes.virement'),
        'PAYPAL': t('facture.methodes.paypal')
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
        erreur.value = ''

        const result = await factureService.telechargerPDF(props.facture.id)

        if (result.success) {
          afficherMessage(t('facture.telechargementReussi'), 'success')
          emit('action-terminee', { type: 'telecharger', success: true })
        } else {
          throw new Error(result.message || 'Erreur téléchargement')
        }
      } catch (error) {
        console.error('Erreur téléchargement PDF:', error)
        erreur.value = error.message || t('facture.erreurTelechargement')
        afficherMessage(t('facture.erreurTelechargement'), 'error')
        emit('action-terminee', { type: 'telecharger', success: false, error: error.message })
      } finally {
        actionEnCours.value = null
      }
    }

    const envoyerParEmail = async () => {
      try {
        actionEnCours.value = 'email'
        erreur.value = ''

        const result = await factureService.envoyerParEmail(props.facture.id)

        if (result.success) {
          afficherMessage(
            t('facture.emailEnvoye', { email: result.email || props.facture.client.email }),
            'success'
          )
          emit('action-terminee', { type: 'email', success: true })
        } else {
          throw new Error(result.message || 'Erreur envoi email')
        }
      } catch (error) {
        console.error('Erreur envoi email:', error)
        erreur.value = error.message || t('facture.erreurEmail')
        afficherMessage(t('facture.erreurEmail'), 'error')
        emit('action-terminee', { type: 'email', success: false, error: error.message })
      } finally {
        actionEnCours.value = null
      }
    }

    const fermerFacture = () => {
      afficherFacture.value = false
      emit('facture-fermee')
    }

    // Validation des données de la facture
    const validerFacture = () => {
      if (!props.facture) {
        erreur.value = t('facture.erreurDonneesManquantes')
        return false
      }

      const champsRequis = ['id', 'numeroFacture', 'montantHT', 'tva', 'montantTTC', 'dateEmission']
      const champsManquants = champsRequis.filter(champ => !props.facture[champ])

      if (champsManquants.length > 0) {
        erreur.value = t('facture.erreurChampsManquants', { champs: champsManquants.join(', ') })
        return false
      }

      return true
    }

    // Cycle de vie
    onMounted(() => {
      if (!validerFacture()) {
        console.error('Données de facture invalides:', props.facture)
      }
    })

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
.facture-container {
  max-width: 800px;
  margin: 0 auto;
}

.loading-container {
  text-align: center;
  padding: 40px;
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

.facture-header {
  margin-bottom: 20px;
}

.entreprise-info p,
.client-info p,
.paiement-info p {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 4px;
}

.client-info,
.paiement-info {
  background-color: #f8f9fa !important;
  border: 1px solid #e9ecef;
}

.legal-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.legal-info ul li {
  margin-bottom: 4px;
  color: #666;
}

.total-section {
  font-size: 1.1em;
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%) !important;
}

.facture-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid #e9ecef;
}

.facture-actions {
  padding: 20px 0;
  border-top: 1px solid #e9ecef;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: none;
  cursor: pointer;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.4);
}

.btn-success {
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(40, 167, 69, 0.3);
}

.btn-success:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
}

.btn-info {
  background: linear-gradient(135deg, #17a2b8 0%, #117a8b 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(23, 162, 184, 0.3);
}

.btn-info:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(23, 162, 184, 0.4);
}

.btn-outline-secondary {
  background: white;
  color: #6c757d;
  border: 2px solid #6c757d;
}

.btn-outline-secondary:hover:not(:disabled) {
  background: #6c757d;
  color: white;
  transform: translateY(-2px);
}

.alert {
  padding: 12px 16px;
  border-radius: 8px;
  border: none;
  font-weight: 500;
}

.alert-success {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
  border-left: 4px solid #28a745;
}

.alert-danger {
  background: linear-gradient(135deg, #f8d7da 0%, #f1b0b7 100%);
  color: #721c24;
  border-left: 4px solid #dc3545;
}

.table {
  margin-bottom: 0;
}

.table th {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  color: #1565c0;
  font-weight: 600;
  border: none;
  padding: 12px;
}

.table td {
  padding: 12px;
  vertical-align: middle;
  border-color: #e9ecef;
}

.badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.spinner-border-sm {
  width: 14px;
  height: 14px;
}

/* Responsive */
@media (max-width: 768px) {
  .facture-pdf {
    padding: 20px;
    margin: 10px;
  }

  .facture-actions {
    text-align: center;
  }

  .facture-actions .btn {
    display: block;
    width: 100%;
    margin-bottom: 10px;
  }

  .facture-actions .btn:last-child {
    margin-bottom: 0;
  }

  .col-md-6 {
    margin-bottom: 20px;
  }

  .text-end {
    text-align: left !important;
  }
}

/* Impression */
@media print {
  .facture-actions,
  .btn {
    display: none !important;
  }

  .facture-pdf {
    box-shadow: none;
    border: none;
    padding: 0;
  }

  .facture-container {
    max-width: none;
  }
}
</style>
