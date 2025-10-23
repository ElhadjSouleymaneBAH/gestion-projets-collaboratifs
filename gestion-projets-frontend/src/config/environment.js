const getApiUrl = () => {
  // En production
  if (import.meta.env.PROD) {
    return import.meta.env.VITE_API_URL || '/api'
  }
  // En développement (local)
  return '/api'
}

const getApiTarget = () => {
  // En production, on utilise la variable d'environnement
  if (import.meta.env.PROD) {
    return import.meta.env.VITE_API_TARGET
  }
  // En développement, on utilise localhost
  return import.meta.env.VITE_API_TARGET || 'http://localhost:8080'
}

export const config = {
  API_BASE_URL: getApiUrl(),
  API_TARGET: getApiTarget(),

  STRIPE_PUBLIC_KEY: 'pk_test_votre_cle_publique_stripe',
  APP_NAME: 'CollabPro',
  VERSION: '1.0.0',
}

export const endpoints = {
  auth: '/auth',
  users: '/utilisateurs',
  projects: '/projets',
  tasks: '/taches',
  stripe: '/stripe',
  factures: '/factures',
}
