<template>
  <div class="max-w-3xl mx-auto p-6">
    <h2 class="text-xl font-bold text-collabpro mb-4">Détail de la tâche</h2>

    <div v-if="loading" class="text-gray-500">Chargement...</div>
    <div v-else-if="tache" class="bg-white p-5 rounded-lg shadow">
      <h3 class="text-lg font-semibold">{{ tache.titre }}</h3>
      <p class="text-sm text-gray-600 mb-2">{{ tache.description }}</p>
      <p class="text-sm text-gray-500">Date de création : {{ formatDate(tache.dateCreation) }}</p>
      <p class="text-sm text-gray-500">Statut : <span :class="getBadge(tache.statut)" class="px-2 py-1 rounded">{{ tache.statut }}</span></p>
      <p class="text-sm text-gray-500">Assignée à : {{ tache.assigne?.nom ?? 'Non défini' }}</p>
    </div>

    <div v-else class="text-red-500">Tâche introuvable</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const tache = ref(null)
const loading = ref(true)
const route = useRoute()

onMounted(async () => {
  try {
    const res = await axios.get(`/api/taches/${route.params.id}`)
    tache.value = res.data
  } catch (err) {
    console.error('Erreur chargement tâche', err)
  } finally {
    loading.value = false
  }
})

function getBadge(statut) {
  if (statut === 'BROUILLON') return 'bg-gray-200 text-gray-800'
  if (statut === 'TERMINE') return 'bg-green-500 text-white'
  return 'bg-yellow-400 text-black'
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString()
}
</script>
