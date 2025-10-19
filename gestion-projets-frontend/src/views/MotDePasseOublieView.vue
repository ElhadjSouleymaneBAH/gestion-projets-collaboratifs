
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
                <h3 class="fw-bold text-collabpro">{{ t('motDePasseOublie.titre') }}</h3>
                <p class="text-muted">{{ t('motDePasseOublie.sousTitre') }}</p>
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
                    <label for="email" class="form-label">{{ t('connexion.email') }}</label>
                    <input
                      type="email"
                      class="form-control"
                      id="email"
                      v-model="email"
                      required
                      :placeholder="t('motDePasseOublie.email')"
                      autocomplete="email"
                    >
                  </div>

                  <button
                    type="submit"
                    class="btn btn-collabpro w-100 mb-3"
                    :disabled="loading"
                  >
                    <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                    {{ loading ? t('commun.chargement') : t('motDePasseOublie.envoyer') }}
                  </button>
                </form>
              </div>

              <!-- Confirmation d'envoi -->
              <div v-else class="text-center">
                <div class="mb-4">
                  <i class="fas fa-envelope-open-text fa-3x text-success mb-3"></i>
                  <h4 class="text-success">{{ t('motDePasseOublie.emailEnvoye') }}</h4>
                  <p class="text-muted">
                    {{ t('motDePasseOublie.sousTitre') }}<br>
                    <strong>{{ email }}</strong>
                  </p>
                </div>

                <button
                  class="btn btn-outline-primary w-100"
                  @click="renvoyerEmail"
                  :disabled="cooldown > 0 || loading"
                >
                  <span v-if="cooldown > 0">{{ t('motDePasseOublie.renvoyerDans') }} {{ cooldown }}s</span>
                  <span v-else><i class="fas fa-redo me-2"></i>{{ t('motDePasseOublie.renvoyer') }}</span>
                </button>
              </div>

              <!-- Liens de navigation -->
              <hr class="my-4">
              <div class="text-center">
                <router-link to="/connexion" class="text-collabpro text-decoration-none fw-bold me-3">
                  <i class="fas fa-arrow-left me-1"></i>{{ t('motDePasseOublie.retourConnexion') }}
                </router-link>
                <router-link to="/inscription" class="text-muted text-decoration-none">
                  {{ t('inscription.titre') }}
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
import { ref, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { authAPI } from '@/services/api.js'

const { t } = useI18n()

const email = ref('')
const error = ref(null)
const loading = ref(false)
const emailEnvoye = ref(false)
const cooldown = ref(0)

let cooldownInterval = null

const envoyerEmailReset = async () => {
  if (!email.value) return
  loading.value = true
  error.value = null

  try {
    await authAPI.forgotPassword({ email: email.value })
    emailEnvoye.value = true
    demarrerCooldown()
  } catch (err) {
    if (err?.response?.status === 404) {
      error.value = t('motDePasseOublie.emailNonTrouve')
    } else {
      error.value = t('motDePasseOublie.erreurEnvoi')
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
    await authAPI.forgotPassword({ email: email.value })
    demarrerCooldown()
  } catch {            // pas de variable capturée => évite le warning ESLint no-unused-vars
    error.value = t('motDePasseOublie.erreurRenvoi')
  } finally {
    loading.value = false
  }
}

const demarrerCooldown = () => {
  cooldown.value = 60
  if (cooldownInterval) clearInterval(cooldownInterval)
  cooldownInterval = setInterval(() => {
    cooldown.value--
    if (cooldown.value <= 0) {
      clearInterval(cooldownInterval)
      cooldownInterval = null
    }
  }, 1000)
}

onUnmounted(() => {
  if (cooldownInterval) clearInterval(cooldownInterval)
})
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
.auth-logo { height: 60px; width: 60px; border-radius: 50%; }
.btn-collabpro {
  background: linear-gradient(135deg, #007bff, #0056b3);
  border: none; color: #fff; font-weight: 600;
}
.btn-collabpro:hover:not(:disabled) {
  background: linear-gradient(135deg, #0056b3, #004085);
  color: #fff;
}
.text-collabpro { color: #007bff; }
.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}
</style>
