/**
 * dataTranslations.js - Mappings de traduction pour toutes les données backend
 * Utilisé par useDataTranslation.js pour traduire les données dynamiques
 */

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
      fr: 'Abonnement Premium CollabPro - Mensuel (excl. TVA)',
      en: 'CollabPro Premium Subscription - Monthly (excl. VAT)',
    },
    'Abonnement Premium CollabPro - Mensuel (excl. TVA)': {
      fr: 'Abonnement Premium CollabPro - Mensuel (excl. TVA)',
      en: 'CollabPro Premium Subscription - Monthly (excl. VAT)',
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
    Janvier: { fr: 'Janvier', en: 'January' },
    Février: { fr: 'Février', en: 'February' },
    Fevrier: { fr: 'Février', en: 'February' },
    Mars: { fr: 'Mars', en: 'March' },
    Avril: { fr: 'Avril', en: 'April' },
    Mai: { fr: 'Mai', en: 'May' },
    Juin: { fr: 'Juin', en: 'June' },
    Juillet: { fr: 'Juillet', en: 'July' },
    Août: { fr: 'Août', en: 'August' },
    Aout: { fr: 'Août', en: 'August' },
    Septembre: { fr: 'Septembre', en: 'September' },
    Octobre: { fr: 'Octobre', en: 'October' },
    Novembre: { fr: 'Novembre', en: 'November' },
    Décembre: { fr: 'Décembre', en: 'December' },
    Decembre: { fr: 'Décembre', en: 'December' },
  },

  // ==================== LABELS DE FACTURES (pour PDF et interface) ====================
  invoiceLabels: {
    'Montant HT': { fr: 'Montant HT', en: 'Amount Excl. Tax' },
    'TVA': { fr: 'TVA', en: 'VAT' },
    'Total TTC': { fr: 'Total TTC', en: 'Total Incl. Tax' },
    'Description': { fr: 'Description', en: 'Description' },
    'Période': { fr: 'Période', en: 'Period' },
    'Facturé à': { fr: 'Facturé à', en: 'Billed to' },
    'Date d\'émission': { fr: 'Date d\'émission', en: 'Issue date' },
    'Date d\'échéance': { fr: 'Date d\'échéance', en: 'Due date' },
    'Numéro de facture': { fr: 'Numéro de facture', en: 'Invoice number' },
    'FACTURE': { fr: 'FACTURE', en: 'INVOICE' },
    'Généré le': { fr: 'Généré le', en: 'Generated on' },
  },
}

export default dataTranslations
