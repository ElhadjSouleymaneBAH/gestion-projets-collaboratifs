<template>
  <div class="container py-4 fade-in">
    <h2 class="text-collabpro text-center mb-4">{{ $t('my_projects') }}</h2>

    <div v-if="loading" class="text-center">
      <div class="spinner-border text-collabpro" role="status">
        <span class="visually-hidden">{{ $t('loading') }}</span>
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
              {{ projet.membres?.length || 0 }} {{ $t('members_count') }}
            </span>
          </div>
        </router-link>
      </div>
    </div>

    <!-- Message si aucun projet -->
    <div v-if="!loading && projets.length === 0" class="text-center py-5">
      <div class="empty-state">
        <i class="fas fa-project-diagram fa-3x text-muted mb-3"></i>
        <h4 class="text-muted">{{ $t('no_projects_yet') }}</h4>
        <p class="text-muted">{{ $t('create_first_project') }}</p>
        <router-link to="/projet/nouveau" class="btn btn-primary">
          <i class="fas fa-plus me-2"></i>{{ $t('new_project') }}
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

<style scoped>
.empty-state {
  max-width: 400px;
  margin: 0 auto;
}

.card-collabpro {
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.card-collabpro:hover {
  transform: translateY(-5px);
  border-color: var(--collabpro-blue);
  box-shadow: 0 8px 25px rgba(0, 123, 255, 0.15);
}

.text-collabpro {
  color: var(--collabpro-blue);
}

.bg-collabpro-light {
  background-color: rgba(0, 123, 255, 0.1);
}

.fade-in {
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Responsive */
@media (max-width: 768px) {
  .container {
    padding: 2rem 1rem;
  }

  .card-collabpro {
    margin-bottom: 1rem;
  }
}
</style>
