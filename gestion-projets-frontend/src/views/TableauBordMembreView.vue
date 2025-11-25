<template>
  <div class="container-fluid py-3">
    <!-- ========== HEADER ========== -->
    <div class="member-header mb-4">
      <div class="d-flex justify-content-between align-items-center flex-wrap">
        <div>
          <h1 class="mb-1 d-flex align-items-center gap-2">
            <i class="fas fa-user-circle text-light"></i>
            {{ $t('tableauBord.membre.titre') }}
          </h1>
          <p class="text-white-75 mb-0">
            {{ $t('commun.bienvenue') }}
            {{ utilisateur?.prenom || utilisateur?.firstName }}
            {{ utilisateur?.nom || utilisateur?.lastName }}
          </p>
        </div>

        <div class="mt-3 mt-md-0">
          <button class="btn btn-outline-light"
                  @click="seDeconnecter"
                  :title="$t('nav.deconnexion')">
            <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
          </button>
        </div>
      </div>
    </div>

    <!-- ========== PROMO PREMIUM ========== -->
    <div v-if="!chargementGlobal && !abonnementActif"
         class="alert alert-warning border-0 shadow-sm mb-4 d-flex justify-content-between align-items-center">
      <div>
        <h6 class="mb-1">
          <i class="fas fa-star text-warning me-2"></i>{{ $t('membre.promoPremium.titre') }}
        </h6>
        <small class="text-muted">{{ $t('membre.promoPremium.description') }}</small>
      </div>
      <router-link to="/abonnement-premium"
                   class="btn btn-warning"
                   :title="$t('membre.promoPremium.bouton')">
        <i class="fas fa-arrow-right me-1"></i>{{ $t('membre.promoPremium.bouton') }}
      </router-link>
    </div>

    <!-- ========== TOAST NOTIFICATIONS ========== -->
    <div v-if="toastMessage"
         class="position-fixed top-0 end-0 p-3"
         style="z-index:9999">
      <div class="toast show" :class="'bg-' + toastType">
        <div class="toast-body text-white d-flex align-items-center">
          <i class="fas me-2" :class="{
            'fa-check-circle': toastType === 'success',
            'fa-exclamation-circle': toastType === 'error',
            'fa-info-circle': toastType === 'info'
          }"></i>
          {{ toastMessage }}
        </div>
      </div>
    </div>

    <!-- ========== ERREUR / CHARGEMENT ========== -->
    <div v-if="erreurBackend" class="alert alert-danger d-flex justify-content-between align-items-center">
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button class="btn btn-sm btn-outline-danger"
              @click="chargerToutesDonnees"
              :title="$t('commun.actualiser')">
        <i class="fas fa-sync-alt me-1"></i>{{ $t('commun.actualiser') }}
      </button>
    </div>

    <div v-else-if="chargementGlobal" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="text-muted mt-2">{{ $t('commun.chargement') }}</p>
    </div>

    <!-- ========== CONTENU PRINCIPAL ========== -->
    <div v-else>
      <!-- ========== KPIs ========== -->
      <div class="row g-3 mb-4">
        <!-- KPI 1: Projets -->
        <div class="col-md-3">
          <div class="card kpi-card border-0 shadow-sm h-100"
               @click="onglet = 'projets'"
               :title="$t('tooltips.kpiProjets')">
            <div class="card-body text-center">
              <i class="fas fa-project-diagram fa-2x mb-2 text-primary"></i>
              <h3 class="fw-bold mb-0">{{ mesProjets.length }}</h3>
              <p class="text-muted mb-0 small">{{ $t('membre.kpi.projetsRejoints') }}</p>
              <small class="text-primary">
                {{ mesProjets.filter(p=>p.statut==='ACTIF').length }} {{ $t('commun.actifs') }}
              </small>
            </div>
          </div>
        </div>

        <!-- KPI 2: Tâches assignées -->
        <div class="col-md-3">
          <div class="card kpi-card border-0 shadow-sm h-100"
               @click="onglet = 'taches'"
               :title="$t('tooltips.kpiTaches')">
            <div class="card-body text-center">
              <i class="fas fa-tasks fa-2x mb-2 text-warning"></i>
              <h3 class="fw-bold mb-0">{{ mesTaches.length }}</h3>
              <p class="text-muted mb-0 small">{{ $t('membre.kpi.tachesAttribuees') }}</p>
              <small class="text-warning">
                {{ tachesEnCours.length }} {{ $t('taches.enCours') }}
              </small>
            </div>
          </div>
        </div>

        <!-- KPI 3: Tâches terminées -->
        <div class="col-md-3">
          <div class="card kpi-card border-0 shadow-sm h-100"
               @click="onglet = 'taches'"
               :title="$t('tooltips.kpiTachesTerminees')">
            <div class="card-body text-center">
              <i class="fas fa-check-circle fa-2x mb-2 text-success"></i>
              <h3 class="fw-bold mb-0">{{ mesTaches.filter(t=>t.statut==='TERMINE').length }}</h3>
              <p class="text-muted mb-0 small">{{ $t('membre.kpi.tachesTerminees') }}</p>
              <small class="text-success">{{ tauxReussite }}% {{ $t('membre.kpi.reussite') }}</small>
            </div>
          </div>
        </div>

        <!-- KPI 4: Notifications -->
        <div class="col-md-3">
          <div class="card kpi-card border-0 shadow-sm h-100"
               @click="onglet = 'notifications'"
               :title="$t('tooltips.kpiNotifications')">
            <div class="card-body text-center">
              <i class="fas fa-bell fa-2x mb-2 text-info"></i>
              <h3 class="fw-bold mb-0">{{ notificationsNonLues.length }}</h3>
              <p class="text-muted mb-0 small">{{ $t('membre.kpi.notifications') }}</p>
              <small class="text-info">{{ notifications.length }} {{ $t('commun.total') }}</small>
            </div>
          </div>
        </div>
      </div>

      <!-- ========== NAV ONGLETS ========== -->
      <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap">
        <ul class="nav nav-pills bg-light rounded p-2">
          <li class="nav-item">
            <a class="nav-link"
               :class="{active:onglet==='projets'}"
               @click="onglet='projets'"
               href="javascript:void(0)"
               :title="$t('tooltips.voirProjets')">
              <i class="fas fa-folder-open me-2"></i>{{ $t('nav.mesProjets') }}
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link"
               :class="{active:onglet==='taches'}"
               @click="onglet='taches'"
               href="javascript:void(0)"
               :title="$t('tooltips.voirTaches')">
              <i class="fas fa-tasks me-2"></i>{{ $t('nav.mesTaches') }}
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link"
               :class="{active:onglet==='notifications'}"
               @click="onglet='notifications'"
               href="javascript:void(0)"
               :title="$t('tooltips.voirNotifications')">
              <i class="fas fa-bell me-2"></i>{{ $t('nav.notifications') }}
              <span v-if="notificationsNonLues.length > 0" class="badge bg-danger ms-1">
                {{ notificationsNonLues.length }}
              </span>
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link"
               :class="{active:onglet==='collaboration'}"
               @click="onglet='collaboration'"
               href="javascript:void(0)"
               :title="$t('tooltips.voirChat')">
              <i class="fas fa-comments me-2"></i>{{ $t('nav.collaboration') }}
            </a>
          </li>
        </ul>
        <router-link to="/profil"
                     class="btn btn-link text-decoration-none"
                     :title="$t('tooltips.voirProfil')">
          <i class="fas fa-user me-1"></i>{{ $t('membre.profil') }}
        </router-link>
      </div>

      <!-- ========== ONGLET PROJETS ========== -->
      <div v-if="onglet==='projets'">
        <div class="card shadow-sm border-0">
          <div class="card-header bg-white">
            <h5 class="mb-0">{{ $t('membre.sections.projets') }}</h5>
            <small class="text-muted">{{ $t('membre.projets.description') }}</small>
          </div>
          <div class="card-body p-0">
            <div v-if="mesProjets.length===0" class="text-center text-muted py-5">
              <i class="fas fa-folder-open fa-3x mb-3"></i>
              <p>{{ $t('membre.projets.aucunProjet') }}</p>
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('projets.projet') }}</th>
                  <th>{{ $t('projets.chefProjet') }}</th>
                  <th>{{ $t('projets.statut') }}</th>
                  <th>{{ $t('projets.progression') }}</th>
                  <th>{{ $t('membre.mesTaches') }}</th>
                  <th>{{ $t('projets.derniereActivite') }}</th>
                  <th class="text-end">{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="p in mesProjets" :key="p.id">
                  <td>
                    <strong>{{ translateProjectTitle(p.titre) }}</strong><br>
                    <small class="text-muted">
                      {{ translateProjectDescription(p.description).substring(0, 50) }}...
                    </small>
                  </td>
                  <td>
                    <span v-if="p.createurNom || p.createurPrenom">
                      {{ p.createurPrenom || '' }} {{ p.createurNom || '' }}
                    </span>
                    <span v-else class="text-muted">{{ $t('commun.nonDefini') }}</span>
                  </td>
                  <td>
                    <span class="badge" :class="getStatutProjetClass(p.statut)">
                      {{ translateData('status', p.statut || 'ACTIF') }}
                    </span>
                  </td>
                  <td>
                    <div class="progress" style="height:6px;">
                      <div class="progress-bar bg-success"
                           role="progressbar"
                           :style="{width: progressionProjet(p.id)+'%'}"
                           :aria-valuenow="progressionProjet(p.id)"
                           aria-valuemin="0"
                           aria-valuemax="100"></div>
                    </div>
                    <small>{{ progressionProjet(p.id) }}%</small>
                  </td>
                  <td>
                    <span class="badge bg-warning text-dark">
                      {{ getTachesProjet(p.id) }}
                    </span>
                  </td>
                  <td>
                    <small class="text-muted">
                      {{ formatDate(p.dateModification || p.date_modification) }}
                    </small>
                  </td>
                  <td class="text-end">
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-primary"
                              @click="consulterProjet(p)"
                              :title="$t('tooltips.consulterProjet')">
                        <i class="fas fa-eye"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-success"
                              @click="ouvrirChatProjet(p)"
                              :title="$t('tooltips.ouvrirChat')">
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

      <!-- ========== À REMPLACER DANS <template> - ONGLET TÂCHES AMÉLIORÉ ========== -->
      <!-- Remplace tout le bloc : <div v-else-if="onglet==='taches'"> ... </div> par ceci : -->

      <div v-else-if="onglet==='taches'">
        <!-- ========== GRAPHIQUE ET STATS ========== -->
        <div class="row g-3 mb-3">
          <!-- Graphique -->
          <div class="col-md-4">
            <div class="card shadow-sm border-0 h-100">
              <div class="card-header bg-white">
                <h6 class="mb-0">
                  <i class="fas fa-chart-pie me-2"></i>Répartition des tâches
                </h6>
              </div>
              <div class="card-body">
                <canvas ref="chartCanvas" style="max-height:250px"></canvas>
              </div>
            </div>
          </div>

          <!-- Stats mini -->
          <div class="col-md-8">
            <div class="card shadow-sm border-0 h-100">
              <div class="card-header bg-white">
                <h6 class="mb-0">
                  <i class="fas fa-chart-bar me-2"></i>Aperçu rapide
                </h6>
              </div>
              <div class="card-body">
                <div class="row g-2 text-center">
                  <div class="col-6 col-md-3">
                    <div class="stat-mini p-3 rounded bg-light">
                      <i class="fas fa-file text-secondary fa-2x mb-2"></i>
                      <h4 class="mb-0">{{ statsTaskes.brouillon }}</h4>
                      <small class="text-muted">{{ $t('taches.statuts.brouillon') }}</small>
                    </div>
                  </div>
                  <div class="col-6 col-md-3">
                    <div class="stat-mini p-3 rounded bg-light">
                      <i class="fas fa-clock text-warning fa-2x mb-2"></i>
                      <h4 class="mb-0">{{ statsTaskes.enAttente }}</h4>
                      <small class="text-muted">{{ $t('taches.statuts.enAttente') }}</small>
                    </div>
                  </div>
                  <div class="col-6 col-md-3">
                    <div class="stat-mini p-3 rounded bg-light">
                      <i class="fas fa-check-circle text-success fa-2x mb-2"></i>
                      <h4 class="mb-0">{{ statsTaskes.termine }}</h4>
                      <small class="text-muted">{{ $t('taches.statuts.termine') }}</small>
                    </div>
                  </div>
                  <div class="col-6 col-md-3">
                    <div class="stat-mini p-3 rounded bg-light">
                      <i class="fas fa-times-circle text-danger fa-2x mb-2"></i>
                      <h4 class="mb-0">{{ statsTaskes.annule }}</h4>
                      <small class="text-muted">{{ $t('taches.statuts.annule') }}</small>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- ========== RECHERCHE ET FILTRES ========== -->
        <div class="card shadow-sm border-0 mb-3">
          <div class="card-body">
            <div class="row g-2 align-items-center">
              <!-- Barre de recherche -->
              <div class="col-md-6">
                <div class="input-group">
            <span class="input-group-text bg-white">
              <i class="fas fa-search text-muted"></i>
            </span>
                  <input
                    type="text"
                    class="form-control border-start-0"
                    v-model="rechercheTask"
                    placeholder="Rechercher une tâche..."
                    @input="filtrerTaches">
                  <button
                    v-if="rechercheTask"
                    class="btn btn-outline-secondary"
                    @click="rechercheTask = ''; filtrerTaches()">
                    <i class="fas fa-times"></i>
                  </button>
                </div>
              </div>

              <!-- Filtres rapides -->
              <div class="col-md-6">
                <div class="btn-group w-100" role="group">
                  <button
                    class="btn btn-sm"
                    :class="filtreStatut === 'TOUS' ? 'btn-primary' : 'btn-outline-primary'"
                    @click="filtreStatut = 'TOUS'; filtrerTaches()">
                    Toutes ({{ mesTaches.length }})
                  </button>
                  <button
                    class="btn btn-sm"
                    :class="filtreStatut === 'EN_COURS' ? 'btn-warning' : 'btn-outline-warning'"
                    @click="filtreStatut = 'EN_COURS'; filtrerTaches()">
                    En cours ({{ tachesEnCours.length }})
                  </button>
                  <button
                    class="btn btn-sm"
                    :class="filtreStatut === 'TERMINE' ? 'btn-success' : 'btn-outline-success'"
                    @click="filtreStatut = 'TERMINE'; filtrerTaches()">
                    Terminées ({{ statsTaskes.termine }})
                  </button>
                  <button
                    class="btn btn-sm"
                    :class="filtreStatut === 'EN_ATTENTE_VALIDATION' ? 'btn-info' : 'btn-outline-info'"
                    @click="filtreStatut = 'EN_ATTENTE_VALIDATION'; filtrerTaches()">
                    En attente ({{ statsTaskes.enAttente }})
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- ========== TABLEAU TÂCHES ========== -->
        <div class="card shadow-sm border-0">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('membre.sections.taches') }}</h5>
              <small class="text-muted">
                {{ tachesFiltrees.length }} résultat(s)
                <span v-if="rechercheTask || filtreStatut !== 'TOUS'"> · Filtré</span>
              </small>
            </div>
            <button
              class="btn btn-sm btn-outline-secondary"
              @click="reinitialiserFiltres"
              v-if="rechercheTask || filtreStatut !== 'TOUS'">
              <i class="fas fa-redo me-1"></i>Réinitialiser
            </button>
          </div>
          <div class="card-body">
            <div v-if="tachesFiltrees.length===0" class="text-center text-muted py-5">
              <i class="fas fa-3x mb-3" :class="rechercheTask || filtreStatut !== 'TOUS' ? 'fa-search' : 'fa-tasks'"></i>
              <p>{{ rechercheTask || filtreStatut !== 'TOUS' ? 'Aucun résultat trouvé' : $t('membre.taches.aucuneTache') }}</p>
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('taches.titre') }}</th>
                  <th>{{ $t('projets.projet') }}</th>
                  <th>{{ $t('taches.statut') }}</th>
                  <th>{{ $t('commentaires.titre') }}</th>
                  <th>{{ $t('taches.dateCreation') }}</th>
                  <th class="text-end">{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="t in tachesFiltrees" :key="t.id">
                  <td>
                    <strong>{{ t.titre }}</strong><br>
                    <small class="text-muted">{{ t.description }}</small>
                  </td>
                  <td>{{ getProjetNom(t.projetId || t.id_projet) }}</td>
                  <td>
              <span class="badge" :class="getStatutTacheClass(t.statut)">
                {{ translateData('taskStatus', t.statut) }}
              </span>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-outline-info position-relative"
                            @click="ouvrirCommentaires(t)"
                            :title="$t('commentaires.voir')">
                      <i class="fas fa-comments me-1"></i>
                      <span
                        v-if="commentairesParTache[normalizeId(t.id)]"
                        class="badge bg-info ms-1 animate-badge">
                  {{ getCommentairesTache(t.id).length }}
                </span>
                      <span v-else class="badge bg-secondary ms-1">
                  <i class="fas fa-spinner fa-spin fa-xs"></i>
                </span>
                    </button>
                  </td>
                  <td>
                    <small class="text-muted">
                      {{ formatDate(t.dateCreation || t.date_creation) }}
                    </small>
                  </td>
                  <td class="text-end">
                    <button
                      v-if="t.statut === 'BROUILLON'"
                      class="btn btn-sm btn-success"
                      @click="soumettreValidation(t)"
                      :title="$t('tooltips.soumettreValidation')">
                      <i class="fas fa-paper-plane me-1"></i>
                      {{ $t('taches.soumettre') }}
                    </button>
                    <span v-else-if="t.statut === 'EN_ATTENTE_VALIDATION'" class="text-info small">
                <i class="fas fa-hourglass-half me-1"></i>{{ $t('taches.enAttenteValidation') }}
              </span>
                    <span v-else-if="t.statut === 'TERMINE'" class="text-success small">
                <i class="fas fa-check-circle me-1"></i>{{ $t('taches.terminee') }}
              </span>
                    <span v-else class="text-muted small">
                {{ $t('taches.pasActionPossible') }}
              </span>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- ========== F15: ONGLET NOTIFICATIONS ========== -->
      <div v-else-if="onglet==='notifications'">
        <div class="card shadow-sm border-0">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ $t('membre.sections.notifications') }}</h5>
            <button
              class="btn btn-outline-secondary btn-sm"
              @click="marquerToutesLues"
              :disabled="notificationsNonLues.length===0"
              :title="$t('tooltips.marquerToutesLues')">
              <i class="fas fa-check-double me-1"></i>{{ $t('notifications.marquerToutesLues') }}
            </button>
          </div>
          <div class="card-body">
            <div v-if="notifications.length===0" class="text-center text-muted py-5">
              <i class="fas fa-bell fa-3x mb-3"></i>
              <p>{{ $t('membre.notifications.vide') }}</p>
            </div>
            <div v-else class="list-group">
              <div
                v-for="n in notifications"
                :key="n.id"
                class="list-group-item d-flex align-items-start gap-3 notification-item"
                :class="{'bg-light': !n.lu}">
                <div class="notification-icon rounded" :class="getNotificationIconClass(n.type)">
                  <i :class="getNotificationIcon(n.type)"></i>
                </div>
                <div class="flex-grow-1">
                  <div class="fw-semibold">{{ n.titre || n.title || $t('notifications.notification') }}</div>
                  <div class="small text-muted">{{ n.message || n.contenu }}</div>
                  <small class="text-muted">{{ formatDateRelative(n.date || n.createdAt) }}</small>
                </div>
                <div class="btn-group">
                  <button v-if="!n.lu"
                          class="btn btn-sm btn-outline-success"
                          @click="marquerNotificationLue(n)"
                          :title="$t('tooltips.marquerLue')">
                    <i class="fas fa-check"></i>
                  </button>
                  <button class="btn btn-sm btn-outline-danger"
                          @click="supprimerNotification(n)"
                          :title="$t('tooltips.supprimerNotification')">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ========== F14: ONGLET COLLABORATION ========== -->
      <div v-else-if="onglet==='collaboration'">
        <div class="row g-3">
          <div class="col-md-4">
            <div class="card border-0 shadow-sm h-100">
              <div class="card-header bg-white">
                <h6 class="mb-0">{{ $t('nav.mesProjets') }}</h6>
              </div>
              <div class="list-group list-group-flush">
                <button
                  v-for="p in mesProjets"
                  :key="p.id"
                  class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                  :class="{active: projetChatActuel && projetChatActuel.id===p.id}"
                  @click="ouvrirChatProjet(p)"
                  :title="$t('tooltips.ouvrirChat')">
                  <span class="text-truncate">{{ translateProjectTitle(p.titre) }}</span>
                  <span class="badge bg-secondary">{{ getMessagesNonLusProjet(p.id) }}</span>
                </button>
                <div v-if="mesProjets.length===0" class="text-center text-muted py-4">
                  {{ $t('projets.aucunProjet') }}
                </div>
              </div>
            </div>
          </div>

          <div class="col-md-8">
            <div class="card border-0 shadow-sm h-100">
              <div class="card-header bg-white d-flex align-items-center gap-2">
                <h6 class="mb-0">
                  <i class="fas fa-comments me-2"></i>
                  {{ projetChatActuel ? translateProjectTitle(projetChatActuel.titre) : $t('nav.collaboration') }}
                </h6>
              </div>

              <div class="card-body d-flex flex-column" style="height:420px">
                <div class="flex-grow-1 overflow-auto messages-container" ref="messagesContainer">
                  <div v-if="!projetChatActuel" class="text-center text-muted py-5">
                    {{ $t('projets.choisirProjet') }}
                  </div>

                  <div v-else>
                    <div v-if="messagesChat.length===0" class="text-center text-muted py-5">
                      {{ $t('chat.aucunMessage') }}
                    </div>

                    <div
                      v-for="m in messagesChat"
                      :key="m.id"
                      class="p-2 rounded mb-2 chat-bubble"
                      :class="getMessageClass(m)"
                      style="max-width:80%">
                      <div class="small opacity-75 mb-1">
                        {{ m.auteur?.prenom || m.auteur?.firstName || m.utilisateurNom || $t('commun.inconnu') }}
                        · {{ formatTime(m.dateEnvoi || m.date || m.createdAt) }}
                      </div>
                      <div>{{ m.contenu }}</div>
                    </div>
                  </div>
                </div>

                <div class="pt-2 border-top" v-if="projetChatActuel">
                  <div class="input-group">
                    <input
                      class="form-control"
                      v-model.trim="nouveauMessage"
                      :placeholder="$t('collaboration.ecrireMessage')"
                      @keyup.enter="envoyerMessage"
                      :title="$t('tooltips.ecrireMessage')">
                    <button class="btn btn-success"
                            :disabled="envoyantMessage || !nouveauMessage"
                            @click="envoyerMessage"
                            :title="$t('tooltips.envoyerMessage')">
                      <span v-if="envoyantMessage" class="spinner-border spinner-border-sm"></span>
                      <i v-else class="fas fa-paper-plane me-1"></i>{{ $t('commun.envoyer') }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== MODAL COMMENTAIRES (F12) - CORRIGÉE ========== -->
    <div v-if="modalCommentaires"
         class="modal d-block"
         style="background:rgba(0,0,0,.6);z-index:1060"
         @click.self="fermerModalCommentaires">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title">
              <i class="fas fa-comments me-2"></i>{{ $t('commentaires.titre') }}
            </h5>
            <button class="btn-close btn-close-white"
                    @click="fermerModalCommentaires"
                    :aria-label="$t('commun.fermer')"></button>
          </div>

          <div class="modal-body">
            <!-- Info tâche -->
            <div class="alert alert-info mb-3">
              <div><strong>{{ $t('taches.tache') }}:</strong> {{ tacheSelectionnee?.titre }}</div>
              <div><strong>{{ $t('projets.projet') }}:</strong> {{ getProjetNom(tacheSelectionnee?.projetId || tacheSelectionnee?.id_projet) }}</div>
            </div>

            <!-- Liste des commentaires -->
            <div v-if="chargementCommentaires" class="text-center py-4">
              <div class="spinner-border text-primary"></div>
              <p class="text-muted mt-2">{{ $t('commun.chargement') }}</p>
            </div>

            <div v-else-if="commentairesTache.length === 0" class="text-center py-4 text-muted">
              <i class="fas fa-comment-slash fa-3x mb-3"></i>
              <p>{{ $t('commentaires.aucunCommentaire') }}</p>
            </div>

            <div v-else class="comments-list mb-3" style="max-height:400px;overflow-y:auto">
              <div v-for="c in commentairesTache" :key="c.id" class="card mb-2">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start mb-2">
                    <div>
                      <strong>{{ c.auteurPrenom }} {{ c.auteurNom }}</strong>
                      <br>
                      <small class="text-muted">{{ formatDateRelative(c.date) }}</small>
                    </div>
                    <!-- Suppression uniquement pour CHEF_PROJET et ADMIN -->
                    <button v-if="peutSupprimerCommentaire(c)"
                            class="btn btn-sm btn-outline-danger"
                            @click="supprimerCommentaire(c.id)"
                            :disabled="suppressionEnCours"
                            :title="$t('commentaires.supprimer')">
                      <span v-if="suppressionEnCours" class="spinner-border spinner-border-sm"></span>
                      <i v-else class="fas fa-trash"></i>
                    </button>
                  </div>
                  <p class="mb-0 text-break">{{ c.contenu }}</p>
                </div>
              </div>
            </div>

            <!-- Formulaire nouveau commentaire -->
            <div class="border-top pt-3">
              <label class="form-label fw-bold">{{ $t('commentaires.nouveau') }}</label>
              <textarea
                class="form-control mb-2"
                v-model.trim="nouveauCommentaire"
                rows="3"
                :placeholder="$t('commentaires.placeholder')"
                maxlength="500"
                :disabled="envoyantCommentaire"></textarea>
              <small class="text-muted d-block mb-2">
                {{ nouveauCommentaire.length }}/500 caractères
              </small>
              <button
                class="btn btn-primary"
                @click="ajouterCommentaire"
                :disabled="!nouveauCommentaire || envoyantCommentaire">
                <span v-if="envoyantCommentaire" class="spinner-border spinner-border-sm me-1"></span>
                <i v-else class="fas fa-paper-plane me-1"></i>
                {{ $t('commentaires.ajouter') }}
              </button>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn btn-secondary" @click="fermerModalCommentaires">
              {{ $t('commun.fermer') }}
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { projectAPI, taskAPI, notificationAPI, messagesAPI, commentaireAPI } from '@/services/api'
import WebSocketService from '@/services/websocket.service.js'
import { useI18n } from 'vue-i18n'
import { useDataTranslation } from '@/composables/useDataTranslation'

const { t } = useI18n()
const { translateData, translateProjectTitle, translateProjectDescription } = useDataTranslation()
const router = useRouter()
const store = useAuthStore()

// ========== HELPERS ==========
const getUserSafe = () => {
  if (store.user) return store.user
  try {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  } catch {
    return null
  }
}

const normalizeId = (v) => v == null ? v : String(v).split(':')[0]

// ========== STATE ==========
const utilisateur = ref(getUserSafe())
const mesProjets = ref([])
const mesTaches = ref([])
const notifications = ref([])
const messagesChat = ref([])
const messagesParProjet = ref({})
const commentairesParTache = ref({})
const nouveauMessage = ref('')
const nouveauCommentaire = ref('')
const abonnementActif = ref(false)
const erreurBackend = ref('')
const chargementGlobal = ref(true)
const chargementCommentaires = ref(false)
const envoyantCommentaire = ref(false)
const suppressionEnCours = ref(false)
const onglet = ref('projets')
const projetChatActuel = ref(null)
const envoyantMessage = ref(false)
const subscribedTopics = new Set()
const messagesContainer = ref(null)
// ========== NOUVEAUX STATE POUR RECHERCHE ET FILTRES ==========
const rechercheTask = ref('')
const filtreStatut = ref('TOUS')
const tachesFiltrees = ref([])
const chartCanvas = ref(null)
let chartInstance = null

// Toast notifications
const toastMessage = ref('')
const toastType = ref('info') // 'success', 'error', 'info'
let toastTimeout = null

// Modal commentaires
const modalCommentaires = ref(false)
const tacheSelectionnee = ref(null)
const commentairesTache = ref([])

// ========== COMPUTED ==========
const tauxReussite = computed(() => {
  if (mesTaches.value.length === 0) return 0
  const terminees = mesTaches.value.filter(t => t.statut === 'TERMINE').length
  return Math.round((terminees / mesTaches.value.length) * 100)
})

const tachesEnCours = computed(() => {
  return mesTaches.value.filter(t =>
    t.statut === 'BROUILLON' || t.statut === 'EN_ATTENTE_VALIDATION'
  )
})

const notificationsNonLues = computed(() => {
  return notifications.value.filter(n => !n.lu)
})

// ========== STATS POUR GRAPHIQUE ==========
const statsTaskes = computed(() => {
  return {
    brouillon: mesTaches.value.filter(t => t.statut === 'BROUILLON').length,
    enAttente: mesTaches.value.filter(t => t.statut === 'EN_ATTENTE_VALIDATION').length,
    termine: mesTaches.value.filter(t => t.statut === 'TERMINE').length,
    annule: mesTaches.value.filter(t => t.statut === 'ANNULE').length
  }
})
// ========== RECHERCHE ET FILTRES ==========
const filtrerTaches = () => {
  let resultats = [...mesTaches.value]

  if (filtreStatut.value !== 'TOUS') {
    if (filtreStatut.value === 'EN_COURS') {
      resultats = resultats.filter(t =>
        t.statut === 'BROUILLON' || t.statut === 'EN_ATTENTE_VALIDATION'
      )
    } else {
      resultats = resultats.filter(t => t.statut === filtreStatut.value)
    }
  }

  if (rechercheTask.value.trim()) {
    const terme = rechercheTask.value.toLowerCase()
    resultats = resultats.filter(t =>
      t.titre?.toLowerCase().includes(terme) ||
      t.description?.toLowerCase().includes(terme) ||
      getProjetNom(t.projetId || t.id_projet).toLowerCase().includes(terme)
    )
  }

  tachesFiltrees.value = resultats
}

const reinitialiserFiltres = () => {
  rechercheTask.value = ''
  filtreStatut.value = 'TOUS'
  filtrerTaches()
}

// ========== GRAPHIQUE CHART.JS ==========
const creerGraphique = async () => {
  if (!chartCanvas.value) return

  if (!window.Chart) {
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js'
    script.onload = () => initialiserGraphique()
    document.head.appendChild(script)
  } else {
    initialiserGraphique()
  }
}

const initialiserGraphique = () => {
  if (chartInstance) {
    chartInstance.destroy()
  }

  const ctx = chartCanvas.value.getContext('2d')
  const Chart = window.Chart

  chartInstance = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: [
        t('taches.statuts.brouillon'),
        t('taches.statuts.enAttente'),
        t('taches.statuts.termine'),
        t('taches.statuts.annule')
      ],
      datasets: [{
        data: [
          statsTaskes.value.brouillon,
          statsTaskes.value.enAttente,
          statsTaskes.value.termine,
          statsTaskes.value.annule
        ],
        backgroundColor: ['#6c757d', '#ffc107', '#28a745', '#dc3545'],
        borderWidth: 2,
        borderColor: '#fff'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'bottom',
          labels: { padding: 10, font: { size: 11 } }
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              const label = context.label || ''
              const value = context.parsed || 0
              const total = context.dataset.data.reduce((a, b) => a + b, 0)
              const percentage = total > 0 ? Math.round((value / total) * 100) : 0
              return `${label}: ${value} (${percentage}%)`
            }
          }
        }
      }
    }
  })
}
// ========== TOAST SYSTEM ==========
const showToast = (message, type = 'info') => {
  if (toastTimeout) {
    clearTimeout(toastTimeout)
  }

  toastMessage.value = message
  toastType.value = type

  toastTimeout = setTimeout(() => {
    toastMessage.value = ''
  }, 3000)
}

