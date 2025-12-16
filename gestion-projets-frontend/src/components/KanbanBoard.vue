<template>
  <div class="kanban-wrapper">
    <!-- En-tête Kanban -->
    <div class="kanban-header">
      <h5 class="kanban-title"><i class="fas fa-columns me-2"></i>{{ t('kanban.titre') }}</h5>
    </div>

    <!-- Loader -->
    <div v-if="loading" class="kanban-loader">
      <div class="spinner-border text-primary"></div>
      <p>{{ t('commun.chargement') }}</p>
    </div>

    <!-- Colonnes Kanban -->
    <div v-else class="kanban-board">
      <div
        v-for="colonne in colonnes"
        :key="colonne.id"
        class="kanban-column"
        :style="{ '--column-color': getColonneCouleur(colonne.nom) }"
      >
        <!-- En-tête colonne -->
        <div class="column-header">
          <div class="column-title">
            <i :class="getColonneIcon(colonne.nom)"></i>
            <span>{{ translateColumnName(colonne.nom) }}</span>
          </div>
          <span class="column-count">{{ getTachesColonne(colonne.id).length }}</span>
        </div>

        <!-- Zone drop -->
        <div
          class="column-body"
          @drop="onDrop($event, colonne.id)"
          @dragover="onDragOver($event, colonne)"
          @dragenter="onDragEnter($event, colonne)"
          @dragleave="onDragLeave($event)"
        >
          <!-- Cartes tâches -->
          <div
            v-for="tache in getTachesColonne(colonne.id)"
            :key="tache.id"
            class="task-card"
            :class="[
              getPrioriteClass(tache.priorite),
              { 'non-draggable': !peutDeplacerTache(tache) },
            ]"
            :draggable="peutDeplacerTache(tache)"
            @dragstart="onDragStart($event, tache)"
            @dragend="onDragEnd($event)"
          >
            <!-- Badges -->
            <div class="task-badges">
              <span class="badge-statut" :class="getStatutBadgeClass(tache.statut)">
                {{ translateData('taskStatus', tache.statut) }}
              </span>
              <span
                v-if="tache.priorite"
                class="badge-priorite"
                :class="getPrioriteBadgeClass(tache.priorite)"
              >
                <i :class="getPrioriteIcon(tache.priorite)"></i>
                {{ t(`priorites.${tache.priorite}`) }}
              </span>
            </div>

            <!-- Titre -->
            <h6 class="task-title">{{ translateTaskTitle(tache.titre) }}</h6>

            <!-- Description -->
            <p v-if="tache.description" class="task-description">
              {{ translateTaskDescription(tache.description).substring(0, 80)
              }}{{ translateTaskDescription(tache.description).length > 80 ? '...' : '' }}
            </p>

            <!-- Footer -->
            <div class="task-footer">
              <div class="task-meta">
                <span
                  class="meta-assigne"
                  :class="{ 'assigne-cliquable': !tache.idAssigne && peutAssigner }"
                  @click="!tache.idAssigne && peutAssigner && ouvrirAssignation(tache)"
                >
                  <i class="far fa-user"></i>
                  {{ getAssigneName(tache) }}
                  <i
                    v-if="!tache.idAssigne && peutAssigner"
                    class="fas fa-plus-circle ms-1 text-primary"
                  ></i>
                </span>
                <span
                  v-if="tache.dateEcheance"
                  class="meta-echeance"
                  :class="getEcheanceClass(tache.dateEcheance)"
                >
                  <i class="far fa-calendar"></i>
                  {{ formatDate(tache.dateEcheance) }}
                </span>
              </div>
              <button
                class="btn-voir"
                @click="voirDetails(tache.id)"
                :title="t('tooltips.voirDetails')"
              >
                <i class="fas fa-eye"></i>
              </button>
            </div>
          </div>

          <!-- Message si vide -->
          <div v-if="getTachesColonne(colonne.id).length === 0" class="column-empty">
            <i class="fas fa-inbox"></i>
            <p>{{ t('kanban.aucuneTache') }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Assignation -->
    <div v-if="showModalAssignation" class="modal-overlay" @click.self="fermerModalAssignation">
      <div class="modal-assignation">
        <div class="modal-header-assign">
          <h6><i class="fas fa-user-plus me-2"></i>{{ t('taches.assignerTache') }}</h6>
          <button class="btn-close-modal" @click="fermerModalAssignation">×</button>
        </div>
        <div class="modal-body-assign">
          <p class="task-name">{{ tacheAAssigner?.titre }}</p>
          <div class="membres-liste">
            <div
              v-for="membre in membresProjet"
              :key="membre.id"
              class="membre-item"
              @click="assignerTache(membre.id)"
            >
              <div class="membre-avatar">
                {{ membre.prenom?.charAt(0) }}{{ membre.nom?.charAt(0) }}
              </div>
              <div class="membre-info">
                <span class="membre-nom">{{ membre.prenom }} {{ membre.nom }}</span>
                <span class="membre-email">{{ membre.email }}</span>
              </div>
            </div>
            <div v-if="membresProjet.length === 0" class="no-membres">
              {{ t('equipe.aucunMembre') }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDataTranslation } from '@/composables/useDataTranslation'
import { useRouter } from 'vue-router'
import axios from 'axios'
import WebSocketService from '@/services/websocket.service.js'

const { t, locale } = useI18n()
const { translateData, translateTaskTitle, translateTaskDescription } = useDataTranslation()
const router = useRouter()

// ========== PROPS ==========
const props = defineProps({
  projetId: {
    type: [Number, String],
    required: true,
  },
  peutAssigner: {
    type: Boolean,
    default: false,
  },
  membreMode: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['tache-assignee'])

// ========== UTILISATEUR CONNECTÉ ==========
const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || '{}')
const userId = utilisateurConnecte.id
const userRole = utilisateurConnecte.role || utilisateurConnecte.roles?.[0]

// ========== STATE ==========
const loading = ref(true)
const colonnes = ref([])
const membresProjet = ref([])
const showModalAssignation = ref(false)
const tacheAAssigner = ref(null)

// ========== TRADUCTION COLONNES ==========
const translateColumnName = (nom) => {
  const columnNames = {
    'À faire': { fr: 'À faire', en: 'To Do' },
    'En cours': { fr: 'En cours', en: 'In Progress' },
    Terminé: { fr: 'Terminé', en: 'Completed' },
  }
  const currentLocale = locale.value || localStorage.getItem('locale') || 'fr'
  return columnNames[nom]?.[currentLocale] || nom
}

// ========== CHARGEMENT ==========
onMounted(async () => {
  await chargerDonnees()

  // Abonnement temps réel Kanban
  WebSocketService.subscribe(`/topic/projet/${props.projetId}/kanban`, (message) => {
    console.log('[Kanban] Mise à jour reçue:', message)
    if (message.source !== 'self') {
      chargerDonnees()
    }
  })
})

onBeforeUnmount(() => {
  WebSocketService.unsubscribe(`/topic/projet/${props.projetId}/kanban`)
})

const chargerDonnees = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const colonnesRes = await axios.get(`/api/projets/${props.projetId}/colonnes`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    colonnes.value = Array.isArray(colonnesRes.data) ? colonnesRes.data : []
    console.log('[Kanban] Colonnes chargées:', colonnes.value.length)
  } catch (error) {
    console.error('[Kanban] Erreur chargement:', error)
  } finally {
    loading.value = false
  }
}

