<template>
  <div class="container-fluid py-3">
    <!-- Header Premium -->
    <div class="chef-header mb-4">
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
              {{ $t('abonnement.expire') }} {{ formatDate(abonnement.date_fin || abonnement.dateFin) }}
            </small>
          </div>
          <button class="btn btn-outline-light" @click="seDeconnecter">
            <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Alerte abonnement expiré -->
    <div v-if="!chargementGlobal && abonnement && !abonnementActif" class="subscription-status">


    <div class="d-flex justify-content-between align-items-center">
        <div>
          <h6 class="mb-1">
            <i class="fas fa-exclamation-triangle me-2"></i>{{ $t('abonnement.abonnementExpire') }}
          </h6>
          <small>{{ $t('abonnement.renouvelerPourContinuer') }}</small>
        </div>
        <router-link to="/abonnement-premium" class="btn btn-warning">
          <i class="fas fa-credit-card me-1"></i>{{ $t('abonnement.renouveler') }}
        </router-link>
      </div>
    </div>

    <!-- Loading / Error -->
    <div v-if="erreurBackend" class="alert alert-danger d-flex justify-content-between align-items-center">
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">{{ $t('commun.actualiser') }}</button>
    </div>
    <div v-else-if="chargementGlobal" class="text-center py-5">
      <div class="spinner-border text-success"></div>
      <p class="text-muted mt-2">{{ $t('commun.chargement') }}</p>
    </div>

    <!-- Content -->
    <div v-else>
      <!-- KPIs Chef de Projet -->
      <div class="row g-3 mb-4">
        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='projets'">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="metric-icon bg-primary bg-opacity-10 rounded-circle p-3">
                <i class="fas fa-project-diagram fa-2x text-primary"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ mesProjets.length }}</h3>
                <p class="text-muted mb-1 small">{{ $t('projets.mesProjets') }}</p>
                <small class="text-primary">{{ projetsActifs.length }} {{ $t('commun.actifs') }}</small>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='taches'">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="metric-icon bg-warning bg-opacity-10 rounded-circle p-3">
                <i class="fas fa-tasks fa-2x text-warning"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ tachesEnAttente.length }}</h3>
                <p class="text-muted mb-1 small">{{ $t('taches.aValider') }}</p>
                <small class="text-warning">{{ totalTaches.length }} {{ $t('commun.total') }}</small>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='equipe'">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="metric-icon bg-success bg-opacity-10 rounded-circle p-3">
                <i class="fas fa-users fa-2x text-success"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ totalMembres }}</h3>
                <p class="text-muted mb-1 small">{{ $t('equipe.collaborateurs') }}</p>
                <small class="text-success">{{ $t('equipe.tousLesprojets') }}</small>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='notifications'">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="metric-icon bg-info bg-opacity-10 rounded-circle p-3">
                <i class="fas fa-bell fa-2x text-info"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ notificationsNonLues.length }}</h3>
                <p class="text-muted mb-1 small">{{ $t('notifications.nouvelles') }}</p>
                <small class="text-info">{{ notifications.length }} {{ $t('commun.total') }}</small>
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
            <i class="fas fa-tasks me-2"></i>{{ $t('nav.taches') }}
            <span v-if="tachesEnAttente.length > 0" class="badge bg-warning ms-1">{{ tachesEnAttente.length }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='equipe'}" @click="onglet='equipe'">
            <i class="fas fa-users me-2"></i>{{ $t('nav.equipe') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='chat'}" @click="onglet='chat'">
            <i class="fas fa-comments me-2"></i>{{ $t('nav.collaboration') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='factures'}" @click="onglet='factures'">
            <i class="fas fa-file-invoice me-2"></i>{{ $t('nav.factures') }}
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='notifications'}" @click="onglet='notifications'">
            <i class="fas fa-bell me-2"></i>{{ $t('nav.notifications') }}
            <span v-if="notificationsNonLues.length > 0" class="badge bg-danger ms-1">{{ notificationsNonLues.length }}</span>
          </a>
        </li>

      </ul>

      <!-- F6: GESTION PROJETS -->
      <div v-if="onglet==='projets'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('projets.gestionProjets') }} ({{ mesProjets.length }})</h5>
              <small class="text-muted">{{ $t('projets.creerGererProjets') }}</small>
            </div>
            <button class="btn btn-success" @click="creerProjet" :disabled="!abonnementActif">
              <i class="fas fa-plus me-2"></i>{{ $t('projets.nouveauProjet') }}
            </button>
          </div>

          <div class="card-body">
            <div v-if="chargementProjets" class="text-center py-4">
              <div class="spinner-border text-success"></div>
            </div>

            <div v-else-if="mesProjets.length===0" class="text-center py-5">
              <i class="fas fa-project-diagram fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">{{ $t('projets.aucunProjet') }}</h5>
              <p class="text-muted">{{ $t('projets.creerPremierProjet') }}</p>
              <button class="btn btn-success" @click="creerProjet" :disabled="!abonnementActif">
                <i class="fas fa-plus me-2"></i>{{ $t('projets.creerProjet') }}
              </button>
            </div>

            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('projets.nom') }}</th>
                  <th>{{ $t('projets.description') }}</th>
                  <th>{{ $t('projets.membres') }}</th>
                  <th>{{ $t('projets.taches') }}</th>
                  <th>{{ $t('projets.statut') }}</th>
                  <th>{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="p in mesProjets" :key="p.id">
                  <td>
                    <div class="fw-semibold">{{ p.titre }}</div>
                    <small class="text-muted">{{ formatDate(p.date_creation || p.dateCreation) }}</small>
                  </td>
                  <td><small class="text-muted">{{ (p.description || '').substring(0, 60) }}...</small></td>
                  <td><span class="badge bg-primary">{{ getMembresProjet(p.id).length }}</span></td>
                  <td><span class="badge bg-warning">{{ getTachesProjet(p.id).length }}</span></td>
                  <td><span class="badge" :class="getStatutProjetClass(p.statut)">{{ translateData('status', p.statut) }}
</span></td>
                  <td>
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-primary" @click="consulterProjet(p)" :title="$t('commun.consulter')" aria-label="Consulter">
                        <i class="fas fa-eye"></i>
                      </button>
                      <button v-if="peutModifierProjet(p)" class="btn btn-sm btn-outline-success" @click="modifierProjet(p)" :title="$t('commun.modifier')" aria-label="Modifier">
                        <i class="fas fa-edit"></i>
                      </button>
                      <button v-if="peutSupprimerProjet(p)" class="btn btn-sm btn-outline-danger" @click="supprimerProjet(p.id)" :title="$t('commun.supprimer')" aria-label="Supprimer">
                        <i class="fas fa-trash"></i>
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

      <!-- F11: FACTURES -->
      <div v-if="onglet==='factures'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white">
            <h5 class="mb-0">{{ $t('factures.mesFactures') }}</h5>
            <small class="text-muted">{{ $t('factures.historiquePaiements') }}</small>
          </div>

          <div class="card-body">
            <div v-if="chargementFactures" class="text-center py-4">
              <div class="spinner-border text-success"></div>
            </div>

            <div v-else-if="factures.length === 0" class="text-center py-5">
              <i class="fas fa-file-invoice fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">{{ $t('factures.aucuneFacture') }}</h5>
            </div>

            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
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

                  <!--  Montant HT -->
                  <td>{{ formatPrix(f.montantHT ?? f.montantHt ?? 0) }}</td>

                  <!-- TVA : affiche celle du backend, sinon calcule 21 % du HT -->
                  <td>
                    21 % ({{ formatPrix(f.tva ?? ((f.montantHT ?? f.montantHt ?? 0) * 0.21)) }})
                  </td>

                  <!--  Total TTC -->
                  <td class="fw-bold">
                    {{ formatPrix(f.montantTtc ?? ((f.montantHT ?? f.montantHt ?? 0) + (f.tva ?? ((f.montantHT ?? f.montantHt ?? 0) * 0.21)))) }}
                  </td>
                  <td>
                    {{ translateData('invoiceStatus', f.statut) }}
                  </td>

                  <!--  Statut -->
                  <td>
                <span
                  class="badge rounded-pill"
                  :class="f.statut === 'GENEREE' ? 'bg-success' : 'bg-secondary'"
                >
                  {{ f.statut }}
                </span>
                  </td>

                  <!--  Téléchargement -->
                  <td>
                    <button
                      class="btn btn-sm btn-outline-primary"
                      @click="telechargerFacture(f)"
                      :title="$t('factures.telechargerPDF')"
                    >
                      <i class="fas fa-download me-1"></i>
                      {{ $t('factures.telecharger') }}
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- =========================== -->
      <!-- 1) F7: TÂCHES (NOUVEAU)     -->
      <!-- =========================== -->
      <div v-if="onglet==='taches'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex align-items-center gap-2">
            <h5 class="mb-0">{{ $t('nav.taches') }} ({{ tachesFiltrees.length }})</h5>
            <div class="ms-auto d-flex align-items-center gap-2">
              <select class="form-select form-select-sm" v-model="filtreProjetTache" style="width:280px">
                <option value="">{{ $t('projets.tousLesProjets') }}</option>
                <option v-for="p in mesProjets" :key="p.id" :value="p.id">{{ p.titre }}</option>
              </select>
            </div>
          </div>

          <div class="card-body">
            <div v-if="chargementTaches" class="text-center py-4">
              <div class="spinner-border text-warning"></div>
            </div>

            <div v-else-if="tachesFiltrees.length===0" class="text-center py-5 text-muted">
              <i class="fas fa-tasks fa-3x mb-3"></i>
              <div>{{ $t('taches.aucuneTache') }}</div>
            </div>

            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('taches.titre') }}</th>
                  <th>{{ $t('projets.projet') }}</th>
                  <th>{{ $t('taches.assigneA') }}</th>
                  <th>{{ $t('taches.statut') }}</th>
                  <th class="text-end">{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="t in tachesFiltrees" :key="t.id">
                  <td class="fw-semibold">{{ t.titre }}</td>
                  <td>{{ t.nomProjet || getProjetNom(t.id_projet || t.projetId) }}</td>
                  <td>{{ t.nomAssigne || getAssigneNom(t.id_assigne || t.assigneId) }}</td>

                  <td><span class="badge" :class="getStatutTacheClass(t.statut)">{{ translateData('taskStatus', t.statut) }}
</span></td>
                  <td class="text-end">
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-success" @click="validerTache(t)">
                        <i class="fas fa-check"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-secondary" @click="renvoyerEnBrouillon(t)">
                        <i class="fas fa-undo"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-warning" @click="annulerTache(t)">
                        <i class="fas fa-ban"></i>
                      </button>
                      <button v-if="peutSupprimerTache(t)" class="btn btn-sm btn-outline-danger" @click="supprimerTache(t.id)">
                        <i class="fas fa-trash"></i>
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

      <!-- =========================== -->
      <!-- 2) F6: ÉQUIPE (NOUVEAU)     -->
      <!-- =========================== -->
      <div v-if="onglet==='equipe'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('equipe.collaborateurs') }} ({{ totalMembres }})</h5>
              <small class="text-muted">{{ $t('equipe.tousLesprojets') }}</small>
            </div>
            <button class="btn btn-success" @click="ouvrirModalAjouterMembre" :disabled="mesProjets.length===0 || !abonnementActif">
              <i class="fas fa-user-plus me-1"></i>{{ $t('equipe.ajouterMembre') }}
            </button>
          </div>

          <div class="card-body">
            <div v-if="mesProjets.length===0" class="text-center py-5 text-muted">
              <i class="fas fa-users fa-3x mb-3"></i>
              <div>{{ $t('projets.aucunProjet') }}</div>
            </div>

            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('projets.projet') }}</th>
                  <th>{{ $t('equipe.membres') }}</th>
                  <th class="text-end">{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="p in mesProjets" :key="p.id">
                  <td class="fw-semibold">{{ p.titre }}</td>
                  <td>
                      <span v-for="m in getMembresProjet(p.id)" :key="m.id" class="badge bg-light text-dark me-1">
                        {{ (m.prenom||m.firstName) }} {{ (m.nom||m.lastName) }}
                      </span>
                    <span v-if="getMembresProjet(p.id).length===0" class="text-muted">—</span>
                  </td>
                  <td class="text-end">
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-success" @click="ajouterMembreAuProjet(p)">
                        <i class="fas fa-user-plus"></i>
                      </button>
                      <button
                        v-if="getMembresProjet(p.id).length"
                        class="btn btn-sm btn-outline-danger"
                        @click="retirerMembreProjet(p.id, getMembresProjet(p.id)[0].id)"
                      >
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
      </div>

      <!-- ===================================== -->
      <!-- 3) F8: COLLABORATION / CHAT  -->
      <!-- ===================================== -->
      <div v-if="onglet==='chat'">
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
                >
                  <span class="text-truncate">{{ p.titre }}</span>
                  <span class="badge bg-secondary">{{ getMessagesNonLusProjet(p.id) }}</span>
                </button>
                <div v-if="mesProjets.length===0" class="text-center text-muted py-4">—</div>
              </div>
            </div>
          </div>

          <div class="col-md-8">
            <div class="card border-0 shadow-sm h-100">
              <div class="card-header bg-white d-flex align-items-center gap-2">
                <h6 class="mb-0">
                  <i class="fas fa-comments me-2"></i>
                  {{ projetChatActuel ? projetChatActuel.titre : $t('nav.collaboration') }}
                </h6>
              </div>

              <div class="card-body d-flex flex-column" style="height:420px">
                <div class="flex-grow-1 overflow-auto messages-container">
                  <div v-if="!projetChatActuel" class="text-center text-muted py-5">
                    {{ $t('projets.choisirProjet') }}
                  </div>

                  <div v-else>
                    <div v-if="messagesChat.length===0" class="text-center text-muted py-5">—</div>

                    <div
                      v-for="m in messagesChat"
                      :key="m.id"
                      class="p-2 rounded mb-2 chat-bubble"
                      :class="getMessageClass(m)"
                      style="max-width:80%"
                    >
                      <div class="small opacity-75 mb-1">
                        {{ m.auteur?.prenom || m.auteur?.firstName || m.utilisateurNom || '—' }}
                        · {{ formatTime(m.date || m.createdAt) }}
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
                    >
                    <button class="btn btn-success" :disabled="envoyantMessage || !nouveauMessage" @click="envoyerMessage">
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

      <!-- =============================== -->
      <!-- 4) F9: NOTIFICATIONS (NOUVEAU)  -->
      <!-- =============================== -->
      <div v-if="onglet==='notifications'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ $t('nav.notifications') }}</h5>
            <button
              class="btn btn-outline-secondary btn-sm"
              @click="marquerToutesLues"
              :disabled="notificationsNonLues.length===0"
            >
              <i class="fas fa-check-double me-1"></i>{{ $t('notifications.marquerToutesLues') }}
            </button>
          </div>

          <div class="card-body">
            <div v-if="chargementNotifications" class="text-center py-4">
              <div class="spinner-border text-info"></div>
            </div>

            <div v-else-if="notifications.length===0" class="text-center text-muted py-5">
              <i class="fas fa-bell-slash fa-3x mb-3"></i>
              <div>{{ $t('notifications.aucuneNotification') }}</div>
            </div>

            <div v-else class="list-group">
              <div
                v-for="n in notifications"
                :key="n.id"
                class="list-group-item d-flex align-items-start gap-3 notification-item"
              >
                <div class="notification-icon rounded" :class="getNotificationIconClass(n.type)">
                  <i :class="getNotificationIcon(n.type)"></i>
                </div>
                <div class="flex-grow-1">
                  <div class="fw-semibold">{{ n.titre || n.title || '—' }}</div>
                  <div class="small text-muted">{{ n.message || n.contenu }}</div>
                  <small class="text-muted">{{ formatDateRelative(n.date || n.createdAt) }}</small>
                </div>
                <div class="btn-group">
                  <button class="btn btn-sm btn-outline-success" v-if="!n.lu" @click="marquerNotificationLue(n)">
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
      </div>

    </div> <!-- /Content -->

    <!-- Modal création projet -->
    <div v-if="showCreateProject" class="modal d-block" style="background:rgba(0,0,0,.5);z-index:1060">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ $t('projets.nouveauProjet') }}</h5>
            <button class="btn-close" @click="showCreateProject=false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.nom') }}</label>
              <input class="form-control" v-model.trim="projetForm.titre" maxlength="120" required>
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.description') }}</label>
              <textarea class="form-control" v-model.trim="projetForm.description" rows="3" maxlength="500"></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="showCreateProject=false">{{ $t('commun.annuler') }}</button>
            <button class="btn btn-success" @click="sauvegarderNouveauProjet" :disabled="!projetForm.titre.trim()">
              <i class="fas fa-check me-1"></i>{{ $t('commun.creer') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal ajout membre -->
    <div v-if="modalAjoutMembre" class="modal d-block" style="background:rgba(0,0,0,.5);z-index:1060">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ $t('equipe.ajouterMembre') }}</h5>
            <button class="btn-close" @click="fermerModalAjoutMembre"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.selectionnerProjet') }}</label>
              <select class="form-select" v-model="projetSelectionne">
                <option value="">-- {{ $t('projets.choisirProjet') }} --</option>
                <option v-for="p in mesProjets" :key="p.id" :value="p">{{ p.titre }}</option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('equipe.rechercherUtilisateur') }}</label>
              <input class="form-control" v-model="rechercheUtilisateur" @input="rechercherUtilisateurs" :placeholder="$t('equipe.tapezEmail')">
            </div>
            <div v-if="utilisateursRecherche.length > 0" class="list-group">
              <button
                v-for="u in utilisateursRecherche"
                :key="u.id"
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                @click="ajouterUtilisateurAuProjet(u)"
              >
                <div>
                  <div class="fw-semibold">{{ u.prenom || u.firstName }} {{ u.nom || u.lastName }}</div>
                  <small class="text-muted">{{ u.email }}</small>
                </div>
                <i class="fas fa-plus text-success"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { useDataTranslation } from '@/composables/useDataTranslation'
