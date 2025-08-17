<template>
  <div class="card">
    <div class="card-header">
      <h5><i class="fas fa-user-plus me-2"></i>{{ $t('invite_member') }}</h5>
    </div>
    <div class="card-body">
      <!-- Formulaire d'invitation -->
      <form @submit.prevent="inviterMembre">
        <div class="mb-3">
          <label class="form-label">{{ $t('projects') }}</label>
          <select class="form-select" v-model="invitation.projetId" required>
            <option value="">{{ $t('search') }} {{ $t('projects').toLowerCase() }}</option>
            <option v-for="projet in mesProjets" :key="projet.id" :value="projet.id">
              {{ projet.titre }}
            </option>
          </select>
        </div>

        <div class="mb-3">
          <label class="form-label">{{ $t('member_email') }}</label>
          <input
            type="email"
            class="form-control"
            v-model="invitation.email"
            :placeholder="'sarah.fournier@exemple.com'"
            required
          >
          <div class="form-text">
            {{ $t('user_must_exist') }}
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">{{ $t('message') }} ({{ $t('optional') }})</label>
          <textarea
            class="form-control"
            v-model="invitation.message"
            rows="2"
            :placeholder="$t('join_project_message')"
          ></textarea>
        </div>

        <button type="submit" class="btn btn-primary" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          <i class="fas fa-paper-plane me-2"></i>
          {{ $t('invite') }} {{ $t('projects').toLowerCase() }}
        </button>
      </form>

      <!-- Invitations récentes -->
      <div v-if="invitationsRecentes.length > 0" class="mt-4">
        <h6>{{ $t('recent_invitations') }} :</h6>
        <div class="list-group">
          <div v-for="inv in invitationsRecentes.slice(0, 3)" :key="inv.id" class="list-group-item">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <strong>{{ inv.email }}</strong>
                <br>
                <small class="text-muted">{{ getProjetTitre(inv.projetId) }}</small>
              </div>
              <span class="badge bg-success">{{ $t('added') }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Message de feedback -->
      <div v-if="message" class="alert mt-3" :class="messageType">
        <i :class="messageType.includes('success') ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle'"></i>
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'InvitationMembres',
  data() {
    return {
      loading: false,
      message: '',
      messageType: 'alert-success',

      invitation: {
        projetId: '',
        email: '',
        message: ''
      },

      // Mes projets (du Chef connecté)
      mesProjets: [],

      // Invitations récentes
      invitationsRecentes: JSON.parse(localStorage.getItem('invitations')) || []
    }
  },

  mounted() {
    this.chargerMesProjets()
  },

  methods: {
    chargerMesProjets() {
      const user = JSON.parse(localStorage.getItem('user'))

      // Projets selon le système hybride
      if (user.id === 1) {
        // Émilie Chef (données figées)
        this.mesProjets = [
          { id: 1, titre: 'Application Mobile E-commerce' },
          { id: 2, titre: 'Site Web Corporate' },
          { id: 3, titre: 'Système de Gestion RH' }
        ]
      } else {
        // Nouveaux chefs - projets dynamiques
        const projets = JSON.parse(localStorage.getItem('projets_utilisateur_' + user.id)) || []
        this.mesProjets = projets
      }
    },

    async inviterMembre() {
      this.loading = true
      this.message = ''

      try {
        // Vérifier si l'utilisateur existe (système hybride)
        const utilisateurExiste = this.verifierUtilisateurExiste(this.invitation.email)

        if (!utilisateurExiste) {
          this.message = `${this.$t('user')} ${this.invitation.email} ${this.$t('user_not_exist')}. ${this.$t('must_create_account')}.`
          this.messageType = 'alert-danger'
          return
        }

        // Vérifier si déjà membre du projet
        if (this.verifierDejaMembre(utilisateurExiste.id, this.invitation.projetId)) {
          this.message = this.$t('already_member')
          this.messageType = 'alert-warning'
          return
        }

        // Ajouter le membre au projet
        this.ajouterMembreAuProjet(utilisateurExiste, this.invitation.projetId)

        // Enregistrer l'invitation
        const nouvelleInvitation = {
          id: Date.now(),
          projetId: this.invitation.projetId,
          email: this.invitation.email,
          message: this.invitation.message,
          dateEnvoi: new Date().toISOString(),
          expediteur: JSON.parse(localStorage.getItem('user')).prenom + ' ' + JSON.parse(localStorage.getItem('user')).nom
        }

        this.invitationsRecentes.unshift(nouvelleInvitation)
        localStorage.setItem('invitations', JSON.stringify(this.invitationsRecentes))

        this.message = `${utilisateurExiste.prenom} ${utilisateurExiste.nom} ${this.$t('member_added')}`
        this.messageType = 'alert-success'

        // Reset formulaire
        this.invitation = { projetId: '', email: '', message: '' }

        // Émettre événement pour parent
        this.$emit('membre-ajoute', {
          utilisateur: utilisateurExiste,
          projetId: this.invitation.projetId
        })

      } catch (error) {
        console.error('Erreur invitation:', error)
        this.message = this.$t('invitation_error')
        this.messageType = 'alert-danger'
      } finally {
        this.loading = false
      }
    },

    verifierUtilisateurExiste(email) {
      // Chercher dans les utilisateurs figés
      const utilisateursFiges = [
        { id: 1, email: 'emilie.durand0@icc.be', prenom: 'Émilie', nom: 'Durand', role: 'CHEF_PROJET' },
        { id: 7, email: 'sarah.fournier6@icc.be', prenom: 'Sarah', nom: 'Fournier', role: 'MEMBRE' },
        { id: 8, email: 'hugo.giraud7@icc.be', prenom: 'Hugo', nom: 'Giraud', role: 'MEMBRE' },
        { id: 24, email: 'nathan.garcia23@icc.be', prenom: 'Nathan', nom: 'Garcia', role: 'VISITEUR' },
        { id: 51, email: 'emilie.durand50@icc.be', prenom: 'Émilie', nom: 'Durand', role: 'ADMINISTRATEUR' }
      ]

      let utilisateur = utilisateursFiges.find(u => u.email === email)

      // Si pas trouvé, chercher dans les nouveaux utilisateurs
      if (!utilisateur) {
        const nouveauxUtilisateurs = JSON.parse(localStorage.getItem('nouveauxUtilisateurs')) || []
        utilisateur = nouveauxUtilisateurs.find(u => u.email === email)
      }

      return utilisateur
    },

    verifierDejaMembre(userId, projetId) {
      const membresProjet = JSON.parse(localStorage.getItem('membres_projet_' + projetId)) || []
      return membresProjet.some(m => m.userId === userId)
    },

    ajouterMembreAuProjet(utilisateur, projetId) {
      const membresProjet = JSON.parse(localStorage.getItem('membres_projet_' + projetId)) || []

      membresProjet.push({
        userId: utilisateur.id,
        nom: utilisateur.nom,
        prenom: utilisateur.prenom,
        email: utilisateur.email,
        role: utilisateur.role,
        dateAjout: new Date().toISOString()
      })

      localStorage.setItem('membres_projet_' + projetId, JSON.stringify(membresProjet))
    },

    getProjetTitre(projetId) {
      const projet = this.mesProjets.find(p => p.id == projetId)
      return projet ? projet.titre : this.$t('unknown_project')
    }
  }
}
</script>

<style scoped>
.list-group-item {
  border-left: 4px solid #007bff;
}
</style>
