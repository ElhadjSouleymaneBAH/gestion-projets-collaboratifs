<template>
  <div class="container-fluid py-3">
    <!-- Header Membre -->
    <div class="member-header mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h1 class="mb-1 d-flex align-items-center gap-2">
            <i class="fas fa-user-circle text-primary"></i>
            {{ $t('tableauBord.membre.titre') }}
          </h1>
          <p class="text-muted mb-0">{{ tOr('commun.bienvenue','Bienvenue') }} {{ utilisateur?.prenom }} {{ utilisateur?.nom }}</p>
        </div>
        <div class="d-flex align-items-center gap-2">
          <div class="btn-group">
            <button class="btn btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown" title="Langue">
              <i class="fas fa-globe"></i>
            </button>
            <div class="dropdown-menu dropdown-menu-end p-2" style="min-width: 220px">
              <SelecteurLangue/>
            </div>
          </div>
          <button class="btn btn-outline-danger" @click="seDeconnecter">
            <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Promotion Premium -->
    <div class="alert alert-warning border-0 shadow-sm mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h6 class="mb-1"><i class="fas fa-star text-warning me-2"></i>Passez Premium pour créer vos propres projets !</h6>
          <small class="text-muted">Devenez Chef de Projet : créez des projets, gérez des équipes, assignez des tâches</small>
        </div>
        <router-link to="/abonnement-premium" class="btn btn-warning">
          <i class="fas fa-arrow-right me-1"></i>Découvrir
        </router-link>
      </div>
    </div>

    <!-- Loading / Error -->
    <div v-if="erreurBackend" class="alert alert-danger d-flex justify-content-between align-items-center">
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">{{ tOr('commun.charger','Charger') }}</button>
    </div>
    <div v-else-if="chargementGlobal" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="text-muted mt-2">{{ $t('commun.chargement') }}</p>
    </div>

    <!-- Content -->
    <div v-else>
      <!-- KPIs Membre -->
      <div class="row g-3 mb-4">
        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='projets'">
            <div class="card-body">
              <div class="d-flex align-items-center gap-3">
                <div class="metric-icon bg-primary bg-opacity-10 rounded-circle p-3">
                  <i class="fas fa-project-diagram fa-2x text-primary"></i>
                </div>
                <div>
                  <h3 class="mb-0 fw-bold">{{ mesProjets.length }}</h3>
                  <p class="text-muted mb-1 small">Projets rejoints</p>
                  <small class="text-primary">{{ projetsActifsCount }} actifs</small>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='taches'">
            <div class="card-body">
              <div class="d-flex align-items-center gap-3">
                <div class="metric-icon bg-warning bg-opacity-10 rounded-circle p-3">
                  <i class="fas fa-tasks fa-2x text-warning"></i>
                </div>
                <div>
                  <h3 class="mb-0 fw-bold">{{ mesTachesEnCours.length }}</h3>
                  <p class="text-muted mb-1 small">Tâches assignées</p>
                  <small class="text-warning">{{ tachesUrgentes.length }} urgentes</small>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='taches'">
            <div class="card-body">
              <div class="d-flex align-items-center gap-3">
                <div class="metric-icon bg-success bg-opacity-10 rounded-circle p-3">
                  <i class="fas fa-check-circle fa-2x text-success"></i>
                </div>
                <div>
                  <h3 class="mb-0 fw-bold">{{ mesTachesTerminees.length }}</h3>
                  <p class="text-muted mb-1 small">Tâches terminées</p>
                  <small class="text-success">{{ tauxCompletion }}% de réussite</small>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='notifications'">
            <div class="card-body">
              <div class="d-flex align-items-center gap-3">
                <div class="metric-icon bg-info bg-opacity-10 rounded-circle p-3">
                  <i class="fas fa-bell fa-2x text-info"></i>
                </div>
                <div>
                  <h3 class="mb-0 fw-bold">{{ notificationsNonLues.length }}</h3>
                  <p class="text-muted mb-1 small">Notifications</p>
                  <small class="text-info">{{ notifications.length }} total</small>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Navigation -->
      <ul class="nav nav-pills bg-light rounded p-2 mb-4">
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='projets'}" @click="onglet='projets'">
            <i class="fas fa-project-diagram me-2"></i>Mes Projets
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='taches'}" @click="onglet='taches'">
            <i class="fas fa-tasks me-2"></i>Mes Tâches
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='notifications'}" @click="onglet='notifications'">
            <i class="fas fa-bell me-2"></i>Notifications
            <span v-if="notificationsNonLues.length > 0" class="badge bg-danger ms-1">{{ notificationsNonLues.length }}</span>
          </a>
        </li>
        <li class="nav-item ms-auto">
          <a class="nav-link" :class="{active:onglet==='profil'}" @click="onglet='profil'">
            <i class="fas fa-user-cog me-2"></i>Mon Profil
          </a>
        </li>
      </ul>

      <!-- ONGLET PROJETS -->
      <div v-if="onglet==='projets'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">Projets où je participe ({{ mesProjets.length }})</h5>
              <small class="text-muted">Collaboration et suivi de progression</small>
            </div>
            <div class="d-flex gap-2">
              <input class="form-control" style="max-width:260px" v-model="rechercheProjets"
                     placeholder="Rechercher...">
              <select class="form-select" v-model="filtreProjetStatut" style="max-width:200px">
                <option value="">Tous les projets</option>
                <option value="actif">Projets actifs</option>
                <option value="termine">Projets terminés</option>
              </select>
            </div>
          </div>
          <div class="card-body">
            <div v-if="chargementProjets" class="text-center py-4">
              <div class="spinner-border text-primary"></div>
            </div>
            <div v-else-if="projetsFiltres.length===0" class="text-center py-5">
              <i class="fas fa-project-diagram fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">Aucun projet rejoint</h5>
              <p class="text-muted">Vous serez invité par des chefs de projet à rejoindre leurs équipes</p>
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>Projet</th>
                  <th>Chef de Projet</th>
                  <th>Mes Tâches</th>
                  <th>Progression</th>
                  <th>Dernière Activité</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="p in projetsFiltres" :key="p.id">
                  <td>
                    <div>
                      <h6 class="mb-0">{{ p.titre }}</h6>
                      <small class="text-muted">{{ p.description?.substring(0, 60) }}...</small>
                    </div>
                  </td>
                  <td>{{ getChefProjetNom(p.id_createur) }}</td>
                  <td>
                    <span class="badge bg-warning text-dark">{{ getMesTachesDansProjet(p.id).length }}</span>
                    <span v-if="getTachesEnCoursProjet(p.id) > 0" class="badge bg-danger ms-1">{{ getTachesEnCoursProjet(p.id) }}</span>
                  </td>
                  <td>
                    <div class="progress" style="width:100px;height:6px">
                      <div class="progress-bar bg-success" :style="{width: getProjetProgression(p.id) + '%'}"></div>
                    </div>
                    <small class="text-muted">{{ getProjetProgression(p.id) }}%</small>
                  </td>
                  <td>
                    <small class="text-muted">{{ formatDateRelative(p.date_creation) }}</small>
                  </td>
                  <td>
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-primary" @click="consulterProjet(p)" title="Consulter">
                        <i class="fas fa-eye"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-success" @click="ouvrirChat(p)" title="Chat Équipe">
                        <i class="fas fa-comments"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- ONGLET TÂCHES -->
      <div v-if="onglet==='taches'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">Mes Tâches Assignées</h5>
              <small class="text-muted">Gérer le cycle de vie de vos tâches</small>
            </div>
            <div class="d-flex gap-2">
              <select class="form-select" v-model="filtreTacheStatut" style="max-width:220px">
                <option value="">Tous les statuts</option>
                <option value="BROUILLON">Brouillon</option>
                <option value="EN_ATTENTE_VALIDATION">En attente validation</option>
                <option value="TERMINE">Terminées</option>
                <option value="ANNULE">Annulées</option>
              </select>
              <select class="form-select" v-model="filtreTachePriorite" style="max-width:200px">
                <option value="">Toutes priorités</option>
                <option value="HAUTE">Haute</option>
                <option value="MOYENNE">Moyenne</option>
                <option value="BASSE">Basse</option>
              </select>
            </div>
          </div>
          <div class="card-body">
            <div v-if="chargementTaches" class="text-center py-4">
              <div class="spinner-border text-warning"></div>
            </div>
            <div v-else-if="tachesFiltrees.length===0" class="text-center py-5">
              <i class="fas fa-tasks fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">Aucune tâche assignée</h5>
              <p class="text-muted">Les chefs de projet vous assigneront des tâches prochainement</p>
            </div>
            <div v-else>
              <!-- Tâches groupées par statut -->
              <div class="row g-3">
                <!-- Brouillons -->
                <div class="col-lg-4" v-if="tachesBrouillon.length > 0">
                  <h6 class="text-secondary mb-3">
                    <i class="fas fa-edit me-2"></i>À Développer ({{ tachesBrouillon.length }})
                  </h6>
                  <div v-for="t in tachesBrouillon" :key="'brouillon-'+t.id" class="card border-start border-4 border-secondary mb-3">
                    <div class="card-body">
                      <div class="d-flex justify-content-between align-items-start mb-2">
                        <h6 class="mb-1">{{ t.titre }}</h6>
                        <span class="badge" :class="getPriorityBadgeClass(t.priorite || 'MOYENNE')">
                          {{ t.priorite || 'MOYENNE' }}
                        </span>
                      </div>
                      <p class="text-muted small mb-2">{{ t.description }}</p>
                      <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">{{ getProjetNom(t.id_projet) }}</small>
                        <div class="btn-group">
                          <button class="btn btn-sm btn-outline-success" @click="soumettreValidation(t)" title="Soumettre">
                            <i class="fas fa-check"></i>
                          </button>
                          <button class="btn btn-sm btn-outline-info" @click="ajouterCommentaire(t)" title="Commenter">
                            <i class="fas fa-comment"></i>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- En Attente -->
                <div class="col-lg-4" v-if="tachesEnValidation.length > 0">
                  <h6 class="text-warning mb-3">
                    <i class="fas fa-hourglass-half me-2"></i>En Validation ({{ tachesEnValidation.length }})
                  </h6>
                  <div v-for="t in tachesEnValidation" :key="'validation-'+t.id" class="card border-start border-4 border-warning mb-3">
                    <div class="card-body">
                      <div class="d-flex justify-content-between align-items-start mb-2">
                        <h6 class="mb-1">{{ t.titre }}</h6>
                        <span class="badge bg-warning text-dark">EN ATTENTE</span>
                      </div>
                      <p class="text-muted small mb-2">{{ t.description }}</p>
                      <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">{{ getProjetNom(t.id_projet) }}</small>
                        <button class="btn btn-sm btn-outline-info" @click="ajouterCommentaire(t)">
                          <i class="fas fa-comment"></i>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Terminées -->
                <div class="col-lg-4" v-if="tachesTerminees.length > 0">
                  <h6 class="text-success mb-3">
                    <i class="fas fa-check-circle me-2"></i>Terminées ({{ tachesTerminees.length }})
                  </h6>
                  <div v-for="t in tachesTerminees.slice(0,5)" :key="'terminee-'+t.id" class="card border-start border-4 border-success mb-3">
                    <div class="card-body">
                      <h6 class="mb-1 text-success">{{ t.titre }}</h6>
                      <p class="text-muted small mb-2">{{ t.description }}</p>
                      <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">{{ getProjetNom(t.id_projet) }}</small>
                        <small class="text-success">
                          <i class="fas fa-check me-1"></i>Validée
                        </small>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ONGLET NOTIFICATIONS -->
      <div v-if="onglet==='notifications'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">Centre de Notifications</h5>
              <small class="text-muted">Restez informé de l'activité de vos projets</small>
            </div>
            <div class="d-flex gap-2">
              <button v-if="notificationsNonLues.length > 0" class="btn btn-outline-primary" @click="marquerToutesLues">
                <i class="fas fa-check-double me-1"></i>Tout marquer lu
              </button>
              <button class="btn btn-outline-secondary" @click="actualiserNotifications">
                <i class="fas fa-sync-alt me-1"></i>Actualiser
              </button>
            </div>
          </div>
          <div class="card-body">
            <div v-if="chargementNotifications" class="text-center py-4">
              <div class="spinner-border text-info"></div>
            </div>
            <div v-else-if="notifications.length===0" class="text-center py-5">
              <i class="fas fa-bell fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">Aucune notification</h5>
              <p class="text-muted">Vous recevrez des notifications lors des activités sur vos projets</p>
            </div>
            <div v-else>
              <div v-for="n in notifications" :key="n.id"
                   class="notification-item d-flex align-items-start gap-3 p-3 rounded mb-2"
                   :class="{'bg-light border border-info': !n.lu, 'bg-white': n.lu}">
                <div class="flex-shrink-0">
                  <div class="notification-icon rounded-circle p-2" :class="getNotificationIconClass(n.type)">
                    <i :class="getNotificationIcon(n.type)"></i>
                  </div>
                </div>
                <div class="flex-grow-1">
                  <div class="d-flex justify-content-between align-items-start">
                    <div>
                      <h6 class="mb-1" :class="{'fw-bold': !n.lu}">{{ n.titre || 'Notification' }}</h6>
                      <p class="mb-2 text-muted">{{ n.message }}</p>
                      <small class="text-muted">
                        <i class="fas fa-clock me-1"></i>{{ formatDateRelative(n.date) }}
                      </small>
                    </div>
                    <div class="flex-shrink-0">
                      <button v-if="!n.lu" class="btn btn-sm btn-outline-primary me-1" @click="marquerNotificationLue(n)">
                        <i class="fas fa-check"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-danger" @click="supprimerNotification(n)">
                        <i class="fas fa-times"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ONGLET PROFIL -->
      <div v-if="onglet==='profil'">
        <div class="row">
          <div class="col-lg-8">
            <div class="card border-0 shadow-sm">
              <div class="card-header bg-white">
                <h5 class="mb-0">Informations Personnelles</h5>
                <small class="text-muted">Gérer votre profil utilisateur</small>
              </div>
              <div class="card-body">
                <form @submit.prevent="sauvegarderProfil">
                  <div class="row">
                    <div class="col-md-6 mb-3">
                      <label class="form-label fw-semibold">{{ $t('inscription.prenom') }}</label>
                      <input class="form-control" v-model.trim="utilisateur.prenom" required>
                    </div>
                    <div class="col-md-6 mb-3">
                      <label class="form-label fw-semibold">{{ $t('inscription.nom') }}</label>
                      <input class="form-control" v-model.trim="utilisateur.nom" required>
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">{{ $t('inscription.email') }}</label>
                    <input type="email" class="form-control" v-model="utilisateur.email" readonly>
                    <div class="form-text">L'email ne peut pas être modifié</div>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">{{ $t('profil.adresse') }}</label>
                    <textarea class="form-control" rows="3" v-model="utilisateur.adresse"
                              placeholder="Votre adresse complète"></textarea>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Langue préférée</label>
                    <select class="form-select" v-model="utilisateur.langue">
                      <option value="fr">Français</option>
                      <option value="en">English</option>
                    </select>
                  </div>
                  <button class="btn btn-primary" :disabled="sauvegardeProfil">
                    <span v-if="sauvegardeProfil" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="fas fa-save me-2"></i>
                    {{ sauvegardeProfil ? 'Sauvegarde...' : $t('commun.enregistrer') }}
                  </button>
                </form>
              </div>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="card border-0 shadow-sm">
              <div class="card-header bg-white">
                <h6 class="mb-0">Statistiques Personnelles</h6>
              </div>
              <div class="card-body">
                <div class="text-center mb-3">
                  <div class="avatar-large bg-primary text-white rounded-circle mx-auto mb-2 d-flex align-items-center justify-content-center" style="width:60px;height:60px">
                    {{ (utilisateur?.prenom?.charAt(0) || '') + (utilisateur?.nom?.charAt(0) || '') }}
                  </div>
                  <h6>{{ utilisateur?.prenom }} {{ utilisateur?.nom }}</h6>
                  <span class="badge bg-primary">{{ $t('roles.membre') }}</span>
                </div>
                <hr>
                <div class="row g-2 text-center">
                  <div class="col-6">
                    <div class="bg-light rounded p-2">
                      <div class="h5 mb-0 text-primary">{{ mesProjets.length }}</div>
                      <small class="text-muted">Projets</small>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="bg-light rounded p-2">
                      <div class="h5 mb-0 text-success">{{ mesTachesTerminees.length }}</div>
                      <small class="text-muted">Tâches OK</small>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="bg-light rounded p-2">
                      <div class="h5 mb-0 text-warning">{{ mesTachesEnCours.length }}</div>
                      <small class="text-muted">En cours</small>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="bg-light rounded p-2">
                      <div class="h5 mb-0 text-info">{{ tauxCompletion }}%</div>
                      <small class="text-muted">Réussite</small>
                    </div>
                  </div>
                </div>
                <hr>
                <p class="mb-2"><strong>Membre depuis:</strong> {{ formatDate(utilisateur?.date_inscription) }}</p>
                <p class="mb-0"><strong>Dernière connexion:</strong> Maintenant</p>

                <!-- Call-to-action Premium -->
                <div class="mt-3 p-3 bg-gradient-warning rounded text-center">
                  <i class="fas fa-crown fa-2x mb-2"></i>
                  <h6 class="mb-1">Devenez Chef de Projet</h6>
                  <small class="d-block mb-2">Créez vos projets et gérez des équipes</small>
                  <router-link to="/abonnement-premium" class="btn btn-sm btn-dark">
                    Passer Premium - 10€/mois
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- MODAL CHAT TEMPS RÉEL -->
      <div v-if="chatOuvert" class="modal d-block" style="background:rgba(0,0,0,.5);z-index:1060">
        <div class="modal-dialog modal-lg">
          <div class="modal-content border-0 shadow">
            <div class="modal-header bg-gradient-primary text-white">
              <h5 class="modal-title">
                <i class="fas fa-comments me-2"></i>{{ $t('collaboration.chatTempsReel') }} - {{ projetChatActuel?.titre }}
              </h5>
              <button class="btn-close btn-close-white" @click="fermerChat"></button>
            </div>
            <div class="modal-body p-0">
              <div class="chat-container" style="height:400px;overflow-y:auto;padding:20px;background:#f8f9fa;">
                <div v-for="m in messagesChat" :key="m.id" class="mb-3">
                  <div class="d-flex" :class="{'justify-content-end': estMonMessage(m)}">
                    <div class="chat-bubble p-3 rounded" :class="getMessageClass(m)" style="max-width:75%">
                      <div v-if="!estMonMessage(m)" class="mb-1">
                        <strong class="small">{{ m.auteur?.prenom || 'Membre' }}</strong>
                        <span class="text-muted ms-2 small">{{ formatTime(m.date_envoi) }}</span>
                      </div>
                      <div>{{ m.contenu }}</div>
                    </div>
                  </div>
                </div>
                <div v-if="messagesChat.length===0" class="text-center text-muted py-4">
                  <i class="fas fa-comments fa-2x mb-2"></i>
                  <p>Commencez à discuter avec votre équipe !</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <div class="input-group">
                <input class="form-control" v-model="nouveauMessage"
                       @keyup.enter="envoyerMessage"
                       :placeholder="$t('collaboration.ecrireMessage')"
                       :disabled="envoyantMessage">
                <button class="btn btn-primary" @click="envoyerMessage"
                        :disabled="!nouveauMessage.trim()||envoyantMessage">
                  <span v-if="envoyantMessage" class="spinner-border spinner-border-sm"></span>
                  <i v-else class="fas fa-paper-plane"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- MODAL COMMENTAIRE -->
      <div v-if="commentaireOuvert" class="modal d-block" style="background:rgba(0,0,0,.5);z-index:1060">
        <div class="modal-dialog">
          <div class="modal-content border-0 shadow">
            <div class="modal-header">
              <h5 class="modal-title">
                <i class="fas fa-comment me-2"></i>Ajouter un Commentaire
              </h5>
              <button class="btn-close" @click="fermerCommentaire"></button>
            </div>
            <div class="modal-body">
              <h6>{{ tacheCommentaire?.titre }}</h6>
              <p class="text-muted">{{ tacheCommentaire?.description }}</p>
              <hr>
              <div class="mb-3">
                <label class="form-label fw-semibold">Votre commentaire</label>
                <textarea class="form-control" rows="4" v-model="nouveauCommentaire"
                          placeholder="Partagez vos remarques, questions ou avancement..."></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="fermerCommentaire">Annuler</button>
              <button class="btn btn-primary" @click="envoyerCommentaire" :disabled="!nouveauCommentaire.trim()">
                <i class="fas fa-comment me-1"></i>Publier
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth'
import { projectAPI, taskAPI, notificationAPI, messagesAPI, userAPI, commentaireAPI } from '@/services/api'
import SelecteurLangue from '@/components/SelecteurLangue.vue'

