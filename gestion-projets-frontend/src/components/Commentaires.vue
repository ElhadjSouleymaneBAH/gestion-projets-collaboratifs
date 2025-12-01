<template>
  <div class="commentaires-tache">
    <!-- Loader -->
    <div v-if="loading" class="text-center py-4">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2 text-muted">{{ t('commun.chargement') }}</p>
    </div>

    <!-- Liste des commentaires -->
    <div v-else>
      <div v-if="commentaires.length === 0" class="text-center text-muted py-4">
        <i class="fas fa-comment-dots fa-2x mb-2"></i>
        <p>{{ t('commentaires.aucun') }}</p>
      </div>

      <ul class="list-group mb-3" v-else>
        <li
          v-for="commentaire in commentaires"
          :key="commentaire.id"
          class="list-group-item"
        >
          <div class="d-flex justify-content-between align-items-start mb-2">
            <div class="d-flex align-items-center">
              <div class="avatar-circle bg-primary text-white me-2">
                {{ getInitiales(commentaire) }}
              </div>
              <div>
                <strong>{{ commentaire?.['auteurPrenom'] ?? '' }} {{ commentaire?.['auteurNom'] ?? '' }}</strong>
                <small class="text-muted d-block">{{ formatDate(commentaire?.date) }}</small>
              </div>
            </div>
            <button
              v-if="peutSupprimer(commentaire)"
              class="btn btn-sm btn-outline-danger"
              @click="supprimerCommentaire(commentaire.id)"
              :title="t('commun.supprimer')"
            >
              <i class="fas fa-trash"></i>
            </button>
          </div>
          <p class="mb-0 ms-5">{{ commentaire?.contenu }}</p>
        </li>
      </ul>

      <!-- Formulaire -->
      <form @submit.prevent="publierCommentaire" class="mt-3">
        <div class="input-group">
          <textarea
            v-model.trim="nouveauCommentaire"
            class="form-control"
            :placeholder="t('commentaires.placeholder')"
            rows="2"
            maxlength="500"
            :disabled="envoi"
          ></textarea>
          <button
            class="btn btn-primary"
            type="submit"
            :disabled="!nouveauCommentaire || envoi"
          >
            <span v-if="envoi" class="spinner-border spinner-border-sm me-1"></span>
            <i v-else class="fas fa-paper-plane me-1"></i>
            {{ t('commun.envoyer') }}
          </button>
        </div>
        <small class="text-muted mt-1 d-block">{{ nouveauCommentaire.length }}/500</small>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useI18n } from 'vue-i18n'
import websocketService from '@/services/websocket.service'
import { commentaireAPI } from '@/services/api'

defineOptions({ name: 'TacheCommentaires' })
const { t } = useI18n()

const props = defineProps({
  tacheId: { type: [String, Number], required: true }
})

const commentaires = ref([])
const nouveauCommentaire = ref('')
const loading = ref(true)
const envoi = ref(false)
const websocketActif = ref(false) // NOUVEAU: tracker si WebSocket fonctionne
const utilisateur = JSON.parse(localStorage.getItem('user') || '{}')

// ========================================================================
// ⭐ WEBSOCKET - TEMPS RÉEL (F9 + F12)
// ========================================================================

