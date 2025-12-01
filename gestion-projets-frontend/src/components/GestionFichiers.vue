<template>
  <div class="fichiers-container">
    <!-- En-tête -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h5 class="mb-0">
        <i class="fas fa-paperclip me-2 text-primary"></i>{{ t('fichiers.titre') }}
        <span class="badge bg-secondary ms-2">{{ fichiers.length }}</span>
      </h5>
      <!-- Bouton Téléverser visible SEULEMENT si des fichiers existent déjà -->
      <button
        v-if="peutTeleverser && fichiers.length > 0 && !chargement"
        class="btn btn-primary"
        @click="ouvrirModalUpload"
      >
        <i class="fas fa-upload me-2"></i>{{ t('fichiers.televerser') }}
      </button>
    </div>

    <!-- Loader -->
    <div v-if="chargement" class="text-center py-4">
      <div class="spinner-border text-primary"></div>
      <p class="text-muted mt-2">{{ t('commun.chargement') }}</p>
    </div>

    <!-- Liste vide -->
    <div v-else-if="fichiers.length === 0" class="text-center py-5">
      <i class="fas fa-folder-open fa-4x text-muted mb-3" style="opacity: 0.3;"></i>
      <h6 class="text-muted">{{ t('fichiers.aucunFichier') }}</h6>
      <p class="text-muted small">{{ t('fichiers.televerserPremier') }}</p>
      <button
        v-if="peutTeleverser"
        class="btn btn-primary mt-2"
        @click="ouvrirModalUpload"
      >
        <i class="fas fa-upload me-2"></i>{{ t('fichiers.televerser') }}
      </button>
    </div>

    <!-- Liste des fichiers -->
    <div v-else class="table-responsive">
      <table class="table table-hover align-middle">
        <thead class="table-light">
        <tr>
          <th><i class="fas fa-file me-2"></i>{{ t('fichiers.nom') }}</th>
          <th><i class="fas fa-weight me-2"></i>{{ t('fichiers.taille') }}</th>
          <th><i class="fas fa-user me-2"></i>{{ t('fichiers.uploadePar') }}</th>
          <th><i class="fas fa-calendar me-2"></i>{{ t('fichiers.date') }}</th>
          <th class="text-end">{{ t('commun.actions') }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="fichier in fichiers" :key="fichier.id">
          <td>
            <i :class="getIconeFichier(fichier.typeMime)" class="me-2"></i>
            <strong>{{ fichier.nomOriginal }}</strong>
          </td>
          <td>{{ fichier.tailleLisible }}</td>
          <td>{{ fichier.uploadParNom }}</td>
          <td>
            <small class="text-muted">{{ formatDate(fichier.dateUpload) }}</small>
          </td>
          <td class="text-end">
            <div class="btn-group">
              <button
                class="btn btn-sm btn-outline-primary"
                @click="telechargerFichier(fichier)"
                :title="t('fichiers.telecharger')"
              >
                <i class="fas fa-download"></i>
              </button>
              <button
                v-if="peutSupprimer(fichier)"
                class="btn btn-sm btn-outline-danger"
                @click="supprimerFichier(fichier)"
                :title="t('fichiers.supprimer')"
              >
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Upload -->
    <div
      v-if="modalUpload"
      class="modal d-block"
      style="background: rgba(0, 0, 0, 0.5); z-index: 1060"
      @click.self="fermerModalUpload"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="fas fa-upload me-2"></i>{{ t('fichiers.televerserFichier') }}
            </h5>
            <button
              class="btn-close"
              @click="fermerModalUpload"
              :aria-label="t('commun.fermer')"
            ></button>
          </div>

          <div class="modal-body">
            <!-- Zone de drop -->
            <div
              class="upload-zone"
              :class="{ 'drag-over': isDragging }"
              @dragover.prevent="isDragging = true"
              @dragleave.prevent="isDragging = false"
              @drop.prevent="onFileDrop"
              @click="$refs.fileInput.click()"
            >
              <i class="fas fa-cloud-upload-alt fa-3x mb-3 text-primary"></i>
              <p class="mb-2">
                {{ t('fichiers.glisserDeposer') }}
              </p>
              <p class="text-muted small mb-0">
                {{ t('fichiers.ou') }}
                <span class="text-primary" style="cursor: pointer">
                  {{ t('fichiers.cliquerParcourir') }}
                </span>
              </p>
              <input
                ref="fileInput"
                type="file"
                style="display: none"
                @change="onFileSelect"
                :accept="acceptedTypes"
              />
            </div>

            <!-- Fichier sélectionné -->
            <div v-if="fichierSelectionne" class="mt-3 p-3 border rounded bg-light">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <i :class="getIconeFichier(fichierSelectionne.type)" class="me-2"></i>
                  <strong>{{ fichierSelectionne.name }}</strong>
                  <br>
                  <small class="text-muted">
                    {{ formatTaille(fichierSelectionne.size) }}
                  </small>
                </div>
                <button
                  class="btn btn-sm btn-outline-danger"
                  @click="fichierSelectionne = null"
                >
                  <i class="fas fa-times"></i>
                </button>
              </div>
            </div>

            <!-- Barre de progression -->
            <div v-if="uploadEnCours" class="mt-3">
              <div class="progress" style="height: 25px">
                <div
                  class="progress-bar progress-bar-striped progress-bar-animated"
                  role="progressbar"
                  :style="{ width: progressUpload + '%' }"
                  :aria-valuenow="progressUpload"
                  aria-valuemin="0"
                  aria-valuemax="100"
                >
                  {{ progressUpload }}%
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button
              class="btn btn-secondary"
              @click="fermerModalUpload"
              :disabled="uploadEnCours"
            >
              {{ t('commun.annuler') }}
            </button>
            <button
              class="btn btn-primary"
              @click="televerser"
              :disabled="!fichierSelectionne || uploadEnCours"
            >
              <span v-if="uploadEnCours" class="spinner-border spinner-border-sm me-2"></span>
              <i v-else class="fas fa-upload me-2"></i>
              {{ uploadEnCours ? t('fichiers.telechargement') : t('fichiers.televerser') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import axios from 'axios'

const { t } = useI18n()

const props = defineProps({
  projetId: {
    type: [Number, String],
    required: true
  }
})

const emit = defineEmits(['refresh'])

// State
const fichiers = ref([])
const chargement = ref(true)
const modalUpload = ref(false)
const fichierSelectionne = ref(null)
const uploadEnCours = ref(false)
const progressUpload = ref(0)
const isDragging = ref(false)

const acceptedTypes = '.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.zip,.rar,.jpg,.jpeg,.png,.gif'

// Computed
const utilisateurActuel = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('user')) || {}
  } catch {
    return {}
  }
})

