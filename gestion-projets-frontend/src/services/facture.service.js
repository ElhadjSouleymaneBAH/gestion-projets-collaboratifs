import api, { factureAPI as base } from './api'
import i18n from '@/i18n'

const ERROR_CODES = {
  LOAD_FACTURES_FAILED: 'factures.errors.loadFailed',
  DOWNLOAD_PDF_FAILED: 'factures.errors.downloadFailed',
  EMAIL_NOT_AVAILABLE: 'factures.errors.emailNotAvailable',
}

const SUCCESS_MESSAGES = {
  FACTURES_LOADED: 'factures.success.loaded',
  PDF_DOWNLOADED: 'factures.success.pdfDownloaded',
}

const normalize = (f) => {
  const d = f?.dateEmission ? new Date(f.dateEmission) : null
  const periode = d ? `${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}` : ''
  return { ...f, montantTTC: (Number(f.montantHT) || 0) + (Number(f.tva) || 0), periode }
}

/**
 * Récupère la langue active de manière robuste
 */
const getActiveLanguage = () => {
  try {
    // 1. localStorage avec la clé 'langue'
    const storedLang = localStorage.getItem('langue')
    if (storedLang && (storedLang === 'fr' || storedLang === 'en')) {
      return storedLang
    }

    // 2. i18n (Composition API)
    if (i18n?.global?.locale?.value) {
      return i18n.global.locale.value
    }

    // 3. i18n (Legacy API )
    if (i18n?.locale) {
      return i18n.locale
    }

    // 4. Langue du navigateur
    const browserLang = navigator.language?.split('-')[0]
    if (browserLang === 'en' || browserLang === 'fr') {
      return browserLang
    }
  } catch (error) {
    console.warn('Erreur récupération langue:', error)
  }

  // 5. Défaut français
  return 'fr'
}

export default {
  async getFactures() {
    try {
      const user = JSON.parse(localStorage.getItem('user') || 'null')
      const isAdmin = user?.role === 'ADMINISTRATEUR'
      const { data } = isAdmin ? await base.getToutesAdmin() : await base.getMesFactures()
      const list = Array.isArray(data) ? data.map(normalize) : []

      return {
        success: true,
        data: {
          content: list,
          number: 0,
          totalPages: 1,
          totalElements: list.length,
        },
        messageKey: SUCCESS_MESSAGES.FACTURES_LOADED,
      }
    } catch (e) {
      console.error('facture.service.getFactures error:', e)
      return { success: false, errorCode: ERROR_CODES.LOAD_FACTURES_FAILED }
    }
  },

  async telechargerPDF(id) {
    try {
      const langue = getActiveLanguage()

      const res = await base.telechargerPDF(id, langue)

      const disp = res.headers?.['content-disposition'] || ''
      const match = /filename="?([^"]+)"?/.exec(disp)
      const file = match?.[1] || `facture_${id}.pdf`

      const blob = new Blob([res.data], { type: 'application/pdf' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = file
      document.body.appendChild(a)
      a.click()
      a.remove()
      URL.revokeObjectURL(url)

      return { success: true, messageKey: SUCCESS_MESSAGES.PDF_DOWNLOADED }
    } catch (e) {
      console.error('facture.service.telechargerPDF error:', e)
      return { success: false, errorCode: ERROR_CODES.DOWNLOAD_PDF_FAILED }
    }
  },

  async envoyerParEmail() {
    return { success: false, errorCode: ERROR_CODES.EMAIL_NOT_AVAILABLE }
  },

  ERROR_CODES,
  SUCCESS_MESSAGES,
}