// ========== CHARGEMENT DONNÉES (CORRIGÉ AVEC PRÉCHARGEMENT COMMENTAIRES) ==========
const chargerToutesDonnees = async () => {
  chargementGlobal.value = true
  erreurBackend.value = ''

  try {
    if (!utilisateur.value || !utilisateur.value.id) {
      console.warn('[Load] Utilisateur non défini, récupération...')
      utilisateur.value = getUserSafe()

      if (!utilisateur.value || !utilisateur.value.id) {
        console.error('[Load] Impossible de récupérer utilisateur')
        erreurBackend.value = t('erreurs.sessionExpiree')
        chargementGlobal.value = false
        setTimeout(() => router.push('/connexion'), 2000)
        return
      }
    }

    const userId = normalizeId(utilisateur.value.id)
    console.log('[Load] Chargement données userId:', userId)

    const [pRes, tRes, nRes] = await Promise.allSettled([
      projectAPI.byUser(userId),
      taskAPI.byUser(userId),
      notificationAPI.list(userId)
    ])

    mesProjets.value = pRes.status === 'fulfilled' && Array.isArray(pRes.value?.data)
      ? pRes.value.data.map(p => ({ ...p, id: normalizeId(p.id) }))
      : []

    mesTaches.value = tRes.status === 'fulfilled' && Array.isArray(tRes.value?.data)
      ? tRes.value.data
        .map(t => ({
          ...t,
          id: normalizeId(t.id),
          projetId: normalizeId(t.projetId || t.id_projet || t.idProjet),
          id_projet: normalizeId(t.id_projet || t.projetId || t.idProjet),
          idProjet: normalizeId(t.idProjet || t.projetId || t.id_projet)
        }))
      : []
    //  Charger les projets manquants des tâches
    const projetsManquants = new Set()
    mesTaches.value.forEach(t => {
      const projetId = t.projetId || t.id_projet || t.idProjet
      if (projetId && !mesProjets.value.some(p => normalizeId(p.id) == normalizeId(projetId))) {
        projetsManquants.add(projetId)
      }
    })

    for (const projetId of projetsManquants) {
      try {
        const pRes = await projectAPI.getById(projetId)
        if (pRes.data) {
          mesProjets.value.push({ ...pRes.data, id: normalizeId(pRes.data.id) })
          console.log('[Load]  Projet manquant chargé:', pRes.data.titre)
        }
      } catch (e) {
        console.warn('[Load]  Impossible de charger projet:', projetId)
      }
    }

    notifications.value = nRes.status === 'fulfilled' && Array.isArray(nRes.value?.data)
      ? nRes.value.data
      : []

    // Précharger les commentaires de chaque tâche
    console.log('[F12]  Préchargement des commentaires pour', mesTaches.value.length, 'tâches')

    const commentairesPromises = mesTaches.value.map(async (tache) => {
      try {
        const tacheIdNormalized = normalizeId(tache.id)
        const response = await commentaireAPI.getByTache(tacheIdNormalized)
        commentairesParTache.value[tacheIdNormalized] = Array.isArray(response.data) ? response.data : []
        console.log(`[F12]  Tâche ${tacheIdNormalized}: ${commentairesParTache.value[tacheIdNormalized].length} commentaires`)
      } catch (e) {
        console.warn(`[F12]  Erreur chargement commentaires tâche ${tache.id}:`, e)
        commentairesParTache.value[normalizeId(tache.id)] = []
      }
    })

    await Promise.allSettled(commentairesPromises)

    console.log('[Load]  Données chargées:', {
      projets: mesProjets.value.length,
      taches: mesTaches.value.length,
      notifications: notifications.value.length,
      commentaires: Object.keys(commentairesParTache.value).length
    })

    const erreurs = [pRes, tRes, nRes].filter(r => r.status === 'rejected')
    if (erreurs.length === 3) {
      erreurBackend.value = t('erreurs.chargementDonnees')
      console.error('[Load] Toutes les API ont échoué')
    } else if (erreurs.length > 0) {
      console.warn('[Load] Erreurs partielles:', erreurs.map(e => e.reason?.message))
    }

  } catch (e) {
    console.error('[Load] Erreur critique:', e)
    erreurBackend.value = t('erreurs.chargementDonnees')
  } finally {
    chargementGlobal.value = false
  }
}

