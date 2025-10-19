<template>
  <div class="auth-container d-flex align-items-center justify-content-center py-5">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card auth-card">
            <div class="card-body p-5">

              <!-- Logo et titre -->
              <div class="text-center mb-4">
                <img :src="logoPath" :alt="$t('app.nom')" class="auth-logo mb-3">
                <h3 class="fw-bold text-collabpro">{{ $t('inscription.titre') }}</h3>
                <p class="text-muted">{{ $t('auth.rejoindreCollaborer') }}</p>
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

              <!-- Formulaire d'inscription -->
              <form @submit.prevent="handleInscription">
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label for="prenom" class="form-label">{{ $t('inscription.prenom') }}</label>
                    <input
                      type="text"
                      class="form-control"
                      id="prenom"
                      v-model="form.prenom"
                      :class="{ 'is-invalid': errors.prenom }"
                      required
                      :placeholder="$t('inscription.prenomPlaceholder')"
                    >
                    <div v-if="errors.prenom" class="invalid-feedback">
                      {{ errors.prenom }}
                    </div>
                  </div>
                  <div class="col-md-6 mb-3">
                    <label for="nom" class="form-label">{{ $t('inscription.nom') }}</label>
                    <input
                      type="text"
                      class="form-control"
                      id="nom"
                      v-model="form.nom"
                      :class="{ 'is-invalid': errors.nom }"
                      required
                      :placeholder="$t('inscription.nomPlaceholder')"
                    >
                    <div v-if="errors.nom" class="invalid-feedback">
                      {{ errors.nom }}
                    </div>
                  </div>
                </div>

                <div class="mb-3">
                  <label for="email" class="form-label">{{ $t('inscription.email') }}</label>
                  <input
                    type="email"
                    class="form-control"
                    id="email"
                    v-model="form.email"
                    :class="{ 'is-invalid': errors.email }"
                    required
                    placeholder="exemple@domaine.com"
                  >
                  <div v-if="errors.email" class="invalid-feedback">
                    {{ errors.email }}
                  </div>
                </div>

                <!-- (SUPPRIMÉ) Sélecteur de rôle -->

                <div class="mb-3">
                  <label for="langue" class="form-label">{{ $t('inscription.languePreferee') }}</label>
                  <select
                    class="form-select"
                    id="langue"
                    v-model="form.langue"
                    :class="{ 'is-invalid': errors.langue }"
                  >
                    <option
                      v-for="langue in languesDisponibles"
                      :key="langue.code"
                      :value="langue.code"
                    >
                      {{ $t(langue.label) }}
                    </option>
                  </select>
                  <div v-if="errors.langue" class="invalid-feedback">
                    {{ errors.langue }}
                  </div>
                </div>

                <div class="mb-3">
                  <label for="password" class="form-label">{{ $t('inscription.motDePasse') }}</label>
                  <div class="input-group">
                    <input
                      :type="showPassword ? 'text' : 'password'"
                      class="form-control"
                      id="password"
                      v-model="form.motDePasse"
                      :class="{ 'is-invalid': errors.motDePasse }"
                      required
                      :placeholder="$t('auth.creezMotDePasse')"
                      :minlength="motDePasseMinLength"
                    >
                    <button
                      type="button"
                      class="btn btn-outline-secondary"
                      @click="showPassword = !showPassword"
                    >
                      <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                    </button>
                  </div>
                  <div v-if="errors.motDePasse" class="invalid-feedback d-block">
                    {{ errors.motDePasse }}
                  </div>
                  <div class="form-text">
                    {{ $t('auth.auMoinsNCaracteres', { n: motDePasseMinLength }) }}
                  </div>
                </div>

                <div class="mb-3">
                  <label for="confirmPassword" class="form-label">{{ $t('inscription.confirmerMotDePasse') }}</label>
                  <input
                    type="password"
                    class="form-control"
                    id="confirmPassword"
                    v-model="form.confirmMotDePasse"
                    :class="{ 'is-invalid': errors.confirmMotDePasse }"
                    required
                    :placeholder="$t('auth.confirmezMotDePasse')"
                    :minlength="motDePasseMinLength"
                  >
                  <div v-if="errors.confirmMotDePasse" class="invalid-feedback">
                    {{ errors.confirmMotDePasse }}
                  </div>
                </div>

                <!-- Acceptation des CGU -->
                <div class="mb-3 form-check">
                  <input
                    type="checkbox"
                    class="form-check-input"
                    id="acceptTerms"
                    v-model="form.acceptTerms"
                    :class="{ 'is-invalid': errors.acceptTerms }"
                    required
                  >
                  <label class="form-check-label" for="acceptTerms">
                    {{ $t('auth.jaccepteCGU') }}
                    <router-link :to="routesCGU" target="_blank" class="text-collabpro">
                      <i class="fas fa-file-contract me-1"></i>
                      {{ $t('auth.conditionsGenerales') }}
                    </router-link>
                  </label>
                  <div v-if="errors.acceptTerms" class="invalid-feedback d-block">
                    {{ errors.acceptTerms }}
                  </div>
                </div>

                <!-- Acceptation de la Politique de Confidentialité -->
                <div class="mb-3 form-check">
                  <input
                    type="checkbox"
                    class="form-check-input"
                    id="acceptPrivacy"
                    v-model="form.acceptPrivacy"
                    :class="{ 'is-invalid': errors.acceptPrivacy }"
                    required
                  >
                  <label class="form-check-label" for="acceptPrivacy">
                    {{ $t('auth.jacceptePolitique') }}
                    <router-link :to="routesPolitique" target="_blank" class="text-collabpro">
                      <i class="fas fa-shield-alt me-1"></i>
                      {{ $t('auth.politiqueConfidentialite') }}
                    </router-link>
                  </label>
                  <div v-if="errors.acceptPrivacy" class="invalid-feedback d-block">
                    {{ errors.acceptPrivacy }}
                  </div>
                </div>

                <button
                  type="submit"
                  class="btn btn-collabpro w-100 mb-3"
                  :disabled="loading"
                >
                  <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                  {{ loading ? $t('auth.creationEnCours') : $t('auth.creerMonCompte') }}
                </button>
              </form>

              <!-- Lien vers connexion -->
              <hr class="my-4">
              <div class="text-center">
                <p class="text-muted mb-0">
                  {{ $t('auth.dejaInscrit') }}
                  <router-link
                    :to="routesConnexion"
                    class="text-collabpro text-decoration-none fw-bold"
                  >
                    {{ $t('connexion.seConnecter') }}
                  </router-link>
                </p>
              </div>
            </div>
          </div>

          <!-- Lien retour accueil -->
          <div class="text-center mt-3">
            <router-link
              :to="routesAccueil"
              class="text-muted text-decoration-none"
            >
              <i class="fas fa-arrow-left me-2"></i>
              {{ $t('auth.retourAccueil') }}
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { authAPI } from '@/services/api.js'

