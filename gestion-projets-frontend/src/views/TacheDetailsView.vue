<template>
  <div class="max-w-3xl mx-auto p-6">
    <h2 class="text-xl font-bold text-collabpro mb-4">{{ t('taches.details.titre') }}</h2>

    <div v-if="loading" class="text-gray-500">{{ t('commun.chargement') }}</div>

    <div v-else-if="tache" class="bg-white p-5 rounded-lg shadow">
      <h3 class="text-lg font-semibold">{{ tache.titre }}</h3>

      <p class="text-sm text-gray-600 mb-2">
        {{ tache.description || t('commun.aucuneDescription') }}
      </p>

      <p class="text-sm text-gray-500">
        {{ t('taches.details.dateCreation') }} : {{ formatDate(tache.dateCreation) }}
      </p>

      <p class="text-sm text-gray-500 mt-1">
        {{ t('taches.details.statut') }} :
        <span
          :class="badgeClass(tache.statut)"
          class="px-2 py-1 rounded"
        >
          {{ t(`taches.statuts.${mapStatutKey(tache.statut)}`) }}
        </span>
      </p>

      <p class="text-sm text-gray-500 mt-1">
        {{ t('taches.details.assigneeA') }} :
        {{ tache.assigne?.nom || t('taches.details.nonDefini') }}
      </p>
    </div>

    <div v-else class="text-red-500">{{ t('taches.details.introuvable') }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
const route = useRoute()

const tache = ref(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await axios.get(`/api/taches/${route.params.id}`)
    tache.value = res.data
  } catch (err) {
    console.error('Erreur chargement tâche', err)
    tache.value = null
  } finally {
    loading.value = false
  }
})

function mapStatutKey(statut) {
  const s = String(statut || '').toUpperCase()
  if (s === 'BROUILLON') return 'brouillon'
  if (s === 'TERMINE' || s === 'TERMINÉ') return 'termine'
  if (s === 'ANNULE' || s === 'ANNULÉ') return 'annule'
  // Par défaut, considérer "en attente"
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
