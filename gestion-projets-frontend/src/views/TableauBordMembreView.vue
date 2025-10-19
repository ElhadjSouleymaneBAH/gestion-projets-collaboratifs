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
          <p class="text-muted mb-0">{{ $t('commun.bienvenue') }} {{ utilisateur?.prenom }} {{ utilisateur?.nom }}</p>
        </div>
        <button class="btn btn-outline-danger" @click="seDeconnecter">
          <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
        </button>
      </div>
    </div>

    <!-- Promotion Premium -->
    <div class="alert alert-warning border-0 shadow-sm mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h6 class="mb-1"><i class="fas fa-star text-warning me-2"></i>{{ $t('membre.promoPremium.titre') }}</h6>
          <small class="text-muted">{{ $t('membre.promoPremium.description') }}</small>
        </div>
        <router-link to="/abonnement-premium" class="btn btn-warning">
          <i class="fas fa-arrow-right me-1"></i>{{ $t('membre.promoPremium.bouton') }}
        </router-link>
      </div>
    </div>

    <!-- Loading / Error -->
    <div v-if="erreurBackend" class="alert alert-danger d-flex justify-content-between align-items-center">
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">{{ $t('commun.actualiser') }}</button>
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
                  <p class="text-muted mb-1 small">{{ $t('membre.kpi.projetsRejoints') }}</p>
                  <small class="text-primary">{{ projetsActifsCount }} {{ $t('commun.actifs') }}</small>
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
                  <p class="text-muted mb-1 small">{{ $t('membre.kpi.tachesAssignees') }}</p>
                  <small class="text-warning">{{ tachesUrgentes.length }} {{ $t('membre.kpi.urgentes') }}</small>
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
                  <p class="text-muted mb-1 small">{{ $t('membre.kpi.tachesTerminees') }}</p>
                  <small class="text-success">{{ tauxCompletion }}% {{ $t('membre.kpi.reussite') }}</small>
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
                  <p class="text-muted mb-1 small">{{ $t('notifications.titre') }}</p>
                  <small class="text-info">{{ notifications.length }} {{ $t('commun.total') }}</small>
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
            <i class="fas fa-project-diagram me-2"></i>{{ $t('nav.mesProjets') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='taches'}" @click="onglet='taches'">
            <i class="fas fa-tasks me-2"></i>{{ $t('nav.mesTaches') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='notifications'}" @click="onglet='notifications'">
            <i class="fas fa-bell me-2"></i>{{ $t('nav.notifications') }}
            <span v-if="notificationsNonLues.length > 0" class="badge bg-danger ms-1">{{ notificationsNonLues.length }}</span>
          </a>
        </li>
        <li class="nav-item ms-auto">
          <a class="nav-link" :class="{active:onglet==='profil'}" @click="onglet='profil'">
            <i class="fas fa-user-cog me-2"></i>{{ $t('nav.monProfil') }}
          </a>
        </li>
      </ul>

      <!-- ONGLET PROJETS -->
      <div v-if="onglet==='projets'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('membre.projets.titre') }} ({{ mesProjets.length }})</h5>
              <small class="text-muted">{{ $t('membre.projets.description') }}</small>
            </div>
            <div class="d-flex gap-2">
              <input class="form-control" style="max-width:260px" v-model="rechercheProjets"
                     :placeholder="$t('commun.rechercher')">
              <select class="form-select" v-model="filtreProjetStatut" style="max-width:200px">
                <option value="">{{ $t('projets.tousLesProjets') }}</option>
                <option value="actif">{{ $t('projets.projetsActifs') }}</option>
                <option value="termine">{{ $t('projets.projetsTermines') }}</option>
              </select>
            </div>
          </div>
          <div class="card-body">
            <div v-if="chargementProjets" class="text-center py-4">
              <div class="spinner-border text-primary"></div>
            </div>
            <div v-else-if="projetsFiltres.length===0" class="text-center py-5">
              <i class="fas fa-project-diagram fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">{{ $t('membre.projets.aucunProjet') }}</h5>
              <p class="text-muted">{{ $t('membre.projets.messageVide') }}</p>
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('projets.projet') }}</th>
                  <th>{{ $t('projets.chefProjet') }}</th>
                  <th>{{ $t('membre.projets.mesTaches') }}</th>
                  <th>{{ $t('projets.progression') }}</th>
                  <th>{{ $t('projets.derniereActivite') }}</th>
                  <th>{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="p in projetsFiltres" :key="p.id">
                  <td>
                    <div>
                      <h6 class="mb-0">{{ p.titre }}</h6>
                      <small class="text-muted">{{ (p.description || '').substring(0, 60) }}...</small>
                    </div>
                  </td>
                  <td>{{ getChefProjetNom(p.id_createur || p.idCreateur) }}</td>
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
                    <small class="text-muted">{{ formatDateRelative(p.date_creation || p.dateCreation) }}</small>
                  </td>
                  <td>
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-primary" @click="consulterProjet(p)" :title="$t('commun.consulter')">
                        <i class="fas fa-eye"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-success" @click="ouvrirChat(p)" :title="$t('collaboration.chat')">
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
              <h5 class="mb-0">{{ $t('membre.taches.titre') }}</h5>
              <small class="text-muted">{{ $t('membre.taches.description') }}</small>
            </div>
            <div class="d-flex gap-2">
              <button class="btn btn-success" @click="creerTache">
                <i class="fas fa-plus me-2"></i>{{ $t('taches.creer') }}
              </button>
              <select class="form-select" v-model="filtreTacheStatut" style="max-width:220px">
                <option value="">{{ $t('taches.tousStatuts') }}</option>
                <option value="BROUILLON">{{ $t('taches.statuts.brouillon') }}</option>
                <option value="EN_ATTENTE_VALIDATION">{{ $t('taches.statuts.en_attente_validation') }}</option>
                <option value="TERMINE">{{ $t('taches.statuts.termine') }}</option>
                <option value="ANNULE">{{ $t('taches.statuts.annule') }}</option>
              </select>
              <select class="form-select" v-model="filtreTachePriorite" style="max-width:200px">
                <option value="">{{ $t('taches.toutesPriorites') }}</option>
                <option value="HAUTE">{{ $t('taches.priorites.haute') }}</option>
                <option value="MOYENNE">{{ $t('taches.priorites.moyenne') }}</option>
                <option value="BASSE">{{ $t('taches.priorites.basse') }}</option>
              </select>
            </div>
          </div>
          <div class="card-body">
            <div v-if="chargementTaches" class="text-center py-4">
              <div class="spinner-border text-warning"></div>
            </div>
            <div v-else-if="tachesFiltrees.length===0" class="text-center py-5">
              <i class="fas fa-tasks fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">{{ $t('membre.taches.aucuneTache') }}</h5>
              <p class="text-muted">{{ $t('membre.taches.messageVide') }}</p>
            </div>
            <div v-else>
              <div class="row g-3">
                <!-- Brouillons -->
                <div class="col-lg-4" v-if="tachesBrouillon.length > 0">
                  <h6 class="text-secondary mb-3">
                    <i class="fas fa-edit me-2"></i>{{ $t('membre.taches.aDevelopper') }} ({{ tachesBrouillon.length }})
                  </h6>
                  <div v-for="t in tachesBrouillon" :key="'brouillon-'+t.id" class="card border-start border-4 border-secondary mb-3">
                    <div class="card-body">
                      <div class="d-flex justify-content-between align-items-start mb-2">
                        <h6 class="mb-1">{{ t.titre }}</h6>
                        <span class="badge" :class="getPriorityBadgeClass(t.priorite || 'MOYENNE')">
                          {{ $t('taches.priorites.' + (t.priorite || 'MOYENNE').toLowerCase()) }}
                        </span>
                      </div>
                      <p class="text-muted small mb-2">{{ t.description }}</p>
                      <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">{{ getProjetNom(t.id_projet || t.projetId) }}</small>
                        <div class="btn-group">
                          <button class="btn btn-sm btn-outline-success" @click="soumettreValidation(t)" :title="$t('taches.soumettre')">
                            <i class="fas fa-check"></i>
                          </button>
                          <button class="btn btn-sm btn-outline-info" @click="ajouterCommentaire(t)" :title="$t('commentaires.ajouter')">
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
                    <i class="fas fa-hourglass-half me-2"></i>{{ $t('membre.taches.enValidation') }} ({{ tachesEnValidation.length }})
                  </h6>
                  <div v-for="t in tachesEnValidation" :key="'validation-'+t.id" class="card border-start border-4 border-warning mb-3">
                    <div class="card-body">
                      <div class="d-flex justify-content-between align-items-start mb-2">
                        <h6 class="mb-1">{{ t.titre }}</h6>
                        <span class="badge bg-warning text-dark">{{ $t('taches.enAttente') }}</span>
                      </div>
                      <p class="text-muted small mb-2">{{ t.description }}</p>
                      <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">{{ getProjetNom(t.id_projet || t.projetId) }}</small>
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
                    <i class="fas fa-check-circle me-2"></i>{{ $t('membre.taches.terminees') }} ({{ tachesTerminees.length }})
                  </h6>
                  <div v-for="t in tachesTerminees.slice(0,5)" :key="'terminee-'+t.id" class="card border-start border-4 border-success mb-3">
                    <div class="card-body">
                      <h6 class="mb-1 text-success">{{ t.titre }}</h6>
                      <p class="text-muted small mb-2">{{ t.description }}</p>
                      <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">{{ getProjetNom(t.id_projet || t.projetId) }}</small>
                        <small class="text-success">
                          <i class="fas fa-check me-1"></i>{{ $t('taches.validee') }}
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
              <h5 class="mb-0">{{ $t('notifications.centre') }}</h5>
              <small class="text-muted">{{ $t('notifications.description') }}</small>
            </div>
            <div class="d-flex gap-2">
              <button v-if="notificationsNonLues.length > 0" class="btn btn-outline-primary" @click="marquerToutesLues">
                <i class="fas fa-check-double me-1"></i>{{ $t('notifications.marquerToutesLues') }}
              </button>
              <button class="btn btn-outline-secondary" @click="actualiserNotifications">
                <i class="fas fa-sync-alt me-1"></i>{{ $t('commun.actualiser') }}
              </button>
            </div>
          </div>
          <div class="card-body">
            <div v-if="chargementNotifications" class="text-center py-4">
              <div class="spinner-border text-info"></div>
            </div>
            <div v-else-if="notifications.length===0" class="text-center py-5">
              <i class="fas fa-bell fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">{{ $t('notifications.aucuneNotification') }}</h5>
              <p class="text-muted">{{ $t('notifications.messageVide') }}</p>
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
                      <h6 class="mb-1" :class="{'fw-bold': !n.lu}">{{ n.titre || $t('notifications.notification') }}</h6>
                      <p class="mb-2 text-muted">{{ n.message }}</p>
                      <small class="text-muted">
                        <i class="fas fa-clock me-1"></i>{{ formatDateRelative(n.date || n.createdAt) }}
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
                <h5 class="mb-0">{{ $t('profil.informations') }}</h5>
                <small class="text-muted">{{ $t('profil.gererProfil') }}</small>
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
                    <div class="form-text">{{ $t('profil.emailNonModifiable') }}</div>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">{{ $t('profil.adresse') }}</label>
                    <textarea class="form-control" rows="3" v-model="utilisateur.adresse"
                              :placeholder="$t('profil.adressePlaceholder')"></textarea>
                  </div>
                  <button class="btn btn-primary" :disabled="sauvegardeProfil">
                    <span v-if="sauvegardeProfil" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="fas fa-save me-2"></i>
                    {{ sauvegardeProfil ? $t('commun.sauvegarde') : $t('commun.enregistrer') }}
                  </button>
                </form>
              </div>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="card border-0 shadow-sm">
              <div class="card-header bg-white">
                <h6 class="mb-0">{{ $t('profil.statistiques') }}</h6>
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
                      <small class="text-muted">{{ $t('projets.projets') }}</small>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="bg-light rounded p-2">
                      <div class="h5 mb-0 text-success">{{ mesTachesTerminees.length }}</div>
                      <small class="text-muted">{{ $t('profil.tachesOK') }}</small>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="bg-light rounded p-2">
                      <div class="h5 mb-0 text-warning">{{ mesTachesEnCours.length }}</div>
                      <small class="text-muted">{{ $t('profil.enCours') }}</small>
                    </div>
                  </div>
                  <div class="col-6">
                    <div class="bg-light rounded p-2">
                      <div class="h5 mb-0 text-info">{{ tauxCompletion }}%</div>
                      <small class="text-muted">{{ $t('profil.reussite') }}</small>
                    </div>
                  </div>
                </div>
                <hr>
                <p class="mb-2"><strong>{{ $t('profil.membreDepuis') }}:</strong> {{ formatDate(utilisateur?.date_inscription || utilisateur?.dateInscription) }}</p>
                <p class="mb-0"><strong>{{ $t('profil.derniereConnexion') }}:</strong> {{ $t('profil.maintenant') }}</p>

                <!-- Call-to-action Premium -->
                <div class="mt-3 p-3 bg-gradient-warning rounded text-center">
                  <i class="fas fa-crown fa-2x mb-2"></i>
                  <h6 class="mb-1">{{ $t('membre.premium.titre') }}</h6>
                  <small class="d-block mb-2">{{ $t('membre.premium.description') }}</small>
                  <router-link to="/abonnement-premium" class="btn btn-sm btn-dark">
                    {{ $t('membre.premium.bouton') }}
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
                        <strong class="small">{{ m.utilisateurNom || m.auteur?.prenom || $t('commun.membre') }}</strong>
                        <span class="text-muted ms-2 small">{{ formatTime(m.date_envoi || m.dateEnvoi) }}</span>
                      </div>
                      <div>{{ m.contenu }}</div>
                    </div>
                  </div>
                </div>
                <div v-if="messagesChat.length===0" class="text-center text-muted py-4">
                  <i class="fas fa-comments fa-2x mb-2"></i>
                  <p>{{ $t('collaboration.commencerDiscussion') }}</p>
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
                <i class="fas fa-comment me-2"></i>{{ $t('commentaires.ajouter') }}
              </h5>
              <button class="btn-close" @click="fermerCommentaire"></button>
            </div>
            <div class="modal-body">
              <h6>{{ tacheCommentaire?.titre }}</h6>
              <p class="text-muted">{{ tacheCommentaire?.description }}</p>
              <hr>
              <div class="mb-3">
                <label class="form-label fw-semibold">{{ $t('commentaires.votreCommentaire') }}</label>
                <textarea class="form-control" rows="4" v-model="nouveauCommentaire"
                          :placeholder="$t('commentaires.placeholder')"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="fermerCommentaire">{{ $t('commun.annuler') }}</button>
              <button class="btn btn-primary" @click="envoyerCommentaire" :disabled="!nouveauCommentaire.trim()">
                <i class="fas fa-comment me-1"></i>{{ $t('commentaires.publier') }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- MODAL CRÉATION TÂCHE -->
      <div v-if="creationTacheOuverte" class="modal d-block" style="background:rgba(0,0,0,.5);z-index:1061">
        <div class="modal-dialog modal-lg">
          <div class="modal-content border-0 shadow">
            <div class="modal-header bg-gradient-primary text-white">
              <h5 class="modal-title">
                <i class="fas fa-plus-circle me-2"></i>{{ $t('taches.nouvelleTache') }}
              </h5>
              <button class="btn-close btn-close-white" @click="fermerCreationTache"></button>
            </div>
            <div class="modal-body">
              <!-- Projet -->
              <div class="mb-3">
                <label class="form-label fw-semibold">
                  <i class="fas fa-project-diagram me-2 text-primary"></i>
                  {{ $t('projets.projet') }} *
                </label>
                <select v-model="nouvelleTache.projetId" class="form-select form-select-lg" required>
                  <option value="">{{ $t('projets.choisirProjet') }}</option>
                  <option v-for="p in mesProjets" :key="p.id" :value="p.id">{{ p.titre }}</option>
                </select>
              </div>

              <!-- Titre -->
              <div class="mb-3">
                <label class="form-label fw-semibold">
                  <i class="fas fa-heading me-2 text-primary"></i>
                  {{ $t('taches.titre') }} *
                </label>
                <input
                  type="text"
                  v-model.trim="nouvelleTache.titre"
                  class="form-control form-control-lg"
                  :placeholder="$t('taches.saisieTitre')"
                  required
                  maxlength="200"
                >
                <div class="form-text">{{ nouvelleTache.titre.length }}/200 caractères</div>
              </div>

              <!-- Description -->
              <div class="mb-3">
                <label class="form-label fw-semibold">
                  <i class="fas fa-align-left me-2 text-primary"></i>
                  {{ $t('taches.description') }}
                </label>
                <textarea
                  v-model.trim="nouvelleTache.description"
                  class="form-control"
                  rows="4"
                  :placeholder="$t('taches.saisieDescription')"
                  maxlength="1000"
                ></textarea>
                <div class="form-text">{{ nouvelleTache.description.length }}/1000 caractères</div>
              </div>

              <!-- Priorité et Échéance -->
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-semibold">
                    <i class="fas fa-flag me-2 text-primary"></i>
                    {{ $t('taches.priorite') }}
                  </label>
                  <select v-model="nouvelleTache.priorite" class="form-select">
                    <option value="BASSE">{{ $t('taches.priorites.basse') }}</option>
                    <option value="MOYENNE">{{ $t('taches.priorites.moyenne') }}</option>
                    <option value="HAUTE">{{ $t('taches.priorites.haute') }}</option>
                    <option value="CRITIQUE">{{ $t('taches.priorites.critique') }}</option>
                  </select>
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label fw-semibold">
                    <i class="fas fa-calendar-alt me-2 text-primary"></i>
                    {{ $t('taches.echeance') }}
                  </label>
                  <input
                    type="date"
                    v-model="nouvelleTache.dateEcheance"
                    class="form-control"
                    :min="new Date().toISOString().split('T')[0]"
                  >
                </div>
              </div>

              <!-- Conseil -->
              <div class="alert alert-info mb-0">
                <small>
                  <i class="fas fa-info-circle me-1"></i>
                  La tâche sera créée en brouillon. Vous pourrez ensuite la soumettre pour validation.
                </small>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="fermerCreationTache">
                <i class="fas fa-times me-1"></i>
                {{ $t('commun.annuler') }}
              </button>
              <button
                class="btn btn-primary"
                @click="confirmerCreationTache"
                :disabled="!nouvelleTache.projetId || !nouvelleTache.titre.trim() || creationEnCours"
              >
                <span v-if="creationEnCours" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="fas fa-check me-2"></i>
                {{ creationEnCours ? $t('taches.creationEnCours') : $t('taches.creer') }}
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

export default {
  name: 'TableauDeBordMembre',
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
      erreurBackend: null,

      // ✅ NOUVEAU : Modal création tâche
      creationTacheOuverte: false,
      creationEnCours: false,
      nouvelleTache: {
        projetId: '',
        titre: '',
        description: '',
        priorite: 'MOYENNE',
        dateEcheance: ''
      }
    }
  },
  computed: {
    utilisateur() {
      return useAuthStore().user
    },
    projetsActifsCount() {
      return this.mesProjets.length
    },
    mesTachesEnCours() {
      return this.mesTaches.filter(t => ['BROUILLON', 'EN_ATTENTE_VALIDATION'].includes(t.statut))
    },
    mesTachesTerminees() {
      return this.mesTaches.filter(t => t.statut === 'TERMINE')
    },
    tachesUrgentes() {
      return this.mesTaches.filter(t => t.priorite === 'HAUTE' && t.statut !== 'TERMINE')
    },
    notificationsNonLues() {
      return this.notifications.filter(n => !n.lu)
    },
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
      if (this.filtreProjetStatut === 'termine') {
        arr = arr.filter(p => p.date_fin || p.dateFin)
      }
      return arr
    },
    tachesFiltrees() {
      let arr = [...this.mesTaches]
      if (this.filtreTacheStatut) arr = arr.filter(t => t.statut === this.filtreTacheStatut)
      if (this.filtreTachePriorite) arr = arr.filter(t => t.priorite === this.filtreTachePriorite)
      return arr
    },
    tachesBrouillon() {
      return this.tachesFiltrees.filter(t => t.statut === 'BROUILLON')
    },
    tachesEnValidation() {
      return this.tachesFiltrees.filter(t => t.statut === 'EN_ATTENTE_VALIDATION')
    },
    tachesTerminees() {
      return this.tachesFiltrees.filter(t => t.statut === 'TERMINE')
    }
  },
  async mounted() {
    const store = useAuthStore()
    if (!store.user && localStorage.getItem('user')) {
      try {
        store.user = JSON.parse(localStorage.getItem('user'))
      } catch (e) {
        console.error(e)
      }
    }
    if (!store.user || store.user.role !== 'MEMBRE') {
      if (store.user?.role === 'CHEF_PROJET') {
        await this.$router.push('/tableau-bord-chef-projet')
      } else if (store.user?.role === 'ADMINISTRATEUR') {
        await this.$router.push('/admin/tableau-de-bord')
      } else {
        await this.$router.push('/connexion')
      }
      return
    }
    await this.chargerToutesDonnees()
  },
  methods: {
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
        this.erreurBackend = this.$t('erreurs.chargementDonnees')
      } finally {
        this.chargementGlobal = false
      }
    },
    async chargerMesProjets() {
      this.chargementProjets = true
      try {
        const r = await projectAPI.byUser(this.utilisateur.id)
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
        const r = await taskAPI.byUser(this.utilisateur.id)
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
        const r = await notificationAPI.list(this.utilisateur.id)
        this.notifications = Array.isArray(r.data) ? r.data : (Array.isArray(r.data?.content) ? r.data.content : [])
      } catch (e) {
        console.error(e)
        this.notifications = []
      } finally {
        this.chargementNotifications = false
      }
    },
    consulterProjet(p) {
      this.$router.push(`/projet/${p.id}`)
    },

    // ✅ MODIFIÉ : Ouvre le modal au lieu de naviguer
    async creerTache() {
      if (this.mesProjets.length === 0) {
        alert(this.$t('membre.taches.erreurAucunProjet'))
        return
      }
      this.creationTacheOuverte = true
      this.resetFormulaireTache()
    },

    // ✅ NOUVEAU : Reset du formulaire
    resetFormulaireTache() {
      this.nouvelleTache = {
        projetId: '',
        titre: '',
        description: '',
        priorite: 'MOYENNE',
        dateEcheance: ''
      }
    },

    // ✅ NOUVEAU : Fermer le modal
    fermerCreationTache() {
      this.creationTacheOuverte = false
      this.resetFormulaireTache()
    },

    // ✅ NOUVEAU : Confirmer la création
    async confirmerCreationTache() {
      if (!this.nouvelleTache.projetId || !this.nouvelleTache.titre.trim()) return

      this.creationEnCours = true
      try {
        const payload = {
          titre: this.nouvelleTache.titre.trim(),
          description: this.nouvelleTache.description.trim() || null,
          priorite: this.nouvelleTache.priorite,
          dateEcheance: this.nouvelleTache.dateEcheance || null,
          statut: 'BROUILLON',
          projetId: parseInt(this.nouvelleTache.projetId),
          utilisateurId: this.utilisateur.id
        }

        await taskAPI.create(payload)
        alert(this.$t('taches.creee'))

        // Recharger les tâches et fermer le modal
        await this.chargerMesTaches()
        this.fermerCreationTache()
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.creationTache'))
      } finally {
        this.creationEnCours = false
      }
    },

    async soumettreValidation(tache) {
      if (!confirm(this.$t('taches.confirmerSoumission'))) return
      try {
        await taskAPI.updateStatus(tache.id, 'EN_ATTENTE_VALIDATION')
        tache.statut = 'EN_ATTENTE_VALIDATION'
        alert(this.$t('taches.soumiseValidation'))
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.soumissionTache'))
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
          targetType: 'TACHE',
          targetId: this.tacheCommentaire.id
        })
        alert(this.$t('commentaires.ajouteSucces'))
        this.fermerCommentaire()
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.ajoutCommentaire'))
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
        const r = await messagesAPI.byProjet(id)
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
        const r = await messagesAPI.send({
          projetId: this.projetChatActuel.id,
          contenu: this.nouveauMessage,
          type: 'TEXT'
        })
        this.messagesChat.push(r.data)
        this.nouveauMessage = ''
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.envoyerMessage'))
      } finally {
        this.envoyantMessage = false
      }
    },
    fermerChat() {
      this.chatOuvert = false
      this.projetChatActuel = null
      this.messagesChat = []
    },
    estMonMessage(m) {
      return (m.utilisateur_id || m.utilisateurId) === this.utilisateur.id
    },
    getMessageClass(m) {
      return this.estMonMessage(m) ? 'bg-primary text-white ms-auto' : 'bg-white border shadow-sm'
    },
    async marquerNotificationLue(n) {
      try {
        await notificationAPI.markAsRead(n.id, this.utilisateur.id)
        n.lu = true
      } catch (e) {
        console.error(e)
      }
    },
    async marquerToutesLues() {
      try {
        await notificationAPI.markAllAsRead(this.utilisateur.id)
        this.notifications.forEach(n => n.lu = true)
        alert(this.$t('notifications.toutesMarquees'))
      } catch (e) {
        console.error(e)
      }
    },
    async supprimerNotification(n) {
      if (!confirm(this.$t('notifications.confirmerSuppression'))) return
      try {
        await notificationAPI.delete(n.id, this.utilisateur.id)
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
          adresse: this.utilisateur.adresse
        })
        const store = useAuthStore()
        store.user = { ...store.user, ...this.utilisateur }
        localStorage.setItem('user', JSON.stringify(store.user))
        alert(this.$t('profil.misAJour'))
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.sauvegardeProfile'))
      } finally {
        this.sauvegardeProfil = false
      }
    },
    getProjetNom(id) {
      const p = this.mesProjets.find(x => x.id == id)
      return p ? p.titre : this.$t('projets.projetInconnu')
    },
    getChefProjetNom(createurId) {
      const projet = this.mesProjets.find(p => (p.id_createur || p.idCreateur) == createurId)
      if (projet?.createur) {
        const c = projet.createur
        return `${c.prenom || c.firstName || ''} ${c.nom || c.lastName || ''}`.trim()
      }
      return `${this.$t('projets.chefProjet')} #${createurId}`
    },
    getMesTachesDansProjet(projetId) {
      return this.mesTaches.filter(t => (t.id_projet || t.projetId) == projetId)
    },
    getTachesEnCoursProjet(projetId) {
      return this.mesTaches.filter(t =>
        (t.id_projet || t.projetId) == projetId &&
        ['BROUILLON', 'EN_ATTENTE_VALIDATION'].includes(t.statut)
      ).length
    },
    getProjetProgression(id) {
      const tachesProjet = this.mesTaches.filter(t => (t.id_projet || t.projetId) == id)
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
    formatDate(date) {
      if (!date) return '—'
      return new Date(date).toLocaleDateString(this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US')
    },
    formatDateRelative(date) {
      if (!date) return '—'
      const now = new Date()
      const diff = now - new Date(date)
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(minutes / 60)
      const days = Math.floor(hours / 24)
      if (minutes < 1) return this.$t('temps.maintenant')
      if (minutes < 60) return `${this.$t('temps.ilYa')} ${minutes}${this.$t('temps.min')}`
      if (hours < 24) return `${this.$t('temps.ilYa')} ${hours}${this.$t('temps.h')}`
      if (days < 7) return `${this.$t('temps.ilYa')} ${days}${this.$t('temps.j')}`
      return this.formatDate(date)
    },
    formatTime(timestamp) {
      if (!timestamp) return ''
      return new Date(timestamp).toLocaleTimeString(this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US', {
        hour: '2-digit',
        minute: '2-digit'
      })
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
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
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
  cursor: pointer;
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
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
  background-color: rgba(0, 123, 255, 0.05);
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

.cursor-pointer {
  cursor: pointer;
}
</style>
