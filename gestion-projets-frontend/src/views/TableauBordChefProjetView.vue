<template>
  <div class="container-fluid py-3">
    <!-- ========== HEADER PREMIUM ========== -->
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
              {{ $t('abonnement.expire') }}
              {{ formatDate(abonnement.date_fin || abonnement.dateFin) }}
            </small>
          </div>
          <button
            class="btn btn-outline-light"
            @click="seDeconnecter"
            :title="$t('nav.deconnexion')"
          >
            <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
          </button>
        </div>
      </div>
    </div>

    <!-- ========== ALERTE ABONNEMENT EXPIRÉ ========== -->
    <div v-if="!chargementGlobal && abonnement && !abonnementActif" class="alert alert-warning">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h6 class="mb-1">
            <i class="fas fa-exclamation-triangle me-2"></i>{{ $t('abonnement.abonnementExpire') }}
          </h6>
          <small>{{ $t('abonnement.renouvelerPourContinuer') }}</small>
        </div>
        <router-link
          to="/abonnement-premium"
          class="btn btn-warning"
          :title="$t('abonnement.renouveler')"
        >
          <i class="fas fa-credit-card me-1"></i>{{ $t('abonnement.renouveler') }}
        </router-link>
      </div>
    </div>

    <!-- ========== LOADING / ERROR ========== -->
    <div
      v-if="erreurBackend"
      class="alert alert-danger d-flex justify-content-between align-items-center"
    >
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button
        class="btn btn-sm btn-outline-danger"
        @click="chargerToutesDonnees"
        :title="$t('commun.actualiser')"
      >
        <i class="fas fa-sync-alt me-1"></i>{{ $t('commun.actualiser') }}
      </button>
    </div>

    <div v-else-if="chargementGlobal" class="text-center py-5">
      <div class="spinner-border text-success"></div>
      <p class="text-muted mt-2">{{ $t('commun.chargement') }}</p>
    </div>

    <!-- ========== CONTENU PRINCIPAL ========== -->
    <div v-else>
      <!-- ========== KPIs CHEF DE PROJET ========== -->
      <div class="row g-3 mb-4">
        <!-- KPI 1: Mes Projets -->
        <div class="col-md-3">
          <div
            class="metric-card card h-100 border-0 shadow-sm cursor-pointer"
            @click="onglet = 'projets'"
            :title="$t('tooltips.kpiProjets')"
          >
            <div class="card-body d-flex align-items-center gap-3">
              <div class="metric-icon bg-primary bg-opacity-10 rounded-circle p-3">
                <i class="fas fa-project-diagram fa-2x text-primary"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ mesProjets.length }}</h3>
                <p class="text-muted mb-1 small">{{ $t('projets.mesProjets') }}</p>
                <small class="text-primary"
                >{{ projetsActifs.length }} {{ $t('commun.actifs') }}</small
                >
              </div>
            </div>
          </div>
        </div>

        <!-- KPI 2: Tâches à Valider -->
        <div class="col-md-3">
          <div
            class="metric-card card h-100 border-0 shadow-sm cursor-pointer"
            @click="onglet = 'taches'"
            :title="$t('tooltips.kpiTaches')"
          >
            <div class="card-body d-flex align-items-center gap-3">
              <div class="metric-icon bg-warning bg-opacity-10 rounded-circle p-3">
                <i class="fas fa-tasks fa-2x text-warning"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ tachesEnAttente.length }}</h3>
                <p class="text-muted mb-1 small">{{ $t('taches.aValider') }}</p>
                <small class="text-warning"
                >{{ totalTaches.length }} {{ $t('commun.total') }}</small
                >
              </div>
            </div>
          </div>
        </div>

        <!-- KPI 3: Collaborateurs -->
        <div class="col-md-3">
          <div
            class="metric-card card h-100 border-0 shadow-sm cursor-pointer"
            @click="onglet = 'equipe'"
            :title="$t('tooltips.kpiEquipe')"
          >
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

        <!-- KPI 4: Notifications -->
        <div class="col-md-3">
          <div
            class="metric-card card h-100 border-0 shadow-sm cursor-pointer"
            @click="onglet = 'notifications'"
            :title="$t('tooltips.kpiNotifications')"
          >
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

      <!-- ========== NAVIGATION ONGLETS ========== -->
      <ul class="nav nav-pills bg-light rounded p-2 mb-4">
        <li class="nav-item">
          <a
            class="nav-link"
            :class="{ active: onglet === 'projets' }"
            @click="onglet = 'projets'"
            href="javascript:void(0)"
          >
            <i class="fas fa-project-diagram me-2"></i>{{ $t('nav.mesProjets') }}
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            :class="{ active: onglet === 'taches' }"
            @click="onglet = 'taches'"
            href="javascript:void(0)"
          >
            <i class="fas fa-tasks me-2"></i>{{ $t('nav.taches') }}
            <span v-if="tachesEnAttente.length > 0" class="badge bg-warning ms-1">
              {{ tachesEnAttente.length }}
            </span>
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            :class="{ active: onglet === 'equipe' }"
            @click="onglet = 'equipe'"
            href="javascript:void(0)"
          >
            <i class="fas fa-users me-2"></i>{{ $t('nav.equipe') }}
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            :class="{ active: onglet === 'chat' }"
            @click="onglet = 'chat'"
            href="javascript:void(0)"
          >
            <i class="fas fa-comments me-2"></i>{{ $t('nav.collaboration') }}
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            :class="{ active: onglet === 'factures' }"
            @click="onglet = 'factures'"
            href="javascript:void(0)"
          >
            <i class="fas fa-file-invoice me-2"></i>{{ $t('nav.factures') }}
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            :class="{ active: onglet === 'notifications' }"
            @click="onglet = 'notifications'"
            href="javascript:void(0)"
          >
            <i class="fas fa-bell me-2"></i>{{ $t('nav.notifications') }}
            <span v-if="notificationsNonLues.length > 0" class="badge bg-danger ms-1">
              {{ notificationsNonLues.length }}
            </span>
          </a>
        </li>
      </ul>

      <!-- ========== F6: GESTION PROJETS ========== -->
      <div v-if="onglet === 'projets'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('projets.gestionProjets') }} ({{ mesProjets.length }})</h5>
              <small class="text-muted">{{ $t('projets.creerGererProjets') }}</small>
            </div>
            <button
              class="btn btn-success"
              @click="creerProjet"
              :disabled="!abonnementActif"
              :title="$t('projets.nouveauProjet')"
            >
              <i class="fas fa-plus me-2"></i>{{ $t('projets.nouveauProjet') }}
            </button>
          </div>

          <div class="card-body">
            <div v-if="chargementProjets" class="text-center py-4">
              <div class="spinner-border text-success"></div>
            </div>

            <div v-else-if="mesProjets.length === 0" class="text-center py-5">
              <i class="fas fa-project-diagram fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">{{ $t('projets.aucunProjet') }}</h5>
              <p class="text-muted">{{ $t('projets.creerPremierProjet') }}</p>
              <button
                class="btn btn-success"
                @click="creerProjet"
                :disabled="!abonnementActif"
                :title="$t('projets.creerProjet')"
              >
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
                    <div class="fw-semibold">{{ translateProjectTitle(p.titre) }}</div>
                    <small class="text-muted">{{
                        formatDate(p.date_creation || p.dateCreation)
                      }}</small>
                  </td>
                  <td>
                    <small class="text-muted">
                      {{ translateProjectDescription(p.description).substring(0, 60) }}...
                    </small>
                  </td>
                  <td>
                    <span class="badge bg-primary">{{ getMembresProjet(p.id).length }}</span>
                  </td>
                  <td>
                    <span class="badge bg-warning">{{ getTachesProjet(p.id).length }}</span>
                  </td>
                  <td>
                      <span class="badge" :class="getStatutProjetClass(p.statut)">
                        {{ translateData('status', p.statut) }}
                      </span>
                  </td>
                  <td>
                    <div class="btn-group">
                      <button
                        class="btn btn-sm btn-outline-primary"
                        @click="consulterProjet(p)"
                        :title="$t('tooltips.consulterProjet')"
                      >
                        <i class="fas fa-eye"></i>
                      </button>
                      <button
                        v-if="peutModifierProjet(p)"
                        class="btn btn-sm btn-outline-success"
                        @click="modifierProjet(p)"
                        :title="$t('tooltips.modifierProjet')"
                      >
                        <i class="fas fa-edit"></i>
                      </button>
                      <button
                        v-if="peutSupprimerProjet(p)"
                        class="btn btn-sm btn-outline-danger"
                        @click="supprimerProjet(p.id)"
                        :title="$t('tooltips.supprimerProjet')"
                      >
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

      <!-- ========== F7: TÂCHES (AMÉLIORÉ + F12 COMMENTAIRES) ========== -->
      <div v-if="onglet === 'taches'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex align-items-center gap-2">
            <h5 class="mb-0">{{ $t('nav.taches') }} ({{ tachesFiltrees.length }})</h5>
            <div class="ms-auto d-flex align-items-center gap-2">
              <select
                class="form-select form-select-sm"
                v-model="filtreProjetTache"
                style="width: 280px"
                :title="$t('tooltips.filtrerParProjet')"
              >
                <option value="">{{ $t('projets.tousLesProjets') }}</option>
                <option v-for="p in mesProjets" :key="p.id" :value="p.id">
                  {{ translateProjectTitle(p.titre) }}
                </option>
              </select>
            </div>
          </div>

          <div class="card-body">
            <div v-if="chargementTaches" class="text-center py-4">
              <div class="spinner-border text-warning"></div>
            </div>

            <div v-else-if="tachesFiltrees.length === 0" class="text-center py-5 text-muted">
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
                  <td>
                      <span
                        :class="getProjetNom(t.projetId || t.id_projet) === $t('projets.projetInconnu') ? 'text-danger' : ''"
                      >
                        {{ getProjetNom(t.projetId || t.id_projet) }}
                      </span>
                  </td>
                  <td>
                      <span v-if="t.nomAssigne || getAssigneNom(t.assigneId || t.id_assigne)">
                        {{ t.nomAssigne || getAssigneNom(t.assigneId || t.id_assigne) }}
                      </span>
                    <span v-else class="text-muted">{{ $t('taches.nonAssignee') }}</span>
                  </td>
                  <td>
                      <span class="badge" :class="getStatutTacheClass(t.statut)">
                        {{ translateData('taskStatus', t.statut) }}
                      </span>
                  </td>
                  <td class="text-end">
                    <div class="btn-group">
                      <button
                        class="btn btn-sm btn-outline-info"
                        @click="ouvrirModalCommentaires(t)"
                        :title="$t('tooltips.voirCommentaires')"
                      >
                        <i class="fas fa-comments"></i>
                      </button>

                      <button
                        v-if="t.statut === 'BROUILLON' && peutModifierTache(t)"
                        class="btn btn-sm btn-outline-warning"
                        @click="modifierTache(t)"
                        :title="$t('tooltips.modifierTache')"
                      >
                        <i class="fas fa-edit"></i>
                      </button>

                      <button
                        v-if="t.statut === 'BROUILLON' && peutSoumettreTache(t)"
                        class="btn btn-sm btn-outline-primary"
                        @click="soumettreTache(t)"
                        :title="$t('tooltips.soumettreTache')"
                      >
                        <i class="fas fa-paper-plane"></i>
                      </button>

                      <button
                        v-if="t.statut === 'EN_ATTENTE_VALIDATION'"
                        class="btn btn-sm btn-outline-success"
                        @click="validerTache(t)"
                        :title="$t('tooltips.validerTache')"
                      >
                        <i class="fas fa-check"></i>
                      </button>
                      <button
                        v-if="t.statut === 'EN_ATTENTE_VALIDATION'"
                        class="btn btn-sm btn-outline-secondary"
                        @click="renvoyerEnBrouillon(t)"
                        :title="$t('tooltips.renvoyerBrouillon')"
                      >
                        <i class="fas fa-undo"></i>
                      </button>
                      <button
                        v-if="t.statut !== 'ANNULE'"
                        class="btn btn-sm btn-outline-warning"
                        @click="annulerTache(t)"
                        :title="$t('tooltips.annulerTache')"
                      >
                        <i class="fas fa-ban"></i>
                      </button>
                      <button
                        v-if="peutSupprimerTache(t)"
                        class="btn btn-sm btn-outline-danger"
                        @click="supprimerTache(t.id)"
                        :title="$t('tooltips.supprimerTache')"
                      >
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
      <!-- Suite de PARTIE 1 -->

      <!-- ========== F8: ÉQUIPE ========== -->
      <div v-if="onglet === 'equipe'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('equipe.collaborateurs') }} ({{ totalMembres }})</h5>
              <small class="text-muted">{{ $t('equipe.gestionMembresEtTaches') }}</small>
            </div>
            <button
              class="btn btn-success"
              @click="ouvrirModalAjoutMembreGlobal"
              :disabled="mesProjets.length === 0 || !abonnementActif"
              :title="$t('tooltips.ajouterMembre')"
            >
              <i class="fas fa-user-plus me-1"></i>{{ $t('equipe.ajouterMembre') }}
            </button>
          </div>

          <div class="card-body">
            <div v-if="mesProjets.length === 0" class="text-center py-5 text-muted">
              <i class="fas fa-users fa-3x mb-3"></i>
              <div>{{ $t('projets.aucunProjet') }}</div>
            </div>

            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('projets.projet') }}</th>
                  <th>{{ $t('equipe.equipe') }}</th>
                  <th>{{ $t('taches.tachesNonAssignees') }}</th>
                  <th class="text-end">{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="p in mesProjets" :key="p.id">
                  <td class="fw-semibold">{{ translateProjectTitle(p.titre) }}</td>
                  <td>
                    <div v-if="getMembresProjet(p.id).length > 0" class="d-flex flex-wrap gap-1">
                        <span
                          v-for="m in getMembresProjet(p.id)"
                          :key="m.id"
                          class="badge bg-light text-dark"
                        >
                          {{ m.prenom || m.firstName }} {{ m.nom || m.lastName }}
                        </span>
                    </div>
                    <span v-else class="text-muted">
                        {{ $t('equipe.aucunMembre') }}
                      </span>
                  </td>
                  <td>
                      <span class="badge bg-warning">
                        {{ getTachesNonAssigneesProjet(p.id).length }}
                      </span>
                  </td>
                  <td class="text-end">
                    <div class="btn-group">
                      <button
                        class="btn btn-sm btn-outline-success"
                        @click="ouvrirModalAjoutMembre(p)"
                        :title="$t('tooltips.ajouterMembreProjet')"
                      >
                        <i class="fas fa-user-plus"></i>
                      </button>
                      <button
                        class="btn btn-sm btn-outline-info"
                        @click="ouvrirModalAssignationProjet(p)"
                        :disabled="getMembresProjet(p.id).length === 0 || getTachesNonAssigneesProjet(p.id).length === 0"
                        :title="
                            getMembresProjet(p.id).length === 0
                              ? $t('tooltips.aucunMembreProjet')
                              : getTachesNonAssigneesProjet(p.id).length === 0
                                ? $t('tooltips.aucuneTacheNonAssignee')
                                : $t('tooltips.assignerTaches')
                          "
                      >
                        <i class="fas fa-user-tag"></i>
                      </button>
                      <button
                        class="btn btn-sm btn-outline-danger"
                        @click="ouvrirModalRetraitMembre(p)"
                        :disabled="getMembresProjet(p.id).length === 0"
                        :title="
                            getMembresProjet(p.id).length === 0
                              ? $t('tooltips.aucunMembreARetirer')
                              : $t('tooltips.retirerMembre')
                          "
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

      <!-- ========== F11: FACTURES ========== -->
      <div v-if="onglet === 'factures'">
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
                  <td>{{ formatPrix(f.montantHT ?? f.montantHt ?? 0) }}</td>
                  <td>
                    21% ({{ formatPrix(f.tva ?? (f.montantHT ?? f.montantHt ?? 0) * 0.21) }})
                  </td>
                  <td class="fw-bold">
                    {{
                      formatPrix(
                        f.montantTtc ??
                        (f.montantHT ?? f.montantHt ?? 0) +
                        (f.tva ?? (f.montantHT ?? f.montantHt ?? 0) * 0.21),
                      )
                    }}
                  </td>
                  <td>
                      <span
                        class="badge rounded-pill"
                        :class="f.statut === 'GENEREE' ? 'bg-success' : 'bg-secondary'"
                      >
                        {{ translateData('invoiceStatus', f.statut) }}
                      </span>
                  </td>
                  <td>
                    <button
                      class="btn btn-sm btn-outline-primary"
                      @click="telechargerFacture(f)"
                      :title="$t('tooltips.telechargerFacture')"
                    >
                      <i class="fas fa-download me-1"></i>{{ $t('factures.telecharger') }}
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- ========== F9: COLLABORATION / CHAT ========== -->
      <div v-if="onglet === 'chat'">
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
                  :class="{ active: projetChatActuel && projetChatActuel.id === p.id }"
                  @click="ouvrirChatProjet(p)"
                  :title="$t('tooltips.ouvrirChat')"
                >
                  <span class="text-truncate">{{ translateProjectTitle(p.titre) }}</span>
                  <span class="badge bg-secondary">{{ getMessagesNonLusProjet(p.id) }}</span>
                </button>
                <div v-if="mesProjets.length === 0" class="text-center text-muted py-4">
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
                  {{
                    projetChatActuel
                      ? translateProjectTitle(projetChatActuel.titre)
                      : $t('nav.collaboration')
                  }}
                </h6>
              </div>

              <div class="card-body d-flex flex-column" style="height: 420px">
                <div class="flex-grow-1 overflow-auto messages-container">
                  <div v-if="!projetChatActuel" class="text-center text-muted py-5">
                    {{ $t('projets.choisirProjet') }}
                  </div>

                  <div v-else>
                    <div v-if="messagesChat.length === 0" class="text-center text-muted py-5">
                      {{ $t('chat.aucunMessage') }}
                    </div>

                    <div
                      v-for="m in messagesChat"
                      :key="m.id"
                      class="p-2 rounded mb-2 chat-bubble"
                      :class="getMessageClass(m)"
                      style="max-width: 80%"
                    >
                      <div class="small opacity-75 mb-1">
                        {{
                          m.auteur?.prenom ||
                          m.auteur?.firstName ||
                          m.utilisateurNom ||
                          $t('commun.inconnu')
                        }}
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
                      :title="$t('tooltips.ecrireMessage')"
                    />
                    <button
                      class="btn btn-success"
                      :disabled="envoyantMessage || !nouveauMessage"
                      @click="envoyerMessage"
                      :title="$t('tooltips.envoyerMessage')"
                    >
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

      <!-- ========== NOTIFICATIONS ========== -->
      <div v-if="onglet === 'notifications'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ $t('nav.notifications') }}</h5>
            <button
              class="btn btn-outline-secondary btn-sm"
              @click="marquerToutesLues"
              :disabled="notificationsNonLues.length === 0"
              :title="$t('tooltips.marquerToutesLues')"
            >
              <i class="fas fa-check-double me-1"></i>{{ $t('notifications.marquerToutesLues') }}
            </button>
          </div>

          <div class="card-body">
            <div v-if="chargementNotifications" class="text-center py-4">
              <div class="spinner-border text-info"></div>
            </div>

            <div v-else-if="notifications.length === 0" class="text-center text-muted py-5">
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
                  <div class="fw-semibold">
                    {{ n.titre || n.title || $t('notifications.notification') }}
                  </div>
                  <div class="small text-muted">{{ n.message || n.contenu }}</div>
                  <small class="text-muted">{{ formatDateRelative(n.date || n.createdAt) }}</small>
                </div>
                <div class="btn-group">
                  <button
                    class="btn btn-sm btn-outline-success"
                    v-if="!n.lu"
                    @click="marquerNotificationLue(n)"
                    :title="$t('tooltips.marquerLue')"
                  >
                    <i class="fas fa-check"></i>
                  </button>
                  <button
                    class="btn btn-sm btn-outline-danger"
                    @click="supprimerNotification(n)"
                    :title="$t('tooltips.supprimerNotification')"
                  >
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== MODALS ========== -->

    <!-- Modal création projet -->
    <div
      v-if="showCreateProject"
      class="modal d-block"
      style="background: rgba(0, 0, 0, 0.5); z-index: 1060"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ $t('projets.nouveauProjet') }}</h5>
            <button
              class="btn-close"
              @click="showCreateProject = false"
              :aria-label="$t('commun.fermer')"
            ></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.nom') }}</label>
              <input
                class="form-control"
                v-model.trim="projetForm.titre"
                maxlength="120"
                :placeholder="$t('projets.nomProjet')"
                required
              />
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.description') }}</label>
              <textarea
                class="form-control"
                v-model.trim="projetForm.description"
                rows="3"
                maxlength="500"
                :placeholder="$t('projets.descriptionProjet')"
              ></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="showCreateProject = false">
              {{ $t('commun.annuler') }}
            </button>
            <button
              class="btn btn-success"
              @click="sauvegarderNouveauProjet"
              :disabled="!projetForm.titre.trim()"
            >
              <i class="fas fa-check me-1"></i>{{ $t('commun.creer') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal ajout membre -->
    <div
      v-if="modalAjoutMembre"
      class="modal d-block"
      style="background: rgba(0, 0, 0, 0.5); z-index: 1060"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="fas fa-user-plus me-2"></i>{{ $t('equipe.ajouterMembre') }}
            </h5>
            <button
              class="btn-close"
              @click="fermerModalAjoutMembre"
              :aria-label="$t('commun.fermer')"
            ></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">{{ $t('projets.selectionnerProjet') }}</label>
              <select
                class="form-select"
                v-model="projetSelectionne"
                :disabled="projetPreSelectionne !== null"
              >
                <option :value="null">{{ $t('projets.choisirProjet') }}</option>
                <option v-for="p in mesProjets" :key="p.id" :value="p">
                  {{ translateProjectTitle(p.titre) }}
                </option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">{{ $t('equipe.rechercherUtilisateur') }}</label>
              <input
                class="form-control"
                v-model="rechercheUtilisateur"
                @input="rechercherUtilisateurs"
                :placeholder="$t('equipe.tapezEmail')"
              />
            </div>
            <div v-if="rechercheUtilisateur.length >= 2" class="mb-3">
              <div v-if="utilisateursRecherche.length > 0" class="list-group">
                <button
                  v-for="u in utilisateursRecherche"
                  :key="u.id"
                  class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                  :class="{ active: membreSelectionne && membreSelectionne.id === u.id }"
                  @click="selectionnerUtilisateur(u)"
                  type="button"
                >
                  <div>
                    <div class="fw-semibold">
                      {{ u.prenom || u.firstName }} {{ u.nom || u.lastName }}
                    </div>
                    <small class="text-muted">{{ u.email }}</small>
                  </div>
                  <i
                    class="fas"
                    :class="
                      membreSelectionne && membreSelectionne.id === u.id
                        ? 'fa-check-circle text-success'
                        : 'fa-circle text-muted'
                    "
                  ></i>
                </button>
              </div>
              <div v-else class="alert alert-info mb-0">
                <i class="fas fa-info-circle me-2"></i>{{ $t('equipe.aucunResultat') }}
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="fermerModalAjoutMembre">
              {{ $t('commun.annuler') }}
            </button>
            <button
              class="btn btn-success"
              @click="ajouterMembreConfirme"
              :disabled="!projetSelectionne || !membreSelectionne"
            >
              <i class="fas fa-user-plus me-1"></i>{{ $t('equipe.ajouter') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Assignation -->
    <div
      v-if="modalAssignationTache"
      class="modal d-block"
      style="background: rgba(0, 0, 0, 0.5); z-index: 1060"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title">
              <i class="fas fa-user-tag me-2"></i>{{ $t('taches.assignerTache') }}
            </h5>
            <button
              class="btn-close btn-close-white"
              @click="fermerModalAssignation"
              :aria-label="$t('commun.fermer')"
            ></button>
          </div>
          <div class="modal-body">
            <div class="alert alert-info d-flex align-items-center mb-3">
              <i class="fas fa-project-diagram fa-2x me-3"></i>
              <div>
                <strong>{{ $t('projets.projet') }}:</strong>
                <div class="fw-bold fs-5">
                  {{
                    projetPourAssignation ? translateProjectTitle(projetPourAssignation.titre) : '—'
                  }}
                </div>
              </div>
            </div>

            <div
              v-if="getMembresProjet(projetPourAssignation?.id).length === 0"
              class="alert alert-warning"
            >
              <div class="d-flex align-items-center mb-2">
                <i class="fas fa-exclamation-triangle fa-2x me-3"></i>
                <div>
                  <strong>{{ $t('taches.aucunMembreProjet') }}</strong>
                  <p class="mb-0 small">{{ $t('taches.ajouterMembreAvantAssignation') }}</p>
                </div>
              </div>
              <button class="btn btn-warning btn-sm mt-2" @click="redirectToAddMember">
                <i class="fas fa-user-plus me-1"></i>{{ $t('equipe.ajouterMembre') }}
              </button>
            </div>

            <div
              v-else-if="getTachesNonAssigneesProjet(projetPourAssignation?.id).length === 0"
              class="alert alert-success"
            >
              <div class="d-flex align-items-center mb-2">
                <i class="fas fa-check-circle fa-2x me-3"></i>
                <div>
                  <strong>✅ {{ $t('taches.toutesLesTachesAssignees') }}</strong>
                  <p class="mb-0 small">{{ $t('taches.toutesAssignees') }}</p>
                </div>
              </div>
              <div class="mt-3">
                <p class="mb-2"><strong>{{ $t('commun.optionsSuivantes') }}:</strong></p>
                <ul class="mb-0">
                  <li>{{ $t('taches.creerNouvelleTache') }}</li>
                  <li>{{ $t('taches.reassignerTacheExistante') }}</li>
                </ul>
              </div>
            </div>

            <div v-else>
              <div class="mb-4">
                <label class="form-label fw-bold d-flex align-items-center">
                  <i class="fas fa-tasks me-2 text-warning"></i>
                  <span>1. {{ $t('taches.selectionnerTache') }}</span>
                </label>
                <select class="form-select form-select-lg" v-model="tacheAAssigner">
                  <option :value="null">-- {{ $t('taches.choisirTache') }} --</option>
                  <option v-for="t in tachesDisponibles" :key="t.id" :value="t">
                    {{ t.titre }}
                  </option>
                </select>
                <small class="text-muted">
                  {{ tachesDisponibles.length }} {{ $t('taches.disponibles') }}
                </small>
              </div>

              <div v-if="tacheAAssigner" class="mb-3">
                <label class="form-label fw-bold d-flex align-items-center">
                  <i class="fas fa-user me-2 text-success"></i>
                  <span>2. {{ $t('taches.selectionnerMembre') }}</span>
                </label>
                <div class="list-group">
                  <button
                    v-for="m in getMembresProjet(projetPourAssignation?.id)"
                    :key="m.id"
                    class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                    :class="{
                      active: membreAssigneSelectionne && membreAssigneSelectionne.id === m.id,
                    }"
                    @click="selectionnerMembreAssignation(m)"
                    type="button"
                  >
                    <div>
                      <div class="fw-semibold">
                        {{ m.prenom || m.firstName }} {{ m.nom || m.lastName }}
                      </div>
                      <small class="text-muted">{{ m.email }}</small>
                    </div>
                    <i
                      class="fas fa-2x"
                      :class="
                        membreAssigneSelectionne && membreAssigneSelectionne.id === m.id
                          ? 'fa-check-circle text-success'
                          : 'fa-circle text-muted'
                      "
                    ></i>
                  </button>
                </div>
              </div>

              <div v-else class="alert alert-secondary">
                <i class="fas fa-arrow-up me-2"></i>{{ $t('taches.choisirTacheAvant') }}
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="fermerModalAssignation">
              <i class="fas fa-times me-1"></i>{{ $t('commun.annuler') }}
            </button>
            <button
              class="btn btn-success btn-lg"
              @click="confirmerAssignation"
              :disabled="!tacheAAssigner || !membreAssigneSelectionne"
            >
              <i class="fas fa-check me-1"></i>{{ $t('taches.assigner') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Retrait Membre -->
    <div
      v-if="modalRetraitMembre"
      class="modal d-block"
      style="background: rgba(0, 0, 0, 0.5); z-index: 1060"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title">
              <i class="fas fa-user-minus me-2"></i>{{ $t('equipe.retirerMembre') }}
            </h5>
            <button
              class="btn-close btn-close-white"
              @click="fermerModalRetraitMembre"
              :aria-label="$t('commun.fermer')"
            ></button>
          </div>
          <div class="modal-body">
            <div class="alert alert-info mb-3">
              <i class="fas fa-project-diagram me-2"></i><strong>{{ $t('projets.projet') }}:</strong>
              {{ projetPourRetrait ? translateProjectTitle(projetPourRetrait.titre) : '—' }}
            </div>
            <div
              v-if="getMembresProjet(projetPourRetrait?.id).length === 0"
              class="alert alert-warning"
            >
              <i class="fas fa-exclamation-triangle me-2"></i>{{ $t('equipe.aucunMembreARetirer') }}
            </div>
            <div v-else class="mb-3">
              <label class="form-label fw-bold">{{ $t('equipe.selectionnerMembre') }}</label>
              <div class="list-group">
                <button
                  v-for="m in getMembresProjet(projetPourRetrait?.id)"
                  :key="m.id"
                  class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                  :class="{
                    'active bg-danger text-white': membreARetirer && membreARetirer.id === m.id,
                  }"
                  @click="membreARetirer = m"
                  type="button"
                >
                  <div>
                    <div class="fw-semibold">
                      {{ m.prenom || m.firstName }} {{ m.nom || m.lastName }}
                    </div>
                    <small
                      :class="
                        membreARetirer && membreARetirer.id === m.id
                          ? 'text-white-50'
                          : 'text-muted'
                      "
                    >{{ m.email }}</small
                    >
                  </div>
                  <i
                    class="fas"
                    :class="
                      membreARetirer && membreARetirer.id === m.id
                        ? 'fa-check-circle'
                        : 'fa-circle text-muted'
                    "
                  ></i>
                </button>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-outline-secondary" @click="fermerModalRetraitMembre">
              {{ $t('commun.annuler') }}
            </button>
            <button
              class="btn btn-danger"
              @click="confirmerRetraitMembre"
              :disabled="!membreARetirer"
            >
              <i class="fas fa-user-minus me-1"></i>{{ $t('equipe.retirer') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal COMMENTAIRES (F12) -->
    <div
      v-if="modalCommentaires"
      class="modal d-block"
      style="background: rgba(0, 0, 0, 0.5); z-index: 1060"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title">
              <i class="fas fa-comments me-2"></i>
              {{ $t('commentaires.commentairesTache') }}
            </h5>
            <button
              class="btn-close btn-close-white"
              @click="fermerModalCommentaires"
              :aria-label="$t('commun.fermer')"
            ></button>
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

              <div
                v-else-if="commentairesTache.length === 0"
                class="text-center text-muted py-5"
              >
                <i class="fas fa-comment-slash fa-3x mb-3"></i>
                <p>{{ $t('commentaires.aucunCommentaire') }}</p>
              </div>

              <div v-else>
                <div
                  v-for="c in commentairesTache"
                  :key="c.id"
                  class="card mb-2 commentaire-item"
                >
                  <div class="card-body p-3">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                      <div class="d-flex align-items-center gap-2">
                        <div class="avatar-circle bg-primary text-white">
                          {{ getInitiales(c.auteurPrenom, c.auteurNom) }}
                        </div>
                        <div>
                          <div class="fw-semibold">
                            {{ c.auteurPrenom }} {{ c.auteurNom }}
                          </div>
                          <small class="text-muted">
                            {{ formatDateRelative(c.date) }}
                          </small>
                        </div>
                      </div>
                      <button
                        v-if="peutSupprimerCommentaire(c)"
                        class="btn btn-sm btn-outline-danger"
                        @click="supprimerCommentaire(c.id)"
                        :title="$t('tooltips.supprimerCommentaire')"
                      >
                        <i class="fas fa-trash"></i>
                      </button>
                    </div>
                    <div class="commentaire-contenu">{{ c.contenu }}</div>
                  </div>
                </div>
              </div>
            </div>

            <div class="border-top pt-3">
              <label class="form-label fw-bold">
                <i class="fas fa-plus-circle me-2"></i>{{ $t('commentaires.ajouterCommentaire') }}
              </label>
              <textarea
                class="form-control mb-2"
                v-model.trim="nouveauCommentaire"
                rows="3"
                :placeholder="$t('commentaires.votreMessage')"
                maxlength="1000"
              ></textarea>
              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">
                  {{ nouveauCommentaire.length }}/1000 {{ $t('commun.caracteres') }}
                </small>
                <button
                  class="btn btn-primary"
                  @click="ajouterCommentaire"
                  :disabled="!nouveauCommentaire.trim() || ajoutCommentaireEnCours"
                >
                  <span v-if="ajoutCommentaireEnCours" class="spinner-border spinner-border-sm me-1"></span>
                  <i v-else class="fas fa-paper-plane me-1"></i>
                  {{ $t('commun.envoyer') }}
                </button>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn btn-secondary" @click="fermerModalCommentaires">
              <i class="fas fa-times me-1"></i>{{ $t('commun.fermer') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useDataTranslation } from '@/composables/useDataTranslation'
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
      projetForm: { titre: '', description: '' },
      subscribedTopics: new Set(),
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
    tachesFiltrees() {
      if (this.filtreProjetTache)
        return this.totalTaches.filter((t) => (t.id_projet || t.projetId) == this.filtreProjetTache)
      return this.totalTaches
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
      if (nv === 'taches' && this.totalTaches.length === 0) this.chargerTaches()
      if (nv === 'chat' && !this.projetChatActuel && this.mesProjets[0])
        this.ouvrirChatProjet(this.mesProjets[0])
    },
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
        let projets = Array.isArray(r.data)
          ? r.data
          : Array.isArray(r.data?.content)
            ? r.data.content
            : []
        projets = projets.filter((p) => this.normalizeId(p.idCreateur) === userId)
        this.mesProjets = projets.map((p) => ({ ...p, id: this.normalizeId(p.id) }))
        await Promise.all(this.mesProjets.map((projet) => this.chargerMembresProjet(projet.id)))
        this.$forceUpdate()
        console.log('📊 Projets chargés:', this.mesProjets.length)
      } catch (e) {
        console.error('[Projets] Erreur:', e)
        this.mesProjets = []
      } finally {
        this.chargementProjets = false
      }
    },

    async chargerMembresProjet(projetId) {
      try {
        const r = await projectAPI.getProjectMembers(projetId)
        const membres = Array.isArray(r.data) ? r.data : []
        this.membresParProjet = { ...this.membresParProjet, [projetId]: membres }
        console.log(`👥 Membres projet ${projetId}:`, membres.length)
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
        let taches = Array.isArray(r.data)
          ? r.data
          : Array.isArray(r.data?.content)
            ? r.data.content
            : []

        const projetsExistantsIds = new Set(
          this.mesProjets.map(p => this.normalizeId(p.id))
        )

        this.totalTaches = taches
          .map((t) => ({
            ...t,
            id: this.normalizeId(t.id),
            projetId: t.projetId
              ? this.normalizeId(t.projetId)
              : t.id_projet
                ? this.normalizeId(t.id_projet)
                : null,
            id_projet: t.id_projet
              ? this.normalizeId(t.id_projet)
              : t.projetId
                ? this.normalizeId(t.projetId)
                : null,
            assigneId: t.assigneId ?? t.idAssigne ?? t.id_assigne ?? null,
            id_assigne: t.id_assigne ?? t.assigneId ?? t.idAssigne ?? null,
            idAssigne: t.idAssigne ?? t.assigneId ?? t.id_assigne ?? null,
          }))
          .filter((t) => {
            const projetId = t.projetId || t.id_projet
            // Filtrer silencieusement les tâches orphelines
            return projetId && projetsExistantsIds.has(this.normalizeId(projetId))
          })

        console.log(' Tâches valides chargées:', this.totalTaches.length)
      } catch (e) {
        console.error('[Taches] Erreur:', e)
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
        else {
          console.error('[Abonnement] Erreur:', e)
          this.abonnement = null
        }
      }
      if (!this.abonnement) this.abonnement = { statut: 'INACTIF', date_fin: null }
    },

    async chargerFactures() {
      this.chargementFactures = true
      try {
        const r = await factureAPI.getMesFactures()
        const arr = Array.isArray(r.data)
          ? r.data
          : Array.isArray(r.data?.content)
            ? r.data.content
            : []
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

    peutModifierProjet(projet) {
      return (
        this.utilisateur?.role === 'ADMINISTRATEUR' ||
        this.normalizeId(projet.idCreateur) == this.normalizeId(this.utilisateur?.id)
      )
    },

    peutSupprimerProjet(projet) {
      return (
        this.utilisateur?.role === 'ADMINISTRATEUR' ||
        this.normalizeId(projet.idCreateur) == this.normalizeId(this.utilisateur?.id)
      )
    },

    peutSupprimerTache(tache) {
      const projetId = this.normalizeId(tache.id_projet || tache.projetId)
      const projet = this.mesProjets.find((p) => this.normalizeId(p.id) == projetId)
      return (
        this.utilisateur?.role === 'ADMINISTRATEUR' ||
        this.normalizeId(projet?.idCreateur) == this.normalizeId(this.utilisateur?.id)
      )
    },

    peutModifierTache(tache) {
      const projetId = this.normalizeId(tache.id_projet || tache.projetId)
      const projet = this.mesProjets.find((p) => this.normalizeId(p.id) == projetId)
      const estChef = this.normalizeId(projet?.idCreateur) == this.normalizeId(this.utilisateur?.id)
      const estCreateur = this.normalizeId(tache.createurId || tache.idCreateur) == this.normalizeId(this.utilisateur?.id)
      return estChef || estCreateur
    },

    peutSoumettreTache(tache) {
      return this.peutModifierTache(tache)
    },

    peutSupprimerCommentaire() {
      return (
        this.utilisateur?.role === 'ADMINISTRATEUR' ||
        this.utilisateur?.role === 'CHEF_PROJET'
      )
    },

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
        })
        await this.chargerProjets()
        await this.chargerTaches()
        this.showCreateProject = false
        this.projetForm = { titre: '', description: '' }
        alert(this.$t('projets.projetCree'))
      } catch (e) {
        console.error('[Projet] Erreur création:', e)
        alert(this.$t('erreurs.creationProjet'))
      }
    },

    consulterProjet(p) {
      this.$router.push(`/projet/${this.normalizeId(p.id)}`)
    },

    async modifierProjet(p) {
      const nouveauTitre = prompt(this.$t('projets.nouveauTitre'), p.titre)
      if (!nouveauTitre || nouveauTitre === p.titre) return
      const nouvelleDesc = prompt(this.$t('projets.nouvelleDescription'), p.description)
      if (nouvelleDesc === null) return
      try {
        await projectAPI.update(p.id, { ...p, titre: nouveauTitre, description: nouvelleDesc })
        p.titre = nouveauTitre
        p.description = nouvelleDesc
        alert(this.$t('projets.projetModifie'))
      } catch (e) {
        console.error('[Projet] Erreur modification:', e)
        if (e.response?.status === 403) alert(this.$t('erreurs.pasAutoriseModifier'))
        else alert(this.$t('erreurs.modificationProjet'))
      }
    },

    async supprimerProjet(id) {
      if (!confirm(this.$t('projets.confirmerSuppression'))) return
      try {
        await projectAPI.delete(id)
        this.mesProjets = this.mesProjets.filter(
          (p) => this.normalizeId(p.id) != this.normalizeId(id),
        )
        await this.chargerTaches()
        alert(this.$t('projets.projetSupprime'))
      } catch (e) {
        console.error('[Projet] Erreur suppression:', e)
        if (e.response?.status === 403) alert(this.$t('erreurs.pasAutoriseSupprimer'))
        else if (e.response?.status === 404) {
          alert(this.$t('erreurs.projetIntrouvable'))
          this.mesProjets = this.mesProjets.filter(
            (p) => this.normalizeId(p.id) != this.normalizeId(id),
          )
        } else alert(this.$t('erreurs.suppressionProjet'))
      }
    },

    async validerTache(tache) {
      if (!confirm(this.$t('taches.confirmerValidation'))) return
      try {
        await taskAPI.updateStatus(tache.id, 'TERMINE')
        tache.statut = 'TERMINE'
        alert(this.$t('taches.validee'))
      } catch (e) {
        console.error('[Tache] Erreur validation:', e)
        alert(this.$t('erreurs.validationTache'))
      }
    },

    async renvoyerEnBrouillon(tache) {
      try {
        await taskAPI.updateStatus(tache.id, 'BROUILLON')
        tache.statut = 'BROUILLON'
        alert(this.$t('taches.renvoyeeBrouillon'))
      } catch (e) {
        console.error('[Tache] Erreur renvoi:', e)
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
        console.error('[Tache] Erreur annulation:', e)
        alert(this.$t('erreurs.annulationTache'))
      }
    },

    async supprimerTache(id) {
      if (!confirm(this.$t('taches.confirmerSuppression'))) return
      try {
        await taskAPI.delete(id)
        this.totalTaches = this.totalTaches.filter(
          (t) => this.normalizeId(t.id) != this.normalizeId(id),
        )
        alert(this.$t('taches.supprimee'))
      } catch (e) {
        console.error('[Tache] Erreur suppression:', e)
        if (e.response?.status === 403) alert(this.$t('erreurs.pasAutoriseSupprimerTache'))
        else alert(this.$t('erreurs.suppressionTache'))
      }
    },

    async modifierTache(tache) {
      const nouveauTitre = prompt(this.$t('taches.nouveauTitre'), tache.titre)
      if (!nouveauTitre || nouveauTitre === tache.titre) return

      const nouvelleDescription = prompt(this.$t('taches.nouvelleDescription'), tache.description || '')
      if (nouvelleDescription === null) return

      try {
        await taskAPI.update(tache.id, {
          ...tache,
          titre: nouveauTitre,
          description: nouvelleDescription
        })
        tache.titre = nouveauTitre
        tache.description = nouvelleDescription
        alert(this.$t('taches.modifiee'))
      } catch (e) {
        console.error('[Tache] Erreur modification:', e)
        alert(this.$t('erreurs.modificationTache'))
      }
    },

    async soumettreTache(tache) {
      if (!confirm(this.$t('taches.confirmerSoumission'))) return
      try {
        await taskAPI.updateStatus(tache.id, 'EN_ATTENTE_VALIDATION')
        tache.statut = 'EN_ATTENTE_VALIDATION'
        alert(this.$t('taches.soumise'))
      } catch (e) {
        console.error('[Tache] Erreur soumission:', e)
        alert(this.$t('erreurs.soumissionTache'))
      }
    },

    async ouvrirModalCommentaires(tache) {
      this.tacheSelectionnee = tache
      this.modalCommentaires = true
      this.nouveauCommentaire = ''
      await this.chargerCommentairesTache(tache.id)
    },

    fermerModalCommentaires() {
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
        console.log(`💬 ${this.commentairesTache.length} commentaires chargés pour tâche ${tacheId}`)
      } catch (e) {
        console.error('[Commentaires] Erreur chargement:', e)
        this.commentairesTache = []
        alert(this.$t('erreurs.chargementCommentaires'))
      } finally {
        this.chargementCommentaires = false
      }
    },

    async ajouterCommentaire() {
      if (!this.nouveauCommentaire.trim()) return
      if (!this.tacheSelectionnee) return

      this.ajoutCommentaireEnCours = true
      try {
        const commentaireDTO = {
          contenu: this.nouveauCommentaire.trim(),
          tacheId: this.tacheSelectionnee.id
        }

        const response = await commentaireAPI.create(commentaireDTO)

        this.commentairesTache.push(response.data)
        this.nouveauCommentaire = ''

        console.log('💬 Commentaire ajouté avec succès')
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
        this.commentairesTache = this.commentairesTache.filter(c => c.id !== commentaireId)
        console.log('💬 Commentaire supprimé')
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

    ouvrirModalAjoutMembreGlobal() {
      if (!this.abonnementActif) {
        alert(this.$t('abonnement.requis'))
        return
      }
      this.projetPreSelectionne = null
      this.projetSelectionne = null
      this.membreSelectionne = null
      this.rechercheUtilisateur = ''
      this.utilisateursRecherche = []
      this.modalAjoutMembre = true
    },

    ouvrirModalAjoutMembre(projet) {
      if (!this.abonnementActif) {
        alert(this.$t('abonnement.requis'))
        return
      }
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
      if (this.rechercheUtilisateur.length < 2) {
        this.utilisateursRecherche = []
        return
      }
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
      if (!this.projetSelectionne || !this.membreSelectionne) {
        alert(this.$t('equipe.selectionnerProjetEtMembre'))
        return
      }
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
      if (!this.tacheAAssigner || !this.membreAssigneSelectionne) {
        alert(this.$t('taches.selectionnerMembre'))
        return
      }
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

    async ouvrirChatProjet(projet) {
      if (projet.prive && !projet.estMembre) {
        this.$router.push('/tableau-bord-chef-projet')
        return
      }
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
        const r = await messagesAPI.send({
          projetId: this.projetChatActuel.id,
          contenu: this.nouveauMessage,
          type: 'TEXT',
        })
        this.messagesChat.push(r.data)
        this.nouveauMessage = ''
      } catch (e) {
        console.error('[Chat] Erreur envoi:', e)
        alert(this.$t('erreurs.envoyerMessage'))
      } finally {
        this.envoyantMessage = false
      }
    },

    estMonMessage(m) {
      return (
        this.normalizeId(m.utilisateur_id || m.authorId) == this.normalizeId(this.utilisateur?.id)
      )
    },

    getMessageClass(m) {
      return this.estMonMessage(m) ? 'bg-primary text-white ms-auto' : 'bg-white border shadow-sm'
    },

    async telechargerFacture(facture) {
      try {
        const raw =
          (this.$i18n && this.$i18n.locale) ||
          localStorage.getItem('lang') ||
          navigator.language ||
          'fr'
        const langue = String(raw).toLowerCase().startsWith('fr') ? 'fr' : 'en'
        const response = await factureAPI.telechargerPDF(facture.id, langue)
        const blob = new Blob([response.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = facture.numeroFacture
          ? `${facture.numeroFacture}.pdf`
          : `facture-${facture.id}.pdf`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (e) {
        console.error('[Facture] Erreur téléchargement:', e)
        alert(this.$t('erreurs.telechargementFacture'))
      }
    },

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

    getMembresProjet(projetId) {
      return this.membresParProjet[projetId] || []
    },

    getTachesProjet(projetId) {
      return this.totalTaches.filter((t) => (t.id_projet || t.projetId) == projetId)
    },

    getTachesNonAssigneesProjet(projetId) {
      return this.totalTaches.filter(
        (t) =>
          (t.projetId === projetId || t.id_projet === projetId) &&
          !(t.idAssigne || t.assigneId || t.id_assigne),
      )
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
        if (utilisateur) {
          return `${utilisateur.prenom || utilisateur.firstName} ${utilisateur.nom || utilisateur.lastName}`
        }
      }
      return this.$t('commun.inconnu')
    },

    getMessagesNonLusProjet(projetId) {
      const messages = this.messagesParProjet[projetId] || []
      return messages.filter((m) => m.statut !== 'LU').length
    },

    getStatutProjetClass(statut) {
      const classes = {
        ACTIF: 'bg-success',
        TERMINE: 'bg-secondary',
        SUSPENDU: 'bg-warning text-dark',
        ANNULE: 'bg-danger',
      }
      return classes[statut] || 'bg-info'
    },

    getStatutTacheClass(statut) {
      const classes = {
        BROUILLON: 'bg-secondary',
        EN_ATTENTE_VALIDATION: 'bg-warning text-dark',
        TERMINE: 'bg-success',
        ANNULE: 'bg-danger',
      }
      return classes[statut] || 'bg-info'
    },

    getNotificationIconClass(type) {
      const classes = {
        TACHE: 'bg-warning',
        PROJET: 'bg-primary',
        EQUIPE: 'bg-success',
        SYSTEME: 'bg-info',
      }
      return classes[type] || 'bg-secondary'
    },

    getNotificationIcon(type) {
      const icons = {
        TACHE: 'fas fa-tasks',
        PROJET: 'fas fa-project-diagram',
        EQUIPE: 'fas fa-users',
        SYSTEME: 'fas fa-cog',
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
      return new Date(timestamp).toLocaleTimeString(
        this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US',
        { hour: '2-digit', minute: '2-digit' },
      )
    },

    formatPrix(prix) {
      if (!prix && prix !== 0) return '—'
      return new Intl.NumberFormat(this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US', {
        style: 'currency',
        currency: 'EUR',
      }).format(prix)
    },
  },
  beforeUnmount() {
    WebSocketService.disconnect()
  },
}
</script>

<style scoped>
/* ========== HEADER ========== */
.chef-header {
  background: linear-gradient(135deg, #b88a00, #403707);
  border-radius: 12px;
  padding: 20px;
  color: #000;
}
.text-white-75 {
  color: rgba(0, 0, 0, 0.75);
}
.bg-gradient-warning {
  background: linear-gradient(135deg, #ffd700, #554208);
}

/* ========== CARDS & METRICS ========== */
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

/* ========== NAVIGATION ========== */
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

/* ========== NOTIFICATIONS ========== */
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
  color: #fff;
}

/* ========== CHAT ========== */
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

/* ========== COMMENTAIRES (F12) ========== */
.commentaires-list {
  scroll-behavior: smooth;
}
.commentaires-list::-webkit-scrollbar {
  width: 6px;
}
.commentaires-list::-webkit-scrollbar-track {
  background: #f1f1f1;
}
.commentaires-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.commentaire-item {
  transition: all 0.2s ease;
}
.commentaire-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.commentaire-contenu {
  white-space: pre-wrap;
  word-wrap: break-word;
}

.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9rem;
}

/* ========== ANIMATIONS ========== */
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
  background-color: rgba(255, 215, 0, 0.08);
}

/* ========== BADGES ========== */
.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}

/* ========== BUTTONS ========== */
.btn-group .btn {
  border-radius: 6px;
  margin: 0 1px;
}

/* ========== MODALS ========== */
.modal {
  backdrop-filter: blur(5px);
}
.modal-header.bg-info,
.modal-header.bg-danger,
.modal-header.bg-primary {
  border-bottom: none;
}

/* ========== MISC ========== */
.subscription-status {
  text-align: center;
}
.list-group-item.active {
  background-color: #198754;
  border-color: #198754;
}
.list-group-item.active.bg-danger {
  background-color: #dc3545;
  border-color: #dc3545;
}
.cursor-pointer {
  cursor: pointer;
}
.modal-lg {
  max-width: 700px;
}
.modal-header.bg-info .btn-close-white,
.modal-header.bg-danger .btn-close-white,
.modal-header.bg-primary .btn-close-white {
  filter: brightness(0) invert(1);
}
.text-danger {
  font-weight: 600;
}
</style>
