import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authAPI } from '@/services/api.js'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const loading = ref(false)
  const error = ref(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || null)
  const isAdmin = computed(() => user.value?.role === 'ADMINISTRATEUR')
  const isChefProjet = computed(() => user.value?.role === 'CHEF_PROJET')
  const isMembre = computed(() => user.value?.role === 'MEMBRE')

  // Actions
  const login = async (credentials) => {
    loading.value = true
    error.value = null

    try {
      const response = await authAPI.login(credentials)
      const { token: authToken, user: userData } = response.data

      // Stockage des données d'authentification
      token.value = authToken
      user.value = userData

      // Persistance dans localStorage
      localStorage.setItem('token', authToken)
      localStorage.setItem('user', JSON.stringify(userData))

      return { success: true, user: userData }

    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur de connexion'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  const register = async (userData) => {
    loading.value = true
    error.value = null

    try {
      const response = await authAPI.register(userData)

      // Connexion automatique après inscription
      if (response.data.success) {
        const loginResult = await login({
          email: userData.email,
          motDePasse: userData.motDePasse
        })
        return loginResult
      }

      return { success: true }

    } catch (err) {
      error.value = err.response?.data?.message || 'Erreur lors de l\'inscription'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    // Nettoyage des données
    token.value = null
    user.value = null
    error.value = null

    // Suppression du localStorage
    authAPI.logout()
  }

  const clearError = () => {
    error.value = null
  }

  // Vérification de la validité du token au démarrage
  const checkAuth = () => {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')

    if (storedToken && storedUser) {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
    } else {
      logout()
    }
  }

  return {
    // State
    token,
    user,
    loading,
    error,

    // Getters
    isAuthenticated,
    userRole,
    isAdmin,
    isChefProjet,
    isMembre,

    // Actions
    login,
    register,
    logout,
    clearError,
    checkAuth
  }
})
