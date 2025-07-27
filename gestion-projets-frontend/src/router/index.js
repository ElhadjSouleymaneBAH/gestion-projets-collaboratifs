import {createRouter, createWebHistory} from 'vue-router'

import HomeView from '../views/HomeView.vue'

const ConnexionView = () => import('../views/ConnexionView.vue')
const InscriptionView = () => import('../views/InscriptionView.vue')
const MotDePasseOublieView = () => import('../views/MotDePasseOublieView.vue')
const TableauDeBordAdministrateurView = () => import('../views/TableauDeBordAdminView.vue')
const TableauBordChefProjetView = () => import('../views/TableauBordChefProjetView.vue')
const TableauBordMembreView = () => import('../views/TableauBordMembreView.vue')
const ProjetsPublicsView = () => import('../views/ProjetsPublicsView.vue')
const ConditionsView = () => import('../views/ConditionsView.vue')
const ProjetDetailView = () => import('../views/ProjetDetailView.vue')
const AbonnementPremiumView = () => import('../views/AbonnementPremiumView.vue')

const routes = [
  {
    path: '/',
    name: 'accueil',
    component: HomeView
  },
  {
    path: '/connexion',
    name: 'connexion',
    component: ConnexionView
  },
  {
    path: '/inscription',
    name: 'inscription',
    component: InscriptionView
  },
  {
    path: '/mot-de-passe-oublie',
    name: 'mot-de-passe-oublie',
    component: MotDePasseOublieView
  },
  {
    path: '/abonnement-premium',
    name: 'abonnement-premium',
    component: AbonnementPremiumView,
    meta: { requiresAuth: true }
  },
  // TEST FACTURE - AJOUT
  {
    path: '/test-facture',
    name: 'TestFacture',
    component: () => import('@/views/TestFactureView.vue')
  },
  // FIN AJOUT
  {
    path: '/tableau-de-bord',
    name: 'tableau-de-bord',
    redirect: () => {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (user.role === 'CHEF_PROJET') {
        return '/tableau-bord-chef-projet'
      } else if (user.role === 'MEMBRE') {
        return '/tableau-bord-membre'
      } else if (user.role === 'ADMINISTRATEUR') {
        return '/admin/tableau-de-bord'
      }
      return '/connexion'
    },
    meta: {requiresAuth: true}
  },
  {
    path: '/tableau-bord-chef-projet',
    name: 'tableau-bord-chef-projet',
    component: TableauBordChefProjetView,
    meta: {requiresAuth: true}
  },
  {
    path: '/tableau-bord-membre',
    name: 'tableau-bord-membre',
    component: TableauBordMembreView,
    meta: {requiresAuth: true}
  },
  {
    path: '/admin/tableau-de-bord',
    name: 'admin-tableau-de-bord',
    component: TableauDeBordAdministrateurView,
    meta: {requiresAuth: true, requiresAdmin: true}
  },
  {
    path: '/projets-publics',
    name: 'projets-publics',
    component: ProjetsPublicsView
  },
  {
    path: '/conditions',
    name: 'conditions',
    component: ConditionsView
  },
  {
    path: '/projet/:id',
    name: 'projet-detail',
    component: ProjetDetailView,
    meta: {requiresAuth: true}
  },
  {
    path: '/login',
    redirect: '/connexion'
  },
  {
    path: '/register',
    redirect: '/inscription'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')

  if (to.meta.requiresAuth && !token) {
    next('/connexion')
  } else if (to.meta.requiresAdmin && user.role !== 'ADMINISTRATEUR') {
    next('/tableau-de-bord')
  } else {
    next()
  }
})

export default router
