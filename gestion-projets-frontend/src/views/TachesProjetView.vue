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
      <div class="d-flex gap-2">
        <!-- Toggle Vue -->
        <div class="btn-group" role="group">
          <button
            class="btn"
            :class="modeAffichage === 'liste' ? 'btn-primary' : 'btn-outline-primary'"
            @click="modeAffichage = 'liste'"
          >
            <i class="fas fa-list me-2"></i>{{ t('taches.vue.liste') }}
          </button>
          <button
            class="btn"
            :class="modeAffichage === 'kanban' ? 'btn-primary' : 'btn-outline-primary'"
            @click="modeAffichage = 'kanban'"
          >
            <i class="fas fa-columns me-2"></i>{{ t('taches.vue.kanban') }}
          </button>
        </div>

        <button
          v-if="estChefProjet"
          class="btn btn-primary"
          @click="creerNouvelleTache"
        >
          <i class="fas fa-plus-circle me-2"></i>{{ t('taches.creer') }}
        </button>
      </div>
    </div>

    <!-- VUE KANBAN -->
    <div v-if="modeAffichage === 'kanban'">
      <KanbanBoard :projetId="projetId" />
    </div>

    <!-- VUE LISTE -->
    <div v-else>
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
                  {{ translateData('taskStatus', tache.statut) }}
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
                      <a class="dropdown-item" href="#" @click.prevent="modifierTache(tache)">
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

                <!-- Priorité et Date d'échéance -->
                <div class="d-flex gap-2 mb-3">
                  <span v-if="tache.priorite" :class="['badge', getPrioriteBadgeClass(tache.priorite)]">
                    <i class="fas fa-flag me-1"></i>{{ translateData('priority', tache.priorite) }}
                  </span>
                  <span v-if="tache.dateEcheance" :class="['badge', getEcheanceBadgeClass(tache.dateEcheance)]">
                    <i class="fas fa-calendar-day me-1"></i>{{ formatDate(tache.dateEcheance) }}
                  </span>
                </div>

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

    <!-- ========== MODAL CRÉATION TÂCHE ========== -->
    <div v-if="modalCreationTache" class="modal d-block" style="background: rgba(0,0,0,0.5); z-index: 1060">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title">
              <i class="fas fa-plus-circle me-2"></i>{{ t('taches.creer') }}
            </h5>
            <button class="btn-close btn-close-white" @click="modalCreationTache = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label fw-semibold">
                {{ t('taches.titre') }} <span class="text-danger">*</span>
              </label>
              <input
                class="form-control"
                v-model="nouvelleTache.titre"
                maxlength="120"
                :placeholder="t('taches.placeholders.titre') || 'Titre de la tâche'"
                required
              />
            </div>

            <div class="mb-3">
              <label class="form-label fw-semibold">{{ t('taches.description') }}</label>
              <textarea
                class="form-control"
                v-model="nouvelleTache.description"
                rows="4"
                :placeholder="t('taches.placeholders.description') || 'Description détaillée de la tâche...'"
              ></textarea>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">
                  <i class="fas fa-flag me-1"></i>{{ t('taches.priorite') }}
                </label>
                <select class="form-select" v-model="nouvelleTache.priorite">
                  <option value="BASSE">🟢 {{ t('taches.priorites.basse') || 'Basse' }}</option>
                  <option value="NORMALE">🔵 {{ t('taches.priorites.normale') || 'Normale' }}</option>
                  <option value="HAUTE">🟠 {{ t('taches.priorites.haute') || 'Haute' }}</option>
                  <option value="URGENTE">🔴 {{ t('taches.priorites.urgente') || 'Urgente' }}</option>
                </select>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">
                  <i class="fas fa-calendar-day me-1"></i>{{ t('taches.dateEcheance') }}
                </label>
                <input
                  type="date"
                  class="form-control"
                  v-model="nouvelleTache.dateEcheance"
                  :min="today"
                />
              </div>
            </div>

            <!-- Aperçu -->
            <div v-if="nouvelleTache.titre" class="alert alert-light border mt-3">
              <small class="text-muted d-block mb-1">Aperçu :</small>
              <strong>{{ nouvelleTache.titre }}</strong>
              <span v-if="nouvelleTache.priorite" :class="['badge ms-2', getPrioriteBadgeClass(nouvelleTache.priorite)]">
                {{ nouvelleTache.priorite }}
              </span>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="modalCreationTache = false">
              <i class="fas fa-times me-1"></i>{{ t('commun.annuler') }}
            </button>
            <button
              class="btn btn-primary"
              @click="sauvegarderNouvelleTache"
              :disabled="!nouvelleTache.titre.trim() || enCoursSauvegarde"
            >
              <span v-if="enCoursSauvegarde" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="fas fa-check me-1"></i>
              {{ t('commun.creer') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== MODAL MODIFICATION TÂCHE ========== -->
    <div v-if="modalModificationTache" class="modal d-block" style="background: rgba(0,0,0,0.5); z-index: 1060">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-warning text-dark">
            <h5 class="modal-title">
              <i class="fas fa-edit me-2"></i>{{ t('taches.modifier') || 'Modifier la tâche' }}
            </h5>
            <button class="btn-close" @click="modalModificationTache = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label fw-semibold">
                {{ t('taches.titre') }} <span class="text-danger">*</span>
              </label>
              <input
                class="form-control"
                v-model="tacheEnModification.titre"
                maxlength="120"
                required
              />
            </div>

            <div class="mb-3">
              <label class="form-label fw-semibold">{{ t('taches.description') }}</label>
              <textarea
                class="form-control"
                v-model="tacheEnModification.description"
                rows="4"
              ></textarea>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">
                  <i class="fas fa-flag me-1"></i>{{ t('taches.priorite') }}
                </label>
                <select class="form-select" v-model="tacheEnModification.priorite">
                  <option value="BASSE">🟢 {{ t('taches.priorites.basse') || 'Basse' }}</option>
                  <option value="NORMALE">🔵 {{ t('taches.priorites.normale') || 'Normale' }}</option>
                  <option value="HAUTE">🟠 {{ t('taches.priorites.haute') || 'Haute' }}</option>
                  <option value="URGENTE">🔴 {{ t('taches.priorites.urgente') || 'Urgente' }}</option>
                </select>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label fw-semibold">
                  <i class="fas fa-calendar-day me-1"></i>{{ t('taches.dateEcheance') }}
                </label>
                <input
                  type="date"
                  class="form-control"
                  v-model="tacheEnModification.dateEcheance"
                />
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="modalModificationTache = false">
              <i class="fas fa-times me-1"></i>{{ t('commun.annuler') }}
            </button>
            <button
              class="btn btn-warning"
              @click="sauvegarderModificationTache"
              :disabled="!tacheEnModification.titre?.trim() || enCoursSauvegarde"
            >
              <span v-if="enCoursSauvegarde" class="spinner-border spinner-border-sm me-1"></span>
              <i v-else class="fas fa-save me-1"></i>
              {{ t('commun.enregistrer') || 'Enregistrer' }}
            </button>
          </div>
        </div>
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
import KanbanBoard from '@/components/KanbanBoard.vue'

const { t, locale } = useI18n()
const { translateData } = useDataTranslation()
const route = useRoute()
const router = useRouter()

const projetId = computed(() => route.params.idProjet || route.params.projetId)
const loading = ref(true)
const taches = ref([])
const tachesFiltrees = ref([])

// Mode d'affichage
const modeAffichage = ref('liste')

// Filtres
const filtreStatut = ref('')
const rechercheTexte = ref('')
const triOrdre = ref('recent')

// Modal création
const modalCreationTache = ref(false)
const enCoursSauvegarde = ref(false)
const nouvelleTache = ref({
  titre: '',
  description: '',
  priorite: 'NORMALE',
  dateEcheance: null
})

// Modal modification
const modalModificationTache = ref(false)
const tacheEnModification = ref({
  id: null,
  titre: '',
  description: '',
  priorite: 'NORMALE',
  dateEcheance: null
})

// Date minimum pour l'échéance
const today = computed(() => {
  return new Date().toISOString().split('T')[0]
})

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
    const response = await taskAPI.byProjet(projetId.value)
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

  if (filtreStatut.value) {
    resultats = resultats.filter(t =>
      String(t.statut).toUpperCase() === filtreStatut.value.toUpperCase()
    )
  }

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

// ========== PRIORITÉS ==========
const getPrioriteBadgeClass = (priorite) => {
  const p = String(priorite || '').toUpperCase()
  const classes = {
    'BASSE': 'bg-success',
    'NORMALE': 'bg-info',
    'HAUTE': 'bg-warning text-dark',
    'URGENTE': 'bg-danger'
  }
  return classes[p] || 'bg-secondary'
}

// ========== ÉCHÉANCE ==========
const getEcheanceBadgeClass = (dateEcheance) => {
  if (!dateEcheance) return 'bg-secondary'
  const echeance = new Date(dateEcheance)
  const maintenant = new Date()
  const diffJours = Math.ceil((echeance - maintenant) / (1000 * 60 * 60 * 24))

  if (diffJours < 0) return 'bg-danger' // En retard
  if (diffJours <= 2) return 'bg-warning text-dark' // Proche
  return 'bg-light text-dark' // OK
}

// ========== PERMISSIONS ==========
const peutGererTache = (tache) => {
  if (estChefProjet.value) return true
  return tache.idAssigne === utilisateurActuel.value?.id
}

// ========== ACTIONS CRÉATION ==========
const creerNouvelleTache = () => {
  nouvelleTache.value = {
    titre: '',
    description: '',
    priorite: 'NORMALE',
    dateEcheance: null
  }
  modalCreationTache.value = true
}

const sauvegarderNouvelleTache = async () => {
  if (!nouvelleTache.value.titre.trim()) return

  enCoursSauvegarde.value = true
  try {
    await taskAPI.create({
      titre: nouvelleTache.value.titre.trim(),
      description: nouvelleTache.value.description?.trim() || '',
      idProjet: projetId.value,
      priorite: nouvelleTache.value.priorite,
      dateEcheance: nouvelleTache.value.dateEcheance || null,
      statut: 'BROUILLON'
    })

    modalCreationTache.value = false
    await chargerTaches()
    alert(t('taches.creee') || 'Tâche créée avec succès !')
  } catch (error) {
    console.error('Erreur création tâche:', error)
    alert(t('erreurs.creationTache') || 'Erreur lors de la création de la tâche')
  } finally {
    enCoursSauvegarde.value = false
  }
}

// ========== ACTIONS MODIFICATION ==========
const modifierTache = (tache) => {
  tacheEnModification.value = {
    id: tache.id,
    titre: tache.titre || '',
    description: tache.description || '',
    priorite: tache.priorite || 'NORMALE',
    dateEcheance: tache.dateEcheance ? tache.dateEcheance.split('T')[0] : null,
    statut: tache.statut
  }
  modalModificationTache.value = true
}

const sauvegarderModificationTache = async () => {
  if (!tacheEnModification.value.titre?.trim()) return

  enCoursSauvegarde.value = true
  try {
    await taskAPI.update(tacheEnModification.value.id, {
      titre: tacheEnModification.value.titre.trim(),
      description: tacheEnModification.value.description?.trim() || '',
      priorite: tacheEnModification.value.priorite,
      dateEcheance: tacheEnModification.value.dateEcheance || null,
      statut: tacheEnModification.value.statut,
      projetId: projetId.value
    })

    modalModificationTache.value = false
    await chargerTaches()
    alert(t('taches.modifiee') || 'Tâche modifiée avec succès !')
  } catch (error) {
    console.error('Erreur modification tâche:', error)
    alert(t('erreurs.modificationTache') || 'Erreur lors de la modification')
  } finally {
    enCoursSauvegarde.value = false
  }
}

// ========== SUPPRESSION ==========
const supprimerTache = async (tacheId) => {
  if (!confirm(t('taches.confirmerSuppression') || 'Êtes-vous sûr de vouloir supprimer cette tâche ?')) return

  try {
    await taskAPI.delete(tacheId)
    taches.value = taches.value.filter(t => t.id !== tacheId)
    filtrerTaches()
    alert(t('taches.supprimee') || 'Tâche supprimée avec succès')
  } catch (error) {
    console.error('Erreur suppression tâche:', error)
    alert(t('erreurs.suppressionTache') || 'Erreur lors de la suppression')
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

/* Toggle Vue */
.btn-group .btn {
  border-radius: 8px;
}

.btn-group .btn:first-child {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.btn-group .btn:last-child {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

/* Modal */
.modal {
  backdrop-filter: blur(4px);
}

.modal-content {
  border-radius: 12px;
  border: none;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  border-radius: 12px 12px 0 0;
}

/* Badges priorité */
.badge {
  font-weight: 500;
  padding: 0.35em 0.65em;
}
</style>