const peutTeleverser = computed(() => {
  return ['CHEF_PROJET', 'MEMBRE', 'ADMINISTRATEUR'].includes(utilisateurActuel.value?.role)
})

// Lifecycle
onMounted(() => {
  chargerFichiers()
})

// Methods
const chargerFichiers = async () => {
  chargement.value = true
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get(`/api/fichiers/projet/${props.projetId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    fichiers.value = Array.isArray(response.data) ? response.data : []
    console.log(`[Fichiers] ${fichiers.value.length} fichiers chargés`)
  } catch (error) {
    console.error('[Fichiers] Erreur chargement:', error)
    fichiers.value = []
  } finally {
    chargement.value = false
  }
}

const ouvrirModalUpload = () => {
  modalUpload.value = true
  fichierSelectionne.value = null
  progressUpload.value = 0
}

const fermerModalUpload = () => {
  if (!uploadEnCours.value) {
    modalUpload.value = false
    fichierSelectionne.value = null
    isDragging.value = false
  }
}

const onFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    fichierSelectionne.value = file
  }
}

const onFileDrop = (event) => {
  isDragging.value = false
  const file = event.dataTransfer.files[0]
  if (file) {
    fichierSelectionne.value = file
  }
}

const televerser = async () => {
  if (!fichierSelectionne.value) return

  uploadEnCours.value = true
  progressUpload.value = 0

  const formData = new FormData()
  formData.append('file', fichierSelectionne.value)
  formData.append('projetId', props.projetId)

  try {
    const token = localStorage.getItem('token')
    const response = await axios.post('/api/fichiers/upload', formData, {
      params: { projetId: props.projetId },
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}`
      },
      onUploadProgress: (progressEvent) => {
        progressUpload.value = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      }
    })

    console.log('[Upload] Succès:', response.data)
    alert(t('fichiers.uploadReussi'))
    await chargerFichiers()
    fermerModalUpload()
    emit('refresh')
  } catch (error) {
    console.error('[Upload] Erreur:', error)
    alert(error.response?.data?.message || t('erreurs.uploadFichier'))
  } finally {
    uploadEnCours.value = false
  }
}

