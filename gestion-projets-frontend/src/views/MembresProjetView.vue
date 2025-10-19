<template>
  <div class="max-w-4xl mx-auto p-6">
    <h2 class="text-xl font-bold text-collabpro mb-4">{{ t('membres.titre') }}</h2>

    <div v-if="loading" class="text-gray-500">
      {{ t('commun.chargement') }}
    </div>

    <div v-else>
      <div v-if="membres.length === 0" class="text-gray-500">
        {{ t('commun.aucunResultat') }}
      </div>

      <ul v-else>
        <li
          v-for="membre in membres"
          :key="membre.id"
          class="mb-2 p-3 bg-white rounded-lg shadow flex justify-between items-center"
        >
          <div>
            <p class="font-semibold">
              {{ membre.nom }} {{ membre.prenom }}
            </p>
            <p class="text-sm text-gray-600">{{ membre.email }}</p>
            <p class="text-xs text-gray-500">
              {{ t('membres.role') }} : {{ membre.role }}
            </p>
          </div>

          <button
            v-if="isChefDeProjet"
            @click="retirerMembre(membre.id)"
            class="text-red-500 text-sm hover:underline"
          >
            {{ t('membres.retirer') }}
          </button>
        </li>
      </ul>

      <!-- Ajout membre -->
      <div v-if="isChefDeProjet" class="mt-6">
        <h3 class="font-semibold mb-2 text-collabpro">{{ t('membres.ajouter') }}</h3>

        <select
          v-model.number="nouveauId"
          class="form-control w-full md:max-w-sm px-3 py-2 border border-gray-300 rounded"
        >
          <option :value="null" disabled>
            — {{ t('membres.email') }} —
          </option>
          <option
            v-for="u in utilisateursDispo"
            :key="u.id"
            :value="u.id"
          >
            {{ u.nom }} {{ u.prenom }} ({{ u.email }})
          </option>
        </select>

        <button
          @click="ajouterMembre"
          class="btn-collabpro mt-2"
          :disabled="nouveauId === null || ajoutEnCours"
        >
          <span v-if="ajoutEnCours">{{ t('commun.chargement') }}</span>
          <span v-else>{{ t('membres.ajouter') }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const route = useRoute()

const membres = ref([])
const utilisateursDispo = ref([])
const nouveauId = ref(null)
const ajoutEnCours = ref(false)
const loading = ref(true)

const user = (() => {
  try { return JSON.parse(localStorage.getItem('user')) || {} } catch { return {} }
})()

const isChefDeProjet = computed(() =>
  ['CHEF_PROJET', 'ADMINISTRATEUR'].includes(user?.role)
)

onMounted(async () => {
  try {
    const idProjet = route.params.id

    // Membres du projet
    const resMembres = await axios.get(`/api/projets/${idProjet}/membres`)
    membres.value = Array.isArray(resMembres.data) ? resMembres.data : []

    // Utilisateurs disponibles pour ajout (exclure ceux déjà membres)
    const resTous = await axios.get('/api/utilisateurs')
    const tous = Array.isArray(resTous.data) ? resTous.data : []
    const idsMembres = new Set(membres.value.map(m => m.id))
    utilisateursDispo.value = tous.filter(u => !idsMembres.has(u.id))
  } catch (e) {
    console.error('Erreur chargement membres', e)
  } finally {
    loading.value = false
  }
})

async function ajouterMembre() {
  if (nouveauId.value == null) return
  try {
    ajoutEnCours.value = true
    const idProjet = route.params.id
    await axios.post(`/api/projets/${idProjet}/membres/${nouveauId.value}`)

    // Mettre à jour l'état local (sans recharger la page)
    const u = utilisateursDispo.value.find(x => x.id === nouveauId.value)
    if (u) {
      membres.value.push(u)
      utilisateursDispo.value = utilisateursDispo.value.filter(x => x.id !== u.id)
    }
    nouveauId.value = null
    // petit feedback (tu peux remplacer par ton système de toast)
    window.alert(t('membres.invitationEnvoyee'))
  } catch (e) {
    console.error('Échec ajout membre', e)
    window.alert(t('commun.erreur'))
  } finally {
    ajoutEnCours.value = false
  }
}

async function retirerMembre(idMembre) {
  try {
    const idProjet = route.params.id
    await axios.delete(`/api/projets/${idProjet}/membres/${idMembre}`)

    // Mettre à jour l'état local
    const m = membres.value.find(x => x.id === idMembre)
    if (m) {
      membres.value = membres.value.filter(x => x.id !== idMembre)
      utilisateursDispo.value.push(m)
    }
  } catch (e) {
    console.error('Échec suppression membre', e)
    window.alert(t('commun.erreur'))
  }
}
</script>
