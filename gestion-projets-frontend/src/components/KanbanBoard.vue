<template>
  <div class="kanban-container">
    <!-- En-tête Kanban -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h5 class="mb-0">
        <i class="fas fa-columns me-2 text-primary"></i>{{ t('kanban.titre') }}
      </h5>
      <button class="btn btn-sm btn-outline-secondary" @click="rafraichir">
        <i class="fas fa-sync-alt me-1"></i>{{ t('commun.actualiser') }}
      </button>
    </div>

    <!-- Loader -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="text-muted mt-3">{{ t('commun.chargement') }}</p>
    </div>

    <!-- Colonnes Kanban -->
    <div v-else class="kanban-board">
      <div
        v-for="colonne in colonnes"
        :key="colonne.id"
        class="kanban-column"
      >
        <!-- En-tête colonne -->
        <div class="column-header">
          <h6 class="mb-0">
            <i :class="getColonneIcon(colonne.nom)" class="me-2"></i>
            {{ colonne.nom }}
          </h6>
          <span class="badge bg-secondary">{{ getTachesColonne(colonne.id).length }}</span>
        </div>

        <!-- Zone drop -->
        <div
          class="column-body"
          @drop="onDrop($event, colonne.id)"
          @dragover.prevent
          @dragenter.prevent
        >
          <!-- Cartes tâches -->
          <div
            v-for="tache in getTachesColonne(colonne.id)"
            :key="tache.id"
            class="kanban-card"
            draggable="true"
            @dragstart="onDragStart($event, tache)"
          >
            <div class="card-header-mini">
              <span :class="['badge', getStatutBadgeClass(tache.statut)]">
                {{ translateData('taskStatus', tache.statut) }}
              </span>
              <span v-if="tache.priorite" :class="['badge ms-2', getPrioriteBadgeClass(tache.priorite)]">
                <i :class="getPrioriteIcon(tache.priorite)" class="me-1"></i>
                {{ t(`priorites.${tache.priorite}`) }}
              </span>
            </div>

            <h6 class="card-title-mini">{{ tache.titre }}</h6>

            <p class="card-description-mini text-muted">
              {{ (tache.description || '').substring(0, 80) }}{{ tache.description && tache.description.length > 80 ? '...' : '' }}
            </p>

            <div class="card-footer-mini">
              <div class="d-flex flex-column gap-1">
                <small class="text-muted">
                  <i class="far fa-user me-1"></i>{{ getAssigneName(tache) }}
                </small>
                <small v-if="tache.dateEcheance" :class="getEcheanceClass(tache.dateEcheance)">
                  <i class="far fa-calendar me-1"></i>{{ formatDate(tache.dateEcheance) }}
                </small>
              </div>
              <button
                class="btn btn-sm btn-outline-primary"
                @click="voirDetails(tache.id)"
              >
                <i class="fas fa-eye"></i>
              </button>
            </div>
          </div>

          <!-- Message si vide -->
          <div v-if="getTachesColonne(colonne.id).length === 0" class="empty-column">
            <i class="fas fa-inbox text-muted"></i>
            <p class="text-muted small mb-0">{{ t('kanban.aucuneTache') }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDataTranslation } from '@/composables/useDataTranslation'
import { useRouter } from 'vue-router'
//import { taskAPI } from '@/services/api'
import axios from 'axios'
const { t } = useI18n()
const { translateData } = useDataTranslation()
const router = useRouter()

const props = defineProps({
  projetId: {
    type: [Number, String],
    required: true
  }
})
const loading = ref(true)
const colonnes = ref([])

// ========== CHARGEMENT ==========
onMounted(async () => {
  await chargerDonnees()
})

