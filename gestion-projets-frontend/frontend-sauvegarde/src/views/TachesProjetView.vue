<template>
  <div class="taches-container max-w-4xl mx-auto p-6">
    <h2 class="text-xl font-bold mb-4 text-collabpro">Liste des tâches du projet</h2>

    <div v-if="loading" class="text-gray-500">Chargement...</div>

    <div v-else>
      <div v-for="tache in taches" :key="tache.id" class="mb-4 p-4 bg-white rounded-lg shadow">
        <h3 class="text-lg font-semibold">{{ tache.titre }}</h3>
        <p class="text-sm text-gray-600">{{ tache.description }}</p>
        <p class="text-sm text-gray-500 mt-1">Date : {{ formatDate(tache.dateCreation) }}</p>
        <p class="text-sm text-gray-500">Assignée à : {{ tache.assigne?.nom ?? 'Non défini' }}</p>
        <span :class="getBadge(tache.statut)" class="px-2 py-1 text-xs rounded inline-block mt-2">
          {{ tache.statut }}
        </span>
        <router-link :to="`/taches/${tache.id}`" class="text-collabpro underline text-sm mt-3 block">Voir détails</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const taches = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await axios.get(`/api/taches/projet/${route.params.idProjet}`)
    taches.value = res.data
  } catch (err) {
    console.error('Erreur chargement tâches', err)
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
