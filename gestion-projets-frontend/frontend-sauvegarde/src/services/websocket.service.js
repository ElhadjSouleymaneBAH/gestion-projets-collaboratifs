import { Client } from '@stomp/stompjs'

class WebSocketService {
  constructor() {
    this.client = null
    this.connected = false
    this.subscriptions = new Map()
  }

  connect(token) {
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
  }

  subscribeToProject(projectId, callback) {
    if (!this.connected) {
      console.warn('WebSocket non connecté')
      return
    }

    const destination = `/topic/projet/${projectId}`
    console.log('Abonnement à:', destination)

    const subscription = this.client.subscribe(destination, message => {
      const messageData = JSON.parse(message.body)
      console.log('Message reçu:', messageData)
      callback(messageData)
    })

    this.subscriptions.set(projectId, subscription)
  }

  sendMessage(projectId, content) {
    if (!this.connected) {
      console.warn('WebSocket non connecté')
      return
    }

    this.client.publish({
      destination: '/app/message.send',
      body: JSON.stringify({
        contenu: content,
        projetId: projectId,
        type: 'TEXT'
      })
    })
  }

  sendJoinMessage(projectId, userName) {
    if (!this.connected) {
      console.warn('WebSocket non connecté')
      return
    }

    this.client.publish({
      destination: '/app/message.send',
      body: JSON.stringify({
        contenu: `${userName} a rejoint le projet`,
        projetId: projectId,
        type: 'SYSTEM'
      })
    })
  }

  sendNotification(projectId, content) {
    if (!this.connected) {
      console.warn('WebSocket non connecté')
      return
    }

    this.client.publish({
      destination: '/app/message.send',
      body: JSON.stringify({
        contenu: content,
        projetId: projectId,
        type: 'NOTIFICATION'
      })
    })
  }

  disconnect() {
    if (this.client) {
      this.subscriptions.clear()
      this.client.deactivate()
      this.connected = false
      console.log('WebSocket déconnecté')
    }
  }
}

export default new WebSocketService()
