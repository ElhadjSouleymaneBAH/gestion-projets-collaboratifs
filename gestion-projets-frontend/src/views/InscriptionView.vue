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
                <h3 class="fw-bold text-collabpro">Créer un compte</h3>
                <p class="text-muted">Rejoignez CollabPro et commencez à collaborer</p>
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
                    <label for="prenom" class="form-label">Prénom</label>
                    <input
                      type="text"
                      class="form-control"
                      id="prenom"
                      v-model="form.prenom"
                      :class="{ 'is-invalid': errors.prenom }"
                      required
                      placeholder="Votre prénom"
                    >
                    <div v-if="errors.prenom" class="invalid-feedback">
                      {{ errors.prenom }}
                    </div>
                  </div>
                  <div class="col-md-6 mb-3">
                    <label for="nom" class="form-label">Nom</label>
                    <input
                      type="text"
                      class="form-control"
                      id="nom"
                      v-model="form.nom"
                      :class="{ 'is-invalid': errors.nom }"
                      required
                      placeholder="Votre nom"
                    >
                    <div v-if="errors.nom" class="invalid-feedback">
                      {{ errors.nom }}
                    </div>
                  </div>
                </div>

                <div class="mb-3">
                  <label for="email" class="form-label">Email</label>
                  <input
                    type="email"
                    class="form-control"
                    id="email"
                    v-model="form.email"
                    :class="{ 'is-invalid': errors.email }"
                    required
                    placeholder="votre@email.com"
                  >
                  <div v-if="errors.email" class="invalid-feedback">
                    {{ errors.email }}
                  </div>
                </div>

                <div class="mb-3">
                  <label for="role" class="form-label">Type de compte</label>
                  <select
                    class="form-select"
                    id="role"
                    v-model="form.role"
                    :class="{ 'is-invalid': errors.role }"
                    required
                  >
                    <option value="">Choisissez votre rôle</option>
                    <option value="CHEF_PROJET">Chef de Projet (Abonnement Premium)</option>
                    <option value="MEMBRE">Membre (Gratuit)</option>
                    <option value="VISITEUR">Visiteur (Consultation)</option>
                  </select>
                  <div v-if="errors.role" class="invalid-feedback">
                    {{ errors.role }}
                  </div>
                  <div v-if="form.role === 'CHEF_PROJET'" class="form-text text-warning">
                    <i class="fas fa-crown me-1"></i>
                    Abonnement Premium requis (10€/mois)
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
                      placeholder="Créez un mot de passe"
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
                    Au moins 6 caractères
                  </div>
                </div>

                <div class="mb-3">
                  <label for="confirmPassword" class="form-label">Confirmer le mot de passe</label>
                  <input
                    type="password"
                    class="form-control"
                    id="confirmPassword"
                    v-model="form.confirmMotDePasse"
                    :class="{ 'is-invalid': errors.confirmMotDePasse }"
                    required
                    placeholder="Confirmez votre mot de passe"
                  >
                  <div v-if="errors.confirmMotDePasse" class="invalid-feedback">
                    {{ errors.confirmMotDePasse }}
                  </div>
                </div>

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
                    J'accepte les
                    <router-link to="/conditions" class="text-collabpro">
                      conditions d'utilisation
                    </router-link>
                  </label>
                  <div v-if="errors.acceptTerms" class="invalid-feedback d-block">
                    {{ errors.acceptTerms }}
                  </div>
                </div>

                <button
                  type="submit"
                  class="btn btn-collabpro w-100 mb-3"
                  :disabled="loading"
                >
                  <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                  {{ loading ? 'Création...' : 'Créer mon compte' }}
                </button>
              </form>

              <!-- Lien vers connexion -->
              <hr class="my-4">
              <div class="text-center">
                <p class="text-muted mb-0">
                  Déjà inscrit ?
                  <router-link
                    to="/connexion"
                    class="text-collabpro text-decoration-none fw-bold"
                  >
                    Se connecter
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

const router = useRouter()

// Variables réactives
const form = reactive({
  prenom: '',
  nom: '',
  email: '',
  role: '',
  motDePasse: '',
  confirmMotDePasse: '',
  acceptTerms: false
})

const errors = reactive({
  prenom: null,
  nom: null,
  email: null,
  role: null,
  motDePasse: null,
  confirmMotDePasse: null,
  acceptTerms: null
})

const error = ref(null)
const success = ref(null)
const loading = ref(false)
const showPassword = ref(false)

