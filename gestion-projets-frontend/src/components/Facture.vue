<template>
  <div class="facture-container">
    <button
      v-if="!afficherFacture"
      class="btn btn-primary"
      @click="afficherFacture = true; chargerDetails()"
    >
      <i class="fas fa-file-invoice mr-2"></i> {{ $t('facture.voirFacture') }}
    </button>

    <!-- Contenu facture -->
    <div v-if="afficherFacture && !chargement" class="facture-pdf">

      <div class="header-entreprise">
        <img
          :src="details.logoPath || '/logo-collabpro.png'"
          alt="Logo CollabPro"
          class="logo-facture"
        />
        <div class="entreprise-info">
          <h2 class="entreprise-nom">{{ details.entrepriseNom || 'CollabPro Solutions' }}</h2>
          <p class="entreprise-addr">{{ details.entrepriseAdresse || 'Avenue de l\'innovation 123' }}</p>
          <p class="entreprise-addr">{{ details.entrepriseVille || '1000 Bruxelles, Belgique' }}</p>
          <p class="entreprise-addr">TVA: {{ details.entrepriseTva || 'BE0123.456.789' }}</p>
          <p class="entreprise-addr">Email: {{ details.entrepriseEmail || 'contact@collabpro.be' }}</p>
        </div>
      </div>

      <!-- Titre FACTURE + infos client -->
      <div class="titre-client-section">
        <h1 class="titre-facture">{{ $t('facture.facture') || 'FACTURE' }}</h1>
        <div class="client-bloc">
          <p class="client-label">{{ $t('facture.factureA') || 'Facturé à :' }}</p>
          <div class="client-nom">{{ details.clientNom || $t('facture.clientDefaut') || 'Client' }}</div>
          <div v-if="details.clientAdresse" class="client-addr">{{ details.clientAdresse }}</div>
          <div v-if="details.clientVille" class="client-addr">{{ details.clientVille }}</div>
          <div v-if="details.clientEmail" class="client-addr">{{ details.clientEmail }}</div>
        </div>
      </div>

      <!-- Références facture -->
      <div class="refs-grid">
        <div class="ref-item">
          <span class="ref-label">{{ $t('facture.numeroFacture') || 'Numéro' }}</span>
          <span class="ref-value">{{ details.numeroFacture || facture.numeroFacture || 'N/A' }}</span>
        </div>
        <div class="ref-item">
          <span class="ref-label">{{ $t('facture.date') || 'Date' }}</span>
          <span class="ref-value">{{ formatDateSafe(details.dateEmission || facture.dateEmission || facture.createdAt || facture.date_emission) }}</span>
        </div>
        <div class="ref-item">
          <span class="ref-label">{{ $t('facture.echeance') || 'Échéance' }}</span>
          <span class="ref-value">{{ formatDateSafe(details.dateEcheance || facture.dateEcheance || facture.date_echeance) }}</span>
        </div>
      </div>

      <!-- Détail ligne -->
      <table class="table-facture">
        <thead>
        <tr>
          <th class="text-left">{{ $t('facture.description') || 'Description' }}</th>
          <th class="text-right">{{ $t('facture.montantHT') || 'Montant HT' }}</th>
          <th class="text-right">{{ $t('facture.tva') || 'TVA' }}</th>
          <th class="text-right">{{ $t('facture.totalTTC') || 'Total TTC' }}</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td class="text-left">
            {{ details.description || facture.description || 'Abonnement Premium Mensuel - Plateforme CollabPro' }}
            <div v-if="details.periode || facture.periode" class="desc-periode">
              {{ $t('facture.periode') || 'Période' }} : {{ details.periode || facture.periode }}
            </div>
          </td>
          <td class="text-right">{{ formatMontant(details.montantHT ?? facture.montantHT ?? 0) }}</td>
          <td class="text-right">
            {{ formatMontant(details.tva ?? facture.tva ?? 0) }}
            <span v-if="details.tauxTva || facture.tauxTva"> ({{ (details.tauxTva ?? facture.tauxTva) || 21 }}%)</span>
          </td>
          <td class="text-right text-bold">
            {{ formatMontant(details.montantTTC ?? facture.montantTTC ?? 0) }}
          </td>
        </tr>
        </tbody>
      </table>

      <!-- Total -->
      <div class="total-ttl">
        {{ $t('facture.total') || 'Total' }} : {{ formatMontant(details.montantTTC ?? facture.montantTTC ?? 0) }}
      </div>

      <!-- Mentions légales -->
      <div class="mentions-legales">
        <p><strong>{{ $t('facture.informationsPaiement') || 'Informations de paiement :' }}</strong></p>
        <p>IBAN : {{ details.ibanEntreprise || 'BE99 9999 9999 9999' }}</p>
        <p>BIC : {{ details.bicEntreprise || 'GEBABEBB' }}</p>
        <p class="mt-3"><small>{{ $t('facture.mentionLegale') || 'En effectuant le règlement de cette facture, vous confirmez automatiquement votre accord avec les conditions générales de vente.' }}</small></p>
      </div>

      <!-- Date de génération -->
      <div class="date-generation">
        {{ $t('facture.genereeLe') || 'Générée le' }} {{ formatDateSafe(new Date()) }}
      </div>

      <!-- Actions -->
      <div class="actions">
        <button
          class="btn btn-success"
          @click="telechargerPDF"
          :disabled="actionEnCours === 'pdf'"
          :title="$t('facture.telechargerPDFTitle') || 'Télécharger le PDF'"
        >
          <span v-if="actionEnCours === 'pdf'" class="spinner-border spinner-border-sm me-2"></span>
          <i v-else class="fas fa-download mr-2"></i>
          {{ $t('facture.telechargerPDF') || 'Télécharger PDF' }}
        </button>
        <button class="btn btn-outline" @click="fermerFacture">
          <i class="fas fa-times mr-2"></i> {{ $t('facture.fermer') || 'Fermer' }}
        </button>
      </div>
    </div>

    <!-- State: chargement -->
    <div v-if="afficherFacture && chargement" class="loading">
      <div class="spinner-border text-primary"></div>
      <p class="mt-2">{{ $t('facture.chargementDetails') || 'Chargement des détails...' }}</p>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import api from '@/services/api'
