package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Abonnement;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.AbonnementService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import be.iccbxl.gestionprojets.enums.StatutAbonnement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des abonnements.
 *
 * Implémente la fonctionnalité définie dans le cahier des charges :
 * - F10 : Paiements et abonnements (Chefs de Projet et Administrateurs)
 *
 * Supporte le modèle économique défini dans le business plan :
 * - Abonnement mensuel obligatoire pour les Chefs de Projet (10€/mois)
 * - Gestion des statuts d'abonnement (ACTIF, EXPIRE, RESILIE, EN_PAUSE)
 * - Intégration avec Stripe pour les paiements sécurisés
 *
 * @author ElhadjSouleymaneBAH
 * @version 2.0 - CORS géré par CorsConfig.java
 * @see "Cahier des charges - F10: Paiements et abonnements"
 * @see "Business plan - Modèle freemium 10€/mois"
 */
@RestController
@RequestMapping("/api/abonnements")
public class AbonnementController {

    private final AbonnementService abonnementService;
    private final UtilisateurService utilisateurService;

    public AbonnementController(AbonnementService abonnementService, UtilisateurService utilisateurService) {
        this.abonnementService = abonnementService;
        this.utilisateurService = utilisateurService;
    }

    // F10 : SOUSCRIRE UN ABONNEMENT

    /**
     * Souscription d'un nouvel abonnement.
     *
     * Fonctionnalité F10 : Paiements et abonnements
     * Utilisateurs : Chef de Projet (pour devenir Chef de Projet)
     * Importance : 5/5
     * Contraintes : Stripe requis (intégration de paiement)
     *
     * @param abonnement Données de l'abonnement à créer
     * @param authentication Informations d'authentification
     * @return L'abonnement créé ou message d'erreur
     * @see "Cahier des charges - F10: Paiements et abonnements"
     */
    @PostMapping("/souscrire")
    public ResponseEntity<?> souscrireAbonnement(@Valid @RequestBody Abonnement abonnement,
                                                 Authentication authentication) {
        try {
            System.out.println("DEBUG: [F10] Souscription abonnement par: " + authentication.getName());

            // Récupérer l'utilisateur authentifié
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(authentication.getName());
            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Utilisateur non trouvé");
            }

            Utilisateur utilisateur = utilisateurOpt.get();

            // Vérifier s'il n'a pas déjà un abonnement actif
            if (abonnementService.hasActiveSubscription(utilisateur.getId())) {
                return ResponseEntity.badRequest()
                        .body("L'utilisateur a déjà un abonnement actif");
            }

            // Créer l'abonnement avec l'utilisateur
            abonnement.setUtilisateur(utilisateur);
            Abonnement abonnementCree = abonnementService.save(abonnement);

            System.out.println("DEBUG: [F10] Abonnement créé avec succès: " + abonnementCree.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(abonnementCree);

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur souscription abonnement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la souscription de l'abonnement");
        }
    }

    // F10 : CONSULTER SON ABONNEMENT

