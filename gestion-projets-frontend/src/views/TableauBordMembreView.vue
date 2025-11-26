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
               href="javascript:void(0)">
              <i class="fas fa-folder-open me-2"></i>{{ $t('nav.mesProjets') }}
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link"
               :class="{active:onglet==='taches'}"
               @click="onglet='taches'"
               href="javascript:void(0)">
              <i class="fas fa-tasks me-2"></i>{{ $t('nav.mesTaches') }}
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link"
               :class="{active:onglet==='notifications'}"
               @click="onglet='notifications'"
               href="javascript:void(0)">
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
               href="javascript:void(0)">
              <i class="fas fa-comments me-2"></i>{{ $t('nav.collaboration') }}
            </a>
          </li>
        </ul>
        <router-link to="/profil" class="btn btn-link text-decoration-none">
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
                           :style="{width: progressionProjet(p.id)+'%'}"></div>
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

      <!-- ========== ONGLET TÂCHES (SIMPLIFIÉ) ========== -->
      <div v-else-if="onglet==='taches'">
        <!-- Recherche et filtres -->
        <div class="card shadow-sm border-0 mb-3">
          <div class="card-body">
            <div class="row g-2 align-items-center">
              <div class="col-md-6">
                <div class="input-group">
            <span class="input-group-text bg-white">
              <i class="fas fa-search text-muted"></i>
            </span>
                  <input type="text" class="form-control border-start-0"
                         v-model="rechercheTask"
                         :placeholder="$t('taches.rechercher')"
                         @input="filtrerTaches">
                  <button v-if="rechercheTask" class="btn btn-outline-secondary"
                          @click="rechercheTask = ''; filtrerTaches()">
                    <i class="fas fa-times"></i>
                  </button>
                </div>
              </div>
              <div class="col-md-6">
                <div class="btn-group w-100" role="group">
                  <button class="btn btn-sm"
                          :class="filtreStatut === 'TOUS' ? 'btn-primary' : 'btn-outline-primary'"
                          @click="filtreStatut = 'TOUS'; filtrerTaches()">
                    {{ $t('commun.toutes') }} ({{ mesTaches.length }})
                  </button>
                  <button class="btn btn-sm"
                          :class="filtreStatut === 'EN_COURS' ? 'btn-warning' : 'btn-outline-warning'"
                          @click="filtreStatut = 'EN_COURS'; filtrerTaches()">
                    {{ $t('taches.enCours') }} ({{ tachesEnCours.length }})
                  </button>
                  <button class="btn btn-sm"
                          :class="filtreStatut === 'TERMINE' ? 'btn-success' : 'btn-outline-success'"
                          @click="filtreStatut = 'TERMINE'; filtrerTaches()">
                    {{ $t('taches.statuts.termine') }} ({{ statsTaskes.termine }})
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Tableau des tâches -->
        <div class="card shadow-sm border-0">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('membre.sections.taches') }}</h5>
              <small class="text-muted">
                {{ tachesFiltrees.length }} {{ $t('commun.resultats') }}
                <span v-if="rechercheTask || filtreStatut !== 'TOUS'"> · {{ $t('commun.filtre') }}</span>
              </small>
            </div>
            <button class="btn btn-sm btn-outline-secondary"
                    @click="reinitialiserFiltres"
                    v-if="rechercheTask || filtreStatut !== 'TOUS'">
              <i class="fas fa-redo me-1"></i>{{ $t('commun.reinitialiser') }}
            </button>
          </div>
          <div class="card-body">
            <div v-if="tachesFiltrees.length===0" class="text-center text-muted py-5">
              <i class="fas fa-tasks fa-3x mb-3"></i>
              <p>{{ $t('membre.taches.aucuneTache') }}</p>
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('taches.titre') }}</th>
                  <th>{{ $t('projets.projet') }}</th>
                  <th>{{ $t('taches.priorite') }}</th>
                  <th>{{ $t('taches.echeance') }}</th>
                  <th>{{ $t('taches.statut') }}</th>
                  <th class="text-end">{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="t in tachesFiltrees" :key="t.id">
                  <td>
                    <strong>{{ t.titre }}</strong><br>
                    <small class="text-muted">
                      {{ (t.description || '').substring(0, 40) }}{{ t.description?.length > 40 ? '...' : '' }}
                    </small>
                  </td>
                  <td>{{ getProjetNom(t.projetId || t.id_projet) }}</td>
                  <td>
              <span class="badge" :class="getPrioriteClass(t.priorite)">
                <i :class="getPrioriteIcon(t.priorite)" class="me-1"></i>
                {{ translateData('priority', t.priorite) || $t('taches.priorites.normale') }}
              </span>
                  </td>
                  <td>
                    <div v-if="t.dateEcheance || t.date_echeance" class="d-flex align-items-center gap-1">
                      <i :class="getEcheanceIcon(t.dateEcheance || t.date_echeance)"></i>
                      <small :class="getEcheanceClass(t.dateEcheance || t.date_echeance)">
                        {{ formatDate(t.dateEcheance || t.date_echeance) }}
                      </small>
                    </div>
                    <small v-else class="text-muted">—</small>
                  </td>
                  <td>
              <span class="badge" :class="getStatutTacheClass(t.statut)">
                {{ translateData('taskStatus', t.statut) }}
              </span>
                  </td>
                  <td class="text-end">
                    <div class="btn-group">
                      <!-- Bouton Voir détails -->
                      <button class="btn btn-sm btn-outline-secondary"
                              @click="ouvrirDetailTache(t)"
                              :title="$t('taches.voirDetails')">
                        <i class="fas fa-eye"></i>
                      </button>
                      <!-- Bouton Soumettre (si BROUILLON) -->
                      <button v-if="t.statut === 'BROUILLON'"
                              class="btn btn-sm btn-success"
                              @click="soumettreValidation(t)"
                              :title="$t('tooltips.soumettreValidation')">
                        <i class="fas fa-paper-plane me-1"></i>
                        {{ $t('taches.soumettre') }}
                      </button>
                      <!-- Indicateur pour EN_ATTENTE -->
                      <span v-else-if="t.statut === 'EN_ATTENTE_VALIDATION'"
                            class="btn btn-sm btn-outline-info disabled">
                  <i class="fas fa-hourglass-half me-1"></i>
                  {{ $t('taches.enAttenteValidation') }}
                </span>
                      <!-- Indicateur pour TERMINE -->
                      <span v-else-if="t.statut === 'TERMINE'"
                            class="btn btn-sm btn-outline-success disabled">
                  <i class="fas fa-check-circle me-1"></i>
                  {{ $t('taches.terminee') }}
                </span>
                    </div>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

<!-- ========== ONGLET NOTIFICATIONS ========== -->
<div v-else-if="onglet==='notifications'">
<div class="card shadow-sm border-0">
  <div class="card-header bg-white d-flex justify-content-between align-items-center">
    <h5 class="mb-0">{{ $t('membre.sections.notifications') }}</h5>
    <button class="btn btn-outline-secondary btn-sm"
            @click="marquerToutesLues"
            :disabled="notificationsNonLues.length===0">
      <i class="fas fa-check-double me-1"></i>{{ $t('notifications.marquerToutesLues') }}
    </button>
  </div>
  <div class="card-body">
    <div v-if="notifications.length===0" class="text-center text-muted py-5">
      <i class="fas fa-bell-slash fa-3x mb-3"></i>
      <p>{{ $t('membre.notifications.vide') }}</p>
    </div>
    <div v-else class="list-group">
      <div v-for="n in notifications" :key="n.id"
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
          <button v-if="!n.lu" class="btn btn-sm btn-outline-success"
                  @click="marquerNotificationLue(n)">
            <i class="fas fa-check"></i>
          </button>
          <button class="btn btn-sm btn-outline-danger"
                  @click="supprimerNotification(n)">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</div>
</div>

<!-- ========== ONGLET COLLABORATION ========== -->
<div v-else-if="onglet==='collaboration'">
<div class="row g-3">
  <div class="col-md-4">
    <div class="card border-0 shadow-sm h-100">
      <div class="card-header bg-white">
        <h6 class="mb-0">{{ $t('nav.mesProjets') }}</h6>
      </div>
      <div class="list-group list-group-flush">
        <button v-for="p in mesProjets" :key="p.id"
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                :class="{active: projetChatActuel && projetChatActuel.id===p.id}"
                @click="ouvrirChatProjet(p)">
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

            <div v-for="m in messagesChat" :key="m.id"
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
            <input class="form-control"
                   v-model.trim="nouveauMessage"
                   :placeholder="$t('collaboration.ecrireMessage')"
                   @keyup.enter="envoyerMessage">
            <button class="btn btn-success"
                    :disabled="envoyantMessage || !nouveauMessage"
                    @click="envoyerMessage">
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

<!-- ========== MODAL COMMENTAIRES ========== -->
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
              @click="fermerModalCommentaires"></button>
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
      </div>

      <div v-else-if="commentairesTache.length === 0" class="text-center py-4 text-muted">
        <i class="fas fa-comment-slash fa-3x mb-3"></i>
        <p>{{ $t('commentaires.aucunCommentaire') }}</p>
      </div>

      <div v-else class="comments-list mb-3" style="max-height:300px;overflow-y:auto">
        <div v-for="c in commentairesTache" :key="c.id" class="card mb-2">
          <div class="card-body py-2">
            <div class="d-flex justify-content-between align-items-start">
              <div>
                <strong>{{ c.auteurPrenom }} {{ c.auteurNom }}</strong>
                <small class="text-muted ms-2">{{ formatDateRelative(c.date) }}</small>
              </div>
              <button v-if="peutSupprimerCommentaire(c)"
                      class="btn btn-sm btn-outline-danger"
                      @click="supprimerCommentaire(c.id)"
                      :disabled="suppressionEnCours">
                <i class="fas fa-trash"></i>
              </button>
            </div>
            <p class="mb-0 mt-2 text-break">{{ c.contenu }}</p>
          </div>
        </div>
      </div>

      <!-- Formulaire nouveau commentaire -->
      <div class="border-top pt-3">
        <label class="form-label fw-bold">{{ $t('commentaires.nouveau') }}</label>
        <textarea class="form-control mb-2"
                  v-model.trim="nouveauCommentaire"
                  rows="3"
                  :placeholder="$t('commentaires.placeholder')"
                  maxlength="500"></textarea>
        <div class="d-flex justify-content-between align-items-center">
          <small class="text-muted">{{ nouveauCommentaire.length }}/500</small>
          <button class="btn btn-primary"
                  @click="ajouterCommentaire"
                  :disabled="!nouveauCommentaire || envoyantCommentaire">
            <span v-if="envoyantCommentaire" class="spinner-border spinner-border-sm me-1"></span>
            <i v-else class="fas fa-paper-plane me-1"></i>
            {{ $t('commentaires.ajouter') }}
          </button>
        </div>
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

<!-- ========== MODAL DÉTAIL TÂCHE ========== -->
<div v-if="modalDetailTache"
     class="modal d-block"
     style="background:rgba(0,0,0,.6);z-index:1060"
     @click.self="fermerModalDetailTache">
<div class="modal-dialog modal-lg">
  <div class="modal-content">
    <div class="modal-header bg-primary text-white">
      <h5 class="modal-title">
        <i class="fas fa-tasks me-2"></i>{{ $t('taches.detailTache') }}
      </h5>
      <button class="btn-close btn-close-white"
              @click="fermerModalDetailTache"></button>
    </div>

    <div class="modal-body" v-if="tacheDetail">
      <!-- En-tête avec titre et statut -->
      <div class="d-flex justify-content-between align-items-start mb-4">
        <div>
          <h4 class="mb-1">{{ tacheDetail.titre }}</h4>
          <small class="text-muted">
            {{ $t('projets.projet') }}: {{ getProjetNom(tacheDetail.projetId || tacheDetail.id_projet) }}
          </small>
        </div>
        <span class="badge fs-6" :class="getStatutTacheClass(tacheDetail.statut)">
                {{ translateData('taskStatus', tacheDetail.statut) }}
              </span>
      </div>

      <!-- Infos principales -->
      <div class="row g-3 mb-4">
        <!-- Priorité -->
        <div class="col-md-4">
          <div class="card border h-100">
            <div class="card-body text-center">
              <i :class="getPrioriteIcon(tacheDetail.priorite)" class="fa-2x mb-2"
                 :style="{color: getPrioriteColor(tacheDetail.priorite)}"></i>
              <h6 class="mb-1">{{ $t('taches.priorite') }}</h6>
              <span class="badge" :class="getPrioriteClass(tacheDetail.priorite)">
                      {{ translateData('priority', tacheDetail.priorite) || $t('taches.priorites.normale') }}
                    </span>
            </div>
          </div>
        </div>

        <!-- Échéance -->
        <div class="col-md-4">
          <div class="card border h-100">
            <div class="card-body text-center">
              <i class="fas fa-calendar-alt fa-2x mb-2"
                 :class="getEcheanceIconClass(tacheDetail.dateEcheance || tacheDetail.date_echeance)"></i>
              <h6 class="mb-1">{{ $t('taches.echeance') }}</h6>
              <div v-if="tacheDetail.dateEcheance || tacheDetail.date_echeance">
                      <span :class="getEcheanceClass(tacheDetail.dateEcheance || tacheDetail.date_echeance)">
                        {{ formatDate(tacheDetail.dateEcheance || tacheDetail.date_echeance) }}
                      </span>
                <div class="mt-1">
                  <small :class="getEcheanceAlertClass(tacheDetail.dateEcheance || tacheDetail.date_echeance)">
                    {{ getEcheanceMessage(tacheDetail.dateEcheance || tacheDetail.date_echeance) }}
                  </small>
                </div>
              </div>
              <span v-else class="text-muted">{{ $t('commun.nonDefini') }}</span>
            </div>
          </div>
        </div>

        <!-- Date création -->
        <div class="col-md-4">
          <div class="card border h-100">
            <div class="card-body text-center">
              <i class="fas fa-clock fa-2x mb-2 text-secondary"></i>
              <h6 class="mb-1">{{ $t('taches.dateCreation') }}</h6>
              <span>{{ formatDate(tacheDetail.dateCreation || tacheDetail.date_creation) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Description -->
      <div class="mb-4">
        <h6 class="fw-bold">
          <i class="fas fa-align-left me-2"></i>{{ $t('taches.description') }}
        </h6>
        <div class="card border">
          <div class="card-body">
            <p class="mb-0" v-if="tacheDetail.description">{{ tacheDetail.description }}</p>
            <p class="mb-0 text-muted fst-italic" v-else>{{ $t('taches.aucuneDescription') }}</p>
          </div>
        </div>
      </div>

      <!-- Commentaires récents -->
      <div class="mb-3">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <h6 class="fw-bold mb-0">
            <i class="fas fa-comments me-2"></i>{{ $t('commentaires.titre') }}
          </h6>
          <button class="btn btn-sm btn-outline-info"
                  @click="fermerModalDetailTache(); ouvrirCommentaires(tacheDetail)">
            {{ $t('commentaires.voirTous') }}
          </button>
        </div>
        <div class="card border">
          <div class="card-body py-2">
            <div v-if="getCommentairesTache(tacheDetail.id).length === 0" class="text-muted text-center py-2">
              {{ $t('commentaires.aucunCommentaire') }}
            </div>
            <div v-else>
              <div v-for="c in getCommentairesTache(tacheDetail.id).slice(0, 3)" :key="c.id" class="mb-2 pb-2 border-bottom">
                <div class="d-flex justify-content-between">
                  <strong class="small">{{ c.auteurPrenom }} {{ c.auteurNom }}</strong>
                  <small class="text-muted">{{ formatDateRelative(c.date) }}</small>
                </div>
                <small>{{ c.contenu.substring(0, 100) }}{{ c.contenu.length > 100 ? '...' : '' }}</small>
              </div>
              <small v-if="getCommentairesTache(tacheDetail.id).length > 3" class="text-muted">
                +{{ getCommentairesTache(tacheDetail.id).length - 3 }} {{ $t('commentaires.autres') }}
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal-footer">
      <!-- Action selon statut -->
      <button v-if="tacheDetail?.statut === 'BROUILLON'"
              class="btn btn-success"
              @click="soumettreValidation(tacheDetail); fermerModalDetailTache()">
        <i class="fas fa-paper-plane me-1"></i>{{ $t('taches.soumettre') }}
      </button>
      <button class="btn btn-secondary" @click="fermerModalDetailTache">
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

// Recherche et filtres
const rechercheTask = ref('')
const filtreStatut = ref('TOUS')
const tachesFiltrees = ref([])

// Toast
const toastMessage = ref('')
const toastType = ref('info')
let toastTimeout = null

// Modal commentaires
const modalCommentaires = ref(false)
const tacheSelectionnee = ref(null)
const commentairesTache = ref([])

// Modal détail tâche
const modalDetailTache = ref(false)
const tacheDetail = ref(null)

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

const statsTaskes = computed(() => {
  return {
    brouillon: mesTaches.value.filter(t => t.statut === 'BROUILLON').length,
    enAttente: mesTaches.value.filter(t => t.statut === 'EN_ATTENTE_VALIDATION').length,
    termine: mesTaches.value.filter(t => t.statut === 'TERMINE').length,
    annule: mesTaches.value.filter(t => t.statut === 'ANNULE').length
  }
})

// ========== TOAST ==========
const showToast = (message, type = 'info') => {
  if (toastTimeout) clearTimeout(toastTimeout)
  toastMessage.value = message
  toastType.value = type
  toastTimeout = setTimeout(() => { toastMessage.value = '' }, 3000)
}

// ========== FILTRES ==========
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

// ========== PRIORITÉ (Erratum N°3) ==========
const getPrioriteClass = (priorite) => {
  const classes = {
    'BASSE': 'bg-success',
    'NORMALE': 'bg-info',
    'HAUTE': 'bg-warning text-dark',
    'URGENTE': 'bg-danger'
  }
  return classes[priorite] || 'bg-secondary'
}

const getPrioriteIcon = (priorite) => {
  const icons = {
    'BASSE': 'fas fa-arrow-down',
    'NORMALE': 'fas fa-minus',
    'HAUTE': 'fas fa-arrow-up',
    'URGENTE': 'fas fa-exclamation-triangle'
  }
  return icons[priorite] || 'fas fa-minus'
}

const getPrioriteColor = (priorite) => {
  const colors = {
    'BASSE': '#28a745',
    'NORMALE': '#17a2b8',
    'HAUTE': '#ffc107',
    'URGENTE': '#dc3545'
  }
  return colors[priorite] || '#6c757d'
}

// ========== ÉCHÉANCE (Erratum N°3) ==========
const getJoursRestants = (dateEcheance) => {
  if (!dateEcheance) return null
  const aujourdhui = new Date()
  aujourdhui.setHours(0, 0, 0, 0)
  const echeance = new Date(dateEcheance)
  echeance.setHours(0, 0, 0, 0)
  const diffTime = echeance - aujourdhui
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}

const getEcheanceIcon = (dateEcheance) => {
  const jours = getJoursRestants(dateEcheance)
  if (jours === null) return 'fas fa-calendar text-muted'
  if (jours < 0) return 'fas fa-exclamation-circle text-danger'
  if (jours <= 3) return 'fas fa-exclamation-triangle text-warning'
  return 'fas fa-calendar-check text-success'
}

const getEcheanceIconClass = (dateEcheance) => {
  const jours = getJoursRestants(dateEcheance)
  if (jours === null) return 'text-muted'
  if (jours < 0) return 'text-danger'
  if (jours <= 3) return 'text-warning'
  return 'text-success'
}

const getEcheanceClass = (dateEcheance) => {
  const jours = getJoursRestants(dateEcheance)
  if (jours === null) return 'text-muted'
  if (jours < 0) return 'text-danger fw-bold'
  if (jours <= 3) return 'text-warning fw-bold'
  return 'text-success'
}

const getEcheanceAlertClass = (dateEcheance) => {
  const jours = getJoursRestants(dateEcheance)
  if (jours === null) return ''
  if (jours < 0) return 'badge bg-danger'
  if (jours <= 3) return 'badge bg-warning text-dark'
  return 'badge bg-success'
}

const getEcheanceTooltip = (dateEcheance) => {
  const jours = getJoursRestants(dateEcheance)
  if (jours === null) return ''
  if (jours < 0) return t('taches.echeanceDepassee', { jours: Math.abs(jours) })
  if (jours === 0) return t('taches.echeanceAujourdhui')
  if (jours === 1) return t('taches.echeanceDemain')
  if (jours <= 3) return t('taches.echeanceProche', { jours })
  return t('taches.echeanceDans', { jours })
}

const getEcheanceMessage = (dateEcheance) => {
  const jours = getJoursRestants(dateEcheance)
  if (jours === null) return ''
  if (jours < 0) return `${t('taches.enRetard')} (${Math.abs(jours)} ${t('temps.jours')})`
  if (jours === 0) return t('taches.echeanceAujourdhui')
  if (jours === 1) return t('taches.echeanceDemain')
  if (jours <= 3) return `${jours} ${t('temps.joursRestants')}`
  return `${jours} ${t('temps.joursRestants')}`
}

// ========== MODAL DÉTAIL TÂCHE ==========
const ouvrirDetailTache = (tache) => {
  tacheDetail.value = tache
  modalDetailTache.value = true
}

const fermerModalDetailTache = () => {
  modalDetailTache.value = false
  tacheDetail.value = null
}

// ========== CHARGEMENT DONNÉES ==========
const chargerToutesDonnees = async () => {
  chargementGlobal.value = true
  erreurBackend.value = ''

  try {
    if (!utilisateur.value || !utilisateur.value.id) {
      utilisateur.value = getUserSafe()
      if (!utilisateur.value || !utilisateur.value.id) {
        erreurBackend.value = t('erreurs.sessionExpiree')
        chargementGlobal.value = false
        setTimeout(() => router.push('/connexion'), 2000)
        return
      }
    }

    const userId = normalizeId(utilisateur.value.id)

    const [pRes, tRes, nRes] = await Promise.allSettled([
      projectAPI.byUser(userId),
      taskAPI.byUser(userId),
      notificationAPI.list(userId)
    ])

    mesProjets.value = pRes.status === 'fulfilled' && Array.isArray(pRes.value?.data)
      ? pRes.value.data.map(p => ({ ...p, id: normalizeId(p.id) }))
      : []

    mesTaches.value = tRes.status === 'fulfilled' && Array.isArray(tRes.value?.data)
      ? tRes.value.data.map(t => ({
        ...t,
        id: normalizeId(t.id),
        projetId: normalizeId(t.projetId || t.id_projet || t.idProjet),
        id_projet: normalizeId(t.id_projet || t.projetId || t.idProjet),
        idProjet: normalizeId(t.idProjet || t.projetId || t.id_projet)
      }))
      : []

    // Charger les projets manquants
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
        }
      } catch (e) {
        console.warn('[Load] Projet manquant non chargé:', projetId)
      }
    }

    notifications.value = nRes.status === 'fulfilled' && Array.isArray(nRes.value?.data)
      ? nRes.value.data
      : []

    // Précharger commentaires
    const commentairesPromises = mesTaches.value.map(async (tache) => {
      try {
        const tacheIdNormalized = normalizeId(tache.id)
        const response = await commentaireAPI.getByTache(tacheIdNormalized)
        commentairesParTache.value[tacheIdNormalized] = Array.isArray(response.data) ? response.data : []
      } catch (e) {
        commentairesParTache.value[normalizeId(tache.id)] = []
      }
    })

    await Promise.allSettled(commentairesPromises)

    const erreurs = [pRes, tRes, nRes].filter(r => r.status === 'rejected')
    if (erreurs.length === 3) {
      erreurBackend.value = t('erreurs.chargementDonnees')
    }

  } catch (e) {
    console.error('[Load] Erreur:', e)
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
    console.error('[Chat] Erreur:', e)
    messagesChat.value = []
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// ========== COMMENTAIRES ==========
const chargerCommentairesTache = async (tacheId) => {
  chargementCommentaires.value = true
  try {
    const tacheIdNormalized = normalizeId(tacheId)
    const response = await commentaireAPI.getByTache(tacheIdNormalized)
    commentairesTache.value = Array.isArray(response.data) ? response.data : []
    commentairesParTache.value[tacheIdNormalized] = commentairesTache.value
  } catch (e) {
    console.error('[Commentaires] Erreur:', e)
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

  const tacheId = normalizeId(tache.id)
  const topicCommentaires = `/topic/tache/${tacheId}/commentaires`

  if (!subscribedTopics.has(topicCommentaires)) {
    WebSocketService.subscribeToTacheCommentaires(tacheId, (nouveauCommentaire) => {
      if (tacheSelectionnee.value && normalizeId(tacheSelectionnee.value.id) === tacheId) {
        const existe = commentairesTache.value.some(c => c.id === nouveauCommentaire.id)
        if (!existe) {
          commentairesTache.value.push(nouveauCommentaire)
        }
      }
      if (!commentairesParTache.value[tacheId]) {
        commentairesParTache.value[tacheId] = []
      }
      const existeDansCache = commentairesParTache.value[tacheId].some(c => c.id === nouveauCommentaire.id)
      if (!existeDansCache) {
        commentairesParTache.value[tacheId].push(nouveauCommentaire)
      }
    })

    WebSocketService.subscribeToTacheCommentairesSuppression(tacheId, (commentaireId) => {
      commentairesTache.value = commentairesTache.value.filter(c => c.id !== commentaireId)
      if (commentairesParTache.value[tacheId]) {
        commentairesParTache.value[tacheId] = commentairesParTache.value[tacheId].filter(c => c.id !== commentaireId)
      }
    })

    subscribedTopics.add(topicCommentaires)
  }
}

const fermerModalCommentaires = () => {
  if (tacheSelectionnee.value) {
    const tacheId = normalizeId(tacheSelectionnee.value.id)
    WebSocketService.unsubscribeFromTacheCommentaires(tacheId)
    subscribedTopics.delete(`/topic/tache/${tacheId}/commentaires`)
  }
  modalCommentaires.value = false
  tacheSelectionnee.value = null
  commentairesTache.value = []
  nouveauCommentaire.value = ''
}

const ajouterCommentaire = async () => {
  if (!nouveauCommentaire.value.trim() || !tacheSelectionnee.value) return

  envoyantCommentaire.value = true
  try {
    const tacheIdNormalized = normalizeId(tacheSelectionnee.value.id)
    await commentaireAPI.create({
      contenu: nouveauCommentaire.value.trim(),
      tacheId: parseInt(tacheIdNormalized, 10)
    })
    nouveauCommentaire.value = ''
    showToast(t('commentaires.ajoutSucces'), 'success')
  } catch (e) {
    console.error('[Commentaires] Erreur ajout:', e)
    showToast(t('erreurs.ajoutCommentaire'), 'error')
  } finally {
    envoyantCommentaire.value = false
  }
}

const supprimerCommentaire = async (commentaireId) => {
  if (!confirm(t('commentaires.confirmerSuppression'))) return

  suppressionEnCours.value = true
  try {
    await commentaireAPI.delete(normalizeId(commentaireId))
    showToast(t('commentaires.suppressionSucces'), 'success')
  } catch (e) {
    console.error('[Commentaires] Erreur suppression:', e)
    if (e.response?.status === 403) {
      showToast(t('erreurs.nonAutorise'), 'error')
    } else {
      showToast(t('erreurs.suppressionCommentaire'), 'error')
    }
  } finally {
    suppressionEnCours.value = false
  }
}

const peutSupprimerCommentaire = () => {
  if (!utilisateur.value) return false
  const role = utilisateur.value.role || utilisateur.value.roles?.[0]
  return role === 'CHEF_PROJET' || role === 'ADMINISTRATEUR'
}

const getCommentairesTache = (tacheId) => {
  if (!tacheId) return []
  return commentairesParTache.value[normalizeId(tacheId)] || []
}

// ========== WEBSOCKET ==========
const initWebsocket = () => {
  const token = localStorage.getItem('token')
  if (!token || !utilisateur.value?.id) return

  WebSocketService.connect(token)

  const userId = normalizeId(utilisateur.value.id)
  const topicNotifications = `/user/${userId}/topic/notifications`

  if (!subscribedTopics.has(topicNotifications)) {
    WebSocketService.subscribe(topicNotifications, (msg) => {
      if (msg?.type === 'NOTIFICATION' || msg?.message) {
        notifications.value.unshift({
          id: msg.id || Date.now(),
          titre: msg.titre || t('notifications.notification'),
          message: msg.message || msg.contenu,
          date: msg.createdAt || new Date().toISOString(),
          lu: false,
          type: msg.sousType || 'SYSTEME'
        })

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
  }
}

// ========== ACTIONS PROJETS ==========
const consulterProjet = (p) => {
  router.push(`/projet/${normalizeId(p.id)}`)
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
    filtrerTaches()
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
    console.error('[Notif] Erreur:', e)
  }
}

const marquerToutesLues = async () => {
  try {
    const userId = normalizeId(utilisateur.value.id)
    await notificationAPI.markAllAsRead(userId)
    notifications.value.forEach(n => n.lu = true)
    showToast(t('notifications.toutesMarquees'), 'success')
  } catch (e) {
    console.error('[Notif] Erreur:', e)
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
    console.error('[Notif] Erreur:', e)
  }
}

// ========== ACTIONS CHAT ==========
const ouvrirChatProjet = async (projet) => {
  if (projet.prive && !projet.estMembre) {
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
      messagesChat.value.push(msg)
      if (messagesParProjet.value[projet.id]) {
        messagesParProjet.value[projet.id].push(msg)
      }
      nextTick(() => scrollToBottom())
    })
    subscribedTopics.add(topicProjet)
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
    console.error('[Chat] Erreur:', e)
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
    console.warn('Erreur déconnexion:', e)
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
}, { deep: true })

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
  WebSocketService.disconnect()
  if (toastTimeout) clearTimeout(toastTimeout)
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

.messages-container,
.comments-list {
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
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
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

/* ========== PRIORITÉ ========== */
.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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

/* ========== BUTTONS ========== */
.btn-group .btn {
  border-radius: 6px;
  margin: 0 1px;
}

/* ========== MODAL ========== */
.modal {
  backdrop-filter: blur(5px);
}

.modal-header.bg-info,
.modal-header.bg-primary {
  border-bottom: none;
}

.modal-header .btn-close-white {
  filter: brightness(0) invert(1);
}

/* ========== UTILITY ========== */
.text-break {
  word-wrap: break-word;
  overflow-wrap: break-word;
}
</style>
