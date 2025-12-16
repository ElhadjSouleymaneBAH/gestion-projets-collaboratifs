<template>
  <div class="profil-container">
    <div class="profil-header">
      <h1>{{ t('profil.titre') }}</h1>
      <p class="profil-subtitle">{{ t('profil.gererInformations') }}</p>
    </div>

    <div v-if="message.texte" :class="['alert', messageCss]">
      {{ message.texte }}
    </div>

    <div v-if="chargement" class="loading">
      <div class="spinner"></div>
      <p>{{ t('commun.chargement') }}</p>
    </div>

    <div v-else class="profil-content">
      <!-- Formulaire profil -->
      <div class="profil-form-container">
        <h2>{{ t('profil.informationsPersonnelles') }}</h2>

        <form @submit.prevent="mettreAJourProfil" class="profil-form">
          <div class="form-group">
            <label for="prenom">{{ t('profil.prenom') }} *</label>
            <input
              id="prenom"
              v-model="formulaire.prenom"
              type="text"
              :placeholder="t('profil.prenomPlaceholder')"
              required
              :disabled="sauvegardeEnCours"
              autocomplete="given-name"
            />
          </div>

          <div class="form-group">
            <label for="nom">{{ t('profil.nom') }} *</label>
            <input
              id="nom"
              v-model="formulaire.nom"
              type="text"
              :placeholder="t('profil.nomPlaceholder')"
              required
              :disabled="sauvegardeEnCours"
              autocomplete="family-name"
            />
          </div>

          <div class="form-group">
            <label for="email">{{ t('profil.email') }} *</label>
            <input
              id="email"
              v-model="formulaire.email"
              type="email"
              placeholder="exemple@domaine.com"
              required
              :disabled="true"
              autocomplete="email"
            />
            <small class="form-help">{{ t('profil.emailNonModifiable') }}</small>
          </div>

          <div class="form-group">
            <label for="adresse">{{ t('profil.adresse') }}</label>
            <textarea
              id="adresse"
              v-model="formulaire.adresse"
              :placeholder="t('profil.adressePlaceholder')"
              :disabled="sauvegardeEnCours"
              rows="3"
              autocomplete="street-address"
            ></textarea>
          </div>

          <div class="form-group">
            <label for="langue">{{ t('profil.langue') }}</label>
            <select id="langue" v-model="formulaire.langue" :disabled="sauvegardeEnCours">
              <option v-for="langue in languesDisponibles" :key="langue.code" :value="langue.code">
                {{ t(langue.label) }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="role">{{ t('profil.role') }}</label>
            <input
              id="role"
              :value="getRoleLabel(utilisateur.role)"
              type="text"
              readonly
              class="readonly"
            />
          </div>

          <div class="form-group">
            <label for="dateInscription">{{ t('profil.dateInscription') }}</label>
            <input
              id="dateInscription"
              :value="formaterDate(utilisateur.dateInscription)"
              type="text"
              readonly
              class="readonly"
            />
          </div>

          <div class="form-actions">
            <button
              type="button"
              @click="annulerModifications"
              class="btn-secondary"
              :disabled="sauvegardeEnCours"
            >
              {{ t('commun.annuler') }}
            </button>

            <button
              type="submit"
              class="btn-primary"
              :disabled="sauvegardeEnCours || !formulaireModifie"
            >
              <span v-if="sauvegardeEnCours" class="btn-spinner"></span>
              {{ t('profil.sauvegarder') }}
            </button>
          </div>
        </form>
      </div>

      <!-- Mot de passe -->
      <div class="mot-de-passe-section">
        <h2>{{ t('profil.motDePasse') }}</h2>

        <form @submit.prevent="changerMotDePasse" class="mot-de-passe-form">
          <input
            type="text"
            :value="formulaire.email"
            autocomplete="username"
            style="display: none"
            aria-hidden="true"
          />

          <div class="form-group">
            <label for="ancienMotDePasse">{{ t('profil.ancienMotDePasse') }} *</label>
            <input
              id="ancienMotDePasse"
              v-model="motDePasseForm.ancienMotDePasse"
              type="password"
              :placeholder="t('profil.ancienMotDePassePlaceholder')"
              required
              :disabled="changementMotDePasseEnCours"
              autocomplete="current-password"
            />
          </div>

          <div class="form-group">
            <label for="nouveauMotDePasse">{{ t('profil.nouveauMotDePasse') }} *</label>
            <input
              id="nouveauMotDePasse"
              v-model="motDePasseForm.nouveauMotDePasse"
              type="password"
              :placeholder="t('profil.nouveauMotDePassePlaceholder')"
              required
              :minlength="motDePasseMinLength"
              :disabled="changementMotDePasseEnCours"
              autocomplete="new-password"
            />
            <small class="form-help">
              {{ t('profil.motDePasseMinLength', { length: motDePasseMinLength }) }}
            </small>
          </div>

          <div class="form-group">
            <label for="confirmationMotDePasse">{{ t('profil.confirmationMotDePasse') }} *</label>
            <input
              id="confirmationMotDePasse"
              v-model="motDePasseForm.confirmationMotDePasse"
              type="password"
              :placeholder="t('profil.confirmationMotDePassePlaceholder')"
              required
              :minlength="motDePasseMinLength"
              :disabled="changementMotDePasseEnCours"
              autocomplete="new-password"
            />
          </div>

          <div
            v-if="
              motDePasseForm.nouveauMotDePasse &&
              motDePasseForm.confirmationMotDePasse &&
              motDePasseForm.nouveauMotDePasse !== motDePasseForm.confirmationMotDePasse
            "
            class="error-message"
          >
            {{ t('profil.motDePasseNonIdentique') }}
          </div>

          <div class="form-actions">
            <button
              type="submit"
              class="btn-primary"
              :disabled="changementMotDePasseEnCours || !motDePasseFormValide"
            >
              <span v-if="changementMotDePasseEnCours" class="btn-spinner"></span>
              {{ t('profil.changerMotDePasse') }}
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
import { userAPI } from '@/services/api'

export default {
  name: 'ProfilView',
  setup() {
    const { t, locale } = useI18n()
    const defaultLocale = import.meta.env.VITE_DEFAULT_LOCALE || 'fr'
    const motDePasseMinLength = parseInt(import.meta.env.VITE_PASSWORD_MIN_LENGTH || '8')

    const languesDisponibles = [
      { code: 'fr', label: 'langues.francais' },
      { code: 'en', label: 'langues.anglais' },
    ]

    const chargement = ref(true)

    const sauvegardeEnCours = ref(false)
    const changementMotDePasseEnCours = ref(false)

    const utilisateur = ref({})

    const message = reactive({ texte: '', type: '' })
    const messageCss = computed(() =>
      message.type === 'success'
        ? 'alert-success'
        : message.type === 'error'
          ? 'alert-danger'
          : 'alert-info',
    )

    const formulaire = reactive({
      nom: '',
      prenom: '',
      email: '',
      langue: defaultLocale,
      adresse: '',
    })
    const formulaireOriginal = reactive({})

    const motDePasseForm = reactive({
      ancienMotDePasse: '',
      nouveauMotDePasse: '',
      confirmationMotDePasse: '',
    })

    const formulaireModifie = computed(() =>
      Object.keys(formulaire).some((k) => formulaire[k] !== formulaireOriginal[k]),
    )

    const motDePasseFormValide = computed(
      () =>
        motDePasseForm.ancienMotDePasse &&
        motDePasseForm.nouveauMotDePasse &&
        motDePasseForm.confirmationMotDePasse &&
        motDePasseForm.nouveauMotDePasse === motDePasseForm.confirmationMotDePasse &&
        motDePasseForm.nouveauMotDePasse.length >= motDePasseMinLength,
    )

    const afficherMessage = (texte, type = 'success') => {
      message.texte = texte
      message.type = type
      setTimeout(() => {
        message.texte = ''
        message.type = ''
      }, 5000)
    }

    const normalizeId = (v) => (v == null ? v : String(v).split(':')[0])

    const chargerProfil = async () => {
      chargement.value = true

      const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || '{}')
      if (!utilisateurConnecte?.id) {
        afficherMessage(t('profil.erreurChargement'), 'error')
        chargement.value = false
        return
      }

      const userId = normalizeId(utilisateurConnecte.id)
      const reponse = await userAPI.getById(userId)

      if (reponse?.data) {
        utilisateur.value = reponse.data

        Object.assign(formulaire, {
          nom: utilisateur.value.nom || '',
          prenom: utilisateur.value.prenom || '',
          email: utilisateur.value.email || '',
          langue: utilisateur.value.langue || locale.value || defaultLocale,
          adresse: utilisateur.value.adresse || '',
        })
        Object.assign(formulaireOriginal, { ...formulaire })
      } else {
        console.error('Erreur chargement profil')
        afficherMessage(t('profil.erreurChargement'), 'error')
      }

      chargement.value = false
    }

    const mettreAJourProfil = async () => {
      sauvegardeEnCours.value = true

      const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || '{}')
      const userId = normalizeId(utilisateurConnecte.id)

      const payload = {}
      Object.keys(formulaire).forEach((k) => {
        if (formulaire[k] !== formulaireOriginal[k]) payload[k] = formulaire[k]
      })

      const reponse = await userAPI.updateProfile(userId, payload)

      if (reponse?.data) {
        const donnees = reponse.data
        Object.assign(utilisateur.value, donnees)
        Object.assign(formulaireOriginal, { ...formulaire })

        const stored = JSON.parse(localStorage.getItem('user') || '{}')
        localStorage.setItem('user', JSON.stringify({ ...stored, ...donnees }))

        afficherMessage(t('profil.profilMisAJour'), 'success')
      } else {
        afficherMessage(t('profil.erreurMiseAJour'), 'error')
      }

      sauvegardeEnCours.value = false
    }

    const changerMotDePasse = async () => {
      changementMotDePasseEnCours.value = true

      const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || '{}')
      const userId = normalizeId(utilisateurConnecte.id)

      await userAPI.updateProfile(userId, { ...motDePasseForm })

      Object.assign(motDePasseForm, {
        ancienMotDePasse: '',
        nouveauMotDePasse: '',
        confirmationMotDePasse: '',
      })
      afficherMessage(t('profil.motDePasseChange'), 'success')

      changementMotDePasseEnCours.value = false
    }

    const annulerModifications = () => Object.assign(formulaire, { ...formulaireOriginal })

    const formaterDate = (str) => {
      if (!str) return ''
      const langueActuelle = formulaire.langue || defaultLocale
      const localeMap = { fr: 'fr-FR', en: 'en-US' }
      return new Date(str).toLocaleDateString(
        localeMap[langueActuelle] || localeMap[defaultLocale],
        {
          year: 'numeric',
          month: 'long',
          day: 'numeric',
        },
      )
    }

    const getRoleLabel = (role) => {
      if (!role) return '—'
      const labels = {
        CHEF_PROJET: t('roles.chefProjet') || 'Chef de Projet',
        MEMBRE: t('roles.membre') || 'Membre',
        ADMINISTRATEUR: t('roles.administrateur') || 'Administrateur',
      }
      return labels[role] || role
    }

    onMounted(() => {
      chargerProfil()
    })

    watch(
      () => formulaire.langue,
      (lang) => {
        if (!lang) return
        locale.value = lang
        const stored = JSON.parse(localStorage.getItem('user') || '{}')
        if (stored?.id) {
          stored.langue = lang
          localStorage.setItem('user', JSON.stringify(stored))
        }
      },
    )

    return {
      chargement,
      sauvegardeEnCours,
      changementMotDePasseEnCours,
      utilisateur,
      message,
      messageCss,
      formulaire,
      motDePasseForm,
      languesDisponibles,
      motDePasseMinLength,
      formulaireModifie,
      motDePasseFormValide,
      mettreAJourProfil,
      changerMotDePasse,
      annulerModifications,
      formaterDate,
      getRoleLabel,
      t,
    }
  },
}
</script>

