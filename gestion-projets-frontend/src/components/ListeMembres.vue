<template>
  <div class="liste-membres">
    <div class="membres-header mb-3">
      <h4 class="text-dark mb-0">
        {{ $t('project_members') }} ({{ membres.length }})
      </h4>
      <small class="text-muted">{{ $t('manage_team_collaboration') }}</small>
    </div>

    <!-- État vide -->
    <div v-if="membres.length === 0" class="aucun-membre">
      <div class="empty-state text-center py-4">
        <i class="fas fa-users fa-3x text-muted mb-3"></i>
        <h5 class="text-muted">{{ $t('no_members_yet') }}</h5>
        <p class="text-muted">{{ $t('invite_first_member') }}</p>
      </div>
    </div>

    <!-- Liste des membres -->
    <div v-else class="membres-container">
      <div
        v-for="membre in membres"
        :key="membre.id"
        class="membre-card"
        :class="{ 'chef-projet': membre.role === 'CHEF_PROJET' }"
      >
        <!-- Avatar et infos -->
        <div class="membre-avatar">
          <div class="avatar-circle" :class="getAvatarClass(membre.role)">
            {{ getInitiales(membre.prenom, membre.nom) }}
          </div>
        </div>

        <div class="membre-info">
          <div class="membre-nom">
            {{ membre.prenom }} {{ membre.nom }}
            <i v-if="membre.role === 'CHEF_PROJET'" class="fas fa-crown text-warning ms-1" :title="$t('role_chef')"></i>
          </div>
          <div class="membre-email">
            <i class="fas fa-envelope me-1"></i>{{ membre.email }}
          </div>
          <div v-if="membre.dateAjout" class="membre-date">
            <i class="fas fa-calendar me-1"></i>
            {{ $t('joined_on') }}: {{ formatDate(membre.dateAjout) }}
          </div>
        </div>

        <!-- Actions et rôle -->
        <div class="membre-actions">
          <span class="membre-role" :class="getRoleClass(membre.role)">
            {{ getRoleLabel(membre.role) }}
          </span>

          <!-- Bouton de suppression (seulement pour Chef et membres non-Chef) -->
          <button
            v-if="estChefProjet && membre.role !== 'CHEF_PROJET'"
            @click="confirmerSuppressionMembre(membre)"
            class="btn-supprimer"
            :title="$t('remove_from_project')"
          >
            <i class="fas fa-times"></i>
          </button>

          <!-- Menu d'actions avancées -->
          <div v-if="estChefProjet" class="dropdown ms-2">
            <button
              class="btn btn-sm btn-outline-secondary dropdown-toggle"
              type="button"
              :id="`actionsDropdown-${membre.id}`"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <i class="fas fa-ellipsis-v"></i>
            </button>
            <ul class="dropdown-menu" :aria-labelledby="`actionsDropdown-${membre.id}`">
              <li>
                <a class="dropdown-item" href="#" @click.prevent="voirProfil(membre)">
                  <i class="fas fa-user me-2"></i>{{ $t('view_profile') }}
                </a>
              </li>
              <li v-if="membre.role !== 'CHEF_PROJET'">
                <a class="dropdown-item" href="#" @click.prevent="changerRole(membre)">
                  <i class="fas fa-user-cog me-2"></i>{{ $t('change_role') }}
                </a>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li v-if="membre.role !== 'CHEF_PROJET'">
                <a class="dropdown-item text-danger" href="#" @click.prevent="confirmerSuppressionMembre(membre)">
                  <i class="fas fa-trash me-2"></i>{{ $t('remove_member') }}
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Statistiques rapides -->
    <div v-if="membres.length > 0" class="membres-stats mt-3 p-3 bg-light rounded">
      <div class="row text-center">
        <div class="col-4">
          <div class="stat-value text-primary">{{ countByRole('CHEF_PROJET') }}</div>
          <div class="stat-label">{{ $t('managers') }}</div>
        </div>
        <div class="col-4">
          <div class="stat-value text-success">{{ countByRole('MEMBRE') }}</div>
          <div class="stat-label">{{ $t('members') }}</div>
        </div>
        <div class="col-4">
          <div class="stat-value text-info">{{ membres.length }}</div>
          <div class="stat-label">{{ $t('total') }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ListeMembres',
  props: {
    membres: {
      type: Array,
      default: () => []
    },
    estChefProjet: {
      type: Boolean,
      default: false
    }
  },
  emits: ['supprimer-membre', 'voir-profil', 'changer-role'],

  methods: {
    getRoleClass(role) {
      return {
        'role-chef': role === 'CHEF_PROJET',
        'role-membre': role === 'MEMBRE',
        'role-admin': role === 'ADMINISTRATEUR'
      }
    },

    getRoleLabel(role) {
      const labels = {
        'CHEF_PROJET': this.$t('role_chef'),
        'MEMBRE': this.$t('role_member'),
        'ADMINISTRATEUR': this.$t('role_admin')
      }
      return labels[role] || role
    },

    getAvatarClass(role) {
      return {
        'avatar-chef': role === 'CHEF_PROJET',
        'avatar-membre': role === 'MEMBRE',
        'avatar-admin': role === 'ADMINISTRATEUR'
      }
    },

    getInitiales(prenom, nom) {
      return `${prenom?.charAt(0) || ''}${nom?.charAt(0) || ''}`.toUpperCase()
    },

    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleDateString(this.$i18n?.locale === 'en' ? 'en-US' : 'fr-FR')
    },

    countByRole(role) {
      return this.membres.filter(m => m.role === role).length
    },

    confirmerSuppressionMembre(membre) {
      const message = this.$t('confirm_remove_member_detailed', {
        name: `${membre.prenom} ${membre.nom}`
      })

      if (confirm(message)) {
        this.$emit('supprimer-membre', membre.id)
      }
    },

    voirProfil(membre) {
      this.$emit('voir-profil', membre)
    },

    changerRole(membre) {
      this.$emit('changer-role', membre)
    }
  }
}
</script>

