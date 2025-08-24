<template>
  <div class="container-fluid py-3">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h1 class="mb-1 d-flex align-items-center gap-2">
          <i class="fas fa-shield-alt text-danger"></i>
          {{ $t('tableauBord.admin.titre') }}
        </h1>
        <p class="text-muted mb-0">
          {{ tOr('commun.bienvenue','Bienvenue') }} {{ utilisateur?.prenom }} {{ utilisateur?.nom }}
        </p>
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
      <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">{{ tOr('commun.charger','Charger') }}</button>
    </div>

    <!-- Content -->
    <div v-else>
      <!-- KPIs Principaux -->
      <div class="row g-3 mb-4">
        <div class="col-md-3">
          <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="flex-shrink-0">
                <div class="rounded-circle bg-danger bg-opacity-10 p-3">
                  <i class="fas fa-users fa-2x text-danger"></i>
                </div>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ stats.totalUsers }}</h3>
                <p class="text-muted mb-0 small">{{ $t('tableauBord.admin.utilisateurs') }}</p>
                <small class="text-success">
                  <i class="fas fa-arrow-up me-1"></i>+{{ stats.nouveauxUtilisateurs }} ce mois
                </small>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="flex-shrink-0">
                <div class="rounded-circle bg-primary bg-opacity-10 p-3">
                  <i class="fas fa-project-diagram fa-2x text-primary"></i>
                </div>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ stats.totalProjects }}</h3>
                <p class="text-muted mb-0 small">{{ $t('projets.titre') }}</p>
                <small class="text-warning">
                  <i class="fas fa-clock me-1"></i>{{ stats.projetsActifs }} actifs
                </small>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="flex-shrink-0">
                <div class="rounded-circle bg-success bg-opacity-10 p-3">
                  <i class="fas fa-credit-card fa-2x text-success"></i>
                </div>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ stats.abonnementsActifs }}</h3>
                <p class="text-muted mb-0 small">Abonnements actifs</p>
                <small class="text-success">
                  <i class="fas fa-euro-sign me-1"></i>{{ stats.revenueMensuel }}€/mois
                </small>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex align-items-center gap-3">
              <div class="flex-shrink-0">
                <div class="rounded-circle bg-info bg-opacity-10 p-3">
                  <i class="fas fa-bell fa-2x text-info"></i>
                </div>
              </div>
              <div>
                <h3 class="mb-0 fw-bold">{{ stats.alertesSysteme }}</h3>
                <p class="text-muted mb-0 small">Alertes système</p>
                <small class="text-danger" v-if="stats.alertesCritiques > 0">
                  <i class="fas fa-exclamation-triangle me-1"></i>{{ stats.alertesCritiques }} critiques
                </small>
                <small class="text-success" v-else>
                  <i class="fas fa-check me-1"></i>Système stable
                </small>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Navigation Onglets -->
      <ul class="nav nav-pills bg-light rounded p-2 mb-4">
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='utilisateurs'}" @click="onglet='utilisateurs'">
            <i class="fas fa-users me-2"></i>{{ $t('tableauBord.admin.utilisateurs') }}
            <span class="badge bg-secondary ms-2">{{ utilisateurs.length }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='abonnements'}" @click="onglet='abonnements'">
            <i class="fas fa-crown me-2"></i>Abonnements & Paiements
            <span class="badge bg-secondary ms-2">{{ abonnements.length }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='projets'}" @click="onglet='projets'">
            <i class="fas fa-project-diagram me-2"></i>{{ $t('projets.titre') }}
            <span class="badge bg-secondary ms-2">{{ projets.length }}</span>
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='maintenance'}" @click="onglet='maintenance'">
            <i class="fas fa-cogs me-2"></i>Maintenance
          </a>
        </li>
      </ul>

      <!-- ONGLET UTILISATEURS -->
      <div v-if="onglet==='utilisateurs'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">{{ $t('tableauBord.admin.utilisateurs') }}</h5>
              <small class="text-muted">Gérer tous les utilisateurs de la plateforme</small>
            </div>
            <button class="btn btn-danger" @click="ouvrirCreationUtilisateur">
              <i class="fas fa-user-plus me-2"></i>Créer Utilisateur
            </button>
          </div>
          <div class="card-body">
            <!-- Filtres -->
            <div class="row g-3 mb-3">
              <div class="col-md-4">
                <input class="form-control" v-model="filtreUtilisateur" placeholder="Rechercher par nom, email...">
              </div>
              <div class="col-md-3">
                <select class="form-select" v-model="filtreRole">
                  <option value="">Tous les rôles</option>
                  <option value="ADMINISTRATEUR">Administrateur</option>
                  <option value="CHEF_PROJET">Chef de Projet</option>
                  <option value="MEMBRE">Membre</option>
                  <option value="VISITEUR">Visiteur</option>
                </select>
              </div>
              <div class="col-md-3">
                <select class="form-select" v-model="filtreStatut">
                  <option value="">Tous les statuts</option>
                  <option value="actif">Actifs</option>
                  <option value="suspendu">Suspendus</option>
                </select>
              </div>
            </div>

            <!-- Tableau Utilisateurs -->
            <div v-if="chargementUtilisateurs" class="text-center py-4">
              <div class="spinner-border text-danger"></div>
            </div>
            <div v-else-if="utilisateursFiltres.length===0" class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>Aucun utilisateur trouvé
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>ID</th>
                  <th>Utilisateur</th>
                  <th>Email</th>
                  <th>Rôle</th>
                  <th>Inscription</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="u in utilisateursFiltres" :key="u.id">
                  <td><span class="badge bg-light text-dark">#{{ u.id }}</span></td>
                  <td>
                    <div class="d-flex align-items-center gap-2">
                      <div class="avatar bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style="width:35px;height:35px">
                        {{ (u.prenom?.charAt(0) || '') + (u.nom?.charAt(0) || '') }}
                      </div>
                      <div>
                        <div class="fw-semibold">{{ u.prenom }} {{ u.nom }}</div>
                        <small class="text-muted">{{ u.langue?.toUpperCase() || 'FR' }}</small>
                      </div>
                    </div>
                  </td>
                  <td>{{ u.email }}</td>
                  <td>
                      <span class="badge" :class="getRoleBadgeClass(u.role)">
                        {{ $t('roles.' + (u.role||'').toLowerCase()) }}
                      </span>
                  </td>
                  <td>{{ formatDate(u.date_inscription) }}</td>
                  <td>
                    <span class="badge bg-success">Actif</span>
                  </td>
                  <td>
                    <div class="btn-group" role="group">
                      <button class="btn btn-sm btn-outline-primary" @click="modifierUtilisateur(u)" title="Modifier">
                        <i class="fas fa-edit"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-info" @click="voirDetailsUtilisateur(u)" title="Détails">
                        <i class="fas fa-eye"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-danger" :disabled="u.id===utilisateur?.id" @click="supprimerUtilisateur(u.id)" title="Supprimer">
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

      <!-- ONGLET ABONNEMENTS & PAIEMENTS -->
      <div v-if="onglet==='abonnements'">
        <div class="row g-3 mb-4">
          <!-- Métriques Financières -->
          <div class="col-md-4">
            <div class="card border-0 shadow-sm bg-success text-white">
              <div class="card-body text-center">
                <i class="fas fa-euro-sign fa-3x mb-3"></i>
                <h4 class="mb-0">{{ stats.revenueMensuel }}€</h4>
                <small>Revenus ce mois</small>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card border-0 shadow-sm bg-warning text-dark">
              <div class="card-body text-center">
                <i class="fas fa-users fa-3x mb-3"></i>
                <h4 class="mb-0">{{ stats.abonnementsActifs }}</h4>
                <small>Abonnements actifs</small>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card border-0 shadow-sm bg-info text-white">
              <div class="card-body text-center">
                <i class="fas fa-chart-line fa-3x mb-3"></i>
                <h4 class="mb-0">{{ stats.tauxConversion }}%</h4>
                <small>Taux de conversion</small>
              </div>
            </div>
          </div>
        </div>

        <!-- Tableau Abonnements -->
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white">
            <h5 class="mb-0">Supervision des Abonnements</h5>
          </div>
          <div class="card-body">
            <div class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>Utilisateur</th>
                  <th>Plan</th>
                  <th>Prix</th>
                  <th>Début</th>
                  <th>Fin</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="a in abonnements" :key="a.id">
                  <td>
                    <div>{{ getUserName(a.utilisateur_id) }}</div>
                    <small class="text-muted">ID: {{ a.utilisateur_id }}</small>
                  </td>
                  <td>{{ a.nom }}</td>
                  <td><span class="fw-bold text-success">{{ a.prix }}€</span></td>
                  <td>{{ formatDate(a.date_debut) }}</td>
                  <td>{{ formatDate(a.date_fin) }}</td>
                  <td>
                      <span class="badge" :class="a.statut === 'ACTIF' ? 'bg-success' : 'bg-danger'">
                        {{ a.statut }}
                      </span>
                  </td>
                  <td>
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-warning" @click="suspendreAbonnement(a.id)" v-if="a.statut === 'ACTIF'">
                        <i class="fas fa-pause"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-success" @click="reactiverAbonnement(a.id)" v-else>
                        <i class="fas fa-play"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-info" @click="voirFacturesUtilisateur(a.utilisateur_id)">
                        <i class="fas fa-file-invoice"></i>
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

      <!-- ONGLET PROJETS -->
      <div v-if="onglet==='projets'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">Supervision des Projets</h5>
              <small class="text-muted">Surveillance globale de tous les projets</small>
            </div>
            <div class="d-flex gap-2">
              <input class="form-control" style="max-width:260px" v-model="filtreProjet" placeholder="Rechercher...">
              <select class="form-select" style="max-width:180px" v-model="filtreProjetStatut">
                <option value="">Tous les statuts</option>
                <option value="ACTIF">Actifs</option>
                <option value="TERMINE">Terminés</option>
                <option value="SUSPENDU">Suspendus</option>
              </select>
            </div>
          </div>
          <div class="card-body">
            <div v-if="chargementProjets" class="text-center py-4">
              <div class="spinner-border text-primary"></div>
            </div>
            <div v-else-if="projetsFiltres.length===0" class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>Aucun projet trouvé
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>Projet</th>
                  <th>Chef de Projet</th>
                  <th>Membres</th>
                  <th>Tâches</th>
                  <th>Statut</th>
                  <th>Activité</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="p in projetsFiltres" :key="p.id">
                  <td>
                    <div>
                      <div class="fw-semibold">{{ p.titre }}</div>
                      <small class="text-muted">{{ p.description?.substring(0, 50) }}...</small>
                    </div>
                  </td>
                  <td>{{ getProprietaireName(p) }}</td>
                  <td>
                    <span class="badge bg-light text-dark">{{ getMembresCount(p.id) }}</span>
                  </td>
                  <td>
                    <div class="progress" style="width:80px;height:6px">
                      <div class="progress-bar bg-success" :style="{width: getTachesProgression(p.id) + '%'}"></div>
                    </div>
                    <small>{{ getTachesProgression(p.id) }}%</small>
                  </td>
                  <td>
                      <span class="badge" :class="getStatutBadgeClass(p.statut)">
                        {{ $t('projets.statuts.'+(p.statut||'').toLowerCase()) }}
                      </span>
                  </td>
                  <td>
                    <small class="text-muted">{{ formatDateRelative(p.date_creation) }}</small>
                  </td>
                  <td>
                    <div class="btn-group">
                      <button class="btn btn-sm btn-outline-info" @click="voirProjet(p.id)" title="Voir">
                        <i class="fas fa-eye"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-warning" @click="modererProjet(p)" title="Modérer">
                        <i class="fas fa-flag"></i>
                      </button>
                      <button class="btn btn-sm btn-outline-danger" @click="archiverProjet(p.id)" title="Archiver">
                        <i class="fas fa-archive"></i>
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

      <!-- ONGLET MAINTENANCE -->
      <div v-if="onglet==='maintenance'">
        <div class="row g-3">
          <div class="col-md-8">
            <div class="card border-0 shadow-sm">
              <div class="card-header bg-white">
                <h5 class="mb-0">État du Système</h5>
              </div>
              <div class="card-body">
                <div class="row g-3">
                  <div class="col-md-6">
                    <div class="d-flex justify-content-between">
                      <span>Base de données</span>
                      <span class="badge bg-success">Opérationnelle</span>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="d-flex justify-content-between">
                      <span>API Spring Boot</span>
                      <span class="badge bg-success">Opérationnelle</span>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="d-flex justify-content-between">
                      <span>Frontend Vue.js</span>
                      <span class="badge bg-success">Opérationnelle</span>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="d-flex justify-content-between">
                      <span>Paiements Stripe</span>
                      <span class="badge bg-success">Opérationnelle</span>
                    </div>
                  </div>
                </div>
                <hr>
                <div class="row g-3">
                  <div class="col-md-4">
                    <button class="btn btn-outline-primary w-100" @click="exporterDonnees">
                      <i class="fas fa-download me-2"></i>Exporter Données
                    </button>
                  </div>
                  <div class="col-md-4">
                    <button class="btn btn-outline-warning w-100" @click="nettoyerLogs">
                      <i class="fas fa-broom me-2"></i>Nettoyer Logs
                    </button>
                  </div>
                  <div class="col-md-4">
                    <button class="btn btn-outline-info w-100" @click="genererRapport">
                      <i class="fas fa-chart-bar me-2"></i>Rapport Activité
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card border-0 shadow-sm">
              <div class="card-header bg-white">
                <h6 class="mb-0">Informations Système</h6>
              </div>
              <div class="card-body">
                <p class="mb-2"><strong>Version:</strong> 1.0.0</p>
                <p class="mb-2"><strong>Environnement:</strong> Production</p>
                <p class="mb-2"><strong>Base:</strong> MySQL 8.2.0</p>
                <p class="mb-2"><strong>Backend:</strong> Spring Boot (Java 21)</p>
                <p class="mb-2"><strong>Frontend:</strong> Vue.js 3</p>
                <p class="mb-0"><strong>Dernière MàJ:</strong> {{ now }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth'
import api, { userAPI, projectAPI, abonnementAPI } from '@/services/api'
import SelecteurLangue from '@/components/SelecteurLangue.vue'

export default {
  name: 'TableauDeBordAdminView',
  components: { SelecteurLangue },
  data(){
    return {
      onglet: 'utilisateurs',
      chargementGlobal: true,
      chargementUtilisateurs: false,
      chargementProjets: false,
      utilisateurs: [],
      projets: [],
      abonnements: [],
      filtreUtilisateur: '',
      filtreProjet: '',
      filtreRole: '',
      filtreStatut: '',
      filtreProjetStatut: '',
      erreurBackend: null
    }
  },
  computed: {
    utilisateur() { return useAuthStore().user },
    now() { return new Date().toLocaleString() },
    stats() {
      const users = Array.isArray(this.utilisateurs) ? this.utilisateurs : []
      const projs = Array.isArray(this.projets) ? this.projets : []
      const abos = Array.isArray(this.abonnements) ? this.abonnements : []

      return {
        totalUsers: users.length,
        totalProjects: projs.length,
        nouveauxUtilisateurs: users.filter(u => this.isThisMonth(u.date_inscription)).length,
        projetsActifs: projs.filter(p => p.statut === 'ACTIF').length,
        abonnementsActifs: abos.filter(a => a.statut === 'ACTIF').length,
        revenueMensuel: abos.filter(a => a.statut === 'ACTIF').reduce((sum, a) => sum + (a.prix || 0), 0),
        tauxConversion: users.length > 0 ? Math.round((abos.length / users.length) * 100) : 0,
        alertesSysteme: 0,
        alertesCritiques: 0
      }
    },
    utilisateursFiltres() {
      const q = this.filtreUtilisateur.trim().toLowerCase()
      let base = Array.isArray(this.utilisateurs) ? this.utilisateurs : []

      if (q) {
        base = base.filter(u =>
          (u.nom || '').toLowerCase().includes(q) ||
          (u.prenom || '').toLowerCase().includes(q) ||
          (u.email || '').toLowerCase().includes(q)
        )
      }

      if (this.filtreRole) {
        base = base.filter(u => u.role === this.filtreRole)
      }

      return base
    },
    projetsFiltres() {
      const q = this.filtreProjet.trim().toLowerCase()
      let base = Array.isArray(this.projets) ? this.projets : []

      if (q) {
        base = base.filter(p =>
          (p.titre || '').toLowerCase().includes(q) ||
          (p.description || '').toLowerCase().includes(q)
        )
      }

      if (this.filtreProjetStatut) {
        base = base.filter(p => p.statut === this.filtreProjetStatut)
      }

      return base
    }
  },
  async mounted() {
    const store = useAuthStore()
    if (!store.user && localStorage.getItem('user')) {
      try { store.user = JSON.parse(localStorage.getItem('user')) } catch {}
    }
    if (!store.user || store.user.role !== 'ADMINISTRATEUR') {
      this.$router.push('/')
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
          this.chargerUtilisateurs(),
          this.chargerProjets(),
          this.chargerAbonnements()
        ])
      } catch (e) {
        console.error(e)
        this.erreurBackend = 'Impossible de charger les données administrateur'
      } finally {
        this.chargementGlobal = false
      }
    },

    async chargerUtilisateurs() {
      this.chargementUtilisateurs = true
      try {
        const { data } = await userAPI.getAllUsers()
        this.utilisateurs = Array.isArray(data) ? data : (Array.isArray(data?.content) ? data.content : [])
      } catch (e) {
        console.error(e)
        this.utilisateurs = []
      } finally {
        this.chargementUtilisateurs = false
      }
    },

    async chargerProjets() {
      this.chargementProjets = true
      try {
        const { data } = await projectAPI.getAllProjects()
        this.projets = Array.isArray(data) ? data : (Array.isArray(data?.content) ? data.content : [])
      } catch (e) {
        console.error(e)
        this.projets = []
      } finally {
        this.chargementProjets = false
      }
    },

    async chargerAbonnements() {
      try {
        const { data } = await abonnementAPI.getAllAbonnements()
        this.abonnements = Array.isArray(data) ? data : []
      } catch (e) {
        console.error(e)
        this.abonnements = []
      }
    },

    // Actions Utilisateurs
    async ouvrirCreationUtilisateur() {
      const nom = prompt('Nom :')
      if (!nom) return
      const prenom = prompt('Prénom :')
      if (!prenom) return
      const email = prompt('Email :')
      if (!email) return
      const role = prompt('Rôle (ADMINISTRATEUR|CHEF_PROJET|MEMBRE|VISITEUR) :', 'MEMBRE') || 'MEMBRE'

      try {
        const { data } = await api.post('/utilisateurs', { nom, prenom, email, role })
        this.utilisateurs.unshift(data)
        alert('Utilisateur créé avec succès')
      } catch (e) {
        console.error(e)
        alert('Erreur lors de la création')
      }
    },

    modifierUtilisateur(u) {
      alert(`Modifier utilisateur ${u.prenom} ${u.nom} (modal à implémenter)`)
    },

    voirDetailsUtilisateur(u) {
      alert(`Détails utilisateur ${u.prenom} ${u.nom} (modal à implémenter)`)
    },

    async supprimerUtilisateur(id) {
      const s = useAuthStore()
      if (id === s.user?.id) {
        alert('Impossible de supprimer votre propre compte')
        return
      }
      if (!confirm('Confirmer la suppression de cet utilisateur ?')) return

      try {
        await userAPI.deleteUser(id)
        this.utilisateurs = this.utilisateurs.filter(x => x.id !== id)
        alert('Utilisateur supprimé avec succès')
      } catch (e) {
        console.error(e)
        alert('Erreur lors de la suppression')
      }
    },

    // Actions Abonnements
    async suspendreAbonnement(id) {
      if (!confirm('Suspendre cet abonnement ?')) return
      try {
        await abonnementAPI.updateStatut(id, 'SUSPENDU')
        const abo = this.abonnements.find(a => a.id === id)
        if (abo) abo.statut = 'SUSPENDU'
        alert('Abonnement suspendu')
      } catch (e) {
        console.error(e)
        alert('Erreur')
      }
    },

    async reactiverAbonnement(id) {
      try {
        await abonnementAPI.updateStatut(id, 'ACTIF')
        const abo = this.abonnements.find(a => a.id === id)
        if (abo) abo.statut = 'ACTIF'
        alert('Abonnement réactivé')
      } catch (e) {
        console.error(e)
        alert('Erreur')
      }
    },

    voirFacturesUtilisateur(userId) {
      alert(`Voir factures utilisateur ${userId} (modal à implémenter)`)
    },

    // Actions Projets
    voirProjet(id) { this.$router.push(`/projet/${id}`) },
    modererProjet(p) { alert(`Modérer projet ${p.titre} (modal à implémenter)`) },
    async archiverProjet(id) {
      if (!confirm('Archiver ce projet ?')) return
      try {
        await projectAPI.updateStatut(id, 'ARCHIVE')
        const projet = this.projets.find(p => p.id === id)
        if (projet) projet.statut = 'ARCHIVE'
        alert('Projet archivé')
      } catch (e) {
        console.error(e)
        alert('Erreur')
      }
    },

    // Actions Maintenance
    exporterDonnees() { alert('Export des données (à implémenter)') },
    nettoyerLogs() { alert('Nettoyage des logs (à implémenter)') },
    genererRapport() { alert('Génération du rapport (à implémenter)') },

    // Utilitaires
    getRoleBadgeClass(role) {
      const classes = {
        'ADMINISTRATEUR': 'bg-danger',
        'CHEF_PROJET': 'bg-success',
        'MEMBRE': 'bg-primary',
        'VISITEUR': 'bg-warning text-dark'
      }
      return classes[role] || 'bg-secondary'
    },

    getStatutBadgeClass(s) {
      const classes = {
        'ACTIF': 'bg-success',
        'TERMINE': 'bg-secondary',
        'SUSPENDU': 'bg-warning text-dark',
        'ARCHIVE': 'bg-dark'
      }
      return classes[s] || 'bg-secondary'
    },

    getProprietaireName(p) {
      if (p.proprietaire) return `${p.proprietaire.prenom} ${p.proprietaire.nom}`
      if (p.id_createur) {
        const u = this.utilisateurs.find(x => x.id === p.id_createur)
        return u ? `${u.prenom} ${u.nom}` : 'Inconnu'
      }
      return 'Inconnu'
    },

    getUserName(userId) {
      const u = this.utilisateurs.find(x => x.id === userId)
      return u ? `${u.prenom} ${u.nom}` : `Utilisateur #${userId}`
    },

    getMembresCount(projetId) {
      return Math.floor(Math.random() * 8) + 1 // Placeholder - à connecter à l'API
    },

    getTachesProgression(projetId) {
      return Math.floor(Math.random() * 100) // Placeholder - à connecter à l'API
    },

    formatDate(date) {
      return new Date(date).toLocaleDateString('fr-FR')
    },

    formatDateRelative(date) {
      const now = new Date()
      const diff = now - new Date(date)
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      if (days === 0) return "Aujourd'hui"
      if (days === 1) return "Hier"
      if (days < 7) return `Il y a ${days} jours`
      return this.formatDate(date)
    },

    isThisMonth(date) {
      const now = new Date()
      const d = new Date(date)
      return now.getMonth() === d.getMonth() && now.getFullYear() === d.getFullYear()
    },

    seDeconnecter() {
      const s = useAuthStore()
      s.logout()
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,.08);
}

.cursor-pointer {
  cursor: pointer;
}

.cursor-pointer:hover {
  opacity: .9;
  transform: translateY(-2px);
  transition: all 0.2s ease;
}

.text-truncate {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.nav-pills .nav-link {
  border-radius: 8px;
  margin: 0 2px;
}

.nav-pills .nav-link.active {
  background: linear-gradient(135deg, #dc3545, #fd7e83);
}

.avatar {
  font-size: 12px;
  font-weight: 600;
}

.progress {
  height: 6px;
  border-radius: 10px;
}

.btn-group .btn {
  border-radius: 6px;
  margin: 0 1px;
}

.table th {
  border-top: none;
  font-weight: 600;
  color: #495057;
  font-size: 0.875rem;
}

.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}
</style>
