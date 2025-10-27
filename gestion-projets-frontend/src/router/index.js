import { createRouter, createWebHistory } from 'vue-router'

// Import direct
import AccueilView from '@/views/AccueilView.vue'

// Lazy loading
const ConnexionView = () => import('@/views/ConnexionView.vue')
const InscriptionView = () => import('@/views/InscriptionView.vue')
const MotDePasseOublieView = () => import('@/views/MotDePasseOublieView.vue')
const TableauDeBordAdministrateurView = () => import('@/views/TableauDeBordAdminView.vue')
const TableauBordChefProjetView = () => import('@/views/TableauBordChefProjetView.vue')
const TableauBordMembreView = () => import('@/views/TableauBordMembreView.vue')
const ProjetsPublicsView = () => import('@/views/ProjetsPublicsView.vue')
const ConditionsView = () => import('@/views/ConditionsView.vue')
const PolitiqueConfidentialiteView = () => import('@/views/PolitiqueConfidentialiteView.vue')
const ProjetDetailView = () => import('@/views/ProjetDetailView.vue')
const FacturesView = () => import('@/views/FacturesView.vue')
const AbonnementView = () => import('@/views/AbonnementView.vue')
const StripeSuccessView = () => import('@/views/StripeSuccessView.vue')
const StripeCancelView = () => import('@/views/StripeCancelView.vue')

// Tâches
const TacheDetailsView = () => import('@/views/TacheDetailsView.vue')
const TachesProjetView = () => import('@/views/TachesProjetView.vue')

// Helper utilisateur
function getSafeUser() {
  try {
    return JSON.parse(localStorage.getItem('user')) || {}
  } catch {
    return {}
  }
}

