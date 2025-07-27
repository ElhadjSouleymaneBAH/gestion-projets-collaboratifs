<template>
  <div class="factures-view">
    <div class="container-fluid">
      <!-- En-tête -->
      <div class="page-header">
        <div class="row align-items-center">
          <div class="col-md-6">
            <h1 class="page-title">
              <i class="fas fa-file-invoice-dollar me-3"></i>
              {{ $t('factures.mesFactures') }}
            </h1>
            <p class="page-subtitle">{{ $t('factures.gestionFactures') }}</p>
          </div>
          <div class="col-md-6 text-end">
            <div class="stats-summary">
              <div class="stat-item">
                <span class="stat-number">{{ statistiques.total }}</span>
                <span class="stat-label">{{ $t('factures.total') }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ formatMontant(statistiques.montantTotal) }}</span>
                <span class="stat-label">{{ $t('factures.montantTotal') }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Filtres et recherche -->
      <div class="filters-section">
        <div class="row">
          <div class="col-md-4">
            <div class="filter-group">
              <label>{{ $t('factures.filtrerParStatut') }}</label>
              <select v-model="filtreStatut" @change="chargerFactures" class="form-select">
                <option value="">{{ $t('factures.tousLesStatuts') }}</option>
                <option value="GENEREE">{{ $t('factures.statuts.GENEREE') }}</option>
                <option value="ECHEC">{{ $t('factures.statuts.ECHEC') }}</option>
                <option value="EN_ATTENTE">{{ $t('factures.statuts.EN_ATTENTE') }}</option>
              </select>
            </div>
          </div>
          <div class="col-md-4">
            <div class="filter-group">
              <label>{{ $t('factures.periode') }}</label>
              <select v-model="filtrePeriode" @change="chargerFactures" class="form-select">
                <option value="">{{ $t('factures.toutesLesPeriodes') }}</option>
                <option value="2025">2025</option>
                <option value="2024">2024</option>
                <option value="2023">2023</option>
              </select>
            </div>
          </div>
          <div class="col-md-4">
            <div class="filter-group">
              <label>{{ $t('factures.rechercher') }}</label>
              <input
                v-model="rechercheTexte"
                @input="rechercherFactures"
                type="text"
                class="form-control"
                :placeholder="$t('factures.rechercherPlaceholder')"
              >
            </div>
          </div>
        </div>
      </div>

      <!-- Messages -->
      <div v-if="message.texte" :class="['alert', message.type === 'success' ? 'alert-success' : 'alert-danger']">
        <i :class="message.type === 'success' ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle'" class="me-2"></i>
        {{ message.texte }}
      </div>

      <!-- Loading -->
      <div v-if="chargement" class="loading-section">
        <div class="text-center">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">{{ $t('commun.chargement') }}</span>
          </div>
          <p class="mt-2">{{ $t('factures.chargementFactures') }}</p>
        </div>
      </div>

      <!-- Liste des factures -->
      <div v-else-if="facturesFiltrees.length > 0" class="factures-grid">
        <div class="row">
          <div
            v-for="facture in facturesFiltrees"
            :key="facture.id"
            class="col-lg-6 col-xl-4 mb-4"
          >
            <div class="facture-card" :class="{ 'facture-echec': facture.statut === 'ECHEC' }">
              <!-- En-tête carte -->
              <div class="facture-card-header">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <h5 class="facture-numero">{{ facture.numeroFacture }}</h5>
                    <p class="facture-date">{{ formatDate(facture.dateEmission) }}</p>
                  </div>
                  <span :class="getStatutClass(facture.statut)">
                    {{ $t(`factures.statuts.${facture.statut}`) }}
                  </span>
                </div>
              </div>

              <!-- Corps carte -->
              <div class="facture-card-body">
                <div class="facture-info">
                  <div class="info-row">
                    <span class="info-label">{{ $t('factures.periode') }}:</span>
                    <span class="info-value">{{ facture.periode }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">{{ $t('factures.description') }}:</span>
                    <span class="info-value">{{ facture.description }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">{{ $t('factures.echeance') }}:</span>
                    <span class="info-value">{{ formatDate(facture.dateEcheance) }}</span>
                  </div>
                </div>

                <!-- Montant -->
                <div class="facture-montant">
                  <div class="montant-details">
                    <div class="montant-line">
                      <span>{{ $t('factures.montantHT') }}:</span>
                      <span>{{ formatMontant(facture.montantHT) }}</span>
                    </div>
                    <div class="montant-line">
                      <span>{{ $t('factures.tva') }} ({{ facture.tauxTva || 21 }}%):</span>
                      <span>{{ formatMontant(facture.tva) }}</span>
                    </div>
                    <div class="montant-total">
                      <span>{{ $t('factures.totalTTC') }}:</span>
                      <strong>{{ formatMontant(facture.montantTTC) }}</strong>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Actions carte -->
              <div class="facture-card-actions">
                <button
                  @click="voirFacture(facture)"
                  class="btn btn-primary btn-sm"
                  :title="$t('factures.voirDetail')"
                >
                  <i class="fas fa-eye me-2"></i>{{ $t('factures.voir') }}
                </button>

                <button
                  @click="telechargerPDF(facture)"
                  class="btn btn-success btn-sm"
                  :disabled="actionsEnCours[facture.id]?.pdf"
                  :title="$t('factures.telechargerPDF')"
                >
                  <span v-if="actionsEnCours[facture.id]?.pdf" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="fas fa-download me-2"></i>{{ $t('factures.telecharger') }}
                </button>

                <button
                  @click="envoyerParEmail(facture)"
                  class="btn btn-info btn-sm"
                  :disabled="actionsEnCours[facture.id]?.email"
                  :title="$t('factures.envoyerEmail')"
                >
                  <span v-if="actionsEnCours[facture.id]?.email" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="fas fa-envelope me-2"></i>{{ $t('factures.envoyer') }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="pagination.totalPages > 1" class="pagination-section">
          <nav aria-label="Navigation des factures">
            <ul class="pagination justify-content-center">
              <li class="page-item" :class="{ disabled: pagination.currentPage === 0 }">
                <button class="page-link" @click="changerPage(pagination.currentPage - 1)" :disabled="pagination.currentPage === 0">
                  <i class="fas fa-chevron-left"></i>
                </button>
              </li>

              <li
                v-for="page in pagesVisibles"
                :key="page"
                class="page-item"
                :class="{ active: page === pagination.currentPage }"
              >
                <button class="page-link" @click="changerPage(page)">{{ page + 1 }}</button>
              </li>

              <li class="page-item" :class="{ disabled: pagination.currentPage === pagination.totalPages - 1 }">
                <button class="page-link" @click="changerPage(pagination.currentPage + 1)" :disabled="pagination.currentPage === pagination.totalPages - 1">
                  <i class="fas fa-chevron-right"></i>
                </button>
              </li>
            </ul>
          </nav>
        </div>
      </div>

      <!-- Aucune facture trouvée -->
      <div v-else-if="!chargement" class="no-factures">
        <div class="text-center py-5">
          <i class="fas fa-file-invoice fa-3x text-muted mb-3"></i>
          <h4>{{ $t('factures.aucuneFacture') }}</h4>
          <p class="text-muted">{{ $t('factures.aucuneFactureDescription') }}</p>
          <button class="btn btn-primary" @click="reinitialiserFiltres">
            <i class="fas fa-refresh me-2"></i>{{ $t('factures.reinitialiserFiltres') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal pour afficher la facture détaillée -->
    <div v-if="factureSelectionnee" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ $t('factures.detailFacture') }} {{ factureSelectionnee.numeroFacture }}</h5>
            <button type="button" class="btn-close" @click="fermerModalFacture"></button>
          </div>
          <div class="modal-body p-0">
            <Facture
              :facture="factureSelectionnee"
              mode="direct"
              @action-terminee="onActionTerminee"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import factureService from '@/services/facture.service.js'
import Facture from '@/components/Facture.vue'

export default {
  name: 'FacturesView',
  components: {
    Facture
  },
  setup() {
    const { t, locale } = useI18n()

    // États réactifs
    const factures = ref([])
    const chargement = ref(false)
    const filtreStatut = ref('')
    const filtrePeriode = ref('')
    const rechercheTexte = ref('')
    const factureSelectionnee = ref(null)
    const actionsEnCours = ref({})

    const pagination = reactive({
      currentPage: 0,
      pageSize: 12,
      totalPages: 0,
      totalElements: 0
    })

    const statistiques = reactive({
      total: 0,
      montantTotal: 0
    })

    const message = reactive({
      texte: '',
      type: ''
    })

    // Computed
    const facturesFiltrees = computed(() => {
      let filtrees = factures.value

      // Filtre par statut
      if (filtreStatut.value) {
        filtrees = filtrees.filter(f => f.statut === filtreStatut.value)
      }

      // Filtre par période
      if (filtrePeriode.value) {
        filtrees = filtrees.filter(f => f.periode?.includes(filtrePeriode.value))
      }

      // Filtre par recherche
      if (rechercheTexte.value) {
        const recherche = rechercheTexte.value.toLowerCase()
        filtrees = filtrees.filter(f =>
          f.numeroFacture?.toLowerCase().includes(recherche) ||
          f.description?.toLowerCase().includes(recherche) ||
          f.periode?.toLowerCase().includes(recherche)
        )
      }

      return filtrees
    })

    const pagesVisibles = computed(() => {
      const pages = []
      const start = Math.max(0, pagination.currentPage - 2)
      const end = Math.min(pagination.totalPages - 1, pagination.currentPage + 2)

      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      return pages
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

    const getStatutClass = (statut) => {
      const classes = {
        'GENEREE': 'badge bg-success',
        'ECHEC': 'badge bg-danger',
        'EN_ATTENTE': 'badge bg-warning text-dark'
      }
      return classes[statut] || 'badge bg-secondary'
    }

    const chargerFactures = async (page = 0) => {
      try {
        chargement.value = true
        message.texte = ''

        const params = {
          page,
          size: pagination.pageSize,
          statut: filtreStatut.value || undefined,
          periode: filtrePeriode.value || undefined,
          recherche: rechercheTexte.value || undefined
        }

        const result = await factureService.getFactures(params)

        if (result.success) {
          factures.value = result.data.content || []
          pagination.currentPage = result.data.number || 0
          pagination.totalPages = result.data.totalPages || 0
          pagination.totalElements = result.data.totalElements || 0

          // Calcul des statistiques
          calculerStatistiques()
        } else {
          throw new Error(result.message || 'Erreur lors du chargement des factures')
        }
      } catch (error) {
        console.error('Erreur chargement factures:', error)
        afficherMessage(t('factures.erreurChargement'), 'error')
      } finally {
        chargement.value = false
      }
    }

    const calculerStatistiques = () => {
      statistiques.total = factures.value.length
      statistiques.montantTotal = factures.value.reduce((total, facture) => {
        return total + (facture.montantTTC || 0)
      }, 0)
    }

    const rechercherFactures = () => {
      // Debounce la recherche
      clearTimeout(window.rechercheTimeout)
      window.rechercheTimeout = setTimeout(() => {
        chargerFactures(0)
      }, 500)
    }

    const changerPage = (page) => {
      if (page >= 0 && page < pagination.totalPages) {
        chargerFactures(page)
      }
    }

    const voirFacture = (facture) => {
      factureSelectionnee.value = facture
    }

    const fermerModalFacture = () => {
      factureSelectionnee.value = null
    }

    const telechargerPDF = async (facture) => {
      try {
        if (!actionsEnCours.value[facture.id]) {
          actionsEnCours.value[facture.id] = {}
        }
        actionsEnCours.value[facture.id].pdf = true

        const result = await factureService.telechargerPDF(facture.id)

        if (result.success) {
          afficherMessage(t('factures.telechargementReussi'), 'success')
        } else {
          throw new Error(result.message || 'Erreur téléchargement')
        }
      } catch (error) {
        console.error('Erreur téléchargement PDF:', error)
        afficherMessage(t('factures.erreurTelechargement'), 'error')
      } finally {
        actionsEnCours.value[facture.id].pdf = false
      }
    }

    const envoyerParEmail = async (facture) => {
      try {
        if (!actionsEnCours.value[facture.id]) {
          actionsEnCours.value[facture.id] = {}
        }
        actionsEnCours.value[facture.id].email = true

        const result = await factureService.envoyerParEmail(facture.id)

        if (result.success) {
          afficherMessage(t('factures.emailEnvoye'), 'success')
        } else {
          throw new Error(result.message || 'Erreur envoi email')
        }
      } catch (error) {
        console.error('Erreur envoi email:', error)
        afficherMessage(t('factures.erreurEmail'), 'error')
      } finally {
        actionsEnCours.value[facture.id].email = false
      }
    }

    const reinitialiserFiltres = () => {
      filtreStatut.value = ''
      filtrePeriode.value = ''
      rechercheTexte.value = ''
      chargerFactures(0)
    }

    const onActionTerminee = (event) => {
      if (event.success) {
        afficherMessage(t(`factures.${event.type}Reussi`), 'success')
      } else {
        afficherMessage(t(`factures.erreur${event.type.charAt(0).toUpperCase() + event.type.slice(1)}`), 'error')
      }
    }

    // Watchers
    watch([filtreStatut, filtrePeriode], () => {
      chargerFactures(0)
    })

    // Cycle de vie
    onMounted(() => {
      chargerFactures()
    })

    return {
      // États
      factures,
      chargement,
      filtreStatut,
      filtrePeriode,
      rechercheTexte,
      factureSelectionnee,
      actionsEnCours,
      pagination,
      statistiques,
      message,

      // Computed
      facturesFiltrees,
      pagesVisibles,

      // Méthodes
      formatDate,
      formatMontant,
      getStatutClass,
      chargerFactures,
      rechercherFactures,
      changerPage,
      voirFacture,
      fermerModalFacture,
      telechargerPDF,
      envoyerParEmail,
      reinitialiserFiltres,
      onActionTerminee
    }
  }
}
</script>

<style scoped>
.factures-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 20px 0;
}

.page-header {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  margin-bottom: 30px;
}

.page-title {
  color: #2c3e50;
  font-weight: 700;
  margin-bottom: 8px;
}

.page-subtitle {
  color: #6c757d;
  margin-bottom: 0;
  font-size: 1.1rem;
}

.stats-summary {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2rem;
  font-weight: 700;
  color: #007bff;
  line-height: 1;
}

.stat-label {
  display: block;
  color: #6c757d;
  font-size: 0.9rem;
  margin-top: 4px;
}

.filters-section {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  margin-bottom: 30px;
}

.filter-group {
  margin-bottom: 0;
}

.filter-group label {
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
  display: block;
}

.form-select,
.form-control {
  border: 2px solid #e9ecef;
  border-radius: 8px;
  padding: 12px 16px;
  font-size: 0.95rem;
  transition: all 0.2s ease;
}

.form-select:focus,
.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.loading-section {
  background: white;
  padding: 60px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.factures-grid {
  min-height: 400px;
}

.facture-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.facture-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.facture-card.facture-echec {
  border-left: 4px solid #dc3545;
}

.facture-card-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.facture-numero {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.facture-date {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 0;
}

.facture-card-body {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.facture-info {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding: 8px 0;
  border-bottom: 1px solid #f8f9fa;
}

.info-label {
  font-weight: 500;
  color: #6c757d;
  font-size: 0.9rem;
}

.info-value {
  font-weight: 600;
  color: #495057;
  text-align: right;
  max-width: 60%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.facture-montant {
  margin-top: auto;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.montant-details {
  font-size: 0.9rem;
}

.montant-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  color: #6c757d;
}

.montant-total {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 2px solid #dee2e6;
  font-size: 1.1rem;
  color: #2c3e50;
}

.facture-card-actions {
  padding: 20px;
  border-top: 1px solid #e9ecef;
  background: #fafafa;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-sm {
  padding: 8px 12px;
  font-size: 0.85rem;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.2s ease;
  flex: 1;
  min-width: 0;
}

.btn-primary {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  border: none;
}

.btn-success {
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
  border: none;
}

.btn-info {
  background: linear-gradient(135deg, #17a2b8 0%, #117a8b 100%);
  border: none;
}

.btn:hover:not(:disabled) {
  transform: translateY(-1px);
}

.pagination-section {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  margin-top: 30px;
}

.pagination {
  margin-bottom: 0;
}

.page-link {
  border: 2px solid #e9ecef;
  color: #007bff;
  padding: 12px 16px;
  border-radius: 8px !important;
  margin: 0 4px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.page-link:hover {
  background: #007bff;
  border-color: #007bff;
  color: white;
  transform: translateY(-1px);
}

.page-item.active .page-link {
  background: #007bff;
  border-color: #007bff;
  color: white;
}

.no-factures {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.badge {
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.alert {
  border: none;
  border-radius: 12px;
  padding: 16px 20px;
  font-weight: 500;
  margin-bottom: 20px;
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

.spinner-border-sm {
  width: 14px;
  height: 14px;
}

.modal {
  z-index: 1060;
}

.modal-xl {
  max-width: 90vw;
}

/* Responsive */
@media (max-width: 768px) {
  .factures-view {
    padding: 10px;
  }

  .page-header,
  .filters-section {
    padding: 20px;
    margin-bottom: 20px;
  }

  .stats-summary {
    gap: 20px;
    margin-top: 20px;
  }

  .facture-card-actions {
    flex-direction: column;
  }

  .facture-card-actions .btn {
    margin-bottom: 8px;
  }

  .info-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .info-value {
    max-width: 100%;
    text-align: left;
    font-size: 0.95rem;
  }

  .stat-number {
    font-size: 1.5rem;
  }

  .page-title {
    font-size: 1.8rem;
  }
}

@media (max-width: 576px) {
  .filters-section .row {
    margin: 0;
  }

  .filters-section .col-md-4 {
    padding: 0;
    margin-bottom: 15px;
  }

  .stats-summary {
    flex-direction: column;
    gap: 15px;
    text-align: left;
  }

  .facture-card {
    margin-bottom: 20px;
  }

  .facture-card-header,
  .facture-card-body,
  .facture-card-actions {
    padding: 15px;
  }

  .montant-total {
    font-size: 1rem;
  }

  .btn-sm {
    padding: 10px 16px;
    font-size: 0.9rem;
  }

  .modal-xl {
    max-width: 95vw;
    margin: 10px auto;
  }

  .pagination {
    flex-wrap: wrap;
    justify-content: center;
  }

  .page-link {
    padding: 8px 12px;
    margin: 2px;
  }
}

/* Animations */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.facture-card {
  animation: fadeIn 0.5s ease-out;
}

.facture-card:nth-child(even) {
  animation-delay: 0.1s;
}

.facture-card:nth-child(3n) {
  animation-delay: 0.2s;
}

/* Print styles */
@media print {
  .page-header,
  .filters-section,
  .facture-card-actions,
  .pagination-section {
    display: none !important;
  }

  .facture-card {
    break-inside: avoid;
    box-shadow: none;
    border: 1px solid #ddd;
    margin-bottom: 20px;
  }

  .factures-view {
    background: white;
    padding: 0;
  }
}
</style>
