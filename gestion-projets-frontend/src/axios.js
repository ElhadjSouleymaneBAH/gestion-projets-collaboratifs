// src/axios.js
import axios from 'axios'
import i18n from './i18n'

axios.defaults.baseURL = 'http://localhost:8080'

axios.interceptors.request.use(
  config => {
    const currentLocale = i18n.global.locale.value || 'fr'
    config.headers['Accept-Language'] = currentLocale
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

export default axios
