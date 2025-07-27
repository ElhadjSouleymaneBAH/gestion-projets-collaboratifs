<template>
  <div class="profil-container">
    <!-- En-tête profil -->
    <div class="profil-header">
      <h1>{{ $t('profil.titre') }}</h1>
      <p class="profil-subtitle">{{ $t('profil.gererInformations') }}</p>
    </div>

    <!-- Messages d'alerte -->
    <div v-if="message.texte" :class="['alert', message.type]">
      {{ message.texte }}
    </div>

    <!-- Loading -->
    <div v-if="chargement" class="loading">
      <div class="spinner"></div>
      <p>{{ $t('commun.chargement') }}</p>
    </div>

    <!-- Contenu principal -->
    <div v-else class="profil-content">

      <!-- Statistiques utilisateur -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-number">{{ statistiques.projets }}</div>
          <div class="stat-label">{{ $t('profil.projets') }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ statistiques.taches }}</div>
          <div class="stat-label">{{ $t('profil.taches') }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ statistiques.notificationsNonLues }}</div>
          <div class="stat-label">{{ $t('profil.notifications') }}</div>
        </div>
      </div>

      <!-- Formulaire profil -->
      <div class="profil-form-container">
        <h2>{{ $t('profil.informationsPersonnelles') }}</h2>

        <form @submit.prevent="mettreAJourProfil" class="profil-form">

          <!-- Prénom -->
          <div class="form-group">
            <label for="prenom">{{ $t('profil.prenom') }} *</label>
            <input
              id="prenom"
              v-model="formulaire.prenom"
              type="text"
              :placeholder="$t('profil.prenomPlaceholder')"
              required
              :disabled="sauvegardeEnCours"
            />
          </div>

          <!-- Nom -->
          <div class="form-group">
            <label for="nom">{{ $t('profil.nom') }} *</label>
            <input
              id="nom"
              v-model="formulaire.nom"
              type="text"
              :placeholder="$t('profil.nomPlaceholder')"
              required
              :disabled="sauvegardeEnCours"
            />
          </div>

          <!-- Email -->
          <div class="form-group">
            <label for="email">{{ $t('profil.email') }} *</label>
            <input
              id="email"
              v-model="formulaire.email"
              type="email"
              :placeholder="$t('profil.emailPlaceholder')"
              required
              :disabled="sauvegardeEnCours"
            />
          </div>

          <!-- Langue -->
          <div class="form-group">
            <label for="langue">{{ $t('profil.langue') }}</label>
            <select
              id="langue"
              v-model="formulaire.langue"
              :disabled="sauvegardeEnCours"
            >
              <option value="fr">{{ $t('profil.francais') }}</option>
              <option value="en">{{ $t('profil.anglais') }}</option>
            </select>
          </div>

          <!-- Rôle (lecture seule) -->
          <div class="form-group">
            <label for="role">{{ $t('profil.role') }}</label>
            <input
              id="role"
              :value="$t(`roles.${utilisateur.role}`)"
              type="text"
              readonly
              class="readonly"
            />
          </div>

          <!-- Date d'inscription (lecture seule) -->
          <div class="form-group">
            <label for="dateInscription">{{ $t('profil.dateInscription') }}</label>
            <input
              id="dateInscription"
              :value="formaterDate(utilisateur.dateInscription)"
              type="text"
              readonly
              class="readonly"
            />
          </div>

          <!-- Boutons -->
          <div class="form-actions">
            <button
              type="button"
              @click="annulerModifications"
              class="btn-secondary"
              :disabled="sauvegardeEnCours"
            >
              {{ $t('commun.annuler') }}
            </button>

            <button
              type="submit"
              class="btn-primary"
              :disabled="sauvegardeEnCours || !formulaireModifie"
            >
              <span v-if="sauvegardeEnCours" class="btn-spinner"></span>
              {{ $t('profil.sauvegarder') }}
            </button>
          </div>
        </form>
      </div>

      <!-- Section changement de mot de passe -->
      <div class="mot-de-passe-section">
        <h2>{{ $t('profil.motDePasse') }}</h2>

        <form @submit.prevent="changerMotDePasse" class="mot-de-passe-form">

          <!-- Ancien mot de passe -->
          <div class="form-group">
            <label for="ancienMotDePasse">{{ $t('profil.ancienMotDePasse') }} *</label>
            <input
              id="ancienMotDePasse"
              v-model="motDePasseForm.ancienMotDePasse"
              type="password"
              :placeholder="$t('profil.ancienMotDePassePlaceholder')"
              required
              :disabled="changementMotDePasseEnCours"
            />
          </div>

          <!-- Nouveau mot de passe -->
          <div class="form-group">
            <label for="nouveauMotDePasse">{{ $t('profil.nouveauMotDePasse') }} *</label>
            <input
              id="nouveauMotDePasse"
              v-model="motDePasseForm.nouveauMotDePasse"
              type="password"
              :placeholder="$t('profil.nouveauMotDePassePlaceholder')"
              required
              minlength="8"
              :disabled="changementMotDePasseEnCours"
            />
          </div>

          <!-- Confirmation mot de passe -->
          <div class="form-group">
            <label for="confirmationMotDePasse">{{ $t('profil.confirmationMotDePasse') }} *</label>
            <input
              id="confirmationMotDePasse"
              v-model="motDePasseForm.confirmationMotDePasse"
              type="password"
              :placeholder="$t('profil.confirmationMotDePassePlaceholder')"
              required
              minlength="8"
              :disabled="changementMotDePasseEnCours"
            />
          </div>

          <!-- Bouton changement -->
          <div class="form-actions">
            <button
              type="submit"
              class="btn-primary"
              :disabled="changementMotDePasseEnCours || !motDePasseFormValide"
            >
              <span v-if="changementMotDePasseEnCours" class="btn-spinner"></span>
              {{ $t('profil.changerMotDePasse') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import profilService from '@/services/profil.service.js'

export default {
  name: 'ProfilView',
  setup() {
    const { t } = useI18n()

    // États réactifs
    const chargement = ref(true)
    const sauvegardeEnCours = ref(false)
    const changementMotDePasseEnCours = ref(false)

    const utilisateur = ref({})
    const statistiques = ref({
      projets: 0,
      taches: 0,
      notificationsNonLues: 0
    })

    const message = reactive({
      texte: '',
      type: ''
    })

    // Formulaire principal
    const formulaire = reactive({
      nom: '',
      prenom: '',
      email: '',
      langue: 'fr'
    })

    const formulaireOriginal = reactive({})

    // Formulaire mot de passe
    const motDePasseForm = reactive({
      ancienMotDePasse: '',
      nouveauMotDePasse: '',
      confirmationMotDePasse: ''
    })

    // Computed
    const formulaireModifie = computed(() => {
      return Object.keys(formulaire).some(key =>
        formulaire[key] !== formulaireOriginal[key]
      )
    })

    const motDePasseFormValide = computed(() => {
      return motDePasseForm.ancienMotDePasse &&
        motDePasseForm.nouveauMotDePasse &&
        motDePasseForm.confirmationMotDePasse &&
        motDePasseForm.nouveauMotDePasse === motDePasseForm.confirmationMotDePasse &&
        motDePasseForm.nouveauMotDePasse.length >= 8
    })

    // Méthodes
    const afficherMessage = (texte, type = 'success') => {
      message.texte = texte
      message.type = type
      setTimeout(() => {
        message.texte = ''
        message.type = ''
      }, 5000)
    }

    const chargerProfil = async () => {
      try {
        chargement.value = true
        const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || '{}')

        if (!utilisateurConnecte.id) {
          throw new Error('Utilisateur non connecté')
        }

        // Charger les données du profil
        const reponse = await profilService.obtenirProfil(utilisateurConnecte.id)
        utilisateur.value = reponse.data

        // Remplir le formulaire
        Object.assign(formulaire, {
          nom: utilisateur.value.nom || '',
          prenom: utilisateur.value.prenom || '',
          email: utilisateur.value.email || '',
          langue: utilisateur.value.langue || 'fr'
        })

        // Sauvegarder l'état original
        Object.assign(formulaireOriginal, { ...formulaire })

        // Charger les statistiques
        const statsReponse = await profilService.obtenirStatistiquesProfil(utilisateurConnecte.id)
        if (statsReponse.success) {
          statistiques.value = statsReponse.data
        }

      } catch (error) {
        console.error('Erreur chargement profil:', error)
        afficherMessage(t('profil.erreurChargement'), 'error')
      } finally {
        chargement.value = false
      }
    }

    const mettreAJourProfil = async () => {
      try {
        sauvegardeEnCours.value = true

        const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || '{}')
        const donneesModifiees = {}

        // Ne passer que les champs modifiés
        Object.keys(formulaire).forEach(key => {
          if (formulaire[key] !== formulaireOriginal[key]) {
            donneesModifiees[key] = formulaire[key]
          }
        })

        const reponse = await profilService.mettreAJourProfil(
          utilisateurConnecte.id,
          donneesModifiees
        )

        if (reponse.success) {
          // Mettre à jour les données locales
          Object.assign(utilisateur.value, reponse.data)
          Object.assign(formulaireOriginal, { ...formulaire })

          afficherMessage(t('profil.profilMisAJour'), 'success')
        }

      } catch (error) {
        console.error('Erreur mise à jour:', error)
        afficherMessage(error.message || t('profil.erreurMiseAJour'), 'error')
      } finally {
        sauvegardeEnCours.value = false
      }
    }

    const changerMotDePasse = async () => {
      try {
        changementMotDePasseEnCours.value = true

        const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || '{}')

        const reponse = await profilService.changerMotDePasse(
          utilisateurConnecte.id,
          { ...motDePasseForm }
        )

        if (reponse.success) {
          // Réinitialiser le formulaire
          Object.assign(motDePasseForm, {
            ancienMotDePasse: '',
            nouveauMotDePasse: '',
            confirmationMotDePasse: ''
          })

          afficherMessage(t('profil.motDePasseChange'), 'success')
        }

      } catch (error) {
        console.error('Erreur changement mot de passe:', error)
        afficherMessage(error.message || t('profil.erreurMotDePasse'), 'error')
      } finally {
        changementMotDePasseEnCours.value = false
      }
    }

    const annulerModifications = () => {
      Object.assign(formulaire, { ...formulaireOriginal })
    }

    const formaterDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    // Cycle de vie
    onMounted(async () => {
      await chargerProfil()
    })

    // Watchers
    watch(() => formulaire.langue, (nouvelleLangue) => {
      // Changer la langue de l'interface si nécessaire
      // Cette partie dépend de votre implémentation i18n
    })

    return {
      // États
      chargement,
      sauvegardeEnCours,
      changementMotDePasseEnCours,
      utilisateur,
      statistiques,
      message,
      formulaire,
      motDePasseForm,

      // Computed
      formulaireModifie,
      motDePasseFormValide,

      // Méthodes
      mettreAJourProfil,
      changerMotDePasse,
      annulerModifications,
      formaterDate
    }
  }
}
</script>