export default {
  name: 'TableauDeBordMembre',
  components: { SelecteurLangue },
  data() {
    return {
      onglet: 'projets',
      chargementGlobal: true,
      chargementProjets: false,
      chargementTaches: false,
      chargementNotifications: false,
      sauvegardeProfil: false,
      envoyantMessage: false,
      mesProjets: [],
      mesTaches: [],
      notifications: [],
      messagesChat: [],
      rechercheProjets: '',
      filtreProjetStatut: '',
      filtreTacheStatut: '',
      filtreTachePriorite: '',
      chatOuvert: false,
      commentaireOuvert: false,
      projetChatActuel: null,
      tacheCommentaire: null,
      nouveauMessage: '',
      nouveauCommentaire: '',
      erreurBackend: null
    }
  },
  computed: {
    utilisateur() { return useAuthStore().user },
    projetsActifsCount() {
      // Plus de statut ACTIF selon cahier des charges - tous les projets sont "actifs"
      return this.mesProjets.length
    },
    mesTachesEnCours() { return this.mesTaches.filter(t => ['BROUILLON', 'EN_ATTENTE_VALIDATION'].includes(t.statut)) },
    mesTachesTerminees() { return this.mesTaches.filter(t => t.statut === 'TERMINE') },
    tachesUrgentes() { return this.mesTaches.filter(t => t.priorite === 'HAUTE' && t.statut !== 'TERMINE') },
    notificationsNonLues() { return this.notifications.filter(n => !n.lu) },
    tauxCompletion() {
      const total = this.mesTaches.length
      if (total === 0) return 0
      return Math.round((this.mesTachesTerminees.length / total) * 100)
    },
    projetsFiltres() {
      let arr = [...this.mesProjets]
      const q = this.rechercheProjets.trim().toLowerCase()
      if (q) {
        arr = arr.filter(p =>
          (p.titre || '').toLowerCase().includes(q) ||
          (p.description || '').toLowerCase().includes(q)
        )
      }
      // Filtre simplifié sans les statuts ACTIF/TERMINE/EN_PAUSE
      if (this.filtreProjetStatut === 'actif') {
        // Tous les projets sont considérés comme actifs
        arr = arr
      }
      if (this.filtreProjetStatut === 'termine') {
        // Filtrer par date de fin ou autre critère
        arr = arr.filter(p => p.date_fin)
      }
      return arr
    },
    tachesFiltrees() {
      let arr = [...this.mesTaches]
      if (this.filtreTacheStatut) arr = arr.filter(t => t.statut === this.filtreTacheStatut)
      if (this.filtreTachePriorite) arr = arr.filter(t => t.priorite === this.filtreTachePriorite)
      return arr
    },
    tachesBrouillon() { return this.tachesFiltrees.filter(t => t.statut === 'BROUILLON') },
    tachesEnValidation() { return this.tachesFiltrees.filter(t => t.statut === 'EN_ATTENTE_VALIDATION') },
    tachesTerminees() { return this.tachesFiltrees.filter(t => t.statut === 'TERMINE') }
  },
  async mounted() {
    const store = useAuthStore()
    if (!store.user && localStorage.getItem('user')) {
      try { store.user = JSON.parse(localStorage.getItem('user')) } catch {}
    }
    if (!store.user || store.user.role !== 'MEMBRE') {
      this.erreurBackend = 'Accès réservé aux membres'
      this.chargementGlobal = false
      return
    }
    await this.chargerToutesDonnees()
  },
  methods: {
    tOr(k, f) { const v = this.$t(k); return v === k ? f : v },
    async chargerToutesDonnees() {
      this.chargementGlobal = true
      this.erreurBackend = null
      try {
        await Promise.all([
          this.chargerMesProjets(),
          this.chargerMesTaches(),
          this.chargerNotifications()
        ])
      } catch (e) {
        console.error(e)
        this.erreurBackend = 'Impossible de charger les données du tableau de bord'
      } finally {
        this.chargementGlobal = false
      }
    },
    async chargerMesProjets() {
      this.chargementProjets = true
      try {
        const r = await projectAPI.getProjectsByUser(this.utilisateur.id)
        this.mesProjets = Array.isArray(r.data) ? r.data : (Array.isArray(r.data?.content) ? r.data.content : [])
      } catch (e) {
        console.error(e)
        this.mesProjets = []
      } finally {
        this.chargementProjets = false
      }
    },
    async chargerMesTaches() {
      this.chargementTaches = true
      try {
        const r = await taskAPI.getTasksByUser(this.utilisateur.id)
        this.mesTaches = Array.isArray(r.data) ? r.data : (Array.isArray(r.data?.content) ? r.data.content : [])
      } catch (e) {
        console.error(e)
        this.mesTaches = []
      } finally {
        this.chargementTaches = false
      }
    },
    async chargerNotifications() {
      this.chargementNotifications = true
      try {
        const r = await notificationAPI.getUserNotifications(this.utilisateur.id)
        this.notifications = Array.isArray(r.data) ? r.data : (Array.isArray(r.data?.content) ? r.data.content : [])
      } catch (e) {
        console.error(e)
        this.notifications = []
      } finally {
        this.chargementNotifications = false
      }
    },
    consulterProjet(p) { this.$router.push(`/projet/${p.id}`) },
    async soumettreValidation(tache) {
      if (!confirm('Soumettre cette tâche pour validation ?')) return
      try {
        await taskAPI.updateStatus(tache.id, 'EN_ATTENTE_VALIDATION')
        tache.statut = 'EN_ATTENTE_VALIDATION'
        alert('Tâche soumise pour validation au chef de projet')
      } catch (e) {
        console.error(e)
        alert('Erreur lors de la soumission')
      }
    },
    ajouterCommentaire(tache) {
      this.tacheCommentaire = tache
      this.commentaireOuvert = true
    },
    async envoyerCommentaire() {
      if (!this.nouveauCommentaire.trim()) return
      try {
        await commentaireAPI.create({
          contenu: this.nouveauCommentaire,
          tache_id: this.tacheCommentaire.id
        })
        alert('Commentaire ajouté avec succès')
        this.fermerCommentaire()
      } catch (e) {
        console.error(e)
        alert('Erreur lors de l\'ajout du commentaire')
      }
    },
    fermerCommentaire() {
      this.commentaireOuvert = false
      this.tacheCommentaire = null
      this.nouveauCommentaire = ''
    },
    async ouvrirChat(projet) {
      this.projetChatActuel = projet
      this.chatOuvert = true
      await this.chargerMessagesChat(projet.id)
    },
    async chargerMessagesChat(id) {
      try {
        const r = await messagesAPI.getByProjet(id)
        this.messagesChat = Array.isArray(r.data) ? r.data : []
      } catch (e) {
        console.error(e)
        this.messagesChat = []
      }
    },
    async envoyerMessage() {
      if (!this.nouveauMessage.trim()) return
      this.envoyantMessage = true
      try {
        const r = await messagesAPI.send(this.projetChatActuel.id, {
          contenu: this.nouveauMessage,
          type: 'TEXT'
        })
        this.messagesChat.push(r.data)
        this.nouveauMessage = ''
      } catch (e) {
        console.error(e)
      } finally {
        this.envoyantMessage = false
      }
    },
    fermerChat() {
      this.chatOuvert = false
      this.projetChatActuel = null
      this.messagesChat = []
    },
    estMonMessage(m) { return m.utilisateur_id === this.utilisateur.id },
    getMessageClass(m) {
      return this.estMonMessage(m) ? 'bg-primary text-white ms-auto' : 'bg-white border shadow-sm'
    },
    async marquerNotificationLue(n) {
      try {
        await notificationAPI.markAsRead(n.id)
        n.lu = true
      } catch (e) {
        console.error(e)
      }
    },
    async marquerToutesLues() {
      try {
        await notificationAPI.markAllAsRead(this.utilisateur.id)
        this.notifications.forEach(n => n.lu = true)
        alert('Toutes les notifications ont été marquées comme lues')
      } catch (e) {
        console.error(e)
      }
    },
    async supprimerNotification(n) {
      if (!confirm('Supprimer cette notification ?')) return
      try {
        await notificationAPI.deleteNotification(n.id)
        this.notifications = this.notifications.filter(x => x.id !== n.id)
      } catch (e) {
        console.error(e)
      }
    },
    async actualiserNotifications() {
      await this.chargerNotifications()
    },
    async sauvegarderProfil() {
      this.sauvegardeProfil = true
      try {
        await userAPI.updateProfile(this.utilisateur.id, {
          nom: this.utilisateur.nom,
          prenom: this.utilisateur.prenom,
          adresse: this.utilisateur.adresse,
          langue: this.utilisateur.langue
        })
        const store = useAuthStore()
        store.user = { ...store.user, ...this.utilisateur }
        localStorage.setItem('user', JSON.stringify(store.user))
        alert('Profil mis à jour avec succès')
      } catch (e) {
        console.error(e)
        alert('Erreur lors de la sauvegarde')
      } finally {
        this.sauvegardeProfil = false
      }
    },
    getProjetNom(id) {
      const p = this.mesProjets.find(x => x.id == id)
      return p ? p.titre : 'Projet inconnu'
    },
    // CORRIGÉ : Plus de données en dur pour le nom du chef
    getChefProjetNom(createurId) {
      // Récupérer depuis l'API ou les données projet au lieu de hardcoder
      const projet = this.mesProjets.find(p => p.id_createur == createurId)
      return projet?.nom_createur || `Chef #${createurId}`
    },
    getMesTachesDansProjet(projetId) {
      return this.mesTaches.filter(t => t.id_projet == projetId)
    },
    getTachesEnCoursProjet(projetId) {
      return this.mesTaches.filter(t => t.id_projet == projetId && ['BROUILLON', 'EN_ATTENTE_VALIDATION'].includes(t.statut)).length
    },
    // CORRIGÉ : Calcul réel de la progression au lieu d'aléatoire
    getProjetProgression(id) {
      const tachesProjet = this.mesTaches.filter(t => t.id_projet == id)
      if (tachesProjet.length === 0) return 0
      const tachesTerminees = tachesProjet.filter(t => t.statut === 'TERMINE').length
      return Math.round((tachesTerminees / tachesProjet.length) * 100)
    },
    getPriorityBadgeClass(p) {
      const classes = { 'HAUTE': 'bg-danger', 'MOYENNE': 'bg-warning text-dark', 'BASSE': 'bg-info' }
      return classes[p] || 'bg-secondary'
    },
    getNotificationIconClass(type) {
      const classes = { 'TACHE': 'bg-warning', 'PROJET': 'bg-primary', 'SYSTEME': 'bg-info', 'MESSAGE': 'bg-success' }
      return classes[type] || 'bg-secondary'
    },
    getNotificationIcon(type) {
      const icons = { 'TACHE': 'fas fa-tasks', 'PROJET': 'fas fa-project-diagram', 'SYSTEME': 'fas fa-cog', 'MESSAGE': 'fas fa-comment' }
      return icons[type] || 'fas fa-bell'
    },
    formatDate(date) { return new Date(date).toLocaleDateString('fr-FR') },
    formatDateRelative(date) {
      const now = new Date()
      const diff = now - new Date(date)
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(minutes / 60)
      const days = Math.floor(hours / 24)
      if (minutes < 1) return "À l'instant"
      if (minutes < 60) return `Il y a ${minutes}min`
      if (hours < 24) return `Il y a ${hours}h`
      if (days < 7) return `Il y a ${days}j`
      return this.formatDate(date)
    },
    formatTime(timestamp) {
      return new Date(timestamp).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
    },
    seDeconnecter() {
      useAuthStore().logout()
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.member-header {
  /* Ancien bleu -> remplacé par un dégradé pro gris ardoise */
  background: linear-gradient(135deg, #119c72, #96ddc8);
  border-radius: 12px;
  padding: 20px;
  color: white;
}

.metric-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.metric-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(202, 120, 120, 0.12);
}

.card {
  border-radius: 12px;
  overflow: hidden;
}

.bg-gradient-primary {
  background: linear-gradient(135deg, #007bff, #0056b3);
}

.bg-gradient-warning {
  background: linear-gradient(135deg, #ffc107, #e0a800);
}

.nav-pills .nav-link {
  border-radius: 8px;
  margin: 0 2px;
  transition: all 0.2s ease;
}

.nav-pills .nav-link.active {
  background: linear-gradient(135deg, #007bff, #0056b3);
  transform: translateY(-1px);
}

.notification-item {
  transition: all 0.2s ease;
}

.notification-item:hover {
  transform: translateX(5px);
}

.notification-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.chat-bubble {
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.progress {
  background-color: #e9ecef;
  border-radius: 10px;
  height: 6px;
}

.table th {
  border-top: none;
  font-weight: 600;
  font-size: 0.875rem;
  color: #495057;
}

.table-hover tbody tr:hover {
  background-color: rgba(0,123,255,.05);
}

.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}

.btn-group .btn {
  border-radius: 6px;
  margin: 0 1px;
}

.modal {
  backdrop-filter: blur(5px);
}

.avatar-large {
  font-size: 1.5rem;
  font-weight: 600;
}
</style>
