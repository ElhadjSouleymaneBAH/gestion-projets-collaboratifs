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

/**
 * Controller REST pour la gestion des projets.
 *
 * Implémente les fonctionnalités du cahier des charges :
 * - F3 : Consulter les projets publics
 * - F6 : Gérer les projets
 * - F8 : Ajouter des membres à un projet
 * - F9 : Support collaboration temps réel
 *
 * CORRIGÉ : hasAuthority() au lieu de hasRole() pour compatibilité avec JwtFilter
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.1
 * @see "Cahier des charges - Fonctionnalités F3, F6, F8, F9"
 */
@RestController
@RequestMapping("/api/projets")
@CrossOrigin(origins = "*")
public class ProjetController {

    private final ProjetService projetService;
    private final UtilisateurService utilisateurService;

    public ProjetController(ProjetService projetService, UtilisateurService utilisateurService) {
        this.projetService = projetService;
        this.utilisateurService = utilisateurService;
    }

    // ========== F6 : GESTION DES PROJETS ==========

    /**
     * Créer un nouveau projet (F6).
     * Utilisateurs : Chef de Projet
     * Importance : 5/5
     * Contraintes : Abonnement actif
     */
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

    /**
     * Obtenir tous les projets (F3 - Consulter projets publics).
     * Utilisateurs : Visiteur (lecture seule)
     * Importance : 4/5
     * Contraintes : Lecture seule
     * CORRIGÉ : hasAuthority() au lieu de hasRole()
     */
    @GetMapping
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

