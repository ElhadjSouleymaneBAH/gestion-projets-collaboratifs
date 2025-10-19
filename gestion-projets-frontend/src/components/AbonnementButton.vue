<template>
  <div class="abonnement-button-container">
    <!-- Pour les membres normaux -->
    <div v-if="user.role === 'MEMBRE'" class="upgrade-prompt">
      <div class="upgrade-card">
        <div class="upgrade-header">
          <h3>{{ $t('abonnement.devenirChef') }}</h3>
          <div class="premium-badge">Premium</div>
        </div>
        <p class="upgrade-description">
          {{ $t('abonnement.avantagesChef') }}
        </p>
        <ul class="features-list">
          <li>{{ $t('abonnement.features.creationProjets') }}</li>
          <li>{{ $t('abonnement.features.gestionEquipes') }}</li>
          <li>{{ $t('abonnement.features.collaboration') }}</li>
          <li>{{ $t('abonnement.features.factures') }}</li>
        </ul>
        <router-link to="/abonnement" class="btn btn-premium">
          {{ $t('abonnement.commencer') }}
          <span class="prix">
            {{ $t('abonnement.prixMensuel', { prix: formatPriceSansCentimes(10) }) }}
          </span>
        </router-link>
      </div>
    </div>

    <!-- Pour les chefs de projet sans abonnement -->
    <div v-else-if="user.role === 'CHEF_PROJET' && !aAbonnementActif" class="activation-prompt">
      <div class="activation-card">
        <div class="warning-icon">⚠️</div>
        <h3>{{ $t('abonnement.activationRequise') }}</h3>
        <p>{{ $t('abonnement.fonctionnalitesLimitees') }}</p>
        <router-link to="/abonnement" class="btn btn-warning">
          {{ $t('abonnement.activerMaintenant') }}
        </router-link>
      </div>
    </div>

    <!-- Pour les chefs de projet avec abonnement actif -->
    <div v-else-if="user.role === 'CHEF_PROJET' && aAbonnementActif" class="premium-status">
      <div class="premium-card">
        <div class="premium-icon">✨</div>
        <div class="premium-info">
          <h4>{{ $t('abonnement.premiumActif') }}</h4>
          <p>{{ $t('abonnement.validiteJusqu') }} {{ formatDate(abonnementActuel.dateFin) }}</p>
        </div>
        <router-link to="/abonnement" class="btn btn-outline-primary btn-sm">
          {{ $t('abonnement.gerer') }}
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted } from 'vue'
import { utiliserAbonnement } from '@/composables/utilisateurStripe'

export default {
  name: 'AbonnementButton',
  setup() {
    const { abonnementActuel, aAbonnementActif, chargerAbonnement } = utiliserAbonnement()

    const user = computed(() => {
      return JSON.parse(localStorage.getItem('user') || '{}')
    })

    onMounted(() => {
      if (user.value.role === 'CHEF_PROJET') {
        chargerAbonnement()
      }
    })

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    // Affichage compact sans centimes (ex: "10€")
    const formatPriceSansCentimes = (v) => `${Math.round(Number(v))}€`

    return {
      user,
      abonnementActuel,
      aAbonnementActif,
      formatDate,
      formatPriceSansCentimes
    }
  }
}
</script>

<style scoped>
.abonnement-button-container {
  margin: 1rem 0;
}

/* Upgrade pour membres */
.upgrade-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1.5rem;
  border-radius: 12px;
  text-align: center;
  position: relative;
  overflow: hidden;
}
.upgrade-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="2" fill="rgba(255,255,255,0.1)"/></svg>') repeat;
  animation: float 20s infinite linear;
  pointer-events: none;
}
@keyframes float {
  0% { transform: translateX(-50%) translateY(-50%) rotate(0deg); }
  100% { transform: translateX(-50%) translateY(-50%) rotate(360deg); }
}

.upgrade-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
.upgrade-header h3 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: bold;
}

.premium-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
}

.upgrade-description {
  margin-bottom: 1.5rem;
  opacity: 0.95;
}

.features-list {
  list-style: none;
  padding: 0;
  margin-bottom: 1.5rem;
  text-align: left;
}
.features-list li {
  position: relative;
  padding: 0.5rem 0 0.5rem 1.5rem; /* shorthand */
}
.features-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #4ade80;
  font-weight: bold;
}

.btn-premium {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}
.btn-premium:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
}

.prix {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.875rem;
}

/* Activation pour chefs de projet */
.activation-card {
  background: #fff3cd;
  border: 2px solid #ffeaa7;
  padding: 1.5rem;
  border-radius: 12px;
  text-align: center;
}
.warning-icon {
  font-size: 2rem;
  margin-bottom: 1rem;
}
.activation-card h3 {
  color: #856404;
  margin-bottom: 1rem;
}
.activation-card p {
  color: #856404;
  margin-bottom: 1.5rem;
}
.btn-warning {
  background: #ffc107;
  color: #212529;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.2s ease;
}
.btn-warning:hover {
  background: #e0a800;
}

/* Statut premium actif */
.premium-card {
  background: #d4edda;
  border: 2px solid #c3e6cb;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 1rem;
}
.premium-icon {
  font-size: 1.5rem;
}
.premium-info {
  flex: 1;
}
.premium-info h4 {
  margin: 0 0 0.25rem 0;
  color: #155724;
  font-size: 1rem;
}
.premium-info p {
  margin: 0;
  color: #155724;
  font-size: 0.875rem;
  opacity: 0.8;
}
.btn-outline-primary {
  background: transparent;
  color: #007bff;
  border: 2px solid #007bff;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
}
.btn-outline-primary:hover {
  background: #007bff;
  color: white;
}
.btn-sm {
  font-size: 0.875rem;
}

/* Responsive */
@media (max-width: 768px) {
  .premium-card {
    flex-direction: column;
    text-align: center;
  }
  .upgrade-header {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>
