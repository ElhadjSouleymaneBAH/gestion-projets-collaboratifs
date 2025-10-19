package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.dto.AbonnementDTO;
import be.iccbxl.gestionprojets.mapper.AbonnementMapper;
import be.iccbxl.gestionprojets.model.Abonnement;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.AbonnementService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import be.iccbxl.gestionprojets.enums.StatutAbonnement;
import be.iccbxl.gestionprojets.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des abonnements.
 * Implémente F10 : Paiements et abonnements.
 *
 * @author ElhadjSouleymaneBAH
 */
@RestController
@RequestMapping("/api/abonnements")
@CrossOrigin(
        origins = {
                "http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5177",
                "http://127.0.0.1:5173", "http://127.0.0.1:5174", "http://127.0.0.1:5175", "http://127.0.0.1:5177"
        },
        allowCredentials = "true"
)
public class AbonnementController {

    private final AbonnementService abonnementService;
    private final UtilisateurService utilisateurService;

    public AbonnementController(AbonnementService abonnementService, UtilisateurService utilisateurService) {
        this.abonnementService = abonnementService;
        this.utilisateurService = utilisateurService;
    }

    // ========== F10 : SOUSCRIRE UN ABONNEMENT ==========
    /**
     * Souscrit ou renouvelle un abonnement pour l'utilisateur connecté.
     *
     * Logique conforme au cahier des charges :
     * 1. Si abonnement actif existant → Renouvellement automatique (+1 mois)
     * 2. Si abonnement expiré → Réactivation
     * 3. Si aucun abonnement → Création nouvel abonnement + Promotion MEMBRE → CHEF_PROJET
     *
     * @param dto Données de l'abonnement (optionnelles, valeurs par défaut appliquées)
     * @param authentication Utilisateur authentifié via JWT
     * @return ResponseEntity avec AbonnementDTO créé/renouvelé
     */
    @PostMapping("/souscrire")
    public ResponseEntity<?> souscrireAbonnement(@RequestBody AbonnementDTO dto,
                                                 Authentication authentication) {
        try {
            System.out.println("DEBUG: [F10] Souscription abonnement par: " + authentication.getName());

            // Récupérer l'utilisateur connecté
            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(authentication.getName());
            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Utilisateur non trouvé");
            }

            Utilisateur utilisateur = utilisateurOpt.get();

            // Charger l'abonnement avec l'utilisateur pour éviter LazyInitializationException
            Optional<Abonnement> abonnementExistant = abonnementService.findByUtilisateurIdWithUtilisateur(utilisateur.getId());

            if (abonnementExistant.isPresent()) {
                Abonnement existant = abonnementExistant.get();

                // CAS 1 : Abonnement actif → Renouvellement automatique
                if (abonnementService.hasActiveSubscription(utilisateur.getId())) {
                    System.out.println("DEBUG: [F10] Renouvellement automatique pour utilisateur: " + utilisateur.getId());
                    Abonnement abonnementRenouvelé = abonnementService.renouvelerAbonnement(existant.getId());
                    AbonnementDTO response = AbonnementMapper.toDTO(abonnementRenouvelé);
                    System.out.println("DEBUG: [F10] Abonnement renouvelé avec succès: " + abonnementRenouvelé.getId());
                    return ResponseEntity.ok(response);
                }

                // CAS 2 : Abonnement expiré → Réactivation
                System.out.println("DEBUG: [F10] Réactivation abonnement expiré pour utilisateur: " + utilisateur.getId());
                Abonnement abonnementReactive = abonnementService.reactiverAbonnement(existant.getId());

                // 🔧 CORRECTION : Remettre le rôle CHEF_PROJET lors de la réactivation
                if (utilisateur.getRole() != Role.CHEF_PROJET) {
                    utilisateur.setRole(Role.CHEF_PROJET);
                    utilisateurService.save(utilisateur);
                    System.out.println("DEBUG: [F10] Rôle réactivé : MEMBRE → CHEF_PROJET");
                }

                AbonnementDTO response = AbonnementMapper.toDTO(abonnementReactive);
                System.out.println("DEBUG: [F10] Abonnement réactivé avec succès: " + abonnementReactive.getId());
                return ResponseEntity.ok(response);
            }

            // CAS 3 : Aucun abonnement → Création
            System.out.println("DEBUG: [F10] Création nouvel abonnement pour utilisateur: " + utilisateur.getId());
            String nom   = (dto.getNom() == null || dto.getNom().isBlank()) ? "Plan Premium Mensuel" : dto.getNom();
            Double prix  = (dto.getPrix() == null || dto.getPrix() <= 0) ? 10.0 : dto.getPrix();
            Integer duree = (dto.getDuree() == null || dto.getDuree() < 1) ? 1 : Math.min(dto.getDuree(), 12);
            String type  = (dto.getType() == null || dto.getType().isBlank()) ? "premium" : dto.getType();

            Abonnement abonnement = new Abonnement();
            abonnement.setNom(nom);
            abonnement.setPrix(prix);
            abonnement.setDuree(duree);
            abonnement.setType(type);
            abonnement.setUtilisateur(utilisateur);
            abonnement.setStatut(StatutAbonnement.ACTIF);
            abonnement.setDateDebut(java.time.LocalDate.now());
            abonnement.setDateFin(java.time.LocalDate.now().plusMonths(duree));

            Abonnement abonnementCree = abonnementService.save(abonnement);

            // 🔧 CORRECTION MAJEURE : Mise à jour du rôle MEMBRE → CHEF_PROJET
            // Conforme au cahier des charges : "Chef de projet (Abonné) : Doit souscrire à un abonnement mensuel"
            if (utilisateur.getRole() != Role.CHEF_PROJET) {
                utilisateur.setRole(Role.CHEF_PROJET);
                utilisateurService.save(utilisateur);
                System.out.println("DEBUG: [F10] Promotion automatique : MEMBRE → CHEF_PROJET");
            }

            AbonnementDTO response = AbonnementMapper.toDTO(abonnementCree);

            System.out.println("DEBUG: [F10] Abonnement créé avec succès: " + abonnementCree.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur souscription abonnement: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la souscription de l'abonnement: " + e.getMessage());
        }
    }

