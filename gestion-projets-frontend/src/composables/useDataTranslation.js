import { useI18n } from 'vue-i18n'
import { dataTranslations } from '@/i18n/locales/dataTranslations.js'

export function useDataTranslation() {
  const { locale } = useI18n()

  /**
   * Traduit une donnée dynamique (ex : status, role, mois, etc.)
   * @param {string} type - Catégorie (status, roles, abonnement, etc.)
   * @param {string} value - Valeur à traduire
   * @returns {string} - Traduction correspondante ou valeur d'origine
   */
  function translateData(type, value) {
    const lang = locale.value
    return dataTranslations[type]?.[value]?.[lang] || value
  }

  return { translateData }
}
