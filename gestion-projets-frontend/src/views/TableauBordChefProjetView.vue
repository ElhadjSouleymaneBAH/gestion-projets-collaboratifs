<template>
  <div class="container-fluid py-3">
    <!-- Header Premium -->
    <div class="premium-header mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h1 class="mb-1 d-flex align-items-center gap-2">
            <i class="fas fa-crown text-warning"></i>
            {{ $t('tableauBord.chefProjet.titre') }}
            <span class="badge bg-gradient-warning text-dark">PREMIUM</span>
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
          <div class="abonnement-status">
            <span class="badge bg-success">Abonnement actif</span>
            <small class="text-muted d-block">Expire le {{ formatDate(abonnementInfo?.date_fin) }}</small>
          </div>
          <button class="btn btn-outline-danger" @click="seDeconnecter">
            <i class="fas fa-sign-out-alt me-2"></i>{{ $t('nav.deconnexion') }}
          </button>
        </div>
      </div>
    </div>

    <!-- Alerte Abonnement -->
    <div v-if="abonnementExpireSoon" class="alert alert-warning d-flex justify-content-between align-items-center mb-4">
      <div>
        <i class="fas fa-exclamation-triangle me-2"></i>
        Votre abonnement expire dans {{ joursRestants }} jours
      </div>
      <button class="btn btn-sm btn-warning" @click="gererAbonnement">
        <i class="fas fa-credit-card me-1"></i>Renouveler
      </button>
    </div>

    <!-- Loading / Error -->
    <div v-if="erreurBackend" class="alert alert-danger d-flex justify-content-between align-items-center">
      <div><i class="fas fa-exclamation-triangle me-2"></i>{{ erreurBackend }}</div>
      <button class="btn btn-sm btn-outline-danger" @click="chargerToutesDonnees">{{ tOr('commun.charger','Charger') }}</button>
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
            <div class="card-body">
              <div class="d-flex align-items-center gap-3">
                <div class="metric-icon bg-success bg-opacity-10 rounded-circle p-3">
                  <i class="fas fa-project-diagram fa-2x text-success"></i>
                </div>
                <div>
                  <h3 class="mb-0 fw-bold">{{ mesProjets.length }}</h3>
                  <p class="text-muted mb-1 small">Mes Projets</p>
                  <small class="text-success">{{ projetsActifs }} actifs</small>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='equipes'">
            <div class="card-body">
              <div class="d-flex align-items-center gap-3">
                <div class="metric-icon bg-primary bg-opacity-10 rounded-circle p-3">
                  <i class="fas fa-users fa-2x text-primary"></i>
                </div>
                <div>
                  <h3 class="mb-0 fw-bold">{{ totalMembres }}</h3>
                  <p class="text-muted mb-1 small">Membres d'équipe</p>
                  <small class="text-info">{{ projetsAvecEquipes }} équipes</small>
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
                  <h3 class="mb-0 fw-bold">{{ tachesEnValidation }}</h3>
                  <p class="text-muted mb-1 small">Tâches en validation</p>
                  <small class="text-warning">{{ tachesUrgentes }} urgentes</small>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="metric-card card h-100 border-0 shadow-sm cursor-pointer" @click="onglet='facturation'">
            <div class="card-body">
              <div class="d-flex align-items-center gap-3">
                <div class="metric-icon bg-info bg-opacity-10 rounded-circle p-3">
                  <i class="fas fa-file-invoice-dollar fa-2x text-info"></i>
                </div>
                <div>
                  <h3 class="mb-0 fw-bold">{{ mesFactures.length }}</h3>
                  <p class="text-muted mb-1 small">Factures</p>
                  <small class="text-success">{{ montantTotal }}€ total</small>
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
          <a class="nav-link" :class="{active:onglet==='equipes'}" @click="onglet='equipes'">
            <i class="fas fa-users me-2"></i>Gestion Équipes
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='taches'}" @click="onglet='taches'">
            <i class="fas fa-tasks me-2"></i>Validation Tâches
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{active:onglet==='facturation'}" @click="onglet='facturation'">
            <i class="fas fa-file-invoice-dollar me-2"></i>Facturation
          </a>
        </li>
        <li class="nav-item ms-auto">
          <a class="nav-link" :class="{active:onglet==='profil'}" @click="onglet='profil'">
            <i class="fas fa-user-cog me-2"></i>Profil
          </a>
        </li>
      </ul>

      <!-- ONGLET PROJETS -->
      <div v-if="onglet==='projets'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <h5 class="mb-0">Mes Projets ({{ mesProjets.length }})</h5>
              <small class="text-muted">Création, gestion et supervision</small>
            </div>
            <button class="btn btn-success" @click="creerNouveauProjet">
              <i class="fas fa-plus me-2"></i>Nouveau Projet
            </button>
          </div>
          <div class="card-body">
            <div v-if="chargementProjets" class="text-center py-4">
              <div class="spinner-border text-success"></div>
            </div>
            <div v-else-if="mesProjets.length===0" class="text-center py-5">
              <i class="fas fa-project-diagram fa-3x text-muted mb-3"></i>
              <h5 class="text-muted">Aucun projet créé</h5>
              <p class="text-muted">Créez votre premier projet pour commencer</p>
              <button class="btn btn-success" @click="creerNouveauProjet">
                <i class="fas fa-plus me-2"></i>Créer un projet
              </button>
            </div>
            <div v-else class="row">
              <div v-for="projet in mesProjets" :key="projet.id" class="col-lg-6 mb-3">
                <div class="card projet-card h-100 border-0 shadow-sm">
                  <div class="card-header bg-gradient-primary text-white d-flex justify-content-between align-items-center">
                    <div>
                      <h6 class="mb-0">{{ projet.titre }}</h6>
                      <small class="opacity-75">Créé le {{ formatDate(projet.date_creation) }}</small>
                    </div>
                    <span class="badge" :class="getProjetBadgeClass(projet.statut)">
                      {{ projet.statut }}
                    </span>
                  </div>
                  <div class="card-body">
                    <p class="text-muted mb-3">{{ projet.description }}</p>
                    <div class="row g-2 mb-3">
                      <div class="col-6">
                        <div class="metric-small">
                          <i class="fas fa-users text-primary me-1"></i>
                          <span>{{ projet.nombreMembres || 0 }} membres</span>
                        </div>
                      </div>
                      <div class="col-6">
                        <div class="metric-small">
                          <i class="fas fa-tasks text-warning me-1"></i>
                          <span>{{ getTachesCount(projet.id) }} tâches</span>
                        </div>
                      </div>
                    </div>
                    <div class="progress mb-3" style="height: 8px;">
                      <div class="progress-bar bg-gradient-success" :style="{width: getProgressionProjet(projet.id) + '%'}"></div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center">
                      <small class="text-muted">{{ getProgressionProjet(projet.id) }}% complété</small>
                      <div class="btn-group">
                        <button class="btn btn-sm btn-outline-primary" @click="ouvrirProjet(projet.id)" title="Ouvrir">
                          <i class="fas fa-external-link-alt"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-info" @click="gererMembres(projet)" title="Équipe">
                          <i class="fas fa-users"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-success" @click="ouvrirChat(projet)" title="Chat">
                          <i class="fas fa-comments"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-secondary" @click="parametresProjet(projet)" title="Paramètres">
                          <i class="fas fa-cog"></i>
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ONGLET GESTION ÉQUIPES -->
      <div v-if="onglet==='equipes'">
        <div class="row g-3">
          <!-- Ajouter Membre -->
          <div class="col-lg-6">
            <div class="card border-0 shadow-sm">
              <div class="card-header bg-white">
                <h5 class="mb-0">
                  <i class="fas fa-user-plus text-primary me-2"></i>Ajouter un Membre
                </h5>
                <small class="text-muted">Inviter des utilisateurs à rejoindre vos projets</small>
              </div>
              <div class="card-body">
                <form @submit.prevent="ajouterMembreProjet">
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Projet</label>
                    <select class="form-select" v-model="formMembre.projetId" required>
                      <option value="">Sélectionner un projet</option>
                      <option v-for="p in mesProjets" :key="p.id" :value="p.id">
                        {{ p.titre }} ({{ p.nombreMembres || 0 }} membres)
                      </option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Email du membre</label>
                    <input type="email" class="form-control" v-model.trim="formMembre.email"
                           placeholder="membre@example.com" required>
                    <div class="form-text">Le membre doit être inscrit sur la plateforme</div>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Rôle dans le projet</label>
                    <select class="form-select" v-model="formMembre.role">
                      <option value="MEMBRE">Membre</option>
                      <option value="CHEF_PROJET">Co-Chef de Projet</option>
                    </select>
                  </div>
                  <button class="btn btn-primary w-100" :disabled="ajoutEnCours">
                    <span v-if="ajoutEnCours" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="fas fa-user-plus me-2"></i>
                    {{ ajoutEnCours ? 'Ajout en cours...' : 'Ajouter au projet' }}
                  </button>
                </form>
              </div>
            </div>
          </div>

          <!-- Créer et Assigner Tâche -->
          <div class="col-lg-6">
            <div class="card border-0 shadow-sm">
              <div class="card-header bg-white">
                <h5 class="mb-0">
                  <i class="fas fa-tasks text-success me-2"></i>Créer & Assigner une Tâche
                </h5>
                <small class="text-muted">Attribution de tâches aux membres</small>
              </div>
              <div class="card-body">
                <form @submit.prevent="creerTache">
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Projet</label>
                    <select class="form-select" v-model="formTache.projetId" required>
                      <option value="">Sélectionner un projet</option>
                      <option v-for="p in mesProjets" :key="p.id" :value="p.id">{{ p.titre }}</option>
                    </select>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Titre de la tâche</label>
                    <input class="form-control" v-model.trim="formTache.titre"
                           placeholder="Ex: Développer l'authentification" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Description</label>
                    <textarea class="form-control" rows="3" v-model.trim="formTache.description"
                              placeholder="Description détaillée de la tâche"></textarea>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Assigner à (email)</label>
                    <input type="email" class="form-control" v-model.trim="formTache.emailMembre"
                           placeholder="membre@example.com" required>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Priorité</label>
                    <select class="form-select" v-model="formTache.priorite">
                      <option value="BASSE">Basse</option>
                      <option value="MOYENNE">Moyenne</option>
                      <option value="HAUTE">Haute</option>
                    </select>
                  </div>
                  <button class="btn btn-success w-100" :disabled="creationTacheEnCours">
                    <span v-if="creationTacheEnCours" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="fas fa-tasks me-2"></i>
                    {{ creationTacheEnCours ? 'Création en cours...' : 'Créer et assigner' }}
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>

        <!-- Vue d'ensemble des équipes -->
        <div class="row mt-4">
          <div v-for="p in mesProjets" :key="'equipe-'+p.id" class="col-md-6 col-lg-4 mb-3">
            <div class="card border-0 shadow-sm h-100">
              <div class="card-header bg-gradient-light">
                <h6 class="mb-0">{{ p.titre }}</h6>
                <small class="text-muted">{{ p.nombreMembres || 0 }} membres</small>
              </div>
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                  <span class="text-muted">Activité récente</span>
                  <button class="btn btn-sm btn-outline-primary" @click="voirEquipeDetaille(p.id)">
                    <i class="fas fa-eye me-1"></i>Voir l'équipe
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ONGLET VALIDATION TÂCHES -->
      <div v-if="onglet==='taches'">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white">
            <h5 class="mb-0">Validation des Tâches</h5>
            <small class="text-muted">Workflow d'approbation des tâches d'équipe</small>
          </div>
          <div class="card-body">
            <!-- Filtres -->
            <div class="row g-3 mb-3">
              <div class="col-md-4">
                <select class="form-select" v-model="filtreTacheProjet">
                  <option value="">Tous les projets</option>
                  <option v-for="p in mesProjets" :key="p.id" :value="p.id">{{ p.titre }}</option>
                </select>
              </div>
              <div class="col-md-4">
                <select class="form-select" v-model="filtreTacheStatut">
                  <option value="">Tous les statuts</option>
                  <option value="BROUILLON">Brouillon</option>
                  <option value="EN_ATTENTE_VALIDATION">En attente validation</option>
                  <option value="TERMINE">Terminées</option>
                </select>
              </div>
              <div class="col-md-4">
                <select class="form-select" v-model="filtreTachePriorite">
                  <option value="">Toutes priorités</option>
                  <option value="HAUTE">Haute</option>
                  <option value="MOYENNE">Moyenne</option>
                  <option value="BASSE">Basse</option>
                </select>
              </div>
            </div>

            <!-- Liste des tâches -->
            <div v-if="tachesFiltrees.length === 0" class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>Aucune tâche à valider
            </div>
            <div v-else>
              <div v-for="t in tachesFiltrees" :key="t.id" class="card mb-3 border-start border-4"
                   :class="{'border-warning': t.statut === 'EN_ATTENTE_VALIDATION', 'border-success': t.statut === 'TERMINE'}">
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start">
                    <div class="flex-grow-1">
                      <h6 class="mb-1">{{ t.titre }}</h6>
                      <p class="text-muted mb-2">{{ t.description }}</p>
                      <div class="d-flex align-items-center gap-3">
                        <small class="text-muted">
                          <i class="fas fa-project-diagram me-1"></i>{{ getProjetNom(t.id_projet) }}
                        </small>
                        <small class="text-muted">
                          <i class="fas fa-user me-1"></i>{{ getAssigneNom(t.id_assigne) }}
                        </small>
                        <span class="badge" :class="getPriorityBadgeClass(t.priorite || 'MOYENNE')">
                          {{ t.priorite || 'MOYENNE' }}
                        </span>
                      </div>
                    </div>
                    <div class="flex-shrink-0">
                      <span class="badge mb-2" :class="getTacheStatusBadge(t.statut)">{{ t.statut }}</span>
                      <div class="btn-group d-block">
                        <button v-if="t.statut === 'EN_ATTENTE_VALIDATION'"
                                class="btn btn-sm btn-success me-1" @click="validerTache(t)">
                          <i class="fas fa-check"></i> Valider
                        </button>
                        <button v-if="t.statut === 'EN_ATTENTE_VALIDATION'"
                                class="btn btn-sm btn-warning me-1" @click="renvoyerBrouillon(t)">
                          <i class="fas fa-undo"></i> Modifier
                        </button>
                        <button class="btn btn-sm btn-outline-danger" @click="annulerTache(t)">
                          <i class="fas fa-times"></i> Annuler
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ONGLET FACTURATION -->
      <div v-if="onglet==='facturation'">
        <div class="row g-3">
          <!-- Statut Abonnement -->
          <div class="col-md-8">
            <div class="card border-0 shadow-sm">
              <div class="card-header bg-white">
                <h5 class="mb-0">Mon Abonnement Premium</h5>
              </div>
              <div class="card-body">
                <div class="row align-items-center">
                  <div class="col-md-8">
                    <h6 class="text-success mb-1">Plan Premium Mensuel - 10€/mois</h6>
                    <p class="text-muted mb-2">Accès complet aux fonctionnalités de gestion de projet</p>
                    <div class="d-flex align-items-center gap-3">
                      <span class="badge bg-success">Actif</span>
                      <small class="text-muted">Expire le {{ formatDate(abonnementInfo?.date_fin) }}</small>
                    </div>
                  </div>
                  <div class="col-md-4 text-end">
                    <button class="btn btn-outline-primary me-2" @click="gererAbonnement">
                      <i class="fas fa-cog me-1"></i>Gérer
                    </button>
                    <button class="btn btn-warning" @click="renouvelerAbonnement">
                      <i class="fas fa-redo me-1"></i>Renouveler
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Résumé Facturation -->
          <div class="col-md-4">
            <div class="card border-0 shadow-sm bg-gradient-success text-white">
              <div class="card-body text-center">
                <i class="fas fa-file-invoice-dollar fa-3x mb-3"></i>
                <h4 class="mb-0">{{ montantTotal }}€</h4>
                <small>Total facturé</small>
              </div>
            </div>
          </div>
        </div>

        <!-- Historique Factures -->
        <div class="card border-0 shadow-sm mt-4">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Mes Factures</h5>
            <button class="btn btn-outline-success" @click="telechargerToutesFactures">
              <i class="fas fa-download me-1"></i>Télécharger tout
            </button>
          </div>
          <div class="card-body">
            <div v-if="chargementFactures" class="text-center py-4">
              <div class="spinner-border text-info"></div>
            </div>
            <div v-else-if="mesFactures.length === 0" class="alert alert-info">
              <i class="fas fa-info-circle me-2"></i>Aucune facture disponible
            </div>
            <div v-else class="table-responsive">
              <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                  <th>Numéro</th>
                  <th>Date</th>
                  <th>Montant HT</th>
                  <th>TVA</th>
                  <th>Total TTC</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="f in mesFactures" :key="f.id">
                  <td>
                    <span class="fw-semibold">{{ f.numero_facture }}</span>
                  </td>
                  <td>{{ formatDate(f.date_emission) }}</td>
                  <td>{{ f.montant_ht }}€</td>
                  <td>{{ f.tva }}€</td>
                  <td class="fw-semibold text-success">{{ (f.montant_ht + f.tva).toFixed(2) }}€</td>
                  <td>
                      <span class="badge" :class="f.statut === 'GENEREE' ? 'bg-success' : 'bg-danger'">
                        {{ f.statut }}
                      </span>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-outline-primary" @click="telechargerFacture(f.id)">
                      <i class="fas fa-download"></i> PDF
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
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
              </div>
              <div class="card-body">
                <form @submit.prevent="sauvegarderProfil">
                  <div class="row">
                    <div class="col-md-6 mb-3">
                      <label class="form-label fw-semibold">Prénom</label>
                      <input class="form-control" v-model.trim="utilisateur.prenom" required>
                    </div>
                    <div class="col-md-6 mb-3">
                      <label class="form-label fw-semibold">Nom</label>
                      <input class="form-control" v-model.trim="utilisateur.nom" required>
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Email</label>
                    <input type="email" class="form-control" v-model="utilisateur.email" readonly>
                    <div class="form-text">L'email ne peut pas être modifié</div>
                  </div>
                  <div class="mb-3">
                    <label class="form-label fw-semibold">Adresse</label>
                    <textarea class="form-control" rows="2" v-model="utilisateur.adresse"
                              placeholder="Votre adresse complète"></textarea>
                  </div>
                  <button class="btn btn-primary" :disabled="sauvegardeProfil">
                    <span v-if="sauvegardeProfil" class="spinner-border spinner-border-sm me-2"></span>
                    <i v-else class="fas fa-save me-2"></i>
                    {{ sauvegardeProfil ? 'Sauvegarde...' : 'Enregistrer' }}
                  </button>
                </form>
              </div>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="card border-0 shadow-sm">
              <div class="card-header bg-white">
                <h6 class="mb-0">Statut Compte</h6>
              </div>
              <div class="card-body">
                <div class="text-center mb-3">
                  <div class="avatar-large bg-success text-white rounded-circle mx-auto mb-2 d-flex align-items-center justify-content-center" style="width:60px;height:60px">
                    <i class="fas fa-crown fa-2x"></i>
                  </div>
                  <h6>Chef de Projet Premium</h6>
                  <span class="badge bg-success">Compte Actif</span>
                </div>
                <hr>
                <p class="mb-2"><strong>Projets créés:</strong> {{ mesProjets.length }}</p>
                <p class="mb-2"><strong>Membres gérés:</strong> {{ totalMembres }}</p>
                <p class="mb-2"><strong>Abonnement:</strong> Premium</p>
                <p class="mb-0"><strong>Membre depuis:</strong> {{ formatDate(utilisateur?.date_inscription) }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- MODAL CHAT TEMPS RÉEL -->
      <div v-if="chatOuvert" class="modal d-block" style="background:rgba(0,0,0,.5);z-index:1060">
        <div class="modal-dialog modal-lg">
          <div class="modal-content border-0 shadow">
            <div class="modal-header bg-gradient-success text-white">
              <h5 class="modal-title">
                <i class="fas fa-comments me-2"></i>Chat Temps Réel - {{ projetChatActuel?.titre }}
              </h5>
              <button class="btn-close btn-close-white" @click="fermerChat"></button>
            </div>
            <div class="modal-body p-0">
              <div class="chat-container" style="height:400px;overflow-y:auto;padding:20px;background:#f8f9fa;">
                <div v-for="m in messages" :key="m.id" class="mb-3">
                  <div class="d-flex" :class="{'justify-content-end': estMonMessage(m)}">
                    <div class="chat-bubble p-3 rounded" :class="getMessageClass(m)" style="max-width:75%">
                      <div v-if="!estMonMessage(m)" class="mb-1">
                        <strong class="small">{{ m.auteur || 'Membre' }}</strong>
                        <span class="text-muted ms-2 small">{{ formatTime(m.date_envoi) }}</span>
                      </div>
                      <div>{{ m.contenu }}</div>
                    </div>
                  </div>
                </div>
                <div v-if="messages.length===0" class="text-center text-muted py-4">
                  <i class="fas fa-comments fa-2x mb-2"></i>
                  <p>Aucun message. Commencez la discussion !</p>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <div class="input-group">
                <input v-model="nouveauMessage" class="form-control"
                       :placeholder="$t('collaboration.ecrireMessage')"
                       @keyup.enter="envoyerMessage" :disabled="envoyantMessage">
                <button class="btn btn-success" @click="envoyerMessage"
                        :disabled="!nouveauMessage.trim()||envoyantMessage">
                  <i class="fas fa-paper-plane"></i>
                </button>
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
import { projectAPI, taskAPI, messagesAPI, userAPI, factureService, abonnementAPI } from '@/services/api'
import SelecteurLangue from '@/components/SelecteurLangue.vue'

export default {
  name: 'TableauBordChefProjet',
  components: { SelecteurLangue },
  data() {
    return {
      onglet: 'projets',
      chargementGlobal: true,
      chargementProjets: false,
      chargementFactures: false,
      sauvegardeProfil: false,
      envoyantMessage: false,
      ajoutEnCours: false,
      creationTacheEnCours: false,
      mesProjets: [],
      mesFactures: [],
      messages: [],
      taches: [],
      abonnementInfo: null,
      chatOuvert: false,
      projetChatActuel: null,
      nouveauMessage: '',
      formMembre: { projetId: '', email: '', role: 'MEMBRE' },
      formTache: { projetId: '', titre: '', description: '', emailMembre: '', priorite: 'MOYENNE' },
      filtreTacheProjet: '',
      filtreTacheStatut: '',
      filtreTachePriorite: '',
      erreurBackend: null
    }
  },
  computed: {
    utilisateur() { return useAuthStore().user },
    totalMembres() { return this.mesProjets.reduce((t, p) => t + (p.nombreMembres || 0), 0) },
    projetsActifs() { return this.mesProjets.filter(p => p.statut === 'ACTIF').length },
    projetsAvecEquipes() { return this.mesProjets.filter(p => (p.nombreMembres || 0) > 1).length },
    tachesEnValidation() { return this.taches.filter(t => t.statut === 'EN_ATTENTE_VALIDATION').length },
    tachesUrgentes() { return this.taches.filter(t => t.priorite === 'HAUTE' && t.statut !== 'TERMINE').length },
    montantTotal() { return this.mesFactures.reduce((sum, f) => sum + (f.montant_ht + f.tva), 0).toFixed(2) },
    abonnementExpireSoon() {
      if (!this.abonnementInfo?.date_fin) return false
      const diff = new Date(this.abonnementInfo.date_fin) - new Date()
      return diff < (7 * 24 * 60 * 60 * 1000) // 7 jours
    },
    joursRestants() {
      if (!this.abonnementInfo?.date_fin) return 0
      const diff = new Date(this.abonnementInfo.date_fin) - new Date()
      return Math.ceil(diff / (24 * 60 * 60 * 1000))
    },
    tachesFiltrees() {
      let arr = [...this.taches]
      if (this.filtreTacheProjet) arr = arr.filter(t => t.id_projet == this.filtreTacheProjet)
      if (this.filtreTacheStatut) arr = arr.filter(t => t.statut === this.filtreTacheStatut)
      if (this.filtreTachePriorite) arr = arr.filter(t => t.priorite === this.filtreTachePriorite)
      return arr
    }
  },
  async mounted() {
    const store = useAuthStore()
    if (!store.user && localStorage.getItem('user')) {
      try { store.user = JSON.parse(localStorage.getItem('user')) } catch {}
    }
    if (!store.user || store.user.role !== 'CHEF_PROJET') {
      this.erreurBackend = 'Accès réservé aux chefs de projet'
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
          this.chargerMesFactures(),
          this.chargerTaches(),
          this.chargerAbonnementInfo()
        ])
      } catch (e) {
        console.error(e)
        this.erreurBackend = 'Impossible de charger les données'
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

    async chargerMesFactures() {
      this.chargementFactures = true
      try {
        const r = await factureService.getFactures({ userId: this.utilisateur.id })
        this.mesFactures = Array.isArray(r.data) ? r.data : (Array.isArray(r.data?.content) ? r.data.content : [])
      } catch (e) {
        console.error(e)
        this.mesFactures = []
      } finally {
        this.chargementFactures = false
      }
    },

    async chargerTaches() {
      try {
        const r = await taskAPI.getTasksByChefProjet(this.utilisateur.id)
        this.taches = Array.isArray(r.data) ? r.data : []
      } catch (e) {
        console.error(e)
        this.taches = []
      }
    },

    async chargerAbonnementInfo() {
      try {
        const r = await abonnementAPI.getByUserId(this.utilisateur.id)
        this.abonnementInfo = r.data
      } catch (e) {
        console.error(e)
        this.abonnementInfo = null
      }
    },

    // Actions Projets
    async creerNouveauProjet() {
      const titre = prompt('Titre du projet :')
      if (!titre) return
      const description = prompt('Description :') || ''

      try {
        const r = await projectAPI.create({ titre, description, statut: 'ACTIF' })
        this.mesProjets.unshift(r.data)
        alert('Projet créé avec succès')
      } catch (e) {
        console.error(e)
        alert('Erreur lors de la création')
      }
    },

    ouvrirProjet(id) { this.$router.push(`/projet/${id}`) },
    gererMembres(projet) { this.onglet = 'equipes'; this.formMembre.projetId = projet.id },
    parametresProjet(projet) { alert(`Paramètres projet ${projet.titre} (modal à implémenter)`) },
    voirEquipeDetaille(id) { this.$router.push(`/projet/${id}/equipe`) },

    // Actions Équipes
    async ajouterMembreProjet() {
      if (!this.formMembre.projetId || !this.formMembre.email) return
      this.ajoutEnCours = true

      try {
        const { data: users } = await userAPI.searchUsers(this.formMembre.email)
        const u = (Array.isArray(users) ? users : []).find(x => x.email?.toLowerCase() === this.formMembre.email.toLowerCase())
        if (!u) {
          alert('Utilisateur introuvable avec cet email')
          return
        }

        await projectAPI.addMember(this.formMembre.projetId, u.id, this.formMembre.role)
        alert('Membre ajouté avec succès')
        this.formMembre = { projetId: '', email: '', role: 'MEMBRE' }
        await this.chargerMesProjets()
      } catch (e) {
        console.error(e)
        alert('Erreur lors de l\'ajout du membre')
      } finally {
        this.ajoutEnCours = false
      }
    },

    // Actions Tâches
    async creerTache() {
      if (!this.formTache.projetId || !this.formTache.titre || !this.formTache.emailMembre) return
      this.creationTacheEnCours = true

      try {
        const payload = {
          titre: this.formTache.titre,
          description: this.formTache.description,
          id_projet: this.formTache.projetId,
          assigneEmail: this.formTache.emailMembre,
          priorite: this.formTache.priorite
        }
        await taskAPI.create(payload)
        alert('Tâche créée et assignée avec succès')
        this.formTache = { projetId: '', titre: '', description: '', emailMembre: '', priorite: 'MOYENNE' }
        await this.chargerTaches()
      } catch (e) {
        console.error(e)
        alert('Erreur lors de la création de la tâche')
      } finally {
        this.creationTacheEnCours = false
      }
    },

    async validerTache(tache) {
      try {
        await taskAPI.updateStatus(tache.id, 'TERMINE')
        tache.statut = 'TERMINE'
        alert('Tâche validée avec succès')
      } catch (e) {
        console.error(e)
        alert('Erreur lors de la validation')
      }
    },

    async renvoyerBrouillon(tache) {
      try {
        await taskAPI.updateStatus(tache.id, 'BROUILLON')
        tache.statut = 'BROUILLON'
        alert('Tâche renvoyée en modification')
      } catch (e) {
        console.error(e)
        alert('Erreur')
      }
    },

    async annulerTache(tache) {
      if (!confirm('Annuler définitivement cette tâche ?')) return
      try {
        await taskAPI.updateStatus(tache.id, 'ANNULE')
        tache.statut = 'ANNULE'
        alert('Tâche annulée')
      } catch (e) {
        console.error(e)
        alert('Erreur')
      }
    },

    // Chat
    async ouvrirChat(projet) {
      this.projetChatActuel = projet
      this.chatOuvert = true
      await this.chargerMessages(projet.id)
    },

    async chargerMessages(projetId) {
      try {
        const r = await messagesAPI.getByProjet(projetId)
        this.messages = Array.isArray(r.data) ? r.data : []
      } catch (e) {
        console.error(e)
        this.messages = []
      }
    },

    async envoyerMessage() {
      if (!this.nouveauMessage.trim() || !this.projetChatActuel) return
      this.envoyantMessage = true

      try {
        const r = await messagesAPI.send(this.projetChatActuel.id, {
          contenu: this.nouveauMessage,
          type: 'TEXT'
        })
        this.messages.push(r.data)
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
      this.nouveauMessage = ''
      this.messages = []
    },

    estMonMessage(m) { return m.utilisateur_id === this.utilisateur.id },
    getMessageClass(m) {
      return this.estMonMessage(m) ? 'bg-success text-white ms-auto' : 'bg-white border shadow-sm'
    },

    // Facturation
    async telechargerFacture(id) {
      try {
        await factureService.telechargerPDF(id)
      } catch (e) {
        console.error(e)
        alert('Erreur téléchargement')
      }
    },

    telechargerToutesFactures() { alert('Téléchargement groupé (à implémenter)') },
    gererAbonnement() { this.$router.push('/abonnement') },
    renouvelerAbonnement() { this.$router.push('/abonnement/renouveler') },

    // Profil
    async sauvegarderProfil() {
      this.sauvegardeProfil = true
      try {
        await userAPI.updateProfile(this.utilisateur.id, {
          nom: this.utilisateur.nom,
          prenom: this.utilisateur.prenom,
          adresse: this.utilisateur.adresse
        })
        alert('Profil mis à jour avec succès')
      } catch (e) {
        console.error(e)
        alert('Erreur lors de la sauvegarde')
      } finally {
        this.sauvegardeProfil = false
      }
    },

    // Utilitaires
    getProjetBadgeClass(s) {
      const classes = {
        'ACTIF': 'bg-success',
        'TERMINE': 'bg-secondary',
        'EN_PAUSE': 'bg-warning text-dark'
      }
      return classes[s] || 'bg-primary'
    },

    getPriorityBadgeClass(p) {
      const classes = {
        'HAUTE': 'bg-danger',
        'MOYENNE': 'bg-warning text-dark',
        'BASSE': 'bg-info'
      }
      return classes[p] || 'bg-secondary'
    },

    getTacheStatusBadge(s) {
      const classes = {
        'BROUILLON': 'bg-secondary',
        'EN_ATTENTE_VALIDATION': 'bg-warning text-dark',
        'TERMINE': 'bg-success',
        'ANNULE': 'bg-danger'
      }
      return classes[s] || 'bg-secondary'
    },

    getTachesCount(projetId) {
      return this.taches.filter(t => t.id_projet == projetId).length
    },

    getProgressionProjet(projetId) {
      const taches = this.taches.filter(t => t.id_projet == projetId)
      if (taches.length === 0) return 0
      const terminees = taches.filter(t => t.statut === 'TERMINE').length
      return Math.round((terminees / taches.length) * 100)
    },

    getProjetNom(id) {
      const p = this.mesProjets.find(x => x.id == id)
      return p ? p.titre : 'Projet inconnu'
    },

    getAssigneNom(id) {
      // À connecter avec l'API pour récupérer les noms
      return `Membre #${id}`
    },

    formatDate(date) {
      return new Date(date).toLocaleDateString('fr-FR')
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
.premium-header {
  background: linear-gradient(135deg, #80661a, #ffeb3b);
  border-radius: 12px;
  padding: 20px;
  color: #000;
}

.metric-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.metric-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,.15);
}

.projet-card {
  transition: all 0.3s ease;
}

.projet-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0,0,0,.15);
}

.card {
  border-radius: 12px;
  overflow: hidden;
}

.bg-gradient-primary {
  background: linear-gradient(135deg, #007bff, #0056b3);
}

.bg-gradient-success {
  background: linear-gradient(135deg, #28a745, #1e7e34);
}

.bg-gradient-light {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
}

.nav-pills .nav-link {
  border-radius: 8px;
  margin: 0 2px;
  transition: all 0.2s ease;
}

.nav-pills .nav-link.active {
  background: linear-gradient(135deg, #28a745, #20c997);
  transform: translateY(-1px);
}

.metric-small {
  font-size: 0.875rem;
  color: #6c757d;
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
}

.btn-group .btn {
  border-radius: 6px;
  margin: 0 1px;
}

.abonnement-status {
  text-align: right;
}

.modal {
  backdrop-filter: blur(5px);
}

.table th {
  border-top: none;
  font-weight: 600;
  font-size: 0.875rem;
}
</style>
