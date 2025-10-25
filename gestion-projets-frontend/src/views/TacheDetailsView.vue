<template>
  <div class="container py-4">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-3">
      <ol class="breadcrumb">
        <li class="breadcrumb-item">
          <router-link to="/dashboard" class="text-decoration-none">
            <i class="fas fa-home me-1"></i>{{ t('navigation.accueil') }}
          </router-link>
        </li>
        <li class="breadcrumb-item">
          <router-link :to="`/projets/${tache?.idProjet}/taches`" class="text-decoration-none">
            {{ t('navigation.taches') }}
          </router-link>
        </li>
        <li class="breadcrumb-item active">{{ tache?.titre || t('taches.details.titre') }}</li>
      </ol>
    </nav>

    <!-- Loader -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;"></div>
      <p class="text-muted mt-3">{{ t('commun.chargement') }}</p>
    </div>

    <!-- Erreur -->
    <div v-else-if="!tache" class="alert alert-danger">
      <i class="fas fa-exclamation-triangle me-2"></i>
      {{ t('taches.details.introuvable') }}
    </div>

    <!-- Contenu -->
    <div v-else>
      <!-- En-tête -->
      <div class="card shadow-sm mb-4">
        <div class="card-body">
          <div class="row align-items-start">
            <div class="col-lg-8">
              <h1 class="h3 mb-3">
                <i class="fas fa-tasks text-primary me-2"></i>{{ tache.titre }}
              </h1>
              <div class="d-flex flex-wrap gap-2 align-items-center">
                <span :class="['badge fs-6 px-3 py-2', getStatutBadgeClass(tache.statut)]">
                  <i :class="getStatutIcon(tache.statut)" class="me-1"></i>
                  {{ t(`taches.statuts.${mapStatutKey(tache.statut)}`) }}
                </span>
                <small class="text-muted">
                  <i class="far fa-calendar-alt me-1"></i>
                  {{ t('taches.details.dateCreation') }} : {{ formatDate(tache.dateCreation) }}
                </small>
              </div>
            </div>

            <!-- Actions -->
            <div class="col-lg-4 text-lg-end mt-3 mt-lg-0">
              <div class="btn-group-vertical" role="group" v-if="peutGererTache">
                <button
                  v-if="peutModifier"
                  class="btn btn-outline-primary btn-sm"
                  @click="modifierTache"
                >
                  <i class="fas fa-edit me-1"></i>{{ t('commun.modifier') }}
                </button>

                <button
                  v-if="peutSoumettre"
                  class="btn btn-primary btn-sm"
                  @click="soumettreValidation"
                  :disabled="actionEnCours"
                >
                  <span v-if="actionEnCours" class="spinner-border spinner-border-sm me-1"></span>
                  <i v-else class="fas fa-paper-plane me-1"></i>
                  {{ t('taches.actions.soumettre') }}
                </button>

                <button
                  v-if="peutValider"
                  class="btn btn-success btn-sm"
                  @click="validerTache"
                  :disabled="actionEnCours"
                >
                  <span v-if="actionEnCours" class="spinner-border spinner-border-sm me-1"></span>
                  <i v-else class="fas fa-check me-1"></i>
                  {{ t('taches.actions.valider') }}
                </button>

                <button
                  v-if="peutAnnuler"
                  class="btn btn-outline-danger btn-sm"
                  @click="annulerTache"
                  :disabled="actionEnCours"
                >
                  <span v-if="actionEnCours" class="spinner-border spinner-border-sm me-1"></span>
                  <i v-else class="fas fa-times me-1"></i>
                  {{ t('taches.actions.annuler') }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Alertes -->
      <div v-if="flash.text" :class="['alert alert-dismissible fade show', `alert-${flash.type}`]">
        <i :class="flash.type === 'success' ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle'" class="me-2"></i>
        {{ flash.text }}
        <button type="button" class="btn-close" @click="flash.text = ''"></button>
      </div>

      <div class="row">
        <!-- Contenu principal -->
        <div class="col-lg-8">
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light">
              <h5 class="mb-0">
                <i class="fas fa-info-circle me-2"></i>{{ t('taches.details.description') }}
              </h5>
            </div>
            <div class="card-body">
              <p class="mb-0" style="white-space: pre-wrap; line-height: 1.6;">
                {{ tache.description || t('commun.aucuneDescription') }}
              </p>
            </div>
          </div>

          <!--  F12: Intégration des commentaires -->
          <div class="card shadow-sm">
            <div class="card-header bg-light">
              <h5 class="mb-0">
                <i class="fas fa-comments me-2"></i>{{ t('taches.commentaires.titre') }}
              </h5>
            </div>
            <div class="card-body">
              <Commentaires :tache-id="tacheId" />
            </div>
          </div>
        </div>

        <!-- Métadonnées -->
        <div class="col-lg-4">
          <div class="card shadow-sm mb-4">
            <div class="card-header bg-light">
              <h6 class="mb-0">
                <i class="fas fa-clipboard-list me-2"></i>{{ t('taches.details.informations') }}
              </h6>
            </div>
            <div class="card-body">
              <dl class="row mb-0">
                <dt class="col-5 text-muted small">
                  <i class="fas fa-hashtag me-1"></i>{{ t('taches.details.id') }}
                </dt>
                <dd class="col-7 small fw-semibold">#{{ tache.id }}</dd>

                <dt class="col-5 text-muted small">
                  <i class="fas fa-signal me-1"></i>{{ t('taches.details.statut') }}
                </dt>
                <dd class="col-7 small">
                  <span :class="['badge', getStatutBadgeClass(tache.statut)]">
                    {{ t(`taches.statuts.${mapStatutKey(tache.statut)}`) }}
                  </span>
                </dd>

                <dt class="col-5 text-muted small">
                  <i class="far fa-calendar-plus me-1"></i>{{ t('taches.details.dateCreation') }}
                </dt>
                <dd class="col-7 small fw-semibold">{{ formatDate(tache.dateCreation) }}</dd>

                <dt class="col-5 text-muted small">
                  <i class="far fa-user me-1"></i>{{ t('taches.details.assigneeA') }}
                </dt>
                <dd class="col-7 small fw-semibold">{{ getAssigneName() }}</dd>

                <dt class="col-5 text-muted small">
                  <i class="fas fa-project-diagram me-1"></i>{{ t('taches.details.projet') }}
                </dt>
                <dd class="col-7 small">
                  <router-link :to="`/projets/${tache.idProjet}`" class="text-decoration-none">
                    {{ t('taches.details.voirProjet') }} →
                  </router-link>
                </dd>
              </dl>
            </div>
          </div>

          <!-- Workflow -->
          <div class="card shadow-sm">
            <div class="card-header bg-light">
              <h6 class="mb-0">
                <i class="fas fa-sitemap me-2"></i>{{ t('taches.workflow.titre') }}
              </h6>
            </div>
            <div class="card-body">
              <div class="workflow-diagram">
                <div class="workflow-step" :class="{ active: estStatut('BROUILLON') }">
                  <i class="fas fa-file"></i>
                  <span>{{ t('taches.statuts.brouillon') }}</span>
                </div>
                <div class="workflow-arrow">↓</div>
                <div class="workflow-step" :class="{ active: estStatut('EN_ATTENTE_VALIDATION') }">
                  <i class="fas fa-clock"></i>
                  <span>{{ t('taches.statuts.enAttente') }}</span>
                </div>
                <div class="workflow-arrow">↓</div>
                <div class="workflow-step" :class="{ active: estStatut('TERMINE') }">
                  <i class="fas fa-check-circle"></i>
                  <span>{{ t('taches.statuts.termine') }}</span>
                </div>
              </div>
              <div class="mt-3 pt-3 border-top">
                <small class="text-muted">
                  <i class="fas fa-info-circle me-1"></i>{{ t('taches.workflow.aide') }}
                </small>
              </div>
            </div>
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
import { taskAPI } from '@/services/api'
import Commentaires from '@/components/Commentaires.vue'

const { t, locale } = useI18n()
const route = useRoute()
const router = useRouter()

const tacheId = computed(() => route.params.id)
const loading = ref(true)
const actionEnCours = ref(false)
const tache = ref(null)
const flash = ref({ text: '', type: 'success' })

const utilisateurActuel = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user) : null
})