    /**
     * Obtenir un projet par ID (F6).
     * Utilisateurs : Membre, Chef de Projet, Administrateur
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> obtenirProjetParId(@PathVariable Long id,
                                                        Authentication authentication) {
        try {
            System.out.println("DEBUG: [F6] Consultation projet ID: " + id);

            Optional<ProjetDTO> projet = projetService.obtenirProjetParId(id);
            if (projet.isPresent()) {
                // TODO: Vérifier accès au projet selon rôle utilisateur
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

    /**
     * Obtenir les projets d'un utilisateur (F6).
     * Utilisateurs : Membre, Chef de Projet, Administrateur
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<ProjetDTO>> obtenirProjetsParUtilisateur(@PathVariable Long utilisateurId,
                                                                        Authentication authentication) {
        try {
            System.out.println("DEBUG: [F6] Consultation projets utilisateur: " + utilisateurId);

            // Vérifier que l'utilisateur consulte ses propres projets (sauf admin)
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

    /**
     * Modifier un projet (F6).
     * Utilisateurs : Chef de Projet, Administrateur
     * Contraintes : Abonnement actif
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> modifierProjet(@PathVariable Long id,
                                                    @Valid @RequestBody ProjetDTO projetDTO,
                                                    Authentication authentication) {
        try {
            System.out.println("DEBUG: [F6] Modification projet ID: " + id);

            String emailConnecte = authentication.getName();

            // Vérifier que l'utilisateur peut modifier ce projet (sauf admin)
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

    /**
     * Supprimer un projet (F6).
     * Utilisateurs : Chef de Projet, Administrateur
     * Contraintes : Abonnement actif
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerProjet(@PathVariable Long id,
                                                Authentication authentication) {
        try {
            System.out.println("DEBUG: [F6] Suppression projet ID: " + id);

            String emailConnecte = authentication.getName();

            // Vérifier que l'utilisateur peut supprimer ce projet (sauf admin)
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

    // ========== F8 : AJOUTER DES MEMBRES À UN PROJET ==========

    /**
     * Ajouter un membre à un projet (F8).
     *
     * Fonctionnalité F8 du cahier des charges :
     * - Utilisateurs : Chef de Projet
     * - Importance : 4/5
     * - Contraintes : Membres existants
     * - Description : Inviter des utilisateurs à rejoindre un projet
     */
    @PostMapping("/{projetId}/membres/{utilisateurId}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> ajouterMembreAuProjet(@PathVariable Long projetId,
                                                        @PathVariable Long utilisateurId,
                                                        Authentication authentication) {
        try {
            System.out.println("DEBUG: [F8] Ajout membre " + utilisateurId + " au projet " + projetId);

            String emailConnecte = authentication.getName();

            // Vérifie que l'utilisateur connecté peut modifier ce projet (sauf admin)
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                System.out.println("ERROR: [F8] Utilisateur n'est pas le créateur du projet " + projetId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Seul le créateur du projet peut ajouter des membres"));
            }

            // Vérifier que l'utilisateur à ajouter existe
            if (!utilisateurService.existeParId(utilisateurId)) {
                System.out.println("ERROR: [F8] Utilisateur à ajouter non trouvé: " + utilisateurId);
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Utilisateur à ajouter non trouvé"));
            }

            // Ajouter le membre au projet
            projetService.ajouterMembreAuProjet(projetId, utilisateurId);

            // Promouvoir l'utilisateur VISITEUR vers MEMBRE (logique métier F8)
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

    /**
     * Obtenir la liste des membres d'un projet (F8).
     * Utilisateurs : Membre, Chef de Projet, Administrateur
     */
    @GetMapping("/{projetId}/membres")
    @PreAuthorize("hasAuthority('MEMBRE') or hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> obtenirMembresProjet(@PathVariable Long projetId,
                                                       Authentication authentication) {
        try {
            System.out.println("DEBUG: [F8] Consultation membres du projet " + projetId);

            // Vérifier que l'utilisateur a accès au projet
            String emailConnecte = authentication.getName();
            Long idUtilisateurConnecte = utilisateurService.obtenirIdParEmail(emailConnecte);

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            // ADMIN : accès total
            // CHEF_PROJET : ses projets
            // MEMBRE : projets où il est membre
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

    /**
     * Obtenir les utilisateurs disponibles pour ajout à un projet (F8).
     * Utilisateurs : Chef de Projet, Administrateur
     * Contraintes : Membres existants
     */
    @GetMapping("/{projetId}/utilisateurs-disponibles")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> obtenirUtilisateursDisponibles(@PathVariable Long projetId,
                                                                 Authentication authentication) {
        try {
            System.out.println("DEBUG: [F8] Recherche utilisateurs disponibles pour projet " + projetId);

            String emailConnecte = authentication.getName();

            // Vérifier que l'utilisateur connecté peut modifier ce projet (sauf admin)
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

    /**
     * Retirer un membre d'un projet (F8).
     * Utilisateurs : Chef de Projet, Administrateur
     */
    @DeleteMapping("/{projetId}/membres/{utilisateurId}")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Object> retirerMembreDuProjet(@PathVariable Long projetId,
                                                        @PathVariable Long utilisateurId,
                                                        Authentication authentication) {
        try {
            System.out.println("DEBUG: [F8] Suppression membre " + utilisateurId + " du projet " + projetId);

            String emailConnecte = authentication.getName();

            // Vérifier que l'utilisateur connecté peut modifier ce projet (sauf admin)
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                System.out.println("ERROR: [F8] Utilisateur n'est pas le créateur du projet " + projetId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Seul le créateur du projet peut retirer des membres"));
            }

            // Vérifier si l'utilisateur a des tâches en cours avant de le retirer
            if (utilisateurService.utilisateurATachesEnCours(utilisateurId)) {
                System.out.println("WARNING: [F8] L'utilisateur " + utilisateurId + " a des tâches en cours");
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Impossible de retirer: l'utilisateur a des tâches en cours"));
            }

            // Retirer le membre du projet
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

    /**
     * Vérifier le statut d'un membre dans un projet (F8).
     * Utilisateurs : Membre, Chef de Projet, Administrateur
     */
    @GetMapping("/{projetId}/membres/{utilisateurId}/statut")
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