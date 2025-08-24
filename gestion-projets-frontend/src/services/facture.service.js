// src/services/facture.service.js
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

function normalizeFacture(f) {
  const toDate = (d) => (d ? new Date(d) : null)
  const emission = toDate(f.dateEmission)
  const periode = emission
    ? `${String(emission.getMonth() + 1).padStart(2, '0')}/${emission.getFullYear()}`
    : ''

  return {
    ...f,
    montantHT: f.montantHT,
    tva: f.tva,
    montantTTC: (Number(f.montantHT) || 0) + (Number(f.tva) || 0),
    periode,
  }
}

export default {
  async getFactures() {
    try {
      const store = useAuthStore()
      const role = store.user?.role
      const url = role === 'ADMINISTRATEUR' ? '/factures/toutes' : '/factures/mes-factures'
      const { data } = await api.get(url)

      const list = Array.isArray(data) ? data.map(normalizeFacture) : []
      return {
        success: true,
        data: {
          content: list,
          number: 0,
          totalPages: 1,
          totalElements: list.length,
        },
      }
    } catch (e) {
      console.error('facture.service.getFactures error:', e)
      return { success: false, message: 'Erreur lors du chargement des factures' }
    }
  },

  async telechargerPDF(id) {
    try {
      const response = await api.get(`/factures/telecharger/${id}`, {
        responseType: 'blob',
      })
      const disp = response.headers['content-disposition'] || ''
      const match = /filename="?([^"]+)"?/.exec(disp)
      const filename = match?.[1] || `facture_${id}.pdf`

      const blob = new Blob([response.data], { type: 'application/pdf' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = filename
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)

      return { success: true }
    } catch (e) {
      console.error('facture.service.telechargerPDF error:', e)
      return { success: false, message: 'Erreur téléchargement PDF' }
    }
  },

  async envoyerParEmail() {
    // pas encore d’endpoint côté backend
    return { success: false, message: "Fonction non disponible côté backend" }
  },
}