const setupWebSocketCommentaires = async () => {
  try {
    // Tenter de connecter si pas déjà connecté
    if (!websocketService.connected) {
      const token = localStorage.getItem('token')
      if (token) {
        console.log('[Commentaires] Tentative de connexion WebSocket...')
        websocketService.connect(token) // Sans await (pas une Promise)
        // Attendre un peu que la connexion s'établisse
        await new Promise(resolve => setTimeout(resolve, 800))
      }
    }
    if (!websocketService.connected) {
      console.warn('[Commentaires] WebSocket non connecté, mode fallback activé')
      websocketActif.value = false
      return
    }

    // Écouter les nouveaux commentaires
    websocketService.subscribeToTacheCommentaires(props.tacheId, (nouveauComm) => {
      console.log('[F12]  Nouveau commentaire reçu en temps réel:', nouveauComm)
      const existe = commentaires.value.some(c => c.id === nouveauComm.id)
      if (!existe) {
        commentaires.value.push(nouveauComm)
      }
    })

    websocketService.subscribeToTacheCommentairesSuppression(props.tacheId, (commentaireId) => {
      console.log('[F12] Suppression commentaire reçue en temps réel:', commentaireId)
      commentaires.value = commentaires.value.filter(c => c.id !== commentaireId)
    })

    websocketActif.value = true
    console.log('[F12] WebSocket actif pour tâche', props.tacheId)

  } catch (error) {
    console.error('[Commentaires] Erreur WebSocket:', error)
    websocketActif.value = false
  }
}

const cleanupWebSocket = () => {
  if (websocketActif.value) {
    websocketService.unsubscribeFromTacheCommentaires(props.tacheId)
    console.log('[F12] Désabonné des commentaires de la tâche', props.tacheId)
  }
}

// ========== CHARGEMENT ==========
const chargerCommentaires = async () => {
  try {
    loading.value = true
    const { data } = await commentaireAPI.getByTache(props.tacheId)
    commentaires.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('Erreur chargement commentaires:', error)
  } finally {
    loading.value = false
  }
}

// ========== ACTIONS ==========
const publierCommentaire = async () => {
  if (!nouveauCommentaire.value || envoi.value) return
  try {
    envoi.value = true
    await commentaireAPI.create({
      contenu: nouveauCommentaire.value,
      tacheId: props.tacheId
    })
    nouveauCommentaire.value = ''

    if (!websocketActif.value) {
      console.log('[F12] WebSocket inactif, rechargement manuel...')
      await chargerCommentaires()
    } else {
      console.log('[F12] Commentaire publié, réception WebSocket attendue...')
    }

  } catch (error) {
    console.error('Erreur publication commentaire:', error)
    alert(t('erreurs.ajouterCommentaire'))
  } finally {
    envoi.value = false
  }
}

const supprimerCommentaire = async (commentaireId) => {
  if (!confirm(t('commentaires.confirmerSuppression'))) return
  try {
    await commentaireAPI.delete(commentaireId)
    if (!websocketActif.value) {
      console.log('[F12] WebSocket inactif, rechargement manuel...')
      await chargerCommentaires()
    } else {
      console.log('[F12] Commentaire supprimé, réception WebSocket attendue...')
    }

  } catch (error) {
    console.error('Erreur suppression commentaire:', error)
    alert(t('erreurs.supprimerCommentaire'))
  }
}

// ========== HELPERS ==========
const peutSupprimer = (commentaire) => {
  const role = utilisateur?.role?.toUpperCase?.() ?? ''
  return (
    commentaire?.['auteurId'] === utilisateur?.id ||
    role === 'CHEF_PROJET' ||
    role === 'ADMINISTRATEUR'
  )
}

const getInitiales = (commentaire) => {
  const prenom = commentaire?.['auteurPrenom'] ?? ''
  const nom = commentaire?.['auteurNom'] ?? ''
  return (prenom.charAt(0) + nom.charAt(0)).toUpperCase() || '?'
}

const formatDate = (iso) => {
  if (!iso) return ''
  try {
    return new Date(iso).toLocaleString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return ''
  }
}

// ========== LIFECYCLE ==========
onMounted(async () => {
  await chargerCommentaires()
  await setupWebSocketCommentaires()
})

onBeforeUnmount(() => {
  cleanupWebSocket()
})
</script>

<style scoped>
.commentaires-tache {
  background: #fff;
  border-radius: 8px;
}

.avatar-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.875rem;
}

.list-group-item {
  border-left: 3px solid #007bff;
  transition: all 0.2s;
}

.list-group-item:hover {
  background-color: #f8f9fa;
}
</style>
