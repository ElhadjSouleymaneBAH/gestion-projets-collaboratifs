package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.ProjetDTO;
import be.iccbxl.gestionprojets.dto.UtilisateurDTO;
import be.iccbxl.gestionprojets.service.ProjetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controller REST pour la gestion des projets.
 *
 * Implémente les fonctionnalités définies dans le cahier des charges :
 * - F3 : Consulter les projets publics (Visiteurs)
 * - F6 : Gérer les projets (Chef de Projet)
 * - F8 : Ajouter des membres à un projet (Chef de Projet)
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @see "Cahier des charges - Fonctionnalités F3, F6, F8"
 */
@RestController
@RequestMapping("/api/projets")
@CrossOrigin(origins = "*")
public class ProjetController {

    private final ProjetService projetService;

    public ProjetController(ProjetService projetService) {
        this.projetService = projetService;
    }

    // ========== F3 : CONSULTER LES PROJETS PUBLICS ==========

    /**
     * Obtenir tous les projets publics.
     *
     * Fonctionnalité F3 : Consulter les projets publics
     * Utilisateurs : Visiteur (accès public)
     * Importance : 4/5
     * Contraintes : Lecture seule
     *
     * @return Liste des projets publics
     * @see "Cahier des charges - F3: Consulter les projets publics"
     */
    @GetMapping
    public ResponseEntity<List<ProjetDTO>> obtenirTousProjets() {
        try {
            List<ProjetDTO> projets = projetService.obtenirTousProjets();
            return ResponseEntity.ok(projets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir un projet par son ID.
     * Extension logique de F3 pour consultation détaillée.
     *
     * @param id L'identifiant du projet
     * @return Le projet demandé ou erreur 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjetDTO> obtenirProjetParId(@PathVariable Long id) {
        try {
            Optional<ProjetDTO> projet = projetService.obtenirProjetParId(id);
            return projet.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F6 : GÉRER LES PROJETS ==========

    /**
     * Créer un nouveau projet.
     *
     * Fonctionnalité F6 : Gérer les projets
     * Utilisateurs : Chef de Projet
     * Importance : 5/5
     * Contraintes : Abonnement actif
     *
     * @param projetDTO Les données du projet à créer
     * @param authentication Informations d'authentification
     * @return Le projet créé
     * @see "Cahier des charges - F6: Gérer les projets"
     */
    @PostMapping
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> creerProjet(@Valid @RequestBody ProjetDTO projetDTO,
                                                 Authentication authentication) {
        try {
            // Définir le créateur du projet
            String emailCreateur = authentication.getName();
            ProjetDTO projetCree = projetService.creerProjet(projetDTO, emailCreateur);
            return ResponseEntity.status(HttpStatus.CREATED).body(projetCree);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtenir les projets d'un créateur spécifique.
     * Support de F6 pour la gestion "mes projets".
     *
     * @param idCreateur L'identifiant du créateur
     * @return Liste des projets du créateur
     */
    @GetMapping("/createur/{idCreateur}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<ProjetDTO>> obtenirProjetsParCreateur(@PathVariable Long idCreateur,
                                                                     Authentication authentication) {
        try {
            // Vérifier que l'utilisateur consulte ses propres projets (ou admin)
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            if (!estAdmin) {
                // TODO: Vérifier que idCreateur correspond à l'utilisateur connecté
                // String emailConnecte = authentication.getName();
                // if (!projetService.utilisateurEstCreateur(idCreateur, emailConnecte)) {
                //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                // }
            }

            List<ProjetDTO> projets = projetService.obtenirProjetsParCreateur(idCreateur);
            return ResponseEntity.ok(projets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Modifier un projet existant.
     *
     * Fonctionnalité F6 : Gérer les projets
     * Seul le créateur du projet ou un admin peut le modifier.
     *
     * @param id L'identifiant du projet
     * @param projetDTO Les nouvelles données
     * @param authentication Informations d'authentification
     * @return Le projet modifié
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<ProjetDTO> modifierProjet(@PathVariable Long id,
                                                    @Valid @RequestBody ProjetDTO projetDTO,
                                                    Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérifier les autorisations (propriétaire ou admin)
            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(id, emailConnecte)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            ProjetDTO projetModifie = projetService.modifierProjet(id, projetDTO);
            return ResponseEntity.ok(projetModifie);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprimer un projet.
     *
     * Fonctionnalité F6 : Gérer les projets
     * Seul le créateur du projet ou un admin peut le supprimer.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerProjet(@PathVariable Long id,
                                                Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérifier les autorisations (propriétaire ou admin)
            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(id, emailConnecte)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            projetService.supprimerProjet(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== F8 : AJOUTER DES MEMBRES À UN PROJET ==========

    /**
     * Ajouter un membre à un projet.
     *
     * Fonctionnalité F8 : Ajouter des membres à un projet
     * Utilisateurs : Chef de Projet
     * Importance : 4/5
     * Contraintes : Membres existants
     *
     * @param projetId L'identifiant du projet
     * @param utilisateurId L'identifiant de l'utilisateur à ajouter
     * @param authentication Informations d'authentification
     * @return Confirmation d'ajout
     * @see "Cahier des charges - F8: Ajouter des membres à un projet"
     */
    @PostMapping("/{projetId}/membres/{utilisateurId}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<String> ajouterMembre(@PathVariable Long projetId,
                                                @PathVariable Long utilisateurId,
                                                Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérifier que l'utilisateur peut gérer ce projet
            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Vous n'avez pas l'autorisation de gérer ce projet");
            }

            // Ajouter le membre au projet
            projetService.ajouterMembreAuProjet(projetId, utilisateurId);
            return ResponseEntity.ok("Membre ajouté avec succès au projet");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'ajout du membre");
        }
    }

    /**
     * Retirer un membre d'un projet.
     *
     * Extension de F8 pour la gestion complète des membres.
     *
     * @param projetId L'identifiant du projet
     * @param utilisateurId L'identifiant de l'utilisateur à retirer
     * @param authentication Informations d'authentification
     * @return Confirmation de suppression
     */
    @DeleteMapping("/{projetId}/membres/{utilisateurId}")
    @PreAuthorize("hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<String> retirerMembre(@PathVariable Long projetId,
                                                @PathVariable Long utilisateurId,
                                                Authentication authentication) {
        try {
            String emailConnecte = authentication.getName();
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérifier que l'utilisateur peut gérer ce projet
            if (!estAdmin && !projetService.utilisateurPeutModifierProjet(projetId, emailConnecte)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Vous n'avez pas l'autorisation de gérer ce projet");
            }

            // Retirer le membre du projet
            projetService.retirerMembreDuProjet(projetId, utilisateurId);
            return ResponseEntity.ok("Membre retiré avec succès du projet");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du membre");
        }
    }

    /**
     * Lister les membres d'un projet.
     *
     * Support de F8 pour visualiser les membres actuels.
     *
     * @param projetId L'identifiant du projet
     * @return Liste des membres du projet
     */
    @GetMapping("/{projetId}/membres")
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<UtilisateurDTO>> listerMembres(@PathVariable Long projetId) {
        try {
            List<UtilisateurDTO> membres = projetService.obtenirMembresProjet(projetId);
            return ResponseEntity.ok(membres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Vérifier si un utilisateur est membre d'un projet.
     *
     * Utile pour les autorisations et l'interface utilisateur.
     *
     * @param projetId L'identifiant du projet
     * @param utilisateurId L'identifiant de l'utilisateur
     * @return true si l'utilisateur est membre
     */
    @GetMapping("/{projetId}/membres/{utilisateurId}/est-membre")
    @PreAuthorize("hasRole('MEMBRE') or hasRole('CHEF_PROJET') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Boolean> estMembreDuProjet(@PathVariable Long projetId,
                                                     @PathVariable Long utilisateurId) {
        try {
            boolean estMembre = projetService.utilisateurEstMembreProjet(projetId, utilisateurId);
            return ResponseEntity.ok(estMembre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}