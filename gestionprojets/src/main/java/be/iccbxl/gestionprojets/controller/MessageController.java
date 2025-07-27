package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Message;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.MessageService;
import be.iccbxl.gestionprojets.service.ProjetService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller WebSocket pour F9 : Collaboration en temps réel
 *
 * Gère les messages en temps réel dans les projets selon le cahier des charges.
 * Respecte l'architecture académique avec :
 * - MessageService pour la logique métier
 * - Message.java comme entité principale
 * - Endpoints WebSocket conformes aux exigences F9
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.0
 * @since 2025-07-14
 * @see "Cahier des charges - F9: Collaborer en temps réel"
 */
@Controller
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ProjetService projetService;
    private final UtilisateurService utilisateurService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * DTO pour les messages entrants via WebSocket
     */
    public static class MessageRequest {
        private String contenu;
        private Long projetId;
        private String type;

        // Constructeurs
        public MessageRequest() {}

        public MessageRequest(String contenu, Long projetId, String type) {
            this.contenu = contenu;
            this.projetId = projetId;
            this.type = type;
        }

        // Getters et Setters
        public String getContenu() { return contenu; }
        public void setContenu(String contenu) { this.contenu = contenu; }

        public Long getProjetId() { return projetId; }
        public void setProjetId(Long projetId) { this.projetId = projetId; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    /**
     * DTO pour les messages sortants via WebSocket
     */
    public static class MessageResponse {
        private Long id;
        private String contenu;
        private String utilisateurNom;
        private String utilisateurEmail;
        private Long projetId;
        private String type;
        private String statut;
        private LocalDateTime dateEnvoi;

        // Constructeur depuis entité Message
        public MessageResponse(Message message) {
            this.id = message.getId();
            this.contenu = message.getContenu();
            this.utilisateurNom = message.getUtilisateur().getNom() + " " + message.getUtilisateur().getPrenom();
            this.utilisateurEmail = message.getUtilisateur().getEmail();
            this.projetId = message.getProjet().getId();
            this.type = message.getType();
            this.statut = message.getStatut();
            this.dateEnvoi = message.getDateEnvoi();
        }

        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getContenu() { return contenu; }
        public void setContenu(String contenu) { this.contenu = contenu; }

        public String getUtilisateurNom() { return utilisateurNom; }
        public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }

        public String getUtilisateurEmail() { return utilisateurEmail; }
        public void setUtilisateurEmail(String utilisateurEmail) { this.utilisateurEmail = utilisateurEmail; }

        public Long getProjetId() { return projetId; }
        public void setProjetId(Long projetId) { this.projetId = projetId; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }

        public LocalDateTime getDateEnvoi() { return dateEnvoi; }
        public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
    }

    // ========== ENDPOINTS WEBSOCKET ==========

    /**
     * Envoyer un message dans un projet.
     *
     * Endpoint : /app/message.send
     * Destination : /topic/projet/{projetId}
     *
     * @param messageRequest Le message à envoyer
     * @param principal L'utilisateur authentifié
     */
    @MessageMapping("/message.send")
    public void envoyerMessage(MessageRequest messageRequest, Principal principal) {
        try {
            // Récupérer l'utilisateur authentifié
            Utilisateur utilisateur;
            if (principal == null) {
                System.out.println("🔍 Principal null - utilisation utilisateur test");
                // TEMPORAIRE : Utilisateur test pour WebSocket sans auth
                utilisateur = new Utilisateur();
                utilisateur.setId(1L);
                utilisateur.setNom("Test");
                utilisateur.setPrenom("Utilisateur");
                utilisateur.setEmail("test@test.com");
            } else {
                utilisateur = utilisateurService.findByEmail(principal.getName())
                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            }

            // Récupérer le projet
            Projet projet = projetService.findById(messageRequest.getProjetId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            // TEMPORAIRE : Skip vérification membre pour tests
            // if (!projetService.estMembreDuProjet(utilisateur.getId(), projet.getId())) {
            //     throw new RuntimeException("Utilisateur non autorisé pour ce projet");
            // }

            // Créer et sauvegarder le message
            Message message = new Message(
                    messageRequest.getContenu(),
                    utilisateur,
                    projet,
                    messageRequest.getType() != null ? messageRequest.getType() : "TEXT"
            );

            Message savedMessage = messageService.sauvegarderMessage(message);

            // Envoyer le message via WebSocket
            MessageResponse response = new MessageResponse(savedMessage);
            messagingTemplate.convertAndSend("/topic/projet/" + messageRequest.getProjetId(), response);

            System.out.println("✅ Message envoyé vers /topic/projet/" + messageRequest.getProjetId());

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi du message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Rejoindre le chat d'un projet.
     *
     * Endpoint : /app/message.join
     * Notification : /topic/projet/{projetId}
     *
     * @param messageRequest Contient l'ID du projet
     * @param principal L'utilisateur authentifié
     */
    @MessageMapping("/message.join")
    public void rejoindreChatProjet(MessageRequest messageRequest, Principal principal) {
        try {
            // Récupérer l'utilisateur authentifié
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Récupérer le projet
            Projet projet = projetService.findById(messageRequest.getProjetId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            // Créer un message système de connexion
            String contenuConnexion = utilisateur.getNom() + " " + utilisateur.getPrenom() + " a rejoint le chat";

            Message messageConnexion = new Message(
                    contenuConnexion,
                    utilisateur,
                    projet,
                    "SYSTEM"
            );

            Message savedMessage = messageService.sauvegarderMessage(messageConnexion);

            // Envoyer via WebSocket
            MessageResponse response = new MessageResponse(savedMessage);
            messagingTemplate.convertAndSend("/topic/projet/" + messageRequest.getProjetId(), response);

        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion au chat: " + e.getMessage());
        }
    }

    // ========== ENDPOINTS REST ==========

    /**
     * Récupérer l'historique des messages d'un projet.
     *
     * Fonctionnalité F9 : Support pour charger l'historique des messages
     * avant de rejoindre le chat temps réel.
     *
     * @param projetId L'ID du projet
     * @param principal L'utilisateur authentifié
     * @return List<MessageResponse> La liste des messages
     */
    @GetMapping("/projet/{projetId}")
    @ResponseBody
    public List<MessageResponse> getMessagesProjet(@PathVariable Long projetId, Principal principal) {
        try {
            // Vérifier que l'utilisateur est authentifié
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Vérifier que l'utilisateur est membre du projet
            if (!projetService.estMembreDuProjet(utilisateur.getId(), projetId)) {
                throw new RuntimeException("Accès non autorisé à ce projet");
            }

            // Récupérer les messages du projet
            List<Message> messages = messageService.getMessagesParProjet(projetId);

            // Convertir en DTO
            return messages.stream()
                    .map(MessageResponse::new)
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des messages: " + e.getMessage());
        }
    }

    /**
     * Marquer un message comme lu.
     *
     * @param messageId L'ID du message
     * @param principal L'utilisateur authentifié
     * @return Map Confirmation
     */
    @PutMapping("/{messageId}/lu")
    @ResponseBody
    public Map<String, Object> marquerCommeLu(@PathVariable Long messageId, Principal principal) {
        try {
            // Récupérer l'utilisateur authentifié
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Marquer le message comme lu
            messageService.marquerCommeLu(messageId, utilisateur.getId());

            return Map.of("success", true, "message", "Message marqué comme lu");

        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    /**
     * Envoyer une notification à tous les membres d'un projet.
     *
     * @param projetId L'ID du projet
     * @param contenu Le contenu de la notification
     * @param principal L'utilisateur authentifié
     */
    public void envoyerNotificationProjet(Long projetId, String contenu, Principal principal) {
        try {
            // Récupérer l'utilisateur authentifié
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Récupérer le projet
            Projet projet = projetService.findById(projetId)
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            // Créer le message de notification
            Message notification = new Message(contenu, utilisateur, projet, "NOTIFICATION");
            Message savedNotification = messageService.sauvegarderMessage(notification);

            // Diffuser la notification via WebSocket
            MessageResponse response = new MessageResponse(savedNotification);
            messagingTemplate.convertAndSend("/topic/projet/" + projetId, response);

        } catch (Exception e) {
            // Log de l'erreur
            System.err.println("Erreur lors de l'envoi de notification: " + e.getMessage());
        }
    }
}