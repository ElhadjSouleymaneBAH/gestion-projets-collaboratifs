<template>
  <div class="gestion-membres">
    <div class="header-section mb-4">
      <h3 class="text-collabpro">
        <i class="fas fa-users me-2"></i>{{ $t('team_management') }}
      </h3>
      <p class="text-muted">{{ $t('manage_project_members') }}</p>
    </div>

    <!-- Messages d'alerte -->
    <div v-if="message.text" :class="['alert', 'alert-dismissible', 'fade', 'show', message.type]" role="alert">
      <i :class="message.icon + ' me-2'"></i>
      {{ message.text }}
      <button type="button" class="btn-close" @click="clearMessage" aria-label="Close"></button>
    </div>

    <!-- Liste des membres -->
    <div class="card mb-4">
      <div class="card-header bg-light">
        <h5 class="mb-0">
          <i class="fas fa-list me-2"></i>{{ $t('project_members') }}
        </h5>
      </div>
      <div class="card-body">
        <ListeMembres
          :membres="membresProjet"
          :est-chef-projet="estChefProjet"
          @supprimer-membre="supprimerMembre"
        />
      </div>
    </div>

    <!-- Invitation de membres (F8) - Seulement pour Chef de Projet -->
    <div v-if="estChefProjet" class="card">
      <div class="card-header bg-primary text-white">
        <h5 class="mb-0">
          <i class="fas fa-user-plus me-2"></i>{{ $t('invite_new_members') }}
        </h5>
      </div>
      <div class="card-body">
        <InvitationMembres
          :projet-id="projetId"
          @membre-invite="onMembreInvite"
        />
      </div>
    </div>

    <!-- Message pour utilisateurs non-Chef -->
    <div v-else class="alert alert-info">
      <i class="fas fa-info-circle me-2"></i>
      {{ $t('only_project_manager_can_invite') }}
    </div>

    <!-- Statistiques membres -->
    <div class="row mt-4">
      <div class="col-md-6">
        <div class="stat-card text-center p-3 bg-light rounded">
          <div class="stat-number text-primary">{{ membresProjet.length }}</div>
          <div class="stat-label text-muted">{{ $t('total_members') }}</div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="stat-card text-center p-3 bg-light rounded">
          <div class="stat-number text-success">{{ membresActifs }}</div>
          <div class="stat-label text-muted">{{ $t('active_members') }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ListeMembres from './ListeMembres.vue'
import InvitationMembres from './InvitationMembres.vue'
import MembreService from '@/services/membre.service.js'

export default {
  name: 'GestionMembres',
  components: {
    ListeMembres,
    InvitationMembres
  },
  props: {
    projetId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      membresProjet: [],
      estChefProjet: false,
      loading: false,
      message: {
        text: '',
        type: '',
        icon: ''
      }
    }
  },

  computed: {
    membresActifs() {
      return this.membresProjet.filter(membre =>
        membre.role === 'MEMBRE' || membre.role === 'CHEF_PROJET'
      ).length
    }
  },

  async mounted() {
    await this.chargerMembres()
    this.verifierRoleUtilisateur()
  },

  methods: {
    async chargerMembres() {
      try {
        this.loading = true
        this.membresProjet = await MembreService.getMembresProjets(this.projetId)
      } catch (error) {
        console.error('Erreur chargement membres:', error)
        this.showMessage(
          this.$t('error_loading_members'),
          'alert-danger',
          'fas fa-exclamation-triangle'
        )
      } finally {
        this.loading = false
      }
    },

    async supprimerMembre(membreId) {
      if (!confirm(this.$t('confirm_remove_member'))) {
        return
      }

      try {
        await MembreService.supprimerMembreProjet(this.projetId, membreId)
        this.membresProjet = this.membresProjet.filter(m => m.id !== membreId)

        this.showMessage(
          this.$t('member_removed_successfully'),
          'alert-success',
          'fas fa-check-circle'
        )

        this.$emit('membre-supprime', membreId)
      } catch (error) {
        console.error('Erreur suppression membre:', error)
        this.showMessage(
          this.$t('error_removing_member'),
          'alert-danger',
          'fas fa-exclamation-triangle'
        )
      }
    },

    onMembreInvite(membre) {
      this.membresProjet.push(membre)
      this.showMessage(
        this.$t('member_added_successfully'),
        'alert-success',
        'fas fa-check-circle'
      )
      this.$emit('membre-ajoute', membre)
    },

    verifierRoleUtilisateur() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      this.estChefProjet = user.role === 'CHEF_PROJET'
    },

    showMessage(text, type, icon) {
      this.message = { text, type, icon }
      // Auto-clear après 5 secondes
      setTimeout(() => {
        this.clearMessage()
      }, 5000)
    },

    clearMessage() {
      this.message = { text: '', type: '', icon: '' }
    }
  }
}
</script>

<style scoped>
.gestion-membres {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.header-section {
  text-align: center;
  margin-bottom: 30px;
}

.header-section h3 {
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 10px;
}

.text-collabpro {
  color: #007bff;
}

.card {
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  margin-bottom: 20px;
}

.card-header {
  border-radius: 12px 12px 0 0 !important;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.card-header h5 {
  font-weight: 600;
}

.stat-card {
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.alert {
  border: none;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.alert-success {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
  border-left: 4px solid #28a745;
}

.alert-danger {
  background: linear-gradient(135deg, #f8d7da 0%, #f1b0b7 100%);
  color: #721c24;
  border-left: 4px solid #dc3545;
}

.alert-info {
  background: linear-gradient(135deg, #d1ecf1 0%, #bee5eb 100%);
  color: #0c5460;
  border-left: 4px solid #17a2b8;
}

/* Animations */
.card {
  animation: slideInUp 0.6s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive */
@media (max-width: 768px) {
  .gestion-membres {
    padding: 15px;
  }

  .header-section h3 {
    font-size: 1.5rem;
  }

  .row .col-md-6 {
    margin-bottom: 15px;
  }
}
</style>
