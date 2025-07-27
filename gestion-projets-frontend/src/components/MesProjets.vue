<router-link :to="`/projet/${projet.id}/chat`">
<div class="projet-card">
  <h3>{{ projet.nom }}</h3>
  <p>{{ projet.description }}</p>
</div>
</router-link>


<template>
  <div class="container py-4 fade-in">
    <h2 class="text-collabpro text-center mb-4">Mes projets</h2>

    <div v-if="loading" class="text-center">
      <div class="spinner-border text-collabpro" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
    </div>

    <div v-else class="row gy-4">
      <div
        v-for="projet in projets"
        :key="projet.id"
        class="col-12 col-md-6 col-lg-4"
      >
        <router-link :to="`/projet/${projet.id}/chat`" class="text-decoration-none">
          <div class="card-collabpro p-3 h-100">
            <h5 class="text-collabpro">{{ projet.nom }}</h5>
            <p class="text-muted mb-2">{{ projet.description }}</p>
            <span class="badge bg-collabpro-light text-dark">
              {{ projet.membres?.length || 0 }} membre(s)
            </span>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import projetService from '@/services/projet.service'

const projets = ref([])
const loading = ref(true)

const authStore = useAuthStore()
const token = authStore.token

onMounted(async () => {
  try {
    const response = await projetService.getMesProjets(token)
    projets.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des projets', error)
  } finally {
    loading.value = false
  }
})
</script>
