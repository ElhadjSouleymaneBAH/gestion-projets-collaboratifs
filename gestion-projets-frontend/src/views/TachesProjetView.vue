<template>
  <div class="container-fluid py-4">
    <!-- En-tête -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h1 class="h3 fw-bold text-primary mb-1">
          <i class="fas fa-tasks me-2"></i>{{ t('taches.liste.titre') }}
        </h1>
        <p class="text-muted mb-0">{{ t('taches.liste.description') }}</p>
      </div>
      <button
        v-if="estChefProjet"
        class="btn btn-primary"
        @click="creerNouvelleTache"
      >
        <i class="fas fa-plus-circle me-2"></i>{{ t('taches.creer') }}
      </button>
    </div>

    <!-- Filtres -->
    <div class="card shadow-sm mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <label class="form-label fw-semibold">
              <i class="fas fa-filter me-1"></i>{{ t('taches.filtres.statut') }}
            </label>
            <select class="form-select" v-model="filtreStatut" @change="filtrerTaches">
              <option value="">{{ t('taches.filtres.tous') }}</option>
              <option value="BROUILLON">{{ t('taches.statuts.brouillon') }}</option>
              <option value="EN_ATTENTE_VALIDATION">{{ t('taches.statuts.enAttente') }}</option>
              <option value="TERMINE">{{ t('taches.statuts.termine') }}</option>
              <option value="ANNULE">{{ t('taches.statuts.annule') }}</option>
            </select>
          </div>

          <div class="col-md-4">
            <label class="form-label fw-semibold">
              <i class="fas fa-search me-1"></i>{{ t('taches.filtres.recherche') }}
            </label>
            <input
              type="text"
              class="form-control"
              v-model="rechercheTexte"
              :placeholder="t('taches.filtres.placeholderRecherche')"
              @input="filtrerTaches"
            >
          </div>

          <div class="col-md-4">
            <label class="form-label fw-semibold">
              <i class="fas fa-sort me-1"></i>{{ t('taches.filtres.tri') }}
            </label>
            <select class="form-select" v-model="triOrdre" @change="trierTaches">
              <option value="recent">{{ t('taches.filtres.plusRecent') }}</option>
              <option value="ancien">{{ t('taches.filtres.plusAncien') }}</option>
              <option value="titre">{{ t('taches.filtres.parTitre') }}</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Loader -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;"></div>
      <p class="text-muted mt-3">{{ t('commun.chargement') }}</p>
    </div>

    <!-- Liste -->
    <div v-else>
      <!-- Message si vide -->
      <div v-if="tachesFiltrees.length === 0" class="text-center py-5">
        <i class="fas fa-inbox fa-4x text-muted mb-3" style="opacity: 0.3;"></i>
        <h5 class="text-muted">{{ t('taches.liste.aucuneTache') }}</h5>
        <p class="text-muted">
          {{ rechercheTexte || filtreStatut
          ? t('taches.liste.aucunResultatFiltre')
          : t('taches.liste.commencerCreer') }}
        </p>
        <button
          v-if="estChefProjet && !rechercheTexte && !filtreStatut"
          class="btn btn-primary mt-3"
          @click="creerNouvelleTache"
        >
          <i class="fas fa-plus-circle me-2"></i>{{ t('taches.creerPremiere') }}
        </button>
      </div>

      <!-- Grille de tâches -->
      <div v-else class="row g-4">
        <div
          v-for="tache in tachesFiltrees"
          :key="tache.id"
          class="col-md-6 col-lg-4"
        >
          <div class="card h-100 shadow-sm hover-card">
            <div class="card-header border-0 d-flex justify-content-between align-items-center">
              <span :class="['badge', getStatutBadgeClass(tache.statut)]">
                <i :class="getStatutIcon(tache.statut)" class="me-1"></i>
                {{ translateTaskStatus(tache.statut) }}
              </span>
              <div class="dropdown" v-if="peutGererTache(tache)">
                <button
                  class="btn btn-sm btn-light"
                  type="button"
                  data-bs-toggle="dropdown"
                >
                  <i class="fas fa-ellipsis-v"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end">
                  <li>
                    <a class="dropdown-item" href="#" @click.prevent="modifierTache(tache.id)">
                      <i class="fas fa-edit me-2"></i>{{ t('commun.modifier') }}
                    </a>
                  </li>
                  <li>
                    <a class="dropdown-item text-danger" href="#" @click.prevent="supprimerTache(tache.id)">
                      <i class="fas fa-trash me-2"></i>{{ t('commun.supprimer') }}
                    </a>
                  </li>
                </ul>
              </div>
            </div>

            <div class="card-body">
              <h5 class="card-title fw-bold mb-3">
                <i class="fas fa-file-alt text-primary me-2"></i>{{ tache.titre }}
              </h5>
              <p class="card-text text-muted small" style="min-height: 60px;">
                {{ tache.description || t('commun.aucuneDescription') }}
              </p>

              <div class="mt-3 pt-3 border-top">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <small class="text-muted">
                    <i class="far fa-calendar-alt me-1"></i>{{ t('taches.liste.dateCreation') }}
                  </small>
                  <small class="fw-semibold">{{ formatDate(tache.dateCreation) }}</small>
                </div>

                <div class="d-flex justify-content-between align-items-center">
                  <small class="text-muted">
                    <i class="far fa-user me-1"></i>{{ t('taches.liste.assigneeA') }}
                  </small>
                  <small class="fw-semibold">{{ getAssigneName(tache) }}</small>
                </div>
              </div>
            </div>

            <div class="card-footer bg-light border-0">
              <router-link
                :to="`/taches/${tache.id}`"
                class="btn btn-outline-primary btn-sm w-100"
              >
                <i class="fas fa-eye me-2"></i>{{ t('taches.voirDetails') }}
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- Info résultats -->
      <div v-if="tachesFiltrees.length > 0" class="mt-4 text-center text-muted">
        <small>
          {{ t('commun.affichageResultats', {
          debut: 1,
          fin: tachesFiltrees.length,
          total: taches.length
        }) }}
        </small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useDataTranslation } from '@/composables/useDataTranslation'