    /**
     * Consultation de l'abonnement utilisateur.
     *
     * Fonctionnalité F10 : Consultation du statut d'abonnement
     * Utilisateurs : Chef de Projet (propriétaire), Administrateur
     *
     * @param id L'identifiant de l'abonnement
     * @param authentication Informations d'authentification
     * @return L'abonnement ou erreur 403/404
     * @see "Cahier des charges - F10: Paiements et abonnements"
     */
    @GetMapping("/{id}")
    public ResponseEntity<Abonnement> getAbonnementParId(@PathVariable Long id,
                                                         Authentication authentication) {
        try {
            Optional<Abonnement> abonnementOpt = abonnementService.findById(id);
            if (abonnementOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Abonnement abonnement = abonnementOpt.get();
            String emailConnecte = authentication.getName();

            // Vérifie si c'est un administrateur
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérifie si c'est son propre abonnement
            boolean estSonAbonnement = abonnement.getUtilisateur().getEmail().equals(emailConnecte);

            // Autorise si admin OU si c'est son propre abonnement
            if (estAdmin || estSonAbonnement) {
                return ResponseEntity.ok(abonnement);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur consultation abonnement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // F10 : CONSULTER ABONNEMENT PAR UTILISATEUR

    /**
     * Consultation de l'abonnement d'un utilisateur.
     * Utilisé pour vérifier les contraintes F6, F7, F8 (abonnement actif requis).
     *
     * @param utilisateurId L'identifiant de l'utilisateur
     * @param authentication Informations d'authentification
     * @return L'abonnement de l'utilisateur ou 404
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<Abonnement> getAbonnementParUtilisateur(@PathVariable Long utilisateurId,
                                                                  Authentication authentication) {
        try {
            Optional<Abonnement> abonnementOpt = abonnementService.findByUtilisateurId(utilisateurId);
            if (abonnementOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Abonnement abonnement = abonnementOpt.get();
            String emailConnecte = authentication.getName();

            // Vérifie si c'est un administrateur
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérifie si c'est son propre abonnement
            boolean estSonAbonnement = abonnement.getUtilisateur().getEmail().equals(emailConnecte);

            if (estAdmin || estSonAbonnement) {
                return ResponseEntity.ok(abonnement);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur consultation abonnement utilisateur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // F10 : RENOUVELER ABONNEMENT

    /**
     * Renouvellement d'un abonnement.
     * Étend la durée de l'abonnement et traite le paiement via Stripe.
     *
     * @param id L'identifiant de l'abonnement
     * @param authentication Informations d'authentification
     * @return L'abonnement renouvelé ou erreur
     */
    @PutMapping("/{id}/renouveler")
    public ResponseEntity<?> renouvelerAbonnement(@PathVariable Long id,
                                                  Authentication authentication) {
        try {
            Optional<Abonnement> abonnementOpt = abonnementService.findById(id);
            if (abonnementOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Abonnement abonnement = abonnementOpt.get();
            String emailConnecte = authentication.getName();

            // Vérifie si c'est un admin
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérifie si c'est son propre abonnement
            boolean estSonAbonnement = abonnement.getUtilisateur().getEmail().equals(emailConnecte);

            if (estAdmin || estSonAbonnement) {
                // Renouveler l'abonnement (méthode du modèle)
                abonnement.renouveler();
                Abonnement abonnementRenouvelé = abonnementService.save(abonnement);

                System.out.println("DEBUG: [F10] Abonnement renouvelé: " + id);
                return ResponseEntity.ok(abonnementRenouvelé);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur renouvellement abonnement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du renouvellement");
        }
    }

    // F10 : RÉSILIER ABONNEMENT

    /**
     * Résiliation d'un abonnement.
     * Change le statut en RESILIE sans supprimer l'historique.
     *
     * @param id L'identifiant de l'abonnement
     * @param authentication Informations d'authentification
     * @return Confirmation de résiliation ou erreur
     */
    @PutMapping("/{id}/resilier")
    public ResponseEntity<?> resilierAbonnement(@PathVariable Long id,
                                                Authentication authentication) {
        try {
            Optional<Abonnement> abonnementOpt = abonnementService.findById(id);
            if (abonnementOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Abonnement abonnement = abonnementOpt.get();
            String emailConnecte = authentication.getName();

            // Vérifie si c'est un admin
            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMINISTRATEUR"));

            // Vérifie si c'est son propre abonnement
            boolean estSonAbonnement = abonnement.getUtilisateur().getEmail().equals(emailConnecte);

            if (estAdmin || estSonAbonnement) {
                // Résilier l'abonnement
                abonnement.setStatut(StatutAbonnement.RESILIE);
                Abonnement abonnementResilie = abonnementService.save(abonnement);

                System.out.println("DEBUG: [F10] Abonnement résilié: " + id);
                return ResponseEntity.ok(abonnementResilie);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur résiliation abonnement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la résiliation");
        }
    }

    // ENDPOINTS UTILITAIRES

    /**
     * Vérification du statut d'abonnement actif.
     * Utilisé par F6, F7, F8 pour vérifier les contraintes d'abonnement.
     *
     * @param utilisateurId L'identifiant de l'utilisateur
     * @return true si l'utilisateur a un abonnement actif
     */
    @GetMapping("/verification-statut/{utilisateurId}")
    public ResponseEntity<Boolean> verifierStatutActif(@PathVariable Long utilisateurId) {
        try {
            boolean hasActiveSubscription = abonnementService.hasActiveSubscription(utilisateurId);
            return ResponseEntity.ok(hasActiveSubscription);
        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur vérification statut: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ENDPOINTS ADMINISTRATEUR

    /**
     * Liste tous les abonnements du système.
     * Réservé aux administrateurs pour la supervision générale.
     *
     * @return Liste de tous les abonnements
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<Abonnement>> getTousLesAbonnements() {
        try {
            List<Abonnement> abonnements = abonnementService.findAll();
            System.out.println("DEBUG: [F10] Liste abonnements demandée par admin: " + abonnements.size() + " trouvés");
            return ResponseEntity.ok(abonnements);
        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur liste abonnements: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Création d'abonnement par l'administrateur.
     * Permet à l'admin de créer des abonnements spéciaux.
     *
     * @param abonnement L'abonnement à créer
     * @return L'abonnement créé
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Abonnement> creerAbonnementAdmin(@Valid @RequestBody Abonnement abonnement) {
        try {
            Abonnement abonnementCree = abonnementService.save(abonnement);
            System.out.println("DEBUG: [F10] Abonnement créé par admin: " + abonnementCree.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(abonnementCree);
        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur création abonnement admin: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Suppression d'abonnement par l'administrateur.
     * Supprime complètement l'abonnement (à utiliser avec précaution).
     *
     * @param id L'identifiant de l'abonnement à supprimer
     * @return Confirmation de suppression
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerAbonnement(@PathVariable Long id) {
        try {
            if (!abonnementService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            abonnementService.deleteById(id);
            System.out.println("DEBUG: [F10] Abonnement supprimé par admin: " + id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur suppression abonnement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}