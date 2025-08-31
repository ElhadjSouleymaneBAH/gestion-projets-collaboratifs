import axios from 'axios'

/** Base URL : proxy Vite => /api ; sinon mets VITE_API_URL=http://localhost:8080 */
const API_BASE = import.meta.env.VITE_API_URL || '/api'

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
})

/** Bearer automatique */
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

/** 401 global */
api.interceptors.response.use(
    (res) => res,
    (error) => {
      if (error.response?.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/connexion'
      }
      return Promise.reject(error)
    }
)

/* ===================== AUTH ===================== */
export const authAPI = {
  login: (credentials) => api.post('/auth/connexion', credentials),
  register: (userData) => api.post('/utilisateurs/inscription', userData),
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },
}

/* ===================== PROJETS ===================== */
export const projectAPI = {
  getAllProjects: () => api.get('/projets'),
  getPublicProjects: () => api.get('/projets/publics'),
  create: (projectData) => api.post('/projets', projectData),
  getProjectById: (id) => api.get(`/projets/${id}`),
  getProjectsByUser: (userId) => api.get(`/projets/utilisateur/${userId}`),
  updateProject: (id, projectData) => api.put(`/projets/${id}`, projectData),
  /** Admin (ARCHIVE, ACTIF, etc.) */
  updateStatut: (id, statut) => api.patch(`/projets/${id}/statut`, { statut }),
  deleteProject: (id) => api.delete(`/projets/${id}`),
  addMember: (projectId, userId, role = 'MEMBRE') =>
      api.post(`/projets/${projectId}/membres/${userId}`, { role }),
  removeMember: (projectId, userId) => api.delete(`/projets/${projectId}/membres/${userId}`),
  getProjectMembers: (projectId) => api.get(`/projets/${projectId}/membres`),
}

/* ===================== TÂCHES ===================== */
export const taskAPI = {
  getAllTasks: () => api.get('/taches'),
  create: (taskData) => api.post('/taches', taskData),
  updateTask: (id, taskData) => api.put(`/taches/${id}`, taskData),
  updateStatus: (id, statut) => api.patch(`/taches/${id}/statut`, { statut }),
  deleteTask: (id) => api.delete(`/taches/${id}`),
  getTasksByProject: (projectId) => api.get(`/taches/projet/${projectId}`),
  getTasksByUser: (userId) => api.get(`/taches/utilisateur/${userId}`),
  getTasksByChefProjet: (chefProjetId) => api.get(`/taches/chefprojet/${chefProjetId}`),
}

/* ===================== UTILISATEURS ===================== */
export const userAPI = {
  /** liste + recherche + pagination */
  list: ({ q = '', role = '', statut = '', page = 0, size = 20 } = {}) =>
      api.get('/utilisateurs', { params: { q, role, statut, page, size } }),

  getUserById: (id) => api.get(`/utilisateurs/${id}`),
  create: (payload) => api.post('/utilisateurs', payload),
  updateProfile: (id, userData) => api.put(`/utilisateurs/${id}`, userData),
  deleteUser: (id) => api.delete(`/utilisateurs/${id}`),

  /** alias simple si besoin */
  search: (q = '', page = 0, size = 20) => api.get('/utilisateurs', { params: { q, page, size } }),
}

/* ===================== MESSAGES ===================== */
export const messagesAPI = {
  getByProjet: (projetId) => api.get(`/messages/projet/${projetId}`),
  send: (projetId, messageData) => api.post(`/messages/projet/${projetId}`, messageData),
}

/* ===================== NOTIFICATIONS ===================== */
export const notificationAPI = {
  getUserNotifications: (userId) => api.get(`/notifications/utilisateur/${userId}`),
  markAsRead: (notificationId) => api.patch(`/notifications/${notificationId}/lue`),
  markAllAsRead: (userId) => api.patch(`/notifications/utilisateur/${userId}/toutes-lues`),
  deleteNotification: (id) => api.delete(`/notifications/${id}`),
}

/* ===================== ABONNEMENTS ===================== */
export const abonnementAPI = {
  /** Admin */
  list: () => api.get('/abonnements'),
  updateStatut: (id, statut) => api.patch(`/abonnements/${id}/statut`, { statut }),

  /** Utilisateur */
  getCurrentSubscription: (userId) => api.get(`/abonnements/utilisateur/${userId}/actuel`),
  subscribe: (subscriptionData) => api.post('/abonnements', subscriptionData),
  cancelSubscription: (subscriptionId) => api.delete(`/abonnements/${subscriptionId}`),
  updatePaymentMethod: (subscriptionId, paymentData) =>
      api.put(`/abonnements/${subscriptionId}/paiement`, paymentData),
  getSubscriptionHistory: (userId) => api.get(`/abonnements/utilisateur/${userId}/historique`),
  getByUserId: (userId) => api.get(`/abonnements/utilisateur/${userId}`),
}

/* ===================== FACTURES ===================== */
export const factureAPI = {
  getFactures: (params = {}) => {
    const queryString = new URLSearchParams(params).toString()
    return api.get(`/factures${queryString ? `?${queryString}` : ''}`)
  },
  getFactureById: (id) => api.get(`/factures/${id}`),
  downloadPDF: (id) =>
      api
          .get(`/factures/${id}/pdf`, {
            responseType: 'blob',
            headers: { Accept: 'application/pdf' },
          })
          .then((response) => {
            const url = window.URL.createObjectURL(new Blob([response.data]))
            const link = document.createElement('a')
            link.href = url
            link.setAttribute('download', `facture_${id}.pdf`)
            document.body.appendChild(link)
            link.click()
            link.remove()
            window.URL.revokeObjectURL(url)
            return { success: true }
          })
          .catch((error) => {
            console.error('Erreur téléchargement PDF:', error)
            return { success: false, message: error.message }
          }),
  sendByEmail: (id, emailData) => api.post(`/factures/${id}/email`, emailData),
  regenerateFacture: (id) => api.post(`/factures/${id}/regenerer`),
}

/* ===================== STATS ===================== */
export const statsAPI = {
  getDashboardStats: (userId) => api.get(`/statistiques/dashboard/${userId}`),
  getProjectStats: (projectId) => api.get(`/statistiques/projet/${projectId}`),
  getUserActivity: (userId) => api.get(`/statistiques/activite/${userId}`),
  getSystemStats: () => api.get('/statistiques/systeme'),
}

/* ===================== UPLOAD ===================== */
export const uploadAPI = {
  uploadFile: (file, type = 'document') => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    return api.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
  deleteFile: (fileId) => api.delete(`/upload/${fileId}`),
  getFileUrl: (fileId) => `${api.defaults.baseURL}/upload/${fileId}`,
}

export default api
