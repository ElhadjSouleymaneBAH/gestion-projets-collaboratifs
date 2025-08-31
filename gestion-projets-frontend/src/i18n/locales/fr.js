// src/i18n/locales/fr.js - VERSION COMPLÈTE CORRIGÉE
export default {
  // NAVIGATION GLOBALE
  nav: {
    accueil: 'Accueil',
    connexion: 'Connexion',
    inscription: 'Inscription',
    deconnexion: 'Déconnexion',
    tableauBord: 'Tableau de Bord',
    projetsPublics: 'Projets Publics',
    monProfil: 'Mon Profil',
    administration: 'Administration',
    abonnement: 'Abonnement',
    factures: 'Factures'
  },

  // COMMUN À TOUTE L'APP
  commun: {
    charger: 'Charger',
    chargement: 'Chargement...',
    enregistrer: 'Enregistrer',
    annuler: 'Annuler',
    modifier: 'Modifier',
    supprimer: 'Supprimer',
    valider: 'Valider',
    fermer: 'Fermer',
    rechercher: 'Rechercher',
    filtrer: 'Filtrer',
    trier: 'Trier',
    suivant: 'Suivant',
    precedent: 'Précédent',
    retour: 'Retour',
    confirmer: 'Confirmer',
    oui: 'Oui',
    non: 'Non',
    aucunResultat: 'Aucun résultat trouvé',
    erreur: 'Une erreur est survenue',
    succes: 'Opération réussie',
    langue: 'Langue',
    fonctionnalites: 'Fonctionnalités',
    pretCommencer: 'Prêt à commencer ?',
    rejoindreCollabPro: 'Rejoignez CollabPro et commencez à gérer vos projets collaboratifs dès maintenant.',
    droitsReserves: 'Tous droits réservés'
  },

  // F1: INSCRIPTION
  inscription: {
    titre: 'Créer un compte',
    sousTitre: 'Rejoignez notre plateforme collaborative',
    prenom: 'Prénom',
    nom: 'Nom',
    email: 'Adresse e-mail',
    motDePasse: 'Mot de passe',
    confirmerMotDePasse: 'Confirmer le mot de passe',
    typeCompte: 'Type de compte',
    choisirRole: 'Choisissez votre rôle',
    accepterCGU: 'J\'accepte les conditions d\'utilisation',
    creerCompte: 'Créer mon compte',
    dejaCompte: 'Vous avez déjà un compte ?',
    seConnecter: 'Se connecter',
    description: 'Créer un compte utilisateur avec nom, prénom, email et mot de passe.',
    languePreferee: 'Langue préférée'
  },

  // F2: CONNEXION
  connexion: {
    titre: 'Connexion',
    sousTitre: 'Accédez à votre espace collaboratif',
    email: 'Adresse e-mail',
    motDePasse: 'Mot de passe',
    seConnecter: 'Se connecter',
    motDePasseOublie: 'Mot de passe oublié ?',
    pasDeCompte: 'Pas encore de compte ?',
    sInscrire: 'S\'inscrire',
    resterConnecte: 'Rester connecté'
  },

  // MOT DE PASSE OUBLIÉ
  motDePasseOublie: {
    titre: 'Mot de passe oublié',
    sousTitre: 'Nous vous enverrons un lien de réinitialisation',
    email: 'Votre adresse e-mail',
    envoyer: 'Envoyer le lien',
    retourConnexion: 'Retour à la connexion',
    emailEnvoye: 'E-mail de réinitialisation envoyé'
  },

  // F3: PROJETS PUBLICS
  projetsPublics: {
    titre: 'Projets Publics',
    sousTitre: 'Découvrez les projets ouverts à tous',
    aucunProjet: 'Aucun projet public disponible',
    voirDetails: 'Voir les détails',
    dateCreation: 'Créé le',
    creePar: 'Créé par',
    statut: 'Statut',
    description: 'Consulter la liste des projets publics même sans compte.'
  },

  // F6: GESTION PROJETS (Chef de Projet)
  projets: {
    titre: 'Mes Projets',
    nouveau: 'Nouveau Projet',
    nom: 'Nom du projet',
    description: 'Description',
    statut: 'Statut',
    dateCreation: 'Date de création',
    membres: 'Membres',
    taches: 'Tâches',
    creer: 'Créer le projet',
    modifier: 'Modifier le projet',
    supprimer: 'Supprimer le projet',
    archiver: 'Archiver',
    restaurer: 'Restaurer',
    gestion: 'Gérer les projets',
    descriptionGestion: 'Créer, modifier ou supprimer des projets (Chef de Projet uniquement).',
    statuts: {
      actif: 'Actif',
      suspendu: 'Suspendu',
      termine: 'Terminé',
      annule: 'Annulé'
    }
  },

  // F7: GESTION TÂCHES
  taches: {
    titre: 'Gestion des Tâches',
    nouvelle: 'Nouvelle Tâche',
    nom: 'Nom de la tâche',
    description: 'Description',
    assigne: 'Assigné à',
    priorite: 'Priorité',
    echeance: 'Échéance',
    statut: 'Statut',
    creer: 'Créer la tâche',
    modifier: 'Modifier la tâche',
    terminer: 'Marquer comme terminée',
    gestion: 'Gérer les tâches',
    descriptionGestion: 'Créer, assigner ou modifier des tâches au sein des projets.',
    statuts: {
      brouillon: 'Brouillon',
      enAttente: 'En attente de validation',
      termine: 'Terminé',
      annule: 'Annulé'
    },
    priorites: {
      basse: 'Basse',
      moyenne: 'Moyenne',
      haute: 'Haute',
      critique: 'Critique'
    }
  },

  // F8: GESTION MEMBRES (Chef de Projet)
  membres: {
    titre: 'Membres du Projet',
    ajouter: 'Ajouter un membre',
    email: 'E-mail du membre',
    role: 'Rôle dans le projet',
    inviter: 'Envoyer l\'invitation',
    retirer: 'Retirer du projet',
    changerRole: 'Changer le rôle',
    invitationEnvoyee: 'Invitation envoyée',
    ajout: 'Ajouter des membres',
    descriptionAjout: 'Inviter et gérer les membres des projets (Chef de Projet).',
    roles: {
      membre: 'Membre',
      moderateur: 'Modérateur',
      admin: 'Administrateur'
    }
  },

  // F9: COLLABORATION TEMPS RÉEL
  collaboration: {
    chat: 'Discussion',
    ecrireMessage: 'Écrire un message...',
    envoyer: 'Envoyer',
    enLigne: 'En ligne',
    horsLigne: 'Hors ligne',
    tape: 'est en train d\'écrire...',
    fichier: 'Fichier',
    partager: 'Partager',
    tempsReel: 'Chat temps réel',
    descriptionTempsReel: 'Discussion instantanée dans les projets pour les membres connectés.'
  },

  // F10: ABONNEMENTS ET PAIEMENTS
  abonnement: {
    titre: 'Abonnement Premium',
    sousTitre: 'Devenez Chef de Projet',
    planPremium: 'Plan Premium',
    prix: '10€/mois',
    fonctionnalites: 'Fonctionnalités incluses',
    souscrire: 'Souscrire maintenant',
    gerer: 'Gérer mon abonnement',
    annuler: 'Annuler l\'abonnement',
    renouveler: 'Renouveler',
    statut: 'Statut de l\'abonnement',
    prochainPaiement: 'Prochain paiement',
    paymentSuccess: 'Paiement réussi !',
    paymentCancel: 'Paiement annulé',
    paiements: 'Paiements Stripe',
    descriptionPaiements: 'Souscrire à un abonnement mensuel sécurisé via Stripe.'
  },

  // F11: FACTURES
  factures: {
    titre: 'Mes Factures',
    numero: 'Numéro',
    date: 'Date',
    montant: 'Montant',
    statut: 'Statut',
    telecharger: 'Télécharger',
    generer: 'Générer la facture',
    aucuneFacture: 'Aucune facture disponible',
    pdf: 'Factures PDF',
    descriptionPdf: 'Génération automatique de facture PDF pour chaque paiement.',
    statuts: {
      payee: 'Payée',
      enAttente: 'En attente',
      echouee: 'Échouée'
    }
  },

  // F12: COMMENTAIRES
  commentaires: {
    titre: 'Commentaires',
    ajouter: 'Ajouter un commentaire',
    ecrire: 'Écrivez votre commentaire...',
    publier: 'Publier',
    modifier: 'Modifier',
    supprimer: 'Supprimer',
    repondre: 'Répondre',
    aucunCommentaire: 'Aucun commentaire'
  },

  // F13: NOTIFICATIONS
  notifications: {
    titre: 'Notifications',
    nouvelle: 'Nouvelle notification',
    marquerLue: 'Marquer comme lue',
    marquerToutesLues: 'Marquer toutes comme lues',
    aucuneNotification: 'Aucune notification',
    types: {
      tache: 'Nouvelle tâche assignée',
      projet: 'Nouveau projet',
      commentaire: 'Nouveau commentaire',
      membre: 'Nouveau membre ajouté'
    }
  },

  // TABLEAUX DE BORD SPÉCIFIQUES
  tableauBord: {
    // Chef de Projet
    chefProjet: {
      titre: 'Tableau de Bord Chef de Projet',
      mesProjets: 'Mes Projets',
      mesEquipes: 'Mes Équipes',
      tachesEnCours: 'Tâches en cours',
      notifications: 'Notifications récentes'
    },
    // Membre
    membre: {
      titre: 'Tableau de Bord Membre',
      mesTaches: 'Mes Tâches',
      projetsParticipes: 'Projets auxquels je participe',
      activiteRecente: 'Activité récente'
    },
    // Administrateur
    admin: {
      titre: 'Tableau de Bord Administrateur',
      utilisateurs: 'Gestion des Utilisateurs',
      abonnements: 'Supervision des Abonnements',
      statistiques: 'Statistiques de la Plateforme',
      maintenance: 'Maintenance'
    }
  },

  // RÔLES UTILISATEURS
  roles: {
    visiteur: 'Visiteur',
    membre: 'Membre',
    chefProjet: 'Chef de Projet',
    administrateur: 'Administrateur'
  },

  // F4 & F5: PROFIL UTILISATEUR
  profil: {
    titre: 'Mon Profil',
    informations: 'Informations personnelles',
    modifier: 'Modifier mon profil',
    changerMotDePasse: 'Changer le mot de passe',
    ancienMotDePasse: 'Ancien mot de passe',
    nouveauMotDePasse: 'Nouveau mot de passe',
    confirmerNouveauMotDePasse: 'Confirmer le nouveau mot de passe',
    photo: 'Photo de profil',
    preferences: 'Préférences',
    langue: 'Langue préférée'
  },

  // CONDITIONS D'UTILISATION & RGPD
  conditions: {
    titre: 'Conditions d\'Utilisation',
    sousTitre: 'Politique de Confidentialité et RGPD',
    accepter: 'Accepter les conditions',
    derniereMiseAJour: 'Dernière mise à jour'
  },

  // VALIDATION & ERREURS
  validation: {
    obligatoire: 'Ce champ est obligatoire',
    emailInvalide: 'Format d\'e-mail invalide',
    motDePasseTropCourt: 'Le mot de passe doit contenir au moins 6 caractères',
    motDePassesDifferents: 'Les mots de passe ne correspondent pas',
    emailDejaUtilise: 'Cette adresse e-mail est déjà utilisée'
  }
}
