// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

// Import direct pour l'accueil
import AccueilView from '@/views/AccueilView.vue'

// Imports lazy pour le reste
const ConnexionView = () => import('@/views/ConnexionView.vue')
const InscriptionView = () => import('@/views/InscriptionView.vue')
const MotDePasseOublieView = () => import('@/views/MotDePasseOublieView.vue')
const TableauDeBordAdministrateurView = () => import('@/views/TableauDeBordAdminView.vue')
const TableauBordChefProjetView = () => import('@/views/TableauBordChefProjetView.vue')
const TableauBordMembreView = () => import('@/views/TableauBordMembreView.vue')
const ProjetsPublicsView = () => import('@/views/ProjetsPublicsView.vue')
const ConditionsView = () => import('@/views/ConditionsView.vue')
const PolitiqueConfidentialiteView = () => import('@/views/PolitiqueConfidentialiteView.vue') // Nouvelle route
const ProjetDetailView = () => import('@/views/ProjetDetailView.vue')
const FacturesView = () => import('@/views/FacturesView.vue')
const AbonnementView = () => import('@/views/AbonnementView.vue')
const StripeSuccessView = () => import('@/views/StripeSuccessView.vue')
const StripeCancelView = () => import('@/views/StripeCancelView.vue')

// Petit helper pour lire l'utilisateur sans crasher si c'est "null"
function getSafeUser() {
  try {
    return JSON.parse(localStorage.getItem('user')) || {}
  } catch {
    return {}
  }
}

const routes = [
  { path: '/', name: 'accueil', component: AccueilView },

  { path: '/connexion', name: 'connexion', component: ConnexionView },
  { path: '/inscription', name: 'inscription', component: InscriptionView },
  { path: '/mot-de-passe-oublie', name: 'mot-de-passe-oublie', component: MotDePasseOublieView },

  // ROUTES ABONNEMENT STRIPE
  {
    path: '/abonnement-premium',
    name: 'abonnement-premium',
    component: AbonnementView,
    meta: { requiresAuth: true }
  },
  {
    path: '/abonnement/success',
    name: 'abonnement-success',
    component: StripeSuccessView,
    meta: { requiresAuth: true }
  },
  {
    path: '/abonnement/cancel',
    name: 'abonnement-cancel',
    component: StripeCancelView,
    meta: { requiresAuth: true }
  },

  // F11 : Factures (Chef de projet ou Admin)
  {
    path: '/factures',
    name: 'factures',
    component: FacturesView,
    meta: { requiresAuth: true, requiresChefOrAdmin: true }
  },
  { path: '/test-facture', redirect: '/factures' },

  // Redirection tableau de bord en fonction du rôle
  {
    path: '/tableau-de-bord',
    name: 'tableau-de-bord',
    redirect: () => {
      const user = getSafeUser()
      if (user.role === 'CHEF_PROJET') return '/tableau-bord-chef-projet'
      if (user.role === 'MEMBRE') return '/tableau-bord-membre'
      if (user.role === 'ADMINISTRATEUR') return '/admin/tableau-de-bord'
      return '/connexion'
    },
    meta: { requiresAuth: true }
  },

  {
    path: '/tableau-bord-chef-projet',
    name: 'tableau-bord-chef-projet',
    component: TableauBordChefProjetView,
    meta: { requiresAuth: true }
  },
  {
    path: '/tableau-bord-membre',
    name: 'tableau-bord-membre',
    component: TableauBordMembreView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/tableau-de-bord',
    name: 'admin-tableau-de-bord',
    component: TableauDeBordAdministrateurView,
    meta: { requiresAuth: true, requiresAdmin: true }
  },

  { path: '/projets-publics', name: 'projets-publics', component: ProjetsPublicsView },
  { path: '/conditions', name: 'conditions', component: ConditionsView },

  // Politique de Confidentialité
  {
    path: '/politique-confidentialite',
    name: 'politique-confidentialite',
    component: PolitiqueConfidentialiteView
  },

  {
    path: '/projet/:id',
    name: 'projet-detail',
    component: ProjetDetailView,
    meta: { requiresAuth: true }
  },

  // Compatibilité anciennes URLs
  { path: '/login', redirect: '/connexion' },
  { path: '/register', redirect: '/inscription' },
  { path: '/privacy', redirect: '/politique-confidentialite' }, // Alias anglais
  { path: '/privacy-policy', redirect: '/politique-confidentialite' }, // Alias anglais
  { path: '/rgpd', redirect: '/politique-confidentialite' }, // Alias RGPD

  // REDIRECTIONS STRIPE SUPPLÉMENTAIRES
  { path: '/abonnement', redirect: '/abonnement-premium' },
  { path: '/subscription', redirect: '/abonnement-premium' },
  { path: '/subscribe', redirect: '/abonnement-premium' },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// GARDES GLOBALES
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = getSafeUser()

  // Auth requise ?
  if (to.meta.requiresAuth && !token) {
    return next({ path: '/connexion', query: { redirect: to.fullPath } })
  }

  // Admin only ?
  if (to.meta.requiresAdmin && user.role !== 'ADMINISTRATEUR') {
    return next('/tableau-de-bord')
  }

  // Chef de projet ou Admin ?
  if (to.meta.requiresChefOrAdmin && !['CHEF_PROJET', 'ADMINISTRATEUR'].includes(user.role)) {
    return next('/tableau-de-bord')
  }

  // PROTECTION SPÉCIALE STRIPE : Vérifie si l'utilisateur essaie d'accéder aux pages de succès sans session_id
  if (to.name === 'abonnement-success' && !to.query.session_id) {
    console.warn('Accès page succès sans session_id, redirection vers abonnement')
    return next('/abonnement-premium')
  }

  // REDIRECTION INTELLIGENTE APRÈS CONNEXION
  if (to.name === 'connexion' && token) {
    // Si l'utilisateur est déjà connecté, rediriger vers son tableau de bord
    if (user.role === 'CHEF_PROJET') return next('/tableau-bord-chef-projet')
    if (user.role === 'MEMBRE') return next('/tableau-bord-membre')
    if (user.role === 'ADMINISTRATEUR') return next('/admin/tableau-de-bord')
    return next('/tableau-de-bord')
  }

  next()
})