const chargerMessagesProjet = async (projetId) => {
  try {
    const r = await messagesAPI.byProjet(projetId)
    messagesChat.value = Array.isArray(r.data) ? r.data : []
    messagesParProjet.value[projetId] = messagesChat.value

    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error('[Chat] Erreur chargement messages:', e)
    messagesChat.value = []
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// ========== COMMENTAIRES (F12) - CONFORMES AU BACKEND ==========
const chargerCommentairesTache = async (tacheId) => {
  chargementCommentaires.value = true
  try {
    const tacheIdNormalized = normalizeId(tacheId)
    console.log(`[F12] GET /api/commentaires/tache/${tacheIdNormalized}`)

    const response = await commentaireAPI.getByTache(tacheIdNormalized)

    commentairesTache.value = Array.isArray(response.data) ? response.data : []
    commentairesParTache.value[tacheIdNormalized] = commentairesTache.value

    console.log(`[F12] ${commentairesTache.value.length} commentaires chargés`)
  } catch (e) {
    console.error('[F12] Erreur chargement commentaires:', e)
    commentairesTache.value = []
    showToast(t('erreurs.chargementCommentaires'), 'error')
  } finally {
    chargementCommentaires.value = false
  }
}

const ouvrirCommentaires = async (tache) => {
  tacheSelectionnee.value = tache
  modalCommentaires.value = true
  await chargerCommentairesTache(tache.id)

  // ⭐ AJOUT : Écoute WebSocket des nouveaux commentaires
  const tacheId = normalizeId(tache.id)
  const topicCommentaires = `/topic/tache/${tacheId}/commentaires`

  if (!subscribedTopics.has(topicCommentaires)) {
    WebSocketService.subscribeToTacheCommentaires(tacheId, (nouveauCommentaire) => {
      console.log('[F12] ✅ Nouveau commentaire temps réel:', nouveauCommentaire)

      // Ajouter à la modal si c'est la tâche actuellement ouverte
      if (tacheSelectionnee.value && normalizeId(tacheSelectionnee.value.id) === tacheId) {
        const existe = commentairesTache.value.some(c => c.id === nouveauCommentaire.id)
        if (!existe) {
          commentairesTache.value.push(nouveauCommentaire)
        }
      }

      // Mettre à jour le cache
      if (!commentairesParTache.value[tacheId]) {
        commentairesParTache.value[tacheId] = []
      }
      const existeDansCache = commentairesParTache.value[tacheId].some(c => c.id === nouveauCommentaire.id)
      if (!existeDansCache) {
        commentairesParTache.value[tacheId].push(nouveauCommentaire)
      }
    })

    // Écoute des suppressions
    WebSocketService.subscribeToTacheCommentairesSuppression(tacheId, (commentaireId) => {
      console.log('[F12]  Suppression commentaire temps réel:', commentaireId)

      // Retirer de la modal
      commentairesTache.value = commentairesTache.value.filter(c => c.id !== commentaireId)

      // Retirer du cache
      if (commentairesParTache.value[tacheId]) {
        commentairesParTache.value[tacheId] = commentairesParTache.value[tacheId].filter(c => c.id !== commentaireId)
      }
    })

    subscribedTopics.add(topicCommentaires)
    console.log('[F12]  Abonné aux commentaires tâche', tacheId)
  }
}

const fermerModalCommentaires = () => {

  if (tacheSelectionnee.value) {
    const tacheId = normalizeId(tacheSelectionnee.value.id)
    WebSocketService.unsubscribeFromTacheCommentaires(tacheId)

    const topicCommentaires = `/topic/tache/${tacheId}/commentaires`
    subscribedTopics.delete(topicCommentaires)
    console.log('[F12] Désabonné des commentaires tâche', tacheId)
  }

  modalCommentaires.value = false
  tacheSelectionnee.value = null
  commentairesTache.value = []
  nouveauCommentaire.value = ''
}

const ajouterCommentaire = async () => {
  if (!nouveauCommentaire.value.trim() || !tacheSelectionnee.value) {
    showToast(t('erreurs.contenuVide'), 'error')
    return
  }

  envoyantCommentaire.value = true
  try {
    const tacheIdNormalized = normalizeId(tacheSelectionnee.value.id)

    console.log('[F12] POST /api/commentaires', {
      contenu: nouveauCommentaire.value.trim(),
      tacheId: parseInt(tacheIdNormalized, 10)
    })

    await commentaireAPI.create({
      contenu: nouveauCommentaire.value.trim(),
      tacheId: parseInt(tacheIdNormalized, 10)
    })


    nouveauCommentaire.value = ''

    showToast(t('commentaires.ajoutSucces'), 'success')
  } catch (e) {
    console.error('[F12] Erreur ajout commentaire:', e)
    showToast(t('erreurs.ajoutCommentaire'), 'error')
  } finally {
    envoyantCommentaire.value = false
  }
}

const supprimerCommentaire = async (commentaireId) => {
  if (!confirm(t('commentaires.confirmerSuppression'))) return

  suppressionEnCours.value = true
  try {
    const commentaireIdNormalized = normalizeId(commentaireId)

    console.log(`[F12] DELETE /api/commentaires/${commentaireIdNormalized}`)

    await commentaireAPI.delete(commentaireIdNormalized)


    showToast(t('commentaires.suppressionSucces'), 'success')
  } catch (e) {
    console.error('[F12] Erreur suppression commentaire:', e)

    if (e.response?.status === 403) {
      showToast(t('erreurs.nonAutorise'), 'error')
    } else {
      showToast(t('erreurs.suppressionCommentaire'), 'error')
    }
  } finally {
    suppressionEnCours.value = false
  }
}

/**
 * CORRECTION CRITIQUE: Vérification permissions suppression
 * Seuls CHEF_PROJET et ADMINISTRATEUR peuvent supprimer
 */
const peutSupprimerCommentaire = () => {
  if (!utilisateur.value) return false

  const role = utilisateur.value.role || utilisateur.value.roles?.[0]
  const estChefOuAdmin = role === 'CHEF_PROJET' || role === 'ADMINISTRATEUR'

  return estChefOuAdmin
}

const getCommentairesTache = (tacheId) => {
  if (!tacheId) return []
  const normalized = normalizeId(tacheId)
  return commentairesParTache.value[normalized] || []
}

// ========== WEBSOCKET ==========
const initWebsocket = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    console.warn('[WS] Pas de token, WebSocket non initialisé')
    return
  }

  if (!utilisateur.value || !utilisateur.value.id) {
    console.error('[WS] Utilisateur non défini, WebSocket non initialisé')
    return
  }

  WebSocketService.connect(token)

  const userId = normalizeId(utilisateur.value.id)
  const topicNotifications = `/user/${userId}/topic/notifications`

  if (!subscribedTopics.has(topicNotifications)) {
    WebSocketService.subscribe(topicNotifications, (msg) => {
      console.log('[WS] Notification reçue:', msg)

      if (msg?.type === 'NOTIFICATION' || msg?.message) {
        notifications.value.unshift({
          id: msg.id || Date.now(),
          titre: msg.titre || t('notifications.notification'),
          message: msg.message || msg.contenu,
          date: msg.createdAt || new Date().toISOString(),
          lu: false,
          type: msg.sousType || 'SYSTEME'
        })

        // Notification navigateur
        if ('Notification' in window && Notification.permission === 'granted') {
          new Notification(msg.titre || t('notifications.notification'), {
            body: msg.message || msg.contenu,
            icon: '/favicon.ico'
          })
        }

        showToast(msg.message || msg.contenu, 'info')
      }
    })
    subscribedTopics.add(topicNotifications)
    console.log('[WS] ✅ Souscription:', topicNotifications)
  }
}

