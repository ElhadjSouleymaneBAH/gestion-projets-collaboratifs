// src/i18n/index.js
import { createI18n } from 'vue-i18n'
import fr from './locales/fr.js'
import en from './locales/en.js'

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('langue') || 'fr',
  fallbackLocale: 'fr',
  globalInjection: true,
  messages: {
    fr,
    en
  }
})

export default i18n
