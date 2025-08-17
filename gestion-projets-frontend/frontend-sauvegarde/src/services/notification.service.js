import apiClient from '@/services/api.js'

class NotificationService {

  async getNotifications(params = {}) {
    try {
      const queryParams = new URLSearchParams()

      if (params.page !== undefined) queryParams.append('page', params.page)
      if (params.size !== undefined) queryParams.append('size', params.size)
      if (params.type) queryParams.append('type', params.type)
      if (params.lu !== undefined) queryParams.append('lu', params.lu)
      if (params.periode) queryParams.append('periode', params.periode)
      if (params.recherche) queryParams.append('recherche', params.recherche)

      const response = await apiClient.get(`/api/notifications?${queryParams.toString()}`)

      return {
        success: true,
        data: response.data
      }
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Erreur lors du chargement des notifications'
      }
    }
  }

  async marquerCommeLue(id) {
    try {
      const response = await apiClient.put(`/api/notifications/${id}/read`)
      return { success: true, data: response.data }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Erreur' }
    }
  }

  async marquerToutesLues() {
    try {
      const response = await apiClient.put('/api/notifications/read-all')
      return { success: true, data: response.data }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Erreur' }
    }
  }

  async supprimerNotification(id) {
    try {
      await apiClient.delete(`/api/notifications/${id}`)
      return { success: true }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Erreur' }
    }
  }

  async supprimerToutesLues() {
    try {
      await apiClient.delete('/api/notifications/read')
      return { success: true }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || 'Erreur' }
    }
  }
}

export default new NotificationService()