// ========== ACTIONS PROJETS ==========
const consulterProjet = (p) => {
  const id = normalizeId(p.id)
  router.push(`/projet/${id}`)
}

const progressionProjet = (projetId) => {
  const taches = mesTaches.value.filter(t =>
    normalizeId(t.projetId || t.id_projet) == normalizeId(projetId)
  )
  if (taches.length === 0) return 0
  const terminees = taches.filter(t => t.statut === 'TERMINE').length
  return Math.round((terminees / taches.length) * 100)
}

const getTachesProjet = (projetId) => {
  return mesTaches.value.filter(t =>
    normalizeId(t.projetId || t.id_projet) == normalizeId(projetId)
  ).length
}

const getProjetNom = (projetId) => {
  const p = mesProjets.value.find(x => normalizeId(x.id) == normalizeId(projetId))
  return p ? translateProjectTitle(p.titre) : t('projets.projetInconnu')
}

// ========== ACTIONS TÂCHES ==========
const soumettreValidation = async (tache) => {
  if (!confirm(t('taches.confirmerSoumission'))) return

  try {
    await taskAPI.updateStatus(tache.id, 'EN_ATTENTE_VALIDATION')
    tache.statut = 'EN_ATTENTE_VALIDATION'
    showToast(t('taches.soumissionReussie'), 'success')
    await chargerToutesDonnees()
  } catch (e) {
    console.error('[Tache] Erreur soumission:', e)
    showToast(t('erreurs.soumissionTache'), 'error')
  }
}

