// src/axios.js
import axios from 'axios'
import i18n from './i18n'

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true,
})

//  Intercepteur pour ajouter dynamiquement la langue avant chaque requête
axiosInstance.interceptors.request.use(
  (config) => {

    const currentLocale =
      typeof i18n.global.locale === 'object'
        ? i18n.global.locale.value
        : i18n.global.locale || 'fr'

    config.headers['Accept-Language'] = currentLocale
    return config
  },
  (error) => Promise.reject(error)
)

export default axiosInstance