const router = useRouter()
const { t } = useI18n()

// Configuration depuis les variables d'environnement
const defaultLocale = import.meta.env.VITE_DEFAULT_LOCALE || 'fr'
const motDePasseMinLength = parseInt(import.meta.env.VITE_PASSWORD_MIN_LENGTH || '6')
const logoPath = import.meta.env.VITE_LOGO_PATH || '/logo-collabpro.png'

// Routes configurables
const routesAccueil = import.meta.env.VITE_ROUTE_HOME || '/'
const routesConnexion = import.meta.env.VITE_ROUTE_LOGIN || '/connexion'
const routesCGU = import.meta.env.VITE_ROUTE_CGU || '/conditions'
const routesPolitique = import.meta.env.VITE_ROUTE_PRIVACY || '/politique-confidentialite'

// (SUPPRIMÉ) rolesDisponibles

// Configuration des langues disponibles (dynamique)
const languesDisponibles = [
  { code: 'fr', label: 'langues.francais' },
  { code: 'en', label: 'langues.anglais' }
]

// Variables réactives
const form = reactive({
  prenom: '',
  nom: '',
  email: '',
  // (SUPPRIMÉ) role
  langue: defaultLocale,
  motDePasse: '',
  confirmMotDePasse: '',
  acceptTerms: false,
  acceptPrivacy: false
})

const errors = reactive({
  prenom: null,
  nom: null,
  email: null,
  // (SUPPRIMÉ) role
  langue: null,
  motDePasse: null,
  confirmMotDePasse: null,
  acceptTerms: null,
  acceptPrivacy: null
})

const error = ref(null)
const success = ref(null)
const loading = ref(false)
const showPassword = ref(false)

// Routes de redirection configurables par rôle
const redirectionParRole = computed(() => ({
  CHEF_PROJET: import.meta.env.VITE_ROUTE_CHEF_PROJET || '/abonnement-premium',
  MEMBRE: import.meta.env.VITE_ROUTE_MEMBRE || '/tableau-bord-membre',
  VISITEUR: import.meta.env.VITE_ROUTE_VISITEUR || '/projets-publics'
}))

