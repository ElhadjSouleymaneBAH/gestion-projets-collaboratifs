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
  fichiers: '/api/fichiers',
  public: '/api/public'
}

/* ============================================
   BASE URL via .env
============================================ */
const RAW_BASE = import.meta.env?.VITE_API_BASE_URL ?? ''
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
  const isPublic =
    config.url?.includes('/api/public/') ||
    config.url?.includes('/api/projets/public') ||
    config.url?.includes('/api/projets/publics')

  if (token && !isPublic) {
    config.headers.Authorization = `Bearer ${token}`
  }

  console.log(`[API REQUEST] ${config.method?.toUpperCase()} ${config.url}`, {
    params: config.params,
    data: config.data
  })

  return config
})

/* --------- Response interceptor --------- */
api.interceptors.response.use(
  (res) => {
    console.log(`[API SUCCESS] ${res.config.method?.toUpperCase()} ${res.config.url}`, {
      status: res.status,
      data: res.data
    })
    return res
  },
  (error) => {
    console.error('[API ERROR]', {
      url: error.config?.url,
      method: error.config?.method?.toUpperCase(),
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message,
      requestHeaders: {
        Authorization: error.config?.headers?.Authorization ? 'Bearer ***' : 'None',
        'Accept-Language': error.config?.headers?.['Accept-Language']
      }
    })

    if (error.response?.status === 401) {
      console.warn('[API] 401 Unauthorized - Redirection vers /connexion')
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (!window.location.pathname.includes('/connexion')) {
        window.location.href = '/connexion'
      }
    }

    if (error.response?.status === 403) {
      console.warn('[API] 403 Forbidden - Accès refusé')
    }

    if (error.response?.status === 404) {
      console.warn('[API] 404 Not Found - Ressource introuvable')
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

  getPublicProjectById: (id) =>
    api.get(`${endpoints.projects}/public/${cleanId(id)}`, {
      headers: { 'Accept-Language': shortLang(localStorage.getItem('lang') || 'fr') }
    }),

  byId: (id) => api.get(`${endpoints.projects}/${cleanId(id)}`),
  byUser: (userId) =>
    api.get(`${endpoints.projects}/utilisateur/${cleanId(userId)}`),

  create: (payload) => api.post(endpoints.projects, payload),
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
  getProjectMembers: (projectId) =>
    api.get(`${endpoints.projects}/${cleanId(projectId)}/membres`),

  getAdminDetails: (id) =>
    api.get(`${endpoints.projects}/admin/${cleanId(id)}/details`),

  getStatuses: () => api.get(`${endpoints.projects}/statuts`),
}
/* ===================== TÂCHES ===================== */
export const taskAPI = {
  list: () => api.get(endpoints.tasks),

  create: (payload) => api.post(endpoints.tasks, payload),

  update: (id, payload) =>
    api.put(`${endpoints.tasks}/${cleanId(id)}`, payload),
  getById: (id) => api.get(`${endpoints.tasks}/${cleanId(id)}`),
  updateStatus: (id, statut) =>
      api.put(
      `${endpoints.tasks}/${cleanId(id)}/statut`,
      { statut: String(statut) },
      { headers: { 'Content-Type': 'application/json' } }
    ),

  // Déplacer une tâche (drag & drop)
  deplacerKanban: (id, colonneDestination) =>
    api.put(
      `${endpoints.tasks}/${cleanId(id)}/deplacer`,
      { colonneDestination: String(colonneDestination) },
      { headers: { 'Content-Type': 'application/json' } }
    ),

  assignTask: (taskId, userId) =>
    api.put(
      `${endpoints.tasks}/${cleanId(taskId)}/assigner`,
      { idAssigne: cleanId(userId) },
      { headers: { 'Content-Type': 'application/json' } }
    ),

  delete: (id) => api.delete(`${endpoints.tasks}/${cleanId(id)}`),

  byUser: (userId) =>
    api.get(`${endpoints.tasks}/utilisateur/${cleanId(userId)}`),

  byChefProjet: (chefId) =>
    api.get(`${endpoints.tasks}/chef/${cleanId(chefId)}`),

  byProjet: (projetId) =>
    api.get(`${endpoints.tasks}/projet/${cleanId(projetId)}`),
  getByIdWithDetails: (id) => api.get(`${endpoints.tasks}/${cleanId(id)}`),

  getStatuses: () => api.get(`${endpoints.tasks}/statuts`),
  getPriorities: () => api.get(`${endpoints.tasks}/priorites`),

  getAllAdmin: () => api.get(`${endpoints.tasks}`),
  annulerParAdmin: (id) =>
    api.put(`${endpoints.tasks}/${cleanId(id)}/annuler`),
}

/* ===================== UTILISATEURS ===================== */
export const userAPI = {
  list: ({ q = '', role = '', statut = '', page = 0, size = 20 } = {}) =>
    api.get(endpoints.users, { params: { q, role, statut, page, size } }),
  create: (payload) => api.post(endpoints.users, payload),
  updateProfile: (id, payload) =>
    api.put(`${endpoints.users}/${cleanId(id)}`, payload),
  search: (q) =>
    api.get(`${endpoints.users}/recherche`, { params: { q: String(q || '').trim() } }),
  updateRole: (id, role) =>
    api.put(`${endpoints.users}/${cleanId(id)}/role`, { role }),
  getById: (id) => api.get(`${endpoints.users}/${cleanId(id)}`),
}

/* ===================== MESSAGES ===================== */
export const messagesAPI = {
  list: () => api.get(endpoints.messages),

  byProjet: (projectId) =>
    api.get(`${endpoints.messages}/projet/${cleanId(projectId)}`),

  send: (payload) => {
    if (!payload.projetId) {
      return Promise.reject(new Error('projetId est requis pour envoyer un message'))
    }
    return api.post(`${endpoints.messages}/projet/${cleanId(payload.projetId)}`, {
      contenu: payload.contenu,
      type: payload.type || 'TEXT'
    })
  },

  markAsRead: (id) => api.put(`${endpoints.messages}/${cleanId(id)}/marquer-lu`),
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
}

/* ===================== COMMENTAIRES ===================== */
export const commentaireAPI = {
  list: () => api.get(endpoints.commentaires),
  getByTache: (tacheId) =>
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

/* ===================== FICHIERS ===================== */
export const fichierAPI = {
  /**
   * Upload un fichier dans un projet
   * @param {FormData} formData - FormData contenant le fichier
   * @param {Number} projetId - ID du projet
   */
  upload: (formData, projetId) => {
    const token = localStorage.getItem('token')
    return api.post(`${endpoints.fichiers}/upload`, formData, {
      params: { projetId: cleanId(projetId) },
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}`
      }
    })
  },

  /**
   * Récupère tous les fichiers d'un projet
   * @param {Number} projetId - ID du projet
   */
  getByProjet: (projetId) =>
    api.get(`${endpoints.fichiers}/projet/${cleanId(projetId)}`),

  /**
   * Télécharge un fichier
   * @param {Number} fichierId - ID du fichier
   * @param {Number} projetId - ID du projet
   */
  download: (fichierId, projetId) =>
    api.get(`${endpoints.fichiers}/${cleanId(fichierId)}/telecharger`, {
      params: { projetId: cleanId(projetId) },
      responseType: 'blob'
    }),

  /**
   * Supprime un fichier
   * @param {Number} fichierId - ID du fichier
   * @param {Number} projetId - ID du projet
   */
  delete: (fichierId, projetId) => {
    const token = localStorage.getItem('token')
    return api.delete(`${endpoints.fichiers}/${cleanId(fichierId)}`, {
      params: { projetId: cleanId(projetId) },
      headers: { 'Authorization': `Bearer ${token}` }
    })
  },

  /**
   * Récupère les informations d'un fichier
   * @param {Number} fichierId - ID du fichier
   * @param {Number} projetId - ID du projet
   */
  getInfo: (fichierId, projetId) =>
    api.get(`${endpoints.fichiers}/${cleanId(fichierId)}`, {
      params: { projetId: cleanId(projetId) }
    })
}

export default api