const telechargerFichier = async (fichier) => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get(
      `/api/fichiers/${fichier.id}/telecharger`,
      {
        params: { projetId: props.projetId },
        headers: { 'Authorization': `Bearer ${token}` },
        responseType: 'blob'
      }
    )

    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', fichier.nomOriginal)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    console.log('[Download] Téléchargement réussi')
  } catch (error) {
    console.error('[Download] Erreur:', error)
    alert(t('erreurs.telechargementFichier'))
  }
}

const supprimerFichier = async (fichier) => {
  if (!confirm(t('fichiers.confirmerSuppression'))) return

  try {
    const token = localStorage.getItem('token')
    await axios.delete(`/api/fichiers/${fichier.id}`, {
      params: { projetId: props.projetId },
      headers: { 'Authorization': `Bearer ${token}` }
    })

    console.log('[Delete] Suppression réussie')
    alert(t('fichiers.suppressionReussie'))
    await chargerFichiers()
    emit('refresh')
  } catch (error) {
    console.error('[Delete] Erreur:', error)
    alert(t('erreurs.suppressionFichier'))
  }
}

const peutSupprimer = (fichier) => {
  const role = utilisateurActuel.value?.role
  const userId = utilisateurActuel.value?.id
  return role === 'ADMINISTRATEUR' ||
    role === 'CHEF_PROJET' ||
    fichier.uploadParId === userId
}

const getIconeFichier = (mimeType) => {
  if (!mimeType) return 'fas fa-file text-secondary'

  if (mimeType.includes('pdf')) return 'fas fa-file-pdf text-danger'
  if (mimeType.includes('word') || mimeType.includes('document')) return 'fas fa-file-word text-primary'
  if (mimeType.includes('excel') || mimeType.includes('spreadsheet')) return 'fas fa-file-excel text-success'
  if (mimeType.includes('powerpoint') || mimeType.includes('presentation')) return 'fas fa-file-powerpoint text-warning'
  if (mimeType.includes('image')) return 'fas fa-file-image text-info'
  if (mimeType.includes('video')) return 'fas fa-file-video text-purple'
  if (mimeType.includes('audio')) return 'fas fa-file-audio text-pink'
  if (mimeType.includes('zip') || mimeType.includes('rar')) return 'fas fa-file-archive text-dark'
  if (mimeType.includes('text')) return 'fas fa-file-alt text-muted'

  return 'fas fa-file text-secondary'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  try {
    return new Date(dateStr).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return '—'
  }
}

const formatTaille = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}
</script>

<style scoped>
.fichiers-container {
  padding: 20px;
}

.upload-zone {
  border: 2px dashed #ccc;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.upload-zone:hover {
  border-color: #007bff;
  background: #e7f3ff;
}

.upload-zone.drag-over {
  border-color: #28a745;
  background: #d4edda;
}

.table th {
  font-weight: 600;
  font-size: 0.875rem;
  color: #495057;
}

.btn-group .btn {
  border-radius: 6px;
  margin: 0 1px;
}

.modal {
  backdrop-filter: blur(5px);
}
</style>