// Validation du formulaire côté client
const validateForm = () => {
  Object.keys(errors).forEach(key => errors[key] = null)
  let isValid = true

  if (!form.prenom.trim()) {
    errors.prenom = t('validation.prenomRequis')
    isValid = false
  }
  if (!form.nom.trim()) {
    errors.nom = t('validation.nomRequis')
    isValid = false
  }
  if (!form.email.trim()) {
    errors.email = t('validation.emailRequis')
    isValid = false
  } else if (!/\S+@\S+\.\S+/.test(form.email)) {
    errors.email = t('validation.emailInvalide')
    isValid = false
  }

  // (SUPPRIMÉ) validation rôle

  if (!form.motDePasse) {
    errors.motDePasse = t('validation.motDePasseRequis')
    isValid = false
  } else if (form.motDePasse.length < motDePasseMinLength) {
    errors.motDePasse = t('validation.motDePasseTropCourt', { min: motDePasseMinLength })
    isValid = false
  }
  if (!form.confirmMotDePasse) {
    errors.confirmMotDePasse = t('validation.confirmerMotDePasse')
    isValid = false
  } else if (form.motDePasse !== form.confirmMotDePasse) {
    errors.confirmMotDePasse = t('validation.motDePassesDifferents')
    isValid = false
  }
  if (!form.acceptTerms) {
    errors.acceptTerms = t('validation.accepterCGU')
    isValid = false
  }
  if (!form.acceptPrivacy) {
    errors.acceptPrivacy = t('validation.accepterPolitique')
    isValid = false
  }
  return isValid
}

// Inscription via API backend
const handleInscription = async () => {
  if (!validateForm()) return

  loading.value = true
  error.value = null
  success.value = null

  try {
    const response = await authAPI.register({
      nom: form.nom.trim(),
      prenom: form.prenom.trim(),
      email: form.email.toLowerCase().trim(),
      motDePasse: form.motDePasse,
      // (SUPPRIMÉ) role
      langue: form.langue,
      cguAccepte: form.acceptTerms && form.acceptPrivacy
    })

    const { token, user } = response.data || {}

    success.value = t('validation.compteCreeeSucces')

    if (token && user) {
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
    }

    const redirectDelay = parseInt(import.meta.env.VITE_REDIRECT_DELAY || '1500')

    // Par défaut après inscription : rôle VISITEUR
    setTimeout(() => {
      const routeRedirection = redirectionParRole.value['VISITEUR'] || routesAccueil
      router.push(routeRedirection)
    }, redirectDelay)

  } catch (err) {
    if (err.response?.status === 400) {
      const backendErrors = err.response.data.errors
      if (backendErrors) {
        Object.keys(backendErrors).forEach(field => {
          if (Object.prototype.hasOwnProperty.call(errors, field)) {
            errors[field] = backendErrors[field]
          }
        })
      } else if (err.response.data.message) {
        error.value = err.response.data.message
      } else {
        error.value = t('validation.donneesInvalides')
      }
    } else if (err.response?.status === 409) {
      errors.email = t('validation.emailDejaUtiliseErr')
    } else if (err.response?.data?.message) {
      error.value = err.response.data.message
    } else {
      error.value = t('validation.erreurCreationCompte')
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
.auth-logo { height: 80px; width: 80px; border-radius: 50%; }
.btn-collabpro { background: linear-gradient(135deg, #007bff, #0056b3); border: none; color: white; font-weight: 600; }
.btn-collabpro:hover:not(:disabled) { background: linear-gradient(135deg, #0056b3, #004085); color: white; }
.text-collabpro { color: #007bff; }
.form-control:focus, .form-select:focus { border-color: #007bff; box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25); }
.form-check-input:checked { background-color: #007bff; border-color: #007bff; }
.form-check-input:focus { border-color: #86b7fe; box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25); }
.form-check-label { font-size: 0.9rem; line-height: 1.4; }
.alert { animation: fadeIn 0.3s ease-in; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(-10px); } to { opacity: 1; transform: translateY(0); } }
@media (max-width: 768px) {
  .col-md-6.col-lg-5 { margin-top: 1rem; }
  .auth-card { margin: 0 1rem; }
  .card-body { padding: 2rem !important; }
}
</style>
