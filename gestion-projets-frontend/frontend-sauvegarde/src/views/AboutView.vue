<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation simple -->
    <nav class="bg-white shadow-sm border-b">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <router-link to="/" class="flex items-center space-x-3">
            <img src="/logo-collabpro.png" alt="COLLABPRO" class="h-8 w-8">
            <span class="text-xl font-bold text-blue-600">COLLABPRO</span>
          </router-link>
          <router-link to="/" class="text-blue-600 hover:text-blue-800 font-medium">
            ← Retour à l'accueil
          </router-link>
        </div>
      </div>
    </nav>

    <!-- Contenu principal -->
    <div class="max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="text-center mb-16">
        <img src="/logo-collabpro.png" alt="COLLABPRO" class="h-20 w-20 mx-auto mb-6">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">À propos de COLLABPRO</h1>
        <p class="text-xl text-gray-600">La collaboration agile, simplifiée.</p>
      </div>

      <!-- Mission -->
      <section class="mb-16">
        <div class="card-collabpro">
          <h2 class="text-2xl font-bold text-gray-900 mb-4">Notre Mission</h2>
          <p class="text-lg text-gray-700 leading-relaxed">
            COLLABPRO est une plateforme de gestion de projets collaboratifs spécialement conçue pour les
            <strong>PME et les freelances</strong>. Notre mission est de simplifier la gestion de projets
            et de faciliter la collaboration en temps réel entre les équipes, où qu'elles soient.
          </p>
        </div>
      </section>

      <!-- Fonctionnalités principales -->
      <section class="mb-16">
        <h2 class="text-2xl font-bold text-gray-900 mb-8 text-center">Fonctionnalités Principales</h2>
        <div class="grid md:grid-cols-2 gap-8">
          <!-- F6 : Gestion de projets -->
          <div class="card-collabpro">
            <div class="flex items-center mb-4">
              <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900">Gestion de Projets</h3>
            </div>
            <p class="text-gray-600">Créez, organisez et supervisez vos projets avec des outils intuitifs adaptés aux besoins des PME.</p>
          </div>

          <!-- F7 : Gestion de tâches -->
          <div class="card-collabpro">
            <div class="flex items-center mb-4">
              <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900">Gestion de Tâches</h3>
            </div>
            <p class="text-gray-600">Assignez, suivez et complétez vos tâches avec un système de workflow adaptatif.</p>
          </div>

          <!-- F8 : Collaboration équipe -->
          <div class="card-collabpro">
            <div class="flex items-center mb-4">
              <div class="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900">Collaboration d'Équipe</h3>
            </div>
            <p class="text-gray-600">Ajoutez des membres à vos projets et collaborez efficacement en temps réel.</p>
          </div>

          <!-- F9 : Temps réel -->
          <div class="card-collabpro">
            <div class="flex items-center mb-4">
              <div class="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900">Temps Réel</h3>
            </div>
            <p class="text-gray-600">Recevez des notifications instantanées et voyez les mises à jour en direct.</p>
          </div>
        </div>
      </section>

      <!-- Technologies -->
      <section class="mb-16">
        <div class="card-collabpro">
          <h2 class="text-2xl font-bold text-gray-900 mb-4">Technologies Utilisées</h2>
          <div class="grid md:grid-cols-2 gap-6">
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">Backend</h3>
              <ul class="text-gray-600 space-y-1">
                <li>• Spring Boot (Java 21)</li>
                <li>• API REST sécurisée</li>
                <li>• Base de données MySQL</li>
                <li>• Authentification JWT</li>
              </ul>
            </div>
            <div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">Frontend</h3>
              <ul class="text-gray-600 space-y-1">
                <li>• Vue.js 3 avec Vite</li>
                <li>• Interface responsive</li>
                <li>• Support multilingue (FR/EN)</li>
                <li>• Paiements sécurisés Stripe</li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      <!-- Conformité -->
      <section class="mb-16">
        <div class="card-collabpro">
          <h2 class="text-2xl font-bold text-gray-900 mb-4">Sécurité et Conformité</h2>
          <div class="grid md:grid-cols-3 gap-6">
            <div class="text-center">
              <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
                <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
                </svg>
              </div>
              <h3 class="font-semibold text-gray-900">RGPD</h3>
              <p class="text-sm text-gray-600">Conforme au règlement européen sur la protection des données</p>
            </div>
            <div class="text-center">
              <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
                <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                </svg>
              </div>
              <h3 class="font-semibold text-gray-900">Sécurité</h3>
              <p class="text-sm text-gray-600">Authentification sécurisée et données chiffrées</p>
            </div>
            <div class="text-center">
              <div class="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-3">
                <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"/>
                </svg>
              </div>
              <h3 class="font-semibold text-gray-900">Paiements</h3>
              <p class="text-sm text-gray-600">Transactions sécurisées via Stripe</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Contact -->
      <section class="text-center">
        <div class="card-collabpro">
          <h2 class="text-2xl font-bold text-gray-900 mb-4">Développé par</h2>
          <p class="text-lg text-gray-700 mb-6">
            <strong>ElhadjSouleymaneBAH</strong><br>
            Dans le cadre d'un Travail de Fin d'Études (TFE)<br>
            Institut des Carrières Commerciales (ICC)
          </p>
          <div class="flex justify-center space-x-4">
            <router-link to="/" class="btn-collabpro">
              Découvrir COLLABPRO
            </router-link>
            <a href="mailto:support@collabpro.com" class="border-2 border-blue-600 text-blue-600 font-semibold px-6 py-3 rounded-lg hover:bg-blue-50 transition-colors">
              Nous contacter
            </a>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
// Page statique, pas de logique particulière
</script>
