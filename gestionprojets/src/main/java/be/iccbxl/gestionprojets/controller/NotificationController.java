package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Notification;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.NotificationService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST Notifications
 * + Alias admin /api/notifications/admin/all pour le dashboard.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final UtilisateurService utilisateurService;

    /** DTO simple pour créer une notification */
    public static class CreateNotificationRequest {
        @NotNull(message = "L'ID utilisateur est obligatoire")
        private Long utilisateurId;

        @NotBlank(message = "Le message est obligatoire")
        private String message;

        public Long getUtilisateurId() { return utilisateurId; }
        public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /** ⇨ AJOUT : alias admin pour le dashboard */
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<Notification>> getAllForAdminAlias() {
        return ResponseEntity.ok(notificationService.obtenirToutes());
    }

    /** F4 : Récupérer toutes les notifications d'un utilisateur */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsUtilisateur(
            @PathVariable Long userId,
            Authentication authentication) {

        try {
            Optional<Utilisateur> utilisateurDemandeOpt = utilisateurService.obtenirParId(userId);
            if (utilisateurDemandeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Utilisateur utilisateurDemande = utilisateurDemandeOpt.get();
            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));

            boolean estSonPropreProfil = utilisateurDemande.getEmail().equals(emailConnecte);

            if (estAdmin || estSonPropreProfil) {
                List<Notification> notifications = notificationService.getNotificationsByUtilisateur(userId);
                return ResponseEntity.ok(notifications);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            log.error("Erreur récupération notifications : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** F5 : Marquer toutes les notifications comme lues */
    @PutMapping("/user/{userId}/mark-all-read")
    public ResponseEntity<Map<String, Object>> marquerToutesCommeLues(
            @PathVariable Long userId,
            Authentication authentication) {

        try {
            Optional<Utilisateur> utilisateurExistantOpt = utilisateurService.obtenirParId(userId);
            if (utilisateurExistantOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Utilisateur utilisateurExistant = utilisateurExistantOpt.get();
            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));
            boolean estSonPropreProfil = utilisateurExistant.getEmail().equals(emailConnecte);

            if (estAdmin || estSonPropreProfil) {
                int count = notificationService.marquerToutesCommeLues(userId);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Toutes les notifications ont été marquées comme lues");
                response.put("count", count);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            log.error("Erreur marquage toutes notifications : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** F4 : Compter les notifications non lues (badge) */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> getCountNotificationsNonLues(
            @PathVariable Long userId,
            Authentication authentication) {

        try {
            Optional<Utilisateur> utilisateurDemandeOpt = utilisateurService.obtenirParId(userId);
            if (utilisateurDemandeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Utilisateur utilisateurDemande = utilisateurDemandeOpt.get();
            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));
            boolean estSonPropreProfil = utilisateurDemande.getEmail().equals(emailConnecte);

            if (estAdmin || estSonPropreProfil) {
                long count = notificationService.compterNotificationsNonLues(userId);
                Map<String, Long> response = new HashMap<>();
                response.put("count", count);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            log.error("Erreur comptage notifications : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** F5 : Marquer une notification comme lue */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, String>> marquerCommeLue(
            @PathVariable Long notificationId,
            @RequestParam Long userId,
            Authentication authentication) {

        try {
            Optional<Utilisateur> utilisateurExistantOpt = utilisateurService.obtenirParId(userId);
            if (utilisateurExistantOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Utilisateur utilisateurExistant = utilisateurExistantOpt.get();
            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));
            boolean estSonPropreProfil = utilisateurExistant.getEmail().equals(emailConnecte);

            if (estAdmin || estSonPropreProfil) {
                notificationService.marquerCommeLue(notificationId, userId);

                Map<String, String> response = new HashMap<>();
                response.put("message", "Notification marquée comme lue");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            log.error("Erreur marquage notification : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** F6 : Notification création de projet (Chef de Projet) */
    @PostMapping("/projet-cree")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> notifierProjetCree(
            @RequestParam Long chefProjetId,
            @RequestParam String nomProjet) {

        try {
            String message = String.format("Votre projet '%s' a été créé avec succès !", nomProjet);
            notificationService.creerNotification(chefProjetId, message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification envoyée");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur notification projet : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** F7 : Notification assignation de tâche */
    @PostMapping("/tache-assignee")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> notifierTacheAssignee(
            @RequestParam Long membreId,
            @RequestParam String nomTache,
            @RequestParam String nomProjet) {

        try {
            String message = String.format("Nouvelle tâche assignée dans '%s' : %s", nomProjet, nomTache);
            notificationService.creerNotification(membreId, message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification envoyée");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur notification tâche : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** F8 : Notification ajout à un projet */
    @PostMapping("/ajout-projet")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> notifierAjoutProjet(
            @RequestParam Long membreId,
            @RequestParam String nomProjet) {

        try {
            String message = String.format("Vous avez été ajouté au projet '%s'", nomProjet);
            notificationService.creerNotification(membreId, message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification envoyée");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur notification ajout projet : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** F10 : Notification paiement abonnement */
    @PostMapping("/paiement")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> notifierPaiement(
            @RequestParam Long chefProjetId,
            @RequestParam String statut,
            @RequestParam(defaultValue = "10.0") Double montant) {

        try {
            String message;
            if ("COMPLETE".equals(statut)) {
                message = String.format("Paiement confirmé (%.2f€). Accès Chef de Projet activé.", montant);
            } else {
                message = String.format("Échec paiement (%.2f€). Veuillez vérifier vos informations.", montant);
            }

            notificationService.creerNotification(chefProjetId, message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification paiement envoyée");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur notification paiement : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** F11 : Notification génération facture */
    @PostMapping("/facture")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> notifierFacture(
            @RequestParam Long chefProjetId,
            @RequestParam String numeroFacture) {

        try {
            String message = String.format("Facture %s générée et disponible en téléchargement", numeroFacture);
            notificationService.creerNotification(chefProjetId, message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification facture envoyée");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur notification facture : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Créer notification système (Admin seulement) */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Notification> creerNotification(@Valid @RequestBody CreateNotificationRequest request) {
        try {
            Notification notification = notificationService.creerNotification(
                    request.getUtilisateurId(),
                    request.getMessage()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (Exception e) {
            log.error("Erreur création notification : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /** Liste toutes les notifications (Admin) */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        try {
            List<Notification> notifications = notificationService.obtenirToutes();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Erreur récupération toutes notifications : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Supprimer notification (Admin ou propriétaire) */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> supprimerNotification(
            @PathVariable Long notificationId,
            @RequestParam Long userId,
            Authentication authentication) {

        try {
            Optional<Utilisateur> utilisateurExistantOpt = utilisateurService.obtenirParId(userId);
            if (utilisateurExistantOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Utilisateur utilisateurExistant = utilisateurExistantOpt.get();
            String emailConnecte = authentication.getName();

            boolean estAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMINISTRATEUR"));
            boolean estSonPropreProfil = utilisateurExistant.getEmail().equals(emailConnecte);

            if (estAdmin || estSonPropreProfil) {
                notificationService.supprimerNotification(notificationId, userId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (Exception e) {
            log.error("Erreur suppression notification : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Notification de bienvenue après inscription (F1) */
    @PostMapping("/bienvenue")
    public ResponseEntity<Map<String, String>> envoyerBienvenue(@RequestParam Long utilisateurId) {
        try {
            String message = "Bienvenue sur la plateforme de gestion de projets collaboratifs !";
            notificationService.creerNotification(utilisateurId, message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification bienvenue envoyée");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur notification bienvenue : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
