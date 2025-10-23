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
          <h2 class="entreprise-nom">{{ details.entrepriseNom || $t('facture.entrepriseNomDefaut') }}</h2>
          <p class="entreprise-addr">{{ details.entrepriseAdresse }}</p>
          <p class="entreprise-addr">{{ details.entrepriseVille }}</p>
          <p class="entreprise-addr">TVA: {{ details.entrepriseTva }}</p>
          <p class="entreprise-addr">Email: {{ details.entrepriseEmail }}</p>
        </div>
      </div>

      <!-- Titre FACTURE + infos client -->
      <div class="titre-client-section">
        <h1 class="titre-facture">{{ $t('facture.facture') }}</h1>
        <div class="client-bloc">
          <p class="client-label">{{ $t('facture.factureA') }}</p>
          <div class="client-nom">{{ details.clientNom || $t('facture.clientDefaut') }}</div>
          <div v-if="details.clientAdresse" class="client-addr">{{ details.clientAdresse }}</div>
          <div v-if="details.clientEmail" class="client-addr">{{ details.clientEmail }}</div>
        </div>
      </div>

      <!-- Références facture -->
      <div class="refs-grid">
        <div class="ref-item">
          <span class="ref-label">{{ $t('facture.numeroFacture') }}</span>
          <span class="ref-value">{{ details.numeroFacture || facture.numeroFacture }}</span>
        </div>
        <div class="ref-item">
          <span class="ref-label">{{ $t('facture.date') }}</span>
          <span class="ref-value">{{ formatDateSafe(details.dateEmission || facture.dateEmission || facture.createdAt || facture.date_emission) }}</span>
        </div>
        <div class="ref-item">
          <span class="ref-label">{{ $t('facture.echeance') }}</span>
          <span class="ref-value">{{ formatDateSafe(details.dateEcheance || facture.dateEcheance || facture.date_echeance) }}</span>
        </div>
      </div>

      <!-- Détail ligne -->
      <table class="table-facture">
        <thead>
        <tr>
          <th class="text-left">{{ $t('facture.description') }}</th>
          <th class="text-right">{{ $t('facture.montantHT') }}</th>
          <th class="text-right">{{ $t('facture.tva') }}</th>
          <th class="text-right">{{ $t('facture.totalTTC') }}</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td class="text-left">
            {{ details.description || facture.description || $t('facture.descriptionDefaut') }}
            <div v-if="details.periode || facture.periode" class="desc-periode">
              {{ $t('facture.periode') }} {{ details.periode || facture.periode }}
            </div>
          </td>
          <td class="text-right">{{ formatMontant(details.montantHT ?? facture.montantHT) }}</td>
          <td class="text-right">
            {{ formatMontant(details.tva ?? facture.tva) }}
            <span v-if="details.tauxTva || facture.tauxTva"> ({{ (details.tauxTva ?? facture.tauxTva) || 21 }}%)</span>
          </td>
          <td class="text-right text-bold">
            {{ formatMontant(details.montantTTC ?? facture.montantTTC) }}
          </td>
        </tr>
        </tbody>
      </table>

      <!-- Total -->
      <div class="total-ttl">
        {{ $t('facture.total') }} {{ formatMontant(details.montantTTC ?? facture.montantTTC) }}
      </div>

      <!-- Mentions légales -->
      <div class="mentions-legales">
        <p><strong>{{ $t('facture.informationsPaiement') }}</strong></p>
        <p>{{ $t('facture.iban') }}</p>
        <p>{{ $t('facture.bic') }}</p>
        <p class="mt-3"><small>{{ $t('facture.mentionLegale') }}</small></p>
      </div>

      <!-- Date de génération -->
      <div class="date-generation">
        {{ $t('facture.genereeLe') }} {{ formatDateSafe(new Date()) }}
      </div>

      <!-- Actions -->
      <div class="actions">
        <button
          class="btn btn-success"
          @click="telechargerPDF"
          :disabled="actionEnCours === 'pdf'"
          :title="$t('facture.telechargerPDFTitle')"
        >
          <span v-if="actionEnCours === 'pdf'" class="spinner-border spinner-border-sm me-2"></span>
          <i v-else class="fas fa-download mr-2"></i>
          {{ $t('facture.telechargerPDF') }}
        </button>
        <button class="btn btn-outline" @click="fermerFacture">
          <i class="fas fa-times mr-2"></i> {{ $t('facture.fermer') }}
        </button>
      </div>
    </div>

    <!-- State: chargement -->
    <div v-if="afficherFacture && chargement" class="loading">
      <div class="spinner-border text-primary"></div>
      <p class="mt-2">{{ $t('facture.chargementDetails') }}</p>
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

    // ✅ CORRECTION : Pas de valeurs en dur, tout vient du backend
    const details = reactive({
      entrepriseNom: '',
      entrepriseAdresse: '',
      entrepriseVille: '',
      entrepriseEmail: '',
      entrepriseTva: '',
      logoPath: '',
      clientNom: '',
      clientAdresse: '',
      clientEmail: '',
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

    const merge = (src) => {
      if (!src || typeof src !== 'object') return

      const mappings = {
        'date_emission': 'dateEmission',
        'date_echeance': 'dateEcheance',
        'montant_ht': 'montantHT',
        'montant_ttc': 'montantTTC',
        'numero_facture': 'numeroFacture',
        'taux_tva': 'tauxTva',
      }

      Object.keys(src).forEach(k => {
        const val = src[k]
        if (val === undefined || val === null) return
        const targetKey = mappings[k] || k
        details[targetKey] = val
      })
    }

    const chargerDetails = async () => {
      try {
        chargement.value = true
        const id = props.facture?.id
        if (!id) return
        const resp = await api.get(`${endpoints.factures}/${id}/pdf-data`)
        merge(resp?.data || {})
      } catch (e) {
        console.error('Erreur chargement détails facture:', e)
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
        emit('action-terminee', { type: 'telechargement', success: true })
      } catch (e) {
        console.error('Erreur téléchargement facture:', e)
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
      if (afficherFacture.value) chargerDetails()
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
.btn{padding:12px 24px;border-radius:8px;cursor:pointer;font-weight:600;font-size:15px}
.btn-primary{background:#0d6efd;color:#fff;border:none}
.btn-success{background:#28a745;color:#fff;border:none}
.btn-outline{border:2px solid #6b7280;background:#fff;color:#6b7280}
.btn:hover:not(:disabled){opacity:.9;transform:translateY(-1px)}

@media (max-width:768px){
  .header-entreprise{flex-direction:column;align-items:center;text-align:center}
  .logo-facture{height:150px}
  .titre-client-section{flex-direction:column;align-items:flex-start;gap:20px}
  .refs-grid{grid-template-columns:1fr;gap:8px}
}
</style>
