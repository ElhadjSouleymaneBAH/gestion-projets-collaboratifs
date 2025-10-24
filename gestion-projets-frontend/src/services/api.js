import axios from 'axios'

/* ============================================
   ENDPOINTS
============================================ */
export const endpoints = {
  auth: '/api/auth',
  users: '/api/utilisateurs',
  stripe: '/api/stripe',
  abonnements: '/api/abonnements',
  factures: '/api/factures',
  projects: '/api/projets',
  tasks: '/api/taches',
  messages: '/api/messages',
  notifications: '/api/notifications',
  commentaires: '/api/commentaires',
  transactions: '/api/transactions',
}

/* ============================================
   BASE URL via .env
============================================ */
const RAW_BASE = import.meta.env?.VITE_API_URL ?? ''
const API_BASE = (RAW_BASE === '/api' ? '' : RAW_BASE).replace(/\/$/, '')

/* ============================================
   Client axios
============================================ */
const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
})

/* utils */
const shortLang = (v) =>
  String(v || '').toLowerCase().startsWith('fr') ? 'fr' : 'en'
const cleanId = (id) => (id == null ? id : String(id).split(':')[0])

/* --------- Auth: bearer auto + langue --------- */
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  const storedLang = localStorage.getItem('lang') || navigator.language || 'fr'
  config.headers['Accept-Language'] = shortLang(storedLang)
  return config
})

/* --------- 401 global --------- */
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
  login: (credentials) => api.post(`${endpoints.auth}/connexion`, credentials),
  register: (userData) => api.post(`${endpoints.auth}/inscription`, userData),
  forgotPassword: (emailData) =>
    api.post(`${endpoints.auth}/mot-de-passe-oublie`, emailData),
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },
}

/* ===================== PAIEMENTS (Stripe) ===================== */
export const stripeAPI = {
  createPaymentIntent: () =>
    api.post(`${endpoints.stripe}/create-payment-intent`),
  // le backend attend payment_intent_id (ou paymentIntentId)
  confirmPayment: (paymentIntentId) =>
    api.post(`${endpoints.stripe}/confirm-payment`, { payment_intent_id: paymentIntentId }),
  listMyTransactions: () => api.get(`${endpoints.stripe}/mes-transactions`),
}

/* ===================== ABONNEMENTS ===================== */
export const abonnementAPI = {
  list: () => api.get(`${endpoints.abonnements}`),
  souscrire: (abonnementData) =>
    api.post(`${endpoints.abonnements}/souscrire`, abonnementData),
  getByUserId: (userId) =>
    api.get(`${endpoints.abonnements}/utilisateur/${cleanId(userId)}`),
  renew: (id) => api.put(`${endpoints.abonnements}/${cleanId(id)}/renouveler`),
  cancel: (id) => api.put(`${endpoints.abonnements}/${cleanId(id)}/resilier`),
}

/* ===================== FACTURES ===================== */
export const factureAPI = {
  getAllAdmin: (params) => api.get(`${endpoints.factures}`, { params }),
  getMesFactures: () => api.get(`${endpoints.factures}/mes-factures`),
  getToutes: (params) => api.get(`${endpoints.factures}/toutes`, { params }),
  telechargerPDF: (id, langue = 'fr') =>
    api.get(`${endpoints.factures}/${cleanId(id)}/telecharger`, {
      params: { langue, _t: Date.now() },
      responseType: 'blob',
      headers: { 'Accept-Language': shortLang(langue) },
    }),
  getDonneesPDF: (id) => api.get(`${endpoints.factures}/${cleanId(id)}/pdf-data`),
  getById: (id) => api.get(`${endpoints.factures}/${cleanId(id)}`),
}

/* ===================== PROJETS ===================== */
export const projectAPI = {
  list: () => api.get(endpoints.projects),
  getAll: () => api.get(endpoints.projects),
  getPublics: () => api.get(`${endpoints.projects}/publics`),
  create: (payload) => api.post(endpoints.projects, payload),
  byId: (id) => api.get(`${endpoints.projects}/${cleanId(id)}`),
  byUser: (userId) =>
    api.get(`${endpoints.projects}/utilisateur/${cleanId(userId)}`),
  update: (id, payload) =>
    api.put(`${endpoints.projects}/${cleanId(id)}`, payload),
  delete: (id) => api.delete(`${endpoints.projects}/${cleanId(id)}`),
  addMember: (projectId, userId, role = 'MEMBRE') =>
    api.post(
      `${endpoints.projects}/${cleanId(projectId)}/membres/${cleanId(userId)}`,
      { role }
    ),
  removeMember: (projectId, userId) =>
    api.delete(
      `${endpoints.projects}/${cleanId(projectId)}/membres/${cleanId(userId)}`
    ),
  getStatuses: () => api.get(`${endpoints.projects}/statuts`),
  getProjectMembers: (projectId) =>
    api.get(`${endpoints.projects}/${cleanId(projectId)}/membres`),
}