import { useAuthStore } from '@/stores/auth'
import { projectAPI, taskAPI, userAPI, abonnementAPI, factureAPI, messagesAPI, notificationAPI } from '@/services/api'
import WebSocketService from '@/services/websocket.service.js'

export default {
  name: 'TableauBordChefProjet',
  data() {
    return {
      onglet: 'projets',
      chargementGlobal: true,
      chargementProjets: false,
      chargementTaches: false,
      chargementMembres: false,
      chargementFactures: false,
      chargementNotifications: false,
      sauvegardeProfil: false,
      envoyantMessage: false,

      mesProjets: [],
      totalTaches: [],
      abonnement: null,
      factures: [],
      notifications: [],
      messagesChat: [],
      messagesParProjet: {},
      membresParProjet: {},

      utilisateursRecherche: [],
      filtreProjetTache: '',
      projetChatActuel: null,
      nouveauMessage: '',
      modalAjoutMembre: false,
      projetSelectionne: null,
      rechercheUtilisateur: '',
      erreurBackend: null,

      showCreateProject: false,
      projetForm: { titre: '', description: '' },
      subscribedTopics: new Set(),
      //translateStatus: null,
      //translateTaskStatus: null,
    }
  },
  computed: {
    utilisateur() { return useAuthStore().user },
    abonnementActif() {
      if (!this.abonnement) return false
      const maintenant = new Date()
      const dateFin = new Date(this.abonnement.date_fin || this.abonnement.dateFin)
      return (this.abonnement.statut || 'ACTIF') === 'ACTIF' && dateFin > maintenant
    },
    projetsActifs() { return this.mesProjets.filter(p => p.statut !== 'TERMINE' && p.statut !== 'ANNULE') },
    tachesEnAttente() { return this.totalTaches.filter(t => t.statut === 'EN_ATTENTE_VALIDATION') },
    tachesFiltrees() {
      if (this.filtreProjetTache) {
        return this.totalTaches.filter(t => (t.id_projet || t.projetId) == this.filtreProjetTache)
      }
      return this.totalTaches
    },
    totalMembres() {
      const membresUniques = new Set()
      Object.values(this.membresParProjet).forEach(membres => {
        membres.forEach(m => membresUniques.add(this.normalizeId(m.id)))
      })
      return membresUniques.size
    },
    notificationsNonLues() { return this.notifications.filter(n => !n.lu) }
  },
  async mounted() {
    this.verifierEtNettoyerUtilisateur()
    const store = useAuthStore()
    if (!store.user && localStorage.getItem('user')) {
      try {
        const userData = JSON.parse(localStorage.getItem('user'))
        store.setUser(userData)
      } catch (e) {
        console.error('Erreur parsing user:', e)
      }
    }
    // Autoriser MEMBRE et CHEF_PROJET
    if (!store.user || (store.user.role !== 'CHEF_PROJET' && store.user.role !== 'MEMBRE')) {
      this.erreurBackend = this.$t('erreurs.accesReserveChefsProjets')
      this.chargementGlobal = false
      return
    }
    const { translateData } = useDataTranslation()
    this.translateData = translateData

    await this.chargerToutesDonnees()
    this.initWebsocket()

    // (C) Ouvrir le modal si ?newProject=1
    if (this.$route.query.newProject === '1') this.ouvrirModalProjet()
  },
  watch: {
    // Lazy-load doux des listes
    onglet(nv) {
      if (nv === 'notifications' && this.notifications.length === 0) this.chargerNotifications()
      if (nv === 'taches' && this.totalTaches.length === 0) this.chargerTaches()
      if (nv === 'chat' && !this.projetChatActuel && this.mesProjets[0]) this.ouvrirChatProjet(this.mesProjets[0])
    }
  },
  methods: {
    // ---- Helpers ----
    normalizeId(v) { return v == null ? v : String(v).split(':')[0] },

    /* ===== AJOUT : Déconnexion ===== */
    seDeconnecter() {
      try { useAuthStore().logout?.() } catch {}
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
          alert('Session expirée. Veuillez vous reconnecter.')
          this.$router.push('/connexion')
        }
      } catch (e) {
        console.error('Erreur vérification utilisateur:', e)
      }
    },

    async chargerToutesDonnees() {
      this.chargementGlobal = true
      this.erreurBackend = null

      try {
        await Promise.all([
          this.chargerProjets(),
          this.chargerTaches(),
          this.chargerFactures(),
          this.chargerNotifications(),
          this.chargerAbonnement(),
        ])
      } catch (e) {
        console.error(e)
        this.erreurBackend = this.$t('erreurs.chargementDonnees')
      } finally {
        this.chargementGlobal = false
      }


  },

    async chargerProjets() {
      this.chargementProjets = true
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        const r = await projectAPI.byUser(userId)
        let projets = Array.isArray(r.data) ? r.data : (Array.isArray(r.data?.content) ? r.data.content : [])
        this.mesProjets = projets.map(p => ({ ...p, id: this.normalizeId(p.id) }))
        // Charger les membres en parallèle
        await Promise.all(this.mesProjets.map(projet => this.chargerMembresProjet(projet.id)))
      } catch (e) {
        console.error(e)
        this.mesProjets = []
      } finally {
        this.chargementProjets = false
      }
    },

    async chargerMembresProjet(projetId) {
      try {
        const r = await projectAPI.byId(projetId)
        const membres = r?.data?.membres || r?.data?.members || []
        this.membresParProjet = { ...this.membresParProjet, [projetId]: Array.isArray(membres) ? membres : [] }
      } catch (e) {
        console.error('Erreur chargement membres projet:', e)
        this.membresParProjet[projetId] = []
      }
    },

    async chargerTaches() {
      this.chargementTaches = true
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        const r = await taskAPI.byChefProjet(userId)
        let taches = Array.isArray(r.data) ? r.data : (Array.isArray(r.data?.content) ? r.data.content : [])
        this.totalTaches = taches.map(t => ({
          ...t,
          id: this.normalizeId(t.id),
          id_projet: t.id_projet ? this.normalizeId(t.id_projet) : t.id_projet,
          projetId: t.projetId ? this.normalizeId(t.projetId) : t.projetId
        }))
      } catch (e) {
        console.error(e)
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
        if (!token) {
          console.warn('[F10] Aucun token JWT trouvé pour la requête abonnement.')
          return
        }

        const r = await abonnementAPI.getByUserId(userId, token)
        this.abonnement = r.data
        console.log('[F10] Abonnement chargé avec succès :', this.abonnement)
      } catch (e) {
        if (e?.response?.status === 404) {
          console.warn('[F10] Aucun abonnement trouvé pour cet utilisateur.')
          this.abonnement = null
        } else if (e?.response?.status === 401) {
          console.error('[F10] Non autorisé - token manquant ou invalide.')
          this.abonnement = null
        } else {
          console.error('Erreur lors du chargement de l’abonnement :', e)
          this.abonnement = null
        }
      }

      if (!this.abonnement) {
        this.abonnement = { statut: 'INACTIF', date_fin: null }
      }

    },

    // === F11: FACTURES ===
    async chargerFactures() {
      this.chargementFactures = true
      try {
        const r = await factureAPI.getMesFactures()
        const arr = Array.isArray(r.data) ? r.data : (Array.isArray(r.data?.content) ? r.data.content : [])
        this.factures = arr.map(f => {
          const id = this.normalizeId(f.id ?? f.id)
          //   lire la clé backend `montantHT` (camel-case), avec fallback
          const montantHT = Number(f.montantHT ?? f.montantHt ?? f.montant_ht ?? 0)
          // Utiliser la TVA fournie par le backend si présente, sinon calcul (21 % du HT)
          const tva = Number(f.tva ?? (montantHT * 0.21))
          return {
            id,
            numeroFacture: f.numeroFacture ?? f.numero_facture ?? `FAC-${id}`,
            dateEmission: f.dateEmission ?? f.date_emission ?? null,
            montantHT,            // garde la clé conforme backend
            montantHt: montantHT, // compat descendante avec la vue
            tva,
            montantTtc: montantHT + tva,
            statut: f.statut ?? 'GENEREE'
          }
        })
      } catch (e) {
        console.error(e)
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
        console.error(e)
        this.notifications = []
      } finally {
        this.chargementNotifications = false
      }
    },
    initWebsocket() {
      try {
        const token = localStorage.getItem('token')
        if (!token) return

        WebSocketService.connect(token)

        //  F13: Topic personnalisé pour notifications
        const userId = this.normalizeId(this.utilisateur.id)
        const topicUser = `/user/${userId}/topic/notifications`

        if (!this.subscribedTopics.has(topicUser)) {
          WebSocketService.subscribe(topicUser, (msg) => {
            console.log('[F13] Notification WebSocket reçue:', msg)

            if (msg?.type === 'NOTIFICATION') {
              this.notifications.unshift({
                id: msg.id || Date.now(),
                message: msg.message || msg.contenu,
                titre: msg.titre || 'Notification',
                date: msg.createdAt || new Date().toISOString(),
                lu: false,
                type: msg.sousType || msg.type || 'SYSTEME'
              })

              // Notification visuelle browser
              if ('Notification' in window && Notification.permission === 'granted') {
                new Notification(msg.titre || 'Notification', {
                  body: msg.message || msg.contenu,
                  icon: '/favicon.ico'
                })
              }
            }
          })
          this.subscribedTopics.add(topicUser)
          console.log(`[F13]  Chef abonné au topic: ${topicUser}`)
        }
      } catch (error) {
        console.error('[F13] Erreur WebSocket:', error)
      }
    },


    peutModifierProjet(projet) {
      const estAdmin = this.utilisateur?.role === 'ADMINISTRATEUR'
      const estCreateur = this.normalizeId(projet.idCreateur) == this.normalizeId(this.utilisateur?.id)
      return estAdmin || estCreateur
    },
    peutSupprimerProjet(projet) {
      const estAdmin = this.utilisateur?.role === 'ADMINISTRATEUR'
      const estCreateur = this.normalizeId(projet.idCreateur) == this.normalizeId(this.utilisateur?.id)
      return estAdmin || estCreateur
    },
    peutSupprimerTache(tache) {
      const estAdmin = this.utilisateur?.role === 'ADMINISTRATEUR'
      const projetId = this.normalizeId(tache.id_projet || tache.projetId)
      const projet = this.mesProjets.find(p => this.normalizeId(p.id) == projetId)
      const estCreateurProjet = this.normalizeId(projet?.idCreateur) == this.normalizeId(this.utilisateur?.id)
      return estAdmin || estCreateurProjet
    },

    creerProjet() {
      if (!this.abonnementActif) {
        alert(this.$t('abonnement.requis'))
        return
      }
      this.ouvrirModalProjet()
    },

    /* ===== AJOUT ===== */
    ouvrirModalProjet() { this.showCreateProject = true },

    async sauvegarderNouveauProjet() {
      if (!this.projetForm.titre.trim()) return
      try {
        const r = await projectAPI.create({ titre: this.projetForm.titre, description: this.projetForm.description })
        await this.chargerProjets()
        this.showCreateProject = false
        this.projetForm = { titre: '', description: '' }
        const id = this.normalizeId(r.data?.id)
        if (id) this.$router.push(`/projet/${id}`)
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.creationProjet'))
      }
    },

    consulterProjet(p) {
      const id = this.normalizeId(p.id)
      this.$router.push(`/projet/${id}`)
    },

    async modifierProjet(p) {
      const nouveauTitre = prompt(' Nouveau titre du projet:', p.titre)
      if (!nouveauTitre || nouveauTitre === p.titre) return
      const nouvelleDesc = prompt(' Nouvelle description:', p.description)
      if (nouvelleDesc === null) return
      try {
        await projectAPI.update(p.id, { ...p, titre: nouveauTitre, description: nouvelleDesc })
        p.titre = nouveauTitre
        p.description = nouvelleDesc
        alert(' Projet modifié avec succès !')
      } catch (e) {
        console.error('Erreur modification projet:', e.response?.data || e)
        if (e.response?.status === 403) {
          alert(' Vous n\'avez pas l\'autorisation de modifier ce projet')
        } else {
          alert(' Erreur lors de la modification du projet')
        }
      }
    },

    async supprimerProjet(id) {
      if (!confirm(' Êtes-vous sûr de vouloir supprimer ce projet ?\n\nCette action est irréversible.')) {
        return
      }
      try {
        await projectAPI.delete(id)
        this.mesProjets = this.mesProjets.filter(p => this.normalizeId(p.id) != this.normalizeId(id))
        alert(' Projet supprimé avec succès !')
      } catch (e) {
        console.error(' Erreur suppression projet:', e.response?.data || e)
        if (e.response?.status === 403) {
          alert(' Vous n\'avez pas l\'autorisation de supprimer ce projet')
        } else if (e.response?.status === 404) {
          alert(' Projet introuvable')
          this.mesProjets = this.mesProjets.filter(p => this.normalizeId(p.id) != this.normalizeId(id))
        } else {
          alert(' Erreur lors de la suppression du projet')
        }
      }
    },

    async validerTache(tache) {
      if (!confirm(this.$t('taches.confirmerValidation'))) return
      try {
        await taskAPI.updateStatus(tache.id, 'TERMINE')
        tache.statut = 'TERMINE'
        alert(this.$t('taches.validee'))
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.validationTache'))
      }
    },

    async renvoyerEnBrouillon(tache) {
      try {
        await taskAPI.updateStatus(tache.id, 'BROUILLON')
        tache.statut = 'BROUILLON'
        alert(this.$t('taches.renvoyeeBrouillon'))
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.modificationTache'))
      }
    },

    async annulerTache(tache) {
      if (!confirm(this.$t('taches.confirmerAnnulation'))) return
      try {
        await taskAPI.updateStatus(tache.id, 'ANNULE')
        tache.statut = 'ANNULE'
        alert(this.$t('taches.annulee'))
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.annulationTache'))
      }
    },

    async supprimerTache(id) {
      if (!confirm(this.$t('taches.confirmerSuppression'))) return
      try {
        await taskAPI.delete(id)
        this.totalTaches = this.totalTaches.filter(t => this.normalizeId(t.id) != this.normalizeId(id))
        alert(this.$t('taches.supprimee'))
      } catch (e) {
        console.error('Erreur suppression tâche:', e.response?.data || e)
        if (e.response?.status === 403) {
          alert(this.$t('erreurs.pasAutoriseSupprimerTache'))
        } else {
          alert(this.$t('erreurs.suppressionTache'))
        }
      }
    },

    ouvrirModalAjouterMembre() {
      if (!this.abonnementActif) {
        alert(this.$t('abonnement.requis'))
        return
      }
      this.modalAjoutMembre = true
    },

    fermerModalAjoutMembre() {
      this.modalAjoutMembre = false
      this.projetSelectionne = null
      this.rechercheUtilisateur = ''
      this.utilisateursRecherche = []
    },

    async rechercherUtilisateurs() {
      if (this.rechercheUtilisateur.length < 2) {
        this.utilisateursRecherche = []
        return
      }
      try {
        const r = await userAPI.search(this.rechercheUtilisateur)
        this.utilisateursRecherche = Array.isArray(r.data) ? r.data : []
      } catch (e) {
        console.error(e)
        this.utilisateursRecherche = []
      }
    },

    async ajouterUtilisateurAuProjet(utilisateur) {
      if (!this.projetSelectionne) {
        alert(this.$t('projets.selectionnerProjetAvant'))
        return
      }
      try {
        await projectAPI.addMember(this.projetSelectionne.id, utilisateur.id)
        await this.chargerMembresProjet(this.projetSelectionne.id)
        alert(this.$t('equipe.membreAjoute'))
        this.fermerModalAjoutMembre()
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.ajoutMembre'))
      }
    },

    ajouterMembreAuProjet(projet) {
      this.projetSelectionne = projet
      this.ouvrirModalAjouterMembre()
    },

    async retirerMembreProjet(projetId, membreId) {
      if (!confirm(this.$t('equipe.confirmerRetrait'))) return
      try {
        await projectAPI.removeMember(projetId, membreId)
        await this.chargerMembresProjet(projetId)
        alert(this.$t('equipe.membreRetire'))
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.retraitMembre'))
      }
    },

    async ouvrirChatProjet(projet) {
      // Désabonner ancien projet
      if (this.projetChatActuel) {
        const oldTopic = `/topic/projet/${this.projetChatActuel.id}`
        WebSocketService.unsubscribe(oldTopic)
        this.subscribedTopics.delete(oldTopic)
      }

      this.projetChatActuel = projet
      await this.chargerMessagesProjet(projet.id)

      // Souscrire nouveau projet
      const topicProjet = `/topic/projet/${projet.id}`
      if (!this.subscribedTopics.has(topicProjet)) {
        WebSocketService.subscribe(topicProjet, (msg) => {
          console.log('[WS] Message chat reçu:', msg)
          this.messagesChat.push(msg)
          if (this.messagesParProjet[projet.id]) {
            this.messagesParProjet[projet.id].push(msg)
          }
        })
        this.subscribedTopics.add(topicProjet)
        console.log(`[WS] Chef abonné au topic: ${topicProjet}`)
      }
    },

    async chargerMessagesProjet(projetId) {
      try {
        const r = await messagesAPI.byProjet(projetId)
        this.messagesChat = Array.isArray(r.data) ? r.data : []

        this.messagesParProjet[projetId] = this.messagesChat
      } catch (e) {
        console.error(e)
        this.messagesChat = []
      }
    },

    async envoyerMessage() {
      if (!this.nouveauMessage.trim() || !this.projetChatActuel) return
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

    estMonMessage(m) {
      return this.normalizeId(m.utilisateur_id || m.authorId) == this.normalizeId(this.utilisateur?.id)
    },

    getMessageClass(m) {
      return this.estMonMessage(m) ? 'bg-primary text-white ms-auto' : 'bg-white border shadow-sm'
    },

    async telechargerFacture(facture) {
      try {
        // 1) Déterminer la langue courante (FR/EN)
        const raw = (this.$i18n && this.$i18n.locale) || localStorage.getItem('lang') || navigator.language || 'fr'
        const langue = String(raw).toLowerCase().startsWith('fr') ? 'fr' : 'en'
        // 2) Appeler l’API (assure responseType: 'blob' côté service)
        const response = await factureAPI.telechargerPDF(facture.id, langue)
        // 3) Déclencher le téléchargement
        const blob = new Blob([response.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        const fileName = facture.numeroFacture ? `${facture.numeroFacture}.pdf` : `facture-${facture.id}.pdf`
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (e) {
        console.error('Erreur téléchargement facture:', e)
        alert(this.$t('erreurs.telechargementFacture'))
      }
    },

    async marquerNotificationLue(n) {
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        await notificationAPI.markAsRead(n.id, userId)
        n.lu = true
      } catch (e) {
        console.error(e)
      }
    },

    async marquerToutesLues() {
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        await notificationAPI.markAllAsRead(userId)
        this.notifications.forEach(n => n.lu = true)
        alert(this.$t('notifications.toutesMarquees'))
      } catch (e) {
        console.error(e)
      }
    },

    async supprimerNotification(n) {
      if (!confirm(this.$t('notifications.confirmerSuppression'))) return
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        await notificationAPI.delete(n.id, userId)
        this.notifications = this.notifications.filter(x => x.id !== n.id)
      } catch (e) {
        console.error(e)
      }
    },

    async actualiserNotifications() { await this.chargerNotifications() },

    async sauvegarderProfil() {
      this.sauvegardeProfil = true
      try {
        const userId = this.normalizeId(this.utilisateur.id)
        await userAPI.updateProfile(userId, {
          nom: this.utilisateur.nom,
          prenom: this.utilisateur.prenom,
          langue: this.utilisateur.langue
        })
        const store = useAuthStore()
        store.setUser({ ...store.user, ...this.utilisateur })
        localStorage.setItem('user', JSON.stringify(store.user))
        alert(this.$t('profil.misAJour'))
      } catch (e) {
        console.error(e)
        alert(this.$t('erreurs.sauvegardeProfile'))
      } finally {
        this.sauvegardeProfil = false
      }
    },

    getMembresProjet(projetId) { return this.membresParProjet[projetId] || [] },
    getTachesProjet(projetId) { return this.totalTaches.filter(t => (t.id_projet || t.projetId) == projetId) },
    getProjetNom(projetId) {
      const p = this.mesProjets.find(x => this.normalizeId(x.id) == this.normalizeId(projetId))
      return p ? p.titre : this.$t('projets.projetInconnu')
    },
    getAssigneNom(userId) {
      const targetId = this.normalizeId(userId)
      for (const membres of Object.values(this.membresParProjet)) {
        const utilisateur = membres.find(m => this.normalizeId(m.id) == targetId)
        if (utilisateur) {
          return `${utilisateur.prenom || utilisateur.firstName} ${utilisateur.nom || utilisateur.lastName}`
        }
      }
      return this.$t('commun.inconnu')
    },
    getInitiales(utilisateur) {
      const prenom = utilisateur.prenom || utilisateur.firstName || ''
      const nom = utilisateur.nom || utilisateur.lastName || ''
      return (prenom.charAt(0) || '') + (nom.charAt(0) || '')
    },
    getMessagesNonLusProjet(projetId) {
      const messages = this.messagesParProjet[projetId] || []
      return messages.filter(m => m.statut !== 'LU').length
    },

    getStatutProjetClass(statut) {
      const classes = {
        'ACTIF': 'bg-success',
        'TERMINE': 'bg-secondary',
        'SUSPENDU': 'bg-warning text-dark',
        'ANNULE': 'bg-danger'
      }
      return classes[statut] || 'bg-info'
    },
    getStatutTacheClass(statut) {
      const classes = {
        'BROUILLON': 'bg-secondary',
        'EN_ATTENTE_VALIDATION': 'bg-warning text-dark',
        'TERMINE': 'bg-success',
        'ANNULE': 'bg-danger'
      }
      return classes[statut] || 'bg-info'
    },
    getNotificationIconClass(type) {
      const classes = {
        'TACHE': 'bg-warning',
        'PROJET': 'bg-primary',
        'EQUIPE': 'bg-success',
        'SYSTEME': 'bg-info'
      }
      return classes[type] || 'bg-secondary'
    },
    getNotificationIcon(type) {
      const icons = {
        'TACHE': 'fas fa-tasks',
        'PROJET': 'fas fa-project-diagram',
        'EQUIPE': 'fas fa-users',
        'SYSTEME': 'fas fa-cog'
      }
      return icons[type] || 'fas fa-bell'
    },

    formatDate(date) {
      if (!date) return '—'
      const d = new Date(date)
      const jour = String(d.getDate()).padStart(2, '0')
      const mois = String(d.getMonth() + 1).padStart(2, '0')
      const annee = d.getFullYear()
      return `${jour}/${mois}/${annee}`
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
        hour: '2-digit', minute: '2-digit'
      })
    },
    formatPrix(prix) {
      if (!prix && prix !== 0) return '—'
      return new Intl.NumberFormat(this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US', {
        style: 'currency', currency: 'EUR'
      }).format(prix)
    }
  },
  beforeUnmount() {
    WebSocketService.disconnect()
  }
}
</script>

