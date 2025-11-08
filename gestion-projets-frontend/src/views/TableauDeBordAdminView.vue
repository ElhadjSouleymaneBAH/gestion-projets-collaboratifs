<template>
  <div class="container-fluid py-3">
    <!-- En-tête -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h1 class="mb-1 d-flex align-items-center gap-2">
          <i class="fas fa-shield-alt text-danger"></i>
          {{ $t('admin.tableauDeBord') }}
        </h1>
        <p class="text-muted mb-0">
          {{ $t('commun.bienvenue') }}
          {{ utilisateur?.prenom || utilisateur?.firstName }}
          {{ utilisateur?.nom || utilisateur?.lastName }}
        </p>
      </div>
      <div class="d-flex align-items-center gap-2">
        <span class="badge bg-danger fs-6">{{ $t('roles.administrateur') }}</span>
        <button class="btn btn-outline-danger" @click="seDeconnecter">
          <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
        </button>
      </div>
    </div>

    <!-- Loading / Error -->
    <div v-if="chargementGlobal" class="text-center py-5">
      <div class="spinner-border text-danger"></div>
      <p class="text-muted mt-2">{{ $t('commun.chargement') }}</p>
    </div>
    <div v-else-if="erreurBackend" class="alert alert-danger d-flex justify-content-between align-items-center">
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">{{ $t('commun.actualiser') }}</button>
    </div>

    <!-- Contenu -->
    <div v-else>
      <!-- KPIs (cliquables) -->
      <div class="row g-3 mb-4">
        <div class="col-md-3">
          <button class="card h-100 shadow-sm border-0 kpi-card" @click="goTo('utilisateurs')" :aria-label="$t('admin.utilisateurs')">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="rounded-circle p-3 bg-primary bg-opacity-10">
                <i class="fas fa-users fa-2x text-primary"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ stats.totalUtilisateurs }}</h3>
                <p class="text-muted mb-0 small">{{ $t('admin.utilisateurs') }}</p>
                <small class="text-primary">{{ stats.utilisateursActifs }} {{ $t('commun.actifs') }}</small>
              </div>
            </div>
          </button>
        </div>
        <div class="col-md-3">
          <button class="card h-100 shadow-sm border-0 kpi-card" @click="goTo('projets')" :aria-label="$t('nav.projets')">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="rounded-circle p-3 bg-success bg-opacity-10">
                <i class="fas fa-project-diagram fa-2x text-success"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ stats.totalProjets }}</h3>
                <p class="text-muted mb-0 small">{{ $t('nav.projets') }}</p>
                <small class="text-success">{{ stats.projetsActifs }} {{ $t('commun.actifs') }}</small>
              </div>
            </div>
          </button>
        </div>
        <div class="col-md-3">
          <button class="card h-100 shadow-sm border-0 kpi-card" @click="goToFinance('abonnements')" :aria-label="$t('admin.abonnements')">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="rounded-circle p-3 bg-warning bg-opacity-10">
                <i class="fas fa-crown fa-2x text-warning"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ stats.totalAbonnements }}</h3>
                <p class="text-muted mb-0 small">{{ $t('admin.abonnements') }}</p>
                <small class="text-warning">{{ formatPrix(stats.revenueMensuel) }}</small>
              </div>
            </div>
          </button>
        </div>
        <div class="col-md-3">
          <button class="card h-100 shadow-sm border-0 kpi-card" @click="goToFinance('factures')" :aria-label="$t('admin.factures')">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="rounded-circle p-3 bg-info bg-opacity-10">
                <i class="fas fa-file-invoice-dollar fa-2x text-info"></i>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ stats.totalFactures }}</h3>
                <p class="text-muted mb-0 small">{{ $t('admin.factures') }}</p>
                <small class="text-info">{{ formatPrix(stats.chiffreAffaires) }}</small>
              </div>
            </div>
          </button>
        </div>
      </div>

      <!-- Alertes et Activité -->
      <div class="row g-3 mb-4">
        <!-- Alertes importantes -->
        <div class="col-md-6">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-danger bg-opacity-10 d-flex justify-content-between align-items-center">
              <h6 class="mb-0 text-danger">
                <i class="fas fa-exclamation-triangle me-2"></i>{{ $t('admin.alertes') || 'Alertes' }}
              </h6>
              <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">
                <i class="fas fa-sync-alt"></i>
              </button>
            </div>
            <div class="card-body">
              <div v-if="abonnementsExpirantBientot.length === 0 && paiementsEchecs.length === 0" class="text-center py-3 text-muted">
                <i class="fas fa-check-circle me-2"></i>
                {{ $t('admin.aucuneAlerte') || 'Aucune alerte' }}
              </div>
              <div v-else>
                <div class="d-flex justify-content-between align-items-center mb-3 p-2 rounded"
                     :class="abonnementsExpirantBientot.length > 0 ? 'bg-warning bg-opacity-10' : ''">
                  <div>
                    <i class="fas fa-clock text-warning me-2"></i>
                    <span>{{ $t('admin.abonnementsExpirant7j') || 'Abonnements expirant (7j)' }}</span>
                  </div>
                  <span class="badge" :class="abonnementsExpirantBientot.length > 0 ? 'bg-warning' : 'bg-secondary'">
                    {{ abonnementsExpirantBientot.length }}
                  </span>
                </div>
                <div class="d-flex justify-content-between align-items-center p-2 rounded"
                     :class="paiementsEchecs.length > 0 ? 'bg-danger bg-opacity-10' : ''">
                  <div>
                    <i class="fas fa-times-circle text-danger me-2"></i>
                    <span>{{ $t('admin.paiementsEchecs') || 'Paiements échoués' }}</span>
                  </div>
                  <span class="badge" :class="paiementsEchecs.length > 0 ? 'bg-danger' : 'bg-secondary'">
                    {{ paiementsEchecs.length }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Activité récente -->
        <div class="col-md-6">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-primary bg-opacity-10 d-flex justify-content-between align-items-center">
              <h6 class="mb-0 text-primary">
                <i class="fas fa-clock me-2"></i>{{ $t('admin.dernieres24h') || 'Dernières 24h' }}
              </h6>
              <span class="badge bg-light text-dark">
                {{ new Date().toLocaleDateString($i18n.locale === 'fr' ? 'fr-FR' : 'en-US') }}
              </span>
            </div>
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-center mb-3 p-2 rounded bg-success bg-opacity-10">
                <div>
                  <i class="fas fa-user-plus text-success me-2"></i>
                  <span>{{ $t('admin.nouveauxUtilisateurs') || 'Nouveaux utilisateurs' }}</span>
                </div>
                <span class="badge bg-success">{{ nouveauxUtilisateurs24h }}</span>
              </div>
              <div class="d-flex justify-content-between align-items-center p-2 rounded bg-primary bg-opacity-10">
                <div>
                  <i class="fas fa-crown text-primary me-2"></i>
                  <span>{{ $t('admin.nouveauxAbonnements') || 'Nouveaux abonnements' }}</span>
                </div>
                <span class="badge bg-primary">{{ nouveauxAbonnements24h }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Onglets principaux -->
      <ul class="nav nav-pills bg-light rounded p-2 mb-4" ref="tabsTop">
        <li class="nav-item">
          <a class="nav-link" :class="{active:ongletActuel==='utilisateurs'}" @click="changerOnglet('utilisateurs')" href="javascript:void(0)">
            <i class="fas fa-users me-2"></i>{{ $t('admin.utilisateurs') }}
            <span class="badge bg-secondary ms-2">{{ totalUtilisateursApi }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:ongletActuel==='projets'}" @click="changerOnglet('projets')" href="javascript:void(0)">
            <i class="fas fa-project-diagram me-2"></i>{{ $t('nav.projets') }}
            <span class="badge bg-secondary ms-2">{{ stats.totalProjets }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:ongletActuel==='finance'}" @click="changerOnglet('finance')" href="javascript:void(0)">
            <i class="fas fa-euro-sign me-2"></i>{{ $t('admin.finance') || 'Finance' }}
            <span class="badge bg-secondary ms-2">{{ stats.totalAbonnements + stats.totalFactures + transactions.length }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:ongletActuel==='taches'}" @click="changerOnglet('taches')" href="javascript:void(0)">
            <i class="fas fa-tasks me-2"></i>{{ $t('admin.taches') || 'Tâches' }}
            <span class="badge bg-secondary ms-2">{{ taches.length }}</span>
          </a>
        </li>
      </ul>

      <!-- Utilisateurs -->
      <div v-if="ongletActuel==='utilisateurs'" ref="utilisateursSection">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('admin.gestionUtilisateurs') }}</h5>
              <small class="text-muted">{{ $t('admin.gererTousUtilisateurs') }}</small>
            </div>
          </div>
          <div class="card-body">
            <div class="row g-3 mb-3">
              <div class="col-md-4">
                <input class="form-control" v-model="filtreUtilisateur" :placeholder="$t('commun.rechercher') + '...'">
              </div>
              <div class="col-md-3">
                <select class="form-select" v-model="filtreRole">
                  <option value="">{{ $t('admin.tousLesRoles') }}</option>
                  <option v-for="role in rolesDisponibles" :key="role" :value="role">{{ $t('roles.' + role.toLowerCase()) }}</option>
                </select>
              </div>
              <div class="col-md-2 ms-auto">
                <select class="form-select" v-model.number="userPageSize">
                  <option :value="10">10</option>
                  <option :value="20">20</option>
                  <option :value="50">50</option>
                  <option :value="100">100</option>
                </select>
              </div>
            </div>

            <div v-if="chargementUtilisateurs" class="text-center py-4">
              <div class="spinner-border text-danger"></div>
            </div>
            <div v-else-if="utilisateurs.length===0" class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>{{ $t('commun.aucunResultat') }}
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>ID</th>
                  <th>{{ $t('commun.utilisateur') }}</th>
                  <th>{{ $t('inscription.email') }}</th>
                  <th>{{ $t('profil.role') }}</th>
                  <th>{{ $t('inscription.dateInscription') }}</th>
                  <th>{{ $t('projets.statut') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="u in utilisateurs" :key="u.id">
                  <td><span class="badge bg-light text-dark">{{ u.id }}</span></td>
                  <td>
                    <div class="d-flex align-items-center gap-2">
                      <div class="avatar bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style="width:35px;height:35px">
                        {{ getInitiales(u) }}
                      </div>
                      <div>
                        <div class="fw-semibold">{{ u.prenom || u.firstName }} {{ u.nom || u.lastName }}</div>
                        <small class="text-muted">{{ (u.langue || 'fr').toUpperCase() }}</small>
                      </div>
                    </div>
                  </td>
                  <td>{{ u.email }}</td>
                  <td><span class="badge" :class="getRoleBadgeClass(u.role)">{{ $t('roles.' + (u.role || '').toLowerCase()) }}</span></td>
                  <td>{{ formatDate(u.dateInscription || u.date_inscription) }}</td>
                  <td><span class="badge" :class="getStatutUtilisateurClass(u.statut)">{{ getStatutUtilisateurLabel(u.statut) }}</span></td>
                </tr>
                </tbody>
              </table>

              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">
                  {{ $t('commun.affiche') }}
                  {{ usersFromItem }}–{{ usersToItem }}
                  {{ $t('commun.sur') }} {{ totalUtilisateursApi }}
                </small>
                <div class="btn-group">
                  <button class="btn btn-outline-secondary btn-sm" :disabled="isFirstUsersPage" @click="prevUsersPage">
                    ‹ {{ $t('commun.precedent') }}
                  </button>
                  <button class="btn btn-outline-secondary btn-sm" :disabled="isLastUsersPage" @click="nextUsersPage">
                    {{ $t('commun.suivant') }} ›
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Projets -->
      <div v-if="ongletActuel==='projets'" ref="projetsSection">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('admin.supervisionProjets') }}</h5>
              <small class="text-muted">{{ $t('admin.surveillanceGlobale') }}</small>
            </div>
            <div class="d-flex gap-2 align-items-center">
              <input class="form-control" style="max-width:260px" v-model="filtreProjet" :placeholder="$t('commun.rechercher') + '...'">
              <select class="form-select" style="max-width:180px" v-model="filtreProjetStatut">
                <option value="">{{ $t('admin.tousLesStatuts') }}</option>
                <option v-for="statut in statutsProjetsDisponibles" :key="statut" :value="statut">
                  {{ $t('projets.statuts.' + statut.toLowerCase()) }}
                </option>
              </select>
              <select class="form-select" style="max-width:120px" v-model.number="pageSize">
                <option :value="10">10</option>
                <option :value="20">20</option>
                <option :value="50">50</option>
                <option :value="100">100</option>
              </select>
            </div>
          </div>
          <div class="card-body">
            <div v-if="chargementProjets" class="text-center py-4">
              <div class="spinner-border text-success"></div>
            </div>
            <div v-else-if="projetsFiltres.length===0" class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>{{ $t('commun.aucunResultat') }}
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>{{ $t('projets.nom') }}</th>
                  <th>{{ $t('projets.chefProjet') }}</th>
                  <th>{{ $t('projets.statut') }}</th>
                  <th>{{ $t('commun.creeLe') }}</th>
                  <th>{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="p in projetsPagine" :key="p.id">
                  <td>
                    <div>
                      <div class="fw-semibold">{{ translateProjectTitle(p.titre) }}</div>
                      <small class="text-muted">{{ translateProjectDescription(getDescription(p)) }}</small>
                    </div>
                  </td>
                  <td>{{ getProprietaireName(p) }}</td>
                  <td><span class="badge" :class="getStatutProjetClass(p.statut)">{{ p.statut }}</span></td>
                  <td><small class="text-muted">{{ formatDate(getDateCreation(p)) }}</small></td>
                  <td>
                      <button
                          v-if="p.visibilite === 'PUBLIC'"
                          class="btn btn-sm btn-outline-info"
                          @click="navigateToProject(p.id)"
                          :title="$t('commun.consulter')"
                      >
                        <i class="fas fa-eye"></i>
                      </button>
                      <span
                          v-else
                          class="text-muted small"
                          :title="$t('admin.projetPrive')"
                      >
                        <i class="fas fa-lock"></i> Privé
                      </span>

                  </td>
                </tr>
                </tbody>
              </table>

              <div class="d-flex justify-content-between align-items-center">
                <small class="text-muted">
                  {{ $t('commun.affiche') }}
                  {{ fromItem }}–{{ toItem }}
                  {{ $t('commun.sur') }} {{ stats.totalProjets }}
                </small>
                <div class="btn-group">
                  <button class="btn btn-outline-secondary btn-sm" :disabled="pageIndex===0" @click="pageIndex--">
                    ‹ {{ $t('commun.precedent') }}
                  </button>
                  <button class="btn btn-outline-secondary btn-sm" :disabled="toItem>=stats.totalProjets" @click="pageIndex++">
                    {{ $t('commun.suivant') }} ›
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ONGLET FINANCE -->
      <div v-if="ongletActuel==='finance'">
        <!-- Sous-menu Finance -->
        <ul class="nav nav-tabs mb-3">
          <li class="nav-item">
            <a class="nav-link" :class="{active:sousOngletFinance==='abonnements'}" @click="sousOngletFinance='abonnements'" href="javascript:void(0)">
              <i class="fas fa-crown me-1"></i>{{ $t('admin.abonnements') }} <span class="badge bg-secondary ms-1">{{ abonnements.length }}</span>
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{active:sousOngletFinance==='factures'}" @click="sousOngletFinance='factures'" href="javascript:void(0)">
              <i class="fas fa-file-invoice me-1"></i>{{ $t('admin.factures') }} <span class="badge bg-secondary ms-1">{{ factures.length }}</span>
            </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" :class="{active:sousOngletFinance==='transactions'}" @click="sousOngletFinance='transactions'" href="javascript:void(0)">
              <i class="fas fa-exchange-alt me-1"></i>{{ $t('admin.transactions') || 'Transactions' }} <span class="badge bg-secondary ms-1">{{ transactions.length }}</span>
            </a>
          </li>
        </ul>

        <!-- Abonnements -->
        <div v-if="sousOngletFinance==='abonnements'" ref="abonnementsSection">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-white">
              <h5 class="mb-0">{{ $t('admin.supervisionAbonnements') }}</h5>
              <small class="text-muted">{{ $t('admin.supervisionRevenus') }}</small>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-hover align-middle">
                  <thead class="table-light">
                  <tr>
                    <th>{{ $t('commun.utilisateur') }}</th>
                    <th>{{ $t('abonnement.plan') }}</th>
                    <th>{{ $t('facture.prix') || 'Prix' }}</th>
                    <th>{{ $t('abonnement.dateDebut') }}</th>
                    <th>{{ $t('abonnement.dateFin') }}</th>
                    <th>{{ $t('abonnement.statut') }}</th>
                    <th>{{ $t('commun.actions') }}</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="a in abonnements" :key="a.id">
                    <td>
                      <div>{{ getUserName(getUtilisateurId(a)) }}</div>
                      <small class="text-muted">ID: {{ getUtilisateurId(a) }}</small>
                    </td>
                    <td>{{ getPlan(a) }}</td>
                    <td><span class="fw-bold text-success">{{ formatPrix(a.prix) }}</span></td>
                    <td>{{ formatDate(getDateDebut(a)) }}</td>
                    <td>{{ formatDate(getDateFin(a)) }}</td>
                    <td><span class="badge" :class="getStatutAbonnementClass(a.statut)">{{ a.statut || 'ACTIF' }}</span></td>
                    <td>
                      <div class="btn-group">
                        <button class="btn btn-sm btn-outline-info" @click="voirFacturesUtilisateur(getUtilisateurId(a))" :title="$t('admin.voirFactures')">
                          <i class="fas fa-file-invoice"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" @click="annulerAbonnement(a.id)" :title="$t('abonnement.annuler')">
                          <i class="fas fa-times"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                  </tbody>
                </table>
                <div v-if="!abonnements.length" class="alert alert-info">
                  <i class="fas fa-info-circle me-2"></i>{{ $t('admin.aucunAbonnement') }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Factures -->
        <div v-if="sousOngletFinance==='factures'" ref="facturesSection">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-white d-flex justify-content-between align-items-center">
              <div>
                <h5 class="mb-0">{{ $t('admin.gestionFactures') }}</h5>
                <small class="text-muted">{{ $t('admin.toutesLesFactures') }}</small>
              </div>
              <div class="d-flex gap-2">
                <button v-if="filtreFactureUserId" class="btn btn-outline-secondary" @click="filtreFactureUserId = null">
                  <i class="fas fa-times me-2"></i>{{ $t('commun.reinitialiser') || 'Réinitialiser' }}
                </button>
                <button class="btn btn-outline-primary" @click="genererRapportFactures">
                  <i class="fas fa-download me-2"></i>{{ $t('admin.exporterFactures') }}
                </button>
              </div>
            </div>
            <div class="card-body">
              <div v-if="filtreFactureUserId" class="alert alert-info mb-3">
                <i class="fas fa-filter me-2"></i>
                Filtré par utilisateur : <strong>{{ getUserName(filtreFactureUserId) }}</strong>
              </div>

              <div v-if="chargementFactures" class="text-center py-4">
                <div class="spinner-border text-info"></div>
              </div>
              <div v-else-if="facturesFiltrees.length===0" class="alert alert-info">
                <i class="fas fa-info-circle me-2"></i>{{ $t('admin.aucuneFacture') }}
              </div>
              <div v-else class="table-responsive">
                <table class="table table-hover align-middle">
                  <thead class="table-light">
                  <tr>
                    <th>{{ $t('facture.numero') }}</th>
                    <th>{{ $t('commun.utilisateur') }}</th>
                    <th>{{ $t('facture.prix') || 'Prix' }}</th>
                    <th>{{ $t('facture.dateEmission') }}</th>
                    <th>{{ $t('facture.statut') }}</th>
                    <th>{{ $t('commun.actions') }}</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="f in facturesFiltrees" :key="f.id">
                    <td><span class="badge bg-primary">#{{ f.numero || f.numeroFacture || f.id }}</span></td>
                    <td>{{ getUserName(f.utilisateurId || f.userId) }}</td>
                    <td><span class="fw-bold text-success">{{ formatPrix(f.montant || f.montantHT || f.montant_ht) }}</span></td>
                    <td>{{ formatDate(f.dateEmission || f.createdAt || f.date_emission) }}</td>
                    <td><span class="badge bg-success">{{ f.statut || 'GENEREE' }}</span></td>
                    <td>
                      <div class="btn-group">
                        <button v-if="hasDownloadAPI" class="btn btn-sm btn-outline-primary" @click="telechargerFacture(f.id)" :title="$t('facture.telecharger')">
                          <i class="fas fa-download"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-info" @click="voirDetailFacture(f)" :title="$t('commun.consulter')">
                          <i class="fas fa-eye"></i>
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

        <!-- Transactions -->
        <div v-if="sousOngletFinance==='transactions'">
          <div class="card border-0 shadow-sm">
            <div class="card-header bg-white">
              <h5 class="mb-0">{{ $t('admin.gestionTransactions') || 'Gestion des transactions' }}</h5>
              <small class="text-muted">{{ $t('admin.historiquePaiements') || 'Historique complet des paiements' }}</small>
            </div>
            <div class="card-body">
              <div v-if="chargementTransactions" class="text-center py-4">
                <div class="spinner-border text-warning"></div>
              </div>
              <div v-else-if="erreurTransactions" class="alert alert-warning">
                <i class="fas fa-exclamation-triangle me-2"></i>
                {{ $t('erreurs.chargementDonnees') || 'Erreur lors du chargement des transactions.' }}
              </div>
              <div v-else-if="transactions.length===0" class="alert alert-info">
                <i class="fas fa-info-circle me-2"></i>{{ $t('admin.aucuneTransaction') || 'Aucune transaction' }}
              </div>
              <div v-else class="table-responsive">
                <table class="table table-hover align-middle">
                  <thead class="table-light">
                  <tr>
                    <th>ID</th>
                    <th>{{ $t('commun.utilisateur') }}</th>
                    <th>Montant HT</th>
                    <th>TVA</th>
                    <th>Montant TTC</th>
                    <th>{{ $t('projets.statut') }}</th>
                    <th>Date</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="t in transactions" :key="t.id">
                    <td><span class="badge bg-light text-dark">{{ t.id }}</span></td>
                    <td>{{ getUserName(t.id_utilisateur || t.idUtilisateur || t.utilisateurId) }}</td>
                    <td>{{ formatPrix(t.montant_ht || t.montantHT) }}</td>
                    <td>{{ formatPrix(t.tva) }}</td>
                    <td><span class="fw-bold text-success">{{ formatPrix(t.montant_ttc || t.montantTTC) }}</span></td>
                    <td><span class="badge" :class="t.statut === 'COMPLETE' ? 'bg-success' : 'bg-danger'">{{ t.statut }}</span></td>
                    <td>{{ formatDate(t.date_creation || t.dateCreation || t.date) }}</td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ONGLET TÂCHES -->
      <div v-if="ongletActuel==='taches'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white">
            <h5 class="mb-0">{{ $t('admin.gestionTaches') || 'Gestion des tâches' }}</h5>
            <small class="text-muted">{{ $t('admin.toutesLesTaches') || 'Toutes les tâches de la plateforme (F7)' }}</small>
          </div>
          <div class="card-body">
            <div v-if="chargementTaches" class="text-center py-4">
              <div class="spinner-border text-primary"></div>
            </div>
            <div v-else-if="erreurTaches" class="alert alert-warning">
              <i class="fas fa-exclamation-triangle me-2"></i>
              <strong>Erreur de chargement</strong><br>
              Impossible de charger les tâches. Vérifiez que l'endpoint backend est bien implémenté.
            </div>
            <div v-else-if="taches.length===0" class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>{{ $t('admin.aucuneTache') || 'Aucune tâche' }}
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>ID</th>
                  <th>{{ $t('taches.titre') || 'Titre' }}</th>
                  <th>{{ $t('nav.projets') }}</th>
                  <th>{{ $t('taches.assigne') || 'Assigné à' }}</th>
                  <th>{{ $t('projets.statut') }}</th>
                  <th>{{ $t('commun.creeLe') }}</th>
                  <th>{{ $t('commun.actions') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="ta in taches" :key="ta.id">
                  <td><span class="badge bg-light text-dark">{{ ta.id }}</span></td>
                  <td>
                    <div class="fw-semibold">{{ ta.titre }}</div>
                    <small class="text-muted">{{ (ta.description || '').substring(0, 50) }}...</small>
                  </td>
                  <td><small>{{ getProjetName(ta.idProjet || ta.id_projet) }}</small></td>
                  <td>{{ getUserName(ta.idAssigne || ta.id_assigne) }}</td>
                  <td><span class="badge" :class="getStatutTacheClass(ta.statut)">{{ ta.statut }}</span></td>
                  <td>{{ formatDate(ta.dateCreation || ta.date_creation) }}</td>
                  <td>
                    <button
                      v-if="ta.statut !== 'ANNULE'"
                      class="btn btn-sm btn-outline-danger"
                      @click="annulerTache(ta.id)"
                      :title="$t('admin.annulerTache') || 'Annuler cette tâche'">
                      <i class="fas fa-ban me-1"></i>{{ $t('commun.annuler') || 'Annuler' }}
                    </button>
                    <span v-else class="badge bg-secondary">
                      <i class="fas fa-ban me-1"></i>Annulée
                    </span>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- Modal détail facture -->
    <div v-if="factureSelectionnee" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Détail facture {{ factureSelectionnee.numero || factureSelectionnee.numeroFacture }}</h5>
            <button type="button" class="btn-close" @click="factureSelectionnee = null"></button>
          </div>
          <div class="modal-body p-0">
            <Facture :facture="factureSelectionnee" mode="direct" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useDataTranslation } from '@/composables/useDataTranslation'
import {
  userAPI,
  projectAPI,
  abonnementAPI,
  factureAPI,
  transactionAPI,
  taskAPI
} from '@/services/api'
import Facture from '@/components/Facture.vue'

export default {
  name: 'TableauDeBordAdmin',
  components: { Facture },

  setup() {
    const router = useRouter()
    const { translateProjectTitle, translateProjectDescription } = useDataTranslation()
    return { router, translateProjectTitle, translateProjectDescription }
  },

  data() {
    return {
      ongletActuel: 'utilisateurs',
      sousOngletFinance: 'abonnements',
      chargementGlobal: false,
      chargementUtilisateurs: false,
      chargementProjets: false,
      chargementFactures: false,
      chargementTransactions: false,
      chargementTaches: false,
      erreurBackend: '',
      erreurTransactions: false,
      erreurTaches: false,
      utilisateurs: [],
      abonnements: [],
      projets: [],
      factures: [],
      transactions: [],
      taches: [],
      filtreUtilisateur: '',
      filtreRole: '',
      filtreProjet: '',
      filtreProjetStatut: '',
      filtreFactureUserId: null,
      pageIndex: 0,
      pageSize: 20,
      userPageIndex: 0,
      userPageSize: 20,
      totalUtilisateursApi: 0,
      userCache: {},
      factureSelectionnee: null,
      _debouncedLoadUsers: null,
    }
  },

  computed: {
    utilisateur() { return useAuthStore().user || {} },

    stats() {
      return {
        totalUtilisateurs: this.totalUtilisateursApi || this.utilisateurs.length,
        utilisateursActifs: this.utilisateurs.filter(u => u.statut !== 'SUSPENDU').length,
        totalProjets: this.projets.length,
        projetsActifs: this.projets.filter(p => p.statut === 'ACTIF').length,
        totalAbonnements: this.abonnements.length,
        revenueMensuel: this.abonnements.reduce((s, a) => s + (a.prix || 0), 0),
        totalFactures: this.factures.length,
        chiffreAffaires: this.factures.reduce((s, f) => s + (f.montant || f.montantHT || f.montant_ht || 0), 0),
      }
    },

    abonnementsActifs() {
      const list = Array.isArray(this.abonnements) ? this.abonnements : []
      return list.filter(a =>
        (a.statut || 'ACTIF') === 'ACTIF' &&
        new Date(this.getDateFin(a)) > new Date()
      )
    },

    abonnementsExpirantBientot() {
      const maintenant = new Date()
      const dans7jours = new Date()
      dans7jours.setDate(dans7jours.getDate() + 7)
      return this.abonnementsActifs.filter(a => {
        const dateFin = new Date(this.getDateFin(a))
        return dateFin >= maintenant && dateFin <= dans7jours
      })
    },

    paiementsEchecs() { return this.factures.filter(f => f.statut === 'ECHEC') },

    nouveauxUtilisateurs24h() {
      const hier = new Date(); hier.setDate(hier.getDate() - 1)
      return this.utilisateurs.filter(u => {
        const d = new Date(u.dateInscription || u.date_inscription)
        return d >= hier
      }).length
    },

    nouveauxAbonnements24h() {
      const hier = new Date(); hier.setDate(hier.getDate() - 1)
      return this.abonnements.filter(a => {
        const d = new Date(this.getDateDebut(a))
        return d >= hier
      }).length
    },

    usersFromItem() { return this.totalUtilisateursApi === 0 ? 0 : this.userPageIndex * this.userPageSize + 1 },
    usersToItem() { return Math.min((this.userPageIndex + 1) * this.userPageSize, this.totalUtilisateursApi) },
    isFirstUsersPage() { return this.userPageIndex === 0 },
    isLastUsersPage() { return (this.userPageIndex + 1) * this.userPageSize >= this.totalUtilisateursApi },

    rolesDisponibles() { return [...new Set(this.utilisateurs.map(u => u.role).filter(Boolean))] },
    statutsProjetsDisponibles() { return [...new Set(this.projets.map(p => p.statut).filter(Boolean))] },

    projetsFiltres() {
      const out = this.projets.filter(p => {
        const matchTitre = (p.titre || '').toLowerCase().includes(this.filtreProjet.toLowerCase())
        const matchStatut = this.filtreProjetStatut ? p.statut === this.filtreProjetStatut : true
        return matchTitre && matchStatut
      })
      const maxPage = Math.max(0, Math.ceil(out.length / this.pageSize) - 1)
      if (this.pageIndex > maxPage) this.pageIndex = 0
      return out
    },

    projetsPagine() {
      const start = this.pageIndex * this.pageSize
      return this.projetsFiltres.slice(start, start + this.pageSize)
    },

    fromItem() { return this.stats.totalProjets === 0 ? 0 : this.pageIndex * this.pageSize + 1 },
    toItem() { return Math.min(this.pageIndex * this.pageSize + this.pageSize, this.projetsFiltres.length) },

    facturesFiltrees() {
      if (!this.filtreFactureUserId) return this.factures
      return this.factures.filter(f => (f.utilisateurId || f.userId) === this.filtreFactureUserId)
    },

    hasDownloadAPI() { return typeof (factureAPI?.telechargerPDF) === 'function' }
  },

  watch: {
    filtreUtilisateur() {
      this.userPageIndex = 0
      this._debouncedLoadUsers?.()
    },
    filtreRole() {
      this.userPageIndex = 0
      this.chargerUtilisateurs()
    },
    userPageSize() {
      this.userPageIndex = 0
      this.chargerUtilisateurs()
    },
  },

  async mounted() {
    // init debounce (sans dépendance externe)
    this._debouncedLoadUsers = this.debounce(() => this.chargerUtilisateurs(), 300)

    const store = useAuthStore()
    if (!store.user && localStorage.getItem('user')) {
      try { store.setUser(JSON.parse(localStorage.getItem('user'))) } catch {}
    }
    if (!store.user || store.user.role !== 'ADMINISTRATEUR') {
      this.erreurBackend = this.$t('erreurs.accesReserveAdmins')
      return
    }
    await this.chargerToutesDonnees()
  },

  methods: {
    // petit utilitaire debounce
    debounce(fn, delay = 300) {
      let t = null
      return (...args) => {
        clearTimeout(t)
        t = setTimeout(() => fn.apply(this, args), delay)
      }
    },

    goTo(onglet) {
      this.ongletActuel = onglet
      this.$nextTick(() => {
        const map = {
          utilisateurs: this.$refs.utilisateursSection,
          projets: this.$refs.projetsSection,
          abonnements: this.$refs.abonnementsSection,
          factures: this.$refs.facturesSection
        }
        const el = map[onglet]
        if (el?.scrollIntoView) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
      })
    },

    goToFinance(sousOnglet) {
      this.ongletActuel = 'finance'
      this.sousOngletFinance = sousOnglet
    },

    formatDate(date) {
      if (date == null) return '—'
      try {
        const d = (typeof date === 'number')
          ? new Date(date)
          : new Date(String(date))
        return isNaN(d) ? '—' : d.toLocaleDateString(this.$i18n.locale === 'fr' ? 'fr-FR' : 'en-US')
      } catch { return '—' }
    },

    formatPrix(prix) { return (prix || prix === 0) ? `${Number(prix).toFixed(2)} €` : '—' },

    async chargerToutesDonnees() {
      try {
        this.chargementGlobal = true
        this.erreurBackend = ''
        await Promise.all([
          this.chargerUtilisateurs(),
          this.chargerAbonnements(),
          this.chargerProjets(),
          this.chargerFactures(),
          this.chargerTransactions(),
          this.chargerTaches()
        ])
      } catch (e) {
        console.error('Erreur chargement global:', e)
        this.erreurBackend = this.$t('erreurs.chargementDonnees')
      } finally {
        this.chargementGlobal = false
      }
    },

    async chargerUtilisateurs() {
      this.chargementUtilisateurs = true
      try {
        const res = await userAPI.list({
          q: this.filtreUtilisateur,
          role: this.filtreRole,
          statut: '',
          page: this.userPageIndex,
          size: this.userPageSize
        })
        if (res.data?.content) {
          this.utilisateurs = Array.isArray(res.data.content) ? res.data.content : []
          this.totalUtilisateursApi = res.data.totalElements ?? this.utilisateurs.length
        } else {
          this.utilisateurs = Array.isArray(res.data) ? res.data : []
          this.totalUtilisateursApi = this.utilisateurs.length
        }
        // hydrate un cache local pour résoudre les noms sans N+1 appels
        this.utilisateurs.forEach(u => { this.userCache[u.id] = u })
      } catch (e) {
        console.error('Erreur utilisateurs:', e)
        this.utilisateurs = []
        this.totalUtilisateursApi = 0
      } finally { this.chargementUtilisateurs = false }
    },

    async prevUsersPage() {
      if (this.isFirstUsersPage) return
      this.userPageIndex--
      await this.chargerUtilisateurs()
    },

    async nextUsersPage() {
      if (this.isLastUsersPage) return
      this.userPageIndex++
      await this.chargerUtilisateurs()
    },

    async chargerAbonnements() {
      try {
        const res = await abonnementAPI.list()
        this.abonnements = Array.isArray(res?.data) ? res.data
          : (Array.isArray(res?.data?.content) ? res.data.content : [])
      } catch (e) {
        console.error('Erreur abonnements:', e)
        this.abonnements = []
      }
    },

    async chargerProjets() {
      this.chargementProjets = true
      try {
        const res = await projectAPI.list()
        const payload = res.data || []
        this.projets = Array.isArray(payload) ? payload : (Array.isArray(payload.content) ? payload.content : [])
      } catch (err) {
        console.error('Erreur projets:', err)
        this.projets = []
      } finally { this.chargementProjets = false }
    },

    async chargerFactures() {
      this.chargementFactures = true
      try {
        const res = await factureAPI.getAllAdmin()
        this.factures = Array.isArray(res?.data) ? res.data : (Array.isArray(res?.data?.content) ? res.data.content : [])
      } catch (e) {
        console.error('Erreur factures:', e)
        this.factures = []
      } finally { this.chargementFactures = false }
    },

    async chargerTransactions() {
      this.chargementTransactions = true
      this.erreurTransactions = false
      try {
        const res = await transactionAPI.getAllAdmin()
        this.transactions = Array.isArray(res?.data)
          ? res.data
          : (Array.isArray(res?.data?.content) ? res.data.content : [])
      } catch (e) {
        console.warn('Erreur chargement transactions:', e)
        this.transactions = []
        this.erreurTransactions = true
      } finally {
        this.chargementTransactions = false
      }
    },

    async chargerTaches() {
      this.chargementTaches = true
      this.erreurTaches = false
      try {
        const res = await taskAPI.getAllAdmin()
        this.taches = Array.isArray(res?.data) ? res.data : []
        console.log('✅ Tâches chargées:', this.taches.length)
      } catch (e) {
        console.error('❌ Erreur chargement tâches:', e)
        this.taches = []
        this.erreurTaches = true
      } finally {
        this.chargementTaches = false
      }
    },

    changerOnglet(ong) {
      this.ongletActuel = ong
      if (ong === 'finance') this.sousOngletFinance = 'abonnements'
    },

    getInitiales(u) {
      const p = u.prenom || u.firstName || ''
      const n = u.nom || u.lastName || ''
      return (p.charAt(0) || '') + (n.charAt(0) || '')
    },

    getUtilisateurId(a) { return a.utilisateurId || a.utilisateur_id || a.userId },
    getPlan(a) { return a.nom || a.plan || a.type || 'Premium' },
    getDateDebut(a) { return a.dateDebut || a.date_debut || a.startDate },
    getDateFin(a) { return a.dateFin || a.date_fin || a.endDate },

    getDescription(p) { return (p.description || '').substring(0, 80) },
    getDateCreation(p) { return p.dateCreation || p.date_creation || p.createdAt },

    // S'appuie sur la page chargée + cache
    getUserName(userId) {
      if (!userId) return this.$t('commun.inconnu')
      const fromList = this.utilisateurs.find(u => u.id === userId)
      const u = fromList || this.userCache[userId]
      if (u) {
        const p = u.prenom || u.firstName || ''
        const n = u.nom || u.lastName || ''
        const full = (p + ' ' + n).trim()
        return full || `Utilisateur #${userId}`
      }
      return `Utilisateur #${userId}`
    },

    getProjetName(projetId) {
      if (!projetId) return '—'
      const projet = this.projets.find(p => p.id === projetId)
      return projet ? projet.titre : `Projet #${projetId}`
    },

    getProprietaireName(p) {
      const id = p.id_createur || p.idCreateur || p.createurId || p.ownerId || p.idCreateur
      return this.getUserName(id)
    },

    getRoleBadgeClass(role) {
      const classes = { ADMINISTRATEUR:'bg-danger', CHEF_PROJET:'bg-primary', MEMBRE:'bg-success', VISITEUR:'bg-secondary' }
      return classes[role] || 'bg-secondary'
    },

    getStatutUtilisateurClass(statut) { return statut === 'SUSPENDU' ? 'bg-warning text-dark' : 'bg-success' },
    getStatutUtilisateurLabel(statut) { return statut === 'SUSPENDU' ? this.$t('admin.suspendu') : this.$t('commun.actifs') },

    getStatutAbonnementClass(statut) { return (statut || 'ACTIF') === 'ACTIF' ? 'bg-success' : 'bg-danger' },

    getStatutProjetClass(statut) {
      const classes = { ACTIF:'bg-success', TERMINE:'bg-secondary', SUSPENDU:'bg-warning text-dark', ARCHIVE:'bg-dark' }
      return classes[statut] || 'bg-light'
    },

    getStatutTacheClass(statut) {
      const classes = {
        BROUILLON:'bg-secondary',
        EN_ATTENTE_VALIDATION:'bg-warning text-dark',
        TERMINE:'bg-success',
        ANNULE:'bg-danger'
      }
      return classes[statut] || 'bg-light'
    },

    async seDeconnecter() {
      const store = useAuthStore()
      store.logout()
      await this.router.push('/')
    },

    async navigateToProject(id) {
      if (!id) {
        console.error('ID projet manquant')
        this.$toast?.error?.(this.$t('erreurs.projetIntrouvable'))
        return
      }
      try {
        console.log('Navigation vers projet ID:', id)
        await this.router.push(`/projet/${id}`)
      } catch (error) {
        console.error('Erreur navigation projet:', error)
        this.$toast?.error?.(this.$t('erreurs.navigationProjet'))
      }
    },

    voirFacturesUtilisateur(userId) {
      this.ongletActuel = 'finance'
      this.sousOngletFinance = 'factures'
      if (userId) this.filtreFactureUserId = userId
      this.$nextTick(() => {
        const el = this.$refs.facturesSection
        if (el?.scrollIntoView) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
      })
    },

    async annulerAbonnement(id) {
      if (!confirm(this.$t('admin.confirmerAnnulation'))) return
      try {
        await abonnementAPI.cancel(id)
        this.abonnements = this.abonnements.filter(a => a.id !== id)
        this.$toast?.success?.(this.$t('admin.abonnementAnnule') || 'Abonnement annulé')
      } catch (e) {
        console.error('Erreur annulation abonnement:', e)
        this.$toast?.error?.(this.$t('admin.erreurAnnulation') || 'Erreur annulation')
      }
    },

    async annulerTache(id) {
      if (!confirm(this.$t('admin.confirmerAnnulationTache') || 'Êtes-vous sûr de vouloir annuler cette tâche ? Cette action est irréversible.')) {
        return
      }
      try {
        console.log('INFO: Admin annule tâche ID:', id)
        const res = await taskAPI.annulerParAdmin(id)
        const index = this.taches.findIndex(t => t.id === id)
        if (index !== -1) this.taches[index] = res.data
        this.$toast?.success?.(this.$t('admin.tacheAnnulee') || 'Tâche annulée avec succès')
      } catch (e) {
        console.error('Erreur annulation tâche:', e)
        this.$toast?.error?.(this.$t('admin.erreurAnnulationTache') || 'Erreur lors de l\'annulation de la tâche')
      }
    },


    async telechargerFacture(id) {
      try {
        if (!this.hasDownloadAPI) throw new Error('no_download_api')

        // ✅ Déterminer la langue courante (FR/EN)
        const raw = (this.$i18n && this.$i18n.locale) || localStorage.getItem('lang') || navigator.language || 'fr'
        const langue = String(raw).toLowerCase().startsWith('fr') ? 'fr' : 'en'

        // ✅ Passer la langue à l'API
        const res = await factureAPI.telechargerPDF(id, langue)
        if (!res?.data) throw new Error('download_failed')

        const blob = new Blob([res.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        const facture = this.factures.find(f => f.id === id)
        const nomFichier = facture?.numero || facture?.numeroFacture || `facture-${id}`
        link.href = url
        link.download = `${nomFichier}.pdf`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        this.$toast?.success?.(this.$t('factures.telechargementReussi'))
      } catch (e) {
        console.error('Erreur téléchargement facture:', e)
        this.$toast?.error?.(this.$t('factures.erreurTelechargement'))
      }
    },

    voirDetailFacture(f) { this.factureSelectionnee = f },

    async genererRapportFactures() {
      try {
        const rows = this.factures.map(f => {
          const numero = f.numero || f.numeroFacture || f.id
          const user = this.getUserName(f.utilisateurId || f.userId)
          const montant = f.montant || f.montantHT || f.montant_ht
          const date = this.formatDate(f.dateEmission || f.createdAt || f.date_emission)
          return `${numero},${user},${montant},${date}`
        }).join('\n')
        const csv = `data:text/csv;charset=utf-8,Numero,Utilisateur,Prix,Date\n${rows}`
        const link = document.createElement('a')
        link.href = encodeURI(csv)
        link.download = `factures_${new Date().toISOString().split('T')[0]}.csv`
        link.click()
      } catch (e) { console.error('Erreur export factures:', e) }
    },
  }
}
</script>

<style scoped>
.card { border: none; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,.08) }
.nav-pills .nav-link { border-radius: 8px; margin: 0 2px }
.nav-pills .nav-link.active { background: linear-gradient(135deg,#dc3545,#fd7e83) }
.nav-tabs .nav-link { border-radius: 8px 8px 0 0; }
.nav-tabs .nav-link.active { background: #fff; border-bottom-color: #fff; }
.avatar { font-size: 12px; font-weight: 600 }
.table th { border-top: none; font-weight: 600; color: #495057; font-size: .875rem }
.badge { font-size: .75rem; padding: .375rem .75rem }

.kpi-card { width: 100%; text-align: left; cursor: pointer; background: #fff;
  transition: transform .08s ease, box-shadow .12s ease; }
.kpi-card:focus, .kpi-card:hover { transform: translateY(-2px); box-shadow: 0 10px 24px rgba(0,0,0,.10) }
.kpi-card:active { transform: translateY(0) }

.modal { z-index: 1060 }
.modal-xl { max-width: 90vw }
</style>