/* ===================== TÂCHES ===================== */
export const taskAPI = {
  list: () => api.get(endpoints.tasks),
  create: (payload) => api.post(endpoints.tasks, payload),
  update: (id, payload) => api.put(`${endpoints.tasks}/${cleanId(id)}`, payload),
  updateStatus: (id, statut) =>
    api.put(`${endpoints.tasks}/${cleanId(id)}/statut`, { statut }),
  delete: (id) => api.delete(`${endpoints.tasks}/${cleanId(id)}`),
  byUser: (userId) =>
    api.get(`${endpoints.tasks}/utilisateur/${cleanId(userId)}`),
  byChefProjet: (userId) =>
    api.get(`${endpoints.tasks}/utilisateur/${cleanId(userId)}`),
  byProjet: (projetId) =>
    api.get(`${endpoints.tasks}/projet/${cleanId(projetId)}`),
  getStatuses: () => api.get(`${endpoints.tasks}/statuts`),
  getPriorities: () => api.get(`${endpoints.tasks}/priorites`),
  deleteTask: (id) => api.delete(`${endpoints.tasks}/${cleanId(id)}`),

  // ADMIN F7 - Voir toutes les tâches (maintenance plateforme + annulation)
  getAllAdmin: () => api.get(`${endpoints.tasks}/admin/all`),

  // ADMIN F7 - Annuler une tâche à tout moment
  annulerParAdmin: (id) => api.put(`${endpoints.tasks}/${cleanId(id)}/annuler`),
}

/* ===================== UTILISATEURS ===================== */
export const userAPI = {
  list: ({ q = '', role = '', statut = '', page = 0, size = 20 } = {}) =>
    api.get(endpoints.users, { params: { q, role, statut, page, size } }),

  create: (payload) => api.post(endpoints.users, payload),
  updateProfile: (id, payload) =>
    api.put(`${endpoints.users}/${cleanId(id)}`, payload),

  search: (q) =>
    api.get(`${endpoints.users}/recherche`, {
      params: { q: String(q || '').trim() },
    }),

  updateRole: (id, role) =>
    api.put(`${endpoints.users}/${cleanId(id)}/role`, { role }),
  getById: (id) => api.get(`${endpoints.users}/${cleanId(id)}`),
}

/* ===================== MESSAGES ===================== */
export const messagesAPI = {
  list: () => api.get(endpoints.messages),
  byProjet: (projectId) =>
    api.get(`${endpoints.messages}/projet/${cleanId(projectId)}`),
  send: (payload) => api.post(endpoints.messages, payload),
  markAsRead: (id) => api.put(`${endpoints.messages}/${cleanId(id)}/lu`),
  getByProjet: (projectId) =>
    api.get(`${endpoints.messages}/projet/${cleanId(projectId)}`),
  sendMessage: (payload) => api.post(endpoints.messages, payload),
}

/* ===================== NOTIFICATIONS ===================== */
export const notificationAPI = {
  list: (userId) =>
    api.get(`${endpoints.notifications}/user/${cleanId(userId)}`),
  markAsRead: (id, userId) =>
    api.put(`${endpoints.notifications}/${cleanId(id)}/read`, null, {
      params: { userId: cleanId(userId) },
    }),
  markAllAsRead: (userId) =>
    api.put(`${endpoints.notifications}/user/${cleanId(userId)}/mark-all-read`),
  countUnread: (userId) =>
    api.get(`${endpoints.notifications}/user/${cleanId(userId)}/count`),
  delete: (id, userId) =>
    api.delete(`${endpoints.notifications}/${cleanId(id)}`, {
      params: { userId: cleanId(userId) },
    }),
  getNotifications: (userId) =>
    api.get(`${endpoints.notifications}/user/${cleanId(userId)}`),
  deleteNotification: (id, userId) =>
    api.delete(`${endpoints.notifications}/${cleanId(id)}`, {
      params: userId ? { userId: cleanId(userId) } : undefined,
    }),
}

/* ===================== COMMENTAIRES ===================== */
export const commentaireAPI = {
  list: () => api.get(endpoints.commentaires),
  byTache: (tacheId) =>
    api.get(`${endpoints.commentaires}/tache/${cleanId(tacheId)}`),
  create: (payload) => api.post(endpoints.commentaires, payload),
  update: (id, payload) =>
    api.put(`${endpoints.commentaires}/${cleanId(id)}`, payload),
  delete: (id) => api.delete(`${endpoints.commentaires}/${cleanId(id)}`),
}

/* ===================== TRANSACTIONS ===================== */
export const transactionAPI = {
  getAllAdmin: () => api.get(`${endpoints.transactions}/admin/all`),
  getById: (id) => api.get(`${endpoints.transactions}/${cleanId(id)}`),
  listMine: () => api.get(`${endpoints.stripe}/mes-transactions`),
}

export default api
