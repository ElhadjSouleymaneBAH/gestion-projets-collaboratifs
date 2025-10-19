import { Client } from '@stomp/stompjs'

/**
 * Service de gestion WebSocket - Frontend Vue.js
 * Internationalisation complète FR/EN
 *
 * @author Équipe Développement
 * @version 1.0
 */

/**
 * Codes d'erreur standardisés pour l'internationalisation
 */
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

/**
 * Messages de succès standardisés
 */
const SUCCESS_MESSAGES = {
  CONNECTED: 'websocket.success.connected',
  SUBSCRIBED: 'websocket.success.subscribed',
  MESSAGE_SENT: 'websocket.success.messageSent',
  JOIN_MESSAGE_SENT: 'websocket.success.joinMessageSent',
  NOTIFICATION_SENT: 'websocket.success.notificationSent',
  DISCONNECTED: 'websocket.success.disconnected'
}



class WebSocketService {
  constructor() {
    this.client = null
    this.connected = false
    this.subscriptions = new Map()
  }

  connect(token) {
    try {
      console.log('Connexion WebSocket...')

      this.client = new Client({
        brokerURL: 'ws://localhost:8080/ws-native',
        connectHeaders: token ? {
          Authorization: `Bearer ${token}`
        } : {},
        debug: str => console.log('STOMP Debug:', str),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      })

      this.client.onConnect = frame => {
        console.log('WebSocket connecté !', frame)
        this.connected = true
      }

      this.client.onStompError = frame => {
        console.error('Erreur STOMP:', frame)
      }

      this.client.activate()

      return {
        success: true,
        messageKey: SUCCESS_MESSAGES.CONNECTED
      }
    } catch (error) {
      console.error('Erreur connexion WebSocket:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.CONNECTION_FAILED,
        message: 'Erreur lors de la connexion WebSocket'
      }
    }
  }

  subscribeToProject(projectId, callback) {
    try {
      if (!this.connected) {
        console.warn('WebSocket non connecté')
        return {
          success: false,
          errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED,
          message: 'WebSocket non connecté'
        }
      }

      if (!projectId) {
        return {
          success: false,
          errorCode: ERROR_CODES.INVALID_PROJECT_ID,
          message: 'ID de projet invalide'
        }
      }

      const destination = `/topic/projet/${projectId}`
      console.log('Abonnement à:', destination)

      const subscription = this.client.subscribe(destination, message => {
        const messageData = JSON.parse(message.body)
        console.log('Message reçu:', messageData)
        callback(messageData)
      })

      this.subscriptions.set(projectId, subscription)

      return {
        success: true,
        messageKey: SUCCESS_MESSAGES.SUBSCRIBED
      }
    } catch (error) {
      console.error('Erreur abonnement projet:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.SUBSCRIPTION_FAILED,
        message: 'Erreur lors de l\'abonnement au projet'
      }
    }
  }

  sendMessage(projectId, content) {
    try {
      if (!this.connected) {
        console.warn('WebSocket non connecté')
        return {
          success: false,
          errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED,
          message: 'WebSocket non connecté'
        }
      }

      if (!projectId || !content) {
        return {
          success: false,
          errorCode: ERROR_CODES.INVALID_MESSAGE_CONTENT,
          message: 'Contenu du message ou ID de projet manquant'
        }
      }

      this.client.publish({
        destination: '/app/message.send',
        body: JSON.stringify({
          contenu: content,
          projetId: projectId,
          type: 'TEXT'
        })
      })

      return {
        success: true,
        messageKey: SUCCESS_MESSAGES.MESSAGE_SENT
      }
    } catch (error) {
      console.error('Erreur envoi message:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.MESSAGE_SEND_FAILED,
        message: 'Erreur lors de l\'envoi du message'
      }
    }
  }

  sendJoinMessage(projectId, userName) {
    try {
      if (!this.connected) {
        console.warn('WebSocket non connecté')
        return {
          success: false,
          errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED,
          message: 'WebSocket non connecté'
        }
      }

      if (!projectId || !userName) {
        return {
          success: false,
          errorCode: ERROR_CODES.INVALID_MESSAGE_CONTENT,
          message: 'ID de projet ou nom d\'utilisateur manquant'
        }
      }

      this.client.publish({
        destination: '/app/message.send',
        body: JSON.stringify({
          contenu: `${userName} a rejoint le projet`,
          projetId: projectId,
          type: 'SYSTEM'
        })
      })

      return {
        success: true,
        messageKey: SUCCESS_MESSAGES.JOIN_MESSAGE_SENT
      }
    } catch (error) {
      console.error('Erreur envoi message de jointure:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.JOIN_MESSAGE_FAILED,
        message: 'Erreur lors de l\'envoi du message de jointure'
      }
    }
  }

  sendNotification(projectId, content) {
    try {
      if (!this.connected) {
        console.warn('WebSocket non connecté')
        return {
          success: false,
          errorCode: ERROR_CODES.CONNECTION_NOT_ESTABLISHED,
          message: 'WebSocket non connecté'
        }
      }

      if (!projectId || !content) {
        return {
          success: false,
          errorCode: ERROR_CODES.INVALID_MESSAGE_CONTENT,
          message: 'Contenu de la notification ou ID de projet manquant'
        }
      }

      this.client.publish({
        destination: '/app/message.send',
        body: JSON.stringify({
          contenu: content,
          projetId: projectId,
          type: 'NOTIFICATION'
        })
      })

      return {
        success: true,
        messageKey: SUCCESS_MESSAGES.NOTIFICATION_SENT
      }
    } catch (error) {
      console.error('Erreur envoi notification:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.NOTIFICATION_SEND_FAILED,
        message: 'Erreur lors de l\'envoi de la notification'
      }
    }
  }

  disconnect() {
    try {
      if (this.client) {
        this.subscriptions.clear()
        this.client.deactivate()
        this.connected = false
        console.log('WebSocket déconnecté')

        return {
          success: true,
          messageKey: SUCCESS_MESSAGES.DISCONNECTED
        }
      }

      return {
        success: true,
        messageKey: SUCCESS_MESSAGES.DISCONNECTED
      }
    } catch (error) {
      console.error('Erreur déconnexion WebSocket:', error)
      return {
        success: false,
        errorCode: ERROR_CODES.DISCONNECT_FAILED,
        message: 'Erreur lors de la déconnexion'
      }
    }
  }


}

export default new WebSocketService()
