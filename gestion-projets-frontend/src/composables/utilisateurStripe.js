import { ref, computed } from 'vue'
import { abonnementAPI } from '@/services/api'

const abonnementActuel = ref(null)
const chargement = ref(false)

const aAbonnementActif = computed(() =>
  !!abonnementActuel.value && abonnementActuel.value.statut === 'ACTIF'
)

/** Charge l'abonnement d'un utilisateur (id optionnel) */
export async function chargerAbonnement(utilisateurId) {
  let id = utilisateurId
  if (!id) {
    try {
      const u = JSON.parse(localStorage.getItem('user') || '{}')
      id = u?.id
    } catch { /* JSON invalide : on ignore */ }
  }
  if (!id) {
    abonnementActuel.value = null
    return
  }

  try {
    chargement.value = true
    const resp = await abonnementAPI.getByUserId(id)
    abonnementActuel.value = resp?.data || null
  } catch {
    // pas d’abonnement (ex: 404) → on remet à null
    abonnementActuel.value = null
  } finally {
    chargement.value = false
  }
}

/** Composable en français */
export function utiliserAbonnement() {
  return { abonnementActuel, aAbonnementActif, chargement, chargerAbonnement }
}
