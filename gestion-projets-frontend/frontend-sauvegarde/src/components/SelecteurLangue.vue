<template>
  <div class="language-switcher">
    <select v-model="currentLanguage" @change="changeLanguage" class="form-select form-select-sm">
      <option v-for="lang in availableLanguages" :key="lang.code" :value="lang.code">
        {{ lang.flag }} {{ lang.name }}
      </option>
    </select>
  </div>
</template>

<script>
// F12 - Service de traduction STRICTEMENT Français/Anglais
class TranslationService {
  constructor() {
    this.currentLang = localStorage.getItem('language') || 'fr'

    // SEULEMENT FRANÇAIS ET ANGLAIS selon cahier des charges
    this.translations = {
      fr: {
        // Interface générale
        'welcome': 'Bienvenue',
        'dashboard': 'Tableau de bord',
        'projects': 'Mes projets',
        'tasks': 'Mes tâches',
        'profile': 'Mon profil',
        'logout': 'Se déconnecter',
        'teams': 'Gestion équipes',
        'invoices': 'Mes factures',
        'users': 'Utilisateurs',
        'system': 'Système',
        'user': 'Utilisateur',
        'message': 'Message',
        'actions': 'Actions',

        // Rôles
        'role_admin': 'Administrateur',
        'role_chef': 'Chef de Projet',
        'role_member': 'Membre',
        'role_visitor': 'Visiteur',
        'managers': 'Chefs',
        'members': 'Membres',

        // Actions
        'create': 'Créer',
        'edit': 'Modifier',
        'delete': 'Supprimer',
        'save': 'Sauvegarder',
        'cancel': 'Annuler',
        'add': 'Ajouter',
        'invite': 'Inviter',
        'search': 'Rechercher',
        'send': 'Envoyer',
        'download': 'Télécharger',
        'view': 'Voir',
        'manage': 'Gérer',
        'update': 'Mettre à jour',
        'close': 'Fermer',
        'open': 'Ouvrir',
        'remove': 'Retirer',

        // États
        'active': 'Actif',
        'inactive': 'Inactif',
        'completed': 'Terminé',
        'pending': 'En attente',
        'loading': 'Chargement...',
        'success': 'Succès !',
        'error': 'Erreur',
        'confirm': 'Êtes-vous sûr ?',
        'no_results': 'Aucun résultat trouvé',
        'required_field': 'Champ requis',
        'processing': 'Traitement...',
        'searching': 'Recherche en cours',
        'optional': 'optionnel',
        'added': 'Ajouté',
        'total': 'Total',

        // Navigation
        'home': 'Accueil',
        'back': 'Retour',
        'next': 'Suivant',
        'previous': 'Précédent',

        // F1/F2 - Inscription/Connexion
        'login': 'Se connecter',
        'register': 'S\'inscrire',
        'password': 'Mot de passe',
        'confirm_password': 'Confirmer le mot de passe',
        'remember_me': 'Se souvenir de moi',
        'forgot_password': 'Mot de passe oublié ?',
        'no_account': 'Pas encore de compte ?',
        'already_account': 'Déjà inscrit ?',
        'create_account': 'Créer un compte',
        'account_type': 'Type de compte',
        'accept_terms': 'J\'accepte les conditions d\'utilisation',
        'login_success': 'Connexion réussie ! Redirection...',
        'register_success': 'Compte créé avec succès !',

        // F3 - Projets publics
        'public_projects': 'Projets publics',
        'view_details': 'Voir les détails',
        'read_only': 'Lecture seule',
        'join_collabpro': 'Rejoignez CollabPro !',
        'consultation_mode': 'Mode consultation',
        'limited_access': 'Accès limité',

        // F4/F5 - Profil
        'first_name': 'Prénom',
        'last_name': 'Nom',
        'email': 'Email',
        'update_profile': 'Mettre à jour le profil',
        'profile_updated': 'Profil mis à jour avec succès',
        'personal_info': 'Informations personnelles',
        'view_profile': 'Voir le profil',

        // F6 - Projets
        'new_project': 'Nouveau projet',
        'project_title': 'Titre du projet',
        'project_description': 'Description',
        'project_members': 'Membres du projet',
        'project_status': 'Statut',
        'my_projects': 'Mes projets',
        'project_created': 'Projet créé avec succès !',
        'no_projects_yet': 'Aucun projet pour le moment',
        'create_first_project': 'Créez votre premier projet collaboratif',
        'members_count': 'membre(s)',
        'project_discussion': 'Discussion du projet',
        'unknown_project': 'Projet inconnu',

        // F7 - Tâches
        'assign_task': 'Assigner une tâche',
        'task_title': 'Titre de la tâche',
        'task_description': 'Description de la tâche',
        'assign_to': 'Assigner à',
        'my_tasks': 'Mes tâches',
        'tasks_assigned': 'Tâches assignées',
        'tasks_completed': 'Tâches terminées',
        'task_assigned': 'Tâche assignée avec succès',
        'no_tasks': 'Aucune tâche assignée pour le moment',

        // F8 - Membres
        'add_member': 'Ajouter un membre',
        'invite_member': 'Inviter des membres',
        'invite_new_members': 'Inviter de nouveaux membres',
        'member_email': 'Email du membre',
        'search_users': 'Rechercher des utilisateurs',
        'search_users_placeholder': 'Rechercher un utilisateur par nom ou email...',
        'team_management': 'Gestion des équipes',
        'manage_team_collaboration': 'Gérer la collaboration d\'équipe',
        'manage_project_members': 'Gérer les membres du projet',
        'member_added': 'a été ajouté au projet avec succès !',
        'invitation_sent': 'Invitation envoyée avec succès',
        'user_not_found': 'Utilisateur non trouvé',
        'user_not_exist': 'n\'existe pas',
        'must_create_account': 'Il doit créer un compte d\'abord',
        'user_must_exist': 'L\'utilisateur doit avoir un compte existant',
        'already_member': 'Ce membre fait déjà partie de ce projet',
        'recent_invitations': 'Invitations récentes',
        'join_project_message': 'Rejoignez-nous sur ce projet...',
        'invitation_error': 'Erreur lors de l\'invitation',
        'no_members_yet': 'Aucun membre pour le moment',
        'invite_first_member': 'Invitez votre premier membre à collaborer',
        'joined_on': 'Rejoint le',
        'remove_from_project': 'Retirer du projet',
        'remove_member': 'Retirer le membre',
        'change_role': 'Changer le rôle',
        'confirm_remove_member': 'Êtes-vous sûr de vouloir retirer ce membre ?',
        'confirm_remove_member_detailed': 'Retirer {name} du projet ?',
        'member_removed_successfully': 'Membre retiré avec succès',
        'member_added_successfully': 'Membre ajouté avec succès',
        'error_loading_members': 'Erreur lors du chargement des membres',
        'error_removing_member': 'Erreur lors de la suppression du membre',
        'only_project_manager_can_invite': 'Seul le Chef de Projet peut inviter de nouveaux membres',
        'total_members': 'Total membres',
        'active_members': 'Membres actifs',

        // F9 - Chat
        'chat': 'Chat',
        'real_time_chat': 'Chat temps réel',
        'send_message': 'Envoyer un message',
        'send_notification': 'Envoyer notification',
        'type_message': 'Tapez votre message...',
        'no_messages': 'Aucun message pour le moment',
        'start_conversation': 'Commencez la conversation',
        'unread_messages': 'Messages non lus',
        'message_sent': 'Message envoyé',
        'back_to_projects': 'Retour aux projets',
        'online_members': 'Membres en ligne',

        // F10 - Abonnement
        'premium_subscription': 'Abonnement Premium',
        'premium_required': 'Abonnement Premium requis',
        'subscribe': 'S\'abonner',
        'monthly': 'par mois',
        'subscription_active': 'Abonnement actif',
        'payment_info': 'Informations de paiement',
        'payment_details': 'Détails du paiement',
        'payment_method': 'Méthode de paiement',
        'card_number': 'Numéro de carte',
        'card': 'Carte',
        'card_name': 'Nom sur la carte',
        'expiry_date': 'Date d\'expiration',
        'cvv': 'CVV',
        'billing_address': 'Adresse de facturation',
        'subtotal': 'Sous-total',
        'vat': 'TVA',
        'subscribe_now': 'S\'abonner maintenant',
        'payment_processing': 'Traitement du paiement...',
        'payment_success': 'Paiement réussi ! Votre abonnement Premium est maintenant actif.',
        'payment_error': 'Erreur lors du traitement du paiement',
        'invalid_card': 'Numéro de carte invalide',
        'invalid_cvv': 'CVV invalide',
        'accept_conditions': 'Veuillez accepter les conditions d\'abonnement',

        // F11 - Factures
        'invoice': 'Facture',
        'invoice_view': 'Voir la facture',
        'invoice_number': 'Numéro de facture',
        'amount': 'Montant',
        'amount_ht': 'Montant HT',
        'date': 'Date',
        'due_date': 'Date d\'échéance',
        'period': 'Période',
        'status': 'Statut',
        'paid': 'Payée',
        'pending_payment': 'En attente de paiement',
        'download_pdf': 'Télécharger PDF',
        'send_by_email': 'Envoyer par email',
        'invoice_sent': 'Facture envoyée par email',
        'no_invoices': 'Aucune facture pour le moment',
        'invoice_details': 'Détails de la facture',
        'billing_info': 'Informations de facturation',
        'transaction_id': 'ID de transaction',
        'description': 'Description',
        'total_ttc': 'Total TTC',
        'bill_to': 'Facturé à',
        'premium_features_access': 'Accès aux fonctionnalités premium',
        'payment_terms': 'Conditions de paiement',
        'automatic_debit': 'Prélèvement automatique',
        'auto_renewal': 'Renouvellement automatique',
        'cancellation_possible': 'Résiliation possible',
        'thank_you_trust': 'Merci pour votre confiance',
        'collaborative_solution': 'Solution collaborative',
        'invoice_status_GENEREE': 'Générée',
        'invoice_status_ECHEC': 'Échec',
        'invoice_status_EN_ATTENTE': 'En attente',

        // Admin
        'admin_dashboard': 'Tableau de bord Administrateur',
        'user_management': 'Gestion des utilisateurs',
        'system_info': 'Informations système',
        'database': 'Base de données',
        'general_stats': 'Statistiques générales',
        'last_connection': 'Dernière connexion',
        'user_deleted': 'Utilisateur supprimé',
        'project_deleted': 'Projet supprimé',

        // CGU
        'terms_of_service': 'Conditions Générales d\'Utilisation',
        'privacy_policy': 'Politique de Confidentialité',
        'terms_title': 'Conditions d\'Utilisation - CollabPro',
        'terms_intro': 'En utilisant CollabPro, vous acceptez les conditions suivantes :',
        'terms_service': 'Description du service',
        'terms_service_desc': 'CollabPro est une plateforme collaborative de gestion de projets permettant aux équipes de travailler ensemble efficacement.',
        'terms_user_obligations': 'Obligations de l\'utilisateur',
        'terms_user_desc': 'L\'utilisateur s\'engage à utiliser le service de manière responsable et à respecter les droits des autres utilisateurs.',
        'terms_payment': 'Conditions de paiement',
        'terms_payment_desc': 'L\'abonnement Premium est facturé mensuellement. Le paiement est traité de manière sécurisée via Stripe.',
        'terms_data': 'Protection des données',
        'terms_data_desc': 'Vos données personnelles sont protégées conformément au RGPD. Nous ne partageons pas vos informations avec des tiers.',
        'terms_liability': 'Limitation de responsabilité',
        'terms_liability_desc': 'CollabPro ne peut être tenu responsable des dommages indirects résultant de l\'utilisation du service.',
        'terms_contact': 'Pour toute question : contact@collabpro.be',
        'terms_updated': 'Dernière mise à jour : Janvier 2025'
      },

      en: {
        // Interface générale
        'welcome': 'Welcome',
        'dashboard': 'Dashboard',
        'projects': 'My projects',
        'tasks': 'My tasks',
        'profile': 'My profile',
        'logout': 'Logout',
        'teams': 'Team management',
        'invoices': 'My invoices',
        'users': 'Users',
        'system': 'System',
        'user': 'User',
        'message': 'Message',
        'actions': 'Actions',

        // Rôles
        'role_admin': 'Administrator',
        'role_chef': 'Project Manager',
        'role_member': 'Member',
        'role_visitor': 'Visitor',
        'managers': 'Managers',
        'members': 'Members',

        // Actions
        'create': 'Create',
        'edit': 'Edit',
        'delete': 'Delete',
        'save': 'Save',
        'cancel': 'Cancel',
        'add': 'Add',
        'invite': 'Invite',
        'search': 'Search',
        'send': 'Send',
        'download': 'Download',
        'view': 'View',
        'manage': 'Manage',
        'update': 'Update',
        'close': 'Close',
        'open': 'Open',
        'remove': 'Remove',

        // États
        'active': 'Active',
        'inactive': 'Inactive',
        'completed': 'Completed',
        'pending': 'Pending',
        'loading': 'Loading...',
        'success': 'Success!',
        'error': 'Error',
        'confirm': 'Are you sure?',
        'no_results': 'No results found',
        'required_field': 'Required field',
        'processing': 'Processing...',
        'searching': 'Searching',
        'optional': 'optional',
        'added': 'Added',
        'total': 'Total',

        // Navigation
        'home': 'Home',
        'back': 'Back',
        'next': 'Next',
        'previous': 'Previous',

        // F1/F2 - Inscription/Connexion
        'login': 'Login',
        'register': 'Register',
        'password': 'Password',
        'confirm_password': 'Confirm password',
        'remember_me': 'Remember me',
        'forgot_password': 'Forgot password?',
        'no_account': 'Don\'t have an account?',
        'already_account': 'Already registered?',
        'create_account': 'Create account',
        'account_type': 'Account type',
        'accept_terms': 'I accept the terms of service',
        'login_success': 'Login successful! Redirecting...',
        'register_success': 'Account created successfully!',

        // F3 - Projets publics
        'public_projects': 'Public projects',
        'view_details': 'View details',
        'read_only': 'Read only',
        'join_collabpro': 'Join CollabPro!',
        'consultation_mode': 'Consultation mode',
        'limited_access': 'Limited access',

        // F4/F5 - Profil
        'first_name': 'First name',
        'last_name': 'Last name',
        'email': 'Email',
        'update_profile': 'Update profile',
        'profile_updated': 'Profile updated successfully',
        'personal_info': 'Personal information',
        'view_profile': 'View profile',

        // F6 - Projets
        'new_project': 'New project',
        'project_title': 'Project title',
        'project_description': 'Description',
        'project_members': 'Project members',
        'project_status': 'Status',
        'my_projects': 'My projects',
        'project_created': 'Project created successfully!',
        'no_projects_yet': 'No projects yet',
        'create_first_project': 'Create your first collaborative project',
        'members_count': 'member(s)',
        'project_discussion': 'Project discussion',
        'unknown_project': 'Unknown project',

        // F7 - Tâches
        'assign_task': 'Assign task',
        'task_title': 'Task title',
        'task_description': 'Task description',
        'assign_to': 'Assign to',
        'my_tasks': 'My tasks',
        'tasks_assigned': 'Assigned tasks',
        'tasks_completed': 'Completed tasks',
        'task_assigned': 'Task assigned successfully',
        'no_tasks': 'No tasks assigned yet',

        // F8 - Membres
        'add_member': 'Add member',
        'invite_member': 'Invite members',
        'invite_new_members': 'Invite new members',
        'member_email': 'Member email',
        'search_users': 'Search users',
        'search_users_placeholder': 'Search user by name or email...',
        'team_management': 'Team management',
        'manage_team_collaboration': 'Manage team collaboration',
        'manage_project_members': 'Manage project members',
        'member_added': 'added to project successfully!',
        'invitation_sent': 'Invitation sent successfully',
        'user_not_found': 'User not found',
        'user_not_exist': 'does not exist',
        'must_create_account': 'They must create an account first',
        'user_must_exist': 'User must have an existing account',
        'already_member': 'This member is already part of this project',
        'recent_invitations': 'Recent invitations',
        'join_project_message': 'Join us on this project...',
        'invitation_error': 'Error during invitation',
        'no_members_yet': 'No members yet',
        'invite_first_member': 'Invite your first member to collaborate',
        'joined_on': 'Joined on',
        'remove_from_project': 'Remove from project',
        'remove_member': 'Remove member',
        'change_role': 'Change role',
        'confirm_remove_member': 'Are you sure you want to remove this member?',
        'confirm_remove_member_detailed': 'Remove {name} from project?',
        'member_removed_successfully': 'Member removed successfully',
        'member_added_successfully': 'Member added successfully',
        'error_loading_members': 'Error loading members',
        'error_removing_member': 'Error removing member',
        'only_project_manager_can_invite': 'Only Project Manager can invite new members',
        'total_members': 'Total members',
        'active_members': 'Active members',

        // F9 - Chat
        'chat': 'Chat',
        'real_time_chat': 'Real-time chat',
        'send_message': 'Send message',
        'send_notification': 'Send notification',
        'type_message': 'Type your message...',
        'no_messages': 'No messages yet',
        'start_conversation': 'Start the conversation',
        'unread_messages': 'Unread messages',
        'message_sent': 'Message sent',
        'back_to_projects': 'Back to projects',
        'online_members': 'Online members',

        // F10 - Abonnement
        'premium_subscription': 'Premium Subscription',
        'premium_required': 'Premium subscription required',
        'subscribe': 'Subscribe',
        'monthly': 'per month',
        'subscription_active': 'Subscription active',
        'payment_info': 'Payment information',
        'payment_details': 'Payment details',
        'payment_method': 'Payment method',
        'card_number': 'Card number',
        'card': 'Card',
        'card_name': 'Name on card',
        'expiry_date': 'Expiry date',
        'cvv': 'CVV',
        'billing_address': 'Billing address',
        'subtotal': 'Subtotal',
        'vat': 'VAT',
        'subscribe_now': 'Subscribe now',
        'payment_processing': 'Processing payment...',
        'payment_success': 'Payment successful! Your Premium subscription is now active.',
        'payment_error': 'Error processing payment',
        'invalid_card': 'Invalid card number',
        'invalid_cvv': 'Invalid CVV',
        'accept_conditions': 'Please accept the subscription terms',

        // F11 - Factures
        'invoice': 'Invoice',
        'invoice_view': 'View invoice',
        'invoice_number': 'Invoice number',
        'amount': 'Amount',
        'amount_ht': 'Amount excl. VAT',
        'date': 'Date',
        'due_date': 'Due date',
        'period': 'Period',
        'status': 'Status',
        'paid': 'Paid',
        'pending_payment': 'Pending payment',
        'download_pdf': 'Download PDF',
        'send_by_email': 'Send by email',
        'invoice_sent': 'Invoice sent by email',
        'no_invoices': 'No invoices yet',
        'invoice_details': 'Invoice details',
        'billing_info': 'Billing information',
        'transaction_id': 'Transaction ID',
        'description': 'Description',
        'total_ttc': 'Total incl. VAT',
        'bill_to': 'Bill to',
        'premium_features_access': 'Access to premium features',
        'payment_terms': 'Payment terms',
        'automatic_debit': 'Automatic debit',
        'auto_renewal': 'Auto renewal',
        'cancellation_possible': 'Cancellation possible',
        'thank_you_trust': 'Thank you for your trust',
        'collaborative_solution': 'Collaborative solution',
        'invoice_status_GENEREE': 'Generated',
        'invoice_status_ECHEC': 'Failed',
        'invoice_status_EN_ATTENTE': 'Pending',

        // Admin
        'admin_dashboard': 'Administrator Dashboard',
        'user_management': 'User management',
        'system_info': 'System information',
        'database': 'Database',
        'general_stats': 'General statistics',
        'last_connection': 'Last connection',
        'user_deleted': 'User deleted',
        'project_deleted': 'Project deleted',

        // CGU
        'terms_of_service': 'Terms of Service',
        'privacy_policy': 'Privacy Policy',
        'terms_title': 'Terms of Service - CollabPro',
        'terms_intro': 'By using CollabPro, you agree to the following terms:',
        'terms_service': 'Service description',
        'terms_service_desc': 'CollabPro is a collaborative project management platform that enables teams to work together efficiently.',
        'terms_user_obligations': 'User obligations',
        'terms_user_desc': 'Users commit to using the service responsibly and respecting the rights of other users.',
        'terms_payment': 'Payment terms',
        'terms_payment_desc': 'Premium subscription is billed monthly. Payment is processed securely via Stripe.',
        'terms_data': 'Data protection',
        'terms_data_desc': 'Your personal data is protected in accordance with GDPR. We do not share your information with third parties.',
        'terms_liability': 'Limitation of liability',
        'terms_liability_desc': 'CollabPro cannot be held responsible for indirect damages resulting from the use of the service.',
        'terms_contact': 'For any questions: contact@collabpro.be',
        'terms_updated': 'Last updated: January 2025'
      }
    }

    console.log(`🌍 TranslationService initialisé en ${this.currentLang}`)
  }