// ========== ACTIONS NOTIFICATIONS ==========
const marquerNotificationLue = async (n) => {
  try {
    const userId = normalizeId(utilisateur.value.id)
    await notificationAPI.markAsRead(n.id, userId)
    n.lu = true
  } catch (e) {
    console.error('[Notif] Erreur marque lue:', e)
  }
}

const marquerToutesLues = async () => {
  try {
    const userId = normalizeId(utilisateur.value.id)
    await notificationAPI.markAllAsRead(userId)
    notifications.value.forEach(n => n.lu = true)
    showToast(t('notifications.toutesMarquees'), 'success')
  } catch (e) {
    console.error('[Notif] Erreur marque toutes:', e)
  }
}

const supprimerNotification = async (n) => {
  if (!confirm(t('notifications.confirmerSuppression'))) return
  try {
    const userId = normalizeId(utilisateur.value.id)
    await notificationAPI.delete(n.id, userId)
    notifications.value = notifications.value.filter(x => x.id !== n.id)
    showToast(t('notifications.suppressionSucces'), 'success')
  } catch (e) {
    console.error('[Notif] Erreur suppression:', e)
  }
}

// ========== ACTIONS CHAT ==========
const ouvrirChatProjet = async (projet) => {
  if (projet.prive && !projet.estMembre) {
    console.warn('[Chat] Accès refusé au projet privé:', projet.titre)
    showToast(t('erreurs.accesRefuse'), 'error')
    return
  }

  if (projetChatActuel.value) {
    const oldTopic = `/topic/projet/${projetChatActuel.value.id}`
    WebSocketService.unsubscribe(oldTopic)
    subscribedTopics.delete(oldTopic)
  }

  projetChatActuel.value = projet
  onglet.value = 'collaboration'
  await chargerMessagesProjet(projet.id)

  const topicProjet = `/topic/projet/${projet.id}`
  if (!subscribedTopics.has(topicProjet)) {
    WebSocketService.subscribe(topicProjet, (msg) => {
      console.log('[Chat] Message reçu:', msg)
      messagesChat.value.push(msg)
      if (messagesParProjet.value[projet.id]) {
        messagesParProjet.value[projet.id].push(msg)
      }

      nextTick(() => scrollToBottom())
    })
    subscribedTopics.add(topicProjet)
    console.log('[Chat] ✅ Souscription:', topicProjet)
  }
}