const estChefProjet = computed(() => utilisateurActuel.value?.role === 'CHEF_PROJET')
const estAdministrateur = computed(() => utilisateurActuel.value?.role === 'ADMINISTRATEUR')
const estAssigne = computed(() => tache.value?.idAssigne === utilisateurActuel.value?.id)

// ========== CHARGEMENT ==========
onMounted(async () => {
  await chargerTache()
})

const chargerTache = async () => {
  try {
    loading.value = true
    const response = await taskAPI.getById(tacheId.value)
    tache.value = response.data
  } catch (error) {
    console.error('Erreur chargement tâche:', error)
    tache.value = null
    showFlash(t('erreurs.chargementTache'), 'danger')
  } finally {
    loading.value = false
  }
}

// ========== PERMISSIONS (Cahier des charges F7) ==========
const peutGererTache = computed(() => {
  return estChefProjet.value || estAdministrateur.value || estAssigne.value
})

const peutModifier = computed(() => {
  const statut = tache.value?.statut?.toUpperCase()
  if (estChefProjet.value) {
    return !['TERMINE', 'TERMINÉ', 'ANNULE', 'ANNULÉ'].includes(statut)
  }
  return estAssigne.value && statut === 'BROUILLON'
})

const peutSoumettre = computed(() => {
  const statut = tache.value?.statut?.toUpperCase()
  return estAssigne.value && statut === 'BROUILLON'
})

