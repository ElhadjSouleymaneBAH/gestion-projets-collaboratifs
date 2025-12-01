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
import java.util.stream.Collectors;

/**
 * Controller REST pour la gestion des tâches (F7)
 *
 * @author Elhadj Souleymane BAH
 * @version 3.1 - CORRECTION FINALE + DEBUG
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
                List.of("URGENTE", "HAUTE", "NORMALE", "BASSE")
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

    // Tâches liées aux projets d'un chef de projet
    @GetMapping("/chef/{idChef}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<TacheDTO>> obtenirTachesParChef(@PathVariable Long idChef) {
        try {
            System.out.println(" [TacheController] Récupération des tâches pour le chef " + idChef);

            List<TacheDTO> taches = tacheService.obtenirTachesParChefDeProjet(idChef);

            System.out.println(" [TacheController] " + taches.size() + " tâches trouvées");

            // Debug: Afficher les détails des tâches
            taches.forEach(t -> {
                System.out.println("    Tâche #" + t.getId() +
                        " - idProjet=" + t.getIdProjet() +
                        " - titre='" + t.getTitre() + "'" +
                        " - statut=" + t.getStatut());
            });

            // Vérifier si des tâches ont un idProjet null
            long tachesSansProjet = taches.stream()
                    .filter(t -> t.getIdProjet() == null)
                    .count();

            if (tachesSansProjet > 0) {
                System.err.println(" [TacheController] " + tachesSansProjet +
                        " tâches SANS idProjet détectées !");
            }

            return ResponseEntity.ok(taches);
        } catch (Exception e) {
            System.err.println(" [TacheController] Erreur lors de la récupération des tâches: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- MODIFIER TÂCHE ----------
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> modifierTache(@PathVariable Long id,
                                                  @Valid @RequestBody TacheDTO tacheDTO,
                                                  Authentication authentication) {
        try {
            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);

            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) return ResponseEntity.notFound().build();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !utilisateurService.peutAccederAuProjet(idUser, tacheExistante.get().getIdProjet())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            TacheDTO tacheModifiee = tacheService.modifierTache(id, tacheDTO);
            return ResponseEntity.ok(tacheModifiee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- CHANGER STATUT ----------
    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> changerStatutTache(@PathVariable Long id,
                                                       @RequestBody Map<String, String> body,
                                                       Authentication authentication) {
        try {
            System.out.println(" [API] Changement statut tâche " + id);

            if (!body.containsKey("statut")) {
                System.err.println(" [API] Paramètre 'statut' manquant");
                return ResponseEntity.badRequest().build();
            }

            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);

            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) {
                System.err.println(" [API] Tâche " + id + " introuvable");
                return ResponseEntity.notFound().build();
            }

            TacheDTO tache = tacheExistante.get();

            // Vérification étendue des permissions
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));
            boolean isChefProjet = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("CHEF_PROJET"));
            boolean estAssigneALaTache = idUser.equals(tache.getIdAssigne());
            boolean peutAccederProjet = utilisateurService.peutAccederAuProjet(idUser, tache.getIdProjet());

            if (!isAdmin && !estAssigneALaTache && !(isChefProjet && peutAccederProjet)) {
                System.err.println(" [API] Accès refusé pour utilisateur " + idUser +
                        " (assigné=" + estAssigneALaTache + ", accèsProjet=" + peutAccederProjet + ")");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            StatutTache nouveauStatut = StatutTache.valueOf(body.get("statut"));

            if (!isAdmin && !isChefProjet && estAssigneALaTache) {
                StatutTache statutActuel = tache.getStatut();
                if (!(statutActuel == StatutTache.BROUILLON && nouveauStatut == StatutTache.EN_ATTENTE_VALIDATION)) {
                    System.err.println(" [API] Un membre ne peut que soumettre une tâche en brouillon");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            System.out.println(" [API] Nouveau statut: " + nouveauStatut);

            TacheDTO tacheUpdated = tacheService.changerStatutTache(id, nouveauStatut);

            // Notification automatique (F13)
            notificationService.notifierChangementStatutTache(tacheUpdated, nouveauStatut);

            System.out.println(" [API] Tâche " + id + " mise à jour avec statut " + nouveauStatut);
            return ResponseEntity.ok(tacheUpdated);

        } catch (IllegalArgumentException e) {
            System.err.println(" [API] Statut invalide: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            System.err.println(" [API] Erreur runtime: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println(" [API] Erreur serveur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- DÉPLACER TÂCHE KANBAN (DRAG & DROP) ----------
    @PutMapping("/{id}/deplacer")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<TacheDTO> deplacerTacheKanban(@PathVariable Long id,
                                                        @RequestBody Map<String, Object> body,
                                                        Authentication authentication) {
        try {
            System.out.println(" [KANBAN] Déplacement tâche " + id);

            if (!body.containsKey("colonneDestination")) {
                return ResponseEntity.badRequest().build();
            }

            String colonneDestination = (String) body.get("colonneDestination");
            String email = authentication.getName();
            Long idUser = utilisateurService.obtenirIdParEmail(email);

            Optional<TacheDTO> tacheExistante = tacheService.obtenirTacheParId(id);
            if (tacheExistante.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TacheDTO tache = tacheExistante.get();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));
            boolean isChefProjet = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("CHEF_PROJET"));
            boolean estAssigneALaTache = idUser.equals(tache.getIdAssigne());
            boolean peutAccederProjet = utilisateurService.peutAccederAuProjet(idUser, tache.getIdProjet());

            if (!isAdmin && !peutAccederProjet) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            StatutTache nouveauStatut = determinerStatutDepuisColonne(colonneDestination);
            if (nouveauStatut == null) {
                return ResponseEntity.badRequest().build();
            }

            StatutTache statutActuel = tache.getStatut();

            if (nouveauStatut == StatutTache.TERMINE && !isAdmin && !isChefProjet) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (!isAdmin && !isChefProjet && estAssigneALaTache) {
                if (!(statutActuel == StatutTache.BROUILLON && nouveauStatut == StatutTache.EN_ATTENTE_VALIDATION)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            TacheDTO tacheUpdated = tacheService.deplacerTacheKanban(id, colonneDestination, nouveauStatut);
            notificationService.notifierChangementStatutTache(tacheUpdated, nouveauStatut);

            return ResponseEntity.ok(tacheUpdated);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private StatutTache determinerStatutDepuisColonne(String nomColonne) {
        if (nomColonne == null) return null;
        String nom = nomColonne.toLowerCase();

        if (nom.contains("faire")) return StatutTache.BROUILLON;
        if (nom.contains("cours") || nom.contains("attente")) return StatutTache.EN_ATTENTE_VALIDATION;
        if (nom.contains("termin")) return StatutTache.TERMINE;
        return null;
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

            // Notification automatique
            notificationService.notifierChangementStatutTache(tache, StatutTache.ANNULE);

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