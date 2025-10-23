<template>
  <div class="commentaires-view container py-4">
    <div class="card-collabpro shadow-sm">
      <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
        <h4 class="mb-0">
          <i class="fas fa-comments me-2"></i>{{ t('commentaires.titrePage') }}
        </h4>
        <button class="btn btn-light btn-sm" @click="retour">
          <i class="fas fa-arrow-left me-1"></i>{{ t('commun.retour') }}
        </button>
      </div>

      <div class="card-body">
        <Commentaires :projet-id="projetId" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import Commentaires from '@/components/Commentaires.vue'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const projetId = route.params.id

const retour = () => router.back()
</script>

<style scoped>
.commentaires-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8faff 0%, #e3f2fd 100%);
}
</style>