// ========== HELPERS COLONNES ==========
const getTachesColonne = (colonneId) => {
  const colonne = colonnes.value.find((c) => c.id === colonneId)
  return colonne ? colonne.taches || [] : []
}

const getColonneIcon = (nom) => {
  const nomLower = nom.toLowerCase()
  if (nomLower.includes('faire')) return 'fas fa-list-ul'
  if (nomLower.includes('cours')) return 'fas fa-spinner'
  if (nomLower.includes('termin')) return 'fas fa-check-circle'
  return 'fas fa-columns'
}

const getColonneCouleur = (nom) => {
  const nomLower = nom.toLowerCase()
  if (nomLower.includes('faire')) return '#FF9800'
  if (nomLower.includes('cours')) return '#2196F3'
  if (nomLower.includes('termin')) return '#4CAF50'
  return '#9E9E9E'
}

// ========== HELPERS STATUT ==========
const getStatutBadgeClass = (statut) => {
  const classes = {
    BROUILLON: 'statut-brouillon',
    EN_ATTENTE_VALIDATION: 'statut-attente',
    TERMINE: 'statut-termine',
    ANNULE: 'statut-annule',
  }
  return classes[statut] || 'statut-default'
}

// ========== HELPERS PRIORITÉ ==========
const getPrioriteClass = (priorite) => {
  const classes = {
    URGENTE: 'priorite-urgente',
    HAUTE: 'priorite-haute',
    NORMALE: 'priorite-normale',
    BASSE: 'priorite-basse',
  }
  return classes[priorite?.toUpperCase()] || ''
}

