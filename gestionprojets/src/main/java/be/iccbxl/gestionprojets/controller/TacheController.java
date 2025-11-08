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
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST pour la gestion des tâches (F7).
 * Version 2.0 - Ajout endpoint assignation avec body JSON
 */
@RestController
@RequestMapping("/api/taches")
public class TacheController {

    private final TacheService tacheService;
    private final UtilisateurService utilisateurService;

    public TacheController(TacheService tacheService, UtilisateurService utilisateurService) {
        this.tacheService = tacheService;
        this.utilisateurService = utilisateurService;
    }

    // ---------- META POUR L'UI ----------
    @GetMapping("/statuts")
    public ResponseEntity<List<String>> listerStatutsTache() {
        return ResponseEntity.ok(
                List.of("BROUILLON", "EN_ATTENTE_VALIDATION", "TERMINE", "ANNULE")
        );
    }

    @GetMapping("/priorites")
    public ResponseEntity<List<String>> listerPrioritesTache() {
        return ResponseEntity.ok(
                List.of("HAUTE", "MOYENNE", "BASSE")
        );
    }

    // ========== F7 : CRÉER UNE TÂCHE ==========
    @PostMapping
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> creerTache(@Valid @RequestBody TacheDTO tacheDTO,
                                               Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

            if (!utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, tacheDTO.getIdProjet())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            TacheDTO tacheCreee = tacheService.creerTache(tacheDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(tacheCreee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : CONSULTATION ==========
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirToutesLesTaches() {
        try {
            return ResponseEntity.ok(tacheService.obtenirToutesLesTaches());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> getAllTachesAdmin() {
        try {
            List<TacheDTO> taches = tacheService.obtenirToutesLesTaches();
            System.out.println("DEBUG: [F7-ADMIN] Retour de " + taches.size() + " tâches pour l'admin");
            return ResponseEntity.ok(taches);
        } catch (Exception e) {
            System.err.println("ERROR: [F7-ADMIN] Erreur récupération tâches: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> obtenirTacheParId(@PathVariable Long id,
                                                      Authentication authentication) {
        try {
            Optional<TacheDTO> tache = tacheService.obtenirTacheParId(id);
            if (tache.isEmpty()) return ResponseEntity.notFound().build();

            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, tache.get().getIdProjet())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(tache.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/projet/{idProjet}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirTachesParProjet(@PathVariable Long idProjet,
                                                                 Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, idProjet)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(tacheService.obtenirTachesParProjet(idProjet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/utilisateur/{idUtilisateur}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirTachesParUtilisateur(@PathVariable Long idUtilisateur,
                                                                      Authentication authentication) {
        try {
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin) {
                String emailConnecte = authentication.getName();
                Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
                if (!idUtilisateur.equals(idUtilisateurConnecte)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            return ResponseEntity.ok(tacheService.obtenirTachesParUtilisateur(idUtilisateur));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/mes-taches")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirMesTaches(Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            Long idUtilisateur = utilisateurService.obtenirIdParEmail(emailConnecte);
            return ResponseEntity.ok(tacheService.obtenirTachesParUtilisateur(idUtilisateur));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : MODIFICATION ==========
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> modifierTache(@PathVariable Long id,
                                                  @Valid @RequestBody TacheDTO tacheDTO,
                                                  Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));
            boolean estChefProjet = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("CHEF_PROJET"));

            if (!estAdmin && !estChefProjet) {
                Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
                Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
                if (tacheExistante.isEmpty()) return ResponseEntity.notFound().build();
                if (!idUtilisateurConnecte.equals(tacheExistante.get().getIdAssigne())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            return ResponseEntity.ok(tacheService.modifierTache(id, tacheDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== ASSIGNATION AVEC BODY JSON ==========
    /**
     * Assigner une tâche à un membre via body JSON
     * PUT /api/taches/{id}/assigner
     *
     * @param id ID de la tâche
     * @param body Payload JSON contenant idAssigne
     * @return TacheDTO enrichi avec les infos de l'assigné
     */
    @PutMapping("/{id}/assigner")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> assignerTacheJSON(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            System.out.println("DEBUG: [F7] Requête assignation tâche " + id + " avec body: " + body);

            if (!body.containsKey("idAssigne")) {
                System.err.println("ERROR: [F7] Clé 'idAssigne' manquante dans le body");
                return ResponseEntity.badRequest().build();
            }

            Long idAssigne = Long.valueOf(body.get("idAssigne").toString());
            System.out.println("DEBUG: [F7] Assignation tâche " + id + " à utilisateur " + idAssigne);

            TacheDTO tacheAssignee = tacheService.assignerTache(id, idAssigne);
            System.out.println("SUCCESS: [F7] Tâche " + id + " assignée à " + tacheAssignee.getNomAssigne());

            return ResponseEntity.ok(tacheAssignee);
        } catch (RuntimeException e) {
            System.err.println("ERROR: [F7] Erreur assignation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("ERROR: [F7] Erreur serveur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : ASSIGNATION (PATH PARAM - LEGACY) ==========
    @PutMapping("/{id}/assigner/{utilisateurId}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> assignerTache(@PathVariable Long id,
                                                  @PathVariable Long utilisateurId) {
        try {
            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) return ResponseEntity.notFound().build();

            if (!utilisateurService.existeParId(utilisateurId)) {
                return ResponseEntity.badRequest().build();
            }

            TacheDTO tacheDTO = tacheExistante.get();
            tacheDTO.setIdAssigne(utilisateurId);
            return ResponseEntity.ok(tacheService.modifierTache(id, tacheDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/desassigner")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> desassignerTache(@PathVariable Long id) {
        try {
            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) return ResponseEntity.notFound().build();

            TacheDTO tacheDTO = tacheExistante.get();
            tacheDTO.setIdAssigne(null);
            return ResponseEntity.ok(tacheService.modifierTache(id, tacheDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7-ADMIN : ANNULER TÂCHE ==========
    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> annulerTache(@PathVariable Long id) {
        try {
            System.out.println("INFO: [F7-ADMIN] Requête annulation tâche ID: " + id);
            TacheDTO tache = tacheService.annulerParAdmin(id);
            return ResponseEntity.ok(tache);
        } catch (RuntimeException e) {
            System.err.println("ERROR: [F7-ADMIN] Tâche non trouvée: " + id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("ERROR: [F7-ADMIN] Erreur annulation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : GESTION DES STATUTS ==========
    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> changerStatutPUT(@PathVariable Long id,
                                                     @RequestParam StatutTache nouveauStatut,
                                                     Authentication authentication) {
        return changerStatutCommun(id, nouveauStatut, authentication);
    }

    public static class StatutPayload {
        public String statut;
        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> changerStatutPATCH(@PathVariable Long id,
                                                       @RequestBody StatutPayload body,
                                                       Authentication authentication) {
        try {
            if (body == null || body.statut == null) {
                return ResponseEntity.badRequest().build();
            }
            StatutTache cible = StatutTache.valueOf(body.statut);
            return changerStatutCommun(id, cible, authentication);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<TacheDTO> changerStatutCommun(Long id, StatutTache nouveauStatut, Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));
            boolean estChefProjet = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("CHEF_PROJET"));

            if (!estAdmin) {
                Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
                if (tacheExistante.isEmpty()) return ResponseEntity.notFound().build();

                if (!estChefProjet) {
                    Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
                    if (!idUtilisateurConnecte.equals(tacheExistante.get().getIdAssigne())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                    if (nouveauStatut != StatutTache.EN_ATTENTE_VALIDATION) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
            }

            TacheDTO tacheModifiee = tacheService.changerStatutTache(id, nouveauStatut);
            return ResponseEntity.ok(tacheModifiee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : SUPPRESSION ==========
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerTache(@PathVariable Long id,
                                               Authentication authentication) {
        try {
            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) return ResponseEntity.notFound().build();

            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, tacheExistante.get().getIdProjet())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            tacheService.supprimerTache(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F7 : STATISTIQUES ==========
    @GetMapping("/projet/{idProjet}/statistiques")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> obtenirStatistiquesProjet(@PathVariable Long idProjet,
                                                            Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !utilisateurService.peutAccederAuProjet(idUtilisateurConnecte, idProjet)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<TacheDTO> taches = tacheService.obtenirTachesParProjet(idProjet);
            long totalTaches = taches.size();
            long tachesTerminees = taches.stream().filter(t -> t.getStatut() == StatutTache.TERMINE).count();
            long tachesEnCours = taches.stream().filter(t ->
                    t.getStatut() == StatutTache.BROUILLON || t.getStatut() == StatutTache.EN_ATTENTE_VALIDATION).count();

            var stats = Map.of(
                    "total", totalTaches,
                    "terminees", tachesTerminees,
                    "enCours", tachesEnCours,
                    "pourcentageCompletion", totalTaches > 0 ? (double) tachesTerminees / totalTaches * 100 : 0.0
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}