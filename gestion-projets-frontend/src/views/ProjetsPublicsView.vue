<template>
  <div class="projets-publics-view">
    <!-- Header simple -->
    <header class="bg-white shadow-sm">
      <div class="container py-3">
        <div class="d-flex justify-content-between align-items-center">
          <router-link to="/" class="d-flex align-items-center text-decoration-none text-dark">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 40px;">
            <h1 class="h5 fw-bold mb-0">CollabPro</h1>
          </router-link>

          <div class="text-muted small">
            {{ $t('commun.modeConsultation') }}
          </div>
        </div>
      </div>
    </header>

    <div class="container py-4">
      <!-- Titre simple -->
      <div class="text-center mb-4">
        <h2 class="h2 fw-bold text-primary mb-2">
          <i class="fas fa-globe me-2"></i>{{ $t('projetsPublics.titre') }}
        </h2>
        <p class="text-muted">
          {{ $t('projetsPublics.sousTitre') }}
        </p>
      </div>

      <!-- Filtres -->
      <div class="row mb-4">
        <div class="col-md-8">
          <input
            v-model="rechercheTexte"
            type="text"
            class="form-control"
            :placeholder="$t('commun.rechercher') + ' ' + $t('projets.nom').toLowerCase() + '...'"
          >
        </div>
        <div class="col-md-4">
          <select v-model="filtreStatut" class="form-select">
            <option value="">{{ $t('factures.tousLesStatuts') }}</option>
            <option value="ACTIF">{{ $t('projets.statuts.actif') }}</option>
            <option value="TERMINE">{{ $t('projets.statuts.termine') }}</option>
          </select>
        </div>
      </div>

      <!-- Compteur -->
      <div class="mb-4">
        <span class="badge bg-primary">
          {{ projetsFiltres.length }} {{ $t('projets.titre').toLowerCase() }} {{ $t('commun.trouve') }}
        </span>
      </div>

      <!-- Loading -->
      <div v-if="chargement" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
        <p class="mt-2 text-muted">{{ $t('commun.chargement') }}</p>
      </div>

      <!-- Erreur -->
      <div v-else-if="erreur" class="alert alert-danger">
        <i class="fas fa-exclamation-triangle me-2"></i>{{ erreur }}
      </div>

      <!-- Liste des projets -->
      <div v-else-if="projetsFiltres.length > 0" class="row g-3">
        <div v-for="projet in projetsFiltres" :key="projet.id" class="col-md-6 col-lg-4">
          <div class="card h-100 shadow-sm">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <h6 class="card-title fw-bold mb-1">{{ projet.titre }}</h6>
                <span :class="getStatutBadgeClass(projet.statut)" class="badge">
                  {{ $t(`projets.statuts.${projet.statut.toLowerCase()}`) }}
                </span>
              </div>

              <p class="card-text text-muted small mb-3" style="min-height: 40px;">
                {{ projet.description
                ? projet.description.substring(0, 100) + (projet.description.length > 100 ? '...' : '')
                : $t('commun.aucuneDescription') }}
              </p>

              <div class="border-top pt-2">
                <div class="d-flex justify-content-between align-items-center">
                  <small class="text-muted">
                    <i class="fas fa-user me-1"></i>{{ projet.createurNom || $t('commun.anonyme') }}
                  </small>
                  <small class="text-muted">
                    <i class="fas fa-calendar me-1"></i>{{ formatDate(projet.dateCreation) }}
                  </small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- État vide -->
      <div v-else class="text-center py-5">
        <i class="fas fa-folder-open fa-3x text-muted mb-3 opacity-50"></i>
        <h5 class="text-muted">{{ $t('projetsPublics.aucuneProjetTrouve') }}</h5>
        <p class="text-muted mb-4">
          {{ rechercheTexte || filtreStatut
          ? $t('commun.aucunResultat')
          : $t('projetsPublics.aucunProjetDescription') }}
        </p>
        <button
          v-if="rechercheTexte || filtreStatut"
          @click="reinitialiserFiltres"
          class="btn btn-outline-primary">
          {{ $t('factures.reinitialiserFiltres') }}
        </button>
      </div>

      <!-- Message info avec lien fonctionnel -->
      <div v-if="!chargement" class="alert alert-info mt-4">
        <div class="text-center">
          <i class="fas fa-info-circle me-2"></i>
          {{ $t('projetsPublics.modeConsultationDescription') }}
          <router-link to="/connexion" class="text-decoration-none fw-bold ms-1">
            {{ $t('nav.connexion') }}
          </router-link>
          {{ $t('commun.pourParticiper') }} !
        </div>
      </div>

      <!-- Retour accueil -->
      <div class="text-center mt-4">
        <router-link to="/" class="btn btn-outline-secondary">
          <i class="fas fa-arrow-left me-2"></i>{{ $t('commun.retour') }} {{ $t('nav.accueil').toLowerCase() }}
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { projectAPI } from '@/services/api.js'

