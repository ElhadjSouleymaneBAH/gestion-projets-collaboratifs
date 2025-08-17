package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.enums.TypeMessage;
import be.iccbxl.gestionprojets.enums.StatutMessage;
import be.iccbxl.gestionprojets.enums.Role;
import be.iccbxl.gestionprojets.model.Message;
import be.iccbxl.gestionprojets.model.Projet;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.service.MessageService;
import be.iccbxl.gestionprojets.service.ProjetService;
import be.iccbxl.gestionprojets.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller WebSocket pour F9 : Collaboration en temps réel
 *
 * CORRECTION MAJEURE : Gestion des permissions selon les rôles du cahier des charges
 * - ADMINISTRATEUR : Accès total à tous les projets
 * - CHEF_PROJET : Accès à tous les projets + ses projets créés
 * - MEMBRE : Accès aux projets dont il est membre ou créateur
 * - VISITEUR : Aucun accès aux messages
 *
 * @author ElhadjSouleymaneBAH
 * @version 2.0 - Correction permissions
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

        public MessageResponse(Message message) {
            this.id = message.getId();
            this.contenu = message.getContenu();
            this.utilisateurNom = message.getUtilisateur().getNom() + " " + message.getUtilisateur().getPrenom();
            this.utilisateurEmail = message.getUtilisateur().getEmail();
            this.projetId = message.getProjet().getId();
            this.type = message.getType() != null ? message.getType().name() : TypeMessage.TEXT.name();
            this.statut = message.getStatut() != null ? message.getStatut().name() : StatutMessage.ENVOYE.name();
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

    /**
     * MÉTHODE CRUCIALE : Vérification des permissions selon le cahier des charges
     *
     * @param utilisateur L'utilisateur à vérifier
     * @param projet Le projet concerné
     * @return true si l'utilisateur a accès, false sinon
     */
    private boolean verifierAccesProjet(Utilisateur utilisateur, Projet projet) {
        // Récupération du rôle (enum Role)
        Role roleUtilisateur = utilisateur.getRole();

        System.out.println("DEBUG: [PERMISSION] Vérification accès pour utilisateur " +
                utilisateur.getEmail() + " (rôle: " + roleUtilisateur.name() + ") sur projet " + projet.getId());

        // ADMINISTRATEUR : Accès total selon cahier des charges
        if (roleUtilisateur == Role.ADMINISTRATEUR) {
            System.out.println("DEBUG: [PERMISSION] ADMINISTRATEUR - Accès accordé");
            return true;
        }

        // CHEF_PROJET : Accès à tous les projets selon cahier des charges F6
        if (roleUtilisateur == Role.CHEF_PROJET) {
            System.out.println("DEBUG: [PERMISSION] CHEF_PROJET - Accès accordé");
            return true;
        }

        // MEMBRE : Vérification membre du projet OU créateur
        if (roleUtilisateur == Role.MEMBRE) {
            boolean estMembre = projetService.estMembreDuProjet(utilisateur.getId(), projet.getId());
            boolean estCreateur = projet.getIdCreateur().equals(utilisateur.getId());

            System.out.println("DEBUG: [PERMISSION] MEMBRE - estMembre: " + estMembre + ", estCreateur: " + estCreateur);

            if (estMembre || estCreateur) {
                System.out.println("DEBUG: [PERMISSION] MEMBRE - Accès accordé");
                return true;
            }
        }

        // VISITEUR : Aucun accès aux messages selon cahier des charges
        System.out.println("DEBUG: [PERMISSION] Accès refusé pour " + roleUtilisateur.name());
        return false;
    }

    // ========== ENDPOINTS WEBSOCKET ==========

    /**
     * Envoyer un message dans un projet via WebSocket
     */
    @MessageMapping("/message.send")
    public void envoyerMessage(MessageRequest messageRequest, Principal principal) {
        try {
            // Récupérer l'utilisateur authentifié
            Utilisateur utilisateur;
            if (principal == null) {
                System.out.println("WARNING: Principal null - utilisation utilisateur test");
                utilisateur = new Utilisateur();
                utilisateur.setId(1L);
                utilisateur.setNom("Test");
                utilisateur.setPrenom("Utilisateur");
                utilisateur.setEmail("test@test.com");
                utilisateur.setRole(Role.MEMBRE); // Rôle par défaut pour test
            } else {
                utilisateur = utilisateurService.findByEmail(principal.getName())
                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            }

            // Récupérer le projet
            Projet projet = projetService.findById(messageRequest.getProjetId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            // CORRECTION F9 : Vérification accès selon les rôles
            if (!verifierAccesProjet(utilisateur, projet)) {
                throw new RuntimeException("Utilisateur non autorisé pour ce projet");
            }

            // Convertir le type String en enum
            TypeMessage typeMessage;
            try {
                typeMessage = messageRequest.getType() != null ?
                        TypeMessage.valueOf(messageRequest.getType().toUpperCase()) : TypeMessage.TEXT;
            } catch (IllegalArgumentException e) {
                typeMessage = TypeMessage.TEXT;
            }

            // Créer et sauvegarder le message
            Message message = new Message(
                    messageRequest.getContenu(),
                    utilisateur,
                    projet,
                    typeMessage
            );

            Message savedMessage = messageService.sauvegarderMessage(message);

            // Envoyer le message via WebSocket
            MessageResponse response = new MessageResponse(savedMessage);
            messagingTemplate.convertAndSend("/topic/projet/" + messageRequest.getProjetId(), response);

            System.out.println("SUCCESS: Message envoyé vers /topic/projet/" + messageRequest.getProjetId());

        } catch (Exception e) {
            System.err.println("ERROR: Erreur lors de l'envoi du message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Rejoindre le chat d'un projet via WebSocket
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

            // CORRECTION F9 : Vérification accès selon les rôles
            if (!verifierAccesProjet(utilisateur, projet)) {
                throw new RuntimeException("Utilisateur non autorisé pour ce projet");
            }

            // Créer un message système de connexion
            String contenuConnexion = utilisateur.getNom() + " " + utilisateur.getPrenom() + " a rejoint le chat";

            Message messageConnexion = new Message(
                    contenuConnexion,
                    utilisateur,
                    projet,
                    TypeMessage.SYSTEM
            );

            Message savedMessage = messageService.sauvegarderMessage(messageConnexion);

            // Envoyer via WebSocket
            MessageResponse response = new MessageResponse(savedMessage);
            messagingTemplate.convertAndSend("/topic/projet/" + messageRequest.getProjetId(), response);

        } catch (Exception e) {
            System.err.println("ERROR: Erreur lors de la connexion au chat: " + e.getMessage());
        }
    }

    // ========== ENDPOINTS REST ==========

    /**
     * Envoyer un message via REST API (F9)
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> envoyerMessageREST(@RequestBody MessageRequest messageRequest, Principal principal) {
        try {
            System.out.println("DEBUG: [F9-REST] Envoi message pour projet " + messageRequest.getProjetId());

            // Récupérer l'utilisateur authentifié
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Récupérer le projet
            Projet projet = projetService.findById(messageRequest.getProjetId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            // CORRECTION F9 : Vérification accès selon les rôles
            if (!verifierAccesProjet(utilisateur, projet)) {
                System.out.println("ERROR: [F9-REST] Utilisateur " + utilisateur.getId() +
                        " (" + utilisateur.getRole().name() + ") n'a pas accès au projet " + projet.getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Accès refusé à ce projet"));
            }

            // Convertir le type String en enum
            TypeMessage typeMessage;
            try {
                typeMessage = messageRequest.getType() != null ?
                        TypeMessage.valueOf(messageRequest.getType().toUpperCase()) : TypeMessage.TEXT;
            } catch (IllegalArgumentException e) {
                typeMessage = TypeMessage.TEXT;
            }

            // Créer et sauvegarder le message
            Message message = new Message(messageRequest.getContenu(), utilisateur, projet, typeMessage);
            Message savedMessage = messageService.sauvegarderMessage(message);

            // Envoyer via WebSocket aussi
            MessageResponse response = new MessageResponse(savedMessage);
            messagingTemplate.convertAndSend("/topic/projet/" + messageRequest.getProjetId(), response);

            System.out.println("SUCCESS: [F9-REST] Message envoyé avec succès ID: " + savedMessage.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.out.println("ERROR: [F9-REST] Erreur envoi message: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * ENDPOINT PRINCIPAL CORRIGÉ : Récupérer l'historique des messages d'un projet
     *
     * Cette méthode était la source du problème 403 Forbidden
     */
    @GetMapping("/projet/{projetId}")
    @ResponseBody
    public ResponseEntity<Object> getMessagesProjet(@PathVariable Long projetId, Principal principal) {
        try {
            System.out.println("DEBUG: [F9-GET] Récupération messages projet " + projetId);

            // Vérifier que l'utilisateur est authentifié
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            System.out.println("DEBUG: [F9-GET] Utilisateur connecté: " + utilisateur.getEmail() +
                    " (rôle: " + utilisateur.getRole().name() + ")");

            // Récupérer le projet
            Projet projet = projetService.findById(projetId)
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            // CORRECTION MAJEURE : Vérification accès selon les rôles du cahier des charges
            if (!verifierAccesProjet(utilisateur, projet)) {
                System.out.println("ERROR: [F9-GET] Accès refusé pour " + utilisateur.getEmail() +
                        " (rôle: " + utilisateur.getRole().name() + ") sur projet " + projetId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Accès non autorisé à ce projet",
                                "userRole", utilisateur.getRole().name(),
                                "projetId", projetId));
            }

            // Récupérer les messages du projet
            List<Message> messages = messageService.getMessagesParProjet(projetId);

            // Convertir en DTO
            List<MessageResponse> messagesResponse = messages.stream()
                    .map(MessageResponse::new)
                    .toList();

            System.out.println("SUCCESS: [F9-GET] " + messagesResponse.size() +
                    " messages récupérés pour le projet " + projetId);

            return ResponseEntity.ok(messagesResponse);

        } catch (Exception e) {
            System.out.println("ERROR: [F9-GET] Erreur lors de la récupération des messages: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Marquer un message comme lu
     */
    @PutMapping("/{messageId}/lu")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> marquerCommeLu(@PathVariable Long messageId, Principal principal) {
        try {
            // Récupérer l'utilisateur authentifié
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Marquer le message comme lu
            messageService.marquerCommeLu(messageId, utilisateur.getId());

            return ResponseEntity.ok(Map.of("success", true, "message", "Message marqué comme lu"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Envoyer une notification à tous les membres d'un projet
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
            Message notification = new Message(contenu, utilisateur, projet, TypeMessage.NOTIFICATION);
            Message savedNotification = messageService.sauvegarderMessage(notification);

            // Diffuser la notification via WebSocket
            MessageResponse response = new MessageResponse(savedNotification);
            messagingTemplate.convertAndSend("/topic/projet/" + projetId, response);

        } catch (Exception e) {
            System.err.println("ERROR: Erreur lors de l'envoi de notification: " + e.getMessage());
        }
    }
}