import { endpoints } from '@/config/endpoints'
import { factureAPI } from '@/services/api'

export default {
  name: 'Facture',
  props: {
    facture: { type: Object, required: true },
    mode: { type: String, default: '' },
  },
  emits: ['action-terminee', 'facture-fermee'],
  setup (props, { emit }) {
    const { locale } = useI18n()

    const afficherFacture = ref(props.mode === 'direct')
    const chargement = ref(false)
    const actionEnCours = ref(null)

    // ✅ CORRECTION : Initialisation avec valeurs par défaut
    const details = reactive({
      // Entreprise
      entrepriseNom: '',
      entrepriseAdresse: '',
      entrepriseVille: '',
      entrepriseEmail: '',
      entrepriseTva: '',
      logoPath: '',
      ibanEntreprise: '',
      bicEntreprise: '',

      // Client
      clientNom: '',
      clientAdresse: '',
      clientVille: '',
      clientEmail: '',

      // Facture
      factureId: props.facture?.id,
      numeroFacture: props.facture?.numeroFacture,
      dateEmission: props.facture?.dateEmission,
      dateEcheance: props.facture?.dateEcheance,
      periode: props.facture?.periode,
      montantHT: props.facture?.montantHT,
      tva: props.facture?.tva,
      montantTTC: props.facture?.montantTTC,
      tauxTva: props.facture?.tauxTva ?? 21,
      description: props.facture?.description,
    })

    const formatDateSafe = (dateStr) => {
      if (!dateStr) return '—'
      try {
        const date = new Date(dateStr)
        if (isNaN(date.getTime())) return '—'
        return date.toLocaleDateString(
          locale.value === 'fr' ? 'fr-FR' : 'en-US',
          { day: '2-digit', month: '2-digit', year: 'numeric' }
        )
      } catch {
        return '—'
      }
    }

    const formatMontant = (m) => {
      const n = Number(m ?? 0)
      return new Intl.NumberFormat(
        locale.value === 'fr' ? 'fr-FR' : 'en-US',
        { style: 'currency', currency: 'EUR' }
      ).format(n)
    }

    // ✅ CORRECTION : Mapping complet pour tous les formats possibles (snake_case, camelCase, PascalCase)
    const merge = (src) => {
      if (!src || typeof src !== 'object') return

      const mappings = {
        // Dates et montants
        'date_emission': 'dateEmission',
        'date_echeance': 'dateEcheance',
        'montant_ht': 'montantHT',
        'montant_ttc': 'montantTTC',
        'numero_facture': 'numeroFacture',
        'taux_tva': 'tauxTva',

        // Entreprise - snake_case
        'entreprise_nom': 'entrepriseNom',
        'entreprise_adresse': 'entrepriseAdresse',
        'entreprise_ville': 'entrepriseVille',
        'entreprise_email': 'entrepriseEmail',
        'entreprise_tva': 'entrepriseTva',
        'logo_path': 'logoPath',
        'iban_entreprise': 'ibanEntreprise',
        'bic_entreprise': 'bicEntreprise',

        // Client - snake_case
        'client_nom': 'clientNom',
        'client_adresse': 'clientAdresse',
        'client_ville': 'clientVille',
        'client_email': 'clientEmail',

        // Variantes possibles avec majuscules
        'entrepriseTVA': 'entrepriseTva',
        'entrepriseEmail': 'entrepriseEmail',
        'ibanEntreprise': 'ibanEntreprise',
        'bicEntreprise': 'bicEntreprise',
      }

      // ✅ Parcourir toutes les clés reçues du backend
      Object.keys(src).forEach(k => {
        const val = src[k]

        // Ignorer les valeurs null/undefined
        if (val === undefined || val === null) return

        // Utiliser le mapping si disponible, sinon garder la clé telle quelle
        const targetKey = mappings[k] || k

        // Affecter la valeur
        details[targetKey] = val

        // Debug pour voir ce qui est reçu
        console.log(` Mapping: ${k} => ${targetKey} = ${val}`)
      })
    }

    const chargerDetails = async () => {
      try {
        chargement.value = true
        const id = props.facture?.id
        if (!id) {
          console.warn(' Aucun ID de facture fourni')
          return
        }

        console.log('🔄 Chargement des détails de la facture', id)
        const resp = await api.get(`${endpoints.factures}/${id}/pdf-data`)

        console.log('✅ Réponse backend reçue:', resp?.data)

        // Merger les données reçues
        merge(resp?.data || {})

        console.log('✅ Détails après merge:', details)
      } catch (e) {
        console.error('❌ Erreur chargement détails facture:', e)
      } finally {
        chargement.value = false
      }
    }

    const telechargerPDF = async () => {
      try {
        actionEnCours.value = 'pdf'
        const id = props.facture?.id

        // ✅ Déterminer la langue courante
        const raw = locale.value || localStorage.getItem('lang') || 'fr'
        const langue = String(raw).toLowerCase().startsWith('fr') ? 'fr' : 'en'

        console.log('📥 Téléchargement PDF pour facture', id, 'langue:', langue)

        // ✅ Passer la langue à l'API
        const res = await factureAPI.telechargerPDF(id, langue)
        if (!res?.data) throw new Error('Aucun contenu')

        const blob = new Blob([res.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `${details.numeroFacture || 'facture'}.pdf`
        a.click()
        window.URL.revokeObjectURL(url)

        console.log('✅ PDF téléchargé avec succès')
        emit('action-terminee', { type: 'telechargement', success: true })
      } catch (e) {
        console.error('❌ Erreur téléchargement facture:', e)
        emit('action-terminee', { type: 'telechargement', success: false })
      } finally {
        actionEnCours.value = null
      }
    }

    const fermerFacture = () => {
      afficherFacture.value = false
      emit('facture-fermee')
    }

    onMounted(() => {
      if (afficherFacture.value) {
        console.log('🚀 Composant Facture monté, chargement des détails...')
        chargerDetails()
      }
    })

    return {
      afficherFacture,
      chargement,
      actionEnCours,
      details,
      formatDateSafe,
      formatMontant,
      telechargerPDF,
      fermerFacture,
      chargerDetails,
    }
  }
}
</script>

<style scoped>
.facture-container{max-width:960px;margin:auto}
.facture-pdf{background:#fff;padding:32px;border-radius:12px;box-shadow:0 4px 12px rgba(0,0,0,.08)}
.loading{text-align:center;padding:40px}

.header-entreprise{
  display:flex;
  align-items:flex-start;
  gap:24px;
  margin-bottom:32px;
  padding-bottom:20px;
  border-bottom:3px solid #e5e7eb;
}
.logo-facture{
  height:200px;
  width:auto;
  object-fit:contain;
  flex-shrink:0;
}
.entreprise-info{flex:1}
.entreprise-nom{color:#0b5ed7;font-weight:700;font-size:28px;margin:0 0 12px 0}
.entreprise-addr{margin:4px 0;color:#374151;font-size:15px}

.titre-client-section{
  display:flex;
  justify-content:space-between;
  align-items:flex-start;
  margin-bottom:24px;
}
.titre-facture{margin:0;font-size:48px;font-weight:800;letter-spacing:.5px;color:#1f2937}
.client-bloc{
  border-left:3px solid #0b5ed7;
  padding-left:16px;
  max-width:360px;
  text-align:left;
}
.client-label{font-weight:700;font-size:14px;color:#6b7280;margin-bottom:8px}
.client-nom{font-weight:700;font-size:16px;margin-bottom:4px}
.client-addr{color:#374151;font-size:14px;margin:2px 0}

.refs-grid{
  display:grid;
  grid-template-columns:1fr 1fr 1fr;
  gap:12px;
  margin:24px 0;
  background:#f9fafb;
  padding:16px;
  border-radius:8px;
}
.ref-item{display:flex;justify-content:space-between;border-bottom:1px solid #e5e7eb;padding:8px 0}
.ref-label{font-weight:600;color:#6b7280}
.ref-value{font-weight:700;color:#1f2937}

.table-facture{
  width:100%;
  border-collapse:collapse;
  margin-top:20px;
  border:2px solid #e5e7eb;
}
.table-facture th,.table-facture td{padding:14px 16px;border-bottom:1px solid #e5e7eb}
.table-facture thead th{background:#f3f6ff;font-weight:700;color:#1f2937}
.text-left{text-align:left}
.text-right{text-align:right}
.text-bold{font-weight:700}
.desc-periode{color:#6b7280;font-size:.9rem;margin-top:6px;font-style:italic}

.total-ttl{
  text-align:right;
  font-weight:800;
  font-size:1.3rem;
  margin-top:16px;
  padding:12px 16px;
  background:#f3f6ff;
  border-radius:8px;
}

.mentions-legales{
  margin-top:32px;
  padding:16px;
  background:#fefce8;
  border-left:4px solid #fbbf24;
  border-radius:6px;
  font-size:13px;
  color:#78350f;
}
.mentions-legales strong{color:#78350f}
.mentions-legales p{margin:6px 0}

.date-generation{
  text-align:center;
  margin-top:20px;
  font-size:13px;
  color:#6b7280;
  font-style:italic;
}

.actions{
  margin-top:28px;
  display:flex;
  gap:12px;
  justify-content:center;
}
.btn{padding:12px 24px;border-radius:8px;cursor:pointer;font-weight:600;font-size:15px;border:none}
.btn-primary{background:#0d6efd;color:#fff}
.btn-success{background:#28a745;color:#fff}
.btn-outline{border:2px solid #6b7280;background:#fff;color:#6b7280}
.btn:hover:not(:disabled){opacity:.9;transform:translateY(-1px)}
.btn:disabled{opacity:0.6;cursor:not-allowed}

@media (max-width:768px){
  .header-entreprise{flex-direction:column;align-items:center;text-align:center}
  .logo-facture{height:150px}
  .titre-client-section{flex-direction:column;align-items:flex-start;gap:20px}
  .refs-grid{grid-template-columns:1fr;gap:8px}
}
</style>
