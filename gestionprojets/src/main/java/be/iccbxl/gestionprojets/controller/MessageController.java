package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.enums.Role;
import be.iccbxl.gestionprojets.enums.StatutMessage;
import be.iccbxl.gestionprojets.enums.TypeMessage;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller WebSocket + REST pour F9 : Collaboration en temps réel
 * Corrections : permissions selon rôles, endpoints REST utilisés par la vue,
 * et alias admin /api/messages/admin/all pour le tableau de bord.
 */
@Controller
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ProjetService projetService;
    private final UtilisateurService utilisateurService;
    private final SimpMessagingTemplate messagingTemplate;

    /** DTO entrant */
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
        public String getContenu() { return contenu; }
        public void setContenu(String contenu) { this.contenu = contenu; }
        public Long getProjetId() { return projetId; }
        public void setProjetId(Long projetId) { this.projetId = projetId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    /** DTO sortant */
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

    /** Vérification des permissions selon le cahier des charges */
    private boolean verifierAccesProjet(Utilisateur utilisateur, Projet projet) {
        Role roleUtilisateur = utilisateur.getRole();
        if (roleUtilisateur == Role.ADMINISTRATEUR) return true;
        if (roleUtilisateur == Role.CHEF_PROJET) return true;
        if (roleUtilisateur == Role.MEMBRE) {
            boolean estMembre = projetService.estMembreDuProjet(utilisateur.getId(), projet.getId());
            boolean estCreateur = projet.getIdCreateur().equals(utilisateur.getId());
            return estMembre || estCreateur;
        }
        return false;
    }

    // ==================== WebSocket ====================

    @MessageMapping("/message.send")
    public void envoyerMessage(MessageRequest messageRequest, Principal principal) {
        try {
            if (principal == null) return;

            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Projet projet = projetService.findById(messageRequest.getProjetId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            if (!verifierAccesProjet(utilisateur, projet)) {
                throw new RuntimeException("Utilisateur non autorisé pour ce projet");
            }

            TypeMessage typeMessage;
            try {
                typeMessage = messageRequest.getType() != null
                        ? TypeMessage.valueOf(messageRequest.getType().toUpperCase())
                        : TypeMessage.TEXT;
            } catch (IllegalArgumentException e) {
                typeMessage = TypeMessage.TEXT;
            }

            Message message = new Message(messageRequest.getContenu(), utilisateur, projet, typeMessage);
            Message savedMessage = messageService.sauvegarderMessage(message);

            MessageResponse response = new MessageResponse(savedMessage);
            messagingTemplate.convertAndSend("/topic/projet/" + messageRequest.getProjetId(), response);

        } catch (Exception e) {
            System.err.println("ERROR WS send: " + e.getMessage());
        }
    }

    @MessageMapping("/message.join")
    public void rejoindreChatProjet(MessageRequest messageRequest, Principal principal) {
        try {
            if (principal == null) return;

            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Projet projet = projetService.findById(messageRequest.getProjetId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            if (!verifierAccesProjet(utilisateur, projet)) {
                throw new RuntimeException("Utilisateur non autorisé pour ce projet");
            }

            Message messageConnexion = new Message(
                    utilisateur.getNom() + " " + utilisateur.getPrenom() + " a rejoint le chat",
                    utilisateur, projet, TypeMessage.SYSTEM
            );
            Message saved = messageService.sauvegarderMessage(messageConnexion);
            messagingTemplate.convertAndSend("/topic/projet/" + messageRequest.getProjetId(), new MessageResponse(saved));

        } catch (Exception e) {
            System.err.println("ERROR WS join: " + e.getMessage());
        }
    }

    // ==================== REST ====================

    /** Alias admin pour le dashboard : toutes les conversations */
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    @ResponseBody
    public ResponseEntity<List<MessageResponse>> getAllForAdmin() {
        // Requiert messageService.findAll() → si absent, ajouter une méthode read-only qui retourne repository.findAll()
        List<Message> messages = messageService.findAll();
        List<MessageResponse> out = messages.stream().map(MessageResponse::new).toList();
        return ResponseEntity.ok(out);
    }

    /** Envoyer un message via REST (format initial : body contient projetId) */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> envoyerMessageREST(@RequestBody MessageRequest messageRequest, Principal principal) {
        try {
            if (principal == null) {
                Map<String, Object> m = new HashMap<>();
                m.put("message", "Non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
            }

            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Projet projet = projetService.findById(messageRequest.getProjetId())
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            if (!verifierAccesProjet(utilisateur, projet)) {
                Map<String, Object> m = new HashMap<>();
                m.put("message", "Accès refusé à ce projet");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(m);
            }

            TypeMessage typeMessage;
            try {
                typeMessage = messageRequest.getType() != null
                        ? TypeMessage.valueOf(messageRequest.getType().toUpperCase())
                        : TypeMessage.TEXT;
            } catch (IllegalArgumentException e) {
                typeMessage = TypeMessage.TEXT;
            }

            Message saved = messageService.sauvegarderMessage(
                    new Message(messageRequest.getContenu(), utilisateur, projet, typeMessage)
            );

            MessageResponse response = new MessageResponse(saved);
            messagingTemplate.convertAndSend("/topic/projet/" + messageRequest.getProjetId(), response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> m = new HashMap<>();
            m.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().body(m);
        }
    }

    /**
     * **Format utilisé par la vue**
     * POST /api/messages/projet/{projetId}  body: {contenu}
     */
    @PostMapping("/projet/{projetId}")
    @ResponseBody
    public ResponseEntity<Object> envoyerMessagePourProjet(
            @PathVariable Long projetId,
            @RequestBody Map<String, String> body,
            Principal principal) {
        MessageRequest req = new MessageRequest(body.getOrDefault("contenu", ""), projetId, "TEXT");
        return envoyerMessageREST(req, principal);
    }

    /** Historique des messages d’un projet */
    @GetMapping("/projet/{projetId}")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getMessagesProjet(@PathVariable Long projetId, Principal principal) {
        try {
            if (principal == null) {
                Map<String, Object> m = new HashMap<>();
                m.put("message", "Non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
            }

            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Projet projet = projetService.findById(projetId)
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            if (!verifierAccesProjet(utilisateur, projet)) {
                Map<String, Object> m = new HashMap<>();
                m.put("message", "Accès non autorisé à ce projet");
                m.put("userRole", utilisateur.getRole().name());
                m.put("projetId", projetId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(m);
            }

            List<Message> messages = messageService.getMessagesParProjet(projetId);
            List<MessageResponse> out = messages.stream().map(MessageResponse::new).toList();
            return ResponseEntity.ok(out);

        } catch (Exception e) {
            Map<String, Object> m = new HashMap<>();
            m.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().body(m);
        }
    }

    /** Marquer un message comme lu */
    @PutMapping("/{messageId}/lu")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> marquerCommeLu(@PathVariable Long messageId, Principal principal) {
        try {
            if (principal == null) {
                Map<String, Object> m = new HashMap<>();
                m.put("success", false);
                m.put("error", "Non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
            }

            utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            messageService.marquerCommeLu(messageId);

            Map<String, Object> m = new HashMap<>();
            m.put("success", true);
            m.put("message", "Message marqué comme lu");
            return ResponseEntity.ok(m);

        } catch (Exception e) {
            Map<String, Object> m = new HashMap<>();
            m.put("success", false);
            m.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(m);
        }
    }

    /** Notification WS à tous les membres d'un projet */
    public void envoyerNotificationProjet(Long projetId, String contenu, Principal principal) {
        try {
            if (principal == null) return;

            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            Projet projet = projetService.findById(projetId)
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            Message notification = new Message(contenu, utilisateur, projet, TypeMessage.NOTIFICATION);
            Message saved = messageService.sauvegarderMessage(notification);
            messagingTemplate.convertAndSend("/topic/projet/" + projetId, new MessageResponse(saved));

        } catch (Exception e) {
            System.err.println("ERROR notif: " + e.getMessage());
        }
    }
}
