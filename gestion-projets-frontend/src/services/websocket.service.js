import { Client } from '@stomp/stompjs'

/**  Codes d'erreur i18n */
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

/**  Messages de succès i18n */
const SUCCESS_MESSAGES = {
  CONNECTED: 'websocket.success.connected',
  SUBSCRIBED: 'websocket.success.subscribed',
  MESSAGE_SENT: 'websocket.success.messageSent',
  JOIN_MESSAGE_SENT: 'websocket.success.joinMessageSent',
  NOTIFICATION_SENT: 'websocket.success.notificationSent',
  DISCONNECTED: 'websocket.success.disconnected'
}

/**  Base API (selon ton .env) */
const RAW_BASE = import.meta.env?.VITE_API_URL ?? ''
const API_BASE = (RAW_BASE === '/api' ? '' : RAW_BASE).replace(/\/$/, '')
const apiUrl = `${API_BASE}/api`

class WebSocketService {
  constructor() {
    this.client = null
    this.connected = false
    this.subscriptions = new Map()
  }

  /**  Connexion WebSocket sécurisée */
  connect(token) {
    try {
      this.client = new Client({
        brokerURL: 'ws://localhost:8080/ws-native',
        connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
        debug: str => console.log('[STOMP]', str),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      })

      this.client.onConnect = frame => {
        console.log(' WebSocket connecté', frame)
        this.connected = true
      }

      this.client.onStompError = frame => {
        console.error(' Erreur STOMP:', frame)
      }

      this.client.activate()
      return { success: true, messageKey: SUCCESS_MESSAGES.CONNECTED }
    } catch (e) {
      console.error('Erreur connexion WS:', e)
      return { success: false, errorCode: ERROR_CODES.CONNECTION_FAILED }
    }
  }

  /**  Abonnement à un projet spécifique */
  subscribeToProject(projectId, callback) {
    try {
      if (!this.connected) {
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      }
      if (!projectId) {
        return { success: false, errorCode: ERROR_CODES.INVALID_PROJECT_ID }
      }

      const destination = `/topic/projet/${projectId}`
      const subscription = this.client.subscribe(destination, msg => {
        const data = JSON.parse(msg.body)
        callback(data)
      })

      this.subscriptions.set(projectId, subscription)
      return { success: true, messageKey: SUCCESS_MESSAGES.SUBSCRIBED }
    } catch (e) {
      console.error('Erreur abonnement:', e)
      return { success: false, errorCode: ERROR_CODES.SUBSCRIPTION_FAILED }
    }
  }

  /**  Abonnement générique à un topic (pour notifications F13) */
  subscribe(topic, callback) {
    try {
      if (!this.client || !this.connected) {
        console.warn(' WebSocket non connecté, attente...')
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      }

      console.log(`[F13] Abonnement au topic: ${topic}`)

      const subscription = this.client.subscribe(topic, (message) => {
        try {
          const payload = JSON.parse(message.body)
          callback(payload)
        } catch (error) {
          console.error('[F13] Erreur parsing message:', error)
        }
      })

      this.subscriptions.set(topic, subscription)
      return { success: true, messageKey: SUCCESS_MESSAGES.SUBSCRIBED }
    } catch (e) {
      console.error('[F13] Erreur abonnement topic:', e)
      return { success: false, errorCode: ERROR_CODES.SUBSCRIPTION_FAILED }
    }
  }

  /**  Envoi d'un message texte */
  sendMessage(projectId, content) {
    try {
      if (!this.connected) {
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      }
      if (!projectId || !content) {
        return { success: false, errorCode: ERROR_CODES.INVALID_MESSAGE_CONTENT }
      }

      this.client.publish({
        destination: '/app/message.send',
        body: JSON.stringify({
          contenu: content,
          projetId: projectId,
          type: 'TEXT'
        })
      })
      return { success: true, messageKey: SUCCESS_MESSAGES.MESSAGE_SENT }
    } catch (e) {
      console.error('Erreur envoi message:', e)
      return { success: false, errorCode: ERROR_CODES.MESSAGE_SEND_FAILED }
    }
  }

  /** Envoi du message "Utilisateur rejoint le projet" */
  sendJoinMessage(projectId, userName) {
    try {
      if (!this.connected) {
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      }
      if (!projectId || !userName) {
        return { success: false, errorCode: ERROR_CODES.INVALID_MESSAGE_CONTENT }
      }

      this.client.publish({
        destination: '/app/message.send',
        body: JSON.stringify({
          contenu: `${userName} a rejoint le projet`,
          projetId: projectId,
          type: 'SYSTEM'
        })
      })
      return { success: true, messageKey: SUCCESS_MESSAGES.JOIN_MESSAGE_SENT }
    } catch (e) {
      console.error('Erreur envoi join:', e)
      return { success: false, errorCode: ERROR_CODES.JOIN_MESSAGE_FAILED }
    }
  }

  /**  Envoi d'une notification temps réel */
  sendNotification(projectId, content) {
    try {
      if (!this.connected) {
        return { success: false, errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED }
      }
      if (!projectId || !content) {
        return { success: false, errorCode: ERROR_CODES.INVALID_MESSAGE_CONTENT }
      }

      this.client.publish({
        destination: '/app/message.send',
        body: JSON.stringify({
          contenu: content,
          projetId: projectId,
          type: 'NOTIFICATION'
        })
      })
      return { success: true, messageKey: SUCCESS_MESSAGES.NOTIFICATION_SENT }
    } catch (e) {
      console.error('Erreur envoi notif:', e)
      return { success: false, errorCode: ERROR_CODES.NOTIFICATION_SEND_FAILED }
    }
  }

  /**  Récupération de l'historique des messages */
  async getHistorique(projectId) {
    try {
      if (!projectId) {
        return { success: false, errorCode: ERROR_CODES.INVALID_PROJECT_ID }
      }

      const token = localStorage.getItem('token') || ''
      const response = await fetch(`${apiUrl}/messages/projet/${projectId}`, {
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { Authorization: `Bearer ${token}` } : {})
        }
      })

      if (!response.ok) {
        console.warn(' HTTP', response.status, '→ fallback local')
        return {
          success: true,
          data: [
            {
              id: 1,
              contenu: 'Bienvenue dans le projet !',
              dateEnvoi: new Date().toISOString(),
              type: 'SYSTEM',
              utilisateurNom: 'Système',
              utilisateurEmail: ''
            }
          ]
        }
      }

      const data = await response.json()
      return { success: true, data }
    } catch (e) {
      console.error('Erreur historique:', e)
      return {
        success: false,
        errorCode: ERROR_CODES.NETWORK_ERROR,
        message: 'Erreur réseau ou serveur'
      }
    }
  }

  disconnect() {
    try {
      if (this.client) {
        this.subscriptions.clear()
        this.client.deactivate()
        this.connected = false
      }
      return { success: true, messageKey: SUCCESS_MESSAGES.DISCONNECTED }
    } catch (e) {
      console.error('Erreur déconnexion:', e)
      return { success: false, errorCode: ERROR_CODES.DISCONNECT_FAILED }
    }
  }
}

export default new WebSocketService()
