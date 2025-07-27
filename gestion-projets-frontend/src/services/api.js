import axios from 'axios'

// Configuration de base pour l'API Spring Boot
const API_BASE_URL = 'http://localhost:8080/api'

// Instance Axios avec configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Intercepteur pour ajouter automatiquement le token JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Intercepteur pour gérer les réponses et erreurs - CORRECTION ICI
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré ou invalide
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/connexion'
    }
    return Promise.reject(error)
  }
)

// Services d'authentification (F1, F2)
export const authAPI = {
  // F2 : Se connecter
  login: (credentials) => {
    return api.post('/auth/login', credentials)
  },

  // F1 : S'inscrire
  register: (userData) => {
    return api.post('/auth/inscription', userData)
  },

  // Déconnexion
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}

// Services des projets (F3, F6, F8)
export const projectAPI = {
  // F3 : Consulter projets publics
  getAllProjects: () => {
    return api.get('/projets')
  },

  // F6 : Gérer les projets
  createProject: (projectData) => {
    return api.post('/projets', projectData)
  },

  getProjectById: (id) => {
    return api.get(`/projets/${id}`)
  },

  updateProject: (id, projectData) => {
    return api.put(`/projets/${id}`, projectData)
  },

  deleteProject: (id) => {
    return api.delete(`/projets/${id}`)
  },

  // F8 : Gestion des membres
  addMember: (projectId, userId) => {
    return api.post(`/projets/${projectId}/membres/${userId}`)
  },

  removeMember: (projectId, userId) => {
    return api.delete(`/projets/${projectId}/membres/${userId}`)
  },

  getProjectMembers: (projectId) => {
    return api.get(`/projets/${projectId}/membres`)
  }
}

// Services des tâches (F7)
export const taskAPI = {
  getAllTasks: () => {
    return api.get('/taches')
  },

  createTask: (taskData) => {
    return api.post('/taches', taskData)
  },

  updateTask: (id, taskData) => {
    return api.put(`/taches/${id}`, taskData)
  },

  deleteTask: (id) => {
    return api.delete(`/taches/${id}`)
  },

  getTasksByProject: (projectId) => {
    return api.get(`/taches/projet/${projectId}`)
  },

  getTasksByUser: (userId) => {
    return api.get(`/taches/utilisateur/${userId}`)
  }
}

export default api
