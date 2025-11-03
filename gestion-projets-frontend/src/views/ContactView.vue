<template>
  <div class="min-vh-100 bg-light">
    <!-- Header simple -->
    <header class="bg-white shadow-sm py-3">
      <div class="container">
        <div class="d-flex justify-content-between align-items-center">
          <router-link to="/" class="d-flex align-items-center text-decoration-none text-dark">
            <img src="/logo-collabpro.png" alt="CollabPro" class="me-3" style="height: 48px; width: 48px;">
            <h1 class="h4 fw-bold text-collabpro mb-0">{{ $t('app.nom') }}</h1>
          </router-link>
          <router-link to="/" class="btn btn-outline-primary">
            <i class="fas fa-arrow-left me-2"></i>{{ $t('contact.retourAccueil') }}
          </router-link>
        </div>
      </div>
    </header>

    <!-- Contenu Contact Simplifié -->
    <section class="py-5">
      <div class="container">
        <div class="row justify-content-center">
          <div class="col-lg-6 col-md-8">
            <!-- En-tête -->
            <div class="text-center mb-5">
              <img src="/logo-collabpro.png" alt="CollabPro" class="mb-4" style="height: 100px; width: 100px;">
              <h1 class="h2 fw-bold text-collabpro mb-3">{{ $t('contact.titre') }}</h1>
              <p class="text-muted">
                {{ $t('contact.description') }}
              </p>
            </div>

            <!-- Carte Informations de contact -->
            <div class="card border-0 shadow-sm p-4">
              <h3 class="h5 fw-bold text-dark mb-4">{{ $t('contact.informations') }}</h3>

              <!-- CollabPro Solutions -->
              <div class="contact-info-item mb-4">
                <div class="d-flex align-items-start">
                  <div class="contact-icon-wrapper me-3">
                    <i class="fas fa-building text-primary"></i>
                  </div>
                  <div>
                    <h5 class="fw-bold mb-2">{{ $t('contact.entreprise') }}</h5>
                    <p class="text-muted mb-0">{{ $t('contact.adresse.rue') }}</p>
                    <p class="text-muted mb-0">{{ $t('contact.adresse.ville') }}</p>
                  </div>
                </div>
              </div>

              <!-- Email -->
              <div class="contact-info-item mb-4">
                <div class="d-flex align-items-start">
                  <div class="contact-icon-wrapper me-3">
                    <i class="fas fa-envelope text-primary"></i>
                  </div>
                  <div>
                    <h5 class="fw-bold mb-2">{{ $t('contact.email.titre') }}</h5>
                    <a href="mailto:contact@collabpro.be" class="text-collabpro text-decoration-none">
                      {{ $t('contact.email.adresse') }}
                    </a>
                  </div>
                </div>
              </div>

              <!-- Numéro TVA -->
              <div class="contact-info-item">
                <div class="d-flex align-items-start">
                  <div class="contact-icon-wrapper me-3">
                    <i class="fas fa-file-invoice text-primary"></i>
                  </div>
                  <div>
                    <h5 class="fw-bold mb-2">{{ $t('contact.tva.titre') }}</h5>
                    <p class="text-muted mb-0">{{ $t('contact.tva.numero') }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'ContactView'
}
</script>

<style scoped>
.text-collabpro {
  color: #007bff;
}

.contact-icon-wrapper {
  width: 45px;
  height: 45px;
  background: #e7f3ff;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.contact-icon-wrapper i {
  font-size: 1.25rem;
}

.contact-info-item {
  transition: transform 0.3s ease;
}

.contact-info-item:hover {
  transform: translateX(5px);
}

.card {
  border-radius: 12px;
}
</style>