const getPrioriteBadgeClass = (priorite) => {
  const classes = {
    URGENTE: 'badge-urgente',
    HAUTE: 'badge-haute',
    NORMALE: 'badge-normale',
    BASSE: 'badge-basse',
  }
  return classes[priorite?.toUpperCase()] || 'badge-normale'
}

const getPrioriteIcon = (priorite) => {
  const icons = {
    URGENTE: 'fas fa-exclamation-circle',
    HAUTE: 'fas fa-arrow-up',
    NORMALE: 'fas fa-minus',
    BASSE: 'fas fa-arrow-down',
  }
  return icons[priorite?.toUpperCase()] || 'fas fa-minus'
}

// ========== HELPERS TÂCHE ==========
const getAssigneName = (tache) => {
  if (!tache.idAssigne) return t('taches.details.nonDefini')
  return (
    `${tache.prenomAssigne || ''} ${tache.nomAssigne || ''}`.trim() ||
    tache.emailAssigne ||
    t('taches.details.nonDefini')
  )
}

const getEcheanceClass = (dateEcheance) => {
  if (!dateEcheance) return ''
  const aujourdhui = new Date()
  aujourdhui.setHours(0, 0, 0, 0)
  const echeance = new Date(dateEcheance)
  echeance.setHours(0, 0, 0, 0)
  const diffJours = Math.ceil((echeance - aujourdhui) / (1000 * 60 * 60 * 24))

  if (diffJours < 0) return 'echeance-retard'
  if (diffJours === 0) return 'echeance-aujourdhui'
  if (diffJours <= 3) return 'echeance-proche'
  return ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const currentLocale = localStorage.getItem('locale') || 'fr'
  return date.toLocaleDateString(currentLocale === 'en' ? 'en-GB' : 'fr-FR', {
    day: '2-digit',
    month: '2-digit',
  })
}

const voirDetails = (tacheId) => {
  router.push(`/taches/${tacheId}`)
}

// ========== PERMISSIONS DRAG & DROP ==========
const peutDeplacerTache = (tache) => {
  // Chef de projet et Admin peuvent tout déplacer
  if (userRole === 'CHEF_PROJET' || userRole === 'ADMINISTRATEUR') {
    return true
  }

  // Membre ne peut déplacer que SES propres tâches en BROUILLON
  if (props.membreMode || userRole === 'MEMBRE') {
    const estSaTache = String(tache.idAssigne) === String(userId)
    const estBrouillon = tache.statut === 'BROUILLON'
    return estSaTache && estBrouillon
  }

  return false
}

const peutDropperVersColonne = (tache, colonneDestination) => {
  // Chef de projet et Admin peuvent tout faire
  if (userRole === 'CHEF_PROJET' || userRole === 'ADMINISTRATEUR') {
    return true
  }

  // Membre : seulement BROUILLON → EN_ATTENTE_VALIDATION (colonne "En cours")
  if (props.membreMode || userRole === 'MEMBRE') {
    const estSaTache = String(tache.idAssigne) === String(userId)
    const estBrouillon = tache.statut === 'BROUILLON'
    const versEnCours = colonneDestination.nom.toLowerCase().includes('cours')

    return estSaTache && estBrouillon && versEnCours
  }

  return false
}

// ========== ASSIGNATION ==========
const ouvrirAssignation = async (tache) => {
  tacheAAssigner.value = tache
  await chargerMembresProjet()
  showModalAssignation.value = true
}

