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
                    :class="{ 'is-invalid': errors.email }"
                    required
                    autocomplete="email"
                    placeholder="votre@email.com"
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
                      v-model="form.motDePasse"
                      :class="{ 'is-invalid': errors.motDePasse }"
                      required
                      autocomplete="current-password"
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
                  <div v-if="errors.motDePasse" class="invalid-feedback d-block">
                    {{ errors.motDePasse }}
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

              <!-- Comptes de test pour développement - MASQUÉS en production -->
              <div v-if="false" class="mt-4 p-3 bg-light rounded">
                <h6 class="text-muted mb-3">🧪 Comptes de test :</h6>
                <div class="row g-2">
                  <div class="col-6">
                    <button class="btn btn-outline-success btn-sm w-100" @click="connexionRapide('chef')">
                      👨‍💼 Chef de Projet
                    </button>
                    <small class="text-muted d-block">Émilie Durand</small>
                  </div>
                  <div class="col-6">
                    <button class="btn btn-outline-info btn-sm w-100" @click="connexionRapide('membre')">
                      👤 Membre
                    </button>
                    <small class="text-muted d-block">Sarah Fournier</small>
                  </div>
                </div>
                <button class="btn btn-outline-danger btn-sm w-100 mt-2" @click="connexionRapide('admin')">
                  🔧 Administrateur
                </button>
              </div>

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
// import { authAPI } from '@/services/api' // À décommenter quand API prête

const router = useRouter()

// Variables réactives
const form = reactive({
  email: '',
  motDePasse: '',
  remember: false
})

const errors = reactive({
  email: null,
  motDePasse: null
})

const error = ref(null)
const success = ref(null)
const loading = ref(false)
const showPassword = ref(false)
// showTestAccounts supprimé - plus besoin

// Base d'utilisateurs HYBRIDE (figés + dynamiques)
const utilisateursFiges = {
  'emilie.durand0@icc.be': {
    id: 1,
    nom: 'Durand',
    prenom: 'Émilie',
    email: 'emilie.durand0@icc.be',
    role: 'CHEF_PROJET',
    motDePasse: '1986',
    abonnement: { type: 'PREMIUM', statut: 'ACTIF' } // Déjà Premium
  },
  'sarah.fournier6@icc.be': {
    id: 7,
    nom: 'Fournier',
    prenom: 'Sarah',
    email: 'sarah.fournier6@icc.be',
    role: 'MEMBRE',
    motDePasse: '1986'
  },
  'emilie.durand50@icc.be': {
    id: 51,
    nom: 'Durand',
    prenom: 'Émilie',
    email: 'emilie.durand50@icc.be',
    role: 'ADMINISTRATEUR',
    motDePasse: '1986'
  },
  'nathan.garcia23@icc.be': {
    id: 24,
    nom: 'Garcia',
    prenom: 'Nathan',
    email: 'nathan.garcia23@icc.be',
    role: 'VISITEUR',
    motDePasse: '1986'
  }
}

// Récupérer les nouveaux utilisateurs du localStorage
const nouveauxUtilisateurs = JSON.parse(localStorage.getItem('nouveauxUtilisateurs')) || []

// FUSION des deux systèmes
const tousLesUtilisateurs = { ...utilisateursFiges }
nouveauxUtilisateurs.forEach(user => {
  tousLesUtilisateurs[user.email] = user
})

// Fonction de validation
const validateForm = () => {
  errors.email = null
  errors.motDePasse = null

  let isValid = true

  if (!form.email) {
    errors.email = 'L\'email est requis'
    isValid = false
  } else if (!/\S+@\S+\.\S+/.test(form.email)) {
    errors.email = 'Format d\'email invalide'
    isValid = false
  }

  if (!form.motDePasse) {
    errors.motDePasse = 'Le mot de passe est requis'
    isValid = false
  }

  return isValid
}

// Fonction principale de connexion
const handleConnexion = async () => {
  if (!validateForm()) return

  loading.value = true
  error.value = null
  success.value = null

  try {
    // Mode hybride - chercher dans les deux systèmes
    const utilisateur = tousLesUtilisateurs[form.email]

    if (!utilisateur) {
      error.value = 'Email non trouvé. Créez d\'abord un compte via "S\'inscrire".'
      return
    }

    if (utilisateur.motDePasse !== form.motDePasse) {
      error.value = 'Mot de passe incorrect.'
      return
    }

    // Connexion réussie
    success.value = 'Connexion réussie ! Redirection...'

    // Sauvegarder les données utilisateur
    localStorage.setItem('user', JSON.stringify(utilisateur))
    localStorage.setItem('token', 'token-' + utilisateur.id)

    // LOGIQUE DE REDIRECTION INTELLIGENTE
    setTimeout(() => {
      // Vérifier si Chef sans abonnement Premium
      if (utilisateur.role === 'CHEF_PROJET') {
        const aAbonnementActif = utilisateur.abonnement?.statut === 'ACTIF'

        if (!aAbonnementActif) {
          // Nouveau Chef → Doit payer d'abord
          router.push('/abonnement-premium')
          return
        }
      }

      // Redirection normale selon le rôle
      switch (utilisateur.role) {
        case 'ADMINISTRATEUR':
          window.location.href = '/admin/tableau-de-bord'
          break
        case 'CHEF_PROJET':
          window.location.href = '/tableau-bord-chef-projet'
          break
        case 'MEMBRE':
          window.location.href = '/tableau-bord-membre'
          break
        case 'VISITEUR':
          window.location.href = '/projets-publics'
          break
        default:
          window.location.href = '/'
      }
    }, 1500)

  } catch (err) {
    console.error('Erreur de connexion:', err)
    error.value = 'Erreur de connexion. Veuillez réessayer.'
  } finally {
    loading.value = false
  }
}

// Connexion rapide pour les tests
const connexionRapide = (typeRole) => {
  switch(typeRole) {
    case 'chef':
      form.email = 'emilie.durand0@icc.be'
      break
    case 'membre':
      form.email = 'sarah.fournier6@icc.be'
      break
    case 'admin':
      form.email = 'emilie.durand50@icc.be'
      break
    case 'visiteur':
      form.email = 'nathan.garcia23@icc.be'
      break
  }
  form.motDePasse = '1986'
  handleConnexion()
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