<style scoped>
.profil-container {
  max-width: 1000px;
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

.alert {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-weight: 500;
}
.alert-success {
  background-color: #f0fff4;
  color: #38a169;
  border: 1px solid #9ae6b4;
}
.alert-danger {
  background-color: #fed7d7;
  color: #e53e3e;
  border: 1px solid #feb2b2;
}

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
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.profil-form-container,
.mot-de-passe-section {
  background: #fff;
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
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.2s;
  background: #fff;
  font-family: inherit;
}
.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}
.form-group input:disabled,
.form-group select:disabled,
.form-group textarea:disabled {
  background: #f7fafc;
  cursor: not-allowed;
  opacity: 0.6;
}
.form-group input.readonly {
  background: #f7fafc;
  color: #718096;
  cursor: default;
}
.form-group textarea {
  resize: vertical;
  min-height: 80px;
}
.form-help {
  display: block;
  margin-top: 6px;
  font-size: 0.85rem;
  color: #718096;
}

.error-message {
  background: #fed7d7;
  color: #e53e3e;
  padding: 10px 12px;
  border-radius: 6px;
  font-size: 0.9rem;
  margin-top: 12px;
  border: 1px solid #feb2b2;
}

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
  transition: all 0.2s;
  border: 2px solid transparent;
  display: flex;
  align-items: center;
  gap: 8px;
}
.btn-primary {
  background: #4299e1;
  color: #fff;
  border-color: #4299e1;
}
.btn-primary:hover:not(:disabled) {
  background: #3182ce;
  border-color: #3182ce;
  transform: translateY(-1px);
}
.btn-secondary {
  background: #fff;
  color: #4a5568;
  border-color: #e2e8f0;
}
.btn-secondary:hover:not(:disabled) {
  background: #f7fafc;
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

@media (max-width: 768px) {
  .profil-container {
    padding: 16px;
  }
  .profil-form-container,
  .mot-de-passe-section {
    padding: 20px;
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
