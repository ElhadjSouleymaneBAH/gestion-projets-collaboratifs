<template>
  <div class="recherche-membres">
    <div class="champ-recherche">
      <input
        v-model="requeteRecherche"
        @input="rechercherUtilisateurs"
        @focus="champActif = true"
        :placeholder="$t('search_users_placeholder')"
        class="form-control"
      >
    </div>

    <div v-if="enChargement" class="text-center py-2">
      <small class="text-muted">{{ $t('searching') }}...</small>
    </div>

    <div
      v-if="utilisateursTrouves.length > 0 && champActif"
      class="resultats-recherche"
    >
      <div
        v-for="utilisateur in utilisateursTrouves"
        :key="utilisateur.id"
        class="utilisateur-resultat"
        @click="selectionnerUtilisateur(utilisateur)"
      >
        <div class="utilisateur-info">
          <div class="utilisateur-nom">
            {{ utilisateur.prenom }} {{ utilisateur.nom }}
          </div>
          <div class="utilisateur-email">
            {{ utilisateur.email }}
          </div>
        </div>
        <span class="badge" :class="getRoleBadgeClass(utilisateur.role)">
          {{ getRoleLabel(utilisateur.role) }}
        </span>
      </div>
    </div>

    <div
      v-if="requeteRecherche.length >= 2 && utilisateursTrouves.length === 0 && !enChargement"
      class="text-center py-2"
    >
      <small class="text-muted">{{ $t('user_not_found') }}</small>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RechercheMembres',
  props: {
    membresExistants: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      requeteRecherche: '',
      utilisateursTrouves: [],
      enChargement: false,
      champActif: false,
      timeoutRecherche: null
    }
  },

  mounted() {
    document.addEventListener('click', this.fermerResultats)
  },

  beforeUnmount() {
    document.removeEventListener('click', this.fermerResultats)
    if (this.timeoutRecherche) {
      clearTimeout(this.timeoutRecherche)
    }
  },

  methods: {
    rechercherUtilisateurs() {
      if (this.timeoutRecherche) {
        clearTimeout(this.timeoutRecherche)
      }

      this.timeoutRecherche = setTimeout(() => {
        if (this.requeteRecherche.length < 2) {
          this.utilisateursTrouves = []
          return
        }

        this.enChargement = true

        // Recherche dans système hybride
        setTimeout(() => {
          const tousLesUtilisateurs = this.obtenirTousLesUtilisateurs()
          const terme = this.requeteRecherche.toLowerCase()

          this.utilisateursTrouves = tousLesUtilisateurs.filter(user => {
            const nomComplet = `${user.prenom} ${user.nom}`.toLowerCase()
            const emailMatch = user.email.toLowerCase().includes(terme)
            const nomMatch = nomComplet.includes(terme)

            // F8 : seulement MEMBRE et CHEF_PROJET
            const roleValide = user.role === 'MEMBRE' || user.role === 'CHEF_PROJET'

            // Exclure les membres déjà dans le projet
            const dejaMembre = this.membresExistants.some(membre => membre.id === user.id)

            return (emailMatch || nomMatch) && roleValide && !dejaMembre
          }).slice(0, 5)

          this.enChargement = false
        }, 300)

      }, 300)
    },

    obtenirTousLesUtilisateurs() {
      // Utilisateurs figés données SQL
      const utilisateursFiges = [
        { id: 1, email: 'emilie.durand0@icc.be', prenom: 'Émilie', nom: 'Durand', role: 'CHEF_PROJET' },
        { id: 7, email: 'sarah.fournier6@icc.be', prenom: 'Sarah', nom: 'Fournier', role: 'MEMBRE' },
        { id: 8, email: 'hugo.giraud7@icc.be', prenom: 'Hugo', nom: 'Giraud', role: 'MEMBRE' }
      ]

      // Nouveaux utilisateurs
      const nouveauxUtilisateurs = JSON.parse(localStorage.getItem('nouveauxUtilisateurs')) || []

      return [...utilisateursFiges, ...nouveauxUtilisateurs]
    },

    selectionnerUtilisateur(utilisateur) {
      this.$emit('utilisateur-selectionne', utilisateur)
      this.requeteRecherche = ''
      this.utilisateursTrouves = []
      this.champActif = false
    },

    fermerResultats(event) {
      if (!this.$el.contains(event.target)) {
        this.champActif = false
      }
    },

    getRoleBadgeClass(role) {
      switch(role) {
        case 'CHEF_PROJET':
          return 'bg-success'
        case 'MEMBRE':
          return 'bg-primary'
        default:
          return 'bg-secondary'
      }
    },

    getRoleLabel(role) {
      const labels = {
        'CHEF_PROJET': this.$t('role_chef'),
        'MEMBRE': this.$t('role_member')
      }
      return labels[role] || role
    }
  }
}
</script>

<style scoped>
.recherche-membres {
  position: relative;
}

.resultats-recherche {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: white;
  border: 1px solid #dee2e6;
  border-top: none;
  border-radius: 0 0 8px 8px;
  max-height: 300px;
  overflow-y: auto;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.utilisateur-resultat {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  cursor: pointer;
  border-bottom: 1px solid #f1f3f5;
  transition: background-color 0.2s ease;
}

.utilisateur-resultat:hover {
  background-color: #e8f4fd;
}

.utilisateur-resultat:last-child {
  border-bottom: none;
}

.utilisateur-info {
  flex: 1;
}

.utilisateur-nom {
  font-weight: 600;
  color: #495057;
  margin-bottom: 2px;
}

.utilisateur-email {
  color: #6c757d;
  font-size: 0.9em;
}
</style>
