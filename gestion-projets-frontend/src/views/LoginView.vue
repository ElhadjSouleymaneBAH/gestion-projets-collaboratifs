<!-- LoginView.vue -->
<template>
  <div class="auth-container d-flex align-items-center justify-content-center py-5">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-4">
          <div class="card auth-card">
            <div class="card-body p-5">

              <!-- Logo et titre -->
              <div class="text-center mb-4">
                <img src="/logo-collabpro.png" alt="CollabPro" class="auth-logo mb-3">
                <h3 class="fw-bold text-collabpro">Se connecter</h3>
                <p class="text-muted">Accédez à votre espace CollabPro</p>
              </div>

              <!-- Messages d'erreur -->
              <div v-if="error" class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-triangle me-2"></i>
                {{ error }}
                <button type="button" class="btn-close" @click="error = null"></button>
              </div>

              <!-- Messages de succès -->
              <div v-if="success" class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle me-2"></i>
                {{ success }}
                <button type="button" class="btn-close" @click="success = null"></button>
              </div>

              <!-- Formulaire de connexion -->
              <form @submit.prevent="handleLogin">
                <div class="mb-3">
                  <label for="email" class="form-label">Email</label>
                  <input
                    type="text"
                    class="form-control"
                    id="email"
                    v-model="form.email"
                    :class="{ 'is-invalid': errors.email }"
                    required
                    autocomplete="email"
                  >
                  <div v-if="errors.email" class="invalid-feedback">
                    {{ errors.email }}
                  </div>
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label">Mot de passe</label>
                  <div class="input-group">
                    <input
                      :type="showPassword ? 'text' : 'password'"
                      class="form-control"
                      id="password"
                      v-model="form.password"
                      :class="{ 'is-invalid': errors.password }"
                      required
                      autocomplete="current-password"
                    >
                    <button
                      type="button"
                      class="btn btn-outline-secondary"
                      @click="showPassword = !showPassword"
                    >
                      <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                    </button>
                  </div>
                  <div v-if="errors.password" class="invalid-feedback d-block">
                    {{ errors.password }}
                  </div>
                </div>

                <div class="mb-3 form-check">
                  <input
                    type="checkbox"
                    class="form-check-input"
                    id="remember"
                    v-model="form.remember"
                  >
                  <label class="form-check-label" for="remember">
                    Se souvenir de moi
                  </label>
                </div>

                <button
                  type="submit"
                  class="btn btn-collabpro w-100 mb-3"
                  :disabled="loading"
                >
                  <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                  {{ loading ? 'Connexion...' : 'Se connecter' }}
                </button>

                <div class="text-center">
                  <router-link
                    to="/forgot-password"
                    class="text-decoration-none text-muted"
                  >
                    Mot de passe oublié ?
                  </router-link>
                </div>
              </form>

              <!-- Lien vers inscription -->
              <hr class="my-4">
              <div class="text-center">
                <p class="text-muted mb-0">
                  Pas encore de compte ?
                  <router-link
                    to="/register"
                    class="text-collabpro text-decoration-none fw-bold"
                  >
                    S'inscrire
                  </router-link>
                </p>
              </div>
            </div>
          </div>

          <!-- Lien retour accueil -->
          <div class="text-center mt-3">
            <router-link
              to="/"
              class="text-muted text-decoration-none"
            >
              <i class="fas fa-arrow-left me-2"></i>
              Retour à l'accueil
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '@/services/api'

const router = useRouter()

// State réactif
const form = reactive({
  email: '',
  password: '',
  remember: false
})

const errors = reactive({
  email: null,
  password: null
})

const error = ref(null)
const success = ref(null)
const loading = ref(false)
const showPassword = ref(false)

// Validation
const validateForm = () => {
  // Reset errors
  errors.email = null
  errors.password = null

  let isValid = true

  // Validation email
  if (!form.email) {
    errors.email = 'L\'email est requis'
    isValid = false
  } else if (!/\S+@\S+\.\S+/.test(form.email)) {
    errors.email = 'Format d\'email invalide'
    isValid = false
  }

  // Validation mot de passe
  if (!form.password) {
    errors.password = 'Le mot de passe est requis'
    isValid = false
  } else if (form.password.length < 4) {
    errors.password = 'Le mot de passe doit contenir au moins 4 caractères'
    isValid = false
  }

  return isValid
}

// Gestion de la connexion
const handleLogin = async () => {
  if (!validateForm()) return

  loading.value = true
  error.value = null
  success.value = null

  try {
    // Appel API de connexion (F2) avec votre service
    const response = await authAPI.login({
      email: form.email,
      motDePasse: form.password
    })

    // Succès
    success.value = 'Connexion réussie ! Redirection...'

    // Stocker le token et les infos utilisateur
    if (response.data.token) {
      localStorage.setItem('token', response.data.token)
    }
    if (response.data.user) {
      localStorage.setItem('user', JSON.stringify(response.data.user))
    }

    // Redirection selon le rôle
    setTimeout(() => {
      if (response.data.user?.role === 'ADMINISTRATEUR') {
        router.push('/admin/dashboard')
      } else if (response.data.user?.role === 'CHEF_PROJET') {
        router.push('/chef-projet/dashboard')
      } else {
        router.push('/membre/dashboard')
      }
    }, 1000)

  } catch (err) {
    console.error('Erreur de connexion:', err)

    if (err.response?.status === 401) {
      error.value = 'Email ou mot de passe incorrect'
    } else if (err.response?.status === 403) {
      error.value = 'Compte désactivé. Contactez l\'administrateur'
    } else {
      error.value = err.response?.data?.message || 'Erreur de connexion. Veuillez réessayer.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
}

.auth-card {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.auth-logo {
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>
