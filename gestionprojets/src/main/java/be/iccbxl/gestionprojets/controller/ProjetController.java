package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.ProjetDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.service.ProjetService;
import be.iccbxl.gestionprojets.service.TranslationService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
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
    private final TranslationService translationService;

    public ProjetController(ProjetService projetService, UtilisateurService utilisateurService, TranslationService translationService) {
        this.projetService = projetService;
        this.utilisateurService = utilisateurService;
        this.translationService = translationService;
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
    public ResponseEntity<List<ProjetDTO>> obtenirTousProjetsAdmin(
            @RequestHeader(value = "Accept-Language", defaultValue = "fr") String acceptLanguage
    ) {
        try {
            List<ProjetDTO> projets = projetService.obtenirTousProjets();
            traduireListeProjets(projets, acceptLanguage);
            return ResponseEntity.ok(projets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- PUBLIC ----------
    @GetMapping("/publics")
    public ResponseEntity<List<ProjetDTO>> obtenirProjetsPublics(
            @RequestHeader(value = "Accept-Language", defaultValue = "fr") String acceptLanguage
    ) {
        try {
            List<ProjetDTO> projetsPublics = projetService.obtenirProjetsPublics();
            traduireListeProjets(projetsPublics, acceptLanguage);
            return ResponseEntity.ok(projetsPublics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ProjetDTO> obtenirProjetPublicParId(
            @PathVariable Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "fr") String acceptLanguage
    ) {
        try {
            Optional<ProjetDTO> projetOpt = projetService.obtenirProjetParId(id);
            if (projetOpt.isEmpty()) return ResponseEntity.notFound().build();

            ProjetDTO projet = projetOpt.get();
            if (!Boolean.TRUE.equals(projet.getPublique())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            traduireProjet(projet, acceptLanguage);
            return ResponseEntity.ok(projet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------- CRUD ----------
    @PostMapping
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> creerProjet(@Valid @RequestBody ProjetDTO projetDTO, Authentication authentication) {
        try {
            ProjetDTO projetCree = projetService.creerProjet(projetDTO, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(projetCree);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VISITEUR') or hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ProjetDTO>> obtenirTousProjets(
            @RequestHeader(value = "Accept-Language", defaultValue = "fr") String acceptLanguage
    ) {
        try {
            List<ProjetDTO> projets = projetService.obtenirTousProjets();
            traduireListeProjets(projets, acceptLanguage);
            return ResponseEntity.ok(projets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> obtenirProjetParId(
            @PathVariable Long id,
            Authentication authentication,
            @RequestHeader(value = "Accept-Language", defaultValue = "fr") String acceptLanguage
    ) {
        try {
            String email = authentication.getName();
            Long userId = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            Optional<ProjetDTO> projetOpt = projetService.obtenirProjetParId(id);
            if (projetOpt.isEmpty()) return ResponseEntity.notFound().build();

            ProjetDTO projet = projetOpt.get();

            if (!isAdmin && !projet.getIdCreateur().equals(userId)
                    && !projetService.estMembreDuProjet(userId, id))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            traduireProjet(projet, acceptLanguage);
            return ResponseEntity.ok(projet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/utilisateur/{utilisateurId:\\d+}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ProjetDTO>> obtenirProjetsParUtilisateur(
            @PathVariable Long utilisateurId,
            Authentication authentication,
            @RequestHeader(value = "Accept-Language", defaultValue = "fr") String acceptLanguage
    ) {
        try {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin) {
                String email = authentication.getName();
                Long userId = utilisateurService.obtenirIdParEmail(email);
                if (!utilisateurId.equals(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<ProjetDTO> projets = projetService.obtenirProjetsParUtilisateur(utilisateurId);
            traduireListeProjets(projets, acceptLanguage);
            return ResponseEntity.ok(projets);
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
            String email = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !projetService.utilisateurPeutModifierProjet(id, email))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

            return ResponseEntity.ok(projetService.modifierProjet(id, projetDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================================
    // ENDPOINT CORRIGÉ : DELETE
    // =====================================================================
    @DeleteMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> supprimerProjet(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            System.out.println("📥 Requête DELETE pour projet ID=" + id);

            // Vérifier que le projet existe
            Optional<ProjetDTO> projetOpt = projetService.obtenirProjetParId(id);
            if (projetOpt.isEmpty()) {
                System.err.println("❌ Projet introuvable: ID=" + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Projet non trouvé avec ID: " + id));
            }

            // Vérifier les permissions
            String email = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            if (!isAdmin && !projetService.utilisateurPeutModifierProjet(id, email)) {
                System.err.println("❌ Accès refusé pour l'utilisateur: " + email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Vous n'êtes pas autorisé à supprimer ce projet"));
            }

            // Supprimer le projet
            projetService.supprimerProjet(id);

            System.out.println("✅ Projet supprimé avec succès par: " + email);
            return ResponseEntity.ok(Map.of("message", "Projet supprimé avec succès"));

        } catch (RuntimeException e) {
            System.err.println("❌ Erreur métier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Erreur serveur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur lors de la suppression"));
        }
    }

    // ---------- MEMBRES DU PROJET ----------
    @GetMapping("/{id:\\d+}/membres")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<UtilisateurDTO>> obtenirMembresProjet(
            @PathVariable Long id,
            Authentication authentication
    ) {
        try {
            // Vérification de l'accès
            String email = authentication.getName();
            Long userId = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            Optional<ProjetDTO> projetOpt = projetService.obtenirProjetParId(id);
            if (projetOpt.isEmpty()) return ResponseEntity.notFound().build();

            ProjetDTO projet = projetOpt.get();

            // Vérifie si l'utilisateur a le droit de consulter les membres
            if (!isAdmin && !projet.getIdCreateur().equals(userId)
                    && !projetService.estMembreDuProjet(userId, id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<UtilisateurDTO> membres = projetService.obtenirMembresProjet(id);
            if (membres == null || membres.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(membres);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Ajouter un membre à un projet (F8)
     */
    @PostMapping("/{projetId}/membres/{membreId}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> ajouterMembreAuProjet(
            @PathVariable Long projetId,
            @PathVariable Long membreId,
            Authentication authentication) {
        try {
            // Vérifier les permissions
            String email = authentication.getName();
            Long userId = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            Optional<ProjetDTO> projetOpt = projetService.obtenirProjetParId(projetId);
            if (projetOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Projet non trouvé"));
            }

            ProjetDTO projet = projetOpt.get();

            // Vérifier que l'utilisateur est le créateur ou admin
            if (!isAdmin && !projet.getIdCreateur().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Vous n'êtes pas autorisé à ajouter des membres à ce projet"));
            }

            // Ajouter le membre
            projetService.ajouterMembreAuProjet(projetId, membreId);

            return ResponseEntity.ok(Map.of(
                    "message", "Membre ajouté avec succès au projet"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'ajout du membre"));
        }
    }

    /**
     * Retirer un membre d'un projet (F8)
     */
    @DeleteMapping("/{projetId}/membres/{membreId}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> retirerMembreDuProjet(
            @PathVariable Long projetId,
            @PathVariable Long membreId,
            Authentication authentication) {
        try {
            // Vérifier les permissions
            String email = authentication.getName();
            Long userId = utilisateurService.obtenirIdParEmail(email);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMINISTRATEUR"));

            Optional<ProjetDTO> projetOpt = projetService.obtenirProjetParId(projetId);
            if (projetOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Projet non trouvé"));
            }

            ProjetDTO projet = projetOpt.get();

            // Vérifier que l'utilisateur est le créateur ou admin
            if (!isAdmin && !projet.getIdCreateur().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Vous n'êtes pas autorisé à retirer des membres de ce projet"));
            }

            // Retirer le membre
            projetService.retirerMembreDuProjet(projetId, membreId);

            return ResponseEntity.ok(Map.of(
                    "message", "Membre retiré avec succès du projet"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors du retrait du membre"));
        }
    }

    // ---------- MÉTHODES PRIVÉES UTILITAIRES ----------
    private void traduireProjet(ProjetDTO projet, String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        if (!"fr".equalsIgnoreCase(locale.getLanguage())) {
            projet.setTitre(translationService.traduireTexteAutomatique(projet.getTitre(), locale));
            projet.setDescription(translationService.traduireTexteAutomatique(projet.getDescription(), locale));
        }
    }

    private void traduireListeProjets(List<ProjetDTO> projets, String acceptLanguage) {
        projets.forEach(p -> traduireProjet(p, acceptLanguage));
    }
}