import { Client } from '@stomp/stompjs'

/** Codes d'erreur i18n */
const ERROR_CODES = {
  CONNECTION_FAILED: 'websocket.errors.connectionFailed',
  CONNECTION_NOT_ESTABLISHED: 'websocket.errors.connectionNotEstablished',
  SUBSCRIPTION_FAILED: 'websocket.errors.subscriptionFailed',
  MESSAGE_SEND_FAILED: 'websocket.errors.messageSendFailed',
  JOIN_MESSAGE_FAILED: 'websocket.errors.joinMessageFailed',
  NOTIFICATION_SEND_FAILED: 'websocket.errors.notificationSendFailed',
  DISCONNECT_FAILED: 'websocket.errors.disconnectFailed',
  STOMP_ERROR: 'websocket.errors.stompError',
  INVALID_PROJECT_ID: 'websocket.errors.invalidProjectId',
  INVALID_MESSAGE_CONTENT: 'websocket.errors.invalidMessageContent',
  NETWORK_ERROR: 'websocket.errors.networkError'
}

/** Messages de succès i18n */
const SUCCESS_MESSAGES = {
  CONNECTED: 'websocket.success.connected',
  SUBSCRIBED: 'websocket.success.subscribed',
  MESSAGE_SENT: 'websocket.success.messageSent',
  JOIN_MESSAGE_SENT: 'websocket.success.joinMessageSent',
  NOTIFICATION_SENT: 'websocket.success.notificationSent',
  DISCONNECTED: 'websocket.success.disconnected'
}

/** Base API */
const RAW_BASE = import.meta.env?.VITE_API_URL ?? ''
const API_BASE = (RAW_BASE === '/api' ? '' : RAW_BASE).replace(/\/$/, '')
const apiUrl = `${API_BASE}/api`

class WebSocketService {
  constructor() {
    this.client = null
    this.connected = false
    this.subscriptions = new Map()
    this.forcedDisconnect = false
  }

  /** Connexion STOMP sécurisée avec JWT */
  connect(token) {
    try {
      if (this.client && this.connected) {
        return { success: true, messageKey: SUCCESS_MESSAGES.CONNECTED }
      }

      this.client = new Client({
        brokerURL: 'ws://localhost:8080/ws-native',
        connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
        debug: (str) => console.log('[STOMP]', str),
        reconnectDelay: 4000,
        heartbeatIncoming: 8000,
        heartbeatOutgoing: 8000
      })

      this.client.onConnect = () => {
        console.log('[WS] Connecté au serveur STOMP')
        this.connected = true
        this.forcedDisconnect = false
      }

      this.client.onStompError = (frame) => {
        console.error('[WS] Erreur STOMP :', frame.headers?.message || frame.body)
      }

      this.client.onWebSocketClose = () => {
        this.connected = false
        if (!this.forcedDisconnect) {
          console.warn('[WS] Connexion perdue, reconnexion...')
          const savedToken = localStorage.getItem('token')
          if (savedToken) setTimeout(() => this.connect(savedToken), 2500)
        } else {
          console.log('[WS] Fermeture volontaire.')
        }
      }

      this.client.activate()
      return { success: true, messageKey: SUCCESS_MESSAGES.CONNECTED }
    } catch (e) {
      console.error('[WS] Erreur connexion :', e)
      return { success: false, errorCode: ERROR_CODES.CONNECTION_FAILED }
    }
  }

  /**  Abonnement générique (corrige l'erreur) */
  subscribe(destination, callback) {
    try {
      if (!this.connected) {
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      }
      const sub = this.client.subscribe(destination, (msg) => {
        try {
          const data = JSON.parse(msg.body)
          callback(data)
        } catch {
          console.warn('[WS] Message non JSON reçu sur', destination, msg.body)
          callback(msg.body)
        }
      })
      this.subscriptions.set(destination, sub)
      console.log(`[WS] Abonné au topic : ${destination}`)
      return { success: true, messageKey: SUCCESS_MESSAGES.SUBSCRIBED }
    } catch (e) {
      console.error('[WS] Erreur abonnement générique :', e)
      return { success: false, errorCode: ERROR_CODES.SUBSCRIPTION_FAILED }
    }
  }

  /**  Désabonnement d'un topic */
  unsubscribe(destination) {
    try {
      const sub = this.subscriptions.get(destination)
      if (sub) {
        sub.unsubscribe()
        this.subscriptions.delete(destination)
        console.log(`[WS] Désabonné de : ${destination}`)
        return { success: true }
      }
      return { success: false, error: 'Subscription not found' }
    } catch (e) {
      console.error('[WS] Erreur unsubscribe:', e)
      return { success: false, error: e.message }
    }
  }

  /** Abonnement au chat d'un projet */
  subscribeToProject(projectId, callback) {
    try {
      if (!this.connected)
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      const dest = `/topic/projet/${projectId}`
      const sub = this.client.subscribe(dest, (msg) => callback(JSON.parse(msg.body)))
      this.subscriptions.set(`projet-${projectId}`, sub)
      return { success: true, messageKey: SUCCESS_MESSAGES.SUBSCRIBED }
    } catch (e) {
      console.error('[WS] Erreur abonnement projet :', e)
      return { success: false, errorCode: ERROR_CODES.SUBSCRIPTION_FAILED }
    }
  }

  /** Abonnement spécifique au chef de projet */
  subscribeChefProjet(chefId, callback) {
    try {
      if (!this.connected)
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      const dest = `/topic/notifications/chef-projet/${chefId}`
      const sub = this.client.subscribe(dest, (msg) => callback(JSON.parse(msg.body)))
      this.subscriptions.set(`chef-${chefId}`, sub)
      console.log(`[WS] Chef ${chefId} abonné à ses notifications`)
      return { success: true, messageKey: SUCCESS_MESSAGES.SUBSCRIBED }
    } catch (e) {
      console.error('[WS] Erreur abonnement chef projet :', e)
      return { success: false, errorCode: ERROR_CODES.SUBSCRIPTION_FAILED }
    }
  }

