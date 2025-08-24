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
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175",
                "http://localhost:5177",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:5174",
                "http://127.0.0.1:5175",
                "http://127.0.0.1:5177"
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

    @PostMapping
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> creerProjet(@Valid @RequestBody ProjetDTO projetDTO,
                                                 Authentication authentication) {
        try {
            System.out.println("DEBUG: [F6] Création projet par " + authentication.getName());

            String emailCreateur = authentication.getName();
            ProjetDTO projetCree = projetService.creerProjet(projetDTO, emailCreateur);

            System.out.println("SUCCESS: [F6] Projet créé avec ID: " + projetCree.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(projetCree);

        } catch (RuntimeException e) {
            System.out.println("ERROR: [F6] Erreur création projet: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("ERROR: [F6] Erreur interne création projet: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping({"", "/publics"})
    @PreAuthorize("hasAuthority('VISITEUR') or hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ProjetDTO>> obtenirTousProjets() {
        try {
            System.out.println("DEBUG: [F3] Consultation projets publics");

            List<ProjetDTO> projets = projetService.obtenirTousProjets();
            System.out.println("SUCCESS: [F3] " + projets.size() + " projets publics trouvés");

            return ResponseEntity.ok(projets);
        } catch (Exception e) {
            System.out.println("ERROR: [F3] Erreur consultation projets: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> obtenirProjetParId(@PathVariable Long id,
                                                        Authentication authentication) {
        try {
            System.out.println("DEBUG: [F6] Consultation projet ID: " + id);

            Optional<ProjetDTO> projet = projetService.obtenirProjetParId(id);
            if (projet.isPresent()) {
                System.out.println("SUCCESS: [F6] Projet trouvé: " + projet.get().getTitre());
                return ResponseEntity.ok(projet.get());
            } else {
                System.out.println("ERROR: [F6] Projet non trouvé: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("ERROR: [F6] Erreur consultation projet: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/utilisateur/{utilisateurId:\\d+}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ProjetDTO>> obtenirProjetsParUtilisateur(@PathVariable Long utilisateurId,
                                                                        Authentication authentication) {
        try {
            System.out.println("DEBUG: [F6] Consultation projets utilisateur: " + utilisateurId);

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin) {
                String emailConnecte = authentication.getName();
                Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

                if (!utilisateurId.equals(idUtilisateurConnecte)) {
                    System.out.println("ERROR: [F6] Tentative d'accès aux projets d'un autre utilisateur");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            List<ProjetDTO> projets = projetService.obtenirProjetsParUtilisateur(utilisateurId);
            System.out.println("SUCCESS: [F6] " + projets.size() + " projets trouvés pour utilisateur " + utilisateurId);

            return ResponseEntity.ok(projets);
        } catch (Exception e) {
            System.out.println("ERROR: [F6] Erreur consultation projets utilisateur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> modifierProjet(@PathVariable Long id,
                                                    @Valid @RequestBody ProjetDTO projetDTO,
                                                    Authentication authentication) {
        try {
            System.out.println("DEBUG: [F6] Modification projet ID: " + id);

            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(id, emailConnecte)) {
                System.out.println("ERROR: [F6] Utilisateur n'est pas le créateur du projet");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            ProjetDTO projetModifie = projetService.modifierProjet(id, projetDTO);
            System.out.println("SUCCESS: [F6] Projet " + id + " modifié avec succès");

            return ResponseEntity.ok(projetModifie);
        } catch (RuntimeException e) {
            System.out.println("ERROR: [F6] Erreur modification projet: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("ERROR: [F6] Erreur interne modification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerProjet(@PathVariable Long id,
                                                Authentication authentication) {
        try {
            System.out.println("DEBUG: [F6] Suppression projet ID: " + id);

            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(id, emailConnecte)) {
                System.out.println("ERROR: [F6] Utilisateur n'est pas le créateur du projet");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            projetService.supprimerProjet(id);
            System.out.println("SUCCESS: [F6] Projet " + id + " supprimé avec succès");

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.out.println("ERROR: [F6] Erreur suppression projet: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println("ERROR: [F6] Erreur interne suppression: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{projetId:\\d+}/membres/{utilisateurId:\\d+}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> ajouterMembreAuProjet(@PathVariable Long projetId,
                                                        @PathVariable Long utilisateurId,
                                                        Authentication authentication) {
        try {
            System.out.println("DEBUG: [F8] Ajout membre " + utilisateurId + " au projet " + projetId);

            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                System.out.println("ERROR: [F8] Utilisateur n'est pas le créateur du projet " + projetId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Seul le créateur du projet peut ajouter des membres"));
            }

            if (!utilisateurService.existeParId(utilisateurId)) {
                System.out.println("ERROR: [F8] Utilisateur à ajouter non trouvé: " + utilisateurId);
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Utilisateur à ajouter non trouvé"));
            }

            projetService.ajouterMembreAuProjet(projetId, utilisateurId);
            utilisateurService.promouvoirVersMembreParChefProjet(utilisateurId);

            System.out.println("SUCCESS: [F8] Membre " + utilisateurId + " ajouté au projet " + projetId);

            return ResponseEntity.ok(Map.of("message", "Membre ajouté avec succès au projet"));

        } catch (RuntimeException e) {
            System.out.println("ERROR: [F8] Erreur ajout membre: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        } catch (Exception e) {
            System.out.println("ERROR: [F8] Erreur interne ajout membre: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/{projetId:\\d+}/membres")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> obtenirMembresProjet(@PathVariable Long projetId,
                                                       Authentication authentication) {
        try {
            System.out.println("DEBUG: [F8] Consultation membres du projet " + projetId);

            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin &&
                    !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte) &&
                    !projetService.estMembreDuProjet(idUtilisateurConnecte, projetId)) {

                System.out.println("ERROR: [F8] Accès refusé au projet " + projetId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Accès refusé à ce projet"));
            }

            List<UtilisateurDTO> membres = projetService.obtenirMembresProjet(projetId);
            System.out.println("SUCCESS: [F8] " + membres.size() + " membres trouvés pour le projet " + projetId);

            return ResponseEntity.ok(membres);

        } catch (RuntimeException e) {
            System.out.println("ERROR: [F8] Erreur consultation membres: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        } catch (Exception e) {
            System.out.println("ERROR: [F8] Erreur interne consultation membres: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/{projetId:\\d+}/utilisateurs-disponibles")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> obtenirUtilisateursDisponibles(@PathVariable Long projetId,
                                                                 Authentication authentication) {
        try {
            System.out.println("DEBUG: [F8] Recherche utilisateurs disponibles pour projet " + projetId);

            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                System.out.println("ERROR: [F8] Utilisateur n'est pas le créateur du projet " + projetId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Seul le créateur du projet peut voir les utilisateurs disponibles"));
            }

            List<UtilisateurDTO> utilisateursDisponibles = utilisateurService.obtenirUtilisateursDisponiblesPourProjet(projetId);
            System.out.println("SUCCESS: [F8] " + utilisateursDisponibles.size() + " utilisateurs disponibles");

            return ResponseEntity.ok(utilisateursDisponibles);

        } catch (Exception e) {
            System.out.println("ERROR: [F8] Erreur recherche utilisateurs disponibles: " + e.getMessage());
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
            System.out.println("DEBUG: [F8] Suppression membre " + utilisateurId + " du projet " + projetId);

            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                System.out.println("ERROR: [F8] Utilisateur n'est pas le créateur du projet " + projetId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Seul le créateur du projet peut retirer des membres"));
            }

            if (utilisateurService.utilisateurATachesEnCours(utilisateurId)) {
                System.out.println("WARNING: [F8] L'utilisateur " + utilisateurId + " a des tâches en cours");
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Impossible de retirer: l'utilisateur a des tâches en cours"));
            }

            projetService.retirerMembreDuProjet(projetId, utilisateurId);

            System.out.println("SUCCESS: [F8] Membre " + utilisateurId + " retiré du projet " + projetId);

            return ResponseEntity.ok(Map.of("message", "Membre retiré avec succès du projet"));

        } catch (RuntimeException e) {
            System.out.println("ERROR: [F8] Erreur suppression membre: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        } catch (Exception e) {
            System.out.println("ERROR: [F8] Erreur interne suppression membre: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/{projetId:\\d+}/membres/{utilisateurId:\\d+}/statut")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> verifierStatutMembre(@PathVariable Long projetId,
                                                       @PathVariable Long utilisateurId,
                                                       Authentication authentication) {
        try {
            System.out.println("DEBUG: [F8] Vérification statut membre " + utilisateurId + " dans projet " + projetId);

            boolean estMembre = projetService.estMembreDuProjet(utilisateurId, projetId);

            var statut = Map.of(
                    "estMembre", estMembre,
                    "projetId", projetId,
                    "utilisateurId", utilisateurId
            );

            System.out.println("DEBUG: [F8] Utilisateur " + utilisateurId +
                    (estMembre ? " est" : " n'est pas") + " membre du projet " + projetId);

            return ResponseEntity.ok(statut);

        } catch (Exception e) {
            System.out.println("ERROR: [F8] Erreur vérification statut: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur"));
        }
    }
}