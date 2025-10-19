import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig(({ mode }) => {

  const env = loadEnv(mode, process.cwd(), '')

  // Cible du backend pour le proxy.
  const target = env.VITE_API_TARGET || 'http://localhost:8080'

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      host: true,
      port: 5174,
      proxy: {

        '/api': {
          target,
          changeOrigin: true,
          secure: false,
        },
      },
    },
  }
})
