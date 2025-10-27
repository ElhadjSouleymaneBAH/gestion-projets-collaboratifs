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
        public String getContenu() { return contenu; }
        public String getUtilisateurNom() { return utilisateurNom; }
        public String getUtilisateurEmail() { return utilisateurEmail; }
        public Long getProjetId() { return projetId; }
        public String getType() { return type; }
        public String getStatut() { return statut; }
        public LocalDateTime getDateEnvoi() { return dateEnvoi; }
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

            TypeMessage typeMessage = TypeMessage.TEXT;
            try {
                if (messageRequest.getType() != null)
                    typeMessage = TypeMessage.valueOf(messageRequest.getType().toUpperCase());
            } catch (IllegalArgumentException ignored) {}

            Message message = new Message(messageRequest.getContenu(), utilisateur, projet, typeMessage);
            Message saved = messageService.sauvegarderMessage(message);

            MessageResponse response = new MessageResponse(saved);

            // Envoi à tous les membres du projet
            messagingTemplate.convertAndSend("/topic/projet/" + messageRequest.getProjetId(), response);

            //  Envoi direct au chef de projet si c’est un membre qui écrit
            if (!utilisateur.getId().equals(projet.getIdCreateur())) {
                messagingTemplate.convertAndSend(
                        "/topic/notifications/chef-projet/" + projet.getIdCreateur(),
                        response
                );
            }

            //  Envoi direct aux membres si c’est le chef qui répond
            if (utilisateur.getId().equals(projet.getIdCreateur())) {
                projetService.listerMembres(projet.getId())
                        .forEach(m -> messagingTemplate.convertAndSendToUser(
                                m.getEmail(),
                                "/topic/chat",
                                response
                        ));
            }

        } catch (Exception e) {
            System.err.println("ERROR WS send: " + e.getMessage());
        }
    }

    // ==================== REST ====================

    @PostMapping("/projet/{projetId}")
    @ResponseBody
    public ResponseEntity<Object> envoyerMessagePourProjet(
            @PathVariable Long projetId,
            @RequestBody Map<String, String> body,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non authentifié");
            }

            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            Projet projet = projetService.findById(projetId)
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            if (!verifierAccesProjet(utilisateur, projet)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès non autorisé");
            }

            Message message = new Message(body.get("contenu"), utilisateur, projet, TypeMessage.TEXT);
            Message saved = messageService.sauvegarderMessage(message);
            MessageResponse response = new MessageResponse(saved);

            messagingTemplate.convertAndSend("/topic/projet/" + projetId, response);

            //  Envoi au chef si message d’un membre
            if (!utilisateur.getId().equals(projet.getIdCreateur())) {
                messagingTemplate.convertAndSend(
                        "/topic/notifications/chef-projet/" + projet.getIdCreateur(),
                        response
                );
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /** Historique des messages d’un projet */
    @GetMapping("/projet/{projetId}")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getMessagesProjet(@PathVariable Long projetId, Principal principal) {
        try {
            if (principal == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non authentifié");

            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            Projet projet = projetService.findById(projetId)
                    .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

            if (!verifierAccesProjet(utilisateur, projet))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès non autorisé");

            List<Message> messages = messageService.getMessagesParProjet(projetId);
            return ResponseEntity.ok(messages.stream().map(MessageResponse::new).toList());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