const chargerMembresProjet = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get(`/api/projets/${props.projetId}/membres`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    membresProjet.value = response.data || []
  } catch (error) {
    console.error('[Kanban] Erreur chargement membres:', error)
  }
}

const assignerTache = async (membreId) => {
  if (!tacheAAssigner.value) return

  try {
    const token = localStorage.getItem('token')
    await axios.put(
      `/api/taches/${tacheAAssigner.value.id}/assigner`,
      { idAssigne: membreId },
      { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } },
    )

    console.log('[Kanban] Tâche assignée avec succès')
    showModalAssignation.value = false
    tacheAAssigner.value = null
    await chargerDonnees()
    emit('tache-assignee')
  } catch (error) {
    console.error('[Kanban] Erreur assignation:', error)
    alert(t('erreurs.assignationTache'))
  }
}

const fermerModalAssignation = () => {
  showModalAssignation.value = false
  tacheAAssigner.value = null
}

// ========== DRAG & DROP (STABLE - SANS TREMBLEMENT) ==========
let draggedTask = null
let draggedElement = null

const onDragStart = (event, tache) => {
  if (!peutDeplacerTache(tache)) {
    event.preventDefault()
    return
  }

  draggedTask = tache
  draggedElement = event.target

  // Configuration du drag
  event.dataTransfer.effectAllowed = 'move'
  event.dataTransfer.setData('text/plain', tache.id.toString())

  // Appliquer le style après un tick pour éviter le flash
  requestAnimationFrame(() => {
    if (draggedElement) {
      draggedElement.classList.add('dragging')
    }
  })
}

const onDragEnd = (event) => {
  // Nettoyer les styles
  if (draggedElement) {
    draggedElement.classList.remove('dragging')
  }

  // Retirer tous les indicateurs de drop
  document.querySelectorAll('.column-body').forEach((col) => {
    col.classList.remove('drag-over', 'drag-over-valid', 'drag-over-invalid')
  })

  draggedTask = null
  draggedElement = null
}

const onDragOver = (event, colonne) => {
  event.preventDefault()

  if (!draggedTask) return

  // Définir l'effet visuel selon les permissions
  if (peutDropperVersColonne(draggedTask, colonne)) {
    event.dataTransfer.dropEffect = 'move'
  } else {
    event.dataTransfer.dropEffect = 'none'
  }
}

const onDragEnter = (event, colonne) => {
  event.preventDefault()

  if (!draggedTask) return

  // Retirer d'abord tous les indicateurs
  document.querySelectorAll('.column-body').forEach((col) => {
    col.classList.remove('drag-over', 'drag-over-valid', 'drag-over-invalid')
  })

  // Ajouter l'indicateur approprié
  if (peutDropperVersColonne(draggedTask, colonne)) {
    event.currentTarget.classList.add('drag-over', 'drag-over-valid')
  } else {
    event.currentTarget.classList.add('drag-over', 'drag-over-invalid')
  }
}

const onDragLeave = (event) => {
  // Vérifier si on quitte vraiment la zone (pas juste un enfant)
  const relatedTarget = event.relatedTarget
  if (relatedTarget && event.currentTarget.contains(relatedTarget)) {
    return
  }
  event.currentTarget.classList.remove('drag-over', 'drag-over-valid', 'drag-over-invalid')
}

