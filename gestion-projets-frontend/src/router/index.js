import { createRouter, createWebHistory } from 'vue-router'

// Import direct
import AccueilView from '@/views/AccueilView.vue'

// Lazy
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

// Helper
function getSafeUser() {
  try { return JSON.parse(localStorage.getItem('user')) || {}; }
  catch { return {}; }
}

const routes = [
  { path: '/', name: 'accueil', component: AccueilView },

  { path: '/connexion', name: 'connexion', component: ConnexionView },
  { path: '/inscription', name: 'inscription', component: InscriptionView },
  { path: '/mot-de-passe-oublie', name: 'mot-de-passe-oublie', component: MotDePasseOublieView },

  { path: '/abonnement-premium', name: 'abonnement-premium', component: AbonnementView, meta: { requiresAuth: true } },
  { path: '/abonnement/success', name: 'abonnement-success', component: StripeSuccessView, meta: { requiresAuth: true } },
  { path: '/abonnement/cancel', name: 'abonnement-cancel', component: StripeCancelView, meta: { requiresAuth: true } },

  { path: '/factures', name: 'factures', component: FacturesView, meta: { requiresAuth: true, requiresChefOrAdmin: true } },
  { path: '/test-facture', redirect: '/factures' },

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

  { path: '/tableau-bord-chef-projet', name: 'tableau-bord-chef-projet', component: TableauBordChefProjetView, meta: { requiresAuth: true } },
  { path: '/tableau-bord-membre', name: 'tableau-bord-membre', component: TableauBordMembreView, meta: { requiresAuth: true } },
  { path: '/admin/tableau-de-bord', name: 'admin-tableau-de-bord', component: TableauDeBordAdministrateurView, meta: { requiresAuth: true, requiresAdmin: true } },

  { path: '/projets-publics', name: 'projets-publics', component: ProjetsPublicsView },
  { path: '/conditions', name: 'conditions', component: ConditionsView },
  { path: '/politique-confidentialite', name: 'politique-confidentialite', component: PolitiqueConfidentialiteView },

  { path: '/projet/:id', name: 'projet-detail', component: ProjetDetailView, meta: { requiresAuth: true } },

  { path: '/login', redirect: '/connexion' },
  { path: '/register', redirect: '/inscription' },
  { path: '/privacy', redirect: '/politique-confidentialite' },
  { path: '/privacy-policy', redirect: '/politique-confidentialite' },
  { path: '/rgpd', redirect: '/politique-confidentialite' },
  { path: '/abonnement', redirect: '/abonnement-premium' },
  { path: '/subscription', redirect: '/abonnement-premium' },
  { path: '/subscribe', redirect: '/abonnement-premium' },

  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// Guards
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = getSafeUser()

  // Auto-reset du "visiteur" quand on revient à l'accueil
  if (to.name === 'accueil' && user.role === 'VISITEUR') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // Auth requise ?
  if (to.meta.requiresAuth && !token) {
    return next({ path: '/connexion', query: { redirect: to.fullPath } })
  }

  // Admin only
  if (to.meta.requiresAdmin && user.role !== 'ADMINISTRATEUR') {
    return next('/tableau-de-bord')
  }

  // Chef de projet OU Admin
  if (to.meta.requiresChefOrAdmin && !['CHEF_PROJET', 'ADMINISTRATEUR'].includes(user.role)) {
    return next('/tableau-de-bord')
  }

  // Stripe success sans session_id => redirige
  if (to.name === 'abonnement-success' && !to.query.session_id) {
    return next('/abonnement-premium')
  }

  // Accès à /connexion
  if (to.name === 'connexion') {
    // Forcer le switch si demandé
    if (to.query.switch === '1') {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      return next()
    }
    // Si connecté en VISITEUR -> on nettoie et on affiche le login
    if (token && user.role === 'VISITEUR') {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      return next()
    }
    // Token orphelin -> nettoyer et afficher le login
    if (token && (!user || !user.role)) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      return next()
    }
    // Déjà connecté normal -> tableau de bord
    if (token && user.role) {
      if (user.role === 'CHEF_PROJET') return next('/tableau-bord-chef-projet')
      if (user.role === 'MEMBRE') return next('/tableau-bord-membre')
      if (user.role === 'ADMINISTRATEUR') return next('/admin/tableau-de-bord')
      return next('/tableau-de-bord')
    }
  }

  next()
})

router.afterEach((to) => {
  const titles = {
    'accueil': 'Gestion de Projets Collaboratifs',
    'abonnement-premium': 'Abonnement Premium - CollabPro',
    'abonnement-success': 'Paiement Réussi - CollabPro',
    'abonnement-cancel': 'Paiement Annulé - CollabPro',
    'factures': 'Mes Factures - CollabPro',
    'tableau-bord-chef-projet': 'Tableau de Bord Chef de Projet',
    'tableau-bord-membre': 'Tableau de Bord Membre',
    'admin-tableau-de-bord': 'Administration - CollabPro',
    'projets-publics': 'Projets Publics - CollabPro',
    'connexion': 'Connexion - CollabPro',
    'inscription': 'Inscription - CollabPro',
    'conditions': 'Conditions Générales d\'Utilisation - CollabPro',
    'politique-confidentialite': 'Politique de Confidentialité - CollabPro'
  }
  document.title = titles[to.name] || 'Gestion de Projets Collaboratifs'
})

export default router
