<!-- src/views/AProposView.vue -->
<template>
  <div class="min-vh-100 bg-light">
    <!-- Navigation -->
    <nav class="navbar navbar-light bg-white border-bottom shadow-sm">
      <div class="container d-flex justify-content-between align-items-center">
        <router-link to="/" class="d-flex align-items-center text-decoration-none">
          <img src="/logo-collabpro.png" alt="COLLABPRO" class="me-2" style="height: 40px;">
          <span class="fs-4 fw-bold text-primary">COLLABPRO</span>
        </router-link>
        <router-link to="/" class="text-primary fw-semibold text-decoration-none">
          ← {{ $t('apropos.retourAccueil') }}
        </router-link>
      </div>
    </nav>

    <!-- Contenu principal -->
    <div class="container py-5">
      <!-- En-tête -->
      <div class="text-center mb-5">
        <img src="/logo-collabpro.png" alt="COLLABPRO" class="mb-3" style="height: 80px;">
        <h1 class="fw-bold text-dark mb-3">{{ $t('apropos.titre') }}</h1>
        <p class="fs-5 text-secondary">{{ $t('apropos.slogan') }}</p>
      </div>

      <!-- Mission -->
      <section class="mb-5">
        <div class="card border-0 shadow-sm p-4">
          <h2 class="h4 fw-bold text-dark mb-3">{{ $t('apropos.mission.titre') }}</h2>
          <p class="fs-6 text-secondary mb-0">
            {{ $t('apropos.mission.description1') }}
            <strong>{{ $t('apropos.mission.cible') }}</strong>{{ $t('apropos.mission.description2') }}
          </p>
        </div>
      </section>

      <!-- Fonctionnalités principales -->
      <section class="mb-5">
        <h2 class="h4 fw-bold text-center text-dark mb-4">{{ $t('apropos.fonctionnalites.titre') }}</h2>
        <div class="row g-4">
          <!-- Gestion de projets -->
          <div class="col-md-6">
            <div class="card border-0 shadow-sm p-4 h-100">
              <div class="d-flex align-items-center mb-3">
                <i class="bi bi-kanban fs-2 text-primary me-3"></i>
                <h3 class="h6 fw-semibold text-dark mb-0">{{ $t('apropos.fonctionnalites.gestionProjets.titre') }}</h3>
              </div>
              <p class="text-secondary small">{{ $t('apropos.fonctionnalites.gestionProjets.description') }}</p>
            </div>
          </div>

          <!-- Gestion de tâches -->
          <div class="col-md-6">
            <div class="card border-0 shadow-sm p-4 h-100">
              <div class="d-flex align-items-center mb-3">
                <i class="bi bi-check2-square fs-2 text-success me-3"></i>
                <h3 class="h6 fw-semibold text-dark mb-0">{{ $t('apropos.fonctionnalites.gestionTaches.titre') }}</h3>
              </div>
              <p class="text-secondary small">{{ $t('apropos.fonctionnalites.gestionTaches.description') }}</p>
            </div>
          </div>

          <!-- Collaboration équipe -->
          <div class="col-md-6">
            <div class="card border-0 shadow-sm p-4 h-100">
              <div class="d-flex align-items-center mb-3">
                <i class="bi bi-people fs-2 text-purple me-3"></i>
                <h3 class="h6 fw-semibold text-dark mb-0">{{ $t('apropos.fonctionnalites.collaborationEquipe.titre') }}</h3>
              </div>
              <p class="text-secondary small">{{ $t('apropos.fonctionnalites.collaborationEquipe.description') }}</p>
            </div>
          </div>

          <!-- Collaboration en temps réel -->
          <div class="col-md-6">
            <div class="card border-0 shadow-sm p-4 h-100">
              <div class="d-flex align-items-center mb-3">
                <i class="bi bi-lightning-charge fs-2 text-warning me-3"></i>
                <h3 class="h6 fw-semibold text-dark mb-0">{{ $t('apropos.fonctionnalites.tempsReel.titre') }}</h3>
              </div>
              <p class="text-secondary small">{{ $t('apropos.fonctionnalites.tempsReel.description') }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Technologies -->
      <section class="mb-5">
        <div class="card border-0 shadow-sm p-4">
          <h2 class="h4 fw-bold text-dark mb-3">{{ $t('apropos.technologies.titre') }}</h2>
          <div class="row">
            <div class="col-md-6">
              <h3 class="h6 fw-semibold text-dark mb-2">{{ $t('apropos.technologies.backend.titre') }}</h3>
              <ul class="list-unstyled text-secondary small mb-0">
                <li>• {{ $t('apropos.technologies.backend.springBoot') }}</li>
                <li>• {{ $t('apropos.technologies.backend.apiRest') }}</li>
                <li>• {{ $t('apropos.technologies.backend.mysql') }}</li>
                <li>• {{ $t('apropos.technologies.backend.jwt') }}</li>
              </ul>
            </div>
            <div class="col-md-6">
              <h3 class="h6 fw-semibold text-dark mb-2">{{ $t('apropos.technologies.frontend.titre') }}</h3>
              <ul class="list-unstyled text-secondary small mb-0">
                <li>• {{ $t('apropos.technologies.frontend.vue') }}</li>
                <li>• {{ $t('apropos.technologies.frontend.responsive') }}</li>
                <li>• {{ $t('apropos.technologies.frontend.multilingue') }}</li>
                <li>• {{ $t('apropos.technologies.frontend.stripe') }}</li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      <!-- Conformité -->
      <section class="mb-5">
        <div class="card border-0 shadow-sm p-4">
          <h2 class="h4 fw-bold text-dark mb-4">{{ $t('apropos.conformite.titre') }}</h2>
          <div class="row text-center">
            <div class="col-md-4">
              <i class="bi bi-shield-check fs-1 text-success mb-2"></i>
              <h3 class="h6 fw-semibold text-dark">{{ $t('apropos.conformite.rgpd.titre') }}</h3>
              <p class="small text-secondary">{{ $t('apropos.conformite.rgpd.description') }}</p>
            </div>
            <div class="col-md-4">
              <i class="bi bi-lock-fill fs-1 text-primary mb-2"></i>
              <h3 class="h6 fw-semibold text-dark">{{ $t('apropos.conformite.securite.titre') }}</h3>
              <p class="small text-secondary">{{ $t('apropos.conformite.securite.description') }}</p>
            </div>
            <div class="col-md-4">
              <i class="bi bi-credit-card-2-front fs-1 text-purple mb-2"></i>
              <h3 class="h6 fw-semibold text-dark">{{ $t('apropos.conformite.paiements.titre') }}</h3>
              <p class="small text-secondary">{{ $t('apropos.conformite.paiements.description') }}</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>
