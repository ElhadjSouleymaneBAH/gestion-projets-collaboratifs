<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <!-- Header de navigation  -->
    <AppHeader v-if="showHeader" />

    <!-- Contenu principal -->
    <main class="min-h-screen">
      <RouterView />
    </main>

    <!-- Footer (optionnel) -->
    <AppFooter v-if="showFooter" />
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
// import AppHeader from '@/components/AppHeader.vue'
// import AppFooter from '@/components/AppFooter.vue'

export default {
  name: 'App',
  // components: {
  //   AppHeader,
  //   AppFooter
  // },
  setup() {
    const route = useRoute()

    // Pages où on ne veut pas afficher header/footer
    const noLayoutPages = ['login', 'register', 'forgot-password']

    const showHeader = computed(() => {
      return !noLayoutPages.includes(route.name)
    })

    const showFooter = computed(() => {
      return !noLayoutPages.includes(route.name)
    })

    return {
      showHeader,
      showFooter
    }
  }
}
</script>

<style>

#app {
  font-family: 'Inter', Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