  // Obtenir traduction
  t(key) {
    const translation = this.translations[this.currentLang]?.[key]
    if (!translation) {
      console.warn(`🌍 Traduction manquante: "${key}" pour langue "${this.currentLang}"`)
      return key
    }
    return translation
  }

  // Changer langue - STRICTEMENT FR/EN selon cahier des charges
  setLanguage(lang) {
    if (lang !== 'fr' && lang !== 'en') {
      console.error(`❌ Langue non supportée: "${lang}". Utilisez "fr" ou "en" uniquement.`)
      return false
    }

    console.log(`🌍 Changement de langue: ${this.currentLang} → ${lang}`)
    this.currentLang = lang
    localStorage.setItem('language', lang)

    // Émettre événement pour rafraîchir l'interface
    window.dispatchEvent(new CustomEvent('languageChanged', { detail: lang }))
    return true
  }

  getCurrentLanguage() {
    return this.currentLang
  }

  // Obtenir langues disponibles selon cahier des charges
  getAvailableLanguages() {
    return [
      { code: 'fr', name: 'Français', flag: '🇫🇷' },
      { code: 'en', name: 'English', flag: '🇬🇧' }
    ]
  }
}

// Instance globale
const i18n = new TranslationService()

export default {
  name: 'SelecteurLangue',
  data() {
    return {
      currentLanguage: i18n.getCurrentLanguage(),
      availableLanguages: i18n.getAvailableLanguages()
    }
  },

  mounted() {
    // Écouter les changements de langue
    window.addEventListener('languageChanged', this.onLanguageChanged)
  },

  beforeUnmount() {
    window.removeEventListener('languageChanged', this.onLanguageChanged)
  },

  methods: {
    changeLanguage() {
      const success = i18n.setLanguage(this.currentLanguage)
      if (success) {
        // Petit feedback visuel
        this.$emit('language-changed', this.currentLanguage)

        // Message de confirmation
        const message = this.currentLanguage === 'fr'
          ? 'Interface basculée en Français 🇫🇷'
          : 'Interface switched to English 🇬🇧'

        console.log(`🌍 ${message}`)

        // Notification toast
        this.showLanguageNotification(message)
      }
    },

    onLanguageChanged(event) {
      this.currentLanguage = event.detail
      // Forcer le re-render si nécessaire
      this.$forceUpdate()
    },

    showLanguageNotification(message) {
      // Créer une notification temporaire
      const notification = document.createElement('div')
      notification.className = 'language-notification'
      notification.textContent = message
      notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #007bff;
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        z-index: 9999;
        box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
        animation: slideInRight 0.3s ease-out;
      `

      document.body.appendChild(notification)

      // Supprimer après 3 secondes
      setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease-in'
        setTimeout(() => {
          if (document.body.contains(notification)) {
            document.body.removeChild(notification)
          }
        }, 300)
      }, 3000)
    }
  }
}

// Plugin Vue global pour traductions F12
export const translationPlugin = {
  install(app) {
    // Méthode globale $t pour toutes les vues
    app.config.globalProperties.$t = (key) => i18n.t(key)

    // Provide pour injection dans composants
    app.provide('i18n', i18n)

    // Mixin global pour réactivité aux changements de langue
    app.mixin({
      data() {
        return {
          currentLang: i18n.getCurrentLanguage()
        }
      },

      mounted() {
        window.addEventListener('languageChanged', this.updateLanguage)
      },

      beforeUnmount() {
        window.removeEventListener('languageChanged', this.updateLanguage)
      },

      methods: {
        updateLanguage(event) {
          this.currentLang = event.detail
          this.$forceUpdate()
        }
      }
    })

    console.log('🌍 Plugin F12 multilingue installé - Version nettoyée')
  }
}

// Export du service pour utilisation directe
export { i18n }
</script>

<style scoped>
.language-switcher {
  display: inline-block;
}

.form-select {
  min-width: 140px;
  font-size: 0.875rem;
  border: 2px solid #007bff;
  border-radius: 8px;
  padding: 8px 12px;
  background: white;
  color: #007bff;
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: pointer;
}

.form-select:focus {
  border-color: #0056b3;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
  outline: none;
}

.form-select:hover {
  background: rgba(0, 123, 255, 0.05);
  transform: translateY(-1px);
}

/* Animations pour notifications */
@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideOutRight {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(100%);
    opacity: 0;
  }
}

/* Responsive */
@media (max-width: 768px) {
  .form-select {
    min-width: 120px;
    font-size: 0.8rem;
    padding: 6px 10px;
  }
}
</style>