// Base d'utilisateurs HYBRIDE (pour éviter doublons)
const utilisateursFiges = [
  { email: 'emilie.durand0@icc.be' },
  { email: 'sarah.fournier6@icc.be' },
  { email: 'emilie.durand50@icc.be' },
  { email: 'nathan.garcia23@icc.be' },
  { email: 'hugo.giraud7@icc.be' }
]

const nouveauxUtilisateurs = JSON.parse(localStorage.getItem('nouveauxUtilisateurs')) || []
const tousLesEmails = [
  ...utilisateursFiges.map(u => u.email),
  ...nouveauxUtilisateurs.map(u => u.email)
]

// Validation du formulaire
const validateForm = () => {
  // Reset erreurs
  Object.keys(errors).forEach(key => errors[key] = null)
  let isValid = true

  if (!form.prenom.trim()) {
    errors.prenom = 'Le prénom est requis'
    isValid = false
  }

  if (!form.nom.trim()) {
    errors.nom = 'Le nom est requis'
    isValid = false
  }

  if (!form.email.trim()) {
    errors.email = 'L\'email est requis'
    isValid = false
  } else if (!/\S+@\S+\.\S+/.test(form.email)) {
    errors.email = 'Format d\'email invalide'
    isValid = false
  } else if (tousLesEmails.includes(form.email)) {
    errors.email = 'Cet email est déjà utilisé'
    isValid = false
  }

  if (!form.role) {
    errors.role = 'Veuillez choisir un type de compte'
    isValid = false
  }

  if (!form.motDePasse) {
    errors.motDePasse = 'Le mot de passe est requis'
    isValid = false
  } else if (form.motDePasse.length < 6) {
    errors.motDePasse = 'Le mot de passe doit contenir au moins 6 caractères'
    isValid = false
  }

  if (!form.confirmMotDePasse) {
    errors.confirmMotDePasse = 'Veuillez confirmer le mot de passe'
    isValid = false
  } else if (form.motDePasse !== form.confirmMotDePasse) {
    errors.confirmMotDePasse = 'Les mots de passe ne correspondent pas'
    isValid = false
  }

  if (!form.acceptTerms) {
    errors.acceptTerms = 'Vous devez accepter les conditions d\'utilisation'
    isValid = false
  }

  return isValid
}

// Inscription
const handleInscription = async () => {
  if (!validateForm()) return

  loading.value = true
  error.value = null
  success.value = null

  try {
    // Générer ID unique (éviter conflits avec IDs figés)
    const idsExistants = [1, 7, 8, 24, 51, ...nouveauxUtilisateurs.map(u => u.id || 0)]
    const newId = Math.max(...idsExistants, 100) + 1

    // Créer nouvel utilisateur
    const nouvelUtilisateur = {
      id: newId,
      nom: form.nom.trim(),
      prenom: form.prenom.trim(),
      email: form.email.toLowerCase().trim(),
      role: form.role,
      motDePasse: form.motDePasse,
      dateCreation: new Date().toISOString(),
      // Pas d'abonnement pour les nouveaux chefs → doivent payer
      abonnement: form.role === 'CHEF_PROJET' ? { statut: 'INACTIF' } : null
    }

    // Ajouter aux nouveaux utilisateurs (séparé des figés)
    nouveauxUtilisateurs.push(nouvelUtilisateur)
    localStorage.setItem('nouveauxUtilisateurs', JSON.stringify(nouveauxUtilisateurs))

    // Connexion automatique
    localStorage.setItem('user', JSON.stringify(nouvelUtilisateur))
    localStorage.setItem('token', 'new-user-token-' + newId)

    success.value = 'Compte créé avec succès ! Redirection...'

    // REDIRECTION INTELLIGENTE
    setTimeout(() => {
      if (form.role === 'CHEF_PROJET') {
        // Nouveau Chef → Doit s'abonner d'abord
        router.push('/abonnement-premium')
      } else {
        // Autres rôles → Accès direct
        switch (form.role) {
          case 'MEMBRE':
            router.push('/tableau-bord-membre')
            break
          case 'VISITEUR':
            router.push('/projets-publics')
            break
          default:
            router.push('/')
        }
      }
    }, 1500)

  } catch (err) {
    console.error('Erreur inscription:', err)
    error.value = 'Erreur lors de la création du compte. Veuillez réessayer.'
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

.form-control:focus,
.form-select:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}
</style>
