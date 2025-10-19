package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.ProjetDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.service.ProjetService;
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

@RestController
@RequestMapping("/api/projets")
@CrossOrigin(
        origins = {
                "http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5177",
                "http://127.0.0.1:5173", "http://127.0.0.1:5174", "http://127.0.0.1:5175", "http://127.0.0.1:5177"
        },
        allowCredentials = "true"
)
public class ProjetController {

    private final ProjetService projetService;
    private final UtilisateurService utilisateurService;

    public ProjetController(ProjetService projetService, UtilisateurService utilisateurService) {
        this.projetService = projetService;
        this.utilisateurService = utilisateurService;
    }

    // ---------- MÉTADONNÉES PROJETS ----------
    @GetMapping("/statuts")
    public ResponseEntity<List<String>> listerStatutsProjet() {
        return ResponseEntity.ok(
                List.of("ACTIF", "SUSPENDU", "TERMINE", "ANNULE", "ARCHIVE")
        );
    }

    // ---------- ADMIN ----------
    @GetMapping("/admin/tous")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ProjetDTO>> obtenirTousProjetsAdmin(Authentication authentication) {
        try {
            List<ProjetDTO> projets = projetService.obtenirTousProjets();
            return ResponseEntity.ok(projets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- PUBLIC ----------
    @GetMapping("/publics")
    public ResponseEntity<List<ProjetDTO>> obtenirProjetsPublics() {
        try {
            List<ProjetDTO> projetsPublics = projetService.obtenirProjetsPublics();
            return ResponseEntity.ok(projetsPublics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- CRUD ----------
    @PostMapping
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> creerProjet(@Valid @RequestBody ProjetDTO projetDTO, Authentication authentication) {
        try {
            String emailCreateur = authentication.getName();
            ProjetDTO projetCree = projetService.creerProjet(projetDTO, emailCreateur);
            return ResponseEntity.status(HttpStatus.CREATED).body(projetCree);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VISITEUR') or hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ProjetDTO>> obtenirTousProjets() {
        try {
            return ResponseEntity.ok(projetService.obtenirTousProjets());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🔧 CORRIGÉ : Vérifier que l'utilisateur a accès au projet
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> obtenirProjetParId(@PathVariable Long id, Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            Optional<ProjetDTO> projetOpt = projetService.obtenirProjetParId(id);

            if (projetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ProjetDTO projet = projetOpt.get();

            // Vérification : ADMIN OU créateur OU membre du projet
            if (!estAdmin
                    && !projet.getIdCreateur().equals(idUtilisateurConnecte)
                    && !projetService.estMembreDuProjet(idUtilisateurConnecte, id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(projet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/utilisateur/{utilisateurId:\\d+}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ProjetDTO>> obtenirProjetsParUtilisateur(@PathVariable Long utilisateurId,
                                                                        Authentication authentication) {
        try {
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin) {
                String emailConnecte = authentication.getName();
                Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
                if (!utilisateurId.equals(idUtilisateurConnecte)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            return ResponseEntity.ok(projetService.obtenirProjetsParUtilisateur(utilisateurId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> modifierProjet(@PathVariable Long id,
                                                    @Valid @RequestBody ProjetDTO projetDTO,
                                                    Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(id, emailConnecte)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(projetService.modifierProjet(id, projetDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerProjet(@PathVariable Long id, Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(id, emailConnecte)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            projetService.supprimerProjet(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- MEMBRES ----------
    @PostMapping("/{projetId:\\d+}/membres/{utilisateurId:\\d+}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> ajouterMembreAuProjet(@PathVariable Long projetId,
                                                        @PathVariable Long utilisateurId,
                                                        Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Seul le créateur du projet peut ajouter des membres"));
            }

            if (!utilisateurService.existeParId(utilisateurId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Utilisateur à ajouter non trouvé"));
            }

            projetService.ajouterMembreAuProjet(projetId, utilisateurId);
            utilisateurService.promouvoirVersMembreParChefProjet(utilisateurId);
            return ResponseEntity.ok(Map.of("message", "Membre ajouté avec succès au projet"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/{projetId:\\d+}/membres")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> obtenirMembresProjet(@PathVariable Long projetId,
                                                       Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin &&
                    !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte) &&
                    !projetService.estMembreDuProjet(idUtilisateurConnecte, projetId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Accès refusé à ce projet"));
            }

            List<UtilisateurDTO> membres = projetService.obtenirMembresProjet(projetId);
            return ResponseEntity.ok(membres);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/{projetId:\\d+}/utilisateurs-disponibles")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> obtenirUtilisateursDisponibles(@PathVariable Long projetId,
                                                                 Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Seul le créateur du projet peut voir les utilisateurs disponibles"));
            }

            List<UtilisateurDTO> utilisateursDisponibles = utilisateurService.obtenirUtilisateursDisponiblesPourProjet(projetId);
            return ResponseEntity.ok(utilisateursDisponibles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }

    @DeleteMapping("/{projetId:\\d+}/membres/{utilisateurId:\\d+}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> retirerMembreDuProjet(@PathVariable Long projetId,
                                                        @PathVariable Long utilisateurId,
                                                        Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Seul le créateur du projet peut retirer des membres"));
            }

            if (utilisateurService.utilisateurATachesEnCours(utilisateurId)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Impossible de retirer: l'utilisateur a des tâches en cours"));
            }

            projetService.retirerMembreDuProjet(projetId, utilisateurId);
            return ResponseEntity.ok(Map.of("message", "Membre retiré avec succès du projet"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/{projetId:\\d+}/membres/{utilisateurId:\\d+}/statut")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> verifierStatutMembre(@PathVariable Long projetId,
                                                       @PathVariable Long utilisateurId) {
        try {
            boolean estMembre = projetService.estMembreDuProjet(utilisateurId, projetId);
            var statut = Map.of("estMembre", estMembre, "projetId", projetId, "utilisateurId", utilisateurId);
            return ResponseEntity.ok(statut);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }
}