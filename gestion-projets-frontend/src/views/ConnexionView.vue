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
              <form @submit.prevent="handleConnexion">
                <div class="mb-3">
                  <label for="email" class="form-label">Email</label>
                  <input
                    type="email"
                    class="form-control"
                    id="email"
                    v-model="form.email"
                    required
                    placeholder="votre@email.com"
                  >
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label">Mot de passe</label>
                  <div class="input-group">
                    <input
                      :type="showPassword ? 'text' : 'password'"
                      class="form-control"
                      id="password"
                      v-model="form.motDePasse"
                      required
                      placeholder="••••••••"
                    >
                    <button
                      type="button"
                      class="btn btn-outline-secondary"
                      @click="showPassword = !showPassword"
                    >
                      <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                    </button>
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
                    to="/mot-de-passe-oublie"
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
                    to="/inscription"
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
import { authAPI } from '@/services/api.js'

const router = useRouter()

const form = reactive({
  email: '',
  motDePasse: '',
  remember: false
})

const error = ref(null)
const success = ref(null)
const loading = ref(false)
const showPassword = ref(false)

const handleConnexion = async () => {
  loading.value = true
  error.value = null
  success.value = null

  try {
    const response = await authAPI.login({
      email: form.email,
      motDePasse: form.motDePasse
    })

    const { token, user } = response.data

    success.value = 'Connexion réussie ! Redirection...'

    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))

    setTimeout(() => {
      switch (user.role) {
        case 'ADMINISTRATEUR':
          router.push('/admin/tableau-de-bord')
          break
        case 'CHEF_PROJET':
          router.push('/tableau-bord-chef-projet')
          break
        case 'MEMBRE':
          router.push('/tableau-bord-membre')
          break
        case 'VISITEUR':
          router.push('/projets-publics')
          break
        default:
          router.push('/')
      }
    }, 1500)

  } catch (err) {
    if (err.response?.status === 401) {
      error.value = 'Email ou mot de passe incorrect.'
    } else if (err.response?.data?.message) {
      error.value = err.response.data.message
    } else {
      error.value = 'Erreur de connexion. Veuillez réessayer.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.auth-card {
  background: rgba(255, 255, 255, 0.95);
  border: none;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.auth-logo {
  height: 60px;
  width: 60px;
  border-radius: 50%;
}

.btn-collabpro {
  background: linear-gradient(135deg, #007bff, #0056b3);
  border: none;
  color: white;
  font-weight: 600;
}

.btn-collabpro:hover:not(:disabled) {
  background: linear-gradient(135deg, #0056b3, #004085);
  color: white;
}

.text-collabpro {
  color: #007bff;
}

.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}
</style>