router.afterEach((to, from) => {
  // Log des conversions Stripe pour analytics
  if (to.name === 'abonnement-success') {
    console.log('Conversion Stripe réussie!')
    // Ici tu peux ajouter Google Analytics, etc.
    // gtag('event', 'purchase', { value: 10, currency: 'EUR' })
  }

  if (to.name === 'abonnement-cancel') {
    console.log('Paiement Stripe annulé')
    // Analytics pour paiement annulé
  }

  // Mise à jour du titre de la page
  const routeTitles = {
    'accueil': 'Gestion de Projets Collaboratifs',
    'abonnement-premium': 'Abonnement Premium - CollabPro',
    'abonnement-success': 'Paiement Réussi - Bienvenue Premium!',
    'abonnement-cancel': 'Paiement Annulé - CollabPro',
    'factures': 'Mes Factures - CollabPro',
    'tableau-bord-chef-projet': 'Tableau de Bord Chef de Projet',
    'tableau-bord-membre': 'Tableau de Bord Membre',
    'admin-tableau-de-bord': 'Administration - CollabPro',
    'projets-publics': 'Projets Publics - CollabPro',
    'connexion': 'Connexion - CollabPro',
    'inscription': 'Inscription - CollabPro',
    'conditions': 'Conditions Générales d\'Utilisation - CollabPro',
    'politique-confidentialite': 'Politique de Confidentialité - CollabPro' // Nouveau titre
  }

  document.title = routeTitles[to.name] || 'Gestion de Projets Collaboratifs'
})

export default router
