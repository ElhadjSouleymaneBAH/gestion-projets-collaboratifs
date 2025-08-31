// src/i18n/locales/en.js - VERSION COMPLÈTE CORRIGÉE
export default {
  // GLOBAL NAVIGATION
  nav: {
    accueil: 'Home',
    connexion: 'Login',
    inscription: 'Register',
    deconnexion: 'Logout',
    tableauBord: 'Dashboard',
    projetsPublics: 'Public Projects',
    monProfil: 'My Profile',
    administration: 'Administration',
    abonnement: 'Subscription',
    factures: 'Invoices'
  },

  // COMMON ACROSS APP
  commun: {
    charger: 'Load',
    chargement: 'Loading...',
    enregistrer: 'Save',
    annuler: 'Cancel',
    modifier: 'Edit',
    supprimer: 'Delete',
    valider: 'Validate',
    fermer: 'Close',
    rechercher: 'Search',
    filtrer: 'Filter',
    trier: 'Sort',
    suivant: 'Next',
    precedent: 'Previous',
    retour: 'Back',
    confirmer: 'Confirm',
    oui: 'Yes',
    non: 'No',
    aucunResultat: 'No results found',
    erreur: 'An error occurred',
    succes: 'Operation successful',
    langue: 'Language',
    fonctionnalites: 'Features',
    pretCommencer: 'Ready to get started?',
    rejoindreCollabPro: 'Join CollabPro and start managing your collaborative projects now.',
    droitsReserves: 'All rights reserved'
  },

  // F1: REGISTRATION
  inscription: {
    titre: 'Create Account',
    sousTitre: 'Join our collaborative platform',
    prenom: 'First Name',
    nom: 'Last Name',
    email: 'Email Address',
    motDePasse: 'Password',
    confirmerMotDePasse: 'Confirm Password',
    typeCompte: 'Account Type',
    choisirRole: 'Choose your role',
    accepterCGU: 'I accept the terms of service',
    creerCompte: 'Create my account',
    dejaCompte: 'Already have an account?',
    seConnecter: 'Sign in',
    description: 'Create user account with name, email and password.',
    languePreferee: 'Preferred Language'
  },

  // F2: LOGIN
  connexion: {
    titre: 'Login',
    sousTitre: 'Access your collaborative workspace',
    email: 'Email Address',
    motDePasse: 'Password',
    seConnecter: 'Sign in',
    motDePasseOublie: 'Forgot password?',
    pasDeCompte: 'Don\'t have an account?',
    sInscrire: 'Sign up',
    resterConnecte: 'Stay logged in'
  },

  // FORGOT PASSWORD
  motDePasseOublie: {
    titre: 'Forgot Password',
    sousTitre: 'We\'ll send you a reset link',
    email: 'Your email address',
    envoyer: 'Send link',
    retourConnexion: 'Back to login',
    emailEnvoye: 'Reset email sent'
  },

  // F3: PUBLIC PROJECTS
  projetsPublics: {
    titre: 'Public Projects',
    sousTitre: 'Discover projects open to everyone',
    aucunProjet: 'No public projects available',
    voirDetails: 'View details',
    dateCreation: 'Created on',
    creePar: 'Created by',
    statut: 'Status',
    description: 'View list of public projects even without account.'
  },

  // F6: PROJECT MANAGEMENT
  projets: {
    titre: 'My Projects',
    nouveau: 'New Project',
    nom: 'Project name',
    description: 'Description',
    statut: 'Status',
    dateCreation: 'Creation date',
    membres: 'Members',
    taches: 'Tasks',
    creer: 'Create project',
    modifier: 'Edit project',
    supprimer: 'Delete project',
    archiver: 'Archive',
    restaurer: 'Restore',
    gestion: 'Manage Projects',
    descriptionGestion: 'Create, edit or delete projects (Project Manager only).',
    statuts: {
      actif: 'Active',
      suspendu: 'Suspended',
      termine: 'Completed',
      annule: 'Cancelled'
    }
  },

  // F7: TASK MANAGEMENT
  taches: {
    titre: 'Task Management',
    nouvelle: 'New Task',
    nom: 'Task name',
    description: 'Description',
    assigne: 'Assigned to',
    priorite: 'Priority',
    echeance: 'Due date',
    statut: 'Status',
    creer: 'Create task',
    modifier: 'Edit task',
    terminer: 'Mark as completed',
    gestion: 'Manage Tasks',
    descriptionGestion: 'Create, assign or modify tasks within projects.',
    statuts: {
      brouillon: 'Draft',
      enAttente: 'Pending validation',
      termine: 'Completed',
      annule: 'Cancelled'
    },
    priorites: {
      basse: 'Low',
      moyenne: 'Medium',
      haute: 'High',
      critique: 'Critical'
    }
  },

  // F8: MEMBER MANAGEMENT
  membres: {
    titre: 'Project Members',
    ajouter: 'Add member',
    email: 'Member email',
    role: 'Role in project',
    inviter: 'Send invitation',
    retirer: 'Remove from project',
    changerRole: 'Change role',
    invitationEnvoyee: 'Invitation sent',
    ajout: 'Add Members',
    descriptionAjout: 'Invite and manage project members (Project Manager).',
    roles: {
      membre: 'Member',
      moderateur: 'Moderator',
      admin: 'Administrator'
    }
  },

  // F9: REAL-TIME COLLABORATION
  collaboration: {
    chat: 'Chat',
    ecrireMessage: 'Write a message...',
    envoyer: 'Send',
    enLigne: 'Online',
    horsLigne: 'Offline',
    tape: 'is typing...',
    fichier: 'File',
    partager: 'Share',
    tempsReel: 'Real-time Chat',
    descriptionTempsReel: 'Real-time chat in projects for connected members.'
  },

  // F10: SUBSCRIPTIONS AND PAYMENTS
  abonnement: {
    titre: 'Premium Subscription',
    sousTitre: 'Become a Project Manager',
    planPremium: 'Premium Plan',
    prix: '€10/month',
    fonctionnalites: 'Included features',
    souscrire: 'Subscribe now',
    gerer: 'Manage my subscription',
    annuler: 'Cancel subscription',
    renouveler: 'Renew',
    statut: 'Subscription status',
    prochainPaiement: 'Next payment',
    paymentSuccess: 'Payment successful!',
    paymentCancel: 'Payment cancelled',
    paiements: 'Stripe Payments',
    descriptionPaiements: 'Subscribe to secure monthly subscription via Stripe.'
  },

  // F11: INVOICES
  factures: {
    titre: 'My Invoices',
    numero: 'Number',
    date: 'Date',
    montant: 'Amount',
    statut: 'Status',
    telecharger: 'Download',
    generer: 'Generate invoice',
    aucuneFacture: 'No invoices available',
    pdf: 'PDF Invoices',
    descriptionPdf: 'Automatic PDF invoice generation for each payment.',
    statuts: {
      payee: 'Paid',
      enAttente: 'Pending',
      echouee: 'Failed'
    }
  },

  // F12: COMMENTS
  commentaires: {
    titre: 'Comments',
    ajouter: 'Add comment',
    ecrire: 'Write your comment...',
    publier: 'Post',
    modifier: 'Edit',
    supprimer: 'Delete',
    repondre: 'Reply',
    aucunCommentaire: 'No comments'
  },

  // F13: NOTIFICATIONS
  notifications: {
    titre: 'Notifications',
    nouvelle: 'New notification',
    marquerLue: 'Mark as read',
    marquerToutesLues: 'Mark all as read',
    aucuneNotification: 'No notifications',
    types: {
      tache: 'New task assigned',
      projet: 'New project',
      commentaire: 'New comment',
      membre: 'New member added'
    }
  },

  // SPECIFIC DASHBOARDS
  tableauBord: {
    // Project Manager
    chefProjet: {
      titre: 'Project Manager Dashboard',
      mesProjets: 'My Projects',
      mesEquipes: 'My Teams',
      tachesEnCours: 'Ongoing Tasks',
      notifications: 'Recent Notifications'
    },
    // Member
    membre: {
      titre: 'Member Dashboard',
      mesTaches: 'My Tasks',
      projetsParticipes: 'Projects I Participate In',
      activiteRecente: 'Recent Activity'
    },
    // Administrator
    admin: {
      titre: 'Administrator Dashboard',
      utilisateurs: 'User Management',
      abonnements: 'Subscription Supervision',
      statistiques: 'Platform Statistics',
      maintenance: 'Maintenance'
    }
  },

  // USER ROLES
  roles: {
    visiteur: 'Visitor',
    membre: 'Member',
    chefProjet: 'Project Manager',
    administrateur: 'Administrator'
  },

  // F4 & F5: USER PROFILE
  profil: {
    titre: 'My Profile',
    informations: 'Personal information',
    modifier: 'Edit my profile',
    changerMotDePasse: 'Change password',
    ancienMotDePasse: 'Old password',
    nouveauMotDePasse: 'New password',
    confirmerNouveauMotDePasse: 'Confirm new password',
    photo: 'Profile picture',
    preferences: 'Preferences',
    langue: 'Preferred language'
  },

  // TERMS & GDPR
  conditions: {
    titre: 'Terms of Service',
    sousTitre: 'Privacy Policy and GDPR',
    accepter: 'Accept terms',
    derniereMiseAJour: 'Last updated'
  },

  // VALIDATION & ERRORS
  validation: {
    obligatoire: 'This field is required',
    emailInvalide: 'Invalid email format',
    motDePasseTropCourt: 'Password must be at least 6 characters',
    motDePassesDifferents: 'Passwords do not match',
    emailDejaUtilise: 'This email address is already in use'
  }
}
