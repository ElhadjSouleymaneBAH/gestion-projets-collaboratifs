import './assets/style.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import i18n from './i18n'
import axios from './axios'

const app = createApp(App)

const savedLocale = localStorage.getItem('langue')
if (savedLocale && i18n.global) {
  i18n.global.locale.value = savedLocale
}

app.use(createPinia())
app.use(i18n)
app.use(router)

app.config.globalProperties.$axios = axios

app.mount('#app')