const envoyerMessage = async () => {
  if (!nouveauMessage.value.trim() || !projetChatActuel.value) return

  envoyantMessage.value = true
  try {
    const r = await messagesAPI.send({
      projetId: projetChatActuel.value.id,
      contenu: nouveauMessage.value,
      type: 'TEXT'
    })

    messagesChat.value.push(r.data)
    nouveauMessage.value = ''

    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error('[Chat] Erreur envoi:', e)
    showToast(t('erreurs.envoyerMessage'), 'error')
  } finally {
    envoyantMessage.value = false
  }
}

const getMessagesNonLusProjet = (projetId) => {
  const messages = messagesParProjet.value[projetId] || []
  return messages.filter(m => m.statut !== 'LU').length
}

const getMessageClass = (m) => {
  const monId = normalizeId(utilisateur.value?.id)
  const auteurId = normalizeId(m.utilisateurId || m.authorId)
  return auteurId == monId ? 'bg-primary text-white ms-auto' : 'bg-white border shadow-sm'
}

// ========== HELPERS UI ==========
const seDeconnecter = () => {
  try {
    useAuthStore().logout?.()
  } catch (e) {
    console.warn('Erreur ignorée lors de la déconnexion :', e)
  }
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.replace({ path: '/connexion', query: { switch: '1' } })
}

