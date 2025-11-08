import { useI18n } from 'vue-i18n'
import { dataTranslations } from '@/i18n/locales/dataTranslations.js'

export function useDataTranslation() {
  const { locale, t } = useI18n()

  /**
   * Traduit une donnée dynamique
   * @param {string} type - Catégorie (status, roles, abonnement, projectTitles, projectDescriptions, etc.)
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

  /**
   * Traduit les titres de projets
   * @param {string} titre - Titre du projet en français
   * @returns {string} - Traduction ou titre original
   */
  function translateProjectTitle(titre) {
    return translateData('projectTitles', titre)
  }

  /**
   * Traduit les descriptions de projets
   * @param {string} description - Description du projet en français
   * @returns {string} - Traduction ou description originale
   */
  function translateProjectDescription(description) {
    return translateData('projectDescriptions', description)
  }

  /**
   * Traduit les descriptions d'abonnements
   * @param {string} description - Description de l'abonnement en français
   * @returns {string} - Traduction ou description originale
   */
  function translateSubscriptionDescription(description) {
    if (!description) return description
    return translateData('subscriptionDescription', description.trim())
  }

  /**
   * Traduit les statuts de factures
   * @param {string} statut - Statut de la facture
   * @returns {string} - Traduction du statut
   */
  function translateInvoiceStatus(statut) {
    return translateData('invoiceStatus', statut)
  }

  /**
   * Traduit les périodes (mois/années)
   * @param {string} periode - Période à traduire
   * @returns {string} - Traduction de la période
   */
  function translatePeriod(periode) {
    if (!periode) return periode
    return translateData('subscriptionDescription', periode.trim())
  }

  return {
    translateData,
    translateRole,
    translateProjectTitle,
    translateProjectDescription,
    translateSubscriptionDescription,
    translateInvoiceStatus,
    translatePeriod
  }
}