const onDrop = async (event, colonneIdDestination) => {
  event.preventDefault()
  event.currentTarget.classList.remove('drag-over', 'drag-over-valid', 'drag-over-invalid')

  if (!draggedTask) return

  const colonneSource = colonnes.value.find((c) => c.taches?.some((t) => t.id === draggedTask.id))
  const colonneDestination = colonnes.value.find((c) => c.id === colonneIdDestination)

  // Pas de changement si même colonne
  if (!colonneSource || !colonneDestination || colonneSource.id === colonneIdDestination) {
    draggedTask = null
    return
  }

  // Vérification des permissions
  if (!peutDropperVersColonne(draggedTask, colonneDestination)) {
    const message =
      props.membreMode || userRole === 'MEMBRE'
        ? t('erreurs.membreDeplacementRestreint') ||
          'Vous pouvez uniquement soumettre vos tâches en brouillon vers "En cours".'
        : t('erreurs.deplacementNonAutorise') || 'Déplacement non autorisé.'
    alert(message)
    draggedTask = null
    return
  }

  // Sauvegarde pour rollback
  const tacheDeplacee = { ...draggedTask }
  const tacheIndex = colonneSource.taches.findIndex((t) => t.id === draggedTask.id)

  try {
    // Mise à jour optimiste de l'UI
    if (tacheIndex !== -1) {
      const tache = colonneSource.taches.splice(tacheIndex, 1)[0]

      const statutMap = {
        'À faire': 'BROUILLON',
        'En cours': 'EN_ATTENTE_VALIDATION',
        Terminé: 'TERMINE',
      }
      tache.statut = statutMap[colonneDestination.nom] || tache.statut

      if (!colonneDestination.taches) colonneDestination.taches = []
      colonneDestination.taches.push(tache)
    }

    // Appel API
    const token = localStorage.getItem('token')
    await axios.put(
      `/api/taches/${tacheDeplacee.id}/deplacer`,
      { colonneDestination: colonneDestination.nom },
      { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } },
    )

    console.log('[Kanban] Tâche déplacée avec succès vers:', colonneDestination.nom)
  } catch (error) {
    console.error('[Kanban] Erreur déplacement:', error)

    // Message d'erreur approprié
    if (error.response?.status === 400) {
      alert(t('taches.kanban.erreurDeplacement') || 'Erreur lors du déplacement de la tâche.')
    } else if (error.response?.status === 403) {
      alert(t('erreurs.accesRefuse') || 'Accès refusé.')
    } else {
      alert(t('taches.kanban.erreurDeplacement') || 'Erreur lors du déplacement de la tâche.')
    }

    // Rollback - recharger les données
    await chargerDonnees()
  } finally {
    draggedTask = null
  }
}
</script>

<style scoped>
/* ========== WRAPPER ========== */
.kanban-wrapper {
  background: #f4f5f7;
  border-radius: 12px;
  padding: 16px;
}

/* ========== HEADER ========== */
.kanban-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.kanban-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #333;
}

/* ========== LOADER ========== */
.kanban-loader {
  text-align: center;
  padding: 60px 20px;
}

.kanban-loader p {
  margin-top: 12px;
  color: #666;
}

/* ========== BOARD - STABLE LAYOUT ========== */
.kanban-board {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 8px;
  min-height: 500px;
  align-items: flex-start;
}

/* ========== COLONNES - HAUTEUR FIXE ========== */
.kanban-column {
  flex: 0 0 320px;
  background: #ebecf0;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  min-height: 450px;
  max-height: calc(100vh - 380px);
}

.column-header {
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 3px solid var(--column-color, #9e9e9e);
  background: white;
  border-radius: 12px 12px 0 0;
  flex-shrink: 0;
}

.column-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 0.95rem;
  color: #333;
}

