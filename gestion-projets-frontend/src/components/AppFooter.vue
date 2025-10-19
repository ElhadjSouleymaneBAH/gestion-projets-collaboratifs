<template>
  <footer class="bg-dark text-white py-3">
    <div class="container d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
      <!-- Logo -->
      <router-link to="/" class="d-inline-flex align-items-center text-decoration-none text-white">
        <img src="/logo-collabpro.png" alt="CollabPro" class="me-2" style="height: 32px; width: 32px;">
        <span class="fw-bold">CollabPro</span>
      </router-link>

      <!-- Liens -->
      <div class="text-center">
        <router-link to="/conditions" class="text-white text-decoration-underline small me-3">
          {{ $t('conditions.titre') }}
        </router-link>
        <router-link to="/politique-confidentialite" class="text-white text-decoration-underline small me-3">
          {{ $t('conditions.sousTitre') }}
        </router-link>
        <span class="small text-white-50">© 2025 {{ $t('commun.droitsReserves') }}</span>
      </div>

      <!-- Sélecteur de langue -->
      <div class="d-flex align-items-center lang-wrap">
        <i class="fas fa-globe me-2"></i>
        <select
          class="form-select form-select-sm lang-select"
          v-model="currentLocale"
        >
          <option value="fr">Français</option>
          <option value="en">English</option>
        </select>
      </div>
    </div>
  </footer>
</template>

<script>
import { computed, onMounted } from 'vue'
import i18n from '@/i18n' // même instance que celle utilisée dans main.js

export default {
  name: 'AppFooter',
  setup() {
    // En Composition API (legacy: false), locale est un ref()
    const currentLocale = computed({
      get: () => i18n.global.locale.value,
      set: (val) => {
        i18n.global.locale.value = val
        localStorage.setItem('langue', val)
      }
    })

    onMounted(() => {
      const saved = localStorage.getItem('langue')
      if (saved) {
        i18n.global.locale.value = saved
      }
    })

    return { currentLocale }
  }
}
</script>

<style scoped>
.lang-wrap {
  color: #fff;
}

.lang-select {
  background: transparent;
  color: #fff;
  border: 1px solid rgba(255,255,255,.6);
  padding: .2rem .5rem;
  cursor: pointer;
}

.lang-select option {
  color: #000;
  background: #fff;
}
</style>
