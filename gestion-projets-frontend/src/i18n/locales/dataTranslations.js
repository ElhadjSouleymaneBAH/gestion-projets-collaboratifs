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
    NORMALE: { fr: 'Normale', en: 'Normal' },
    MOYENNE: { fr: 'Moyenne', en: 'Medium' },
    HAUTE: { fr: 'Haute', en: 'High' },
    URGENTE: { fr: 'Urgente', en: 'Urgent' },
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
  months: {
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

  // ==================== LABELS DE FACTURES ====================
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
  },

  // ==================== TITRES DE PROJETS ====================
  projectTitles: {
    // === A ===
    'Application Mobile E-commerce': {
      fr: 'Application Mobile E-commerce',
      en: 'E-commerce Mobile Application',
    },
    'Application de Quiz Éducatif': {
      fr: 'Application de Quiz Éducatif',
      en: 'Educational Quiz Application',
    },
    'Application de Réalité Augmentée': {
      fr: 'Application de Réalité Augmentée',
      en: 'Augmented Reality Application',
    },
    'Application de Mindfulness': {
      fr: 'Application de Mindfulness',
      en: 'Mindfulness Application',
    },
    'Application de Suivi Grossesse': {
      fr: 'Application de Suivi Grossesse',
      en: 'Pregnancy Tracking Application',
    },
    'Application de Tracking Grossesse': {
      fr: 'Application de Suivi Grossesse',
      en: 'Pregnancy Tracking Application',
    },
    'Application de Calcul Itinéraire': {
      fr: 'Application de Calcul Itinéraire',
      en: 'Route Calculator Application',
    },
    'Application de Gestion Animaux': {
      fr: 'Application de Gestion Animaux',
      en: 'Pet Management Application',
    },
    'Application de Management Animaux': {
      fr: 'Application de Gestion Animaux',
      en: 'Pet Management Application',
    },
    'Application de Suivi Enfants': {
      fr: 'Application de Suivi Enfants',
      en: 'Child Tracking Application',
    },
    'Application de Tracking Enfants': {
      fr: 'Application de Suivi Enfants',
      en: 'Child Tracking Application',
    },
    'Application de Gestion de Stock': {
      fr: 'Application de Gestion de Stock',
      en: 'Inventory Management Application',
    },
    'Application Mobile Fitness': {
      fr: 'Application Mobile Fitness',
      en: 'Fitness Mobile Application',
    },
    'Application de Suivi de Projet': {
      fr: 'Application de Suivi de Projet',
      en: 'Project Tracking Application',
    },

    // === B ===
    'Boutique en Ligne Vêtements': {
      fr: 'Boutique en Ligne Vêtements',
      en: 'Online Clothing Store',
    },

    // === E ===
    'E-commerce Produits Bio': {
      fr: 'E-commerce Produits Bio',
      en: 'Organic Products E-commerce',
    },

    // === G ===
    'Gestion de Flotte Véhicules': {
      fr: 'Gestion de Flotte Véhicules',
      en: 'Vehicle Fleet Management',
    },
    'Management de Flotte Véhicules': {
      fr: 'Gestion de Flotte Véhicules',
      en: 'Vehicle Fleet Management',
    },
    'Gestion de Bibliothèque': {
      fr: 'Gestion de Bibliothèque',
      en: 'Library Management',
    },
    'Management de Bibliothèque': {
      fr: 'Gestion de Bibliothèque',
      en: 'Library Management',
    },

    // === P ===
    'Plateforme E-learning': {
      fr: 'Plateforme E-learning',
      en: 'E-learning Platform',
    },
    'Plateforme de Réservation': {
      fr: 'Plateforme de Réservation',
      en: 'Booking Platform',
    },
    'Plateforme de Microblogging': {
      fr: 'Plateforme de Microblogging',
      en: 'Microblogging Platform',
    },
    'Platform de Microblogging': {
      fr: 'Plateforme de Microblogging',
      en: 'Microblogging Platform',
    },
    'Plateforme de Gestion Énergétique': {
      fr: 'Plateforme de Gestion Énergétique',
      en: 'Energy Management Platform',
    },
    'Platform de Management Énergétique': {
      fr: 'Plateforme de Gestion Énergétique',
      en: 'Energy Management Platform',
    },
    'Plateforme de Home Staging': {
      fr: 'Plateforme de Home Staging',
      en: 'Home Staging Platform',
    },
    'Platform de Home Staging': {
      fr: 'Plateforme de Home Staging',
      en: 'Home Staging Platform',
    },
    'Plateforme de Financement Étudiant': {
      fr: 'Plateforme de Financement Étudiant',
      en: 'Student Financing Platform',
    },
    'Platform de Financement Étudiant': {
      fr: 'Plateforme de Financement Étudiant',
      en: 'Student Financing Platform',
    },

    // === S ===
    'Système de Gestion RH': {
      fr: 'Système de Gestion RH',
      en: 'HR Management System',
    },
    'System de Management HR': {
      fr: 'Système de Gestion RH',
      en: 'HR Management System',
    },
    'Système CRM': {
      fr: 'Système CRM',
      en: 'CRM System',
    },
    'Système de Caisse Restaurant': {
      fr: 'Système de Caisse Restaurant',
      en: 'Restaurant POS System',
    },
    'System de Caisse Restaurant': {
      fr: 'Système de Caisse Restaurant',
      en: 'Restaurant POS System',
    },
    'Système de Parking Intelligent': {
      fr: 'Système de Parking Intelligent',
      en: 'Smart Parking System',
    },
    'System de Parking Intelligent': {
      fr: 'Système de Parking Intelligent',
      en: 'Smart Parking System',
    },
    'Système de Vote Assemblée': {
      fr: 'Système de Vote Assemblée',
      en: 'Assembly Voting System',
    },
    'System de Vote Assemblée': {
      fr: 'Système de Vote Assemblée',
      en: 'Assembly Voting System',
    },
    'Système de Réservation Salles': {
      fr: 'Système de Réservation Salles',
      en: 'Room Booking System',
    },
    'System de Booking Salles': {
      fr: 'Système de Réservation Salles',
      en: 'Room Booking System',
    },

    // === Sites ===
    'Site Web Portfolio Architecte': {
      fr: 'Site Web Portfolio Architecte',
      en: 'Architect Portfolio Website',
    },
    'Site Web Restaurant': {
      fr: 'Site Web Restaurant',
      en: 'Restaurant Website',
    },
    'Site de Booking Hôtel': {
      fr: 'Site de Réservation Hôtel',
      en: 'Hotel Booking Website',
    },
    'Site de Journalisme Citoyen': {
      fr: 'Site de Journalisme Citoyen',
      en: 'Citizen Journalism Website',
    },
    'Site de Location Matériel': {
      fr: 'Site de Location Matériel',
      en: 'Equipment Rental Website',
    },
    'Site de Comparaison Assurances': {
      fr: 'Site de Comparaison Assurances',
      en: 'Insurance Comparison Website',
    },
    'Site de Location Déguisements': {
      fr: 'Site de Location Déguisements',
      en: 'Costume Rental Website',
    },
  },

  // ==================== DESCRIPTIONS DE PROJETS ====================
  projectDescriptions: {
    // Applications mobiles
    "Développement d'une Application Mobile de vente Online with paiement intégré": {
      fr: "Développement d'une application mobile de vente en ligne avec paiement intégré",
      en: 'Development of an online sales mobile application with integrated payment',
    },
    "Développement d'une application mobile de vente en ligne avec paiement intégré": {
      fr: "Développement d'une application mobile de vente en ligne avec paiement intégré",
      en: 'Development of an online sales mobile application with integrated payment',
    },
    'Jeux éducatifs for enfants with récompenses': {
      fr: 'Jeux éducatifs pour enfants avec récompenses',
      en: 'Educational games for children with rewards',
    },
    'Jeux éducatifs pour enfants avec récompenses': {
      fr: 'Jeux éducatifs pour enfants avec récompenses',
      en: 'Educational games for children with rewards',
    },
    'Essayage virtuel for lunettes et montres': {
      fr: 'Essayage virtuel pour lunettes et montres',
      en: 'Virtual try-on for glasses and watches',
    },
    'Essayage virtuel pour lunettes et montres': {
      fr: 'Essayage virtuel pour lunettes et montres',
      en: 'Virtual try-on for glasses and watches',
    },
    'Méditation et relaxation with Tracking progrès': {
      fr: 'Méditation et relaxation avec suivi des progrès',
      en: 'Meditation and relaxation with progress tracking',
    },
    'Méditation et relaxation avec suivi des progrès': {
      fr: 'Méditation et relaxation avec suivi des progrès',
      en: 'Meditation and relaxation with progress tracking',
    },
    'Accompagnement personnalisé future maman': {
      fr: 'Accompagnement personnalisé future maman',
      en: 'Personalized support for expectant mothers',
    },
    'Navigation optimisée transports en commun': {
      fr: 'Navigation optimisée transports en commun',
      en: 'Optimized public transport navigation',
    },
    'Carnet of santé digital for animaux': {
      fr: 'Carnet de santé digital pour animaux',
      en: 'Digital health record for pets',
    },
    'Carnet de santé digital pour animaux': {
      fr: 'Carnet de santé digital pour animaux',
      en: 'Digital health record for pets',
    },
    'Localisation et sécurité enfants école': {
      fr: 'Localisation et sécurité enfants école',
      en: 'Child location and school safety',
    },

    // Systèmes de gestion
    'Application web for Management des ressources humaines (congés, évaluations, pai': {
      fr: 'Application web pour gestion des ressources humaines (congés, évaluations, paie)',
      en: 'Web application for HR management (leave, evaluations, payroll)',
    },
    'Application web pour gestion des ressources humaines (congés, évaluations, paie)': {
      fr: 'Application web pour gestion des ressources humaines (congés, évaluations, paie)',
      en: 'Web application for HR management (leave, evaluations, payroll)',
    },
    'Logiciel of point of vente for restauration': {
      fr: 'Logiciel de point de vente pour restauration',
      en: 'Point of sale software for restaurants',
    },
    'Logiciel de point de vente pour restauration': {
      fr: 'Logiciel de point de vente pour restauration',
      en: 'Point of sale software for restaurants',
    },
    'Tracking GPS et maintenance of véhicules d\'entreprise': {
      fr: 'Tracking GPS et maintenance de véhicules d\'entreprise',
      en: 'GPS tracking and company vehicle maintenance',
    },
    'Suivi GPS et maintenance des véhicules d\'entreprise': {
      fr: 'Suivi GPS et maintenance des véhicules d\'entreprise',
      en: 'GPS tracking and company vehicle maintenance',
    },
    'System of prêt et catalogage of livres': {
      fr: 'Système de prêt et catalogage de livres',
      en: 'Book lending and cataloging system',
    },
    'Système de prêt et catalogage de livres': {
      fr: 'Système de prêt et catalogage de livres',
      en: 'Book lending and cataloging system',
    },
    'Monitoring consommation électrique smart home': {
      fr: 'Monitoring consommation électrique maison intelligente',
      en: 'Smart home electricity consumption monitoring',
    },

    // E-commerce et boutiques
    'Store Online spécialisée produits biologiques': {
      fr: 'Boutique en ligne spécialisée produits biologiques',
      en: 'Online store specialized in organic products',
    },
    'Boutique en ligne spécialisée produits biologiques': {
      fr: 'Boutique en ligne spécialisée produits biologiques',
      en: 'Online store specialized in organic products',
    },
    'E-commerce spécialisé mode with essayage virtuel': {
      fr: 'E-commerce spécialisé mode avec essayage virtuel',
      en: 'Fashion e-commerce with virtual fitting',
    },
    'E-commerce spécialisé mode avec essayage virtuel': {
      fr: 'E-commerce spécialisé mode avec essayage virtuel',
      en: 'Fashion e-commerce with virtual fitting',
    },

    // Plateformes
    'Platform of booking with paiement Online': {
      fr: 'Plateforme de réservation avec paiement en ligne',
      en: 'Booking platform with online payment',
    },
    'Plateforme de réservation avec paiement en ligne': {
      fr: 'Plateforme de réservation avec paiement en ligne',
      en: 'Booking platform with online payment',
    },
    'Réseau social of messages courts': {
      fr: 'Réseau social de messages courts',
      en: 'Short message social network',
    },
    'Réseau social de messages courts': {
      fr: 'Réseau social de messages courts',
      en: 'Short message social network',
    },
    'Platform collaborative d\'information locale': {
      fr: 'Plateforme collaborative d\'information locale',
      en: 'Collaborative local news platform',
    },
    'Plateforme collaborative d\'information locale': {
      fr: 'Plateforme collaborative d\'information locale',
      en: 'Collaborative local news platform',
    },
    'Visualisation 3D aménagement intérieur': {
      fr: 'Visualisation 3D aménagement intérieur',
      en: '3D interior design visualization',
    },
    'Platform décisionnelle for copropriétés': {
      fr: 'Plateforme décisionnelle pour copropriétés',
      en: 'Decision-making platform for condominiums',
    },
    'Plateforme décisionnelle pour copropriétés': {
      fr: 'Plateforme décisionnelle pour copropriétés',
      en: 'Decision-making platform for condominiums',
    },
    'Prêts étudiants with garantie diplôme': {
      fr: 'Prêts étudiants avec garantie diplôme',
      en: 'Student loans with diploma guarantee',
    },
    'Prêts étudiants avec garantie diplôme': {
      fr: 'Prêts étudiants avec garantie diplôme',
      en: 'Student loans with diploma guarantee',
    },

    // Sites et services
    'Booking et paiement of places of parking': {
      fr: 'Réservation et paiement de places de parking',
      en: 'Parking space booking and payment',
    },
    'Réservation et paiement de places de parking': {
      fr: 'Réservation et paiement de places de parking',
      en: 'Parking space booking and payment',
    },
    'Platform of prêt d\'outils entre particuliers': {
      fr: 'Plateforme de prêt d\'outils entre particuliers',
      en: 'Tool lending platform between individuals',
    },
    'Plateforme de prêt d\'outils entre particuliers': {
      fr: 'Plateforme de prêt d\'outils entre particuliers',
      en: 'Tool lending platform between individuals',
    },
    'Devis automatiques assurance auto/habitation': {
      fr: 'Devis automatiques assurance auto/habitation',
      en: 'Automatic car/home insurance quotes',
    },
    'Booking espaces événementiels with visite 3D': {
      fr: 'Réservation espaces événementiels avec visite 3D',
      en: 'Event space booking with 3D tour',
    },
    'Réservation espaces événementiels avec visite 3D': {
      fr: 'Réservation espaces événementiels avec visite 3D',
      en: 'Event space booking with 3D tour',
    },
    'Platform location costumes fêtes': {
      fr: 'Plateforme location costumes fêtes',
      en: 'Party costume rental platform',
    },
    'Plateforme location costumes fêtes': {
      fr: 'Plateforme location costumes fêtes',
      en: 'Party costume rental platform',
    },
    'Points of fidélité et récompenses': {
      fr: 'Points de fidélité et récompenses',
      en: 'Loyalty points and rewards',
    },
    'Points de fidélité et récompenses': {
      fr: 'Points de fidélité et récompenses',
      en: 'Loyalty points and rewards',
    },
  },

  // ==================== TITRES DE TÂCHES ====================
  taskTitles: {
    'Création maquettes UI': {
      fr: 'Création maquettes UI',
      en: 'UI mockups creation',
    },
    'Développement API REST': {
      fr: 'Développement API REST',
      en: 'REST API development',
    },
    'Tests unitaires': {
      fr: 'Tests unitaires',
      en: 'Unit testing',
    },
    'Intégration paiement': {
      fr: 'Intégration paiement',
      en: 'Payment integration',
    },
    'Déploiement production': {
      fr: 'Déploiement production',
      en: 'Production deployment',
    },
    'Conception base de données': {
      fr: 'Conception base de données',
      en: 'Database design',
    },
    'Authentification utilisateurs': {
      fr: 'Authentification utilisateurs',
      en: 'User authentication',
    },
    'Page d\'accueil optimisée pour le SEO': {
      fr: 'Page d\'accueil optimisée pour le SEO',
      en: 'SEO-optimized homepage',
    },
    'Programme fidélité client': {
      fr: 'Programme fidélité client',
      en: 'Customer loyalty program',
    },
  },

  // ==================== DESCRIPTIONS DE TÂCHES ====================
  taskDescriptions: {
    'Points de fidélité et récompenses': {
      fr: 'Points de fidélité et récompenses',
      en: 'Loyalty points and rewards',
    },
  },
}
