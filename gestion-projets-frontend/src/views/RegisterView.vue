<!-- RegisterView.vue - Adapté de votre version existante -->
<template>
  <div class="min-vh-100 d-flex align-items-center justify-content-center bg-light px-3 py-5">
    <div class="w-100" style="max-width: 500px;">
      <!-- Card principale -->
      <div class="card shadow-lg border-0 rounded-4 bg-white">
        <div class="card-body p-5">

          <!-- Logo et titre -->
          <div class="d-flex align-items-center gap-3 mb-4">
            <img src="/logo-collabpro.png" alt="CollabPro" style="height: 40px; width: 40px;">
            <h1 class="h4 fw-bold text-collabpro mb-0">CollabPro</h1>
          </div>

          <h2 class="h4 fw-semibold text-dark mb-2">Créer votre compte</h2>
          <p class="text-muted small mb-4">Rejoignez la plateforme de gestion collaborative</p>

          <!-- Formulaire -->
          <form @submit.prevent="handleRegister">

            <!-- Prénom -->
            <div class="mb-3">
              <label class="form-label text-dark fw-medium">Prénom</label>
              <input
                v-model="form.prenom"
                type="text"
                required
                class="form-control form-control-lg"
                placeholder="Votre prénom"
              >
            </div>

            <!-- Nom -->
            <div class="mb-3">
              <label class="form-label text-dark fw-medium">Nom</label>
              <input
                v-model="form.nom"
                type="text"
                required
                class="form-control form-control-lg"
                placeholder="Votre nom"
              >
            </div>

            <!-- Email -->
            <div class="mb-3">
              <label class="form-label text-dark fw-medium">Email</label>
              <input
                v-model="form.email"
                type="email"
                required
                class="form-control form-control-lg"
                placeholder="votre@email.com"
              >
            </div>

            <!-- Mot de passe -->
            <div class="mb-3">
              <label class="form-label text-dark fw-medium">Mot de passe</label>
              <input
                v-model="form.motDePasse"
                type="password"
                required
                minlength="6"
                class="form-control form-control-lg"
                placeholder="••••••••"
              >
            </div>

            <!-- Langue préférée -->
            <div class="mb-3">
              <label class="form-label text-dark fw-medium">Langue préférée</label>
              <select
                v-model="form.langue"
                required
                class="form-select form-select-lg"
              >
                <option value="fr">Français</option>
                <option value="en">English</option>
              </select>
            </div>

            <!-- CGU -->
            <div class="mb-4">
              <div class="form-check">
                <input
                  v-model="form.cguAccepte"
                  type="checkbox"
                  required
                  class="form-check-input"
                  id="cguCheck"
                >
                <label class="form-check-label small" for="cguCheck">
                  J'accepte les
                  <router-link to="/conditions" class="text-decoration-none text-collabpro">
                    conditions générales
                  </router-link>
                  et la
                  <router-link to="/conditions#confidentialite" class="text-decoration-none text-collabpro">
                    politique de confidentialité
                  </router-link>.
                </label>
              </div>
            </div>

            <!-- Messages d'erreur -->
            <div v-if="errorMessage" class="alert alert-danger d-flex align-items-center mb-3">
              <i class="fas fa-exclamation-triangle me-2"></i>
              {{ errorMessage }}
            </div>

            <!-- Bouton inscription -->
            <button
              type="submit"
              :disabled="loading"
              class="btn btn-collabpro btn-lg w-100 mb-3"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
              <span v-if="loading">Création du compte...</span>
              <span v-else>Créer mon compte</span>
            </button>
          </form>

          <!-- Lien connexion -->
          <div class="text-center">
            <p class="text-muted small mb-0">
              Vous avez déjà un compte ?
              <button @click="goToLogin" class="btn btn-link p-0 text-collabpro fw-medium text-decoration-none">
                Se connecter
              </button>
            </p>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const errorMessage = ref('')

    const form = reactive({
      nom: '',
      prenom: '',
      email: '',
      motDePasse: '',
      langue: 'fr',
      cguAccepte: false
    })

    const handleRegister = async () => {
      loading.value = true
      errorMessage.value = ''

      try {
        const response = await fetch('/api/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            nom: form.nom,
            prenom: form.prenom,
            email: form.email,
            motDePasse: form.motDePasse,
            langue: form.langue,
            cguAccepte: form.cguAccepte,
            role: 'MEMBRE'
          })
        })

        if (response.ok) {
          alert('Compte créé avec succès ! Vous pouvez maintenant vous connecter.')
          router.push('/login')
        } else {
          const errorData = await response.json()
          throw new Error(errorData.message || 'Erreur lors de la création')
        }

      } catch (error) {
        errorMessage.value = 'Erreur lors de la création du compte. Vérifiez vos informations.'
      } finally {
        loading.value = false
      }
    }

    const goToLogin = () => {
      router.push('/login')
    }

    return {
      form,
      loading,
      errorMessage,
      handleRegister,
      goToLogin
    }
  }
}
</script>

<style scoped>
.btn-collabpro {
  background: linear-gradient(135deg, #007bff, #0056b3);
  border: none;
  color: white;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-collabpro:hover:not(:disabled) {
  background: linear-gradient(135deg, #0056b3, #004085);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
  color: white;
}

.btn-collabpro:disabled {
  opacity: 0.7;
  transform: none;
}

.text-collabpro {
  color: #007bff;
}

.rounded-4 {
  border-radius: 1rem;
}

.form-control:focus,
.form-select:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

@media (max-width: 768px) {
  .card-body {
    padding: 2rem !important;
  }
}
</style>