<style scoped>
.chef-header {
  background: linear-gradient(135deg, #b88a00, #403707); /* Dégradé doré premium */
  border-radius: 12px;
  padding: 20px;
  color: #000; /* Texte noir pour contraste sur fond doré */
}

.text-white-75 {
  color: rgba(0, 0, 0, 0.75); /* Texte secondaire assombri */
}

.bg-gradient-warning {
  background: linear-gradient(135deg, #ffd700, #554208); /* Dégradé doré pour les badges et boutons */
}

/* Cartes et effets */
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

/* Navigation (onglets) */
.nav-pills .nav-link {
  border-radius: 8px;
  margin: 0 2px;
  transition: all 0.2s ease;
}
.nav-pills .nav-link.active {
  background: linear-gradient(135deg, #b88a00, #ffd700);
  color: #000;
  font-weight: 600;
  transform: translateY(-1px);
}

/* Notifications */
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

/* Chat */
.chat-bubble {
  animation: slideIn 0.3s ease;
}
.messages-container {
  scroll-behavior: smooth;
}
.messages-container::-webkit-scrollbar {
  width: 6px;
}
.messages-container::-webkit-scrollbar-track {
  background: #f1f1f1;
}
.messages-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

/* Animation */
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

/* Table et badges */
.table th {
  border-top: none;
  font-weight: 600;
  font-size: 0.875rem;
  color: #495057;
}
.table-hover tbody tr:hover {
  background-color: rgba(255, 215, 0, 0.08); /* léger reflet doré au survol */
}
.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}
.btn-group .btn {
  border-radius: 6px;
  margin: 0 1px;
}

/* Autres */
.modal {
  backdrop-filter: blur(5px);
}
.avatar-large {
  font-size: 1.5rem;
  font-weight: 600;
}
.subscription-status {
  text-align: center;
}
</style>
