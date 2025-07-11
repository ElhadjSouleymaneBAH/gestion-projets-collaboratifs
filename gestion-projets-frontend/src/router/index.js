import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'

// Imports dynamiques pour meilleure performance
const RegisterView = () => import('../views/RegisterView.vue')
const ProjetsPublicsView = () => import('../views/ProjetsPublicsView.vue')
const ConditionsView = () => import('../views/ConditionsView.vue')

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/dashboard',
    name: 'admin-dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

//  Guard d’authentification
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
