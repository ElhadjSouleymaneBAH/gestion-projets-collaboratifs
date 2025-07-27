<template>
  <div class="liste-membres">
    <h4>Membres du projet ({{ membres.length }})</h4>

    <div v-if="membres.length === 0" class="aucun-membre">
      Aucun membre dans ce projet
    </div>

    <div v-else class="membres-container">
      <div
        v-for="membre in membres"
        :key="membre.id"
        class="membre-card"
      >
        <div class="membre-info">
          <div class="membre-nom">
            {{ membre.prenom }} {{ membre.nom }}
          </div>
          <div class="membre-email">
            {{ membre.email }}
          </div>
        </div>

        <div class="membre-actions">
          <span class="membre-role" :class="getRoleClass(membre.role)">
            {{ getRoleLabel(membre.role) }}
          </span>

          <button
            v-if="estChefProjet && membre.role !== 'CHEF_PROJET'"
            @click="confirmerSuppressionMembre(membre)"
            class="btn-supprimer"
            title="Retirer du projet"
          >
            ✕
          </button>
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
  methods: {
    getRoleClass(role) {
      return {
        'role-chef': role === 'CHEF_PROJET',
        'role-membre': role === 'MEMBRE'
      }
    },

    getRoleLabel(role) {
      const labels = {
        'CHEF_PROJET': 'Chef de Projet',
        'MEMBRE': 'Membre'
      }
      return labels[role] || role
    },

    confirmerSuppressionMembre(membre) {
      if (confirm(`Retirer ${membre.prenom} ${membre.nom} du projet ?`)) {
        this.$emit('supprimer-membre', membre.id)
      }
    }
  }
}
</script>

<style scoped>
.liste-membres {
  margin-bottom: 30px;
}

.liste-membres h4 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 1.1em;
}

.aucun-membre {
  text-align: center;
  color: #6c757d;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.membres-container {
  display: grid;
  gap: 10px;
}

.membre-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: white;
  border: 1px solid #e0e6ed;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.membre-card:hover {
  border-color: #3498db;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.1);
}

.membre-info {
  flex: 1;
}

.membre-nom {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.membre-email {
  color: #6c757d;
  font-size: 0.9em;
}

.membre-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.membre-role {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8em;
  font-weight: 600;
  text-transform: uppercase;
}

.role-chef {
  background-color: #e74c3c;
  color: white;
}

.role-membre {
  background-color: #3498db;
  color: white;
}

.btn-supprimer {
  background-color: #e74c3c;
  color: white;
  border: none;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
}

.btn-supprimer:hover {
  background-color: #c0392b;
}
</style>