const chargerDonnees = async () => {
  loading.value = true
  try {

    const token = localStorage.getItem('token')
    const colonnesRes = await axios.get(`/api/projets/${props.projetId}/colonnes`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    colonnes.value = Array.isArray(colonnesRes.data) ? colonnesRes.data : []

    console.log('[Kanban] Colonnes chargées:', colonnes.value.length)
    colonnes.value.forEach(c => {
      console.log(`  - ${c.nom}: ${c.taches?.length || 0} tâches`)
    })
  } catch (error) {
    console.error('[Kanban] Erreur chargement:', error)
  } finally {
    loading.value = false
  }

}

const rafraichir = async () => {
  await chargerDonnees()
}

// ========== HELPERS ==========
const getTachesColonne = (colonneId) => {
  const colonne = colonnes.value.find(c => c.id === colonneId)
  return colonne ? (colonne.taches || []) : []
}

const getColonneIcon = (nom) => {
  if (nom.toLowerCase().includes('faire')) return 'fas fa-list-ul'
  if (nom.toLowerCase().includes('cours')) return 'fas fa-spinner'
  if (nom.toLowerCase().includes('termin')) return 'fas fa-check-circle'
  return 'fas fa-columns'
}

const getStatutBadgeClass = (statut) => {
  const classes = {
    'BROUILLON': 'bg-secondary',
    'EN_ATTENTE_VALIDATION': 'bg-warning text-dark',
    'TERMINE': 'bg-success',
    'ANNULE': 'bg-danger'
  }
  return classes[statut] || 'bg-info'
}

const getAssigneName = (tache) => {
  if (!tache.assigne) return t('taches.details.nonDefini')
  return `${tache.assigne.prenom || ''} ${tache.assigne.nom || ''}`.trim() ||
    tache.assigne.email ||
    t('taches.details.nonDefini')
}

const voirDetails = (tacheId) => {
  router.push(`/taches/${tacheId}`)
}

const getPrioriteBadgeClass = (priorite) => {
  const classes = {
    'URGENTE': 'bg-danger text-white',
    'HAUTE': 'bg-warning text-dark',
    'NORMALE': 'bg-info text-white',
    'BASSE': 'bg-secondary text-white'
  }
  return classes[priorite?.toUpperCase()] || 'bg-secondary text-white'
}

const getPrioriteIcon = (priorite) => {
  const icons = {
    'URGENTE': 'fas fa-exclamation-circle',
    'HAUTE': 'fas fa-arrow-up',
    'NORMALE': 'fas fa-minus',
    'BASSE': 'fas fa-arrow-down'
  }
  return icons[priorite?.toUpperCase()] || 'fas fa-minus'
}
const getEcheanceClass = (dateEcheance) => {
  if (!dateEcheance) return ''
  const aujourdhui = new Date()
  aujourdhui.setHours(0, 0, 0, 0)
  const echeance = new Date(dateEcheance)
  echeance.setHours(0, 0, 0, 0)

  const diffJours = Math.ceil((echeance - aujourdhui) / (1000 * 60 * 60 * 24))

  if (diffJours < 0) return 'text-danger fw-bold' // En retard
  if (diffJours === 0) return 'text-warning fw-bold' // Aujourd'hui
  if (diffJours <= 3) return 'text-warning' // Dans 3 jours
  return 'text-muted' // Plus tard
}
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}
// ========== DRAG & DROP ==========
let draggedTask = null

const onDragStart = (event, tache) => {
  draggedTask = tache
  event.dataTransfer.effectAllowed = 'move'
  event.target.style.opacity = '0.5'
}

const onDrop = async (event, colonneIdDestination) => {
  event.preventDefault()

  if (!draggedTask) return

  // Trouver la colonne source
  const colonneSource = colonnes.value.find(c =>
    c.taches?.some(t => t.id === draggedTask.id)
  )

  if (!colonneSource || colonneSource.id === colonneIdDestination) {
    console.log('[Kanban] Même colonne, pas de changement')
    draggedTask = null
    return
  }

  try {
    console.log(`[Kanban] Déplacement tâche ${draggedTask.id}`)

    // Mise à jour locale immédiate
    const tacheIndex = colonneSource.taches.findIndex(t => t.id === draggedTask.id)
    if (tacheIndex !== -1) {
      const tache = colonneSource.taches.splice(tacheIndex, 1)[0]

      const colonneDestination = colonnes.value.find(c => c.id === colonneIdDestination)
      if (colonneDestination) {
        if (!colonneDestination.taches) colonneDestination.taches = []
        colonneDestination.taches.push(tache)
      }
    }

    // Recharger pour synchroniser avec le backend
    await chargerDonnees()

    console.log('[Kanban]  Tâche déplacée')
  } catch (error) {
    console.error('[Kanban]  Erreur:', error)
    await chargerDonnees()
  } finally {
    draggedTask = null
  }
}
</script>

<style scoped>
.kanban-container {
  width: 100%;
  height: calc(100vh - 250px);
  min-height: 600px;
}

.kanban-board {
  display: flex;
  gap: 20px;
  height: calc(100% - 60px);
  overflow-x: auto;
  padding-bottom: 20px;
}

/* ========== COLONNES ========== */
.kanban-column {
  flex: 0 0 320px;
  background: #f8f9fa;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  max-height: 100%;
}

.column-header {
  padding: 16px;
  border-bottom: 2px solid #dee2e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 12px 12px 0 0;
}

.column-header h6 {
  color: #495057;
  font-weight: 600;
}

.column-body {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  min-height: 200px;
}

.column-body::-webkit-scrollbar {
  width: 6px;
}

.column-body::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.column-body::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

/* ========== CARTES ========== */
.kanban-card {
  background: white;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.08);
  cursor: grab;
  transition: all 0.2s ease;
  border-left: 4px solid #007bff;
}

.kanban-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.kanban-card:active {
  cursor: grabbing;
  opacity: 0.8;
}

.card-header-mini {
  margin-bottom: 8px;
}

.card-title-mini {
  font-size: 0.95rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #212529;
  line-height: 1.4;
}

.card-description-mini {
  font-size: 0.85rem;
  margin-bottom: 12px;
  line-height: 1.4;
  min-height: 40px;
}

.card-footer-mini {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #e9ecef;
}

/* ========== EMPTY STATE ========== */
.empty-column {
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.empty-column i {
  font-size: 2rem;
  margin-bottom: 10px;
  opacity: 0.5;
}

/* ========== RESPONSIVE ========== */
@media (max-width: 768px) {
  .kanban-board {
    flex-direction: column;
  }

  .kanban-column {
    flex: 0 0 auto;
    min-height: 300px;
  }
}
</style>
