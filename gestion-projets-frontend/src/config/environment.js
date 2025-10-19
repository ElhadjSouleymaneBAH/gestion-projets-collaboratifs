export const config = {
  API_BASE_URL: 'http://localhost:8080',
  STRIPE_PUBLIC_KEY: 'pk_test_votre_cle_publique_stripe',
  APP_NAME: 'CollabPro',
  VERSION: '1.0.0'
}

export const endpoints = {
  auth: '/auth',
  users: '/utilisateurs',
  projects: '/projets',
  tasks: '/taches',
  stripe: '/stripe',
  factures: '/factures'
}