const getStatutProjetClass = (statut) => {
  const classes = {
    'ACTIF': 'bg-success',
    'TERMINE': 'bg-secondary',
    'SUSPENDU': 'bg-warning text-dark',
    'ANNULE': 'bg-danger'
  }
  return classes[statut] || 'bg-info'
}

const getStatutTacheClass = (statut) => {
  const classes = {
    'BROUILLON': 'bg-secondary',
    'EN_ATTENTE_VALIDATION': 'bg-warning text-dark',
    'TERMINE': 'bg-success',
    'ANNULE': 'bg-danger'
  }
  return classes[statut] || 'bg-info'
}

const getNotificationIconClass = (type) => {
  const classes = {
    'TACHE': 'bg-warning',
    'PROJET': 'bg-primary',
    'EQUIPE': 'bg-success',
    'SYSTEME': 'bg-info'
  }
  return classes[type] || 'bg-secondary'
}

const getNotificationIcon = (type) => {
  const icons = {
    'TACHE': 'fas fa-tasks',
    'PROJET': 'fas fa-project-diagram',
    'EQUIPE': 'fas fa-users',
    'SYSTEME': 'fas fa-cog'
  }
  return icons[type] || 'fas fa-bell'
}

const formatDate = (date) => {
  if (!date) return '—'
  const d = new Date(date)
  const jour = String(d.getDate()).padStart(2, '0')
  const mois = String(d.getMonth() + 1).padStart(2, '0')
  const annee = d.getFullYear()
  return `${jour}/${mois}/${annee}`
}

