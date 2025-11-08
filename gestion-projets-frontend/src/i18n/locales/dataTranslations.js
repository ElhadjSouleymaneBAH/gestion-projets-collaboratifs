export const dataTranslations = {
  // ==================== STATUTS DE PROJETS ====================
  status: {
    ACTIF: { fr: 'Actif', en: 'Active' },
    SUSPENDU: { fr: 'Suspendu', en: 'Suspended' },
    TERMINE: { fr: 'Terminé', en: 'Completed' },
    ANNULE: { fr: 'Annulé', en: 'Cancelled' },
    ARCHIVE: { fr: 'Archivé', en: 'Archived' },
    EN_COURS: { fr: 'En cours', en: 'In Progress' },
  },

  // ==================== STATUTS DE TÂCHES ====================
  taskStatus: {
    BROUILLON: { fr: 'Brouillon', en: 'Draft' },
    EN_ATTENTE: { fr: 'En attente', en: 'Pending' },
    EN_ATTENTE_VALIDATION: { fr: 'En attente de validation', en: 'Pending Validation' },
    EN_COURS: { fr: 'En cours', en: 'In Progress' },
    TERMINE: { fr: 'Terminé', en: 'Completed' },
    VALIDEE: { fr: 'Validée', en: 'Validated' },
    ANNULE: { fr: 'Annulé', en: 'Cancelled' },
  },

  // ==================== PRIORITÉS DE TÂCHES ====================
  priority: {
    BASSE: { fr: 'Basse', en: 'Low' },
    MOYENNE: { fr: 'Moyenne', en: 'Medium' },
    HAUTE: { fr: 'Haute', en: 'High' },
    CRITIQUE: { fr: 'Critique', en: 'Critical' },
  },

  // ==================== RÔLES UTILISATEURS ====================
  roles: {
    ADMIN: { fr: 'Administrateur', en: 'Administrator' },
    ADMINISTRATEUR: { fr: 'Administrateur', en: 'Administrator' },
    CHEF_DE_PROJET: { fr: 'Chef de projet', en: 'Project Manager' },
    CHEF_PROJET: { fr: 'Chef de projet', en: 'Project Manager' },
    CO_CHEF_PROJET: { fr: 'Co-chef de projet', en: 'Co-Project Manager' },
    MEMBRE: { fr: 'Membre', en: 'Member' },
    VISITEUR: { fr: 'Visiteur', en: 'Visitor' },
  },

  // ==================== STATUTS DE FACTURES ====================
  invoiceStatus: {
    GENEREE: { fr: 'Générée', en: 'Generated' },
    PAYEE: { fr: 'Payée', en: 'Paid' },
    EN_ATTENTE: { fr: 'En attente', en: 'Pending' },
    ECHEC: { fr: 'Échec', en: 'Failed' },
    ENVOYEE: { fr: 'Envoyée', en: 'Sent' },
    ANNULEE: { fr: 'Annulée', en: 'Cancelled' },
  },

  // ==================== TYPES ET STATUTS D'ABONNEMENTS ====================
  subscriptionType: {
    PREMIUM: { fr: 'Premium', en: 'Premium' },
    GRATUIT: { fr: 'Gratuit', en: 'Free' },
    BASIC: { fr: 'Basique', en: 'Basic' },
  },

  subscriptionStatus: {
    ACTIF: { fr: 'Actif', en: 'Active' },
    EXPIRE: { fr: 'Expiré', en: 'Expired' },
    SUSPENDU: { fr: 'Suspendu', en: 'Suspended' },
    ANNULE: { fr: 'Annulé', en: 'Cancelled' },
  },

  // ==================== DESCRIPTIONS D'ABONNEMENTS ====================
  subscriptionDescription: {
    'Abonnement Premium CollabPro - Mensuel': {
      fr: 'Abonnement Premium CollabPro - Mensuel',
      en: 'CollabPro Premium Subscription - Monthly',
    },
    'Abonnement Premium CollabPro - Mensuel (excl. TVA)': {
      fr: 'Abonnement Premium CollabPro - Mensuel (excl. TVA)',
      en: 'CollabPro Premium Subscription - Monthly (excl. VAT)',
    },
    'Période : : Décembre 2025': {
      fr: 'Période : : Décembre 2025',
      en: 'Period: December 2025',
    },
    'Période : : Janvier 2025': {
      fr: 'Période : : Janvier 2025',
      en: 'Period: January 2025',
    },
    'Période : : Février 2025': {
      fr: 'Période : : Février 2025',
      en: 'Period: February 2025',
    },
    'Période : : Mars 2025': {
      fr: 'Période : : Mars 2025',
      en: 'Period: March 2025',
    },
    'Période : : Avril 2025': {
      fr: 'Période : : Avril 2025',
      en: 'Period: April 2025',
    },
    'Période : : Mai 2025': {
      fr: 'Période : : Mai 2025',
      en: 'Period: May 2025',
    },
    'Période : : Juin 2025': {
      fr: 'Période : : Juin 2025',
      en: 'Period: June 2025',
    },
    'Période : : Juillet 2025': {
      fr: 'Période : : Juillet 2025',
      en: 'Period: July 2025',
    },
    'Période : : Août 2025': {
      fr: 'Période : : Août 2025',
      en: 'Period: August 2025',
    },
    'Période : : Septembre 2025': {
      fr: 'Période : : Septembre 2025',
      en: 'Period: September 2025',
    },
    'Période : : Octobre 2025': {
      fr: 'Période : : Octobre 2025',
      en: 'Period: October 2025',
    },
    'Période : : Novembre 2025': {
      fr: 'Période : : Novembre 2025',
      en: 'Period: November 2025',
    },
  },

  // ==================== VISIBILITÉ DE PROJETS ====================
  visibility: {
    PRIVE: { fr: 'Privé', en: 'Private' },
    PUBLIC: { fr: 'Public', en: 'Public' },
  },

  // ==================== TYPES DE NOTIFICATIONS ====================
  notificationType: {
    TACHE: { fr: 'Tâche', en: 'Task' },
    PROJET: { fr: 'Projet', en: 'Project' },
    COMMENTAIRE: { fr: 'Commentaire', en: 'Comment' },
    INVITATION: { fr: 'Invitation', en: 'Invitation' },
    PAIEMENT: { fr: 'Paiement', en: 'Payment' },
    MEMBRE: { fr: 'Membre', en: 'Member' },
    SYSTEME: { fr: 'Système', en: 'System' },
  },

  // ==================== MOIS DE L'ANNÉE ====================
  mois: {
    JANVIER: { fr: 'Janvier', en: 'January' },
    FEVRIER: { fr: 'Février', en: 'February' },
    MARS: { fr: 'Mars', en: 'March' },
    AVRIL: { fr: 'Avril', en: 'April' },
    MAI: { fr: 'Mai', en: 'May' },
    JUIN: { fr: 'Juin', en: 'June' },
    JUILLET: { fr: 'Juillet', en: 'July' },
    AOUT: { fr: 'Août', en: 'August' },
    SEPTEMBRE: { fr: 'Septembre', en: 'September' },
    OCTOBRE: { fr: 'Octobre', en: 'October' },
    NOVEMBRE: { fr: 'Novembre', en: 'November' },
    DECEMBRE: { fr: 'Décembre', en: 'December' },
  },

  // ==================== LABELS DE FACTURES (pour PDF et interface) ====================
  invoiceLabels: {
    MONTANT_HT: { fr: 'Montant HT', en: 'Amount Excl. Tax' },
    TVA: { fr: 'TVA', en: 'VAT' },
    TOTAL_TTC: { fr: 'Total TTC', en: 'Total Incl. Tax' },
    DESCRIPTION: { fr: 'Description', en: 'Description' },
    PERIODE: { fr: 'Période', en: 'Period' },
    FACTURE_A: { fr: 'Facturé à', en: 'Billed to' },
    DATE_EMISSION: { fr: "Date d'émission", en: 'Issue date' },
    DATE_ECHEANCE: { fr: "Date d'échéance", en: 'Due date' },
    NUMERO_FACTURE: { fr: 'Numéro de facture', en: 'Invoice number' },
    FACTURE: { fr: 'FACTURE', en: 'INVOICE' },
    GENERE_LE: { fr: 'Généré le', en: 'Generated on' },
  },

  // ==================== TITRES DE PROJETS (DONNÉES DYNAMIQUES) ====================
  projectTitles: {
    'Application Mobile E-commerce': {
      fr: 'Application Mobile E-commerce',
      en: 'E-commerce Mobile Application',
    },
    'Site Web Portfolio Architecte': {
      fr: 'Site Web Portfolio Architecte',
      en: 'Architect Portfolio Website',
    },
    'Système de Gestion RH': {
      fr: 'Système de Gestion RH',
      en: 'HR Management System',
    },
    'Plateforme E-learning': {
      fr: 'Plateforme E-learning',
      en: 'E-learning Platform',
    },
    'Application de Gestion de Stock': {
      fr: 'Application de Gestion de Stock',
      en: 'Inventory Management Application',
    },
    'Site Web Restaurant': {
      fr: 'Site Web Restaurant',
      en: 'Restaurant Website',
    },
    'Application Mobile Fitness': {
      fr: 'Application Mobile Fitness',
      en: 'Fitness Mobile Application',
    },
    'Système CRM': {
      fr: 'Système CRM',
      en: 'CRM System',
    },
    'Plateforme de Réservation': {
      fr: 'Plateforme de Réservation',
      en: 'Booking Platform',
    },
    'Application de Suivi de Projet': {
      fr: 'Application de Suivi de Projet',
      en: 'Project Tracking Application',
    },
    'Boutique en Ligne Vêtements': {
      fr: 'Boutique en Ligne Vêtements',
      en: 'Online Clothing Store',
    },
  },

  // ==================== DESCRIPTIONS DE PROJETS (DONNÉES DYNAMIQUES) ====================
  projectDescriptions: {
    "Développement d'une application mobile de vente en ligne avec paiement intégré": {
      fr: "Développement d'une application mobile de vente en ligne avec paiement intégré",
      en: 'Development of an online sales mobile application with integrated payment',
    },
    "Création d'un site vitrine pour cabinet d'architecture avec galerie interactive": {
      fr: "Création d'un site vitrine pour cabinet d'architecture avec galerie interactive",
      en: 'Creation of a showcase website for an architecture firm with interactive gallery',
    },
    'Application web pour gestion des ressources humaines (congés, évaluations, paie)': {
      fr: 'Application web pour gestion des ressources humaines (congés, évaluations, paie)',
      en: 'Web application for human resources management (leave, evaluations, payroll)',
    },
    'Plateforme de formation en ligne avec système de certification': {
      fr: 'Plateforme de formation en ligne avec système de certification',
      en: 'Online training platform with certification system',
    },
    'Système de gestion de stock avec codes-barres et alertes automatiques': {
      fr: 'Système de gestion de stock avec codes-barres et alertes automatiques',
      en: 'Inventory management system with barcodes and automatic alerts',
    },
    'Site web responsive pour restaurant avec système de réservation en ligne': {
      fr: 'Site web responsive pour restaurant avec système de réservation en ligne',
      en: 'Responsive website for restaurant with online booking system',
    },
    'Application mobile de fitness avec suivi personnalisé et programmes entraînement': {
      fr: 'Application mobile de fitness avec suivi personnalisé et programmes entraînement',
      en: 'Fitness mobile app with personalized tracking and training programs',
    },
    'Système CRM pour gestion de la relation client et pipeline de ventes': {
      fr: 'Système CRM pour gestion de la relation client et pipeline de ventes',
      en: 'CRM system for customer relationship management and sales pipeline',
    },
    'Plateforme de réservation multi-services avec paiement sécurisé': {
      fr: 'Plateforme de réservation multi-services avec paiement sécurisé',
      en: 'Multi-service booking platform with secure payment',
    },
    'Application de suivi de projet agile avec tableaux Kanban et reporting': {
      fr: 'Application de suivi de projet agile avec tableaux Kanban et reporting',
      en: 'Agile project tracking application with Kanban boards and reporting',
    },
    'E-commerce spécialisé mode avec essayage virtuel...': {
      fr: 'E-commerce spécialisé mode avec essayage virtuel...',
      en: 'Specialized fashion e-commerce with virtual fitting...',
    },
  },
}
