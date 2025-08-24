<template>
  <div class="facture-container">
    <!-- Bouton pour ouvrir -->
    <button v-if="!afficherFacture" class="btn btn-primary" @click="afficherFacture = true">
      <i class="fas fa-file-invoice mr-2"></i> Voir la facture
    </button>

    <!-- Facture complète -->
    <div v-if="afficherFacture && !chargement" class="facture-pdf">
      <!-- Header -->
      <div class="facture-header grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <!-- ✅ logo conservé -->
          <img :src="facture.entreprise?.logo || '/logo-collabpro.png'" alt="CollabPro" class="logo-facture">
          <h3 class="text-blue-600 text-xl font-bold mt-2">{{ facture.entreprise?.nom || 'CollabPro' }}</h3>
          <p>{{ facture.entreprise?.adresse || '456 Avenue Louise' }}</p>
          <p>{{ facture.entreprise?.ville || '1050 Bruxelles, Belgique' }}</p>
          <p>Email: {{ facture.entreprise?.email || 'contact@collabpro.be' }}</p>
          <p>TVA: {{ facture.entreprise?.tva || 'BE0123456789' }}</p>
        </div>
        <div class="text-right">
          <h2 class="text-red-600 text-2xl font-bold uppercase">FACTURE</h2>
          <h4 class="text-lg font-semibold">{{ facture.numeroFacture }}</h4>
          <p><strong>Date:</strong> {{ formatDate(facture.dateEmission) }}</p>
          <p><strong>Échéance:</strong> {{ formatDate(facture.dateEcheance) }}</p>
          <p><strong>Période:</strong> {{ facture.periode || 'N/A' }}</p>
        </div>
      </div>

      <hr class="my-6 border-gray-300">

      <!-- Détails client -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        <div>
          <h5 class="font-semibold mb-3">Facturé à:</h5>
          <div class="client-info p-4 bg-gray-50 rounded-lg border">
            <p class="font-semibold">{{ facture.client?.nom || 'Client' }}</p>
            <p>{{ facture.client?.email || '' }}</p>
          </div>
        </div>
      </div>

      <!-- Tableau -->
      <table class="w-full border border-gray-300 mb-6">
        <thead class="bg-blue-50">
        <tr>
          <th class="px-4 py-2 text-left">Description</th>
          <th class="px-4 py-2 text-right">Montant HT</th>
          <th class="px-4 py-2 text-right">TVA</th>
          <th class="px-4 py-2 text-right">Total TTC</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td class="px-4 py-2">{{ facture.description || 'Abonnement Premium' }}</td>
          <td class="px-4 py-2 text-right">{{ formatMontant(facture.montantHT) }}</td>
          <td class="px-4 py-2 text-right">{{ formatMontant(facture.tva) }}</td>
          <td class="px-4 py-2 text-right font-bold">{{ formatMontant(facture.montantTTC) }}</td>
        </tr>
        </tbody>
      </table>

      <!-- Total -->
      <div class="text-right font-bold text-lg">
        TOTAL TTC : {{ formatMontant(facture.montantTTC) }}
      </div>

      <!-- Actions -->
      <div class="mt-6 text-center">
        <button class="btn btn-success mr-3" @click="telechargerPDF" :disabled="actionEnCours === 'pdf'">
          <i class="fas fa-download mr-2"></i>Télécharger PDF
        </button>
        <button class="btn btn-outline" @click="fermerFacture">
          <i class="fas fa-times mr-2"></i>Fermer
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import factureService from '@/services/facture.service.js'

export default {
  name: 'FactureComplete',
  props: {
    facture: { type: Object, required: true }
  },
  setup(props, { emit }) {
    const afficherFacture = ref(false)
    const actionEnCours = ref(null)

    const formatDate = (date) => {
      if (!date) return ''
      return new Date(date).toLocaleDateString('fr-FR')
    }
    const formatMontant = (m) => new Intl.NumberFormat('fr-FR', {
      style: 'currency', currency: 'EUR'
    }).format(m || 0)

    const telechargerPDF = async () => {
      try {
        actionEnCours.value = 'pdf'
        const res = await factureService.telechargerPDF(props.facture.id)
        if (!res.success) throw new Error(res.message)
      } catch (e) {
        console.error('Erreur téléchargement facture:', e)
      } finally {
        actionEnCours.value = null
      }
    }

    const fermerFacture = () => {
      afficherFacture.value = false
      emit('facture-fermee')
    }

    return { afficherFacture, actionEnCours, formatDate, formatMontant, telechargerPDF, fermerFacture }
  }
}
</script>

<style scoped>
.facture-container { max-width: 800px; margin: auto; }
.logo-facture { height: 60px; }
.facture-pdf { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,.1); }
.btn { padding: 10px 20px; border-radius: 8px; cursor: pointer; }
.btn-success { background: #28a745; color: #fff; }
.btn-outline { border: 2px solid #6b7280; background: white; color: #6b7280; }
</style>