import { taskAPI } from '@/services/api'

const { t, locale } = useI18n()
const { translateTaskStatus, translatePriority } = useDataTranslation()
const route = useRoute()
const router = useRouter()

const projetId = computed(() => route.params.idProjet || route.params.projetId)
const loading = ref(true)
const taches = ref([])
const tachesFiltrees = ref([])

// Filtres
const filtreStatut = ref('')
const rechercheTexte = ref('')
const triOrdre = ref('recent')

const utilisateurActuel = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('user')) || {}
  } catch {
    return {}
  }
})

const estChefProjet = computed(() => {
  return utilisateurActuel.value?.role === 'CHEF_PROJET' ||
    utilisateurActuel.value?.role === 'ADMINISTRATEUR'
})

// ========== CHARGEMENT ==========
onMounted(async () => {
  await chargerTaches()
})

const chargerTaches = async () => {
  try {
    loading.value = true
    const response = await taskAPI.getByProjet(projetId.value)
    taches.value = Array.isArray(response.data) ? response.data : []
    tachesFiltrees.value = [...taches.value]
    trierTaches()
  } catch (error) {
    console.error('Erreur chargement tâches:', error)
    taches.value = []
    tachesFiltrees.value = []
  } finally {
    loading.value = false
  }
}

// ========== FILTRAGE ==========
const filtrerTaches = () => {
  let resultats = [...taches.value]

  // Filtre par statut
  if (filtreStatut.value) {
    resultats = resultats.filter(t =>
      String(t.statut).toUpperCase() === filtreStatut.value.toUpperCase()
    )
  }

  // Filtre par recherche
  if (rechercheTexte.value.trim()) {
    const search = rechercheTexte.value.toLowerCase()
    resultats = resultats.filter(t =>
      t.titre?.toLowerCase().includes(search) ||
      t.description?.toLowerCase().includes(search)
    )
  }

  tachesFiltrees.value = resultats
  trierTaches()
}

// ========== TRI ==========
const trierTaches = () => {
  const ordre = triOrdre.value

  tachesFiltrees.value.sort((a, b) => {
    switch (ordre) {
      case 'recent':
        return new Date(b.dateCreation) - new Date(a.dateCreation)
      case 'ancien':
        return new Date(a.dateCreation) - new Date(b.dateCreation)
      case 'titre':
        return (a.titre || '').localeCompare(b.titre || '')
      default:
        return 0
    }
  })
}

// ========== FORMATAGE ==========
const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  try {
    return new Date(dateStr).toLocaleDateString(locale.value || 'fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    })
  } catch {
    return '—'
  }
}

const getAssigneName = (tache) => {
  if (!tache.assigne) return t('taches.details.nonDefini')
  return `${tache.assigne.prenom || ''} ${tache.assigne.nom || ''}`.trim() ||
    tache.assigne.email ||
    t('taches.details.nonDefini')
}

// ========== STATUTS ==========
const mapStatutKey = (statut) => {
  const s = String(statut || '').toUpperCase()
  const mapping = {
    'BROUILLON': 'brouillon',
    'EN_ATTENTE_VALIDATION': 'enAttente',
    'TERMINE': 'termine',
    'TERMINÉ': 'termine',
    'ANNULE': 'annule',
    'ANNULÉ': 'annule'
  }
  return mapping[s] || 'enAttente'
}

const getStatutBadgeClass = (statut) => {
  const s = String(statut || '').toUpperCase()
  const classes = {
    'BROUILLON': 'bg-secondary',
    'EN_ATTENTE_VALIDATION': 'bg-warning text-dark',
    'TERMINE': 'bg-success',
    'TERMINÉ': 'bg-success',
    'ANNULE': 'bg-danger',
    'ANNULÉ': 'bg-danger'
  }
  return classes[s] || 'bg-info'
}

const getStatutIcon = (statut) => {
  const s = String(statut || '').toUpperCase()
  const icons = {
    'BROUILLON': 'fas fa-file',
    'EN_ATTENTE_VALIDATION': 'fas fa-clock',
    'TERMINE': 'fas fa-check-circle',
    'TERMINÉ': 'fas fa-check-circle',
    'ANNULE': 'fas fa-times-circle',
    'ANNULÉ': 'fas fa-times-circle'
  }
  return icons[s] || 'fas fa-circle'
}

// ========== PERMISSIONS ==========
const peutGererTache = (tache) => {
  if (estChefProjet.value) return true
  return tache.idAssigne === utilisateurActuel.value?.id
}

// ========== ACTIONS ==========
const creerNouvelleTache = () => {
  router.push(`/projets/${projetId.value}/taches/creer`)
}

const modifierTache = (tacheId) => {
  router.push(`/taches/${tacheId}/modifier`)
}

const supprimerTache = async (tacheId) => {
  if (!confirm(t('taches.confirmerSuppression'))) return

  try {
    await taskAPI.delete(tacheId)
    taches.value = taches.value.filter(t => t.id !== tacheId)
    filtrerTaches()
  } catch (error) {
    console.error('Erreur suppression tâche:', error)
    alert(t('erreurs.suppressionTache'))
  }
}
</script>

<style scoped>
.hover-card {
  transition: all 0.3s ease;
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
}

.hover-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
}

.card-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 1rem 1.25rem;
}

.card-footer {
  padding: 1rem 1.25rem;
}
</style>
