package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.TacheDTO;
import be.iccbxl.gestionprojets.enums.StatutTache;
import be.iccbxl.gestionprojets.service.NotificationService;
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
 * Controller REST pour la gestion des tâches (F7)
 * Version complète corrigée — conforme au cahier des charges.
 */
@RestController
@RequestMapping("/api/taches")
public class TacheController {

    private final TacheService tacheService;
    private final UtilisateurService utilisateurService;
    private final NotificationService notificationService;

    public TacheController(TacheService tacheService,
                           UtilisateurService utilisateurService,
                           NotificationService notificationService) {
        this.tacheService = tacheService;
        this.utilisateurService = utilisateurService;
        this.notificationService = notificationService;
    }

    // ---------- META ----------
    @GetMapping("/statuts")
    public ResponseEntity<List<String>> listerStatutsTache() {
        return ResponseEntity.ok(
                List.of("BROUILLON", "EN_ATTENTE_VALIDATION", "TERMINE", "ANNULE")
        );
    }

    @GetMapping("/priorites")
    public ResponseEntity<List<String>> listerPrioritesTache() {
        return ResponseEntity.ok(
                List.of("HAUTE", "NORMALE", "BASSE")
        );
    }

    // ---------- CRÉER ----------
    @PostMapping
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> creerTache(@Valid @RequestBody TacheDTO tacheDTO,
                                               Authentication authentication) {
        try {
            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);

            if (!utilisateurService.peutAccederAuProjet(idUser, tacheDTO.getIdProjet())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            TacheDTO tacheCree = tacheService.creerTache(tacheDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(tacheCree);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- CONSULTER ----------
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirToutesLesTaches() {
        try {
            return ResponseEntity.ok(tacheService.obtenirToutesLesTaches());
        } catch (Exception e) {
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

            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !utilisateurService.peutAccederAuProjet(idUser, tache.get().getIdProjet())) {
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
            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !utilisateurService.peutAccederAuProjet(idUser, idProjet)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(tacheService.obtenirTachesParProjet(idProjet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- NOUVEAUX ENDPOINTS CONFORMES AU CAHIER DES CHARGES ----------

    // Tâches assignées à un utilisateur (membre)
    @GetMapping("/utilisateur/{idUtilisateur}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirTachesParUtilisateur(@PathVariable Long idUtilisateur) {
        try {
            List<TacheDTO> taches = tacheService.obtenirTachesParUtilisateur(idUtilisateur);
            return ResponseEntity.ok(taches);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Tâches liées aux projets d’un chef de projet
    @GetMapping("/chef/{idChef}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirTachesParChef(@PathVariable Long idChef) {
        try {
            List<TacheDTO> taches = tacheService.obtenirTachesParChefDeProjet(idChef);
            return ResponseEntity.ok(taches);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- ASSIGNATION ----------
    @PutMapping("/{id}/assigner")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> assignerTache(@PathVariable Long id,
                                                  @RequestBody Map<String, Object> body) {
        try {
            if (!body.containsKey("idAssigne")) {
                return ResponseEntity.badRequest().build();
            }

            Long idAssigne = Long.valueOf(body.get("idAssigne").toString());
            TacheDTO tacheAssignee = tacheService.assignerTache(id, idAssigne);

            notificationService.notifierTacheAssignee(
                    idAssigne,
                    tacheAssignee.getTitre(),
                    tacheAssignee.getNomProjet()
            );

            return ResponseEntity.ok(tacheAssignee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- ANNULER (ADMIN) ----------
    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> annulerTache(@PathVariable Long id) {
        try {
            TacheDTO tache = tacheService.annulerParAdmin(id);
            return ResponseEntity.ok(tache);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- SUPPRIMER ----------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerTache(@PathVariable Long id,
                                               Authentication authentication) {
        try {
            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) return ResponseEntity.notFound().build();

            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !utilisateurService.peutAccederAuProjet(idUser, tacheExistante.get().getIdProjet())) {
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

    // ---------- STATISTIQUES ----------
    @GetMapping("/projet/{idProjet}/statistiques")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> obtenirStatistiquesProjet(@PathVariable Long idProjet,
                                                            Authentication authentication) {
        try {
            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !utilisateurService.peutAccederAuProjet(idUser, idProjet)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<TacheDTO> taches = tacheService.obtenirTachesParProjet(idProjet);
            long total = taches.size();
            long terminees = taches.stream().filter(t -> t.getStatut() == StatutTache.TERMINE).count();
            long enCours = taches.stream().filter(t -> t.getStatut() == StatutTache.BROUILLON
                    || t.getStatut() == StatutTache.EN_ATTENTE_VALIDATION).count();

            var stats = Map.of(
                    "total", total,
                    "terminees", terminees,
                    "enCours", enCours,
                    "pourcentageCompletion", total > 0 ? (double) terminees / total * 100 : 0.0
            );

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
