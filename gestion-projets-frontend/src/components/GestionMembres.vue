<template>
  <div class="gestion-membres">
    <h3>Gestion des membres du projet</h3>

    <!-- Liste des membres -->
    <ListeMembres
      :membres="membresProjet"
      :est-chef-projet="estChefProjet"
      @supprimer-membre="supprimerMembre"
    />

    <!-- Invitation de membres (F8) -->
    <InvitationMembres
      v-if="estChefProjet"
      :projet-id="projetId"
      @membre-invite="onMembreInvite"
    />
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
      estChefProjet: false
    }
  },
  async mounted() {
    await this.chargerMembres()
    this.verifierRoleUtilisateur()
  },
  methods: {
    async chargerMembres() {
      try {
        this.membresProjet = await MembreService.getMembresProjets(this.projetId)
      } catch (error) {
        console.error('Erreur chargement membres:', error)
      }
    },

    async supprimerMembre(membreId) {
      try {
        await MembreService.supprimerMembreProjet(this.projetId, membreId)
        this.membresProjet = this.membresProjet.filter(m => m.id !== membreId)
      } catch (error) {
        console.error('Erreur suppression membre:', error)
      }
    },

    onMembreInvite(membre) {
      this.membresProjet.push(membre)
      this.$emit('membre-ajoute', membre)
    },

    verifierRoleUtilisateur() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      this.estChefProjet = user.role === 'CHEF_PROJET'
    }
  }
}
</script>
