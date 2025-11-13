import { useI18n } from 'vue-i18n'
import { dataTranslations } from '@/i18n/locales/dataTranslations.js'

export function useDataTranslation() {
  const { locale, t } = useI18n()

  // Dictionnaire de mots-clés pour traduction automatique
  const keywords = {
    'Application': 'Application',
    'Plateforme': 'Platform',
    'Système': 'System',
    'Site Web': 'Website',
    'Boutique': 'Store',
    'Gestion': 'Management',
    'Mobile': 'Mobile',
    'E-commerce': 'E-commerce',
    'Restaurant': 'Restaurant',
    'Fitness': 'Fitness',
    'CRM': 'CRM',
    'RH': 'HR',
    'Stock': 'Inventory',
    'Réservation': 'Booking',
    'Suivi': 'Tracking',
    'Projet': 'Project',
    'Vêtements': 'Clothing',
    'En ligne': 'Online',
    'Portfolio': 'Portfolio',
    'Architecte': 'Architect',
    'Formation': 'Training',
    'de': 'of',
    'pour': 'for',
    'avec': 'with'
  }

  // Traduction automatique
  function autoTranslate(text) {
    if (!text || locale.value === 'fr') return text

    let result = text
    Object.entries(keywords).forEach(([fr, en]) => {
      result = result.replace(new RegExp(`\\b${fr}\\b`, 'gi'), en)
    })
    return result
  }

  function translateData(type, value) {
    return dataTranslations[type]?.[value]?.[locale.value] || value
  }

  function translateRole(role) {
    const roles = {
      CHEF_PROJET: t('roles.chefProjet'),
      ADMINISTRATEUR: t('roles.administrateur'),
      MEMBRE: t('roles.membre'),
    }
    return roles[role?.toUpperCase()] || t('roles.membre')
  }

  function translateProjectTitle(titre) {
    // Traduction automatique uniquement
    return autoTranslate(titre)
  }

  function translateProjectDescription(description) {
    // Traduction automatique uniquement
    return autoTranslate(description)
  }

  function translateSubscriptionDescription(description) {
    if (!description) return description
    return translateData('subscriptionDescription', description.trim())
  }

  function translateInvoiceStatus(statut) {
    return translateData('invoiceStatus', statut)
  }

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
