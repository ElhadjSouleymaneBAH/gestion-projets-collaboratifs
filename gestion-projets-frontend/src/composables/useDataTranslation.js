import { useI18n } from 'vue-i18n'
import { dataTranslations } from '@/i18n/locales/dataTranslations.js'

export function useDataTranslation() {
  const { locale, t } = useI18n()

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

  /**
   * Traduit les rôles utilisateur (spécifique)
   * @param {string} role - Code du rôle (ex : CHEF_PROJET, MEMBRE)
   * @returns {string} - Traduction lisible
   */
  function translateRole(role) {
    const roles = {
      CHEF_PROJET: t('roles.chefProjet'),
      ADMINISTRATEUR: t('roles.administrateur'),
      MEMBRE: t('roles.membre'),
    }
    return roles[role?.toUpperCase()] || t('roles.membre')
  }

  return { translateData, translateRole }
}