const peutValider = computed(() => {
  const statut = tache.value?.statut?.toUpperCase()
  return estChefProjet.value && statut === 'EN_ATTENTE_VALIDATION'
})

const peutAnnuler = computed(() => {
  const statut = tache.value?.statut?.toUpperCase()
  if (estAdministrateur.value) return true
  if (estChefProjet.value) {
    return !['TERMINE', 'TERMINÉ'].includes(statut)
  }
  return false
})

// ========== ACTIONS ==========
const soumettreValidation = async () => {
  if (!confirm(t('taches.actions.confirmerSoumission'))) return

  try {
    actionEnCours.value = true
    await taskAPI.updateStatus(tacheId.value, 'EN_ATTENTE_VALIDATION')
    tache.value.statut = 'EN_ATTENTE_VALIDATION'
    showFlash(t('taches.actions.soumissionReussie'), 'success')
  } catch (error) {
    console.error('Erreur soumission:', error)
    showFlash(t('erreurs.soumissionTache'), 'danger')
  } finally {
    actionEnCours.value = false
  }
}

const validerTache = async () => {
  if (!confirm(t('taches.actions.confirmerValidation'))) return

  try {
    actionEnCours.value = true
    await taskAPI.updateStatus(tacheId.value, 'TERMINE')
    tache.value.statut = 'TERMINE'
    showFlash(t('taches.actions.validationReussie'), 'success')
  } catch (error) {
    console.error('Erreur validation:', error)
    showFlash(t('erreurs.validationTache'), 'danger')
  } finally {
    actionEnCours.value = false
  }
}

const annulerTache = async () => {
  if (!confirm(t('taches.actions.confirmerAnnulation'))) return

  try {
    actionEnCours.value = true
    await taskAPI.updateStatus(tacheId.value, 'ANNULE')
    tache.value.statut = 'ANNULE'
    showFlash(t('taches.actions.annulationReussie'), 'success')
  } catch (error) {
    console.error('Erreur annulation:', error)
    showFlash(t('erreurs.annulationTache'), 'danger')
  } finally {
    actionEnCours.value = false
  }
}

const modifierTache = () => {
  router.push(`/taches/${tacheId.value}/modifier`)
}

// ========== HELPERS ==========
const estStatut = (statutTest) => {
  return tache.value?.statut?.toUpperCase() === statutTest.toUpperCase()
}

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  try {
    return new Date(dateStr).toLocaleDateString(locale.value || 'fr-FR', {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    })
  } catch {
    return '—'
  }
}

const getAssigneName = () => {
  if (!tache.value?.assigne) return t('taches.details.nonDefini')
  const assigne = tache.value.assigne
  return `${assigne.prenom || ''} ${assigne.nom || ''}`.trim() ||
    assigne.email ||
    t('taches.details.nonDefini')
}

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

const showFlash = (text, type = 'success') => {
  flash.value = { text, type }
  setTimeout(() => {
    flash.value = { text: '', type: 'success' }
  }, 5000)
}
</script>

<style scoped>
.workflow-diagram {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem 0;
}

.workflow-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: #f8f9fa;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  width: 100%;
  transition: all 0.3s ease;
}

.workflow-step.active {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
  border-color: #007bff;
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
}

.workflow-step i {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.workflow-step span {
  font-size: 0.875rem;
  font-weight: 600;
  text-align: center;
}

.workflow-arrow {
  color: #6c757d;
  font-size: 1.5rem;
  margin: 0.5rem 0;
}

dl dt, dl dd {
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

dl dt:last-of-type, dl dd:last-of-type {
  border-bottom: none;
}
</style>
