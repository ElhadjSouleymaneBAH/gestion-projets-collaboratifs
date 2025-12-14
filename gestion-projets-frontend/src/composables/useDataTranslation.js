import { useI18n } from 'vue-i18n'
import { dataTranslations } from '@/i18n/locales/dataTranslations.js'

export function useDataTranslation() {
  const { locale, t } = useI18n()

  // ==================== DICTIONNAIRE DE TRADUCTION ====================
  const translationDictionary = {
    // Mots complets (ordre important : les plus longs en premier)
    phrases: {
      // FR -> EN
      frToEn: {
        // Préfixes de projets
        'Application Mobile': 'Mobile Application',
        'Application de Gestion': 'Management Application',
        'Application de Suivi': 'Tracking Application',
        'Application de': 'Application of',
        'Plateforme de Gestion': 'Management Platform',
        'Plateforme de Réservation': 'Booking Platform',
        'Plateforme de': 'Platform of',
        'Système de Gestion': 'Management System',
        'Système de Réservation': 'Booking System',
        'Système de': 'System of',
        'Site Web': 'Website',
        'Site de': 'Website of',
        'Boutique en Ligne': 'Online Store',
        'Gestion de': 'Management of',
        'Suivi de': 'Tracking of',

        // Mots courants
        'en ligne': 'online',
        'avec paiement': 'with payment',
        'avec suivi': 'with tracking',
        'avec récompenses': 'with rewards',
        'pour enfants': 'for children',
        'pour animaux': 'for pets',
        'pour restaurant': 'for restaurant',
        'de vente': 'sales',
        'point de vente': 'point of sale',
        'ressources humaines': 'human resources',
        'maison intelligente': 'smart home',
        'transports en commun': 'public transport',
        'essayage virtuel': 'virtual fitting',
        'visite 3D': '3D tour',
        'd\'entreprise': 'company',
        'entre particuliers': 'between individuals',

        // Mots simples
        'Développement': 'Development',
        'Création': 'Creation',
        'Intégration': 'Integration',
        'Conception': 'Design',
        'Déploiement': 'Deployment',
        'Authentification': 'Authentication',
        'Réservation': 'Booking',
        'Localisation': 'Location',
        'Visualisation': 'Visualization',
        'Navigation': 'Navigation',
        'Monitoring': 'Monitoring',
        'Accompagnement': 'Support',
        'Méditation': 'Meditation',
        'Relaxation': 'Relaxation',

        'application': 'application',
        'plateforme': 'platform',
        'système': 'system',
        'logiciel': 'software',
        'boutique': 'store',
        'site': 'website',
        'réseau': 'network',

        'mobile': 'mobile',
        'virtuel': 'virtual',
        'automatique': 'automatic',
        'personnalisé': 'personalized',
        'optimisé': 'optimized',
        'intelligent': 'smart',
        'collaboratif': 'collaborative',
        'décisionnel': 'decision-making',
        'éducatif': 'educational',
        'numérique': 'digital',
        'digital': 'digital',

        'paiement': 'payment',
        'réservation': 'booking',
        'gestion': 'management',
        'suivi': 'tracking',
        'location': 'rental',
        'prêt': 'lending',
        'vente': 'sale',
        'achat': 'purchase',
        'livraison': 'delivery',

        'utilisateurs': 'users',
        'clients': 'customers',
        'membres': 'members',
        'enfants': 'children',
        'étudiants': 'students',
        'animaux': 'pets',
        'véhicules': 'vehicles',
        'produits': 'products',
        'livres': 'books',
        'costumes': 'costumes',
        'lunettes': 'glasses',
        'montres': 'watches',
        'outils': 'tools',
        'espaces': 'spaces',
        'places': 'spaces',
        'salles': 'rooms',

        'restaurant': 'restaurant',
        'hôtel': 'hotel',
        'école': 'school',
        'bibliothèque': 'library',
        'copropriétés': 'condominiums',
        'parking': 'parking',

        'santé': 'health',
        'sécurité': 'security',
        'fidélité': 'loyalty',
        'progrès': 'progress',
        'garantie': 'guarantee',
        'diplôme': 'diploma',
        'consommation': 'consumption',
        'électrique': 'electrical',
        'intérieur': 'interior',
        'aménagement': 'design',

        'biologiques': 'organic',
        'courts': 'short',
        'locale': 'local',
        'événementiels': 'event',
        'fêtes': 'party',

        'avec': 'with',
        'pour': 'for',
        'entre': 'between',
        'et': 'and',
        'de': 'of',
        'des': 'of',
        'du': 'of the',
        'la': 'the',
        'le': 'the',
        'les': 'the',
        'un': 'a',
        'une': 'a',
      },

      // EN -> FR
      enToFr: {
        // Préfixes de projets
        'Mobile Application': 'Application Mobile',
        'Management Application': 'Application de Gestion',
        'Tracking Application': 'Application de Suivi',
        'Application of': 'Application de',
        'Management Platform': 'Plateforme de Gestion',
        'Booking Platform': 'Plateforme de Réservation',
        'Platform of': 'Plateforme de',
        'Management System': 'Système de Gestion',
        'Booking System': 'Système de Réservation',
        'System of': 'Système de',
        'Website of': 'Site de',
        'Website': 'Site Web',
        'Online Store': 'Boutique en Ligne',
        'Management of': 'Gestion de',
        'Tracking of': 'Suivi de',

        // Mots courants
        'online': 'en ligne',
        'with payment': 'avec paiement',
        'with tracking': 'avec suivi',
        'with rewards': 'avec récompenses',
        'for children': 'pour enfants',
        'for pets': 'pour animaux',
        'for restaurant': 'pour restaurant',
        'sales': 'de vente',
        'point of sale': 'point de vente',
        'human resources': 'ressources humaines',
        'smart home': 'maison intelligente',
        'public transport': 'transports en commun',
        'virtual fitting': 'essayage virtuel',
        '3D tour': 'visite 3D',
        'company': 'd\'entreprise',
        'between individuals': 'entre particuliers',

        // Mots simples
        'Development': 'Développement',
        'Creation': 'Création',
        'Integration': 'Intégration',
        'Design': 'Conception',
        'Deployment': 'Déploiement',
        'Authentication': 'Authentification',
        'Booking': 'Réservation',
        'Location': 'Localisation',
        'Visualization': 'Visualisation',
        'Navigation': 'Navigation',
        'Monitoring': 'Surveillance',
        'Support': 'Accompagnement',
        'Meditation': 'Méditation',
        'Relaxation': 'Relaxation',

        'application': 'application',
        'platform': 'plateforme',
        'system': 'système',
        'software': 'logiciel',
        'store': 'boutique',
        'website': 'site',
        'network': 'réseau',

        'mobile': 'mobile',
        'virtual': 'virtuel',
        'automatic': 'automatique',
        'personalized': 'personnalisé',
        'optimized': 'optimisé',
        'smart': 'intelligent',
        'collaborative': 'collaboratif',
        'decision-making': 'décisionnel',
        'educational': 'éducatif',
        'digital': 'numérique',

        'payment': 'paiement',
        'booking': 'réservation',
        'management': 'gestion',
        'tracking': 'suivi',
        'rental': 'location',
        'lending': 'prêt',
        'sale': 'vente',
        'purchase': 'achat',
        'delivery': 'livraison',

        'users': 'utilisateurs',
        'customers': 'clients',
        'members': 'membres',
        'children': 'enfants',
        'students': 'étudiants',
        'pets': 'animaux',
        'vehicles': 'véhicules',
        'products': 'produits',
        'books': 'livres',
        'costumes': 'costumes',
        'glasses': 'lunettes',
        'watches': 'montres',
        'tools': 'outils',
        'spaces': 'espaces',
        'rooms': 'salles',

        'restaurant': 'restaurant',
        'hotel': 'hôtel',
        'school': 'école',
        'library': 'bibliothèque',
        'condominiums': 'copropriétés',
        'parking': 'parking',

        'health': 'santé',
        'security': 'sécurité',
        'loyalty': 'fidélité',
        'progress': 'progrès',
        'guarantee': 'garantie',
        'diploma': 'diplôme',
        'consumption': 'consommation',
        'electrical': 'électrique',
        'interior': 'intérieur',

        'organic': 'biologiques',
        'short': 'courts',
        'local': 'locale',
        'event': 'événementiel',
        'party': 'fêtes',

        'with': 'avec',
        'for': 'pour',
        'between': 'entre',
        'and': 'et',
        'of': 'de',
        'the': 'le',
        'a': 'un',
      },
    },
  }

  // ==================== FONCTIONS UTILITAIRES ====================

  /**
   * Détecte la langue d'un texte
   */
  function detectLanguage(text) {
    if (!text) return 'unknown'

    const frenchIndicators = ['é', 'è', 'ê', 'ë', 'à', 'â', 'ù', 'û', 'ô', 'î', 'ï', 'ç', 'œ', 'æ']
    const frenchWords = ['de', 'du', 'des', 'le', 'la', 'les', 'un', 'une', 'et', 'pour', 'avec', 'dans', 'sur']
    const englishWords = ['the', 'of', 'and', 'for', 'with', 'in', 'on', 'to', 'a', 'an']

    const lowerText = text.toLowerCase()

    // Vérifier les accents français
    for (const char of frenchIndicators) {
      if (lowerText.includes(char)) return 'fr'
    }

    // Compter les mots français et anglais
    let frCount = 0
    let enCount = 0

    frenchWords.forEach((word) => {
      const regex = new RegExp(`\\b${word}\\b`, 'gi')
      frCount += (lowerText.match(regex) || []).length
    })

    englishWords.forEach((word) => {
      const regex = new RegExp(`\\b${word}\\b`, 'gi')
      enCount += (lowerText.match(regex) || []).length
    })

    if (frCount > enCount) return 'fr'
    if (enCount > frCount) return 'en'

    return 'fr' // Par défaut français
  }

  /**
   * Traduction intelligente - retourne le texte original si pas de traduction exacte
   */
  function smartTranslate(text, targetLang) {
    if (!text) return text

    return text
  }

  // ==================== FONCTIONS PRINCIPALES ====================

  /**
   * Traduit une donnée statique (status, priority, roles, etc.)
   */
  function translateData(type, value) {
    if (!value) return value
    const translation = dataTranslations[type]?.[value]?.[locale.value]
    return translation || value
  }

  /**
   * Traduit un rôle utilisateur
   */
  function translateRole(role) {
    if (!role) return t('roles.membre')

    const roleKey = role.toUpperCase().replace('-', '_')
    const translation = dataTranslations.roles?.[roleKey]?.[locale.value]

    if (translation) return translation

    // Fallback sur les clés i18n
    const roleMap = {
      CHEF_PROJET: 'roles.chefProjet',
      ADMINISTRATEUR: 'roles.administrateur',
      MEMBRE: 'roles.membre',
      VISITEUR: 'roles.visiteur',
    }

    return t(roleMap[roleKey] || 'roles.membre')
  }

  /**
   * Traduit un titre de projet
   */
  function translateProjectTitle(titre) {
    if (!titre) return titre

    // 1. Chercher traduction exacte dans le dictionnaire
    const exactMatch = dataTranslations.projectTitles?.[titre]?.[locale.value]
    if (exactMatch) return exactMatch

    // 2. Chercher avec trim
    const trimmedMatch = dataTranslations.projectTitles?.[titre.trim()]?.[locale.value]
    if (trimmedMatch) return trimmedMatch

    // 3. Traduction intelligente automatique
    return smartTranslate(titre, locale.value)
  }

  /**
   * Traduit une description de projet
   */
  function translateProjectDescription(description) {
    if (!description) return description

    // 1. Chercher traduction exacte
    const exactMatch = dataTranslations.projectDescriptions?.[description]?.[locale.value]
    if (exactMatch) return exactMatch

    // 2. Chercher avec trim
    const trimmedMatch = dataTranslations.projectDescriptions?.[description.trim()]?.[locale.value]
    if (trimmedMatch) return trimmedMatch

    // 3. Traduction intelligente automatique
    return smartTranslate(description, locale.value)
  }

  /**
   * Traduit un titre de tâche
   */
  function translateTaskTitle(titre) {
    if (!titre) return titre

    const exactMatch = dataTranslations.taskTitles?.[titre]?.[locale.value]
    if (exactMatch) return exactMatch

    return smartTranslate(titre, locale.value)
  }

  /**
   * Traduit une description de tâche
   */
  function translateTaskDescription(description) {
    if (!description) return description

    const exactMatch = dataTranslations.taskDescriptions?.[description]?.[locale.value]
    if (exactMatch) return exactMatch

    return smartTranslate(description, locale.value)
  }

  /**
   * Traduit une description d'abonnement
   */
  function translateSubscriptionDescription(description) {
    if (!description) return description
    return translateData('subscriptionDescription', description.trim()) || description
  }

  /**
   * Traduit un statut de facture
   */
  function translateInvoiceStatus(statut) {
    return translateData('invoiceStatus', statut)
  }

  /**
   * Traduit une période (mois)
   */
  function translatePeriod(periode) {
    if (!periode) return periode

    // Chercher dans les descriptions d'abonnement
    const match = dataTranslations.subscriptionDescription?.[periode.trim()]?.[locale.value]
    if (match) return match

    // Traduire les noms de mois
    let result = periode
    Object.entries(dataTranslations.months || {}).forEach(([key, values]) => {
      const frMonth = values.fr
      const enMonth = values.en

      if (locale.value === 'en' && result.includes(frMonth)) {
        result = result.replace(frMonth, enMonth)
      } else if (locale.value === 'fr' && result.includes(enMonth)) {
        result = result.replace(enMonth, frMonth)
      }
    })

    return result
  }

  /**
   * Traduit un label de facture
   */
  function translateInvoiceLabel(label) {
    return translateData('invoiceLabels', label)
  }

  /**
   * Traduit un texte générique (auto-détection)
   */
  function translateText(text) {
    if (!text) return text
    return smartTranslate(text, locale.value)
  }

  // ==================== EXPORT ====================
  return {
    // Fonctions principales
    translateData,
    translateRole,
    translateProjectTitle,
    translateProjectDescription,
    translateTaskTitle,
    translateTaskDescription,
    translateSubscriptionDescription,
    translateInvoiceStatus,
    translateInvoiceLabel,
    translatePeriod,
    translateText,

    // Utilitaires
    detectLanguage,
    smartTranslate,

    // Accès direct aux données
    dataTranslations,
  }
}
