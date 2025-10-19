<template>
  <div class="taches-container max-w-4xl mx-auto p-6">
    <h2 class="text-xl font-bold mb-4 text-collabpro">{{ t('taches.liste.titre') }}</h2>

    <div v-if="loading" class="text-gray-500">{{ t('commun.chargement') }}</div>

    <div v-else>
      <template v-if="taches.length">
        <div
          v-for="t in taches"
          :key="t.id"
          class="mb-4 p-4 bg-white rounded-lg shadow"
        >
          <h3 class="text-lg font-semibold">{{ t.titre }}</h3>
          <p class="text-sm text-gray-600">
            {{ t.description || t('commun.aucuneDescription') }}
          </p>

          <p class="text-sm text-gray-500 mt-1">
            {{ t('taches.liste.dateCreation') }} : {{ formatDate(t.dateCreation) }}
          </p>

          <p class="text-sm text-gray-500">
            {{ t('taches.liste.assigneeA') }} :
            {{ t.assigne?.nom || t('taches.details.nonDefini') }}
          </p>

          <span
            :class="badgeClass(t.statut)"
            class="px-2 py-1 text-xs rounded inline-block mt-2"
          >
            {{ t(`taches.statuts.${mapStatutKey(t.statut)}`) }}
          </span>

          <router-link
            :to="`/taches/${t.id}`"
            class="text-collabpro underline text-sm mt-3 block"
          >
            {{ t('taches.voirDetails') }}
          </router-link>
        </div>
      </template>

      <div v-else class="text-gray-500">
        {{ t('taches.liste.aucuneTache') }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { useI18n } from 'vue-i18n'

const route = useRoute()
const { t, locale } = useI18n()

const taches = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await axios.get(`/api/taches/projet/${route.params.idProjet}`)
    taches.value = Array.isArray(res.data) ? res.data : []
  } catch (err) {
    console.error('Erreur chargement tâches', err)
    taches.value = []
  } finally {
    loading.value = false
  }
})

function mapStatutKey(statut) {
  const s = String(statut || '').toUpperCase()
  if (s === 'BROUILLON') return 'brouillon'
  if (s === 'TERMINE' || s === 'TERMINÉ') return 'termine'
  if (s === 'ANNULE' || s === 'ANNULÉ') return 'annule'
  return 'enAttente'
}

function badgeClass(statut) {
  const s = String(statut || '').toUpperCase()
  if (s === 'BROUILLON') return 'bg-gray-200 text-gray-800'
  if (s === 'TERMINE' || s === 'TERMINÉ') return 'bg-green-500 text-white'
  if (s === 'ANNULE' || s === 'ANNULÉ') return 'bg-red-500 text-white'
  return 'bg-yellow-400 text-black'
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  try {
    return new Date(dateStr).toLocaleDateString(locale.value || 'fr-FR', {
      day: '2-digit', month: '2-digit', year: 'numeric'
    })
  } catch {
    return '—'
  }
}
</script>
