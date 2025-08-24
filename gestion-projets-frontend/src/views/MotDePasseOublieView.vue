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
                <h3 class="fw-bold text-collabpro">Mot de passe oublié</h3>
                <p class="text-muted">Récupérez l'accès à votre compte</p>
              </div>

              <!-- Demande de réinitialisation -->
              <div v-if="!emailEnvoye">

                <!-- Messages d'erreur -->
                <div v-if="error" class="alert alert-danger">
                  <i class="fas fa-exclamation-triangle me-2"></i>
                  {{ error }}
                </div>

                <form @submit.prevent="envoyerEmailReset">
                  <div class="mb-4">
                    <label for="email" class="form-label">Adresse email</label>
                    <input
                      type="email"
                      class="form-control"
                      id="email"
                      v-model="email"
                      required
                      placeholder="votre@email.com"
                    >
                  </div>

                  <button
                    type="submit"
                    class="btn btn-collabpro w-100 mb-3"
                    :disabled="loading"
                  >
                    <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                    {{ loading ? 'Envoi...' : 'Envoyer le lien' }}
                  </button>
                </form>
              </div>

              <!-- Confirmation d'envoi -->
              <div v-else class="text-center">
                <div class="mb-4">
                  <i class="fas fa-envelope-open-text fa-3x text-success mb-3"></i>
                  <h4 class="text-success">Email envoyé !</h4>
                  <p class="text-muted">
                    Un email de réinitialisation a été envoyé à<br>
                    <strong>{{ email }}</strong>
                  </p>
                </div>

                <button
                  class="btn btn-outline-primary w-100"
                  @click="renvoyerEmail"
                  :disabled="cooldown > 0"
                >
                  <span v-if="cooldown > 0">Renvoyer dans {{ cooldown }}s</span>
                  <span v-else><i class="fas fa-redo me-2"></i>Renvoyer</span>
                </button>
              </div>

              <!-- Liens de navigation -->
              <hr class="my-4">
              <div class="text-center">
                <router-link to="/connexion" class="text-collabpro text-decoration-none fw-bold me-3">
                  <i class="fas fa-arrow-left me-1"></i>Retour connexion
                </router-link>
                <router-link to="/inscription" class="text-muted text-decoration-none">
                  Créer un compte
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { authAPI } from '@/services/api.js'

const email = ref('')
const error = ref(null)
const loading = ref(false)
const emailEnvoye = ref(false)
const cooldown = ref(0)

const envoyerEmailReset = async () => {
  if (!email.value) return

  loading.value = true
  error.value = null

  try {
    const response = await authAPI.forgotPassword({
      email: email.value
    })

    emailEnvoye.value = true
    demarrerCooldown()

  } catch (err) {
    if (err.response?.status === 404) {
      error.value = 'Aucun compte associé à cette adresse email'
    } else {
      error.value = 'Erreur lors de l\'envoi de l\'email'
    }
  } finally {
    loading.value = false
  }
}

const renvoyerEmail = async () => {
  if (cooldown.value > 0) return

  loading.value = true
  error.value = null

  try {
    await authAPI.forgotPassword({
      email: email.value
    })

    demarrerCooldown()

  } catch (err) {
    error.value = 'Erreur lors du renvoi'
  } finally {
    loading.value = false
  }
}

const demarrerCooldown = () => {
  cooldown.value = 60

  const interval = setInterval(() => {
    cooldown.value--
    if (cooldown.value <= 0) {
      clearInterval(interval)
    }
  }, 1000)
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
