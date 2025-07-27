<template>
  <div class="container mt-4">
    <!-- Header Projet -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h2 class="text-primary mb-1">{{ projet.titre }}</h2>
                <p class="text-muted mb-0">{{ projet.description }}</p>
              </div>
              <div>
                <span class="badge bg-success fs-6">{{ projet.statut }}</span>
                <br>
                <small class="text-muted">Créé le {{ formatDate(projet.dateCreation) }}</small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Notifications de test -->
    <div v-if="testNotification" class="alert alert-info alert-dismissible fade show" role="alert">
      <i class="fas fa-info-circle me-2"></i>
      <strong>Test F8/F9:</strong> {{ testNotification }}
      <button type="button" class="btn-close" @click="testNotification = null"></button>
    </div>

    <div class="row">
      <!-- F8 - Gestion des membres (améliorée) -->
      <div class="col-lg-4">
        <div class="card h-100">
          <div class="card-header bg-primary text-white">
            <h5 class="mb-0">
              <i class="fas fa-users me-2"></i>
              Membres du projet ({{ membres.length }})
            </h5>
          </div>

          <div class="card-body p-0">
            <!-- Liste des membres -->
            <div class="list-group list-group-flush">
              <div v-for="membre in membres" :key="membre.id" class="list-group-item">
                <div class="d-flex align-items-center">
                  <div class="avatar-circle me-3" :style="getAvatarStyle(membre)">
                    {{ getInitials(membre.nom, membre.prenom) }}
                  </div>
                  <div class="flex-grow-1">
                    <h6 class="mb-1">{{ membre.prenom }} {{ membre.nom }}</h6>
                    <small class="text-muted">{{ membre.email }}</small>
                    <!-- Indicateur de présence en ligne -->
                    <div class="d-flex align-items-center mt-1">
                      <span
                        :class="['status-dot', membre.enLigne ? 'online' : 'offline']"
                        :title="membre.enLigne ? 'En ligne' : 'Hors ligne'"
                      ></span>
                      <small class="text-muted ms-1">
                        {{ membre.enLigne ? 'En ligne' : `Vu il y a ${membre.derniereSeen || '2h'}` }}
                      </small>
                    </div>
                  </div>
                  <div>
                    <span class="badge" :class="getRoleBadgeClass(membre.role)">
                      {{ getRoleLabel(membre.role) }}
                    </span>
                    <button
                      v-if="peutSupprimerMembre(membre)"
                      @click="supprimerMembre(membre.id)"
                      class="btn btn-sm btn-outline-danger ms-2"
                      title="Retirer du projet"
                    >
                      <i class="fas fa-times"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Ajouter un membre (Chef uniquement) -->
            <div v-if="estChefProjet" class="p-3 border-top">
              <h6 class="text-muted mb-3">
                <i class="fas fa-user-plus me-1"></i>
                Ajouter un membre
              </h6>

              <div class="input-group mb-3">
                <span class="input-group-text">
                  <i class="fas fa-search"></i>
                </span>
                <input
                  v-model="rechercheQuery"
                  @input="rechercherUtilisateurs"
                  class="form-control"
                  placeholder="Rechercher par nom ou email..."
                  :disabled="chargementRecherche"
                >
              </div>

              <!-- Indicateur de chargement -->
              <div v-if="chargementRecherche" class="text-center p-3">
                <div class="spinner-border spinner-border-sm text-primary" role="status">
                  <span class="visually-hidden">Recherche...</span>
                </div>
                <small class="text-muted ms-2">Recherche en cours...</small>
              </div>

              <!-- Résultats de recherche avec données SQL réelles -->
              <div v-else-if="utilisateursTrouves.length > 0" class="search-results">
                <small class="text-muted">{{ utilisateursTrouves.length }} utilisateur(s) trouvé(s)</small>
                <div
                  v-for="utilisateur in utilisateursTrouves"
                  :key="utilisateur.id"
                  class="search-result-item p-2 border rounded mb-2"
                  @click="ajouterMembre(utilisateur)"
                >
                  <div class="d-flex align-items-center">
                    <div class="avatar-circle-sm me-2" :style="getAvatarStyle(utilisateur)">
                      {{ getInitials(utilisateur.nom, utilisateur.prenom) }}
                    </div>
                    <div class="flex-grow-1">
                      <small class="fw-bold">{{ utilisateur.prenom }} {{ utilisateur.nom }}</small>
                      <br>
                      <small class="text-muted">{{ utilisateur.email }}</small>
                    </div>
                    <span class="badge" :class="getRoleBadgeClass(utilisateur.role)">
                      {{ getRoleLabel(utilisateur.role) }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- Message si aucun résultat -->
              <div v-else-if="rechercheQuery.length >= 2" class="text-center p-3 text-muted">
                <i class="fas fa-search fa-2x mb-2"></i>
                <p>Aucun utilisateur trouvé</p>
              </div>

              <!-- Suggestions rapides (vos données SQL) -->
              <div v-if="!rechercheQuery" class="mt-3">
                <small class="text-muted">Suggestions :</small>
                <div class="d-flex flex-wrap gap-1 mt-2">
                  <span
                    v-for="suggestion in suggestionsMembres"
                    :key="suggestion.id"
                    class="badge bg-light text-dark border suggestion-badge"
                    @click="ajouterMembreRapide(suggestion)"
                  >
                    {{ suggestion.prenom }} {{ suggestion.nom }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Message pour membres non-chef -->
            <div v-else class="p-3 border-top text-center text-muted">
              <i class="fas fa-lock fa-2x mb-2"></i>
              <p class="mb-0">Seuls les chefs de projet peuvent ajouter des membres</p>
            </div>
          </div>
        </div>
      </div>

      <!-- F9 - Chat temps réel (amélioré) -->
      <div class="col-lg-8">
        <div class="card h-100">
          <div class="card-header bg-success text-white">
            <div class="d-flex justify-content-between align-items-center">
              <h5 class="mb-0">
                <i class="fas fa-comments me-2"></i>
                Chat du projet
              </h5>
              <div class="d-flex align-items-center gap-3">
                <!-- Indicateur membres en ligne -->
                <small>
                  <i class="fas fa-users me-1"></i>
                  {{ membresEnLigne.length }}/{{ membres.length }} en ligne
                </small>
                <!-- Status connexion -->
                <span class="badge" :class="chatConnected ? 'bg-light text-success' : 'bg-warning text-dark'">
                  <i class="fas fa-circle fa-xs me-1" :class="chatConnected ? 'text-success' : 'text-warning'"></i>
                  {{ chatConnected ? 'Connecté' : 'Déconnecté' }}
                </span>
              </div>
            </div>
          </div>

          <!-- Messages -->
          <div class="card-body p-0 d-flex flex-column" style="height: 450px;">
            <div class="flex-grow-1 overflow-auto p-3" ref="messagesContainer">
              <div v-if="messages.length === 0" class="text-center text-muted py-5">
                <i class="fas fa-comments fa-3x mb-3"></i>
                <h6>Aucun message pour le moment</h6>
                <p>Commencez la conversation !</p>
              </div>

              <div v-for="message in messages" :key="message.id" class="message-item mb-3">
                <div class="d-flex" :class="{ 'justify-content-end': estMonMessage(message) }">
                  <div class="message-bubble" :class="getMessageClass(message)">
                    <div v-if="!estMonMessage(message)" class="message-header d-flex justify-content-between align-items-center mb-1">
                      <small class="fw-bold">{{ message.utilisateurNom }}</small>
                      <small class="text-muted">{{ formatTime(message.dateEnvoi) }}</small>
                    </div>
                    <div v-else class="message-header d-flex justify-content-end mb-1">
                      <small class="text-muted">{{ formatTime(message.dateEnvoi) }}</small>
                    </div>

                    <div class="message-content">{{ message.contenu }}</div>

                    <!-- Indicateurs de type de message -->
                    <div v-if="message.type !== 'TEXT'" class="message-type mt-1">
                      <span class="badge" :class="getTypeBadgeClass(message.type)">
                        <i :class="getTypeIcon(message.type)"></i>
                        {{ getTypeLabel(message.type) }}
                      </span>
                    </div>

                    <!-- Indicateur de lecture (pour vos messages) -->
                    <div v-if="estMonMessage(message)" class="message-status mt-1 text-end">
                      <small class="text-muted">
                        <i class="fas fa-check" :class="{ 'fa-check-double text-primary': message.lu }"></i>
                        {{ message.lu ? 'Lu' : 'Envoyé' }}
                      </small>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Indicateur "en train d'écrire" -->
              <div v-if="utilisateursEnTrainEcrire.length > 0" class="typing-indicator mb-3">
                <div class="d-flex align-items-center text-muted">
                  <div class="typing-dots me-2">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                  <small>
                    {{ utilisateursEnTrainEcrire.join(', ') }}
                    {{ utilisateursEnTrainEcrire.length === 1 ? 'est en train d\'écrire...' : 'sont en train d\'écrire...' }}
                  </small>
                </div>
              </div>
            </div>

            <!-- Zone de saisie -->
            <div class="border-top p-3">
              <div class="input-group">
                <input
                  v-model="nouveauMessage"
                  @keyup.enter="envoyerMessage"
                  @input="onTyping"
                  :disabled="!chatConnected"
                  class="form-control"
                  placeholder="Tapez votre message..."
                  maxlength="500"
                >
                <button
                  @click="envoyerMessage"
                  :disabled="!chatConnected || !nouveauMessage.trim()"
                  class="btn btn-success"
                  title="Envoyer (Entrée)"
                >
                  <i class="fas fa-paper-plane"></i>
                </button>
                <!-- Bouton options -->
                <div class="btn-group" role="group">
                  <button
                    class="btn btn-outline-secondary dropdown-toggle"
                    type="button"
                    data-bs-toggle="dropdown"
                    title="Plus d'options"
                  >
                    <i class="fas fa-plus"></i>
                  </button>
                  <ul class="dropdown-menu">
                    <li><a class="dropdown-item" href="#" @click="envoyerNotificationTache">
                      <i class="fas fa-tasks me-2"></i>Notifier une tâche
                    </a></li>
                    <li><a class="dropdown-item" href="#" @click="partagerFichier">
                      <i class="fas fa-file me-2"></i>Partager un fichier
                    </a></li>
                  </ul>
                </div>
              </div>

              <!-- Indicateurs de statut -->
              <div class="d-flex justify-content-between align-items-center mt-2">
                <small v-if="!chatConnected" class="text-warning">
                  <i class="fas fa-exclamation-triangle me-1"></i>
                  Connexion au chat en cours...
                </small>
                <small v-else class="text-success">
                  <i class="fas fa-check-circle me-1"></i>
                  Connecté au chat temps réel
                </small>

                <small class="text-muted">
                  {{ nouveauMessage.length }}/500 caractères
                </small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Actions et tests -->
    <div class="row mt-4">
      <div class="col-12">
        <div class="d-flex flex-wrap gap-2">
          <button class="btn btn-outline-primary" @click="retourAccueil">
            <i class="fas fa-arrow-left me-1"></i>
            Retour
          </button>
          <button v-if="estChefProjet" class="btn btn-outline-warning">
            <i class="fas fa-edit me-1"></i>
            Modifier le projet
          </button>
          <button class="btn btn-outline-info" @click="connecterChat">
            <i class="fas fa-sync me-1"></i>
            Reconnecter chat
          </button>

          <!-- Boutons de test F8/F9 -->
          <div class="btn-group" role="group">
            <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
              🧪 Tests F8/F9
            </button>
            <ul class="dropdown-menu">
              <li><h6 class="dropdown-header">Tests F8 - Membres</h6></li>
              <li><a class="dropdown-item" href="#" @click="testerAjoutMasseMembers">
                Ajouter plusieurs membres
              </a></li>
              <li><a class="dropdown-item" href="#" @click="testerNotificationNouveauMembre">
                Simuler arrivée membre
              </a></li>
              <li><hr class="dropdown-divider"></li>
              <li><h6 class="dropdown-header">Tests F9 - Chat</h6></li>
              <li><a class="dropdown-item" href="#" @click="testerMessagesMultiples">
                Simuler conversation équipe
              </a></li>
              <li><a class="dropdown-item" href="#" @click="testerNotificationsTaches">
                Simuler notifications tâches
              </a></li>
              <li><a class="dropdown-item" href="#" @click="testerDeconnexionReconnexion">
                Test déconnexion/reconnexion
              </a></li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Client } from '@stomp/stompjs'

export default {
  name: 'ProjetDetail',
  data() {
    return {
      projetId: 1,
      testNotification: null,
      chargementRecherche: false,
      utilisateursEnTrainEcrire: [],

      // Données du projet
      projet: {
        id: 1,
        titre: 'Application Mobile E-commerce',
        description: 'Développement d\'une application mobile de vente en ligne avec paie',
        statut: 'ACTIF',
        dateCreation: '2024-01-15T10:00:00'
      },

      // Membres avec données de votre SQL (avec statut en ligne)
      membres: [
        {
          id: 1,
          nom: 'Durand',
          prenom: 'Émilie',
          email: 'emilie.durand0@icc.be',
          role: 'CHEF_PROJET',
          enLigne: true,
          derniereSeen: null
        },
        {
          id: 7,
          nom: 'Fournier',
          prenom: 'Sarah',
          email: 'sarah.fournier6@icc.be',
          role: 'MEMBRE',
          enLigne: true,
          derniereSeen: null
        },
        {
          id: 8,
          nom: 'Giraud',
          prenom: 'Hugo',
          email: 'hugo.giraud7@icc.be',
          role: 'MEMBRE',
          enLigne: false,
          derniereSeen: '2h'
        }
      ],

      // Base d'utilisateurs pour recherche (vos données SQL)
      tousUtilisateurs: [
        {id: 5, nom: 'Petit', prenom: 'Camille', email: 'camille.petit4@icc.be', role: 'MEMBRE'},
        {id: 6, nom: 'Roux', prenom: 'Léo', email: 'leo.roux5@icc.be', role: 'MEMBRE'},
        {id: 9, nom: 'Lambert', prenom: 'Manon', email: 'manon.lambert8@icc.be', role: 'MEMBRE'},
        {id: 10, nom: 'Bertrand', prenom: 'Julien', email: 'julien.bertrand9@icc.be', role: 'MEMBRE'},
        {id: 2, nom: 'Lefevre', prenom: 'Lucas', email: 'lucas.lefevre1@icc.be', role: 'CHEF_PROJET'},
        {id: 3, nom: 'Moreau', prenom: 'Chloé', email: 'chloe.moreau2@icc.be', role: 'CHEF_PROJET'}
      ],

      rechercheQuery: '',
      utilisateursTrouves: [],

      // Chat WebSocket
      chatClient: null,
      chatConnected: false,
      messages: [
        {
          id: 1,
          contenu: 'Bonjour équipe ! Le projet démarre bien, j\'ai hâte de collaborer avec vous 🚀',
          utilisateurNom: 'Émilie Durand',
          utilisateurEmail: 'emilie.durand0@icc.be',
          dateEnvoi: '2024-01-16T10:30:00',
          type: 'TEXT',
          lu: true
        },
        {
          id: 2,
          contenu: 'Parfait ! J\'ai regardé les spécifications, tout me semble clair. Prêt à commencer 💪',
          utilisateurNom: 'Hugo Giraud',
          utilisateurEmail: 'hugo.giraud7@icc.be',
          dateEnvoi: '2024-01-16T16:30:00',
          type: 'TEXT',
          lu: true
        }
      ],
      nouveauMessage: '',
      typingTimer: null,

      // Utilisateur actuel (simulation connexion)
      utilisateurActuel: {
        id: 1,
        nom: 'Durand',
        prenom: 'Émilie',
        email: 'emilie.durand0@icc.be',
        role: 'CHEF_PROJET'
      }
    }
  },

  computed: {
    estChefProjet() {
      return this.utilisateurActuel?.role === 'CHEF_PROJET'
    },

    membresEnLigne() {
      return this.membres.filter(m => m.enLigne)
    },

    suggestionsMembres() {
      const membresIds = this.membres.map(m => m.id)
      return this.tousUtilisateurs
        .filter(u => !membresIds.includes(u.id))
        .slice(0, 3)
    }
  },

  mounted() {
    this.connecterChat()
    this.simulerActiviteEnLigne()
  },

  beforeUnmount() {
    this.deconnecterChat()
  },

  methods: {
    // === F8 - Gestion des membres (améliorée) ===

    async rechercherUtilisateurs() {
      if (this.rechercheQuery.length < 2) {
        this.utilisateursTrouves = []
        return
      }

      this.chargementRecherche = true

      // Simulation délai API
      await new Promise(resolve => setTimeout(resolve, 300))

      const query = this.rechercheQuery.toLowerCase()
      const membresIds = this.membres.map(m => m.id)

      this.utilisateursTrouves = this.tousUtilisateurs.filter(u =>
          !membresIds.includes(u.id) && (
            u.nom.toLowerCase().includes(query) ||
            u.prenom.toLowerCase().includes(query) ||
            u.email.toLowerCase().includes(query)
          )
      )

      this.chargementRecherche = false
    },

    ajouterMembre(utilisateur) {
      const nouveauMembre = {
        ...utilisateur,
        enLigne: Math.random() > 0.5,
        derniereSeen: Math.random() > 0.7 ? null : `${Math.floor(Math.random() * 12) + 1}h`
      }

      this.membres.push(nouveauMembre)
      this.utilisateursTrouves = []
      this.rechercheQuery = ''

      this.testNotification = `${utilisateur.prenom} ${utilisateur.nom} ajouté au projet avec succès !`

      // Notification dans le chat
      this.ajouterMessage({
        id: Date.now(),
        contenu: `${utilisateur.prenom} ${utilisateur.nom} a rejoint le projet`,
        utilisateurNom: 'Système',
        utilisateurEmail: 'system',
        dateEnvoi: new Date().toISOString(),
        type: 'SYSTEM'
      })
    },

    ajouterMembreRapide(suggestion) {
      this.ajouterMembre(suggestion)
    },

    supprimerMembre(membreId) {
      if (!confirm('Êtes-vous sûr de vouloir retirer ce membre du projet ?')) return

      const membre = this.membres.find(m => m.id === membreId)
      this.membres = this.membres.filter(m => m.id !== membreId)

      this.testNotification = `${membre.prenom} ${membre.nom} retiré du projet`

      // Notification dans le chat
      this.ajouterMessage({
        id: Date.now(),
        contenu: `${membre.prenom} ${membre.nom} a quitté le projet`,
        utilisateurNom: 'Système',
        utilisateurEmail: 'system',
        dateEnvoi: new Date().toISOString(),
        type: 'SYSTEM'
      })
    },

    // === F9 - Chat temps réel  ===

    connecterChat() {
      console.log('Connexion au chat WebSocket...')

      this.chatClient = new Client({
        brokerURL: 'ws://localhost:8080/ws-native',
        debug: (str) => console.log('📡 STOMP:', str),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      })

      this.chatClient.onConnect = () => {
        console.log('Chat connecté avec succès')
        this.chatConnected = true
        this.sAbonnerAuProjet()
        this.testNotification = 'Connexion chat établie - Tests F9 opérationnels'
      }

      this.chatClient.onStompError = (frame) => {
        console.error('Erreur STOMP:', frame)
        this.chatConnected = false
        this.testNotification = 'Erreur connexion chat - Mode simulation activé'
      }

      this.chatClient.onDisconnect = () => {
        console.log('🔌 Chat déconnecté')
        this.chatConnected = false
      }

      this.chatClient.activate()
    },

    sAbonnerAuProjet() {
      const destination = `/topic/projet/${this.projetId}`
      console.log(`Abonnement au canal: ${destination}`)

      this.chatClient.subscribe(destination, (message) => {
        const messageData = JSON.parse(message.body)
        this.ajouterMessage(messageData)
      })

      // Abonnement aux notifications de typing
      this.chatClient.subscribe(`/topic/projet/${this.projetId}/typing`, (message) => {
        const data = JSON.parse(message.body)
        this.gererIndicateurEcriture(data)
      })
    },

    envoyerMessage() {
      if (!this.nouveauMessage.trim()) return

      const message = {
        id: Date.now(),
        contenu: this.nouveauMessage,
        utilisateurNom: `${this.utilisateurActuel.prenom} ${this.utilisateurActuel.nom}`,
        utilisateurEmail: this.utilisateurActuel.email,
        dateEnvoi: new Date().toISOString(),
        type: 'TEXT',
        lu: false
      }

      if (this.chatConnected && this.chatClient) {
        this.chatClient.publish({
          destination: '/app/message.send',
          body: JSON.stringify({
            ...message,
            projetId: this.projetId
          })
        })
      } else {
        // Mode simulation si pas de connexion
        this.ajouterMessage(message)
        setTimeout(() => this.marquerCommeLu(message.id), 2000)
      }

      this.nouveauMessage = ''
      this.arreterIndicateurEcriture()
    },

    onTyping() {
      if (this.chatConnected && this.chatClient) {
        this.chatClient.publish({
          destination: '/app/typing',
          body: JSON.stringify({
            projetId: this.projetId,
            utilisateurNom: `${this.utilisateurActuel.prenom} ${this.utilisateurActuel.nom}`
          })
        })
      }

      clearTimeout(this.typingTimer)
      this.typingTimer = setTimeout(() => {
        this.arreterIndicateurEcriture()
      }, 1000)
    },

    // === Méthodes utilitaires ===

    getAvatarStyle(user) {
      const colors = [
        'linear-gradient(45deg, #007bff, #6f42c1)',
        'linear-gradient(45deg, #28a745, #20c997)',
        'linear-gradient(45deg, #ffc107, #fd7e14)',
        'linear-gradient(45deg, #dc3545, #e83e8c)',
        'linear-gradient(45deg, #6f42c1, #007bff)'
      ]
      return {
        background: colors[user.id % colors.length]
      }
    },

    getTypeBadgeClass(type) {
      return {
        'SYSTEM': 'bg-secondary',
        'NOTIFICATION': 'bg-info',
        'TASK': 'bg-warning text-dark',
        'FILE': 'bg-primary'
      }[type] || 'bg-secondary'
    },

    getTypeIcon(type) {
      return {
        'SYSTEM': 'fas fa-cog',
        'NOTIFICATION': 'fas fa-bell',
        'TASK': 'fas fa-tasks',
        'FILE': 'fas fa-file'
      }[type] || 'fas fa-info'
    },

    getTypeLabel(type) {
      return {
        'SYSTEM': 'Système',
        'NOTIFICATION': 'Notification',
        'TASK': 'Tâche',
        'FILE': 'Fichier'
      }[type] || type
    },

    // === Tests automatisés F8/F9 ===

    async testerAjoutMasseMembers() {
      this.testNotification = 'Test F8: Ajout en masse de membres...'

      const membresAjouter = this.suggestionsMembres.slice(0, 2)
      for (const membre of membresAjouter) {
        await new Promise(resolve => setTimeout(resolve, 1000))
        this.ajouterMembre(membre)
      }
    },

    testerMessagesMultiples() {
      this.testNotification = 'Test F9: Simulation conversation équipe...'

      const messagesTest = [
        {
          contenu: 'Est-ce que tout le monde a bien reçu les spécifications ?',
          utilisateurNom: 'Sarah Fournier',
          email: 'sarah.fournier6@icc.be',
          type: 'TEXT'
        },
        {
          contenu: 'Oui, parfait ! Je commence par la partie interface utilisateur',
          utilisateurNom: 'Hugo Giraud',
          email: 'hugo.giraud7@icc.be',
          type: 'TEXT'
        },
        {
          contenu: 'Super ! Moi je m\'occupe de la base de données',
          utilisateurNom: 'Camille Petit',
          email: 'camille.petit4@icc.be',
          type: 'TEXT'
        }
      ]

      messagesTest.forEach((msg, index) => {
        setTimeout(() => {
          this.ajouterMessage({
            id: Date.now() + index,
            contenu: msg.contenu,
            utilisateurNom: msg.utilisateurNom,
            utilisateurEmail: msg.email,
            dateEnvoi: new Date().toISOString(),
            type: msg.type,
            lu: true
          })
        }, (index + 1) * 2000)
      })
    },

    testerNotificationsTaches() {
      this.testNotification = 'Test F9: Notifications de tâches automatiques...'

      const notifications = [
        {
          contenu: '📋 Nouvelle tâche assignée: "Créer les mockups de l\'interface"',
          type: 'TASK'
        },
        {
          contenu: 'Tâche terminée: "Configuration de l\'environnement de développement"',
          type: 'TASK'
        },
        {
          contenu: 'Tâche en retard: "Tests unitaires du module authentification"',
          type: 'TASK'
        }
      ]

      notifications.forEach((notif, index) => {
        setTimeout(() => {
          this.ajouterMessage({
            id: Date.now() + index,
            contenu: notif.contenu,
            utilisateurNom: 'Système de Tâches',
            utilisateurEmail: 'system-tasks',
            dateEnvoi: new Date().toISOString(),
            type: notif.type,
            lu: false
          })
        }, (index + 1) * 1500)
      })
    },

    testerNotificationNouveauMembre() {
      const nouveauMembre = this.suggestionsMembres[0]
      if (nouveauMembre) {
        this.ajouterMembre(nouveauMembre)
      }
    },

    async testerDeconnexionReconnexion() {
      this.testNotification = 'Test F9: Simulation déconnexion/reconnexion...'

      // Simuler déconnexion
      this.chatConnected = false
      await new Promise(resolve => setTimeout(resolve, 3000))

      // Simuler reconnexion
      this.chatConnected = true
      this.testNotification = 'Test F9: Reconnexion réussie !'

      // Message de reconnexion
      this.ajouterMessage({
        id: Date.now(),
        contenu: 'Reconnexion au chat établie',
        utilisateurNom: 'Système',
        utilisateurEmail: 'system',
        dateEnvoi: new Date().toISOString(),
        type: 'SYSTEM'
      })
    },

    // === Notifications spéciales ===

    envoyerNotificationTache() {
      const message = {
        id: Date.now(),
        contenu: '📋 Nouvelle tâche créée: "Intégrer l\'API de paiement Stripe"',
        utilisateurNom: `${this.utilisateurActuel.prenom} ${this.utilisateurActuel.nom}`,
        utilisateurEmail: this.utilisateurActuel.email,
        dateEnvoi: new Date().toISOString(),
        type: 'TASK',
        lu: false
      }

      this.ajouterMessage(message)
      this.testNotification = 'Notification de tâche envoyée !'
    },

    partagerFichier() {
      const message = {
        id: Date.now(),
        contenu: '📎 Fichier partagé: "specifications_v2.pdf" (2.3 MB)',
        utilisateurNom: `${this.utilisateurActuel.prenom} ${this.utilisateurActuel.nom}`,
        utilisateurEmail: this.utilisateurActuel.email,
        dateEnvoi: new Date().toISOString(),
        type: 'FILE',
        lu: false
      }

      this.ajouterMessage(message)
      this.testNotification = 'Fichier partagé dans le chat !'
    },

    // === Gestion des messages ===

    ajouterMessage(message) {
      this.messages.push(message)
      this.$nextTick(() => {
        this.scrollToBottom()
        // Marquer comme lu après un délai si c'est notre message
        if (this.estMonMessage(message)) {
          setTimeout(() => this.marquerCommeLu(message.id), 1500)
        }
      })
    },

    marquerCommeLu(messageId) {
      const message = this.messages.find(m => m.id === messageId)
      if (message) {
        message.lu = true
      }
    },

    gererIndicateurEcriture(data) {
      if (data.utilisateurNom !== `${this.utilisateurActuel.prenom} ${this.utilisateurActuel.nom}`) {
        if (!this.utilisateursEnTrainEcrire.includes(data.utilisateurNom)) {
          this.utilisateursEnTrainEcrire.push(data.utilisateurNom)
        }

        // Retirer après 3 secondes
        setTimeout(() => {
          const index = this.utilisateursEnTrainEcrire.indexOf(data.utilisateurNom)
          if (index > -1) {
            this.utilisateursEnTrainEcrire.splice(index, 1)
          }
        }, 3000)
      }
    },

    arreterIndicateurEcriture() {
      if (this.chatConnected && this.chatClient) {
        this.chatClient.publish({
          destination: '/app/typing/stop',
          body: JSON.stringify({
            projetId: this.projetId,
            utilisateurNom: `${this.utilisateurActuel.prenom} ${this.utilisateurActuel.nom}`
          })
        })
      }
    },

    simulerActiviteEnLigne() {
      // Simuler des changements de statut en ligne
      setInterval(() => {
        this.membres.forEach(membre => {
          if (Math.random() > 0.9) {
            membre.enLigne = !membre.enLigne
            if (!membre.enLigne) {
              membre.derniereSeen = `${Math.floor(Math.random() * 60) + 1}min`
            } else {
              membre.derniereSeen = null
            }
          }
        })
      }, 10000)
    },

    deconnecterChat() {
      if (this.chatClient) {
        this.chatClient.deactivate()
        this.chatConnected = false
        console.log('Chat déconnecté')
      }
    },

    // === Méthodes existantes améliorées ===

    peutSupprimerMembre(membre) {
      return this.estChefProjet &&
        membre.role !== 'CHEF_PROJET' &&
        membre.id !== this.utilisateurActuel.id
    },

    getInitials(nom, prenom) {
      return (prenom?.[0] || '') + (nom?.[0] || '')
    },

    getRoleBadgeClass(role) {
      return {
        'CHEF_PROJET': 'bg-danger',
        'MEMBRE': 'bg-primary',
        'ADMINISTRATEUR': 'bg-dark',
        'VISITEUR': 'bg-secondary'
      }[role] || 'bg-secondary'
    },

    getRoleLabel(role) {
      return {
        'CHEF_PROJET': 'Chef',
        'MEMBRE': 'Membre',
        'ADMINISTRATEUR': 'Admin',
        'VISITEUR': 'Visiteur'
      }[role] || role
    },

    estMonMessage(message) {
      return message.utilisateurEmail === this.utilisateurActuel?.email
    },

    getMessageClass(message) {
      if (this.estMonMessage(message)) {
        return 'message-own'
      }
      return message.type === 'SYSTEM' ? 'message-system' : 'message-other'
    },

    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },

    formatTime(dateString) {
      return new Date(dateString).toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    scrollToBottom() {
      const container = this.$refs.messagesContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },

    retourAccueil() {
      if (this.$router) {
        this.$router.push('/')
      } else {
        console.log('🏠 Retour à l\'accueil')
      }
    }
  }
}
</script>

<style scoped>
/* === Styles existants améliorés === */
.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.avatar-circle-sm {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 12px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* === Status indicators === */
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.status-dot.online {
  background-color: #28a745;
  box-shadow: 0 0 4px #28a745;
}

.status-dot.offline {
  background-color: #6c757d;
}

/* === Search improvements === */
.search-result-item {
  background-color: #f8f9fa;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e9ecef !important;
}

.search-result-item:hover {
  background-color: #e3f2fd;
  border-color: #2196F3 !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.15);
}

.suggestion-badge {
  cursor: pointer;
  transition: all 0.2s ease;
}

.suggestion-badge:hover {
  background-color: #007bff !important;
  color: white !important;
  transform: translateY(-1px);
}

/* === Message bubbles === */
.message-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 18px;
  margin-bottom: 8px;
  word-wrap: break-word;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.2s ease;
}

.message-bubble:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.message-own {
  background: linear-gradient(135deg, #007bff, #0056b3);
  color: white;
  margin-left: auto;
}

.message-other {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  color: #333;
}

.message-system {
  background: linear-gradient(135deg, #6c757d, #495057);
  color: white;
  font-style: italic;
  text-align: center;
  max-width: 90%;
  margin: 0 auto;
  font-size: 0.9em;
}

.message-content {
  word-wrap: break-word;
  line-height: 1.4;
}

.message-status {
  font-size: 0.75em;
  opacity: 0.8;
}

/* === Typing indicator === */
.typing-indicator {
  padding: 8px 16px;
}

.typing-dots {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #6c757d;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dots span:nth-child(1) { animation-delay: -0.32s; }
.typing-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* === Card enhancements === */
.card {
  border: none;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 8px 30px rgba(0,0,0,0.12);
}

.card-header {
  border: none;
  font-weight: 600;
}

/* === Responsive improvements === */
@media (max-width: 768px) {
  .col-lg-4, .col-lg-8 {
    margin-bottom: 20px;
  }

  .message-bubble {
    max-width: 90%;
    padding: 10px 14px;
  }

  .d-flex.gap-2 {
    flex-wrap: wrap;
  }

  .btn {
    margin-bottom: 8px;
  }
}

/* === Animations === */
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item {
  animation: slideInUp 0.3s ease;
}

/* === Badge enhancements === */
.badge {
  font-size: 0.75em;
  padding: 0.35em 0.65em;
  font-weight: 600;
}

/* === Input improvements === */
.form-control:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

/* === Scrollbar styling === */
.overflow-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.overflow-auto::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.overflow-auto::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>