.column-title i {
  color: var(--column-color, #666);
}

.column-count {
  background: var(--column-color, #9e9e9e);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

/* ========== COLUMN BODY - STABLE HEIGHT ========== */
.column-body {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
  min-height: 350px;
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease;
  border: 2px solid transparent;
  border-radius: 0 0 10px 10px;
}

.column-body::-webkit-scrollbar {
  width: 6px;
}

.column-body::-webkit-scrollbar-track {
  background: transparent;
}

.column-body::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

/* ========== DRAG OVER STATES ========== */
.column-body.drag-over-valid {
  background-color: rgba(76, 175, 80, 0.1);
  border-color: #4caf50;
  border-style: dashed;
}

.column-body.drag-over-invalid {
  background-color: rgba(244, 67, 54, 0.05);
  border-color: #f44336;
  border-style: dashed;
}

/* ========== EMPTY STATE ========== */
.column-empty {
  text-align: center;
  padding: 40px 16px;
  color: #999;
}

.column-empty i {
  font-size: 2rem;
  margin-bottom: 10px;
  opacity: 0.5;
}

.column-empty p {
  margin: 0;
  font-size: 0.85rem;
}

/* ========== TASK CARD ========== */
.task-card {
  background: white;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  cursor: grab;
  transition:
    box-shadow 0.2s ease,
    transform 0.15s ease;
  border-left: 4px solid #e0e0e0;
  user-select: none;
}

.task-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.task-card:active {
  cursor: grabbing;
}

/* ========== TASK CARD DRAGGING ========== */
.task-card.dragging {
  opacity: 0.6;
  transform: rotate(2deg) scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.25);
  z-index: 1000;
}

/* ========== NON-DRAGGABLE TASKS ========== */
.task-card.non-draggable {
  cursor: default;
  opacity: 0.9;
}

.task-card.non-draggable:hover {
  transform: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* ========== PRIORITÉ BORDURE GAUCHE ========== */
.task-card.priorite-urgente {
  border-left-color: #ef5350;
}

.task-card.priorite-haute {
  border-left-color: #ffb74d;
}

.task-card.priorite-normale {
  border-left-color: #64b5f6;
}

.task-card.priorite-basse {
  border-left-color: #bdbdbd;
}

/* ========== BADGES ========== */
.task-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 10px;
}

.badge-statut,
.badge-priorite {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 600;
  text-transform: uppercase;
}

/* Statuts */
.statut-brouillon {
  background: #eceff1;
  color: #607d8b;
}

.statut-attente {
  background: #fff3e0;
  color: #e65100;
}

.statut-termine {
  background: #e8f5e9;
  color: #2e7d32;
}

.statut-annule {
  background: #ffebee;
  color: #c62828;
}

.statut-default {
  background: #e3f2fd;
  color: #1565c0;
}

/* Priorités */
.badge-urgente {
  background: #ffebee;
  color: #c62828;
}

.badge-haute {
  background: #fff3e0;
  color: #e65100;
}

.badge-normale {
  background: #e3f2fd;
  color: #1565c0;
}

.badge-basse {
  background: #eceff1;
  color: #607d8b;
}

.badge-priorite i {
  margin-right: 4px;
  font-size: 0.65rem;
}

/* ========== TASK CONTENT ========== */
.task-title {
  margin: 0 0 8px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.4;
}

.task-description {
  margin: 0 0 12px;
  font-size: 0.8rem;
  color: #666;
  line-height: 1.4;
}

/* ========== TASK FOOTER ========== */
.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.task-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-assigne,
.meta-echeance {
  font-size: 0.75rem;
  color: #888;
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-assigne i,
.meta-echeance i {
  font-size: 0.7rem;
}

/* Échéance alertes */
.echeance-retard {
  color: #c62828 !important;
  font-weight: 600;
}

.echeance-aujourdhui {
  color: #e65100 !important;
  font-weight: 600;
}

.echeance-proche {
  color: #f9a825 !important;
}

/* Bouton voir */
.btn-voir {
  width: 32px;
  height: 32px;
  border: 1px solid #e0e0e0;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.2s;
}

.btn-voir:hover {
  background: #2196f3;
  border-color: #2196f3;
  color: white;
}

/* ========== ASSIGNATION ========== */
.assigne-cliquable {
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  transition: background 0.2s;
}

.assigne-cliquable:hover {
  background: #e3f2fd;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-assignation {
  background: white;
  border-radius: 12px;
  width: 320px;
  max-height: 400px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header-assign {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.modal-header-assign h6 {
  margin: 0;
  font-size: 0.95rem;
}

.btn-close-modal {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.modal-body-assign {
  padding: 16px;
}

.task-name {
  font-weight: 600;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.membres-liste {
  max-height: 250px;
  overflow-y: auto;
}

.membre-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.membre-item:hover {
  background: #f5f5f5;
}

.membre-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
}

.membre-info {
  display: flex;
  flex-direction: column;
}

.membre-nom {
  font-weight: 500;
  font-size: 0.85rem;
}

.membre-email {
  font-size: 0.75rem;
  color: #888;
}

.no-membres {
  text-align: center;
  color: #999;
  padding: 20px;
}

/* ========== RESPONSIVE ========== */
@media (max-width: 768px) {
  .kanban-board {
    flex-direction: column;
  }

  .kanban-column {
    flex: 0 0 auto;
    min-height: 250px;
    max-height: none;
  }
}
</style>
