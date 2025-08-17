package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.enums.StatutTache;
import be.iccbxl.gestionprojets.service.TacheService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller REST pour la gestion des tâches.
 *
 * Implémente la fonctionnalité F7 du cahier des charges :
 * - F7 : Gérer les tâches (Chef de Projet, Membre)
 * - Utilisateurs : Chef de Projet, Membre
 * - Importance : 5/5
 * - Contraintes : Tâches assignées uniquement (pour les membres)
 *
 * CORRIGÉ : hasAuthority() au lieu de hasRole() pour compatibilité avec JwtFilter
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.1
 * @see "Cahier des charges - Fonctionnalité F7"
 */
@RestController
@RequestMapping("/api/taches")
@CrossOrigin(origins = "*")
public class TacheController {

    private final TacheService tacheService;
    private final UtilisateurService utilisateurService;

    public TacheController(TacheService tacheService, UtilisateurService utilisateurService) {
        this.tacheService = tacheService;
        this.utilisateurService = utilisateurService;
    }

    // ========== F7 : CRÉER UNE TÂCHE ==========

    /**
     * Création d'une tâche.
     *
     * Fonctionnalité F7 : Gérer les tâches
     * Utilisateurs : Chef de Projet, Membre
     */
    @PostMapping
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> creerTache(@Valid @RequestBody TacheDTO tacheDTO,
                                               Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Création tâche par " + authentication.getName());

            // Vérifier que l'utilisateur a accès au projet
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

            if (!utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, tacheDTO.getIdProjet())) {
                System.out.println("ERROR: [F7] Utilisateur n'a pas accès au projet " + tacheDTO.getIdProjet());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            TacheDTO tacheCreee = tacheService.creerTache(tacheDTO);
            System.out.println("SUCCESS: [F7] Tâche créée avec ID: " + tacheCreee.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(tacheCreee);
        } catch (RuntimeException e) {
            System.out.println("ERROR: [F7] Erreur création tâche: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur interne: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : CONSULTATION DES TÂCHES ==========

    /**
     * Obtenir toutes les tâches (accès limité selon le rôle).
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirToutesLesTaches() {
        try {
            System.out.println("DEBUG: [F7] Consultation toutes tâches (ADMIN)");
            List<TacheDTO> taches = tacheService.obtenirToutesLesTaches();
            return ResponseEntity.ok(taches);
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur consultation tâches: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir une tâche par ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> obtenirTacheParId(@PathVariable Long id,
                                                      Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Consultation tâche ID: " + id);

            Optional<TacheDTO> tache = tacheService.obtenirTacheParId(id);
            if (tache.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Vérifier que l'utilisateur a accès à cette tâche
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, tache.get().getIdProjet())) {
                System.out.println("ERROR: [F7] Accès refusé à la tâche " + id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(tache.get());
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur consultation tâche: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir les tâches d'un projet.
     *
     * Fonctionnalité F7 : Consultation des tâches par projet
     */
    @GetMapping("/projet/{idProjet}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirTachesParProjet(@PathVariable Long idProjet,
                                                                 Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Consultation tâches projet ID: " + idProjet);

            // Vérifier que l'utilisateur a accès au projet
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, idProjet)) {
                System.out.println("ERROR: [F7] Accès refusé au projet " + idProjet);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<TacheDTO> taches = tacheService.obtenirTachesParProjet(idProjet);
            System.out.println("SUCCESS: [F7] " + taches.size() + " tâches trouvées pour le projet " + idProjet);

            return ResponseEntity.ok(taches);
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur consultation tâches projet: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir les tâches assignées à un utilisateur.
     *
     * Fonctionnalité F7 : Contrainte "Tâches assignées uniquement"
     */
    @GetMapping("/utilisateur/{idUtilisateur}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirTachesParUtilisateur(@PathVariable Long idUtilisateur,
                                                                      Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Consultation tâches utilisateur ID: " + idUtilisateur);

            // Vérifier que l'utilisateur consulte ses propres tâches (sauf admin)
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin) {
                String emailConnecte = authentication.getName();
                Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

                if (!idUtilisateur.equals(idUtilisateurConnecte)) {
                    System.out.println("ERROR: [F7] Tentative d'accès aux tâches d'un autre utilisateur");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            List<TacheDTO> taches = tacheService.obtenirTachesParUtilisateur(idUtilisateur);
            System.out.println("SUCCESS: [F7] " + taches.size() + " tâches assignées à l'utilisateur " + idUtilisateur);

            return ResponseEntity.ok(taches);
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur consultation tâches utilisateur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir mes tâches assignées.
     *
     * Endpoint pratique pour l'utilisateur connecté.
     */
    @GetMapping("/mes-taches")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirMesTaches(Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Consultation mes tâches pour " + authentication.getName());

            String emailConnecte = authentication.getName();
            Long idUtilisateur = utilisateurService.obtenirIdParEmail(emailConnecte);

            List<TacheDTO> taches = tacheService.obtenirTachesParUtilisateur(idUtilisateur);
            System.out.println("SUCCESS: [F7] " + taches.size() + " tâches assignées à " + emailConnecte);

            return ResponseEntity.ok(taches);
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur consultation mes tâches: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : MODIFICATION DES TÂCHES ==========

    /**
     * Modification d'une tâche.
     *
     * Fonctionnalité F7 : Gérer les tâches
     * Contraintes : Les MEMBRES ne peuvent modifier que leurs tâches assignées
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> modifierTache(@PathVariable Long id,
                                                  @Valid @RequestBody TacheDTO tacheDTO,
                                                  Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Modification tâche ID: " + id + " par " + authentication.getName());

            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));
            boolean estChefProjet = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("CHEF_PROJET"));

            // Contrainte F7 : MEMBRE ne peut modifier que ses tâches assignées
            if (!estAdmin && !estChefProjet) {
                Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
                Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);

                if (tacheExistante.isEmpty()) {
                    System.out.println("ERROR: [F7] Tâche non trouvée: " + id);
                    return ResponseEntity.notFound().build();
                }

                // Vérifier que la tâche est assignée à l'utilisateur connecté
                if (!idUtilisateurConnecte.equals(tacheExistante.get().getIdAssigne())) {
                    System.out.println("ERROR: [F7] MEMBRE ne peut modifier que ses tâches assignées");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            TacheDTO tacheModifiee = tacheService.modifierTache(id, tacheDTO);
            System.out.println("SUCCESS: [F7] Tâche " + id + " modifiée avec succès");

            return ResponseEntity.ok(tacheModifiee);
        } catch (RuntimeException e) {
            System.out.println("ERROR: [F7] Erreur modification tâche: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur interne modification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : ASSIGNATION DES TÂCHES ==========

    /**
     * Assigner une tâche à un utilisateur.
     *
     * Fonctionnalité F7 : Assigner des tâches (Chef de Projet uniquement)
     */
    @PutMapping("/{id}/assigner/{utilisateurId}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> assignerTache(@PathVariable Long id,
                                                  @PathVariable Long utilisateurId,
                                                  Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Assignation tâche " + id + " à utilisateur " + utilisateurId);

            // Récupérer la tâche existante
            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) {
                System.out.println("ERROR: [F7] Tâche non trouvée pour assignation: " + id);
                return ResponseEntity.notFound().build();
            }

            // Vérifier que l'utilisateur cible existe
            if (!utilisateurService.existeParId(utilisateurId)) {
                System.out.println("ERROR: [F7] Utilisateur cible non trouvé: " + utilisateurId);
                return ResponseEntity.badRequest().build();
            }

            // Modifier l'assignation
            TacheDTO tacheDTO = tacheExistante.get();
            tacheDTO.setIdAssigne(utilisateurId);

            TacheDTO tacheAssignee = tacheService.modifierTache(id, tacheDTO);
            System.out.println("SUCCESS: [F7] Tâche " + id + " assignée à l'utilisateur " + utilisateurId);

            return ResponseEntity.ok(tacheAssignee);
        } catch (RuntimeException e) {
            System.out.println("ERROR: [F7] Erreur assignation tâche: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur interne assignation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Désassigner une tâche.
     */
    @PutMapping("/{id}/desassigner")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> desassignerTache(@PathVariable Long id,
                                                     Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Désassignation tâche " + id);

            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) {
                System.out.println("ERROR: [F7] Tâche non trouvée pour désassignation: " + id);
                return ResponseEntity.notFound().build();
            }

            TacheDTO tacheDTO = tacheExistante.get();
            tacheDTO.setIdAssigne(null);

            TacheDTO tacheDesassignee = tacheService.modifierTache(id, tacheDTO);
            System.out.println("SUCCESS: [F7] Tâche " + id + " désassignée");

            return ResponseEntity.ok(tacheDesassignee);
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur désassignation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : GESTION DES STATUTS ==========

    /**
     * Changer le statut d'une tâche.
     *
     * Fonctionnalité F7 : Gestion des statuts selon le diagramme d'état
     * Statuts : BROUILLON, EN_ATTENTE_VALIDATION, TERMINE, ANNULE
     */
    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> changerStatut(@PathVariable Long id,
                                                  @RequestParam StatutTache nouveauStatut,
                                                  Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Changement statut tâche " + id + " vers " + nouveauStatut);

            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));
            boolean estChefProjet = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("CHEF_PROJET"));

            // Contraintes selon le diagramme d'état
            if (!estAdmin) {
                Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
                if (tacheExistante.isEmpty()) {
                    System.out.println("ERROR: [F7] Tâche non trouvée pour changement statut: " + id);
                    return ResponseEntity.notFound().build();
                }

                // MEMBRE : peut seulement "Compléter" (passer en EN_ATTENTE_VALIDATION)
                if (!estChefProjet) {
                    Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

                    // Vérifier que la tâche est assignée à l'utilisateur
                    if (!idUtilisateurConnecte.equals(tacheExistante.get().getIdAssigne())) {
                        System.out.println("ERROR: [F7] MEMBRE ne peut modifier que ses tâches assignées");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }

                    // MEMBRE peut seulement "Compléter"
                    if (nouveauStatut != StatutTache.EN_ATTENTE_VALIDATION) {
                        System.out.println("ERROR: [F7] MEMBRE peut seulement passer en EN_ATTENTE_VALIDATION");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
                // CHEF_PROJET : peut Valider, Modifier, Annuler
            }

            TacheDTO tacheModifiee = tacheService.changerStatutTache(id, nouveauStatut);
            System.out.println("SUCCESS: [F7] Statut tâche " + id + " changé vers " + nouveauStatut);

            return ResponseEntity.ok(tacheModifiee);
        } catch (RuntimeException e) {
            System.out.println("ERROR: [F7] Erreur changement statut: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur interne changement statut: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : SUPPRESSION DES TÂCHES ==========

    /**
     * Suppression d'une tâche.
     *
     * Fonctionnalité F7 : Supprimer une tâche (Chef de Projet uniquement)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerTache(@PathVariable Long id,
                                               Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Suppression tâche " + id + " par " + authentication.getName());

            // Vérifier que la tâche existe avant suppression
            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) {
                System.out.println("ERROR: [F7] Tâche non trouvée pour suppression: " + id);
                return ResponseEntity.notFound().build();
            }

            // Vérifier que le chef de projet peut supprimer cette tâche (son projet)
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, tacheExistante.get().getIdProjet())) {
                System.out.println("ERROR: [F7] Chef de projet ne peut supprimer que ses tâches de projet");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            tacheService.supprimerTache(id);
            System.out.println("SUCCESS: [F7] Tâche " + id + " supprimée avec succès");

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.out.println("ERROR: [F7] Erreur suppression tâche: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur interne suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : STATISTIQUES & MONITORING ==========

    /**
     * Obtenir les statistiques des tâches d'un projet.
     * Utile pour le dashboard des Chefs de Projet.
     */
    @GetMapping("/projet/{idProjet}/statistiques")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> obtenirStatistiquesProjet(@PathVariable Long idProjet,
                                                            Authentication authentication) {
        try {
            System.out.println("DEBUG: [F7] Statistiques projet " + idProjet);

            // Vérifier accès au projet
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, idProjet)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<TacheDTO> taches = tacheService.obtenirTachesParProjet(idProjet);

            // Calcul des statistiques
            long totalTaches = taches.size();
            long tachesTerminees = taches.stream().filter(t -> t.getStatut() == StatutTache.TERMINE).count();
            long tachesEnCours = taches.stream().filter(t ->
                    t.getStatut() == StatutTache.BROUILLON || t.getStatut() == StatutTache.EN_ATTENTE_VALIDATION).count();

            var stats = new Object() {
                public final long total = totalTaches;
                public final long terminees = tachesTerminees;
                public final long enCours = tachesEnCours;
                public final double pourcentageCompletion = totalTaches > 0 ? (double) tachesTerminees / totalTaches * 100 : 0;
            };

            System.out.println("SUCCESS: [F7] Statistiques calculées pour projet " + idProjet);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            System.out.println("ERROR: [F7] Erreur calcul statistiques: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}