    // ========== F10 : CONSULTER SON ABONNEMENT PAR ID ==========
    /**
     * Récupère un abonnement par son ID.
     * Accessible uniquement par le propriétaire ou un administrateur.
     *
     * @param id ID de l'abonnement
     * @param authentication Utilisateur authentifié
     * @return ResponseEntity avec AbonnementDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAbonnementParId(@PathVariable Long id,
                                                Authentication authentication) {
        try {
            Optional<Abonnement> abonnementOpt = abonnementService.findByIdWithUtilisateur(id);
            if (abonnementOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Abonnement abonnement = abonnementOpt.get();
            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            boolean estSonAbonnement = abonnement.getUtilisateur().getEmail().equals(emailConnecte);

            if (estAdmin || estSonAbonnement) {
                AbonnementDTO dto = AbonnementMapper.toDTO(abonnement);
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur consultation abonnement: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F10 : CONSULTER ABONNEMENT PAR UTILISATEUR ==========
    /**
     * Récupère l'abonnement d'un utilisateur par son ID.
     * Accessible uniquement par le propriétaire ou un administrateur.
     *
     * @param utilisateurId ID de l'utilisateur
     * @param authentication Utilisateur authentifié
     * @return ResponseEntity avec AbonnementDTO ou 404 si non trouvé
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<?> getAbonnementParUtilisateur(@PathVariable Long utilisateurId,
                                                         Authentication authentication) {
        try {
            Optional<Abonnement> abonnementOpt = abonnementService.findByUtilisateurIdWithUtilisateur(utilisateurId);
            if (abonnementOpt.isEmpty()) {
                System.out.println("DEBUG: [F10] Aucun abonnement trouvé pour l'utilisateur: " + utilisateurId);
                return ResponseEntity.notFound().build();
            }

            Abonnement abonnement = abonnementOpt.get();
            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            boolean estSonAbonnement = utilisateurService.findByEmail(emailConnecte)
                    .map(u -> u.getId().equals(utilisateurId))
                    .orElse(false);

            if (estAdmin || estSonAbonnement) {
                AbonnementDTO dto = AbonnementMapper.toDTO(abonnement);
                System.out.println("DEBUG: [F10] Abonnement trouvé pour utilisateur " + utilisateurId + ": " + abonnement.getId());
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur consultation abonnement utilisateur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== F10 : RENOUVELER ABONNEMENT ==========
    /**
     * Renouvelle un abonnement pour 1mois supplémentaire.
     * Accessible uniquement par le propriétaire ou un administrateur.
     *
     * @param id ID de l'abonnement
     * @param authentication Utilisateur authentifié
     * @return ResponseEntity avec AbonnementDTO renouvelé
     */
    @PutMapping("/{id}/renouveler")
    public ResponseEntity<?> renouvelerAbonnement(@PathVariable Long id,
                                                  Authentication authentication) {
        try {
            System.out.println("DEBUG: [F10] Tentative de renouvellement abonnement ID: " + id);

            Optional<Abonnement> abonnementOpt = abonnementService.findByIdWithUtilisateur(id);
            if (abonnementOpt.isEmpty()) {
                System.err.println("ERROR: [F10] Abonnement introuvable: " + id);
                return ResponseEntity.notFound().build();
            }

            Abonnement abonnement = abonnementOpt.get();
            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            boolean estSonAbonnement = abonnement.getUtilisateur().getEmail().equals(emailConnecte);

            if (!estAdmin && !estSonAbonnement) {
                System.err.println("ERROR: [F10] Accès refusé pour renouveler abonnement: " + id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Abonnement abonnementRenouvelé = abonnementService.renouvelerAbonnement(id);
            AbonnementDTO dto = AbonnementMapper.toDTO(abonnementRenouvelé);

            System.out.println("DEBUG: [F10] Abonnement renouvelé avec succès: " + id +
                    " - Nouvelle date fin: " + abonnementRenouvelé.getDateFin());
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur renouvellement abonnement: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du renouvellement: " + e.getMessage());
        }
    }

    // ========== F10 : RÉSILIER ABONNEMENT ==========
    /**
     * Résilie un abonnement.
     * Accessible uniquement par le propriétaire ou un administrateur.
     *
     * @param id ID de l'abonnement
     * @param authentication Utilisateur authentifié
     * @return ResponseEntity avec AbonnementDTO résilié
     */
    @PutMapping("/{id}/resilier")
    public ResponseEntity<?> resilierAbonnement(@PathVariable Long id,
                                                Authentication authentication) {
        try {
            Optional<Abonnement> abonnementOpt = abonnementService.findByIdWithUtilisateur(id);
            if (abonnementOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Abonnement abonnement = abonnementOpt.get();
            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            boolean estSonAbonnement = abonnement.getUtilisateur().getEmail().equals(emailConnecte);

            if (estAdmin || estSonAbonnement) {
                Abonnement abonnementResilie = abonnementService.resilierAbonnement(id);

                // 🔧 CORRECTION : Rétrograder le rôle CHEF_PROJET → MEMBRE lors de la résiliation
                Utilisateur utilisateur = abonnement.getUtilisateur();
                if (utilisateur.getRole() == Role.CHEF_PROJET) {
                    utilisateur.setRole(Role.MEMBRE);
                    utilisateurService.save(utilisateur);
                    System.out.println("DEBUG: [F10] Rétrogradation : CHEF_PROJET → MEMBRE");
                }

                AbonnementDTO dto = AbonnementMapper.toDTO(abonnementResilie);

                System.out.println("DEBUG: [F10] Abonnement résilié: " + id);
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur résiliation abonnement: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la résiliation");
        }
    }

    // ========== ENDPOINTS UTILITAIRES ==========
    @GetMapping("/verification-statut/{utilisateurId}")
    public ResponseEntity<Boolean> verifierStatutActif(@PathVariable Long utilisateurId) {
        try {
            boolean hasActiveSubscription = abonnementService.hasActiveSubscription(utilisateurId);
            System.out.println("DEBUG: [F10] Vérification statut utilisateur " + utilisateurId + ": " + hasActiveSubscription);
            return ResponseEntity.ok(hasActiveSubscription);
        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur vérification statut: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== ENDPOINTS ADMINISTRATEUR ==========
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<AbonnementDTO>> getTousLesAbonnements() {
        try {
            List<Abonnement> abonnements = abonnementService.findAllWithUtilisateurs();

            List<AbonnementDTO> abonnementsDTO = abonnements.stream()
                    .map(AbonnementMapper::toDTO)
                    .collect(Collectors.toList());

            System.out.println("DEBUG: [F10] " + abonnementsDTO.size() + " abonnements retournés avec utilisateurs");
            return ResponseEntity.ok(abonnementsDTO);

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur liste abonnements: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<?> creerAbonnementAdmin(@Valid @RequestBody Abonnement abonnement) {
        try {
            Abonnement abonnementCree = abonnementService.save(abonnement);
            AbonnementDTO dto = AbonnementMapper.toDTO(abonnementCree);

            System.out.println("DEBUG: [F10] Abonnement créé par admin: " + abonnementCree.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);

        } catch (Exception e) {
            System.err.println("ERROR: [F10] Erreur création abonnement admin: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}