<style scoped>
.liste-membres {
  width: 100%;
}

.membres-header h4 {
  color: #2c3e50;
  font-weight: 600;
}

.aucun-membre .empty-state {
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.aucun-membre .empty-state:hover {
  border-color: #007bff;
  background-color: #f0f7ff;
}

.membres-container {
  display: grid;
  gap: 15px;
}

.membre-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: white;
  border: 1px solid #e0e6ed;
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.membre-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 4px;
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.membre-card:hover::before {
  transform: scaleY(1);
}

.membre-card:hover {
  border-color: #007bff;
  box-shadow: 0 8px 25px rgba(0, 123, 255, 0.15);
  transform: translateY(-2px);
}

.membre-card.chef-projet {
  background: linear-gradient(135deg, #fff8e1 0%, #ffecb3 100%);
  border-color: #ffc107;
}

.membre-avatar {
  margin-right: 15px;
}

.avatar-circle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.1rem;
  color: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.avatar-chef {
  background: linear-gradient(135deg, #ffc107 0%, #e68900 100%);
}

.avatar-membre {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
}

.avatar-admin {
  background: linear-gradient(135deg, #dc3545 0%, #a71e2a 100%);
}

.membre-info {
  flex: 1;
}

.membre-nom {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
}

.membre-email {
  color: #6c757d;
  font-size: 0.9rem;
  margin-bottom: 3px;
}

.membre-date {
  color: #6c757d;
  font-size: 0.8rem;
}

.membre-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.membre-role {
  padding: 8px 16px;
  border-radius: 25px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border: 2px solid transparent;
}

.role-chef {
  background: linear-gradient(135deg, #ffc107 0%, #e68900 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(255, 193, 7, 0.3);
}

.role-membre {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.3);
}

.role-admin {
  background: linear-gradient(135deg, #dc3545 0%, #a71e2a 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(220, 53, 69, 0.3);
}

.btn-supprimer {
  background: linear-gradient(135deg, #dc3545 0%, #a71e2a 100%);
  color: white;
  border: none;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(220, 53, 69, 0.3);
}

.btn-supprimer:hover {
  background: linear-gradient(135deg, #a71e2a 0%, #7a1019 100%);
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.4);
}

.membres-stats {
  border: 1px solid #e9ecef;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.8rem;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
}

/* Responsive */
@media (max-width: 768px) {
  .membre-card {
    flex-direction: column;
    text-align: center;
  }

  .membre-avatar {
    margin-right: 0;
    margin-bottom: 15px;
  }

  .membre-actions {
    margin-top: 15px;
    justify-content: center;
    flex-wrap: wrap;
  }

  .membres-stats .col-4 {
    margin-bottom: 10px;
  }
}

/* Animations */
.membre-card {
  animation: slideIn 0.6s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
