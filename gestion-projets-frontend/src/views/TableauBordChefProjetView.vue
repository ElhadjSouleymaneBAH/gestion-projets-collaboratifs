<template>
  <div class="dashboard-container">

    <!-- ========== HEADER ORIGINAL (INCHANGÉ) ========== -->
    <div class="chef-header mb-3">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h1 class="mb-1 d-flex align-items-center gap-2">
            <i class="fas fa-crown text-warning"></i>
            {{ $t('tableauBord.chefProjet.titre') }}
            <span class="badge bg-gradient-warning text-dark">PREMIUM</span>
          </h1>
          <p class="text-white-75 mb-0">
            {{ $t('commun.bienvenue') }}
            {{ utilisateur?.prenom || utilisateur?.firstName }}
            {{ utilisateur?.nom || utilisateur?.lastName }}
          </p>
        </div>

        <div class="d-flex align-items-center gap-2">
          <div v-if="abonnement" class="subscription-status">
            <span class="badge" :class="abonnementActif ? 'bg-success' : 'bg-danger'">
              {{ abonnementActif ? $t('abonnement.actif') : $t('abonnement.expire') }}
            </span>
            <small class="text-white-50 d-block" v-if="abonnement?.date_fin || abonnement?.dateFin">
              {{ $t('abonnement.expire') }}
              {{ formatDate(abonnement.date_fin || abonnement.dateFin) }}
            </small>
          </div>
          <button class="btn btn-outline-light" @click="seDeconnecter" :title="$t('nav.deconnexion')">
            <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
          </button>
        </div>
      </div>
    </div>

    <!-- ========== ALERTE ABONNEMENT EXPIRÉ ========== -->
    <div v-if="!chargementGlobal && abonnement && !abonnementActif" class="alert alert-warning alert-compact mb-3">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <i class="fas fa-exclamation-triangle me-2"></i>
          <strong>{{ $t('abonnement.abonnementExpire') }}</strong> -
          <small>{{ $t('abonnement.renouvelerPourContinuer') }}</small>
        </div>
        <router-link to="/abonnement-premium" class="btn btn-warning btn-sm">
          <i class="fas fa-credit-card me-1"></i>{{ $t('abonnement.renouveler') }}
        </router-link>
      </div>
    </div>

    <!-- ========== LOADING / ERROR ========== -->
    <div v-if="erreurBackend" class="alert alert-danger d-flex justify-content-between align-items-center mb-3">
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">
        <i class="fas fa-sync-alt me-1"></i>{{ $t('commun.actualiser') }}
      </button>
    </div>

    <div v-else-if="chargementGlobal" class="text-center py-5">
      <div class="spinner-border text-success"></div>
      <p class="text-muted mt-2">{{ $t('commun.chargement') }}</p>
    </div>

    <!-- ========== CONTENU PRINCIPAL ========== -->
    <div v-else>

      <!-- ========== KPIs CONDENSÉS (UNE LIGNE) ========== -->
      <div class="kpi-bar mb-3">
        <div class="kpi-item" @click="onglet = 'projets'" :class="{ active: onglet === 'projets' }">
          <div class="kpi-icon bg-primary-soft"><i class="fas fa-project-diagram text-primary"></i></div>
          <div class="kpi-content">
            <span class="kpi-value">{{ mesProjets.length }}</span>
            <span class="kpi-label">{{ $t('projets.mesProjets') }}</span>
            <span class="kpi-sub text-primary">{{ projetsActifs.length }} {{ $t('commun.actifs') }}</span>
          </div>
        </div>

        <div class="kpi-item" @click="onglet = 'kanban'" :class="{ active: onglet === 'kanban' }">
          <div class="kpi-icon bg-warning-soft"><i class="fas fa-tasks text-warning"></i></div>
          <div class="kpi-content">
            <span class="kpi-value">{{ tachesEnAttente.length }}</span>
            <span class="kpi-label">{{ $t('taches.aValider') }}</span>
            <span class="kpi-sub text-warning">{{ totalTaches.length }} {{ $t('commun.total') }}</span>
          </div>
        </div>

        <div class="kpi-item" @click="onglet = 'equipe'" :class="{ active: onglet === 'equipe' }">
          <div class="kpi-icon bg-success-soft"><i class="fas fa-users text-success"></i></div>
          <div class="kpi-content">
            <span class="kpi-value">{{ totalMembres }}</span>
            <span class="kpi-label">{{ $t('equipe.collaborateurs') }}</span>
            <span class="kpi-sub text-success">{{ $t('equipe.tousLesprojets') }}</span>
          </div>
        </div>

        <div class="kpi-item" @click="onglet = 'notifications'" :class="{ active: onglet === 'notifications' }">
          <div class="kpi-icon bg-info-soft"><i class="fas fa-bell text-info"></i></div>
          <div class="kpi-content">
            <span class="kpi-value">{{ notificationsNonLues.length }}</span>
            <span class="kpi-label">{{ $t('notifications.nouvelles') }}</span>
            <span class="kpi-sub text-info">{{ notifications.length }} {{ $t('commun.total') }}</span>
          </div>
        </div>
      </div>

      <!-- ========== NAVIGATION SIMPLIFIÉE ========== -->
      <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap">
        <ul class="nav nav-pills nav-pills-custom">
        <li class="nav-item">
          <a class="nav-link" :class="{ active: onglet === 'projets' }" @click="onglet = 'projets'" href="javascript:void(0)">
            <i class="fas fa-folder me-2"></i>{{ $t('nav.mesProjets') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: onglet === 'kanban' }" @click="onglet = 'kanban'" href="javascript:void(0)">
            <i class="fas fa-columns me-2"></i>Kanban
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: onglet === 'equipe' }" @click="onglet = 'equipe'" href="javascript:void(0)">
            <i class="fas fa-users me-2"></i>{{ $t('nav.equipe') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: onglet === 'chat' }" @click="onglet = 'chat'" href="javascript:void(0)">
            <i class="fas fa-comments me-2"></i>{{ $t('nav.chat') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: onglet === 'factures' }" @click="onglet = 'factures'" href="javascript:void(0)">
            <i class="fas fa-file-invoice me-2"></i>{{ $t('nav.factures') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: onglet === 'notifications' }" @click="onglet = 'notifications'" href="javascript:void(0)">
            <i class="fas fa-bell me-2"></i>{{ $t('nav.notifications') }}
            <span v-if="notificationsNonLues.length > 0" class="badge bg-danger ms-1">{{ notificationsNonLues.length }}</span>
          </a>
        </li>
          <li class="nav-item">
            <a class="nav-link" :class="{ active: onglet === 'statistiques' }" @click="onglet = 'statistiques'" href="javascript:void(0)">
              <i class="fas fa-chart-pie me-2"></i>{{ $t('nav.statistiques') }}
            </a>
          </li>
      </ul>
        <router-link to="/profil" class="btn btn-link text-decoration-none">
          <i class="fas fa-user me-1"></i>{{ $t('membre.profil') }}
        </router-link>
      </div>

      <!-- ========== ONGLET PROJETS (STYLE CARTES TRELLO) ========== -->
      <div v-if="onglet === 'projets'">
        <div class="section-header mb-3">
          <div>
            <h5 class="section-title">{{ $t('projets.gestionProjets') }}</h5>
            <p class="section-subtitle">{{ mesProjets.length }} {{ $t('projets.projets') }} • {{ projetsActifs.length }} {{ $t('commun.actifs') }}</p>
          </div>
          <button class="btn btn-success" @click="creerProjet" :disabled="!abonnementActif">
            <i class="fas fa-plus me-2"></i>{{ $t('projets.nouveauProjet') }}
          </button>
        </div>

        <div v-if="chargementProjets" class="text-center py-5">
          <div class="spinner-border text-success"></div>
        </div>

        <div v-else-if="mesProjets.length === 0" class="empty-state">
          <i class="fas fa-project-diagram"></i>
          <h5>{{ $t('projets.aucunProjet') }}</h5>
          <p>{{ $t('projets.creerPremierProjet') }}</p>
          <button class="btn btn-success" @click="creerProjet" :disabled="!abonnementActif">
            <i class="fas fa-plus me-2"></i>{{ $t('projets.creerProjet') }}
          </button>
        </div>

        <!-- Grille de cartes projets style Trello -->
        <div v-else class="projects-grid">
          <div v-for="p in mesProjets" :key="p.id" class="project-card" :style="{ '--project-color': getProjetCouleur(p) }">
            <div class="project-card-color-bar"></div>
            <div class="project-card-body">
              <div class="project-card-header">
                <h6 class="project-title">{{ translateProjectTitle(p.titre) }}</h6>
                <span class="badge" :class="getStatutProjetClass(p.statut)">
                  {{ translateData('status', p.statut) }}
                </span>
              </div>

              <p class="project-description">
                {{ translateProjectDescription(p.description).substring(0, 80) }}{{ p.description && p.description.length > 80 ? '...' : '' }}
              </p>

              <div class="project-meta">
                <span class="meta-item"><i class="fas fa-users"></i> {{ getMembresProjet(p.id).length }}</span>
                <span class="meta-item"><i class="fas fa-tasks"></i> {{ getTachesProjet(p.id).length }}</span>
                <span class="meta-item"><i class="fas fa-calendar"></i> {{ formatDate(p.date_creation || p.dateCreation) }}</span>
              </div>

              <div class="project-actions">
                <button class="btn btn-sm btn-light" @click="consulterProjet(p)" :title="$t('tooltips.consulterProjet')">
                  <i class="fas fa-eye"></i>
                </button>
                <button v-if="peutModifierProjet(p)" class="btn btn-sm btn-light" @click="modifierProjet(p)" :title="$t('tooltips.modifierProjet')">
                  <i class="fas fa-edit"></i>
                </button>
                <button v-if="peutModifierProjet(p)" class="btn btn-sm btn-light" @click="toggleVisibilite(p)"
                        :title="p.publique ? $t('projets.rendrePrive') : $t('projets.rendrePublic')">
                  <i :class="p.publique ? 'fas fa-lock' : 'fas fa-globe'"></i>
                </button>
                <button v-if="peutSupprimerProjet(p)" class="btn btn-sm btn-light text-danger" @click="supprimerProjet(p.id)" :title="$t('tooltips.supprimerProjet')">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ========== ONGLET KANBAN ========== -->
      <div v-if="onglet === 'kanban'">
        <div v-if="mesProjets.length === 0" class="alert alert-warning">
          <i class="fas fa-exclamation-triangle me-2"></i>{{ $t('projets.aucunProjet') }}
        </div>

        <div v-else>
          <div class="section-header mb-3">
            <div class="kanban-project-selector">
              <label class="form-label mb-1"><i class="fas fa-project-diagram me-2"></i>{{ $t('projets.selectionnerProjet') }}</label>
              <select class="form-select" v-model="projetKanbanSelectionne" @change="changerProjetKanban">
                <option :value="null">-- {{ $t('projets.choisirProjet') }} --</option>
                <option v-for="p in mesProjets" :key="p.id" :value="p">{{ translateProjectTitle(p.titre) }}</option>
              </select>
            </div>
            <button v-if="projetKanbanSelectionne" class="btn btn-success" @click="ouvrirModalCreationTache" :disabled="!abonnementActif">
              <i class="fas fa-plus me-2"></i>{{ $t('taches.nouvelleTache') }}
            </button>
          </div>

          <KanbanBoard v-if="projetKanbanSelectionne" :projetId="projetKanbanSelectionne.id" :peut-assigner="true" :key="projetKanbanSelectionne.id + '-' + kanbanRefreshKey" @tache-assignee="chargerTaches" />

          <div v-else class="empty-state">
            <i class="fas fa-hand-pointer"></i>
            <h5>{{ $t('projets.selectionnerProjetPourKanban') }}</h5>
          </div>
        </div>
      </div>

      <!-- ========== ONGLET ÉQUIPE (SIMPLIFIÉ) ========== -->
      <div v-if="onglet === 'equipe'">
        <div class="section-header mb-3">
          <div>
            <h5 class="section-title">{{ $t('equipe.collaborateurs') }}</h5>
            <p class="section-subtitle">{{ totalMembres }} {{ $t('equipe.collaborateursSurTousProjets') }}</p>
          </div>
          <button class="btn btn-success" @click="ouvrirModalAjoutMembreGlobal" :disabled="mesProjets.length === 0 || !abonnementActif">
            <i class="fas fa-user-plus me-1"></i>{{ $t('equipe.ajouterMembre') }}
          </button>
        </div>

        <div v-if="mesProjets.length === 0" class="empty-state">
          <i class="fas fa-users"></i>
          <p>{{ $t('projets.aucunProjet') }}</p>
        </div>

        <div v-else class="card border-0 shadow-sm">
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
              <tr>
                <th>{{ $t('projets.projet') }}</th>
                <th>{{ $t('equipe.equipe') }}</th>
                <th class="text-center">{{ $t('taches.tachesAssignees') }}</th>
                <th class="text-center">{{ $t('taches.tachesNonAssignees') }}</th>
                <th class="text-end">{{ $t('commun.actions') }}</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="p in mesProjets" :key="p.id">
                <td class="fw-semibold">{{ translateProjectTitle(p.titre) }}</td>
                <td>
                  <div v-if="getMembresProjet(p.id).length > 0" class="d-flex flex-wrap gap-1">
                      <span v-for="m in getMembresProjet(p.id)" :key="m.id" class="badge bg-light text-dark">
                        {{ m.prenom || m.firstName }} {{ m.nom || m.lastName }}
                      </span>
                  </div>
                  <span v-else class="text-muted small">{{ $t('equipe.aucunMembre') }}</span>
                </td>
                <td class="text-center">
                  <span class="badge bg-success-soft text-success">{{ getTachesAssigneesProjet(p.id).length }}</span>
                </td>
                <td class="text-center">
                    <span class="badge" :class="getTachesNonAssigneesProjet(p.id).length > 0 ? 'bg-warning-soft text-warning' : 'bg-success-soft text-success'">
                      {{ getTachesNonAssigneesProjet(p.id).length }}
                    </span>
                </td>
                <td class="text-end">
                  <div class="btn-group">
                    <button class="btn btn-sm btn-outline-success" @click="ouvrirModalAjoutMembre(p)" :title="$t('tooltips.ajouterMembreProjet')">
                      <i class="fas fa-user-plus"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-info" @click="ouvrirModalAssignationProjet(p)"
                            :disabled="getMembresProjet(p.id).length === 0 || getTachesNonAssigneesProjet(p.id).length === 0">
                      <i class="fas fa-user-tag"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" @click="ouvrirModalRetraitMembre(p)" :disabled="getMembresProjet(p.id).length === 0">
                      <i class="fas fa-user-minus"></i>
                    </button>
                  </div>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ========== ONGLET CHAT PROJET ========== -->
      <div v-if="onglet === 'chat'">
        <div class="chat-container">
          <!-- Liste projets -->
          <div class="chat-sidebar">
            <div class="chat-sidebar-header">
              <h6 class="mb-0"><i class="fas fa-comments me-2"></i>{{ $t('chat.chatProjet') }}</h6>
            </div>
            <div class="chat-project-list">
              <button v-for="p in mesProjets" :key="p.id"
                      class="chat-project-item" :class="{ active: projetChatActuel && projetChatActuel.id === p.id }"
                      @click="ouvrirChatProjet(p)">
                <span class="project-name">{{ translateProjectTitle(p.titre) }}</span>
                <span v-if="getMessagesNonLusProjet(p.id) > 0" class="badge bg-success">{{ getMessagesNonLusProjet(p.id) }}</span>
              </button>
              <div v-if="mesProjets.length === 0" class="text-center text-muted py-4">
                {{ $t('projets.aucunProjet') }}
              </div>
            </div>
          </div>

          <!-- Zone chat -->
          <div class="chat-main">
            <div class="chat-main-header">
              <h6 class="mb-0">
                <i class="fas fa-comments me-2"></i>
                {{ projetChatActuel ? translateProjectTitle(projetChatActuel.titre) : $t('projets.choisirProjet') }}
              </h6>
            </div>

            <div class="chat-messages">
              <div v-if="!projetChatActuel" class="empty-state small-empty">
                <i class="fas fa-hand-pointer"></i>
                <p>{{ $t('projets.choisirProjet') }}</p>
              </div>

              <div v-else-if="messagesChat.length === 0" class="empty-state small-empty">
                <i class="fas fa-comment-slash"></i>
                <p>{{ $t('chat.aucunMessage') }}</p>
              </div>

              <div v-else class="messages-list">
                <div v-for="m in messagesChat" :key="m.id" class="message-bubble" :class="getMessageClass(m)">
                  <div class="message-author">
                    {{ m.auteur?.prenom || m.auteur?.firstName || m.utilisateurNom || $t('commun.inconnu') }}
                    <span class="message-time">{{ formatTime(m.date || m.createdAt) }}</span>
                  </div>
                  <div class="message-content">{{ m.contenu }}</div>
                </div>
              </div>
            </div>

            <div class="chat-input" v-if="projetChatActuel">
              <input class="form-control" v-model.trim="nouveauMessage" :placeholder="$t('collaboration.ecrireMessage')" @keyup.enter="envoyerMessage" />
              <button class="btn btn-success" :disabled="envoyantMessage || !nouveauMessage" @click="envoyerMessage">
                <span v-if="envoyantMessage" class="spinner-border spinner-border-sm"></span>
                <i v-else class="fas fa-paper-plane"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- ========== ONGLET FACTURES ========== -->
      <div v-if="onglet === 'factures'">
        <div class="section-header mb-3">
          <div>
            <h5 class="section-title">{{ $t('factures.mesFactures') }}</h5>
            <p class="section-subtitle">{{ $t('factures.historiquePaiements') }}</p>
          </div>
        </div>

        <div v-if="chargementFactures" class="text-center py-4">
          <div class="spinner-border text-success"></div>
        </div>

        <div v-else-if="factures.length === 0" class="empty-state">
          <i class="fas fa-file-invoice"></i>
          <h5>{{ $t('factures.aucuneFacture') }}</h5>
        </div>

        <div v-else class="card border-0 shadow-sm">
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
              <tr>
                <th>{{ $t('factures.numero') }}</th>
                <th>{{ $t('factures.dateEmission') }}</th>
                <th>{{ $t('factures.montantHT') }}</th>
                <th>{{ $t('factures.tva') }}</th>
                <th>{{ $t('factures.totalTTC') }}</th>
                <th>{{ $t('factures.statut') }}</th>
                <th>{{ $t('commun.actions') }}</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="f in factures" :key="f.id">
                <td class="fw-semibold">{{ f.numeroFacture }}</td>
                <td>{{ formatDate(f.dateEmission) }}</td>
                <td>{{ formatPrix(f.montantHT ?? f.montantHt ?? 0) }}</td>
                <td>21% ({{ formatPrix(f.tva ?? (f.montantHT ?? f.montantHt ?? 0) * 0.21) }})</td>
                <td class="fw-bold">{{ formatPrix(f.montantTtc ?? (f.montantHT ?? f.montantHt ?? 0) + (f.tva ?? (f.montantHT ?? f.montantHt ?? 0) * 0.21)) }}</td>
                <td>
                    <span class="badge rounded-pill" :class="f.statut === 'GENEREE' ? 'bg-success' : 'bg-secondary'">
                      {{ translateData('invoiceStatus', f.statut) }}
                    </span>
                </td>
                <td>
                  <button class="btn btn-sm btn-outline-primary" @click="telechargerFacture(f)">
                    <i class="fas fa-download me-1"></i>{{ $t('factures.telecharger') }}
                  </button>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- ========== ONGLET NOTIFICATIONS ========== -->
      <div v-if="onglet === 'notifications'">
        <div class="section-header mb-3">
          <div>
            <h5 class="section-title">{{ $t('nav.notifications') }}</h5>
            <p class="section-subtitle">{{ notificationsNonLues.length }} {{ $t('notifications.nonLues') }} {{ $t('commun.sur') }} {{ notifications.length }}</p>
          </div>
          <button class="btn btn-outline-secondary btn-sm" @click="marquerToutesLues" :disabled="notificationsNonLues.length === 0">
            <i class="fas fa-check-double me-1"></i>{{ $t('notifications.marquerToutesLues') }}
          </button>
        </div>

        <div v-if="chargementNotifications" class="text-center py-4">
          <div class="spinner-border text-info"></div>
        </div>

        <div v-else-if="notifications.length === 0" class="empty-state">
          <i class="fas fa-bell-slash"></i>
          <p>{{ $t('notifications.aucuneNotification') }}</p>
        </div>

        <div v-else class="notifications-list">
          <div v-for="n in notifications" :key="n.id" class="notification-item" :class="{ unread: !n.lu }">
            <div class="notification-icon" :class="getNotificationIconClass(n.type)">
              <i :class="getNotificationIcon(n.type)"></i>
            </div>
            <div class="notification-content">
              <div class="notification-title">{{ n.titre || n.title || $t('notifications.notification') }}</div>
              <div class="notification-message">{{ n.message || n.contenu }}</div>
              <div class="notification-time">{{ formatDateRelative(n.date || n.createdAt) }}</div>
            </div>
            <div class="notification-actions">
              <button v-if="!n.lu" class="btn btn-sm btn-outline-success" @click="marquerNotificationLue(n)">
                <i class="fas fa-check"></i>
              </button>
              <button class="btn btn-sm btn-outline-danger" @click="supprimerNotification(n)">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

    </div>
    <!-- ========== ONGLET STATISTIQUES (PREMIUM) ========== -->
    <div v-if="onglet === 'statistiques'">
      <div class="section-header mb-3">
        <div>
          <h5 class="section-title"><i class="fas fa-chart-pie me-2 text-warning"></i>{{ $t('statistiques.titre') }}</h5>
          <p class="section-subtitle">{{ $t('statistiques.description') }}</p>
        </div>
      </div>

      <div class="row g-4">
        <!-- Camembert : Répartition des tâches -->
        <div class="col-md-6">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-header bg-white">
              <h6 class="mb-0"><i class="fas fa-tasks me-2 text-warning"></i>{{ $t('statistiques.repartitionTaches') }}</h6>
            </div>
            <div class="card-body">
              <canvas id="chartTaches" height="250"></canvas>
            </div>
            <div class="card-footer bg-light">
              <div class="d-flex justify-content-around text-center small">
                <div><span class="badge bg-secondary">{{ statsCalculees.taches.brouillon }}</span> {{ $t('taches.statuts.brouillon') }}</div>
                <div><span class="badge bg-warning text-dark">{{ statsCalculees.taches.enAttente }}</span> {{ $t('taches.statuts.enAttente') }}</div>
                <div><span class="badge bg-success">{{ statsCalculees.taches.termine }}</span> {{ $t('taches.statuts.termine') }}</div>
                <div><span class="badge bg-danger">{{ statsCalculees.taches.annule }}</span> {{ $t('taches.statuts.annule') }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Camembert : Répartition des projets -->
        <div class="col-md-6">
          <div class="card border-0 shadow-sm h-100">
            <div class="card-header bg-white">
              <h6 class="mb-0"><i class="fas fa-project-diagram me-2 text-primary"></i>{{ $t('statistiques.repartitionProjets') }}</h6>
            </div>
            <div class="card-body">
              <canvas id="chartProjets" height="250"></canvas>
            </div>
            <div class="card-footer bg-light">
              <div class="d-flex justify-content-around text-center small">
                <div><span class="badge bg-success">{{ statsCalculees.projets.actif }}</span> {{ $t('statuts.ACTIF') }}</div>
                <div><span class="badge bg-warning text-dark">{{ statsCalculees.projets.suspendu }}</span> {{ $t('statuts.SUSPENDU') }}</div>
                <div><span class="badge bg-secondary">{{ statsCalculees.projets.termine }}</span> {{ $t('statuts.TERMINE') }}</div>
                <div><span class="badge bg-danger">{{ statsCalculees.projets.annule }}</span> {{ $t('statuts.ANNULE') }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Indicateurs clés -->
        <div class="col-12">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-white">
              <h6 class="mb-0"><i class="fas fa-tachometer-alt me-2 text-info"></i>{{ $t('statistiques.indicateurs') }}</h6>
            </div>
            <div class="card-body">
              <div class="row g-3">
                <div class="col-md-3">
                  <div class="stat-box text-center p-3 bg-light rounded">
                    <div class="stat-value text-primary">{{ statsCalculees.tauxCompletion }}%</div>
                    <div class="stat-label">{{ $t('statistiques.tauxCompletion') }}</div>
                    <div class="progress mt-2" style="height: 6px;">
                      <div class="progress-bar bg-primary" :style="{ width: statsCalculees.tauxCompletion + '%' }"></div>
                    </div>
                  </div>
                </div>
                <div class="col-md-3">
                  <div class="stat-box text-center p-3 bg-light rounded">
                    <div class="stat-value text-success">{{ statsCalculees.tachesAssignees }}</div>
                    <div class="stat-label">{{ $t('statistiques.tachesAssignees') }}</div>
                    <small class="text-muted">{{ $t('commun.sur') }} {{ totalTaches.length }} {{ $t('commun.total') }}</small>
                  </div>
                </div>
                <div class="col-md-3">
                  <div class="stat-box text-center p-3 bg-light rounded">
                    <div class="stat-value text-warning">{{ statsCalculees.tachesNonAssignees }}</div>
                    <div class="stat-label">{{ $t('statistiques.tachesNonAssignees') }}</div>
                    <small class="text-muted">{{ $t('statistiques.aAssigner') }}</small>
                  </div>
                </div>
                <div class="col-md-3">
                  <div class="stat-box text-center p-3 bg-light rounded">
                    <div class="stat-value text-info">{{ statsCalculees.moyenneMembres }}</div>
                    <div class="stat-label">{{ $t('statistiques.moyenneMembresProjet') }}</div>
                    <small class="text-muted">{{ totalMembres }} {{ $t('commun.total') }}</small>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== SUPPORT PREMIUM (EN BAS) ========== -->
    <div v-if="abonnementActif && !chargementGlobal" class="support-premium-bar">
      <div class="d-flex align-items-center gap-2">
        <i class="fas fa-headset"></i>
        <span><strong>{{ $t('tableauBord.chefProjet.supportPremium.titre') }}</strong> : {{ $t('tableauBord.chefProjet.supportPremium.description') }}</span>
      </div>
      <router-link to="/contact" class="btn btn-sm btn-outline-success">
        <i class="fas fa-envelope me-1"></i>{{ $t('tableauBord.chefProjet.supportPremium.bouton') }}
      </router-link>
    </div>
    <!-- Modal création projet -->
    <div v-if="showCreateProject" class="modal d-block" style="background: rgba(0, 0, 0, 0.5); z-index: 1060">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ $t('projets.nouveauProjet') }}</h5>
            <button class="btn-close" @click="showCreateProject = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.nom') }}</label>
              <input class="form-control" v-model.trim="projetForm.titre" maxlength="120" :placeholder="$t('projets.nomProjet')" required />
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.description') }}</label>
              <textarea class="form-control" v-model.trim="projetForm.description" rows="3" maxlength="500" :placeholder="$t('projets.descriptionProjet')"></textarea>
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.visibilite') }}</label>
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" id="projetPublique" v-model="projetForm.publique">
                <label class="form-check-label" for="projetPublique">
                  <span v-if="projetForm.publique"><i class="fas fa-globe text-success me-1"></i>{{ $t('projets.public') }}</span>
                  <span v-else><i class="fas fa-lock text-secondary me-1"></i>{{ $t('projets.prive') }}</span>
                </label>
              </div>
              <small class="text-muted">{{ projetForm.publique ? $t('projets.publicDescription') : $t('projets.priveDescription') }}</small>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="showCreateProject = false">{{ $t('commun.annuler') }}</button>
            <button class="btn btn-success" @click="sauvegarderNouveauProjet" :disabled="!projetForm.titre.trim()">
              <i class="fas fa-check me-1"></i>{{ $t('commun.creer') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal modification projet -->
    <div v-if="modalModificationProjet" class="modal d-block" style="background: rgba(0, 0, 0, 0.5); z-index: 1060">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-warning">
            <h5 class="modal-title"><i class="fas fa-edit me-2"></i>{{ $t('projets.modifierProjet') }}</h5>
            <button class="btn-close" @click="fermerModalModificationProjet"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.nom') }} *</label>
              <input class="form-control" v-model.trim="projetModifForm.titre" maxlength="120" required />
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.description') }}</label>
              <textarea class="form-control" v-model.trim="projetModifForm.description" rows="3" maxlength="500"></textarea>
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.statut') }}</label>
              <select class="form-select" v-model="projetModifForm.statut">
                <option value="ACTIF">{{ $t('statuts.ACTIF') }}</option>
                <option value="SUSPENDU">{{ $t('statuts.SUSPENDU') }}</option>
                <option value="TERMINE">{{ $t('statuts.TERMINE') }}</option>
                <option value="ANNULE">{{ $t('statuts.ANNULE') }}</option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.visibilite') }}</label>
              <div class="form-check form-switch">
                <input class="form-check-input" type="checkbox" id="projetModifPublique" v-model="projetModifForm.publique">
                <label class="form-check-label" for="projetModifPublique">
                  <span v-if="projetModifForm.publique"><i class="fas fa-globe text-success me-1"></i>{{ $t('projets.public') }}</span>
                  <span v-else><i class="fas fa-lock text-secondary me-1"></i>{{ $t('projets.prive') }}</span>
                </label>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="fermerModalModificationProjet">{{ $t('commun.annuler') }}</button>
            <button class="btn btn-warning" @click="sauvegarderModificationProjet" :disabled="!projetModifForm.titre.trim()">
              <i class="fas fa-save me-1"></i>{{ $t('commun.enregistrer') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal création tâche -->
    <div v-if="modalCreationTache" class="modal d-block" style="background: rgba(0, 0, 0, 0.5); z-index: 1060">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-success text-white">
            <h5 class="modal-title"><i class="fas fa-plus me-2"></i>{{ $t('taches.nouvelleTache') }}</h5>
            <button class="btn-close btn-close-white" @click="modalCreationTache = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.projet') }} *</label>
              <select class="form-select" v-model="nouvelleTache.projetId" required>
                <option :value="null">-- {{ $t('projets.choisirProjet') }} --</option>
                <option v-for="p in mesProjets" :key="p.id" :value="p.id">{{ translateProjectTitle(p.titre) }}</option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('taches.titre') }} *</label>
              <input class="form-control" v-model="nouvelleTache.titre" maxlength="120" required />
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('taches.description') }}</label>
              <textarea class="form-control" v-model="nouvelleTache.description" rows="3"></textarea>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">{{ $t('taches.priorite') }}</label>
                <select class="form-select" v-model="nouvelleTache.priorite">
                  <option value="BASSE">{{ $t('priorites.BASSE') }}</option>
                  <option value="NORMALE">{{ $t('priorites.NORMALE') }}</option>
                  <option value="HAUTE">{{ $t('priorites.HAUTE') }}</option>
                  <option value="URGENTE">{{ $t('priorites.URGENTE') }}</option>
                </select>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">{{ $t('taches.dateEcheance') }}</label>
                <input type="date" class="form-control" v-model="nouvelleTache.dateEcheance" />
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="modalCreationTache = false">{{ $t('commun.annuler') }}</button>
            <button class="btn btn-success" @click="creerNouvelleTache" :disabled="!nouvelleTache.titre || !nouvelleTache.projetId">
              <i class="fas fa-check me-1"></i>{{ $t('commun.creer') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal ajout membre -->
    <div v-if="modalAjoutMembre" class="modal d-block" style="background: rgba(0, 0, 0, 0.5); z-index: 1060">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title"><i class="fas fa-user-plus me-2"></i>{{ $t('equipe.ajouterMembre') }}</h5>
            <button class="btn-close" @click="fermerModalAjoutMembre"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.selectionnerProjet') }}</label>
              <select class="form-select" v-model="projetSelectionne" :disabled="projetPreSelectionne !== null">
                <option :value="null">{{ $t('projets.choisirProjet') }}</option>
                <option v-for="p in mesProjets" :key="p.id" :value="p">{{ translateProjectTitle(p.titre) }}</option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('equipe.rechercherUtilisateur') }}</label>
              <input class="form-control" v-model="rechercheUtilisateur" @input="rechercherUtilisateurs" :placeholder="$t('equipe.tapezEmail')" />
            </div>
            <div v-if="rechercheUtilisateur.length >= 2" class="mb-3">
              <div v-if="utilisateursRecherche.length > 0" class="list-group">
                <button v-for="u in utilisateursRecherche" :key="u.id"
                        class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                        :class="{ active: membreSelectionne && membreSelectionne.id === u.id }"
                        @click="selectionnerUtilisateur(u)" type="button">
                  <div>
                    <div class="fw-semibold">{{ u.prenom || u.firstName }} {{ u.nom || u.lastName }}</div>
                    <small class="text-muted">{{ u.email }}</small>
                  </div>
                  <i class="fas" :class="membreSelectionne && membreSelectionne.id === u.id ? 'fa-check-circle text-success' : 'fa-circle text-muted'"></i>
                </button>
              </div>
              <div v-else class="alert alert-info mb-0">
                <i class="fas fa-info-circle me-2"></i>{{ $t('equipe.aucunResultat') }}
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="fermerModalAjoutMembre">{{ $t('commun.annuler') }}</button>
            <button class="btn btn-success" @click="ajouterMembreConfirme" :disabled="!projetSelectionne || !membreSelectionne">
              <i class="fas fa-user-plus me-1"></i>{{ $t('equipe.ajouter') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Assignation -->
    <div v-if="modalAssignationTache" class="modal d-block" style="background: rgba(0, 0, 0, 0.5); z-index: 1060">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title"><i class="fas fa-user-tag me-2"></i>{{ $t('taches.assignerTache') }}</h5>
            <button class="btn-close btn-close-white" @click="fermerModalAssignation"></button>
          </div>
          <div class="modal-body">
            <div class="alert alert-info d-flex align-items-center mb-3">
              <i class="fas fa-project-diagram fa-2x me-3"></i>
              <div>
                <strong>{{ $t('projets.projet') }}:</strong>
                <div class="fw-bold fs-5">{{ projetPourAssignation ? translateProjectTitle(projetPourAssignation.titre) : '—' }}</div>
              </div>
            </div>

            <div v-if="getMembresProjet(projetPourAssignation?.id).length === 0" class="alert alert-warning">
              <i class="fas fa-exclamation-triangle me-2"></i>{{ $t('taches.aucunMembreProjet') }}
              <button class="btn btn-warning btn-sm mt-2" @click="redirectToAddMember">
                <i class="fas fa-user-plus me-1"></i>{{ $t('equipe.ajouterMembre') }}
              </button>
            </div>

            <div v-else-if="getTachesNonAssigneesProjet(projetPourAssignation?.id).length === 0" class="alert alert-success">
              <i class="fas fa-check-circle me-2"></i>{{ $t('taches.toutesLesTachesAssignees') }}
            </div>

            <div v-else>
              <div class="mb-4">
                <label class="form-label fw-bold"><i class="fas fa-tasks me-2 text-warning"></i>1. {{ $t('taches.selectionnerTache') }}</label>
                <select class="form-select form-select-lg" v-model="tacheAAssigner">
                  <option :value="null">-- {{ $t('taches.choisirTache') }} --</option>
                  <option v-for="t in tachesDisponibles" :key="t.id" :value="t">{{ t.titre }}</option>
                </select>
              </div>

              <div v-if="tacheAAssigner" class="mb-3">
                <label class="form-label fw-bold"><i class="fas fa-user me-2 text-success"></i>2. {{ $t('taches.selectionnerMembre') }}</label>
                <div class="list-group">
                  <button v-for="m in getMembresProjet(projetPourAssignation?.id)" :key="m.id"
                          class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                          :class="{ active: membreAssigneSelectionne && membreAssigneSelectionne.id === m.id }"
                          @click="selectionnerMembreAssignation(m)" type="button">
                    <div>
                      <div class="fw-semibold">{{ m.prenom || m.firstName }} {{ m.nom || m.lastName }}</div>
                      <small class="text-muted">{{ m.email }}</small>
                    </div>
                    <i class="fas fa-2x" :class="membreAssigneSelectionne && membreAssigneSelectionne.id === m.id ? 'fa-check-circle text-success' : 'fa-circle text-muted'"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="fermerModalAssignation">{{ $t('commun.annuler') }}</button>
            <button class="btn btn-success btn-lg" @click="confirmerAssignation" :disabled="!tacheAAssigner || !membreAssigneSelectionne">
              <i class="fas fa-check me-1"></i>{{ $t('taches.assigner') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Retrait Membre -->
    <div v-if="modalRetraitMembre" class="modal d-block" style="background: rgba(0, 0, 0, 0.5); z-index: 1060">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title"><i class="fas fa-user-minus me-2"></i>{{ $t('equipe.retirerMembre') }}</h5>
            <button class="btn-close btn-close-white" @click="fermerModalRetraitMembre"></button>
          </div>
          <div class="modal-body">
            <div class="alert alert-info mb-3">
              <i class="fas fa-project-diagram me-2"></i><strong>{{ $t('projets.projet') }}:</strong>
              {{ projetPourRetrait ? translateProjectTitle(projetPourRetrait.titre) : '—' }}
            </div>
            <div v-if="getMembresProjet(projetPourRetrait?.id).length === 0" class="alert alert-warning">
              <i class="fas fa-exclamation-triangle me-2"></i>{{ $t('equipe.aucunMembreARetirer') }}
            </div>
            <div v-else class="mb-3">
              <label class="form-label fw-bold">{{ $t('equipe.selectionnerMembre') }}</label>
              <div class="list-group">
                <button v-for="m in getMembresProjet(projetPourRetrait?.id)" :key="m.id"
                        class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                        :class="{ 'active bg-danger text-white': membreARetirer && membreARetirer.id === m.id }"
                        @click="membreARetirer = m" type="button">
                  <div>
                    <div class="fw-semibold">{{ m.prenom || m.firstName }} {{ m.nom || m.lastName }}</div>
                    <small :class="membreARetirer && membreARetirer.id === m.id ? 'text-white-50' : 'text-muted'">{{ m.email }}</small>
                  </div>
                  <i class="fas" :class="membreARetirer && membreARetirer.id === m.id ? 'fa-check-circle' : 'fa-circle text-muted'"></i>
                </button>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="fermerModalRetraitMembre">{{ $t('commun.annuler') }}</button>
            <button class="btn btn-danger" @click="confirmerRetraitMembre" :disabled="!membreARetirer">
              <i class="fas fa-user-minus me-1"></i>{{ $t('equipe.retirer') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal COMMENTAIRES (F12) -->
    <div v-if="modalCommentaires" class="modal d-block" style="background: rgba(0, 0, 0, 0.5); z-index: 1060">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title"><i class="fas fa-comments me-2"></i>{{ $t('commentaires.commentairesTache') }}</h5>
            <button class="btn-close btn-close-white" @click="fermerModalCommentaires"></button>
          </div>
          <div class="modal-body">
            <div class="alert alert-info d-flex align-items-center mb-3">
              <i class="fas fa-tasks fa-2x me-3"></i>
              <div>
                <strong>{{ $t('taches.tache') }}:</strong>
                <div class="fw-bold">{{ tacheSelectionnee?.titre || '—' }}</div>
              </div>
            </div>

            <div class="commentaires-list mb-3" style="max-height: 400px; overflow-y: auto">
              <div v-if="chargementCommentaires" class="text-center py-4">
                <div class="spinner-border text-primary"></div>
              </div>
              <div v-else-if="commentairesTache.length === 0" class="text-center text-muted py-5">
                <i class="fas fa-comment-slash fa-3x mb-3"></i>
                <p>{{ $t('commentaires.aucunCommentaire') }}</p>
              </div>
              <div v-else>
                <div v-for="c in commentairesTache" :key="c.id" class="card mb-2 commentaire-item">
                  <div class="card-body p-3">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                      <div class="d-flex align-items-center gap-2">
                        <div class="avatar-circle bg-primary text-white">{{ getInitiales(c.auteurPrenom, c.auteurNom) }}</div>
                        <div>
                          <div class="fw-semibold">{{ c.auteurPrenom }} {{ c.auteurNom }}</div>
                          <small class="text-muted">{{ formatDateRelative(c.date) }}</small>
                        </div>
                      </div>
                      <button v-if="peutSupprimerCommentaire(c)" class="btn btn-sm btn-outline-danger" @click="supprimerCommentaire(c.id)">
                        <i class="fas fa-trash"></i>
                      </button>
                    </div>
                    <div class="commentaire-contenu">{{ c.contenu }}</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="border-top pt-3">
              <label class="form-label fw-bold"><i class="fas fa-plus-circle me-2"></i>{{ $t('commentaires.ajouterCommentaire') }}</label>
              <textarea class="form-control mb-2" v-model.trim="nouveauCommentaire" rows="3" :placeholder="$t('commentaires.votreMessage')" maxlength="1000"></textarea>
              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">{{ nouveauCommentaire.length }}/1000</small>
                <button class="btn btn-primary" @click="ajouterCommentaire" :disabled="!nouveauCommentaire.trim() || ajoutCommentaireEnCours">
                  <span v-if="ajoutCommentaireEnCours" class="spinner-border spinner-border-sm me-1"></span>
                  <i v-else class="fas fa-paper-plane me-1"></i>{{ $t('commun.envoyer') }}
                </button>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" @click="fermerModalCommentaires">{{ $t('commun.fermer') }}</button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>
<script>
import KanbanBoard from '@/components/KanbanBoard.vue'
import { useDataTranslation } from '@/composables/useDataTranslation'
import { Chart, registerables } from 'chart.js'
Chart.register(...registerables)
import { useAuthStore } from '@/stores/auth'
import {
  projectAPI,
  taskAPI,
  userAPI,
  abonnementAPI,
  factureAPI,
  messagesAPI,
  notificationAPI,
  commentaireAPI,
} from '@/services/api'
import WebSocketService from '@/services/websocket.service.js'
import tacheService from '@/services/tache.service.js'

export default {
  name: 'TableauBordChefProjet',
  components: {
    KanbanBoard
  },
  setup() {
    const { translateData, translateProjectTitle, translateProjectDescription } =
      useDataTranslation()
    return { translateData, translateProjectTitle, translateProjectDescription }
  },
  data() {
    return {
      onglet: 'projets',
      chargementGlobal: true,
      chargementProjets: false,
      chargementTaches: false,
      chargementFactures: false,
      chargementNotifications: false,
      chargementCommentaires: false,
      envoyantMessage: false,
      ajoutCommentaireEnCours: false,
      mesProjets: [],
      totalTaches: [],
      abonnement: null,
      factures: [],
      notifications: [],
      messagesChat: [],
      messagesParProjet: {},
      membresParProjet: {},
      utilisateursRecherche: [],
      commentairesTache: [],
      tacheSelectionnee: null,
      nouveauCommentaire: '',
      modalCommentaires: false,
      filtreProjetTache: '',
      projetChatActuel: null,
      nouveauMessage: '',
      modalAjoutMembre: false,
      projetSelectionne: null,
      projetPreSelectionne: null,
      membreSelectionne: null,
      rechercheUtilisateur: '',
      modalAssignationTache: false,
      tacheAAssigner: null,
      membreAssigneSelectionne: null,
      projetPourAssignation: null,
      tachesDisponibles: [],
      modalRetraitMembre: false,
      projetPourRetrait: null,
      membreARetirer: null,
      erreurBackend: null,
      showCreateProject: false,
      projetForm: { titre: '', description: '', publique: false },
      modalModificationProjet: false,
      projetEnModification: null,
      projetModifForm: { titre: '', description: '', statut: 'ACTIF', publique: false },
      subscribedTopics: new Set(),
      projetKanbanSelectionne: null,
      kanbanRefreshKey: 0,
      modalCreationTache: false,
      nouvelleTache: { projetId: null, titre: '', description: '', priorite: 'NORMALE', dateEcheance: null },
      // Couleurs pour les projets
      projetCouleurs: ['#4CAF50', '#2196F3', '#9C27B0', '#FF9800', '#E91E63', '#00BCD4', '#795548', '#607D8B'],
      // Statistiques - instances Chart.js
      chartTachesInstance: null,
      chartProjetsInstance: null,
    }
  },
  computed: {
    utilisateur() {
      return useAuthStore().user
    },
    abonnementActif() {
      if (!this.abonnement) return false
      const maintenant = new Date()
      const dateFin = new Date(this.abonnement.date_fin || this.abonnement.dateFin)
      return (this.abonnement.statut || 'ACTIF') === 'ACTIF' && dateFin > maintenant
    },
    projetsActifs() {
      return this.mesProjets.filter((p) => p.statut !== 'TERMINE' && p.statut !== 'ANNULE')
    },
    tachesEnAttente() {
      return this.totalTaches.filter((t) => t.statut === 'EN_ATTENTE_VALIDATION')
    },
    totalMembres() {
      const membresUniques = new Set()
      Object.values(this.membresParProjet).forEach((membres) => {
        membres.forEach((m) => membresUniques.add(this.normalizeId(m.id)))
      })
      return membresUniques.size
    },
    notificationsNonLues() {
      return this.notifications.filter((n) => !n.lu)

    },

    statsCalculees() {
      // Répartition des tâches par statut
      const taches = {
        brouillon: this.totalTaches.filter(t => t.statut === 'BROUILLON').length,
        enAttente: this.totalTaches.filter(t => t.statut === 'EN_ATTENTE_VALIDATION').length,
        termine: this.totalTaches.filter(t => t.statut === 'TERMINE').length,
        annule: this.totalTaches.filter(t => t.statut === 'ANNULE').length
      }

      // Répartition des projets par statut
      const projets = {
        actif: this.mesProjets.filter(p => p.statut === 'ACTIF').length,
        suspendu: this.mesProjets.filter(p => p.statut === 'SUSPENDU').length,
        termine: this.mesProjets.filter(p => p.statut === 'TERMINE').length,
        annule: this.mesProjets.filter(p => p.statut === 'ANNULE').length
      }

      // Taux de complétion
      const tauxCompletion = this.totalTaches.length > 0
        ? Math.round((taches.termine / this.totalTaches.length) * 100)
        : 0

      // Tâches assignées / non assignées
      const tachesAssignees = this.totalTaches.filter(t => t.assigneId || t.idAssigne || t.id_assigne).length
      const tachesNonAssignees = this.totalTaches.length - tachesAssignees

      // Moyenne membres par projet
      const moyenneMembres = this.mesProjets.length > 0
        ? Math.round(this.totalMembres / this.mesProjets.length * 10) / 10
        : 0

      return { taches, projets, tauxCompletion, tachesAssignees, tachesNonAssignees, moyenneMembres }
    },
  },
  async mounted() {
    this.verifierEtNettoyerUtilisateur()
    const store = useAuthStore()
    if (!store.user && localStorage.getItem('user')) {
      try {
        store.setUser(JSON.parse(localStorage.getItem('user')))
      } catch (e) {
        console.error('[Mount] Erreur parsing user:', e)
      }
    }
    try {
      await this.chargerTachesChefProjet()
    } catch (e) {
      console.error('[Mount] Erreur chargement tâches chef:', e)
    }
    if (!store.user || (store.user.role !== 'CHEF_PROJET' && store.user.role !== 'MEMBRE')) {
      this.erreurBackend = this.$t('erreurs.accesReserveChefsProjets')
      this.chargementGlobal = false
      return
    }
    await this.chargerToutesDonnees()
    const token = localStorage.getItem('token')
    if (token && Array.isArray(this.mesProjets) && this.mesProjets.length > 0) this.initWebsocket()
    if (this.$route.query.newProject === '1') this.ouvrirModalProjet()
  },
  watch: {
    onglet(nv) {
      if (nv === 'notifications' && this.notifications.length === 0) this.chargerNotifications()
      if (nv === 'chat' && !this.projetChatActuel && this.mesProjets[0])
        this.ouvrirChatProjet(this.mesProjets[0])
      if (nv === 'statistiques') this.dessinerGraphiques()
    },

    '$i18n.locale'() {
      if (this.onglet === 'statistiques') {
        this.dessinerGraphiques()
      }
    }
  },
  methods: {
    normalizeId(v) {
      return v == null ? v : String(v).split(':')[0]
    },

    seDeconnecter() {
      try {
        useAuthStore().logout?.()
      } catch {}
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.replace({ path: '/connexion', query: { switch: '1' } })
    },

    verifierEtNettoyerUtilisateur() {
      try {
        const userStr = localStorage.getItem('user')
        if (!userStr) return
        const user = JSON.parse(userStr)
        if (user.id && String(user.id).includes(':')) {
          localStorage.removeItem('user')
          localStorage.removeItem('token')
          alert(this.$t('erreurs.sessionExpiree'))
          this.$router.push('/connexion')
        }
      } catch (e) {
        console.error('[Verify] Erreur vérification:', e)
      }
    },

    async chargerToutesDonnees() {
      this.chargementGlobal = true
      this.erreurBackend = null
      try {
        await this.chargerProjets()
        await this.chargerTaches()
        await Promise.all([
          this.chargerFactures(),
          this.chargerNotifications(),
          this.chargerAbonnement(),
        ])
      } catch (e) {
        console.error('[Load] Erreur chargement:', e)
        this.erreurBackend = this.$t('erreurs.chargementDonnees')
      } finally {
        this.chargementGlobal = false
      }
    },

    async chargerTachesChefProjet() {
      try {
        const userId = this.utilisateur?.id
        const result = await tacheService.getTachesUtilisateur(userId)
        if (result.success) this.taches = result.data
      } catch (error) {
        console.error('[Frontend] Erreur chargement tâches:', error)
      }
    },

    async chargerProjets() {
      this.chargementProjets = true
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        const r = await projectAPI.byUser(userId)
        let projets = Array.isArray(r.data) ? r.data : Array.isArray(r.data?.content) ? r.data.content : []
        projets = projets.filter((p) => this.normalizeId(p.idCreateur) === userId)
        this.mesProjets = projets.map((p) => ({ ...p, id: this.normalizeId(p.id) }))
        await Promise.all(this.mesProjets.map((projet) => this.chargerMembresProjet(projet.id)))
        this.$forceUpdate()
      } catch (e) {
        console.error('[Projets] Erreur:', e)
        this.mesProjets = []
        if (e.response?.status === 401) {
          this.erreurBackend = this.$t('erreurs.sessionExpiree')
        } else {
          this.erreurBackend = this.$t('erreurs.chargementProjets')
        }
      } finally {
        this.chargementProjets = false
      }
    },

    async chargerMembresProjet(projetId) {
      try {
        const r = await projectAPI.getProjectMembers(projetId)
        const membres = Array.isArray(r.data) ? r.data : []
        this.membresParProjet = { ...this.membresParProjet, [projetId]: membres }
      } catch (e) {
        console.error(`[Membres projet ${projetId}] Erreur:`, e)
        this.membresParProjet[projetId] = []
      }
    },

    async chargerTaches() {
      this.chargementTaches = true
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        const r = await taskAPI.byChefProjet(userId)
        if (!r || !r.data) {
          this.totalTaches = []
          this.chargementTaches = false
          return
        }
        let taches = Array.isArray(r.data) ? r.data : Array.isArray(r.data?.content) ? r.data.content : []
        if (this.mesProjets.length === 0) {
          this.totalTaches = taches.map((t) => ({
            ...t,
            id: this.normalizeId(t.id),
            projetId: this.normalizeId(t.projetId || t.id_projet || t.idProjet),
            id_projet: this.normalizeId(t.id_projet || t.projetId || t.idProjet),
            idProjet: this.normalizeId(t.idProjet || t.projetId || t.id_projet),
            assigneId: t.assigneId ?? t.idAssigne ?? t.id_assigne ?? null,
            id_assigne: t.id_assigne ?? t.assigneId ?? t.idAssigne ?? null,
            idAssigne: t.idAssigne ?? t.assigneId ?? t.id_assigne ?? null,
          }))
        } else {
          const projetsExistantsIds = new Set(this.mesProjets.map(p => this.normalizeId(p.id)))
          this.totalTaches = taches
            .map((t) => ({
              ...t,
              id: this.normalizeId(t.id),
              projetId: this.normalizeId(t.projetId || t.id_projet || t.idProjet),
              id_projet: this.normalizeId(t.id_projet || t.projetId || t.idProjet),
              idProjet: this.normalizeId(t.idProjet || t.projetId || t.id_projet),
              assigneId: t.assigneId ?? t.idAssigne ?? t.id_assigne ?? null,
              id_assigne: t.id_assigne ?? t.assigneId ?? t.idAssigne ?? null,
              idAssigne: t.idAssigne ?? t.assigneId ?? t.id_assigne ?? null,
            }))
            .filter((t) => {
              const projetId = t.projetId || t.id_projet || t.idProjet
              return projetId && projetsExistantsIds.has(this.normalizeId(projetId))
            })
        }
      } catch (e) {
        console.error('[chargerTaches] Erreur:', e)
        this.totalTaches = []
      } finally {
        this.chargementTaches = false
      }
    },

    async chargerAbonnement() {
      try {
        const u = this.utilisateur
        if (!u || !u.id) return
        const userId = this.normalizeId(u.id)
        const token = localStorage.getItem('token')
        if (!token) return
        const r = await abonnementAPI.getByUserId(userId, token)
        this.abonnement = r.data
      } catch (e) {
        if (e?.response?.status === 404 || e?.response?.status === 401) this.abonnement = null
        else this.abonnement = null
      }
      if (!this.abonnement) this.abonnement = { statut: 'INACTIF', date_fin: null }
    },

    async chargerFactures() {
      this.chargementFactures = true
      try {
        const r = await factureAPI.getMesFactures()
        const arr = Array.isArray(r.data) ? r.data : Array.isArray(r.data?.content) ? r.data.content : []
        this.factures = arr.map((f) => {
          const id = this.normalizeId(f.id ?? f.id)
          const montantHT = Number(f.montantHT ?? f.montantHt ?? f.montant_ht ?? 0)
          const tva = Number(f.tva ?? montantHT * 0.21)
          return {
            id,
            numeroFacture: f.numeroFacture ?? f.numero_facture ?? `FAC-${id}`,
            dateEmission: f.dateEmission ?? f.date_emission ?? null,
            montantHT,
            montantHt: montantHT,
            tva,
            montantTtc: montantHT + tva,
            statut: f.statut ?? 'GENEREE',
          }
        })
      } catch (e) {
        console.error('[Factures] Erreur:', e)
        this.factures = []
      } finally {
        this.chargementFactures = false
      }
    },

    async chargerNotifications() {
      this.chargementNotifications = true
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        const r = await notificationAPI.list(userId)
        this.notifications = Array.isArray(r.data) ? r.data : []
      } catch (e) {
        console.error('[Notifications] Erreur:', e)
        this.notifications = []
      } finally {
        this.chargementNotifications = false
      }
    },

    initWebsocket() {
      try {
        const token = localStorage.getItem('token')
        if (!token || !this.utilisateur?.id) return
        WebSocketService.connect(token)
        const userId = this.normalizeId(this.utilisateur.id)
        const topicUser = `/user/${userId}/topic/notifications`
        if (!this.subscribedTopics.has(topicUser)) {
          WebSocketService.subscribe(topicUser, (msg) => {
            if (msg?.type === 'NOTIFICATION') {
              this.notifications.unshift({
                id: msg.id || Date.now(),
                message: msg.message || msg.contenu,
                titre: msg.titre || this.$t('notifications.notification'),
                date: msg.createdAt || new Date().toISOString(),
                lu: false,
                type: msg.sousType || msg.type || 'SYSTEME',
              })
              if ('Notification' in window && Notification.permission === 'granted')
                new Notification(msg.titre || this.$t('notifications.notification'), {
                  body: msg.message || msg.contenu,
                  icon: '/favicon.ico',
                })
            }
          })
          this.subscribedTopics.add(topicUser)
        }
      } catch (error) {
        console.error('[WS] Erreur:', error)
      }
    },

    // ========== PERMISSIONS ==========
    peutModifierProjet(projet) {
      return this.utilisateur?.role === 'ADMINISTRATEUR' || this.normalizeId(projet.idCreateur) == this.normalizeId(this.utilisateur?.id)
    },
    peutSupprimerProjet(projet) {
      return this.utilisateur?.role === 'ADMINISTRATEUR' || this.normalizeId(projet.idCreateur) == this.normalizeId(this.utilisateur?.id)
    },
    peutSupprimerTache(tache) {
      const projetId = this.normalizeId(tache.id_projet || tache.projetId)
      const projet = this.mesProjets.find((p) => this.normalizeId(p.id) == projetId)
      return this.utilisateur?.role === 'ADMINISTRATEUR' || this.normalizeId(projet?.idCreateur) == this.normalizeId(this.utilisateur?.id)
    },
    peutModifierTache(tache) {
      const projetId = this.normalizeId(tache.id_projet || tache.projetId)
      const projet = this.mesProjets.find((p) => this.normalizeId(p.id) == projetId)
      const estChef = this.normalizeId(projet?.idCreateur) == this.normalizeId(this.utilisateur?.id)
      const estCreateur = this.normalizeId(tache.createurId || tache.idCreateur) == this.normalizeId(this.utilisateur?.id)
      return estChef || estCreateur
    },
    peutSupprimerCommentaire() {
      return this.utilisateur?.role === 'ADMINISTRATEUR' || this.utilisateur?.role === 'CHEF_PROJET'
    },

    // ========== GESTION PROJETS ==========
    creerProjet() {
      if (!this.abonnementActif) {
        alert(this.$t('abonnement.requis'))
        return
      }
      this.ouvrirModalProjet()
    },
    ouvrirModalProjet() {
      this.showCreateProject = true
    },
    async sauvegarderNouveauProjet() {
      if (!this.projetForm.titre.trim()) return
      try {
        await projectAPI.create({
          titre: this.projetForm.titre,
          description: this.projetForm.description,
          publique: this.projetForm.publique,
        })
        await this.chargerProjets()
        await this.chargerTaches()
        this.showCreateProject = false
        this.projetForm = { titre: '', description: '', publique: false }
        alert(this.$t('projets.projetCree'))
      } catch (e) {
        console.error('[Projet] Erreur création:', e)
        alert(this.$t('erreurs.creationProjet'))
      }
    },
    consulterProjet(p) {
      this.$router.push(`/projet/${this.normalizeId(p.id)}`)
    },
    modifierProjet(p) {
      this.projetEnModification = p
      this.projetModifForm = {
        titre: p.titre || '',
        description: p.description || '',
        statut: p.statut || 'ACTIF',
        publique: p.publique || false
      }
      this.modalModificationProjet = true
    },
    fermerModalModificationProjet() {
      this.modalModificationProjet = false
      this.projetEnModification = null
      this.projetModifForm = { titre: '', description: '', statut: 'ACTIF', publique: false }
    },
    async sauvegarderModificationProjet() {
      if (!this.projetModifForm.titre.trim()) {
        alert(this.$t('projets.titreObligatoire'))
        return
      }
      try {
        const projetMaj = {
          ...this.projetEnModification,
          titre: this.projetModifForm.titre,
          description: this.projetModifForm.description,
          statut: this.projetModifForm.statut,
          publique: this.projetModifForm.publique
        }
        await projectAPI.update(this.projetEnModification.id, projetMaj)

        // Mettre à jour localement
        this.projetEnModification.titre = this.projetModifForm.titre
        this.projetEnModification.description = this.projetModifForm.description
        this.projetEnModification.statut = this.projetModifForm.statut
        this.projetEnModification.publique = this.projetModifForm.publique

        this.$forceUpdate()
        alert(this.$t('projets.projetModifie'))
        this.fermerModalModificationProjet()
      } catch (e) {
        console.error('[Projet] Erreur modification:', e)
        if (e.response?.status === 403) alert(this.$t('erreurs.pasAutoriseModifier'))
        else alert(this.$t('erreurs.modificationProjet'))
      }
    },
    async toggleVisibilite(projet) {
      const nouveauStatut = !projet.publique
      const message = nouveauStatut ? this.$t('projets.confirmerRendrePublic') : this.$t('projets.confirmerRendrePrive')
      if (!confirm(message)) return
      try {
        await projectAPI.update(projet.id, { ...projet, publique: nouveauStatut })
        projet.publique = nouveauStatut
        this.$forceUpdate()
        alert(this.$t('projets.visibiliteModifiee'))
      } catch (e) {
        console.error('[Projet] Erreur changement visibilité:', e)
        alert(this.$t('erreurs.modificationProjet'))
      }
    },
    async supprimerProjet(id) {
      if (!confirm(this.$t('projets.confirmerSuppression'))) return
      try {
        await projectAPI.delete(id)
        this.mesProjets = this.mesProjets.filter((p) => this.normalizeId(p.id) != this.normalizeId(id))
        await this.chargerTaches()
        alert(this.$t('projets.projetSupprime'))
      } catch (e) {
        console.error('[Projet] Erreur suppression:', e)
        if (e.response?.status === 403) alert(this.$t('erreurs.pasAutoriseSupprimer'))
        else if (e.response?.status === 404) {
          alert(this.$t('erreurs.projetIntrouvable'))
          this.mesProjets = this.mesProjets.filter((p) => this.normalizeId(p.id) != this.normalizeId(id))
        } else alert(this.$t('erreurs.suppressionProjet'))
      }
    },
    getProjetCouleur(projet) {
      const index = this.mesProjets.indexOf(projet)
      return this.projetCouleurs[index % this.projetCouleurs.length]
    },

    // ========== GESTION TÂCHES ==========
    changerProjetKanban() {
      this.$nextTick(() => { this.$forceUpdate() })
    },
    ouvrirModalCreationTache() {
      this.nouvelleTache = {
        projetId: this.projetKanbanSelectionne?.id || null,
        titre: '',
        description: '',
        priorite: 'NORMALE',
        dateEcheance: null
      }
      this.modalCreationTache = true
    },
    async creerNouvelleTache() {
      if (!this.nouvelleTache.titre || !this.nouvelleTache.projetId) return
      try {
        await taskAPI.create({
          titre: this.nouvelleTache.titre,
          description: this.nouvelleTache.description,
          idProjet: this.nouvelleTache.projetId,
          priorite: this.nouvelleTache.priorite,
          dateEcheance: this.nouvelleTache.dateEcheance,
          statut: 'BROUILLON'
        })
        await this.chargerTaches()
        this.kanbanRefreshKey++
        this.modalCreationTache = false
        alert(this.$t('taches.tacheCreee'))
      } catch (e) {
        console.error('[Tache] Erreur création:', e)
        alert(this.$t('erreurs.creationTache'))
      }
    },

    // ========== COMMENTAIRES ==========
    async ouvrirModalCommentaires(tache) {
      this.tacheSelectionnee = tache
      this.modalCommentaires = true
      this.nouveauCommentaire = ''
      await this.chargerCommentairesTache(tache.id)
      const tacheId = this.normalizeId(tache.id)
      const topicCommentaires = `/topic/tache/${tacheId}/commentaires`
      if (!this.subscribedTopics.has(topicCommentaires)) {
        WebSocketService.subscribeToTacheCommentaires(tacheId, (nouveauCommentaire) => {
          if (this.tacheSelectionnee && this.normalizeId(this.tacheSelectionnee.id) === tacheId) {
            const existe = this.commentairesTache.some(c => c.id === nouveauCommentaire.id)
            if (!existe) this.commentairesTache.push(nouveauCommentaire)
          }
        })
        WebSocketService.subscribeToTacheCommentairesSuppression(tacheId, (commentaireId) => {
          this.commentairesTache = this.commentairesTache.filter(c => c.id !== commentaireId)
        })
        this.subscribedTopics.add(topicCommentaires)
      }
    },
    fermerModalCommentaires() {
      if (this.tacheSelectionnee) {
        const tacheId = this.normalizeId(this.tacheSelectionnee.id)
        WebSocketService.unsubscribeFromTacheCommentaires(tacheId)
        const topicCommentaires = `/topic/tache/${tacheId}/commentaires`
        this.subscribedTopics.delete(topicCommentaires)
      }
      this.modalCommentaires = false
      this.tacheSelectionnee = null
      this.commentairesTache = []
      this.nouveauCommentaire = ''
    },
    async chargerCommentairesTache(tacheId) {
      this.chargementCommentaires = true
      try {
        const response = await commentaireAPI.getByTache(tacheId)
        this.commentairesTache = response.data || []
      } catch (e) {
        console.error('[Commentaires] Erreur chargement:', e)
        this.commentairesTache = []
        alert(this.$t('erreurs.chargementCommentaires'))
      } finally {
        this.chargementCommentaires = false
      }
    },
    async ajouterCommentaire() {
      if (!this.nouveauCommentaire.trim() || !this.tacheSelectionnee) return
      this.ajoutCommentaireEnCours = true
      try {
        await commentaireAPI.create({ contenu: this.nouveauCommentaire.trim(), tacheId: this.tacheSelectionnee.id })
        this.nouveauCommentaire = ''
      } catch (e) {
        console.error('[Commentaires] Erreur ajout:', e)
        alert(this.$t('erreurs.ajoutCommentaire'))
      } finally {
        this.ajoutCommentaireEnCours = false
      }
    },
    async supprimerCommentaire(commentaireId) {
      if (!confirm(this.$t('commentaires.confirmerSuppression'))) return
      try {
        await commentaireAPI.delete(commentaireId)
      } catch (e) {
        console.error('[Commentaires] Erreur suppression:', e)
        alert(this.$t('erreurs.suppressionCommentaire'))
      }
    },
    getInitiales(prenom, nom) {
      const p = (prenom || '').charAt(0).toUpperCase()
      const n = (nom || '').charAt(0).toUpperCase()
      return p + n || '?'
    },

    // ========== GESTION MEMBRES ==========
    ouvrirModalAjoutMembreGlobal() {
      if (!this.abonnementActif) { alert(this.$t('abonnement.requis')); return }
      this.projetPreSelectionne = null
      this.projetSelectionne = null
      this.membreSelectionne = null
      this.rechercheUtilisateur = ''
      this.utilisateursRecherche = []
      this.modalAjoutMembre = true
    },
    ouvrirModalAjoutMembre(projet) {
      if (!this.abonnementActif) { alert(this.$t('abonnement.requis')); return }
      this.projetPreSelectionne = projet
      this.projetSelectionne = projet
      this.membreSelectionne = null
      this.rechercheUtilisateur = ''
      this.utilisateursRecherche = []
      this.modalAjoutMembre = true
    },
    fermerModalAjoutMembre() {
      this.modalAjoutMembre = false
      this.projetSelectionne = null
      this.projetPreSelectionne = null
      this.membreSelectionne = null
      this.rechercheUtilisateur = ''
      this.utilisateursRecherche = []
    },
    async rechercherUtilisateurs() {
      if (this.rechercheUtilisateur.length < 2) { this.utilisateursRecherche = []; return }
      try {
        const r = await userAPI.search(this.rechercheUtilisateur)
        this.utilisateursRecherche = Array.isArray(r.data) ? r.data : []
      } catch (e) {
        console.error('[Search] Erreur:', e)
        this.utilisateursRecherche = []
      }
    },
    selectionnerUtilisateur(utilisateur) {
      this.membreSelectionne = utilisateur
    },
    async ajouterMembreConfirme() {
      if (!this.projetSelectionne || !this.membreSelectionne) { alert(this.$t('equipe.selectionnerProjetEtMembre')); return }
      try {
        await projectAPI.addMember(this.projetSelectionne.id, this.membreSelectionne.id)
        await this.chargerMembresProjet(this.projetSelectionne.id)
        await this.chargerTaches()
        this.$forceUpdate()
        alert(this.$t('equipe.membreAjoute'))
        this.fermerModalAjoutMembre()
      } catch (e) {
        console.error('[Membre] Erreur ajout:', e)
        alert(e.response?.data?.message || this.$t('erreurs.ajoutMembre'))
      }
    },
    ouvrirModalRetraitMembre(projet) {
      this.projetPourRetrait = projet
      this.membreARetirer = null
      this.modalRetraitMembre = true
    },
    fermerModalRetraitMembre() {
      this.modalRetraitMembre = false
      this.projetPourRetrait = null
      this.membreARetirer = null
    },
    async confirmerRetraitMembre() {
      if (!this.projetPourRetrait || !this.membreARetirer) return
      if (!confirm(this.$t('equipe.confirmerRetrait'))) return
      try {
        await projectAPI.removeMember(this.projetPourRetrait.id, this.membreARetirer.id)
        await this.chargerMembresProjet(this.projetPourRetrait.id)
        await this.chargerTaches()
        this.$forceUpdate()
        alert(this.$t('equipe.membreRetire'))
        this.fermerModalRetraitMembre()
      } catch (e) {
        console.error('[Membre] Erreur retrait:', e)
        alert(this.$t('erreurs.retraitMembre'))
      }
    },
    ouvrirModalAssignationProjet(projet) {
      const taches = this.getTachesNonAssigneesProjet(projet.id)
      this.projetPourAssignation = projet
      this.tachesDisponibles = taches
      this.tacheAAssigner = null
      this.membreAssigneSelectionne = null
      this.modalAssignationTache = true
    },
    fermerModalAssignation() {
      this.modalAssignationTache = false
      this.tacheAAssigner = null
      this.membreAssigneSelectionne = null
      this.projetPourAssignation = null
      this.tachesDisponibles = []
    },
    selectionnerMembreAssignation(membre) {
      this.membreAssigneSelectionne = membre
    },
    async confirmerAssignation() {
      if (!this.tacheAAssigner || !this.membreAssigneSelectionne) { alert(this.$t('taches.selectionnerMembre')); return }
      try {
        await taskAPI.assignTask(this.tacheAAssigner.id, this.membreAssigneSelectionne.id)
        await this.chargerTaches()
        await this.chargerMembresProjet(this.projetPourAssignation.id)
        this.$forceUpdate()
        alert(this.$t('taches.tacheAssignee'))
        this.fermerModalAssignation()
      } catch (e) {
        console.error('[Assignation] Erreur:', e)
        alert(this.$t('erreurs.assignationTache'))
      }
    },
    redirectToAddMember() {
      this.fermerModalAssignation()
      this.ouvrirModalAjoutMembre(this.projetPourAssignation)
    },

    // ========== CHAT ==========
    async ouvrirChatProjet(projet) {
      if (projet.prive && !projet.estMembre) { this.$router.push('/tableau-bord-chef-projet'); return }
      if (this.projetChatActuel) {
        const oldTopic = `/topic/projet/${this.projetChatActuel.id}`
        WebSocketService.unsubscribe(oldTopic)
        this.subscribedTopics.delete(oldTopic)
      }
      this.projetChatActuel = projet
      await this.chargerMessagesProjet(projet.id)
      const topicProjet = `/topic/projet/${projet.id}`
      if (!this.subscribedTopics.has(topicProjet)) {
        WebSocketService.subscribe(topicProjet, (msg) => {
          this.messagesChat.push(msg)
          if (this.messagesParProjet[projet.id]) this.messagesParProjet[projet.id].push(msg)
        })
        this.subscribedTopics.add(topicProjet)
      }
    },
    async chargerMessagesProjet(projetId) {
      try {
        const r = await messagesAPI.byProjet(projetId)
        this.messagesChat = Array.isArray(r.data) ? r.data : []
        this.messagesParProjet[projetId] = this.messagesChat
      } catch (e) {
        console.error('[Chat] Erreur:', e)
        this.messagesChat = []
      }
    },
    async envoyerMessage() {
      if (!this.nouveauMessage.trim() || !this.projetChatActuel) return
      this.envoyantMessage = true
      try {
        await messagesAPI.send({ projetId: this.projetChatActuel.id, contenu: this.nouveauMessage, type: 'TEXT' })
        this.nouveauMessage = ''
      } catch (e) {
        console.error('[Chat] Erreur envoi:', e)
        alert(this.$t('erreurs.envoyerMessage'))
      } finally {
        this.envoyantMessage = false
      }
    },
    estMonMessage(m) {
      return this.normalizeId(m.utilisateur_id || m.authorId) == this.normalizeId(this.utilisateur?.id)
    },
    getMessageClass(m) {
      return this.estMonMessage(m) ? 'my-message' : 'other-message'
    },

    // ========== FACTURES ==========
    async telechargerFacture(facture) {
      try {
        const raw = (this.$i18n && this.$i18n.locale) || localStorage.getItem('lang') || navigator.language || 'fr'
        const langue = String(raw).toLowerCase().startsWith('fr') ? 'fr' : 'en'
        const response = await factureAPI.telechargerPDF(facture.id, langue)
        const blob = new Blob([response.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = facture.numeroFacture ? `${facture.numeroFacture}.pdf` : `facture-${facture.id}.pdf`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (e) {
        console.error('[Facture] Erreur téléchargement:', e)
        alert(this.$t('erreurs.telechargementFacture'))
      }
    },

    // ========== NOTIFICATIONS ==========
    async marquerNotificationLue(n) {
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        await notificationAPI.markAsRead(n.id, userId)
        n.lu = true
      } catch (e) {
        console.error('[Notif] Erreur marque lue:', e)
      }
    },
    async marquerToutesLues() {
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        await notificationAPI.markAllAsRead(userId)
        this.notifications.forEach((n) => (n.lu = true))
        alert(this.$t('notifications.toutesMarquees'))
      } catch (e) {
        console.error('[Notif] Erreur marque toutes:', e)
      }
    },
    async supprimerNotification(n) {
      if (!confirm(this.$t('notifications.confirmerSuppression'))) return
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        await notificationAPI.delete(n.id, userId)
        this.notifications = this.notifications.filter((x) => x.id !== n.id)
      } catch (e) {
        console.error('[Notif] Erreur suppression:', e)
      }
    },

    // ========== HELPERS ==========
    getMembresProjet(projetId) {
      return this.membresParProjet[projetId] || []
    },
    getTachesProjet(projetId) {
      return this.totalTaches.filter((t) => (t.id_projet || t.projetId) == projetId)
    },
    getTachesNonAssigneesProjet(projetId) {
      const normalizedProjetId = this.normalizeId(projetId)
      return this.totalTaches.filter((t) => {
        const tacheProjetId = this.normalizeId(t.id_projet || t.projetId)
        const estDansProjet = tacheProjetId == normalizedProjetId
        const nonAssignee = t.idAssigne == null && t.assigneId == null && t.id_assigne == null
        return estDansProjet && nonAssignee
      })
    },
    getTachesAssigneesProjet(projetId) {
      const normalizedProjetId = this.normalizeId(projetId)
      return this.totalTaches.filter((t) => {
        const tacheProjetId = this.normalizeId(t.id_projet || t.projetId)
        const estDansProjet = tacheProjetId == normalizedProjetId
        const estAssignee = t.idAssigne != null || t.assigneId != null || t.id_assigne != null
        return estDansProjet && estAssignee
      })
    },
    getProjetNom(projetId) {
      const p = this.mesProjets.find((x) => this.normalizeId(x.id) == this.normalizeId(projetId))
      return p ? this.translateProjectTitle(p.titre) : this.$t('projets.projetInconnu')
    },
    getAssigneNom(userId) {
      if (!userId) return this.$t('commun.inconnu')
      const targetId = this.normalizeId(userId)
      for (const membres of Object.values(this.membresParProjet)) {
        const utilisateur = membres.find((m) => this.normalizeId(m.id) == targetId)
        if (utilisateur) return `${utilisateur.prenom || utilisateur.firstName} ${utilisateur.nom || utilisateur.lastName}`
      }
      return this.$t('commun.inconnu')
    },
    getMessagesNonLusProjet(projetId) {
      const messages = this.messagesParProjet[projetId] || []
      return messages.filter((m) => m.statut !== 'LU').length
    },
    getStatutProjetClass(statut) {
      const classes = { ACTIF: 'bg-success', TERMINE: 'bg-secondary', SUSPENDU: 'bg-warning text-dark', ANNULE: 'bg-danger' }
      return classes[statut] || 'bg-info'
    },
    getNotificationIconClass(type) {
      const classes = { TACHE: 'bg-warning-soft', PROJET: 'bg-primary-soft', EQUIPE: 'bg-success-soft', SYSTEME: 'bg-info-soft' }
      return classes[type] || 'bg-secondary-soft'
    },
    getNotificationIcon(type) {
      const icons = { TACHE: 'fas fa-tasks', PROJET: 'fas fa-project-diagram', EQUIPE: 'fas fa-users', SYSTEME: 'fas fa-cog' }
      return icons[type] || 'fas fa-bell'
    },
    formatDate(date) {
      if (!date) return '—'
      const d = new Date(date)
      return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
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
      return new Date(timestamp).toLocaleTimeString(this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US', { hour: '2-digit', minute: '2-digit' })
    },
    formatPrix(prix) {
      if (!prix && prix !== 0) return '—'
      return new Intl.NumberFormat(this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US', { style: 'currency', currency: 'EUR' }).format(prix)
    },

    dessinerGraphiques() {
      this.$nextTick(() => {
        // Graphique Tâches
        const ctxTaches = document.getElementById('chartTaches')
        if (ctxTaches) {
          if (this.chartTachesInstance) this.chartTachesInstance.destroy()
          this.chartTachesInstance = new Chart(ctxTaches, {
            type: 'doughnut',
            data: {
              labels: [this.$t('taches.statuts.brouillon'), this.$t('taches.statuts.enAttente'), this.$t('taches.statuts.termine'), this.$t('taches.statuts.annule')],
              datasets: [{
                data: [this.statsCalculees.taches.brouillon, this.statsCalculees.taches.enAttente, this.statsCalculees.taches.termine, this.statsCalculees.taches.annule],
                backgroundColor: ['#6c757d', '#ffc107', '#28a745', '#dc3545'],
                borderWidth: 0
              }]
            },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
          })
        }

        // Graphique Projets
        const ctxProjets = document.getElementById('chartProjets')
        if (ctxProjets) {
          if (this.chartProjetsInstance) this.chartProjetsInstance.destroy()
          this.chartProjetsInstance = new Chart(ctxProjets, {
            type: 'doughnut',
            data: {
              labels: [this.$t('statuts.ACTIF'), this.$t('statuts.SUSPENDU'), this.$t('statuts.TERMINE'), this.$t('statuts.ANNULE')],
              datasets: [{
                data: [this.statsCalculees.projets.actif, this.statsCalculees.projets.suspendu, this.statsCalculees.projets.termine, this.statsCalculees.projets.annule],
                backgroundColor: ['#28a745', '#ffc107', '#6c757d', '#dc3545'],
                borderWidth: 0
              }]
            },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
          })
        }
      })
    },
  },
  beforeUnmount() {
    WebSocketService.disconnect()
  },
}
</script>

<style scoped>
/* ========== VARIABLES ========== */
:root {
  --gold-primary: #b88a00;
  --gold-secondary: #d4a500;
}

/* ========== DASHBOARD CONTAINER ========== */
.dashboard-container {
  padding: 16px;
  background: #f4f5f7;
  min-height: 100vh;
}

/* ========== HEADER ORIGINAL (INCHANGÉ) ========== */
.chef-header {
  background: linear-gradient(135deg, #b88a00, #403707);
  border-radius: 12px;
  padding: 20px;
  color: #000;
}
.text-white-75 { color: rgba(0, 0, 0, 0.75); }
.bg-gradient-warning { background: linear-gradient(135deg, #ffd700, #554208); }
.subscription-status { text-align: center; }

/* ========== ALERT COMPACT ========== */
.alert-compact { padding: 10px 16px; }

/* ========== KPI BAR (CONDENSÉ) ========== */
.kpi-bar {
  display: flex;
  gap: 12px;
  background: white;
  padding: 12px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}
.kpi-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
}
.kpi-item:hover { background: #f0f0f0; transform: translateY(-2px); }
.kpi-item.active { border-color: #b88a00; background: #fffbeb; }
.kpi-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
}
.kpi-content { display: flex; flex-direction: column; }
.kpi-value { font-size: 1.5rem; font-weight: 700; color: #1a1a1a; line-height: 1; }
.kpi-label { font-size: 0.8rem; color: #666; margin-top: 2px; }
.kpi-sub { font-size: 0.7rem; font-weight: 500; }

/* Soft backgrounds */
.bg-primary-soft { background: rgba(33, 150, 243, 0.1); }
.bg-warning-soft { background: rgba(255, 152, 0, 0.1); }
.bg-success-soft { background: rgba(76, 175, 80, 0.1); }
.bg-info-soft { background: rgba(0, 188, 212, 0.1); }
.bg-secondary-soft { background: rgba(158, 158, 158, 0.1); }

/* ========== NAVIGATION PILLS ========== */
.nav-pills-custom {
  background: white;
  padding: 8px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  gap: 4px;
}
.nav-pills-custom .nav-link {
  border-radius: 8px;
  padding: 10px 18px;
  color: #666;
  font-weight: 500;
  font-size: 0.9rem;
  transition: all 0.2s;
}
.nav-pills-custom .nav-link:hover { background: #f5f5f5; color: #333; }
.nav-pills-custom .nav-link.active {
  background: linear-gradient(135deg, #b88a00, #d4a500);
  color: #1a1a1a;
  font-weight: 600;
}

/* ========== SECTION HEADER ========== */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.section-title { margin: 0; font-size: 1.1rem; font-weight: 600; color: #1a1a1a; }
.section-subtitle { margin: 4px 0 0; font-size: 0.85rem; color: #666; }

/* ========== PROJECTS GRID (STYLE TRELLO) ========== */
.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}
.project-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  transition: all 0.2s;
  border: 1px solid #e8e8e8;
}
.project-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  transform: translateY(-3px);
}
.project-card-color-bar {
  height: 6px;
  background: var(--project-color, #4CAF50);
}
.project-card-body { padding: 16px; }
.project-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}
.project-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.3;
  flex: 1;
  margin-right: 10px;
}
.project-description {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.4;
  min-height: 40px;
}
.project-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.meta-item {
  font-size: 0.8rem;
  color: #888;
  display: flex;
  align-items: center;
  gap: 5px;
}
.meta-item i { font-size: 0.75rem; }
.project-actions {
  display: flex;
  gap: 6px;
}
.project-actions .btn { padding: 6px 10px; font-size: 0.8rem; }

/* ========== EMPTY STATE ========== */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}
.empty-state i { font-size: 3rem; color: #ccc; margin-bottom: 16px; display: block; }
.empty-state h5 { color: #666; margin-bottom: 8px; }
.empty-state p { color: #999; margin-bottom: 16px; }
.empty-state.small-empty { padding: 40px 20px; }
.empty-state.small-empty i { font-size: 2rem; }

/* ========== KANBAN PROJECT SELECTOR ========== */
.kanban-project-selector { min-width: 300px; }
.kanban-project-selector .form-label { font-size: 0.85rem; color: #666; font-weight: 500; }

/* ========== CHAT CONTAINER ========== */
.chat-container {
  display: flex;
  gap: 16px;
  height: calc(100vh - 340px);
  min-height: 450px;
}
.chat-sidebar {
  width: 280px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.chat-sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
  background: #fafafa;
}
.chat-project-list { flex: 1; overflow-y: auto; }
.chat-project-item {
  width: 100%;
  padding: 14px 16px;
  border: none;
  background: transparent;
  text-align: left;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.2s;
  border-bottom: 1px solid #f5f5f5;
}
.chat-project-item:hover { background: #f9f9f9; }
.chat-project-item.active { background: #fffbeb; border-left: 3px solid #b88a00; }
.chat-project-item .project-name {
  font-size: 0.9rem;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.chat-main {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.chat-main-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
  background: #fafafa;
}
.chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f9f9f9;
}
.messages-list { display: flex; flex-direction: column; gap: 12px; }
.message-bubble {
  max-width: 75%;
  padding: 10px 14px;
  border-radius: 12px;
}
.message-bubble.my-message {
  background: linear-gradient(135deg, #b88a00, #d4a500);
  color: white;
  align-self: flex-end;
  border-bottom-right-radius: 4px;
}
.message-bubble.other-message {
  background: white;
  color: #333;
  align-self: flex-start;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}
.message-author { font-size: 0.75rem; opacity: 0.8; margin-bottom: 4px; }
.message-time { margin-left: 8px; }
.message-content { font-size: 0.9rem; line-height: 1.4; }
.chat-input {
  padding: 16px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
  background: white;
}
.chat-input .form-control { border-radius: 20px; padding: 10px 16px; }
.chat-input .btn { border-radius: 50%; width: 44px; height: 44px; padding: 0; }

/* ========== NOTIFICATIONS LIST ========== */
.notifications-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  overflow: hidden;
}
.notification-item {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #f5f5f5;
  transition: all 0.2s;
}
.notification-item:last-child { border-bottom: none; }
.notification-item:hover { background: #fafafa; }
.notification-item.unread { background: #fffbeb; }
.notification-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
}
.notification-content { flex: 1; }
.notification-title { font-weight: 600; color: #333; font-size: 0.95rem; }
.notification-message { color: #666; font-size: 0.85rem; margin-top: 2px; }
.notification-time { color: #999; font-size: 0.75rem; margin-top: 4px; }
.notification-actions { display: flex; gap: 6px; }

/* ========== SUPPORT PREMIUM BAR ========== */
.support-premium-bar {
  margin-top: 20px;
  padding: 14px 20px;
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

/* ========== TABLE STYLES ========== */
.table th {
  border-top: none;
  font-weight: 600;
  font-size: 0.85rem;
  color: #666;
  padding: 14px 16px;
}
.table td { padding: 14px 16px; vertical-align: middle; }
.table-hover tbody tr:hover { background-color: #fffbeb; }

/* ========== BADGES ========== */
.badge { font-size: 0.75rem; padding: 5px 10px; font-weight: 500; }

/* ========== MODALS ========== */
.modal { backdrop-filter: blur(4px); }
.modal-header.bg-info, .modal-header.bg-danger, .modal-header.bg-primary, .modal-header.bg-success { border-bottom: none; }

/* ========== COMMENTAIRES ========== */
.commentaires-list::-webkit-scrollbar { width: 6px; }
.commentaires-list::-webkit-scrollbar-thumb { background: #ddd; border-radius: 3px; }
.commentaire-item { transition: all 0.2s; }
.commentaire-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.commentaire-contenu { white-space: pre-wrap; word-wrap: break-word; }
.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.85rem;
}

/* ========== RESPONSIVE ========== */
@media (max-width: 992px) {
  .kpi-bar { flex-wrap: wrap; }
  .kpi-item { flex: 1 1 calc(50% - 6px); min-width: 150px; }
  .projects-grid { grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); }
  .chat-container { flex-direction: column; height: auto; }
  .chat-sidebar { width: 100%; max-height: 200px; }
}
@media (max-width: 576px) {
  .kpi-item { flex: 1 1 100%; }
  .section-header { flex-direction: column; gap: 12px; align-items: stretch; }
  .section-header .btn { width: 100%; }
}

/* ========== STATISTIQUES ========== */
.stat-value {
  font-size: 2rem;
  font-weight: 700;
}
.stat-label {
  font-size: 0.85rem;
  color: #666;
}
.stat-box {
  transition: all 0.2s;
}
.stat-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
#chartTaches, #chartProjets {
  max-height: 250px;
}
</style>