<style scoped>
.profil-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profil-header {
  text-align: center;
  margin-bottom: 30px;
}

.profil-header h1 {
  font-size: 2.5rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 8px;
}

.profil-subtitle {
  color: #718096;
  font-size: 1.1rem;
}

/* Alertes */
.alert {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-weight: 500;
}

.alert.success {
  background-color: #f0fff4;
  color: #38a169;
  border: 1px solid #9ae6b4;
}

.alert.error {
  background-color: #fed7d7;
  color: #e53e3e;
  border: 1px solid #feb2b2;
}

/* Loading */
.loading {
  text-align: center;
  padding: 40px;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e2e8f0;
  border-top: 3px solid #4299e1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Statistiques */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  text-align: center;
  border: 1px solid #e2e8f0;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  color: #4299e1;
  margin-bottom: 8px;
}

.stat-label {
  color: #718096;
  font-weight: 500;
  text-transform: uppercase;
  font-size: 0.875rem;
  letter-spacing: 0.05em;
}

/* Formulaires */
.profil-form-container,
.mot-de-passe-section {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  margin-bottom: 30px;
}

.profil-form-container h2,
.mot-de-passe-section h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 24px;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 12px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 6px;
  font-size: 0.95rem;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.2s ease;
  background-color: white;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.form-group input:disabled,
.form-group select:disabled {
  background-color: #f7fafc;
  cursor: not-allowed;
  opacity: 0.6;
}

.form-group input.readonly {
  background-color: #f7fafc;
  color: #718096;
  cursor: default;
}

/* Boutons */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.btn-primary,
.btn-secondary {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid transparent;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background-color: #4299e1;
  color: white;
  border-color: #4299e1;
}

.btn-primary:hover:not(:disabled) {
  background-color: #3182ce;
  border-color: #3182ce;
  transform: translateY(-1px);
}

.btn-secondary {
  background-color: white;
  color: #4a5568;
  border-color: #e2e8f0;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #f7fafc;
  border-color: #cbd5e0;
}

.btn-primary:disabled,
.btn-secondary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

/* Responsive */
@media (max-width: 768px) {
  .profil-container {
    padding: 16px;
  }

  .profil-form-container,
  .mot-de-passe-section {
    padding: 20px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
    justify-content: center;
  }
}
</style>
