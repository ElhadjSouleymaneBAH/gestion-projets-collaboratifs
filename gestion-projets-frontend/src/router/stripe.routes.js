// src/router/stripe.routes.js
export default [
  {
    path: '/abonnement-premium',
    name: 'abonnement-premium',
    component: () => import('@/views/AbonnementView.vue'),
    meta: {
      requiresAuth: true,
      title: 'Abonnement Premium - CollabPro'
    }
  },
  {
    path: '/abonnement/success',
    name: 'abonnement-success',
    component: () => import('@/views/StripeSuccessView.vue'),
    meta: {
      requiresAuth: true,
      title: 'Paiement Réussi - CollabPro'
    }
  },
  {
    path: '/abonnement/cancel',
    name: 'abonnement-cancel',
    component: () => import('@/views/StripeCancelView.vue'),
    meta: {
      requiresAuth: true,
      title: 'Paiement Annulé - CollabPro'
    }
  },
  {
    path: '/stripe/checkout',
    name: 'stripe-checkout',
    component: () => import('@/components/StripeCheckout.vue'),
    meta: {
      requiresAuth: true,
      title: 'Processus de Paiement'
    }
  },
  {
    path: '/factures',
    name: 'factures',
    component: () => import('@/views/FacturesView.vue'),
    meta: {
      requiresAuth: true,
      requiresChefOrAdmin: true,
      title: 'Mes Factures - CollabPro'
    }
  }
]