const formatDateRelative = (date) => {
  if (!date) return '—'
  const now = new Date()
  const diff = now - new Date(date)
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (minutes < 1) return t('temps.maintenant')
  if (minutes < 60) return `${t('temps.ilYa')} ${minutes}${t('temps.min')}`
  if (hours < 24) return `${t('temps.ilYa')} ${hours}${t('temps.h')}`
  if (days < 7) return `${t('temps.ilYa')} ${days}${t('temps.j')}`
  return formatDate(date)
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleTimeString(
    t('locale') === 'fr' ? 'fr-FR' : 'en-US',
    { hour: '2-digit', minute: '2-digit' }
  )
}
// ========== WATCHERS ==========
watch(mesTaches, () => {
  filtrerTaches()
  if (onglet.value === 'taches') {
    nextTick(() => creerGraphique())
  }
}, { deep: true })

watch(onglet, (newVal) => {
  if (newVal === 'taches') {
    nextTick(() => creerGraphique())
  }
})
// ========== LIFECYCLE ==========
onMounted(async () => {
  await chargerToutesDonnees()
  initWebsocket()
  filtrerTaches()

  if ('Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission()
  }
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.destroy()
  }
  WebSocketService.disconnect()
  if (toastTimeout) {
    clearTimeout(toastTimeout)
  }
})
</script>

<style scoped>
/* ========== HEADER ========== */
.member-header {
  background: linear-gradient(135deg, #119c72, #96ddc8);
  border-radius: 12px;
  padding: 20px;
  color: white;
}

.text-white-75 {
  color: rgba(255, 255, 255, 0.75);
}

/* ========== TOAST ========== */
.toast {
  min-width: 250px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

/* ========== CARDS ========== */
.kpi-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.kpi-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.card {
  border-radius: 12px;
  overflow: hidden;
}

/* ========== NAVIGATION ========== */
.nav-pills .nav-link {
  border-radius: 8px;
  margin: 0 2px;
  transition: all 0.2s ease;
}

.nav-pills .nav-link.active {
  background: linear-gradient(135deg, #119c72, #96ddc8);
  color: white;
  font-weight: 600;
  transform: translateY(-1px);
}

/* ========== NOTIFICATIONS ========== */
.notification-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  border-radius: 8px;
}

.notification-item {
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.notification-item:hover {
  transform: translateX(5px);
  background-color: rgba(17, 156, 114, 0.05);
  border-left-color: #119c72;
}

/* ========== CHAT ========== */
.chat-bubble {
  animation: slideIn 0.3s ease;
}

.messages-container {
  scroll-behavior: smooth;
}

.messages-container::-webkit-scrollbar,
.comments-list::-webkit-scrollbar {
  width: 6px;
}

.messages-container::-webkit-scrollbar-track,
.comments-list::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.messages-container::-webkit-scrollbar-thumb,
.comments-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
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

/* ========== TABLES ========== */
.table th {
  border-top: none;
  font-weight: 600;
  font-size: 0.875rem;
  color: #495057;
}

.table-hover tbody tr:hover {
  background-color: rgba(17, 156, 114, 0.08);
}

/* ========== BADGES ========== */
.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}

/* ✨ CORRECTION F12: Animation badge commentaires */
.animate-badge {
  animation: fadeInScale 0.4s ease-out;
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.3);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* ========== PROGRESS ========== */
.progress {
  background-color: #e9ecef;
  border-radius: 4px;
}

/* ========== LIST GROUP ========== */
.list-group-item.active {
  background-color: #119c72;
  border-color: #119c72;
}

.list-group-item {
  transition: all 0.2s ease;
}

.list-group-item:hover {
  background-color: rgba(17, 156, 114, 0.05);
}

/* ========== BUTTONS ========== */
.btn-group .btn {
  border-radius: 6px;
  margin: 0 1px;
}

/* ========== MODAL ========== */
.modal {
  backdrop-filter: blur(5px);
}

.modal-header.bg-info {
  border-bottom: none;
}

.modal-header .btn-close-white {
  filter: brightness(0) invert(1);
}

/* ========== COMMENTAIRES ========== */
.comments-list {
  max-height: 400px;
  overflow-y: auto;
}

.comments-list .card {
  transition: all 0.2s ease;
}

.comments-list .card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* ========== UTILITY ========== */
.text-break {
  word-wrap: break-word;
  overflow-wrap: break-word;
}
/* Styles pour les mini-stats */
.stat-mini {
  transition: all 0.3s ease;
}

.stat-mini:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Styles pour le graphique */
canvas {
  max-width: 100%;
  height: auto !important;
}
</style>
