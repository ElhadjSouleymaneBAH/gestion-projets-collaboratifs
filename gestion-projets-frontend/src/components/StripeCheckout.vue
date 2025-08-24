<template>
  <div class="stripe-checkout">
    <!-- Page de succès -->
    <div v-if="isSuccessPage" class="checkout-result success">
      <div class="result-container">
        <!-- Animation de succès -->
        <div class="success-animation">
          <div class="checkmark-circle">
            <svg class="checkmark" viewBox="0 0 24 24" fill="none">
              <path
                d="M8 12.5L11 15.5L16 9.5"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
        </div>

        <!-- Contenu succès -->
        <div class="result-content">
          <h1 class="result-title">Paiement réussi ! 🎉</h1>
          <p class="result-subtitle">
            Votre abonnement Premium a été activé avec succès.
          </p>

          <!-- Détails de l'abonnement -->
          <div v-if="verificationResult?.subscription" class="subscription-details">
            <div class="detail-card">
              <h3>Détails de votre abonnement</h3>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">Plan :</span>
                  <span class="detail-value">{{ verificationResult.subscription.nom }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Prix :</span>
                  <span class="detail-value">{{ verificationResult.subscription.prix }}€/mois</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Statut :</span>
                  <span class="detail-value status active">{{ verificationResult.subscription.statut }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">Prochaine facturation :</span>
                  <span class="detail-value">{{ formatDate(verificationResult.subscription.date_fin) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="result-actions">
            <router-link to="/projets" class="btn btn-primary">
              Commencer à créer des projets
            </router-link>
            <router-link to="/abonnement" class="btn btn-outline">
              Gérer mon abonnement
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Page d'annulation -->
    <div v-if="isCancelPage" class="checkout-result cancel">
      <div class="result-container">
        <!-- Animation d'annulation -->
        <div class="cancel-animation">
          <div class="cancel-circle">
            <svg class="cancel-icon" viewBox="0 0 24 24" fill="none">
              <path
                d="M18 6L6 18M6 6L18 18"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </div>
        </div>

        <!-- Contenu annulation -->
        <div class="result-content">
          <h1 class="result-title">Paiement annulé</h1>
          <p class="result-subtitle">
            Votre paiement a été annulé. Aucun montant n'a été débité.
          </p>

          <!-- Informations supplémentaires -->
          <div class="info-box">
            <h3>Pourquoi choisir un abonnement Premium ?</h3>
            <ul class="benefits-list">
              <li>Création illimitée de projets</li>
              <li>Gestion complète des équipes</li>
              <li>Collaboration en temps réel</li>
              <li>Facturation automatique</li>
              <li>Support prioritaire</li>
            </ul>
          </div>

          <!-- Actions -->
          <div class="result-actions">
            <router-link to="/abonnement" class="btn btn-primary">
              Réessayer le paiement
            </router-link>
            <router-link to="/projets" class="btn btn-outline">
              Retour aux projets
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- État de vérification -->
    <div v-if="isVerifying" class="verification-loading">
      <div class="loading-container">
        <div class="loading-spinner">
          <div class="spinner"></div>
        </div>
        <h2>Vérification de votre paiement...</h2>
        <p>Veuillez patienter pendant que nous confirmons votre transaction.</p>
      </div>
    </div>

    <!-- Erreur de vérification -->
    <div v-if="verificationError" class="checkout-result error">
      <div class="result-container">
        <div class="error-animation">
          <div class="error-circle">
            <svg class="error-icon" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M15 9l-6 6M9 9l6 6" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
        </div>

        <div class="result-content">
          <h1 class="result-title">Erreur de vérification</h1>
          <p class="result-subtitle">
            Une erreur s'est produite lors de la vérification de votre paiement.
          </p>

          <div class="error-details">
            <p><strong>Erreur :</strong> {{ verificationError }}</p>
            <p>Si le problème persiste, contactez notre support.</p>
          </div>

          <div class="result-actions">
            <button @click="retryVerification" class="btn btn-primary">
              Réessayer la vérification
            </button>
            <router-link to="/abonnement" class="btn btn-outline">
              Retour aux abonnements
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSubscriptionStore } from '@/stores/subscriptionStore.js'

// Vue Router
const route = useRoute()
const router = useRouter()

// Store
const subscriptionStore = useSubscriptionStore()

// State
const isVerifying = ref(false)
const verificationResult = ref(null)
const verificationError = ref(null)

// Computed
const isSuccessPage = computed(() =>
  route.name === 'subscription-success' || route.path.includes('/success')
)

const isCancelPage = computed(() =>
  route.name === 'subscription-cancel' || route.path.includes('/cancel')
)

// Méthodes
const verifyPayment = async () => {
  const sessionId = route.query.session_id

  if (!sessionId) {
    verificationError.value = 'ID de session manquant'
    return
  }

  isVerifying.value = true
  verificationError.value = null

  try {
    const result = await subscriptionStore.verifyPaymentSuccess(sessionId)

    if (result.success) {
      verificationResult.value = result
    } else {
      verificationError.value = result.error || 'Échec de la vérification'
    }
  } catch (error) {
    console.error('Erreur vérification paiement:', error)
    verificationError.value = 'Erreur technique lors de la vérification'
  } finally {
    isVerifying.value = false
  }
}

const retryVerification = async () => {
  verificationError.value = null
  await verifyPayment()
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// Lifecycle
onMounted(async () => {
  if (isSuccessPage.value && route.query.session_id) {
    await verifyPayment()
  }
})
</script>

<style scoped>
.stripe-checkout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.checkout-result {
  width: 100%;
  max-width: 600px;
}

.result-container {
  background: white;
  border-radius: 1rem;
  padding: 3rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  text-align: center;
}

/* Animations de succès */
.success-animation {
  margin-bottom: 2rem;
}

.checkmark-circle {
  width: 5rem;
  height: 5rem;
  background: linear-gradient(135deg, #10b981, #059669);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  animation: checkmarkBounce 0.6s ease-out;
}

.checkmark {
  width: 2rem;
  height: 2rem;
  color: white;
  animation: checkmarkDraw 0.4s ease-out 0.2s both;
}

@keyframes checkmarkBounce {
  0% { transform: scale(0); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

@keyframes checkmarkDraw {
  0% { stroke-dasharray: 0, 100; }
  100% { stroke-dasharray: 100, 0; }
}

/* Animations d'annulation */
.cancel-animation, .error-animation {
  margin-bottom: 2rem;
}

.cancel-circle, .error-circle {
  width: 5rem;
  height: 5rem;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  animation: errorBounce 0.6s ease-out;
}

.cancel-icon, .error-icon {
  width: 2rem;
  height: 2rem;
  color: white;
}

@keyframes errorBounce {
  0% { transform: scale(0); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

/* Contenu */
.result-content h1 {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 1rem;
  color: #1f2937;
}

.result-subtitle {
  font-size: 1.125rem;
  color: #6b7280;
  margin-bottom: 2rem;
}

/* Détails de l'abonnement */
.subscription-details {
  margin: 2rem 0;
}

.detail-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  padding: 1.5rem;
  text-align: left;
}

.detail-card h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 1rem;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-label {
  font-weight: 500;
  color: #6b7280;
}

.detail-value {
  font-weight: 600;
  color: #1f2937;
}

.detail-value.status.active {
  color: #10b981;
}

/* Box d'informations */
.info-box {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 0.5rem;
  padding: 1.5rem;
  margin: 2rem 0;
  text-align: left;
}

.info-box h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e40af;
  margin-bottom: 1rem;
}

.benefits-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.benefits-list li {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0;
  color: #374151;
}

.benefits-list li::before {
  content: '✓';
  color: #10b981;
  font-weight: bold;
}

/* Détails d'erreur */
.error-details {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 0.5rem;
  padding: 1rem;
  margin: 2rem 0;
  text-align: left;
  color: #991b1b;
}

/* Actions */
.result-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border-radius: 0.5rem;
  font-weight: 600;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #1d4ed8, #1e40af);
  transform: translateY(-1px);
}

.btn-outline {
  background: transparent;
  border: 2px solid #e5e7eb;
  color: #6b7280;
}

.btn-outline:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

/* État de chargement */
.verification-loading, .loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
  color: white;
  text-align: center;
}

.loading-spinner {
  display: flex;
  justify-content: center;
}

.spinner {
  width: 3rem;
  height: 3rem;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.verification-loading h2 {
  font-size: 1.5rem;
  margin: 0;
}

.verification-loading p {
  font-size: 1rem;
  opacity: 0.8;
  margin: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .stripe-checkout {
    padding: 1rem;
  }

  .result-container {
    padding: 2rem;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .result-actions {
    gap: 0.75rem;
  }

  .btn {
    width: 100%;
  }
}

@media (min-width: 768px) {
  .result-actions {
    flex-direction: row;
    justify-content: center;
  }
}
</style>
