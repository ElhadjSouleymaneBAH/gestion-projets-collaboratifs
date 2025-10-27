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
import java.util.*;

/**
 * Controller REST Notifications
 * + Alias admin /api/notifications/admin/all pour le dashboard.
 * + Intégration WebSocket pour notifications temps réel (via NotificationWebSocketController)
 *
 * @author ElhadjSouleymaneBAH
 * @version 2.0
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final UtilisateurService utilisateurService;
    private final NotificationWebSocketController notificationWebSocketController;

    /** DTO simple pour créer une notification */
    public static class CreateNotificationRequest {
        @NotNull(message = "L'ID utilisateur est obligatoire")
        private Long utilisateurId;

        @NotBlank(message = "Le message est obligatoire")
        private String message;

        private String titre;

        public Long getUtilisateurId() { return utilisateurId; }
        public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
    }

    // ================================
    //  RÉCUPÉRATION / CONSULTATION
    // ================================

    /** Alias admin */
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<Notification>> getAllForAdminAlias() {
        return ResponseEntity.ok(notificationService.obtenirToutes());
    }

    /** Notifications d'un utilisateur */
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

    /** Compter les notifications non lues */
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

    // ================================
    //  MISE À JOUR
    // ================================

    /** Marquer une notification comme lue */
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

    /** Marquer toutes les notifications comme lues */
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

    // ================================
    //  CRÉATION DE NOTIFICATIONS
    // ================================

    /** Notification manuelle (admin ou système) */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Notification> creerNotification(@Valid @RequestBody CreateNotificationRequest request) {
        try {
            Notification notification = notificationService.creerNotification(
                    request.getUtilisateurId(),
                    request.getMessage()
            );

            //  Envoi temps réel WebSocket
            notificationWebSocketController.envoyerNotificationUtilisateur(
                    request.getUtilisateurId(),
                    request.getTitre() != null ? request.getTitre() : "Notification",
                    request.getMessage()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (Exception e) {
            log.error("Erreur création notification : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /** Notification automatique : ajout projet (chef → membre) */
    @PostMapping("/ajout-projet")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> notifierAjoutProjet(
            @RequestParam Long membreId,
            @RequestParam String nomProjet) {

        try {
            String message = String.format("Vous avez été ajouté au projet '%s'", nomProjet);
            notificationService.creerNotification(membreId, message);

            // Envoi en direct via WebSocket
            notificationWebSocketController.envoyerNotificationUtilisateur(membreId, "Nouveau projet", message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification envoyée");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur notification ajout projet : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Notification : tâche assignée */
    @PostMapping("/tache-assignee")
    @PreAuthorize("hasAuthority('CHEF_PROJET') or hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, String>> notifierTacheAssignee(
            @RequestParam Long membreId,
            @RequestParam String nomTache,
            @RequestParam String nomProjet) {

        try {
            String message = String.format("Nouvelle tâche dans '%s' : %s", nomProjet, nomTache);
            notificationService.creerNotification(membreId, message);

            //  Envoi temps réel
            notificationWebSocketController.envoyerNotificationUtilisateur(membreId, "Tâche assignée", message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification tâche envoyée");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur notification tâche : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Notification automatique : action du membre → chef projet */
    @PostMapping("/membre-action")
    @PreAuthorize("hasAuthority('MEMBRE')")
    public ResponseEntity<Map<String, String>> notifierChefProjet(
            @RequestParam Long chefProjetId,
            @RequestParam String message) {

        try {
            notificationService.creerNotification(chefProjetId, message);
            notificationWebSocketController.envoyerNotificationUtilisateur(
                    chefProjetId, "Activité membre", message);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification envoyée au chef de projet");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur notification membre → chef : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