const routes = [
  // Pages publiques
  { path: '/', name: 'accueil', component: AccueilView },

  // Authentification
  { path: '/connexion', name: 'connexion', component: ConnexionView },
  { path: '/inscription', name: 'inscription', component: InscriptionView },
  { path: '/mot-de-passe-oublie', name: 'mot-de-passe-oublie', component: MotDePasseOublieView },

  // Abonnements
  { path: '/abonnement-premium', name: 'abonnement-premium', component: AbonnementView, meta: { requiresAuth: true } },
  { path: '/abonnement/success', name: 'abonnement-success', component: StripeSuccessView, meta: { requiresAuth: true } },
  { path: '/abonnement/cancel', name: 'abonnement-cancel', component: StripeCancelView, meta: { requiresAuth: true } },

  // Factures
  { path: '/factures', name: 'factures', component: FacturesView, meta: { requiresAuth: true } },

  // Tableau de bord
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

  // Tableaux par rôle
  { path: '/tableau-bord-chef-projet', name: 'tableau-bord-chef-projet', component: TableauBordChefProjetView, meta: { requiresAuth: true, role: 'CHEF_PROJET' } },
  { path: '/tableau-bord-membre', name: 'tableau-bord-membre', component: TableauBordMembreView, meta: { requiresAuth: true, role: 'MEMBRE' } },
  { path: '/admin/tableau-de-bord', name: 'admin-tableau-de-bord', component: TableauDeBordAdministrateurView, meta: { requiresAuth: true, requiresAdmin: true } },

  // Projets
  { path: '/projets-publics', name: 'projets-publics', component: ProjetsPublicsView },
  { path: '/projet/:id', name: 'projet-detail', component: ProjetDetailView, meta: { requiresAuth: true } },

  // Tâches
  { path: '/taches/:id', name: 'tache-details', component: TacheDetailsView, meta: { requiresAuth: true } },
  { path: '/projets/:idProjet/taches', name: 'projet-taches', component: TachesProjetView, meta: { requiresAuth: true } },

  // Légal
  { path: '/conditions', name: 'conditions', component: ConditionsView },
  { path: '/politique-confidentialite', name: 'politique-confidentialite', component: PolitiqueConfidentialiteView },

  // Redirections SEO
  { path: '/login', redirect: '/connexion' },
  { path: '/register', redirect: '/inscription' },
  { path: '/privacy', redirect: '/politique-confidentialite' },
  { path: '/privacy-policy', redirect: '/politique-confidentialite' },
  { path: '/rgpd', redirect: '/politique-confidentialite' },
  { path: '/abonnement', redirect: '/abonnement-premium' },
  { path: '/subscription', redirect: '/abonnement-premium' },
  { path: '/subscribe', redirect: '/abonnement-premium' },
  { path: '/test-facture', redirect: '/factures' },

  // Alias /projets/:id → /projet/:id
  { path: '/projets/:id', redirect: to => `/projet/${to.params.id}` },

  // Création projet
  { path: '/projets/nouveau', redirect: { path: '/tableau-bord-chef-projet', query: { newProject: '1' } } },

  // 404
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// ==================== GUARDS ====================
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = getSafeUser()

  // Nettoyage visiteur
  if (to.name === 'accueil' && user.role === 'VISITEUR') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // Auth requise
  if (to.meta.requiresAuth && !token) {
    return next({ path: '/connexion', query: { redirect: to.fullPath } })
  }

  // Admin only
  if (to.meta.requiresAdmin && user.role !== 'ADMINISTRATEUR') {
    return next('/tableau-de-bord')
  }

  // Contrôle rôle
  if (to.meta.role) {
    if (!user?.role) return next('/connexion')
    if (user.role !== to.meta.role) {
      if (user.role === 'CHEF_PROJET') return next('/tableau-bord-chef-projet')
      if (user.role === 'MEMBRE') return next('/tableau-bord-membre')
      if (user.role === 'ADMINISTRATEUR') return next('/admin/tableau-de-bord')
      return next('/connexion')
    }
  }

  // Stripe : session_id manquant
  if (to.name === 'abonnement-success' && !to.query.session_id) {
    return next('/abonnement-premium')
  }

  // Cas connexion
  if (to.name === 'connexion') {
    if (to.query.switch === '1') {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      return next()
    }
    if (token && (!user || !user.role)) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      return next()
    }
    if (token && user.role) {
      if (user.role === 'CHEF_PROJET') return next('/tableau-bord-chef-projet')
      if (user.role === 'MEMBRE') return next('/tableau-bord-membre')
      if (user.role === 'ADMINISTRATEUR') return next('/admin/tableau-de-bord')
      return next('/tableau-de-bord')
    }
  }

  next()
})

// ==================== TITRES ====================
router.afterEach((to) => {
  const titles = {
    'accueil': 'CollabPro - Gestion de Projets Collaboratifs',
    'connexion': 'Connexion - CollabPro',
    'inscription': 'Inscription - CollabPro',
    'mot-de-passe-oublie': 'Mot de passe oublié - CollabPro',
    'abonnement-premium': 'Abonnement Premium - CollabPro',
    'abonnement-success': 'Paiement Réussi - CollabPro',
    'abonnement-cancel': 'Paiement Annulé - CollabPro',
    'factures': 'Mes Factures - CollabPro',
    'tableau-bord-chef-projet': 'Tableau de Bord Chef de Projet - CollabPro',
    'tableau-bord-membre': 'Tableau de Bord Membre - CollabPro',
    'admin-tableau-de-bord': 'Administration - CollabPro',
    'projets-publics': 'Projets Publics - CollabPro',
    'projet-detail': 'Détail du Projet - CollabPro',
    'tache-details': 'Détail de la Tâche - CollabPro',
    'projet-taches': 'Tâches du Projet - CollabPro',
    'conditions': 'Conditions Générales d’Utilisation - CollabPro',
    'politique-confidentialite': 'Politique de Confidentialité - CollabPro'
  }
  document.title = titles[to.name] || 'CollabPro - Gestion de Projets Collaboratifs'
})

export default router
