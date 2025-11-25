<template>
  <div class="container my-4" v-if="!loading">
    <!-- Carte d'accès restreint - sobre et pro -->
    <div v-if="accesDenie" class="d-flex justify-content-center align-items-center" style="min-height: 60vh;">
      <div class="card border-0 shadow-sm text-center" style="max-width: 400px;">
        <div class="card-body p-4">
          <div class="mb-3">
            <i class="fas fa-lock text-secondary" style="font-size: 2.5rem;"></i>
          </div>
          <h5 class="card-title text-dark mb-2">{{ t('erreurs.accesRestreint') }}</h5>
          <p class="card-text text-muted small mb-4">
            {{ messageErreur || t('erreurs.projetNonAccessible') }}
          </p>
          <div class="d-flex justify-content-center gap-2">
            <button class="btn btn-outline-secondary btn-sm" @click="$router.push('/projets-publics')">
              <i class="fas fa-arrow-left me-1"></i>{{ t('commun.retour') }}
            </button>
            <button class="btn btn-primary btn-sm" @click="$router.push('/connexion')">
              <i class="fas fa-sign-in-alt me-1"></i>{{ t('commun.seConnecter') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Contenu principal (si accès autorisé) -->
    <div v-else>
      <!-- En-tête projet -->
      <div class="card mb-4 shadow-sm">
        <div class="card-body d-flex justify-content-between align-items-start">
          <div>
            <h2 class="text-primary mb-1">{{ projet?.titre }}</h2>
            <p class="text-muted mb-0">{{ projet?.description }}</p>
          </div>
          <div class="text-end">
            <span class="badge bg-success fs-6">{{ projet?.statut }}</span><br>
            <small class="text-muted">
              {{ t('projet.creeLe') }} : {{ formatDate(projet?.dateCreation) }}
            </small>
          </div>
        </div>
      </div>

      <!-- Alertes -->
      <div v-if="flash.text" :class="['alert alert-dismissible fade show', `alert-${flash.type}`]" role="alert">
        <i :class="flash.type === 'success' ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle'" class="me-2"></i>
        {{ flash.text }}
        <button type="button" class="btn-close" @click="flash.text = ''"></button>
      </div>

      <!-- Navigation par onglets -->
      <ul class="nav nav-tabs mb-3" role="tablist">
        <li class="nav-item">
          <a
            class="nav-link"
            :class="{ active: ongletActif === 'membres' }"
            @click="ongletActif = 'membres'"
            href="javascript:void(0)"
          >
            <i class="fas fa-users me-2"></i>{{ t('membres.titre') }}
            <span class="badge bg-secondary ms-1">{{ membres.length }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            :class="{ active: ongletActif === 'chat' }"
            @click="ongletActif = 'chat'"
            href="javascript:void(0)"
          >
            <i class="fas fa-comments me-2"></i>{{ t('chat.titre') }}
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            :class="{ active: ongletActif === 'fichiers' }"
            @click="ongletActif = 'fichiers'"
            href="javascript:void(0)"
          >
            <i class="fas fa-paperclip me-2"></i>{{ t('fichiers.titre') }}
          </a>
        </li>
      </ul>

      <!-- Contenu des onglets -->
      <div class="tab-content">
        <!-- ========== ONGLET MEMBRES ========== -->
        <div v-show="ongletActif === 'membres'" class="row">
          <div class="col-12">
            <div class="card shadow-sm">
              <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                  <i class="fas fa-users me-2"></i>
                  {{ t('membres.titre') }} ({{ membres.length }})
                </h5>
              </div>

              <div class="card-body p-0" style="max-height: 500px; overflow-y: auto;">
                <div v-if="membres.length === 0" class="p-3 text-center text-muted">
                  <i class="fas fa-user-slash fa-3x mb-3 opacity-25"></i>
                  <p class="mb-0">{{ t('membres.aucun') }}</p>
                </div>

                <ul class="list-group list-group-flush" v-else>
                  <li
                    v-for="m in membres"
                    :key="m.id"
                    class="list-group-item d-flex justify-content-between align-items-center"
                  >
                    <div class="d-flex align-items-center">
                      <div class="avatar-circle bg-primary text-white me-3">
                        {{ getInitiales(m) }}
                      </div>
                      <div>
                        <div class="fw-semibold">{{ m.prenom }} {{ m.nom }}</div>
                        <small class="text-muted">{{ m.email }}</small>
                      </div>
                    </div>

                    <div class="d-flex align-items-center gap-2">
                      <span :class="['badge', getRoleBadge(translateRole(m.role) || 'MEMBRE')]">
                        {{ translateRole(m.role || 'MEMBRE') }}
                      </span>
                      <button
                        v-if="peutSupprimerMembre(m)"
                        class="btn btn-sm btn-outline-danger"
                        @click="retirerMembre(m.id)"
                        :disabled="busyMember[m.id]"
                        :title="t('membres.retirer')"
                      >
                        <span v-if="busyMember[m.id]" class="spinner-border spinner-border-sm"></span>
                        <i v-else class="fas fa-user-minus"></i>
                      </button>
                    </div>
                  </li>
                </ul>
              </div>

              <div v-if="estChefProjet" class="card-footer bg-light">
                <button
                  class="btn btn-primary w-100"
                  @click="ouvrirModalAjoutMembre"
                  :disabled="ajoutEnCours"
                >
                  <i class="fas fa-user-plus me-2"></i>{{ t('membres.ajouter') }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- ========== ONGLET CHAT ========== -->
        <div v-show="ongletActif === 'chat'" class="row">
          <div class="col-12">
            <div class="card shadow-sm">
              <div class="card-header bg-success text-white">
                <h5 class="mb-0">
                  <i class="fas fa-comments me-2"></i>{{ t('chat.titre') }}
                </h5>
              </div>

              <div class="card-body p-0 d-flex flex-column" style="height: 500px;">
                <div class="flex-grow-1 overflow-auto p-3 bg-light" ref="messagesContainer">
                  <div v-if="messages.length === 0" class="text-center text-muted py-5">
                    <i class="fas fa-comments fa-3x mb-3 opacity-25"></i>
                    <h6>{{ t('chat.aucunMessage') }}</h6>
                    <p class="small">{{ t('chat.commencerConversation') }}</p>
                  </div>

                  <div
                    v-for="msg in messages"
                    :key="msg.id"
                    class="mb-3 d-flex"
                    :class="{ 'justify-content-end': estMonMessage(msg) }"
                  >
                    <div class="message-bubble" :class="estMonMessage(msg) ? 'message-own' : 'message-other'">
                      <div class="d-flex justify-content-between mb-1">
                        <small class="fw-bold" v-if="!estMonMessage(msg)">{{ msg.utilisateurNom }}</small>
                        <small class="text-muted ms-2">{{ formatTime(msg.dateEnvoi) }}</small>
                      </div>
                      <div class="message-content">{{ msg.contenu }}</div>
                    </div>
                  </div>
                </div>

                <div class="border-top p-3 bg-white">
                  <form @submit.prevent="envoyerMessage">
                    <div class="input-group">
                      <input
                        type="text"
                        class="form-control"
                        v-model="nouveauMessage"
                        :placeholder="t('chat.placeholder')"
                        maxlength="500"
                        :disabled="sending"
                      >
                      <button
                        type="submit"
                        class="btn btn-success"
                        :disabled="!nouveauMessage.trim() || sending"
                      >
                        <span v-if="sending" class="spinner-border spinner-border-sm me-1"></span>
                        <i v-else class="fas fa-paper-plane me-1"></i>
                        {{ t('commun.envoyer') }}
                      </button>
                    </div>
                    <small class="text-muted d-block mt-1">{{ nouveauMessage.length }}/500</small>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- ========== ONGLET FICHIERS ========== -->
        <div v-show="ongletActif === 'fichiers'" class="row">
          <div class="col-12">
            <GestionFichiers :projet-id="projetId" @refresh="chargerFichiers" />
          </div>
        </div>
      </div>
    </div>

    <!-- ========== MODAL AJOUT MEMBRE ========== -->
    <div
      v-if="modalAjoutMembre"
      class="modal fade show d-block"
      tabindex="-1"
      style="background: rgba(0,0,0,0.5);"
      @click.self="fermerModalAjoutMembre"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title">
              <i class="fas fa-user-plus me-2"></i>{{ t('membres.ajouterMembre') }}
            </h5>
            <button type="button" class="btn-close btn-close-white" @click="fermerModalAjoutMembre"></button>
          </div>

          <div class="modal-body">
            <!-- Info abonnement -->
            <div class="alert alert-info d-flex align-items-start mb-3">
              <i class="fas fa-info-circle me-2 mt-1"></i>
              <small>{{ t('membres.infoAbonnement') }}</small>
            </div>

            <!-- Recherche -->
            <div class="mb-3">
              <label class="form-label fw-semibold">
                <i class="fas fa-search me-1"></i>{{ t('membres.rechercherUtilisateur') }}
              </label>
              <input
                type="text"
                class="form-control"
                v-model="rechercheUtilisateur"
                @input="onRechercheInput"
                :placeholder="t('membres.placeholderRecherche')"
                autocomplete="off"
              >
              <small class="form-text text-muted">
                {{ t('membres.minimum3caracteres') }}
              </small>
            </div>

            <!-- Loader -->
            <div v-if="rechercheEnCours" class="text-center py-3">
              <div class="spinner-border text-primary"></div>
              <p class="text-muted small mt-2">{{ t('commun.recherche') }}</p>
            </div>

            <!-- Résultats -->
            <div v-else-if="utilisateursRecherche.length > 0">
              <h6 class="text-muted mb-2">
                {{ t('membres.resultatsTrouves', { count: utilisateursRecherche.length }) }}
              </h6>
              <div class="list-group">
                <div
                  v-for="user in utilisateursRecherche"
                  :key="user.id"
                  class="list-group-item d-flex justify-content-between align-items-center"
                >
                  <div class="d-flex align-items-center">
                    <div class="avatar-circle bg-secondary text-white me-3">
                      {{ getInitialesUser(user) }}
                    </div>
                    <div>
                      <div class="fw-semibold">{{ user.prenom || user.firstName }} {{ user.nom || user.lastName }}</div>
                      <small class="text-muted">{{ user.email }}</small>
                    </div>
                  </div>
                  <div>
                    <span v-if="estDejaMembre(user.id)" class="badge bg-secondary">
                      <i class="fas fa-check me-1"></i>{{ t('membres.dejaMembre') }}
                    </span>
                    <button
                      v-else
                      type="button"
                      class="btn btn-sm btn-success"
                      @click="ajouterUtilisateurAuProjet(user)"
                      :disabled="ajoutEnCours"
                    >
                      <span v-if="ajoutEnCours" class="spinner-border spinner-border-sm me-1"></span>
                      <i v-else class="fas fa-plus me-1"></i>
                      {{ t('commun.ajouter') }}
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Message aucun résultat -->
            <div v-else-if="rechercheUtilisateur.trim().length >= 3" class="alert alert-warning">
              <i class="fas fa-exclamation-triangle me-2"></i>
              {{ t('membres.aucunResultat') }}
            </div>

            <!-- Instructions -->
            <div v-else class="text-center text-muted py-3">
              <i class="fas fa-keyboard fa-2x mb-2 opacity-25"></i>
              <p class="mb-0 small">{{ t('membres.instructionsRecherche') }}</p>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="fermerModalAjoutMembre">
              <i class="fas fa-times me-1"></i>{{ t('commun.fermer') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Loader principal -->
  <div v-else class="py-5 text-center">
    <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;"></div>
    <p class="text-muted mt-3">{{ t('commun.chargement') }}</p>
  </div>
</template>

<script setup>
import { useDataTranslation } from '@/composables/useDataTranslation'
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { projectAPI, messagesAPI, userAPI } from '@/services/api'
import GestionFichiers from '@/components/GestionFichiers.vue'

const { t } = useI18n()
const { translateRole } = useDataTranslation()
const route = useRoute()
const router = useRouter()

const projetId = computed(() => route.params.id || route.params.projetId)
const loading = ref(true)
const sending = ref(false)
const accesDenie = ref(false)
const messageErreur = ref('')

const ongletActif = ref('membres')
const projet = ref(null)
const membres = ref([])
const messages = ref([])
const nouveauMessage = ref('')
const messagesContainer = ref(null)

const flash = ref({ text: '', type: 'success' })
const busyMember = ref({})

// Modal
const modalAjoutMembre = ref(false)
const rechercheUtilisateur = ref('')
const utilisateursRecherche = ref([])
const rechercheEnCours = ref(false)
const ajoutEnCours = ref(false)
let searchTimer = null

const utilisateurActuel = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('user')) || {}
  } catch {
    return {}
  }
})

const estChefProjet = computed(() => utilisateurActuel.value?.role === 'CHEF_PROJET')

// ========== FORMATAGE ==========
const formatDate = (d) => {
  if (!d) return '—'
  try {
    return new Date(d).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  } catch {
    return '—'
  }
}

const formatTime = (d) => {
  if (!d) return ''
  try {
    return new Date(d).toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return ''
  }
}

// ========== HELPERS ==========
const getInitiales = (membre) => {
  const prenom = membre?.prenom || ''
  const nom = membre?.nom || ''
  return `${prenom.charAt(0)}${nom.charAt(0)}`.toUpperCase() || '?'
}

const getInitialesUser = (user) => {
  const prenom = user?.prenom || user?.firstName || ''
  const nom = user?.nom || user?.lastName || ''
  return `${prenom.charAt(0)}${nom.charAt(0)}`.toUpperCase() || '?'
}

const getRoleBadge = (role) => {
  const r = String(role || 'MEMBRE').toUpperCase()
  return {
    'CHEF_PROJET': 'bg-primary',
    'ADMINISTRATEUR': 'bg-danger',
    'MEMBRE': 'bg-secondary'
  }[r] || 'bg-secondary'
}

const estMonMessage = (msg) => msg?.utilisateurEmail === utilisateurActuel.value?.email

const estDejaMembre = (userId) => membres.value.some(m => m.id === userId)

const peutSupprimerMembre = (m) => {
  return estChefProjet.value && m.role !== 'CHEF_PROJET' && m.id !== utilisateurActuel.value?.id
}

// ========== CHARGEMENT ==========
const fetchAll = async () => {
  try {
    loading.value = true
    accesDenie.value = false
    messageErreur.value = ''

    console.log('[Projet] Chargement du projet:', projetId.value)

    // Étape 1: Charger les informations du projet
    try {
      let projetRes
      if (utilisateurActuel.value?.id) {
        try {
          projetRes = await projectAPI.byId(projetId.value)
        } catch (err) {
          if (err.response?.status === 403) {
            console.warn('[Projet] Accès restreint - tentative route publique')
            projetRes = await projectAPI.getPublicProjectById(projetId.value)
          } else {
            throw err
          }
        }
      } else {
        projetRes = await projectAPI.getPublicProjectById(projetId.value)
      }

      projet.value = projetRes.data
      console.log('[Projet] Projet chargé:', projet.value?.titre)
    } catch (error) {
      if (error.response?.status === 403) {
        console.error('[Projet] 403 - Accès refusé')
        accesDenie.value = true
        messageErreur.value = "Vous n'avez pas les permissions pour accéder à ce projet."
        return
      }
      throw error
    }

    // Étape 2: Charger les membres du projet
    try {
      const membresRes = await projectAPI.getProjectMembers(projetId.value)
      membres.value = Array.isArray(membresRes.data) ? membresRes.data : []
      console.log('[Membres]', membres.value.length, 'membres')

      const estMembre = membres.value.some(
        m => m.id === utilisateurActuel.value?.id ||
          m.email === utilisateurActuel.value?.email
      )

      if (!estMembre && projet.value?.visibilite !== 'PUBLIC') {
        console.warn('[Projet] Utilisateur non membre d\'un projet privé')
        accesDenie.value = true
        messageErreur.value = 'Ce projet est privé. Seuls les membres peuvent y accéder.'
        return
      }

      if (!estMembre) {
        console.info('[Projet] Utilisateur non membre - Pas de chargement des messages')
        messages.value = []
        return
      }

    } catch (error) {
      if (error.response?.status === 403) {
        console.error('[Membres] 403 - Accès refusé')
        accesDenie.value = true
        messageErreur.value = 'Vous devez être membre de ce projet pour voir la liste des membres.'
        return
      }
      throw error
    }

    // Étape 3: Charger les messages (seulement si membre)
    try {
      const messagesRes = await messagesAPI.byProjet(projetId.value)
      messages.value = (Array.isArray(messagesRes.data) ? messagesRes.data : [])
        .sort((a, b) => new Date(a.dateEnvoi) - new Date(b.dateEnvoi))

      console.log('[Messages]', messages.value.length, 'messages')

      await nextTick()
      scrollToBottom()
    } catch (error) {
      if (error.response?.status === 403) {
        console.error('[Messages] 403 - Accès refusé')
        messages.value = []
        showFlash('Vous n\'avez pas accès aux messages de ce projet.', 'warning')
        return
      }
      throw error
    }

  } catch (error) {
    console.error('[Projet] Erreur chargement:', error)

    if (error.response?.status === 403) {
      accesDenie.value = true
      messageErreur.value = error.response?.data?.message ||
        'Accès refusé. Vous n\'avez pas les permissions nécessaires.'

      setTimeout(() => {
        router.push('/projets-publics')
      }, 5000)
    }
  } finally {
    loading.value = false
  }
}

const chargerFichiers = () => {
  console.log('[Fichiers] Rafraîchissement demandé')
}

// ========== CHAT ==========
const envoyerMessage = async () => {
  const contenu = nouveauMessage.value.trim()
  if (!contenu || sending.value) return

  try {
    sending.value = true
    const response = await messagesAPI.send({
      contenu,
      projetId: projetId.value,
      type: 'TEXT'
    })

    messages.value.push({
      id: response.data?.id || Date.now(),
      contenu,
      utilisateurEmail: utilisateurActuel.value?.email,
      utilisateurNom: `${utilisateurActuel.value?.prenom || ''} ${utilisateurActuel.value?.nom || ''}`.trim(),
      dateEnvoi: new Date().toISOString(),
      type: 'TEXT'
    })

    nouveauMessage.value = ''
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('[Chat] Erreur envoi message:', error)

    if (error.response?.status === 403) {
      showFlash('Vous n\'avez pas les permissions pour envoyer des messages dans ce projet.', 'danger')
    } else {
      showFlash(t('erreurs.envoyerMessage'), 'danger')
    }
  } finally {
    sending.value = false
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// ========== MEMBRES ==========
const retirerMembre = async (membreId) => {
  if (!confirm(t('membres.confirmerRetrait'))) return

  try {
    busyMember.value[membreId] = true
    await projectAPI.removeMember(projetId.value, membreId)
    membres.value = membres.value.filter(m => m.id !== membreId)
    showFlash(t('membres.membreRetire'), 'success')
  } catch (error) {
    console.error('[Membres] Erreur retrait membre:', error)
    showFlash(t('erreurs.retirerMembre'), 'danger')
  } finally {
    busyMember.value[membreId] = false
  }
}

// ========== MODAL ==========
const ouvrirModalAjoutMembre = () => {
  modalAjoutMembre.value = true
}

const fermerModalAjoutMembre = () => {
  modalAjoutMembre.value = false
  rechercheUtilisateur.value = ''
  utilisateursRecherche.value = []
  rechercheEnCours.value = false
}

const onRechercheInput = () => {
  clearTimeout(searchTimer)
  const query = rechercheUtilisateur.value.trim()

  if (query.length < 3) {
    utilisateursRecherche.value = []
    rechercheEnCours.value = false
    return
  }

  rechercheEnCours.value = true
  searchTimer = setTimeout(async () => {
    await rechercherUtilisateurs(query)
  }, 400)
}

const rechercherUtilisateurs = async (query) => {
  try {
    rechercheEnCours.value = true
    const response = await userAPI.search(encodeURIComponent(query))
    utilisateursRecherche.value = Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('[Recherche] Erreur recherche utilisateur:', error)
    utilisateursRecherche.value = []
  } finally {
    rechercheEnCours.value = false
  }
}

const ajouterUtilisateurAuProjet = async (user) => {
  if (ajoutEnCours.value || estDejaMembre(user.id)) return

  try {
    ajoutEnCours.value = true
    await projectAPI.addMember(projetId.value, user.id)

    const nouveauMembre = {
      id: user.id,
      prenom: user.prenom || user.firstName,
      nom: user.nom || user.lastName,
      email: user.email,
      role: user.role || 'MEMBRE'
    }
    membres.value.push(nouveauMembre)

    showFlash(
      t('membres.membreAjouteAvecNotification', {
        nom: `${nouveauMembre.prenom} ${nouveauMembre.nom}`
      }),
      'success'
    )

    fermerModalAjoutMembre()
  } catch (error) {
    console.error('[Membres] Erreur ajout membre:', error)
    const message = error.response?.data?.message || t('erreurs.ajouterMembre')
    showFlash(message, 'danger')
  } finally {
    ajoutEnCours.value = false
  }
}

// ========== UTILITAIRES ==========
const showFlash = (text, type = 'success') => {
  flash.value = { text, type }
  setTimeout(() => {
    flash.value = { text: '', type: 'success' }
  }, 5000)
}

onMounted(() => {
  fetchAll()
})

onBeforeUnmount(() => {
  clearTimeout(searchTimer)
})
</script>

<style scoped>
.nav-tabs .nav-link {
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-tabs .nav-link:hover {
  background-color: #f8f9fa;
}

.nav-tabs .nav-link.active {
  font-weight: 600;
}

.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.message-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 18px;
  margin-bottom: 8px;
  word-wrap: break-word;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message-own {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-other {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  color: #333;
  border-bottom-left-radius: 4px;
}

.message-content {
  line-height: 1.5;
  word-break: break-word;
}
</style>
