package be.iccbxl.gestionprojets.service;

import be.iccbxl.gestionprojets.controller.NotificationWebSocketController;
import be.iccbxl.gestionprojets.dto.CommentaireDTO;
import be.iccbxl.gestionprojets.dto.NotificationDTO;
import be.iccbxl.gestionprojets.model.Commentaire;
import be.iccbxl.gestionprojets.model.Tache;
import be.iccbxl.gestionprojets.model.Utilisateur;
import be.iccbxl.gestionprojets.repository.CommentaireRepository;
import be.iccbxl.gestionprojets.repository.TacheRepository;
import be.iccbxl.gestionprojets.repository.UtilisateurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des commentaires (F12) avec notifications temps réel (F13)
 * Intégration WebSocket pour notifications instantanées et broadcast des commentaires
 *
 * @author ElhadjSouleymaneBAH
 * @version 1.3 - Ajout broadcast WebSocket des commentaires
 */
@Service
@Transactional
@Slf4j
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final TacheRepository tacheRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationService notificationService;
    private final NotificationWebSocketController webSocketController;
    private final SimpMessagingTemplate messagingTemplate; // ⭐ AJOUT

    public CommentaireService(CommentaireRepository commentaireRepository,
                              TacheRepository tacheRepository,
                              UtilisateurRepository utilisateurRepository,
                              NotificationService notificationService,
                              NotificationWebSocketController webSocketController,
                              SimpMessagingTemplate messagingTemplate) { // ⭐ AJOUT
        this.commentaireRepository = commentaireRepository;
        this.tacheRepository = tacheRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.notificationService = notificationService;
        this.webSocketController = webSocketController;
        this.messagingTemplate = messagingTemplate; // ⭐ AJOUT
    }

    // ============================================================================
    // F12 : COMMENTER LES TÂCHES/PROJETS
    // ============================================================================

    /**
     * Créer un nouveau commentaire (F12) + Notification temps réel (F13)
     * + Broadcast WebSocket du commentaire (F9)
     */
    public CommentaireDTO creerCommentaire(CommentaireDTO commentaireDTO, String emailAuteur) {
        log.info("[F12] Création commentaire par {}", emailAuteur);

        // 1. Validation des données
        if (commentaireDTO.getContenu() == null || commentaireDTO.getContenu().trim().isEmpty()) {
            throw new RuntimeException("Le contenu du commentaire est obligatoire");
        }

        // 2. Récupérer la tâche
        Tache tache = tacheRepository.findById(commentaireDTO.getTacheId())
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée: " + commentaireDTO.getTacheId()));

        // 3. Récupérer l'auteur du commentaire
        Utilisateur auteur = utilisateurRepository.findByEmail(emailAuteur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + emailAuteur));

        // 4. Créer le commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(commentaireDTO.getContenu());
        commentaire.setDate(LocalDateTime.now());
        commentaire.setTache(tache);
        commentaire.setAuteur(auteur);

        Commentaire commentaireSauvegarde = commentaireRepository.save(commentaire);
        log.info("[F12] ✅ Commentaire créé avec ID: {}", commentaireSauvegarde.getId());

        // Convertir en DTO pour le retour et le broadcast
        CommentaireDTO dtoRetour = convertirEnDTO(commentaireSauvegarde);

        // ========================================================================
        // ⭐ BROADCAST WEBSOCKET DU COMMENTAIRE EN TEMPS RÉEL
        // ========================================================================
        try {
            // Envoyer le commentaire à tous ceux qui écoutent cette tâche
            messagingTemplate.convertAndSend(
                    "/topic/tache/" + tache.getId() + "/commentaires",
                    dtoRetour
            );
            log.info("[F9] ✅ Commentaire broadcasté via WebSocket sur /topic/tache/{}/commentaires", tache.getId());
        } catch (Exception e) {
            log.error("[F9] ❌ Échec broadcast commentaire: {}", e.getMessage());
            // On continue même si le broadcast échoue (robustesse)
        }

        // ========================================================================
        // F13 : NOTIFICATION TEMPS RÉEL VIA WEBSOCKET
        // ========================================================================
        if (tache.getAssigneA() != null && !tache.getAssigneA().getId().equals(auteur.getId())) {
            try {
                String nomCompletAuteur = auteur.getPrenom() + " " + auteur.getNom();
                String titre = "Nouveau commentaire";
                String message = String.format(
                        "Nouveau commentaire de %s sur '%s'",
                        nomCompletAuteur,
                        tache.getTitre()
                );

                // 1. Créer notification en base de données
                notificationService.creerNotification(tache.getAssigneA().getId(), message);

                // 2. Envoyer notification WebSocket en temps réel
                webSocketController.envoyerNotificationUtilisateur(
                        tache.getAssigneA().getId(),
                        titre,
                        message
                );

                log.info("[F13] ✅ Notification temps réel envoyée à l'utilisateur {}",
                        tache.getAssigneA().getId());

            } catch (Exception e) {
                log.error("[F13] ❌ Échec notification: {}", e.getMessage());
                // On continue même si la notification échoue (robustesse)
            }
        } else {
            log.debug("[F13] ℹ️ Aucune notification (pas d'assigné ou auto-commentaire)");
        }

        return dtoRetour;
    }

    /**
     * Obtenir tous les commentaires d'une tâche (F12)
     */
    @Transactional(readOnly = true)
    public List<CommentaireDTO> obtenirCommentairesTache(Long tacheId) {
        log.debug("[F12] Recherche commentaires pour tâche {}", tacheId);
        List<Commentaire> commentaires = commentaireRepository.findByTacheIdOrderByDateAsc(tacheId);
        log.debug("[F12] {} commentaires trouvés", commentaires.size());
        return commentaires.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les commentaires d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<CommentaireDTO> obtenirCommentairesUtilisateur(Long utilisateurId) {
        log.debug("[F12] Recherche commentaires de l'utilisateur {}", utilisateurId);
        List<Commentaire> commentaires = commentaireRepository.findByAuteurIdOrderByDateDesc(utilisateurId);
        return commentaires.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Supprimer un commentaire (F12)
     * Règles métier :
     * - ADMINISTRATEUR : peut supprimer tous les commentaires
     * - CHEF_PROJET : peut supprimer tous les commentaires
     * - MEMBRE : peut supprimer UNIQUEMENT ses propres commentaires
     */
    public void supprimerCommentaire(Long commentaireId, String emailUtilisateur) {
        log.info("[F12] Tentative suppression commentaire {} par {}", commentaireId, emailUtilisateur);

        // 1. Vérifier existence du commentaire
        Commentaire commentaire = commentaireRepository.findById(commentaireId)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé: " + commentaireId));

        // 2. Récupérer l'utilisateur connecté
        Utilisateur utilisateur = utilisateurRepository.findByEmail(emailUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + emailUtilisateur));

        // 3. Vérifier les permissions
        String role = utilisateur.getRole().name();
        boolean estAdmin = "ADMINISTRATEUR".equals(role);
        boolean estChef = "CHEF_PROJET".equals(role);
        boolean estAuteur = commentaire.getAuteur().getId().equals(utilisateur.getId());

        // 4. Appliquer la logique métier
        if (!estAdmin && !estChef && !estAuteur) {
            log.warn("[F12] ❌ REFUS : {} (role={}) tente de supprimer commentaire de {}",
                    emailUtilisateur, role, commentaire.getAuteur().getEmail());
            throw new SecurityException("Vous n'êtes pas autorisé à supprimer ce commentaire");
        }

        // 5. Récupérer l'ID de la tâche avant suppression (pour le broadcast)
        Long tacheId = commentaire.getTache().getId();

        // 6. Supprimer le commentaire
        commentaireRepository.deleteById(commentaireId);
        log.info("[F12] ✅ Commentaire {} supprimé par {} (role={})",
                commentaireId, emailUtilisateur, role);

        // ========================================================================
        // ⭐ BROADCAST WEBSOCKET DE LA SUPPRESSION
        // ========================================================================
        try {
            // Notifier la suppression à tous ceux qui écoutent cette tâche
            messagingTemplate.convertAndSend(
                    "/topic/tache/" + tacheId + "/commentaires/supprime",
                    commentaireId
            );
            log.info("[F9] ✅ Suppression commentaire broadcastée via WebSocket");
        } catch (Exception e) {
            log.error("[F9] ❌ Échec broadcast suppression: {}", e.getMessage());
        }
    }

    /**
     * Obtenir tous les commentaires (Admin)
     */
    @Transactional(readOnly = true)
    public List<CommentaireDTO> obtenirTous() {
        return commentaireRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .toList();
    }

    // ============================================================================
    // MÉTHODES UTILITAIRES
    // ============================================================================

    /**
     * Conversion Commentaire → CommentaireDTO
     */
    private CommentaireDTO convertirEnDTO(Commentaire commentaire) {
        CommentaireDTO dto = new CommentaireDTO();
        dto.setId(commentaire.getId());
        dto.setContenu(commentaire.getContenu());
        dto.setDate(commentaire.getDate());
        dto.setTacheId(commentaire.getTache().getId());

        if (commentaire.getAuteur() != null) {
            dto.setAuteurId(commentaire.getAuteur().getId());
            dto.setAuteurNom(commentaire.getAuteur().getNom());
            dto.setAuteurPrenom(commentaire.getAuteur().getPrenom());
            dto.setAuteurEmail(commentaire.getAuteur().getEmail());
        }

        return dto;
    }
}