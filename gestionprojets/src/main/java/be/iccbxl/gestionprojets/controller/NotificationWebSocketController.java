package be.iccbxl.gestionprojets.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller WebSocket pour les notifications en temps réel (F13)
 *Utilise Map au lieu de NotificationDTO custom
 * Compatible avec le NotificationDTO existant
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envoyer une notification à un utilisateur spécifique via WebSocket
     * Topic : /user/{userId}/topic/notifications
     * Utilisé automatiquement par CommentaireService lors de la création d'un commentaire
     */
    public void envoyerNotificationUtilisateur(Long userId, String titre, String message) {
        try {
            log.info("[F13] Envoi notification WebSocket à l'utilisateur {}: {}", userId, message);

            // Créer payload JSON compatible avec frontend
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "NOTIFICATION");
            notification.put("titre", titre);
            notification.put("message", message);
            notification.put("contenu", message); // Alias
            notification.put("createdAt", LocalDateTime.now().toString());
            notification.put("lu", false);

            // Envoyer au topic personnalisé de l'utilisateur
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(userId),
                    "/topic/notifications",
                    notification
            );

            log.info("[F13]  Notification WebSocket envoyée avec succès");
        } catch (Exception e) {
            log.error("[F13]  Erreur envoi notification WebSocket: {}", e.getMessage());
        }
    }

    /**
     * Envoyer une notification à tous les membres d'un projet
     *
     * Topic : /topic/project/{projectId}/notifications
     */
    public void envoyerNotificationProjet(Long projectId, String titre, String message) {
        try {
            log.info("[F13] Envoi notification projet {} via WebSocket", projectId);

            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "NOTIFICATION");
            notification.put("titre", titre);
            notification.put("message", message);
            notification.put("createdAt", LocalDateTime.now().toString());

            messagingTemplate.convertAndSend(
                    "/topic/project/" + projectId + "/notifications",
                    notification
            );

            log.info("[F13]  Notification projet envoyée");
        } catch (Exception e) {
            log.error("[F13]  Erreur envoi notification projet: {}", e.getMessage());
        }
    }

    /**
     * Broadcast notification à tous les utilisateurs connectés
     * Topic : /topic/notifications
     */
    public void envoyerNotificationGlobale(String titre, String message) {
        try {
            log.info("[F13] Envoi notification globale via WebSocket");

            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "NOTIFICATION");
            notification.put("titre", titre);
            notification.put("message", message);
            notification.put("createdAt", LocalDateTime.now().toString());

            messagingTemplate.convertAndSend(
                    "/topic/notifications",
                    notification
            );

            log.info("[F13]  Notification globale envoyée");
        } catch (Exception e) {
            log.error("[F13]  Erreur envoi notification globale: {}", e.getMessage());
        }
    }
}