  // ========================================================================
  // ⭐ NOUVELLES MÉTHODES POUR LES COMMENTAIRES (F12 + F9)
  // ========================================================================

  /** ⭐ Abonnement aux commentaires d'une tâche (F12 + F9) */
  subscribeToTacheCommentaires(tacheId, callback) {
    try {
      if (!this.connected)
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      const dest = `/topic/tache/${tacheId}/commentaires`
      const sub = this.client.subscribe(dest, (msg) => callback(JSON.parse(msg.body)))
      this.subscriptions.set(`tache-commentaires-${tacheId}`, sub)
      console.log(`[WS] ✅ Abonné aux commentaires de la tâche ${tacheId}`)
      return { success: true, messageKey: SUCCESS_MESSAGES.SUBSCRIBED }
    } catch (e) {
      console.error('[WS] ❌ Erreur abonnement commentaires tâche :', e)
      return { success: false, errorCode: ERROR_CODES.SUBSCRIPTION_FAILED }
    }
  }

  /** ⭐ Abonnement aux suppressions de commentaires d'une tâche */
  subscribeToTacheCommentairesSuppression(tacheId, callback) {
    try {
      if (!this.connected)
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      const dest = `/topic/tache/${tacheId}/commentaires/supprime`
      const sub = this.client.subscribe(dest, (msg) => {
        // Le message contient juste l'ID du commentaire supprimé
        const commentaireId = typeof msg.body === 'string' ? parseInt(msg.body) : msg.body
        callback(commentaireId)
      })
      this.subscriptions.set(`tache-commentaires-suppression-${tacheId}`, sub)
      console.log(`[WS] ✅ Abonné aux suppressions de commentaires de la tâche ${tacheId}`)
      return { success: true, messageKey: SUCCESS_MESSAGES.SUBSCRIBED }
    } catch (e) {
      console.error('[WS] ❌ Erreur abonnement suppressions commentaires :', e)
      return { success: false, errorCode: ERROR_CODES.SUBSCRIPTION_FAILED }
    }
  }

  /** ⭐ Désabonnement des commentaires d'une tâche */
  unsubscribeFromTacheCommentaires(tacheId) {
    try {
      // Désabonnement des nouveaux commentaires
      const subCommentaires = this.subscriptions.get(`tache-commentaires-${tacheId}`)
      if (subCommentaires) {
        subCommentaires.unsubscribe()
        this.subscriptions.delete(`tache-commentaires-${tacheId}`)
        console.log(`[WS] Désabonné des commentaires de la tâche ${tacheId}`)
      }

      // Désabonnement des suppressions
      const subSuppressions = this.subscriptions.get(`tache-commentaires-suppression-${tacheId}`)
      if (subSuppressions) {
        subSuppressions.unsubscribe()
        this.subscriptions.delete(`tache-commentaires-suppression-${tacheId}`)
        console.log(`[WS] Désabonné des suppressions de commentaires de la tâche ${tacheId}`)
      }

      return { success: true }
    } catch (e) {
      console.error('[WS] Erreur désabonnement commentaires tâche:', e)
      return { success: false, error: e.message }
    }
  }

  // ========================================================================
  // FIN DES MÉTHODES COMMENTAIRES
  // ========================================================================

  /** ✉ Envoi d'un message normal */
  sendMessage(projectId, content) {
    try {
      if (!this.connected)
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      this.client.publish({
        destination: '/app/message.send',
        body: JSON.stringify({ contenu: content, projetId: projectId, type: 'TEXT' })
      })
      return { success: true, messageKey: SUCCESS_MESSAGES.MESSAGE_SENT }
    } catch (e) {
      console.error('[WS] Erreur envoi message:', e)
      return { success: false, errorCode: ERROR_CODES.MESSAGE_SEND_FAILED }
    }
  }

  /** Envoi notification système (optionnel) */
  sendNotification(projectId, content) {
    try {
      if (!this.connected)
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      this.client.publish({
        destination: '/app/message.send',
        body: JSON.stringify({ contenu: content, projetId: projectId, type: 'NOTIFICATION' })
      })
      return { success: true, messageKey: SUCCESS_MESSAGES.NOTIFICATION_SENT }
    } catch (e) {
      console.error('[WS] Erreur notif :', e)
      return { success: false, errorCode: ERROR_CODES.NOTIFICATION_SEND_FAILED }
    }
  }

  /** Historique du chat */
  async getHistorique(projectId) {
    try {
      const token = localStorage.getItem('token') || ''
      const res = await fetch(`${apiUrl}/messages/projet/${projectId}`, {
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` }
      })
      if (!res.ok) throw new Error('Erreur réseau')
      const data = await res.json()
      return { success: true, data }
    } catch (e) {
      console.error('[WS] Erreur historique:', e)
      return { success: false, errorCode: ERROR_CODES.NETWORK_ERROR }
    }
  }

  /** Déconnexion propre */
  disconnect() {
    try {
      if (this.client) {
        this.forcedDisconnect = true
        this.subscriptions.clear()
        this.client.deactivate()
        this.connected = false
      }
      console.log('[WS] Déconnexion propre effectuée.')
      return { success: true, messageKey: SUCCESS_MESSAGES.DISCONNECTED }
    } catch (e) {
      console.error('[WS] Erreur déconnexion:', e)
      return { success: false, errorCode: ERROR_CODES.DISCONNECT_FAILED }
    }
  }
}

export default new WebSocketService()
