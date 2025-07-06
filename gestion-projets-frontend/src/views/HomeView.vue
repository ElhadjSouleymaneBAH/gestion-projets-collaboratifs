<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
    <!-- Navigation Header -->
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <!-- Logo et nom -->
          <div class="flex items-center space-x-3">
            <img src="/logo-collabpro.png" alt="COLLABPRO" class="h-10 w-10">
            <div>
              <h1 class="text-xl font-bold text-blue-600">COLLABPRO</h1>
              <p class="text-xs text-gray-500">La collaboration agile, simplifiée</p>
            </div>
          </div>

          <!-- Boutons de navigation -->
          <div class="flex items-center space-x-4">
            <router-link
              to="/login"
              class="text-blue-600 hover:text-blue-800 font-medium px-4 py-2 rounded-lg hover:bg-blue-50 transition-colors"
            >
              Se connecter
            </router-link>
            <router-link
              to="/register"
              class="bg-blue-600 text-white font-medium px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors shadow-lg"
            >
              S'inscrire
            </router-link>
          </div>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="relative py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center">
          <h1 class="text-5xl font-extrabold text-gray-900 mb-6">
            Gestion de Projets
            <span class="text-blue-600">Collaboratifs</span>
          </h1>
          <p class="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
            Plateforme intuitive pour les <strong>PME et freelances</strong>.
            Créez, gérez vos projets et collaborez en temps réel avec votre équipe.
          </p>

          <!-- CTA Buttons -->
          <div class="flex flex-col sm:flex-row gap-4 justify-center items-center">
            <router-link
              to="/register"
              class="bg-blue-600 text-white font-semibold px-8 py-4 rounded-lg text-lg hover:bg-blue-700 transition-colors shadow-xl"
            >
              Commencer gratuitement
            </router-link>
            <button
              @click="scrollToProjets"
              class="border-2 border-blue-600 text-blue-600 font-semibold px-8 py-4 rounded-lg text-lg hover:bg-blue-50 transition-colors"
            >
              Voir les projets publics
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Fonctionnalités -->
    <section class="py-16 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-16">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">
            Fonctionnalités principales
          </h2>
          <p class="text-lg text-gray-600">
            Tout ce dont vous avez besoin pour gérer vos projets efficacement
          </p>
        </div>

        <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
          <!-- F6 : Gérer les projets -->
          <div class="text-center p-6 rounded-lg hover:shadow-lg transition-shadow">
            <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Gestion de Projets</h3>
            <p class="text-gray-600">Créez, modifiez et supervisez vos projets en toute simplicité</p>
          </div>

          <!-- F7 : Gérer les tâches -->
          <div class="text-center p-6 rounded-lg hover:shadow-lg transition-shadow">
            <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Gestion de Tâches</h3>
            <p class="text-gray-600">Assignez, suivez et complétez vos tâches efficacement</p>
          </div>

          <!-- F8 : Collaboration équipe -->
          <div class="text-center p-6 rounded-lg hover:shadow-lg transition-shadow">
            <div class="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Collaboration d'Équipe</h3>
            <p class="text-gray-600">Ajoutez des membres et collaborez en temps réel</p>
          </div>

          <!-- F9 : Temps réel -->
          <div class="text-center p-6 rounded-lg hover:shadow-lg transition-shadow">
            <div class="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Temps Réel</h3>
            <p class="text-gray-600">Notifications instantanées et mises à jour en direct</p>
          </div>
        </div>
      </div>
    </section>

    <!-- F3 : Projets Publics -->
    <section id="projets-publics" class="py-16 bg-gray-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">
            Projets Publics
          </h2>
          <p class="text-lg text-gray-600">
            Découvrez les projets partagés par notre communauté
          </p>
        </div>

        <!-- Liste des projets publics -->
        <div v-if="loading" class="text-center">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <p class="mt-2 text-gray-600">Chargement des projets...</p>
        </div>

        <div v-else-if="projets.length > 0" class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="projet in projets.slice(0, 6)"
            :key="projet.id"
            class="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow p-6"
          >
            <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ projet.titre }}</h3>
            <p class="text-gray-600 mb-4 line-clamp-3">{{ projet.description }}</p>
            <div class="flex items-center justify-between">
              <span class="inline-block bg-green-100 text-green-800 text-xs px-2 py-1 rounded-full">
                {{ projet.statut }}
              </span>
              <span class="text-sm text-gray-500">
                {{ formatDate(projet.dateCreation) }}
              </span>
            </div>
          </div>
        </div>

        <div v-else class="text-center text-gray-600">
          <svg class="w-16 h-16 mx-auto mb-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
          </svg>
          <p>Aucun projet public disponible pour le moment</p>
        </div>

        <div class="text-center mt-8">
          <router-link
            to="/register"
            class="bg-blue-600 text-white font-semibold px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors"
          >
            Rejoignez-nous pour créer votre projet
          </router-link>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-gray-900 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid md:grid-cols-4 gap-8">
          <!-- Logo et description -->
          <div class="md:col-span-2">
            <div class="flex items-center space-x-3 mb-4">
              <img src="/logo-collabpro.png" alt="COLLABPRO" class="h-8 w-8">
              <div>
                <h3 class="text-lg font-bold">COLLABPRO</h3>
                <p class="text-sm text-gray-400">La collaboration agile, simplifiée</p>
              </div>
            </div>
            <p class="text-gray-400 max-w-md">
              Plateforme de gestion de projets collaboratifs destinée aux PME et freelances.
              Créez, gérez et collaborez efficacement sur vos projets.
            </p>
          </div>

          <!-- Liens légaux -->
          <div>
            <h4 class="text-lg font-semibold mb-4">Légal</h4>
            <ul class="space-y-2">
              <li>
                <router-link to="/cgu" class="text-gray-400 hover:text-white transition-colors">
                  Conditions d'utilisation
                </router-link>
              </li>
              <li>
                <router-link to="/confidentialite" class="text-gray-400 hover:text-white transition-colors">
                  Politique de confidentialité
                </router-link>
              </li>
              <li>
                <router-link to="/mentions-legales" class="text-gray-400 hover:text-white transition-colors">
                  Mentions légales
                </router-link>
              </li>
            </ul>
          </div>

          <!-- Contact -->
          <div>
            <h4 class="text-lg font-semibold mb-4">Contact</h4>
            <ul class="space-y-2 text-gray-400">
              <li>support@collabpro.com</li>
              <li>+32 (0) 2 123 45 67</li>
              <li>Bruxelles, Belgique</li>
            </ul>
          </div>
        </div>

        <div class="border-t border-gray-800 pt-8 mt-8 text-center text-gray-400">
          <p>&copy; 2025 COLLABPRO. Tous droits réservés. Développé par ElhadjSouleymaneBAH.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { projectAPI } from '@/services/api'

// State
const projets = ref([])
const loading = ref(true)

// Méthodes
const loadProjetsPublics = async () => {
  try {
    const response = await projectAPI.getAllProjects()
    projets.value = response.data
  } catch (error) {
    console.error('Erreur lors du chargement des projets:', error)
    projets.value = []
  } finally {
    loading.value = false
  }
}

const scrollToProjets = () => {
  document.getElementById('projets-publics').scrollIntoView({
    behavior: 'smooth'
  })
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// Lifecycle
onMounted(() => {
  loadProjetsPublics()
})
</script>

<style scoped>
.line-clamp-3 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}
</style>
