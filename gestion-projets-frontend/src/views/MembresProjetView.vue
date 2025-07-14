<template>
  <div class="max-w-4xl mx-auto p-6">
    <h2 class="text-xl font-bold text-collabpro mb-4">Membres du projet</h2>

    <div v-if="loading" class="text-gray-500">Chargement des membres...</div>

    <div v-else>
      <div v-if="membres.length === 0" class="text-gray-500">Aucun membre pour ce projet.</div>

      <ul>
        <li v-for="membre in membres" :key="membre.id" class="mb-2 p-3 bg-white rounded-lg shadow flex justify-between items-center">
          <div>
            <p class="font-semibold">{{ membre.nom }} {{ membre.prenom }}</p>
            <p class="text-sm text-gray-600">{{ membre.email }}</p>
            <p class="text-xs text-gray-500">Rôle : {{ membre.role }}</p>
          </div>

          <button v-if="isChefDeProjet" @click="retirerMembre(membre.id)" class="text-red-500 text-sm hover:underline">
            Retirer
          </button>
        </li>
      </ul>

      <!-- Ajout membre -->
      <div v-if="isChefDeProjet" class="mt-6">
        <h3 class="font-semibold mb-2 text-collabpro">Ajouter un membre</h3>
        <select v-model="nouveauId" class="form-control w-full md:max-w-sm px-3 py-2 border border-gray-300 rounded">
          <option v-for="u in utilisateursDispo" :key="u.id" :value="u.id">
            {{ u.nom }} {{ u.prenom }} ({{ u.email }})
          </option>
        </select>
        <button @click="ajouterMembre" class="btn-collabpro mt-2">Ajouter</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const membres = ref([])
const utilisateursDispo = ref([])
const nouveauId = ref(null)
const loading = ref(true)
const isChefDeProjet = true // à remplacer plus tard par vérif réelle du rôle

onMounted(async () => {
  try {
    const idProjet = route.params.id

    // Obtenir les membres du projet
    const resMembres = await axios.get(`/api/projets/${idProjet}/membres`)
    membres.value = resMembres.data

    // Obtenir les utilisateurs non membres (optionnel pour ajout)
    const resTous = await axios.get('/api/utilisateurs') // suppose cette route existante
    utilisateursDispo.value = resTous.data.filter(u =>
      !membres.value.some(m => m.id === u.id)
    )
  } catch (err) {
    console.error('Erreur chargement membres', err)
  } finally {
    loading.value = false
  }
})

async function ajouterMembre() {
  try {
    const idProjet = route.params.id
    await axios.post(`/api/projets/${idProjet}/membres/${nouveauId.value}`)
    alert('Membre ajouté')
    location.reload()
  } catch (err) {
    alert("Échec de l'ajout")
  }
}

async function retirerMembre(idMembre) {
  try {
    const idProjet = route.params.id
    await axios.delete(`/api/projets/${idProjet}/membres/${idMembre}`)
    alert('Membre retiré')
    membres.value = membres.value.filter(m => m.id !== idMembre)
  } catch (err) {
    alert("Échec de la suppression")
  }
}
</script>