export default {
  name: 'ProjetsPublicsView',
  setup() {
    const { t, locale } = useI18n()

    // État du composant
    const projets = ref([])
    const chargement = ref(false)
    const erreur = ref(null)
    const rechercheTexte = ref('')
    const filtreStatut = ref('')
    const currentLocale = ref(locale.value)

    // Computed
    const projetsFiltres = computed(() => {
      let filtres = projets.value

      if (rechercheTexte.value) {
        const recherche = rechercheTexte.value.toLowerCase()
        filtres = filtres.filter(projet =>
          projet.titre?.toLowerCase().includes(recherche) ||
          projet.description?.toLowerCase().includes(recherche)
        )
      }

      if (filtreStatut.value) {
        filtres = filtres.filter(projet => projet.statut === filtreStatut.value)
      }

      return filtres
    })

    // Méthodes
    const chargerProjets = async () => {
      try {
        chargement.value = true
        erreur.value = null

        const response = await projectAPI.getPublics()

        projets.value = (response.data || [])
          .map(projet => ({
            ...projet,
            createurNom:
              (projet.createurPrenom && projet.createurNom)
                ? `${projet.createurPrenom} ${projet.createurNom}`
                : t('commun.utilisateur')
          }))
          .sort((a, b) => new Date(b.dateCreation) - new Date(a.dateCreation))
      } catch (error) {
        console.error('Erreur chargement projets publics:', error)
        erreur.value = t('projetsPublics.erreurChargement')
      } finally {
        chargement.value = false
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      const localeString = locale.value === 'fr' ? 'fr-FR' : 'en-US'
      return new Date(dateString).toLocaleDateString(localeString, {
        day: '2-digit',
        month: 'long',
        year: 'numeric'
      })
    }

    const getStatutBadgeClass = (statut) => {
      const classes = {
        'ACTIF': 'bg-success',
        'TERMINE': 'bg-primary',
        'PAUSE': 'bg-warning text-dark',
        'ANNULE': 'bg-danger'
      }
      return classes[statut] || 'bg-secondary'
    }

    const reinitialiserFiltres = () => {
      rechercheTexte.value = ''
      filtreStatut.value = ''
    }

    const changeLanguage = () => {
      locale.value = currentLocale.value
      localStorage.setItem('langue', currentLocale.value)
      chargerProjets()
    }

    onMounted(() => {
      chargerProjets()
    })

    return {
      projets,
      chargement,
      erreur,
      rechercheTexte,
      filtreStatut,
      projetsFiltres,
      currentLocale,
      formatDate,
      getStatutBadgeClass,
      reinitialiserFiltres,
      changeLanguage,
      t
    }
  }
}
</script>

<style scoped>
.projets-publics-view {
  min-height: 100vh;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
}

.card {
  transition: transform 0.2s ease;
  border-radius: 8px;
}

.card:hover {
  transform: translateY(-2px);
}

.badge {
  font-size: 0.7rem;
  padding: 0.3rem 0.6rem;
}

footer {
  margin-top: auto;
}

.form-select.bg-dark {
  background-color: #212529 !important;
  border-color: #6c757d;
}

.form-select.bg-dark:focus {
  background-color: #212529 !important;
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}
</style>
