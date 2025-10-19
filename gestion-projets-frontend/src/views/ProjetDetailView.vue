<template>
  <div class="container my-4" v-if="!loading">
    <!-- En-tête projet -->
    <div class="card mb-4">
      <div class="card-body d-flex justify-content-between align-items-start">
        <div>
          <h2 class="text-primary mb-1">{{ projet?.titre }}</h2>
          <p class="text-muted mb-0">{{ projet?.description }}</p>
        </div>
        <div class="text-end">
          <span class="badge bg-success fs-6">{{ projet?.statut }}</span><br>
          <small class="text-muted">
            {{ t('taches.details.dateCreation') }} : {{ formatDate(projet?.dateCreation) }}
          </small>
        </div>
      </div>
    </div>

    <div class="row">
      <!-- Membres -->
      <div class="col-lg-4">
        <div class="card h-100">
          <div class="card-header bg-primary text-white">
            <h5 class="mb-0">
              <i class="fas fa-users me-2"></i>
              {{ $t('membres.titre') }} ({{ membres.length }})
            </h5>
          </div>

          <div class="card-body p-0">
            <div v-if="membres.length === 0" class="p-3 text-center text-muted">
              {{ $t('membres.aucun') }}
            </div>

            <ul class="list-group list-group-flush">
              <li
                v-for="m in membres"
                :key="m.id"
                class="list-group-item d-flex justify-content-between align-items-center"
              >
                <div>
                  <div class="fw-semibold">{{ m.prenom }} {{ m.nom }}</div>
                  <small class="text-muted">{{ m.email }}</small>
                </div>

                <div class="d-flex align-items-center gap-2">
                  <span class="badge bg-secondary">
                    {{ $t(`roles.${m.role || 'MEMBRE'}`) }}
                  </span>
                  <button
                    v-if="peutSupprimerMembre(m)"
                    class="btn btn-sm btn-outline-danger"
                    @click="retirerMembre(m.id)"
                    :disabled="busyMember[m.id]"
                    :title="$t('membres.retirer')"
                  >
                    <span v-if="busyMember[m.id]" class="spinner-border spinner-border-sm"></span>
                    <i v-else class="fas fa-times"></i>
                  </button>
                </div>
              </li>
            </ul>
          </div>

          <div v-if="estChefProjet" class="card-footer bg-light">
            <router-link
              class="btn btn-sm btn-outline-primary w-100"
              :to="`/admin/utilisateurs?projetId=${projetId}`"
            >
              <i class="fas fa-user-plus me-2"></i>{{ $t('membres.ajouter') }}
            </router-link>
          </div>
        </div>
      </div>

      <!-- Chat du projet -->
      <div class="col-lg-8">
        <div class="card h-100">
          <div class="card-header bg-success text-white">
            <h5 class="mb-0">
              <i class="fas fa-comments me-2"></i>Chat
            </h5>
          </div>

          <div class="card-body p-0 d-flex flex-column" style="height: 450px;">
            <div class="flex-grow-1 overflow-auto p-3" ref="messagesContainer">
              <div v-if="messages.length === 0" class="text-center text-muted py-5">
                <i class="fas fa-comments fa-3x mb-3"></i>
                <h6>{{ $t('commun.aucunMessage') }}</h6>
                <p>{{ $t('commun.commencerConversation') }}</p>
              </div>

              <div
                v-for="m in messages"
                :key="m.id"
                class="mb-3 d-flex"
                :class="{ 'justify-content-end': estMonMessage(m) }"
              >
                <div class="message-bubble" :class="estMonMessage(m) ? 'message-own' : 'message-other'">
                  <div class="d-flex justify-content-between mb-1">
                    <small class="fw-bold" v-if="!estMonMessage(m)">{{ m.utilisateurNom }}</small>
                    <small class="text-muted ms-2">{{ formatTime(m.dateEnvoi) }}</small>
                  </div>
                  <div class="message-content">{{ m.contenu }}</div>
                </div>
              </div>
            </div>

            <!-- Zone de saisie -->
            <div class="border-top p-3">
              <div class="input-group">
                <input
                  class="form-control"
                  v-model="nouveauMessage"
                  @keyup.enter="envoyerMessage"
                  :placeholder="$t('commun.placeholderMessage')"
                  maxlength="500"
                >
                <button
                  class="btn btn-success"
                  :disabled="!nouveauMessage.trim() || sending"
                  @click="envoyerMessage"
                >
                  <span v-if="sending" class="spinner-border spinner-border-sm me-1"></span>
                  <i v-else class="fas fa-paper-plane me-1"></i>{{ $t('commun.envoyer') }}
                </button>
              </div>
              <small class="text-muted d-block mt-1">{{ nouveauMessage.length }}/500</small>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Messages globaux -->
    <div v-if="flash.text" :class="['alert mt-3', flash.type === 'success' ? 'alert-success' : 'alert-danger']">
      {{ flash.text }}
    </div>
  </div>

  <div v-else class="py-5 text-center text-muted">
    <div class="spinner-border text-primary mb-3"></div>
    <div>{{ t('commun.chargement') }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { projectAPI, messagesAPI } from '@/services/api.js'

const { t } = useI18n()
const route = useRoute()

const projetId = route.params.id || route.params.projetId
const loading = ref(true)
const sending  = ref(false)

const projet   = ref(null)
const membres  = ref([])
const messages = ref([])
const nouveauMessage = ref('')
const messagesContainer = ref(null)

const flash = ref({ text: '', type: 'success' })
const busyMember = ref({})

const utilisateurActuel = computed(() => {
  try { return JSON.parse(localStorage.getItem('user')) || {} } catch { return {} }
})
const estChefProjet = computed(() => utilisateurActuel.value?.role === 'CHEF_PROJET')

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR', { year:'numeric', month:'long', day:'numeric' }) : ''
const formatTime = (d) => d ? new Date(d).toLocaleTimeString('fr-FR', { hour:'2-digit', minute:'2-digit' }) : ''

const estMonMessage = (m) => m?.utilisateurEmail === utilisateurActuel.value?.email

const fetchAll = async () => {
  try {
    loading.value = true
    const [p, mem, msgs] = await Promise.all([
      projectAPI.byId(projetId),                    // 🔧 CORRECTION ICI !
      projectAPI.getProjectMembers(projetId),
      messagesAPI.getByProjet(projetId)
    ])
    projet.value   = p.data
    membres.value  = mem.data || []
    messages.value = (msgs.data || []).sort((a, b) => new Date(a.dateEnvoi) - new Date(b.dateEnvoi))
    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error('Erreur chargement projet:', e)
    flash.value = { text: e?.response?.data?.message || t('commun.erreurChargement'), type: 'danger' }
  } finally {
    loading.value = false
  }
}

const envoyerMessage = async () => {
  const contenu = nouveauMessage.value.trim()
  if (!contenu) return
  try {
    sending.value = true
    await messagesAPI.sendMessage({ contenu, projetId, type: 'TEXT' })

    // Optimistic UI
    messages.value.push({
      id: Date.now(),
      contenu,
      utilisateurEmail: utilisateurActuel.value?.email,
      utilisateurNom: `${utilisateurActuel.value?.prenom || ''} ${utilisateurActuel.value?.nom || ''}`.trim(),
      dateEnvoi: new Date().toISOString()
    })
    nouveauMessage.value = ''
    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error('Erreur envoi message:', e)
    flash.value = { text: t('commun.erreurEnvoi'), type: 'danger' }
  } finally {
    sending.value = false
  }
}

const retirerMembre = async (idMembre) => {
  if (!confirm(t('membres.confirmerRetrait'))) return
  try {
    busyMember.value[idMembre] = true
    await projectAPI.removeMember(projetId, idMembre)
    membres.value = membres.value.filter(m => m.id !== idMembre)
    flash.value = { text: t('membres.membreRetire'), type: 'success' }
  } catch (e) {
    console.error('Erreur retrait membre:', e)
    flash.value = { text: t('membres.erreurRetrait'), type: 'danger' }
  } finally {
    busyMember.value[idMembre] = false
  }
}

const peutSupprimerMembre = (m) => {
  return estChefProjet.value && m.role !== 'CHEF_PROJET' && m.id !== utilisateurActuel.value?.id
}

const scrollToBottom = () => {
  const c = messagesContainer.value
  if (c) c.scrollTop = c.scrollHeight
}

onMounted(fetchAll)
</script>

<style scoped>
.message-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 18px;
  margin-bottom: 8px;
  word-wrap: break-word;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.message-own   { background: linear-gradient(135deg, #007bff, #0056b3); color: #fff; }
.message-other { background: #f8f9fa; border: 1px solid #e9ecef; color: #333; }
.message-content { line-height: 1.4; }
</style>
